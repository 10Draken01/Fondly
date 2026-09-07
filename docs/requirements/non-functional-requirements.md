# Requerimientos No Funcionales

## Rendimiento

- **RNF-001**: Las operaciones de lectura del dashboard (disponible de saldos) deben resolverse desde el caché de `Balance.available`, sin recorrer el ledger completo en cada render.
- **RNF-002**: El registro de una transacción (ingreso/gasto) debe reflejarse en la UI en menos de 300ms en un dispositivo de gama media.

## Disponibilidad Offline

- **RNF-003**: La aplicación debe ser completamente funcional sin conexión a internet en todo momento; ninguna operación crítica (registrar, consultar, configurar) puede depender de red.

## Seguridad Local y Privacidad

- **RNF-004**: Ningún dato financiero del usuario debe salir del dispositivo por ningún medio (red, logs remotos, analítica de terceros) en la versión local-first del MVP.
- **RNF-005**: La aplicación no debe requerir permisos de Android más allá de los estrictamente necesarios (notificaciones locales; ninguno de red, contactos, ubicación, etc.).

## Persistencia y Consistencia de Datos

- **RNF-006**: El sistema debe garantizar que el caché `available` de cualquier `Balance` sea siempre reconstruible desde el ledger de transacciones (ver ADR-0001).
- **RNF-007**: Las operaciones que afectan más de un `Balance` (cascada hacia ancestros, reajuste de hijos periódicos) deben ejecutarse de forma atómica (transacción de base de datos), evitando estados intermedios inconsistentes.

## Usabilidad

- **RNF-008**: La aplicación debe seguir Material Design 3 y adaptarse a modo claro y oscuro.
- **RNF-009**: Los estados vacíos (sin saldos, sin transacciones) deben mostrar orientación clara sobre el siguiente paso a seguir, no solo una pantalla en blanco.

## Internacionalización (i18n)

- **RNF-010**: Todo texto visible al usuario debe residir en recursos de strings (`strings.xml`), nunca hardcodeado en el código fuente.
- **RNF-011**: La aplicación debe soportar Español e Inglés desde el MVP, con arquitectura de recursos preparada para agregar más idiomas sin cambios estructurales.

## Mantenibilidad

- **RNF-012**: El código de dominio (entidades, casos de uso, reglas de negocio) debe estar desacoplado de detalles de Android (Compose, Room) para permitir pruebas unitarias sin dependencias de framework.

## Testabilidad

- **RNF-013**: Los casos de uso críticos (registrar transacción, renovación de saldo, reajuste de hijos periódicos) deben tener cobertura de pruebas unitarias antes de considerarse completos (Definition of Done, ver Fase 3).

## Robustez de Automatizaciones (Android)

- **RNF-014**: Las automatizaciones (ingresos/gastos recurrentes, renovaciones, notificaciones programadas) deben ejecutarse correctamente ante reinicios del dispositivo, cambios de fecha/hora del sistema, cambios de zona horaria y horario de verano, sin duplicar ni omitir ejecuciones. (Detalle técnico de implementación en Fase 2 — sección de automatizaciones Android, y en la Fase 4 de implementación.)