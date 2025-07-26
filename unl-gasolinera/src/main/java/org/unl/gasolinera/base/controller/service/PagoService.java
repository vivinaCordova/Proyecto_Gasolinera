package org.unl.gasolinera.base.controller.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.unl.gasolinera.base.controller.PagoControl;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPago;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Estacion;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.Pago;
import org.unl.gasolinera.base.models.Persona;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.Vehiculo;

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

    /*public void delete(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("ID de pago inválido");
        }
        if (!db.deletePago(id)) {
            throw new Exception("No se pudo eliminar el pago con ID: " + id);
        }
    }*/

   

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
                        
                        // ACTUALIZAR EL ESTADO DE LA ORDEN DE DESPACHO A COMPLETADO
                        try {
                            OrdenDespachoService ordenService = new OrdenDespachoService();
                            ordenService.actualizarEstado(idOrdenDespacho, "COMPLETADO");
                            System.out.println("Estado de OrdenDespacho actualizado a COMPLETADO para orden ID: " + idOrdenDespacho);
                        } catch (Exception ordenException) {
                            System.err.println("Error al actualizar estado de OrdenDespacho: " + ordenException.getMessage());
                            // No lanzamos excepción aquí para no interrumpir el proceso de pago exitoso
                        }
                        
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
            } else {
                // Si el pago no fue exitoso, crear un pago con estado false
                System.out.println("PAGO NO EXITOSO DETECTADO - Estado: '" + estadoPago + "'");
                
                try {
                    // Obtener idOrdenDespacho desde el mapa usando el checkoutId
                    Integer idOrdenDespacho = checkoutToOrdenMap.get(idCheckout);
                    System.out.println("ID Orden Despacho obtenido del mapa para pago fallido: " + idOrdenDespacho);
                    
                    if (idOrdenDespacho != null) {
                        // Generar número de transacción automáticamente
                        Integer nroTransaccion = db.listAll().getLength() + 1;
                        System.out.println("Número de transacción generado para pago fallido: " + nroTransaccion);
                        
                        // Crear el nuevo pago con estado false (fallido)
                        create(nroTransaccion, false, idOrdenDespacho);
                        System.out.println("Pago fallido registrado - Transacción: " + nroTransaccion + ", Orden: " + idOrdenDespacho);
                        
                        // Limpiar la asociación del mapa
                        checkoutToOrdenMap.remove(idCheckout);
                        System.out.println("Asociación eliminada del mapa para checkoutId (pago fallido): " + idCheckout);
                        
                        // Agregar información del pago fallido al estado
                        estado.put("pago_registrado", "false");
                        estado.put("nro_transaccion", nroTransaccion);
                    } else {
                        System.out.println("WARNING: No se encontró idOrdenDespacho para checkoutId en pago fallido: " + idCheckout);
                    }
                    
                } catch (Exception e) {
                    System.err.println("ERROR al crear el registro de pago fallido: " + e.getMessage());
                    e.printStackTrace();
                    estado.put("error_registro", "No se pudo registrar el pago fallido: " + e.getMessage());
                }
            }
        } else {
            // Si no se pudo obtener el estado del pago, también crear un pago fallido si existe la asociación
            System.out.println("ERROR: No se pudo obtener el estado del pago para checkoutId: " + idCheckout);
            
            try {
                Integer idOrdenDespacho = checkoutToOrdenMap.get(idCheckout);
                if (idOrdenDespacho != null) {
                    Integer nroTransaccion = db.listAll().getLength() + 1;
                    create(nroTransaccion, false, idOrdenDespacho);
                    System.out.println("Pago fallido registrado por error de consulta - Transacción: " + nroTransaccion + ", Orden: " + idOrdenDespacho);
                    
                    checkoutToOrdenMap.remove(idCheckout);
                    
                    // Inicializar estado si es null
                    if (estado == null) {
                        estado = new HashMap<>();
                        estado.put("estado", "error");
                    }
                    
                    estado.put("pago_registrado", "false");
                    estado.put("nro_transaccion", nroTransaccion);
                    estado.put("motivo", "Error al consultar estado del pago");
                }
            } catch (Exception e) {
                System.err.println("ERROR al crear el registro de pago fallido por error de consulta: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Asegurar que estado no sea null antes de retornarlo
        if (estado == null) {
            estado = new HashMap<>();
            estado.put("estado", "error");
            estado.put("mensaje", "No se pudo consultar el estado del pago");
        }
        
        return estado;
    }

    public HashMap<String, Object> generarRecibo(Integer idPago) throws Exception {
        if (idPago == null || idPago <= 0) {
            throw new Exception("ID de pago inválido");
        }
        HashMap<String, Object> recibo = new HashMap<>();
        // Obtener el pago
        Pago pago = null;
        LinkedList<Pago> listaPagos = db.listAll();
        for (int i = 0; i < listaPagos.getLength(); i++) {
            if (listaPagos.get(i).getId().equals(idPago)) {
                pago = listaPagos.get(i);
                break;
            }
        }

        if (pago == null) {
            throw new Exception("No se encontró el pago con ID: " + idPago);
        }
        // Datos básicos del pago
        recibo.put("nroTransaccion", pago.getNroTransaccion());
        recibo.put("estadoPago", pago.getEstadoP() ? "EXITOSO" : "FALLIDO");
        recibo.put("fechaRecibo", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

        // Obtener datos de la orden de despacho
        DaoOrdenDespacho daoOrden = new DaoOrdenDespacho();
        OrdenDespacho orden = null;
        LinkedList<OrdenDespacho> listaOrdenes = daoOrden.listAll();
        
        for (int i = 0; i < listaOrdenes.getLength(); i++) {
            if (listaOrdenes.get(i).getId().equals(pago.getIdOrdenDespacho())) {
                orden = listaOrdenes.get(i);
                break;
            }
        }

        if (orden != null) {
            recibo.put("codigo", orden.getCodigo());
            recibo.put("fecha", orden.getFecha() != null ? 
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(orden.getFecha()) : "No registrada");
            recibo.put("nroGalones", orden.getNroGalones());
            recibo.put("precioTotal", orden.getPrecioTotal());
            recibo.put("estado", orden.getEstado() != null ? orden.getEstado().toString() : "Sin estado");

            // Obtener placa del vehículo y propietario
            if (orden.getIdVehiculo() != null) {
                DaoVehiculo daoVehiculo = new DaoVehiculo();
                LinkedList<Vehiculo> listaVehiculos = daoVehiculo.listAll();
                
                for (int i = 0; i < listaVehiculos.getLength(); i++) {
                    Vehiculo vehiculo = listaVehiculos.get(i);
                    
                    if (vehiculo.getId().equals(orden.getIdVehiculo())) {
                        recibo.put("placa", vehiculo.getPlaca());
                        
                        // Obtener propietario del vehículo
                        Integer idPropietario = vehiculo.getIdPropietario();
                        
                        if (idPropietario != null) {
                            DaoPersona daoPersona = new DaoPersona();
                            LinkedList<Persona> listaPersonas = daoPersona.listAll();
                            
                            for (int j = 0; j < listaPersonas.getLength(); j++) {
                                Persona persona = listaPersonas.get(j);
                                
                                if (persona.getId().equals(idPropietario)) {
                                    String nombrePropietario = persona.getUsuario() != null ? persona.getUsuario() : "Sin usuario";
                                    recibo.put("propietarioVehiculo", nombrePropietario);
                                    recibo.put("cedulaPropietario", persona.getCedula());
                                    break;
                                }
                            }
                        } else {
                            recibo.put("propietarioVehiculo", "Sin propietario");
                            recibo.put("cedulaPropietario", "N/A");
                        }
                        break;
                    }
                }
            }

            // Obtener código de estación
            if (orden.getIdEstacion() != null) {
                DaoEstacion daoEstacion = new DaoEstacion();
                LinkedList<Estacion> listaEstaciones = daoEstacion.listAll();
                for (int i = 0; i < listaEstaciones.getLength(); i++) {
                    if (listaEstaciones.get(i).getId().equals(orden.getIdEstacion())) {
                        recibo.put("estacion", listaEstaciones.get(i).getCodigo());
                        break;
                    }
                }
            }

            // Obtener tipo de gasolina y precio
            if (orden.getIdPrecioEstablecido() != null) {
                DaoPrecioEstablecido daoPrecio = new DaoPrecioEstablecido();
                LinkedList<PrecioEstablecido> listaPrecios = daoPrecio.listAll();
                for (int i = 0; i < listaPrecios.getLength(); i++) {
                    if (listaPrecios.get(i).getId().equals(orden.getIdPrecioEstablecido())) {
                        PrecioEstablecido precio = listaPrecios.get(i);
                        recibo.put("nombreGasolina", precio.getTipoCombustible() != null ? 
                            precio.getTipoCombustible().toString() : "Sin tipo");
                        recibo.put("precio_establecido", precio.getPrecio());
                        break;
                    }
                }
            }
        }

        return recibo;
    }

    

    public static void main(String[] args) {
        PagoService service = new PagoService();
        String checkoutIdTest = "TEST_CHECKOUT_123";
        Integer ordenIdTest = 1;
        checkoutToOrdenMap.put(checkoutIdTest, ordenIdTest);
        System.out.println("PRUEBA: Asociación creada para testing: " + checkoutIdTest + " -> " + ordenIdTest);
        
        try {
                     
            // Probar con el checkout de prueba que agregamos
            System.out.println("\nCheckout de prueba");
            HashMap<String, Object> resultado3 = service.consultarEstadoPago(checkoutIdTest);
            System.out.println("Resultado caso 3: " + resultado3);
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
