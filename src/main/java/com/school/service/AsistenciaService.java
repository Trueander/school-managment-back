package com.school.service;

import com.school.model.Asistencia;
import com.school.reportDto.AsistenciaReporte;

import java.util.List;

public interface AsistenciaService {

    byte[] generarReporte(String tipo, String fecha);
    public List<Asistencia> findByFecha(String fecha);
    public AsistenciaReporte obtenerDatosAsistenciaPorDia(String fecha);
}
