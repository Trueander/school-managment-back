package com.school.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.school.dao.MaterialDao;
import com.school.model.Material;
import com.school.model.Nota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.school.model.Clase;
import com.school.service.ClaseService;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/clases")
public class ClaseController {

	@Autowired
	private ClaseService claseService;

	@Autowired
	private MaterialDao materialDao;
	
	@GetMapping
	public ResponseEntity<List<Clase>> getAllClases(){
		return new ResponseEntity<List<Clase>>(claseService.findAll(), HttpStatus.OK);
	}

	@PostMapping("/crearNota")
	public ResponseEntity<Nota> crearNota(@RequestBody Nota nota){
		return new ResponseEntity<Nota>(nota, HttpStatus.CREATED);
	}


	@GetMapping("/{id}")
	public ResponseEntity<?> getClase(@PathVariable Long id){
		
		Optional<Clase> clase = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			clase = claseService.getClaseById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar la clase en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(clase.isEmpty()) {
			response.put("mensaje", "La clase con el ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Clase>(clase.get() ,HttpStatus.OK);
	}
	
	
	@PostMapping("/crear")
	public ResponseEntity<?> saveClase(@Valid @RequestBody Clase clase, BindingResult result){
		
		Clase claseNueva = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(er -> "El campo '" + er.getField() + "' " + er.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		try {
			claseNueva = claseService.save(clase);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar la clase en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("clase", claseNueva);
		response.put("mensaje", "La clase ha sido creada con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	

	@PutMapping("/{id}")
	public ResponseEntity<?> updateClase(@Valid @RequestBody Clase clase, BindingResult result, @PathVariable Long id){
		
		Clase claseActual = claseService.getClaseById(id).orElse(null);
		Clase claseActualizada = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(er -> "El campo '" + er.getField() + "' " + er.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		
		if(claseActual == null) {
			response.put("mensaje", "Error: No se pudo editar la clase con el ID: ".concat(id.toString().concat(" no existe en la base de datos.")));
		}
		
		try {
			claseActual.setAula(clase.getAula());
			claseActual.setEmpleado(clase.getEmpleado());
			claseActual.setCurso(clase.getCurso());
			claseActual.setFrecuencias(clase.getFrecuencias());
			claseActual.setMateriales(clase.getMateriales());
			claseActualizada = claseService.update(claseActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar la clase en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("clase", claseActualizada);
		response.put("mensaje", "La clase ha sido actualizada con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteClase(@PathVariable Long id){
		
		Clase clase = claseService.getClaseById(id).orElse(null);
		Map<String, Object> response = new HashMap<>();
		
		if(clase == null) {
			response.put("mensaje", "Error: No se pudo eliminar la clase con el ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			clase.getMateriales().forEach( c -> {
				String archivoPDFparaBorrar = c.getArchivo();

				if(archivoPDFparaBorrar != null && archivoPDFparaBorrar.length() > 0){
					Path rutaArchivoPDF = Paths.get("uploads").resolve(archivoPDFparaBorrar).toAbsolutePath();
					File archivoPDFparaborrar = rutaArchivoPDF.toFile();
					if(archivoPDFparaborrar.exists() && archivoPDFparaborrar.canRead()){
						archivoPDFparaborrar.delete();
					}
				}
			});
			claseService.delete(id);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la clase en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La clase ha sido eliminada con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@PostMapping("/uploads")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("idClase") String idAula, @RequestParam("nombreFile") String nombreFile){
		Map<String, Object> response = new HashMap<>();

		Clase clase = claseService.getClaseById(Long.parseLong(idAula)).orElse(null);

		if(!archivo.isEmpty()){
			String nombreArchivoPDF = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivoPDF).toAbsolutePath();

			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir el archivo "+nombreArchivoPDF);
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			Material material = new Material();
			material.setNombre(nombreFile);
			material.setArchivo(nombreArchivoPDF);
			clase.getMateriales().add(material);

			claseService.update(clase);

			response.put("clase", clase);
			response.put("mensaje", "Has subido correctamente el archivo " + nombreArchivoPDF);

		}


		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/eliminarMaterial")
	public ResponseEntity<?> deleteArchivo(@RequestParam("idClase") String idClase, @RequestParam("idMaterial") String idMaterial){

		Map<String, Object> response = new HashMap<>();
		Clase clase = claseService.getClaseById(Long.parseLong(idClase)).orElse(null);

		if(clase == null){
			response.put("mensaje", "La clase con el id "+idClase+" no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		Material materialEncontrado = clase.getMateriales().stream().filter(material -> Long.parseLong(idMaterial) == material.getId())
							 .findAny()
							 .orElse(null);

		if(materialEncontrado == null){
			response.put("mensaje", "El material con el id "+idMaterial+" no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		String archivoPDFparaBorrar = materialEncontrado.getArchivo();

		if(archivoPDFparaBorrar == null || archivoPDFparaBorrar.length() == 0 || archivoPDFparaBorrar.length() < 0){
			response.put("mensaje", "Hubo un error al eliminar el material "+archivoPDFparaBorrar);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Path rutaArchivoPDF = Paths.get("uploads").resolve(archivoPDFparaBorrar).toAbsolutePath();
		File archivoPDFparaborrar = rutaArchivoPDF.toFile();
		if(!archivoPDFparaborrar.exists() || !archivoPDFparaborrar.canRead()){
			response.put("mensaje", "Error al eliminar "+archivoPDFparaBorrar+", el archivo no existe o no se puede leer");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try{

			clase.getMateriales().remove(materialEncontrado);

			claseService.update(clase);
			materialDao.deleteById(materialEncontrado.getId());
			archivoPDFparaborrar.delete();
		}catch (DataAccessException e){
			response.put("mensaje", "Error al eliminar el archivo en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}



		response.put("mensaje", "El archivo se eliminó con éxito.");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/uploads/pdf/{nombrePDF:.+}")
	public ResponseEntity<Resource> verArchivoPDF(@PathVariable String nombrePDF){

		Path rutaArchivoPDF = Paths.get("uploads").resolve(nombrePDF).toAbsolutePath();
		Resource recurso = null;

		try {
			recurso = new UrlResource(rutaArchivoPDF.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if(!recurso.exists() && !recurso.isReadable()){
			throw new RuntimeException("Error no se pudo cargar el PDF "+nombrePDF);
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
