package com.example.store.oc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.TypedValue;
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

public class SubCategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    String category_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
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
        category_name = CategoryIntent.getStringExtra("category_name");
        setTitle(Html.fromHtml(category_name));

        LinearLayout main_linear_products = (LinearLayout) findViewById(R.id.main_linear_category);
        new OcApiSubCategory(SubCategoryActivity.this, main_linear_products).execute(category_id);

    }
    public class OcApiSubCategory extends AsyncTask<String, Void, String> {
        private Context context;
        TextView text;
        String id;
        LinearLayout main_linear_products;
        Gson gson;


        public OcApiSubCategory(Context context, LinearLayout main_linear_products) {
            this.context = context;
            this.main_linear_products = main_linear_products;
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String id = (String) arg0[0];
                this.id = id;

                String link = getString(R.string.domain) + "index.php?route=appapi/category&parent=" + id;

                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
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
            LinearLayout line_for_subcat = null;
            TextView txt;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int i=0; i<category.length; i++){
                line_for_subcat = new LinearLayout(context);
                line_for_subcat.setOrientation(LinearLayout.HORIZONTAL);
                layoutParams.setMargins(0,3,0,3);
                line_for_subcat.setBackground(ContextCompat.getDrawable(context, R.drawable.border1));
                setOnClick(line_for_subcat, String.valueOf(category[i].getCategory_id()), category[i].getName());

                txt = new TextView(context);
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                txt.setText(category[i].getName());

                line_for_subcat.addView(txt,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                main_linear_products.addView(line_for_subcat, layoutParams);
            }
            line_for_subcat = new LinearLayout(context);
            line_for_subcat.setOrientation(LinearLayout.HORIZONTAL);
            line_for_subcat.setBackground(ContextCompat.getDrawable(context, R.drawable.border1));
            setOnClick(line_for_subcat, this.id, category_name);

            txt = new TextView(context);
            txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
            txt.setTextColor(Color.parseColor("#3333bb"));
            txt.setText("Всі товари в цій категорії");

            line_for_subcat.addView(txt,new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            LinearLayout.LayoutParams layoutParams_all = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams_all.setMargins(0,30,0,3);
            main_linear_products.addView(line_for_subcat, layoutParams_all);
        }
        private void setOnClick(final LinearLayout btn, final String str, final String name){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CategoryProductsActivity.class);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Intent intent = new Intent(SubCategoryActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {

            Intent intent = new Intent(SubCategoryActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(SubCategoryActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
