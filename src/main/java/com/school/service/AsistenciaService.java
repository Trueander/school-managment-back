package com.school.service;

import com.school.model.Asistencia;
import com.school.reportDto.AsistenciaReporte;

import java.util.List;

public interface AsistenciaService {

    byte[] generarReporteAsistencia(String tipo, String fecha);
    public List<Asistencia> findByFecha(String fecha);
    public List<Asistencia> findAsistenciaByFechaAula(String fecha, String idAula);
    public AsistenciaReporte obtenerDatosAsistenciaPorDia(String fecha);
}
