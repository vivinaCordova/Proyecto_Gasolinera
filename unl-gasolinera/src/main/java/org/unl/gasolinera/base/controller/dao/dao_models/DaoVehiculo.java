package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.Vehiculo;

public class DaoVehiculo extends AdapterDao<Vehiculo> {
    private Vehiculo obj;

    public DaoVehiculo(){
        super(Vehiculo.class);
    }

    public Vehiculo getObj() {
        if(obj==null)
            this.obj=new Vehiculo();
        return this.obj;
    }

    
    public void setObj(Vehiculo obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength()+1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
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
        DaoVehiculo da= new DaoVehiculo();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setPlaca("IJ58LO");
        da.getObj().setMarca("KIA");
        da.getObj().setModelo("K3");
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }


}