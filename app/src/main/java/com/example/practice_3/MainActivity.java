package com.example.practice_3;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.opencv.imgproc.Imgproc.convexityDefects;

public class MainActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String    TAG = "MainActivity";
    private CameraBridgeViewBase   mOpenCvCameraView;
    private Mat mat1, matI;
    static Mat matT;
    int i = 0;
    Scalar scalarLow,scalarHigh;
    private boolean isButtonPressed;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        scalarLow = new Scalar(45,20,10);
        scalarHigh = new Scalar(75,255,255);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1 = new Mat(width, height, CvType.CV_8UC3);
    }

    @Override
    public void onCameraViewStopped() {
        mat1.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mat1 = inputFrame.rgba();
        matI = mat1.t();
        Core.flip(mat1.t(),matI, 1);
        Imgproc.resize(matI, matI, mat1.size());
        return matI;
    }

    public void onCaptureImage(View view) {
        Toast.makeText(MainActivity.this, "button worked.", Toast.LENGTH_SHORT).show();

        Mat image = matI.clone();
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                double[] rgb = image.get(x,y);
                if(isRedColor(rgb)){
                    image.put(x,y,new double[]{0,255,0});
                }
            }
        }


//        Mat newImage = Mat.zeros(image.size(), image.type());
//        double alpha = 3.0; /*< Simple contrast control */
//        int beta = 90;       /*< Simple brightness control */
//
//        byte[] imageData = new byte[(int) (image.total()*image.channels())];
//        image.get(0, 0, imageData);
//        byte[] newImageData = new byte[(int) (newImage.total()*newImage.channels())];
//        for (int y = 0; y < image.rows(); y++) {
//            for (int x = 0; x < image.cols(); x++) {
//                for (int c = 0; c < image.channels(); c++) {
//                    double pixelValue = imageData[(y * image.cols() + x) * image.channels() + c];
//                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;
//                    newImageData[(y * image.cols() + x) * image.channels() + c]
//                            = saturate(alpha * pixelValue + beta);
//                }
//            }
//        }
//        newImage.put(0, 0, newImageData);

//        Mat gray = new Mat();
//        Imgproc.cvtColor(temp, gray, Imgproc.COLOR_RGBA2GRAY);
//
//        Imgproc.Canny(gray, gray, 20, 20*3, 3, true);
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//
//        Imgproc.findContours(gray,contours,hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        // create Mat for mask
//        Mat mask =  new Mat(new Size(temp.cols(), temp.rows() ), CvType.CV_8UC1);
//        mask.setTo(new Scalar(0, 0, 255));
//
//        // create Scalar for color of mask objects
//        Scalar green = new Scalar(0, 255, 0);
//
//        // draw contours border and fill them
//        Imgproc.drawContours(mask, contours, 1, green, 1);
//        for (MatOfPoint contour: contours) {
//            Imgproc.polylines(mask,Arrays.asList(contour),false,green); // (mask, Arrays.asList(contour), green);
//        }
//
//        // create mat foe masked image
//        Mat masked = new Mat();
//
//        // apply mask to srcMat and set result to masked
//        temp.copyTo(masked, mask);

        //Imgproc.cvtColor (temp, temp, Imgproc.COLOR_RGB2HSV);// convert to HSV
//        org.opencv.core.Size s = new Size(3,3);
//        Imgproc.GaussianBlur(temp, temp, s, 2);
        //Core.inRange (temp, new Scalar (160,50,70), new Scalar (180,255,255), temp);


//        Mat hierarchy = Mat.zeros (new Size(5,5), CvType.CV_8UC1);
//        List<MatOfPoint>contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours (matI, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
//        Scalar color = new Scalar(200,20,100);
//
//        // Imgproc.drawContours (src1, contours, -1, color, 10);
//        int i = 0;
//        int index = -1;
//        double area = 0;
//        for (i = 0;i<contours.size ();i ++)
//        {
//            double tmp = Imgproc.contourArea (contours.get (i));
//            if (area < tmp) {
//                area = tmp;
//                index = i;
//            }
//        }
//        if (index != -1) {
//            MatOfPoint ptmat = contours.get (index);
//            color = new Scalar (0,255,0);
//            MatOfPoint2f ptmat2 = new MatOfPoint2f (ptmat.toArray ());
//            RotatedRect bbox = Imgproc.minAreaRect (ptmat2);
//            Imgproc.circle (temp, bbox.center, 5, color, 1);
//        }

        matT = image;
        Intent intent = new Intent(getApplicationContext(), DisplayImage.class);
        startActivity(intent);
        finish();
    }

    private boolean isRedColor(double[] rgb){
        //r - 135 - 255, g - 0 - 200, b - 0 - 200
        if(rgb[0] >= 135 && rgb[0] <= 255){
            if(rgb[1] >= 0 && rgb[1] <= 200){
                if(rgb[2] >= 0 && rgb[2] <= 200){
                    return true;
                }
            }
        }
        return false;
    }

//    private byte saturate(double val) {
//        int iVal = (int) Math.round(val);
//        iVal = iVal > 255 ? 255 : (Math.max(iVal, 0));
//        return (byte) iVal;
//    }
}
