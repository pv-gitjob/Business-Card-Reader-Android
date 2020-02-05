package com.praveenv.businesscardreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    ContactInfo contactInfo;

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
                    contactInfo.setFields(imageUri);
                    break;

                case CAMERA_REQUEST:

                    break;
            }
    }


}
