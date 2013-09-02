package com.meet_me;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class Camera extends Activity {

	private Uri fileuri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
   public void OpenCamera(View v){
  	  Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileuri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri); // set the image file name

         startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }
    @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
          if (resultCode == RESULT_OK) {  
        		ImageView imageview= (ImageView) findViewById(R.id.image);
        		Bitmap image=BitmapFactory.decodeFile(path);	
				imageview.setImageBitmap(image);
				Log.d("debug", path);	
          }
    }
    } 
    
    private  Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }
String path="";
    private  File getOutputMediaFile(int type){
       
       File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_DCIM), "Camera");
    
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
        	path=mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpg";
            mediaFile = new File(path);
        }  else {
            return null;
        }
        return mediaFile;
    }
  
}
