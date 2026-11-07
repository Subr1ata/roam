package com.roam.app.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class HomeTabsView extends LinearLayout {
    public interface Listener {
        void onSignOut();
    }

    private enum Tab {
        HOME,
        SEARCH,
        TRIPS,
        PROFILE
    }

    private static final int INK = Color.rgb(17, 19, 24);
    private static final int MUTED = Color.rgb(119, 123, 137);
    private static final int PAGE = Color.rgb(250, 250, 251);
    private static final int LAVENDER = Color.rgb(196, 170, 255);
    private static final int GOLD = Color.rgb(255, 181, 0);
    private static final List<String> CATEGORIES = Arrays.asList(
            "All Stays", "Cabins", "Beach", "City", "Villas"
    );

    private final Listener listener;
    private final FrameLayout screenContent;
    private final LinearLayout navigation;
    private final List<TextView> navigationItems = new ArrayList<>();
    private final List<TextView> categoryChipViews = new ArrayList<>();
    private Tab selectedTab = Tab.HOME;
    private String selectedCategory = "All Stays";
    private String searchQuery = "";
    private LinearLayout searchResults;
    private TextView searchResultCount;

    public HomeTabsView(@NonNull Context context, @NonNull Listener listener) {
        super(context);
        this.listener = listener;
        setOrientation(VERTICAL);
        setBackgroundColor(PAGE);

        screenContent = new FrameLayout(context);
        addView(screenContent, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0, 1f));

        navigation = new LinearLayout(context);
        navigation.setGravity(Gravity.CENTER);
        navigation.setOrientation(HORIZONTAL);
        navigation.setPadding(dp(12), dp(8), dp(12), dp(10));
        navigation.setBackgroundColor(Color.WHITE);
        addView(navigation, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(76)));

        addNavigationItem("⌂", "Home", Tab.HOME);
        addNavigationItem("⌕", "Search", Tab.SEARCH);
        addNavigationItem("▣", "Trips", Tab.TRIPS);
        addNavigationItem("♙", "Profile", Tab.PROFILE);

        selectTab(Tab.HOME, false);
    }

    private void addNavigationItem(String icon, String label, Tab tab) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(8), dp(6), dp(8), dp(5));
        item.setBackground(round(Color.TRANSPARENT, Color.TRANSPARENT, dp(26), 0));

        TextView iconView = text(icon, 23, INK);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView);

        TextView labelView = text(label, 12, INK);
        labelView.setGravity(Gravity.CENTER);
        item.addView(labelView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(18)));

        navigation.addView(item, new LinearLayout.LayoutParams(0, dp(60), 1f));
        navigationItems.add(labelView);
        item.setOnClickListener(view -> selectTab(tab, true));
    }

    private void selectTab(Tab tab, boolean animate) {
        selectedTab = tab;
        Tab[] tabs = {Tab.HOME, Tab.SEARCH, Tab.TRIPS, Tab.PROFILE};
        for (int i = 0; i < navigationItems.size(); i++) {
            boolean selected = tabs[i] == tab;
            TextView label = navigationItems.get(i);
            label.setTextColor(selected ? INK : MUTED);
            LinearLayout item = (LinearLayout) label.getParent();
            item.getChildAt(0).setAlpha(selected ? 1f : 0.58f);
            item.setBackground(round(
                    selected ? GOLD : Color.TRANSPARENT,
                    Color.TRANSPARENT,
                    dp(26),
                    0
            ));
            if (animate) {
                item.setScaleX(0.96f);
                item.setScaleY(0.96f);
                item.animate().scaleX(1f).scaleY(1f).setDuration(180)
                        .setInterpolator(new DecelerateInterpolator()).start();
            }
        }

        Runnable replaceScreen = () -> {
            screenContent.removeAllViews();
            View next = buildScreen(tab);
            screenContent.addView(next, new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if (animate) {
                next.setAlpha(0f);
                next.setTranslationY(dp(6));
                next.animate().alpha(1f).translationY(0f).setDuration(200)
                        .setInterpolator(new DecelerateInterpolator()).start();
            }
        };

        View current = screenContent.getChildAt(0);
        if (animate && current != null) {
            current.animate().alpha(0f).translationY(-dp(4)).setDuration(100)
                    .withEndAction(replaceScreen)
                    .start();
        } else {
            replaceScreen.run();
        }
    }

    private View buildScreen(Tab tab) {
        switch (tab) {
            case HOME:
                return buildHome();
            case SEARCH:
                return buildSearch();
            case TRIPS:
                return buildTrips();
            case PROFILE:
                return buildProfile();
            default:
                throw new IllegalStateException("Unsupported app tab: " + tab);
        }
    }

    private View buildHome() {
        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(PAGE);

        LinearLayout page = pageColumn();
        page.setPadding(dp(22), dp(14), dp(22), dp(24));
        scroll.addView(page);

        LinearLayout top = new LinearLayout(getContext());
        top.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(top, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(58)));
        TextView wordmark = text("roam", 36, INK);
        wordmark.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
        top.addView(wordmark, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        TextView avatar = text("J", 18, INK);
        avatar.setGravity(Gravity.CENTER);
        avatar.setTypeface(Typeface.DEFAULT_BOLD);
        avatar.setBackground(round(Color.rgb(255, 223, 162), Color.TRANSPARENT, dp(26), 0));
        top.addView(avatar, new LinearLayout.LayoutParams(dp(50), dp(50)));
        avatar.setOnClickListener(view -> selectTab(Tab.PROFILE, true));

        addText(page, "Hey, Joanna 👋", 17, MUTED, true, 0, 10);
        TextView heading = text("Find your\nnext stay", 34, INK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        heading.setLineSpacing(0, 0.95f);
        page.addView(heading, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 18));

        addHomeSearch(page);
        addSectionHeading(page, "Find your kind of stay", "See all", () -> {
            selectedCategory = "All Stays";
            searchQuery = "";
            selectTab(Tab.SEARCH, true);
        });
        addCategoryChips(page, false);
        addSectionHeading(page, "Stays worth the trip", "Explore all", () -> {
            selectedCategory = "All Stays";
            searchQuery = "";
            selectTab(Tab.SEARCH, true);
        });

        int index = 0;
        for (Stay stay : StayRepository.getStays()) {
            if (stay.matches("", selectedCategory)) {
                View card = createStayCard(stay, false);
                page.addView(card, marginParams(LayoutParams.MATCH_PARENT,
                        dp(286), 0, 0, 0, 18));
                animateCard(card, index++);
            }
        }
        return scroll;
    }

    private void addHomeSearch(LinearLayout page) {
        FrameLayout search = new FrameLayout(getContext());
        search.setBackground(round(LAVENDER, Color.TRANSPARENT, dp(25), 0));
        page.addView(search, marginParams(LayoutParams.MATCH_PARENT, dp(62), 0, 0, 0, 20));

        TextView input = text("", 15, INK);
        input.setGravity(Gravity.CENTER_VERTICAL);
        input.setHintTextColor(Color.rgb(74, 58, 108));
        input.setHint("✦  quiet pet friendly cabin with hot tub");
        input.setPadding(dp(18), 0, dp(62), 0);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setSingleLine(true);
        search.addView(input, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TextView submit = text("↗", 25, INK);
        submit.setGravity(Gravity.CENTER);
        submit.setBackground(round(Color.WHITE, Color.TRANSPARENT, dp(23), 0));
        FrameLayout.LayoutParams submitParams = new FrameLayout.LayoutParams(dp(46), dp(46));
        submitParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        submitParams.rightMargin = dp(8);
        search.addView(submit, submitParams);

        Runnable openSearch = () -> {
            searchQuery = input.getText().toString().trim();
            selectedCategory = "All Stays";
            openSearch(true);
        };
        input.setOnClickListener(view -> openSearch.run());
        submit.setOnClickListener(view -> openSearch.run());
    }

    private void openSearch(boolean focusInput) {
        boolean searchAlreadyVisible = selectedTab == Tab.SEARCH
                && screenContent.findViewWithTag("roam-search-input") != null;
        if (!searchAlreadyVisible) selectTab(Tab.SEARCH, true);
        if (focusInput) {
            screenContent.postDelayed(() -> {
                View searchInput = screenContent.findViewWithTag("roam-search-input");
                if (searchInput instanceof EditText) {
                    EditText editText = (EditText) searchInput;
                    if (!editText.getText().toString().equals(searchQuery)) {
                        editText.setText(searchQuery);
                        editText.setSelection(editText.length());
                    }
                    searchInput.requestFocus();
                    InputMethodManager keyboard = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
                }
            }, searchAlreadyVisible ? 0 : 150);
        }
    }

    private View buildSearch() {
        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout page = pageColumn();
        page.setPadding(dp(22), dp(18), dp(22), dp(24));
        scroll.addView(page);

        TextView heading = text("Search stays", 30, INK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        page.addView(heading, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 16));

        FrameLayout searchBox = new FrameLayout(getContext());
        searchBox.setBackground(round(Color.WHITE, Color.rgb(228, 229, 234), dp(18), dp(1)));
        page.addView(searchBox, marginParams(LayoutParams.MATCH_PARENT, dp(58), 0, 0, 0, 16));

        EditText input = new EditText(getContext());
        input.setTag("roam-search-input");
        input.setSingleLine(true);
        input.setTextSize(15);
        input.setTextColor(INK);
        input.setHintTextColor(MUTED);
        input.setHint("Try “pet friendly cabin”");
        input.setPadding(dp(16), 0, dp(16), 0);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.addView(input, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (!searchQuery.isEmpty()) {
            input.setText(searchQuery);
            input.setSelection(input.length());
        }

        addCategoryChips(page, true);
        searchResultCount = text("", 14, MUTED);
        page.addView(searchResultCount, marginParams(LayoutParams.MATCH_PARENT,
                dp(34), 0, 14, 0, 6));
        searchResults = new LinearLayout(getContext());
        searchResults.setOrientation(VERTICAL);
        page.addView(searchResults, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        updateSearchResults();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                updateSearchResults();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        input.setOnEditorActionListener((view, actionId, event) -> {
            searchQuery = input.getText().toString().trim();
            updateSearchResults();
            return true;
        });
        return scroll;
    }

    private void addCategoryChips(LinearLayout parent, boolean search) {
        categoryChipViews.clear();
        HorizontalScrollView horizontal = new HorizontalScrollView(getContext());
        horizontal.setHorizontalScrollBarEnabled(false);
        parent.addView(horizontal, marginParams(LayoutParams.MATCH_PARENT,
                dp(48), 0, 0, 0, search ? 2 : 12));

        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        horizontal.addView(row);

        String[] icons = {"⌂", "♣", "≈", "▦", "♧"};
        for (int i = 0; i < CATEGORIES.size(); i++) {
            String category = CATEGORIES.get(i);
            TextView chip = text(icons[i] + "  " + category, 14, INK);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(16), 0, dp(16), 0);
            boolean selected = category.equals(selectedCategory);
            chip.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            chip.setBackground(round(selected ? GOLD : Color.rgb(241, 242, 244),
                    Color.TRANSPARENT, dp(24), 0));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, dp(42));
            params.rightMargin = dp(8);
            row.addView(chip, params);
            categoryChipViews.add(chip);
            chip.setOnClickListener(view -> {
                selectedCategory = category;
                if (search) {
                    refreshCategoryChips();
                    updateSearchResults();
                } else {
                    selectTab(Tab.HOME, true);
                }
            });
        }
    }

    private void refreshCategoryChips() {
        for (int i = 0; i < categoryChipViews.size(); i++) {
            TextView chip = categoryChipViews.get(i);
            chip.setBackground(round(
                    CATEGORIES.get(i).equals(selectedCategory)
                            ? GOLD : Color.rgb(241, 242, 244),
                    Color.TRANSPARENT,
                    dp(24),
                    0
            ));
        }
    }

    private void updateSearchResults() {
        if (searchResults == null || searchResultCount == null) return;
        searchResults.removeAllViews();
        List<Stay> matches = new ArrayList<>();
        for (Stay stay : StayRepository.getStays()) {
            if (stay.matches(searchQuery, selectedCategory)) matches.add(stay);
        }
        searchResultCount.setText(matches.size() + (matches.size() == 1 ? " stay" : " stays")
                + (searchQuery.isEmpty() ? " to explore" : " for “" + searchQuery + "”"));

        if (matches.isEmpty()) {
            TextView empty = text("No stays match that search. Try a different place or phrase.",
                    16, MUTED);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(20), dp(28), dp(20), dp(28));
            searchResults.addView(empty);
            return;
        }
        for (Stay stay : matches) {
            searchResults.addView(createStayCard(stay, true),
                    marginParams(LayoutParams.MATCH_PARENT, dp(254), 0, 0, 0, 16));
        }
    }

    private View createStayCard(Stay stay, boolean compact) {
        FrameLayout card = new FrameLayout(getContext());
        card.setBackground(round(Color.rgb(220, 220, 224), Color.TRANSPARENT, dp(22), 0));
        card.setClipToOutline(true);
        card.setClickable(true);
        card.setFocusable(true);
        card.setContentDescription(stay.getName() + ", " + stay.getLocation()
                + ", $" + stay.getNightlyPrice() + " per night");

        ImageView image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackgroundColor(Color.rgb(220, 220, 224));
        card.addView(image, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Glide.with(this)
                .load(stay.getImageUrl())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(240))
                .into(image);

        View gradient = new View(getContext());
        GradientDrawable scrim = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x00000000, 0x12000000, 0xB8000000, 0xF0000000}
        );
        gradient.setBackground(scrim);
        card.addView(gradient, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TextView rating = text(String.format(Locale.US, "★ %.1f", stay.getRating()), 14, INK);
        rating.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        rating.setGravity(Gravity.CENTER);
        rating.setPadding(dp(12), 0, dp(12), 0);
        rating.setBackground(round(Color.WHITE, Color.TRANSPARENT, dp(20), 0));
        FrameLayout.LayoutParams ratingParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, dp(38), Gravity.TOP | Gravity.END);
        ratingParams.setMargins(0, dp(12), dp(12), 0);
        card.addView(rating, ratingParams);

        LinearLayout details = new LinearLayout(getContext());
        details.setOrientation(VERTICAL);
        details.setPadding(dp(17), 0, dp(17), dp(17));
        FrameLayout.LayoutParams detailsParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.START);
        card.addView(details, detailsParams);

        TextView location = text("●  " + stay.getLocation(), 13, Color.WHITE);
        details.addView(location, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        TextView title = text(stay.getName(), compact ? 20 : 22, Color.WHITE);
        title.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        title.setMaxLines(2);
        title.setPadding(0, dp(4), 0, dp(5));
        details.addView(title);
        TextView price = text("$" + stay.getNightlyPrice()
                + " night  ·  " + stay.getReviewCount() + " reviews", 13,
                Color.rgb(242, 242, 244));
        details.addView(price);
        card.setOnClickListener(view -> {
            searchQuery = stay.getName();
            selectedCategory = "All Stays";
            openSearch(true);
        });
        return card;
    }

    private View buildTrips() {
        LinearLayout page = centeredPage();
        addText(page, "Your trips", 30, INK, true, 0, 10);
        addText(page, "Your next getaway will feel right at home.",
                16, MUTED, false, 24, 16);
        TextView browse = actionButton("Explore stays");
        page.addView(browse, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(54)));
        browse.setOnClickListener(view -> {
            selectedCategory = "All Stays";
            searchQuery = "";
            selectTab(Tab.HOME, true);
        });
        return page;
    }

    private View buildProfile() {
        ScrollView scroll = new ScrollView(getContext());
        LinearLayout page = pageColumn();
        page.setPadding(dp(24), dp(24), dp(24), dp(24));
        scroll.addView(page);

        TextView heading = text("Your profile", 30, INK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        page.addView(heading, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 24));
        TextView avatar = text("J", 32, INK);
        avatar.setGravity(Gravity.CENTER);
        avatar.setTypeface(Typeface.DEFAULT_BOLD);
        avatar.setBackground(round(Color.rgb(255, 223, 162), Color.TRANSPARENT, dp(48), 0));
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dp(86), dp(86));
        avatarParams.bottomMargin = dp(14);
        page.addView(avatar, avatarParams);
        addText(page, "Joanna", 22, INK, true, 0, 4);
        addText(page, "Your Roam account", 15, MUTED, false, 0, 22);

        LinearLayout session = new LinearLayout(getContext());
        session.setOrientation(VERTICAL);
        session.setPadding(dp(18), dp(16), dp(18), dp(16));
        session.setBackground(round(Color.WHITE, Color.rgb(233, 234, 237), dp(18), dp(1)));
        page.addView(session, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 20));
        addText(session, "Account session", 15, INK, true, 0, 5);
        addText(session, "Protected by Clerk", 14, MUTED, false, 0, 0);

        TextView signOut = text("Sign out", 16, Color.rgb(163, 55, 49));
        signOut.setGravity(Gravity.CENTER);
        signOut.setBackground(round(Color.WHITE, Color.rgb(235, 218, 216), dp(16), dp(1)));
        page.addView(signOut, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(54)));
        signOut.setOnClickListener(view -> listener.onSignOut());
        return scroll;
    }

    private LinearLayout centeredPage() {
        LinearLayout page = pageColumn();
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(26), dp(24), dp(26), dp(24));
        return page;
    }

    private LinearLayout pageColumn() {
        LinearLayout page = new LinearLayout(getContext());
        page.setOrientation(VERTICAL);
        page.setBackgroundColor(PAGE);
        return page;
    }

    private void addSectionHeading(LinearLayout parent, String title, String action,
                                  Runnable onClick) {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        parent.addView(row, marginParams(LayoutParams.MATCH_PARENT,
                dp(42), 0, 2, 0, 4));
        TextView heading = text(title, 18, INK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        row.addView(heading, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        TextView link = text(action + "  ›", 13, MUTED);
        row.addView(link);
        row.setOnClickListener(view -> onClick.run());
    }

    private void addText(LinearLayout parent, String value, int size, int color,
                         boolean bold, int top, int bottom) {
        TextView text = text(value, size, color);
        if (bold) text.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        parent.addView(text, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, top, 0, bottom));
    }

    private TextView actionButton(String label) {
        TextView button = text(label, 16, Color.WHITE);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setBackground(round(INK, Color.TRANSPARENT, dp(18), 0));
        return button;
    }

    private TextView text(String value, int sizeSp, int color) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        view.setTextColor(color);
        return view;
    }

    private GradientDrawable round(int color, int strokeColor, int radius, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private LinearLayout.LayoutParams marginParams(int width, int height,
                                                   int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return params;
    }

    private void animateCard(View card, int index) {
        card.setAlpha(0f);
        card.setTranslationY(dp(12));
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 45L)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
