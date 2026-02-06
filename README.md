# hibernate-generic-dao
Fork de Hibernate Generic D.A.O. Framework https://code.google.com/archive/p/hibernate-generic-dao

El motivo de realizar este proyecto es actualizarlo para que sea compatible con Hibernate 5, que el desarrollador original ha dejado de mantenerlo.

De este proyecto se han suprimido los test unitarios del desarrollador original, al menos temporalmente.

## Que hace este proyecto

Este framework ofrece una base generica para construir DAOs y una capa de busqueda reutilizable sobre Hibernate/JPA. La
idea original surgio al mantener muchos DAOs escritos a mano que:

- Tenian metodos y comportamientos inconsistentes.
- Eran tediosos de ampliar cuando se necesitaban nuevas columnas filtrables u ordenables.
- Exigian duplicar mucho codigo cada vez que se creaba un DAO nuevo.
- Eran dificiles de testear y, por tanto, rara vez se testeaban.

El proyecto propone una solucion con foco en simplicidad y consistencia:

- **Generic DAO**: Implementaciones genericas robustas y faciles de reutilizar. Si no quieres usarlas tal cual, el
  codigo fuente sirve como guia para resolver detalles complejos habituales.
- **Search**: Una capa de busqueda pensada para listados con ordenacion, filtros, seleccion de columnas y paginacion. Se
  puede usar con o sin el generic DAO, reduce codigo repetitivo y simplifica tests. Es similar a Hibernate Criteria,
  pero mas simple y transportable entre capas (incluida la vista o capas remotas), y funciona tanto con Hibernate como
  con JPA.
- **Remote DAO / Remote Search**: Utilidades y ejemplos para exponer un punto unico de acceso remoto que permita CRUD y
  busquedas para cualquier entidad. Esto facilita aplicaciones con clientes pesados (R.I.A.), evitando un servicio
  remoto por entidad y manteniendo consistencia entre cliente y servidor.

Nota: para JPA se requiere un adaptador por proveedor. Actualmente el adaptador incluido es para Hibernate Entity
Manager.

## Como usarlo en otros proyectos

### Maven

Elige los modulos que necesites y anadelos como dependencias. Ejemplos:

Para Hibernate (Session) con DAO y Search:

```xml

<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>dao-hibernate</artifactId>
  <version>2.0.2</version>
</dependency>
```

Para JPA con Search:

```xml

<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>search-jpa-hibernate</artifactId>
  <version>2.0.2</version>
</dependency>
```

Si solo necesitas la capa de busqueda independiente:

```xml

<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>search</artifactId>
  <version>2.0.2</version>
</dependency>
```

### Gradle

```gradle
dependencies {
  implementation "io.github.jcprieto:dao-hibernate:2.0.2"
  // o: implementation "io.github.jcprieto:search-jpa-hibernate:2.0.2"
}
```

### Uso desde el codigo fuente

Si prefieres consumir la libreria desde el codigo fuente, compila e instala en el repositorio local:

```bash
mvn clean install
```

Luego declara las dependencias anteriores usando la misma version.

## Changelog

### 2.0.2

- Se agrega workflow de CI `ci-baseline` para ejecutar `mvn clean verify -Dgpg.skip=true` y recoger metricas de
  compilacion.
- Se anade script `.github/scripts/collect-baseline-metrics.sh` para publicar resumen y JSON de duracion, warnings,
  errores y tests.
- Se actualiza `maven-compiler-plugin` a `3.15.0`.

### 2.0.1

- Ajustes de metadatos Hibernate para evitar API deprecada y mejorar proxies en Hibernate 5.6.
- Nuevas pruebas unitarias en `search-hibernate`.

### 2.0.0

- Se redefinen dependencias, se añaden pruebas unitarias y se amplía documentación
