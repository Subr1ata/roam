package com.roam.app.ui.auth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class AuthFlowView extends ScrollView {
    public enum Screen {
        SIGN_UP,
        VERIFY_SIGN_UP,
        VERIFY_SIGN_IN,
        SIGN_IN
    }

    public interface Listener {
        void onBack();

        void onCreateAccount(String name, String email, String password);

        void onVerifyEmail(String email, String code, boolean signIn);

        void onRequestSignInCode(String email);

        void onOpenSignIn();

        void onOpenSignUp();

        void onResendCode(String email, boolean signIn);
    }

    private static final int INK = Color.rgb(17, 19, 24);
    private static final int MUTED = Color.rgb(143, 147, 162);
    private static final int YELLOW = Color.rgb(255, 192, 70);
    private static final int FIELD = Color.rgb(246, 246, 247);
    private final LinearLayout content;
    private final Listener listener;
    private final List<View> actionViews = new ArrayList<>();
    private String email = "";
    private TextView feedback;

    public AuthFlowView(@NonNull Context context, @NonNull Listener listener) {
        super(context);
        this.listener = listener;
        setFillViewport(true);
        setClipToPadding(false);
        setBackgroundColor(Color.rgb(255, 255, 255));
        content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(26), dp(12), dp(26), dp(28));
        addView(content, new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void show(@NonNull Screen screen, @NonNull String currentEmail) {
        email = currentEmail;
        content.removeAllViews();
        actionViews.clear();
        feedback = null;
        addHeader();
        switch (screen) {
            case SIGN_UP:
                showSignUp();
                break;
            case VERIFY_SIGN_UP:
                showVerification(false);
                break;
            case VERIFY_SIGN_IN:
                showVerification(true);
                break;
            case SIGN_IN:
                showSignIn();
                break;
        }
        setLoading(false);
        post(() -> smoothScrollTo(0, 0));
    }

    public void setLoading(boolean loading) {
        for (View view : actionViews) {
            view.setEnabled(!loading);
            view.setAlpha(loading ? 0.6f : 1f);
        }
    }

    public void showMessage(@NonNull String message) {
        if (feedback != null) {
            feedback.setText(message);
            feedback.setVisibility(View.VISIBLE);
        }
    }

    private void addHeader() {
        LinearLayout bar = new LinearLayout(getContext());
        bar.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(bar, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(56)));

        TextView back = text("‹", 38, INK);
        back.setGravity(Gravity.CENTER);
        back.setBackground(round(Color.WHITE, Color.rgb(235, 236, 239), dp(28), dp(1)));
        bar.addView(back, new LinearLayout.LayoutParams(dp(48), dp(48)));
        actionViews.add(back);
        back.setOnClickListener(view -> listener.onBack());

        View spacer = new View(getContext());
        bar.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1));
        TextView wordmark = text("roam", 27, INK);
        wordmark.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
        bar.addView(wordmark);
        addSpace(30);
    }

    private void showSignUp() {
        addHeading("Create\nyour account",
                "Join roam to save your stays, get personalized recommendations and access them across all your devices.");
        EditText name = addField("Full name", "e.g. Adrian Hajdin",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS, false);
        EditText emailInput = addField("Email address", "you@example.com",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, false);
        if (!email.isEmpty()) {
            emailInput.setText(email);
            emailInput.setSelection(emailInput.length());
        }
        EditText password = addField("Password", "At least 8 characters",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, true);
        addPrimaryButton("Create account", () ->
                listener.onCreateAccount(value(name), value(emailInput), value(password)));
        addFooter("Already have an account? ", "Sign in", listener::onOpenSignIn);
    }

    private void showVerification(boolean signIn) {
        addHeading(signIn ? "Sign in with\nyour email" : "Check your\nemail",
                signIn
                        ? "Enter the verification code sent to " + email + "."
                        : "Enter the verification code sent to " + email + " to finish creating your account.");
        EditText code = addField("Verification code", "Enter 6-digit code",
                InputType.TYPE_CLASS_NUMBER, false);
        code.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});
        code.setLetterSpacing(0.25f);
        addPrimaryButton("Verify code", () -> listener.onVerifyEmail(email, value(code), signIn));
        addFooter("Didn't receive a code? ", "Resend code",
                () -> listener.onResendCode(email, signIn));
        if (!signIn) {
            addFooter("Already have an account? ", "Sign in", listener::onOpenSignIn);
        }
    }

    private void showSignIn() {
        addHeading("Welcome\nback",
                "Enter your email and we’ll send you a secure sign-in code.");
        EditText emailInput = addField("Email address", "you@example.com",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, false);
        if (!email.isEmpty()) {
            emailInput.setText(email);
            emailInput.setSelection(emailInput.length());
        }
        addPrimaryButton("Send sign-in code", () ->
                listener.onRequestSignInCode(value(emailInput)));
        addFooter("New to roam? ", "Create account", listener::onOpenSignUp);
    }

    private void addHeading(String title, String description) {
        TextView heading = text(title, 38, INK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        heading.setLineSpacing(0, 0.94f);
        content.addView(heading, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 14));

        TextView body = text(description, 17, MUTED);
        body.setLineSpacing(dp(4), 1f);
        content.addView(body, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 26));

        feedback = text("", 14, Color.rgb(173, 49, 45));
        feedback.setVisibility(View.GONE);
        content.addView(feedback, marginParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0, 0, 0, 12));
    }

    private EditText addField(String label, String hint, int inputType, boolean password) {
        LinearLayout field = new LinearLayout(getContext());
        field.setGravity(Gravity.CENTER_VERTICAL);
        field.setPadding(dp(16), dp(8), dp(16), dp(8));
        field.setBackground(round(FIELD, Color.TRANSPARENT, dp(18), 0));
        content.addView(field, marginParams(LayoutParams.MATCH_PARENT, dp(70), 0, 0, 0, 14));

        TextView icon = text(iconFor(label), 25, INK);
        icon.setGravity(Gravity.CENTER);
        field.addView(icon, new LinearLayout.LayoutParams(dp(42), LayoutParams.MATCH_PARENT));

        LinearLayout labels = new LinearLayout(getContext());
        labels.setGravity(Gravity.CENTER_VERTICAL);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(14), 0, 0, 0);
        field.addView(labels, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));

        TextView caption = text(label, 14, INK);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        labels.addView(caption);

        EditText input = new EditText(getContext());
        input.setSingleLine(true);
        input.setTextSize(15);
        input.setTextColor(INK);
        input.setHintTextColor(MUTED);
        input.setHint(hint);
        input.setPadding(0, dp(2), 0, 0);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setInputType(inputType);
        input.setImeOptions(password ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        if (password) {
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        labels.addView(input, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dp(27)));

        if (password) {
            TextView toggle = text("◉", 22, INK);
            toggle.setGravity(Gravity.CENTER);
            field.addView(toggle, new LinearLayout.LayoutParams(dp(40), LayoutParams.MATCH_PARENT));
            toggle.setOnClickListener(view -> {
                boolean hidden = input.getTransformationMethod() instanceof PasswordTransformationMethod;
                input.setTransformationMethod(hidden ? null : PasswordTransformationMethod.getInstance());
                input.setSelection(input.length());
                toggle.setText(hidden ? "◎" : "◉");
            });
        }
        return input;
    }

    private void addPrimaryButton(String label, Runnable action) {
        TextView button = text(label, 18, Color.BLACK);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setBackground(round(YELLOW, Color.TRANSPARENT, dp(32), 0));
        content.addView(button, marginParams(LayoutParams.MATCH_PARENT, dp(60), 0, 12, 0, 0));
        actionViews.add(button);
        button.setOnClickListener(view -> action.run());
    }

    private void addFooter(String prefix, String actionLabel, Runnable action) {
        LinearLayout footer = new LinearLayout(getContext());
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(14), 0, dp(2));
        content.addView(footer, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        TextView description = text(prefix, 14, MUTED);
        footer.addView(description);
        TextView link = text(actionLabel, 14, INK);
        link.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        link.setPaintFlags(link.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        footer.addView(link);
        actionViews.add(link);
        link.setOnClickListener(view -> action.run());
    }

    private void addSpace(int heightDp) {
        content.addView(new View(getContext()), new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private TextView text(String value, int sizeSp, int color) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        return view;
    }

    private String iconFor(String label) {
        if (label.toLowerCase().contains("name")) return "♙";
        if (label.toLowerCase().contains("email")) return "✉";
        if (label.toLowerCase().contains("password")) return "▣";
        return "◉";
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

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String value(EditText input) {
        return input.getText().toString().trim();
    }
}
