package appus.pro.labelview.label;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
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
    private Paint bodyPaint;
    private Path path;
    private int resWidth;
    private int resHeight;
    private Paint.Align textAlign = Paint.Align.RIGHT;
    private boolean left = true;

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
            left = atr.getBoolean(R.styleable.LabelView_isLeftDirection, true);
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
            left = atr.getBoolean(R.styleable.LabelView_isLeftDirection, true);
        } finally {
            atr.recycle();
        }
    }

    private void init(){

        //Draw background
        bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        labelText = String.valueOf(getText());



        if (getText().length()>0) {

            Rect bounds = new Rect();
            getPaint().getTextBounds(labelText, 0, labelText.length(), bounds);

            int desiredHeight = (int) (bounds.height()*getContext().getResources().getDisplayMetrics().scaledDensity + getPaddingBottom() + getPaddingTop());
            int desiredWidth = (int) ( bounds.width()*getContext().getResources().getDisplayMetrics().scaledDensity + (desiredHeight )*0.5 + getPaddingLeft() + getPaddingRight());

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
            setMeasuredDimension(resWidth , (int) (resHeight));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        List<Pair<Float, Float>> coordinates = getPathCoordinats();

        path.reset();

        for (int i = 0; i < coordinates.size(); i++) {
            if (i == 0){
                path.moveTo(coordinates.get(i).first, coordinates.get(i).second);
                Log.d("Label", "onDraw: 1st " + coordinates.get(i).first + "  " + coordinates.get(i).second);
            } else {
                path.lineTo(coordinates.get(i).first, coordinates.get(i).second);
                Log.d("Label", "onDraw: other " + coordinates.get(i).first + "  " + coordinates.get(i).second);
            }
        }

        bodyPaint.setColor(labelBackgroundColor);
        canvas.drawPath(path, bodyPaint);
        canvas.save();
        Pair<Float, Float> translateCoord = getTextCoords();
        canvas.translate(translateCoord.first, translateCoord.second);
        Log.d("Label", "onDraw: x " + canvas.getWidth() + " y " + canvas.getHeight());
        Log.d("Label", "onDraw: x " + getMeasuredWidth() + " y " + getMeasuredHeight());

        super.onDraw(canvas);
        canvas.restore();
    }

    private List<Pair<Float, Float>> getPathCoordinats(){
        List<Pair<Float, Float>> coordinates = new ArrayList<>(6);
        if (left){
            coordinates.add(0, new Pair<Float, Float>(0f, getMeasuredHeight()*0.5f));
            coordinates.add(1, new Pair<Float, Float>(getMeasuredHeight()*0.5f, 0.5f));
            coordinates.add(2, new Pair<Float, Float>((float) getMeasuredWidth() , 0f));
            coordinates.add(3, new Pair<Float, Float>((float) (getMeasuredWidth() ), (float) getMeasuredHeight()));
            coordinates.add(4, new Pair<Float, Float>(this.getMeasuredHeight()*0.5f, (float) getMeasuredHeight()));
        } else {
            coordinates.add(0, new Pair<Float, Float>(0f, 0f));
            coordinates.add(1, new Pair<Float, Float>((float) this.getMeasuredWidth() - this.getMeasuredHeight()*0.5f, 0f));
            coordinates.add(2, new Pair<Float, Float>((float) this.getMeasuredWidth(), this.getMeasuredHeight()*0.5f));
            coordinates.add(3, new Pair<Float, Float>((float)this.getMeasuredWidth() - this.getMeasuredHeight()*0.5f,(float) this.getMeasuredHeight()));
            coordinates.add(4, new Pair<Float, Float>(0f, (float) this.getMeasuredHeight()));

        }

        return coordinates;
    }

    private void resize(){
        setMeasuredDimension((int) (getMeasuredWidth() + getMeasuredHeight()), getMeasuredHeight());
    }


    private Pair<Float, Float> getTextCoords(){
        float x = 0, y = 0;

        if (left){
            x = (float) ((getMeasuredHeight())*0.5 );
            y = 0f;
        } else {
            x = 0f;
            y = 0f;
        }

        return new Pair<Float, Float>(x,y);
    }
}
