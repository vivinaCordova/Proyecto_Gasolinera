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
    // Mapa temporal para asociar checkoutId con idOrdenDespacho
    private static Map<String, Integer> checkoutToOrdenMap = new HashMap<>();

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

    public Map<String, Object> checkout(@RequestParam float total, @RequestParam String currency, @RequestParam Integer idOrdenDespacho) {
        try {
            HashMap<String, Object> response = new PagoControl().request(total, currency);
            System.out.println("Respuesta checkout backend: " + response);
            
            // Si el checkout fue exitoso, guardar la asociación checkoutId -> idOrdenDespacho
            if (response.get("id") != null && idOrdenDespacho != null) {
                String checkoutId = response.get("id").toString();
                checkoutToOrdenMap.put(checkoutId, idOrdenDespacho);
                System.out.println("💾 Asociación guardada: checkoutId=" + checkoutId + " -> idOrdenDespacho=" + idOrdenDespacho);
            }
            
            return response;
        } catch (Exception e) {
            return Map.of("estado", "false", "error", e.getMessage());
        }
    }

    public HashMap<String, Object> consultarEstadoPago(String idCheckout) throws IOException, Exception {
        System.out.println("=== INICIO consultarEstadoPago ===");
        System.out.println("Parámetros recibidos: idCheckout=" + idCheckout);
        
        PagoControl pagoControl = new PagoControl();
        HashMap<String, Object> estado = pagoControl.requestPay(idCheckout);
        
        // Validar si el pago se realizó con éxito
        if (estado != null && estado.get("estado") != null) {
            String estadoPago = estado.get("estado").toString();
            System.out.println("Estado completo del pago: " + estado);
            System.out.println("Estado del pago extraído: '" + estadoPago + "'");
            System.out.println("Tipo del estado: " + estadoPago.getClass().getSimpleName());
            
            // Si el pago fue exitoso, crear un nuevo registro de pago
            if ("approved".equalsIgnoreCase(estadoPago) || "success".equalsIgnoreCase(estadoPago) || "true".equalsIgnoreCase(estadoPago)) {
                System.out.println("PAGO EXITOSO DETECTADO - Iniciando proceso de creación...");
                
                try {
                    // Obtener datos necesarios del estado del pago
                    Integer nroTransaccion;
                    Integer idOrdenDespacho;
                    
                    // Extraer número de transacción si está disponible
                    if (estado.get("transaction_id") != null) {
                        nroTransaccion = Integer.valueOf(estado.get("transaction_id").toString());
                    } else if (estado.get("id") != null) {
                        nroTransaccion = Integer.valueOf(estado.get("id").toString());
                    } else {
                        // Generar número de transacción automáticamente
                        nroTransaccion = db.listAll().getLength() + 1;
                    }
                    System.out.println("Número de transacción generado automáticamente: " + nroTransaccion);
                    
                    // OBTENER idOrdenDespacho desde el mapa usando el checkoutId
                    idOrdenDespacho = checkoutToOrdenMap.get(idCheckout);
                    System.out.println("ID Orden Despacho obtenido del mapa: " + idOrdenDespacho);
                    
                    if (idOrdenDespacho != null) {
                        // Crear el nuevo pago con estado exitoso
                        create(nroTransaccion, true, idOrdenDespacho);
                        System.out.println("Pago exitoso registrado - Transacción: " + nroTransaccion + ", Orden: " + idOrdenDespacho);
                        
                        checkoutToOrdenMap.remove(idCheckout);
                        System.out.println("Asociación eliminada del mapa para checkoutId: " + idCheckout);
                    } else {
                        System.out.println("ERROR: No se encontró idOrdenDespacho para checkoutId: " + idCheckout);
                        System.out.println("Contenido actual del mapa: " + checkoutToOrdenMap);
                        throw new Exception("No se encontró la orden de despacho asociada al checkout: " + idCheckout);
                    }
                    
                } catch (Exception e) {
                    System.err.println("ERROR al crear el registro de pago: " + e.getMessage());
                    e.printStackTrace();
                    estado.put("warning", "Pago exitoso pero no se pudo registrar: " + e.getMessage());
                    throw e;
                }
            }
        }
        
        return estado;
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
