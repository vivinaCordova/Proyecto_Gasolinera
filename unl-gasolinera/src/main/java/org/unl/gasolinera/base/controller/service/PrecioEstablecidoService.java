package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.github.javaparser.quality.NotNull;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class PrecioEstablecidoService {
    private DaoPrecioEstablecido dp;
    public PrecioEstablecidoService() {
        dp = new DaoPrecioEstablecido();
    }

    public void create(@NonNull Date fecha, @NonNull Date fechaFin, boolean estado, float precio, @NotEmpty String tipoCombustible) throws Exception {
        if (fecha != null && fechaFin != null && precio > 0 && tipoCombustible.trim().length() > 0) {
            
        }
        dp.getObj().setId(dp.listAll().getLength() + 1);
        dp.getObj().setFecha(fecha);
        dp.getObj().setFechaFin(fechaFin);
        dp.getObj().setEstado(estado);
        dp.getObj().setPrecio(precio);
        dp.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf(tipoCombustible));
        
        if (!dp.save()){
            throw new Exception("No se pudo guardar el precio establecido");
        }
    }

    public void update(@NotNull Integer id, @NonNull Date fecha, @NonNull Date fechaFin, boolean estado, float precio, @NotEmpty String tipoCombustible) throws Exception {
        if (fecha != null && fechaFin != null && precio > 0 && tipoCombustible.trim().length() > 0) {  
        }
        
        PrecioEstablecido precioExistente = dp.getById(id);
        if (precioExistente == null) {
            throw new Exception("No se encontró el precio establecido con ID: " + id);
        }

        Integer posicion = null;
        for (int i = 0; i < dp.listAll().getLength(); i++) {
            PrecioEstablecido precioEnPosicion = dp.listAll().get(i);
            if (precioEnPosicion.getId().equals(id)) {
                posicion = i;
                break;
            }
        }
        
        if (posicion == null) {
            throw new Exception("No se pudo encontrar la posición del precio establecido con ID: " + id);
        }
        
        dp.setObj(new PrecioEstablecido());
        dp.getObj().setId(id); // Mantener el ID original
        dp.getObj().setFecha(fecha);
        dp.getObj().setFechaFin(fechaFin);
        dp.getObj().setEstado(estado);
        dp.getObj().setPrecio(precio);
        dp.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf(tipoCombustible));
        
        if(!dp.update(posicion)){
            throw new Exception("No se pudo guardar los datos del Precio Establecido");
        }
    }

    public List<String> listTipoCombustible() {
        List<String> lista = new ArrayList<>();
        for (TipoCombustibleEnum t : TipoCombustibleEnum.values()) {
            lista.add(t.toString());
        }
        return lista;
    }

     @SuppressWarnings("unchecked")
     public List<HashMap<String, Object>> listAll() {
        if(dp.listAll().isEmpty()) {
            return new ArrayList<>();
        } else return (List<HashMap<String, Object>>) (List<?>) Arrays.asList(dp.all().toArray());
        
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(dp.orderByPrecioEstablecido(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = dp.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }




}