package com.badr.hourimeche.hiddenfounders.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badr.hourimeche.hiddenfounders.MainActivity;
import com.badr.hourimeche.hiddenfounders.PhotosActivity;
import com.badr.hourimeche.hiddenfounders.R;
import com.badr.hourimeche.hiddenfounders.models.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

    public static int nbr = 0;
    private Context mContext;
    private List<AlbumModel> albumModels;
    public static List<AlbumModel> selectedAlbum = new ArrayList<>();
    private static ArrayList<CompoundButton> checkArray = new ArrayList<>();

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
                ((Activity)mContext).overridePendingTransition(0, 0);
            }
        });

        //Checker les albums à exporter et les ajouter à la liste "selectedAlbum" et à la liste "checkArray"
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    nbr++;
                    setTxt();
                    selectedAlbum.add(albumModels.get(position));
                    checkArray.add(compoundButton);
                } else {
                    nbr--;
                    setTxt();
                    selectedAlbum.remove(albumModels.get(position));
                }
            }
        });
    }

    //Unchecker les albums déjà ajouté à la liste "checkArray"
    public static void deselectAll(){
        for(int a=0; a<checkArray.size(); a++)
        {
            checkArray.get(a).setChecked(false);
        }
    }

    //afficher un button pour exporter les albums selectionnées en cas de selection
    private void setTxt(){
        if (nbr == 0)
            MainActivity.selected.setVisibility(View.GONE);
        else {
            MainActivity.selected.setVisibility(View.VISIBLE);
            if (nbr == 1)
                MainActivity.selected.setText("Exporter l'album Sélectionné sur Firebase");
            else
                MainActivity.selected.setText("Exporter les " + nbr + " Albums Sélectionnés sur Firebase");
        }
    }

    @Override
    public int getItemCount() {
        return albumModels.size();
    }


    class AlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView albumName;
        private CardView cvRow;
        private CheckBox checkBox;

        AlbumViewHolder(View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.txt2);
            cvRow = itemView.findViewById(R.id.cardV);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
