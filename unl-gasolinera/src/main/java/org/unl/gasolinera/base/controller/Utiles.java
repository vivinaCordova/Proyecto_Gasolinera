package org.unl.gasolinera.base.controller;

import java.text.DecimalFormat;

public class Utiles {
    public static Integer ASCENDENTE=1;
    public static Integer DESCENDENTE=2;
    public static Integer START;
    public static Integer END;
    public static Integer CONSTIANS;
   
    public String tranformStringFloatTwoDecimal(double dato){
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
