# Changelog

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
