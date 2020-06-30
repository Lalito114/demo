package com.szzcs.smartpos.Ticket;

import java.io.Serializable;

public class Entidad implements Serializable {
    private int imgFoto;
    private String titulo;
    private String contenido;
    //private int identifica;

    public Entidad (int imgFoto, String titulo, String contenido){
        this.imgFoto = imgFoto;
        this.titulo = titulo;
        this.contenido = contenido;
        //this.identifica= identifica;
    }
    public int getImgFoto(){ return imgFoto;}
    public String getTitulo(){ return titulo;}
    public String getContenido(){ return contenido;}
    //public int getIdentifica(){ return identifica;}
}
