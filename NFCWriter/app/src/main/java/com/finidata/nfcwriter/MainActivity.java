package com.finidata.nfcwriter;

/*
 * Copyright (C) 2016, Finidata & Agency Services
 *
 *(http://www.finidata.com.ng)
 *
 * 03/01/16
 */


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.nfc.tech.MifareClassic;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;



public class MainActivity extends AppCompatActivity {

    private NFCManager nfcMger;
    private MifareClassic mMfc;

    UserDBHelper userDBHelper;
    Context context = this;
    SQLiteDatabase sqLiteDatabase;

    private View v;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    Tag currentTag;

    private Uri imageCaptureUri;
    private ImageView mImageView;
    Button btn_choose_image;

    private EditText et;
    private EditText lastname;
    private EditText phone;
    private EditText occupation;
    private EditText dateOfBirth;
    private EditText gender;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nfcMger = new NFCManager(this);

        v = findViewById(R.id.mainLyt);

        final Spinner sp = (Spinner) findViewById(R.id.tagType);
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.tagContentType, android.R.layout.simple_spinner_dropdown_item);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);

        et = (EditText) findViewById(R.id.editFirstName);
        lastname = (EditText) findViewById(R.id.editLastName);
        phone = (EditText) findViewById(R.id.editPhone);
        occupation = (EditText) findViewById(R.id.editOccupation);
        dateOfBirth = (EditText) findViewById(R.id.editDoB);
        gender = (EditText) findViewById(R.id.editGender);

        //Added Codes to Take Picture or Select Image from the External SD Card
        final String[] items = new String[] {"From Cam", "From SD Card"};
        ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(aAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar" + String.valueOf(System.currentTimeMillis()) + "jpg");
                    imageCaptureUri = Uri.fromFile(file);
                    try{
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                        intent.putExtra("return data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    dialogInterface.cancel();
                } else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "Complete Action Using"), PICK_FROM_FILE);
                }
            }
        });
        final AlertDialog dialog1 = builder.create();
        mImageView = (ImageView) findViewById(R.id.imageView);
        btn_choose_image = (Button) findViewById(R.id.btnChooseTakePic);
        btn_choose_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog1.show();
            }
        });


        //Old Code
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = sp.getSelectedItemPosition();

                //Added Codes
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(et.getText().toString() + ";");
                stringBuffer.append(lastname.getText().toString() + ";");
                stringBuffer.append(phone.getText().toString() + ";");
                stringBuffer.append(occupation.getText().toString() + ";");
                stringBuffer.append(dateOfBirth.getText().toString() + ";");
                stringBuffer.append(gender.getText().toString() + ";");

                String content = stringBuffer.toString();



                //Old Code
                // String content = et.getText().toString();

                switch (pos) {
                    case 0:
                        message =  nfcMger.createUriMessage(content, "http://");
                        break;
                    case 1:
                        message =  nfcMger.createUriMessage(content, "tel:");
                        break;
                    case 2:
                        message =  nfcMger.createTextMessage(content);
                        break;
                }

                if (message != null) {

                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setMessage("Tap Card please");
                    dialog.show();
                }
            }
        });

    }

    //Function used by the Take Picture Codes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        Bitmap bitmap = null;
        String path = "";

        if(resultCode == PICK_FROM_FILE){
            imageCaptureUri = data.getData();
            path = getRealPathFromURI(imageCaptureUri);
            if (path==null)
                path = imageCaptureUri.getPath();
            if (path!=null)
                bitmap = BitmapFactory.decodeFile(path);
        }else {
            path = imageCaptureUri.getPath();
            bitmap = BitmapFactory.decodeFile(path);
        }
        mImageView.setImageBitmap(bitmap);

    }

    public String getRealPathFromURI(Uri contentURI){
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentURI, proj, null, null, null);
        if (cursor== null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            nfcMger.verifyNFC();
            //nfcMger.enableDispatch();

            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[] {};
            String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };
            NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);
            nfcAdpt.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
        catch(NFCManager.NFCNotSupported nfcnsup) {
            Snackbar.make(v, "NFC not supported", Snackbar.LENGTH_LONG).show();
        }
        catch(NFCManager.NFCNotEnabled nfcnEn) {
            Snackbar.make(v, "NFC Not enabled", Snackbar.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        nfcMger.disableDispatch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Nfc", "New intent for MainActivity -- Write");


        // It is the time to write the tag
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mMfc = MifareClassic.get(currentTag);

        if (et.getText().length()>0 || phone.getText().length()>0) {

            //nfcMger.writeTag(currentTag, message);

            try {
                mMfc.connect();
                boolean authA = mMfc.authenticateSectorWithKeyA(1,
                        MifareClassic.KEY_DEFAULT);

                //EditText ett = (EditText) findViewById(R.id.editFirstName);
                //String text = "Obinna Okolie";
                //byte[] value  = text.getBytes();
                String combine = et.getText().toString()+" "+lastname.getText().toString();
                byte[] value = hexStringToByteArray(stringToHex(combine));

                //byte[] value = hexStringToByteArray(stringToHex());
                byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];


                for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
                    if (i < value.length) toWrite[i] = value[i];
                    else toWrite[i] = 0;
                }

                mMfc.writeBlock(4, toWrite);



                //mMfc.writeBlock(5, new byte[] {(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7});
                mMfc.close();
                //mMfc.writeBlock(4, "abcd".getBytes(Charset.forName("UTF-8")));
                //mMfc.writeBlock(5, "efgh".getBytes(Charset.forName("US-ASCII")));
                //mMfc.writeBlock(6, "ijkl".getBytes(Charset.forName("US-ASCII")));
                //mMfc.writeBlock(7, "mnop".getBytes(Charset.forName("US-ASCII")));


                mMfc.connect();
                boolean authA2 = mMfc.authenticateSectorWithKeyA(1,
                        MifareClassic.KEY_DEFAULT);

                //EditText ett = (EditText) findViewById(R.id.editFirstName);
                //String text = "Obinna Okolie";
                //byte[] value  = text.getBytes();
                String combine2 = gender.getText().toString()+" "+occupation.getText().toString();
                byte[] value2 = hexStringToByteArray(stringToHex(combine2));

                //byte[] value = hexStringToByteArray(stringToHex());
                byte[] toWrite2 = new byte[MifareClassic.BLOCK_SIZE];


                for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
                    if (i < value2.length) toWrite2[i] = value2[i];
                    else toWrite2[i] = 0;
                }

                mMfc.writeBlock(5, toWrite2);



                //mMfc.writeBlock(5, new byte[] {(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7,(byte)0xD3,(byte)0xF7});
                mMfc.close();

            } catch (IOException e) {
                Log.e("TAG", "IOException while closing MifareUltralight...", e);
            } finally {
                try {
                    mMfc.close();
                } catch (IOException e) {
                    Log.e("TAG", "IOException while closing MifareUltralight...", e);
                }
            }



            //Local SQLite Database Operations
            userDBHelper = new UserDBHelper(context);
            sqLiteDatabase = userDBHelper.getWritableDatabase();
            userDBHelper.addInformation(et.getText().toString(), lastname.getText().toString(), "test@nisal.com", occupation.getText().toString(), "Adamawa", gender.getText().toString(), phone.getText().toString(), "IMAGE",sqLiteDatabase);

            Snackbar.make(v, "Data saved to Database", Snackbar.LENGTH_LONG).show();
            userDBHelper.close();


            new AlertDialog.Builder(this).setTitle("Temporary ID").setMessage("You temporary ID is:" + " ASHIS-" + phone.getText().toString()).setNeutralButton("Close", null).show();

            dialog.dismiss();
            //Snackbar.make(v, "Tag written", Snackbar.LENGTH_LONG).show();


            //Save on the Cloud Operation
            String method = "newUser";
            BackgroundTask backgroundTask = new BackgroundTask(this);
            String userID = "NIRSAL-"+ phone.getText().toString();

            //'$userID','$user_fname','$user_lname','$user_email','$user_occupation','$user_location','$user_gender','$user_phone','$user_picture'
            backgroundTask.execute(method,userID, et.getText().toString(),lastname.getText().toString(), "test@nisal.com", occupation.getText().toString(), "Adamawa", gender.getText().toString(), phone.getText().toString(), "IMAGE");


        }
        else {
            // Handle intent
            dialog.dismiss();
            new AlertDialog.Builder(this).setTitle("Incomplete Record").setMessage("Please fill all the required fields").setNeutralButton("Ok", null).show();

        }

    }

    public static String stringToHex (String str){
        return String.format("%x", new BigInteger(1, str.getBytes(Charset.forName("UTF-8"))));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public void onClickReadMenu(MenuItem item) {
        Intent readCardActivity = new Intent(this, Read2.class);
        startActivity(readCardActivity);
    }

    public void onClickTransactionMenu(MenuItem item) {
        Intent writeTransactionActivity = new Intent(this, Transactions.class);
        startActivity(writeTransactionActivity);
    }




}
