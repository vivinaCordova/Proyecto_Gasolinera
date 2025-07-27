package org.unl.gasolinera.base.controller.dao.dao_models;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.unl.gasolinera.base.controller.Utiles;
import org.unl.gasolinera.base.controller.dao.AdapterDao;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;
import org.unl.gasolinera.base.models.Cuenta;
import org.unl.gasolinera.base.models.Persona;

public class DaoCuenta extends AdapterDao<Cuenta> {
    private Cuenta obj;

    public DaoCuenta() {
        super(Cuenta.class);
    }

    public Cuenta getObj() {
        if (obj == null)
            this.obj = new Cuenta();
        return this.obj;
    }

    public void setObj(Cuenta obj) {
        this.obj = obj;
    }

    public Boolean save() {
        try {
            obj.setId(listAll().getLength() + 1);
            this.persist(obj);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean update(Integer pos) {
        try {
            this.update(obj, pos);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


public LinkedList<HashMap<String, Object>> search(String attribute, String text, Integer type) throws Exception {
    LinkedList<HashMap<String, Object>> lista = all();
    LinkedList<HashMap<String, Object>> resp = new LinkedList<>();
    
    if (!lista.isEmpty()) {
        HashMap<String, Object>[] arr = lista.toArray();
        System.out.println(attribute+" "+text+" ** *** * * ** * * * *");
        switch (type) {
            case 1:
            System.out.println(attribute+" "+text+" UNO");
                for (HashMap m : arr) {
                    if (m.get(attribute).toString().toLowerCase().startsWith(text.toLowerCase())) {
                        resp.add(m);
                    }
                }
                break;
            case 2:
            System.out.println(attribute+" "+text+" DOS");
                for (HashMap m : arr) {
                    if (m.get(attribute).toString().toLowerCase().endsWith(text.toLowerCase())) {
                        resp.add(m);
                    }
                }
                break;
            default:
            System.out.println(attribute+" "+text+" TRES");
                for (HashMap m : arr) {
                    System.out.println("***** "+m.get(attribute)+"   "+attribute);
                    if (m.get(attribute).toString().toLowerCase().contains(text.toLowerCase())) {
                        resp.add(m);
                    }
                }
                break;
        }
    }
    return resp;
}


public LinkedList<HashMap<String, Object>> orderByCuenta(Integer type, String attribute) throws Exception {
    LinkedList<HashMap<String, Object>> lista = all();
    if (!lista.isEmpty()) {
        HashMap arr[] = lista.toArray();
        quickSort(arr, 0, arr.length - 1, type, attribute);
        lista.toList(arr);
    }
    return lista;
}

public LinkedList<HashMap<String, Object>> all() throws Exception {
    LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
    if (!this.listAll().isEmpty()) {
        Cuenta[] arreglo = this.listAll().toArray();
        for (int i = 0; i < arreglo.length; i++) {
            lista.add(toDict(arreglo[i]));
        }
    }
    return lista;
}

    public HashMap<String, Object> toDict(Cuenta c) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp = new DaoPersona();
        dp.setObj(dp.get(c.getId_persona()));
        map.put("id", c.getId());
        map.put("correo", c.getCorreo());
        map.put("usuario", dp.listAll().get(c.getId_persona()-1).getUsuario());
        map.put("estado", c.getEstado());
        map.put("rol", dp.getObj().getId_rol());
        return map;
    }   

    private HashMap<String, Object> toDictPassword(Cuenta c) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        DaoPersona dp = new DaoPersona();
        dp.setObj(dp.get(c.getId_persona()));
        map.put("correo", c.getCorreo());
        map.put("id", c.getId());
        map.put("clave", c.getClave());
        map.put("estado", c.getEstado());
        return map;
    }

    private LinkedList<HashMap<String, Object>> listPrivate() throws Exception {
        LinkedList<HashMap<String, Object>> lista = new LinkedList<>();
        if (!listAll().isEmpty()) {
            Cuenta[] aux = listAll().toArray();
            for (Cuenta c : aux) {
                lista.add(toDictPassword(c));
            }
        }
        return lista;
    }

    private int partition(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        HashMap<String, Object> pivot = arr[end];
        int i = (begin - 1);
        if (type == Utiles.ASCENDENTE) {
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString().toLowerCase().compareTo(pivot.get(attribute).toString().toLowerCase()) < 0) {
                    i++;
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        } else {
            for (int j = begin; j < end; j++) {
                if (arr[j].get(attribute).toString().toLowerCase().compareTo(pivot.get(attribute).toString().toLowerCase()) > 0) {
                    i++;
                    HashMap<String, Object> swapTemp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = swapTemp;
                }
            }
        }
        HashMap<String, Object> swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    private void quickSort(HashMap<String, Object> arr[], int begin, int end, Integer type, String attribute) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end, type, attribute);

            quickSort(arr, begin, partitionIndex - 1, type, attribute);
            quickSort(arr, partitionIndex + 1, end, type, attribute);
        }
    }

    public HashMap<String, Object> login(String email, String password) throws Exception {
        if (!listAll().isEmpty()) {
            HashMap<String, Object>[] arreglo = listPrivate().toArray();
            quickSort(arreglo, 0, arreglo.length - 1, 1, "correo");
            for(HashMap mapa: arreglo){
                System.out.println(mapa.get("correo"));
            }    
            HashMap<String, Object> search = BinarySearchRecursive(arreglo, 0, arreglo.length - 1, "correo", email);
            if(search != null) {
                if (((Boolean)search.get("estado"))){
                    
                    if(search.get("clave").toString().equals(password)) {
                        System.out.println("xxxx "+search.get("clave")+"   "+search.get("id")+"  "+get((Integer)search.get("id")));    
                        return toDict(get((Integer)search.get("id")));
                    } else throw new Exception("Su clave o usuario son incorrectos");
                } else throw new Exception("Cuenta desactivada");
            } else
                throw new Exception("No se encontro la cuenta");
        } else
            return null;
    }

    public HashMap<String, Object> BinarySearchRecursive(HashMap<String, Object> arr[], int a, int b, String attribute,
            String value) throws Exception {
        if (b < 1) {
            return null;
        }
        int n = a + (b = 1) / 2;
        System.out.println("n "+(n-1)+" a "+a+"  b "+b+" arrgelo "+arr.length+" atributo "+attribute+" valor "+value+" atributo valor "+arr[n].get(attribute));
        if (arr[n].get(attribute).toString().equals(value))
            return arr[n];
        else if (arr[n].get(attribute).toString().compareTo(value) > 0){            
            return BinarySearchRecursive(arr, a, n - 1, attribute, value);
        }else
            return BinarySearchRecursive(arr, n + 1, b, attribute, value);
    }

    /*public Boolean deleteCuenta(Integer id) {
        try {
            super.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
    }*/

}