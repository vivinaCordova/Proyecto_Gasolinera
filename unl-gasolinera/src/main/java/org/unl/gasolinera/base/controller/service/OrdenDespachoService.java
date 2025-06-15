package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Estacion;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.Vehiculo;

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

    public void create(@NotEmpty String codigo, float nroGalones, @NonNull Date fecha, float precioTotal, @NotEmpty String estado, Integer idPrecioEstablecido, Integer idVehiculo, Integer idEstacion) throws Exception {
        if (codigo.trim().length() > 0 && nroGalones > 0 && fecha != null  && estado.trim().length() > 0 && idPrecioEstablecido != null && idVehiculo != null) {
            
        }

        db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado(EstadoOrdenDespachadoEnum.valueOf(estado));
            db.getObj().setIdVehiculo(idVehiculo);
            db.getObj().setIdPrecioEstablecido(idPrecioEstablecido);
            db.getObj().setIdEstacion(idEstacion);

            if (!db.save()) {
                throw new Exception("No se pudo guardar los datos de la orden de despacho");
            }
    }

    public void update(Integer id, @NotEmpty String codigo, float nroGalones, @NonNull Date fecha, float precioTotal, @NotEmpty String estado, Integer idPrecioEstablecido, Integer idVehiculo, Integer idEstacion) throws Exception {
        if (id != null && id > 0 && codigo.trim().length() > 0 && nroGalones > 0 && fecha != null
                && precioTotal > 0 && estado.trim().length() > 0 && idPrecioEstablecido != null && idVehiculo != null) {
            db.setObj(db.listAll().get(id));
            db.getObj().setCodigo(codigo);
            db.getObj().setNroGalones(nroGalones);
            db.getObj().setFecha(fecha);
            db.getObj().setPrecioTotal(precioTotal);
            db.getObj().setEstado(EstadoOrdenDespachadoEnum.valueOf(estado));
            db.getObj().setIdVehiculo(idVehiculo);
            db.getObj().setIdPrecioEstablecido(idPrecioEstablecido);
            db.getObj().setIdEstacion(idEstacion);

            if (!db.update(id)) {
                throw new Exception("No se pudo actualizar la orden de despacho");
            }
        }
    }

    public List<HashMap> listaVehiculoCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoVehiculo dao = new DaoVehiculo();
        if (!dao.listAll().isEmpty()) {
            for (Vehiculo v : dao.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", v.getId().toString());
                aux.put("label", v.getPlaca());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listaPrecioEstablecidosCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoPrecioEstablecido dao = new DaoPrecioEstablecido();
        if (!dao.listAll().isEmpty()) {
            for (PrecioEstablecido p : dao.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", p.getId().toString());
                aux.put("label", String.valueOf(p.getPrecio()));
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listaEstacionCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoEstacion dao = new DaoEstacion();
        if (!dao.listAll().isEmpty()) {
            for (Estacion e : dao.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", e.getId().toString());
                aux.put("label", e.getCodigo()); 
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<String> listEstadoOrdenDespacho() {
        List<String> lista = new ArrayList<>();
        for (EstadoOrdenDespachadoEnum r : EstadoOrdenDespachadoEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByOrdenDespacho(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }

   
}
