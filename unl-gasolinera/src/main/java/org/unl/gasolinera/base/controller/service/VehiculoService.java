package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
import org.unl.gasolinera.base.models.Persona;
import org.unl.gasolinera.base.models.Vehiculo;

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

    public void createVehiculo(@NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca) throws Exception{
        dv.getObj().setPlaca(placa);
        dv.getObj().setModelo(modelo);
        dv.getObj().setMarca(marca);
        if(!dv.save())
            throw new  Exception("No se pudo guardar los datos de Vehiculo");
    }


    public List<HashMap> listPersonaCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoPersona   dp= new DaoPersona();
        if(!dp.listAll().isEmpty()) {
            Persona [] arreglo = dp.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNombres());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listVehiculo(){
        List<HashMap> lista = new ArrayList<>();
        if(!dv.listAll().isEmpty()) {
            Vehiculo [] arreglo = dv.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString(i));             
                aux.put("placa", arreglo[i].getPlaca());
                aux.put("modelo", arreglo[i].getModelo());
                aux.put("marca", arreglo[i].getMarca());
                aux.put("propietario", new DaoPersona().listAll().get(arreglo[i].getIdPropietario()-1).getNombres());
                aux.put("idPropietario", new DaoPersona().listAll().get(arreglo[i].getIdPropietario()-1).getId().toString());
                lista.add(aux);
            }
        }
        return lista;
    }

        public List<Vehiculo> listAll() {  
       return (List<Vehiculo>)Arrays.asList(dv.listAll().toArray());
    }
}
