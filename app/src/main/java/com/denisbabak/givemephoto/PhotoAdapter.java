package com.denisbabak.givemephoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denisbabak.givemephoto.net.model.PhotoModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denisbabak on 1/11/16.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<VKApiPhoto> models = new ArrayList<>();
    Context ctx;
    private WallpaperActionInterface wallpaperActionInterface;

    public PhotoAdapter(Context context, WallpaperActionInterface wallpaperInterface) {
        this.ctx = context;
        this.wallpaperActionInterface = wallpaperInterface;
    }

    public void setData(List<VKApiPhoto> data) {
        models.clear();
        models.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_photo, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("GiveMeWallpapers", "onBindViewHolder() " + position + " : " + models.get(position).photo_807);
        ControllerListener listener = new BaseControllerListener();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(models.get(position).photo_604))
                .setTapToRetryEnabled(true)
                .setOldController(holder.photo.getController())
                .setControllerListener(listener)
                .build();

        holder.photo.setController(controller);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wallpaperActionInterface.setWallpaper(models.get(position).photo_2560);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models != null ? models.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (SimpleDraweeView) itemView.findViewById(R.id.my_image_view);
        }
    }

}
