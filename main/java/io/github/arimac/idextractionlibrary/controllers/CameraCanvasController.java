package io.github.arimac.idextractionlibrary.controllers;

import io.github.arimac.idextractionlibrary.views.CameraView;
import io.github.arimac.idextractionlibrary.views.CanvasDraw;
import io.github.arimac.idextractionlibrary.utils.OpenCVConnector;
import io.github.arimac.idextractionlibrary.values.ImageMat;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class CameraCanvasController {
    private static final String TAG = "idcap";
    private static io.github.arimac.idextractionlibrary.values.ImageMat imageMat;

    public void onLoadCamera(CameraView cameraView, CanvasDraw canvasDraw, PublishSubject<io.github.arimac.idextractionlibrary.values.CamValues> subject) {
        cameraView.setCallback((data, camera) -> {
            io.github.arimac.idextractionlibrary.values.CamValues cameValues = new io.github.arimac.idextractionlibrary.values.CamValues();
            cameValues.data = data;
            cameValues.camera = camera;
            subject.onNext(cameValues);
        });
        cameraView.setOnClickListener(v -> cameraView.focus());
        subject.concatMap(cameraData ->
                OpenCVConnector.getRgbMat(new io.github.arimac.idextractionlibrary.values.ImageMat(), cameraData.data, cameraData.camera))
                .concatMap(matData -> OpenCVConnector.resize(matData, 400, 400))
                .map(matData -> {
                    matData.resizeRatio = (float) matData.oriMat.height() / matData.resizeMat.height();
                    matData.cameraRatio = (float) cameraView.getHeight() / matData.oriMat.height();
                    matData.cameraHeight = cameraView.getHeight();
                    matData.cameraWidth = cameraView.getWidth();
                    return matData;
                })
                .concatMap(this::detectRect)
                .compose(mainAsync())
                .subscribe(matData -> {
                    if (canvasDraw != null) {
                        if (matData.cameraPath != null) {
                            canvasDraw.setPath(matData.cameraPath);
                            this.imageMat = matData;

                        } else {
                            canvasDraw.setPath(null);
                        }
                        canvasDraw.invalidate();
                    }
                });
    }
    private Observable<ImageMat> detectRect(io.github.arimac.idextractionlibrary.values.ImageMat mataData) {
        return Observable.just(mataData)
                .concatMap(OpenCVConnector::getMonochromeMat)
                .concatMap(OpenCVConnector::getContoursMat)
                .concatMap(OpenCVConnector::getPath);
    }
    private static <T> Observable.Transformer<T, T> mainAsync() {
        return obs -> obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public io.github.arimac.idextractionlibrary.values.ImageMat getMatData(){
        return imageMat;
    }


}
