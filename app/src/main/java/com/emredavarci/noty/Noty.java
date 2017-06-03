package com.emredavarci.noty;

/*       Copyright 2017 M. Emre Davarci
*
*        Licensed under the Apache License, Version 2.0 (the "License");
*        you may not use this file except in compliance with the License.
*        You may obtain a copy of the License at
*
*        http://www.apache.org/licenses/LICENSE-2.0
*
*        Unless required by applicable law or agreed to in writing, software
*        distributed under the License is distributed on an "AS IS" BASIS,
*        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*        See the License for the specific language governing permissions and
*        limitations under the License.
*/

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by M. Emre Davarci on 02.05.2017.
 */

public class Noty {

    private Context context;
    private Resources resources;
    private ViewGroup parentLayout;

    private RelativeLayout.LayoutParams     rlp                     = null;
    private LinearLayout.LayoutParams       llp                     = null;
    private RelativeLayout                  warningBox              = null;
    private LinearLayout                    contentFrame            = null;
    private TextView                        tvWarning               = null;
    private TextView                        tvAction                = null;

    private int                             warningBoxWidth         = ViewGroup.LayoutParams.MATCH_PARENT;
    private int                             warningBoxHeight        = 110; // Using wrap_content is better
    private WarningPos                      warningBoxPosition      = WarningPos.BOTTOM;
    private WarningStyle                    warningStyle            = WarningStyle.ACTION;
    private int                             warningMarginLeft       = 0;
    private int                             warningMarginRight      = 0;
    private int                             warningMarginTop        = 0;
    private int                             warningMarginBottom     = 0;
    private float                           topLeftRadius           = 2;
    private float                           topRightRadius          = 2;
    private float                           bottomLeftRadius        = 2;
    private float                           bottomRightRadius       = 2;
    private float                           shadowRadius            = 3;
    private float                           shadowOffsetX           = 0;
    private float                           shadowOffsetY           = 2;
    private float                           warningInsetLeft        = 3;
    private float                           warningInsetUp          = 3;
    private float                           warningInsetRight       = 3;
    private float                           warningInsetBottom      = 3;
    private String                          warningBoxBgColor       = "#ff66cc";
    private String                          tappedColor             = "#ff80d5";
    private String                          shadowColor             = "#b4b4b4";
    private boolean                         hasShadow               = true;

    private int                             contentFramePaddingLeft = 20;
    private int                             contentFramePaddingUp   = 10;
    private int                             contentFramePaddingRight= 20;
    private int                             contentFramePaddingDown = 10;

    private String                          warningMessage          = "Warning!";
    private TextStyle                       warningTextStyle        = TextStyle.BOLD;
    private float                           warningTextSize         = 15;
    private int                             warningTextAlign        = Gravity.LEFT; // Gravity of the warning text
    private String                          warningTextColor        = "#DDDDDD";
    private int                             warningTextMarginRight  = 20;
    private int                             warningTextMaxLines     = 2;
    private float                           warningTextWeight       = 0.65f;
    private Typeface                        warningTextFont         = Typeface.DEFAULT;

    private String                          actionText              = "GOT IT!";
    private TextStyle                       actionTextStyle         = TextStyle.BOLD;
    private float                           actionTextSize          = 15;
    private String                          actionTextColor         = "#DDDDDD";
    private String                          actionTextPressedColor  = "#FFFFFF";
    private int                             actionTextMaxLines      = 2;
    private float                           actionTextWeight        = 0.35f;
    private Typeface                        actionTextFont          = Typeface.DEFAULT;

    private int                             revealTime              = 200;
    private int                             dismissTime             = 200;
    private boolean                         tapToDismiss            = true;
    private RevealAnim                      revealAnim              = RevealAnim.SLIDE_UP;
    private DismissAnim                     dismissAnim             = DismissAnim.BACK_TO_BOTTOM;

    private ShapeDrawable shapeDrawable                             = null;
    private LayerDrawable layerDrawable                             = null;

    private TapListener tapListener;
    private ClickListener clickListener;
    private AnimListener animListener;

    public enum WarningPos { TOP, BOTTOM, CENTER }
    public enum WarningStyle { SIMPLE, ACTION }
    public enum TextStyle { NORMAL, BOLD, ITALIC, BOLD_ITALIC }
    public enum RevealAnim { SLIDE_UP, SLIDE_DOWN, FADE_IN, NO_ANIM }
    public enum DismissAnim { BACK_TO_TOP, BACK_TO_BOTTOM, FADE_OUT, NO_ANIM}

    private int screenWidth;
    private int screenHeight;

    private Animation revealAnimation = null;
    private Animation dismissAnimation = null;

    public Noty(Context context, String warningMessage, ViewGroup parentLayout, WarningStyle warningStyle){
        this.context = context;
        this.warningMessage = warningMessage;
        this.parentLayout = parentLayout;
        this.resources = context.getResources();
        this.warningStyle = warningStyle;

        getScreen();

        // Warning box
        this.warningBox = new RelativeLayout(this.context);
        this.rlp = new RelativeLayout.LayoutParams(warningBoxWidth, warningBoxHeight);
        this.warningBox.setGravity(Gravity.CENTER_VERTICAL);
        setWarningBoxPosition(this.warningBoxPosition);

        createShape();
        createWarning(this.warningBoxBgColor, false);

        // Content frame in warning box
        this.contentFrame = new LinearLayout(this.context);
        this.contentFrame.setOrientation(LinearLayout.HORIZONTAL);
        this.llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // bunlar match parenttı
        setWarningBoxPaddings(contentFramePaddingLeft,contentFramePaddingUp,contentFramePaddingRight,contentFramePaddingDown);

        // Warning text
        tvWarning = new TextView(this.context);
        setWarningText(this.warningMessage);
        setWarningTextSizeSp(this.warningTextSize);
        setWarningTextStyle(this.warningTextStyle);
        setWarningTextColor(this.warningTextColor);
        setWarningTextMaxLines(this.warningTextMaxLines);
        setAnimation(this.revealAnim, this.dismissAnim, this.revealTime, this.dismissTime);

        LinearLayout.LayoutParams warningParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Prevent null pointer
        tvAction = new TextView(this.context);

        if(warningStyle == WarningStyle.ACTION){
            warningParams.weight = this.warningTextWeight;
            setWarningTextAlign(this.warningTextAlign);

            // Action btn
            LinearLayout.LayoutParams actionParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            setActionText(this.actionText);
            setActionTextSizeSp(this.actionTextSize);
            setActionTextStyle(actionTextStyle);
            setActionTextColor(this.actionTextColor);
            setActionMaxLines(this.actionTextMaxLines);

            actionParams.gravity = Gravity.CENTER_VERTICAL;
            tvAction.setGravity(Gravity.RIGHT);
            tvAction.setEllipsize(TextUtils.TruncateAt.END);
            tvAction.setLayoutParams(actionParams);
            tvAction.setSingleLine(false);
            tvAction.setAllCaps(false);
        }else{
            warningParams.weight = 1;
            setWarningTextAlign(Gravity.CENTER);
        }

        warningParams.gravity = Gravity.CENTER_VERTICAL; // Center the warning in layout
        warningParams.setMargins(0,0,dpToPx(warningTextMarginRight),0);
        tvWarning.setEllipsize(TextUtils.TruncateAt.END);
        tvWarning.setSingleLine(false);
        tvWarning.setLayoutParams(warningParams);
        tvWarning.setAllCaps(false);

        this.warningBox.setOnTouchListener(new CustomTapListener());
        this.tvAction.setOnTouchListener(new CustomClickListener());
        attachAnimListener();
    }

    public interface TapListener{
        public void onTap(Noty warning);
    }

    public interface ClickListener{
        public void onClick(Noty warning);
    }

    public interface AnimListener{
        public void onRevealStart(Noty warning);
        public void onRevealEnd(Noty warning);
        public void onDismissStart(Noty warning);
        public void onDismissEnd(Noty warning);
    }

    public Noty setTapListener(TapListener listener){
        this.tapListener = listener;
        return this;
    }

    public Noty setClickListener(ClickListener listener){
        this.clickListener = listener;
        return this;
    }

    public Noty setAnimationListener(AnimListener listener){
        this.animListener = listener;
        return this;
    }


    public static Noty init(Context context, String warningMessage,
                            ViewGroup parentLayout, WarningStyle warningStyle){
        return new Noty(context, warningMessage, parentLayout, warningStyle);
    }

    public Noty setHeightDp(int warningHeight){
        this.warningBoxHeight = warningHeight;
        this.rlp.height = dpToPx(warningHeight);
        return this;
    }

    public Noty setWidthDp(int warningWidth){
        this.warningBoxWidth = warningWidth;
        this.rlp.width = dpToPx(warningWidth);
        return this;
    }

    public Noty setAlpha(float alpha){
        this.warningBox.setAlpha(alpha);
        return this;
    }

    public Noty setHeight(ViewGroup.LayoutParams lp){
        this.warningBoxHeight = lp.height;
        this.rlp.height = lp.height;
        return this;
    }

    public Noty setWidth(ViewGroup.LayoutParams lp){
        this.warningBoxWidth = lp.width;
        this.rlp.width = lp.width;
        return this;
    }

    public Noty tapToDismiss(boolean tapToDismiss){
        this.tapToDismiss = tapToDismiss;
        return this;
    }

    public Noty setAnimation(RevealAnim reveal, DismissAnim dismiss, int revealTime, int dismissTime){
        this.revealTime = revealTime;
        this.dismissTime = dismissTime;

        if(reveal == RevealAnim.SLIDE_UP){
            revealAnimation = new TranslateAnimation(0,0,this.screenHeight,0);
        }
        else if(reveal == RevealAnim.SLIDE_DOWN){
            revealAnimation = new TranslateAnimation(0,0,-this.screenHeight,0);
        }
        else if(reveal == RevealAnim.FADE_IN){
            revealAnimation = new AnimationUtils().loadAnimation(context, android.R.anim.fade_in);
        }
        else if(reveal == RevealAnim.NO_ANIM){
            revealAnimation = new TranslateAnimation(0,0,0,0);
            this.revealTime = 0;
        }

        if(dismiss == DismissAnim.BACK_TO_BOTTOM){
            dismissAnimation = new TranslateAnimation(0,0,0,this.screenHeight);
        }
        else if(dismiss == DismissAnim.BACK_TO_TOP){
            dismissAnimation = new TranslateAnimation(0,0,0,-this.screenHeight);
        }
        else if(dismiss == DismissAnim.FADE_OUT){
            dismissAnimation = new AnimationUtils().loadAnimation(context, android.R.anim.fade_out);
        }
        else if(dismiss == DismissAnim.NO_ANIM){
            dismissAnimation = new TranslateAnimation(0,0,0,0);
            this.dismissTime = 0;
        }

        return this;
    }

    private void createShape(){
        this.shapeDrawable = new ShapeDrawable();
        this.layerDrawable = new LayerDrawable(new Drawable[] { shapeDrawable });
    }

    private void createWarning(String color,boolean isToggle){
        setWarningBoxRadius(this.topLeftRadius, this.topRightRadius, this.bottomLeftRadius, this.bottomRightRadius);
        if(isToggle){
            toggleWarningbgColor(color);
        }else{
            setWarningBoxBgColor(color);
        }
        setWarningBoxShadow(this.shadowRadius, this.shadowOffsetX, this.shadowOffsetY, this.shadowColor);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        setWarningInset(this.warningInsetLeft, this.warningInsetUp, this.warningInsetRight, this.warningInsetBottom);
    }

    public Noty setWarningBoxRadius(float topLeftRadius, float topRightRadius,
                                    float bottomLeftRadius, float bottomRightRadius){
        int topLeft = dpToPx(topLeftRadius);
        int topRight = dpToPx(topRightRadius);
        int bottomLeft = dpToPx(bottomLeftRadius);
        int bottomRight = dpToPx(bottomRightRadius);

        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;

        this.shapeDrawable.setShape(new RoundRectShape(new float[] {topLeft, topLeft, topRight, topRight,
                bottomLeft, bottomLeft, bottomRight, bottomRight }, null, null));

        return this;
    }

    public Noty setShadowColor(String htmlColor){
        this.shadowColor = htmlColor;
        this.shapeDrawable.getPaint().setShadowLayer(this.shadowRadius, this.shadowOffsetX, this.shadowOffsetY, Color.parseColor(htmlColor));
        return this;
    }

    public Noty setWarningBoxBgColor(String htmlColor){
        //this.tappedColor = htmlColor;
        this.warningBoxBgColor = htmlColor;
        this.shapeDrawable.getPaint().setColor(Color.parseColor(htmlColor));
        return this;
    }

    private void toggleWarningbgColor(String htmlColor){
        this.shapeDrawable.getPaint().setColor(Color.parseColor(htmlColor));
    }

    public Noty setWarningTappedColor(String htmlColor){
        this.tappedColor = htmlColor;
        return this;
    }

    public Noty setWarningBoxShadow(float shadowRadius, float shadowOffsetX, float shadowOffsetY, String color){
        int radius = dpToPx(shadowRadius);
        int x = dpToPx(shadowOffsetX);
        int y = dpToPx(shadowOffsetY);
        this.shadowRadius = shadowRadius;
        this.shadowOffsetX = shadowOffsetX;
        this.shadowOffsetY = shadowOffsetY;
        if(color == null){
            this.shapeDrawable.getPaint().setShadowLayer(radius, x, y, Color.parseColor(this.shadowColor));
        }else{
            this.shapeDrawable.getPaint().setShadowLayer(radius, x, y, Color.parseColor(color));
            this.shadowColor = color;
        }
        return this;
    }

    public Noty setWarningInset(float warningInsetLeft, float warningInsetUp, float warningInsetRight, float warningInsetBottom){
        this.warningInsetUp = warningInsetUp;
        this.warningInsetBottom = warningInsetBottom;
        this.warningInsetLeft = warningInsetLeft;
        this.warningInsetRight = warningInsetRight;
        this.layerDrawable.setLayerInset(0, dpToPx(warningInsetLeft), dpToPx(warningInsetUp), dpToPx(warningInsetRight), dpToPx(warningInsetBottom));
        return this;
    }

    public Noty hasShadow(boolean hasShadow){
        this.hasShadow = hasShadow;
        if(!hasShadow){
            this.shadowRadius = 0;
            setWarningBoxShadow(this.shadowRadius, this.shadowOffsetX, this.shadowOffsetY, this.shadowColor);
        }
        return this;
    }

    public Noty setWarningBoxPosition(WarningPos position){
        this.rlp.addRule(getWarningPos(this.warningBoxPosition), 0);
        this.rlp.addRule(getWarningPos(position));
        this.rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.warningBoxPosition = position;
        return this;
    }

    private int getWarningPos(WarningPos position){
        if(position == WarningPos.TOP) return RelativeLayout.ALIGN_PARENT_TOP;
        else if(position == WarningPos.BOTTOM) return RelativeLayout.ALIGN_PARENT_BOTTOM;
        else if(position == WarningPos.CENTER) return RelativeLayout.CENTER_VERTICAL;
        else return RelativeLayout.ALIGN_PARENT_TOP;
    }

    public Noty setWarningBoxPaddings(int left, int top, int right, int bottom){
        int l = dpToPx(left);
        int r = dpToPx(right);
        int u = dpToPx(top);
        int d = dpToPx(bottom);
        this.contentFramePaddingLeft = left;
        this.contentFramePaddingUp = top;
        this.contentFramePaddingRight = right;
        this.contentFramePaddingDown = bottom;
        this.contentFrame.setPadding(l, u, r, d);
        return this;
    }

    public Noty setWarningBoxMargins(int left, int top, int right, int bottom){
        int l = dpToPx(left);
        int r = dpToPx(right);
        int u = dpToPx(top);
        int d = dpToPx(bottom);
        this.warningMarginLeft = left;
        this.warningMarginTop = top;
        this.warningMarginRight = right;
        this.warningMarginBottom = bottom;
        this.rlp.setMargins(l, u, r, d);
        return this;
    }

    // ===================== \\
    //    Warning TextView   \\
    // ===================== \\

    public Noty setWarningText(String warningMessage){
        this.warningMessage = warningMessage;
        this.tvWarning.setText(warningMessage);
        return this;
    }

    public Noty setWarningTextStyle(TextStyle textStyle){
        this.warningTextStyle = textStyle;
        if(textStyle == TextStyle.NORMAL) this.tvWarning.setTypeface(null, Typeface.NORMAL);
        else if(textStyle == TextStyle.ITALIC) this.tvWarning.setTypeface(null, Typeface.ITALIC);
        else if(textStyle == TextStyle.BOLD_ITALIC) this.tvWarning.setTypeface(null, Typeface.BOLD_ITALIC);
        else this.tvWarning.setTypeface(null, Typeface.BOLD);
        return this;
    }

    public Noty setWarningTextSizeDp(float size){
        this.warningTextSize = size;
        this.tvWarning.setTextSize(dpToPx(size));
        return this;
    }

    public Noty setWarningTextSizeSp(float size){
        this.tvWarning.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        this.warningTextSize = this.tvWarning.getTextSize();
        return this;
    }

    public Noty setWarningTextAlign(int align){
        this.warningTextAlign = align;
        this.tvWarning.setGravity(align);
        return this;
    }

    public Noty setWarningTextColor(String htmlColor){
        this.warningTextColor = htmlColor;
        this.tvWarning.setTextColor(Color.parseColor(htmlColor));
        return this;
    }

    public Noty setWarningTextFont(String fontPath){
        this.warningTextFont = Typeface.createFromAsset(context.getAssets(),fontPath);
        Typeface font = Typeface.createFromAsset(context.getAssets(),fontPath);
        this.tvWarning.setTypeface(font);
        return this;
    }

    public Noty setWarningTextMaxLines(int lines){
        this.warningTextMaxLines = lines;
        this.tvWarning.setMaxLines(lines);
        return this;
    }

    // ===================== \\
    //    Action TextView    \\
    // ===================== \\

    public Noty setActionText(String actionMessage){
        this.actionText = actionMessage;
        tvAction.setText(actionMessage);
        return this;
    }

    public Noty setActionTextStyle(TextStyle textStyle){
        this.actionTextStyle = textStyle;
        if(textStyle == TextStyle.NORMAL) this.tvAction.setTypeface(null, Typeface.NORMAL);
        else if(textStyle == TextStyle.ITALIC) this.tvAction.setTypeface(null, Typeface.ITALIC);
        else if(textStyle == TextStyle.BOLD_ITALIC) this.tvAction.setTypeface(null, Typeface.BOLD_ITALIC);
        else this.tvAction.setTypeface(null, Typeface.BOLD);
        return this;
    }

    public Noty setActionTextSizeDp(float size){
        this.actionTextSize = size;
        this.tvAction.setTextSize(dpToPx(size));
        return this;
    }

    public Noty setActionTextSizeSp(float size){
        this.tvAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        this.actionTextSize = this.tvAction.getTextSize();
        return this;
    }

    public Noty setActionTextColor(String htmlColor){
        this.tvAction.setTextColor(Color.parseColor(htmlColor));
        return this;
    }

    public Noty setActionFont(String fontPath){
        this.actionTextFont = Typeface.createFromAsset(context.getAssets(),fontPath);
        Typeface font = Typeface.createFromAsset(context.getAssets(),fontPath);
        this.tvAction.setTypeface(font);
        return this;
    }

    public Noty setActionMaxLines(int lines){
        this.actionTextMaxLines = lines;
        this.tvAction.setMaxLines(lines);
        return this;
    }

    public Noty setActionPressedColor(String htmlColor){
        this.actionTextPressedColor = htmlColor;
        return this;
    }

    // Show warning
    public Noty show(){

        this.warningBox.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.warningBox.setBackground(layerDrawable);

        this.warningBox.setLayoutParams(this.rlp);
        this.contentFrame.setLayoutParams(this.llp);

        this.parentLayout.addView(this.warningBox);
        this.warningBox.addView(this.contentFrame);
        this.contentFrame.addView(this.tvWarning);
        if(this.warningStyle == WarningStyle.ACTION){
            this.contentFrame.addView(this.tvAction);
        }

        revealAnimation.setDuration(this.revealTime);
        revealAnimation.setFillAfter(true);
        revealAnimation.setInterpolator(context, android.R.anim.decelerate_interpolator);
        this.warningBox.setVisibility(View.VISIBLE);
        this.warningBox.startAnimation(this.revealAnimation);

        dismissAnimation.setDuration(dismissTime);
        dismissAnimation.setFillAfter(true);
        dismissAnimation.setInterpolator(context, android.R.anim.accelerate_interpolator);
        warningBox.setVisibility(View.VISIBLE);

        return this;

    }

    private void attachAnimListener(){
        this.revealAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(animListener != null){
                    animListener.onRevealStart(Noty.this);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(animListener != null){
                    animListener.onRevealEnd(Noty.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        this.dismissAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(animListener != null){
                    animListener.onDismissStart(Noty.this);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(animListener != null){
                    animListener.onDismissEnd(Noty.this);
                }
                parentLayout.removeView(warningBox);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    public class CustomTapListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    createWarning(tappedColor, true);
                    warningBox.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    warningBox.setBackground(layerDrawable);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if(tapToDismiss){
                        createWarning(warningBoxBgColor, false);
                        warningBox.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        warningBox.setBackground(layerDrawable);
                        warningBox.startAnimation(dismissAnimation);
                    }
                    if(tapListener != null){
                        tapListener.onTap(Noty.this);
                    }
                    break;
            }
            return true;
        }
    }

    public class CustomClickListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    setActionTextColor(actionTextPressedColor);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    setActionTextColor(actionTextColor);
                    warningBox.startAnimation(dismissAnimation);
                    if(clickListener != null){
                        clickListener.onClick(Noty.this);
                    }
                    break;
            }
            return true;
        }
    }

    private void getScreen(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screenWidth = size.x;
        this.screenHeight = size.y;
    }

    private int dpToPx(float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

}
