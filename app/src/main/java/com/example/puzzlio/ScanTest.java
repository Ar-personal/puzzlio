package com.example.puzzlio;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScanTest extends AppCompatActivity {


    private static final String TAG = ScanTest.class.getSimpleName();
    public static final String TESS_DATA = "/tessData";
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private String DATA_PATH;
    private String mCurrentPhotoPath;
    private ImageProcessing imageProcessing;
    private Bitmap bitmap, finalbmp;
    private List<Mat> grids;
//    private Bitmap gridBitmaps[][] = new Bitmap[9][9];
    private ArrayList<Bitmap> gridBitmaps = new ArrayList();
//    private String [][] scannedValues = new String[9][9];
    private ArrayList<String> scannedValues = new ArrayList();
    private Context mContext;
    private Uri imageUri;
    private ContentValues values;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView, imageTest;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int gridSize;
    private boolean canCreate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scantest);
        checkPermission();

        mContext = getApplicationContext();
        DATA_PATH = mContext.getExternalFilesDir(null).toString();

//        setContentView(R.layout.capture);

        //test if open cv loads correctly
        if(OpenCVLoader.initDebug()){
            Toast.makeText(this, "loaded", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("ok", "open cv fault");
        }

        TextView textView = findViewById(R.id.ocrtext);
        Button processButton = findViewById(R.id.processbutton);

        imageTest = findViewById(R.id.imagetest);


        //read image from file
//        if(bitmap == null) {
//            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sudokuexample).copy(Bitmap.Config.ARGB_8888, true);
//        }

        Button createTest = findViewById(R.id.createtest);

        Button capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);

                    if(imageUri != null){
                        processButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                imageView.setImageBitmap(bitmap);
//                textView.setText(getText(finalbmp));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        imageProcessing = new ImageProcessing(ScanTest.this);
                        //pass bitmap to class
                        imageProcessing.setBitmap(bitmap);

                        //returns a bitmap to display to imageview via button press
                        imageProcessing.img();

                        int limit = 0;
                        for(int i = 0; i < gridBitmaps.size(); i++){
                            if(limit < gridSize){
                                String res = getText((Bitmap) gridBitmaps.get(i));
                                scannedValues.add(res);
                                textView.append(res + " ");
                                limit++;
                            }

                        }
                        }
                });
                createTest.setVisibility(View.VISIBLE);

            }
        });

        //temp code

        createTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SudokuCreator.class);
                intent.putExtra("valuesScanned", scannedValues);
                intent.putExtra("type", 2);
                intent.putExtra("title", "scanned sudoku");
                intent.putExtra("dims", new int[]{9, 9});
                intent.putExtra("scanned", true);
                startActivity(intent);
                finish();
            }
        });

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
    }

    public void setBitmaps(Mat m){
        finalbmp = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(m, finalbmp);
    }

    //for testing, image extraction
    public void writeImageToStorage(Bitmap bitmap){
        File sd = new File(getExternalFilesDir("/tests").toString() + "/" + bitmap.hashCode() + " " + ".png");

        try {
            sd.createNewFile();
            FileOutputStream out = new FileOutputStream(sd);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMats(List<Mat> mats){
        //currently loops through hardcoded sudoku grid
        int n = 0;
        int s = 0;
        for(int i = 0; i < mats.size(); i++){
            try {
                gridBitmaps.add(Bitmap.createBitmap(mats.get(i).cols(), mats.get(i).rows(), Bitmap.Config.ARGB_8888));
                Utils.matToBitmap(mats.get(i), (Bitmap) gridBitmaps.get(i));
            } catch (CvException o) {
                o.printStackTrace();
            } catch (IndexOutOfBoundsException p) {
                p.printStackTrace();
            }
            mats.get(i).release();

            File sd = new File(getExternalFilesDir("/grids").toString() + "/" + n + " " + ".png");
            n++;
            s++;
            try {
                sd.createNewFile();
                FileOutputStream out = new FileOutputStream(sd);
                gridBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getText(Bitmap bitmap){
        prepareTessData();
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        String dataPath = getExternalFilesDir("/").getPath();
        File dir = new File(dataPath + "/tessData");
        if(!dir.exists()){
            dir.mkdir();
        }
        System.out.println(dir);
        tessBaseAPI.setDebug(false);
        tessBaseAPI.init(dataPath, "eng");
//        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "123456789 ");
//        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ";:'@#~[]{}-#-+=`!£$%^&*()/.,<>?|¬¦·•°‘„abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {


            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(bitmap);
                String imageUrl = getRealPathFromURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                prepareTessData();
//                startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //load files into sd card
    private void prepareTessData() {

        File file = new File(DATA_PATH + "/tessdata");
        if(!file.exists()){
            file.mkdir();
        }

        if (!new File(DATA_PATH + "/tessdata/" + "eng.traineddata").exists()) {
            try {
                AssetManager assetManager = mContext.getAssets();
                InputStream in = assetManager.open("tessData/" + "eng.traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH + "/tessData/eng.traineddata");

                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case MY_CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }

            case 120:

            case 121:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    public void setImageTest(Bitmap imageTest) {
        this.imageTest.setImageBitmap(imageTest);
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
