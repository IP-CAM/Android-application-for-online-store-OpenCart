package com.example.store.oc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CategoryProductsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent CategoryIntent = getIntent();
        String category_id = CategoryIntent.getStringExtra("category_id");
        String category_name = CategoryIntent.getStringExtra("category_name");
        setTitle(Html.fromHtml(category_name));

        LinearLayout main_linear_products = (LinearLayout) findViewById(R.id.main_linear_products);
        new OcApiCategoryProducts(CategoryProductsActivity.this, main_linear_products).execute(category_id);

    }
    public class OcApiCategoryProducts extends AsyncTask<String, Void, String> {
        private Context context;
        LinearLayout main_linear_products;
        Gson gson;


        public OcApiCategoryProducts(Context context, LinearLayout main_linear_products) {
            this.context = context;
            this.main_linear_products = main_linear_products;
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String id = (String) arg0[0];
                String link = getString(R.string.domain) + "index.php?route=appapi/products&id=" + id;
                URL url = new URL(link);
                URLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                //wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            //return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(String result) {
            gson = new Gson();
            GsonProducts[] products = gson.fromJson(result, GsonProducts[].class);
            LinearLayout line_for_products = null;
            LinearLayout product;
            ImageView image;
            TextView txt;
            TextView txtPrice;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParams_product = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);

            for (int i=0; i<products.length; i++){
                if(i%2==0){
                    line_for_products = new LinearLayout(context);
                    line_for_products.setOrientation(LinearLayout.HORIZONTAL);
                    line_for_products.setLayoutParams(layoutParams);
                }
                product = new LinearLayout(context);
                product.setOrientation(LinearLayout.VERTICAL);
                product.setPadding(5,5,5,5);
                layoutParams_product.setMargins(10,10,10,10);
                product.setBackground(ContextCompat.getDrawable(context, R.drawable.border1));
                setOnClick(product, String.valueOf(products[i].getProduct_id()));

                image = new ImageView(context);
                new DownloadImageTask(image)
                        .execute(products[i].getImage());

                txt = new TextView(context);
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txt.setText(Html.fromHtml(products[i].getName()));

                txtPrice = new TextView(context);
                txtPrice.setText(String.format("%.2f", products[i].getPrice()) + " грн.");
                txtPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                product.addView(image,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                product.addView(txt,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                product.addView(txtPrice,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                line_for_products.addView(product, layoutParams_product);

                if(i%2==0){
                    main_linear_products.addView(line_for_products,new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                }
            }
            if(products.length % 2 == 1){
                product = new LinearLayout(context);
                product.setOrientation(LinearLayout.VERTICAL);
                line_for_products.addView(product, layoutParams_product);
            }
        }
        private void setOnClick(final LinearLayout btn, final String str){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductActivity.class);
                    intent.putExtra("product_id", str);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Intent intent = new Intent(CategoryProductsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_category) {

            Intent intent = new Intent(CategoryProductsActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(CategoryProductsActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
