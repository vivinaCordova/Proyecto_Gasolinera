package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
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
    public List<HashMap> listProveedor(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            Proveedor [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString());
                aux.put("nombre", arreglo[i].getNombre().toString());
                aux.put("correoElectronico", arreglo[i].getCorreoElectronico().toString());
                aux.put("tipoCombustible", arreglo[i].getTipoCombustible().toString());
                lista.add(aux);
            }
        }
        return lista;
    }  

}