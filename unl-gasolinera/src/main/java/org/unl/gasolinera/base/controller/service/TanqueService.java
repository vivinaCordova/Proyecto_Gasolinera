package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.Tanque;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;


import jakarta.validation.constraints.NotEmpty;

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
    public void createTanque(float capacidad, float capacidadTotal, float capacidadMinima, @NotEmpty String tipo, Integer idOrdenCompra, Integer idOrdenDespacho) throws Exception {
        if (tipo.trim().length() > 0 && capacidad > 0 && capacidadTotal > 0  && capacidadMinima > 0 && tipo.toString().length() > 0 && idOrdenCompra > 0 &&idOrdenDespacho >0  ) {
            db.getObj().setCapacidad(capacidad);
            db.getObj().setCapacidadMinima(capacidadMinima);
            db.getObj().setCapacidadTotal(capacidadTotal);
            db.getObj().setTipo(TipoCombustibleEnum.valueOf(tipo));
            db.getObj().setIdOrdenCompra(idOrdenCompra);
            db.getObj().setIdOrdenDespacho(idOrdenDespacho);
            if (!db.save())
                throw new Exception("No se pudo guardar los datos de la Tanque");
        }
    }

    public List<HashMap> listOrdenDespachoCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenDespacho da = new DaoOrdenDespacho();
        if (!db.listAll().isEmpty()) {
            OrdenDespacho[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNroGalones().toString());
                lista.add(aux);
            }

        }
        return lista;
    }

    public List<HashMap> listOrdenCopraCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenCompra da = new DaoOrdenCompra();
        if (!db.listAll().isEmpty()) {
            OrdenCompra[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", String.valueOf(arreglo[i].getCantidad()).toString());
                lista.add(aux);
            }

        }
        return lista;
    }

    public List<String> listTipo() {
        List<String> lista = new ArrayList<>();
        for (TipoCombustibleEnum r : TipoCombustibleEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }
}