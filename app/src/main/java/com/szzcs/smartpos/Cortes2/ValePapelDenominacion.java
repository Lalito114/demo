package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class ValePapelDenominacion implements Serializable {

    /// <summary>
    /// Tipo de vale asociado a la denominacion
    /// </summary>
    long TipoValePapelId;

    /// <summary>
    /// Cantidad de vales
    /// </summary>
    int Cantidad;

    /// <summary>
    /// Importe de la denominacion
    /// </summary>
    double Importe;

    /// <summary>
    /// Total de vales por su denominacion
    /// </summary>
    public double Total ;

    /// <summary>
    /// Posicion en la cual se le da click al listview
    /// </summary>
    int Posicion;

    /// <summary>
    /// Nombre del tipo de vale
    /// </summary>
    String NombreVale;

    /// <summary>
    /// Denominacion de la moneda de la fajilla
    /// </summary>
    public double Denominacion ;

    /// <summary>
    /// //Declaramos los get y set de cada uno de los elementos de esta clase
    /// </summary>
    public String getNombreVale() {return NombreVale;}

    public void setNombreVale(String nombreVale) {NombreVale = nombreVale;}

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public long getTipoValePapelId() {
        return TipoValePapelId;
    }

    public void setTipoValePapelId(long tipoValePapelId) {
        TipoValePapelId = tipoValePapelId;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public double getImporte() {
        return Importe;
    }

    public void setImporte(double importe) {
        Importe = importe;
    }

    public int getPosicion() {
        return Posicion;
    }

    public void setPosicion(int posicion) {
        this.Posicion = posicion;
    }

    public double getDenominacion() {
        return Denominacion;
    }

    public void setDenominacion(double denominacion) {
        Denominacion = denominacion;
    }
}
