package com.badr.hourimeche.hiddenfounders.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class FirebaseAdapter extends AsyncTask<String, Void, byte[]> {

    private Context mContext;
    private int photoNum;
    private ProgressDialog progressDialog;
    private String albumName, userName;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public FirebaseAdapter(Context mContext, String albumName, String userName, int photoNum) {
        this.mContext = mContext;
        this.albumName = albumName;
        this.userName = userName;
        this.photoNum = photoNum;
    }

    @Override
    public void onPreExecute() {
        progressDialog = new ProgressDialog(this.mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Chargement des Photos");
        progressDialog.show();
    }

    @Override
    public byte[] doInBackground(String... params) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            URL toDownload = new URL(params[0]);
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException ignored) {
        }
        return outputStream.toByteArray();
    }

    @Override
    public void onPostExecute(byte[] bytes) {
        StorageReference riversRef = mStorageRef.child(userName.concat(" - ").concat(albumName).concat(" - ").concat(String.valueOf(photoNum)).concat(".jpg"));
        Toasty.success(mContext, "Image N° " + String.valueOf(photoNum) + " de l'Album " + albumName + " est chargé sur Firebase.").show();
        UploadTask uploadTask = riversRef.putBytes(bytes);
        uploadTask.addOnSuccessListener((Activity) mContext, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
            }
        });

    }
}
