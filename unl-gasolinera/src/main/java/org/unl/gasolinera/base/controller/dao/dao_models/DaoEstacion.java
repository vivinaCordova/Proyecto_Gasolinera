package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Estacion;

public class DaoEstacion extends AdapterDao<Estacion> {

    private Estacion obj;

    public DaoEstacion() {
        super(Estacion.class);
    }

    public Estacion getObj() {
        if (obj == null) {
            this.obj = new Estacion();
        }
        return this.obj;
    }

    public void setObj(Estacion obj) {
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

    public Boolean update(Integer pos) {
        try {
            this.update(obj, pos);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            //LOG DE ERROR
            return false;
        }
    }

    public void quickSort(Estacion arr[], int low, int high, Integer type) {
        if (low < high) {
            int pi = partition(arr, low, high, type);
            quickSort(arr, low, pi - 1, type);
            quickSort(arr, pi + 1, high, type);
        }
    }

    private int partition(Estacion[] arr, int low, int high, Integer type) {
        Estacion pivot = arr[high];
        int i = (low - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = low; j < high; j++) {
                if (arr[j].getCodigo().toLowerCase().compareTo(pivot.getCodigo().toLowerCase()) < 0) {
                    i++;
                    Estacion temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        } else {
            for (int j = low; j < high; j++) {
                if (arr[j].getCodigo().toLowerCase().compareTo(pivot.getCodigo().toLowerCase()) > 0) {
                    i++;
                    Estacion temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        Estacion temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;

    }

    public LinkedList<HashMap<String, Object>> all() {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            Estacion[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }


    private HashMap<String, Object> toDict(Estacion arreglo, Integer i) {
        HashMap<String, Object> aux = new HashMap<>();
        aux.put("id", arreglo.getId());
        aux.put("codigo", arreglo.getCodigo());
        aux.put("estado", arreglo.getEstadoE().toString());
        return aux;
    }

    public LinkedList<HashMap<String, Object>> orderbyEstacion(Integer type, String attribute) {
        LinkedList<HashMap<String, Object>> lista = all();
        if (!lista.isEmpty()){
          Estacion[] Estaciones = this.listAll().toArray();
          quickSort(Estaciones, 0, Estaciones.length - 1, type);
          lista = new LinkedList<>();
          for (int i = 0; i < Estaciones.length; i++) {
            lista.add(toDict(Estaciones[i], i));
           }
        }
        return lista;
    }


    public Estacion getById(Integer id) {
        if (!this.listAll().isEmpty()) {
            Estacion[] arreglo = this.listAll().toArray();
            for (Estacion e : arreglo) {
                if (e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public Boolean deleteEstacion(Integer id) {
        try {
            super.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
    }

}
