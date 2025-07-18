package org.unl.gasolinera.base.controller.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
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

    public void create(@NotEmpty String codigo, float nroGalones, @NonNull Date fecha, @NotEmpty String estado,
                       Integer idPrecioEstablecido, Integer idVehiculo, Integer idEstacion) throws Exception {
        if (codigo.trim().length() > 0 && nroGalones > 0 && fecha != null && estado.trim().length() > 0
                && idPrecioEstablecido != null && idVehiculo != null && idEstacion != null) {

            DaoPrecioEstablecido daoPrecio = new DaoPrecioEstablecido();
            PrecioEstablecido precio = daoPrecio.getById(idPrecioEstablecido);
            if (precio == null) {
                throw new Exception("No se encontró el PrecioEstablecido con id: " + idPrecioEstablecido);
            }

            float precioTotal = precio.getPrecio() * nroGalones;

            db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado(EstadoOrdenDespachadoEnum.valueOf(estado));
            db.getObj().setIdPrecioEstablecido(idPrecioEstablecido);
            db.getObj().setIdVehiculo(idVehiculo);
            db.getObj().setIdEstacion(idEstacion);

            if (!db.save()) {
                throw new Exception("No se pudo guardar la Orden de Despacho");
            }
        }
    }

    public void update(@NotNull Integer id, @NotEmpty String codigo, float nroGalones, @NonNull Date fecha,
                       @NotEmpty String estado, Integer idPrecioEstablecido, Integer idVehiculo, Integer idEstacion) throws Exception {
        if (codigo.trim().length() > 0 && nroGalones > 0 && fecha != null && estado.trim().length() > 0
                && idPrecioEstablecido != null && idVehiculo != null && idEstacion != null) {

            DaoPrecioEstablecido daoPrecio = new DaoPrecioEstablecido();
            PrecioEstablecido precio = daoPrecio.getById(idPrecioEstablecido);
            if (precio == null) {
                throw new Exception("No se encontró el PrecioEstablecido con id: " + idPrecioEstablecido);
            }

            Utiles utiles = new Utiles();
            float precioTotal = Float.parseFloat(utiles.tranformStringFloatTwoDecimal(precio.getPrecio() * nroGalones));


            db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado(EstadoOrdenDespachadoEnum.valueOf(estado));
            db.getObj().setIdPrecioEstablecido(idPrecioEstablecido);
            db.getObj().setIdVehiculo(idVehiculo);
            db.getObj().setIdEstacion(idEstacion);

            if (!db.update(id)) {
                throw new Exception("No se pudo actualizar la Orden de Despacho");
            }
        }
    }

    // ✅ Ahora el DAO ya entrega todos los datos listos, no necesitas getById
    public List<HashMap> listOrdenDespacho() {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> listVehiculoCombo() {
        List<HashMap> lista = new java.util.ArrayList<>();
        DaoVehiculo dv = new DaoVehiculo();
        if (!dv.listAll().isEmpty()) {
            var arreglo = dv.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
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
                HashMap<String, String> aux = new HashMap<>();
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
                HashMap<String, String> aux = new HashMap<>();
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

    public List<HashMap> order(String attribute, Integer type) {
        return Arrays.asList(db.orderbyOrdenDespacho(type, attribute).toArray());
    }
}
