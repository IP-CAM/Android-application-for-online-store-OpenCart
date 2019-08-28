package com.example.store.oc;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ProductActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    ViewPager viewPager;
    CustomSwipeAdapter adapter;
    TextView txt;
    TextView price;
    TextView review;
    LinearLayout rating_wrap;
    Button in_cart;
    Button description;
    Button attribute;
    Button btn_review;
    DialogFragment dlg;
    DbCart dbCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
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
        String product_id = CategoryIntent.getStringExtra("product_id");

        txt = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        review = (TextView) findViewById(R.id.review);
        rating_wrap = (LinearLayout) findViewById(R.id.rating_wrap);

        in_cart = (Button) findViewById(R.id.in_cart);
        description = (Button) findViewById(R.id.btn_description);
        attribute = (Button) findViewById(R.id.attribute);
        btn_review = (Button) findViewById(R.id.btn_review);

        dbCart = new DbCart(this);


        new OcApiProduct(ProductActivity.this).execute(product_id);

    }


    public class OcApiProduct extends AsyncTask<String, Void, String> {
        private Context context;
        Gson gson;
        Intent intent;


        public OcApiProduct(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String id = (String) arg0[0];

                String link = getString(R.string.domain) + "index.php?route=appapi/product&product_id=" + id;
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
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            final GsonProduct product = gson.fromJson(result, GsonProduct.class);
            txt.setText(Html.fromHtml(product.getName()));
            setTitle(Html.fromHtml(product.getName()));
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            adapter = new CustomSwipeAdapter(context, product.getImages());
            viewPager.setAdapter(adapter);

            price.setText(String.format("%.2f", product.getPrice()) + " грн.");
            review.setText("Відгуки" + " (" + String.valueOf(product.getReviews()) + ")");

            ImageView starImage;
            for (int i = 1; i <=5; i++){
                starImage = new ImageView(context);
                if (i <= product.getRating()){
                    starImage.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star1));
                } else {
                    starImage.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star2));
                }
                rating_wrap.addView(starImage,new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            in_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    in_cart.setText(R.string.in_cart);
                    in_cart.setBackgroundColor(Color.GRAY);
                    in_cart.setClickable(false);

                    SQLiteDatabase db = dbCart.getWritableDatabase();

                    ContentValues contextValues = new ContentValues();

                    contextValues.put(dbCart.KEY_PRODUCT_ID, product.getProduct_id());
                    contextValues.put(dbCart.KEY_NAME, product.getName());
                    contextValues.put(dbCart.KEY_QUANTITY, 1);
                    contextValues.put(dbCart.KEY_IMAGE, product.getImage());
                    contextValues.put(dbCart.KEY_PRICE, product.getPrice());

                    db.insert(dbCart.TABLE_CART, null, contextValues);
                }
            });

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                    builder.setTitle(R.string.desc)
                            .setMessage(Html.fromHtml(product.getDescription()))
                            .setIcon(R.drawable.ic_star1)
                            .setCancelable(true)
                            .setNegativeButton(R.string.close,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            dlg = new Attribute(product.getAttribute_groups(), context);
            if (product.getAttribute_groups().length == 0){
                attribute.setVisibility(View.GONE);
            }
            attribute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.show(getFragmentManager(), "d");
                }
            });

            btn_review.setText("Відгуки" + " (" + String.valueOf(product.getReviews()) + ")");

            intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("product_id", String.valueOf(product.getProduct_id()));

            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(intent);
                }
            });
            btn_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            /*Toast toast_else = Toast.makeText(ProductActivity.this, "Головна", Toast.LENGTH_LONG);
            toast_else.setGravity(Gravity.CENTER, 0, 0);
            toast_else.show();*/

            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {
            /*Toast toast_else = Toast.makeText(ProductActivity.this, "Категорії", Toast.LENGTH_LONG);
            toast_else.setGravity(Gravity.CENTER, 0, 0);
            toast_else.show();*/

            Intent intent = new Intent(ProductActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(ProductActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
