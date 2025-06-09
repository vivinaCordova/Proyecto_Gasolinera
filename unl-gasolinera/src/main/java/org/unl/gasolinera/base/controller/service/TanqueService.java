package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;


import jakarta.validation.constraints.NotEmpty;
import org.unl.gasolinera.base.models.Tanque;


@BrowserCallable
@AnonymousAllowed
public class TanqueService {
    private DaoTanque db;

    public TanqueService() {
        db = new DaoTanque();
    }

    public void createTanque(float capacidad, float capacidadTotal, float capacidadMinima, @NotEmpty String tipo,
            @NotEmpty String estado, Integer idOrden, Integer idOrdenCompra, Integer idProveedor) throws Exception {
        if (tipo.trim().length() > 0 && capacidad > 0 && capacidadTotal > 0  && capacidadMinima > 0 ) {
            db.getObj().setCapacidad(capacidad);
            db.getObj().setCapacidadMinima(capacidadMinima);
            db.getObj().setCapacidadTotal(capacidadTotal);
            db.getObj().setTipo(TipoCombustibleEnum.valueOf(tipo));

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
    public List<HashMap> listTanque() {
        List<HashMap> lista = new ArrayList<>();
        if (!db.listAll().isEmpty()) {
            Tanque[] arreglo = db.listAll().toArray();

            for (int i = 0; i < arreglo.length; i++) {

                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString());
                aux.put("capacidad", String.valueOf(arreglo[i].getCapacidad()));
                aux.put("capacidadTotal", String.valueOf(arreglo[i].getCapacidadTotal()));
                aux.put("capacidaddMinima", String.valueOf(arreglo[i].getCapacidadMinima()));
                aux.put("tipo", arreglo[i].getTipo().toString());
                aux.put("ordenCompra", String.valueOf(new DaoOrdenCompra().listAll().get(arreglo[i].getIdOrdenCompra() - 1).getCantidad()));
                aux.put("id_ordenCompra",new DaoOrdenCompra().listAll().get(arreglo[i].getIdOrdenCompra() - 1).toString());
                aux.put("ordenDespacho", String.valueOf(new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrden() - 1).getNroGalones()));
                aux.put("id_OrdenDespacho",new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrden() - 1).toString());
                lista.add(aux);
            }
        }
        return lista;
    }
}