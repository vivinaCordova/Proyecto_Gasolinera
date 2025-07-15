package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
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
        dp.setObj(dp.listAll().get(id));
        dp.getObj().setFecha(fecha);
        dp.getObj().setFechaFin(fechaFin);
        dp.getObj().setEstado(estado);
        dp.getObj().setPrecio(precio);
        dp.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf(tipoCombustible));
        
        if(!dp.update(id)){
            throw new  Exception("No se pudo guardar los datos de Estacion");

        }
    }

    public List<String> listTipoCombustible() {
        List<String> lista = new ArrayList<>();
        for (TipoCombustibleEnum t : TipoCombustibleEnum.values()) {
            lista.add(t.toString());
        }
        return lista;
    }

     public List<HashMap> listAll() {
        if(dp.listAll().isEmpty()) {
            return new ArrayList<>();
        } else return Arrays.asList(dp.all().toArray());
        
    }

    public List<HashMap> order(String attribute, Integer type) {
        return Arrays.asList(dp.orderByPrecioEstablecido(type, attribute).toArray());
    }




}