package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.Tanque;

public class DaoTanque extends AdapterDao <Tanque>{
    
    private Tanque obj;

    public DaoTanque(){
        super(Tanque.class);
    }

    public Tanque getObj() {
        if(obj==null)
            this.obj=new Tanque();
        return this.obj;
    }

    
    public void setObj(Tanque obj) {
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
    //public String enviarAlerta(){

    //}
    //public void aumentarStock(){

    //}
    //public float reducirStock(){

    //}
    public static void main(String[] args) {
        DaoTanque da= new DaoTanque();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCapacidad(150000);
        da.getObj().setCapacidadMinima(100000);
        da.getObj().setCapacidadTotal(0);
        da.getObj().setIdOrdenCompra(1);
        da.getObj().setEstado(EstadoOrdenCompraEnum.valueOf("COMPLETADO"));
        da.getObj().setIdOrden(1);
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");

    }
}