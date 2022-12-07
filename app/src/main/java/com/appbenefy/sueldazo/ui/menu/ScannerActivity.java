package com.appbenefy.sueldazo.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.appbenefy.sueldazo.R;
import com.appbenefy.sueldazo.ui.home.adapter.Cupon_BeneficioAdapter;
import com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio;
import com.appbenefy.sueldazo.service.NetworkService;
import com.appbenefy.sueldazo.room.database.RoomDataBase;
import com.appbenefy.sueldazo.room.entity.CountryUser;
import java.util.ArrayList;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio.BENEFIT_LAYOUT;
import static com.appbenefy.sueldazo.entity.firebase.Cupon_Beneficio.CUPON_LAYOUT;

public class ScannerActivity extends AppCompatActivity {

    public final static String CATEGORYID = "categoryid";
    public final static String CATEGORYNAME = "categoryiname";
    private RoomDataBase roomDataBase;
    private TextView titulo;
    private TextView txt_vacio;
    private ImageView img_vacio;
    private ArrayList<Cupon_Beneficio> cardList;
    private Cupon_BeneficioAdapter cardAdapter;
    private boolean fav = false;

    public static Intent getIntent(Context context, String category, String name) {
        return new Intent(context, ScannerActivity.class)
                .putExtra(CATEGORYID, category)
                .putExtra(CATEGORYNAME, name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // ROOM
        roomDataBase = RoomDataBase.getRoomDatabase(ScannerActivity.this);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Views
        titulo = findViewById(R.id.toolbar_title);
        titulo.setText("Scanner");
        txt_vacio = findViewById(R.id.txt_vacio);
        img_vacio = findViewById(R.id.img_vacio);

        // Adapter
        cardList = new ArrayList<>();
        cardAdapter = new Cupon_BeneficioAdapter(cardList);

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager rvLiLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLiLayoutManager);
        recyclerView.setAdapter(cardAdapter);

        // Show Data
        scanner();
    }

    @Override
    protected void onDestroy() {
        RoomDataBase.destroyInstance();
        this.finish();
        super.onDestroy();
    }

    private void scanner() {
        CountryUser cu = roomDataBase.accessDao().getCountry();

        Call<ArrayList<Cupon_Beneficio>> scanner = getRetrofit().create(NetworkService.class).getScanner(cu.getAbr(), "");

        scanner.enqueue(new Callback<ArrayList<Cupon_Beneficio>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Cupon_Beneficio>> call, @NonNull Response<ArrayList<Cupon_Beneficio>> response) {
                if (response.code() == 200) {
                    ArrayList<Cupon_Beneficio> ap = response.body();
                    if (Objects.requireNonNull(ap).isEmpty()) {
                        txt_vacio.setVisibility(View.VISIBLE);
                        img_vacio.setVisibility(View.VISIBLE);
                    } else {
                        txt_vacio.setVisibility(View.GONE);
                        img_vacio.setVisibility(View.GONE);
                        for (Cupon_Beneficio cb : ap) {
                            if (cb.getDocumentID() == null) {
                                titulo.setText(cb.getNombreComercio());
                                String tipoDescuento;
                                if (cb.getFbCodeType() == null || cb.getFbCodeType().equals("")) {
                                    tipoDescuento = "1";
                                } else {
                                    tipoDescuento = cb.getFbCodeType();
                                }
                                cardList.add(new Cupon_Beneficio(CUPON_LAYOUT, cb.getIdCategoria(), cb.getTitulo(), cb.getSubtitulo(), cb.getPrecioDesc(), cb.getPrecioReal(), cb.getDescripcion(), cb.getTyc(), cb.getCantCanje(), cb.getCantCanjeUSER(), cb.getFechaInicio(), cb.getFechaFin(), cb.getImagenCupon(), cb.getImagenComercio(), cb.getNombreComercio(), tipoDescuento, fav));
                            } else {
                                titulo.setText(getString(R.string.scan));
                                cardList.add(new Cupon_Beneficio(BENEFIT_LAYOUT, cb.getDocumentID(), cb.getBtitulo(), cb.getBsubtitulo(), cb.getBdescripcion(), cb.getBurl(), cb.getImagen(), cb.getBimagenComercio(), cb.getBfechaInicio(), cb.getBfechaFin(), fav));
                            }
                        }
                        cardAdapter.notifyDataSetChanged();
                    }
                } else {
                    txt_vacio.setVisibility(View.VISIBLE);
                    img_vacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Cupon_Beneficio>> call, @NonNull Throwable t) {
                txt_vacio.setVisibility(View.VISIBLE);
                img_vacio.setVisibility(View.VISIBLE);
            }
        });
    }

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(NetworkService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
