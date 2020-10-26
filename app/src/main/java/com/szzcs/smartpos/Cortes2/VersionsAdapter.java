package com.szzcs.smartpos.Cortes2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.List;

public class VersionsAdapter extends RecyclerView.Adapter<VersionsAdapter.versionVH> {

    List<CierreDesgloceVales> cierreDesgloceValesList;

    public VersionsAdapter(List<CierreDesgloceVales> versionsList) {
        this.cierreDesgloceValesList = versionsList;
    }

    @NonNull
    @Override
    public versionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new versionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull versionVH holder, int position) {
        CierreDesgloceVales cierreDesgloceVales = cierreDesgloceValesList.get(position);
        holder.tipoTxt.setText(cierreDesgloceVales.getTipoVale());
        holder.descripcionTxt.setText(cierreDesgloceVales.getDescripcion());
        holder.numeroValeTxt.setText(cierreDesgloceVales.getNumeroVales());
        holder.importeTxt.setText(cierreDesgloceVales.getImporteVales());

        boolean isExpandible = cierreDesgloceValesList.get(position).isExpandible();
        holder.expandibleLayout.setVisibility(isExpandible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return cierreDesgloceValesList.size();
    }

    public class versionVH extends RecyclerView.ViewHolder {

        TextView tipoTxt, descripcionTxt, numeroValeTxt, importeTxt;
        LinearLayout linearLayout;
        RelativeLayout expandibleLayout;
        public versionVH(@NonNull View itemView) {
            super(itemView);
            descripcionTxt = itemView.findViewById(R.id.descripcionTipoVale);
            numeroValeTxt = itemView.findViewById(R.id.cantidadVales);
            importeTxt = itemView.findViewById(R.id.importeVales);
            tipoTxt = itemView.findViewById(R.id.descripcion);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandibleLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CierreDesgloceVales cierreDesgloceVales = cierreDesgloceValesList.get(getAdapterPosition());
                    cierreDesgloceVales.setExpandible(!cierreDesgloceVales.isExpandible());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
