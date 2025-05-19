package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.Calendar;
import java.util.Date;

import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.models.PrecioEstablecido;
import org.unl.gasolinera.base.models.TipoCombustibleEnum;


public class DaoPrecioEstablecido extends AdapterDao<PrecioEstablecido> {
    private PrecioEstablecido obj;

    public DaoPrecioEstablecido(){
        super(PrecioEstablecido.class);
    }

    public PrecioEstablecido getObj() {
        if(obj==null)
            this.obj=new PrecioEstablecido();
        return this.obj;
    }

    
    public void setObj(PrecioEstablecido obj) {
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
        DaoPrecioEstablecido da= new DaoPrecioEstablecido();
        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setFecha(new Date());

        Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        da.getObj().setFechaFin(calendar.getTime());


        da.getObj().setEstado(true);
        da.getObj().setPrecio(1.80f);
        da.getObj().setTipoCombustible(TipoCombustibleEnum.DIESEL); 
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");

        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setFecha(new Date());

    
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 25);
        da.getObj().setFechaFin(calendar.getTime());


        da.getObj().setEstado(true);
        da.getObj().setPrecio(2.50f);
        da.getObj().setTipoCombustible(TipoCombustibleEnum.ECOPAIS); 
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");

        da.getObj().setId(da.listAll().getLength()+1);
        da.getObj().setFecha(new Date());

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        da.getObj().setFechaFin(calendar.getTime());


        da.getObj().setEstado(true);
        da.getObj().setPrecio(3.52f);
        da.getObj().setTipoCombustible(TipoCombustibleEnum.SUPER); 
        
        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }
}