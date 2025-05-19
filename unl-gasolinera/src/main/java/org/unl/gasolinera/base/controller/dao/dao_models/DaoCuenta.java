package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.Cuenta;

public class DaoCuenta extends AdapterDao<Cuenta> {
    private Cuenta obj;

    public DaoCuenta(){
        super(Cuenta.class);
    }

    public Cuenta getObj() {
        if(obj==null)
            this.obj=new Cuenta();
        return this.obj;
    }

    
    public void setObj(Cuenta obj) {
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
            return false;
        }
    }

    public static void main(String[] args) {
        DaoCuenta da= new DaoCuenta();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setUsuario("Shamira");
        da.getObj().setClave("1234");
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }


}