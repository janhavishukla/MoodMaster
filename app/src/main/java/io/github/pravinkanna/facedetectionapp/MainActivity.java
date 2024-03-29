package io.github.pravinkanna.facedetectionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton cameraButton;
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    private FirebaseVisionImage image; //Correction here
    private FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FirebaseApp.initializeApp(this);
        cameraButton = findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            detectFace(bitmap);

        }


    }

    private void detectFace(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(true)
                        .build();

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                String resultText = "";
                int i = 1;
                for (FirebaseVisionFace face : firebaseVisionFaces){

                    if ((face.getSmilingProbability()*100)<25){
                         resultText = resultText.concat("\n"+i+".")
                                 .concat("\nEmotion : Depressed");
                        i++;
                        //break;
                    }else if ((face.getSmilingProbability()*100)<50){
                        resultText = resultText.concat("\n"+i+".")
                                .concat("\nEmotion : Sad");
                        i++;
                        //break;
                    }else if ((face.getSmilingProbability()*100)<75){
                        resultText = resultText.concat("\n"+i+".")
                                .concat("\nEmotion : Neutral");
                        i++;
                        // break;
                    }else if ((face.getSmilingProbability()*100)<90){
                        resultText = resultText.concat("\n"+i+".")
                                .concat("\nEmotion : Happy");
                        i++;
                        //break;
                    }else if ((face.getSmilingProbability()*100)<=100){
                        resultText = resultText.concat("\n"+i+".")
                                .concat("\nEmotion : Excited");
                        i++;
                        //break;
                    }

                   // resultText = resultText.concat("\n"+i+".")
                     //       .concat("\nEmotion:"+ face.getSmilingProbability()*100+"%")
                            // .concat("\nSmile: " + face.getSmilingProbability()*100+"%")
                       //     .concat("\nRightEye:"+ face.getRightEyeOpenProbability()*100+"%")
                         //   .concat("\nLeftEye:"+ face.getLeftEyeOpenProbability()*100+"%");
                }

                if (firebaseVisionFaces.size() == 0){
                    Toast.makeText(MainActivity.this, "NO FACES", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FaceDetection.RESULT_TEXT, resultText);
                    DialogFragment resultDialog = new ResultDialog();
                    resultDialog.setArguments(bundle);
                    resultDialog.setCancelable(false);
                    resultDialog.show(getSupportFragmentManager(), FaceDetection.RESULT_DIALOG);
                }
            }
        });


    }
}