package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
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

     public void quickSort(OrdenDespacho arr[], int low, int high, Integer type) {
        if (low < high) {
            int pi = partition(arr, low, high, type);
            quickSort(arr, low, pi - 1, type);
            quickSort(arr, pi + 1, high, type);
        }
    }

    private int partition(OrdenDespacho[] arr, int low, int high, Integer type) {
        OrdenDespacho pivot = arr[high];
        int i = (low - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = low; j < high; j++) {
                if (arr[j].getCodigo().toLowerCase().compareTo(pivot.getCodigo().toLowerCase()) < 0) {
                    i++;
                    OrdenDespacho temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        } else {
            for (int j = low; j < high; j++) {
                if (arr[j].getCodigo().toLowerCase().compareTo(pivot.getCodigo().toLowerCase()) > 0) {
                    i++;
                    OrdenDespacho temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        OrdenDespacho temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;

    }

    public LinkedList<HashMap<String, String>> all() {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        try {
            if (!this.listAll().isEmpty()) {
                OrdenDespacho[] arreglo = this.listAll().toArray();
                for (int i = 0; i < arreglo.length; i++) {
                    lista.add(toDict(arreglo[i]));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return lista;
    }

    private HashMap<String, String> toDict(OrdenDespacho arreglo) throws Exception{
        HashMap<String, String> aux = new HashMap<>();
        DaoVehiculo dv = new DaoVehiculo();
        DaoPrecioEstablecido dp = new DaoPrecioEstablecido();
        DaoEstacion de = new DaoEstacion();
        
        if (arreglo.getIdVehiculo() != null) {
            try {
                dv.setObj(getObjectById(dv, arreglo.getIdVehiculo()));
            } catch (Exception e) {
                dv.setObj(dv.get(arreglo.getIdVehiculo()));
            }
        }
        if (arreglo.getIdPrecioEstablecido() != null) {
            try {
                dp.setObj(getObjectById(dp, arreglo.getIdPrecioEstablecido()));
            } catch (Exception e) {
                dp.setObj(dp.get(arreglo.getIdPrecioEstablecido()));
            }
        }
        if (arreglo.getIdEstacion() != null) {
            try {
                de.setObj(getObjectById(de, arreglo.getIdEstacion()));
            } catch (Exception e) {
                de.setObj(de.get(arreglo.getIdEstacion()));
            }
        }
        
        aux.put("id", arreglo.getId().toString());
        aux.put("codigo", arreglo.getCodigo());
        aux.put("nroGalones", arreglo.getNroGalones().toString());
        aux.put("fecha", arreglo.getFecha().toString());
        aux.put("precioTotal", Float.toString(arreglo.getPrecioTotal()));
        aux.put("estado", arreglo.getEstado().toString());
        

        if (dp.getObj() != null) {
            aux.put("precio_establecido", Float.toString(dp.getObj().getPrecio()));
            aux.put("tipo_combustible", dp.getObj().getTipoCombustible().toString());
        } else {
            aux.put("precio_establecido", "N/A");
            aux.put("tipo_combustible", "N/A");
        }
        
        aux.put("idVehiculo", arreglo.getIdVehiculo().toString());
        aux.put("estacion", de.getObj() != null ? de.getObj().getCodigo() : "N/A");
        aux.put("placa", dv.getObj() != null ? dv.getObj().getPlaca() : "N/A");

        return aux;
    }

    private <T> T getObjectById(AdapterDao<T> dao, Integer id) throws Exception {
        if (dao.listAll().isEmpty()) {
            return null;
        }
        
        T[] array = dao.listAll().toArray();
        for (T obj : array) {
            try {
                java.lang.reflect.Method getIdMethod = obj.getClass().getMethod("getId");
                Integer objId = (Integer) getIdMethod.invoke(obj);
                if (objId != null && objId.equals(id)) {
                    return obj;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }



    public LinkedList<HashMap<String, String>> orderbyOrdenDespacho(Integer type, String attribute) {
        LinkedList<HashMap<String, String>> lista = all();
        try {
            if (!lista.isEmpty()){
                // Convert HashMap array back to OrdenDespacho array for sorting
                OrdenDespacho[] OrdenDespachos = this.listAll().toArray();
                quickSort(OrdenDespachos, 0, OrdenDespachos.length - 1, type);
                // Update lista with sorted OrdenDespachos
                lista = new LinkedList<>();
                for (OrdenDespacho OrdenDespacho : OrdenDespachos) {
                  lista.add(toDict(OrdenDespacho));
                 }
              }
        } catch (Exception e) {
        }
        return lista;
    }

    

} 