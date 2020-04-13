package com.praveenv.businesscardreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        setAllThread thread = new setAllThread(imageUri);
        thread.execute();
    }

    private void clearFields() {
        readText.setText("Press the get button below to begin");
        //DrawableCompat.setTint(businessCardView.getDrawable(), ContextCompat.getColor(context, R.color.colorPrimary));
        nameText.setText("");
        phoneText.setText("");
        emailText.setText("");
        companyText.setText("");
        addressText.setText("");
        faxText.setText("");
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

    void setSpecificField(int field, Uri imageUri) {
        setFieldThread thread = new setFieldThread(field, imageUri);
        thread.execute();
    }

    private class setAllThread extends AsyncTask<Void, Void, String> {
        ArrayList<String> text;
        Uri imageUri;
        setAllThread(Uri imageUri) {
            this.imageUri = imageUri;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                return ocr.getText(bitmap);
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String recognizedText) {
            text = new ArrayList<String>(Arrays.asList(recognizedText.split(" ")));

            readText.setText(recognizedText);
            businessCardView.setImageURI(imageUri);
            nameText.setText(recognizedText);
            phoneText.setText(getPhone());
            emailText.setText(getEmail());
            companyText.setText(recognizedText);
            addressText.setText(recognizedText);
            faxText.setText(getPhone());
        }

        private String getEmail() {
            String email = "";
            for (int i=0; i<text.size(); i++){
                if (text.get(i).contains("@")) {
                    email = text.get(i);
                    text.remove(i);
                    break;
                }
            }
            return email;
        }

        private String getPhone() {
            String phone = "";
            for (int i=0; i<text.size(); i++){
                if (text.get(i).contains("+1") || text.get(i).contains("\\d\\.\\d") || text.get(i).contains("\\d\\s\\d")||text.get(i).contains("(")) {
                    phone = text.get(i);
                    text.remove(i);
                    break;
                }
            }
            return phone;
        }
    }

    private class setFieldThread extends AsyncTask<Void, Void, String> {
        int field;
        Uri imageUri;
        setFieldThread(int field, Uri imageUri) {
            this.field = field;
            this.imageUri = imageUri;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                return ocr.getText(bitmap);
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String recognizedText) {
            switch (field) {
                case 1:
                    nameText.setText(recognizedText);
                    break;
                case 2:
                    phoneText.setText(recognizedText);
                    break;
                case 3:
                    emailText.setText(recognizedText);
                    break;
                case 4:
                    companyText.setText(recognizedText);
                    break;
                case 5:
                    addressText.setText(recognizedText);
                    break;
                case 6:
                    faxText.setText(recognizedText);
                    break;
            }
        }
    }

}
