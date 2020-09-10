package com.szzcs.smartpos.Productos;

public class singleRow {
    private String Titulo;
    private String Subtitulo;

    public singleRow(String titulo, String subtitulo) {
        Titulo = titulo;
        Subtitulo = subtitulo;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getSubtitulo() {
        return Subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        Subtitulo = subtitulo;
    }
}
