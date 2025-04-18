package com.example.user.ConexionBD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.user.Activity.home_activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BD {

    private static final String BASE_URL = "https://boostup.life/";
    private final Gson gson;
    private final OkHttpClient client;
    private Context context;

    //Funciones uso comun
    public void runOnUiThread(Runnable action) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(action);
        }
    }

    public BD(Context context) {
        this.context = context;
        gson = new GsonBuilder().create();
        client = new OkHttpClient();
    }

    public interface JsonCallback {
        void onSuccess(JsonObject obj);
        void onError(String mensaje);
    }

    public interface JsonArrayCallback {
        void onSuccess(JsonArray array);
        void onError(String mensaje);
    }

    public interface PrecioCallback {
        void onSuccess(double precio);
        void onError(String mensaje);
    }

    private void getRequest(String route, Callback callback) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
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
    public double getPrecioCurcuma(int idCurcuma) {
        String url = BASE_URL + "curcuma/" + idCurcuma + "/precio/";

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

    //Token e inicio de sesion
    public void iniciaSesion(String email, String pass){


        getAuthToken(email, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {

                Preferences preferencias = new Preferences(context);
                preferencias.guardarToken(token);

                Intent intent = new Intent(context, home_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                runOnUiThread(() -> {
                    // Mostrar mensaje
                    Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    // Mostrar el mensaje de error
                    Toast.makeText(context, "Usuario o Contraseña erroneos", Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    public interface AuthCallback {
        void onSuccess(String token);
        void onFailure(String error);
    }

    public void getAuthToken(String username, String password, AuthCallback callback){
        String url = BASE_URL + "usuario/token/";
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .add("scope", "")
                .add("client_id", "string")
                .add("client_secret", "string")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()){
                    if (!response.isSuccessful()){
                        runOnUiThread(() -> callback.onFailure("HTTP error: " + response.code()));
                        return;
                    }

                    assert  responseBody != null;
                    String responseData = responseBody.string();

                    try {
                        JSONObject json = new JSONObject(responseData);
                        String token = json.optString("access_token");
                        if (!token.isEmpty()){
                            runOnUiThread(()-> callback.onSuccess(token));
                        }else{
                            runOnUiThread(()-> callback.onFailure("Invalid Token in response"));
                        }
                    } catch (JSONException e) {
                        runOnUiThread(()-> callback.onFailure("JSON parsing error"));
                    }
                }
            }
        });
    }

    //Get los detalles del pedido (proteina, marca, monto, fecha de compra, estado de canje,
    // gr de proteina y curcuma, marca de curcuma, sabor, tipo de saborizante, marca saborizante)
    public void getDetallesPedido(String id, JsonCallback callback) {
        String ruta = "pedido/"+ id+"/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show());
                callback.onError("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error HTTP: " + response.message());
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    runOnUiThread(() -> Toast.makeText(context, "Pedido no encontrado", Toast.LENGTH_SHORT).show());
                    callback.onError("Pedido no encontrado");
                    return;
                }

                try {
                    String json = responseBody.string();
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    String proteina = obj.get("proteina").getAsString();
                    double monto = obj.get("monto_total").getAsDouble();
                    String fechacompra = obj.get("fec_hora_compra").getAsString();
                    String estadocanje = obj.get("estado_canje").getAsString();
                    int proteinagr = obj.get("proteina_gr").getAsInt();
                    String sabor = obj.get("sabor").getAsString();
                    String tipoSabor = obj.get("tipo_saborizante").getAsString();
                    String marcaProteina = obj.get("proteina_marca").getAsString();
                    String marcaSaborizante = obj.get("saborizante_marca").getAsString();

                    String curcumaMarca = obj.has("curcuma_marca") && !obj.get("curcuma_marca").isJsonNull()
                            ? obj.get("curcuma_marca").getAsString()
                            : "N/A";

                    int curcumaGr = obj.has("curcuma_gr") && !obj.get("curcuma_gr").isJsonNull()
                            ? obj.get("curcuma_gr").getAsInt()
                            : 0;

                    // Retornar los datos al callback
                    callback.onSuccess(obj);

                } catch (Exception e) {
                    callback.onError("Error al procesar JSON: " + e.getMessage());
                }
            }


        });
    }

    //Get opciones de proteina: id, nombre, tipo y precio
    public void getOpcionesProteinas(JsonArrayCallback callback){
        String ruta= "proteina/opciones/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                        callback.onSuccess(array);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    //Get precio de una proteina en especifico
    public void getPrecioProteina(int id, PrecioCallback callback) {
        String ruta = "proteina/" + id + "/precio/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        double precio = obj.get("precio").getAsDouble();
                        callback.onSuccess(precio);
                    } catch (Exception e) {
                        callback.onError("Error al procesar el precio");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
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
