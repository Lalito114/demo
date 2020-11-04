package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;
import java.util.List;

public class Control implements Serializable {

    /// <summary>
    /// Identificador de la entidad Llavero
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long LlaveroId;

    /// <summary>
    /// Numero interno del llavero en base hexadecimal
    /// Ejemplos:
    /// 01C02088
    /// 01C02065
    /// 000A0000
    /// </summary>
    public String NumeroInternoLlavero;

    /// <summary>
    /// Identificador de la entidad Turno
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long TurnoId;

    /// <summary>
    /// Número interno de control del turno en la sucursal
    /// Ejemplos: 1,2,3,etc...
    /// </summary>
    public int NumeroInternoTurno;

    /// <summary>
    /// Identificador de la entidad Isla
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long IslaId;

    /// <summary>
    /// Número interno de control de la isla en la sucursal
    /// Ejemplos: 1,2,3,etc...
    /// </summary>
    public int NumeroInternoIsla;

    /// <summary>
    /// Lista de controles asignados al usuario.
    /// Dicho listado corresponde a los datos existentes en la tabla Islas
    /// </summary>
    public List<Posicion> Posiciones;

    public long getLlaveroId() { return LlaveroId; }

    public void setLlaveroId(long llaveroId) { LlaveroId = llaveroId; }

    public String getNumeroInternoLlavero() { return NumeroInternoLlavero; }

    public void setNumeroInternoLlavero(String numeroInternoLlavero) { NumeroInternoLlavero = numeroInternoLlavero; }

    public long getTurnoId() { return TurnoId; }

    public void setTurnoId(long turnoId) { TurnoId = turnoId; }

    public int getNumeroInternoTurno() { return NumeroInternoTurno; }

    public void setNumeroInternoTurno(int numeroInternoTurno) { NumeroInternoTurno = numeroInternoTurno; }

    public long getIslaId() { return IslaId; }

    public void setIslaId(long islaId) { IslaId = islaId; }

    public int getNumeroInternoIsla() { return NumeroInternoIsla; }

    public void setNumeroInternoIsla(int numeroInternoIsla) { NumeroInternoIsla = numeroInternoIsla; }

    public List<Posicion> getPosiciones() { return Posiciones; }

    public void setPosiciones(List<Posicion> posiciones) { Posiciones = posiciones; }
}
