package com.badr.hourimeche.hiddenfounders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.adapters.AlbumsAdapter;
import com.badr.hourimeche.hiddenfounders.adapters.FirebaseAdapter;
import com.badr.hourimeche.hiddenfounders.models.AlbumModel;
import com.badr.hourimeche.hiddenfounders.models.PhotosModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    public static Button selected;
    private CallbackManager callbackManager;
    private RelativeLayout infoLayout;
    private ProfilePictureView fbPicture;
    private TextView fbEmail, fbName;
    private LoginButton btnLogin;
    private RecyclerView recyclerView;
    private List<AlbumModel> albumModels;
    private AlbumsAdapter albumsAdapter;
    private SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); //initialiser le SDK de Facebook
        setContentView(R.layout.activity_main);

        //récupérer les éléments de la vue
        fbEmail = (TextView) findViewById(R.id.email);
        fbName = (TextView) findViewById(R.id.name);
        fbPicture = (ProfilePictureView) findViewById(R.id.image);
        infoLayout = (RelativeLayout) findViewById(R.id.layout_info);

        //Exporter tout les images des albums séléctionnés à Firebase
        selected = (Button) findViewById(R.id.txt4);
        selected.setVisibility(View.GONE);
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (AlbumModel album : AlbumsAdapter.selectedAlbum) {
                    String nameAlb = album.getNameAlbum();
                    String idAlb = album.getIdAlbum();
                    uploadImages(idAlb, nameAlb);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerV);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        //Button de déconnexion
        Button button = (Button) findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                checkConnexion();
            }
        });

        //Button de connexion
        btnLogin = (LoginButton) findViewById(R.id.login_button);
        btnLogin.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_photos");
        btnLogin.setReadPermissions(permissions);
        callbackManager = CallbackManager.Factory.create(); //initialiser CallbackManager

        checkConnexion();

        //créer le callback
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //obtenir le nom, l'email, et la photo de profil et les stocker dans SharedPreferences
                                myEditor = getSharedPreferences("crd", MODE_PRIVATE).edit();
                                try {
                                    myEditor.putString("id", object.optString("id"));
                                    myEditor.putString("fbName", object.optString("name"));
                                    myEditor.putString("fbEmail", object.optString("email"));
                                } catch (Exception ignored) {}
                                myEditor.apply();
                                uploadInfos();
                                checkConnexion();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toasty.info(MainActivity.this, "Canceled").show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toasty.error(MainActivity.this, "Error : " + exception).show();
            }
        });
    }

    //Exporter tout les images d'un séléctionné à Firebase
    private void uploadImages(final String albumID, final String nameAlb) {
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
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");
                                        SharedPreferences prefs = getSharedPreferences("crd", MODE_PRIVATE);
                                        //Envoyer l'image à firebase avec le nom de l'utilisateur, le nom de son album, et son numéro
                                        new FirebaseAdapter(MainActivity.this, nameAlb, prefs.getString("fbName", ""), i+1)
                                                .execute(new PhotosModel(jaImages.getJSONObject(0).getString("source")).getUrlImage());
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }).executeAsync();
    }

    //Importer tout les albums d'un utilisateur et les afficher
    private void downloadAlbums() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getError() == null) {
                                JSONObject data = response.getJSONObject();
                                if (data.has("data")) {
                                    JSONArray jaData = data.optJSONArray("data");
                                    albumModels = new ArrayList<>();
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        albumModels.add(new AlbumModel(joAlbum.getString("id"), joAlbum.getString("name")));
                                    }
                                    albumsAdapter = new AlbumsAdapter(MainActivity.this, albumModels);
                                    recyclerView.setAdapter(albumsAdapter);
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }).executeAsync();
    }

    //Tester si l'utilisateur est connécter afin de gerer l'interface
    private void checkConnexion() {
        if (AccessToken.getCurrentAccessToken() != null) {
            uploadInfos();
            infoLayout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            downloadAlbums();
        } else {
            AlbumsAdapter.nbr = 0;
            selected.setVisibility(View.GONE);
            infoLayout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            myEditor = getSharedPreferences("crd", MODE_PRIVATE).edit();
            myEditor.putString("id", "");
            myEditor.putString("fbName", "");
            myEditor.putString("fbEmail", "");
            myEditor.apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //afficher les infos stockés sur SharedPreferences si l'utilisateur est connecté meme au cas ou l'application se redemare
    private void uploadInfos() {
        SharedPreferences prefs = getSharedPreferences("crd", MODE_PRIVATE);
        fbPicture.setPresetSize(ProfilePictureView.NORMAL);
        fbPicture.setProfileId(prefs.getString("id", ""));
        fbName.setText(prefs.getString("fbName", "."));
        fbEmail.setText(prefs.getString("fbEmail", "."));
    }

    @Override
    public void onBackPressed() {
        if (AlbumsAdapter.nbr == 0) {
            super.onBackPressed();
        } else {
            selected.setVisibility(View.GONE);
            AlbumsAdapter.deselectAll();
            AlbumsAdapter.nbr = 0;
        }
    }
}