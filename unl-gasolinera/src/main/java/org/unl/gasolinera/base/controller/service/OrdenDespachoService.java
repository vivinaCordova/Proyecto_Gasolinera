package org.unl.gasolinera.base.controller.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.PrecioEstablecido;

import com.github.javaparser.quality.NotNull;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed
public class OrdenDespachoService {

    private DaoOrdenDespacho db;

    public OrdenDespachoService() {
        db = new DaoOrdenDespacho();
    }

    public void create(float nroGalones, Integer idPrecioEstablecido, Integer idVehiculo) throws Exception {
        if (nroGalones > 0 && idPrecioEstablecido != null && idVehiculo != null) {

            DaoPrecioEstablecido daoPrecio = new DaoPrecioEstablecido();
            PrecioEstablecido precio = daoPrecio.getById(idPrecioEstablecido);
            if (precio == null) {
                throw new Exception("No se encontró el PrecioEstablecido con id: " + idPrecioEstablecido);
            }

            float precioTotal = Math.round((precio.getPrecio() * nroGalones) * 100.0f) / 100.0f;

            // Generar un código único para la orden
            Integer tamaño = db.listAll().getLength() + 1;
            String codigo = String.valueOf(tamaño);

            // Obtener la fecha actual
            Date fecha = new Date();

            // Generar una estacion aleatoria para simulacion
            DaoEstacion daoEstacion = new DaoEstacion();
            var estaciones = daoEstacion.listAll();
            if (estaciones.isEmpty()) {
                throw new Exception("No hay estaciones registradas.");
            }
            int indexAleatorio = new Random().nextInt(estaciones.getLength());
            Integer idEstacion = estaciones.get(indexAleatorio).getId();

            db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado((EstadoOrdenDespachadoEnum.EN_PROCESO));
            db.getObj().setIdPrecioEstablecido(idPrecioEstablecido);
            db.getObj().setIdVehiculo(idVehiculo);
            db.getObj().setIdEstacion(idEstacion);

            if (!db.save()) {
                throw new Exception("No se pudo guardar la Orden de Despacho");
            }
        }
    }

    public List<HashMap> listOrdenDespacho() {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> listVehiculoCombo() {
        List<HashMap> lista = new java.util.ArrayList<>();
        DaoVehiculo dv = new DaoVehiculo();
        if (!dv.listAll().isEmpty()) {
            var arreglo = dv.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, Object> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getPlaca());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listPrecioGalonCombo() {
        List<HashMap> lista = new java.util.ArrayList<>();
        DaoPrecioEstablecido dp = new DaoPrecioEstablecido();
        if (!dp.listAll().isEmpty()) {
            var arreglo = dp.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, Object> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getTipoCombustible().toString());
                aux.put("precio", String.valueOf(arreglo[i].getPrecio()));
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listEstacionCombo() {
        List<HashMap> lista = new java.util.ArrayList<>();
        DaoEstacion de = new DaoEstacion();
        if (!de.listAll().isEmpty()) {
            var arreglo = de.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, Object> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getCodigo());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<String> listEstadoOrdenDespacho() {
        List<String> lista = new java.util.ArrayList<>();
        for (EstadoOrdenDespachadoEnum r : EstadoOrdenDespachadoEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }

    public List<HashMap> listAll() {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByOrdenDespacho(type, attribute).toArray());
    }

    public void actualizarEstado(@NotNull Integer id, @NotEmpty String nuevoEstado) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("ID de orden de despacho inválido");
        }

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new Exception("El nuevo estado no puede estar vacío");
        }

        // Validar que el estado sea válido
        try {
            EstadoOrdenDespachadoEnum.valueOf(nuevoEstado.trim());
        } catch (IllegalArgumentException e) {
            throw new Exception("Estado inválido: " + nuevoEstado + ". Estados válidos: EN_PROCESO, COMPLETADO");
        }

        // Buscar la orden por ID usando posición en el arreglo
        var listaOrdenes = db.listAll();
        OrdenDespacho ordenEncontrada = null;
        Integer posicion = null;

        for (int i = 0; i < listaOrdenes.getLength(); i++) {
            if (listaOrdenes.get(i).getId().equals(id)) {
                ordenEncontrada = listaOrdenes.get(i);
                posicion = i;
                break;
            }
        }

        if (ordenEncontrada == null) {
            throw new Exception("No se encontró la Orden de Despacho con ID: " + id);
        }

        // Actualizar solo el estado
        ordenEncontrada.setEstado(EstadoOrdenDespachadoEnum.valueOf(nuevoEstado.trim()));

        // Configurar el objeto actualizado en el DAO
        db.setObj(ordenEncontrada);

        if (!db.update(posicion)) {
            throw new Exception("No se pudo actualizar el estado de la Orden de Despacho");
        }

        System.out.println("Estado de OrdenDespacho ID " + id + " actualizado a: " + nuevoEstado);

        // Descontar stock solo si la orden fue completada
        // Si la orden quedó COMPLETADA, descontar stock
        if (EstadoOrdenDespachadoEnum.COMPLETADO.equals(ordenEncontrada.getEstado())) {
            DaoTanque daoTanque = new DaoTanque();
            boolean descontado = daoTanque.descontarStock(id); // solo idOrdenDespacho
            if (descontado) {
                System.out.println("Stock descontado correctamente para la orden " + id);
            } else {
                System.out.println("No se pudo descontar stock para la orden " + id);
            }
        }
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }

    /*
     * public void delete(Integer id) throws Exception {
     * if (id == null || id <= 0) {
     * throw new Exception("ID de pago inválido");
     * }
     * if (!db.deleteOrdenDespacho(id)) {
     * throw new Exception("No se pudo eliminar el pago con ID: " + id);
     * }
     * }
     */

    public OrdenDespacho getById(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("ID no puede ser null");
        }

        if (!db.listAll().isEmpty()) {
            OrdenDespacho[] ordenes = db.listAll().toArray();
            for (OrdenDespacho orden : ordenes) {
                if (orden.getId() != null && orden.getId().equals(id)) {
                    return orden;
                }
            }
        }
        return null;
    }

    public EstadoOrdenDespachadoEnum getEstadoDespacho() {
        return EstadoOrdenDespachadoEnum.EN_PROCESO;
    }

}