package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Proveedor;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class ProveedorService {
    private DaoProveedor db;
    public ProveedorService(){
        db = new DaoProveedor();
    }

    public List<HashMap> listAll() throws Exception{
        return Arrays.asList(db.all().toArray());
    }
    public List<Proveedor> listAlla(){
        return (List<Proveedor>)Arrays.asList(db.listAll().toArray());
    }
    public List<HashMap> order(String attribute, Integer type ) throws Exception {
        return Arrays.asList(db.orderByProveedor(type, attribute).toArray());
    }
    public List<HashMap> search(String attribute, String text ,Integer type ) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if(!lista.isEmpty())
           return Arrays.asList(lista.toArray());
        else 
            return new ArrayList<>();   
    
    }
    public void createProveedor(@NotEmpty String nombre, @NotEmpty String correoElectronico, @NotEmpty String tipo) throws Exception {
        if(nombre.trim().length() > 0 && correoElectronico.trim().length() > 0 && tipo.trim().length() > 0) {
            db.getObj().setNombre(nombre);
            db.getObj().setCorreoElectronico(correoElectronico);
            db.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf(tipo));
            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de la Proveedor");
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