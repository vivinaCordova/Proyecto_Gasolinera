package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Vehiculo;
import org.unl.gasolinera.base.models.Persona;

public class DaoVehiculo extends AdapterDao<Vehiculo> {
    private Vehiculo obj;

    public DaoVehiculo(){
        super(Vehiculo.class);
    }

    public Vehiculo getObj() {
        if(obj==null)
            this.obj=new Vehiculo();
        return this.obj;
    }

    
    public void setObj(Vehiculo obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength()+1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            return false;
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
            Vehiculo[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, Object> toDict(Vehiculo arreglo, Integer i) {
        HashMap<String, Object> aux = new HashMap<>();
        DaoPersona dp = new DaoPersona();
        
        aux.put("id", arreglo.getId().toString());
        aux.put("placa", arreglo.getPlaca());
        aux.put("modelo", arreglo.getModelo());
        aux.put("marca", arreglo.getMarca());
        
        String nombrePropietario = "Sin propietario";
        if (arreglo.getIdPropietario() != null) {
            try {
                if (!dp.listAll().isEmpty()) {
                    Persona[] personas = dp.listAll().toArray();
                    for (Persona persona : personas) {
                        if (persona.getId() != null && persona.getId().equals(arreglo.getIdPropietario())) {
                            nombrePropietario = persona.getUsuario();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al buscar propietario: " + e.getMessage());
                nombrePropietario = "Error al cargar propietario";
            }
        }
        
        aux.put("propietario", nombrePropietario);
        return aux;
    }

    public LinkedList<HashMap<String, Object>> orderByVehiculo(Integer type, String attribute) throws Exception {
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
            lista = orderByVehiculo(Utiles.ASCENDENTE, attribute);
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