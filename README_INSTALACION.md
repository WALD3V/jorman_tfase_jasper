# Guía de Instalación
### Sistema de Generación de Reportes de Rol de Pagos

---

## Requisitos Previos

Antes de instalar, asegúrese de tener lo siguiente:

| Requisito | Versión mínima | Verificar con |
|---|---|---|
| Java JDK | 8 | `java -version` |
| Apache Maven | 3.6 | `mvn -version` |
| Acceso a red | — | Conexión al servidor Sybase |

---

## Paso 1 — Clonar o descomprimir el proyecto

```bash
# Opción A: clonar desde repositorio
git clone <url-del-repositorio>
cd jorman_tfase_jasper

# Opción B: descomprimir el archivo entregado
# Extraer el ZIP y acceder a la carpeta del proyecto
```

---

## Paso 2 — Verificar los JARs locales

El proyecto requiere dos librerías propietarias ubicadas en la carpeta `librerias/`. Verifique que existan:

```
librerias/
├── KabitaLib.jar   ✔ debe estar presente
└── jconn4d.jar     ✔ debe estar presente
```

> Si alguno falta, solicítelos al administrador del sistema antes de continuar.

---

## Paso 3 — Configurar el archivo `.env`

En la **raíz del proyecto** cree el archivo `.env` con el siguiente contenido, ajustando los valores según el entorno:

```dotenv
# Base de Datos Sybase
DB_HOST=192.168.100.143
DB_PORT=2638
DB_NAME=mayekawa8
DB_USERNAME=dba
DB_PASSWORD=sql

# Puerto del servidor
SERVER_PORT=8081

# Parámetros por defecto
DEFAULT_CIA_CODE=01
DEFAULT_TIPO_ROL=001
DEFAULT_COD_INFORME=01
DEFAULT_COD_CEN=000
DEFAULT_EMP_INICIAL=000
DEFAULT_EMP_FINAL=999999
```

> Si el archivo `.env` ya existe en el proyecto, solo edite los valores de conexión (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`).

---

## Paso 4 — Instalar las librerías locales en Maven

Los JARs propietarios deben registrarse en el repositorio local de Maven antes de compilar:

```bash
mvn install:install-file -Dfile=librerias/KabitaLib.jar -DgroupId=com.kabita -DartifactId=KabitaLib -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=librerias/jconn4d.jar -DgroupId=com.sybase -DartifactId=jconn4d -Dversion=1.0 -Dpackaging=jar
```

---

## Paso 5 — Compilar el proyecto

```bash
mvn clean package -DskipTests
```

Si la compilación es exitosa, verá al final:

```
[INFO] BUILD SUCCESS
```

Y se generará el archivo ejecutable en:

```
target/rol-pagos-1.0.0.jar
```

---

## Paso 6 — Ejecutar el servicio

```bash
java -jar target/rol-pagos-1.0.0.jar
```

El servidor arranca en el puerto configurado (`SERVER_PORT`). Cuando esté listo verá en consola:

```
Started Application in X.XXX seconds
```

---

## Paso 7 — Verificar que el servicio está activo

Abra un navegador o herramienta como Postman y acceda a:

```
GET http://localhost:8081/api/reportes/health
```

Respuesta esperada:
```
Servicio de reportes activo
```

---

## Solución de Problemas Comunes

| Problema | Causa probable | Solución |
|---|---|---|
| `BUILD FAILURE` al compilar | JARs locales no instalados en Maven | Ejecutar el Paso 4 nuevamente |
| `No se encontró el archivo SQL` | Archivos `.sql` fuera del classpath | Verificar que `src/main/resources/sql/` contiene todos los `.sql` |
| `Error de conexión a Sybase` | Credenciales o red incorrecta | Revisar `DB_HOST`, `DB_PORT`, `DB_USERNAME` y `DB_PASSWORD` en `.env` |
| Puerto ya en uso | Otro proceso ocupa el puerto | Cambiar `SERVER_PORT` en `.env` o liberar el puerto |
| `java.lang.UnsupportedClassVersionError` | Java instalado es inferior a 8 | Instalar JDK 8 o superior |
