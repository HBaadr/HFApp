package com.badr.hourimeche.hiddenfounders.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badr.hourimeche.hiddenfounders.adapters.AlbumsAdapter;
import com.badr.hourimeche.hiddenfounders.helper.PreferencesHelper;
import com.badr.hourimeche.hiddenfounders.models.AlbumModel;
import com.badr.hourimeche.hiddenfounders.view.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter {

    private Context context;
    private MainActivity view;
    private CallbackManager callbackManager;
    private PreferencesHelper preferencesHelper;
    private List<AlbumModel> albumModels;
    private AlbumsAdapter albumsAdapter;

    public MainPresenter(Context context) {
        this.context = context;
        this.view = (MainActivity) context;
        preferencesHelper = new PreferencesHelper(context);
        init();
    }

    private void init() {
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext()); //initialiser le SDK de Facebook
        callbackManager = CallbackManager.Factory.create(); //initialiser CallbackManager
    }

    //Tester si l'utilisateur est connécter afin de gerer l'interface
    public void checkConnexion() {
        if (AccessToken.getCurrentAccessToken() != null) {
            view.isConnected();
        } else {
            view.isNotConnected();
        }
    }

    public void logout(){
        LoginManager.getInstance().logOut();
    }

    public List<String> getPermissions(){
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_photos");
        return  permissions;
    }

    public void onLoginButtonClick(LoginButton btnLogin){
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Bundle bundle = new Bundle();
                bundle.putString("fields", "name,email");
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + AccessToken.getCurrentAccessToken().getUserId(),
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                JSONObject data = response.getJSONObject();
                                //obtenir le nom, l'email, et la photo de profil et les stocker dans SharedPreferences
                                try {
                                    preferencesHelper.setUserId(data.get("id").toString());
                                    preferencesHelper.setUserName(data.get("name").toString());
                                    preferencesHelper.setUserEmail(data.get("email").toString());
                                } catch (Exception ex) {
                                    view.errorToast(ex.getMessage());
                                }
                                view.isConnected();
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                view.infoToast("Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                view.errorToast("Error : " + exception);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    //Importer tout les albums d'un utilisateur et les afficher
    public void downloadAlbums() {
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject data = response.getJSONObject();
                            JSONArray jaData = data.optJSONArray("data");
                            albumModels = new ArrayList<>();
                            for (int i = 0; i < jaData.length(); i++) {
                                JSONObject joAlbum = jaData.getJSONObject(i);
                                albumModels.add(new AlbumModel(
                                        joAlbum.getString("id"),
                                        joAlbum.getString("name")));
                            }
                            albumsAdapter = new AlbumsAdapter(context, albumModels);
                            view.setAdapter(albumsAdapter);
                        } catch (Exception ex) {
                            view.errorToast(ex.getMessage());
                        }
                    }
                }).executeAsync();
    }
}
