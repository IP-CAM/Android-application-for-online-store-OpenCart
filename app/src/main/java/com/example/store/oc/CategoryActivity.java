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

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout main_linear_products = (LinearLayout) findViewById(R.id.main_linear_category);
        new OcApiCategory(CategoryActivity.this, main_linear_products).execute();

    }
    public class OcApiCategory extends AsyncTask<String, Void, String> {
        private Context context;
        TextView text;
        LinearLayout main_linear_products;
        Gson gson;


        public OcApiCategory(Context context, LinearLayout main_linear_products) {
            this.context = context;
            this.main_linear_products = main_linear_products;
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = getString(R.string.domain) + "index.php?route=appapi/category";

                URL url = new URL(link);
                URLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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
            GsonCategory[] category = gson.fromJson(result, GsonCategory[].class);
            LinearLayout line_for_category = null;
            LinearLayout category_block;
            ImageView image;
            TextView txt;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParams_product = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);

            for (int i=0; i<category.length; i++){
                if(i%2==0){
                    line_for_category = new LinearLayout(context);
                    line_for_category.setOrientation(LinearLayout.HORIZONTAL);
                    line_for_category.setLayoutParams(layoutParams);
                }
                category_block = new LinearLayout(context);

                category_block.setOrientation(LinearLayout.VERTICAL);
                category_block.setPadding(5,5,5,5);
                layoutParams_product.setMargins(10,10,10,10);
                category_block.setBackground(ContextCompat.getDrawable(context, R.drawable.border1));
                setOnClick(category_block, String.valueOf(category[i].getCategory_id()), String.valueOf(category[i].getName()));

                image = new ImageView(context);
                new DownloadImageTask(image)
                        .execute(category[i].getImage());

                txt = new TextView(context);
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txt.setText(Html.fromHtml(category[i].getName()));

                category_block.addView(image,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                category_block.addView(txt,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                line_for_category.addView(category_block, layoutParams_product);

                if(i%2==0){
                    main_linear_products.addView(line_for_category,new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                }
            }
            if(category.length % 2 == 1){
                category_block = new LinearLayout(context);
                category_block.setOrientation(LinearLayout.VERTICAL);
                line_for_category.addView(category_block, layoutParams_product);
            }
        }
        private void setOnClick(final LinearLayout btn, final String str, final String name){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SubCategoryActivity.class);
                    intent.putExtra("category_id", str);
                    intent.putExtra("category_name", name);
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
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {

            /*Intent intent = new Intent(CategoryActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(CategoryActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
