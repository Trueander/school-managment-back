package com.school.reportDto;

import java.io.Serializable;

public class CursoReporteSerie implements Serializable {

    private Integer aprobados;
    private Integer desaprobados;

    public CursoReporteSerie() {
    }

    public CursoReporteSerie(Integer aprobados, Integer desaprobados) {
        this.aprobados = aprobados;
        this.desaprobados = desaprobados;
    }

    public Integer getAprobados() {
        return aprobados;
    }

    public void setAprobados(Integer aprobados) {
        this.aprobados = aprobados;
    }

    public Integer getDesaprobados() {
        return desaprobados;
    }

    public void setDesaprobados(Integer desaprobados) {
        this.desaprobados = desaprobados;
    }
}
