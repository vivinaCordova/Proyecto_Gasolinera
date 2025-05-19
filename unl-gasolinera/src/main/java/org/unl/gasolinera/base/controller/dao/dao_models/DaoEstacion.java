package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.Estacion;
import org.unl.gasolinera.base.models.EstadoUsoEnum;

public class DaoEstacion extends AdapterDao<Estacion> {
    private Estacion obj;

    public DaoEstacion(){
        super(Estacion.class);
    }

    public Estacion getObj() {
        if(obj==null)
            this.obj=new Estacion();
        return this.obj;
    }

    
    public void setObj(Estacion obj) {
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
        DaoEstacion da= new DaoEstacion();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setEstadoE(EstadoUsoEnum.ACTIVO);
        da.getObj().setCodigo("E001");
        
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }


}