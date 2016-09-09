package appus.pro.labelview.label;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import appus.pro.labelview.R;

/**
 * Created by yaroslav.romanyuta, Appus LLC on 9/9/16.
 */
public class LabelView extends TextView {

    private String labelText = "Some Text";
    private int labelBackgroundColor = Color.RED;
    private Paint textPaint;
    private Paint bodyPaint;
    private Path path;
    private int resWidth;
    private int resHeight;
    private Paint.Align textAlign = Paint.Align.RIGHT;
    private boolean left = true;
    private float textSise;
    private DisplayMetrics displayMetrics;

    public LabelView(Context context) {
        super(context);
        init();
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray atr = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LabelView,
                0, 0);
        try {
            labelBackgroundColor = atr.getColor(R.styleable.LabelView_labelColor, Color.TRANSPARENT);
            labelText = atr.getString(R.styleable.LabelView_labelText);
            left = atr.getBoolean(R.styleable.LabelView_isLeftDirection, true);
            textSise = getTextSize();
        } finally {
            atr.recycle();
        }
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray atr = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LabelView,
                0, 0);
        try {
            labelBackgroundColor = atr.getColor(R.styleable.LabelView_labelColor, Color.TRANSPARENT);
            labelText = atr.getString(R.styleable.LabelView_labelText);
            left = atr.getBoolean(R.styleable.LabelView_isLeftDirection, true);
        } finally {
            atr.recycle();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        TypedArray atr = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LabelView,
                0, 0);
        try {
            labelBackgroundColor = atr.getColor(R.styleable.LabelView_labelColor, Color.TRANSPARENT);
            labelText = atr.getString(R.styleable.LabelView_labelText);
            left = atr.getBoolean(R.styleable.LabelView_isLeftDirection, true);
        } finally {
            atr.recycle();
        }
    }

    private void init(){

        displayMetrics = getContext().getResources().getDisplayMetrics();

        //Draw text
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(textAlign);

        //Draw background
        bodyPaint = new Paint();
        bodyPaint.setColor(labelBackgroundColor);

        path = new Path();
    }

    public void setLabelColor(int color){
        this.labelBackgroundColor = color;
        bodyPaint.setColor(labelBackgroundColor);
        invalidate();
    }

    public void setLabelText(String labelText){
        this.labelText = labelText;
        setText(labelText);
        invalidate();

    }

    public void setTextAligment(Paint.Align aligment){
        this.textAlign = aligment;
    }

    public void isLeft(boolean isLeft){
        this.left = isLeft;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Pair<Float, Float>> coordinates = getPathCoordinats();

        path.reset();

        for (int i = 0; i < coordinates.size(); i++) {
            if (i == 0){
                path.moveTo(coordinates.get(i).first, coordinates.get(i).second);
            } else {
                path.lineTo(coordinates.get(i).first, coordinates.get(i).second);
            }
        }

        canvas.clipPath(path);
        bodyPaint.setColor(labelBackgroundColor);
        canvas.drawPath(path, bodyPaint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSise);
        Pair<Float, Float> textCoordinates = getTextCoords();
        canvas.drawText(labelText, textCoordinates.first, textCoordinates.second, textPaint);
    }

    private List<Pair<Float, Float>> getPathCoordinats(){
        List<Pair<Float, Float>> coordinates = new ArrayList<>(6);
        if (left){
            coordinates.add(0, new Pair<Float, Float>(0f, this.getHeight()*0.5f));
            coordinates.add(1, new Pair<Float, Float>(this.getHeight()*0.5f, (float) this.getHeight()));
            coordinates.add(2, new Pair<Float, Float>(this.getWidth() + this.getHeight()*0.5f, (float) this.getHeight()));
            coordinates.add(3, new Pair<Float, Float>(this.getWidth() + this.getHeight()*0.5f, 0f));
            coordinates.add(4, new Pair<Float, Float>(this.getHeight()*0.5f, 0f));
        }

        return coordinates;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (labelText.length()>0) {
            int desiredWidth = getViewWidth();
            int desiredHeight = getViewHeight();
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (widthMode == MeasureSpec.EXACTLY) {
                resWidth = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                resWidth = Math.min(desiredWidth, widthSize);
            } else {
                resWidth = desiredWidth;
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                resHeight = desiredHeight;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                resHeight = Math.min(desiredHeight, heightSize);
            } else {
                resHeight = desiredHeight;
            }
            setMeasuredDimension(resWidth, resHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int getViewWidth(){
        int xPad = getPaddingLeft() + getPaddingRight();
        int textWidth = (int) (labelText.length()*getTextSize());

        return xPad + textWidth;
    }

    private int getViewHeight(){
        int yPad = getPaddingBottom() + getPaddingTop();
        return (int) (yPad + getTextSize());
    }

    private Pair<Float, Float> getTextCoords(){
        float x = 0, y = 0;

        switch (textAlign){
            case CENTER:
                x = (float) ((getWidth() + getHeight()*0.5)*0.5 + getPaddingLeft());
                y = getHeight() - getPaddingBottom();
                break;

            case LEFT:
                x = (float) (getHeight()*0.5 + getPaddingLeft());
                y = getHeight() - getPaddingBottom();
                break;

            case RIGHT:
                x = (float) (getWidth() - getPaddingRight());
                y = getHeight() - getPaddingBottom();
                break;
        }

        return new Pair<Float, Float>(x,y);
    }
}
