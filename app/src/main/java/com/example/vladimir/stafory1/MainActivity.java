package com.example.vladimir.stafory1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    public static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Catalog catalog;
    Messages messages;
    NewVacItem newVacItem;
    Candidate candidate;
    SharedPreferences email;
    static NewVac newvac;
    static VacView vacView;
    static MessageView messageView;
    static RecView recView;
    static CanView canView;

    private View mProgressView;
    private View linearMain;
    private View linearStart;
    private Toolbar toolbar;

    static final StaforyConnection staforyConnection = new StaforyConnection();
    static Document html;
    static Document html1;
    static Document html2;
    static Document html3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        long start = System.currentTimeMillis();

        String saved_email = getDefaults("email", MainActivity.this);
        System.out.println(saved_email+"SAVEDEMAIL");

        if(saved_email == "") {
            Long end = System.currentTimeMillis()-start;
            System.out.println("Время проверки: " + end);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {

            catalog = new Catalog();
            messages = new Messages();
            newVacItem = new NewVacItem();
            candidate = new Candidate();
            newvac = new NewVac();
            vacView = new VacView();
            messageView = new MessageView();
            recView = new RecView();
            canView = new CanView();

            setContentView(R.layout.activity_main);
            mProgressView = findViewById(R.id.login_progress1);
            linearMain = findViewById(R.id.lineaMain);
            linearStart = findViewById(R.id.action_start);
            showProgress(true);
            LoadContent loadContent = new LoadContent("https://stafory.com/login.html");
            loadContent.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fragmentTransaction = getFragmentManager().beginTransaction();
        int id = item.getItemId();

        if (id == R.id.new_vac) {
            fragmentTransaction.replace(R.id.frameLayout, newVacItem);
        } else if (id == R.id.catalog) {
            fragmentTransaction.replace(R.id.frameLayout, catalog);
        } else if (id == R.id.mesages) {
            fragmentTransaction.replace(R.id.frameLayout, messages);
        } else if (id == R.id.candidate) {
            fragmentTransaction.replace(R.id.frameLayout, candidate);
        } else if (id == R.id.exit) {

            LoginActivity.setDefaults("email", "", MainActivity.this);
            LoginActivity.setDefaults("pass", "", MainActivity.this);
            LoginActivity.setDefaults("status", "", MainActivity.this);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            linearMain.setVisibility(show ? View.GONE : View.VISIBLE);
            linearStart.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearMain.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            linearStart.setVisibility(show ? View.VISIBLE : View.GONE);
            linearStart.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearStart.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            linearStart.setVisibility(show ? View.VISIBLE : View.GONE);
            linearMain.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public class LoadContent extends AsyncTask<String, Void, String> {

        private final String url;

        LoadContent(String _url) {
            url = _url;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                MainActivity.staforyConnection.getInputStream2(url,
                        getDefaults("email", MainActivity.this), getDefaults("pass", MainActivity.this));
                StaforyConnection.runparse();
                StaforyConnection.runparseall();
            } catch (KeyManagementException e) {
                e.printStackTrace();
                return "";
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return "succes";
        }

        @Override
        protected void onPostExecute(String  succes) {
            showProgress(false);

            if (succes.contains("succes")) {
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setVisibility(View.VISIBLE);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        MainActivity.this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MainActivity.this);
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy =
                            new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.frameLayout, newvac);
                fragmentTransaction.commit();
            } else {
                System.out.println("NOTTTTTT!!!");
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
