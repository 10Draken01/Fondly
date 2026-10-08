# Fondly

Aplicación Android de control financiero personal — local-first, sin cuentas, sin nube, sin dependencias externas.

## Descripción

Fondly ayuda a controlar ingresos, gastos, presupuestos, apartados de dinero (saldos) y ahorro, todo almacenado directamente en el dispositivo del usuario. No requiere inicio de sesión, backend, ni sincronización en la nube — tus datos financieros nunca salen de tu teléfono.

## Objetivo

Dar a una persona control claro y sencillo sobre su dinero: cuánto tiene disponible, en qué categorías/periodos, qué gastos recurrentes tiene comprometidos, y cuánto está ahorrando realmente — sin la complejidad ni los riesgos de privacidad de una app bancaria conectada.

## Funcionalidades principales

- Registro de ingresos y gastos.
- Saldos jerárquicos (padre/hijo) para presupuestos y apartados, con reajuste automático entre periodos relacionados.
- Ingresos y gastos recurrentes (nómina, suscripciones, servicios).
- Renovación automática de saldos con estrategias configurables (reinicio, acumulación, transferencia a ahorro).
- Historial de ahorro mensual, diferenciado de dinero simplemente no gastado.
- Notificaciones locales de advertencia de presupuesto.
- Configuración de tema (claro/oscuro) e idioma (Español/Inglés, ampliable a futuro).

## Estado del proyecto

🚧 En desarrollo — Sprint 1 (gestión de saldos) implementado sobre arquitectura multi-módulo y base de datos Room v2 (ver [`docs/`](./docs) y ADR-0001 a ADR-0004).

## Stack tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose
- **Build**: Gradle con Kotlin DSL (`build.gradle.kts`)
- **Arquitectura**: Clean Architecture (simplificada) + MVVM — ver [`docs/architecture/architecture.md`](./docs/architecture/architecture.md)
- **Persistencia local**: Room (migraciones explícitas, esquema versionado — `docs/architecture/database.md`)
- Detalles completos y justificación de cada elección en [`docs/architecture/decisions.md`](./docs/architecture/decisions.md)

## Convenciones del proyecto

- **Código** (proyecto, paquetes, clases, funciones, variables, entidades): siempre en **inglés**.
- **Interfaz de usuario** (texto visible): siempre en **español** por defecto, con soporte de internacionalización (i18n) para español e inglés desde el diseño inicial.

## Documentación

Toda la documentación técnica y de producto vive en [`docs/`](./docs):

```
docs/
├── requirements/
│   ├── functional-requirements.md
│   ├── non-functional-requirements.md
│   └── business-rules.md
├── architecture/
│   ├── architecture.md
│   ├── domain-model.md
│   ├── database.md
│   └── decisions.md
├── agile/          (próximamente: backlog, historias de usuario, sprints)
└── ux/             (próximamente: pantallas y flujos de usuario)
```

## Requisitos

- Android Studio (versión estable más reciente).
- JDK embebido de Android Studio (no requiere instalación manual).
- Android SDK API 24+ (minSdk del proyecto).

## Instalación y ejecución

```bash
git clone https://github.com/10Draken01/Fondly.git
cd Fondly
```

Abre la carpeta en Android Studio, espera el sync de Gradle, y ejecuta con ▶️ en un emulador o dispositivo físico con depuración USB habilitada.

## Estructura del proyecto

```
app/                   (composition root: Application, MainActivity)
core/
├── domain/            (JVM puro: modelos, ports, excepciones — sin Android/Room)
├── application/       (JVM puro: casos de uso)
├── infrastructure/    (Android: Room, mappers, repositorios, DI Hilt)
└── ui_common/         (design system Compose compartido, rutas)
feature/
├── balances/          (dashboard, crear, editar, detalle)
├── balance_sections/  (apartados)
├── home/              (shell de navegación, splash)
├── settings/          (tema e idioma)
├── transactions/      (Sprint 2)
└── recurring/         (Sprint 3)
```
Reglas de dependencia: ver ADR-0002.

## Roadmap

- [x] Fase 1 — Preparación del entorno
- [x] Fase 2 — Requerimientos, modelo de dominio, arquitectura, base de datos, casos de uso
- [x] Fase 3 — Metodología Scrum (épicas, historias de usuario, backlog, sprints)
- [ ] Fase 4 — Implementación incremental por sprint

## Metodología

Este proyecto se desarrolla siguiendo **Scrum**, con entregas incrementales priorizadas por un Product Backlog (ver `docs/agile/` cuando se publique en la Fase 3).

## Licencia

Por definir.