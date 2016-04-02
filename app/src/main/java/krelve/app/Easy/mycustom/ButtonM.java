package krelve.app.Easy.mycustom;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * 重写Button,自定义Button样式
 *
 * @author landptf
 * @date 2015-6-8
 */
public class ButtonM extends Button {
    private GradientDrawable gradientDrawable;//控件的样式
    private String backColors = " ";//背景色，String类型
    private int backColori = 0;//背景色，int类型
    private String backColorSelecteds = " ";//按下后的背景色，String类型
    private int backColorSelectedi = 0;//按下后的背景色，int类型
    private int backGroundImage = 0;//背景图，只提供了Id
    private int backGroundImageSeleted = 0;//按下后的背景图，只提供了Id
    private String textColors = " ";//文字颜色，String类型
    private int textColori = 0;//文字颜色，int类型
    private String textColorSeleteds = " ";//按下后的文字颜色，String类型
    private int textColorSeletedi = 0;//按下后的文字颜色，int类型
    private float radius = 8;//圆角半径
    private int shape = 0;//圆角样式，矩形、圆形等，由于矩形的Id为0，默认为矩形
    private Boolean fillet = false;//是否设置圆角

    public ButtonM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonM(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonM(Context context) {
        this(context, null);
    }

    private void init() {
        //将Button的默认背景色改为透明，本人不喜欢原来的颜色
        if (fillet) {
            if (gradientDrawable == null) {
                gradientDrawable = new GradientDrawable();
            }
            gradientDrawable.setColor(Color.TRANSPARENT);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }
        //设置文字默认居中
        setGravity(Gravity.CENTER);
        //设置Touch事件
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                //按下改变样式
                setColor(event.getAction());
                //此处设置为false，防止Click事件被屏蔽
                return false;
            }
        });
    }

    //改变样式
    private void setColor(int state) {
        if (state == MotionEvent.ACTION_DOWN) {
            //按下
            if (backColorSelectedi != 0) {
                //先判断是否设置了按下后的背景色int型
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = new GradientDrawable();
                    }
                    gradientDrawable.setColor(backColorSelectedi);
                } else {
                    setBackgroundColor(backColorSelectedi);
                }
            } else if (!backColorSelecteds.equals("")) {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = new GradientDrawable();
                    }
                    gradientDrawable.setColor(Color.parseColor(backColorSelecteds));
                } else {
                    setBackgroundColor(Color.parseColor(backColorSelecteds));
                }
            }
            //判断是否设置了按下后文字的颜色
            if (textColorSeletedi != 0) {
                setTextColor(textColorSeletedi);
            } else if (!textColorSeleteds.equals("")) {
                setTextColor(Color.parseColor(textColorSeleteds));
            }
            //判断是否设置了按下后的背景图
            if (backGroundImageSeleted != 0) {
                setBackgroundResource(backGroundImageSeleted);
            }
        }
        if (state == MotionEvent.ACTION_UP) {
            //抬起
            if (backColori == 0 && backColors.equals("")) {
                //如果没有设置背景色，默认改为透明
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = new GradientDrawable();
                    }
                    gradientDrawable.setColor(Color.TRANSPARENT);
                } else {
                    setBackgroundColor(Color.TRANSPARENT);
                }
            } else if (backColori != 0) {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = new GradientDrawable();
                    }
                    gradientDrawable.setColor(backColori);
                } else {
                    setBackgroundColor(backColori);
                }
            } else {
                if (fillet) {
                    if (gradientDrawable == null) {
                        gradientDrawable = new GradientDrawable();
                    }
                    gradientDrawable.setColor(Color.parseColor(backColors));
                } else {
                    setBackgroundColor(Color.parseColor(backColors));
                }
            }
            //如果为设置字体颜色，默认为黑色
            if (textColori == 0 && textColors.equals("")) {
                setTextColor(Color.BLACK);
            } else if (textColori != 0) {
                setTextColor(textColori);
            } else {
                setTextColor(Color.parseColor(textColors));
            }
            if (backGroundImage != 0) {
                setBackgroundResource(backGroundImage);
            }
        }
    }

    /**
     * 设置按钮的背景色,如果未设置则默认为透明
     *
     * @param backColor
     */
    public void setBackColor(String backColor) {
        this.backColors = backColor;
        if (backColor.equals("")) {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = new GradientDrawable();
                }
                gradientDrawable.setColor(Color.TRANSPARENT);
            } else {
                setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = new GradientDrawable();
                }
                gradientDrawable.setColor(Color.parseColor(backColor));
            } else {
                setBackgroundColor(Color.parseColor(backColor));
            }
        }
    }

    /**
     * 设置按钮的背景色,如果未设置则默认为透明
     *
     * @param backColor
     */
    public void setBackColor(int backColor) {
        this.backColori = backColor;
        if (backColori == 0) {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = new GradientDrawable();
                }
                gradientDrawable.setColor(Color.TRANSPARENT);
            } else {
                setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            if (fillet) {
                if (gradientDrawable == null) {
                    gradientDrawable = new GradientDrawable();
                }
                gradientDrawable.setColor(backColor);
            } else {
                setBackgroundColor(backColor);
            }
        }
    }

    /**
     * 设置按钮按下后的颜色
     *
     * @param backColorSelected
     */
    public void setBackColorSelected(int backColorSelected) {
        this.backColorSelectedi = backColorSelected;
    }

    /**
     * 设置按钮按下后的颜色
     *
     * @param backColorSelected
     */
    public void setBackColorSelected(String backColorSelected) {
        this.backColorSelecteds = backColorSelected;
    }

    /**
     * 设置按钮的背景图
     *
     * @param backGroundImage
     */
    public void setBackGroundImage(int backGroundImage) {
        this.backGroundImage = backGroundImage;
        if (backGroundImage != 0) {
            setBackgroundResource(backGroundImage);
        }
    }

    /**
     * 设置按钮按下的背景图
     *
     * @param backGroundImageSeleted
     */
    public void setBackGroundImageSeleted(int backGroundImageSeleted) {
        this.backGroundImageSeleted = backGroundImageSeleted;
    }

    /**
     * 设置按钮圆角半径大小
     *
     * @param radius
     */
    public void setRadius(float radius) {
        if (gradientDrawable == null) {
            gradientDrawable = new GradientDrawable();
        }
        gradientDrawable.setCornerRadius(radius);
    }

    /**
     * 设置按钮文字颜色
     *
     * @param textColor
     */
    public void setTextColors(String textColor) {
        this.textColors = textColor;
        setTextColor(Color.parseColor(textColor));
    }

    /**
     * 设置按钮文字颜色
     *
     * @param textColor
     */
    public void setTextColori(int textColor) {
        this.textColori = textColor;
        setTextColor(textColor);
    }

    /**
     * 设置按钮按下的文字颜色
     *
     * @param textColor
     */
    public void setTextColorSelected(String textColor) {
        this.textColorSeleteds = textColor;
    }

    /**
     * 设置按钮按下的文字颜色
     *
     * @param textColor
     */
    public void setTextColorSelected(int textColor) {
        this.textColorSeletedi = textColor;
    }

    /**
     * 按钮的形状
     *
     * @param shape
     */
    public void setShape(int shape) {
        this.shape = shape;
    }

    /**
     * 设置其是否为圆角
     *
     * @param fillet
     */
    @SuppressWarnings("deprecation")
    public void setFillet(Boolean fillet) {
        this.fillet = fillet;
        if (fillet) {
            if (gradientDrawable == null) {
                gradientDrawable = new GradientDrawable();
            }
            //GradientDrawable.RECTANGLE
            gradientDrawable.setShape(shape);
            gradientDrawable.setCornerRadius(radius);
            setBackgroundDrawable(gradientDrawable);
        }
    }
}