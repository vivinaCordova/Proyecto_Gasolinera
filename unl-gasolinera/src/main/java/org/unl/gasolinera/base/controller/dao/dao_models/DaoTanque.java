package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.OrdenDespacho;
import org.unl.gasolinera.base.models.Pago;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.Proveedor;
import org.unl.gasolinera.base.models.Tanque;

public class DaoTanque extends AdapterDao<Tanque> {

    private Tanque obj;

    public DaoTanque() {
        super(Tanque.class);
    }

    public Tanque getObj() {
        if (obj == null)
            this.obj = new Tanque();
        return this.obj;
    }

    public void setObj(Tanque obj) {
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

    public Boolean listar() {
        try {
            this.listAll();
            for (int i = 0; i > this.listAll().getLength(); i++) {
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
    }

    public LinkedList<HashMap<String, Object>> all() throws Exception {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            Tanque[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, Object> toDict(Tanque arreglo, Integer i) throws Exception {
        HashMap<String, Object> aux = new HashMap<>();
        aux.put("id", arreglo.getId());
        aux.put("codigo", arreglo.getCodigo().toString());
        aux.put("capacidad", arreglo.getCapacidad());
        aux.put("capacidadTotal", arreglo.getCapacidadTotal());
        aux.put("capacidaddMinima", arreglo.getCapacidadMinima());
        aux.put("tipo", arreglo.getTipo().toString());
        return aux;
    }

    public LinkedList<HashMap<String, Object>> orderByTanque(Integer type, String attribute) throws Exception {
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
            lista = orderByTanque(Utiles.ASCENDENTE, attribute);
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

    public String[] enviarAlerta() {
        List<String> mensajes = new ArrayList<>();
        try {
            LinkedList<HashMap<String, Object>> tanquesOrdenados = orderByTanque(Utiles.ASCENDENTE, "capacidad");

            if (!tanquesOrdenados.isEmpty()) {
                HashMap<String, Object>[] tanquesArray = tanquesOrdenados.toArray(); // Usar el método toArray
                                                                                     // implementado

                for (HashMap<String, Object> tanqueData : tanquesArray) {
                    Tanque tanque = listAll().get((Integer) tanqueData.get("id") - 1);

                    if (tanque.getCapacidad() <= tanque.getCapacidadMinima()) {
                        mensajes.add("Tanque " + tanque.getCodigo() +
                                " capacidad actual " + tanque.getCapacidad() +
                                " está por debajo del mínimo " + tanque.getCapacidadMinima());
                    } else {
                        mensajes.add("Tanque " + tanque.getCodigo() + " sin riesgos");
                    }
                }
            } else {
                mensajes.add("No hay tanques registrados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mensajes.add("Error al enviar alertas para los tanques.");
        }

        return mensajes.toArray(new String[0]); // Convertir la lista de mensajes a un array
    }

    public Boolean aumentarStock() {
        try {
            DaoTanque daoTanque = new DaoTanque();
            DaoProveedor daoProveedor = new DaoProveedor();
            DaoOrdenCompra daoOrdenCompra = new DaoOrdenCompra();

            LinkedList<Tanque> tanques = daoTanque.listAll();
            LinkedList<Proveedor> proveedores = daoProveedor.listAll();

            if (tanques.isEmpty() || proveedores.isEmpty()) {
                System.out.println("No hay tanques o proveedores registrados.");
                return false; // Retorna false si no hay tanques o proveedores registrados
            }

            boolean actualizado = false; // Variable para verificar si se actualizó algún tanque

            for (int i = 0; i < tanques.getLength(); i++) {
                Tanque tanque = tanques.get(i);

                if (tanque.getCapacidad() <= tanque.getCapacidadMinima()) {
                    System.out
                            .println("Tanque " + tanque.getCodigo() + " con capacidad actual " + tanque.getCapacidad() +
                                    " está por debajo de la capacidad mínima " + tanque.getCapacidadMinima()
                                    + ". Buscando proveedor y generando orden de compra...");

                    // Buscar un proveedor que coincida con el tipo de combustible del tanque
                    Proveedor proveedorAsociado = null;
                    for (int j = 0; j < proveedores.getLength(); j++) {
                        Proveedor proveedor = proveedores.get(j);
                        if (proveedor.getTipoCombustible().toString().equalsIgnoreCase(tanque.getTipo().toString())) {
                            proveedorAsociado = proveedor;
                            break;
                        }
                    }

                    if (proveedorAsociado == null) {
                        System.out.println(
                                "No se encontró un proveedor asociado al tipo de combustible: " + tanque.getTipo());
                        continue; // Saltar al siguiente tanque si no hay proveedor asociado
                    }

                    // Registrar una nueva orden de compra
                    OrdenCompra nuevaOrden = new OrdenCompra();
                    nuevaOrden.setCantidad(50); // Aumentar automáticamente en 50 unidades
                    nuevaOrden.setIdProveedor(proveedorAsociado.getId());
                    nuevaOrden.setIdTanque(tanque.getId());
                    nuevaOrden.setEstado(EstadoOrdenCompraEnum.COMPLETADO); // Estado inicial de la orden
                    daoOrdenCompra.setObj(nuevaOrden);
                    daoOrdenCompra.save();

                    // Aumentar el stock del tanque
                    tanque.setCapacidad(tanque.getCapacidad() + 50); // Aumentar automáticamente en 50 unidades
                    daoTanque.update(tanque, i);

                    System.out.println("Orden de compra generada y stock aumentado exitosamente en el tanque "
                            + tanque.getCodigo() +
                            ". Nueva capacidad: " + tanque.getCapacidad());
                    actualizado = true; // Indica que se realizó una actualización
                } else {
                    System.out.println("Tanque " + tanque.getCodigo() + " está en condiciones seguras.");
                }
            }

            return actualizado; // Retorna true si se actualizó al menos un tanque
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al generar órdenes de compra y actualizar los tanques.");
            return false; // Retorna false en caso de excepción
        }

    }

    private Boolean descontarStock(Integer idOrdenDespacho, Integer idPago) {
        try {
            // Instancias necesarias
            DaoPago daoPago = new DaoPago();
            DaoOrdenDespacho daoOrdenDespacho = new DaoOrdenDespacho();
            DaoPrecioEstablecido daoPrecioEstablecido = new DaoPrecioEstablecido();
    
            // Validar el estado del pago utilizando DaoPago
            Pago[] pagosArray = daoPago.listAll().toArray();
            Pago pago = null;
            for (Pago p : pagosArray) {
                if (p.getId().equals(idPago)) {
                    pago = p;
                    break;
                }
            }
    
            if (pago == null || !pago.getEstadoP()) {
                System.out.println("El pago no fue exitoso o no existe.");
                return false; // Retorna false si el pago no fue exitoso
            }
    
            // Buscar la orden de despacho manualmente
            OrdenDespacho[] ordenesArray = daoOrdenDespacho.listAll().toArray();
            OrdenDespacho ordenDespacho = null;
            for (OrdenDespacho orden : ordenesArray) {
                if (orden.getId().equals(idOrdenDespacho)) {
                    ordenDespacho = orden;
                    break;
                }
            }
    
            if (ordenDespacho == null) {
                System.out.println("La orden de despacho no existe.");
                return false; // Retorna false si la orden de despacho no existe
            }
    
            // Obtener el número de galones de la orden de despacho
            Integer numeroGalones = Math.round(ordenDespacho.getNroGalones()); // Convertir Float a Integer
            System.out.println("Número de galones en la orden de despacho: " + numeroGalones);
    
            // Buscar el precio establecido manualmente
            PrecioEstablecido[] preciosArray = daoPrecioEstablecido.listAll().toArray();
            PrecioEstablecido precioEstablecido = null;
            for (PrecioEstablecido precio : preciosArray) {
                /*if (precio.getId().equals(ordenDespacho.getIdPrecioGalon())) {
                    precioEstablecido = precio;
                    break;
                }*/
            }
    
            if (precioEstablecido == null) {
                System.out.println("No se encontró el precio establecido para la orden.");
                return false; // Retorna false si no se encuentra el precio establecido
            }
    
            // Obtener el tipo de combustible asociado al precio establecido
            String tipoCombustible = precioEstablecido.getTipoCombustible().toString();
            System.out.println("Tipo de combustible asociado: " + tipoCombustible);
    
            // Retorna true si todo se verificó correctamente
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al verificar el pago y obtener detalles.");
            return false; // Retorna false en caso de excepción
        }
    }

    public void procesarDescuentoAutomatico(Integer idOrdenDespacho, Integer idPago) {
        try {
            // Validar que los IDs sean válidos
            if (idOrdenDespacho == null || idPago == null) {
                System.out.println("Los IDs proporcionados son inválidos.");
                return; // Termina la ejecución si los IDs son inválidos
            }
            // Llamar al método privado descontarStock
            Boolean resultado = descontarStock(idOrdenDespacho, idPago);
            if (resultado != null && resultado) {
                System.out.println("Descuento realizado automáticamente para la orden de despacho: " + idOrdenDespacho);
            } else {
                System.out.println("No se pudo realizar el descuento automáticamente. Verifique los datos.");
            }
        } catch (NullPointerException e) {
            System.out.println("Error: Se encontró un valor nulo durante el procesamiento.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error al procesar el descuento automático: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
