package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class CierreCarrete implements Serializable {

    /// <summary>
    /// Identificador de la entidad Sucursal
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long SucursalId;
    /// <summary>
    /// Identificador de la llave compuesta de la entidad Sucursal
    /// NOTA: Id de la tabla(este NO es computado)
    /// </summary>
//    public virtual Sucursal Sucursal { get; set; }

    public long CierreId;

    /// <summary>
    /// Identificador de la entidad Sucursal
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long CierreSucursalId;


//    public virtual Cierre Cierre { get; set; }

    /// <summary>
    /// Id de la manguera
    /// </summary>
    public long MangueraId;

    /// <summary>
    /// Estacion de la manguera
    /// </summary>
    public long MangueraEstacionId;

    /// <summary>
    /// Relacion con el catalogo de mangueras
    /// </summary>
//    public virtual Manguera Manguera { get; set; }

    /// <summary>
    /// Identificador de la entidad Combustible
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>

    public long CombustibleId;

    /// <summary>
    /// Relacion con la entidad de Combustible del esquema Catalogos
    /// Llave foranea
    /// </summary>
//    public virtual Combustible Combustible { get; set; }


    /// <summary>
    /// Valor inicial del totalizador(desde la ultima lectura)
    /// </summary>
    public double ValorInicial;

    /// <summary>
    /// Valor final del totalizador obtenido del ALVIC
    /// </summary>
    public double ValorFinal;

    public long getSucursalId() {
        return SucursalId;
    }

    public void setSucursalId(long sucursalId) {
        SucursalId = sucursalId;
    }

    public long getCierreId() {
        return CierreId;
    }

    public void setCierreId(long cierreId) {
        CierreId = cierreId;
    }

    public long getCierreSucursalId() {
        return CierreSucursalId;
    }

    public void setCierreSucursalId(long cierreSucursalId) {
        CierreSucursalId = cierreSucursalId;
    }

    public long getMangueraId() {
        return MangueraId;
    }

    public void setMangueraId(long mangueraId) {
        MangueraId = mangueraId;
    }

    public long getMangueraEstacionId() {
        return MangueraEstacionId;
    }

    public void setMangueraEstacionId(long mangueraEstacionId) {
        MangueraEstacionId = mangueraEstacionId;
    }

    public long getCombustibleId() {
        return CombustibleId;
    }

    public void setCombustibleId(long combustibleId) {
        CombustibleId = combustibleId;
    }

    public double getValorInicial() {
        return ValorInicial;
    }

    public void setValorInicial(double valorInicial) {
        ValorInicial = valorInicial;
    }

    public double getValorFinal() {
        return ValorFinal;
    }

    public void setValorFinal(double valorFinal) {
        ValorFinal = valorFinal;
    }
}
