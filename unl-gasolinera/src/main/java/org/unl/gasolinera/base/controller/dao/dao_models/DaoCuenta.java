package org.unl.gasolinera.base.controller.dao.dao_models;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
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
        dp.setObj(dp.get(c.getId_persona()));
        map.put("correo", c.getCorreo());
        map.put("id", c.getId());
        map.put("estado", c.getEstado());
        map.put("usuario", dp.getObj().getUsuario());
        return map;
    }

    private HashMap<String, Object> toDictPassword(Cuenta c) throws Exception{
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp= new DaoPersona();
        dp.setObj(dp.get(c.getId_persona()));
        map.put("correo", c.getCorreo());
        map.put("id", c.getId());
        map.put("id", c.getClave());
        map.put("estado", c.getEstado());
        map.put("usuario", dp.getObj().getUsuario());
        return map;
    }

    private LinkedList<HashMap<String, Object>> listPrivate() throws Exception{
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if(!listAll().isEmpty()){
            Cuenta[] aux = listAll().toArray();
            for(Cuenta c: aux){
                lista.add(toDictPassword(c));
            }
        }
        return lista;
    }

    private int partition(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        HashMap<String, Object> pivot = arr[end];
        int i = (begin-1);
        if(type==Utiles.ASCENDENTE){
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString()
                .compareTo(pivot.get(attribute).toString()) < 0) {
                    i++;
        
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        }else{
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString()
                .compareTo(pivot.get(attribute).toString()) > 0) {
                    i++;
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        }
    
        HashMap<String, Object> swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;
    
        return i+1;
    }

    public void quickSort(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end, type, attribute);
    
            quickSort(arr, begin, partitionIndex-1, type, attribute);
            quickSort(arr, partitionIndex+1, end, type, attribute);
        }
    }




    public HashMap login(String email, String password) throws Exception{
        if (!listAll().isEmpty()) {
            HashMap<String, Object>[] arreglo = listPrivate().toArray();
            quickSort(arreglo, 0, arreglo.length-1, 1, password);
            HashMap<String, Object> search = BinarySearchRecursive(arreglo, 0, arreglo.length-1, "correo", email);
            if(search!=null){
                if((Boolean)search.get("estado")){
                    if(search.get("clave").toString().equals(password)){
                        return toDict(get((Integer)search.get("id")));
                    }else throw new Exception("Su clave o contraseña son incorrectos");
                }else throw new Exception("Cuenta desactivada");
            }else
            throw new Exception("No se encontro la cuenta");
        }else return null;
    }

    public HashMap<String, Object> BinarySearchRecursive(HashMap<String, Object> arr[], int a, int b, String attribute, String value) throws Exception {
        if (b < 1) {
            return null;
        }
        int n = a + (b = 1) / 2;

        if (arr[n].get(attribute).toString().equals(value))
            return arr[n];
        else if (arr[n].get(attribute).toString().compareTo(value) > 0)
            return BinarySearchRecursive(arr, a, n - 1, attribute, value);
        else
            return BinarySearchRecursive(arr, n + 1, b, attribute, value);
    }

    public static void main(String[] args) {
        DaoCuenta da= new DaoCuenta();
        //da.getObj().setId(da.listAll().getLength()+1);
        //da.getObj().setUsuario("Shamira");
        //da.getObj().setClave("1234");
        try {
            //da.setObj(da.get(3));
            System.out.println(da.get(3));
            /*if(da.getObj()!=null && da.getObj().getId() != null){
                System.out.println("Encontre a "+da.getObj());
            }else{
                System.out.println("No se encontro");
            }*/

        } catch (Exception e) {
            System.out.println("Hubo un error"+e);
        }

        if(da.save())
            System.out.println("GUARDADO");
        else
        System.out.println("Error");
    }





}