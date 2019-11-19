package com.freak.marquee;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.AnimRes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freak
 * @date 2019/11/19.
 */
@SuppressWarnings("ALL")
public class MarqueeView extends ViewFlipper {

    private int interval = 3000;
    private boolean hasSetAnimDuration = false;
    private int animDuration = 1000;
    private int textSize = 14;
    private int textColor = Color.BLACK;
    private boolean singleLine = false;

    private int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;

    private boolean hasSetDirection = false;
    private int direction = DIRECTION_BOTTOM_TO_TOP;
    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = 2;
    private static final int DIRECTION_LEFT_TO_RIGHT = 3;
    private boolean hasAnimInRes = false;
    private boolean hasAnimOutRes = false;

    @AnimRes
    private int inAnimResId = R.anim.anim_bottom_in;
    @AnimRes
    private int outAnimResId = R.anim.anim_top_out;

    private int position;
    private List<? extends CharSequence> notices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int gravityType;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.MarqueeView_MarqueeViewInterval) {
                interval = typedArray.getInteger(attr, interval);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewAnimDuration) {
                hasSetAnimDuration = typedArray.hasValue(attr);
                animDuration = typedArray.getInteger(attr, animDuration);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewSingleLine) {
                singleLine = typedArray.getBoolean(attr, false);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewTextSize) {
                textSize = (int) typedArray.getDimension(attr, textSize);
                textSize = MarqueeViewUtils.px2sp(context, textSize);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewTextColor) {
                textColor = typedArray.getColor(attr, textColor);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewGravity) {
                gravityType = typedArray.getInt(attr, GRAVITY_LEFT);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewDirection) {
                hasSetDirection = typedArray.hasValue(attr);
                direction = typedArray.getInt(attr, direction);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewAnimIn) {
                hasAnimInRes = typedArray.hasValue(attr);
                inAnimResId = typedArray.getResourceId(attr, inAnimResId);
            } else if (attr == R.styleable.MarqueeView_MarqueeViewAnimOut) {
                hasAnimOutRes = typedArray.hasValue(attr);
                outAnimResId = typedArray.getResourceId(attr, outAnimResId);
            }
        }
        typedArray.recycle();

        switch (gravityType) {
            case GRAVITY_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            default:
                break;
        }

        if (!(hasAnimInRes && hasAnimOutRes)) {
            if (hasSetDirection) {
                switch (direction) {
                    case DIRECTION_BOTTOM_TO_TOP:
                        inAnimResId = R.anim.anim_bottom_in;
                        outAnimResId = R.anim.anim_top_out;
                        break;
                    case DIRECTION_TOP_TO_BOTTOM:
                        inAnimResId = R.anim.anim_top_in;
                        outAnimResId = R.anim.anim_bottom_out;
                        break;
                    case DIRECTION_RIGHT_TO_LEFT:
                        inAnimResId = R.anim.anim_right_in;
                        outAnimResId = R.anim.anim_left_out;
                        break;
                    case DIRECTION_LEFT_TO_RIGHT:
                        inAnimResId = R.anim.anim_left_in;
                        outAnimResId = R.anim.anim_right_out;
                        break;
                    default:
                        break;
                }
            } else {
                inAnimResId = R.anim.anim_bottom_in;
                outAnimResId = R.anim.anim_top_out;
            }
        }


        setFlipInterval(interval);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice 字符串
     */
    public void startWithText(String notice) {
        startWithText(notice, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice       字符串
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    @SuppressWarnings("deprecation")
    public void startWithText(final String notice, final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        if (TextUtils.isEmpty(notice)) {
            return;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                startWithFixedWidth(notice, inAnimResId, outAnimResID);
            }
        });
    }

    /**
     * 根据字符串和宽度，启动翻页公告
     *
     * @param notice 字符串
     */
    private void startWithFixedWidth(String notice, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        int noticeLength = notice.length();
        int width = MarqueeViewUtils.px2dip(getContext(), getWidth());
        if (width == 0) {
            throw new RuntimeException("Please set the width of MarqueeView !");
        }
        int limit = width / textSize;
        List list = new ArrayList();

        if (noticeLength <= limit) {
            list.add(notice);
        } else {
            int size = noticeLength / limit + (noticeLength % limit != 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int startIndex = i * limit;
                int endIndex = ((i + 1) * limit >= noticeLength ? noticeLength : (i + 1) * limit);
                list.add(notice.substring(startIndex, endIndex));
            }
        }

        if (notices == null) {
            notices = new ArrayList<>();
        }
        notices.clear();
        notices.addAll(list);
        start(inAnimResId, outAnimResID);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices 字符串列表
     */
    public void startWithList(List<? extends CharSequence> notices) {
        startWithList(notices, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices      字符串列表
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    public void startWithList(List<? extends CharSequence> notices, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        if (MarqueeViewUtils.isEmpty(notices)) {
            return;
        }
        setNotices(notices);
        start(inAnimResId, outAnimResID);
    }

    private boolean start(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        removeAllViews();
        clearAnimation();

        position = 0;
        addView(createTextView(notices.get(position)));

        if (notices.size() > 1) {
            setInAndOutAnimation(inAnimResId, outAnimResID);
            startFlipping();
        }

        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    position++;
                    if (position >= notices.size()) {
                        position = 0;
                    }
                    View view = createTextView(notices.get(position));
                    if (view.getParent() == null) {
                        addView(view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        return true;
    }

    private TextView createTextView(CharSequence text) {
        TextView textView = (TextView) getChildAt((getDisplayedChild() + 1) % 3);
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setGravity(gravity);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setSingleLine(singleLine);
            textView.setIncludeFontPadding(false);
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getPosition(), (TextView) v);
                }
            }
        });
        textView.setText(Html.fromHtml(String.valueOf(text)));
        textView.setTag(position);
        return textView;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<? extends CharSequence> getNotices() {
        return notices;
    }

    public void setNotices(List<? extends CharSequence> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        if (hasSetAnimDuration) {
            inAnim.setDuration(animDuration);
        }
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        if (hasSetAnimDuration) {
            outAnim.setDuration(animDuration);
        }
        setOutAnimation(outAnim);
    }

}
