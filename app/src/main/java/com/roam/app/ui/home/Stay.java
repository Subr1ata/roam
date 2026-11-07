package com.roam.app.ui.home;

public final class Stay {
    private final String id;
    private final String name;
    private final String location;
    private final String category;
    private final String description;
    private final String imageUrl;
    private final double rating;
    private final int reviewCount;
    private final int nightlyPrice;

    public Stay(
            String id,
            String name,
            String location,
            String category,
            String description,
            String imageUrl,
            double rating,
            int reviewCount,
            int nightlyPrice
    ) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.nightlyPrice = nightlyPrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getNightlyPrice() {
        return nightlyPrice;
    }

    public boolean matches(String query, String selectedCategory) {
        if (!"All Stays".equals(selectedCategory)
                && !category.equalsIgnoreCase(selectedCategory)) {
            return false;
        }
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String searchable = (name + " " + location + " " + category + " " + description)
                .toLowerCase(java.util.Locale.ROOT);
        String[] terms = query.trim().toLowerCase(java.util.Locale.ROOT).split("\\s+");
        for (String term : terms) {
            if (!searchable.contains(term)) {
                return false;
            }
        }
        return true;
    }
}
