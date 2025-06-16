package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoCuenta;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoRol;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Cuenta;
import org.unl.gasolinera.base.models.Persona;
import org.unl.gasolinera.base.models.Rol;

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

        public List<HashMap> listAll() throws Exception{
        return Arrays.asList(da.all().toArray());
    }

    public List<HashMap> order(String attribute, Integer type)throws Exception{
        return Arrays.asList(da.orderByPersona(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = da.search(attribute, text, type);
        if(!lista.isEmpty())
            return Arrays.asList(lista.toArray());
        else
            return new ArrayList<>();
    }

    public void createPersona(@NotEmpty String usuario, String cedula, Integer id_rol) throws Exception {
        if (usuario.trim().length() > 0 && cedula.trim().length() > 0 && id_rol > 0) {
            da.getObj().setUsuario(usuario);
            da.getObj().setCedula(cedula);
            da.getObj().setId_rol(id_rol);
            if (!da.save())
                throw new Exception("No se pudo guardar los datos de Persona");
        }
    }

    public List<HashMap> listRolCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoRol dc = new DaoRol();
        if (!da.listAll().isEmpty()) {
            Rol[] arreglo = dc.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNombre());
                lista.add(aux);
            }
        }
        return lista;
    }

    public List<Persona> listAlla() {
        return (List<Persona>) Arrays.asList(da.listAll().toArray());
    }

}
