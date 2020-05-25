package com.badr.hourimeche.hiddenfounders.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.adapters.PhotosAdapter;
import com.badr.hourimeche.hiddenfounders.presenter.PhotosPresenter;
import com.facebook.login.widget.ProfilePictureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PhotosActivity extends AppCompatActivity {

    @BindView(R.id.txt3)
    TextView textView;
    @BindView(R.id.recyclerV2)
    RecyclerView recyclerView;
    @BindView(R.id.image)
    ProfilePictureView fbPicture;
    @BindView(R.id.email)
    TextView fbEmail;
    @BindView(R.id.name)
    TextView fbName;

    private PhotosPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        ButterKnife.bind(this);
        presenter = new PhotosPresenter(this);

        uploadInfos();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);

        presenter.downloadImages();

    }

    @OnClick(R.id.logout)
    public void logout(){
        presenter.logout();
    }

    public void setTextView(String albumName){
        textView.setText(albumName);
    }

    //afficher les infos stockés sur SharedPreferences si l'utilisateur passe d'une autre Activity
    public void uploadInfos() {
        fbPicture.setPresetSize(ProfilePictureView.NORMAL);
        fbPicture.setProfileId(presenter.getUserId());
        fbName.setText(presenter.getUserName());
        fbEmail.setText(presenter.getUserEmail());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    public void setAdapter(PhotosAdapter photosAdapter) {
        recyclerView.setAdapter(photosAdapter);
    }
}
