package com.runhuaoil.yyweater.dataAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.runhuaoil.yyweater.R;

import java.util.List;

/**
 * Created by RunHua on 2016/10/25.
 */

public class AreaRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> areaList;
    private Context context;

    public AreaRecyclerViewAdapter(List<String> areaList) {
        this.areaList = areaList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView areaTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            areaTextView = (TextView) itemView.findViewById(R.id.area_text_view);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.areaTextView.setText(areaList.get(position));

    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }


}
