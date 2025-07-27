package org.unl.gasolinera.base.controller.service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoOrdenCompra;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoPrecioEstablecido;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoProveedor;
import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.Proveedor;
import org.unl.gasolinera.base.models.Tanque;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import jakarta.validation.constraints.NotEmpty;

@BrowserCallable
@AnonymousAllowed
public class OrdenCompraService {
    private DaoOrdenCompra db;

    public OrdenCompraService() {
        db = new DaoOrdenCompra();
    }

    public List<HashMap> listAll() throws Exception {
        return Arrays.asList(db.all().toArray());
    }

    public List<OrdenCompra> listAlla() {
        return (List<OrdenCompra>) Arrays.asList(db.listAll().toArray());
    }

    public List<HashMap> order(String attribute, Integer type) throws Exception {
        return Arrays.asList(db.orderByOrdenCompra(type, attribute).toArray());
    }

    public List<HashMap> search(String attribute, String text, Integer type) throws Exception {
        LinkedList<HashMap<String, Object>> lista = db.search(attribute, text, type);
        if (!lista.isEmpty())
            return Arrays.asList(lista.toArray());
        else
            return new ArrayList<>();

    }
    

    public boolean createOrdenCompra(Tanque tanque, float cantidad) throws Exception {
        try {
            DaoProveedor daoProveedor = new DaoProveedor();
            DaoOrdenCompra daoOrdenCompra = new DaoOrdenCompra();
            DaoPrecioEstablecido daoPrecioEstablecido = new DaoPrecioEstablecido();
    
            // Validar que el tanque tenga un PrecioEstablecido asociado
            if (tanque.getIdPrecioEstablecido() == null) {
                System.out.println("El tanque no tiene un PrecioEstablecido asociado.");
                return false;
            }
    
            // Obtener el PrecioEstablecido
            PrecioEstablecido precioEstablecido = daoPrecioEstablecido.getById(tanque.getIdPrecioEstablecido());
            if (precioEstablecido == null) {
                System.out.println("No se encontró un PrecioEstablecido para el tanque.");
                return false;
            }
    
            // Buscar proveedor asociado al PrecioEstablecido
            LinkedList<Proveedor> proveedores = daoProveedor.listAll();
            Proveedor proveedorAsociado = null;
            for (int i = 0; i < proveedores.getLength(); i++) {
                Proveedor proveedor = proveedores.get(i);
                if (proveedor.getIdPrecioEstablecido() != null &&
                    proveedor.getIdPrecioEstablecido().equals(precioEstablecido.getId())) {
                    proveedorAsociado = proveedor;
                    break;
                }
            }
    
            if (proveedorAsociado == null) {
                System.out.println("No se encontró proveedor asociado al tanque.");
                return false;
            }
    
            // Calcular el precio total
            float precioPorGalon = precioEstablecido.getPrecio(); // Precio por galón del combustible
            float precioTotal = cantidad * precioPorGalon;
    
            // Crear la orden de compra
            OrdenCompra nuevaOrden = new OrdenCompra();
            nuevaOrden.setCantidad(cantidad);
            nuevaOrden.setIdProveedor(proveedorAsociado.getId());
            nuevaOrden.setIdTanque(tanque.getId());
            nuevaOrden.setPrecioTotal(precioTotal); // Asignar el precio total calculado
            nuevaOrden.setEstado(EstadoOrdenCompraEnum.COMPLETADO);
    
            // Guardar la orden de compra
            daoOrdenCompra.setObj(nuevaOrden);
            daoOrdenCompra.save();
    
            System.out.println("Orden de compra creada exitosamente. Cantidad: " + cantidad + ", Precio total: " + precioTotal);
            return true;
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error creando orden de compra.");
            return false;
        }
    }
    public List<HashMap> listProveedorCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoProveedor da = new DaoProveedor();
        if (!db.listAll().isEmpty()) {
            Proveedor[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getNombre().toString());
                lista.add(aux);
            }

        }
        return lista;
    }

    public List<HashMap> listTanqueCombo() {
        List<HashMap> lista = new ArrayList<>();
        DaoTanque da = new DaoTanque();
        if (!db.listAll().isEmpty()) {
            Tanque[] arreglo = da.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                HashMap<String, String> aux = new HashMap<>();
                aux.put("value", arreglo[i].getId().toString(i));
                aux.put("label", arreglo[i].getCodigo().toString());
                lista.add(aux);
            }

        }
        return lista;
    }

    public List<String> listEstado() {
        List<String> lista = new ArrayList<>();
        for (EstadoOrdenCompraEnum r : EstadoOrdenCompraEnum.values()) {
            lista.add(r.toString());
        }
        return lista;
    }

}