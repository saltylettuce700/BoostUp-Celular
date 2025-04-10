package com.example.user.ConexionBD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BD {

    private static final String BASE_URL = "https://boostup.life/";
    private final Gson gson;
    private final OkHttpClient client;

    public BD() {
        gson = new GsonBuilder().create();
        client = new OkHttpClient();
    }
/*--------------------------------------------------------------------------------*/
    //GETS:

    //Get Todas las alergias existentes para mostrarlas como opciones
    public Map<Integer, String> getOpcionesAlergias() {
        String url = BASE_URL + "alergenos/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();

                // Deserializa el JSON como un JsonArray
                JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);

                Map<Integer, String> alergiasMap = new HashMap<>();
                for (JsonElement element : jsonArray) {
                    JsonObject obj = element.getAsJsonObject();
                    int id = obj.get("id_tipo_alergeno").getAsInt();  // Obtén el id
                    String alergeno = obj.get("alergeno").getAsString();  // Obtén el nombre del alergeno

                    // Agrega el id y el alergeno al mapa
                    alergiasMap.put(id, alergeno);
                }

                return alergiasMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Get todos los saborizantes con su id, sabor y tipo de saborizante
    public List<Map<String, String>> getSaborizantes() {
        String url = BASE_URL + "saborizante/opciones/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();

                // Deserializa el JSON como un JsonArray
                JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);

                List<Map<String, String>> saborizantesList = new ArrayList<>();

                // Recorre cada objeto JSON
                for (JsonElement element : jsonArray) {
                    JsonObject obj = element.getAsJsonObject();

                    Map<String, String> saborizante = new HashMap<>();
                    saborizante.put("id", String.valueOf(obj.get("id_saborizante").getAsInt()));
                    saborizante.put("sabor", obj.get("sabor").getAsString());
                    saborizante.put("tipo", obj.get("tipo_saborizante").getAsString());

                    saborizantesList.add(saborizante);
                }

                return saborizantesList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Get precio de una curcuma especifica
    public double getPrecioSaborizante(int idSaborizante) {
        String url = BASE_URL + "curcuma/" + idSaborizante + "/precio/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();

                // Parseamos directamente el precio
                JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                return jsonObject.get("precio").getAsDouble();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;  // Devuelve -1 si algo falla
    }


    /*---------------------------------------------------------------------------------------*/
    //POSTS:

    //Post Crear Cuenta de Usuario

    public boolean registrarUsuario(String email, String password, String username, String nombre, String apellido,
                                    String sexo, String fec_nacimiento, float peso_kg, int talla_cm, int cintura_cm,
                                    int cadera_cm, int circ_brazo_cm) {

        String url = BASE_URL + "usuario/registrar/";

        Usuario usuario = new Usuario(email, password, username, nombre, apellido, sexo, fec_nacimiento, peso_kg, talla_cm, cintura_cm, cadera_cm, circ_brazo_cm);

        String json = gson.toJson(usuario);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return true; // El POST fue exitoso
            } else {
                return false; // El POST falló
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Si algo salió mal
    }




}
