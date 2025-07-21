package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.PrecioEstablecido;

public class DaoPrecioEstablecido extends AdapterDao<PrecioEstablecido> {
    private PrecioEstablecido obj;

    public DaoPrecioEstablecido() {
        super(PrecioEstablecido.class);
    }

    public PrecioEstablecido getObj() {
        if (obj == null)
            this.obj = new PrecioEstablecido();
        return this.obj;
    }

    public void setObj(PrecioEstablecido obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            // TODO
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
            // LOG DE ERROR
            return false;
        }
    }

    public void quickSort(PrecioEstablecido arr[], int low, int high, Integer type) {
        if (low < high) {
            int pi = partition(arr, low, high, type);
            quickSort(arr, low, pi - 1, type);
            quickSort(arr, pi + 1, high, type);
        }
    }

    private int partition(PrecioEstablecido[] arr, int low, int high, Integer type) {
        PrecioEstablecido pivot = arr[high];
        int i = (low - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = low; j < high; j++) {
                if (arr[j].getTipoCombustible().toString().compareTo(pivot.getTipoCombustible().toString()) < 0) {
                    i++;
                    PrecioEstablecido temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        } else {
            for (int j = low; j < high; j++) {
                if (arr[j].getTipoCombustible().toString().compareTo(pivot.getTipoCombustible().toString()) > 0) {
                    i++;
                    PrecioEstablecido temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        PrecioEstablecido temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;

    }

    public LinkedList<HashMap<String, String>> all() {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, String> toDict(PrecioEstablecido precioEstablecido, Integer i) {
        HashMap<String, String> aux = new HashMap<>();
        aux.put("id", precioEstablecido.getId().toString());
        aux.put("fecha", precioEstablecido.getFecha().toString());
        aux.put("fechaFin", precioEstablecido.getFechaFin().toString());
        aux.put("estado", String.valueOf(precioEstablecido.isEstado()));
        aux.put("precio", String.valueOf(precioEstablecido.getPrecio()));
        aux.put("tipoCombustible", precioEstablecido.getTipoCombustible().toString());
        return aux;
    }

    public LinkedList<HashMap<String, String>> orderByPrecioEstablecido(Integer type, String attribute) {
        LinkedList<HashMap<String, String>> lista = all();
        if (!lista.isEmpty()) {
            // Convert HashMap array back to PrecioEstablecido array for sorting
            PrecioEstablecido[] PrecioEstablecidoes = this.listAll().toArray();
            quickSort(PrecioEstablecidoes, 0, PrecioEstablecidoes.length - 1, type);
            // Update lista with sorted PrecioEstablecidoes
            lista = new LinkedList<>();
            for (int i = 0; i < PrecioEstablecidoes.length; i++) {
                lista.add(toDict(PrecioEstablecidoes[i], i));
            }
        }
        return lista;
    }

    public PrecioEstablecido getById(Integer id) {
        if (!this.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = this.listAll().toArray();
            for (PrecioEstablecido e : arreglo) {
                if (e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }

}