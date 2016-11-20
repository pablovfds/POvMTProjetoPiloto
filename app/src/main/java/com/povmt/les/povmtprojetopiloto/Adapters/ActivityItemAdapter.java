package com.povmt.les.povmtprojetopiloto.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.List;

/**
 * Created by PABLOVICTOR on 20/11/2016.
 */

public class ActivityItemAdapter  extends RecyclerView.Adapter<ActivityItemAdapter.ActivityItemViewHolder>{

    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<ActivityItem> activityItems;

    public ActivityItemAdapter(Context activity, List<ActivityItem> activityItems) {
        this.activityItems = activityItems;
        this.context = activity;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ActivityItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.item_card_activity_item, parent, false);
        ActivityItemViewHolder gameViewHolder = new ActivityItemViewHolder(view);

        return gameViewHolder;
    }

    @Override
    public void onBindViewHolder(final ActivityItemViewHolder holder, final int position) {
        ActivityItem activityItem = activityItems.get(position);
        Log.d("Adapter", activityItem.getTitle()+"");
        holder.name.setText(activityItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return activityItems.size();
    }

    public void update(List<ActivityItem> newActivities){
        this.activityItems = newActivities;
        notifyDataSetChanged();
    }

    class ActivityItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        ActivityItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewTitle);
        }


    }
}
