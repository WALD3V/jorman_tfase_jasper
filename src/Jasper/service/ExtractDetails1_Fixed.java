package Jasper.service;

import Jasper.model.DetalleRol;
import Jasper.model.RolGeneral;
import java.sql.*;
import java.util.*;

public class ExtractDetails1_Fixed {

    public static Map<String, RolGeneral> getAllRolesByParametros(Connection connection,
            String empInicial, String empFinal,
            String ciaCode, String tipoRol,
            String codInforme, String codCen) throws SQLException {

        Map<String, RolGeneral> rolesMap = new HashMap<>();
        Map<String, List<DetalleRol>> ingresosMap = new HashMap<>();
        Map<String, List<DetalleRol>> egresosMap = new HashMap<>();
        Map<String, List<DetalleRol>> descuentosMap = new HashMap<>();

        System.out.println("\n========================================");
        System.out.println("EJECUTANDO QUERY_ROLES_PRINCIPALES");
        System.out.println("Parámetros:");
        System.out.println("  codInforme : " + codInforme);
        System.out.println("  ciaCode    : " + ciaCode);
        System.out.println("  tipoRol    : " + tipoRol);
        System.out.println("  empInicial : " + empInicial);
        System.out.println("  empFinal   : " + empFinal);
        System.out.println("  codCen     : " + codCen);
        System.out.println("========================================");

        try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_ROLES_PRINCIPALES"))) {
            stmt.setString(1, codInforme);
            stmt.setString(2, codInforme);
            stmt.setString(3, ciaCode);
            stmt.setString(4, tipoRol);
            stmt.setString(5, ciaCode);
            stmt.setString(6, codInforme);
            stmt.setString(7, empInicial);
            stmt.setString(8, empFinal);
            stmt.setString(9, tipoRol);
            stmt.setString(10, ciaCode);
            stmt.setString(11, codInforme);
            stmt.setString(12, codCen);
            stmt.setString(13, codCen);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nResultados QUERY_ROLES_PRINCIPALES:");
            boolean hayFilas = false;
            int filaNum = 0;

            while (rs.next()) {
                hayFilas = true;
                filaNum++;
                String empCodigo = rs.getString("emp_codigo");

                System.out.println("\n  [Fila " + filaNum + "]");
                System.out.println("    emp_codigo              : " + empCodigo);
                System.out.println("    emp_nombres             : " + rs.getString("emp_nombres"));
                System.out.println("    emp_apellidos           : " + rs.getString("emp_apellidos"));
                System.out.println("    cargo                   : " + rs.getString("cargo"));
                System.out.println("    tro_anio                : " + rs.getString("tro_anio"));
                System.out.println("    mae_fechaini            : " + rs.getString("mae_fechaini"));
                System.out.println("    mae_fechafin            : " + rs.getString("mae_fechafin"));
                System.out.println("    periodo_descripcion     : " + rs.getString("periodo_descripcion"));
                System.out.println("    tro_descripcion         : " + rs.getString("tro_descripcion"));
                System.out.println("    cia_descripcion         : " + rs.getString("cia_descripcion"));
                System.out.println("    tipo_impresion          : " + rs.getString("tb_rol_orden_de_impresion_tipo"));
                System.out.println(
                        "    esm_descripcion         : " + rs.getString("tb_rol_estructura_maestro_esm_descripcion"));
                System.out.println("    valor                   : " + rs.getDouble("valor"));

                if (!rolesMap.containsKey(empCodigo)) {
                    String nombres = rs.getString("emp_nombres");
                    String apellidos = rs.getString("emp_apellidos");
                    String funcion = rs.getString("cargo");
                    String periodoAnio = rs.getString("tro_anio");
                    String fechaInicio = rs.getString("mae_fechaini");
                    String fechaFin = rs.getString("mae_fechafin");
                    String periodoMes = rs.getString("periodo_descripcion");
                    String fechaCorte = "2025-11-20";
                    String descripcionTipoRol = rs.getString("tro_descripcion");
                    String ciaDescripcion = rs.getString("cia_descripcion");

                    ingresosMap.put(empCodigo, new ArrayList<>());
                    egresosMap.put(empCodigo, new ArrayList<>());
                    descuentosMap.put(empCodigo, new ArrayList<>());

                    RolGeneral rol = new RolGeneral(periodoAnio, periodoMes, fechaCorte,
                            fechaInicio, fechaFin, empCodigo, nombres,
                            apellidos, funcion, descripcionTipoRol, ciaDescripcion,
                            ingresosMap.get(empCodigo),
                            egresosMap.get(empCodigo),
                            descuentosMap.get(empCodigo));
                    rolesMap.put(empCodigo, rol);
                    System.out.println("    --> Nuevo empleado registrado en rolesMap");
                }

                String tipoImpresion = rs.getString("tb_rol_orden_de_impresion_tipo");
                String descripcion = rs.getString("tb_rol_estructura_maestro_esm_descripcion");
                double valor = rs.getDouble("valor");

                if (descripcion != null && valor != 0) {
                    DetalleRol detalle = new DetalleRol(empCodigo, descripcion, tipoImpresion,
                            "Referencia", valor, 0.0);

                    if ("I".equalsIgnoreCase(tipoImpresion)) {
                        ingresosMap.get(empCodigo).add(detalle);
                        System.out.println("    --> Detalle agregado a INGRESOS");
                    } else if ("E".equalsIgnoreCase(tipoImpresion)) {
                        egresosMap.get(empCodigo).add(detalle);
                        System.out.println("    --> Detalle agregado a EGRESOS");
                    } else if ("D".equalsIgnoreCase(tipoImpresion)) {
                        descuentosMap.get(empCodigo).add(detalle);
                        System.out.println("    --> Detalle agregado a DESCUENTOS");
                    }
                } else {
                    System.out.println("    (fila ignorada: descripcion=" + descripcion + ", valor=" + valor + ")");
                }
            }

            if (!hayFilas) {
                System.out.println(
                        "  *** SIN RESULTADOS: La query principal no retornó filas. Verifique los parámetros. ***");
            } else {
                System.out.println("\n  Total filas obtenidas: " + filaNum);
                System.out.println("  Empleados únicos encontrados: " + rolesMap.size());
            }
            System.out.println("========================================\n");
        }

        if (!rolesMap.isEmpty()) {
            cargarIngresos(connection, rolesMap, ciaCode, codInforme);
            cargarEgresos(connection, rolesMap, ciaCode, codInforme);
            cargarDescuentos(connection, rolesMap, ciaCode, codInforme);
            cargarTotalIngresos(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
            cargarTotalEgresos(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
            cargarNetoAPagar(connection, rolesMap, empInicial, empFinal, ciaCode, codInforme);
        }

        return rolesMap;
    }

    private static void cargarIngresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String ciaCode, String codInforme) throws SQLException {

        for (String empCodigo : rolesMap.keySet()) {
            System.out.println("\n--- EJECUTANDO QUERY_INGRESOS para empleado: " + empCodigo + " ---");
            System.out.println("Parámetros: empCodigo=" + empCodigo + ", codInforme=" + codInforme
                    + ", tipo=H, ciaCode=" + ciaCode);

            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_INGRESOS"))) {
                stmt.setString(1, empCodigo);
                stmt.setString(2, empCodigo);
                stmt.setString(3, codInforme);
                stmt.setString(4, "H");
                stmt.setString(5, ciaCode);
                stmt.setString(6, empCodigo);
                stmt.setString(7, empCodigo);
                stmt.setString(8, codInforme);
                stmt.setString(9, "H");
                stmt.setString(10, ciaCode);

                ResultSet rs = stmt.executeQuery();
                RolGeneral rol = rolesMap.get(empCodigo);

                System.out.println("Resultados QUERY_INGRESOS:");
                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    double valor = rs.getDouble("valor");
                    String descripcion = rs.getString("esm_descripcion");

                    System.out.println("  valor: " + valor);
                    System.out.println("  esm_descripcion: " + descripcion);
                    System.out.println("  orden: " + rs.getInt("orden"));
                    System.out.println("  ---");

                    if (valor != 0) {
                        DetalleRol detalle = new DetalleRol(empCodigo, descripcion, "H",
                                "Referencia", valor, 0.0);
                        rol.getIngresos().add(detalle);
                        System.out.println("  *** GUARDADO EN ROL ***");
                    }
                }

                if (!hayDatos) {
                    System.out.println("  No se encontraron ingresos para este empleado");
                }
            }
        }
    }

    private static void cargarEgresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String ciaCode, String codInforme) throws SQLException {

        for (String empCodigo : rolesMap.keySet()) {
            System.out.println("\n--- EJECUTANDO QUERY_EGRESOS para empleado: " + empCodigo + " ---");
            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_EGRESOS"))) {
                stmt.setString(1, empCodigo);
                stmt.setString(2, empCodigo);
                stmt.setString(3, "D");
                stmt.setString(4, codInforme);
                stmt.setString(5, ciaCode);
                stmt.setString(6, empCodigo);
                stmt.setString(7, empCodigo);
                stmt.setString(8, "D");
                stmt.setString(9, codInforme);
                stmt.setString(10, ciaCode);

                ResultSet rs = stmt.executeQuery();

                System.out.println("Resultados QUERY_EGRESOS:");
                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    System.out.println("  valor: " + rs.getDouble("valor"));
                    System.out.println("  esm_descripcion: " + rs.getString("esm_descripcion"));
                    System.out.println("  orden: " + rs.getInt("orden"));
                    System.out.println("  ---");
                }

                if (!hayDatos) {
                    System.out.println("  No se encontraron egresos para este empleado");
                }
            }
        }
    }

    private static void cargarDescuentos(Connection connection, Map<String, RolGeneral> rolesMap,
            String ciaCode, String codInforme) throws SQLException {

        for (String empCodigo : rolesMap.keySet()) {
            System.out.println("\n--- EJECUTANDO QUERY_DESCUENTO_TOTAL para empleado: " + empCodigo + " ---");
            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_DESCUENTO_TOTAL"))) {
                stmt.setString(1, empCodigo);
                stmt.setString(2, empCodigo);
                stmt.setString(3, codInforme);
                stmt.setString(4, ciaCode);
                stmt.setString(5, empCodigo);
                stmt.setString(6, empCodigo);
                stmt.setString(7, codInforme);
                stmt.setString(8, ciaCode);

                ResultSet rs = stmt.executeQuery();
                System.out.println("Resultados QUERY_DESCUENTO_TOTAL:");
                RolGeneral rol = rolesMap.get(empCodigo);
                boolean hayDatos = false;
                if (rs.next()) {
                    hayDatos = true;
                    double valorDescuento = rs.getDouble("DESCUENTO");
                    System.out.println("  DESCUENTO (Total Egresos): " + valorDescuento);

                    if (valorDescuento != 0) {
                        DetalleRol detalle = new DetalleRol(empCodigo, "Descuento Total", "D",
                                "Referencia", Math.abs(valorDescuento), 0.0);
                        rol.getDescuentos().add(detalle);

                        rol.setTotalEgresos(valorDescuento);
                    }
                }

                if (!hayDatos) {
                    System.out.println("  No se encontraron descuentos para este empleado");
                }
            }
        }
    }

    private static void cargarTotalIngresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        System.out.println("\n--- EJECUTANDO QUERY_HAB_TOTSAL_DES POR EMPLEADO ---");

        for (String empCodigo : rolesMap.keySet()) {
            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_HAB_TOTSAL_DES"))) {
                // Usar el MISMO empleado como inicial y final para sumar solo ESE empleado
                stmt.setString(1, empCodigo); // emp_ini = empleado específico
                stmt.setString(2, empCodigo); // emp_fin = empleado específico
                stmt.setString(3, codInforme);
                stmt.setString(4, ciaCode);
                stmt.setString(5, empCodigo); // emp_ini = empleado específico
                stmt.setString(6, empCodigo); // emp_fin = empleado específico
                stmt.setString(7, codInforme);
                stmt.setString(8, ciaCode);

                ResultSet rs = stmt.executeQuery();
                RolGeneral rol = rolesMap.get(empCodigo);

                if (rs.next()) {
                    double totalHaberes = rs.getDouble("DESCUENTO");
                    rol.setTotalIngresos(totalHaberes);
                    System.out.println(
                            "  *** EMPLEADO " + empCodigo + ": totalIngresos=" + totalHaberes + " (desde query) ***");
                }
            }
        }
    }

    private static void cargarTotalEgresos(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        System.out.println("\n--- EJECUTANDO QUERY_DESCUENTO_TOTAL POR EMPLEADO ---");

        for (String empCodigo : rolesMap.keySet()) {
            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_DESCUENTO_TOTAL"))) {
                stmt.setString(1, empCodigo); // emp_ini = empleado específico
                stmt.setString(2, empCodigo); // emp_fin = empleado específico
                stmt.setString(3, codInforme);
                stmt.setString(4, ciaCode);
                stmt.setString(5, empCodigo); // emp_ini = empleado específico
                stmt.setString(6, empCodigo); // emp_fin = empleado específico
                stmt.setString(7, codInforme);
                stmt.setString(8, ciaCode);

                ResultSet rs = stmt.executeQuery();
                RolGeneral rol = rolesMap.get(empCodigo);

                if (rs.next()) {
                    double totalEgresos = rs.getDouble("DESCUENTO");
                    rol.setTotalEgresos(totalEgresos);
                    System.out.println(
                            "  *** EMPLEADO " + empCodigo + ": totalEgresos=" + totalEgresos + " (desde query) ***");
                }
            }
        }
    }

    private static void cargarNetoAPagar(Connection connection, Map<String, RolGeneral> rolesMap,
            String empInicial, String empFinal, String ciaCode, String codInforme) throws SQLException {

        System.out.println("\n--- EJECUTANDO QUERY_NETO POR EMPLEADO ---");

        for (String empCodigo : rolesMap.keySet()) {
            try (PreparedStatement stmt = connection.prepareStatement(QueryLoader.getQuery("QUERY_NETO"))) {
                // 24 parámetros, todos usando el mismo empleado
                stmt.setString(1, empCodigo); // haberes principales
                stmt.setString(2, empCodigo);
                stmt.setString(3, codInforme);
                stmt.setString(4, ciaCode);
                stmt.setString(5, empCodigo); // descuentos principales
                stmt.setString(6, empCodigo);
                stmt.setString(7, codInforme);
                stmt.setString(8, ciaCode);
                stmt.setString(9, empCodigo); // haberes anexos
                stmt.setString(10, empCodigo);
                stmt.setString(11, codInforme);
                stmt.setString(12, ciaCode);
                stmt.setString(13, empCodigo); // descuentos anexos
                stmt.setString(14, empCodigo);
                stmt.setString(15, codInforme);
                stmt.setString(16, ciaCode);
                stmt.setString(17, empCodigo); // repetir para anexos
                stmt.setString(18, empCodigo);
                stmt.setString(19, codInforme);
                stmt.setString(20, ciaCode);
                stmt.setString(21, empCodigo);
                stmt.setString(22, empCodigo);
                stmt.setString(23, codInforme);
                stmt.setString(24, ciaCode);

                ResultSet rs = stmt.executeQuery();
                RolGeneral rol = rolesMap.get(empCodigo);

                if (rs.next()) {
                    double netoTotal = rs.getDouble("TOTAL");
                    rol.setNetoAPagar(netoTotal);
                    System.out.println(
                            "  *** EMPLEADO " + empCodigo + ": netoAPagar=" + netoTotal + " (desde query) ***");
                }
            }
        }
    }
}
