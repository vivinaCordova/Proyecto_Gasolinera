package org.unl.gasolinera.base.controller.service;

import java.util.Arrays;
import java.util.List;


import com.vaadin.hilla.mappedtypes.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoCuenta;
import org.unl.gasolinera.base.models.Cuenta;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@Transactional(propagation = Propagation.REQUIRES_NEW)
@AnonymousAllowed

public class CuentaService {
    private DaoCuenta da;
    public CuentaService() {
        da = new DaoCuenta();
    }

    public void createCuenta(@NotEmpty @NotBlank String usuario,@NotEmpty @NotBlank String clave) throws Exception{
        //da.getObj().setUsuario(usuario);
        da.getObj().setClave(clave);
        if(!da.save())
            throw new  Exception("No se pudo guardar los datos de Cuenta");
    }
    public List<Cuenta> list(Pageable pageable) {        
        return Arrays.asList(da.listAll().toArray());
    }
    public List<Cuenta> listAll() {  
       return (List<Cuenta>)Arrays.asList(da.listAll().toArray());
    }

}
