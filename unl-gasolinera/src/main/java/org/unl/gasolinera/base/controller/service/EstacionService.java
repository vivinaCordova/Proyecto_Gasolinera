package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
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
        if(estadoE.trim().length() > 0 && codigo.trim().length() > 0)
           db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
           db.getObj().setCodigo(codigo);
           

            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de Estacion");
    }

    public void update(@NotEmpty Integer id, @NotEmpty String codigo, @NotEmpty String estadoE) throws Exception{
       if(estadoE.trim().length() > 0 && codigo.trim().length() > 0)
            db.setObj(db.listAll().get(id - 1));
            db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
            db.getObj().setCodigo(codigo);
            
            if(!db.update(id - 1))
                throw new  Exception("No se pudo guardar los datos de Estacion");
    }

    
    public List<HashMap> listEstacion(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            Estacion [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString(i));                
                aux.put("estado", arreglo[i].getEstadoE().toString());
                aux.put("codigo", arreglo[i].getCodigo());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<String> listEstadoE() {
        List<String> lista = new ArrayList<>();
        for(EstadoUsoEnum r: EstadoUsoEnum.values()) {
            lista.add(r.toString());
        }        
        return lista;
    }
}