package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;
import java.util.List;

public class AccesoUsuario implements Serializable {

    public long SucursalEmpleadoId;

    /// <summary>
    /// Nombres del usuario
    /// Ejemplo: Porfirio Antonio
    /// </summary>
    public String Nombre;

    /// <summary>
    /// Apellido Paterno
    /// Ejemplo: Salinas
    /// </summary>
    public String ApellidoPaterno;

    /// <summary>
    /// Apellido Materno
    /// Ejemplo: Reyes
    /// </summary>
    public String ApellidoMaterno;


    /// <summary>
    /// Propiedad de solo lectura para obtener el nombre completo del usuario
    /// </summary>
    public String NombreCompleto;

    /// <summary>
    /// Clave para accesar a aplicaciones
    /// Ejemplo: ******
    /// </summary>
    public String Clave;

    /// <summary>
    /// Identificador de la entidad Rol
    /// NOTA: Id de la tabla(este es computado)
    /// </summary>
    public long RolId;

    /// <summary>
    /// Numero interno del rol del usuario
    /// </summary>
    public int NumeroInternoRol;

    /// <summary>
    /// Nombre del puesto
    /// Ejemplos:
    /// *Gerente
    /// *Despachador
    /// *Jefe de isla
    /// *Sistema
    /// *etc...
    /// </summary>
    public String DescripcionRol;

    /// <summary>
    /// Lista de controles asignados al usuario.
    /// Dicho listado corresponde a los datos existentes en la tabla EstacionControles
    /// </summary>
    public List<Control> Controles;

    public long getSucursalEmpleadoId() { return SucursalEmpleadoId; }

    public void setSucursalEmpleadoId(long sucursalEmpleadoId) { SucursalEmpleadoId = sucursalEmpleadoId; }

    public String getNombre() { return Nombre; }

    public void setNombre(String nombre) { Nombre = nombre; }

    public String getApellidoPaterno() { return ApellidoPaterno; }

    public void setApellidoPaterno(String apellidoPaterno) { ApellidoPaterno = apellidoPaterno; }


    public String getApellidoMaterno() { return ApellidoMaterno; }

    public void setApellidoMaterno(String apellidoMaterno) { ApellidoMaterno = apellidoMaterno; }

    public String getNombreCompleto() { return NombreCompleto; }

    public void setNombreCompleto(String nombreCompleto) { NombreCompleto = nombreCompleto; }

    public String getClave() { return Clave; }

    public void setClave(String clave) { Clave = clave; }

    public long getRolId() { return RolId; }

    public void setRolId(long rolId) { RolId = rolId; }

    public int getNumeroInternoRol() { return NumeroInternoRol; }

    public void setNumeroInternoRol(int numeroInternoRol) { NumeroInternoRol = numeroInternoRol; }

    public String getDescripcionRol() { return DescripcionRol; }

    public void setDescripcionRol(String descripcionRol) { DescripcionRol = descripcionRol; }

    public List<Control> getControles() { return Controles; }

    public void setControles(List<Control> controles) { Controles = controles; }
}
