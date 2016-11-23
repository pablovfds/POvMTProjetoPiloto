package com.povmt.les.povmtprojetopiloto.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.List;

/**
 * Created by PABLOVICTOR on 22/11/2016.
 */

public class InvestedTimeAdapter extends RecyclerView.Adapter<InvestedTimeAdapter.InvestedTimeViewHolder>{
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<InvestedTime> investedTimes;

    public InvestedTimeAdapter(Context activity, List<InvestedTime> investedTimes) {
        this.investedTimes = investedTimes;
        this.context = activity;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public InvestedTimeAdapter.InvestedTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.item_card_invested_time, parent, false);

        return new InvestedTimeAdapter.InvestedTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InvestedTimeAdapter.InvestedTimeViewHolder holder, final int position) {
        InvestedTime investedTime = investedTimes.get(position);
        holder.investedTime.setText(String.valueOf(investedTime.getTime()));
        holder.createdAt.setText(investedTime.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return investedTimes.size();
    }

    public void update(List<InvestedTime> newInvestedTime){
        this.investedTimes = newInvestedTime;
        notifyDataSetChanged();
    }

    class InvestedTimeViewHolder extends RecyclerView.ViewHolder {

        private TextView investedTime;
        private TextView createdAt;

        InvestedTimeViewHolder(View itemView) {
            super(itemView);
            investedTime = (TextView) itemView.findViewById(R.id.textViewTi);
            createdAt = (TextView) itemView.findViewById(R.id.textViewTiCreatedAt);
        }


    }
}
