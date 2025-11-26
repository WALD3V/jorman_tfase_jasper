/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Jasper.service;

/**
 * Clase para extraer detalles del rol de empleados desde la base de datos. Usa
 * tu consulta SQL original exacta, solo recibe los parámetros desde el main.
 *
 * @author PC
 */
import Jasper.model.DetalleRol;
import Jasper.model.RolGeneral;
import java.sql.*;
import java.util.*;

public class ExtractDetails1 {

    /**
     * Método para obtener todos los roles usando tu consulta SQL original
     * exacta. Los parámetros se pasan desde el main.
     */
    public static Map<String, RolGeneral> getAllRolesByParametros(Connection connection,
            String empInicial, String empFinal,
            String ciaCode, String tipoRol,
            String codInforme, String codCen) throws SQLException {

        Map<String, RolGeneral> rolesMap = new HashMap<>();
        Map<String, List<DetalleRol>> ingresosMap = new HashMap<>();
        Map<String, List<DetalleRol>> egresosMap = new HashMap<>();
        Map<String, List<DetalleRol>> descuentosMap = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_ROLES_PRINCIPALES"))) {
            // Parámetros en el orden exacto de tu consulta original
            stmt.setString(1, codInforme);    // :cod_inf
            stmt.setString(2, codInforme);    // :cod_inf
            stmt.setString(3, ciaCode);       // :cia
            stmt.setString(4, tipoRol);       // :tipo_rol
            stmt.setString(5, ciaCode);       // :cia
            stmt.setString(6, codInforme);    // :cod_inf
            stmt.setString(7, empInicial);    // :emp_ini
            stmt.setString(8, empFinal);      // :emp_fin
            stmt.setString(9, tipoRol);       // :tipo_rol
            stmt.setString(10, ciaCode);      // :cia
            stmt.setString(11, codInforme);   // :cod_inf
            stmt.setString(12, codCen);       // :codcen
            stmt.setString(13, codCen);       // :codcen

            System.out.print("Ejecutando QUERY_ROLES_PRINCIPALES con parámetros:");
            System.out.print("datos " + codInforme + ", " + ciaCode + ", " + tipoRol + ", " + empInicial + ", " + empFinal + ", " + codCen);   


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String empCodigo = rs.getString("emp_codigo");

                // Si es la primera vez que vemos este empleado, crear sus datos básicos
                if (!rolesMap.containsKey(empCodigo)) {
                    // Extraer datos básicos del empleado
                    String nombres = rs.getString("emp_nombres");
                    String apellidos = rs.getString("emp_apellidos");
                    String funcion = rs.getString("cargo");
                    String periodoAnio = rs.getString("tro_anio");
                    String fechaInicio = rs.getString("mae_fechaini");
                    String fechaFin = rs.getString("mae_fechafin");

                    // Valores por defecto como en tu clase original
                    int periodoMes = 11;
                    String fechaCorte = "2025-11-20";

                    // Inicializar listas para este empleado
                    ingresosMap.put(empCodigo, new ArrayList<>());
                    egresosMap.put(empCodigo, new ArrayList<>());
                    descuentosMap.put(empCodigo, new ArrayList<>());

                    // Crear el objeto RolGeneral
                    RolGeneral rol = new RolGeneral(periodoAnio, periodoMes, fechaCorte,
                            fechaInicio, fechaFin, empCodigo, nombres,
                            apellidos, funcion,
                            ingresosMap.get(empCodigo),
                            egresosMap.get(empCodigo),
                            descuentosMap.get(empCodigo));
                    rolesMap.put(empCodigo, rol);
                }

                // Procesar detalles de rol
                String tipoImpresion = rs.getString("tb_rol_orden_de_impresion_tipo");
                String descripcion = rs.getString("tb_rol_estructura_maestro_esm_descripcion");
                double valor = rs.getDouble("valor");

                if (descripcion != null && valor != 0) {
                    DetalleRol detalle = new DetalleRol(empCodigo, descripcion, tipoImpresion,
                            "Referencia", valor, 0.0);

                    // Clasificar según el tipo (ajusta según tu lógica de negocio)
                    if ("I".equalsIgnoreCase(tipoImpresion)) {
                        ingresosMap.get(empCodigo).add(detalle);
                    } else if ("E".equalsIgnoreCase(tipoImpresion)) {
                        egresosMap.get(empCodigo).add(detalle);
                    } else if ("D".equalsIgnoreCase(tipoImpresion)) {
                        descuentosMap.get(empCodigo).add(detalle);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al extraer datos de empleados: " + e.getMessage());
            throw e;
        }

        // Cargar ingresos, egresos y descuentos
        if (!rolesMap.isEmpty()) {
            cargarIngresos(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
            cargarEgresos(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
            cargarDescuentos(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
        }

        return rolesMap;
    }

    /**
     * Método para obtener un empleado específico (compatible con tu main
     * actual)
     */
    public static RolGeneral getRolByEmpleado(Connection connection, String empCodigo,
            String ciaCode, String tipoRol,
            String codInforme, String codCen) throws SQLException {

        Map<String, RolGeneral> roles = getAllRolesByParametros(connection, empCodigo, empCodigo,
                ciaCode, tipoRol, codInforme, codCen);
        return roles.get(empCodigo);
    }

    /**
     * Carga los ingresos para los empleados en el rango especificado
     */
    private static void cargarIngresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_INGRESOS"))) {
            stmt.setString(1, empInicial);
            stmt.setString(2, empFinal);
            stmt.setString(3, codInforme);
            stmt.setString(4, "I");
            stmt.setString(5, ciaCode);
            stmt.setString(6, empInicial);
            stmt.setString(7, empFinal);
            stmt.setString(8, codInforme);
            stmt.setString(9, "I");
            stmt.setString(10, ciaCode);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double valor = rs.getDouble("valor");
                String descripcion = rs.getString("esm_descripcion");

                if (valor != 0) {
                    // Buscar el RolGeneral correspondiente y agregar el ingreso
                    for (RolGeneral rol : rolesMap.values()) {
                        DetalleRol detalle = new DetalleRol(rol.getEmpCodigo(), descripcion, "I",
                                "Referencia", valor, 0.0);
                        rol.getIngresos().add(detalle);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar ingresos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Carga los egresos para los empleados en el rango especificado
     */
    private static void cargarEgresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_EGRESOS"))) {
            stmt.setString(1, empInicial);
            stmt.setString(2, empFinal);
            stmt.setString(3, "E");
            stmt.setString(4, codInforme);
            stmt.setString(5, ciaCode);
            stmt.setString(6, empInicial);
            stmt.setString(7, empFinal);
            stmt.setString(8, "E");
            stmt.setString(9, codInforme);
            stmt.setString(10, ciaCode);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double valor = rs.getDouble("valor");
                String descripcion = rs.getString("esm_descripcion");

                if (valor != 0) {
                    // Buscar el RolGeneral correspondiente y agregar el egreso
                    for (RolGeneral rol : rolesMap.values()) {
                        DetalleRol detalle = new DetalleRol(rol.getEmpCodigo(), descripcion, "E",
                                "Referencia", valor, 0.0);
                        rol.getEgresos().add(detalle);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar egresos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Carga los descuentos para los empleados en el rango especificado
     */
    private static void cargarDescuentos(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_DESCUENTO_TOTAL"))) {
            stmt.setString(1, empInicial);
            stmt.setString(2, empFinal);
            stmt.setString(3, codInforme);
            stmt.setString(4, ciaCode);
            stmt.setString(5, empInicial);
            stmt.setString(6, empFinal);
            stmt.setString(7, codInforme);
            stmt.setString(8, ciaCode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double valorDescuento = rs.getDouble("DESCUENTO");

                // Distribuir el descuento entre todos los empleados
                if (valorDescuento != 0) {
                    double descuentoPorEmpleado = valorDescuento / rolesMap.size();

                    for (RolGeneral rol : rolesMap.values()) {
                        DetalleRol detalle = new DetalleRol(rol.getEmpCodigo(), "Descuento Total", "D",
                                "Referencia", Math.abs(descuentoPorEmpleado), 0.0);
                        rol.getDescuentos().add(detalle);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar descuentos: " + e.getMessage());
            throw e;
        }
    }
}
