package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.Tanque;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;

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

    public static void main(String[] args) {
        DaoTanque da= new DaoTanque();
        da.setObj(new Tanque());
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCapacidad(24000);
        da.getObj().setCapacidadMinima(10000);    
        da.getObj().setCapacidadTotal(30000);
        da.getObj().setEstado(EstadoOrdenCompraEnum.valueOf("PROCESO"));
        da.getObj().setIdOrden(1);
        da.getObj().setIdOrdenCompra(1);
        da.getObj().setTipo(TipoCombustibleEnum.valueOf("ECOPAIS"));
        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
        
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCapacidad(200);
        da.getObj().setCapacidadMinima(10000);    
        da.getObj().setCapacidadTotal(30000);
        da.getObj().setEstado(EstadoOrdenCompraEnum.valueOf("PROCESO"));
        da.getObj().setIdOrden(2);
        da.getObj().setIdOrdenCompra(2);
        da.getObj().setTipo(TipoCombustibleEnum.valueOf("SUPER"));
        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");
        
    }
}
