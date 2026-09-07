# Arquitectura

## Enfoque general

**Clean Architecture simplificada + MVVM + Repository Pattern.** No se adopta Clean Architecture "de libro" con múltiples módulos Gradle separados (`:domain`, `:data`, `:app`) — para un proyecto de este tamaño, la separación por **paquetes** dentro de un único módulo es suficiente y evita la sobrecarga de configuración de build multi-módulo sin beneficio real en esta etapa. Si el proyecto crece considerablemente (equipo grande, necesidad de builds independientes), se puede modularizar después sin rediseñar la lógica interna.

## Capas

```
ui/          → Composables, ViewModels, estado de UI
domain/      → Modelos de dominio, casos de uso (Use Cases), interfaces de repositorio
data/        → Implementación de repositorios, Room (entities, DAOs, database), mappers
di/          → Módulos de inyección de dependencias
```

### `domain/`

- No depende de Android ni de Room — es Kotlin puro. Esto permite probarlo con JUnit sin instrumentación, cumpliendo RNF-012.
- Contiene los modelos definidos en [`domain-model.md`](./domain-model.md) como clases de datos (`data class`) puras.
- Contiene los **Use Cases**: una clase por operación de negocio significativa (`RegisterTransactionUseCase`, `CreateBalanceUseCase`, `RenewBalanceUseCase`, `RecalculateBalanceUseCase`, etc.). Cada uno encapsula una operación transaccional completa (ver `domain-model.md`, sección de operaciones atómicas).
- Define **interfaces** de repositorio (`BalanceRepository`, `TransactionRepository`, `RecurringRuleRepository`) — el dominio no sabe que la implementación usa Room.

### `data/`

- Implementa las interfaces de repositorio definidas en `domain/`.
- Contiene las entidades de Room (`@Entity`) y DAOs (`@Dao`) — ver [`database.md`](./database.md).
- Contiene *mappers* explícitos entre entidades de Room y modelos de dominio (evita filtrar anotaciones de Room hacia el dominio).

### `ui/`

- Un `ViewModel` por pantalla, que invoca Use Cases (nunca repositorios directamente, para mantener la lógica de negocio fuera de la capa de presentación).
- Estado expuesto vía `StateFlow`, consumido por Composables con `collectAsStateWithLifecycle()`.
- Composables sin lógica de negocio — solo renderizado y delegación de eventos al `ViewModel`.

### `di/`

- Inyección de dependencias con **Hilt** (ver justificación en [`decisions.md`](./decisions.md) — pendiente de ADR específica si se requiere, pero es la opción estándar recomendada para proyectos Android/Kotlin actuales, con integración directa a `ViewModel` vía `@HiltViewModel`).

## Patrones aplicados y su justificación

| Patrón | ¿Se usa? | Justificación |
|---|---|---|
| MVVM | Sí | Estándar recomendado por Google para Compose; separa estado de UI de la lógica de presentación. |
| Repository Pattern | Sí | Desacopla el dominio de la fuente de datos concreta (Room); facilita testing con repositorios falsos. |
| Use Cases | Sí | Las operaciones de este dominio (cascadas, reajustes, renovaciones) tienen suficiente complejidad de negocio como para no vivir directamente en el ViewModel — mantenerlas en Use Cases las hace reutilizables y testeables de forma aislada. |
| Multi-módulo Gradle | **No** (por ahora) | Sobreingeniería para el tamaño actual del proyecto; se reconsiderará si el proyecto escala significativamente. |
| Entidades de dominio separadas de entidades de Room | Sí | Necesario porque el dominio no debe depender de anotaciones de framework (RNF-012); el costo de los mappers es bajo y vale la pena. |

## Flujo de una operación típica (ejemplo: registrar un gasto)

```
Composable (UI evento) 
  → ViewModel.onRegisterExpense(...) 
    → RegisterTransactionUseCase(balanceId, amount, ...) 
      → TransactionRepository.insert(...)       [dentro de una transacción de Room]
      → BalanceRepository.propagateToAncestors(...)
      → BalanceRepository.triggerRebalanceIfNeeded(...)
    ← Result<Unit>
  ← actualiza StateFlow
← Composable recompone con el nuevo estado
```