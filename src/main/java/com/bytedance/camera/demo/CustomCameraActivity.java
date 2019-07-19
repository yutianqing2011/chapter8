package com.bytedance.camera.demo;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.camera.demo.utils.Utils.getOutputMediaFile;

//import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;

public class CustomCameraActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private  SurfaceHolder surfaceHolder;
    private String videoPath = null;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;
    private static int zoomValue = 0;

    private Camera.AutoFocusCallback myAutoFocusCallback = null;

    private boolean mPreviewRunning = false;
    private final int videoWidth = 1280;

    private final int videoHight = 720;


//设置聚焦
//
//    private void initCamera(SurfaceHolder holder) {
//        if (mPreviewRunning) {
//            mCamera.stopPreview();
//        }
//        Camera.Parameters parameters;
//        try {
//            parameters = mCamera.getParameters();
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return;
//        }
//        parameters.setPreviewSize(videoWidth, videoHight);
//        parameters.setPictureFormat(PixelFormat.JPEG);
//        parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
//        SetCameraFPS(parameters);
//        setCameraDisplayOrientation(this, curCameraIndex, mCamera);
//        mCamera.setParameters(parameters);
//        int bufferSize = (((videoWidth | 0xf) + 1) * videoHight * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())) / 8;
//        mCamera.addCallbackBuffer(new byte[bufferSize]);
//        mCamera.setPreviewCallbackWithBuffer(this);
//        try {
//            mCamera.setPreviewDisplay(holder);
//        } catch (Exception ex) {
//            // TODO Auto-generated catch block
//            if (null != mCamera) {
//                mCamera.release();
//                mCamera = null;
//            }
//            ex.printStackTrace();
//        }
//        mCamera.startPreview();
//        mCamera.autoFocus(myAutoFocusCallback);
//        mCamera.cancelAutoFocus();
//        mPreviewRunning = true;
//    }
//
//    private void doAutoFocus() {
//        parameters = mCamera.getParameters();
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        mCamera.setParameters(parameters);
//        mCamera.autoFocus(new Camera.AutoFocusCallback() {
//            @Override
//            public void onAutoFocus(boolean success, Camera camera) {
//                if (success) {
//                    camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
//                    if (!Build.MODEL.equals("KORIDY H30")) {
//                        parameters = camera.getParameters();
//                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续自动对焦
//                        camera.setParameters(parameters);
//                    } else {
//                        parameters = camera.getParameters();
//                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                        camera.setParameters(parameters);
//                    }
//                }
//            }
//        });
//    }
//
//    private void SetCameraFPS(Camera.Parameters parameters) {
//        if (parameters == null) {
//            return;
//        }
//        int[] findRange = null;
//        int defFPS = 20 * 1000;
//        List<int[]> fpsList = parameters.getSupportedPreviewFpsRange();
//        if (fpsList != null && fpsList.size() > 0) {
//            for (int i = 0; i < fpsList.size(); ++i) {
//                int[] range = fpsList.get(i);
//                if (range != null && Camera.Parameters.PREVIEW_FPS_MIN_INDEX < range.length
//                        && Camera.Parameters.PREVIEW_FPS_MAX_INDEX < range.length) {
//                    if (findRange == null) {
//                        if (defFPS <= range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]) {
//                            findRange = range;
//                            }
//                    }
//                }
//            }
//        }
//        if (findRange != null) {
//            parameters.setPreviewFpsRange(findRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], findRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
//        }
//    }
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        //i do
        mPreviewRunning = true;
//        myAutoFocusCallback = new Camera.AutoFocusCallback() {
//            public void onAutoFocus(boolean success, Camera camera) {
//                if (success){ //success表示对焦成功
//                    Log.i("myAutoFocusCallback", "onAutoFocus succeed...");
//                    camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
//                    initCamera(surfaceHolder);
//                    doAutoFocus();
//                } else {
//                    Log.i("myAutoFocusCallback", "onAutoFocus failed...");
//                }
//            }
//        };
        mCamera=getCamera(1);
        mSurfaceView=findViewById(R.id.img);
        surfaceHolder=mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                    mCamera.autoFocus(myAutoFocusCallback);
                    mCamera.cancelAutoFocus();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera=null;
            }
        });


        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback
        mCamera = getCamera(CAMERA_TYPE);
        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        });


        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            //todo 拍一张照片
            mCamera.takePicture(null, null, mPicture);
        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                //todo 停止录制
                releaseMediaRecorder();
                isRecording = false;
            } else {
                //todo 录制
                isRecording = true;
                prepareVideoRecorder();
                try{
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                }catch (Exception e){
                    mMediaRecorder.release();
                    releaseMediaRecorder();
                    return;
                }
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头
            if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_FRONT)
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            else
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            startPreview(surfaceHolder);
        });

        findViewById(R.id.btn_zoom).setOnClickListener(v -> {
            //todo 调焦，需要判断手机是否支持
            if(!mCamera.getParameters().isZoomSupported()){
                Log.d("Zoom","not supported!");
                return;
            }

            Camera.Parameters params = mCamera.getParameters();
            try{
                if(zoomValue >= 50)
                {
                    zoomValue = 0;
                }
                else
                {
                    zoomValue+=5;
                }
                params.setZoom(zoomValue);
                mCamera.setParameters(params);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE);
        cam.setDisplayOrientation(rotationDegree);

        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        if(mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览

        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder


        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        mMediaRecorder.setOutputFile(videoPath);
//        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            releaseMediaRecorder();
        }catch (Exception e){
            releaseMediaRecorder();
        }
        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        try{
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoPath))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
