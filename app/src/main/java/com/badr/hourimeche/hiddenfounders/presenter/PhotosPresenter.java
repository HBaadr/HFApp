package com.badr.hourimeche.hiddenfounders.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badr.hourimeche.hiddenfounders.adapters.PhotosAdapter;
import com.badr.hourimeche.hiddenfounders.helper.PreferencesHelper;
import com.badr.hourimeche.hiddenfounders.models.PhotosModel;
import com.badr.hourimeche.hiddenfounders.view.MainActivity;
import com.badr.hourimeche.hiddenfounders.view.PhotosActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotosPresenter {

    private Context context;
    private PhotosActivity view;
    private PreferencesHelper preferencesHelper;

    private String albumID;
    private String albumName;
    private List<PhotosModel> photosModels;

    public PhotosPresenter(Context context) {
        this.context = context;
        this.view = (PhotosActivity) context;
        preferencesHelper = new PreferencesHelper(context);
        init();
    }

    private void init() {
        albumID = view.getIntent().getStringExtra("albumID");
        albumName = view.getIntent().getStringExtra("albumName");
        view.setTextView(albumName);
    }

    public String getUserId() {
        return preferencesHelper.getUserId();
    }

    public String getUserName() {
        return preferencesHelper.getUserName();
    }

    public String getUserEmail() {
        return preferencesHelper.getUserEmail();
    }

    public void reset() {
        preferencesHelper.reset();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        context.startActivity(new Intent(context, MainActivity.class));
        view.overridePendingTransition(0, 0);
        preferencesHelper.reset();
    }


    //Importer tout les images d'un Album sélectionné et les afficher
    public void downloadImages() {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "images");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumID + "/photos",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject data = response.getJSONObject();
                            JSONArray jaData = data.optJSONArray("data");
                            photosModels = new ArrayList<>();
                            for (int i = 0; i < jaData.length(); i++) {
                                JSONArray jaImages = jaData.getJSONObject(i).getJSONArray("images");
                                photosModels.add(new PhotosModel(
                                        jaImages.getJSONObject(0).getString("source")));
                            }
                            PhotosAdapter photosAdapter = new PhotosAdapter(context, photosModels);
                            view.setAdapter(photosAdapter);
                        } catch (Exception ignored) {
                        }
                    }
                }).executeAsync();
    }
}
