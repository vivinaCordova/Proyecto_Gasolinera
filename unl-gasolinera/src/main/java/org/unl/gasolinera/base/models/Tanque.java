package org.unl.gasolinera.base.models;

public class Tanque {
    private Integer id;
    private float capacidad;
    private float capacidadTotal;
    private float capacidadMinima;
    private TipoCombustibleEnum tipo;
    private EstadoOrdenCompraEnum estado;
    private Integer idOrden;
    private Integer idOrdenCompra;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(float capacidad) {
        this.capacidad = capacidad;
    }

    public float getCapacidadTotal() {
        return this.capacidadTotal;
    }

    public void setCapacidadTotal(float capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public float getCapacidadMinima() {
        return this.capacidadMinima;
    }

    public void setCapacidadMinima(float capacidadMinima) {
        this.capacidadMinima = capacidadMinima;
    }

    public TipoCombustibleEnum getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoCombustibleEnum tipo) {
        this.tipo = tipo;
    }

    public EstadoOrdenCompraEnum getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoOrdenCompraEnum estado) {
        this.estado = estado;
    }

    public Integer getIdOrden() {
        return this.idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Integer getIdOrdenCompra() {
        return this.idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }
    
    
}
