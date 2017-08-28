package com.olegsagenadatrytwo.w4_w_googleplacesapi.view.mapsactivity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.R;
import com.olegsagenadatrytwo.w4_w_googleplacesapi.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omcna on 8/27/2017.
 */

public class AdapterNearByPlaces extends RecyclerView.Adapter<AdapterNearByPlaces.ViewHolder> {

    private static final String KEY = "AIzaSyAEns11Xxw1w9YKlSv0RR08aON71tSEaPs";
    public static final String TAG = "FlickerAdapter";
    private List<Result> resultList = new ArrayList<>();
    private Context context;

    public AdapterNearByPlaces(List<Result> resultList, Context context) {
        this.resultList = resultList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_adapter_near_by_places, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvName.setText(resultList.get(position).getName());
        if(resultList.get(position).getPhotos()!=null&&resultList.get(position).getPhotos().size()>0) {
            String a = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"+"&photoreference=" + resultList.get(position).getPhotos().get(0).getPhotoReference() + "&key=" + KEY;
            Glide.with(context).load(a).into(holder.ivIcon);
        }
        else{
            Glide.with(context).load(resultList.get(position).getIcon()).into(holder.ivIcon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.layout_detail);

                ImageView imageView = (ImageView) dialog.findViewById(R.id.ivImage);
                TextView tvName = (TextView) dialog.findViewById(R.id.tvName);
                TextView tvRating = (TextView) dialog.findViewById(R.id.tvRatings);
                TextView tvOpen = (TextView) dialog.findViewById(R.id.tvOpen);

                tvName.setText(resultList.get(position).getName());
                tvRating.setText(resultList.get(position).getRating()+"");
                if(resultList.get(position).getOpeningHours() != null) {
                    tvOpen.setText(resultList.get(position).getOpeningHours().getOpenNow() + "");
                }else{
                    tvOpen.setText("N/A");

                }
                if(resultList.get(position).getPhotos()!= null) {
                    String a = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" + "&photoreference=" + resultList.get(position).getPhotos().get(0).getPhotoReference() + "&key=" + KEY;
                    Glide.with(context).load(a).into(imageView);
                }else{
                    Glide.with(context).load(resultList.get(position).getIcon()).into(imageView);
                }

                Button navigate = (Button) dialog.findViewById(R.id.btnNavigateTo);
                Button done = (Button) dialog.findViewById(R.id.btnDone);
                //if button is clicked, close the custom dialog
                navigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);

        }
    }
}

