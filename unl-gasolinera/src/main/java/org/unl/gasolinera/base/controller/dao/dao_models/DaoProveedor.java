package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.Proveedor;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

public class DaoProveedor extends AdapterDao<Proveedor> {
    
    private Proveedor obj;

    public DaoProveedor(){
        super(Proveedor.class);
    }

    public Proveedor getObj() {
        if(obj==null)
            this.obj=new Proveedor();
        return this.obj;
    }

    
    public void setObj(Proveedor obj) {
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
        DaoProveedor da= new DaoProveedor();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setNombre("PETROECUADOR");
        da.getObj().setCorreoElectronico("petro.e.ecuador@org.ecu.gob");
        da.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf("SUPER"));
        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setNombre("PRIMAX");
        da.getObj().setCorreoElectronico("primax.a.aramco@org.ecu.gob");
        da.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf("ECOPAIS"));
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setNombre("P&S");
        da.getObj().setCorreoElectronico("petroleos.e.servicios@org.ecu.gob");
        da.getObj().setTipoCombustible(TipoCombustibleEnum.valueOf("EXTRA"));
        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
    }
}