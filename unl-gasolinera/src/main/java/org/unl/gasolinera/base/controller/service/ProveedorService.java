package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.PrecioEstablecido;
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
    public void createProveedor(@NotEmpty String nombre, @NotEmpty String correoElectronico, Integer tipoCombustible) throws Exception {
        if (nombre.trim().length() > 0 && correoElectronico.trim().length()> 0 && tipoCombustible > 0 ) {
    
        db.getObj().setNombre(nombre);
        db.getObj().setCorreoElectronico(correoElectronico);
        db.getObj().setIdPrecioEstablecido(tipoCombustible);
    
        if (!db.save()) 
            throw new Exception("No se pudo guardar los datos del proveedor. Verifica la conexión o los datos.");
        }
    }
    public List<HashMap> listTipo() {
        List<HashMap> lista = new ArrayList<>();
        DaoPrecioEstablecido da = new DaoPrecioEstablecido();
        if (!db.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getTipoCombustible().toString());
                lista.add(aux);
            }

        }
        return lista;
    } 
}