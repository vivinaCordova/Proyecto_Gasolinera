package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.Date;
import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;


public class DaoOrdenDespacho extends AdapterDao<OrdenDespacho> {
    private OrdenDespacho obj;

    public DaoOrdenDespacho(){
        super(OrdenDespacho.class);
    }

    public OrdenDespacho getObj() {
        if(obj==null)
            this.obj=new OrdenDespacho();
        return this.obj;
    }

    
    public void setObj(OrdenDespacho obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            //TODO
            return false;
            // TODO: handle exception
        }
    }

    public Boolean update(Integer pos){
        try{
            obj.setId(listAll().getLength());
            this.update(obj, pos);
            return true;

        }catch(Exception e){
            e.printStackTrace(); 
            System.out.println(e);
            //LOG DE ERROR
            return false;
        }
    }


    public void quickSort(HashMap arr[], int begin, int end, Integer type, String attribute) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end, type, attribute);

            quickSort(arr, begin, partitionIndex - 1, type, attribute);
            quickSort(arr, partitionIndex + 1, end, type, attribute);
        }
    }

    private int partition(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        HashMap<String, Object> pivot = arr[end];
        int i = (begin - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString().compareTo(pivot.get(attribute).toString()) < 0) {

                    i++;
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        } else {
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString().compareTo(pivot.get(attribute).toString()) > 0) {
                    i++;
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        }
        HashMap<String, Object> swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    public LinkedList<HashMap<String, Object>> all() throws Exception {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            OrdenDespacho[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }
    
    private HashMap<String, Object> toDict(OrdenDespacho orden, Integer i) throws Exception {
        HashMap<String, Object> aux = new HashMap<>();
        DaoEstacion de= new DaoEstacion();
        DaoPrecioEstablecido dpe = new DaoPrecioEstablecido();
        DaoVehiculo dv = new DaoVehiculo();
        aux.put("id", orden.getId().toString(i));
        aux.put("nroGalones", orden.getNroGalones());
        aux.put("codigo", orden.getCodigo());
        aux.put("fecha", orden.getFecha().toString());
        aux.put("precioTotal", orden.getPrecioTotal());
        aux.put("estado", orden.getEstado().toString());

        aux.put("PrecioEstablecido", dpe.listAll().get(orden.getIdPrecioEstablecido()).getTipoCombustible().toString());
        aux.put("Vehiculo", dv.listAll().get(orden.getIdVehiculo()).getPlaca());
        aux.put("Estacion", de.listAll().get(orden.getIdEstacion()).getCodigo());
        return aux;
        
    }

    public LinkedList<HashMap<String, Object>> orderByOrdenDespacho(Integer type, String attribute) throws Exception {
        LinkedList<HashMap<String, Object>> lista = all();
        if (!listAll().isEmpty()) {
            HashMap arr[] = lista.toArray();
            quickSort(arr, 0, arr.length - 1, type, attribute);
            lista.toList(arr);
        }
        return lista;
    }

    
    private Integer bynaryLineal(HashMap<String, Object>[] arr, String attribute, String text) {
        Integer half = 0;
        if (!(arr.length == 0) && !text.isEmpty()) {
            half = arr.length / 2;
            int aux = 0;
            if (text.trim().toLowerCase().charAt(0) > arr[half].get(attribute).toString().trim().toLowerCase().charAt(0)) {
                aux = 1;
            } else if (text.trim().toLowerCase().charAt(0) < arr[half].get(attribute).toString().trim().toLowerCase().charAt(0)) {
                aux = -1;
            }

            half = half * aux;

        }
        return half;
    }

    public LinkedList<HashMap<String, Object>> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = all();
        LinkedList<HashMap<String, Object>> resp = new LinkedList<>();
        if (!lista.isEmpty()) {
            lista = orderByOrdenDespacho(Utiles.ASCENDENTE, attribute);
            HashMap<String, Object>[] arr = lista.toArray();
            Integer n = bynaryLineal(arr, attribute, text);
            switch (type) {
                case 1:
                    if (n > 0) {
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else if (n < 0) {
                        n *= -1;
                        for (int i = 0; i < n; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    }
                    break;
                case 2:
                    if (n > 0) {
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else if (n < 0) {
                        n *= -1;
                        for (int i = 0; i < n; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    }
                    break;
                default:
          
                    for (int i = 0; i < arr.length; i++) {
                        if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                            resp.add(arr[i]);
                        }
                    }
                    break;

            }
        }
        return resp;
    }



    public static void main(String[] args) {
        DaoOrdenDespacho da= new DaoOrdenDespacho();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCodigo("a001");
        da.getObj().setNroGalones(9.2f);
        da.getObj().setFecha(new Date());
        da.getObj().setPrecioTotal(20.6f); 
        da.getObj().setEstado(EstadoOrdenDespachadoEnum.COMPLETADO);
        da.getObj().setIdVehiculo(1);

        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
    }

 
}