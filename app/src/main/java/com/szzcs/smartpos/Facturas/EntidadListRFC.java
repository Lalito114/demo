package com.szzcs.smartpos.Facturas;

public class EntidadListRFC {

    private String razonSocial;
    private String rfc;
    private String email;
    private String idCliente;
    private String idAlias;



    public EntidadListRFC(String razonSocial, String rfc, String email, String idCliente, String idAlias){

        this.razonSocial = razonSocial;
        this.rfc = rfc;
        this.email = email;
        this.idCliente = idCliente;
        this.idAlias = idAlias;

    }

    public String getRazonSocial(){
        return razonSocial;
    }

    public String getRfc(){
        return rfc;
    }

    public String getEmail(){
        return email;
    }

    public String getIdCliente(){
        return idCliente;
    }

    public String getIdAlias(){
        return idAlias;
    }

}
