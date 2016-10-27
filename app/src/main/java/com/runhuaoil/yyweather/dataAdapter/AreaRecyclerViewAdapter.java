package com.runhuaoil.yyweather.dataAdapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.runhuaoil.yyweather.R;
import com.runhuaoil.yyweather.util.CardViewOnClickListener;

import java.util.List;

/**
 * Created by RunHua on 2016/10/25.
 */

public class AreaRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> areaList;
    private Context context;
    private CardViewOnClickListener listener;


    public AreaRecyclerViewAdapter(List<String> areaList, CardViewOnClickListener listener) {
        this.areaList = areaList;
        this.listener = listener;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView areaTextView;
        public CardView areaCardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            areaTextView = (TextView) itemView.findViewById(R.id.area_text_view);
            areaCardView = (CardView) itemView.findViewById(R.id.area_card_view);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.area_item_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.areaTextView.setText(areaList.get(position));
        myViewHolder.areaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    public void notifyUpData(List<String> list){
        this.areaList = list;

        notifyDataSetChanged();
    }


}
