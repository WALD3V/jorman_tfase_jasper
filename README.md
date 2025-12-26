# Sistema de Generación de Reportes de Rol de Pagos

## Parámetros del Sistema

Los siguientes parámetros se configuran en `Main1.java`:

- **COD_INFORME** = "001" - Código del informe
- **CIA_CODE** = "01" - Código de compañía  
- **TIPO_ROL** = "001" - Código del tipo de rol
- **EMP_INICIAL** = "000" - Empleado inicial
- **EMP_FINAL** = "999999" - Empleado final
- **COD_CEN** = "000" - Centro de costo (000 = todos)

## Queries SQL y sus Parámetros

### 1. QUERY_ROLES_PRINCIPALES.sql
**Parámetros (13):**
1. COD_INFORME
2. COD_INFORME  
3. CIA_CODE
4. TIPO_ROL
5. CIA_CODE
6. COD_INFORME
7. EMP_INICIAL
8. EMP_FINAL
9. TIPO_ROL
10. CIA_CODE
11. COD_INFORME
12. COD_CEN
13. COD_CEN

**Retorna:** Información completa del empleado y rol

### 2. QUERY_RUBROS.sql
**Parámetros (10):**
1. EMP_INICIAL
2. EMP_FINAL
3. COD_INFORME
4. TIPO (fijo: 'R')
5. CIA_CODE
6. EMP_INICIAL
7. EMP_FINAL
8. COD_INFORME
9. TIPO (fijo: 'R')
10. CIA_CODE

**Retorna:** Rubros o conceptos salariales

### 3. QUERY_DET_HAB_DES.sql
**Parámetros:** Ninguno
**Retorna:** Códigos de períodos, fondos, otros y centros

### 4. QUERY_INGRESOS.sql
**Parámetros (10):**
1. EMP_CODIGO (específico)
2. EMP_CODIGO (específico)
3. COD_INFORME
4. TIPO (fijo: 'H')
5. CIA_CODE
6. EMP_CODIGO (específico)
7. EMP_CODIGO (específico)
8. COD_INFORME
9. TIPO (fijo: 'H')
10. CIA_CODE

**Retorna:** Ingresos/haberes del empleado

### 5. QUERY_EGRESOS.sql
**Parámetros (10):**
1. EMP_INICIAL
2. EMP_FINAL
3. TIPO (fijo: 'D')
4. COD_INFORME
5. CIA_CODE
6. EMP_INICIAL
7. EMP_FINAL
8. TIPO (fijo: 'D')
9. COD_INFORME
10. CIA_CODE

**Retorna:** Egresos/descuentos

### 6. QUERY_HAB_TOTSAL_DES.sql
**Parámetros (14):**
1. EMP_INICIAL
2. EMP_FINAL
3. COD_INFORME
4. CIA_CODE
5. EMP_INICIAL
6. EMP_FINAL
7. COD_INFORME
8. CIA_CODE

**Retorna:** Total de haberes/salarios

### 7. QUERY_DESCUENTO_TOTAL.sql
**Parámetros (14):**
1. EMP_INICIAL
2. EMP_FINAL
3. COD_INFORME
4. CIA_CODE
5. EMP_INICIAL
6. EMP_FINAL
7. COD_INFORME
8. CIA_CODE

**Retorna:** Total de descuentos

### 8. QUERY_NETO.sql
**Parámetros (24):**
1-6. EMP_INICIAL, EMP_FINAL, COD_INFORME, CIA_CODE (haberes principales)
7-12. EMP_INICIAL, EMP_FINAL, COD_INFORME, CIA_CODE (descuentos principales)
13-18. EMP_INICIAL, EMP_FINAL, COD_INFORME, CIA_CODE (haberes anexos)
19-24. EMP_INICIAL, EMP_FINAL, COD_INFORME, CIA_CODE (descuentos anexos)

**Retorna:** Neto a pagar (haberes - descuentos)

## Tipos de Movimientos

- **'H'** = Haberes/Ingresos
- **'D'** = Descuentos/Egresos  
- **'R'** = Rubros/Conceptos

## Ejecución

El sistema ejecuta las queries en secuencia para cada empleado encontrado y genera un PDF individual con toda la información del rol de pagos.
