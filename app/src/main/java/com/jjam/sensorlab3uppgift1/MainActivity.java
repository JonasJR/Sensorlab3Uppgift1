package com.jjam.sensorlab3uppgift1;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    NfcManager mNfcManager;
    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;

    TextView idTV;
    TextView techTV;

    String id;
    String[] technologies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        idTV = (TextView)findViewById(R.id.idTV);
        techTV = (TextView) findViewById(R.id.supportedTechnologiesTV);

        mNfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();

        if(mNfcAdapter == null){
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        resolveIntent(intent);
        super.onNewIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)
                || action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)
                || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            clearValues();
                // Hämta NFC taggen
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                // Hämta dess id (UID, hexadecimal)
            byte[] id = tag.getId();
            if (id != null) {
                String tag_id = bintohex(id);
                idTV.setText("ID: " + tag_id);
            } else {
                idTV.setText("No ID");
            }
                // Hämta all teknologi som stöds i taggen
            technologies = tag.getTechList();
            for (String tech : technologies){
                techTV.append(tech + "\n");
            }
        }
    }

    private String bintohex(byte[] id) {
            StringBuilder stringBuilder = new StringBuilder("");
            if (id == null || id.length <= 0) {
                return null;
            }

            char[] buffer = new char[2];
            for (int i = 0; i < id.length; i++) {
                buffer[0] = Character.forDigit((id[i] >>> 4) & 0x0F, 16);
                buffer[1] = Character.forDigit(id[i] & 0x0F, 16);
                System.out.println(buffer);
                stringBuilder.append(buffer);
            }

            return stringBuilder.toString();
    }

    private void clearValues() {
        id = "";
        idTV.setText("");
        technologies = null;
        techTV.setText("");
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
}
