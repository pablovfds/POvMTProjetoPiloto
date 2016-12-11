package com.povmt.les.povmtprojetopiloto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Activities.ActivityItemDetailsActivity;

import java.util.List;

public class ActivityItemAdapter extends RecyclerView.Adapter<ActivityItemAdapter.ActivityItemViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Activity activity;
    private List<ActivityItem> activityItems;

    public ActivityItemAdapter(Context activity, List<ActivityItem> activityItems) {
        this.activityItems = activityItems;
        this.activity = (Activity) activity;
        this.layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ActivityItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_card_activity_item, parent, false);
        return new ActivityItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActivityItemViewHolder holder, final int position) {
        final ActivityItem activityItem = activityItems.get(position);

        String textResultTitle = "Titulo: " + activityItem.getTitle();
        String textResultUpdateAt = "Ultima atualização: " + activityItem.getUpdatedAt();

        holder.name.setText(textResultTitle);
        holder.updatedAt.setText(textResultUpdateAt);

        if (activityItem.getImageUrl() != null) {
            retrieveActivityImage(activityItem.getImageUrl(), holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return activityItems.size();
    }

    public void update(List<ActivityItem> newActivities){
        this.activityItems = newActivities;
        notifyDataSetChanged();
    }

    private void retrieveActivityImage(String imageId, final ImageView imageView){
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Photos").child(imageId);

        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(activity)
                        .load(uri)
                        .asBitmap()
                        .placeholder(R.drawable.camera_image)
                        .centerCrop()
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    class ActivityItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView updatedAt;
        private ImageView photo;

        ActivityItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewTitle);
            updatedAt = (TextView) itemView.findViewById(R.id.textViewUpdatedAt);
            photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent newIntent = new Intent(activity,ActivityItemDetailsActivity.class);
            newIntent.putExtra("activityItem", activityItems.get(getAdapterPosition()));
            activity.startActivity(newIntent);
        }
    }
}
