package com.badr.hourimeche.hiddenfounders.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.models.PhotosModel;
import com.badr.hourimeche.hiddenfounders.view.FullscreenActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder> {


    private Context mContext;
    private List<PhotosModel> imageAlbumModels;

    public PhotosAdapter(Context mContext, List<PhotosModel> imageAlbumModels) {
        this.mContext = mContext;
        this.imageAlbumModels = imageAlbumModels;
    }

    @Override
    public PhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.photo, parent, false);
        return new PhotosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PhotosViewHolder holder, int position) {
        final PhotosModel albumModel = imageAlbumModels.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.wait);
        requestOptions.error(R.drawable.error);

        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(albumModel.getUrlImage())
                .into(holder.ivPhoto);
        holder.cvRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullscreenActivity.class);
                intent.putExtra("imageUrl", albumModel.getUrlImage());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageAlbumModels.size();
    }

    class PhotosViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardV)
        CardView cvRow;
        @BindView(R.id.img2)
        ImageView ivPhoto;

        PhotosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
