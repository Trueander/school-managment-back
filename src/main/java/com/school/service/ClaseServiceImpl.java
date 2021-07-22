package com.school.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.school.dao.MaterialDao;
import com.school.dao.NotaDao;
import com.school.model.Aula;
import com.school.model.Nota;
import com.school.reportDto.CursoReporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.dao.AulaDao;
import com.school.dao.ClaseDao;
import com.school.model.Clase;

@Service
public class ClaseServiceImpl implements ClaseService{

	@Autowired
	private ClaseDao claseDao;
	
	@Autowired
	private AulaDao aulaDao;

	@Autowired
	private NotaDao notaDao;

	@Override
	@Transactional
	public Clase save(Clase clase) {
		// TODO Auto-generated method stub
		clase.setAula(aulaDao.findById(clase.getAula().getId()).get());
		clase.getFrecuencias().stream().forEach(f -> f.setClase(clase));
		return claseDao.save(clase);
	}
	

	@Override
	@Transactional
	public Clase update(Clase clase) {
		// TODO Auto-generated method stub
		return claseDao.save(clase);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Clase> getClaseById(Long id) {
		// TODO Auto-generated method stub
		return claseDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Clase> findAll() {
		// TODO Auto-generated method stub
		return (List<Clase>) claseDao.findAll();
	}

	@Override
	@Transactional
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return getClaseById(id).map(c -> {
			claseDao.deleteById(id);
			return true;
		}).orElse(false);
	}


	@Override
	public List<CursoReporte> getCursoReporte(Long idCurso, Long idGrado) {

		List<Aula> aulasPorGrado = null;
		List<Aula> aulasGeneral = null;
		List<CursoReporte> cursoReporteList = new ArrayList<>();

		if(idGrado == 0){
			aulasGeneral = (List<Aula>) aulaDao.findAll();

			cursoReporteList = iterarAulas(aulasGeneral, idCurso);

			return cursoReporteList;

//			System.out.println(aulasGeneral.size());
//			for(Aula aula: aulasGeneral){
//				long aprobados = 0;
//				long desaprobados = 0;
//				Clase clase = claseDao.findClasePorAulaYCurso(idCurso ,aula.getId());
//				if(clase != null){
//					List<Nota> notas = notaDao.notasPorAulaYCurso(idCurso, aula.getId());
//
//					if(notas.size() == 0){
//						return cursoReporteList;
//					}
//					aprobados = notas.stream().filter(n -> n.getNota_bim1() > 12).count();
//					desaprobados = notas.size() - aprobados;
//					CursoReporte cursoReporte = new CursoReporte(notas.get(0).getEstudiante().getAulaEstudiante().getNombre() + notas.get(0).getEstudiante().getAulaEstudiante().getSeccion(), aprobados, desaprobados);
//					cursoReporteList.add(cursoReporte);
//
//
//				}
//			}
		}
		aulasPorGrado = aulaDao.findAulaPorGrado(idGrado);
		cursoReporteList = iterarAulas(aulasPorGrado, idCurso);


		return cursoReporteList;
	}

	private List<CursoReporte> iterarAulas(List<Aula> aulas, Long idCurso){
		List<CursoReporte> cursoReporteList = new ArrayList<>();

		for(Aula aula: aulas){
			long aprobados = 0;
			long desaprobados = 0;
			Clase clase = claseDao.findClasePorAulaYCurso(idCurso ,aula.getId());
			if(clase != null){
				List<Nota> notas = notaDao.notasPorAulaYCurso(idCurso, aula.getId());

				if(notas.size() == 0){
					return cursoReporteList;
				}
				aprobados = notas.stream().filter(n -> n.getNota_bim1() > 12).count();
				desaprobados = notas.size() - aprobados;
				CursoReporte cursoReporte = new CursoReporte(notas.get(0).getEstudiante().getAulaEstudiante().getNombre() + notas.get(0).getEstudiante().getAulaEstudiante().getSeccion(), aprobados, desaprobados);
				cursoReporteList.add(cursoReporte);


			}
		}

		return cursoReporteList;
	}

}
