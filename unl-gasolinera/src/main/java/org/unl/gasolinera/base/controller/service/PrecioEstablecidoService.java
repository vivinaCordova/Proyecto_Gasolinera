package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class PrecioEstablecidoService {
    private DaoPrecioEstablecido db;
    public PrecioEstablecidoService() {
        db = new DaoPrecioEstablecido();
    }

    public void create(@NonNull Date fecha, @NonNull Date fechaFin, boolean estado, float precio, @NotEmpty String tipoCombustible) throws Exception {
        if (fecha != null && fechaFin != null && precio > 0 && tipoCombustible.trim().length() > 0) {
            db.getObj().setId(db.listAll().getLength() + 1);
            db.getObj().setFecha(fecha);
            db.getObj().setFechaFin(fechaFin);
            db.getObj().setEstado(estado);
            db.getObj().setPrecio(precio);
            db.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf(tipoCombustible));
            if (!db.save())
                throw new Exception("No se pudo guardar el precio establecido");
        }
    }

    public List<HashMap> listPrecioEstablecido() {
        List<HashMap> lista = new ArrayList<>();
        if (!db.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = db.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString());
                aux.put("fecha", arreglo[i].getFecha().toString());
                aux.put("fechaFin", arreglo[i].getFechaFin().toString());
                aux.put("estado", arreglo[i].isEstado() ? "Activo" : "Inactivo");
                aux.put("precio", String.valueOf(arreglo[i].getPrecio()));
                aux.put("tipoCombustible", arreglo[i].getTipoCombustible().toString());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<String> listTipoCombustible() {
        List<String> lista = new ArrayList<>();
        for (TipoCombustibleEnum t : TipoCombustibleEnum.values()) {
            lista.add(t.toString());
        }
        return lista;
    }
}