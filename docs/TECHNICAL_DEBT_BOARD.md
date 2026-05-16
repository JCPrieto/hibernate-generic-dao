# Tablero de deuda tecnica

Este tablero centraliza la deuda tecnica por modulo. El owner es responsable de priorizar, mantener el estado
actualizado
y asegurar que cada cambio relevante deje pruebas o una justificacion explicita.

## Owners por modulo

| Modulo                 | Owner inicial                           | Alcance                                                                                       |
|------------------------|-----------------------------------------|-----------------------------------------------------------------------------------------------|
| `search`               | `JCPrieto` / owner-search               | API de busqueda, filtros, ordenacion, resultados, procesador base y utilidades JPA comunes.   |
| `dao`                  | `JCPrieto` / owner-dao                  | API DAO JPA, dispatcher base, reflection utilities y DAOs genericos JPA.                      |
| `search-hibernate`     | `JCPrieto` / owner-search-hibernate     | Integracion Search con Hibernate Session, metadatos Hibernate y transformacion de resultados. |
| `dao-hibernate`        | `JCPrieto` / owner-dao-hibernate        | DAO Hibernate nativo, adapters Flex/remotos y paquete legacy `original`.                      |
| `search-jpa-hibernate` | `JCPrieto` / owner-search-jpa-hibernate | Adaptador JPA Hibernate y compatibilidad entre EntityManagerFactory y SessionFactory.         |

## Criterios de prioridad

| Prioridad | Criterio                                                                          |
|-----------|-----------------------------------------------------------------------------------|
| P0        | Riesgo alto de fallo en runtime, fuga de memoria, concurrencia o build bloqueado. |
| P1        | Deuda que frena mantenimiento, upgrades o pruebas de comportamiento critico.      |
| P2        | Limpieza estructural o documentacion que reduce friccion, sin impacto urgente.    |

## Backlog priorizado

| ID     | Modulo                 | Owner                      | Prioridad | Estado  | Punto de mejora                                                                                        | Criterio de aceptacion                                                                                                            |
|--------|------------------------|----------------------------|-----------|---------|--------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| TD-001 | `search-hibernate`     | owner-search-hibernate     | P0        | Backlog | Revisar caches estaticas por `SessionFactory` en `HibernateMetadataUtil` y `HibernateSearchProcessor`. | Acceso concurrente seguro, estrategia contra referencias retenidas indefinidamente y tests de reutilizacion por `SessionFactory`. |
| TD-002 | `search`               | owner-search               | P0        | Backlog | Sustituir uso publico de `javax.activation.UnsupportedDataTypeException` por excepcion de dominio.     | API documentada, compatibilidad de migracion definida y tests para valores no soportados.                                         |
| TD-003 | `dao-hibernate`        | owner-dao-hibernate        | P0        | Backlog | Validar cargas dinamicas con `Class.forName` en adapters Flex/remotos.                                 | Resolver clases mediante whitelist/registro controlado y tests de clase permitida/no permitida.                                   |
| TD-004 | `dao`                  | owner-dao                  | P1        | Backlog | Cubrir `BaseDAODispatcher` y `DAOUtil` con pruebas de reflection, varargs y ambiguedad.                | Tests unitarios para resolucion de metodo, argumentos nulos, varargs y errores esperados.                                         |
| TD-005 | `dao-hibernate`        | owner-dao-hibernate        | P1        | Backlog | Reducir duplicidad entre dispatcher DAO Hibernate y JPA.                                               | Logica comun extraida o patron documentado; sin cambios de comportamiento publico.                                                |
| TD-006 | `search-hibernate`     | owner-search-hibernate     | P1        | Backlog | Migrar o aislar APIs Hibernate legacy (`ResultTransformer`, `Transformers`).                           | Compatibilidad Hibernate 5 preservada y camino de migracion a Hibernate 6/7 documentado.                                          |
| TD-007 | `search-jpa-hibernate` | owner-search-jpa-hibernate | P1        | Backlog | Revisar dependencia de `org.hibernate.ejb.HibernateEntityManagerFactory`.                              | Adaptador compatible con la version soportada de Hibernate o deprecacion documentada.                                             |
| TD-008 | `search`               | owner-search               | P1        | Backlog | Reducir raw types en `SearchFacade`, `SearchResult` y fachadas relacionadas.                           | API generica donde sea viable sin romper compatibilidad binaria; warnings reducidos o justificados.                               |
| TD-009 | `dao-hibernate`        | owner-dao-hibernate        | P1        | Backlog | Decidir estrategia para paquete `com.googlecode.genericdao.dao.hibernate.original`.                    | Mantener/deprecar/eliminar definido en changelog o roadmap, con impacto de compatibilidad documentado.                            |
| TD-010 | `search`               | owner-search               | P2        | Backlog | Centralizar conversiones y construccion de numeros deprecada en `InternalUtil`/`SearchUtil`.           | Reemplazo por `valueOf`/constructores no deprecados y tests de conversion existentes verdes.                                      |
| TD-011 | `dao`                  | owner-dao                  | P2        | Backlog | Ampliar cobertura de DAOs JPA genericos.                                                               | Tests para `JPABaseDAO`, `GenericDAOImpl` o smoke tests con fixture minimo.                                                       |
| TD-012 | `search-jpa-hibernate` | owner-search-jpa-hibernate | P2        | Backlog | Anadir pruebas especificas del adaptador JPA Hibernate.                                                | Tests que validen puente EntityManagerFactory/SessionFactory o limitacion documentada.                                            |

## En curso

| ID     | Modulo | Owner             | Prioridad | Estado   | Punto de mejora                                                                       |
|--------|--------|-------------------|-----------|----------|---------------------------------------------------------------------------------------|
| TD-000 | Todos  | owner-maintenance | P1        | En curso | Mantener baseline CI y metricas de build/test/warnings como artefacto de seguimiento. |

## Hecho

| ID          | Modulo | Owner             | Prioridad | Estado | Punto de mejora                                                 |
|-------------|--------|-------------------|-----------|--------|-----------------------------------------------------------------|
| TD-DONE-001 | Todos  | owner-maintenance | P1        | Hecho  | Crear workflow `ci-baseline` y recolector de metricas de build. |

## Reglas de mantenimiento

- Actualizar el estado cuando una tarea entre en desarrollo o se cierre.
- Anadir tareas nuevas con owner, prioridad y criterio de aceptacion.
- En PRs, referenciar el ID del tablero cuando el cambio reduzca deuda tecnica.
- Mantener el owner como rol si no hay responsable nominal confirmado.
