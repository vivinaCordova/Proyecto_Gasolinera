package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Persona;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class VehiculoService {

    private DaoVehiculo db;

    public VehiculoService() {
        db = new DaoVehiculo();
    }

    public void create(@NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca, Integer idPropietario) throws Exception {
        if (placa.trim().length() > 0 && modelo.trim().length() > 0 && marca.trim().length() > 0 && idPropietario > 0) {

        }
        db.getObj().setPlaca(placa);
        db.getObj().setModelo(modelo);
        db.getObj().setMarca(marca);
        db.getObj().setIdPropietario(idPropietario);

        if (!db.save()) {
            throw new Exception("No se pudo guardar los datos del vehículo");
        }

    }

    public void update(Integer id, @NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca, Integer idPropietario) throws Exception {
        if (id != null && id > 0 && placa.trim().length() > 0 && modelo.trim().length() > 0 && marca.trim().length() > 0 && idPropietario != null) {
            
        }

        db.setObj(db.listAll().get(id));
        db.getObj().setPlaca(placa);
        db.getObj().setModelo(modelo);
        db.getObj().setMarca(marca);
        db.getObj().setIdPropietario(idPropietario);

        if (!db.update(id)) {
            throw new Exception("No se pudo actualizar el vehículo");
        }
    }

    public List<HashMap> listaPersonaCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoPersona dao = new DaoPersona();
        if (!dao.listAll().isEmpty()) {
            for (Persona p : dao.listAll().toArray()) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", p.getId().toString());
                aux.put("label", p.getNombres());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByVehiculo(type, attribute).toArray());
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
