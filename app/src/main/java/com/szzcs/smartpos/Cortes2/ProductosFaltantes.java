package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class ProductosFaltantes implements Serializable {
    public String TProducto;
    public String numeroproducto;
    public String internonumero;
    public int cantidad;
    public String preciounitario;
    public String descCorta;
    public String codBarras;
    public int posicion;
    public int diferencia;

    public String getTProducto() {
        return TProducto;
    }

    public void setTProducto(String TProducto) {
        this.TProducto = TProducto;
    }

    public String getNumeroproducto() {
        return numeroproducto;
    }

    public void setNumeroproducto(String numeroproducto) {
        this.numeroproducto = numeroproducto;
    }

    public String getInternonumero() {
        return internonumero;
    }

    public void setInternonumero(String internonumero) {
        this.internonumero = internonumero;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getPreciounitario() {
        return preciounitario;
    }

    public void setPreciounitario(String preciounitario) {
        this.preciounitario = preciounitario;
    }

    public String getDescCorta() {
        return descCorta;
    }

    public void setDescCorta(String descCorta) {
        this.descCorta = descCorta;
    }

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(int diferencia) {
        this.diferencia = diferencia;
    }
}
