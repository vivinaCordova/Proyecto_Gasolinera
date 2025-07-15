package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.Tanque;

public class DaoOrdenCompra extends AdapterDao<OrdenCompra> {
    private OrdenCompra obj;

    public DaoOrdenCompra() {
        super(OrdenCompra.class);
    }

    public OrdenCompra getObj() {
        if (obj == null)
            this.obj = new OrdenCompra();
        return this.obj;
    }

    public void setObj(OrdenCompra obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            DaoTanque daoTanque = new DaoTanque();
            Tanque tanque = daoTanque.listAll().get(obj.getIdTanque() - 1); // Ajusta según ID/posicion real

            // 2. Calcular nuevo stock
            float stockActual = tanque.getCapacidad();
            float cantidadOrden = obj.getCantidad();

            if (stockActual < cantidadOrden) {
                System.out.println("Error: No hay suficiente stock en el tanque para la orden.");
                return false;
            }

            tanque.setCapacidad(stockActual - cantidadOrden);

            // 3. Actualizar tanque
            daoTanque.update(tanque, obj.getIdTanque() - 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            obj.setId(listAll().getLength() + 1);

            this.update(obj, pos);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            // LOG DE ERROR
            return false;
        }
    }

    public LinkedList<HashMap<String, Object>> all() throws Exception {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            OrdenCompra[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, Object> toDict(OrdenCompra arreglo, Integer i) {
        DaoProveedor db = new DaoProveedor();
        DaoTanque da = new DaoTanque();
        HashMap<String, Object> aux = new HashMap<>();
        aux.put("id", arreglo.getId().toString());
        aux.put("cantidad", arreglo.getCantidad());
        aux.put("estado", arreglo.getEstado().toString());
        aux.put("proveedor", db.listAll().get(arreglo.getIdProveedor() - 1).getNombre());
        aux.put("tanque", da.listAll().get(arreglo.getIdTanque() - 1).getCodigo());

        return aux;
    }

    public LinkedList<HashMap<String, Object>> orderByOrdenCompra(Integer type, String attribute) throws Exception {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!all().isEmpty()) {
            HashMap<String, Object> arr[] = all().toArray();
            quickSort(arr, 0, arr.length - 1, type, attribute);
            lista.toList(arr);
        }
        return lista;
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
                if (arr[j].get(attribute).toString().compareTo(pivot.get(attribute).toString()) < 0) {
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

    public LinkedList<HashMap<String, Object>> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = all();
        LinkedList<HashMap<String, Object>> resp = new LinkedList<>();

        if (!lista.isEmpty()) {
            lista = orderByOrdenCompra(Utiles.ASCENDENTE, attribute);
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
                        for (int i = 0; i < n; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    }
                    break;

                case 2:
                    if (n > 0) {
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().endsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else if (n < 0) {
                        n *= -1;
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().endsWith(text.toLowerCase())) {

                                resp.add(arr[i]);
                            }
                        }
                    } else {
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().endsWith(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    }
                    break;

                default:
                    System.out.println(attribute + " " + text + " TRES " + n);
                    if (n > 0) {
                        for (int i = n; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().contains(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else if (n < 0) {
                        n *= -1;
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().contains(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].get(attribute).toString().toLowerCase().contains(text.toLowerCase())) {
                                resp.add(arr[i]);
                            }
                        }
                    }
                    break;
            }
        }
        return resp;
    }

    private Integer bynaryLineal(HashMap<String, Object>[] array, String attribute, String text)
            throws Exception {
        Integer half = 0;
        if (!(array.length == 0) && !text.isEmpty()) {
            half = array.length / 2;
            int aux = 0;
            if (text.trim().toLowerCase().charAt(0) > array[half].get(attribute).toString().trim().toLowerCase()
                    .charAt(0))
                aux = 1;
            else if (text.trim().toLowerCase().charAt(0) < array[half].get(attribute).toString().trim().toLowerCase()
                    .charAt(0))
                aux = -1;

            half = half * aux;

        }
        return half;
    }

    public void quickSort(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end, type, attribute);
            quickSort(arr, begin, partitionIndex - 1, type, attribute);
            quickSort(arr, partitionIndex + 1, end, type, attribute);
        }
    }

    public Boolean aumentarStock(float cantidad) {
        try {
            DaoTanque daoTanque = new DaoTanque();
            LinkedList<Tanque> tanques = daoTanque.listAll();
    
            if (tanques.isEmpty()) {
                System.out.println("No hay tanques registrados.");
                return false; // Retorna false si no hay tanques registrados
            }
    
            boolean actualizado = false; // Variable para verificar si se actualizó algún tanque
    
            for (int i = 0; i < tanques.getLength(); i++) {
                Tanque tanque = tanques.get(i);
    
                if (tanque.getCapacidad() <= tanque.getCapacidadMinima()) {
                    System.out.println("Tanque " + tanque.getCodigo() + " con capacidad actual " + tanque.getCapacidad() +
                            " está por debajo de la capacidad mínima " + tanque.getCapacidadMinima() + ". Actualizando...");
    
                    // Aumentar el stock del tanque
                    tanque.setCapacidad(tanque.getCapacidad() + cantidad);
                    daoTanque.update(tanque, i);
    
                    System.out.println("Stock aumentado exitosamente en el tanque " + tanque.getCodigo() +
                            ". Nueva capacidad: " + tanque.getCapacidad());
                    actualizado = true; // Indica que se realizó una actualización
                } else {
                    System.out.println("Tanque " + tanque.getCodigo() + " está en condiciones seguras.");
                }
            }
    
            return actualizado; // Retorna true si se actualizó al menos un tanque
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al verificar y actualizar los tanques.");
            return false; // Retorna false en caso de excepción
        }
    }
}