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

    public LinkedList<HashMap<String, Object>> all() {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            PrecioEstablecido[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, Object> toDict(PrecioEstablecido precioEstablecido, Integer i) {
        HashMap<String, Object> aux = new HashMap<>();
        aux.put("id", precioEstablecido.getId().toString());
        aux.put("fecha", precioEstablecido.getFecha().toString());
        aux.put("fechaFin", precioEstablecido.getFechaFin().toString());
        aux.put("estado", String.valueOf(precioEstablecido.isEstado()));
        aux.put("precio", String.valueOf(precioEstablecido.getPrecio()));
        aux.put("tipoCombustible", precioEstablecido.getTipoCombustible().toString());
        return aux;
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

    public LinkedList<HashMap<String, Object>> orderByPrecioEstablecido(Integer type, String attribute) throws Exception {
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
            lista = orderByPrecioEstablecido(Utiles.ASCENDENTE, attribute);
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
                    /*
                     * if (n > 0) {
                     * for (int i = n; i < arr.length; i++) {
                     * if
                     * (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase()
                     * )) {
                     * resp.add(arr[i]);
                     * }
                     * }
                     * } else if (n < 0) {
                     * n *= -1;
                     * for (int i = 0; i < n; i++) {
                     * if
                     * (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase()
                     * )) {
                     * resp.add(arr[i]);
                     * }
                     * }
                     * } else {
                     * for (int i = 0; i < arr.length; i++) {
                     * if
                     * (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase()
                     * )) {
                     * resp.add(arr[i]);
                     * }
                     * }
                     * }
                     */
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
}