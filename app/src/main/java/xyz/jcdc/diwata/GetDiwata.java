package xyz.jcdc.diwata;

/**
 * Created by jcdc on 4/22/17.
 */

import android.os.AsyncTask;

import xyz.jcdc.diwata.model.Diwata;

public class GetDiwata extends AsyncTask<String, String, Diwata> {

    Diwata.DiwataPositionListener diwataPositionListener;

    public GetDiwata(Diwata.DiwataPositionListener diwataPositionListener) {
        this.diwataPositionListener = diwataPositionListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        diwataPositionListener.onStartTracking();
    }

    @Override
    protected Diwata doInBackground(String... strings) {
        try {
            return Diwata.getDiwata();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Diwata diwata) {
        super.onPostExecute(diwata);

        diwataPositionListener.onDiwataPositionReceived(diwata);
    }
}