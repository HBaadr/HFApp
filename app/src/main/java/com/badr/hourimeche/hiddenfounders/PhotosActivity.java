package com.badr.hourimeche.hiddenfounders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.adapters.AlbumsAdapter;
import com.badr.hourimeche.hiddenfounders.adapters.PhotosAdapter;
import com.badr.hourimeche.hiddenfounders.models.PhotosModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PhotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PhotosModel> photosModels;
    private String albumID;
    private ProfilePictureView fbPicture;
    private TextView fbEmail;
    private TextView fbName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        fbEmail = (TextView) findViewById(R.id.email);
        fbName = (TextView) findViewById(R.id.name);
        fbPicture = (ProfilePictureView) findViewById(R.id.image);
        uploadInfos();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerV2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);

        albumID = getIntent().getStringExtra("albumID");
        String albumName = getIntent().getStringExtra("albumName");
        TextView textView = (TextView) findViewById(R.id.txt3);
        textView.setText(albumName);
        downloadImages();

        //Button de déconnexion
        Button button = (Button) findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                AlbumsAdapter.nbr = 0;
                Intent intent = new Intent(PhotosActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                SharedPreferences.Editor myEditor = getSharedPreferences("crd", MODE_PRIVATE).edit();
                myEditor.putString("id", "");
                myEditor.putString("fbName", "");
                myEditor.putString("fbEmail", "");
                myEditor.apply();
            }
        });
    }

    //afficher les infos stockés sur SharedPreferences si l'utilisateur passe d'une autre Activity
    private void uploadInfos() {
        SharedPreferences prefs = getSharedPreferences("crd", MODE_PRIVATE);
        fbEmail.setText(prefs.getString("fbEmail", ""));
        fbName.setText(prefs.getString("fbName", ""));
        fbPicture.setPresetSize(ProfilePictureView.NORMAL);
        fbPicture.setProfileId(prefs.getString("id", ""));
    }

    //Importer tout les images d'un Album sélectionné et les afficher
    private void downloadImages() {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "images");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + albumID + "/photos",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getError() == null) {
                                JSONObject data = response.getJSONObject();
                                if (data.has("data")) {
                                    JSONArray jaData = data.optJSONArray("data");
                                    photosModels = new ArrayList<>();
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");
                                        photosModels.add(new PhotosModel(jaImages.getJSONObject(0).getString("source")));
                                    }
                                    PhotosAdapter photosAdapter = new PhotosAdapter(PhotosActivity.this, photosModels);
                                    recyclerView.setAdapter(photosAdapter);
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }).executeAsync();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
