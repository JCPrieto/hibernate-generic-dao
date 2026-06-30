# Changelog

## 3.0.2

- Se limpia `FlexSearch` en `search`, eliminando comprobaciones redundantes sobre arrays tipados, modernizando bucles
  internos y manteniendo el filtrado defensivo de valores nulos o vacios.
- Se amplia la cobertura unitaria de `FlexSearch` en `search`, incluyendo filtros, ordenaciones, campos, fetches y
  propiedades escalares.
- Se ajustan los adaptadores Flex de `dao-hibernate`, eliminando declaraciones de excepciones innecesarias en metodos
  de busqueda, suprimiendo trazas directas a consola y acotando tipos genericos en resultados de listas y conteos.
- Se actualiza `org.javassist:javassist` de `3.31.0-GA` a `3.32.0-GA` en `search-hibernate`.
- Se ignora el directorio local `.codex/` para evitar publicar configuracion de entorno.
- Se suben todos los modulos a `3.0.2` para mantener alineado el grafo interno basado en `${project.version}`; el
  impacto es de mantenimiento y correccion, sin cambios funcionales incompatibles previstos.

## 3.0.1

- Se corrige `dao` para que `DAODispatcher` convierta los arrays de entidades e IDs al tipo real antes de delegar en
  `GenericDAO`, evitando `ClassCastException` y errores de seleccion de overload en operaciones varargs.
- Se amplia la cobertura unitaria de `DAODispatcher` en `dao`, incluyendo delegacion a DAOs especificos tipados,
  delegacion por reflexion, fallback al DAO general y caminos de arrays nulos, vacios, uniformes y mixtos.
- Se cambia `dao-hibernate` para que `DAODispatcher.save((Object[]) null)` devuelva un array booleano vacio en vez de
  `null`, alineandolo con el comportamiento defensivo de no-op.
- Se actualiza `jacoco-maven-plugin` de `0.8.14` a `0.8.15`.
- Se actualiza `central-publishing-maven-plugin` de `0.10.0` a `0.11.0` y se reemplaza `tokenAuth` por `autoPublish`.
- Se mueve la firma GPG y la publicacion en Maven Central al perfil `release`, dejando el build local/CI normal sin
  firma.
- Se fija `softprops/action-gh-release` a un SHA completo para evitar dependencias de GitHub Actions basadas en tags
  mutables.
- Se suben todos los modulos a `3.0.1` para mantener alineado el grafo interno basado en `${project.version}`.

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
