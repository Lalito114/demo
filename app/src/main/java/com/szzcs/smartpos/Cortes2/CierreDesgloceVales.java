package com.szzcs.smartpos.Cortes2;



public class CierreDesgloceVales {

    /// <summary>
    /// Tipo de vale
    /// </summary>
    String TipoVale;

    /// <summary>
    /// Descripción del tipo de vale
    /// </summary>
    String Descripcion;

    /// <summary>
    /// Número de vales por cada tipo
    /// </summary>
    String NumeroVales;

    /// <summary>
    /// Importe de los vales
    /// </summary>
    String ImporteVales;

    /// <summary>
    /// expandible del recyclerView
    /// </summary>
    public boolean expandible;

    /// <summary>
    /// Creamos un constructor.
    /// </summary>
    /// <param name="tipoVale"></param>
    /// <param name="descripcion"></param>
    /// <param name="numeroVales"></param>
    /// <param name="importeVales"></param>

    public CierreDesgloceVales(String tipoVale, String descripcion, String numeroVales, String importeVales) {
        TipoVale = tipoVale;
        Descripcion = descripcion;
        NumeroVales = numeroVales;
        ImporteVales = importeVales;
    }


    /// <summary>
    /// Declaramos los get y set de cada uno de los elementos de esta clase
    /// </summary>

    public String getTipoVale() {
        return TipoVale;
    }

    public void setTipoVale(String tipoVale) {
        TipoVale = tipoVale;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getNumeroVales() {
        return NumeroVales;
    }

    public void setNumeroVales(String numeroVales) {
        NumeroVales = numeroVales;
    }

    public String getImporteVales() {
        return ImporteVales;
    }

    public void setImporteVales(String importeVales) {
        ImporteVales = importeVales;
    }

    public boolean isExpandible() {
        return expandible;
    }

    public void setExpandible(boolean expandible) {
        this.expandible = expandible;
    }



    /// <summary>
    /// En caso de querer ocuparlo los parametros como cadena
    /// </summary>
    @Override
    public String toString() {
        return "CierreDesgloceVales{" +
                "TipoVale='" + TipoVale + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", NumeroVales='" + NumeroVales + '\'' +
                ", ImporteVales='" + ImporteVales + '\'' +
                '}';
    }
}
