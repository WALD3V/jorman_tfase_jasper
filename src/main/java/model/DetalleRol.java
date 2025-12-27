/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Jasper.model;



/**
 *
 * @author PC
 */
public class DetalleRol {
    private String detalleCodigo;
    private String nombre;
    private String tipo;
    private String referencia;
    private double valor;
    private double saldo;

    // Constructor
    public DetalleRol(String detalleCodigo, String nombre, String tipo, String referencia, double valor, double saldo) {
        this.detalleCodigo = detalleCodigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.referencia = referencia;
        this.valor = valor;
        this.saldo = saldo;
    }

    DetalleRol(String periodoAnio, String periodoMes, String fechaCorte, String codigo, String nombres, String apellidos, String funcion, String tipo, String nombre, double valor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

    // Getters
    public String getDetalleCodigo() {
        return detalleCodigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public double getValor() {
        return valor;
    }

    public double getSaldo() {
        return saldo;
    }
}



