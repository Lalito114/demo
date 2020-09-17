package com.szzcs.smartpos.TanqueLleno;

public class ConvertirdorLitrosPesos {

    public double CalcularLitros(double dinero, double precio){

        double costofinal = dinero / precio;

        return costofinal;
    }
}
