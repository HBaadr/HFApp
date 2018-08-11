package com.badr.hourimeche.hiddenfounders.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.models.AlbumModel;
import com.badr.hourimeche.hiddenfounders.view.PhotosActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

    private Context mContext;
    private List<AlbumModel> albumModels;

    public AlbumsAdapter(Context mContext, List<AlbumModel> albumModels) {
        this.mContext = mContext;
        this.albumModels = albumModels;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.album, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, final int position) {
        final AlbumModel albumModel = albumModels.get(position);
        holder.albumName.setText(albumModel.getNameAlbum());
        holder.cvRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotosActivity.class);
                intent.putExtra("albumID", albumModel.getIdAlbum());
                intent.putExtra("albumName", albumModel.getNameAlbum());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumModels.size();
    }


    class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt2)
        TextView albumName;
        @BindView(R.id.cardV)
        CardView cvRow;

        AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
