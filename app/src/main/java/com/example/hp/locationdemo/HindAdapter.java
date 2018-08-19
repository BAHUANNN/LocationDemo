package com.example.hp.locationdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;

class HindAdapter extends RecyclerView.Adapter {
    private static final int TEXT_VIEW = 0;
    private static final int BOTTOM_VIEW = 1;
    private ArrayList<PoiItem> list;
    private ChangeText changeText;

    public HindAdapter(ArrayList<PoiItem> list,ChangeText changeText){
        this.list = list;
        this.changeText = changeText;
    }

    @Override
    public int getItemViewType(int position) {
        return position==list.size() ? BOTTOM_VIEW :TEXT_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TEXT_VIEW){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text,parent,false);
            return new TextHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hind,parent,false);
            return new BottomHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == list.size()){
            ((BottomHolder)holder).bind();
        }else {
            ((TextHolder) holder).bind(list.get(position).getAdName(),list.get(position).getLatLonPoint());
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public class TextHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public TextHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
        }

        public void bind(final String s, final LatLonPoint l){
            textView.setText(s);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeText.changeEditText(s,l);
                }
            });
        }
    }

    public class BottomHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public BottomHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_hind);
        }

        public void bind() {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转操作
                }
            });
        }
    }
}
