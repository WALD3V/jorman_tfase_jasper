package Jasper.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection1 {
    
    private static final Dotenv dotenv = Dotenv.load();
    
    public static Connection conectar(String ip, String puerto, String database, String usuario, String password) {
        Connection con = null;
        try {
            System.out.println("=== Iniciando conexión a Sybase ===");
            System.out.println("IP: " + ip);
            System.out.println("Puerto: " + puerto);
            System.out.println("Database: " + database);
            System.out.println("Usuario: " + usuario);
            
            System.out.println("Registrando driver Sybase...");
            DriverManager.registerDriver((Driver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance());
            System.out.println("Driver registrado exitosamente");
            
            Properties props = new Properties();
            props.put("User", usuario);
            props.put("Password", password);
            
            String url = "jdbc:sybase:Tds:" + ip + ":" + puerto + "/" + database;
            System.out.println("URL de conexión: " + url);
            System.out.println("Intentando conectar...");
            
            con = DriverManager.getConnection(url, props);
            System.out.println("✓ CONEXIÓN EXITOSA a Sybase en " + ip + ":" + puerto);
            
        } catch (SQLException e) {
            System.out.println("✗ ERROR SQL: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
        } catch (Exception e) {
            System.out.println("✗ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }
    
    public static Connection getConnection() throws SQLException {
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String database = dotenv.get("DB_NAME");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");
        
        return conectar(host, port, database, username, password);
    }
    
    public static boolean testConnection() {
        System.out.println("=== TEST DE CONEXIÓN SYBASE ===\n");
        try {
            Connection con = getConnection();
            
            if (con != null) {
                System.out.println("\n=== Cerrando conexión ===");
                con.close();
                System.out.println("✓ Conexión cerrada correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error en conexión: " + e.getMessage());
        }
        
        System.out.println("\n✗ No se pudo establecer la conexión");
        return false;
    }
    
    public static void main(String[] args) {
        testConnection();
        System.out.println("\n=== FIN DEL TEST ===");
    }
}

