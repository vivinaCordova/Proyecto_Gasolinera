package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.Proveedor;
import org.unl.gasolinera.base.models.Tanque;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class OrdenCompraService {
    private DaoOrdenCompra db;

    public OrdenCompraService() {
        db = new DaoOrdenCompra();
    }

    public List<HashMap> listAll() throws Exception{
        return Arrays.asList(db.all().toArray());
    }
    public List<OrdenCompra> listAlla(){
        return (List<OrdenCompra>)Arrays.asList(db.listAll().toArray());
    }
    public List<HashMap> order(String attribute, Integer type ) throws Exception {
        return Arrays.asList(db.orderByOrdenCompra(type, attribute).toArray());
    }
    public List<HashMap> search(String attribute, String text ,Integer type ) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if(!lista.isEmpty())
           return Arrays.asList(lista.toArray());
        else 
            return new ArrayList<>();   
    
    }

    public void createOrdenCompra(float cantidad, Integer id_proveedor, Integer id_tanque, @NotEmpty String tipo) throws Exception {
        if(cantidad > 0 && id_proveedor> 0 && tipo.trim().length() >0) {
            db.getObj().setCantidad(cantidad);
            db.getObj().setIdProveedor(id_proveedor);
            db.getObj().setIdTanque(id_tanque);
            db.getObj().setEstado(EstadoOrdenCompraEnum.valueOf(tipo));
            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de la banda");
        }
    }
    public List<HashMap> listProveedorCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoProveedor da = new DaoProveedor();
        if(!db.listAll().isEmpty()) {
            Proveedor[] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNombre().toString());
                lista.add(aux); 
            }

        }
        return lista;
    }
    public List<HashMap> listTanqueCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoTanque da = new DaoTanque();
        if(!db.listAll().isEmpty()) {
            Tanque[] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getCodigo().toString());
                lista.add(aux); 
            }

        }
        return lista;
    }
    public List<String> listEstado() {
        List<String> lista = new ArrayList<>();
        for (EstadoOrdenCompraEnum r : EstadoOrdenCompraEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }
}