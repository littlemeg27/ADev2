package com.example.appa;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>
{
    private ArrayList<Uri> imageUris;
    private final OnItemClickListener onClickListener;
    private final OnDeleteListener onDeleteListener;

    public interface OnItemClickListener
    {
        void onItemClick(Uri uri);
    }

    public interface OnDeleteListener
    {
        void onDelete(Uri uri);
    }

    public ImageAdapter(ArrayList<Uri> imageUris, OnItemClickListener clickListener, OnDeleteListener deleteListener)
    {
        this.imageUris = imageUris;
        this.onClickListener = clickListener;
        this.onDeleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        Uri uri = imageUris.get(position);
        holder.imageView.setImageURI(uri);
        holder.itemView.setOnClickListener(v -> onClickListener.onItemClick(uri));
        holder.deleteButton.setOnClickListener(v ->
        {
            if (onDeleteListener != null)
            {
                onDeleteListener.onDelete(uri);
            }
            imageUris.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount()
    {
        return imageUris.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        Button deleteButton;

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}