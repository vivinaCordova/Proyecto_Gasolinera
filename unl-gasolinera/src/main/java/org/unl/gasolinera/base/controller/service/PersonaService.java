package org.unl.gasolinera.base.controller.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoCuenta;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.models.Cuenta;
import org.unl.gasolinera.base.models.Persona;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class PersonaService {
    private DaoPersona da;
    public PersonaService() {
        da = new DaoPersona();
    }

    public void createPersona(@NotEmpty String nombre, @NotEmpty String apellido, @NotEmpty String cedula) throws Exception {
        if(nombre.trim().length() > 0 && apellido.trim().length() > 0  && cedula.trim().length() > 0) {
            da.getObj().setNombre(nombre);
            da.getObj().setApellido(apellido);
            da.getObj().setCedula(cedula);
            if(!da.save())
                throw new  Exception("No se pudo guardar los datos de Persona");
        }
    }

    public List<HashMap> listCuentaCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoCuenta dc= new DaoCuenta();
        if(!dc.listAll().isEmpty()) {
            Cuenta [] arreglo = dc.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                //aux.put("label", arreglo[i].getUsuario());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<HashMap> listPersona(){
        List<HashMap> lista = new ArrayList<>();
        if(!da.listAll().isEmpty()) {
            Persona [] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString(i));             
                aux.put("nombre", arreglo[i].getNombre());
                aux.put("apellido", arreglo[i].getApellido());
                aux.put("cedula", arreglo[i].getCedula());
                //aux.put("cuenta", new DaoCuenta().listAll().get(arreglo[i].getIdCuenta()-1).getUsuario());
                aux.put("idCuenta", new DaoCuenta().listAll().get(arreglo[i].getIdCuenta()-1).getId().toString());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<Persona> listAll() {  
        return (List<Persona>)Arrays.asList(da.listAll().toArray());
     }
    

}
