package com.jldevtech.mactracker.ControllerFolder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jldevtech.mactracker.Adapters.SearchAdapter;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.util.RequestBuilderLocal;
import com.fatsecret.platform.model.CompactFood;
import com.fatsecret.platform.services.Response;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;


public class SearchFood extends AppCompatActivity implements View.OnClickListener {

    RecyclerView superListView;
    SearchView search;
    ImageButton barcodeScan;
    boolean found;
    String id;
    private ArrayList<CompactFood> foodList;
    public Request req;
    public RequestQueue requestQueue;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_food);
        superListView = findViewById(R.id.searchView);
        search = findViewById(R.id.searchQuery);
        int id = search.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) search.findViewById(id);
        textView.setTextColor(Color.WHITE);

        barcodeScan = findViewById(R.id.barcodeScan);
        barcodeScan.setOnClickListener(this);
        foodList = new ArrayList<>();
        setRecyclerView();

        //Listens for user input to search bar
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CharSequence searchQ = search.getQuery();
                String sQ = String.valueOf(searchQ);
                getFoods(sQ);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void setRecyclerView() {
            SearchAdapter adapter = new SearchAdapter(this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
            superListView.setLayoutManager(gridLayoutManager);
            superListView.setItemAnimator(new DefaultItemAnimator());
            superListView.setAdapter(adapter);
            adapter.setTerms(foodList);
    }

    /**Opens barcode scanner (phone camera) */
    @Override
    public void onClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(BarcodeScan.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.initiateScan();
    }

    /**Creates FatSecret request to query macro info for users search from search bar */
    private void getFoods(String query) {
        String key = getString(R.string.KEY);
        String secret = getString(R.string.SECRET);
        requestQueue = Volley.newRequestQueue(this);
        SearchFood.Listener listener = new SearchFood.Listener();
        req = new Request(key, secret, listener);
        req.searchFoods(requestQueue, query);
        foodList.clear();
    }

    /**Listens for API response and adds results to foodList for viewing */
    class Listener implements ResponseListener {
        @Override
        public void onFoodListRespone(Response<CompactFood> response) {
            List<CompactFood> foods = response.getResults();
            foodList.addAll(foods);
            setRecyclerView();
        }
    }

    /**On barcode scan result, a request url is built (FatSecret) and the passed to getJSONResponse method */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                System.out.println(result.getContents());
                String key = getString(R.string.KEY);
                String secret = getString(R.string.SECRET);
                RequestBuilderLocal requestBuilder = new RequestBuilderLocal(key, secret);
                try {
                    String id = requestBuilder.buildBarcodeSearchUrl("0" + result.getContents());
                    getJSONResponse(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addFoodFromRecent(View view) {
        Intent i = new Intent(this, HistorySearch.class);
        startActivity(i);
    }

    public void addFoodManually(View view) {
        Intent i = new Intent(this, AddFood.class);
        startActivity(i);
    }

    /**Uses okhttp to build request using url from barcode activity listener and queries food.find_id_for_barcode */
    public void getJSONResponse(String apiUrl) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(apiUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
                    String jsonData = responseBody.string();
                    JSONObject Jobject = new JSONObject(jsonData);
                        try {
                            System.out.println(jsonData);
                            JSONObject j = Jobject.getJSONObject("food_id");
                            id = j.getString("value");
                            if (id.equals("0")) {
                                found = false;
                            } else {
                                found = true;
                            }
                        } catch (org.json.JSONException e ) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchFood.this);
                            builder.setMessage("No matching info found!");
                            builder.setTitle("Alert !");
                            builder.setCancelable(true);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            found = false;
                            goToMain();
                        }
                    if (found) { addFromBarcode(id); }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**Passses in id returned from FatSecretAPI call and starts next activity using the food id from the barcode */
    public void addFromBarcode(String id) {
        Intent i = new Intent(this, AddFoodFromBarcode.class);
        i.putExtra("foodId" , Long.parseLong(id));
        startActivity(i);
    }

    public void goToMain() {
        Intent i = new Intent(this, MacroInfoScreen.class);
        startActivity(i);
    }
}
