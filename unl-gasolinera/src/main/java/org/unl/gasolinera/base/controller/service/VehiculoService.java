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

import com.github.javaparser.quality.NotNull;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class VehiculoService {
    private DaoVehiculo dv;
    public VehiculoService() {
        dv = new DaoVehiculo();
    }

   public void create(@NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca, @NotNull Integer idPropietario) throws Exception {
        if (placa.trim().length() > 0 && modelo.trim().length() > 0 && marca.trim().length() > 0 && idPropietario > 0) {

        }

        dv.getObj().setPlaca(placa);
        dv.getObj().setModelo(modelo);
        dv.getObj().setMarca(marca);
        dv.getObj().setIdPropietario(idPropietario);

        if (!dv.save()) {
            throw new Exception("No se pudo guardar los datos del Vehículo");
        }
    }

    public void update(@NotNull Integer id,@NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca, @NotNull Integer idPropietario) throws Exception {
        if (placa.trim().length() > 0 && modelo.trim().length() > 0 && marca.trim().length() > 0 && idPropietario > 0) {

        }

        dv.setObj(dv.listAll().get(id));
        dv.getObj().setPlaca(placa);
        dv.getObj().setModelo(modelo);
        dv.getObj().setMarca(marca);
        dv.getObj().setIdPropietario(idPropietario);
        
        if(!dv.update(id)){
            throw new  Exception("No se pudo guardar los datos de Estacion");

        }
    }


    public List<HashMap> listPersonaCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoPersona   dp= new DaoPersona();
        if(!dp.listAll().isEmpty()) {
            Persona [] arreglo = dp.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getUsuario());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(dv.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(dv.orderByVehiculo(type, attribute).toArray());
    }

     public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = dv.search(attribute, text, type);
        if (!lista.isEmpty()) {
            return Arrays.asList(lista.toArray());
        } else {
            return new ArrayList<>();
        }
    }

}
