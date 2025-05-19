package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.Date;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.EstadoOrdenDespachadoEnum;
import org.unl.gasolinera.base.models.OrdenDespacho;


public class DaoOrdenDespacho extends AdapterDao<OrdenDespacho> {
    private OrdenDespacho obj;

    public DaoOrdenDespacho(){
        super(OrdenDespacho.class);
    }

    public OrdenDespacho getObj() {
        if(obj==null)
            this.obj=new OrdenDespacho();
        return this.obj;
    }

    
    public void setObj(OrdenDespacho obj) {
        this.obj = obj;
    }

    public Boolean save(){
        try{
            this.persist(obj); 
            return true;

        }catch(Exception e){
            e.printStackTrace(); 
            System.out.println(e);
            //LOG DE ERROR
            return false;
        }
    }

    public Boolean update(Integer pos){
        try{
            obj.setId(listAll().getLength()+1);
            this.update(obj, pos);
            return true;

        }catch(Exception e){
            e.printStackTrace(); 
            System.out.println(e);
            //LOG DE ERROR
            return false;
        }
    }

    public static void main(String[] args) {
        DaoOrdenDespacho da= new DaoOrdenDespacho();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCodigo("a001");
        da.getObj().setNroGalones(9.2f);
        da.getObj().setFecha(new Date());
        da.getObj().setPrecioTotal(20.6f); 
        da.getObj().setEstado(EstadoOrdenDespachadoEnum.COMPLETADO);
        da.getObj().setIdVehiculo(1);

        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
    }
}