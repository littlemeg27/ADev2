package com.example.downloadworker;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ImageAdapter

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>
{
    private final List<Uri> imageUris;
    private final OnItemClickListener onClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(Uri uri);
    }

    public ImageAdapter(List<Uri> imageUris, OnItemClickListener clickListener)
    {
        this.imageUris = imageUris;
        this.onClickListener = clickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        Uri uri = imageUris.get(position);
        holder.imageView.setImageURI(uri);
        holder.itemView.setOnClickListener(v -> onClickListener.onItemClick(uri));
    }

    @Override
    public int getItemCount()
    {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
