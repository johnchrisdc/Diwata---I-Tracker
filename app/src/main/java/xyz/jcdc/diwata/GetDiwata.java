package xyz.jcdc.diwata;

/**
 * Created by jcdc on 4/22/17.
 */

import android.os.AsyncTask;

import xyz.jcdc.diwata.model.Diwata;

public class GetDiwata extends AsyncTask<String, String, Diwata> {

    Diwata.DiwataPositionListener diwataPositionListener;

    private boolean isCancelled = false;

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
        if (isCancelled)
            return null;

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

    @Override
    protected void onCancelled() {
        super.onCancelled();
        isCancelled = true;
    }
}