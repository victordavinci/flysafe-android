package ar.gob.jiaac.flysafe.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import ar.gob.jiaac.flysafe.R;


public class StatsFragment extends Fragment {

    public StatsFragment() {
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    class JSInterface {
        int modified = 0;
        String data;

        JSInterface() {
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public int getModified() {
            return modified;
        }

        void setData(String data) {
            this.data = data;
            modified++;
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public String getData() {
            return data;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        final JSInterface jsi = new JSInterface();
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object o = dataSnapshot.getValue(Object.class);
                String json = new Gson().toJson(o);
                jsi.setData(json);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference("stats");
        statsRef.addListenerForSingleValueEvent(vel);
        statsRef.addValueEventListener(vel);

        WebView wv = v.findViewById(R.id.stats_webview);
        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setAllowFileAccess(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setBlockNetworkLoads(true);
        s.setBlockNetworkImage(true);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.addJavascriptInterface(jsi, "Android");
        wv.loadUrl("file:///android_asset/stats.html");
        return v;
    }

}
