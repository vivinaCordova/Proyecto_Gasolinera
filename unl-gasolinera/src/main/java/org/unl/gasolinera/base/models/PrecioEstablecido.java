package org.unl.gasolinera.base.models;

import java.util.Date;

public class PrecioEstablecido {
    private Integer id;
    private Date fecha;
    private Date fechaFin;
    private boolean estado;
    private float precio;
    private TipoCombustibleEnum tipoCombustible;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return this.fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaFin() {
        return this.fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isEstado() {
        return this.estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public float getPrecio() {
        return this.precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public TipoCombustibleEnum getTipoCombustible() {
        return this.tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustibleEnum tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }
}