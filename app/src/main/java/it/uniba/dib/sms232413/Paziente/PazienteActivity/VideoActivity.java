package it.uniba.dib.sms232413.Paziente.PazienteActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232413.Paziente.PazienteAdapter.VideoAdapter;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.YouTubeVideos;

public class VideoActivity extends AppCompatActivity implements VideoAdapter.VideoClickListener {

    private WebView videoWebView;
    private RecyclerView recyclerView;
    private boolean webViewVisible = false;
    private Animation slideInRightAnimation;
    private Animation slideOutRightAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        slideInRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        videoWebView = findViewById(R.id.videoWebView);
        videoWebView.getSettings().setJavaScriptEnabled(true); // Abilita JavaScript nel WebView
        videoWebView.setVisibility(View.GONE); // Nascondi il WebView all'inizio

        recyclerView = findViewById(R.id.recyclerViewVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        VideoAdapter videoAdapter = new VideoAdapter(getVideoList());
        videoAdapter.setClickListener(this); // Imposta il listener per i click sugli elementi del videoAdapter
        recyclerView.setAdapter(videoAdapter);
    }

    // Metodo per ottenere una lista di video di esempio (puoi sostituirla con la tua lista di video)
    private ArrayList<YouTubeVideos> getVideoList() {
        ArrayList<YouTubeVideos> videoList = new ArrayList<>();
        videoList.add(new YouTubeVideos("https://youtu.be/7BGHxCuWjfc?si=mQYTbXRlfbvPGxmm", "Asilo Politico e Protezione Internazionale"));
        videoList.add(new YouTubeVideos("https://www.youtube.com/watch?v=7ioQBkSb454", "Chi sono i richiedenti di asilo?"));
        videoList.add(new YouTubeVideos("https://www.youtube.com/watch?v=puCj5Blij5k", "Dove vanno i rifugiati?"));
        videoList.add(new YouTubeVideos("https://www.youtube.com/watch?v=1oXxJGR3cd8", "Diritti dei rifugiati"));
        // Aggiungi altri video come necessario
        return videoList;
    }

    @Override
    public void onVideoClick(YouTubeVideos video) {
        // Quando un video viene cliccato, apri l'URL del video nel WebView
        if (video != null && video.getVideoUrl() != null) {
            videoWebView.setWebViewClient(new WebViewClient());
            videoWebView.loadUrl(video.getVideoUrl());
            slideInLeft();
            webViewVisible = true;
        } else {
            Toast.makeText(this, "URL del video non valido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webViewVisible) {
            slideOutRight();
            webViewVisible = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void slideInLeft() {
        videoWebView.setVisibility(View.VISIBLE);
        videoWebView.startAnimation(slideInRightAnimation);
    }

    private void slideOutRight() {
        slideOutRightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                videoWebView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        videoWebView.startAnimation(slideOutRightAnimation);
    }
}