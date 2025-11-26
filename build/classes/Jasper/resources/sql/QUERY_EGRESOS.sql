SELECT "tb_rol_vista_maestro"."valor",   
       "tb_rol_estructura_maestro"."esm_descripcion",   
       "tb_rol_orden_de_impresion"."orden"  
FROM "tb_rol_vista_maestro",   
     "tb_rol_orden_de_impresion",   
     "tb_rol_estructura_maestro"  
WHERE ( "tb_rol_vista_maestro"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
( "tb_rol_vista_maestro"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     ( "tb_rol_vista_maestro"."cia_codigo" = "tb_rol_estructura_maestro"."cia_codigo" ) and  
       ( "tb_rol_vista_maestro"."esm_campo" = "tb_rol_estructura_maestro"."esm_campo" ) and  
     ("tb_rol_vista_maestro"."emp_codigo" >= ? ) AND  
     ("tb_rol_vista_maestro"."emp_codigo" <= ? ) AND  
     ("tb_rol_orden_de_impresion"."orden" <> 0 ) AND  
     "tb_rol_orden_de_impresion"."tipo" = ? AND  
     "tb_rol_orden_de_impresion"."cod_informe" = ? 
	and "tb_rol_vista_maestro"."cia_codigo" =?
UNION ALL 
SELECT "tb_rol_vista_maestro_anexo"."valor",   
       "tb_rol_estructura_maestro"."esm_descripcion",   
       "tb_rol_orden_de_impresion"."orden"  
FROM "tb_rol_vista_maestro_anexo",   
     "tb_rol_orden_de_impresion",   
     "tb_rol_estructura_maestro"  
WHERE ( "tb_rol_vista_maestro_anexo"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
( "tb_rol_vista_maestro_anexo"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     ( "tb_rol_vista_maestro_anexo"."cia_codigo" = "tb_rol_estructura_maestro"."cia_codigo" ) and  
     ( "tb_rol_vista_maestro_anexo"."esm_campo" = "tb_rol_estructura_maestro"."esm_campo" ) and  
     ("tb_rol_vista_maestro_anexo"."emp_codigo" >= ? ) AND  
     ("tb_rol_vista_maestro_anexo"."emp_codigo" <= ? ) AND  
     ("tb_rol_orden_de_impresion"."orden" <> 0 ) AND  
     "tb_rol_orden_de_impresion"."tipo" = ? AND  
     "tb_rol_orden_de_impresion"."cod_informe" = ? and
	 "tb_rol_vista_maestro_anexo"."cia_codigo" =?
