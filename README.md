# hibernate-generic-dao

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JCPrieto_hibernate-generic-dao&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JCPrieto_hibernate-generic-dao)

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
  <version>3.0.4</version>
</dependency>
```

Para JPA con Search:

```xml

<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>search-jpa-hibernate</artifactId>
  <version>3.0.4</version>
</dependency>
```

Si solo necesitas la capa de busqueda independiente:

```xml

<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>search</artifactId>
  <version>3.0.4</version>
</dependency>
```

### Gradle

```gradle
dependencies {
  implementation "io.github.jcprieto:dao-hibernate:3.0.4"
  // o: implementation "io.github.jcprieto:search-jpa-hibernate:3.0.4"
}
```

### Uso desde el codigo fuente

Si prefieres consumir la libreria desde el codigo fuente, compila e instala en el repositorio local:

```bash
mvn clean install
```

Luego declara las dependencias anteriores usando la misma version.

## Publicacion automatica en Maven Central

Al publicar una release estable en GitHub (`release.published`), el workflow
[release](.github/workflows/release.yml) valida la version, compila e instala con Java 8,
ejecuta SonarQube con Java 17 y, si todo termina correctamente, firma y publica el reactor
completo (POM padre y cinco modulos) con Java 8 mediante `mvn -B -ntp clean deploy -Prelease`.
El perfil existente usa `autoPublish=true` y espera hasta que Central confirme la publicacion.
Los pushes a `master` mantienen el analisis SonarQube; las prereleases y los borradores no publican.

### Configuracion inicial

En GitHub, configura estos secretos en el entorno **release**
(`Settings > Environments > release`) o como secretos del repositorio:

| Secreto            | Contenido                                                                |
|--------------------|--------------------------------------------------------------------------|
| `CENTRAL_USERNAME` | Usuario del token generado en Central Portal.                            |
| `CENTRAL_PASSWORD` | Password del mismo token de Central Portal.                              |
| `GPG_PRIVATE_KEY`  | Clave privada ASCII armored, incluyendo las cabeceras y saltos de linea. |
| `GPG_PASSPHRASE`   | Frase de paso de la clave privada.                                       |

El job de SonarQube utiliza `SONAR_TOKEN` como secreto del **repositorio**, ya que se ejecuta
fuera del entorno `release`. Si el entorno tiene revisores obligatorios, la publicacion
esperara su aprobacion; elimina ese requisito si deseas un flujo totalmente automatico.

La cuenta de Central debe tener verificado el namespace `io.github.jcprieto`. Genera las
credenciales siguiendo
la [guia de tokens de Central Portal](https://central.sonatype.org/publish/generate-portal-token/).
La clave GPG debe corresponder al fingerprint configurado en el POM (`558442263B6AC29B9FF4AEB5F320065B4D152C59`) y su
clave publica debe estar disponible
segun los [requisitos de firma de Central](https://central.sonatype.org/publish/requirements/gpg/).
Para exportar la clave privada a un archivo local que puedas copiar al secreto:

```bash
gpg --armor --export-secret-keys 558442263B6AC29B9FF4AEB5F320065B4D152C59 > private-key.asc
```

No subas ese archivo al repositorio y eliminalo despues de configurar el secreto.

### Publicar una version

1. Actualiza la version del padre y de los cinco modulos, incluidas sus referencias al padre,
   en los seis `pom.xml`, y actualiza el changelog. Cada publicacion necesita una version nueva:
   Maven Central no permite sobrescribir versiones existentes.
2. Integra los cambios y crea un tag sobre ese commit con la version exacta o con prefijo `v`
   (por ejemplo, `v3.0.5` para POMs con version `3.0.5`). No se admiten versiones `SNAPSHOT`.
3. En GitHub, crea la release sobre ese tag y pulsa **Publish release**, sin marcarla como prerelease.
4. Comprueba el workflow `release` en Actions. Solo termina correctamente cuando Central confirma
   el estado `published`; la indexacion en el buscador puede tardar mas.

Publica la release desde la interfaz de GitHub o con un token que pueda generar eventos de Actions.
Una release creada por otro workflow con `GITHUB_TOKEN` no dispara este workflow, debido a la
[restriccion de eventos de GitHub Actions](https://docs.github.com/en/actions/how-tos/writing-workflows/choosing-when-your-workflow-runs/triggering-a-workflow#triggering-a-workflow-from-a-workflow).
Si falla antes de publicar, corrige la causa y reejecuta los jobs fallidos desde Actions.
Si Central ya recibio o publico la version, revisa primero el deployment en Central Portal antes
de reintentar; no crees otra release con las mismas coordenadas.

## Changelog

Consulta el historial de cambios en [CHANGELOG.md](CHANGELOG.md).
