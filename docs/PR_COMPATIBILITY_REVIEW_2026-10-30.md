# Evaluación de PR abiertos: Java 8 e Hibernate 5

Revisión del 30 de octubre de 2026 sobre `master` en
`598250cc5d773c961fcb43923210ae73837455c1` (versión 3.0.4).

## Resultado

No quedan actualizaciones compatibles pendientes de aplicar entre los cuatro PR
abiertos revisados. Los dos cambios compatibles ya entraron mediante el
[PR #40](https://github.com/JCPrieto/hibernate-generic-dao/pull/40), commit
`806ca512f80aa36041bc5f11362f9e5c9cd2b71c`. Reaplicarlos no produciría cambios.
Se mantienen las versiones actuales y no se incrementa la versión del proyecto.

| PR | Cambio propuesto | Decisión | Evidencia |
|----|------------------|----------|-----------|
| [#36](https://github.com/JCPrieto/hibernate-generic-dao/pull/36) | SLF4J API 2.0.19 → 2.0.20 | Compatible, ya aplicado | `search/pom.xml` ya declara 2.0.20; SLF4J 2.0 mantiene Java 8 como mínimo. |
| [#39](https://github.com/JCPrieto/hibernate-generic-dao/pull/39) | Central Publishing Maven Plugin 0.11.0 → 0.12.0 | Ya aplicado; herramienta de publicación | `pom.xml` ya declara 0.12.0 dentro de `release`; no es una dependencia de ejecución de la biblioteca. |
| [#16](https://github.com/JCPrieto/hibernate-generic-dao/pull/16) | Hibernate Core 5.6.15.Final → 6.0.0.Final en ambos módulos | Excluir | Hibernate 6 requiere Java 11 y Jakarta Persistence; incumple Java 8 y la API `javax.persistence` soportada. |
| [#41](https://github.com/JCPrieto/hibernate-generic-dao/pull/41) | Hibernate Core 5.6.15.Final → 7.4.14.Final en ambos módulos | Excluir | Hibernate 7.4 requiere Java 17 como mínimo y Jakarta Persistence 3.2; incumple Java 8 y Hibernate 5. |

## Compatibilidad

`search-hibernate` y `search-jpa-hibernate` conservan Hibernate 5.6.15.Final.
El adaptador JPA todavía expone
`org.hibernate.ejb.HibernateEntityManagerFactory`; subir solo la dependencia no
constituye una migración compatible. La revisión de ese adaptador está registrada
en TD-007, y el aislamiento de las API legacy en TD-006. Esta evaluación no resuelve
esas deudas ni modifica la API pública.

Fuentes oficiales:

- [Requisitos de SLF4J](https://www.slf4j.org/faq.html#requirements).
- [Migración a Hibernate 6: Java 11 y Jakarta Persistence](https://docs.hibernate.org/orm/6.0/migration-guide/).
- [Compatibilidad de Hibernate 7.4](https://hibernate.org/orm/releases/7.4/#compatibility).

## Validación

Validación local con Amazon Corretto 1.8.0_482 y Maven 3.9.12:

- `mvn -B clean verify`: **BUILD SUCCESS** en el padre y los cinco módulos.
  Se generan los JAR, fuentes, Javadoc e informes JaCoCo correspondientes.
- 63 pruebas ejecutadas, sin fallos, errores ni pruebas omitidas:
  `search` (12), `search-hibernate` (10) y `dao` (41).
- `mvn -B -Prelease validate`: **BUILD SUCCESS**. Comprueba la carga de la
  extensión Central Publishing 0.12.0 con Java 8 y la validación del reactor;
  no ejecuta firma, subida ni publicación. No acredita una publicación real.

Ambos comandos se ejecutaron con `JAVA_HOME` apuntando al JDK 8 y su directorio
`bin` al principio de `PATH`. El build conserva avisos existentes de Javadoc y
codificación. Las pruebas actuales no cubren toda la integración con una base de
datos real; el resultado acredita el build y la suite existente, sin garantizar
todos los escenarios de las aplicaciones consumidoras.

Los PR originales no se cierran ni se fusionan como parte de esta revisión.
