package com.example.hp.locationdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class HindAdapter extends RecyclerView.Adapter {
    private static final int TEXT_VIEW = 0;
    private static final int BOTTOM_VIEW = 1;
    private ArrayList<String> list;

    public HindAdapter(ArrayList<String> list){
        this.list = list;
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
            ((TextHolder) holder).bind("8号教学楼");
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

        public void bind(String s){
            textView.setText(s);
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

                }
            });
        }
    }
}
