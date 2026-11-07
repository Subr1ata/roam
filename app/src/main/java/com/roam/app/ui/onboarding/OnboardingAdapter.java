package com.roam.app.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roam.app.R;

import java.util.List;

public final class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideHolder> {
    private final List<OnboardingSlide> slides;

    public OnboardingAdapter(List<OnboardingSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideHolder holder, int position) {
        OnboardingSlide slide = slides.get(position);
        holder.location.setText(slide.getLocation());
        Glide.with(holder.image)
                .load(slide.getImageUrl())
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static final class SlideHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView location;

        SlideHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.slideImage);
            location = itemView.findViewById(R.id.slideLocation);
        }
    }
}
