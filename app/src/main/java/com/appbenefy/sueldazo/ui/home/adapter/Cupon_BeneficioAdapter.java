package com.appbenefy.sueldazo.ui.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.appbenefy.sueldazo.R;
import com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio;
import com.appbenefy.sueldazo.room.database.RoomDataBase;
import com.appbenefy.sueldazo.ui.coupon.CouponDetailActivity;
import com.appbenefy.sueldazo.utils.Constants;
import com.appbenefy.sueldazo.utils.Functions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio.BENEFIT_LAYOUT;
import static com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio.CUPON_LAYOUT;

public class Cupon_BeneficioAdapter extends RecyclerView.Adapter {

    private ArrayList<Cupon_Beneficio> mData;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public Cupon_BeneficioAdapter(ArrayList<Cupon_Beneficio> mData) {
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).getViewType()) {
            case 0:
                return CUPON_LAYOUT;
            case 1:
                return BENEFIT_LAYOUT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viweType) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        switch (viweType) {
            case CUPON_LAYOUT:
                View cuponLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_card_items, viewGroup, false);
                return new CuponLayout(cuponLayout);
            case BENEFIT_LAYOUT:
                View benefitLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_benefits_items, viewGroup, false);
                return new BenefitLayout(benefitLayout);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        switch (mData.get(position).getViewType()) {
            case CUPON_LAYOUT:
                ((CuponLayout) viewHolder).favorite(Objects.requireNonNull(currentUser), mData.get(position).getIdCategoria(), viewHolder.itemView.getContext());

                String cTitulo = mData.get(position).getTitulo();
                String cSubtitulo = mData.get(position).getSubtitulo();
                String cPrecioReal = mData.get(position).getPrecioReal();
                String cPrecioDescuento = mData.get(position).getPrecioDesc();
                String cImagenCupon = mData.get(position).getImagenCupon();
                String cImagenComercio = mData.get(position).getImagenComercio();
                String cTipoDescuento = mData.get(position).getFbCodeType();

                ((CuponLayout) viewHolder).setData(cTitulo, cSubtitulo, cPrecioReal, cPrecioDescuento, cImagenCupon, cImagenComercio, cTipoDescuento);

                ((CuponLayout) viewHolder).fav.setOnClickListener(v -> {
                    Animation animScale = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.scale_fav);
                    v.startAnimation(animScale);
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser1 = mAuth.getCurrentUser();
                    ((CuponLayout) viewHolder).addFav(Objects.requireNonNull(currentUser1), mData.get(position).getIdCategoria());
                });

                ((CuponLayout) viewHolder).cardView.setOnClickListener(v -> {
                    if (mData.size() != 0) {
//                        viewHolder.itemView.getContext().startActivity(DetalleCuponActivity.getIntent(viewHolder.itemView.getContext(), mData.get(position).getIdCategoria(), "0"));
                        Intent intent = new Intent(viewHolder.itemView.getContext(), CouponDetailActivity.class);
                        intent.putExtra("couponId", mData.get(position).getIdCategoria());
                        intent.putExtra("couponType", "0");
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
                break;
            case BENEFIT_LAYOUT:
                ((BenefitLayout) viewHolder).favorite(Objects.requireNonNull(currentUser), mData.get(position).getDocumentID(), viewHolder.itemView.getContext());

                String bTitulo = mData.get(position).getBtitulo();
                String bSubtitulo = mData.get(position).getBsubtitulo();
                String bImagen = mData.get(position).getImagen();

                ((BenefitLayout) viewHolder).setData(bTitulo, bSubtitulo, bImagen);

                ((BenefitLayout) viewHolder).fav.setOnClickListener(v -> {
                    Animation animScale = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.scale_fav);
                    v.startAnimation(animScale);
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser12 = mAuth.getCurrentUser();
                    ((BenefitLayout) viewHolder).addFav(Objects.requireNonNull(currentUser12), mData.get(position).getIdCategoria());
                });

                ((BenefitLayout) viewHolder).cardView.setOnClickListener(v -> {
                    if (mData.size() != 0) {
                        Log.e("Click", "Benefits Details");
//                        viewHolder.itemView.getContext().startActivity(DetalleBeneficioActivity.getIntent(viewHolder.itemView.getContext(), mData.get(position).getDocumentID()));
                    }
                });
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class CuponLayout extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView subtitulo;
        private TextView precioReal;
        private TextView precioDesc;
        private ImageView imagen;
        private ImageView imgComercio;
        private ImageButton fav;
        private CardView cardView;

        CuponLayout(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titleId);
            subtitulo = itemView.findViewById(R.id.txt_subtitulo);
            precioReal = itemView.findViewById(R.id.txt_precioReal);
            precioDesc = itemView.findViewById(R.id.txt_precioDesc);
            imagen = itemView.findViewById(R.id.couponImageView);
            imgComercio = itemView.findViewById(R.id.commerceImage);
            cardView = itemView.findViewById(R.id.card_view);
            fav = itemView.findViewById(R.id.fav);
        }

        private void setData(String cTitulo, String cSubtitulo, String cPrecioReal, String cPrecioDescuento, String cImagenCupon, String cImagenComercio, String cTipoDescuento) {
            DecimalFormat format = new DecimalFormat("###,###.00");
            String m;

            if (Constants.Companion.getUserCountryProfile() != null) {
                m = Constants.Companion.getUserCountryProfile().getMoneda();
            } else {
                m = Functions.Companion.getUserSession().getMoneda();
                RoomDataBase.destroyInstance();
            }

            titulo.setText(cTitulo);
            subtitulo.setText(cSubtitulo);

            // Validation Coupon Code
            if (cTipoDescuento == null || cTipoDescuento.equals("") || cTipoDescuento.equals("1")) {
                double cpr = Double.parseDouble(cPrecioReal);
                precioReal.setText(Html.fromHtml("<strike>" + m + " " + format.format(cpr) + "</strike>"));
                double cpd = Double.parseDouble(cPrecioDescuento);
                String desc = m + " " + format.format(cpd);
                precioDesc.setText(desc);
            } else if (cTipoDescuento.equals("2")) {
                precioReal.setText("");
                double cpd = Double.parseDouble(cPrecioDescuento);
                String desc = format.format(cpd) + " %";
                precioDesc.setText(desc);
            } else if (cTipoDescuento.equals("3")) {
                precioReal.setText("");
                double cpd = Double.parseDouble(cPrecioDescuento);
                String desc = m + " " + format.format(cpd);
                precioDesc.setText(desc);
            }

            Picasso.get()
                    .load(cImagenCupon)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.notfound)
                    .into(imagen);
            Picasso.get()
                    .load(cImagenComercio)
                    .fit()
                    .centerInside()
                    .error(R.drawable.notfound)
                    .into(imgComercio);
        }

        private void favorite(FirebaseUser user, String id, Context ctx) {
            String favorito = "0";
            db.collection("Favorito")
                    .whereEqualTo("idUsuario", user.getUid())
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                fav.setBackgroundResource(R.drawable.ic_favorito);
                            } else {
                                Animation animScale = AnimationUtils.loadAnimation(ctx, R.anim.scale_fav);
                                fav.startAnimation(animScale);
                                fav.setBackgroundResource(R.drawable.ic_corazon);
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.getException());
                    });
        }

        private void addFav(final FirebaseUser user, final String id) {
            String favorito = "0";
            db.collection("Favorito")
                    .whereEqualTo("idUsuario", user.getUid())
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                fav.setBackgroundResource(R.drawable.ic_corazon);
                                Map<String, Object> newFav = new HashMap<>();
                                newFav.put("idUsuario", user.getUid());
                                newFav.put("idCupon", id);
                                newFav.put("fecha", new Date(System.currentTimeMillis()));
                                newFav.put("estado", "0");
                                newFav.put("tipo", "c");

                                db.collection("Favorito")
                                        .add(newFav)
                                        .addOnFailureListener(e -> fav.setBackgroundResource(R.drawable.ic_favorito));
                            } else {
                                fav.setBackgroundResource(R.drawable.ic_favorito);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference ref = db.collection("Favorito").document(document.getId());
                                    ref.update("estado", "1")
                                            .addOnFailureListener(e -> fav.setBackgroundResource(R.drawable.ic_corazon));
                                }
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.getException());
                    });
        }

    }

    class BenefitLayout extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView subtitulo;
        private ImageView imagen;
        private ImageButton fav;
        private CardView cardView;

        BenefitLayout(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txt_name);
            subtitulo = itemView.findViewById(R.id.txt_rubro);
            imagen = itemView.findViewById(R.id.img_shop);
            cardView = itemView.findViewById(R.id.card_view);
            fav = itemView.findViewById(R.id.fav);
        }

        private void setData(String bTitulo, String bSubtitulo, String bImagen) {
            titulo.setText(bTitulo);
            subtitulo.setText(bSubtitulo);
            Picasso.get()
                    .load(bImagen)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.notfound)
                    .into(imagen);
        }

        private void favorite(FirebaseUser user, String id, Context ctx) {
            String favorito = "0";
            db.collection("Favorito")
                    .whereEqualTo("idUsuario", user.getUid())
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                fav.setBackgroundResource(R.drawable.ic_favorito);
                            } else {
                                Animation animScale = AnimationUtils.loadAnimation(ctx, R.anim.scale_fav);
                                fav.startAnimation(animScale);
                                fav.setBackgroundResource(R.drawable.ic_corazon);
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.getException());
                    });
        }

        private void addFav(final FirebaseUser user, final String id) {
            String favorito = "0";
            db.collection("Favorito")
                    .whereEqualTo("idUsuario", user.getUid())
                    .whereEqualTo("idCupon", id)
                    .whereEqualTo("estado", favorito)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                fav.setBackgroundResource(R.drawable.ic_corazon);
                                Map<String, Object> newFav = new HashMap<>();
                                newFav.put("idUsuario", user.getUid());
                                newFav.put("idCupon", id);
                                newFav.put("fecha", new Date(System.currentTimeMillis()));
                                newFav.put("estado", "0");
                                newFav.put("tipo", "b");

                                db.collection("Favorito")
                                        .add(newFav)
                                        .addOnFailureListener(e -> fav.setBackgroundResource(R.drawable.ic_favorito));
                            } else {
                                fav.setBackgroundResource(R.drawable.ic_favorito);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference ref = db.collection("Favorito").document(document.getId());
                                    ref.update("estado", "1")
                                            .addOnFailureListener(e -> fav.setBackgroundResource(R.drawable.ic_corazon));
                                }
                            }
                        } else Log.d("ERROR", "Error getting documents: ", task.getException());
                    });
        }
    }

}
