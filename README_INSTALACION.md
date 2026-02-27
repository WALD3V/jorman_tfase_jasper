# Sistema de Generación de Reportes de Rol de Pagos

## 📋 Requisitos Previos

- **Java 8** instalado (`java -version` debe funcionar)
- **Maven 3.x** instalado (`mvn --version` debe funcionar)
- Acceso a la base de datos Sybase

---

## ⚙️ Configuración del archivo `.env`

Edita el archivo `.env` con los datos de tu base de datos antes de ejecutar:

```env
DB_HOST=192.168.1.100     # IP del servidor Sybase
DB_PORT=2638              # Puerto de Sybase
DB_NAME=tu_base_datos     # Nombre de la base de datos
DB_USERNAME=tu_usuario    # Usuario de BD
DB_PASSWORD=tu_password   # Contraseña de BD
SERVER_PORT=8081          # Puerto del servidor web
```

---

## 🔨 Compilar y generar el JAR

Desde la raíz del proyecto, ejecuta:

```cmd
mvn clean package -DskipTests
```

> ✅ Al finalizar debes ver: `BUILD SUCCESS`  
> 📁 El JAR se genera en: `target\rol-pagos-1.0.0.jar`

> ℹ️ Las librerías locales (`KabitaLib`, `jconn4d`) están en la carpeta `librerias\` y se incluyen automáticamente. No es necesario instalar nada manualmente.

---

## 🚀 Ejecutar la aplicación

```cmd
java -jar target\rol-pagos-1.0.0.jar
```

La aplicación estará disponible en:
- **URL Base:** http://localhost:8081
- **Health Check:** http://localhost:8081/api/reportes/health

---

## 🌐 Uso de la API

### Generar Reporte de Rol de Pagos

**Endpoint:** `GET /api/reportes/rol-pagos`

| Parámetro    | Descripción             | Ejemplo      |
|--------------|-------------------------|--------------|
| `empInicial` | Código empleado inicial | `0920521226` |
| `empFinal`   | Código empleado final   | `0920521226` |
| `ciaCode`    | Código de compañía      | `01`         |
| `tipoRol`    | Tipo de rol             | `001`        |
| `codInforme` | Código de informe       | `01`         |
| `codCen`     | Centro de costo         | `000`        |

**Ejemplo:**
```
http://localhost:8081/api/reportes/rol-pagos?empInicial=0920521226&empFinal=0920521226&ciaCode=01&tipoRol=001&codInforme=01&codCen=000
```

---

## 🔧 Solución de Problemas

### ❌ Puerto ocupado
```cmd
java -jar target\rol-pagos-1.0.0.jar --server.port=8082
```

### ❌ Error de conexión a BD
1. Verificar que los datos en `.env` sean correctos
2. Verificar que el servidor Sybase esté accesible
3. Revisar los logs en la consola

### 🐛 Ver logs detallados
```cmd
java -jar target\rol-pagos-1.0.0.jar --logging.level.root=DEBUG
```
