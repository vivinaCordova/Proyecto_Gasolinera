package org.unl.gasolinera.base.controller.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.unl.gasolinera.base.controller.PagoControl;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPago;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.OrdenDespacho;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class PagoService {

    private DaoPago db;

    public PagoService() {
        db = new DaoPago();
    }

    public void create(Integer nroTansaccion, Boolean estadoP, Integer idOrdenDespacho) throws Exception {
        if (nroTansaccion > 0 && estadoP != null && idOrdenDespacho > 0) {
            db.getObj().setNroTransaccion(nroTansaccion);
        }
        db.getObj().setEstadoP(estadoP);
        db.getObj().setIdOrdenDespacho(idOrdenDespacho);
        if (!db.save()) {
            throw new Exception("No se pudo guardar los datos de Pago");
        }
    }

    public void update(Integer id, Integer nroTansaccion, Boolean estadoP, Integer idOrdenDespacho) throws Exception {
        if (nroTansaccion > 0 && estadoP != null && idOrdenDespacho > 0) {
            db.setObj(db.listAll().get(id));
        }
        db.getObj().setNroTransaccion(nroTansaccion);
        db.getObj().setEstadoP(estadoP);
        db.getObj().setIdOrdenDespacho(idOrdenDespacho);
        if (!db.update(id)) {
            throw new Exception("No se pudo guardar los datos de Pago");
        }
    }

    public List<HashMap> listaPagoOrdenDespacho() {
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenDespacho da = new DaoOrdenDespacho();
        if (!db.listAll().isEmpty()) {
            OrdenDespacho[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, Object> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getCodigo());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByPago(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }

    public void crearPago(Integer idOrdenDespacho, Boolean estado) throws Exception {
        System.out.println("Llamada a crearPago: idOrdenDespacho=" + idOrdenDespacho + ", estado=" + estado);
        if (idOrdenDespacho == null || estado == null) {
            throw new Exception("Datos incompletos para crear el pago");
        }
        int nroTransaccion = db.listAll().getLength() + 1;
        org.unl.gasolinera.base.models.Pago nuevoPago = new org.unl.gasolinera.base.models.Pago();
        nuevoPago.setNroTransaccion(nroTransaccion);
        nuevoPago.setEstadoP(estado);
        nuevoPago.setIdOrdenDespacho(idOrdenDespacho);
        db.setObj(nuevoPago);
        if (!db.save()) {
            throw new Exception("No se pudo guardar el pago");
        }
    }

    /*public String realizarCobro(Integer idPago) throws Exception {
        Pago pago = db.listAll().get(idPago - 1);
        pago.setEstadoP(true);
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
    }*/
    public Map<String, Object> checkout(@RequestParam float total, @RequestParam String currency) {
        try {
            HashMap<String, Object> response = new PagoControl().request(total, currency);
            System.out.println("Respuesta checkout backend: " + response);
            return response;
        } catch (Exception e) {
            return Map.of("estado", "false", "error", e.getMessage());
        }
    }

    public HashMap<String, Object> consultarEstadoPago(String idCheckout) throws IOException {
        PagoControl pagoControl = new PagoControl();
        return pagoControl.requestPay(idCheckout);
    }


    /*public static void main(String[] args) {
        PagoService service = new PagoService();
        try {
            HashMap<String, Object> resultado = service.consultarEstadoPago("8920F4D08D3C0D65606BB9670B00C4E5.uat01-vm-tx04");
            System.out.println("Estado del pago: " + resultado.get("estado"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
