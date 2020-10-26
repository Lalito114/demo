package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class TanqueLlenoPrueba implements Serializable {
    /// <summary>
    /// Estacion de donde se envia la peticion
    /// </summary>
    public long EstacionId ;

    /// <summary>
    /// Posicion de carga desde la que se desea ingresar la tarjeta
    /// </summary>
    public int PosicionCarga;

    /// <summary>
    /// tarjeta recibida por la hand held
    /// </summary>
    public String TarjetaCliente;

    public long getEstacionId() {
        return EstacionId;
    }

    public void setEstacionId(long estacionId) {
        EstacionId = estacionId;
    }

    public int getPosicionCarga() {
        return PosicionCarga;
    }

    public void setPosicionCarga(int posicionCarga) {
        PosicionCarga = posicionCarga;
    }

    public String getTarjetaCliente() {
        return TarjetaCliente;
    }

    public void setTarjetaCliente(String tarjetaCliente) {
        TarjetaCliente = tarjetaCliente;
    }
}
