package com.praveenv.businesscardreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.IOException;

class ContactInfo {

    private Context context;
    private TextView readText;
    private ImageView businessCardView;
    private TextView nameText;
    private TextView phoneText;
    private TextView emailText;
    private TextView companyText;
    private TextView addressText;
    private TextView faxText;
    private OCR ocr;

    ContactInfo(
            Context context,
            TextView readText,
            ImageView businessCardView,
            TextView nameText,
            TextView phoneText,
            TextView emailText,
            TextView companyText,
            TextView addressText,
            TextView faxText
            ) {
        this.context = context;
        this.readText = readText;
        this.businessCardView = businessCardView;
        this.nameText = nameText;
        this.phoneText = phoneText;
        this.emailText = emailText;
        this.companyText = companyText;
        this.addressText = addressText;
        this.faxText = faxText;
        ocr = new OCR(context);
    }

    void setFields(Uri imageUri) {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String recognizedText = ocr.getText(bitmap);

        readText.setText(recognizedText);
        businessCardView.setImageURI(imageUri);
        nameText.setText(recognizedText);
        phoneText.setText(recognizedText);
        emailText.setText(recognizedText);
        companyText.setText(recognizedText);
        addressText.setText(recognizedText);
        faxText.setText(recognizedText);
    }

    private void clearFields() {
        readText.setText("Press the get button below to begin");
        DrawableCompat.setTint(businessCardView.getDrawable(), ContextCompat.getColor(context, R.color.colorPrimary));
        nameText.setText("");
        phoneText.setText("");
        emailText.setText("");
        companyText.setText("");
        addressText.setText("");
    }

    void addingContact() {
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
}
