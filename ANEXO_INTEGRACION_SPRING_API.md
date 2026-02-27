# ANEXO: Integración de Spring Boot como API REST
## Sistema de Generación de Reportes de Rol de Pagos

---

## 1. Introducción

Este anexo documenta la integración del framework **Spring Boot 2.7.18** como capa de exposición HTTP (API REST) en el sistema de generación de reportes de rol de pagos con JasperReports. La evolución del proyecto pasó de una ejecución puramente por línea de comandos (`Main1.java`) a una arquitectura de servicio web que permite solicitar y recibir reportes PDF mediante peticiones HTTP.

---

## 2. Tecnologías Involucradas

| Tecnología | Versión | Rol dentro del sistema |
|---|---|---|
| Spring Boot | 2.7.18 | Framework principal del servidor HTTP |
| Spring Web (MVC) | — | Exposición de endpoints REST (`@RestController`) |
| JasperReports | 6.20.6 | Motor de generación de reportes PDF |
| MySQL Connector / Sybase (jconn4d) | 8.0.33 / — | Acceso a base de datos |
| dotenv-java | 2.2.4 | Lectura de variables de entorno desde `.env` |
| Apache POI | 5.2.4 | Soporte para formatos Office (complementario) |
| Maven | — | Gestión de dependencias y construcción del JAR |

---

## 3. Estructura de Paquetes del Proyecto

La aplicación sigue una estructura de capas estándar de Spring MVC:

```
src/main/java/
│
├── Application.java              ← Punto de entrada Spring Boot
├── Main1.java                    ← Ejecución directa (modo CLI, sin Spring)
│
├── controller/
│   └── ReportController.java     ← Capa HTTP: expone los endpoints REST
│
├── service/
│   ├── ReportService.java        ← Lógica de negocio (orquesta la generación)
│   ├── JasperReportExample.java  ← Generación de reportes con JasperReports
│   ├── ExtractDetails1_Fixed.java← Extracción de datos desde la base de datos
│   └── QueryLoader.java          ← Carga de consultas SQL desde archivos externos
│
├── model/
│   ├── RolGeneral.java           ← Entidad principal: datos del empleado y su rol
│   └── DetalleRol.java           ← Entidad de detalle: ítems de ingresos/egresos
│
└── config/
    └── DatabaseConnection1.java  ← Configuración y conexión a la base de datos Sybase
```

```
src/main/resources/
│
├── jasperRolReport.jasper        ← Plantilla compilada de JasperReports
├── jasperRolReport.jrxml         ← Plantilla fuente de JasperReports
└── sql/                          ← Archivos de consultas SQL externas
```

---

## 4. Punto de Entrada: `Application.java`

La clase `Application` es el punto de arranque de Spring Boot. Se anota con `@SpringBootApplication` para activar la autoconfiguración del framework. Se excluye `DataSourceAutoConfiguration` porque la conexión a la base de datos Sybase se gestiona manualmente (no usa el datasource estándar de Spring que esperaría un driver JDBC estándar de Spring Data).

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**¿Por qué se excluye `DataSourceAutoConfiguration`?**
Spring Boot intentaría configurar automáticamente una fuente de datos JDBC estándar al arrancar. Como la aplicación usa **Sybase ASA** (un motor de base de datos propietario con su propio driver `jconn4d`) y gestiona la conexión manualmente a través de `DatabaseConnection1`, es necesario deshabilitar esta autoconfiguración para evitar errores de arranque cuando Spring no encuentra un datasource estándar configurado.

---

## 5. Capa de Control: `ReportController.java`

El controlador actúa como la interfaz HTTP del sistema. Recibe las peticiones, extrae los parámetros y delega la lógica al servicio.

```java
@RestController
@RequestMapping("/api/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/rol-pagos")
    public ResponseEntity<String> generarRolPagos(
            @RequestParam(defaultValue = "001") String codInforme,
            @RequestParam(defaultValue = "01")  String ciaCode,
            @RequestParam(defaultValue = "001") String tipoRol,
            @RequestParam(defaultValue = "000") String empInicial,
            @RequestParam(defaultValue = "999999") String empFinal,
            @RequestParam(defaultValue = "000") String codCen) {

        try {
            String resultado = reportService.generarReporte(
                codInforme, ciaCode, tipoRol, empInicial, empFinal, codCen);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error generando reporte: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de reportes activo");
    }
}
```

### Endpoints Expuestos

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/reportes/rol-pagos` | Genera reportes PDF para un rango de empleados. Devuelve JSON con los PDF en Base64. |
| `GET` | `/api/reportes/health` | Verifica que el servicio está en ejecución. |

### Parámetros del Endpoint `/api/reportes/rol-pagos`

| Parámetro | Tipo | Valor por defecto | Descripción |
|---|---|---|---|
| `codInforme` | `String` | `001` | Código del tipo de informe/reporte |
| `ciaCode` | `String` | `01` | Código de la empresa (compañía) |
| `tipoRol` | `String` | `001` | Tipo de rol de pagos |
| `empInicial` | `String` | `000` | Código del primer empleado del rango |
| `empFinal` | `String` | `999999` | Código del último empleado del rango |
| `codCen` | `String` | `000` | Código del centro de costos |

### Ejemplo de Petición

```
GET http://localhost:8081/api/reportes/rol-pagos?ciaCode=01&tipoRol=001&empInicial=100&empFinal=200
```

### Ejemplo de Respuesta (JSON)

```json
{
  "total": 2,
  "reportes": {
    "0": {
      "empCodigo": "001",
      "nombres": "Juan",
      "apellidos": "Pérez",
      "pdf": "JVBERi0xLjQKJ...",
      "tamaño": 48230
    },
    "1": {
      "empCodigo": "002",
      "nombres": "María",
      "apellidos": "García",
      "pdf": "JVBERi0xLjQKJ...",
      "tamaño": 51024
    }
  }
}
```

> **Nota:** El campo `pdf` contiene el contenido del archivo PDF codificado en **Base64**. El cliente puede decodificarlo para obtener el archivo PDF binario.

---

## 6. Capa de Servicio: `ReportService.java`

El servicio es el núcleo de la lógica de negocio. Se anota con `@Service` para que Spring lo administre como un bean e inyecte sus dependencias automáticamente.

```java
@Service
public class ReportService {

    public String generarReporte(String codInforme, String ciaCode, String tipoRol,
                               String empInicial, String empFinal, String codCen) {
        // 1. Obtener conexión a la base de datos
        Connection connection = DatabaseConnection1.getConnection();

        // 2. Consultar datos de empleados en el rango indicado
        Map<String, RolGeneral> rolesMap = ExtractDetails1_Fixed.getAllRolesByParametros(
            connection, empInicial, empFinal, ciaCode, tipoRol, codInforme, codCen
        );

        // 3. Por cada empleado, generar su PDF en memoria
        for (Map.Entry<String, RolGeneral> entry : rolesMap.entrySet()) {
            ByteArrayOutputStream pdfStream =
                JasperReportExample.generarReporteEnMemoria(entry.getValue());

            // 4. Codificar el PDF en Base64 para incluirlo en la respuesta JSON
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfStream.toByteArray());
            // ... construir mapa de resultado ...
        }

        // 5. Serializar el resultado a JSON y devolverlo
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(resultado);
    }
}
```

**Flujo de procesamiento dentro del servicio:**

```
Petición HTTP
     │
     ▼
[ReportController.generarRolPagos()]
     │  Delega con parámetros
     ▼
[ReportService.generarReporte()]
     │
     ├─► [DatabaseConnection1.getConnection()]
     │         Lee credenciales del .env → conecta a Sybase
     │
     ├─► [ExtractDetails1_Fixed.getAllRolesByParametros()]
     │         Ejecuta queries SQL → retorna Map<empCodigo, RolGeneral>
     │
     └─► Por cada empleado:
           [JasperReportExample.generarReporteEnMemoria()]
                 Carga plantilla .jasper
                 Llena con datos del RolGeneral
                 Exporta a ByteArrayOutputStream
                 → Retorna PDF en memoria (sin escribir archivo)
           [Base64.encode(pdfStream)]
                 → PDF convertido a String Base64
     │
     ▼
  JSON serializado con ObjectMapper
     │
     ▼
ResponseEntity<String> → Cliente HTTP
```

---

## 7. Generación de PDF en Memoria: `JasperReportExample.java`

Para funcionar como API (en lugar de guardar archivos en disco como hacía `Main1.java`), se creó el método `generarReporteEnMemoria()`:

```java
// Modo API: genera el PDF en memoria (ByteArrayOutputStream)
public static ByteArrayOutputStream generarReporteEnMemoria(RolGeneral rolGeneral)
        throws JRException {

    String jasperPath = "src/resources/jasperRolReport.jasper";

    // Preparar fuentes de datos para JasperReports
    JRBeanCollectionDataSource dataSource =
        new JRBeanCollectionDataSource(Collections.singletonList(rolGeneral));
    JRBeanCollectionDataSource ingresosDS =
        new JRBeanCollectionDataSource(rolGeneral.getIngresos());
    JRBeanCollectionDataSource egresosDS =
        new JRBeanCollectionDataSource(rolGeneral.getEgresos());
    JRBeanCollectionDataSource descuentosDS =
        new JRBeanCollectionDataSource(rolGeneral.getDescuentos());

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("REPORT_LOCALE", Locale.US);
    parameters.put("ingresosDataSource", ingresosDS);
    parameters.put("egresosDataSource", egresosDS);
    parameters.put("descuentosDataSource", descuentosDS);

    // Llenar y exportar el reporte directamente al stream
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, dataSource);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

    return outputStream;
}
```

**Diferencia entre los dos modos:**

| Aspecto | Modo CLI (`Main1`) | Modo API (`ReportService`) |
|---|---|---|
| Salida del PDF | Archivo `.pdf` en disco (`RolGeneral_001.pdf`) | `ByteArrayOutputStream` en memoria |
| Método usado | `generarReporte(rol, fileName)` | `generarReporteEnMemoria(rol)` |
| Formato de respuesta | Ninguno (salida estándar) | JSON con PDF en Base64 |
| Iniciado por | Ejecución directa de `main()` | Petición HTTP `GET` |

---

## 8. Configuración de Base de Datos: `DatabaseConnection1.java`

La configuración de la base de datos se gestiona mediante el archivo `.env`, leído con la librería `dotenv-java`. Esto permite que las credenciales y parámetros de conexión no estén hardcodeados en el código fuente.

```java
public class DatabaseConnection1 {

    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getConnection() throws SQLException {
        String host     = dotenv.get("DB_HOST");
        String port     = dotenv.get("DB_PORT");
        String database = dotenv.get("DB_NAME");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        return conectar(host, port, database, username, password);
    }

    private static Connection conectar(String ip, String puerto,
                                       String database, String usuario, String password) {
        // Registra el driver Sybase jconn4d manualmente
        DriverManager.registerDriver(
            (Driver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance()
        );
        String url = "jdbc:sybase:Tds:" + ip + ":" + puerto + "/" + database;
        Properties props = new Properties();
        props.put("User", usuario);
        props.put("Password", password);
        return DriverManager.getConnection(url, props);
    }
}
```

### Archivo `.env` (variables de entorno)

```dotenv
# Configuración de Base de Datos Sybase
DB_HOST=192.168.100.143
DB_PORT=2638
DB_NAME=mayekawa8
DB_USERNAME=dba
DB_PASSWORD=sql

# Puerto del servidor Spring Boot
SERVER_PORT=8081

# Parámetros por defecto del sistema
DEFAULT_CIA_CODE=01
DEFAULT_TIPO_ROL=001
DEFAULT_COD_INFORME=01
DEFAULT_COD_CEN=000
DEFAULT_EMP_INICIAL=000
DEFAULT_EMP_FINAL=999999
```

---

## 9. Configuración del `pom.xml`

Las dependencias clave de Spring en el archivo `pom.xml`:

```xml
<!-- Hereda la configuración de Spring Boot -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<!-- Habilita el servidor HTTP embebido (Tomcat) y Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JPA (se excluye en Application.java para no conflictuar con Sybase) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Plugin para empaquetar como JAR ejecutable -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <!-- Incluye los JARs del sistema (KabitaLib, jconn4d) en el fat JAR -->
        <includeSystemScope>true</includeSystemScope>
    </configuration>
</plugin>
```

**¿Por qué `spring-boot-starter-web`?**
Esta dependencia incluye automáticamente:
- **Tomcat embebido**: servidor HTTP que escucha en el puerto configurado (por defecto `8080`, en este proyecto `8081`).
- **Spring MVC**: el framework que procesa las anotaciones `@RestController`, `@GetMapping`, `@RequestParam`, etc.
- **Jackson**: librería para serializar/deserializar objetos Java a JSON automáticamente.
- **Spring Core**: inyección de dependencias (`@Autowired`, `@Service`, `@Bean`).

---

## 10. Modelos de Datos

### `RolGeneral.java`
Encapsula toda la información de un empleado para un período de nómina:

| Campo | Tipo | Descripción |
|---|---|---|
| `periodoAnio` | `String` | Año del período de nómina |
| `periodoMes` | `String` | Mes del período |
| `fechaCorte` | `String` | Fecha de corte del rol |
| `empCodigo` | `String` | Código único del empleado |
| `empNombres` / `empApellidos` | `String` | Datos personales del empleado |
| `empFuncion` | `String` | Cargo o función del empleado |
| `descripcionTipoRol` | `String` | Descripción del tipo de rol |
| `ciaDescripcion` | `String` | Nombre de la empresa |
| `totalIngresos` | `double` | Suma total de ingresos |
| `totalEgresos` | `double` | Suma total de egresos |
| `netoAPagar` | `double` | Valor neto a pagar al empleado |
| `ingresos` | `List<DetalleRol>` | Ítems de ingresos del rol |
| `egresos` | `List<DetalleRol>` | Ítems de egresos del rol |
| `descuentos` | `List<DetalleRol>` | Ítems de descuentos del rol |

### `DetalleRol.java`
Representa una línea de detalle individual (ingreso, egreso o descuento):

| Campo | Tipo | Descripción |
|---|---|---|
| `detalleCodigo` | `String` | Código del concepto |
| `nombre` | `String` | Descripción del concepto |
| `tipo` | `String` | Clasificación (ingreso/egreso/descuento) |
| `referencia` | `String` | Referencia adicional |
| `valor` | `double` | Valor del concepto |
| `saldo` | `double` | Saldo si aplica |

---

## 11. Diagrama de Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                   CLIENTE (Frontend / Postman)           │
│            GET /api/reportes/rol-pagos?params...         │
└──────────────────────────┬───────────────────────────────┘
                           │ HTTP Request
                           ▼
┌──────────────────────────────────────────────────────────┐
│              SPRING BOOT (Puerto 8081)                   │
│                                                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │           ReportController (@RestController)    │    │
│  │  - Mapea la ruta HTTP al método Java            │    │
│  │  - Extrae parámetros de la URL                  │    │
│  │  - Retorna ResponseEntity<String> (JSON)        │    │
│  └──────────────────────┬──────────────────────────┘    │
│                         │ @Autowired                     │
│  ┌──────────────────────▼──────────────────────────┐    │
│  │             ReportService (@Service)            │    │
│  │  - Orquesta la lógica de negocio               │    │
│  │  - Llama a la BD y al motor de reportes        │    │
│  └───────┬──────────────────────────┬─────────────┘    │
│           │                          │                   │
│  ┌────────▼──────────┐   ┌──────────▼───────────────┐  │
│  │ DatabaseConnection│   │  JasperReportExample      │  │
│  │  (dotenv → Sybase)│   │  (JasperFillManager      │  │
│  │  jconn4d driver   │   │   → ByteArrayOutputStream)│  │
│  └────────┬──────────┘   └───────────────────────────┘  │
│           │                                              │
└───────────┼──────────────────────────────────────────────┘
            │ JDBC (jconn4d)
            ▼
┌──────────────────────────┐
│   BASE DE DATOS SYBASE   │
│   192.168.100.143:2638   │
│   DB: mayekawa8          │
└──────────────────────────┘
```

---

## 12. Ejecución del Servicio

### Modo desarrollo (Maven)
```bash
mvn spring-boot:run
```

### Modo producción (JAR ejecutable)
```bash
# Construir el JAR
mvn clean package -DskipTests

# Ejecutar el JAR
java -jar target/rol-pagos-1.0.0.jar
```

El servidor arranca en el puerto definido por `SERVER_PORT` en el archivo `.env` (actualmente **8081**).

---

## 13. Comparación: Ejecución CLI vs. API REST

| Característica | `Main1.java` (CLI) | `Application.java` (API) |
|---|---|---|
| **Inicio** | `java -cp ... Jasper.Main1` | `java -jar rol-pagos-1.0.0.jar` |
| **Parámetros** | Desde el archivo `.env` (estáticos) | Desde parámetros de la URL HTTP (dinámicos) |
| **Salida** | Archivos `.pdf` en el directorio de ejecución | JSON con los PDF codificados en Base64 |
| **Invocación** | Manual, una vez por ejecución | HTTP GET, múltiples veces sin reiniciar |
| **Integración** | No integrable con otros sistemas | Integrable con cualquier cliente HTTP |
| **Escalabilidad** | No aplicable | Puede servir múltiples peticiones concurrentes |

---

*Documento generado para el proyecto: Sistema de Generación de Reportes de Rol de Pagos — `rol-pagos-1.0.0`*
