package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class RespuestaApi<T> implements Serializable{

    /// <summary>
    /// bandera para validar si la respuesta de la api fue correcta
    /// </summary>
    public boolean Correcto;
    /// <summary>
    /// Mensaje en caso de algun error
    /// </summary>
    public String Mensaje;
    /// <summary>
    /// Objeto que sera enviado por la Api
    /// </summary>
    private final T ObjetoRespuesta;

    public RespuestaApi(T objetoRespuesta) {
        ObjetoRespuesta = objetoRespuesta;
    }

    public T getObjetoRespuesta() {
        return ObjetoRespuesta;
    }
}
