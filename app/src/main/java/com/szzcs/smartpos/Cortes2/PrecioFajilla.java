package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class PrecioFajilla implements Serializable {
    /// <summary>
    /// Identificador de la entidad Sucursal
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long SucursalId ;

    /// <summary>
    /// Identificador del tipo de fajilla asociada a la sucursal
    /// </summary>
    public long TipoFajillaId ;

    /// <summary>
    /// Relacion con la entidad de TipoFajilla del esquema Catalogos
    /// </summary>
    public  TipoFajilla TipoFajilla;

    /// <summary>
    /// Precio de la fajilla en la sucursal
    /// </summary>
    public int Precio ;

    public long getSucursalId() {
        return SucursalId;
    }

    public void setSucursalId(long sucursalId) {
        SucursalId = sucursalId;
    }

    public long getTipoFajillaId() {
        return TipoFajillaId;
    }

    public void setTipoFajillaId(long tipoFajillaId) {
        TipoFajillaId = tipoFajillaId;
    }

    public com.szzcs.smartpos.Cortes2.TipoFajilla getTipoFajilla() {
        return TipoFajilla;
    }

    public void setTipoFajilla(com.szzcs.smartpos.Cortes2.TipoFajilla tipoFajilla) {
        TipoFajilla = tipoFajilla;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }
}
