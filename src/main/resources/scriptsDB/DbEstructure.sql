-- Tabla de cabecera de Rol General
CREATE TABLE RolGeneral_cab (
    cia_codigo CHAR(2),
    Periodo_anio INT,
    Periodo_mes INT,
    Fecha_corte DATE,
    EMP_CODIGO CHAR(15),
    EMP_NOMBRES CHAR(50),
    EMP_APELLIDOS CHAR(50),
    EMP_FUNCION VARCHAR(80),
    PRIMARY KEY (cia_codigo, Periodo_anio, Periodo_mes, EMP_CODIGO)
);

-- Tabla de detalles de Rol General
CREATE TABLE RolGeneral_Det (
    detalle_codigo VARCHAR(10),  -- Cambié 'codigo' por 'detalle_codigo'
    nombre VARCHAR(80),
    tipo CHAR(1) CHECK (tipo IN ('I', 'E', 'D')), -- I: Ingreso, E: Egreso, D: Desglose valores
    referencia VARCHAR(30),
    valor DECIMAL(18,2),
    saldo DECIMAL(18,2),
    cia_codigo CHAR(2),
    Periodo_anio INT,
    Periodo_mes INT,
    EMP_CODIGO CHAR(15),  -- Asociación directa al código de empleado
    FOREIGN KEY (cia_codigo, Periodo_anio, Periodo_mes, EMP_CODIGO)
        REFERENCES RolGeneral_cab (cia_codigo, Periodo_anio, Periodo_mes, EMP_CODIGO)
        ON DELETE CASCADE
);
