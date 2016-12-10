package com.povmt.les.povmtprojetopiloto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Activities.ActivityItemDetailsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityItemAdapter  extends RecyclerView.Adapter<ActivityItemAdapter.ActivityItemViewHolder>{

    private final LayoutInflater layoutInflater;
    private final Activity activity;
    private List<ActivityItem> activityItems;

    public ActivityItemAdapter(Context activity, List<ActivityItem> activityItems) {
        this.activityItems = activityItems;
        this.activity = (Activity) activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        String imageUrl = activityItem.getImageUrl();

        try {
            Bitmap image = decodeFromFirebaseBase64(imageUrl);
            holder.photo.setImageBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
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

    private static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
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
            getAdapterPosition();
        }

        @Override
        public void onClick(View v) {
            Intent newIntent = new Intent(activity,ActivityItemDetailsActivity.class);
            newIntent.putExtra("activityItem", activityItems.get(getAdapterPosition()));
            activity.startActivity(newIntent);
        }
    }
}
