package com.school.reportDto;

import java.io.Serializable;
import java.util.List;

public class CursoReporte implements Serializable {

    private String nombreCurso;
    private long aprobados;
    private long desaprobados;


    public CursoReporte() {
    }

    public CursoReporte(String nombreCuso, long aprobados, long desaprobados) {
        this.nombreCurso = nombreCuso;
        this.aprobados = aprobados;
        this.desaprobados = desaprobados;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public long getAprobados() {
        return aprobados;
    }

    public void setAprobados(long aprobados) {
        this.aprobados = aprobados;
    }

    public long getDesaprobados() {
        return desaprobados;
    }

    public void setDesaprobados(long desaprobados) {
        this.desaprobados = desaprobados;
    }

    private static final long serialVersionUID = 1L;

}
