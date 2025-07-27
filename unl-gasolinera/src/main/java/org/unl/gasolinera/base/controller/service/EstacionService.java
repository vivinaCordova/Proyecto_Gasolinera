package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoEstacion;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoUsoEnum;
import org.unl.gasolinera.base.models.Estacion;

import com.github.javaparser.quality.NotNull;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class EstacionService {
    private DaoEstacion db;

    public EstacionService() {
        db = new DaoEstacion();
    }

    public void create(@NotEmpty String codigo, @NotEmpty String estadoE) throws Exception {
        if (estadoE.trim().length() > 0 && codigo.trim().length() > 0) {

        }
        db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
        db.getObj().setCodigo(codigo);

        if (!db.save()) {
            throw new Exception("No se pudo guardar los datos de Estacion");

        }
    }

    public void update(@NotNull Integer id, @NotEmpty String codigo, @NotEmpty String estadoE) throws Exception {
        if (estadoE.trim().length() > 0 && codigo.trim().length() > 0) {

        }
        Estacion estacionExistente = db.getById(id);
        if (estacionExistente == null) {
            throw new Exception("No se encontró la estación con ID: " + id);
        }

        Integer posicion = null;
        for (int i = 0; i < db.listAll().getLength(); i++) {
            Estacion estacionEnPosicion = db.listAll().get(i);
            if (estacionEnPosicion.getId().equals(id)) {
                posicion = i;
                break;
            }
        }
        if (posicion == null) {
            throw new Exception("No se pudo encontrar la posición de la estación con ID: " + id);
        }

        db.setObj(new Estacion());
        db.getObj().setId(id);
        db.getObj().setEstadoE(EstadoUsoEnum.valueOf(estadoE));
        db.getObj().setCodigo(codigo);

        if (!db.update(posicion)) {
            throw new Exception("No se pudo guardar los datos de Estacion");
        }
    }

    public List<String> listEstadoE() {
        List<String> lista = new ArrayList<>();
        for (EstadoUsoEnum r : EstadoUsoEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }

    public List<HashMap<String, Object>> listAll() {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByEstacion(type, attribute).toArray());
    }

    /*public void delete(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("ID de estación inválido");
        }
        if (!db.deleteEstacion(id)) {
            throw new Exception("No se pudo eliminar la estación con ID: " + id);
        }
    }*/

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }

}