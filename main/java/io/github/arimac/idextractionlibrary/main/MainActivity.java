package io.github.arimac.idextractionlibrary.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.opencv.android.OpenCVLoader;

import io.github.arimac.idextractionlibrary.values.ImageMat;
import rx.subjects.PublishSubject;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "idcap";
    public static io.github.arimac.idextractionlibrary.values.ImageMat imageMat;
    @SuppressWarnings("deprecation")
    private Camera camera;
    @SuppressWarnings("deprecation")
    private Parameters parameters;
    boolean isFlash = false;
    boolean isOn = false;
    public io.github.arimac.idextractionlibrary.views.CameraView camView;


    static {
        if (!OpenCVLoader.initDebug()) {
        }
    }

    private PublishSubject<io.github.arimac.idextractionlibrary.values.CamValues> subject = PublishSubject.create();
    private static io.github.arimac.idextractionlibrary.controllers.CameraCanvasController value = new io.github.arimac.idextractionlibrary.controllers.CameraCanvasController();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /************************************************************************
         ***** Refer ID Extraction Documentation to setup MainActivity.class*****
         *************************************************************************/

        isFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlash) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
            return;
        }
    }


    public void capimage(View view){
        imageMat = value.getMatData();
        if (imageMat != null) {
            Intent newActivity = new Intent(this, ImageView.class);
            startActivity(newActivity);
        }
    }

    public static ImageMat getImageMat() {
        return imageMat;
    }


    @SuppressWarnings("deprecation")
    private void getCamera() {
        if (camera == null) {
            try {
                camera = android.hardware.Camera.open();
                parameters = camera.getParameters();
            } catch (RuntimeException e) {
                Log.i(TAG, e.getMessage());
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();

        }
    }
}


