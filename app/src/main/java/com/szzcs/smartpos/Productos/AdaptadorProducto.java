package com.szzcs.smartpos.Productos;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.ArrayList;

public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.productoViewHolder> {
    private ArrayList<singleRow> mProductoList;
    public static class productoViewHolder extends  RecyclerView.ViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;

        public productoViewHolder(View itemView){
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }

    }

    public AdaptadorProducto(ArrayList<singleRow> productoList){
        mProductoList = productoList;
    }

    @NonNull
    @Override
    public productoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listaproductos, parent, true);
        productoViewHolder pvh = new productoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull productoViewHolder productoViewHolder, int i) {
        singleRow currentItem = mProductoList.get(i);

        productoViewHolder.mTextView1.setText(currentItem.getTitulo());
        productoViewHolder.mTextView2.setText(currentItem.getSubtitulo());
    }

    @Override
    public int getItemCount() {
        return mProductoList.size();
    }
}
