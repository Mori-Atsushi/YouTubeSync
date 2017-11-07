package com.example.atsushi.youtubesync.server;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.atsushi.youtubesync.MySelf;
import com.example.atsushi.youtubesync.json_data.JsonParameter;
import com.example.atsushi.youtubesync.json_data.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by chigichan24 on 2017/10/29.
 */

public class HttpRequestsHelper {

    @NonNull
    final static String TAG = HttpRequestsHelper.class.getSimpleName();
    @NonNull
    final static String host = "http://59.106.220.89:3000/api/v1/";
    final static private int HTTP_SUCCESS_STATUS = 200;
    @NonNull
    protected JsonParameter parameter;

    @NonNull
    private Gson gson;

    protected HttpRequestsHelper() {
        this.gson = new Gson();
    }

    protected void post(final JsonParameter jsonParameter, final String endPoint, final HttpRequestCallback callback) {
        communicate("POST", jsonParameter, endPoint, callback);
    }

    protected void get(final HashMap<String, String> hashParameter, final String endPoint, final HttpRequestCallback callback) {
        String params = "";
        for (String key : hashParameter.keySet()) {
            if(params.equals("")) {
                params += "?";
            } else {
                params += "&";
            }
            params += key + "=" + hashParameter.get(key);
        }
        communicate("GET", null, endPoint + params, callback);
    }

    public interface HttpRequestCallback {
        void call(Response response);
    }

    private void communicate(final String method, final JsonParameter jsonParameter, final String endPoint, final HttpRequestCallback callback) {
        try {
            this.parameter = jsonParameter;
        } catch (NullPointerException e) {
            Log.e(TAG, "There was error that NullPointerException" + Arrays.toString(e.getStackTrace()));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(host + endPoint);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(method);
                    con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    if (MySelf.exists()) {
                        con.setRequestProperty("Authorization", MySelf.getToken());
                    }

                    if (jsonParameter != null) {
                        PrintStream ps = new PrintStream(con.getOutputStream());
                        ps.print(gson.toJson(parameter));
                        ps.close();
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String buffer = reader.readLine();

                    Response response = gson.fromJson(buffer, Response.class);
                    if (con.getResponseCode() == HTTP_SUCCESS_STATUS) {
                        callback.call(response);
                    }

                } catch (MalformedURLException e) {
                    Log.e(TAG, "There was a post error: " + Arrays.toString(e.getStackTrace()));
                } catch (IOException e) {
                    Log.e(TAG, "There was a I/O error: " + Arrays.toString(e.getStackTrace()));
                } catch (NullPointerException e) {
                    Log.e(TAG, "There was a error that NullPointerException" + Arrays.toString(e.getStackTrace()));
                }
            }
        }).start();
    }


}
