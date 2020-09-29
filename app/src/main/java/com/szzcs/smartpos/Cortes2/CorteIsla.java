package com.szzcs.smartpos.Cortes2;

public class CorteIsla {
    private String posicionManguera;
    private String tipoCombustible;
    private Double litroselectronicos;
    private String sucursalId;
    private String mangueraId;
    private String turnoId;
    private String turnoAuxiliar;
    private String fechaTrabajo;
    private Double contadorMecanico;

    public CorteIsla(String posicionManguera, String tipoCombustible, Double litroselectronicos,
                     String sucursalId, String mangueraId, String turnoId, String turnoAuxiliar,
                     String fechaTrabajo, Double contadorMecanico)
    {
        this.posicionManguera = posicionManguera;
        this.tipoCombustible = tipoCombustible;
        this.litroselectronicos = litroselectronicos;
        this.sucursalId = sucursalId;
        this.mangueraId = mangueraId;
        this.turnoId = turnoId;
        this.turnoAuxiliar = turnoAuxiliar;
        this.fechaTrabajo = fechaTrabajo;
        this.contadorMecanico = contadorMecanico;

    }

    public String getPosicionManguera(){return posicionManguera;}
    public String getTipoCombustible(){return  tipoCombustible;}
    public Double getLitroselectronicos(){return litroselectronicos;}
    public String getSucursalId(){return sucursalId;}
    public String getMangueraId(){return mangueraId;}
    public String getTurnoId(){return turnoId;}
    public String getTurnoAuxiliar(){return turnoAuxiliar;}
    public String getFechaTrabajo(){return fechaTrabajo;}
    public Double getContadorMecanico(){return contadorMecanico;}


}


