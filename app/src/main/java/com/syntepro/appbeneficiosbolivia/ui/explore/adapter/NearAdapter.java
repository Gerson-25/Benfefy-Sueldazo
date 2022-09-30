package com.syntepro.appbeneficiosbolivia.ui.explore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.syntepro.appbeneficiosbolivia.R;
import com.syntepro.appbeneficiosbolivia.entity.service.CercaDeTi;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NearAdapter extends RecyclerView.Adapter<NearAdapter.ViewHolder> {

    private ArrayList<CercaDeTi> mList;
    private NearAdapterCallback callback;
    private DecimalFormat format = new DecimalFormat("###,###.##");

    public NearAdapter(ArrayList<CercaDeTi> list, NearAdapterCallback callback) {
        this.mList = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_shops_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (!mList.get(i).getUrlImagen().isEmpty()) Picasso.get().load(mList.get(i).getUrlImagen()).fetch();
        viewHolder.bind(mList.get(i));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView comercio;
        private TextView sucursal;
        private TextView distancia;
        private TextView cupones;
        private ImageView imagen;
        private CardView cardView;

        private ViewHolder(View itemView) {
            super(itemView);
            comercio = itemView.findViewById(R.id.commerceNameId);
            sucursal = itemView.findViewById(R.id.txt_sucursal);
            distancia = itemView.findViewById(R.id.txt_distancia);
            cupones = itemView.findViewById(R.id.txt_cupones);
            imagen = itemView.findViewById(R.id.commerceImage);
            cardView = itemView.findViewById(R.id.cardview_id);
        }

        private void bind(CercaDeTi model) {
            comercio.setText(model.getNombreComercio());
            sucursal.setText(model.getNombreSucursal());
            double d = Double.parseDouble(model.getDistance());
            double km = d/1000;
            String dis = format.format(km) + "km";
            distancia.setText(dis);
            cupones.setText(model.getCampanas());
            Picasso.get()
                    .load(model.getUrlImagen())
                    .fit()
                    .centerInside()
                    .error(R.drawable.notfound)
                    .into(imagen);

            cardView.setOnClickListener(v -> callback.cardAction(model.getIdComercio(), model.getNombreComercio()));
        }
    }

    public interface NearAdapterCallback {
        void cardAction(String id, String name);
    }

}
