package com.povmt.les.povmtprojetopiloto.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Activities.ActivityItemDetailsActivity;

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
        holder.position = position;

        String textResultTitle = "Titulo: " + activityItem.getTitle();
        String textResultUpdateAt = "Ultima atualização: " + activityItem.getUpdatedAt();

        holder.name.setText(textResultTitle);
        holder.updatedAt.setText(textResultUpdateAt);
    }

    @Override
    public int getItemCount() {
        return activityItems.size();
    }

    public void update(List<ActivityItem> newActivities){
        this.activityItems = newActivities;
        notifyDataSetChanged();
    }

    class ActivityItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int position;
        private TextView name;
        private TextView updatedAt;

        ActivityItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewTitle);
            updatedAt = (TextView) itemView.findViewById(R.id.textViewUpdatedAt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent newIntent = new Intent(activity,ActivityItemDetailsActivity.class);
            ActivityItem activityItem = activityItems.get(position);
            Log.d("name", activityItem.getTitle());

            newIntent.putExtra("activityItem", activityItems.get(position));
            activity.startActivity(newIntent);
        }
    }
}
