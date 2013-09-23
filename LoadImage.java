package com.meet_me;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadImage extends Activity {

 private ImageView image;
 private Button uploadButton;
 private Bitmap bitmap;
 private Button selectImageButton;

 // number of images to select
 private static final int PICK_IMAGE = 1;

 /**
  * called when the activity is first created
  */
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.load_image);

  // find the views
  image = (ImageView) findViewById(R.id.uploadImage);
  uploadButton = (Button) findViewById(R.id.uploadButton);

  // on click select an image
  selectImageButton = (Button) findViewById(R.id.selectImageButton);
  selectImageButton.setOnClickListener(new OnClickListener() {

   @Override
   public void onClick(View v) {
    selectImageFromGallery();

   }
  });

  // when uploadButton is clicked
  uploadButton.setOnClickListener(new OnClickListener() {

   @Override
   public void onClick(View v) {
    new ImageUploadTask().execute();
   }
  });
 }

 /**
  * Opens dialog picker, so the user can select image from the gallery. The
  * result is returned in the method <code>onActivityResult()</code>
  */
 public void selectImageFromGallery() {
  Intent intent = new Intent();
  intent.setType("image/*");
  intent.setAction(Intent.ACTION_GET_CONTENT);
  startActivityForResult(Intent.createChooser(intent, "Select Picture"),
    PICK_IMAGE);
 }

 /**
  * Retrives the result returned from selecting image, by invoking the method
  * <code>selectImageFromGallery()</code>
  */
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);

  if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
    && null != data) {
   Uri selectedImage = data.getData();
   String[] filePathColumn = { MediaStore.Images.Media.DATA };

   Cursor cursor = getContentResolver().query(selectedImage,
     filePathColumn, null, null, null);
   cursor.moveToFirst();

   int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
   String picturePath = cursor.getString(columnIndex);
   cursor.close();

   decodeFile(picturePath);

  }
 }

 /**
  * The method decodes the image file to avoid out of memory issues. Sets the
  * selected image in to the ImageView.
  * 
  * @param filePath
  */
 public void decodeFile(String filePath) {
  // Decode image size
  BitmapFactory.Options o = new BitmapFactory.Options();
  o.inJustDecodeBounds = true;
  BitmapFactory.decodeFile(filePath, o);

  // The new size we want to scale to
  final int REQUIRED_SIZE = 1024;

  // Find the correct scale value. It should be the power of 2.
  int width_tmp = o.outWidth, height_tmp = o.outHeight;
  int scale = 1;
  while (true) {
   if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
    break;
   width_tmp /= 2;
   height_tmp /= 2;
   scale *= 2;
  }

  // Decode with inSampleSize
  BitmapFactory.Options o2 = new BitmapFactory.Options();
  o2.inSampleSize = scale;
  bitmap = BitmapFactory.decodeFile(filePath, o2);

  image.setImageBitmap(bitmap);
 }

 /**
  * The class connects with server and uploads the photo
  * 
  * 
  */
 class ImageUploadTask extends AsyncTask<Void, Void, String> {
  private String webAddressToPost = "http://p2mu.net/meetme/upload.pl";

  // private ProgressDialog dialog;
  private ProgressDialog dialog = new ProgressDialog(LoadImage.this);

  @Override
  protected void onPreExecute() {
   dialog.setMessage("Uploading...");
   dialog.show();
  }

  @Override
  protected String doInBackground(Void... params) {
   try {
    HttpClient httpClient = new DefaultHttpClient();
    HttpContext localContext = new BasicHttpContext();
    HttpPost httpPost = new HttpPost(webAddressToPost);

    MultipartEntity entity = new MultipartEntity(
      HttpMultipartMode.BROWSER_COMPATIBLE);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 100, bos);
    byte[] data = bos.toByteArray();
    String file = Base64.encodeToString(data, Base64.DEFAULT);
    entity.addPart("image", new StringBody(file));
    entity.addPart("action", new StringBody("upload"));
    entity.addPart("uid", new StringBody("1"));
    
    
    httpPost.setEntity(entity);
    HttpResponse response = httpClient.execute(httpPost,
      localContext);
    
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        response.getEntity().getContent(), "UTF-8"));

    String sResponse = reader.readLine();
    Log.d("image", sResponse);
    return sResponse;
  
   } catch (Exception e) {
    // something went wrong. connection with the server error
   }
   return null;
  }

  @Override
  protected void onPostExecute(String result) {
   dialog.dismiss();
   Toast.makeText(getApplicationContext(), "file uploaded"+result,
     Toast.LENGTH_LONG).show();
  }

 }

}