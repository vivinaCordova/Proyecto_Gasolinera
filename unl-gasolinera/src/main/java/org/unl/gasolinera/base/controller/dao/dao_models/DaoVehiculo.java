package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
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

    public Boolean update(Integer pos) {
        try {
            obj.setId(listAll().getLength());
            this.update(obj, pos);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            //LOG DE ERROR
            return false;
        }
    }

   

    public void quickSort(Vehiculo arr[], int low, int high, Integer type) {
        if (low < high) {
            int pi = partition(arr, low, high, type);
            quickSort(arr, low, pi - 1, type);
            quickSort(arr, pi + 1, high, type);
        }
    }

    private int partition(Vehiculo[] arr, int low, int high, Integer type) {
        Vehiculo pivot = arr[high];
        int i = (low - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = low; j < high; j++) {
                if (arr[j].getPlaca().toLowerCase().compareTo(pivot.getPlaca().toLowerCase()) < 0) {
                    i++;
                    Vehiculo temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        } else {
            for (int j = low; j < high; j++) {
                if (arr[j].getPlaca().toLowerCase().compareTo(pivot.getPlaca().toLowerCase()) > 0) {
                    i++;
                    Vehiculo temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        Vehiculo temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;

    }

    

    public LinkedList<HashMap<String, String>> all() {
        LinkedList<HashMap<String, String>> lista = new LinkedList<>();
        if (!this.listAll().isEmpty()) {
            Vehiculo[] arreglo = this.listAll().toArray();
            for (int i = 0; i < arreglo.length; i++) {
                lista.add(toDict(arreglo[i], i));
            }
        }
        return lista;
    }

    private HashMap<String, String> toDict(Vehiculo arreglo, Integer i) {
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp = new DaoPersona();
        HashMap<String, String> aux = new HashMap<>();
        aux.put("id", arreglo.getId().toString(i));
        aux.put("placa", arreglo.getPlaca());
        aux.put("modelo", arreglo.getModelo());
        aux.put("marca", arreglo.getMarca());
        aux.put("propietario", dp.listAll().get(arreglo.getIdPropietario()).getNombres());

        return aux;
    }

    public LinkedList<HashMap<String, String>> orderbyVehiculo(Integer type, String attribute) {
      LinkedList<HashMap<String, String>> lista = all();
      if (!lista.isEmpty()){
        HashMap arr[] = lista.toArray();
        // Convert HashMap array back to Vehiculo array for sorting
        Vehiculo[] Vehiculoes = this.listAll().toArray();
        quickSort(Vehiculoes, 0, Vehiculoes.length - 1, type);
        // Update lista with sorted Vehiculoes
        lista = new LinkedList<>();
        for (Vehiculo Vehiculo : Vehiculoes) {
            lista.add(toDict(Vehiculo, type));
        }
      }
      return lista;
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