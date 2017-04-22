package xyz.jcdc.diwata;

import android.os.AsyncTask;

import xyz.jcdc.diwata.model.Path;

/**
 * Created by jcdc on 4/22/17.
 */

public class GetPath extends AsyncTask <String, String, Path> {

    Path.PathListener pathListener;
    int hours = 8;

    public GetPath(Path.PathListener pathListener) {
        this.pathListener = pathListener;
    }

    public GetPath(Path.PathListener pathListener, int hours) {
        this.pathListener = pathListener;
        this.hours = hours;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pathListener.onStartTracking();
    }

    @Override
    protected Path doInBackground(String... strings) {
        try {
            if (hours == 8)
                return Path.getPath();
            else
                return Path.getPath_2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Path path) {
        super.onPostExecute(path);
        pathListener.onPathReceived(path);
    }
}
