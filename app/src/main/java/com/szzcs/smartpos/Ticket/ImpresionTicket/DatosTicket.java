package com.szzcs.smartpos.Ticket.ImpresionTicket;

public class DatosTicket {

    String Cabecero;
    String Detalle;
    DatosTicketPie Pie;
    DatosTicketResultado Resultado;

    public DatosTicketResultado getResultado() {
        return Resultado;
    }

    public void setResultado(DatosTicketResultado resultado) {
        Resultado = resultado;
    }
}
