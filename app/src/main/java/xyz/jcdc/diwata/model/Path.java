package xyz.jcdc.diwata.model;

import com.google.gson.Gson;

import java.io.Serializable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.diwata.Variables;

/**
 * Created by jcdc on 4/22/17.
 */

public class Path implements Serializable {

    public interface PathListener {
        void onStartTracking();
        void onPathReceived(Path path);
    }

    private Features[] features;

    private String type;

    public Features[] getFeatures ()
    {
        return features;
    }

    public void setFeatures (Features[] features)
    {
        this.features = features;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public static Path getPath() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Variables.URL_PATH_8)
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();

        return new Gson().fromJson(json, Path.class);
    }
    public static Path getPath_2() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Variables.URL_PATH_2)
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();

        return new Gson().fromJson(json, Path.class);
    }


}
