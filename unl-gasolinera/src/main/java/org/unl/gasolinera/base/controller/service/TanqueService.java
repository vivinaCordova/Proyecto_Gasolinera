package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;
import org.unl.gasolinera.base.models.Tanque;

@BrowserCallable
@AnonymousAllowed
public class TanqueService {
    private DaoTanque db;

    public TanqueService() {
        db = new DaoTanque();
    }

    public List<HashMap> listAll() throws Exception{
        return Arrays.asList(db.all().toArray());
    }
    public List<Tanque> listAlla(){
        return (List<Tanque>)Arrays.asList(db.listAll().toArray());
    }
    public List<HashMap> order(String attribute, Integer type ) throws Exception {
        return Arrays.asList(db.orderByTanque(type, attribute).toArray());
    }
    public List<HashMap> search(String attribute, String text ,Integer type ) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if(!lista.isEmpty())
           return Arrays.asList(lista.toArray());
        else 
            return new ArrayList<>();   
    }
    
    public void createTanque(float capacidad, float capacidadTotal, float capacidadMinima, @NotEmpty String tipo, @NotEmpty String codigo) throws Exception {
        if (tipo.trim().length() > 0 && capacidad > 0 && capacidadTotal > 0  && capacidadMinima > 0 && tipo.toString().length() > 0 && codigo.toString().length() > 0  ) {
            db.getObj().setCodigo(codigo);;
            db.getObj().setCapacidad(capacidad);
            db.getObj().setCapacidadMinima(capacidadMinima);
            db.getObj().setCapacidadTotal(capacidadTotal);
            db.getObj().setTipo(TipoCombustibleEnum.valueOf(tipo));
            if (!db.save())
                throw new Exception("No se pudo guardar los datos de la Tanque");
        }
    }

    public List<String> listTipo() {
        List<String> lista = new ArrayList<>();
        for (TipoCombustibleEnum r : TipoCombustibleEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }
}