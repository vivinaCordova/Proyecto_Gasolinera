package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPago;
import org.unl.gasolinera.base.models.EstadoPagoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.Pago;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;



@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class PagoService {
    private DaoPago db;
    public PagoService() {
        db = new DaoPago();
    }

    public void create(@NotEmpty Integer nroTansaccion, @NotEmpty String estadoP, Integer idOrdenDespacho) throws Exception{
        if(nroTansaccion > 0 && estadoP.trim().length() > 0 && idOrdenDespacho > 0)
            db.getObj().setNroTransaccion(nroTansaccion);
            db.getObj().setEstadoP(EstadoPagoEnum.valueOf(estadoP));
            db.getObj().setIdOrdenDespacho(idOrdenDespacho);
            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de Pago");
    }

    public void update(@NotEmpty Integer id, @NotEmpty Integer nroTansaccion, @NotEmpty String estadoP, Integer idOrdenDespacho) throws Exception{
       if(nroTansaccion > 0 && estadoP.trim().length() > 0 && idOrdenDespacho > 0)
            db.setObj(db.listAll().get(id - 1));
            db.getObj().setNroTransaccion(nroTansaccion);
            db.getObj().setEstadoP(EstadoPagoEnum.valueOf(estadoP));
            db.getObj().setIdOrdenDespacho(idOrdenDespacho);
            if(!db.update(id - 1))
                throw new  Exception("No se pudo guardar los datos de Pago");
    }

    

    public List<HashMap> listaPagoOrdenDespacho(){
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenDespacho da = new DaoOrdenDespacho();
        if(!db.listAll().isEmpty()) {
            OrdenDespacho [] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++){
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i)); 
                aux.put("label", arreglo[i].getCodigo());   
                lista.add(aux);   
            }
        }
        return lista;
    }



    public List<HashMap> listPago(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            Pago [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString(i));                
                aux.put("nroTransaccion", arreglo[i].getNroTransaccion().toString());
                aux.put("orden", new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrdenDespacho() - 1).getCodigo());
                aux.put("id_orden", new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrdenDespacho() - 1).getId().toString());
                aux.put("estadoP", arreglo[i].getEstadoP().toString());
                lista.add(aux);
            }
        }
        return lista;
    }


    public List<String> listEstadoP() {
        List<String> lista = new ArrayList<>();
        for(EstadoPagoEnum r: EstadoPagoEnum.values()) {
            lista.add(r.toString());
        }        
        return lista;
    }

    public List<Pago> listAll() {
        return (List<Pago>) db.listAll();
    }
    

    public String realizarCobro(Integer idPago) throws Exception {
    Pago pago = db.listAll().get(idPago - 1);
    pago.setEstadoP(EstadoPagoEnum.COMPLETADO);
    db.setObj(pago);
    if (!db.update(idPago - 1)) {
        throw new Exception("No se pudo actualizar el estado del pago");
    }
    return "Cobro realizado con éxito. Transacción #" + pago.getNroTransaccion();
}

    public void generarFactura(Integer idPago) {
        Pago pago = db.listAll().get(idPago - 1);
        System.out.println("Factura:");
        System.out.println("ID Pago: " + pago.getId());
        System.out.println("Transacción: " + pago.getNroTransaccion());
        System.out.println("Estado: " + pago.getEstadoP());
    }
}
