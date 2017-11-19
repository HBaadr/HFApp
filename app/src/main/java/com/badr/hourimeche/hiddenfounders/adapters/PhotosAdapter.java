package com.badr.hourimeche.hiddenfounders.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badr.hourimeche.hiddenfounders.FullscreenActivity;
import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.models.PhotosModel;
import com.bumptech.glide.Glide;

import java.util.List;

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
        Glide.with(mContext).load(albumModel.getUrlImage()).placeholder(R.drawable.wait).into(holder.iv_Photo);
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

        CardView cvRow;
        ImageView iv_Photo;

        PhotosViewHolder(View itemView) {
            super(itemView);
            iv_Photo = itemView.findViewById(R.id.img2);
            cvRow = itemView.findViewById(R.id.cardV);
        }
    }
}
