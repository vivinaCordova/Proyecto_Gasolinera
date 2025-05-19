package org.unl.gasolinera.base.controller.dao.dao_models;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.EstadoOrdenCompraEnum;
import org.unl.gasolinera.base.models.OrdenCompra;

public class DaoOrdenCompra extends AdapterDao<OrdenCompra>{
    private OrdenCompra obj;

    public DaoOrdenCompra(){
        super(OrdenCompra.class);
    }

    public OrdenCompra getObj() {
        if(obj==null)
            this.obj=new OrdenCompra();
        return this.obj;
    }

    
    public void setObj(OrdenCompra obj) {
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
        DaoOrdenCompra da= new DaoOrdenCompra();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setCantidad(3000);
        da.getObj().setIdProveedor(1);
        da.getObj().setEstado(EstadoOrdenCompraEnum.valueOf("COMPLETADO"));;
        if(da.save())
            System.out.println("GUARDADO");
        else
            System.out.println("Error");

    }
}