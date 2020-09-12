package io.github.arimac.idextractionlibrary.values;

import android.graphics.Path;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

public class ImageMat {
    public Mat resizeMat;
    public Mat monoChrome;
    public List<Point> points;
    public Mat oriMat;
    public float resizeRatio;
    public float cameraRatio;
    public Path cameraPath;
    public  float cameraHeight;
    public  float cameraWidth;

}
