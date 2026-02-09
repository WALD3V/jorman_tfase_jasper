# Sistema de Generación de Reportes de Rol de Pagos

## 📋 Requisitos Previos
- Java 8 o superior instalado
- Acceso a la base de datos Sybase

## 📦 Archivos Incluidos
```
rol-pagos/
├── rol-pagos-1.0.0.jar          # Aplicación principal
├── .env                         # Configuración de base de datos
├── ejecutar.bat                 # Script para Windows
├── ejecutar.sh                  # Script para Linux/Mac
├── README.md                    # Este archivo
└── src/
    └── Jasper/
        └── resources/
            └── jasperRolReport.jasper  # Template del reporte
```

## ⚙️ Configuración

### 1. Editar archivo `.env`
Abrir `.env` con un editor de texto y configurar los datos de tu base de datos:

```env
# Configuración de Base de datos Sybase
DB_HOST=192.168.1.100          # IP del servidor Sybase
DB_PORT=2638                   # Puerto de Sybase
DB_NAME=tu_base_datos          # Nombre de la base de datos
DB_USERNAME=tu_usuario         # Usuario de BD
DB_PASSWORD=tu_password        # Contraseña de BD

# Puerto del servidor web (opcional)
SERVER_PORT=8081 // 8080
```

## 🚀 Ejecución

### Windows:
1. Hacer doble clic en `ejecutar.bat`
2. O desde CMD: `ejecutar.bat`

### Linux/Mac:
1. Abrir terminal en la carpeta
2. Ejecutar: `./ejecutar.sh`

### Manual:
```bash
java -jar rol-pagos-1.0.0.jar
```

## 🌐 Uso de la API

Una vez iniciado, la aplicación estará disponible en:
- **URL Base:** http://localhost:8081
- **Health Check:** http://localhost:8081/api/reportes/health

### Generar Reporte de Rol de Pagos

**Endpoint:** `GET /api/reportes/rol-pagos`

**Parámetros:**
- `empInicial`: Código empleado inicial (ej: 0920521226)
- `empFinal`: Código empleado final (ej: 0920521226)
- `ciaCode`: Código de compañía (ej: 01)
- `tipoRol`: Tipo de rol (ej: 001)
- `codInforme`: Código de informe (ej: 01)
- `codCen`: Centro de costo (ej: 000)

**Ejemplo:**
```
http://localhost:8081/api/reportes/rol-pagos?empInicial=0920521226&empFinal=0920521226&ciaCode=01&tipoRol=001&codInforme=01&codCen=000
```

**Respuesta JSON:**
```json
{
  "total": 1,
  "reportes": {
    "0": {
      "empCodigo": "0920521226",
      "nombres": "Juan",
      "apellidos": "Pérez",
      "pdf": "JVBERi0xLjQKJeLjz9MK...",
      "tamaño": 15234
    }
  }
}
```

## 🔧 Solución de Problemas

### Error de conexión a BD:
1. Verificar que los datos en `.env` sean correctos
2. Verificar que el servidor Sybase esté accesible
3. Revisar logs en la consola

### Puerto ocupado:
```bash
java -jar rol-pagos-1.0.0.jar --server.port=8082
```

### Ver logs detallados:
```bash
java -jar rol-pagos-1.0.0.jar --logging.level.root=DEBUG
```


