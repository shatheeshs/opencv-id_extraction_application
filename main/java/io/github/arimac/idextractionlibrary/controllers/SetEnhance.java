package io.github.arimac.idextractionlibrary.controllers;


import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SetEnhance {
    public String pathString;


    public static List<Point> cameraPoint(io.github.arimac.idextractionlibrary.values.ImageMat imageMat) {
        List<Point> cameraPoints = new ArrayList<>();
        List<Point> sortedCameraPoint = new ArrayList<>();
        for (int i = 0; i < imageMat.points.size(); i++) {
            Point point = imageMat.points.get(i);
            Point cameraPoint = new Point(
                    (int) (point.x * imageMat.resizeRatio * imageMat.cameraRatio),
                    (int) (point.y * imageMat.resizeRatio * imageMat.cameraRatio));
            cameraPoints.add(cameraPoint);
        }
        int firstcamPoint = 0;
        int secondPoint = 0;
        int thirdPoint = 0;
        int forthPoint = 0;

        for (int r = 0; r < cameraPoints.size(); r++) {
            if (cameraPoints.get(firstcamPoint).x * cameraPoints.get(firstcamPoint).x + cameraPoints.get(firstcamPoint).y * cameraPoints.get(firstcamPoint).y > cameraPoints.get(r).x * cameraPoints.get(r).x + cameraPoints.get(r).y * cameraPoints.get(r).y) {
                firstcamPoint = r;
            }
        }
        for (int r = 0; r < cameraPoints.size(); r++) {
            if (cameraPoints.get(thirdPoint).x * cameraPoints.get(thirdPoint).x + cameraPoints.get(thirdPoint).y * cameraPoints.get(thirdPoint).y < cameraPoints.get(r).x * cameraPoints.get(r).x + cameraPoints.get(r).y * cameraPoints.get(r).y) {
                thirdPoint = r;
            }
        }
        secondPoint = thirdPoint;
        forthPoint = firstcamPoint;
        for (int r = 0; r < cameraPoints.size(); r++) {
            if ((firstcamPoint != r && thirdPoint != r) && (cameraPoints.get(secondPoint).y > cameraPoints.get(r).y)) {
                secondPoint = r;
            }

        }
        for (int r = 0; r < cameraPoints.size(); r++) {
            if ((firstcamPoint != r && thirdPoint != r && secondPoint != r)) {
                forthPoint = r;
            }

        }
        sortedCameraPoint.add(cameraPoints.get(firstcamPoint));
        sortedCameraPoint.add(cameraPoints.get(secondPoint));
        sortedCameraPoint.add(cameraPoints.get(thirdPoint));
        sortedCameraPoint.add(cameraPoints.get(forthPoint));
        return sortedCameraPoint;
    }

    public Mat brightnessController(Mat sourceMat, double alpha, double beta) {
        sourceMat.convertTo(sourceMat, -1, alpha, beta);
        return sourceMat;
    }

    public Mat contrastController(Mat sourceMat, Mat destMat) {
        Imgproc.equalizeHist(sourceMat, destMat);
        return destMat;
    }

    public Mat gaussianController(Mat sourceMat, Mat destMat, Size kernalSize, double sigmaX) {
        Imgproc.GaussianBlur(sourceMat, destMat, kernalSize, sigmaX);
        return destMat;
    }

    public Mat sharpnessController(Mat sourceArray, double alpha, Mat destArray, double beta, double gamma, Mat destMat) {
        Core.addWeighted(sourceArray, alpha, destArray, beta, gamma, destMat);
        return destMat;
    }

    public void saveCapturedImageController(Bitmap img) throws FileNotFoundException {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        File file = new File(path, "IDExtraction" + timeStamp + ".jpg");
        fOut = new FileOutputStream(file);
        img.setHasAlpha(true);
        img.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void overallEnhancementController(Mat imageToEnhance, Mat mask, Mat sampledImage) {
        Mat channel = new Mat(sampledImage.rows(), sampledImage.cols(), CvType.CV_8UC1);
        sampledImage.copyTo(channel, mask);
        Imgproc.cvtColor(channel, channel, Imgproc.COLOR_RGB2GRAY, 1);
        Imgproc.equalizeHist(channel, channel);
        Imgproc.cvtColor(channel, channel, Imgproc.COLOR_GRAY2RGB, 3);
        channel.copyTo(imageToEnhance, mask);
    }

    public Mat hsvEnhancementController(Mat srcImage, Mat destImage) {
        Mat V = new Mat(destImage.rows(), destImage.cols(), CvType.CV_8UC1);
        Mat S = new Mat(destImage.rows(), destImage.cols(), CvType.CV_8UC1);
        Mat HSV = new Mat();
        Imgproc.cvtColor(srcImage, HSV, Imgproc.COLOR_RGB2HSV);
        byte[] Vs = new byte[3];
        byte[] vsout = new byte[1];
        byte[] ssout = new byte[1];
        for (
                int i = 0;
                i < HSV.rows(); i++)

        {
            for (int j = 0; j < HSV.cols(); j++) {
                HSV.get(i, j, Vs);
                V.put(i, j, new byte[]{Vs[2]});
                S.put(i, j, new byte[]{Vs[1]});
            }
        }

        Imgproc.equalizeHist(V, V);
        Imgproc.equalizeHist(S, S);
        for (
                int i = 0;
                i < HSV.rows(); i++)

        {
            for (int j = 0; j < HSV.cols(); j++) {
                V.get(i, j, vsout);
                S.get(i, j, ssout);
                HSV.get(i, j, Vs);
                Vs[2] = vsout[0];
                Vs[1] = ssout[0];
                HSV.put(i, j, Vs);
            }
        }

        Mat enhancedImage = new Mat();
        Imgproc.cvtColor(HSV, enhancedImage, Imgproc.COLOR_HSV2RGB);
        return enhancedImage;
    }

    public Mat noiseCancellationThresholdMethodController(Mat srcImage,int lowthresh,int hithresh){
        Mat gray=new Mat(srcImage.size(),CvType.CV_8U);
        Imgproc.cvtColor(srcImage, gray, Imgproc.COLOR_BGR2GRAY);
        Mat mask=new Mat(srcImage.size(),CvType.CV_8U);
        Imgproc.threshold(gray, mask, lowthresh, hithresh, Imgproc.THRESH_BINARY_INV);
        Mat destMat=new Mat(srcImage.size(),CvType.CV_8UC3);
        Photo.inpaint(srcImage, mask, destMat, 20, Photo.INPAINT_TELEA);
        return destMat;

    }


    public Mat fastNoiceCancellationController(Mat srcImage,float h,float hColour,int templateWindowSize,int searchWindowSize){
        Mat destImage = new Mat(srcImage.size(),srcImage.type());
        Photo.fastNlMeansDenoisingColored(srcImage, destImage, h, hColour, templateWindowSize, searchWindowSize);
        return destImage;
    }
    public Mat histogramViewController(Mat srcImage){
        int histSize=256;
        MatOfInt hitogramSize=new MatOfInt(histSize);
        int histogramHeigth=(int) srcImage.size().height;
        int binWidth=5;
        MatOfFloat histogramRange =new MatOfFloat(0f,256f);
        Scalar[] colorsRgb = new Scalar[]{new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255)};
        MatOfInt[] channels = new MatOfInt[]{new MatOfInt(0), new MatOfInt(1), new MatOfInt(2)};
        Mat[] histograms = new Mat[]{new Mat(), new Mat(), new Mat()};
        Mat histMatBitmap = new Mat(srcImage.size(), srcImage.type());
        for (int i = 0; i < channels.length; i++) {
            Imgproc.calcHist(Collections.singletonList(srcImage), channels[i], new Mat(), histograms[i], hitogramSize, histogramRange);
            Core.normalize(histograms[i], histograms[i], histogramHeigth, 0, Core.NORM_INF);
            for (int j = 0; j < histSize; j++) {
                Point p1 = new Point(binWidth * (j - 1), histogramHeigth - Math.round(histograms[i].get(j - 1, 0)[0]));
                Point p2 = new Point(binWidth * j, histogramHeigth - Math.round(histograms[i].get(j, 0)[0]));
                Core.line(histMatBitmap, p1, p2, colorsRgb[i], 2, 8, 0);

            }
        }
        return histMatBitmap;
    }
}

