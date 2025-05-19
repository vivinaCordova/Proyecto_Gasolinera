package org.unl.gasolinera.base.models;

import java.util.Date;

public class OrdenDespacho {
    private Integer id;
    private Float nroGalones;
    private String codigo;
    private Date fecha;
    private float precioTotal;
    private EstadoOrdenDespachadoEnum estado;
    private Integer idPrecioGalon;
    private Integer idVehiculo;
    private Integer idEstacion;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getNroGalones() {
        return this.nroGalones;
    }

    public void setNroGalones(Float nroGalones) {
        this.nroGalones = nroGalones;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFecha() {
        return this.fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getPrecioTotal() {
        return this.precioTotal;
    }

    public void setPrecioTotal(float precioTotal) {
        this.precioTotal = precioTotal;
    }

    public EstadoOrdenDespachadoEnum getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoOrdenDespachadoEnum estado) {
        this.estado = estado;
    }

    public Integer getIdPrecioGalon() {
        return this.idPrecioGalon;
    }

    public void setIdPrecioGalon(Integer idPrecioGalon) {
        this.idPrecioGalon = idPrecioGalon;
    }

    public Integer getIdVehiculo() {
        return this.idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Integer getIdEstacion() {
        return this.idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }



    
}