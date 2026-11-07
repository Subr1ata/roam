package com.roam.app.ui.onboarding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public final class TravelIllustrationView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int scene;

    public TravelIllustrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentDescription("Illustrated travel destination");
    }

    public void setScene(int scene) {
        this.scene = scene;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        if (width == 0 || height == 0) {
            return;
        }

        if (scene == 0) {
            drawCoast(canvas, width, height);
        } else if (scene == 1) {
            drawMountainLake(canvas, width, height);
        } else {
            drawEveningGarden(canvas, width, height);
        }
        drawForegroundShade(canvas, width, height);
    }

    private void drawCoast(Canvas canvas, float width, float height) {
        drawSky(canvas, width, height, 0xFFE4C6AA, 0xFF799C9B);
        circle(canvas, width * 0.74f, height * 0.18f, width * 0.085f, 0xFFFFE1AC);

        Path water = new Path();
        water.moveTo(0, height * 0.56f);
        water.cubicTo(width * 0.37f, height * 0.49f, width * 0.7f, height * 0.69f, width, height * 0.55f);
        water.lineTo(width, height);
        water.lineTo(0, height);
        water.close();
        fill(canvas, water, 0xFF628D91);

        Path distantCliff = new Path();
        distantCliff.moveTo(0, height * 0.6f);
        distantCliff.lineTo(width * 0.2f, height * 0.43f);
        distantCliff.lineTo(width * 0.32f, height * 0.46f);
        distantCliff.lineTo(width * 0.49f, height * 0.35f);
        distantCliff.lineTo(width * 0.62f, height * 0.51f);
        distantCliff.lineTo(width, height * 0.48f);
        distantCliff.lineTo(width, height);
        distantCliff.lineTo(0, height);
        distantCliff.close();
        fill(canvas, distantCliff, 0xFF496B62);

        Path headland = new Path();
        headland.moveTo(0, height * 0.66f);
        headland.lineTo(width * 0.2f, height * 0.53f);
        headland.lineTo(width * 0.39f, height * 0.57f);
        headland.lineTo(width * 0.45f, height * 0.75f);
        headland.lineTo(width * 0.58f, height);
        headland.lineTo(0, height);
        headland.close();
        fill(canvas, headland, 0xFFBA8061);

        drawHouse(canvas, width * 0.13f, height * 0.57f, width * 0.09f, height * 0.1f, 0xFFF3D8B5);
        drawHouse(canvas, width * 0.29f, height * 0.59f, width * 0.07f, height * 0.085f, 0xFFE7C49A);
        drawHouse(canvas, width * 0.39f, height * 0.67f, width * 0.08f, height * 0.1f, 0xFFF6E0BB);
    }

    private void drawMountainLake(Canvas canvas, float width, float height) {
        drawSky(canvas, width, height, 0xFFD4E2D9, 0xFFA2B5A9);
        circle(canvas, width * 0.75f, height * 0.21f, width * 0.075f, 0xFFFFE9C1);

        Path farPeak = new Path();
        farPeak.moveTo(0, height * 0.62f);
        farPeak.lineTo(width * 0.31f, height * 0.22f);
        farPeak.lineTo(width * 0.45f, height * 0.41f);
        farPeak.lineTo(width * 0.64f, height * 0.18f);
        farPeak.lineTo(width, height * 0.62f);
        farPeak.close();
        fill(canvas, farPeak, 0xFF879C90);

        Path snow = new Path();
        snow.moveTo(width * 0.22f, height * 0.34f);
        snow.lineTo(width * 0.31f, height * 0.22f);
        snow.lineTo(width * 0.4f, height * 0.35f);
        snow.lineTo(width * 0.34f, height * 0.33f);
        snow.lineTo(width * 0.3f, height * 0.38f);
        snow.close();
        fill(canvas, snow, 0xFFF5EFE0);

        Path nearPeak = new Path();
        nearPeak.moveTo(0, height * 0.71f);
        nearPeak.lineTo(width * 0.49f, height * 0.36f);
        nearPeak.lineTo(width * 0.76f, height * 0.68f);
        nearPeak.lineTo(width, height * 0.47f);
        nearPeak.lineTo(width, height);
        nearPeak.lineTo(0, height);
        nearPeak.close();
        fill(canvas, nearPeak, 0xFF4C6759);

        Path lake = new Path();
        lake.moveTo(0, height * 0.77f);
        lake.cubicTo(width * 0.35f, height * 0.72f, width * 0.67f, height * 0.84f, width, height * 0.75f);
        lake.lineTo(width, height);
        lake.lineTo(0, height);
        lake.close();
        fill(canvas, lake, 0xFF638C88);
    }

    private void drawEveningGarden(Canvas canvas, float width, float height) {
        drawSky(canvas, width, height, 0xFFCCB9A3, 0xFF687C72);
        circle(canvas, width * 0.76f, height * 0.2f, width * 0.08f, 0xFFFFE0B0);

        Path ridge = new Path();
        ridge.moveTo(0, height * 0.63f);
        ridge.lineTo(width * 0.27f, height * 0.38f);
        ridge.lineTo(width * 0.57f, height * 0.55f);
        ridge.lineTo(width * 0.84f, height * 0.35f);
        ridge.lineTo(width, height * 0.48f);
        ridge.lineTo(width, height);
        ridge.lineTo(0, height);
        ridge.close();
        fill(canvas, ridge, 0xFF647363);

        Path garden = new Path();
        garden.moveTo(0, height * 0.77f);
        garden.cubicTo(width * 0.33f, height * 0.65f, width * 0.7f, height * 0.79f, width, height * 0.66f);
        garden.lineTo(width, height);
        garden.lineTo(0, height);
        garden.close();
        fill(canvas, garden, 0xFF3D5B4D);

        drawPagoda(canvas, width * 0.52f, height * 0.6f, width * 0.22f, height * 0.23f);
        circle(canvas, width * 0.16f, height * 0.72f, width * 0.07f, 0xFFB47D6A);
        circle(canvas, width * 0.29f, height * 0.82f, width * 0.045f, 0xFFD4A278);
        circle(canvas, width * 0.84f, height * 0.77f, width * 0.065f, 0xFFD19B78);
    }

    private void drawSky(Canvas canvas, float width, float height, int top, int bottom) {
        paint.setShader(new LinearGradient(0, 0, 0, height * 0.72f, top, bottom, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);
    }

    private void drawHouse(Canvas canvas, float left, float top, float width, float height, int color) {
        paint.setColor(color);
        canvas.drawRect(left, top, left + width, top + height, paint);
        paint.setColor(0xFF78594A);
        Path roof = new Path();
        roof.moveTo(left - width * 0.12f, top);
        roof.lineTo(left + width * 0.5f, top - height * 0.38f);
        roof.lineTo(left + width * 1.12f, top);
        roof.close();
        canvas.drawPath(roof, paint);
        paint.setColor(0xFF5A5144);
        canvas.drawRect(left + width * 0.39f, top + height * 0.48f, left + width * 0.62f, top + height, paint);
    }

    private void drawPagoda(Canvas canvas, float centerX, float baseY, float width, float height) {
        float floorHeight = height * 0.24f;
        paint.setColor(0xFFD6B998);
        canvas.drawRect(centerX - width * 0.16f, baseY - height, centerX + width * 0.16f, baseY, paint);

        paint.setColor(0xFF694F42);
        for (int floor = 0; floor < 3; floor++) {
            float y = baseY - floorHeight * (floor + 1);
            float halfWidth = width * (0.5f - floor * 0.1f);
            Path roof = new Path();
            roof.moveTo(centerX - halfWidth, y + floorHeight * 0.16f);
            roof.quadTo(centerX, y - floorHeight * 0.42f, centerX + halfWidth, y + floorHeight * 0.16f);
            roof.lineTo(centerX + halfWidth * 0.92f, y + floorHeight * 0.3f);
            roof.lineTo(centerX - halfWidth * 0.92f, y + floorHeight * 0.3f);
            roof.close();
            canvas.drawPath(roof, paint);
        }
    }

    private void drawForegroundShade(Canvas canvas, float width, float height) {
        paint.setShader(new LinearGradient(
                0,
                height * 0.36f,
                0,
                height,
                0x001B2925,
                0x66202E29,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, height * 0.34f, width, height, paint);
        paint.setShader(null);
    }

    private void circle(Canvas canvas, float x, float y, float radius, int color) {
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }

    private void fill(Canvas canvas, Path path, int color) {
        paint.setColor(color);
        canvas.drawPath(path, paint);
    }
}
