package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Estacion;
import org.unl.gasolinera.base.models.EstadoUsoEnum;

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
        if(estadoE.trim().length() > 0 && codigo.trim().length() > 0) {

        }
        db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
        db.getObj().setCodigo(codigo);
        
        if(!db.save()){
            throw new  Exception("No se pudo guardar los datos de Estacion");
        }
    
    
    }

    public void update(@NotEmpty Integer id, @NotEmpty String codigo, @NotEmpty String estadoE) throws Exception{
       if(estadoE.trim().length() > 0 && codigo.trim().length() > 0){
            db.setObj(db.listAll().get(id - 1));
            db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
            db.getObj().setCodigo(codigo);
            
            if(!db.update(id - 1)){
                throw new  Exception("No se pudo guardar los datos de Estacion");
            }
        }    
    }

    public List<String> listEstadoE() {
        List<String> lista = new ArrayList<>();
        for(EstadoUsoEnum r: EstadoUsoEnum.values()) {
            lista.add(r.toString());
        }        
        return lista;
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByEstacion(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }



}