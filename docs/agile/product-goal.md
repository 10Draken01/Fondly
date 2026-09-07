# Product Goal y Definiciones Scrum

## Product Goal

> Entregar una aplicación Android local, privada y confiable que permita a una persona entender y controlar su dinero — cuánto tiene, cuánto puede gastar, cuánto debe pagar próximamente y cuánto está ahorrando realmente — sin necesidad de conexión, cuentas ni exposición de sus datos financieros a terceros.

## Definition of Ready (DoR)

Una historia de usuario está lista para entrar a un Sprint cuando:

- Tiene criterios de aceptación claros y verificables.
- Referencia los requerimientos (`RF-XXX`) y reglas de negocio (`BR-XXX`) aplicables, si corresponde.
- No tiene dependencias bloqueantes sin resolver.
- Su complejidad ha sido estimada por el equipo (en este caso, tú como único desarrollador, validando que el alcance es claro antes de empezar).
- La UI necesaria (si aplica) tiene al menos un boceto o descripción de pantalla en `docs/ux/`.

## Definition of Done (DoD)

Una historia de usuario se considera terminada cuando:

- El código compila sin advertencias nuevas y pasa el linter configurado.
- Los casos de uso y reglas de negocio involucrados tienen pruebas unitarias (RNF-013).
- La funcionalidad fue probada manualmente en un dispositivo/emulador real.
- Todo texto de UI está en `strings.xml` (español e inglés), nunca hardcodeado (RNF-010, RNF-011).
- El código sigue las convenciones de nombres definidas (inglés en código, español en UI).
- Los cambios están commiteados con mensajes descriptivos y pusheados a GitHub.
- La documentación relevante (`docs/`) está actualizada si la historia introdujo un cambio de modelo, arquitectura o regla de negocio no contemplado antes.

## Épicas

| ID | Épica | Descripción |
|---|---|---|
| E1 | Gestión de Saldos | Crear, editar, desactivar y jerarquizar saldos (padres/hijos). |
| E2 | Ingresos | Registro manual de ingresos. |
| E3 | Gastos | Registro manual de gastos, selección de saldo, cascada de descuento. |
| E4 | Automatizaciones | Ingresos/gastos recurrentes, renovación de saldos, reajuste de hijos periódicos. |
| E5 | Ahorro | Clasificación de saldos como ahorro, historial mensual. |
| E6 | Notificaciones | Alertas locales configurables. |
| E7 | Configuración y Personalización | Tema e idioma. |
| E8 | Historial y Consultas | Listado filtrable de transacciones, resumen mensual. |

## Sprint Goal (plantilla, se completa por sprint)

> Al finalizar el Sprint N, el usuario podrá [capacidad concreta y demostrable], validado mediante [criterio de aceptación/demo].