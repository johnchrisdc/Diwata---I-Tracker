package xyz.jcdc.diwata;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.jcdc.diwata.model.Diwata;
import xyz.jcdc.diwata.model.Path;

public class LoaderActivity extends AppCompatActivity {

    public static final String EXTRA_DIWATA = "diwata";
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_PATH_2 = "path2";

    private Context context;

    @BindView(R.id.task)
    TextView task;

    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;

    private GetDiwata getDiwata;
    private GetPath getPath;
    private GetPath getPath2;

    private Diwata diwata;
    private Path path, path2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_loader);

        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lottieAnimationView.setScale(0.5f);

        initializedTasks();

        getDiwata.execute();
    }

    private void showErrorSnackBar() {
        Snackbar errorSnackBar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                "Oh snap! Something went wrong", Snackbar.LENGTH_INDEFINITE);
        errorSnackBar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTasks();
                initializedTasks();

                getDiwata.execute();
                lottieAnimationView.playAnimation();
            }
        });
        errorSnackBar.show();

        lottieAnimationView.pauseAnimation();
        task.setText("Cannot find Diwata-1");
    }

    private void initializedTasks() {
        getDiwata = new GetDiwata(new Diwata.DiwataPositionListener() {
            @Override
            public void onStartTracking() {
                task.setText("Finding Diwata-1");
            }

            @Override
            public void onDiwataPositionReceived(Diwata diwata) {
                if (diwata != null) {
                    LoaderActivity.this.diwata = diwata;
                    getPath.execute();
                } else {
                    showErrorSnackBar();
                }
            }
        });

        getPath = new GetPath(new Path.PathListener() {
            @Override
            public void onStartTracking() {
                task.setText("Getting Diwata-1 path");
            }

            @Override
            public void onPathReceived(Path path) {
                if (path != null) {
                    LoaderActivity.this.path = path;
                    getPath2.execute();
                } else {
                    showErrorSnackBar();
                }
            }
        });

        getPath2 = new GetPath(new Path.PathListener() {
            @Override
            public void onStartTracking() {
                task.setText("Getting Diwata-1 path");
            }

            @Override
            public void onPathReceived(Path path) {
                if (path != null) {
                    LoaderActivity.this.path2 = path;

                    openMapActivity();
                } else {
                    showErrorSnackBar();
                }
            }
        }, 2);
    }

    private void openMapActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_DIWATA, diwata);
        intent.putExtra(EXTRA_PATH, path);
        intent.putExtra(EXTRA_PATH_2, path2);

        startActivity(intent);
        finish();
    }

    private void cancelTasks() {
        if (getDiwata != null)
            getDiwata.cancel(true);

        if (getPath != null)
            getDiwata.cancel(true);

        if (getPath2 != null)
            getPath2.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTasks();
    }
}
