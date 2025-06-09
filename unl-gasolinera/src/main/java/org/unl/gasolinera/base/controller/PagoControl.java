package org.unl.gasolinera.base.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.hibernate.engine.spi.TypedValue;

import com.fasterxml.jackson.databind.util.TypeKey;
import com.google.gson.Gson;

public class PagoControl {
    public static String CODE[] = { "000.200.100", "000.000.000", "000.000.100", "000.200.000" };
    public static String CODEPAY[] = { "000.100.111", "000.100.110", "000.100.112" };

    public HashMap<String, String> request(float total, String currency) throws IOException {
        URL url = new URL("https://eu-test.oppwa.com/v1/checkouts");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization",
                "Bearer OGE4Mjk0MTc1ZDYwMjM2OTAxNWQ3M2JmMDBlNTE4MGN8QlhSdUJFc1d4JU1yNmRHQnR5NzI=");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        String data = ""
                + "entityId=8a8294175d602369015d73bf009f1808"
                + "&amount=" + new Utiles().tranformStringFloatTwoDecimal(total)
                + "&currency=" + currency
                + "&paymentType=CC"
                + "&integrity=true";

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
        int responseCode = conn.getResponseCode();
        InputStream is;

        if (responseCode >= 400)
            is = conn.getErrorStream();
        else
            is = conn.getInputStream();

        // Gson g = new Gson();
        return readJsonCheckout(IOUtils.toString(is));
    }

    public HashMap<String, String> readJsonCheckout(String jsonValue) {
        HashMap<String, String> respMap = new HashMap<>();
        Gson g = new Gson();
        HashMap mapa = g.fromJson(jsonValue, HashMap.class);
        
        com.google.gson.internal.LinkedTreeMap<TypeKey, TypedValue> lista = (com.google.gson.internal.LinkedTreeMap) mapa
                .get("result");
        String codigo = lista.get("code") + "";
        respMap.put("codigo", codigo);
        respMap.put("error", "");
        respMap.put("estado", "false");
        if (new Utiles().constanceArray(CODE, codigo)) {
            respMap.put("id", mapa.get("id").toString());
            respMap.put("estado", "true");
        } else {
            respMap.put("id", "No definido");
            respMap.put("error", lista.get("parameterErrors") + "");
            
        }

        return respMap;
    }

    public HashMap<String, String> readJsonPay(String jsonValue) {
        HashMap<String, String> respMap = new HashMap<>();
        Gson g = new Gson();
        HashMap mapa = g.fromJson(jsonValue, HashMap.class);
        mapa.forEach((key, value) -> System.out.println(key + " " + value));
        com.google.gson.internal.LinkedTreeMap<TypeKey, TypedValue> lista = (com.google.gson.internal.LinkedTreeMap) mapa
                .get("result");
        String codigo = lista.get("code") + "";
        respMap.put("codigo", codigo);
        
        respMap.put("descripcion", lista.get("description") + "");
        respMap.put("estado", "false");
        if (new Utiles().constanceArray(CODEPAY, codigo)) {
            respMap.put("tarjeta", mapa.get("paymentBrand").toString());
            respMap.put("estado", "true");
        } else {
            respMap.put("tarjeta", "No definida");
            
        }

        return respMap;
    }

    private HashMap<String, String> requestPay(String id) throws IOException {
        URL url = new URL(
                "https://eu-test.oppwa.com/v1/checkouts/" + id + "/payment?entityId=8a8294175d602369015d73bf009f1808");

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization",
                "Bearer OGE4Mjk0MTc1ZDYwMjM2OTAxNWQ3M2JmMDBlNTE4MGN8QlhSdUJFc1d4JU1yNmRHQnR5NzI=");
        int responseCode = conn.getResponseCode();
        InputStream is;

        if (responseCode >= 400)
            is = conn.getErrorStream();
        else
            is = conn.getInputStream();

        return requestPay(id);
    }

    public static void main(String[] args) {
        PagoControl pc = new PagoControl();
        try {
            // System.out.println(pc.request(67.00f, "USD"));
            // checkout
            String json = "{\"result\":{\"code\":\"000.200.100\",\"description\":\"successfully created checkout\"},\"buildNumber\":\"c6ca2f7e2ab6517140c927f556eac7d0a372941b@2025-06-03 14:14:03 +0000\",\"timestamp\":\"2025-06-03 20:48:21+0000\",\"ndc\":\"3A9AA8CFC309475335B3B56398C96ACF.uat01-vm-tx02\",\"id\":\"3A9AA8CFC309475335B3B56398C96ACF.uat01-vm-tx02\",\"integrity\":\"sha384-CGYjAK7QlS3M/T8WNbNkVz3wr5umg+pRWXKAr2q35qHL82Bj13NmkhgPK8EUjsa0\"}";
            String jsonError = "{\r\n" + //
                    "  \"result\":{\r\n" + //
                    "    \"code\":\"200.300.404\",\r\n" + //
                    "    \"description\":\"invalid or missing parameter\",\r\n" + //
                    "    \"parameterErrors\":[\r\n" + //
                    "      {\r\n" + //
                    "        \"name\":\"amount\",\r\n" + //
                    "        \"value\":\"92.0\",\r\n" + //
                    "        \"message\":\"must match ^[0-9]{1,12}(\\\\.[0-9]{2})?$\"\r\n" + //
                    "      }\r\n" + //
                    "    ]\r\n" + //
                    "  },\r\n" + //
                    "  \"buildNumber\":\"c6ca2f7e2ab6517140c927f556eac7d0a372941b@2025-06-03 14:14:03 +0000\",\r\n" + //
                    "  \"timestamp\":\"2025-06-04 13:30:03+0000\",\r\n" + //
                    "  \"ndc\":\"04FA0B06D7C9F5778958BD5552FE4135.uat01-vm-tx04\"\r\n" + //
                    "}";
            
            System.out.println(pc.readJsonCheckout(json).toString());

            // SEARCH PAY
            System.out.println("\n PAY RESERAH \n");
            String jsonpayerror = "{\r\n" + //
                    "  \"result\":{\r\n" + //
                    "    \"code\":\"200.300.404\",\r\n" + //
                    "    \"description\":\"invalid or missing parameter - (opp) No payment session found for the requested id - are you mixing test/live servers or have you paid more than 30min ago?\"\r\n"
                    + //
                    "  },\r\n" + //
                    "  \"buildNumber\":\"c6ca2f7e2ab6517140c927f556eac7d0a372941b@2025-06-03 14:14:03 +0000\",\r\n" + //
                    "  \"timestamp\":\"2025-06-04 13:36:44+0000\",\r\n" + //
                    "  \"ndc\":\"8a8294175d602369015d73bf009f1808_f2dc9211f75040e1b9bd6674261d6fea\"\r\n" + //
                    "}";

            String jsonpayok = "{\r\n" + //
                    "  \"id\":\"8ac7a49f973ace7901973b2d3d2f565f\",\r\n" + //
                    "  \"paymentType\":\"CC\",\r\n" + //
                    "  \"paymentBrand\":\"MASTER\",\r\n" + //
                    "  \"amount\":\"92.01\",\r\n" + //
                    "  \"currency\":\"EUR\",\r\n" + //
                    "  \"descriptor\":\"7700.5633.1938 OPP_Channel\",\r\n" + //
                    "  \"result\":{\r\n" + //
                    "    \"code\":\"000.100.110\",\r\n" + //
                    "    \"description\":\"Request successfully processed in 'Merchant in Integrator Test Mode'\"\r\n" + //
                    "  },\r\n" + //
                    "  \"resultDetails\":{\r\n" + //
                    "    \"ExtendedDescription\":\"Authorized\",\r\n" + //
                    "    \"clearingInstituteName\":\"Hobex via TECS\",\r\n" + //
                    "    \"ConnectorTxID1\":\"00000000770056331938\",\r\n" + //
                    "    \"ConnectorTxID3\":\"0734|057\",\r\n" + //
                    "    \"ConnectorTxID2\":\"082405\",\r\n" + //
                    "    \"AcquirerResponse\":\"0000\"\r\n" + //
                    "  },\r\n" + //
                    "  \"card\":{\r\n" + //
                    "    \"bin\":\"545454\",\r\n" + //
                    "    \"binCountry\":\"US\",\r\n" + //
                    "    \"last4Digits\":\"5454\",\r\n" + //
                    "    \"holder\":\"Arelis Agila\",\r\n" + //
                    "    \"expiryMonth\":\"12\",\r\n" + //
                    "    \"expiryYear\":\"2027\",\r\n" + //
                    "    \"issuer\":{\r\n" + //
                    "      \"bank\":\"CENTRAL TRUST BANK, THE\"\r\n" + //
                    "    },\r\n" + //
                    "    \"type\":\"CREDIT\",\r\n" + //
                    "    \"level\":\"B2B\",\r\n" + //
                    "    \"country\":\"US\",\r\n" + //
                    "    \"maxPanLength\":\"16\",\r\n" + //
                    "    \"binType\":\"COMMERCIAL\",\r\n" + //
                    "    \"regulatedFlag\":\"Y\"\r\n" + //
                    "  },\r\n" + //
                    "  \"customer\":{\r\n" + //
                    "    \"ip\":\"192.188.49.207\"\r\n" + //
                    "  },\r\n" + //
                    "  \"customParameters\":{\r\n" + //
                    "    \"SHOPPER_EndToEndIdentity\":\"86aec2abbd8fb84dd410ad65190b56bbfed42d9a626be017ac331fc8a626b287\"\r\n"
                    + //
                    "  },\r\n" + //
                    "  \"risk\":{\r\n" + //
                    "    \"score\":\"100\"\r\n" + //
                    "  },\r\n" + //
                    "  \"buildNumber\":\"c6ca2f7e2ab6517140c927f556eac7d0a372941b@2025-06-03 14:14:03 +0000\",\r\n" + //
                    "  \"timestamp\":\"2025-06-04 13:41:50+0000\",\r\n" + //
                    "  \"ndc\":\"6627583709900EB76656DA222471C19C.uat01-vm-tx04\",\r\n" + //
                    "  \"source\":\"OPPUI\",\r\n" + //
                    "  \"paymentMethod\":\"CC\",\r\n" + //
                    "  \"shortId\":\"7700.5633.1938\"\r\n" + //
                    "}";

            System.out.println(pc.readJsonPay(jsonpayok).toString());

            

        } catch (Exception e) {
            System.out.println("Error " + e);
            // TODO: handle exception
        }
    }
}
