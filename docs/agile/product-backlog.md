# Product Backlog

Priorizado con MoSCoW, agrupado por épica. El orden dentro de cada categoría refleja el orden sugerido de implementación (de arriba hacia abajo).

## Must Have (MVP no es viable sin esto)

| ID | Historia | Épica | Puntos |
|---|---|---|---|
| US-001 | Crear saldo | E1 | 3 |
| US-002 | Editar saldo | E1 | 2 |
| US-004 | Crear saldo hijo (apartado) | E1 | 5 |
| US-005 | Registrar ingreso | E2 | 3 |
| US-006 | Registrar gasto | E3 | 5 |
| US-012 | Consultar saldos (dashboard) | E8 | 3 |
| US-010 | Ejecución automática de recurrencias | E4 | 8 |
| US-011 | Renovación automática de saldos | E4 | 8 |

**Subtotal Must Have**: 37 puntos

## Should Have (MVP funcional sin esto, pero esperado por el usuario)

| ID | Historia | Épica | Puntos |
|---|---|---|---|
| US-003 | Desactivar/eliminar saldo | E1 | 3 |
| US-007 | Reajuste automático de saldo diario ante gasto del padre | E1/E4 | 8 |
| US-008 | Crear ingreso recurrente | E4 | 5 |
| US-009 | Crear gasto recurrente (servicio/membresía) | E4 | 5 |
| US-013 | Consultar historial de transacciones | E8 | 3 |
| US-014 | Consultar historial de ahorro | E5 | 3 |
| US-017 | Configurar idioma | E7 | 3 |

**Subtotal Should Have**: 30 puntos

## Could Have (valioso, pero postergable sin comprometer el MVP)

| ID | Historia | Épica | Puntos |
|---|---|---|---|
| US-015 | Configurar notificaciones | E6 | 5 |
| US-016 | Configurar tema | E7 | 2 |
| US-018 | Resumen mensual | E8 | 5 |

**Subtotal Could Have**: 12 puntos

## Won't Have (explícitamente fuera de alcance para esta versión)

| Elemento | Razón |
|---|---|
| Cuentas de usuario, login, backend | Fuera de alcance por definición del proyecto (100% local). |
| Sincronización en la nube | Ídem. |
| Estrategias `FRONT_LOADED` y `CUSTOM` de reajuste de hijos periódicos | Documentadas en ADR-0001 como trabajo futuro; solo `EVEN` en el MVP. |
| `Category` como entidad completa con jerarquía y gestión propia | Se mantiene como campo simple opcional; se reevaluará si se solicita mayor granularidad. |
| Estadísticas avanzadas (gráficas de tendencia, proyecciones) | Valor claro pero no esencial para el objetivo mínimo de controlar el dinero; candidato para una v2. |
| Soporte de más de 2 idiomas | La arquitectura de recursos lo permite, pero solo ES/EN se implementan ahora. |

## Orden sugerido de implementación (resumen)

1. **Fundamentos de saldos** (US-001, US-002, US-004, US-012) — sin esto no hay nada que probar.
2. **Movimiento de dinero** (US-005, US-006) — el corazón funcional de la app.
3. **Automatización crítica** (US-010, US-011) — lo que distingue a la app de una simple libreta de notas.
4. **Refinamiento de saldos** (US-003, US-007) — mejora la robustez del modelo ya funcional.
5. **Recurrencias configurables** (US-008, US-009) — construyen sobre US-010.
6. **Consultas** (US-013, US-014) — requieren datos ya generados por los pasos anteriores para tener sentido al probarlas.
7. **Personalización y notificaciones** (US-015, US-016, US-017) — mejoras de experiencia, no bloquean el flujo core.
8. **Resumen mensual** (US-018) — se beneficia de tener ya historial y ahorro implementados.