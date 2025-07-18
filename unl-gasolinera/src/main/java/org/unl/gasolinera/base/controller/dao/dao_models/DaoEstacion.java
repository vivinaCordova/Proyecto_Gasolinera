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
            obj.setId(listAll().getLength());
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

    public LinkedList<HashMap<String, String>> all() {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            Estacion[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }


    private HashMap<String, String> toDict(Estacion arreglo, Integer i) {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, String> aux = new HashMap<>();
        aux.put("id", arreglo.getId().toString(i));
        aux.put("codigo", arreglo.getCodigo());
        aux.put("estado", arreglo.getEstadoE().toString());
        return aux;
    }

    public LinkedList<HashMap<String, String>> orderbyEstacion(Integer type, String attribute) {
        LinkedList<HashMap<String, String>> lista = all();
        if (!lista.isEmpty()){
          HashMap arr[] = lista.toArray();
          // Convert HashMap array back to Estacion array for sorting
          Estacion[] Estaciones = this.listAll().toArray();
          quickSort(Estaciones, 0, Estaciones.length - 1, type);
          // Update lista with sorted Estaciones
          lista = new LinkedList<>();
          for (Estacion Estacion : Estaciones) {
            lista.add(toDict(Estacion, type));
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
    


}
