package com.example.store.oc;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class CheckoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DbCart dbCart;
    SQLiteDatabase db;
    Gson gson;
    String json;
    Button send;
    TextInputEditText firstname;
    TextInputEditText lastname;
    TextInputEditText email;
    TextInputEditText phone;
    TextInputEditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);

        send = findViewById(R.id.send);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean error = false;
                if(firstname.getText().length() < 3 ){
                    error = true;
                    firstname.setBackgroundColor(Color.parseColor(getString(R.string.checkout_error)));
                } else
                    firstname.setBackgroundColor(0);

                if(lastname.getText().length() < 3 ){
                    error = true;
                    lastname.setBackgroundColor(Color.parseColor(getString(R.string.checkout_error)));
                } else
                    lastname.setBackgroundColor(0);

                if(email.getText().length() < 7 ){
                    error = true;
                    email.setBackgroundColor(Color.parseColor(getString(R.string.checkout_error)));
                } else
                    email.setBackgroundColor(0);

                if(phone.getText().length() < 7 ){
                    error = true;
                    phone.setBackgroundColor(Color.parseColor(getString(R.string.checkout_error)));
                } else
                    phone.setBackgroundColor(0);

                if(address.getText().length() < 10 ){
                    error = true;
                    address.setBackgroundColor(Color.parseColor(getString(R.string.checkout_error)));
                } else
                    address.setBackgroundColor(0);

                if (!error) {
                    dbCart = new DbCart(CheckoutActivity.this);
                    db = dbCart.getWritableDatabase();
                    //db.execSQL("delete table "+ dbCart.TABLE_CART);

                    Cursor c;

                    GsonOrder data = new GsonOrder();

                    data.setFirstname(String.valueOf(firstname.getText()));
                    data.setLastname(String.valueOf(lastname.getText()));
                    data.setEmail(String.valueOf(email.getText()));
                    data.setTelephone(String.valueOf(phone.getText()));
                    data.setAddress(String.valueOf(address.getText()));

                    c = db.rawQuery("SELECT COUNT("+dbCart.KEY_ID+") as count_item FROM "+dbCart.TABLE_CART, new String[]{});
                    c.moveToFirst();
                    int ciColIndex = c.getColumnIndex("count_item");
                    int ci = c.getInt(ciColIndex);
                    c.close();


                    GsonProductInCart[] products = new GsonProductInCart[ci];
                    c = db.query(dbCart.TABLE_CART, null, null, null, null, null, null);

                    if (c.moveToFirst()) {
                        int prodIdColIndex = c.getColumnIndex(dbCart.KEY_PRODUCT_ID);
                        int nameColIndex = c.getColumnIndex(dbCart.KEY_NAME);
                        int priceColIndex = c.getColumnIndex(dbCart.KEY_PRICE);
                        int quantityColIndex = c.getColumnIndex(dbCart.KEY_QUANTITY);
                        int i = 0;
                        do {
                            products[i] = new GsonProductInCart();
                            products[i].setProduct_id(c.getInt(prodIdColIndex));
                            products[i].setName(c.getString(nameColIndex));
                            products[i].setPrice(c.getFloat(priceColIndex));
                            products[i].setQuantity(c.getInt(quantityColIndex));
                            i++;
                        } while (c.moveToNext());
                    }
                    c.close();

                    data.setProducts(products);
                    gson = new Gson();
                    json = gson.toJson(data);
                    //txt.setText(json);
                    new OcCheckout().execute(json);
                }
            }
        });
    }

    public class OcCheckout extends AsyncTask<String, Void, String> {
        TextView text;
        LinearLayout main_linear_products;

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String data = (String) arg0[0];

                String link = getString(R.string.domain) + "index.php?route=appapi/checkout&data=" + data;

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
            send.setText("Готово");
            send.setBackgroundColor(Color.parseColor("#44bb44"));
            db.delete(dbCart.TABLE_CART, null, null);
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            CheckoutActivity b = CheckoutActivity.this;
            startActivity(intent);
            finish();
            //super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            send.setText("Відправляється...");
            send.setClickable(false);
            super.onPreExecute();
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
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {

            Intent intent = new Intent(CheckoutActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
