package com.example.user.ConexionBD;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.user.Activity.home_activity;
import com.example.user.Activity.login_activity;
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
import java.util.logging.Handler;

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

    public interface AlergiaCallback {
        void onSuccess();
        void onFailure();
    }

    public interface PedidoCallback {
        void onSuccess(String clientSecret, String id_pedido);
        void onFailure();
    }

    public interface BooleanCallback {
        void onSuccess(boolean existe);
        void onFailure();
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

    private void authGetRequest(String route, Callback callback) {
        Preferences preferences = new Preferences(context);
        String token = preferences.obtenerToken();

        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void putAuthRequest(String route, String jsonBody, Callback callback) {
        Preferences preferences = new Preferences(context);
        String token = preferences.obtenerToken();

        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Crear el cuerpo de la solicitud con el JSON
        RequestBody body = RequestBody.create(JSON, jsonBody);

        // Construir la solicitud PUT con el token de autenticación
        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .put(body) 
                .build();

        // Realizar la llamada a la API
        client.newCall(request).enqueue(callback);
    }

    private void PostRequest(String route, JSONObject json, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        final String JSONStr = json.toString();

        final RequestBody requestBody = RequestBody.create(
                MediaType.get("application/json; charset=utf-8"),
                JSONStr
                );

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void autenticarYGuardarToken(String email, String pass, LoginCallback callback){
        getAuthToken(email, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {
                Preferences preferencias = new Preferences(context);
                preferencias.guardarCredenciales(token, email, pass);

                runOnUiThread(() -> {
                    Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    callback.onLoginSuccess();  // no lanza actividad
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    //Toast.makeText(context, "Usuario o Contraseña erroneos", Toast.LENGTH_SHORT).show();
                    callback.onLoginFailed();
                });
            }
        });
    }

    private void PostAuthRequest(String route, JSONObject json, Callback callback) {
        Preferences preferences = new Preferences(context);
        String token = preferences.obtenerToken();

        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
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

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    //Token e inicio de sesion
    public void iniciaSesion(String email, String pass, LoginCallback callback){


        getAuthToken(email, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {

                Preferences preferencias = new Preferences(context);
                preferencias.guardarCredenciales(token, email, pass);

                Intent intent = new Intent(context, home_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                runOnUiThread(() -> {
                    // Mostrar mensaje
                    Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    callback.onLoginSuccess();
                });

            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    // Mostrar el mensaje de error
                    //Toast.makeText(context, "Usuario o Contraseña erroneos", Toast.LENGTH_SHORT).show();
                    callback.onLoginFailed();
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

    //Get alergenos de una proteina en especifico
    public void getAlergenosProteina(int id, JsonArrayCallback callback){
        String ruta = "proteina/"+ id + "/alergenos/";

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
                        callback.onError("Error al procesar los alérgenos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    //Get Nombre, username, email, edad, sexo, fec. nacimiento, fec. registro, pk de sus medida y
    // pk de sus cantidades
    public void getInfoUser(JsonCallback callback){
        String ruta = "usuario/yo/";

        authGetRequest(ruta, new Callback() {
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
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void probarToken(JsonCallback callback) {
        getInfoUser(new JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                callback.onSuccess(obj); // Token válido
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje); // Token inválido
            }
        });
    }

    //Get peso, talla, cintura, cadera, circ. brazo y edad
    public void getMedidasUser(JsonCallback callback){
        String ruta = "usuario/medidas/";

        authGetRequest(ruta, new Callback() {
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
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    //GET las alergias y el id de la alergia del usuario
    public void getAlergiasUser(JsonArrayCallback callback){
        String ruta= "usuario/alergenos/";

        authGetRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        // Si la respuesta está vacía o es un array vacío, igual se retorna un array válido
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                        if (array == null) {
                            callback.onSuccess(new JsonArray());  // pasa array vacío
                        } else {
                            callback.onSuccess(array);
                        }
                    } catch (Exception e) {
                        // En caso de error en el parseo, pasar array vacío en lugar de error
                        callback.onSuccess(new JsonArray());
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    //GET id pedido, tipo proteina, proteina, sabor y fecha y hora de compra
    public void getInfoPedidosGeneralUser(JsonArrayCallback callback){
        String ruta = "usuario/pedidos/general/";

        authGetRequest(ruta, new Callback() {
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

    public void getTopProteina(JsonArrayCallback callback){
        String ruta = "top-proteinas/";

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
                        callback.onError("Error al procesar el top");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getTopSabores(JsonArrayCallback callback){
        String ruta = "top-sabores/";

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
                        callback.onError("Error al procesar el top");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDatosCurcuma(JsonArrayCallback callback){
        String ruta = "curcuma/opciones/";

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
                        callback.onError("Error al obetener los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesProteina(int id, JsonCallback callback){
        String ruta = "proteina/"+id+"/detalles/";

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
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesSaborizante(int id, JsonCallback callback){
        String ruta = "saborizante/"+id+"/detalles";

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
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesCurcuma(int id, JsonCallback callback){
        String ruta= "curcuma/"+id+"/detalles/";

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
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
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
                                    String sexo, String fec_nacimiento, int peso_kg, int talla_cm, int cintura_cm,
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


    public void registrarAlergiaUsuario(int id_alergia, AlergiaCallback callback) {
        String ruta = "usuario/alergenos/";

        JSONObject json = new JSONObject();
        try {
            json.put("tipo_alergeno", id_alergia);
        } catch (JSONException e) {
            callback.onFailure();
            return;
        }

        PostAuthRequest(ruta, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    public void comprobarEmailExiste (String email, BooleanCallback callback){
        String ruta = "usuario/existe/";
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            callback.onFailure();
            return;
        }

        PostRequest(ruta, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody); // <- parsear a JSON
                        boolean existe = jsonResponse.optBoolean("exists", false); // <- obtener el booleano
                        callback.onSuccess(existe);
                    } catch (JSONException e) {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }
        });
    }


    /*---------------------------------------------------------------------------------------*/
    //PUTS

    public void ActualizarDatosUser(String nombre, String apellido, String username, Callback callback) {
        String ruta = "usuario/updateMe/";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("nombre", nombre);
            jsonObject.put("apellido", apellido);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonBody = jsonObject.toString();

        putAuthRequest(ruta, jsonBody, callback);
    }

    public void ActualizarMedidasUser(int peso, int talla, int cintura, int cadera, int circ_brazo, Callback callback){
        String ruta = "usuario/updateMedidas/";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("peso_kg", peso);
            jsonObject.put("talla_cm", talla);
            jsonObject.put("cintura_cm", cintura);
            jsonObject.put("cadera_cm", cadera);
            jsonObject.put("circ_brazo_cm", circ_brazo);
        }catch (JSONException e){
            e.printStackTrace();
        }

        String jsonBody = jsonObject.toString();
        putAuthRequest(ruta, jsonBody, callback);
    }

    public  void ActualizarPass(String pass, Callback callback){
        String ruta = "usuario/updatePassword/";
        JSONObject json = new JSONObject();
        try {
            json.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonBody = json.toString();
        putAuthRequest(ruta, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(context, "Fallo: "+e, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(()->{
                    Toast.makeText(context, "Contraseña modificada", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /*---------------------------------------------------------------------------------------*/
    //DELETES

    public void eliminarCuenta(Callback callback) {
        String ruta = "usuario/deleteMe/";

        Preferences preferences = new Preferences(context);
        String token = preferences.obtenerToken();

        if (token == null || token.isEmpty()) {
            runOnUiThread(() -> {
                Toast.makeText(context, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            });
            return;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + ruta)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .delete()
                .build();

        client.newCall(request).enqueue(callback);

    }

    /*--------------Stripe--------------------*/

    public void crearPedido(int proteina, int curcuma, int saborizante, PedidoCallback callback){
        String ruta = "usuario/pedir/";

        JSONObject json = new JSONObject();
        try {
            json.put("proteina", proteina);
            json.put("curcuma", curcuma);
            json.put("saborizante", saborizante);
        } catch (JSONException e) {
            callback.onFailure();
            return;
        }
        PostAuthRequest(ruta, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String clientSecret = jsonResponse.optString("clientSecret", null);
                        String id_pedido = jsonResponse.optString("id_pedido", null);
                        if (clientSecret != null) {
                            callback.onSuccess(clientSecret, id_pedido);
                        } else {
                            callback.onFailure();
                        }
                    } catch (JSONException e) {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }
        });
    }
}
