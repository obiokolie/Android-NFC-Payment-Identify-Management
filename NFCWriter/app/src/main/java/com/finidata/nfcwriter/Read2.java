package com.finidata.nfcwriter;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.TagLostException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import android.nfc.tech.MifareClassic;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;


public class Read2 extends AppCompatActivity {

    private NFCManager nfcMger;
    private MifareClassic mMfc;
    private MifareClassic mMfc2;
    private ProgressDialog dialog;

    private TextView txtView;
    private TextView txtView2;
    private TextView txtView3;
    private TextView txtView4;
    private TextView txtView5;
    private TextView txtView7;

    String stringValue;
    String stringValue2;
    String stringValue3;
    String stringValue4;

    Tag currentTag;

    String displayedNameFromTag = "";
    String displayedGenderAndOccupationFromTag = "";
    String displayedTransaction1 = "";
    String displayedTransaction2 = "";

    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read2);
        txtView = (TextView) findViewById(R.id.textViewRead2);
        txtView2 = (TextView) findViewById(R.id.textView2);
        txtView3 = (TextView) findViewById(R.id.textView3);
        txtView4 = (TextView) findViewById(R.id.textView4);
        txtView7 = (TextView) findViewById(R.id.textView7);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        dialog = new ProgressDialog(Read2.this);
        dialog.setMessage("Tap your MECA Card please");
        setSupportActionBar(toolbar);

        nfcMger = new NFCManager(this);

        v = findViewById(R.id.read2layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                dialog.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

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
        Log.d("Nfc", "New intent for Read");
        //Intent intents = getIntent();
       String action = intent.getAction();

        // It is the time to write the tag



        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
        //if (true) {
            currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mMfc = MifareClassic.get(currentTag);


            //nfcMger.writeTag(currentTag, message);

            try {
                mMfc.connect();
                boolean authA = mMfc.authenticateSectorWithKeyA(1,
                        MifareClassic.KEY_DEFAULT);
                boolean authB = mMfc.authenticateSectorWithKeyB(1,
                        MifareClassic.KEY_DEFAULT);

                //Toast.makeText(this,"Ok",Toast.LENGTH_LONG).show();

                byte blockBytes1[] = mMfc.readBlock(4);
                byte blockBytes2[] = mMfc.readBlock(5);

                boolean authB1 = mMfc.authenticateSectorWithKeyB(3,
                        MifareClassic.KEY_DEFAULT);
                boolean authA1 = mMfc.authenticateSectorWithKeyA(3,
                        MifareClassic.KEY_DEFAULT);
                byte blockBytes3[] = mMfc.readBlock(12);
                byte blockBytes4[] = mMfc.readBlock(13);


                //
                displayedNameFromTag = byte2HexString(blockBytes1).toString();
                displayedGenderAndOccupationFromTag = byte2HexString(blockBytes2).toString();
                displayedTransaction1 = byte2HexString(blockBytes3).toString();
                displayedTransaction2 = byte2HexString(blockBytes4).toString();
                stringValue = new String(new BigInteger(displayedNameFromTag, 16).toByteArray());
                stringValue2 = new String(new BigInteger(displayedGenderAndOccupationFromTag, 16).toByteArray());
                stringValue3 = new String(new BigInteger(displayedTransaction1, 16).toByteArray());
                stringValue4 = new String(new BigInteger(displayedTransaction2, 16).toByteArray());
                Toast.makeText(Read2.this,stringValue,Toast.LENGTH_LONG).show();


                if (blockBytes1.length < 16) {
                    throw new IOException();
                }
                if (blockBytes1.length > 16) {
                    byte[] blockBytesTmp = Arrays.copyOf(blockBytes1,16);
                    blockBytes1 = blockBytesTmp;

                    //TextView txtView = (TextView) findViewById(R.id.tvReadTag);
                    //txtView.setText(byte2HexString(blockBytes).toString());



                    mMfc.close();

                }

            } catch (TagLostException e) {
                Log.e("TAG", "Tag Removed while reading closing Card...", e);
                Snackbar.make(v, "Tag Removed while reading closing Card", Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                // Could not read block.
                // (Maybe due to key/authentication method.)
                Log.d("LOG_TAG", "(Recoverable) Error while reading block "
                        + 5 + " from tag.");

                if (!mMfc.isConnected()) {
                    Snackbar.make(v, "There was an error while reading Card", Snackbar.LENGTH_LONG).show();
                }
                // After an error, a re-authentication is needed.
                //authenticate(sectorIndex, key, useAsKeyB);

            }


        }
        else {
            // Handle intent
            Snackbar.make(v, "Nothing really happened", Snackbar.LENGTH_LONG).show();

       }

        changeTextView();

        dialog.dismiss();

    }

    private void changeTextView() {
        //setContentView(R.layout.activity_read2);
        txtView3.setText(stringValue);
        txtView4.setText(stringValue2);
        txtView7.setText(stringValue3+"\n"+stringValue4);
    }

    public static String byte2HexString(byte[] bytes) {
        String ret = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                ret += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        return ret;
    }

}

