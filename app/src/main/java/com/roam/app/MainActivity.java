package com.roam.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.roam.app.ui.onboarding.OnboardingAdapter;
import com.roam.app.ui.onboarding.OnboardingSlide;
import com.roam.app.ui.onboarding.SwipeableOnboardingSheet;
import com.roam.app.ui.auth.AuthFlowView;
import com.roam.app.ui.home.HomeTabsView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ROAM_GREEN = Color.rgb(49, 91, 69);
    private static final int MUTED_DOT = Color.rgb(205, 207, 195);

    private List<OnboardingSlide> slides;
    private View onboardingContainer;
    private android.widget.FrameLayout authContainer;
    private View signedInContainer;
    private ViewPager2 onboardingPager;
    private SwipeableOnboardingSheet onboardingSheet;
    private LinearLayout pageDots;
    private TextView slideTitle;
    private TextView slideDescription;
    private TextView authStatusTextView;
    private Button getStartedButton;
    private Button continueSignInButton;
    private HomeTabsView homeTabsView;
    private boolean clerkInitialized;
    private boolean signedIn;
    private AuthFlowView authFlowView;
    private AuthFlowView.Screen authScreen;
    private String pendingVerificationEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(220, 226, 215));
        getWindow().setNavigationBarColor(Color.rgb(250, 248, 242));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        slides = OnboardingSlide.load(this);
        bindViews();
        onboardingPager.setAdapter(new OnboardingAdapter(slides));
        onboardingSheet.setOnSwipeListener(direction -> {
            int targetPage = onboardingPager.getCurrentItem() + direction;
            if (targetPage >= 0 && targetPage < slides.size()) {
                onboardingPager.setCurrentItem(targetPage, true);
            }
        });
        onboardingPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                showSlide(position);
            }
        });
        showSlide(0);

        findViewById(R.id.skipButton).setOnClickListener(
                v -> openAuthScreen(AuthFlowView.Screen.SIGN_IN));
        getStartedButton.setOnClickListener(
                v -> openAuthScreen(AuthFlowView.Screen.SIGN_UP));
        continueSignInButton.setOnClickListener(
                v -> openAuthScreen(AuthFlowView.Screen.SIGN_IN));

        authStatusTextView.setText(ClerkAuth.isConfigured()
                ? "A LITTLE ROOM TO WANDER"
                : "AUTHENTICATION IS NOT CONFIGURED");
        setAuthActionsEnabled(!ClerkAuth.isConfigured());
        if (ClerkAuth.isConfigured()) {
            setAuthActionsEnabled(false);
            ClerkAuth.observe(this, (initialized, isSignedIn) -> {
                clerkInitialized = initialized;
                signedIn = initialized && isSignedIn;
                if (signedIn) {
                    authScreen = null;
                    pendingVerificationEmail = "";
                }
                authStatusTextView.setText(initialized
                        ? "A LITTLE ROOM TO WANDER"
                        : "CONNECTING TO CLERK");
                setAuthActionsEnabled(initialized && !signedIn);
                showCorrectScreen();
            });
        }
        showCorrectScreen();
    }

    private void bindViews() {
        onboardingContainer = findViewById(R.id.onboardingContainer);
        authContainer = findViewById(R.id.authContainer);
        signedInContainer = findViewById(R.id.signedInContainer);
        onboardingPager = findViewById(R.id.onboardingPager);
        onboardingSheet = findViewById(R.id.onboardingSheet);
        pageDots = findViewById(R.id.pageDots);
        slideTitle = findViewById(R.id.slideTitle);
        slideDescription = findViewById(R.id.slideDescription);
        authStatusTextView = findViewById(R.id.authStatusTextView);
        getStartedButton = findViewById(R.id.getStartedButton);
        continueSignInButton = findViewById(R.id.continueSignInButton);
    }

    private void showSlide(int position) {
        OnboardingSlide slide = slides.get(position);
        slideTitle.setText(slide.getTitle());
        slideDescription.setText(slide.getDescription());
        renderPageDots(position);
    }

    private void renderPageDots(int selectedPosition) {
        pageDots.removeAllViews();
        for (int i = 0; i < slides.size(); i++) {
            View dot = new View(this);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            background.setCornerRadius(dp(4));
            background.setColor(i == selectedPosition ? ROAM_GREEN : MUTED_DOT);
            dot.setBackground(background);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp(i == selectedPosition ? 24 : 7),
                    dp(7)
            );
            if (i > 0) {
                params.leftMargin = dp(6);
            }
            pageDots.addView(dot, params);
        }
    }

    private void showCorrectScreen() {
        boolean authenticated = ClerkAuth.isConfigured() && clerkInitialized && signedIn;
        onboardingContainer.setVisibility(authScreen == null && !authenticated ? View.VISIBLE : View.GONE);
        authContainer.setVisibility(authScreen != null && !authenticated ? View.VISIBLE : View.GONE);
        signedInContainer.setVisibility(authenticated ? View.VISIBLE : View.GONE);
        if (authenticated && homeTabsView == null) {
            homeTabsView = new HomeTabsView(this,
                    () -> ClerkAuth.signOut(this, authActionListener()));
            ((android.widget.FrameLayout) signedInContainer).addView(homeTabsView,
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
        }
        int systemBarColor = authenticated || authScreen != null
                ? Color.WHITE : Color.rgb(220, 226, 215);
        getWindow().setStatusBarColor(systemBarColor);
        getWindow().setNavigationBarColor(systemBarColor);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    private void openAuthScreen(AuthFlowView.Screen screen) {
        if (!ClerkAuth.isConfigured()) {
            showAuthMessage("Set CLERK_PUBLISHABLE_KEY in .env or local.properties, then rebuild.");
            return;
        }
        if (!clerkInitialized) {
            showAuthMessage("Connecting to Clerk. Please try again shortly.");
            return;
        }
        pendingVerificationEmail = "";
        showAuthScreen(screen);
    }

    private void showAuthScreen(AuthFlowView.Screen screen) {
        authScreen = screen;
        if (authFlowView == null) {
            authFlowView = new AuthFlowView(this, new AuthFlowView.Listener() {
                @Override
                public void onBack() {
                    if (authScreen == AuthFlowView.Screen.VERIFY_SIGN_UP) {
                        showAuthScreen(AuthFlowView.Screen.SIGN_UP);
                    } else if (authScreen == AuthFlowView.Screen.VERIFY_SIGN_IN) {
                        showAuthScreen(AuthFlowView.Screen.SIGN_IN);
                    } else {
                        closeAuthScreen();
                    }
                }

                @Override
                public void onCreateAccount(String name, String email, String password) {
                    if (name.isEmpty()) {
                        showAuthMessage("Enter your full name.");
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        showAuthMessage("Enter a valid email address.");
                    } else if (password.length() < 8) {
                        showAuthMessage("Your password must be at least 8 characters.");
                    } else {
                        pendingVerificationEmail = email;
                        ClerkAuth.createAccount(MainActivity.this, name, email, password, authActionListener());
                    }
                }

                @Override
                public void onVerifyEmail(String email, String code, boolean signIn) {
                    if (!code.matches("\\d{6}")) {
                        showAuthMessage("Enter the 6-digit code to continue.");
                    } else {
                        ClerkAuth.verifyEmailCode(MainActivity.this, code, signIn, authActionListener());
                    }
                }

                @Override
                public void onRequestSignInCode(String email) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        showAuthMessage("Enter a valid email address.");
                    } else {
                        pendingVerificationEmail = email;
                        ClerkAuth.requestSignInCode(MainActivity.this, email, authActionListener());
                    }
                }

                @Override
                public void onOpenSignIn() {
                    if (authScreen == AuthFlowView.Screen.VERIFY_SIGN_UP) {
                        pendingVerificationEmail = "";
                    }
                    showAuthScreen(AuthFlowView.Screen.SIGN_IN);
                }

                @Override
                public void onOpenSignUp() {
                    pendingVerificationEmail = "";
                    showAuthScreen(AuthFlowView.Screen.SIGN_UP);
                }

                @Override
                public void onResendCode(String email, boolean signIn) {
                    ClerkAuth.resendEmailCode(MainActivity.this, email, signIn, authActionListener());
                }
            });
            authContainer.addView(authFlowView, new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
        }
        authFlowView.show(screen, pendingVerificationEmail);
        showCorrectScreen();
    }

    private ClerkAuthActionListener authActionListener() {
        return new ClerkAuthActionListener() {
            @Override
            public void onLoadingChanged(boolean loading) {
                if (authFlowView != null) authFlowView.setLoading(loading);
            }

            @Override
            public void onCodeSent(String email, boolean signIn) {
                pendingVerificationEmail = email;
                showAuthScreen(signIn
                        ? AuthFlowView.Screen.VERIFY_SIGN_IN
                        : AuthFlowView.Screen.VERIFY_SIGN_UP);
            }

            @Override
            public void onSignedOut() {
                authScreen = null;
                pendingVerificationEmail = "";
                signedIn = false;
                showCorrectScreen();
            }

            @Override
            public void onError(String message) {
                if (authFlowView != null && authScreen != null) {
                    authFlowView.showMessage(message);
                } else {
                    showAuthMessage(message);
                }
            }
        };
    }

    private void setAuthActionsEnabled(boolean enabled) {
        findViewById(R.id.skipButton).setEnabled(enabled);
        getStartedButton.setEnabled(enabled);
        continueSignInButton.setEnabled(enabled);
        findViewById(R.id.skipButton).setAlpha(enabled ? 1f : 0.6f);
        getStartedButton.setAlpha(enabled ? 1f : 0.6f);
        continueSignInButton.setAlpha(enabled ? 1f : 0.6f);
    }

    private void closeAuthScreen() {
        authScreen = null;
        pendingVerificationEmail = "";
        showCorrectScreen();
    }

    private void showAuthMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (authScreen != null) {
            if (authScreen == AuthFlowView.Screen.VERIFY_SIGN_UP) {
                showAuthScreen(AuthFlowView.Screen.SIGN_UP);
            } else if (authScreen == AuthFlowView.Screen.VERIFY_SIGN_IN) {
                showAuthScreen(AuthFlowView.Screen.SIGN_IN);
            } else {
                closeAuthScreen();
            }
            return;
        }
        super.onBackPressed();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
