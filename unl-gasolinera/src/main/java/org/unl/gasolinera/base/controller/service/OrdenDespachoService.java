package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotEmpty;


@BrowserCallable
@AnonymousAllowed

public class OrdenDespachoService {
    private DaoOrdenDespacho db;
    
    public OrdenDespachoService(){
        db = new DaoOrdenDespacho();
    }

    public void create(@NotEmpty String codigo, float nroGalones, @NonNull Date fecha, float precioTotal, @NotEmpty String  estado, Integer idPrecioGalon, Integer idVehiculo  ) throws Exception {
        if(codigo.trim().length() > 0 && nroGalones > 0 && fecha.toString().length() > 0 && precioTotal > 0 && estado.trim().length() > 0 && idPrecioGalon != null && idVehiculo != null) {
            db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado(EstadoOrdenDespachadoEnum.valueOf(estado));
            db.getObj().setIdVehiculo(idVehiculo);
            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de la banda");
        }
    }

  
    public List<HashMap> listOrdenDespacho(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            OrdenDespacho [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString());
                aux.put("codigo", arreglo[i].getCodigo());
                aux.put("nroGalones", arreglo[i].getNroGalones().toString());
                aux.put("fecha", arreglo[i].getFecha().toString());
                aux.put("precioTotal", String.valueOf(arreglo[i].getPrecioTotal()));
                aux.put("estado", arreglo[i].getEstado().toString());
                aux.put("idPrecioGalon", arreglo[i].getIdPrecioGalon().toString());
                aux.put("idVehiculo", arreglo[i].getIdVehiculo().toString());
                aux.put("idEstacion", arreglo[i].getIdEstacion().toString());
                aux.put("idEstacion", arreglo[i].getIdEstacion().toString());
                lista.add(aux);
            }
        }
        return lista;
    }  

     public List<String> listEstadoOrdenDespacho() {
        List<String> lista = new ArrayList<>();
        for(EstadoOrdenDespachadoEnum r: EstadoOrdenDespachadoEnum.values()) {
            lista.add(r.toString());
        }        
        return lista;
    }
}