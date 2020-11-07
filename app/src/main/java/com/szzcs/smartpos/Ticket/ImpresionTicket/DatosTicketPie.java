package com.szzcs.smartpos.Ticket.ImpresionTicket;

import java.util.List;

public class DatosTicketPie {
    String NombreTarjeta;
    String NumeroTrjeta;
    String Odometro;
    String Placas;
    String Puntos;
    String Saldo;
    List<DatosTicketPieMensaje> Mensaje;

    public List<DatosTicketPieMensaje> getMensaje() {
        return Mensaje;
    }

    public void setMensaje(List<DatosTicketPieMensaje> mensaje) {
        Mensaje = mensaje;
    }

    public String getNombreTarjeta() {
        return NombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        NombreTarjeta = nombreTarjeta;
    }

    public String getNumeroTrjeta() {
        return NumeroTrjeta;
    }

    public void setNumeroTrjeta(String numeroTrjeta) {
        NumeroTrjeta = numeroTrjeta;
    }

    public String getOdometro() {
        return Odometro;
    }

    public void setOdometro(String odometro) {
        Odometro = odometro;
    }

    public String getPlacas() {
        return Placas;
    }

    public void setPlacas(String placas) {
        Placas = placas;
    }

    public String getPuntos() {
        return Puntos;
    }

    public void setPuntos(String puntos) {
        Puntos = puntos;
    }

    public String getSaldo() {
        return Saldo;
    }

    public void setSaldo(String saldo) {
        Saldo = saldo;
    }
}
