package com.smxff.crypto_base1;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void StoreSecretData(View view) {
        //Intent intent = new Intent();
        EditText SecretText = (EditText) findViewById(R.id.secretText);
        TextView SimpleTesterTextView = (TextView) findViewById(R.id.resultTextView);

        String SecretData = SecretText.getText().toString();
        SharedPreferences.Editor SPEditor = createSharedPreferencesEditor();

        storeSecretInEncryptedFormatAndItskeyAtSharedPreferenes(SPEditor, SecretData);

        SPEditor.commit();

        SimpleTesterTextView.setText("Successful!");
    }

    public SharedPreferences.Editor createSharedPreferencesEditor() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        return editor;
    }


    private void storeSecretInEncryptedFormatAndItskeyAtSharedPreferenes(SharedPreferences.Editor SPEditor, String secretData) {
        try {
            byte[] plaintext = secretData.getBytes();
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            SecretKey key = keygen.generateKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV();
            byte[] keyByte = key.getEncoded();
            byte[] ciphertext = cipher.doFinal(plaintext);

            ArrayList keyMaterials = createBase64EncodedListofMaterials(iv, keyByte, ciphertext);
            SPEditor.putString("IV", keyMaterials.get(0).toString());
            SPEditor.putString("KEY", keyMaterials.get(1).toString());
            SPEditor.putString("Ciphertext", keyMaterials.get(2).toString());

        }
        catch (Exception ex) {
            Log.e("MY_APP_LOG_TAG", "Error:", ex);
        }

    }

    private ArrayList createBase64EncodedListofMaterials(byte[] iv, byte[] keyByte, byte[] ciphertext) {
        String base64EncodedIV =  Base64.encodeToString(iv, Base64.URL_SAFE);
        String base64EncodedKey =  Base64.encodeToString(keyByte, Base64.URL_SAFE);
        String base64EncodedCiphertext =  Base64.encodeToString(ciphertext, Base64.URL_SAFE);

        return new ArrayList<String>(Arrays.asList(base64EncodedIV, base64EncodedKey ,base64EncodedCiphertext));
    }

}