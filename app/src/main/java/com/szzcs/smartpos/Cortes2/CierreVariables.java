package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;
import java.util.List;

public class CierreVariables implements Serializable {
    public DiferenciaPermitida DiferenciaPermitida;
    public List<PrecioFajilla> PrecioFajillas;

    public com.szzcs.smartpos.Cortes2.DiferenciaPermitida getDiferenciaPermitida() {
        return DiferenciaPermitida;
    }

    public void setDiferenciaPermitida(com.szzcs.smartpos.Cortes2.DiferenciaPermitida diferenciaPermitida) {
        DiferenciaPermitida = diferenciaPermitida;
    }

    public List<PrecioFajilla> getPrecioFajillas() {
        return PrecioFajillas;
    }

    public void setPrecioFajillas(List<PrecioFajilla> precioFajillas) {
        PrecioFajillas = precioFajillas;
    }
}
