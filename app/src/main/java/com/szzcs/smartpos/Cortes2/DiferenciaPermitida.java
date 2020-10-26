package com.szzcs.smartpos.Cortes2;

import java.io.Serializable;

public class DiferenciaPermitida implements Serializable {
    /// <summary>
    /// Diferencia permitida de importe vs (litros * precio)
    /// </summary>
    public double ImporteDespacho ;

    /// <summary>
    /// Diferencia permitida(en litros) entre las lecturas electronicas y las lecturas mecanicas
    /// </summary>
    public double LecturasElectronicasMecanicas ;

    /// <summary>
    /// Diferencia permitida(en litros) entre las lecturas electronicas y la sumatoria de los despachos
    /// </summary>
    public double LecturasElectronicasDespachos ;

    public double getImporteDespacho() {
        return ImporteDespacho;
    }

    public void setImporteDespacho(double importeDespacho) {
        ImporteDespacho = importeDespacho;
    }

    public double getLecturasElectronicasMecanicas() {
        return LecturasElectronicasMecanicas;
    }

    public void setLecturasElectronicasMecanicas(double lecturasElectronicasMecanicas) {
        LecturasElectronicasMecanicas = lecturasElectronicasMecanicas;
    }

    public double getLecturasElectronicasDespachos() {
        return LecturasElectronicasDespachos;
    }

    public void setLecturasElectronicasDespachos(double lecturasElectronicasDespachos) {
        LecturasElectronicasDespachos = lecturasElectronicasDespachos;
    }
}
