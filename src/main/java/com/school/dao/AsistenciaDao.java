package com.school.dao;

import com.school.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaDao extends JpaRepository<Asistencia, Long> {

    public List<Asistencia> findByFecha(String fecha);

}
