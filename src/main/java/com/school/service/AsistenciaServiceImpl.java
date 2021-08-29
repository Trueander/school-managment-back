package com.school.service;

import com.school.dao.AsistenciaDao;
import com.school.model.Asistencia;
import com.school.reportDto.AsistenciaReporte;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenciaServiceImpl implements AsistenciaService{

    @Autowired
    private AsistenciaDao asistenciaDao;

    @Autowired
    private ClaseService claseService;



    @Override
    public byte[] generarReporteAsistencia(String tipo, String fecha) {
        byte[] data = null;

        AsistenciaReporte asistenciaDTO = obtenerDatosAsistenciaPorDia(fecha);

        if(asistenciaDTO == null) return data;

        List<AsistenciaReporte> lista = new ArrayList<>();
        lista.add(asistenciaDTO);

        try {
            File file = new File(getClass().getClassLoader().getResource("asistencias.jasper").getFile());

            JasperPrint rpt = JasperFillManager.fillReport(file.getPath(), null, new JRBeanCollectionDataSource(lista));

            if(tipo.equals("pdf")){
                data = JasperExportManager.exportReportToPdf(rpt);
            }else{
                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(true);
                config.setIgnoreGraphics(false);

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Exporter exporter =  new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(rpt));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();

                data = out.toByteArray();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public List<Asistencia> findByFecha(String fecha) {
        return asistenciaDao.findByFecha(fecha);
    }

    @Override
    public List<Asistencia> findAsistenciaByFechaAula(String fecha, Long idAula) {
        return asistenciaDao.findAsistenciaByFechaAula(fecha, idAula);
    }

    @Override
    public AsistenciaReporte obtenerDatosAsistenciaPorDia(String fecha){

        List<Asistencia> asistencias = findByFecha(fecha);

        if(asistencias.size() == 0) return null;

        Long puntual = 0L;
        Long tardanza = 0L;
        Long inasistencia = 0L;

        for(Asistencia asistencia: asistencias){
            if(asistencia.getEstado().equals("PUNTUAL")) puntual++;
            if(asistencia.getEstado().equals("TARDANZA")) tardanza++;
            if(asistencia.getEstado().equals("FALTA")) inasistencia++;
        }

        AsistenciaReporte asistenciaDTO = new AsistenciaReporte(fecha,puntual,tardanza,inasistencia);

        return asistenciaDTO;
    }

    @Override
    public List<Asistencia> updateAsistencias(List<Asistencia> asistencias) {
        return asistenciaDao.saveAll(asistencias);
    }


}
