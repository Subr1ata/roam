package com.roam.app.ui.onboarding;

import android.content.Context;

import com.roam.app.R;

import java.util.ArrayList;
import java.util.List;

public final class OnboardingSlide {
    private final String id;
    private final String location;
    private final String title;
    private final String description;
    private final String imageUrl;

    private OnboardingSlide(String id, String title, String description, String imageUrl, String location) {
        this.id = id;
        this.location = location;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static List<OnboardingSlide> load(Context context) {
        String[] ids = context.getResources().getStringArray(R.array.onboarding_slide_ids);
        String[] titles = context.getResources().getStringArray(R.array.onboarding_slide_titles);
        String[] descriptions = context.getResources().getStringArray(R.array.onboarding_slide_bodies);
        String[] imageUrls = context.getResources().getStringArray(R.array.onboarding_slide_images);
        String[] locations = context.getResources().getStringArray(R.array.onboarding_slide_locations);

        if (ids.length == 0
                || ids.length != titles.length
                || ids.length != descriptions.length
                || ids.length != imageUrls.length
                || ids.length != locations.length) {
            throw new IllegalStateException("Onboarding slide resource arrays must be non-empty and the same length.");
        }

        List<OnboardingSlide> slides = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            slides.add(new OnboardingSlide(ids[i], titles[i], descriptions[i], imageUrls[i], locations[i]));
        }
        return slides;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
