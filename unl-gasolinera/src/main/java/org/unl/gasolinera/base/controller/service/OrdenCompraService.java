package org.unl.gasolinera.base.controller.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.Proveedor;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class OrdenCompraService {
    private DaoOrdenCompra db;

    public void createOrdenCompra(float cantidad, Integer id_proveedor, @NotEmpty String tipo) throws Exception {
        if(cantidad > 0 && id_proveedor> 0 && tipo.trim().length() >0) {
            db.getObj().setCantidad(cantidad);
            db.getObj().setIdProveedor(id_proveedor);
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
                aux.put("label", arreglo[i].getNombre());
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
    public List<HashMap> listOrdenCompra(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            OrdenCompra [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString(i)); 
                aux.put("cantidad", String.valueOf(arreglo[i].getCantidad()));
                aux.put("estado", arreglo[i].getEstado().toString());
                aux.put("proveedor", new DaoProveedor().listAll().get(arreglo[i].getIdProveedor()-1).getNombre());
                aux.put("idProveedor", new DaoProveedor().listAll().get(arreglo[i].getIdProveedor()-1).getId().toString());

                lista.add(aux);
            }
        }
        return lista;
    }
}