package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

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

    public HashMap<String, Object> toDict(Cuenta c) throws Exception{
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp= new DaoPersona();
        dp.setObj(dp.get(c.getId_persona()));;
        //dp.get(1);
        map.put("correo", c.getEmail());
        map.put("id", c.getId());
        map.put("estado", c.getEstado());
        return map;
    }

    private HashMap<String, Object> toDictPassword(Cuenta c) throws Exception{
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp= new DaoPersona();
        dp.setObj(dp.get(c.getId_persona()));;
        //dp.get(1);
        map.put("correo", c.getEmail());
        map.put("id", c.getId());
        map.put("id", c.getClave());
        map.put("estado", c.getEstado());
        return map;
    }

    public static void main(String[] args) {
        DaoCuenta da= new DaoCuenta();
        //da.getObj().setId(da.listAll().getLength()+1);
        //da.getObj().setUsuario("Shamira");
        //da.getObj().setClave("1234");
        try {
            da.setObj(da.get(3));
            if(da.getObj()!=null && da.getObj().getId() != null){
                System.out.println("Encontre a "+da.getObj());
            }else{
                System.out.println("No se encontro");
            }

        } catch (Exception e) {
            System.out.println("Hubo un error"+e);
        }

        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }


}