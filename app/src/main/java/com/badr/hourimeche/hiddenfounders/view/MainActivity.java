package com.badr.hourimeche.hiddenfounders.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.adapters.AlbumsAdapter;
import com.badr.hourimeche.hiddenfounders.presenter.MainPresenter;
import com.facebook.login.LoginBehavior;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ProfilePictureView fbPicture;
    @BindView(R.id.email)
    TextView fbEmail;
    @BindView(R.id.name)
    TextView fbName;
    @BindView(R.id.login_button)
    LoginButton btnLogin;
    @BindView(R.id.recyclerV)
    RecyclerView recyclerView;
    @BindView(R.id.layout_info)
    RelativeLayout infoLayout;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //récupérer les éléments de la vue
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        presenter = new MainPresenter(this);

        //Button de connexion
        btnLogin.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        btnLogin.setReadPermissions(presenter.getPermissions());
        presenter.checkConnexion();

        //créer le callback
        presenter.onLoginButtonClick(btnLogin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    //afficher les infos stockés sur SharedPreferences si l'utilisateur est connecté meme au cas ou l'application se redemare
    public void uploadInfos() {
        fbPicture.setPresetSize(ProfilePictureView.NORMAL);
        fbPicture.setProfileId(presenter.getUserId());
        fbName.setText(presenter.getUserName());
        fbEmail.setText(presenter.getUserEmail());
    }


    public void isConnected() {
        uploadInfos();
        infoLayout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        presenter.downloadAlbums();
    }

    public void isNotConnected() {
        infoLayout.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        presenter.reset();
    }

    @OnClick(R.id.logout)
    public void logout() {
        presenter.logout();
        presenter.checkConnexion();
    }

    public void infoToast(String msg){
        Toasty.info(MainActivity.this, msg).show();
    }

    public void errorToast(String msg){
        Toasty.error(MainActivity.this, msg).show();
    }

    public void setAdapter(AlbumsAdapter albumsAdapter) {
        recyclerView.setAdapter(albumsAdapter);
    }
}