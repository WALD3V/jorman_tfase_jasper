# ANEXO: Integración de Spring Boot como API REST
## Parte 2 — API y Funcionamiento

---

## 1. Endpoints Disponibles

El controlador `ReportController` expone dos rutas bajo la base `/api/reportes`:

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/reportes/health` | Verifica que el servicio está activo |
| `GET` | `/api/reportes/rol-pagos` | Genera reportes PDF para un rango de empleados |

### Parámetros de `/api/reportes/rol-pagos`

| Parámetro | Valor por defecto | Descripción |
|---|---|---|
| `ciaCode` | `01` | Código de empresa |
| `tipoRol` | `001` | Tipo de rol de pagos |
| `codInforme` | `001` | Código del informe |
| `empInicial` | `000` | Empleado inicial del rango |
| `empFinal` | `999999` | Empleado final del rango |
| `codCen` | `000` | Centro de costos |

**Ejemplo de petición:**
```
GET http://localhost:8081/api/reportes/rol-pagos?ciaCode=01&empInicial=100&empFinal=200
```

**Respuesta (JSON):** Un objeto con el total de empleados procesados y, por cada uno, su código, nombre y el PDF generado codificado en **Base64**.

---

## 2. Flujo de Procesamiento

```
Petición HTTP
     ↓
ReportController      →  extrae parámetros de la URL
     ↓
ReportService         →  consulta datos en Sybase (vía DatabaseConnection1)
     ↓
ExtractDetails1_Fixed →  ejecuta queries SQL, retorna datos de empleados
     ↓
JasperReportExample   →  genera el PDF en memoria (ByteArrayOutputStream)
     ↓
Base64.encode(pdf)    →  convierte el PDF a texto para incluirlo en JSON
     ↓
Respuesta JSON        →  devuelta al cliente
```

---

## 3. Anotaciones Spring Utilizadas

| Anotación | Clase | Propósito |
|---|---|---|
| `@SpringBootApplication` | `Application` | Arranca el contexto de Spring y el servidor Tomcat |
| `@RestController` | `ReportController` | Marca la clase como controlador HTTP que retorna datos (no vistas) |
| `@RequestMapping` | `ReportController` | Define la ruta base `/api/reportes` |
| `@GetMapping` | Métodos del controller | Mapea peticiones `GET` a métodos Java específicos |
| `@RequestParam` | Parámetros del método | Extrae parámetros de la URL de la petición |
| `@Autowired` | `ReportController` | Inyecta automáticamente el bean `ReportService` |
| `@Service` | `ReportService` | Registra la clase como componente de servicio en el contexto de Spring |

---

*Viene de → `ANEXO_SPRING_API_PARTE1.md`*

*Proyecto: Sistema de Generación de Reportes de Rol de Pagos — `rol-pagos-1.0.0`*
