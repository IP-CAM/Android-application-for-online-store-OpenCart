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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
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

public class CartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DbCart dbCart;
    SQLiteDatabase db;
    int count_item;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txt = findViewById(R.id.txt);
        TextView final_price = findViewById(R.id.final_price);
        TextView btn_order = findViewById(R.id.btn_order);
        LinearLayout main_linear_cart = findViewById(R.id.main_linear_cart);

        dbCart = new DbCart(this);
        db = dbCart.getWritableDatabase();
        Cursor c = db.query(dbCart.TABLE_CART, null, null, null, null, null, null);

        LayoutInflater ltInflater = getLayoutInflater();
        if (c.moveToFirst()) {
            txt.setVisibility(View.GONE);

            int idColIndex = c.getColumnIndex(dbCart.KEY_ID);
            int nameColIndex = c.getColumnIndex(dbCart.KEY_NAME);
            int imageColIndex = c.getColumnIndex(dbCart.KEY_IMAGE);
            int priceColIndex = c.getColumnIndex(dbCart.KEY_PRICE);
            int quantityColIndex = c.getColumnIndex(dbCart.KEY_QUANTITY);

            do {
                View item = ltInflater.inflate(R.layout.cart_item, main_linear_cart, false);
                TextView name = item.findViewById(R.id.prod_name);
                TextView quantity = item.findViewById(R.id.prod_quantity);
                TextView price = item.findViewById(R.id.prod_price);

                ImageView prod_img = item.findViewById(R.id.prod_img);
                Button btn_dec = item.findViewById(R.id.btn_dec);
                Button btn_inc = item.findViewById(R.id.btn_inc);
                Button prod_drop = item.findViewById(R.id.prod_drop);

                new DownloadImageTask(prod_img)
                        .execute(c.getString(imageColIndex));
                name.setText(Html.fromHtml(c.getString(nameColIndex)));
                quantity.setText(Html.fromHtml(c.getString(quantityColIndex)));
                price.setText(String.format("%.2f", c.getFloat(priceColIndex)*c.getInt(quantityColIndex)) + " грн.");
                main_linear_cart.addView(item);

                prod_drop.setOnClickListener(new DropOnClickListener(item, c.getString(idColIndex), final_price));
                btn_inc.setOnClickListener(new MyOnClickListener(btn_inc, c.getString(idColIndex),
                        quantity, c.getInt(quantityColIndex), price, c.getFloat(priceColIndex), final_price));
                btn_dec.setOnClickListener(new MyOnClickListener(btn_dec, c.getString(idColIndex),
                        quantity, c.getInt(quantityColIndex), price, c.getFloat(priceColIndex), final_price));
            } while (c.moveToNext());
        } else
            txt.setText(R.string.cart_empty);

        c = db.rawQuery("SELECT SUM("+dbCart.KEY_QUANTITY+"*"+DbCart.KEY_PRICE+") as final_sum " +
                "FROM "+dbCart.TABLE_CART, new String[]{});
        c.moveToFirst();
        int fsColIndex = c.getColumnIndex("final_sum");
        float fs = c.getFloat(fsColIndex);
        final_price.setText(String.format("%.2f", fs) + " грн.");
        c.close();

        c = db.rawQuery("SELECT COUNT("+dbCart.KEY_ID+") as count_item FROM "+dbCart.TABLE_CART, new String[]{});
        c.moveToFirst();
        int ciColIndex = c.getColumnIndex("count_item");
        count_item = c.getInt(ciColIndex);
        c.close();

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_item > 0) {
                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                    startActivity(intent);
                } else {
                    txt.setTextColor(Color.parseColor("#ffbb4444"));
                }
            }
        });

    }
    public class MyOnClickListener implements View.OnClickListener
    {

        Button btn;
        String id;
        TextView quantity_pol;
        int quantity;
        TextView price_pole;
        float price;
        TextView final_price;
        DbCart dbCart;
        SQLiteDatabase db;
        ContentValues cv;
        Cursor c;

        public MyOnClickListener(Button btn, String id, TextView quantity_pol, int quantity,
                                 TextView price_pole, float price, TextView final_price) {
            this.btn = btn;
            this.id = id;
            this.quantity_pol = quantity_pol;
            this.quantity = quantity;
            this.price_pole = price_pole;
            this.price = price;
            this.final_price = final_price;

            dbCart = new DbCart(CartActivity.this);
            db = dbCart.getWritableDatabase();
            cv = new ContentValues();
        }

        @Override
        public void onClick(View v)
        {
            c = db.rawQuery("SELECT * FROM "+dbCart.TABLE_CART+" WHERE "+dbCart.KEY_ID+" = ?", new String[]{id});
            c.moveToFirst();
            int quantityColIndex = c.getColumnIndex(dbCart.KEY_QUANTITY);
            quantity = c.getInt(quantityColIndex);

            if (v.getId() == R.id.btn_inc) {
                quantity++;
            } else if (v.getId() == R.id.btn_dec){
                if (quantity > 1) quantity--;
            }

            cv.put(DbCart.KEY_QUANTITY, quantity);
            db.update(DbCart.TABLE_CART, cv, dbCart.KEY_ID + " = ?",
                    new String[] { id });

            quantity_pol.setText(String.valueOf(quantity));
            price_pole.setText(String.format("%.2f", Float.valueOf(price * quantity)) + " грн.");
            c = db.rawQuery("SELECT SUM("+dbCart.KEY_QUANTITY+"*"+dbCart.KEY_PRICE+") as final_sum " +
                    "FROM "+dbCart.TABLE_CART, new String[]{});
            c.moveToFirst();
            int fsColIndex = c.getColumnIndex("final_sum");
            float fs = c.getFloat(fsColIndex);
            final_price.setText(String.format("%.2f", fs) + " грн.");
        }

    }
    public class DropOnClickListener implements View.OnClickListener{
        View v;
        String id;
        DbCart dbCart;
        SQLiteDatabase db;
        ContentValues cv;
        Cursor c;
        TextView final_price;

        public DropOnClickListener(View v, String id, TextView final_price) {
            this.v = v;
            this.id = id;
            this.final_price = final_price;

            dbCart = new DbCart(CartActivity.this);
            db = dbCart.getWritableDatabase();
            cv = new ContentValues();
        }

        @Override
        public void onClick(View view) {
            v.setVisibility(View.GONE);
            /*int delCount =*/ db.delete(dbCart.TABLE_CART, dbCart.KEY_ID+" = " + id, null);

            c = db.rawQuery("SELECT SUM("+dbCart.KEY_QUANTITY+"*"+dbCart.KEY_PRICE+") as final_sum " +
                    "FROM "+dbCart.TABLE_CART, new String[]{});
            c.moveToFirst();
            int fsColIndex = c.getColumnIndex("final_sum");
            float fs = c.getFloat(fsColIndex);
            final_price.setText(String.valueOf(fs) + " грн.");

            c = db.rawQuery("SELECT COUNT("+dbCart.KEY_ID+") as count_item " +
                    "FROM "+dbCart.TABLE_CART, new String[]{});
            c.moveToFirst();
            int ciColIndex = c.getColumnIndex("count_item");
            count_item = c.getInt(ciColIndex);
            if(count_item < 1){
                txt.setVisibility(View.VISIBLE);
            }
            c.close();
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
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {

            Intent intent = new Intent(CartActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(CartActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
