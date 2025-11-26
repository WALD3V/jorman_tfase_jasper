SELECT  ISNULL( ( SELECT  SUM( coalesce("tb_rol_vista_maestro"."valor",0) ) 
       FROM "tb_rol_vista_maestro", "tb_rol_orden_de_impresion"  
	    WHERE ("tb_rol_vista_maestro"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
			("tb_rol_vista_maestro"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     	     (( "tb_rol_vista_maestro"."emp_codigo" >= ? ) AND  
     	     ("tb_rol_vista_maestro"."emp_codigo" <= ? ) AND  
     	     ("tb_rol_orden_de_impresion"."orden" <> 0 ))   AND
				"tb_rol_orden_de_impresion"."tipo"='H' and
                  "tb_rol_orden_de_impresion"."cod_informe" =? and
			"tb_rol_vista_maestro"."cia_codigo" = ?
     ),0)  -                        
	 ISNULL( ( SELECT SUM( coalesce("tb_rol_vista_maestro"."valor",0) ) 
       FROM   "tb_rol_vista_maestro", "tb_rol_orden_de_impresion"  
	    WHERE ("tb_rol_vista_maestro"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
			("tb_rol_vista_maestro"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     	     (( "tb_rol_vista_maestro"."emp_codigo" >=?) AND  
     	     ("tb_rol_vista_maestro"."emp_codigo" <=?) AND  
     	     ("tb_rol_orden_de_impresion"."orden" <> 0 ))   AND
				"tb_rol_orden_de_impresion"."tipo"='D'  and
                  "tb_rol_orden_de_impresion"."cod_informe" =? and
			"tb_rol_vista_maestro"."cia_codigo" = ?
      ),0) +
ISNULL( ( SELECT  SUM( coalesce("tb_rol_vista_maestro_anexo"."valor",0) ) 
       FROM "tb_rol_vista_maestro_anexo", "tb_rol_orden_de_impresion"  
	    WHERE ("tb_rol_vista_maestro_anexo"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
			("tb_rol_vista_maestro_anexo"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     	     (( "tb_rol_vista_maestro_anexo"."emp_codigo" >= ? ) AND  
     	     ("tb_rol_vista_maestro_anexo"."emp_codigo" <= ? ) AND  
     	     ("tb_rol_orden_de_impresion"."orden" <> 0 ))   AND
				"tb_rol_orden_de_impresion"."tipo"='H' and
                  "tb_rol_orden_de_impresion"."cod_informe" =? and
			"tb_rol_vista_maestro_anexo"."cia_codigo" = ?
     ),0)  -                        
	 ISNULL( ( SELECT SUM( coalesce("tb_rol_vista_maestro_anexo"."valor",0) ) 
       FROM   "tb_rol_vista_maestro_anexo", "tb_rol_orden_de_impresion"  
	    WHERE ("tb_rol_vista_maestro_anexo"."cia_codigo" = "tb_rol_orden_de_impresion"."cia_codigo" ) and  
			("tb_rol_vista_maestro_anexo"."esm_campo" = "tb_rol_orden_de_impresion"."esm_codigo" ) and  
     	     (( "tb_rol_vista_maestro_anexo"."emp_codigo" >=?) AND  
     	     ("tb_rol_vista_maestro_anexo"."emp_codigo" <=?) AND  
     	     ("tb_rol_orden_de_impresion"."orden" <> 0 ))   AND
				"tb_rol_orden_de_impresion"."tipo"='D'  and
                  "tb_rol_orden_de_impresion"."cod_informe" =? and
			"tb_rol_vista_maestro_anexo"."cia_codigo" = ? ),0)  AS  TOTAL
