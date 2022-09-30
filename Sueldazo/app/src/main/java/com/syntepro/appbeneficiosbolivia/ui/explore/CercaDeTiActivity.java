package com.syntepro.appbeneficiosbolivia.ui.explore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.syntepro.appbeneficiosbolivia.R;
import com.syntepro.appbeneficiosbolivia.entity.service.CercaDeTi;
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase;
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser;
import com.syntepro.appbeneficiosbolivia.service.GPS_Service;
import com.syntepro.appbeneficiosbolivia.service.NetworkService;
import com.syntepro.appbeneficiosbolivia.service.NotificationService;
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters.CustomSearchAdapter;
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponListActivity;
import com.syntepro.appbeneficiosbolivia.ui.explore.adapter.NearAdapter;
import com.syntepro.appbeneficiosbolivia.utils.Constants;
import com.syntepro.appbeneficiosbolivia.utils.PreCachingLayoutManagerHelper;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CercaDeTiActivity extends AppCompatActivity implements NearAdapter.NearAdapterCallback {

    public static RoomDataBase roomDataBase;
    double longitud;
    double latitud;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    private ConstraintLayout emptytId;
    private RecyclerView recyclerView;
    private ArrayList<CercaDeTi> cardList;
    private NearAdapter nearAdapter;
    private BroadcastReceiver broadcastReceiver;
    // Modification Custom Search Adapter
    private SearchView searchView;
    private CustomSearchAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Object lo = Objects.requireNonNull(intent.getExtras()).get("longitud");
                    Object la = intent.getExtras().get("latitude");
                    if (lo != null && la != null) {
                        latitud = (double) la;
                        longitud = (double) lo;
                        getNear(latitud, longitud);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cercadeti);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Views
        roomDataBase = RoomDataBase.getRoomDatabase(this);
        swipeRefreshLayout = findViewById(R.id.swipe);
        emptytId = findViewById(R.id.emptyId);
        ImageView mapId = findViewById(R.id.mapId);
        recyclerView = findViewById(R.id.rv);

        // Adapter
        cardList = new ArrayList<>();
        nearAdapter = new NearAdapter(cardList, this);

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setItemViewCacheSize(Constants.PAGE_SIZE * 2);
        PreCachingLayoutManagerHelper rvLiLayoutManager = new PreCachingLayoutManagerHelper(this);
        rvLiLayoutManager.setOrientation(androidx.recyclerview.widget.LinearLayoutManager.VERTICAL);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        rvLiLayoutManager.setExtraLayoutSpace(metrics.heightPixels * 3);
        recyclerView.setLayoutManager(rvLiLayoutManager);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(nearAdapter);

        TextView titulo = findViewById(R.id.toolbar_title);
        titulo.setText(getString(R.string.cerca_exp));

        mapId.setOnClickListener(view -> {
            String la = String.valueOf(latitud);
            String lo = String.valueOf(longitud);
            startActivity(MapsActivity.getIntent(this, la, lo));
        });

        dialog = new Dialog(CercaDeTiActivity.this);
        dialog.setContentView(R.layout.custom_loader);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        dialog.setOnCancelListener(dialog -> {
            finish();
            dialog.dismiss();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000));

        mAdapter = new CustomSearchAdapter(this, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!runtime_permissions()) enable_service();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RoomDataBase.destroyInstance();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        stopService(i);
        Intent j = new Intent(getApplicationContext(), NotificationService.class);
        startService(j);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_trends_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.black));
        searchEditText.setTextSize(14f);
        searchEditText.setBackgroundResource(R.drawable.search_text_background);

        searchView.setSuggestionsAdapter(mAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("name"));
                searchView.setQuery(txt, true);
                return true;
            }
        });

        String[] columns = new String[] {BaseColumns._ID, "reference", "image", "title", "subtitle", "type"};
        MatrixCursor c = new MatrixCursor(columns);
        addDefaultSuggestions(c);

        // Autocomplete
        SearchView.SearchAutoComplete aut = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        // Hace que se muestre el dialogo de suggestions al hacer click en buscar
        aut.setThreshold(0);

        searchView.setOnCloseListener(() -> {
            searchView.setQuery("", false);
            searchView.clearFocus();
            return false;
        });

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewItem.setOnMenuItemClickListener(menuItem -> false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                populateAdapter(query);
                return false;
            }
        });

        searchView.setOnClickListener(view -> {
            //String g = searchEditText.getText().toString();
            searchView.clearFocus();
        });

        return true;
    }

    private void populateAdapter(String query) {
        String[] columns = new String[] {BaseColumns._ID, "reference", "image", "title", "subtitle", "type"};
        MatrixCursor cursor = new MatrixCursor(columns);

        if (query.length() > 2) {
            query = query.toLowerCase();
            int i = 0;
            for (CercaDeTi c : cardList) {
                String commerceName = c.getNombreComercio().toLowerCase();
                String officeName = c.getNombreSucursal().toLowerCase();
                if (commerceName.contains(query) || officeName.contains(query)) {
                    String[] row = new String[]{String.valueOf(i), c.getIdComercio(), c.getUrlImagen(), c.getNombreComercio(), c.getNombreSucursal(), String.valueOf(Constants.SUGGESTION_BRANCH_OFFICE)};
                    cursor.addRow(row);
                    i++;
                }
            }
            addDefaultSuggestions(cursor);
        }
    }

    private void addDefaultSuggestions(MatrixCursor c){
        mAdapter.changeCursor(c);
    }

    private void getNear(double la, double lo) {
        CountryUser cu = roomDataBase.accessDao().getCountry();

        cardList.clear();
        recyclerView.getRecycledViewPool().clear();
        nearAdapter.notifyDataSetChanged();

        Call<ArrayList<CercaDeTi>> cerca = getRetrofit().create(NetworkService.class).getCerca(cu.getAbr(), la, lo);

        cerca.enqueue(new Callback<ArrayList<CercaDeTi>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CercaDeTi>> call, @NonNull Response<ArrayList<CercaDeTi>> response) {
                if (response.code() == 200) {
                    ArrayList<CercaDeTi> ap = response.body();
                    if (Objects.requireNonNull(ap).isEmpty()) {
                        cardList.clear();
                        nearAdapter.notifyDataSetChanged();
                        emptytId.setVisibility(View.VISIBLE);
                    } else {
                        emptytId.setVisibility(View.GONE);
                        for (CercaDeTi ct : Objects.requireNonNull(ap)) {
                            cardList.add(new CercaDeTi(ct.getIdSucursal(), ct.getIdComercio(), ct.getNombreComercio(), ct.getUrlImagen(), ct.getNombreSucursal(), ct.getCampanas(), ct.getDistance(), ct.getDireccion(), ct.getLatitude(), ct.getLongitud()));
                        }
                        nearAdapter.notifyDataSetChanged();
                    }
                } else {
                    cardList.clear();
                    nearAdapter.notifyDataSetChanged();
                    emptytId.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CercaDeTi>> call, @NonNull Throwable t) {
                cardList.clear();
                nearAdapter.notifyDataSetChanged();
                emptytId.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 100) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    enable_service();
                } else {
                    runtime_permissions();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest
                .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                    .ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    private void enable_service() {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
        Intent j = new Intent(getApplicationContext(), NotificationService.class);
        stopService(j);
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

    @Override
    public void cardAction(String id, String name) {
        Intent intent = new Intent(this, CouponListActivity.class);
        intent.putExtra("commerceId", id);
        intent.putExtra("commerceName", name);
        startActivity(intent);
    }
}
