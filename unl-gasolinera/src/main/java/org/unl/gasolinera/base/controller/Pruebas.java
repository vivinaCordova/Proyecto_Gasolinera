package org.unl.gasolinera.base.controller;

import java.util.HashMap;

import org.unl.gasolinera.base.controller.dao.dao_models.DaoTanque;
import org.unl.gasolinera.base.controller.dataStruct.list.LinkedList;

public class Pruebas {
    public LinkedList<String> enviarAlertaProveedor() throws Exception {
        LinkedList<String> alertas = new LinkedList<>();
        DaoTanque daoTanque = new DaoTanque();
        LinkedList<HashMap<String, Object>> tanques = daoTanque.all();

        for (int i = 0; i < tanques.getLength(); i++) {
            HashMap<String, Object> tanque = tanques.get(i);
            Double capacidadTotal = (Double) tanque.get("capacidadTotal");

            if (capacidadTotal != null && capacidadTotal < 1200) {
                String alerta = "⚠️ Alerta: El tanque con ID " + tanque.get("id") +
                        " tiene una capacidad baja: " + capacidadTotal + " litros.";
                alertas.add(alerta);
            }
        }

        return alertas;
    }

    public static void main(String[] args) throws Exception {
        Pruebas prueba = new Pruebas(); // Crear instancia para llamar al método no estático

        // Llamar a enviarAlertaProveedor() y guardar alertas
        LinkedList<String> alertas = prueba.enviarAlertaProveedor();

        // Mostrar las alertas en consola para verificar que funciona
        if (alertas.isEmpty()) {
            System.out.println("No hay alertas de capacidad baja.");
        } else {
            System.out.println("Alertas de tanques con capacidad baja:");
            for (int i = 0; i < alertas.getLength(); i++) {
                System.out.println(alertas.get(i));
            }
        }
    }
    
}
