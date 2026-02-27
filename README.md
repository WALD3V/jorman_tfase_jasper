# Sistema de Generación de Reportes de Rol de Pagos
### `rol-pagos-1.0.0` — Spring Boot + JasperReports + Sybase

---

## Índice

1. [Descripción General](#1-descripción-general)
2. [Tecnologías y Dependencias](#2-tecnologías-y-dependencias)
3. [Requisitos Previos](#3-requisitos-previos)
4. [Estructura del Proyecto](#4-estructura-del-proyecto)
5. [Configuración](#5-configuración)
6. [Arquitectura de la Aplicación](#6-arquitectura-de-la-aplicación)
7. [Capa de Datos: Consultas SQL](#7-capa-de-datos-consultas-sql)
8. [Modelos de Datos](#8-modelos-de-datos)
9. [API REST](#9-api-rest)
10. [Generación de Reportes PDF](#10-generación-de-reportes-pdf)
11. [Modos de Ejecución](#11-modos-de-ejecución)
12. [Compilación y Empaquetado](#12-compilación-y-empaquetado)

---

## 1. Descripción General

Este sistema genera reportes de **rol de pagos** en formato PDF por empleado, consultando los datos directamente desde una base de datos **Sybase ASA**. Expone sus funcionalidades a través de una **API REST** construida con Spring Boot, lo que permite integrarlo con cualquier frontend o sistema externo mediante peticiones HTTP.

Cada reporte incluye los ingresos, egresos y descuentos del empleado para un período determinado, con los totales calculados directamente desde las consultas SQL. Los reportes se generan con el motor **JasperReports** a partir de una plantilla `.jasper` precompilada.

El sistema también conserva un modo de ejecución por **línea de comandos** (`Main1.java`) que genera los PDFs directamente en disco, útil para pruebas locales.

---

## 2. Tecnologías y Dependencias

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 8 | Lenguaje de programación |
| Spring Boot | 2.7.18 | Framework principal (servidor HTTP embebido) |
| Spring Web (MVC) | — | Exposición de endpoints REST |
| JasperReports | 6.20.6 | Motor de generación de reportes PDF |
| Sybase jconn4d | — | Driver JDBC propietario para Sybase ASA |
| MySQL Connector | 8.0.33 | Driver JDBC alternativo |
| dotenv-java | 2.2.4 | Lectura de variables de entorno desde `.env` |
| Apache POI | 5.2.4 | Soporte para formatos Office (complementario) |
| commons-beanutils | 1.9.4 | Requerido por JasperReports internamente |
| KabitaLib | 1.0 | Librería local personalizada |
| Maven | — | Gestión de dependencias y construcción |

---

## 3. Requisitos Previos

- **Java 8** o superior instalado
- **Maven 3.6+** instalado
- Acceso de red a la base de datos **Sybase ASA** (host, puerto, credenciales)
- Archivo `.env` configurado correctamente en la raíz del proyecto
- Los JARs locales presentes en `librerias/`:
  - `KabitaLib.jar`
  - `jconn4d.jar`

---

## 4. Estructura del Proyecto

```
jorman_tfase_jasper/
│
├── .env                              ← Variables de entorno (BD, puerto, parámetros)
├── pom.xml                           ← Configuración de Maven y dependencias
│
├── librerias/
│   ├── KabitaLib.jar                 ← Librería local (scope: system)
│   └── jconn4d.jar                   ← Driver JDBC Sybase (scope: system)
│
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── Application.java              ← Punto de entrada Spring Boot
    │   │   ├── Main1.java                    ← Modo ejecución CLI (sin Spring)
    │   │   │
    │   │   ├── controller/
    │   │   │   └── ReportController.java     ← Endpoints HTTP REST
    │   │   │
    │   │   ├── service/
    │   │   │   ├── ReportService.java        ← Lógica de negocio (orquestador)
    │   │   │   ├── ExtractDetails1_Fixed.java← Extracción de datos desde Sybase
    │   │   │   ├── JasperReportExample.java  ← Generación de PDFs con JasperReports
    │   │   │   └── QueryLoader.java          ← Carga de archivos SQL desde resources
    │   │   │
    │   │   ├── model/
    │   │   │   ├── RolGeneral.java           ← Entidad principal del reporte
    │   │   │   └── DetalleRol.java           ← Ítem de ingreso, egreso o descuento
    │   │   │
    │   │   └── config/
    │   │       └── DatabaseConnection1.java  ← Conexión JDBC a Sybase
    │   │
    │   └── resources/
    │       ├── jasperRolReport.jasper        ← Plantilla PDF compilada
    │       ├── jasperRolReport.jrxml         ← Plantilla fuente (editable)
    │       └── sql/
    │           ├── QUERY_ROLES_PRINCIPALES.sql
    │           ├── QUERY_INGRESOS.sql
    │           ├── QUERY_EGRESOS.sql
    │           ├── QUERY_DESCUENTO_TOTAL.sql
    │           ├── QUERY_HAB_TOTSAL_DES.sql
    │           ├── QUERY_NETO.sql
    │           ├── QUERY_RUBROS.sql
    │           └── QUERY_DET_HAB_DES.sql
    │
    └── resources/                            ← Resources NetBeans (legacy)
        ├── jasperRolReport.jasper
        └── sql/
```

---

## 5. Configuración

Toda la configuración sensible y los parámetros por defecto se definen en el archivo `.env` ubicado en la **raíz del proyecto**. La librería `dotenv-java` se encarga de leerlos al iniciar la aplicación.

```dotenv
# ─── Base de Datos Sybase ───────────────────────────────
DB_HOST=192.168.100.143
DB_PORT=2638
DB_NAME=mayekawa8
DB_USERNAME=dba
DB_PASSWORD=sql

# ─── Servidor Spring Boot ───────────────────────────────
SERVER_PORT=8081

# ─── Parámetros por defecto del sistema ─────────────────
DEFAULT_CIA_CODE=01
DEFAULT_TIPO_ROL=001
DEFAULT_COD_INFORME=01
DEFAULT_COD_CEN=000
DEFAULT_EMP_INICIAL=000
DEFAULT_EMP_FINAL=999999
```

> **Nota:** El archivo `.env` no debe incluirse en el repositorio. Asegúrese de que esté listado en `.gitignore`.

---

## 6. Arquitectura de la Aplicación

La aplicación implementa una arquitectura en capas clásica de Spring MVC:

```
┌─────────────────────────────────────────────────────────┐
│  CLIENTE  (navegador / Postman / frontend Angular)      │
│       GET http://localhost:8081/api/reportes/rol-pagos  │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP Request
                         ▼
┌─────────────────────────────────────────────────────────┐
│              SPRING BOOT  (puerto 8081)                 │
│                                                         │
│   ┌─────────────────────────────────────────────────┐  │
│   │  ReportController  (@RestController)            │  │
│   │  Mapea rutas HTTP → métodos Java                │  │
│   │  Extrae @RequestParam de la URL                 │  │
│   └───────────────────────┬─────────────────────────┘  │
│                           │ @Autowired                  │
│   ┌───────────────────────▼─────────────────────────┐  │
│   │  ReportService  (@Service)                      │  │
│   │  Orquesta: BD → datos → PDF → JSON              │  │
│   └──────────┬────────────────────────┬─────────────┘  │
│              │                        │                 │
│  ┌───────────▼──────────┐  ┌──────────▼─────────────┐  │
│  │ DatabaseConnection1  │  │  JasperReportExample   │  │
│  │ dotenv → Sybase JDBC │  │  JasperFillManager     │  │
│  │ jconn4d driver       │  │  → ByteArrayOutputStream│  │
│  └───────────┬──────────┘  └────────────────────────┘  │
│              │             ▲                            │
│  ┌───────────▼──────────┐  │ RolGeneral (modelo)        │
│  │ ExtractDetails1_Fixed│  │                            │
│  │ QueryLoader → SQL    │──┘                            │
│  └──────────────────────┘                               │
└──────────────────────────────────────────────────────── ┘
                         │ JDBC (jconn4d)
                         ▼
        ┌────────────────────────────┐
        │  SYBASE ASA               │
        │  192.168.100.143 : 2638   │
        │  Base de datos: mayekawa8 │
        └────────────────────────────┘
```

### Descripción de cada componente

| Clase | Paquete | Responsabilidad |
|---|---|---|
| `Application` | `Jasper` | Arranca el contexto de Spring Boot y el servidor Tomcat embebido |
| `ReportController` | `Jasper.controller` | Recibe peticiones HTTP y delega al servicio |
| `ReportService` | `Jasper.service` | Coordina la consulta de datos, generación de PDFs y construcción de la respuesta JSON |
| `ExtractDetails1_Fixed` | `Jasper.service` | Ejecuta las consultas SQL y construye los objetos `RolGeneral` por empleado |
| `QueryLoader` | `Jasper.service` | Carga los archivos `.sql` desde `resources/sql/` al classpath en memoria |
| `JasperReportExample` | `Jasper.service` | Usa JasperReports para generar el PDF a partir del modelo `RolGeneral` |
| `DatabaseConnection1` | `Jasper.config` | Gestiona la conexión JDBC a Sybase leyendo credenciales del `.env` |
| `RolGeneral` | `Jasper.model` | Modelo de datos principal: contiene los datos del empleado y sus listas de detalles |
| `DetalleRol` | `Jasper.model` | Modelo de un ítem individual (ingreso, egreso o descuento) |
| `Main1` | `Jasper` | Alternativa CLI: genera PDFs en disco sin levantar el servidor HTTP |

---

## 7. Capa de Datos: Consultas SQL

Todas las consultas SQL están separadas en archivos `.sql` individuales dentro de `src/main/resources/sql/`. La clase `QueryLoader` las lee del classpath al arrancar y las mantiene en memoria como un mapa `nombre → consulta`.

### Consultas disponibles

| Archivo SQL | Propósito |
|---|---|
| `QUERY_ROLES_PRINCIPALES.sql` | Consulta principal. Obtiene los empleados dentro del rango indicado junto con sus datos básicos y los ítems del rol (ingresos/egresos/descuentos) clasificados por tipo de impresión (`I`, `E`, `D`) |
| `QUERY_INGRESOS.sql` | Obtiene el detalle de haberes/ingresos (`tipo = H`) de cada empleado |
| `QUERY_EGRESOS.sql` | Obtiene el detalle de egresos (`tipo = D`) de cada empleado |
| `QUERY_DESCUENTO_TOTAL.sql` | Calcula el total de descuentos del empleado (campo `DESCUENTO`) |
| `QUERY_HAB_TOTSAL_DES.sql` | Calcula el total de haberes/ingresos del empleado (campo `DESCUENTO`) |
| `QUERY_NETO.sql` | Calcula el neto a pagar al empleado (campo `TOTAL`), considerando haberes y descuentos principales y anexos |
| `QUERY_RUBROS.sql` | Consulta auxiliar de rubros (uso interno) |
| `QUERY_DET_HAB_DES.sql` | Consulta auxiliar de detalle de haberes y descuentos |

### Flujo de consultas por empleado

```
1. QUERY_ROLES_PRINCIPALES  →  obtiene la lista de empleados y clasifica sus ítems
         ↓ Por cada empleado encontrado:
2. QUERY_INGRESOS           →  detalla los ingresos individuales
3. QUERY_EGRESOS            →  detalla los egresos individuales
4. QUERY_DESCUENTO_TOTAL    →  calcula y guarda el total de egresos
5. QUERY_HAB_TOTSAL_DES     →  calcula y guarda el total de ingresos
6. QUERY_NETO               →  calcula y guarda el neto a pagar
```

> Los totales **no se calculan en Java**; provienen directamente de las consultas SQL para garantizar precisión contable.

---

## 8. Modelos de Datos

### `RolGeneral`

Representa el rol de pagos completo de un empleado para un período.

| Campo | Tipo | Descripción |
|---|---|---|
| `periodoAnio` | `String` | Año del período |
| `periodoMes` | `String` | Nombre del mes del período |
| `fechaCorte` | `String` | Fecha de corte del rol (formato `dd/MM/yyyy`) |
| `fechaInicio` | `String` | Fecha de inicio del período |
| `fechaFin` | `String` | Fecha de fin del período |
| `empCodigo` | `String` | Código único del empleado |
| `empNombres` | `String` | Nombres del empleado |
| `empApellidos` | `String` | Apellidos del empleado |
| `empFuncion` | `String` | Cargo o función desempeñada |
| `descripcionTipoRol` | `String` | Descripción del tipo de rol (ej: "Rol General") |
| `ciaDescripcion` | `String` | Nombre de la empresa |
| `totalIngresos` | `double` | Total de ingresos (desde BD) |
| `totalEgresos` | `double` | Total de egresos/descuentos (desde BD) |
| `netoAPagar` | `double` | Neto a pagar al empleado (desde BD) |
| `ingresos` | `List<DetalleRol>` | Lista de ítems de ingresos |
| `egresos` | `List<DetalleRol>` | Lista de ítems de egresos |
| `descuentos` | `List<DetalleRol>` | Lista de ítems de descuentos |

### `DetalleRol`

Representa una línea de detalle individual del rol.

| Campo | Tipo | Descripción |
|---|---|---|
| `detalleCodigo` | `String` | Código del concepto |
| `nombre` | `String` | Descripción del concepto (ej: "Sueldo Básico") |
| `tipo` | `String` | Tipo: `I` = Ingreso, `E` = Egreso, `D` = Descuento |
| `referencia` | `String` | Referencia adicional |
| `valor` | `double` | Valor monetario del concepto |
| `saldo` | `double` | Saldo si aplica |

---

## 9. API REST

El servidor escucha en el puerto definido por `SERVER_PORT` en el `.env` (por defecto **8081**).

### Endpoints

#### `GET /api/reportes/health`
Verifica que el servicio está en ejecución.

**Respuesta exitosa:**
```
200 OK
"Servicio de reportes activo"
```

---

#### `GET /api/reportes/rol-pagos`
Genera reportes PDF para todos los empleados dentro del rango indicado.

**Parámetros de consulta (Query Params):**

| Parámetro | Tipo | Valor por defecto | Descripción |
|---|---|---|---|
| `ciaCode` | `String` | `01` | Código de la empresa |
| `tipoRol` | `String` | `001` | Tipo de rol de pagos |
| `codInforme` | `String` | `001` | Código del informe |
| `codCen` | `String` | `000` | Código del centro de costos |
| `empInicial` | `String` | `000` | Código del primer empleado del rango |
| `empFinal` | `String` | `999999` | Código del último empleado del rango |

**Ejemplo de petición:**
```
GET http://localhost:8081/api/reportes/rol-pagos?ciaCode=01&tipoRol=001&empInicial=100&empFinal=150
```

**Respuesta exitosa (`200 OK`):**
```json
{
  "total": 2,
  "reportes": {
    "0": {
      "empCodigo": "001",
      "nombres": "Juan",
      "apellidos": "Pérez",
      "pdf": "JVBERi0xLjQKJeLjz9MKM...",
      "tamaño": 48230
    },
    "1": {
      "empCodigo": "002",
      "nombres": "María",
      "apellidos": "García",
      "pdf": "JVBERi0xLjQKJeLjz9MKM...",
      "tamaño": 51024
    }
  }
}
```

> El campo `pdf` contiene el contenido del archivo PDF codificado en **Base64**. El cliente debe decodificarlo para obtener el binario del PDF.

**Respuesta de error (`500 Internal Server Error`):**
```
"Error generando reporte: <mensaje del error>"
```

---

## 10. Generación de Reportes PDF

La generación de PDF es realizada por `JasperReportExample` usando el motor JasperReports.

### Plantilla
- **Fuente editable:** `src/main/resources/jasperRolReport.jrxml`
- **Plantilla compilada:** `src/main/resources/jasperRolReport.jasper`

> Solo se usa el `.jasper` en tiempo de ejecución. El `.jrxml` es editable con JasperStudio.

### Fuentes de datos que recibe la plantilla

| Parámetro Jasper | Tipo | Contenido |
|---|---|---|
| `dataSource` principal | `JRBeanCollectionDataSource` | Lista con el único `RolGeneral` del empleado |
| `ingresosDataSource` | `JRBeanCollectionDataSource` | Lista de `DetalleRol` de ingresos |
| `egresosDataSource` | `JRBeanCollectionDataSource` | Lista de `DetalleRol` de egresos |
| `descuentosDataSource` | `JRBeanCollectionDataSource` | Lista de `DetalleRol` de descuentos |
| `REPORT_LOCALE` | `Locale` | `Locale.US` para formato numérico |

### Modo API vs Modo CLI

| Aspecto | Modo CLI (`Main1`) | Modo API (`ReportService`) |
|---|---|---|
| Método utilizado | `generarReporte(rol, fileName)` | `generarReporteEnMemoria(rol)` |
| Salida del PDF | Archivo `.pdf` en disco | `ByteArrayOutputStream` en memoria |
| Codificación | No aplica | Base64 para incluir en JSON |
| Iniciado por | Ejecución directa de `main()` | Petición HTTP `GET` |

---

## 11. Modos de Ejecución

### Modo API (Spring Boot) — **Recomendado**

Levanta el servidor HTTP y expone los endpoints REST.

```bash
# Desarrollo
mvn spring-boot:run

# Producción (con JAR ya generado)
java -jar target/rol-pagos-1.0.0.jar
```

El servidor queda disponible en: `http://localhost:8081`

---

### Modo CLI (sin Spring)

Ejecuta la generación directamente desde `Main1.java`. Lee todos los parámetros desde el `.env` y guarda los PDFs en el directorio de ejecución.

```bash
# Compilar
mvn compile

# Ejecutar clase principal alternativa
mvn exec:java -Dexec.mainClass="Jasper.Main1"
```

O con el JAR ya compilado:
```bash
java -cp target/rol-pagos-1.0.0.jar Jasper.Main1
```

---

## 12. Compilación y Empaquetado

### Compilar sin ejecutar pruebas
```bash
mvn clean package -DskipTests
```

Genera el archivo ejecutable en:
```
target/rol-pagos-1.0.0.jar
```

> El plugin `spring-boot-maven-plugin` con `includeSystemScope=true` empaqueta también los JARs locales (`KabitaLib.jar` y `jconn4d.jar`) dentro del fat JAR, por lo que no se necesitan dependencias externas al ejecutarlo.

### Ejecutar el JAR generado
```bash
java -jar target/rol-pagos-1.0.0.jar
```

---

*Sistema de Generación de Reportes de Rol de Pagos — `com.jasper:rol-pagos:1.0.0`*
