package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;
import java.util.List;

public class Posicion implements Serializable {

    /// <summary>
    /// Identificador de la entidad PosicionCarga
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long PosicionCargaId;

    /// <summary>
    /// Número interno de control de la posicion de carga
    /// Ejemplos: 1,2,3,etc...
    /// </summary>
    public int NumeroInterno;

    /// <summary>
    /// Bandera que indica si la posicion de carga se encuentra disponible
    /// </summary>
    public boolean Disponible;

    /// <summary>
    /// Bandera que indica si la posicion de carga ya se encuentra en pendiente de cobro
    /// </summary>
    public boolean PendienteCobro;

    /// <summary>
    /// Operacion que se esta realizando en la posicion de carga (tanque lleno, puntada...)
    /// </summary>
    public int Operativa;

    /// <summary>
    /// Descripcion de la operativa en la que se encuentra la posicion de carga
    /// </summary>
    public String DescripcionOperativa;

    /// <summary>
    /// Descripcion del estado de la posicion de carga
    /// </summary>
    public String Descripcion;

    /// <summary>
    /// Bandera que indica si la posicion de carga tiene permitido cargar productos
    /// </summary>
    public boolean PermiteProductos;

    /// <summary>
    /// Lista de controles asignados al usuario.
    /// Dicho listado corresponde a los datos existentes en la tabla Islas
    /// </summary>
    public List<MangueraPorPosicion> Mangueras;

    public long getPosicionCargaId() { return PosicionCargaId; }

    public void setPosicionCargaId(long posicionCargaId) { PosicionCargaId = posicionCargaId; }

    public int getNumeroInterno() { return NumeroInterno; }

    public void setNumeroInterno(int numeroInterno) { NumeroInterno = numeroInterno; }

    public boolean isDisponible() { return Disponible; }

    public void setDisponible(boolean disponible) { Disponible = disponible; }

    public boolean isPendienteCobro() { return PendienteCobro; }

    public void setPendienteCobro(boolean pendienteCobro) { PendienteCobro = pendienteCobro; }

    public int getOperativa() { return Operativa; }

    public void setOperativa(int operativa) { Operativa = operativa; }

    public String getDescripcionOperativa() { return DescripcionOperativa; }

    public void setDescripcionOperativa(String descripcionOperativa) { DescripcionOperativa = descripcionOperativa; }

    public String getDescripcion() { return Descripcion; }

    public void setDescripcion(String descripcion) { Descripcion = descripcion; }

    public boolean isPermiteProductos() { return PermiteProductos; }

    public void setPermiteProductos(boolean permiteProductos) { PermiteProductos = permiteProductos; }

    public List<MangueraPorPosicion> getMangueras() { return Mangueras; }

    public void setMangueras(List<MangueraPorPosicion> mangueras) { Mangueras = mangueras; }
}
