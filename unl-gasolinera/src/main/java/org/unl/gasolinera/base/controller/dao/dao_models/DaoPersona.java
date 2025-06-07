package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.Persona;

public class DaoPersona extends AdapterDao<Persona> {
    private Persona obj;

    public DaoPersona(){
        super(Persona.class);
    }

    public Persona getObj() {
        if(obj==null)
            this.obj=new Persona();
        return this.obj;
    }

    
    public void setObj(Persona obj) {
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
        /*DaoPersona da= new DaoPersona();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setNombre("Viviana");
        da.getObj().setApellido("Cordova");
        da.getObj().setCedula("1107303293");
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");*/

        DaoPersona dp = new DaoPersona();
        try {
            DaoCuenta dc = new DaoCuenta();
            HashMap mapa = dc.login("Arelys", "Are");
            if(mapa != null){
                System.out.println(mapa.get("usuario"));
            }
        } catch (Exception e) {
            System.out.println("HUbo un error"+e);
            e.printStackTrace();
        }
    }


}