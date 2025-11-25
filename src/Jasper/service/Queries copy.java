/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Jasper.service;

/**
 * Clase centralizada para todas las consultas SQL del sistema de reportes de roles.
 * Mantiene organizadas y separadas todas las queries de la lógica de negocio.
 *
 * @author PC
 */
public class Queries {

    /**
     * Consulta principal para obtener todos los roles de empleados con sus detalles.
     * Incluye información de empleados, tipos de rol, estructura de campos,
     * períodos y el orden de impresión de los campos.
     *
     * Parámetros en orden:
     * 1. codInforme - Código del informe
     * 2. codInforme - Código del informe (2do uso)
     * 3. ciaCode - Código de compañía
     * 4. tipoRol - Código del tipo de rol
     * 5. ciaCode - Código de compañía (2do uso)
     * 6. codInforme - Código del informe (3er uso)
     * 7. empInicial - Empleado inicial (rango)
     * 8. empFinal - Empleado final (rango)
     * 9. tipoRol - Código del tipo de rol (2do uso)
     * 10. ciaCode - Código de compañía (3er uso)
     * 11. codInforme - Código del informe (4to uso)
     * 12. codCen - Centro de costo
     * 13. codCen - Centro de costo (2do uso)
     */
    public static final String QUERY_ROLES_PRINCIPALES = 
            "SELECT tb_rol_vista_maestro.emp_codigo, "
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

}
