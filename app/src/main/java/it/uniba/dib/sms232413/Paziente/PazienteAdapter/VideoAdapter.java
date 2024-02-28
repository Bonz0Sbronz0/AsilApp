package it.uniba.dib.sms232413.Paziente.PazienteAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.YouTubeVideos;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<YouTubeVideos> videoList;
    private VideoClickListener clickListener;

    public VideoAdapter(ArrayList<YouTubeVideos> videoList) {
        this.videoList = videoList;
    }

    public void setClickListener(VideoClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        YouTubeVideos video = videoList.get(position);
        holder.videoTitle.setText(video.getVideoTitle());

        // Mostra l'icona del play e gestisci il clic
        holder.playIcon.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onVideoClick(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        ImageView playIcon;
        CardView videoCard;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoCard = itemView.findViewById(R.id.videoCard);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            playIcon = itemView.findViewById(R.id.playIcon);
        }
    }


    public interface VideoClickListener {
        void onVideoClick(YouTubeVideos video);
    }
}
