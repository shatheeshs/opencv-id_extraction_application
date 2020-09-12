package io.github.arimac.idextractionlibrary.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;


public class ImageView extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (MainActivity.getImageMat() != null) {
            io.github.arimac.idextractionlibrary.values.ImageMat imageMat = MainActivity.getImageMat();
            List<Point> cameraPoints = io.github.arimac.idextractionlibrary.controllers.SetEnhance.cameraPoint(imageMat);
            Mat destImage = new Mat((int) imageMat.cameraWidth, (int) imageMat.cameraHeight, imageMat.oriMat.type());
            Mat src = new MatOfPoint2f(cameraPoints.get(0), cameraPoints.get(1), cameraPoints.get(2), cameraPoints.get(3));
            Mat dst = new MatOfPoint2f(new Point(0, 0), new Point(imageMat.oriMat.width() * imageMat.cameraRatio - 1, 0), new Point(imageMat.oriMat.width() * imageMat.cameraRatio - 1, imageMat.oriMat.height() * imageMat.cameraRatio - 1), new Point(0, imageMat.oriMat.height() * imageMat.cameraRatio - 1));
            Mat transform = Imgproc.getPerspectiveTransform(src, dst);
            Imgproc.warpPerspective(imageMat.oriMat, destImage, transform, new Size(imageMat.cameraWidth, imageMat.cameraHeight));
        }



    }
}


