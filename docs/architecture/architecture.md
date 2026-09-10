# Arquitectura

## Enfoque general

**Arquitectura Hexagonal (Ports & Adapters, Alistair Cockburn) + MVVM + Use Cases.** Esta es una corrección respecto a la versión anterior de este documento: originalmente hablábamos de "Clean Architecture simplificada" con una carpeta genérica `data/`, pero eso generaba ambigüedad sobre dónde vive cada cosa y por qué. Adoptamos formalmente la terminología Hexagonal porque describe con precisión exacta lo que ya necesitábamos: un núcleo de negocio (`domain/`) que define **contratos** (Ports) sin saber cómo se implementan, y una capa externa (`infrastructure/`) que los **implementa** con tecnología concreta (Adapters).

No se adopta modularización Gradle multi-módulo — sigue sin ser necesaria para el tamaño actual del proyecto (ver ADR-0001 y decisión original en `decisions.md`).

## Aclaración de terminología: dos significados de "Entity"

Esta confusión es real y común, así que la dejamos documentada explícitamente:

| Término | Dónde vive | Qué significa |
|---|---|---|
| **Entity** (Clean Architecture / DDD) | `domain/model/` | Un objeto de negocio puro, ej. `Balance`. Nunca depende de ninguna tecnología. |
| **Entity** (anotación de Room) | `infrastructure/persistence/room/entity/` | Nombre técnico que Room usa para "clase que mapea una tabla SQL", ej. `BalanceEntity`. Es un detalle de implementación de un Adapter — **nunca debe usarse fuera de `infrastructure/`**. |

Ambos existen a propósito y **no son redundantes**: `Balance` (dominio) es la verdad de negocio; `BalanceEntity` (Room) es solo la forma en que esa verdad se serializa a SQLite. Un *mapper* explícito traduce entre ambos.

## Capas (terminología Hexagonal)

Siguiendo la convención formal de Hexagonal/DDD (popularizada por Vaughn Vernon), usamos **tres paquetes de primer nivel** — `domain`, `application` e `infrastructure` — en vez de anidar los casos de uso dentro de `domain`. La razón: el dominio (`model/` + `port/`) es el núcleo estático de reglas y contratos; `application/` es la capa que **orquesta** ese núcleo para cumplir una operación concreta. Son responsabilidades distintas y separarlas en carpetas de primer nivel lo deja inequívoco.

```
domain/            → El núcleo del hexágono. Kotlin puro, cero dependencias de Android/frameworks.
├── model/         → Entidades de negocio (Balance, Transaction, RecurringTransactionRule...)
└── port/          → Interfaces que el dominio necesita del exterior (BalanceRepository, etc.) — los "Ports"

application/       → Casos de uso: orquestan modelos + ports para cumplir una operación de negocio completa.
└── usecase/       → RegisterTransactionUseCase, CreateBalanceUseCase, RenewBalanceUseCase, etc.
                      También Kotlin puro — depende de domain/, nunca de infrastructure/ directamente
                      (solo conoce los Ports, nunca sus implementaciones).

infrastructure/    → Los "Adapters": implementaciones concretas de los Ports, usando tecnología real.
├── persistence/
│   └── room/
│       ├── entity/     → Clases @Entity de Room (detalle técnico, nunca se expone fuera de aquí)
│       ├── dao/         → Interfaces @Dao de Room
│       ├── mapper/      → Traductores Entity (Room) ↔ Modelo (domain)
│       ├── repository/  → Implementaciones concretas de los Ports (ej. BalanceRepositoryImpl)
│       └── FondlyDatabase.kt
├── workers/        → WorkManager (recurrencias, renovaciones) — se agrega en Sprint 3
└── tests/          → Tests que sí dependen de tecnología (Room instrumentado, etc.)

ui/                → Adapter "primario" (el que dispara acciones hacia el dominio)
├── theme/
├── components/
└── screens/         → Composables + ViewModels (un ViewModel por pantalla, invoca Use Cases)

di/                → Módulos de Hilt: conectan cada Port con su Adapter concreto (@Binds)
```

## Por qué esta separación importa aquí en concreto

- **`domain/port/BalanceRepository.kt`** declara la interfaz que el caso de uso necesita (ej. `suspend fun insert(balance: Balance): Long`), **sin mencionar Room en ningún lado**.
- **`application/usecase/RegisterTransactionUseCase.kt`** depende únicamente de `domain/` (modelos + ports) para orquestar la operación — nunca importa nada de `infrastructure/`.
- **`infrastructure/persistence/room/repository/BalanceRepositoryImpl.kt`** implementa esa interfaz usando `BalanceDao` + `BalanceMapper` por dentro.
- **`di/RepositoryModule.kt`** (Hilt) le dice al sistema: "cuando alguien pida un `BalanceRepository`, dale una instancia de `BalanceRepositoryImpl`" — vía `@Binds`.
- Los ViewModels (`ui/`) invocan directamente los Use Cases de `application/`, nunca los Ports ni los repositorios directamente — así toda regla de orquestación vive en un solo lugar, testeable sin Android.
- Si mañana cambiamos Room por otra tecnología, solo se toca `infrastructure/` — ni `domain/`, ni `application/`, ni `ui/` se enteran.

## Regla de dependencias entre capas (una sola dirección, nunca al revés)

```
ui/  →  application/  →  domain/  ←  infrastructure/
```

- `ui/` conoce `application/` (invoca Use Cases).
- `application/` conoce `domain/` (modelos y Ports), nunca `infrastructure/`.
- `infrastructure/` conoce `domain/` (implementa sus Ports), nunca al revés.
- `domain/` no conoce a nadie — es el centro del hexágono, completamente aislado.
- `di/` es la única capa que conoce todo a la vez, porque su trabajo es exactamente conectar Ports con Adapters.

## Patrones aplicados y su justificación

| Patrón | ¿Se usa? | Justificación |
|---|---|---|
| Hexagonal (Ports & Adapters) | Sí | Aísla el negocio de la tecnología; resuelve con nombres claros la separación que ya necesitábamos. |
| MVVM | Sí | Estándar recomendado por Google para Compose. |
| Use Cases | Sí | Las operaciones de este dominio (cascadas, reajustes, renovaciones) tienen complejidad de negocio suficiente para vivir aisladas y ser testeables sin UI ni Room. |
| Repository (como Port) | Sí | Es, literalmente, el mecanismo Hexagonal para desacoplar dominio de infraestructura — no es un patrón aparte, es cómo se ve un Port en este contexto. |
| Multi-módulo Gradle | No (por ahora) | Sigue siendo sobreingeniería para el tamaño actual. |

## Flujo de una operación típica (ejemplo: registrar un gasto)

```
Composable (UI, Adapter primario)
  → ViewModel.onRegisterExpense(...)
    → RegisterTransactionUseCase (application/usecase)
      → TransactionRepository (domain/port — interfaz)
        ⇢ [Hilt resuelve la implementación real]
        → TransactionRepositoryImpl (infrastructure/persistence/room/repository — Adapter)
          → TransactionDao.insert(...)  [dentro de una transacción de Room]
          → BalanceDao.updateAvailable(...) en cascada de ancestros
    ← Result<Unit>
  ← actualiza StateFlow
← Composable recompone con el nuevo estado
```

Nota cómo el `UseCase` nunca menciona Room, DAOs, ni SQL — solo conoce la interfaz `TransactionRepository`. Eso es exactamente lo que hace esto "Hexagonal": el negocio no sabe (ni le importa) qué hay del otro lado del Port.