package com.dots.hackntu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by AdrianHsu on 15/8/22.
 */
public class NumberView extends View {

  private final Interpolator mInterpolator;
  private final Paint mPaint;
  private final Path mPath;

  // Numbers currently shown.
  private int mCurrent = 0;
  private int mNext = 1;

  // Frame of transition between current and next frames.
  private int mFrame = 0;

  // The 5 end points. (Note: The last end point is the first end point of the next segment.
  private final float[][][] mPoints = {
    {{44.5f, 100}, {100, 18}, {156, 100}, {100, 180}, {44.5f, 100}}};

  // The set of the "first" control points of each segment.
    private final float[][][] mControlPoint1 = {};

// The set of the "second" control points of each segment.
    private final float[][][] mControlPoint2 = {};

    public NumberView(Context context, AttributeSet attrs) {
      super(context, attrs);

      setWillNotDraw(false);
      mInterpolator = new AccelerateDecelerateInterpolator();

    // A new paint with the style as stroke.
      mPaint = new Paint();
      mPaint.setAntiAlias(true);
      mPaint.setColor(Color.BLACK);
      mPaint.setStrokeWidth(5.0f);
      mPaint.setStyle(Paint.Style.STROKE);

      mPath = new Path();
    }

    @Override
    public void onDraw(Canvas canvas) {
      super.onDraw(canvas);

// Frames 0, 1 is the first pause.
// Frames 9, 10 is the last pause.
// Constrain current frame to be between 0 and 6.
      final int currentFrame;
      if (mFrame < 2) {
        currentFrame = 0;
      } else if (mFrame > 8) {
        currentFrame = 6;
      } else {
        currentFrame = mFrame - 2;
      }

// A factor of the difference between current
// and next frame based on interpolation.
// Only 6 frames are used between the transition.
      final float factor = mInterpolator.getInterpolation(currentFrame / 6.0f);

  // Reset the path.
      mPath.reset();

      final float[][] current = mPoints[mCurrent];
      final float[][] next = mPoints[mNext];

      final float[][] curr1 = mControlPoint1[mCurrent];
      final float[][] next1 = mControlPoint1[mNext];

      final float[][] curr2 = mControlPoint2[mCurrent];
      final float[][] next2 = mControlPoint2[mNext];

  // First point.
      mPath.moveTo(current[0][0] + ((next[0][0] - current[0][0]) * factor),
        current[0][1] + ((next[0][1] - current[0][1]) * factor));

  // Rest of the points connected as bezier curve.
      for (int i = 1; i < 5 ; i++ ) {
        mPath.cubicTo( (curr1[i-1][0]) + ((next1[i-1][0] - curr1[i-1][0]) * factor),
         curr1[i-1][1] + ((next1[i-1][1] - curr1[i-1][1]) * factor),
         curr2[i-1][0] + ((next2[i-1][0] - curr2[i-1][0]) * factor),
         curr2[i-1][1] + ((next2[i-1][1] - curr2[i-1][1]) * factor),
         current[i][0] + ((next[i][0] - current[i][0]) * factor),
         current[i][1] + ((next[i][1] - current[i][1]) * factor) );
      }

  // Draw the path.
      canvas.drawPath(mPath, mPaint);

  // Next frame.
      mFrame++;

  // Each number change has 10 frames. Reset.
      if (mFrame == 10) {
  // Reset to zarro.
        mFrame = 0;

        mCurrent = mNext;
        mNext++;

  // Reset to zarro.
        if (mNext == 10) {
          mNext = 0;
        }
      }

  // Callback for the next frame.
      postInvalidateDelayed(100);
    }
}