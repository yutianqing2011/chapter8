package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

import com.bytedance.camera.demo.utils.Utils;

public class TakePictureActivity extends AppCompatActivity {
    private  String permision[]={
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    File imgFile;

    public static File getOutputMediaFile(int type){
        File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"CameraDemo");
        String timeStamp="";
        if (Build.VERSION.SDK_INT >= 24) {
            timeStamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        }
        File mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
        return mediaFile;
    }

    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                //ContextCompat.checkSelfPermission(this,);
                ActivityCompat.requestPermissions(TakePictureActivity.this,permision,1);


            } else {
                takePicture();
            }
        });

    }


    private void takePicture() {
        //todo 打开相机
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile=Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if(imgFile!=null){
            Uri fileUri= FileProvider.getUriForFile(this,"com.bytedance.camera.demo",imgFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }

        //startActivityForResult(takePictureIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory
                    .decodeFile(imgFile.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scalFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scalFactor;
            bmOptions.inPurgeable = true;

            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
            imageView.setImageBitmap(bmp);
//            setPic();
//
//            //i do
//            Bundle extras=data.getExtras();
//            Bitmap imageBitmap=(Bitmap)extras.get("data");
//            ImageView mImageView=findViewById(R.id.img);
//            mImageView.setImageBitmap(imageBitmap);
        }
        setPic();
    }

    private void setPic() {
        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        String path = imgFile.getAbsolutePath();

        BitmapFactory.decodeFile(path,options);

        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        Bitmap bitmapRotated = Utils.rotateImage(bitmap,path);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imgFile)));

        imageView.setImageBitmap(bitmapRotated);


        //todo 如果存在预览方向改变，进行图片旋转

        //todo 如果存在预览方向改变，进行图片旋转
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                break;
            }
        }
    }
}
