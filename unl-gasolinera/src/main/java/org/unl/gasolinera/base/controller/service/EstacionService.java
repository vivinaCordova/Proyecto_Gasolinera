package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.models.EstadoUsoEnum;

import com.github.javaparser.quality.NotNull;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;



@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class EstacionService {
    private DaoEstacion db;
    public EstacionService() {
        db = new DaoEstacion();
    }

    public void create(@NotEmpty String codigo, @NotEmpty String estadoE) throws Exception{
        if(estadoE.trim().length() > 0 && codigo.trim().length() > 0){

        }
        db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
        db.getObj().setCodigo(codigo);
           

        if(!db.save()){
            throw new  Exception("No se pudo guardar los datos de Estacion");

        }
    }

    public void update(@NotNull Integer id, @NotEmpty String codigo, @NotEmpty String estadoE) throws Exception{
       if(estadoE.trim().length() > 0 && codigo.trim().length() > 0){

       }
        db.setObj(db.listAll().get(id));
        db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
        db.getObj().setCodigo(codigo);
            
        if(!db.update(id)){
            throw new  Exception("No se pudo guardar los datos de Estacion");

        }
    }

    public List<String> listEstadoE() {
        List<String> lista = new ArrayList<>();
        for(EstadoUsoEnum r: EstadoUsoEnum.values()) {
            lista.add(r.toString());
        }        
        return lista;
    }

    public List<HashMap> listAll() {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) {
        return Arrays.asList(db.orderbyEstacion(type, attribute).toArray());
    }










}