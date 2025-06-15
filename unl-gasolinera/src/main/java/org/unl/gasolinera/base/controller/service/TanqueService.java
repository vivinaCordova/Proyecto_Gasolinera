package org.unl.gasolinera.base.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenDespacho;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

import jakarta.validation.constraints.NotEmpty;
import org.unl.gasolinera.base.models.Tanque;

public class TanqueService {
    private DaoTanque db;
    private OrdenCompraService OrdenCompraService;
    public TanqueService(){
        db = new DaoTanque();
        OrdenCompraService = new OrdenCompraService(); 
    }

    
    public float calcularStockActual(int idTanque) throws Exception {
    Object[] tanques = db.listAll().toArray();

    for (Object obj : tanques) {
        Tanque tanque = (Tanque) obj;
        if (tanque.getId() == idTanque) {
            return tanque.getCapacidad(); 
        }
    }
    throw new Exception("Tanque Id " + idTanque + " no encontrado.");
    }
    public String enviarAlerta(){
        boolean hayAlerta = false;
        Object[] tanquesArray = db.listAll().toArray();
        for (Object obj : tanquesArray) {
            Tanque t = (Tanque) obj;
            if (t.getCapacidad() < t.getCapacidadMinima()) {
                System.out.println("¡Alerta!Tanque"+t.getId()+"capacidad mínima");
                hayAlerta = true;
            }
        }
    
        if (hayAlerta) {

            System.out.println("Enviando alerta al proveedor...");
            boolean envioExitoso = true;  
    
            if (envioExitoso) {
                return "Alerta enviada correctamente al proveedor.";
            } else {
                return "No se pudo enviar la alerta al proveedor.";
            }
        } else {
            return "Todos los tanques están por encima del nivel mínimo.";
        }
    }
    
    public void aumentarStock(int idTanque) throws Exception {
        Object[] tanquesArray = db.listAll().toArray();

        for (Object obj : tanquesArray) {
            Tanque t = (Tanque) obj;

            if (t.getId() == idTanque) {
                float capacidadMaxima = t.getCapacidadTotal();
                float capacidadActual = t.getCapacidad();
                float objetivo = capacidadMaxima * 0.8f;

                if (capacidadActual >= objetivo) {
                    System.out.println("El tanque ya está al 80% o más de su capacidad.");
                    return;
                }

                float cantidadAumentar = objetivo - capacidadActual;

                DaoOrdenCompra daoOrden = new DaoOrdenCompra();
                OrdenCompra orden = new OrdenCompra();
                orden.setCantidad(cantidadAumentar);
                orden.setEstado(EstadoOrdenCompraEnum.COMPLETADO);

                daoOrden.setObj(orden);
                if (!daoOrden.save()) {
                    throw new Exception("No se pudo guardar la orden de compra.");
                }


                float nuevaCapacidad = capacidadActual + cantidadAumentar;
                if (nuevaCapacidad > capacidadMaxima) {
                    nuevaCapacidad = capacidadMaxima;
                }
                t.setCapacidad(nuevaCapacidad);


                t.setIdOrdenCompra(daoOrden.getObj().getId());
                db.update(t, idTanque);

                System.out.println("✔ Orden de compra generada por " + cantidadAumentar +
                        " galones para tanque ID " + idTanque +
                        " (orden ID: " + daoOrden.getObj().getId() + ")");
                System.out.println("✔ Nuevo stock del tanque: " + nuevaCapacidad + " galones.");
                return;
            }
        }

        throw new Exception("Tanque con ID " + idTanque + " no encontrado.");
    }
    public float reducirStock(OrdenDespacho despacho) throws Exception {
        
        int idTanque = despacho.getIdEstacion(); 
    
        float galonesDespachados = despacho.getNroGalones(); 
        Object[] tanques = db.listAll().toArray();
    
        for (Object obj : tanques) {
            Tanque t = (Tanque) obj;
    
            if (t.getId() == idTanque) {
                float capacidadActual = t.getCapacidad();
                float nuevaCapacidad = capacidadActual - galonesDespachados;
    
                if (nuevaCapacidad < 0) {
                    throw new Exception("Stock insuficiente. El tanque solo tiene "
                                        + capacidadActual + " galones.");
                }
    
                t.setCapacidad(nuevaCapacidad);
                db.update(t, idTanque);
    
                System.out.println("✔ Stock reducido en tanque ID " + idTanque
                                 + ": -" + galonesDespachados + " galones. Nuevo stock: " + nuevaCapacidad);
    
                return nuevaCapacidad;
            }
        }
    
        throw new Exception("Tanque con ID " + idTanque + " no encontrado.");
    }
    public void createTanque(float capacidad, float capacidadTotal, float capacidadMinima,@NotEmpty String tipo, @NotEmpty String estado, Integer idOrden, Integer idOrdenCompra) throws Exception {
        if(tipo.trim().length() > 0 && estado.toString().length() > 0 && capacidad > 0 && capacidadTotal >0 && capacidadMinima>0 && idOrden > 0 && idOrdenCompra > 0) {
            db.getObj().setCapacidad(capacidad);
            db.getObj().setCapacidadMinima(capacidadMinima);
            db.getObj().setCapacidadTotal(capacidadTotal);
            db.getObj().setEstado(EstadoOrdenCompraEnum.valueOf(estado));
            db.getObj().setIdOrden(idOrden);
            db.getObj().setIdOrdenCompra(idOrdenCompra);
            db.getObj().setTipo(TipoCombustibleEnum.valueOf(tipo));

            if(!db.save())
                throw new  Exception("No se pudo guardar los datos de la Tanque");
        }
    }
    
   public List<HashMap> listOrdenDespachoCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenDespacho da = new DaoOrdenDespacho();
        if(!db.listAll().isEmpty()) {
            OrdenDespacho [] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNroGalones().toString());
                lista.add(aux); 
            }

        }
        return lista;
    }
    public List<HashMap> listOrdenCopraCombo(){
        List<HashMap> lista = new ArrayList<>();
        DaoOrdenCompra da = new DaoOrdenCompra();
        if(!db.listAll().isEmpty()) {
            OrdenCompra [] arreglo = da.listAll().toArray();
            for(int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", String.valueOf(arreglo[i].getCantidad()).toString());
                lista.add(aux); 
            }

        }
        return lista;
    }
    public List<String> listTipo() {
        List<String> lista = new ArrayList<>();
        for(TipoCombustibleEnum r: TipoCombustibleEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }
    public List<String> listEstado() {
        List<String> lista = new ArrayList<>();
        for(EstadoOrdenCompraEnum r: EstadoOrdenCompraEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }

    public List<HashMap> listTanque(){
        List<HashMap> lista = new ArrayList<>();
        if(!db.listAll().isEmpty()) {
            Tanque [] arreglo = db.listAll().toArray();
           
            for(int i = 0; i < arreglo.length; i++) {
                
                HashMap<String, String> aux = new HashMap<>();
                aux.put("id", arreglo[i].getId().toString());; 
                aux.put("capacidad",  String.valueOf(arreglo[i].getCapacidad()));
                aux.put("canpacidadTotal", String.valueOf(arreglo[i].getCapacidadTotal()));   
                aux.put("canpacidadMinima", String.valueOf(arreglo[i].getCapacidadMinima())); 
                aux.put("tipo", arreglo[i].getTipo().toString());
                aux.put("ordenCompra",String.valueOf(new DaoOrdenCompra().listAll().get(arreglo[i].getIdOrdenCompra()-1).getCantidad()));
                aux.put("id_ordenCompra",new DaoOrdenCompra().listAll().get(arreglo[i].getIdOrdenCompra()-1).toString());
                aux.put("ordenDespacho",String.valueOf(new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrden()-1).getNroGalones()));
                aux.put("id_OrdenDespacho",new DaoOrdenDespacho().listAll().get(arreglo[i].getIdOrden()-1).toString());
                lista.add(aux);
            }
        }
        return lista;
    }

    
}