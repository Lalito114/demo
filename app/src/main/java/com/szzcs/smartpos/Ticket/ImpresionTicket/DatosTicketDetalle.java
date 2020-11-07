package com.szzcs.smartpos.Ticket.ImpresionTicket;

import java.util.List;

public class DatosTicketDetalle {

    String Clave;
    int Desp;
    int EmpleadoImpresionId;
    int EmpleadoReimpresionId;
    int EmpleadoVentaId;
    String FormaPago;
    boolean ImprimeSaldo;
    String IVA;
    String NoRastreo;
    String NoRecibo;
    String NoTransaccion;
    int PosCarga;
    String Reimpresion;
    String Rfc;
    String Total;
    String TotalTexto;
    int Vend;
    DatosTicketDetalleFormaPagoTicket FormaPagoTicket;
    List<DatosTicketDetalleProductos> Productos;
}
