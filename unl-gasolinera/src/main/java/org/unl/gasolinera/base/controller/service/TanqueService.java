package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.Tanque;

@BrowserCallable
@AnonymousAllowed
public class TanqueService {
    private DaoTanque db;

    public TanqueService() {
        db = new DaoTanque();
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<Tanque> listAlla() {
        return (List<Tanque>) Arrays.asList(db.listAll().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByTanque(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty())
            return Arrays.asList(lista.toArray());
        else
            return new ArrayList<>();
    }

    public List<String> obtenerAlertasTanques() {
        List<String> mensajes = new ArrayList<>();
        try {
            String[] mensajesArray = db.enviarAlerta(); // Llama al método del DAO
            for (String mensaje : mensajesArray) {
                mensajes.add(mensaje); // Agrega cada mensaje al List
            }
        } catch (Exception e) {
            e.printStackTrace();
            mensajes.add("Error al obtener alertas de los tanques: " + e.getMessage());
        }
        return mensajes;
    }

    public boolean aumentarStock(String codigoTanque) {
        try {
            DaoTanque daoTanque = new DaoTanque();
            LinkedList<Tanque> tanques = daoTanque.listAll();
    
            Tanque tanqueEncontrado = null;
            int posicionTanque = -1;
    
            for (int i = 0; i < tanques.getLength(); i++) {
                Tanque t = tanques.get(i);
                if (t.getCodigo().equals(codigoTanque)) {
                    tanqueEncontrado = t;
                    posicionTanque = i;
                    break;
                }
            }
    
            if (tanqueEncontrado == null) {
                System.out.println("No se encontró el tanque con código: " + codigoTanque);
                return false;
            }
    
            if (tanqueEncontrado.getCapacidad() > tanqueEncontrado.getCapacidadMinima()) {
                System.out.println("El tanque tiene suficiente capacidad.");
                return false;
            }
    
            float cantidadReposicion = tanqueEncontrado.getCapacidadMinima() * 0.2f;
    
            // Crear la orden de compra a través del servicio
            OrdenCompraService ordenCompraService = new OrdenCompraService();
            boolean ordenCreada = ordenCompraService.createOrdenCompra(tanqueEncontrado, cantidadReposicion);
    
            if (!ordenCreada) {
                System.out.println("No se pudo crear la orden de compra.");
                return false;
            }
    
            // Aumentar la capacidad del tanque
            tanqueEncontrado.setCapacidad(tanqueEncontrado.getCapacidad() + cantidadReposicion);
    
            // Actualizar el tanque en la lista DAO
            daoTanque.update(tanqueEncontrado, posicionTanque);
    
            System.out.println("Stock del tanque " + tanqueEncontrado.getCodigo() +
                    " aumentado a " + tanqueEncontrado.getCapacidad());
    
            return true;
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al aumentar stock.");
            return false;
        }
    }
    
    public void createTanque(float capacidad, float capacidadTotal, float capacidadMinima, Integer tipoCombustible,
            @NotEmpty String codigo) throws Exception {
        if (codigo.trim().length() > 0 && capacidad > 0 && capacidadTotal > 0 && capacidadMinima > 0
                && tipoCombustible > 0 && codigo.toString().length() > 0) {
            db.getObj().setCodigo(codigo);
            ;
            db.getObj().setCapacidad(capacidad);
            db.getObj().setCapacidadMinima(capacidadMinima);
            db.getObj().setCapacidadTotal(capacidadTotal);
            db.getObj().setIdPrecioEstablecido(tipoCombustible);
            if (!db.save())
                throw new Exception("No se pudo guardar los datos de la Tanque");
        }
    }

    public List<HashMap> listTipo() {
        List<HashMap> lista = new ArrayList<>();
        DaoPrecioEstablecido da = new DaoPrecioEstablecido();
        if (!db.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString());
                aux.put("label", arreglo[i].getTipoCombustible().toString());
                lista.add(aux);
            }

        }
        return lista;
    }
}