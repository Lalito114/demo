package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Cierre implements Serializable {

    /// <summary>
    /// Identificador de la Sucursal
    /// </summary>
    public long SucursalId;

    /// <summary>
    /// Identificador del turno actual
    /// </summary>
    public long TurnoId;

    /// <summary>
    /// es el idcierre del cabecero
    /// </summary>
    public long Id;

    /// <summary>
    /// Representa la ultima fecha de trabajo
    /// </summary>
    public String  FechaTrabajo ;

    /// <summary>
    /// Identificador del turno
    /// </summary>
    public long TurnoSucursalId;

    /// <summary>
    /// Identificador de las Transacciones
    /// </summary>
    public long Transacciones;

    /// <summary>
    /// Identificador de la Venta Total
    /// </summary>
    public double TotalVenta;

    /// <summary>
    /// Identificador del IVA
    /// </summary>
    public double TotalIva;

    /// <summary>
    /// Identificador del IEPS
    /// </summary>
    public double TotalIeps;

    /// <summary>
    /// Identificador si el Cierre esta Completado
    /// </summary>
    public boolean Completado;

    /// <summary>
    /// Identificador de la Isla
    /// </summary>
    public long IslaId;
    /// <summary>
    /// Identificador
    /// </summary>
    public long IslaEstacionId;

    public  CierreVariables Variables;

    public long getSucursalId() {return SucursalId;}

    public void setSucursalId(long sucursalId) {SucursalId = sucursalId;}

    public long getTurnoId() {return TurnoId;}

    public void setTurnoId(long turnoId) {TurnoId = turnoId;}

    public long getId() {return Id;}

    public void setId(long id) {Id = id;}

    public long getTurnoSucursalId() { return TurnoSucursalId; }

    public void setTurnoSucursalId(long turnoSucursalId) { TurnoSucursalId = turnoSucursalId; }

    public long getTransacciones() { return Transacciones; }

    public void setTransacciones(long transacciones) { Transacciones = transacciones; }

    public double getTotalVenta() { return TotalVenta; }

    public void setTotalVenta(double totalVenta) { TotalVenta = totalVenta; }

    public double getTotalIva() { return TotalIva; }

    public void setTotalIva(double totalIva) { TotalIva = totalIva; }

    public double getTotalIeps() { return TotalIeps; }

    public void setTotalIeps(double totalIeps) { TotalIeps = totalIeps; }

    public boolean isCompletado() { return Completado; }

    public void setCompletado(boolean completado) { Completado = completado; }

    public long getIslaId() { return IslaId; }

    public void setIslaId(long islaId) { IslaId = islaId; }

    public long getIslaEstacionId() { return IslaEstacionId; }

    public void setIslaEstacionId(long islaEstacionId) { IslaEstacionId = islaEstacionId; }

    public CierreVariables getVariables() {return Variables;}

    public void setVariables(CierreVariables variables) { Variables = variables;}

    public String getFechaTrabajo() {
        return FechaTrabajo;
    }

    public void setFechaTrabajo(String fechaTrabajo) {
        FechaTrabajo = fechaTrabajo;
    }
}
