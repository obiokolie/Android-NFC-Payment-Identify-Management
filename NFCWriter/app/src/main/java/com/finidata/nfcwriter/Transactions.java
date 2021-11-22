package com.finidata.nfcwriter;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.nfc.tech.MifareClassic;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class Transactions extends AppCompatActivity {

    private NFCManager nfcMger;
    private MifareClassic mMfc;

    private View v;
    boolean message = false;
    private ProgressDialog dialog;
    Tag currentTag;
    String transaction1 = "";
    String transaction2 = "";
    String transaction3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nfcMger = new NFCManager(this);

        final Spinner sp = (Spinner) findViewById(R.id.selectTransaction);
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.tagTransactions,android.R.layout.select_dialog_item);
        aa.setDropDownViewResource(android.R.layout.select_dialog_item);
        sp.setAdapter(aa);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = sp.getSelectedItemPosition();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        transaction1 = "Rad : "+currentDate;
                        message = true;
                        break;
                    case 2:
                        transaction2 = "FulF : "+currentDate;
                        message = true;
                        break;
                    case 3:
                        transaction2 = "Hxd : "+currentDate;
                        message = true;
                        break;
                    case 4:
                        transaction3 = "Txf : "+currentDate;
                        message = true;
                        break;
                    case 5:
                        transaction3 = "Axf : "+currentDate;
                        message = true;
                        break;
                    case 6:
                        transaction3 = "Ca f : "+currentDate;
                        message = true;
                        break;
                    case 7:
                        transaction3 = "FanB : "+currentDate;
                        message = true;
                        break;
                    case 8:
                        transaction3 = "Cle : "+currentDate;
                        message = true;
                        break;
                    case 9:
                        transaction3 = "Frnt : "+currentDate;
                        message = true;
                        break;
                    case 10:
                        transaction3 = "Relx : "+currentDate;
                        message = true;
                        break;
                    case 11:
                        transaction3 = "Trx : "+currentDate;
                        message = true;
                        break;
                    case 12:
                        transaction3 = "Gear : "+currentDate;
                        message = true;
                        break;
                    case 13:
                        transaction3 = "Ele : "+currentDate;
                        message = true;
                        break;
                    case 14:
                        transaction3 = "Bat : "+currentDate;
                        message = true;
                        break;
                    case 15:
                        transaction3 = "PTO : "+currentDate;
                        message = true;
                        break;
                    case 16:
                        transaction3 = "Oil : "+currentDate;
                        message = true;
                        break;


                }

                if (message) {

                    dialog = new ProgressDialog(Transactions.this);
                    dialog.setMessage("Tap Card please");
                    dialog.show();
                }

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Log.d("Nfc", "New intent for Transaction Recording -- Write");


        // It is the time to write the tag
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mMfc = MifareClassic.get(currentTag);

        if (transaction1.length()>0) {
            try {
                mMfc.connect();
                boolean authA = mMfc.authenticateSectorWithKeyA(3,
                        MifareClassic.KEY_DEFAULT);

                String transactionMessage = transaction1;
                byte[] value = hexStringToByteArray(stringToHex(transactionMessage));
                byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];

                for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
                    if (i < value.length) toWrite[i] = value[i];
                    else toWrite[i] = 0;
                }

                mMfc.writeBlock(12, toWrite);
                mMfc.close();

             } catch (IOException e) {
                Log.e("TAG", "IOException while closing MifareClassic...", e);
            } finally {
                try {
                    mMfc.close();
                } catch (IOException e) {
                    Log.e("TAG", "IOException while closing MifareClassic...", e);
                }
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(Transactions.this,"Transaction Successful!!!",Toast.LENGTH_LONG).show();


            //Save on the Cloud Operation
            String method = "newTx";
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            Random r = new Random();
            Integer i1 = r.nextInt(10000080 - 10000065) + 10000065;
            String txID = i1.toString();
            BackgroundTask backgroundTask = new BackgroundTask(this);

            //'$userID','$user_fname','$user_lname','$user_email','$user_occupation','$user_location','$user_gender','$user_phone','$user_picture'
            backgroundTask.execute(method,txID, currentDate, transaction1,"1", currentTag.getId().toString(),"34KD00000811", "N000001");


            //Snackbar.make(v, "Transaction Recorded!!!", Snackbar.LENGTH_LONG).show();

        }
        else if(transaction2.length()>0) {
            try {
                mMfc.connect();
                boolean authA = mMfc.authenticateSectorWithKeyA(3,
                        MifareClassic.KEY_DEFAULT);

                String transactionMessage = transaction2;
                byte[] value = hexStringToByteArray(stringToHex(transactionMessage));
                byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];

                for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
                    if (i < value.length) toWrite[i] = value[i];
                    else toWrite[i] = 0;
                }

                mMfc.writeBlock(13, toWrite);
                mMfc.close();

            } catch (IOException e) {
                Log.e("TAG", "IOException while closing MifareClassic...", e);
            } finally {
                try {
                    mMfc.close();
                } catch (IOException e) {
                    Log.e("TAG", "IOException while closing MifareClassic...", e);
                }
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(Transactions.this,"Transaction Successful!!!",Toast.LENGTH_LONG).show();
            //Snackbar.make(v, "Transaction Recorded!!!", Snackbar.LENGTH_LONG).show();

            //Save on the Cloud Operation
            String method = "newTx";
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String txID = "NIRSAL-"+ currentDate;
            BackgroundTask backgroundTask = new BackgroundTask(this);

            //'$userID','$user_fname','$user_lname','$user_email','$user_occupation','$user_location','$user_gender','$user_phone','$user_picture'
            backgroundTask.execute(method,txID, currentDate, transaction2,"1", currentTag.getId().toString(),"34KDKFKKSKK", "N000001");


        }
        else if(transaction3.length()>0) {
            try {
                mMfc.connect();
                boolean authA = mMfc.authenticateSectorWithKeyA(3,
                        MifareClassic.KEY_DEFAULT);

                String transactionMessage = transaction3;
                byte[] value = hexStringToByteArray(stringToHex(transactionMessage));
                byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];

                for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
                    if (i < value.length) toWrite[i] = value[i];
                    else toWrite[i] = 0;
                }

                mMfc.writeBlock(14, toWrite);
                mMfc.close();

            } catch (IOException e) {
                Log.e("TAG", "IOException while closing MifareClassic...", e);
            } finally {
                try {
                    mMfc.close();
                } catch (IOException e) {
                    Log.e("TAG", "IOException while closing MifareClassic...", e);
                }
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(Transactions.this,"Transaction Successful!!!",Toast.LENGTH_LONG).show();


            //Save on the Cloud Operation
            String method = "newTx";
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String txID = "NIRSAL-"+ currentDate;
            BackgroundTask backgroundTask = new BackgroundTask(this);

            //'$userID','$user_fname','$user_lname','$user_email','$user_occupation','$user_location','$user_gender','$user_phone','$user_picture'
            backgroundTask.execute(method,txID, currentDate, transaction3,"1", currentTag.getId().toString(),"34KDKFKKSKK", "N000001");

            //Snackbar.make(v, "Transaction Recorded!!!", Snackbar.LENGTH_LONG).show();

        }
        else
            {
                // Handle intent
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                new AlertDialog.Builder(this).setTitle("No Transaction").setMessage("Please choose a transaction1 to record").setNeutralButton("Ok", null).show();

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

}
