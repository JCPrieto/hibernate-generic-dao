# Changelog

## 3.0.0

- Breaking change en `search`: se elimina definitivamente `Filter.equal(...)`; usar `Filter.eq(...)` como API de
  igualdad.
- Se actualiza `org.slf4j:slf4j-api` de `2.0.17` a `2.0.18` en `search`.
- Se agrega cobertura unitaria para `BaseDAODispatcher` en `dao` y dependencia de test `JUnit 4.13.2`.
- Se reemplaza en `dao-hibernate` la consulta HQL dinamica de existencia por `Session.byId(...).loadOptional(...)`,
  evitando construccion dinamica de query y manteniendo compatibilidad Hibernate 5.
- Se anade `@Override` en `DAODispatcher.flush()` para aclarar la implementacion de `GeneralDAO`.
- Se configura JaCoCo XML para que SonarQube importe cobertura desde los reportes generados durante `verify`.
- Se restringen permisos globales del workflow de release a `contents: read` y se mueve `contents: write` al job que
  crea la release.
- Se anade insignia de Quality Gate de SonarQube al `README`.
- Se documenta el tablero de deuda tecnica en `docs/TECHNICAL_DEBT_BOARD.md` y se referencia desde `AGENTS.md`.
- Se suben todos los modulos a `3.0.0` porque el cambio de API en `search` impacta al grafo completo de dependencias
  internas mediante `${project.version}`.

## 2.1.0

- Se anade `Filter.eq(...)` como metodo recomendado para crear filtros de igualdad y se depreca `Filter.equal(...)`
  para evitar confusion con `Object.equals(Object)`, manteniendo compatibilidad.
- Se actualizan usos internos de filtros de igualdad para usar `Filter.eq(...)`.
- Se reemplaza el conteo dinamico HQL de `HibernateBaseDAO` por Criteria `rowCount()`.
- Se marca `DAODispatcher.flush()` con `@Deprecated`, alineandolo con su Javadoc.
- Se integra SonarQube en pull requests y en el workflow de release, esperando la Quality Gate antes de generar
  releases.
- Se actualiza `org.javassist:javassist` de `3.30.2-GA` a `3.31.0-GA` en `search-hibernate`.

## 2.0.2

- Se agrega workflow de CI `ci-baseline` para ejecutar `mvn clean verify -Dgpg.skip=true` y recoger metricas de
  compilacion.
- Se anade script `.github/scripts/collect-baseline-metrics.sh` para publicar resumen y JSON de duracion, warnings,
  errores y tests.
- Se actualiza `maven-compiler-plugin` a `3.15.0`.

## 2.0.1

- Ajustes de metadatos Hibernate para evitar API deprecada y mejorar proxies en Hibernate 5.6.
- Nuevas pruebas unitarias en `search-hibernate`.

## 2.0.0

- Se redefinen dependencias, se añaden pruebas unitarias y se amplía documentación
