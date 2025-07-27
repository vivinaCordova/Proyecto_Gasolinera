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
import org.unl.gasolinera.base.controller.dao.dao_models.DaoVehiculo;
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
    private DaoCuenta dc;
    private DaoVehiculo dv;

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

    public boolean isCreated(String cedula) throws Exception {
        List<HashMap> personas = listAll();
        for (HashMap persona : personas) {
            Object cedulaObj = persona.get("cedula");
            if (cedulaObj != null && cedulaObj.toString().trim().equalsIgnoreCase(cedula.trim())) {
                return true;
            }
        }
        return false;
    }
        
        public boolean isUser(String usuario) throws Exception {
            List<HashMap> personas = listAll();
            for (HashMap persona : personas) {
                Object personaObj = persona.get("usuario");
                if (personaObj != null && personaObj.toString().trim().equalsIgnoreCase(usuario.trim())) {
                    return true;
                }
            }
            return false;
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

public void createRegistro(@NotEmpty String usuario, @NotEmpty String cedula, @NotEmpty String correo,
            @NotEmpty String clave, @NotEmpty String placa, @NotEmpty String modelo, @NotEmpty String marca)
            throws Exception {
        if (usuario.trim().length() > 0 && cedula.trim().length() > 0 && correo.trim().length() > 0
                && clave.trim().length() > 0 && placa.trim().length() > 0 && modelo.trim().length() > 0
                && marca.trim().length() > 0) {
            dc = new DaoCuenta();
            dv = new DaoVehiculo();
            da.getObj().setUsuario(usuario);
            da.getObj().setCedula(cedula);
            da.getObj().setId_rol(2);
            Integer idPersona = da.listAll().getLength() + 1;
            if (!da.save())
                throw new Exception("No se pudo guardar los datos de Persona");
            dc.getObj().setCorreo(correo);
            dc.getObj().setClave(clave);
            dc.getObj().setId_persona(idPersona);
            dc.getObj().setEstado(true);
            if (!dc.save())
                throw new Exception("No se pudo guardar la cuenta del usuario");
            dv.getObj().setIdPropietario(idPersona);
            dv.getObj().setPlaca(placa);
            dv.getObj().setModelo(modelo);
            dv.getObj().setMarca(marca);
            if (!dv.save())
                throw new Exception("No se pudo guardar el vehículo del usuario");

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

    /*public void delete(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("ID de pago inválido");
        }
        if (!da.deletePersona(id)) {
            throw new Exception("No se pudo eliminar el pago con ID: " + id);
        }
    }*/
}
