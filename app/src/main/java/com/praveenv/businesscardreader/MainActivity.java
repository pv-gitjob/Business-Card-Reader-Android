package com.praveenv.businesscardreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    TextView readText;
    ImageView businessCardView;
    TextView nameText;
    TextView phoneText;
    TextView emailText;
    TextView companyText;
    TextView addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readText = findViewById(R.id.readText);
        businessCardView = findViewById(R.id.businessCard);
        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        companyText = findViewById(R.id.companyText);
        addressText = findViewById(R.id.addressText);

        Button getBusinessCardBtn = findViewById(R.id.getBusinessCard);
        getBusinessCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        Button addContactBtn = findViewById(R.id.addToContacts);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingContact();
            }
        });
    }

    public void addingContact() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent
                .putExtra(ContactsContract.Intents.Insert.NAME, nameText.getText())
                .putExtra(ContactsContract.Intents.Insert.PHONE, phoneText.getText())
                .putExtra(ContactsContract.Intents.Insert.PHONE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, emailText.getText())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.POSTAL, addressText.getText())
                .putExtra(ContactsContract.Intents.Insert.POSTAL, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
        ;

        clearFields();
    }

    public void clearFields() {
        readText.setText("Press the get button below to begin");
        DrawableCompat.setTint(businessCardView.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        nameText.setText("");
        phoneText.setText("");
        emailText.setText("");
        companyText.setText("");
        addressText.setText("");
    }

    public void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri imageUri = data.getData();

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setUpTesseractData();
                    String recognizedText = getText(bitmap);

                    //Will get back to this
                    String[] details = recognizedText.split(" ");


                    nameText.setText(recognizedText);
                    phoneText.setText(recognizedText);
                    emailText.setText(recognizedText);
                    companyText.setText(recognizedText);
                    addressText.setText(recognizedText);

                    readText.setText(recognizedText);
                    businessCardView.setImageURI(imageUri);
                    break;
            }
    }

    public void setUpTesseractData(){
        try{
            File dir = getExternalFilesDir("/tessdata");
            if(!dir.exists()){
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = dir + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.class.getSimpleName(), e.getMessage());
        }
    }

    public String getText(Bitmap bitmap){
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(getExternalFilesDir("/").getPath() + "/", "eng");
        tessBaseAPI.setImage(toGrayscale(bitmap));
        String retStr = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        return retStr;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

}
