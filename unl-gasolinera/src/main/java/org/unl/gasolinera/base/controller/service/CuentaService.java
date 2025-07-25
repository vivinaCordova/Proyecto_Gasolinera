package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.vaadin.hilla.mappedtypes.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoCuenta;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPersona;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoRol;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Cuenta;
import org.unl.gasolinera.base.models.Persona;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed

public class CuentaService {
    private DaoCuenta da;
    private SecurityContext context;
    // private Authentication auth;

    public CuentaService() {
        da = new DaoCuenta();
        context = SecurityContextHolder.getContext();
        // auth= context.getAuthentication();
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(da.orderByCuenta(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = da.search(attribute, text, type);
        if (!lista.isEmpty())
            return Arrays.asList(lista.toArray());
        else
            return new ArrayList<>();
    }

    public boolean isCreated(String correo) throws Exception {
        List<HashMap> cuentas = listAll();
        for (HashMap cuenta : cuentas) {
            String correoExistente = (String) cuenta.get("correo");
            if (correoExistente != null && correoExistente.equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }


    public void createCuenta(@NotEmpty @NotBlank String correo, @NotEmpty @NotBlank String clave, boolean estado,
            Integer id_persona) throws Exception {
                    da.getObj().setCorreo(correo);
                    da.getObj().setClave(clave);
                    da.getObj().setId_persona(id_persona);
                    da.getObj().setEstado(estado);
            
                    if (!da.save()) {
                        throw new Exception("No se pudo guardar los datos de Cuenta");
                    }
    }

    public HashMap<String, String> createRoles() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("resp", "Ya creado");
        mapa.put("code", "201");
        DaoRol dr = new DaoRol();
        if (dr.listAll().isEmpty()) {
            dr.getObj().setNombre("admin");
            dr.save();
            dr.setObj(null);
            dr.getObj().setNombre("user");
            dr.save();
            dr.setObj(null);
            mapa.put("resp", "Creado");
            mapa.put("code", "200");
        }
        return mapa;
    }

    public Authentication getAuthentication() {
        System.out.println("PIDE AUTENTICACION**************");
        System.out.println(context.getAuthentication());
        return context.getAuthentication();
    }

    public HashMap<String, String> view_rol() {
        HashMap<String, String> mapa = new HashMap<>();
        if (context.getAuthentication() != null) {
            Object obj[] = context.getAuthentication().getAuthorities().toArray();
            mapa.put("rol", obj[0].toString());
            mapa.put("usuario", context.getAuthentication().getName());
        }
        return mapa;
    }

    public Boolean isLogin() {
        if (getAuthentication() != null)
            return getAuthentication().isAuthenticated();

        return false;

    }

    public HashMap<String, Object> login(String email, String password) throws Exception {
        HashMap<String, Object> mapa = new HashMap<>();
        try {
            HashMap<String, Object> aux = da.login(email, password);
            if (aux != null) {
                context.setAuthentication(new UsernamePasswordAuthenticationToken(aux.get("usuario").toString(),
                        aux.get("id").toString(), getAuthorities(aux)));
                mapa.put("user", context.getAuthentication());
                mapa.put("message", "OK");
                mapa.put("estado", "true");
            }
        } catch (Exception e) {
            mapa.put("user", new HashMap<>());
            mapa.put("message", "Usuario o clave incorrectos, o cuenta no existe");
            mapa.put("estado", "false");
            context.setAuthentication(null);
            System.out.println(e);
        }
        return mapa;
    }

    private static List<GrantedAuthority> getAuthorities(HashMap<String, Object> user) throws Exception {
        DaoRol dr = new DaoRol();
        dr.setObj(dr.get(Integer.parseInt(user.get("rol").toString())));
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_" + dr.getObj().getNombre()));
        return list;
    }

    public HashMap<String, String> logout() {
        context.setAuthentication(null);
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("msg", "ok");
        return mapa;
    }

    public List<Cuenta> list(Pageable pageable) {
        return Arrays.asList(da.listAll().toArray());
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(da.all().toArray());
    }

    public List<HashMap> listaPersonaCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoPersona dao = new DaoPersona();
        if (!da.listAll().isEmpty()) {
            Persona[] arreglo = dao.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getUsuario());
                lista.add(aux);
            }
        }
        return lista;
    }

    public void delete(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("ID de pago inválido");
        }
        if (!da.deleteCuenta(id)) {
            throw new Exception("No se pudo eliminar el pago con ID: " + id);
        }
    }

}