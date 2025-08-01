package org.unl.gasolinera.base.models;

public class OrdenCompra {
    private Integer id;
    private float cantidad;
    private float precioTotal;
    private Integer idProveedor;
    private Integer idTanque;
    private EstadoOrdenCompraEnum estado;

    public EstadoOrdenCompraEnum getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoOrdenCompraEnum estado) {
        this.estado = estado;
    }
    
    public Integer getIdTanque() {
        return this.idTanque;
    }

    public void setIdTanque(Integer idTanque) {
        this.idTanque = idTanque;
    }

    public Integer getIdProveedor() {
        return this.idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
    public float getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public float getPrecioTotal() {
        return this.precioTotal;
    }

    public void setPrecioTotal(float precioTotal) {
        this.precioTotal = precioTotal;
    }
    
}