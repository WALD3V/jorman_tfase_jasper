# ANEXO: Integración de Spring Boot como API REST
## Parte 1 — Configuración e Infraestructura

---

## 1. Descripción General

El sistema de generación de reportes de rol de pagos fue desarrollado con **Spring Boot 2.7.18** como servidor HTTP embebido. Esto permite que el sistema sea invocado mediante peticiones HTTP en lugar de ejecutarse únicamente por línea de comandos, facilitando su integración con otros sistemas como un frontend web.

---

## 2. Estructura del Proyecto

El proyecto sigue una arquitectura en capas estándar de Spring MVC:

```
src/main/java/
├── Application.java              ← Punto de entrada Spring Boot
├── controller/
│   └── ReportController.java     ← Expone los endpoints HTTP
├── service/
│   ├── ReportService.java        ← Lógica de negocio
│   └── JasperReportExample.java  ← Generación de PDFs
├── model/
│   ├── RolGeneral.java           ← Datos del empleado y su rol
│   └── DetalleRol.java           ← Ítems de ingresos/egresos
└── config/
    └── DatabaseConnection1.java  ← Conexión a base de datos Sybase
```

---

## 3. Punto de Entrada

La clase `Application.java` arranca el servidor con `@SpringBootApplication`. Se excluye la autoconfiguración del datasource estándar porque la conexión a **Sybase** se gestiona manualmente con su propio driver (`jconn4d`).

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## 4. Dependencias Clave (`pom.xml`)

| Dependencia | Función |
|---|---|
| `spring-boot-starter-web` | Servidor Tomcat embebido + Spring MVC + Jackson (JSON) |
| `spring-boot-starter-data-jpa` | Soporte JPA (excluido en arranque para no conflictuar con Sybase) |
| `jasperreports 6.20.6` | Motor de generación de reportes PDF |
| `mysql-connector-java` / `jconn4d` | Drivers de base de datos |
| `dotenv-java 2.2.4` | Lectura de variables de entorno desde `.env` |

---

## 5. Configuración

Las credenciales de base de datos y parámetros del sistema se leen desde el archivo `.env` gracias a la librería `dotenv-java`:

```dotenv
DB_HOST=192.168.100.143
DB_PORT=2638
DB_NAME=mayekawa8
DB_USERNAME=dba
DB_PASSWORD=sql
SERVER_PORT=8081
```

Esto evita que datos sensibles estén hardcodeados en el código fuente.

---

## 6. Ejecución

```bash
# Modo desarrollo
mvn spring-boot:run

# Modo producción
mvn clean package -DskipTests
java -jar target/rol-pagos-1.0.0.jar
```

El servidor queda disponible en `http://localhost:8081`.

---

*Continúa en → `ANEXO_SPRING_API_PARTE2.md`*

*Proyecto: Sistema de Generación de Reportes de Rol de Pagos — `rol-pagos-1.0.0`*
