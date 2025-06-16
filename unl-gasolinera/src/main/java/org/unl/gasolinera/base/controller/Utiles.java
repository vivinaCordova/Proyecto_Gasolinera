package org.unl.gasolinera.base.controller;

import java.text.DecimalFormat;

public class Utiles {
    public static Integer ASCENDENTE=1;
    public static Integer DESCENDENTE=2;
    public static Integer START;//1
    public static Integer END;//2
    public static Integer CONSTIANS;//lo que dios quiera
    public String tranformStringFloatTwoDecimal(float dato){
        //67.876
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(dato);

    }
    public Boolean constanceArray(Object[] array, String text) {
        Boolean band = false;
        for(Object a: array) {
            if(a.toString().equals(text)){
                band = true;
                break;
            }
        }
        return band;
    }
}

