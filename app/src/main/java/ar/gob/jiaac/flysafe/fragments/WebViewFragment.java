package ar.gob.jiaac.flysafe.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ar.gob.jiaac.flysafe.R;

public class WebViewFragment extends Fragment {
    private static final String ARG_URL = "url";

    private String mURL;

    public WebViewFragment() {
    }

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL= getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);
        WebView wv = v.findViewById(R.id.webview);
        WebSettings s = wv.getSettings();
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String host = Uri.parse(url).getHost();
                if (host.equals("twitter.com") || host.equals("www.twitter.com") || host.equals("mobile.twitter.com")) {
                    view.loadUrl(url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });
        s.setJavaScriptEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setDomStorageEnabled(true);
        s.setLoadsImagesAutomatically(true);
        wv.loadUrl(mURL != null ? mURL : "https://jiaac.gob.ar");
        return v;
    }

}
