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

        // Tu consulta SQL original EXACTA
        String queryPrincipal
                = "SELECT tb_rol_vista_maestro.emp_codigo, "
                + "       tb_rol_vista_maestro.valor, "
                + "       tb_rol_orden_de_impresion_tipo = (select tipo from tb_rol_orden_de_impresion where ( tb_rol_vista_maestro.cia_codigo = tb_rol_orden_de_impresion.cia_codigo ) and "
                + "           ( tb_rol_vista_maestro.esm_campo = tb_rol_orden_de_impresion.esm_codigo ) and cod_informe = ?), "
                + "       tb_rol_orden_de_impresion_orden = (select orden from tb_rol_orden_de_impresion where ( tb_rol_vista_maestro.cia_codigo = tb_rol_orden_de_impresion.cia_codigo ) and "
                + "           ( tb_rol_vista_maestro.esm_campo = tb_rol_orden_de_impresion.esm_codigo ) and cod_informe = ?), "
                + "       tb_rol_empleado.emp_nombres, "
                + "       tb_rol_empleado.emp_apellidos, "
                + "       tb_rol_tipo_rol.tro_descripcion, "
                + "       tb_rol_estructura_maestro_esm_descripcion = (select esm_descripcion from tb_rol_estructura_maestro where cia_codigo = tb_rol_vista_maestro.cia_codigo and esm_campo = tb_rol_vista_maestro.esm_campo), "
                + "       cia_descripcion, "
                + "       tb_rol_tipo_rol.mae_001, "
                + "       tb_rol_tipo_rol.tro_anio, "
                + "       (select tb_rol_fichatablas.ftb_descripcion "
                + "        from tb_rol_parametros,tb_rol_fichatablas "
                + "        where tb_rol_fichatablas.cia_codigo = tb_rol_parametros.cia_codigo "
                + "          and tb_rol_fichatablas.tbl_codigo = tb_rol_parametros.tbl_codigo_per "
                + "          and tb_rol_fichatablas.cia_codigo = ? "
                + "          and tb_rol_fichatablas.ftb_valor = ( select tb_rol_tipo_rol.mae_001 from tb_rol_tipo_rol where tb_rol_tipo_rol.tro_codigo = ? AND CIA_CODIGO=?)) as periodo_descripcion, "
                + "       (SELECT fun_descripcion from tb_rol_funciones where fun_codigo=tb_rol_empleado.fun_codigo) cargo, "
                + "       tb_rol_maestro.mae_fechaini, tb_rol_maestro.mae_fechafin, "
                + "       cod_informe = (select cod_informe from tb_rol_orden_de_impresion where ( tb_rol_vista_maestro.cia_codigo = tb_rol_orden_de_impresion.cia_codigo ) and "
                + "           ( tb_rol_vista_maestro.esm_campo = tb_rol_orden_de_impresion.esm_codigo ) and cod_informe = ?) "
                + "FROM tb_rol_vista_maestro, tb_rol_maestro, "
                + "     tb_rol_empleado, "
                + "     tb_rol_tipo_rol, "
                + "     tbcon_mcias, "
                + "     tb_rol_relemp_tiporol "
                + "WHERE ( tb_rol_vista_maestro.emp_codigo = tb_rol_empleado.emp_codigo ) and "
                + "      ( tb_rol_tipo_rol.cia_codigo = tb_rol_relemp_tiporol.cia_codigo ) and "
                + "      ( tb_rol_tipo_rol.tro_codigo = tb_rol_relemp_tiporol.tro_codigo ) and "
                + "      ( tb_rol_empleado.emp_codigo = tb_rol_relemp_tiporol.emp_codigo ) and "
                + "      (tb_rol_vista_maestro.emp_codigo = tb_rol_maestro.emp_codigo ) and "
                + "      ( tb_rol_vista_maestro.cia_codigo = tbcon_mcias.cia_codigo ) and "
                + "      ( ( tb_rol_vista_maestro.emp_codigo >= ? ) AND "
                + "      ( tb_rol_vista_maestro.emp_codigo <= ? ) AND "
                + "      ( tb_rol_relemp_tiporol.emp_estado = 'A' ) AND "
                + "      ( tb_rol_tipo_rol.tro_codigo = ? ) ) AND "
                + "      tb_rol_orden_de_impresion_orden > 0 and "
                + "      tb_rol_vista_maestro.cia_codigo =? "
                + "      and cod_informe=? "
                + "      and (tb_rol_empleado.codcen=? or ?='000') "
                + "ORDER BY tb_rol_empleado.codcen Asc, "
                + "         tb_rol_empleado.emp_apellidos Asc, "
                + "         tb_rol_empleado.emp_nombres Asc";

        try (PreparedStatement stmt = connection.prepareStatement(queryPrincipal)) {
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
}
