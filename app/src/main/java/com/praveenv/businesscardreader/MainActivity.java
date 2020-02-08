package com.praveenv.businesscardreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;
    private static final int FIELD_BUTTON_REQUEST = 2002;
    public Uri imageUri;
    protected int clickedField;
    private ContactInfo contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView readText = findViewById(R.id.readText);
        ImageView businessCardView = findViewById(R.id.businessCard);
        TextView nameText = findViewById(R.id.nameText);
        TextView phoneText = findViewById(R.id.phoneText);
        TextView emailText = findViewById(R.id.emailText);
        TextView companyText = findViewById(R.id.companyText);
        TextView addressText = findViewById(R.id.addressText);
        TextView faxText = findViewById(R.id.faxText);

        contactInfo = new ContactInfo(
                getApplicationContext(),
                readText,
                businessCardView,
                nameText,
                phoneText,
                emailText,
                companyText,
                addressText,
                faxText
                );

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
                contactInfo.addingContact();
            }
        });

        Button nameBtn = findViewById(R.id.nameBtn);
        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 1;
                    cropSelected();
                }
            }
        });

        Button phoneBtn = findViewById(R.id.phoneBtn);
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 2;
                    cropSelected();
                }
            }
        });

        Button emailBtn = findViewById(R.id.emailBtn);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 3;
                    cropSelected();
                }
            }
        });

        Button companyBtn = findViewById(R.id.companyBtn);
        companyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 4;
                    cropSelected();
                }
            }
        });

        Button addressBtn = findViewById(R.id.addressBtn);
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 5;
                    cropSelected();
                }
            }
        });

        Button faxBtn = findViewById(R.id.faxBtn);
        faxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    clickedField = 6;
                    cropSelected();
                }
            }
        });

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

    public void cropSelected() {
        Intent intent = getIntent();
        startActivityForResult(intent, FIELD_BUTTON_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GALLERY_REQUEST:
                imageUri = data.getData();
                contactInfo.setFields(imageUri);
                break;

            case CAMERA_REQUEST:

                break;

            case FIELD_BUTTON_REQUEST:
                CropImage.activity(imageUri).start(this);
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                contactInfo.setSpecificField(clickedField, resultUri);
                break;
        }
    }



}
