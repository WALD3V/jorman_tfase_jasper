package Jasper.config;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection1 {
    
    // Configuración para SQL Anywhere 8
    private static final String IP = "localhost";
    private static final String PUERTO = "2638";
    private static final String DATABASE = "mayekawa8"; // AGREGAR EL NOMBRE DE TU BASE DE DATOS
    private static final String USUARIO = "DBA";
    private static final String PASSWORD = "sql";
    
    public static Connection conectar(String ip, String puerto, String database, String usuario, String password) {
        Connection con = null;
        try {
            System.out.println("=== Iniciando conexión a SQL Anywhere 8 ===");
            System.out.println("IP: " + ip);
            System.out.println("Puerto: " + puerto);
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
            System.out.println("✓ CONEXIÓN EXITOSA a SQL Anywhere 8 en " + ip + ":" + puerto);
            
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
        return conectar(IP, PUERTO, DATABASE, USUARIO, PASSWORD);
    }
    
    public static boolean testConnection() {
        System.out.println("=== TEST DE CONEXIÓN SQL ANYWHERE 8 ===\n");
        Connection con = conectar(IP, PUERTO, DATABASE, USUARIO, PASSWORD);
        
        if (con != null) {
            System.out.println("\n=== Cerrando conexión ===");
            try {
                con.close();
                System.out.println("✓ Conexión cerrada correctamente");
                return true;
            } catch (SQLException e) {
                System.out.println("✗ Error cerrando conexión: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("\n✗ No se pudo establecer la conexión");
            return false;
        }
    }
    
    public static void main(String[] args) {
        testConnection();
        System.out.println("\n=== FIN DEL TEST ===");
    }
}
