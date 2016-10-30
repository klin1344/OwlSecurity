package com.sneakred.securitycam;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.FrameLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.sneakred.securitycam.imgurmodel.ImageResponse;
import com.sneakred.securitycam.imgurmodel.Upload;
import com.sneakred.securitycam.services.UploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyD10IQjSk6oClBn73afBzlsiF6uVRPttDs";
    private static final String IBM_KEY = "9137e36957251e14a22a38fd417f2e90c96547f8";
    private static final String[] keywords = {"gun", "firearm", "fire arm", "attack", "threat", "weapon", "ammo",
            "ammunition", "bullet", "hand gun", "handgun", "rifle", "assault", "machine gun",
            "gunmetal", "trigger", "burst", "caliber", "choke", "gauge", "gunpowder", "holographic",
            "cartridge", "assault rifle", "gun barrel", "weapon", "gun accessory", "revolver",
            "shotgun", "knife", "blade", "melee", "cold weapon", "hunting knife", "bowie knife",
            "throwing knife", "fire", "burn", "bomb", "armed", "defuse", "activated", "flame",
            "campfire", "bonfire", "dynamite", "explosion", "missile", "charge",
            "geological phenomenon", "thief", "robber", "sneak", "stealing", "steal", "crash",
            "fall", "danger", "burglar", "accident", "stolen", "break in"};

    static String filePath;
    private final String EMERGENCY_NO = "7143264413";
    VisualRecognition service;
    private String uRL;
    private Camera mCamera;
    private CameraPreview mPreview;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private int count;
    private boolean sentSMS = false;
    private String watsonString;
    private String[] numbers;
    private Upload upload;
    private ArrayList<String> links = new ArrayList<String>();
    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                //Log.d(TAG, "Error creating media file, check storage permissions: " + e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                mCamera.startPreview();
                IBMVisualRecognition(filePath);
                callCloudVision(BitmapFactory.decodeFile(filePath));

            } catch (FileNotFoundException e) {
                //Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                //Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
        count = 0;
        imagePaths = new ArrayList<String>();

        numbers = new String[5];
        for (int i = 0; i < 5; i++) {
            numbers[i] = sharedPref.getString("contact" + (i + 1), "");
            System.out.println(" numbers " + numbers[i]);
        }

        service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey(IBM_KEY);
        //service.setUsernameAndPassword("GXXXXxxxxxxx", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");


        // Create an instance of Camera
        mCamera = getCameraInstance();
        Camera.Parameters param = mCamera.getParameters();
        param.setPictureSize(800, 600);
        mCamera.setParameters(param);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        StartRecording();

    }

    private void IBMVisualRecognition(final String filePath) throws IOException {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    //System.out.println("Classify an image");
                    ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                            .images(new File(filePath))
                            .build();
                    VisualClassification result = service.classify(options).execute();
                    String watsonClassifier = result.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getName();
                    //System.out.println("IBM " + watsonClassifier);
                    if (watsonClassifier != null) {
                        watsonString = watsonClassifier;

                    } else {
                        watsonString = "";
                    }
                } catch (Exception e) {

                }

                return "IBM Visual Recognition API request failed. Check logs for details.";
            }


        }.execute();


    }

    @Override
    protected void onPause() {
        super.onPause();
        for (String path : imagePaths) {
            File delete = new File(path);
            delete.delete();
        }
        imagePaths.clear();

        releaseCamera();              // release the camera immediately on pause event
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(MainActivity.this.getFilesDir(), "SecurityCam");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        filePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        imagePaths.add(filePath);
        System.out.println(filePath);

        mediaFile = new File(filePath);
        return mediaFile;
    }


    private void StartRecording() {
        final Handler h = new Handler();
        final int delay = 2000; //milliseconds
        //mCamera.takePicture(null, null, mPicture);
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                mCamera.takePicture(null, null, mPicture);

                //System.out.println("pic taken");
                if (count >= 10) {

                    File delete = new File(imagePaths.get(0));
                    delete.delete();
                    imagePaths.remove(0);

                } else {
                    count++;
                }
                //System.out.println(sentSMS);
                if (!sentSMS) {
                    h.postDelayed(this, delay);
                } else {
                    h.removeCallbacks(this);
                }


            }
        }, delay);


    }
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    //Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    //System.out.println(response);
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    System.out.println("failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    System.out.println("failed to make API request because of other IOException " +
                            e.getMessage());
                }

                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                String[] arr = result.split(",");
                //System.out.println(result);
                /*for (int i = 0; i < arr.length; i++) {
                    System.out.println("Google "+ arr[i]);
                }*/
                if (isDangerous(arr, 3)) {
                    uploadImage(filePath);
                    sendSMS(arr);
                }


                //sendSMS(arr, 3);
                //boolean danger = isDangerous(arr, 3);
                //System.out.println("is Dangerours" + danger);
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        //String [] arr = new String[10];

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {

            for (int i = 0; i < labels.size(); i++) {
                EntityAnnotation label = new EntityAnnotation();
                if (label != null) {
                    message += String.format(labels.get(i).getDescription() + ",");
                }

            }
            //System.out.println("watsonString " + watsonString);
            message += watsonString;
            //System.out.println(message);

        }
        //System.out.println(message);
        return message;
    }

    private boolean isDangerous(String[] detections, int confidence) {
        int counter = 0;
        boolean detectDanger = false;
        for (int i = 0; i < keywords.length; i++) {
            for (int j = 0; j < detections.length; j++) {
                if (keywords[i].toLowerCase().equals(detections[j].toLowerCase())) {
                    counter = counter + 1;
                }
            }
        }
        if (counter >= confidence)
            detectDanger = true;

        return detectDanger;
    }

    private void sendSMS(String[] detections) {

        if (!sentSMS) {
            sentSMS = true;
            String timeStamp = new SimpleDateFormat("HH:mm:ss, MM/dd/yyyy").format(new Date());
            String number = EMERGENCY_NO;
            String message = "OwlSecurity has detected the following threats: " + detections[0] + ", " + detections[1] + ", and " + detections[2] + " at " + timeStamp + ".";
            message += "\n911 has been alerted.\nPhoto:\n";

            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(number, null, message, null, null);
            for (int i = 0; i < numbers.length; i++) {
                if (!numbers[i].equals("")) {
                    manager.sendTextMessage(numbers[i], null, message, null, null);
                }
            }
        }
    }

    public void uploadImage(String path) {
    /*
      Create the @Upload object
     */
        if (!sentSMS) {
            File chosenFile = new File(path);
            if (chosenFile == null) return;
            createUpload(chosenFile);
        }


    /*
      Start upload
     */
        new UploadService(this).Execute(upload, new UiCallback());
    }

    private void createUpload(File image) {
        upload = new Upload();
        upload.image = image;

    }

    private class UiCallback implements Callback<ImageResponse> {


        @Override
        public void success(ImageResponse imageResponse, Response response) {
            //clearInput();
            //System.out.println("success");
            //System.out.println(imageResponse.data.link.toString());
            uRL = imageResponse.data.link;
            String photo = "Photo:\n" + uRL;
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(EMERGENCY_NO, null, photo, null, null);
            for (int i = 0; i < numbers.length; i++) {
                if (!numbers[i].equals("")) {
                    manager.sendTextMessage(numbers[i], null, photo, null, null);
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            //System.out.println("ERROR" );
            //Assume we have no connection, since error is null
            if (error == null) {
                //nackbar.make(findViewById(R.id.rootView), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}
