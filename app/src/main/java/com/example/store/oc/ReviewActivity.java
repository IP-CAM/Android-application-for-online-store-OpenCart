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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ReviewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout review_body;
    TextView send_name;
    TextView send_text;
    RatingBar send_rating;
    TextView send_name_error;
    TextView send_text_error;
    TextView send_rating_error;
    Button send;
    String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
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
        product_id = CategoryIntent.getStringExtra("product_id");

        review_body = (LinearLayout) findViewById(R.id.body);

        send_name = findViewById(R.id.send_name);
        send_text = findViewById(R.id.send_text);
        send_rating = findViewById(R.id.send_rating);
        send_name_error = findViewById(R.id.send_name_error);
        send_text_error = findViewById(R.id.send_text_error);
        send_rating_error = findViewById(R.id.send_rating_error);

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean error = false;
                if(send_name.getText().length() < 2 ){
                    error = true;
                    send_name_error.setVisibility(View.VISIBLE);
                } else
                    send_name_error.setVisibility(View.GONE);

                if(send_text.getText().length() < 2 ){
                    error = true;
                    send_text_error.setVisibility(View.VISIBLE);
                } else
                    send_text_error.setVisibility(View.GONE);

                if(send_rating.getRating() < 1 ){
                    error = true;
                    send_rating_error.setVisibility(View.VISIBLE);
                } else
                    send_rating_error.setVisibility(View.GONE);

                if (!error) {
                    send.setBackgroundColor(Color.parseColor("#FFbbbbbb"));

                    GsonReview review = new GsonReview();

                    review.setAuthor(String.valueOf(send_name.getText()));
                    review.setText(String.valueOf(send_text.getText()));
                    review.setRating((int) send_rating.getRating());
                    review.setProduct_id(Integer.valueOf(product_id));

                    Gson gson = new Gson();
                    String json = gson.toJson(review);
                    new OcApiReview(ReviewActivity.this, "send").execute(json);
                } else
                    send.setBackgroundColor(Color.parseColor("#ffbb4444"));
            }
        });

        //LinearLayout main_linear_products = (LinearLayout) findViewById(R.id.main_linear_products);
        new OcApiReview(ReviewActivity.this, "get").execute(product_id);

    }

    public class OcApiReview extends AsyncTask<String, Void, String> {
        private Context context;
        String flag;


        public OcApiReview(Context context, String flag) {
            this.context = context;
            this.flag = flag;
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String data = (String) arg0[0];
                String link = null;
                if (flag.equals("get")) {
                    link = getString(R.string.domain) + "index.php?route=appapi/review&product_id=" + data;
                } else if(flag.equals("send")){
                    link = getString(R.string.domain) + "index.php?route=appapi/review/addreview&data=" + data;
                }
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

            if (flag.equals("get")) {
                Gson gson = new Gson();
                GsonReview[] reviews = gson.fromJson(result, GsonReview[].class);

                LayoutInflater ltInflater = getLayoutInflater();
                for (int i = 0; i < reviews.length; i++) {
                    View item = ltInflater.inflate(R.layout.review_item, review_body, false);
                    TextView name = item.findViewById(R.id.item_name);
                    TextView text = item.findViewById(R.id.item_text);
                    TextView date = item.findViewById(R.id.item_date);
                    LinearLayout rating = item.findViewById(R.id.rating);

                    name.setText(reviews[i].getAuthor());
                    text.setText(reviews[i].getText());
                    date.setText(reviews[i].getDate_added());

                    ImageView star;
                    for (int j = 1; j <= 5; j++) {
                        star = new ImageView(context);
                        if (j <= reviews[i].getRating()) {
                            star.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star1));
                        } else {
                            star.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_star2));
                        }
                        rating.addView(star, new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                    review_body.addView(item);
                }
            } else if(flag.equals("send")){
                send.setText("Відправлено");
                send.setBackgroundColor(Color.parseColor("#FF44bb44"));

                send_name.setText("");
                send_text.setText("");
                send_rating.setRating(0);
            }
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
            Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_category) {

            Intent intent = new Intent(ReviewActivity.this, CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(ReviewActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
