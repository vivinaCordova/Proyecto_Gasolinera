package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.EstadoPagoEnum;
import org.unl.gasolinera.base.models.Pago;

public class DaoPago extends AdapterDao<Pago> {
    private Pago obj;

    public DaoPago(){
        super(Pago.class);
    }

    public Pago getObj() {
        if(obj==null)
            this.obj=new Pago();
        return this.obj;
    }

    
    public void setObj(Pago obj) {
        this.obj = obj;
    }

    public Boolean save(){
        try{
            this.persist(obj); 
            return true;

        }catch(Exception e){
            e.printStackTrace(); 
            System.out.println(e);
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
            return false;
        }
    }

    public static void main(String[] args) {
        DaoPago da= new DaoPago();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setNroTransaccion(1);
        da.getObj().setEstadoP(EstadoPagoEnum.COMPLETADO);
        da.getObj().setIdOrdenDespacho(1);
       
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }
}