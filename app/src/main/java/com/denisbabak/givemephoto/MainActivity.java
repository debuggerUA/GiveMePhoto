package com.denisbabak.givemephoto;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denisbabak.givemephoto.net.api.VolleyInstance;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiBase;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    PhotoAdapter adapter;
    WallpaperMode mode = WallpaperMode.NATURE;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.photo_recyclerview);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        adapter = new PhotoAdapter(this, new WallpaperActionInterface() {
            @Override
            public void setWallpaper(final String url) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Would You like to set this wallpaper or just save it?");
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File c = new File(getCacheDir(), "cropped.png");
                        UCrop.of(Uri.parse(url), Uri.fromFile(c)).start(MainActivity.this);
                    }
                });
                builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DownloadManager dM = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        if(dM != null) {
                            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
                            dM.enqueue(downloadRequest);
                        }
                    }
                });
                builder.create().show();
            }
        });
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if(!prefs.contains("auth_info")) {
            authorize();
        } else {
            loadData(mode.toString());
        }

        adView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5806966430875275~9563399577");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    void authorize() {
        VKSdk.login(this);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = new File(resultUri.getSchemeSpecificPart());
                        FileInputStream fileInputStream = new FileInputStream(file);
                        WallpaperManager.getInstance(getApplicationContext()).setStream(fileInputStream);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        } else if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                prefs.edit().putString("auth_info", res.accessToken).commit();
                loadData(mode.toString());
            }
            @Override
            public void onError(VKError error) {
                Log.d("GiveMeWallpapers", error.toString());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void loadData(String albumId) {
        SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "https://api.vk.com/method/photos.get?" +
                "owner_id=-133010373&" +
                "album_id=" + albumId + "&" +
                "access_token=" + p.getString("auth_info", "") +
                "&v=5.60",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null) {
                    try {
                        int count = response.getJSONObject("response").getInt("count");
                        List<VKApiPhoto> result = new ArrayList<>(count);
                        for(int i = 0; i < count; i++) {
                            VKApiPhoto photo = new VKApiPhoto();
                            photo.parse(response.getJSONObject("response").getJSONArray("items").getJSONObject(i));
                            result.add(photo);
                        }
                        adapter.setData(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleyInstance.INSTANCE.getQueue(this).add(req);
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_art) {
            loadData(WallpaperMode.ART.toString());
        } else if (id == R.id.nav_nature) {
            loadData(WallpaperMode.NATURE.toString());
        } else if (id == R.id.nav_things) {
            loadData(WallpaperMode.THINGS.toString());
        } else if (id == R.id.nav_people) {
            loadData(WallpaperMode.PEOPLE.toString());
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
