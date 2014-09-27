package com.example.compoundbuttonview.view;

import com.example.compoundbuttonview.R;

import android.R.integer;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;

public class SwitchButton extends CompoundButton {
	private static final int TOUCH_MODE_IDLE = 0;
	private static final int TOUCH_MODE_DOWN = 1;
	private static final int TOUCH_MODE_DRAGGING = 2;
	private static final int SANS = 1;
	private static final int SERIF = 2;
	private static final int MONOSPACE = 3;
	private static final int BOLD = 1;// 字体加粗
	private static final int ITALIC = 2;// 字体倾斜

	private Drawable mThumbDrawable;// 按钮滑动背景轨迹
	private Drawable mTrackDrawable;// 按钮

	private int mThumbTextPadding;// 按钮上的字体大小
	private int mSwitchMinWidth;// 按钮最小宽度
	private int mSwitchPadding;// 按钮Padding属性值

	private CharSequence mTextOnCharSequence;// 按钮打开是现实的文字
	private CharSequence mTextOffCharSequence;

	private int mTouchMode;// 按钮触摸的模式
	private int mTouchSlop;
	private float mTouchX;
	private float mTouchY;
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
	private int mMinFlingVelocity;
	private float mThumbPosition;// 按钮位置
	private int mThumbWidth;

	private int mSwitchWidth;// 开关空间宽度
	private int mSwitchHeight;

	private int mSwitchLeft;
	private int mSwitchRight;
	private int mSwitchTop;
	private int mSwitchBottom;

	private TextPaint mTextPaint;
	private ColorStateList mTextColorStateList;
	private Layout mOnLayout;
	private Layout mOffLayout;

	private Context context;
	private final Rect mTempRect = new Rect();
	private static final int[] CHECKED_STATE_SET = { android.R.attr.checked };

	public SwitchButton(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		this(context, attrs, R.attr.switchStyle);
		this.context = context;

	}

	public SwitchButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		mTextPaint.density = resources.getDisplayMetrics().density;
		//初始化相关资源
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SwitchButton, defStyle, 0);
		mThumbDrawable = ta.getDrawable(R.styleable.SwitchButton_thumb);// 获取配置的轨迹资源
		mTrackDrawable = ta.getDrawable(R.styleable.SwitchButton_track);// 获取配置的按钮资源
		mTextOffCharSequence = ta.getText(R.styleable.SwitchButton_textOff);// 获取按钮关闭下的文字显示
		mTextOnCharSequence = ta.getText(R.styleable.SwitchButton_textOn);// 获取按钮打开时候的文字显示
		mThumbTextPadding = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_thumbTextPadding, 0);// 获取配置的按钮的文字大小
		mSwitchMinWidth = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_switchMinWidth, 0);// 获取配置中的控件最小宽度
		mSwitchPadding = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_switchPadding, 0);// 获取配置文件控件的Padding属性
		int appearance = ta.getResourceId(
				R.styleable.SwitchButton_switchTextAppearance, 0);// 获取配置文件中的显示字体格式
		if (appearance != 0) {
			setSwitchAppearance(context, appearance);// 设置字体属性
		}
		ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
		mTouchSlop = viewConfiguration.getScaledTouchSlop();// 获得能够进行手势滑动的距离
		mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();// 获得允许执行一个fling手势动作的最小速度值
		refreshDrawableState();
		setChecked(isChecked());
		ta.recycle();
	}

	@Override
	public void setChecked(boolean isChecked) {
		super.setChecked(isChecked);
		mThumbPosition = isChecked ? getThumbScrollRange() : 0;
		invalidate();
	}
/**
 * 按钮运动范围
 * @return
 */
	private int getThumbScrollRange() {
		// TODO Auto-generated method stub
		if (mThumbDrawable == null)
			return 0;
		mTrackDrawable.getPadding(mTempRect);// ？？？？？？？？
		return mSwitchWidth - mThumbWidth - mTempRect.left - mTempRect.right;// //???????????????
	}

	private void setSwitchAppearance(Context context, int appearance) {
		// TODO Auto-generated method stub
		TypedArray ta = context.obtainStyledAttributes(appearance,
				R.styleable.TextApperance);// 参数1：所需样式资源，参数2：详细样式资源集合
		ColorStateList colorStateList;
		int ts;
		colorStateList = ta
				.getColorStateList(R.styleable.TextApperance_textClolor);
		if (colorStateList != null) {
			mTextColorStateList = colorStateList;

		} else {
			mTextColorStateList = getTextColors();
		}
		ts = ta.getDimensionPixelSize(R.styleable.TextApperance_textSize, 0);// 获取字体的大小
		if (ts != 0) {
			if (ts != mTextPaint.getTextSize()) {
				mTextPaint.setTextSize(ts);
				requestLayout();
			}
		}
		int typeFaceIndex, styleIndex;
		typeFaceIndex = ta.getInt(R.styleable.TextApperance_textFace, 0);
		styleIndex = ta.getInt(R.styleable.TextApperance_textSize, 0);
		setSwitchTypeFaceByIndex(typeFaceIndex, styleIndex);
		ta.recycle();
	}

	private void setSwitchTypeFaceByIndex(int typeFaceIndex, int styleIndex) {
		// TODO Auto-generated method stub
		Typeface tf = null;
		switch (typeFaceIndex) {
		case SANS:
			tf = Typeface.SANS_SERIF;// 模人物陈贤字体风格
			break;
		case SERIF:
			tf = Typeface.SERIF;// 默认的衬线字体风格
			break;
		case MONOSPACE:
			tf = Typeface.MONOSPACE;// 默认的等宽字体风格
			break;
		default:
			break;
		}
		setSwitchTypeFace(tf, styleIndex);
	}

	private void setSwitchTypeFace(Typeface tf, int styleIndex) {
		// TODO Auto-generated method stub
		if (styleIndex > 0) {
			if (tf == null) {
				tf = Typeface.defaultFromStyle(styleIndex);
			} else
				tf = Typeface.create(tf, styleIndex);
			setSwitchTypeFace(tf);// 设置TYpe
			int typeFaceStyle = tf != null ? tf.getStyle() : 0;
			int need = styleIndex & ~typeFaceStyle;
			mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);// 设置字体倾斜
		} else {
			mTextPaint.setFakeBoldText(false);
			mTextPaint.setTextSkewX(0);
			setSwitchTypeFace(tf);
		}

	}

	private void setSwitchTypeFace(Typeface tf) {
		// TODO Auto-generated method stub
		if (mTextPaint.getTypeface() != tf) {
			mTextPaint.setTypeface(tf);
			requestLayout();
			invalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);//寬度模式
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);//高度模式
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (mOffLayout == null) {
			mOffLayout = maleLyout(mTextOffCharSequence);//新建布局
		}
		if (mOnLayout == null) {
			mOnLayout = maleLyout(mTextOnCharSequence);
		}
		mTrackDrawable.getPadding(mTempRect);
		final int maxTextWidth = Math.max(mOffLayout.getWidth(),
				mOnLayout.getWidth());//最宽字体布局
		final int switchWidth = Math.max(mSwitchMinWidth, maxTextWidth * 2
				+ mThumbTextPadding * 4 + mTempRect.left + mTempRect.right);
		final int switchHeight = mTrackDrawable.getIntrinsicHeight();// 对象内在的高度
		mThumbWidth = mThumbTextPadding * 2 + maxTextWidth;// 按钮宽度
		switch (widthMode) {
		case MeasureSpec.AT_MOST:
			widthSize = Math.min(widthSize, switchWidth);
			break;
		case MeasureSpec.UNSPECIFIED:
			widthSize = switchWidth;
			break;
		case MeasureSpec.EXACTLY:
			break;
		default:
			break;
		}
		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			heightSize = Math.min(heightSize, switchHeight);
			break;
		case MeasureSpec.EXACTLY:
			break;
		case MeasureSpec.UNSPECIFIED:
			heightSize = switchHeight;
			break;
		default:
			break;
		}

		mSwitchWidth = switchWidth;
		mSwitchHeight = switchHeight;
		final int measuredHeiht = getMeasuredHeight();// 会的控件高度
		if (measuredHeiht < switchHeight) {
			setMeasuredDimension(getMeasuredWidth(), switchHeight);
		}
	}

	/**
	 * 参数含义： 1.字符串子资源 2 .画笔对象 3.layout的宽度，字符串超出宽度时自动换行。
	 * 4.layout的样式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种。
	 * 5.相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。 6.相对行间距，0表示0个像素。 实际行间距等于这两者的和。
	 * 7.还不知道是什么意思，参数名是boolean includepad。
	 * 需要指出的是这个layout是默认画在Canvas的(0,0)点的，如果需要调整位置只能在draw之前移Canvas的起始坐标
	 * canvas.translate(x,y);
	 * 
	 * @param mTextCharSequence
	 * @return
	 */
	private Layout maleLyout(CharSequence mTextCharSequence) {
		// TODO Auto-generated method stub
		System.out.println("text：" + mTextCharSequence);
		return new StaticLayout(mTextCharSequence, mTextPaint,
				(int) Math.ceil(Layout.getDesiredWidth(mTextCharSequence,
						mTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1f, 0,
				true);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		mThumbPosition = isChecked() ? getThumbScrollRange() : 0;//初始化的按钮位置
		int switchRight = getWidth() - getPaddingRight();
		int switchLeft = switchRight - mSwitchWidth;
		int switchTop = 0;
		int switchBottom = 0;
		switch (getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
		case Gravity.TOP:
			switchTop = getPaddingTop();
			switchBottom = switchTop + mSwitchHeight;
			break;
		case Gravity.CENTER_VERTICAL:
			switchTop = getHeight() / 2 - mSwitchHeight / 2;
			switchBottom = switchTop + mSwitchHeight;
			break;
		case Gravity.BOTTOM:
			switchBottom = getHeight() - getPaddingBottom();
			switchTop = mSwitchHeight - switchBottom;
		default:
			break;
		}
		//获取控件上下左右的位置
		mSwitchLeft = switchLeft;
		mSwitchTop = switchTop;
		mSwitchBottom = switchBottom;
		mSwitchRight = switchRight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int switchLeft = mSwitchLeft;
		int switchTop = mSwitchTop;
		int switchRight = mSwitchRight;
		int switchBottom = mSwitchBottom;
		mTrackDrawable.setBounds(switchLeft, switchTop, switchRight,
				switchBottom);
		mTrackDrawable.draw(canvas);
		canvas.save();// save和restore之间，往往夹杂的是对Canvas的特殊操作。
		mTrackDrawable.getPadding(mTempRect);
		//轨迹范围的上下左右位置
		int switchInnerLeft = switchLeft + mTempRect.left;
		int switchInnerTop = switchTop + mTempRect.top;
		int switchInnerRight = switchRight - mTempRect.right;
		int switchInnerBottom = switchBottom - mTempRect.bottom;
		canvas.clipRect(switchInnerLeft, switchInnerTop, switchInnerRight,
				switchInnerBottom);// 裁剪轨迹显示区域
		mThumbDrawable.getPadding(mTempRect);// 按钮
		final int thumbPos = (int) (mThumbPosition + 0.5f);
		int thumbLeft = switchInnerLeft + thumbPos - mTempRect.left;
		int thumbRight = switchInnerLeft + thumbPos + mTempRect.right
				+ mThumbWidth;
		mThumbDrawable.setBounds(thumbLeft, switchInnerTop, thumbRight,
				switchInnerBottom);//围绕按钮的矩形
		mThumbDrawable.draw(canvas);// 画出按钮

		if (mTextColorStateList != null) {// 设置字体颜色
			mTextPaint.setColor(mTextColorStateList.getColorForState(
					getDrawableState(), mTextColorStateList.getDefaultColor()));
		}
		mTextPaint.drawableState = getDrawableState();
		Layout switchTextLayout = getTargetCheckedState() ? mOnLayout
				: mOffLayout;// 设置文字布局
		canvas.translate(
				(thumbLeft + thumbRight) / 2 - switchTextLayout.getWidth() / 2,
				(switchInnerTop + switchInnerBottom) / 2
						- switchTextLayout.getHeight() / 2);
		switchTextLayout.draw(canvas);
		canvas.restore();

	}
/**
 * 判断位置是否过中间
 */
	private boolean getTargetCheckedState() {
		// TODO Auto-generated method stub
		return mThumbPosition > getThumbScrollRange() / 2;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mVelocityTracker.addMovement(event);// 添加事件，获得速度
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {// 按下事件
			final float x = event.getX();
			final float y = event.getY();
			if (isEnabled() && hitThumb(x, y)) {//可以使用，在按鈕矩形框內点击
				mTouchMode = TOUCH_MODE_DOWN;
				mTouchX = x;
				mTouchY = y;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			switch (mTouchMode) {
			case TOUCH_MODE_IDLE:// 无动作
				return true;
			case TOUCH_MODE_DOWN: {// 已按下
				final float x2 = event.getX();
				final float y2 = event.getY();
				if (Math.abs(x2 - mTouchX) > mTouchSlop
						|| Math.abs(y2 - mTouchY) > mTouchSlop) {// 拖动动作
					mTouchMode = TOUCH_MODE_DRAGGING;// 拖动模式
					getParent().requestDisallowInterceptTouchEvent(true);// 父控件别处理拖动事件
					mTouchX = x2;
					mTouchY = y2;
					return true;
				}
				break;
			}
			case TOUCH_MODE_DRAGGING: {// 拖动
				final float x3 = event.getX();
				final float dx = x3 - mTouchX;// 拖动距离
				float newPos = Math.max(0,
						Math.min(mThumbPosition + dx, getThumbScrollRange()));// 获得按钮的最新位置
				if (newPos != mThumbPosition) {// 发生改变重绘
					mThumbPosition = newPos;
					mTouchX = x3;
					invalidate();
				}
				return true;
			}
			}
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:// 事件被父控件截断,滑动到父控件之上触发
		{
			if (mTouchMode == TOUCH_MODE_DRAGGING) {
				stopDrg(event);
				return true;
			}
			mTouchMode = TOUCH_MODE_IDLE;
			mVelocityTracker.clear();
			break;
		}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 结束事件并且归位
	 * @param event
	 */
	private void stopDrg(MotionEvent event) {
		// TODO Auto-generated method stub
		mTouchMode = TOUCH_MODE_IDLE;
		boolean commitChange = event.getAction() == MotionEvent.ACTION_UP
				&& isEnabled();
		cancleSupertouch(event);
		if (commitChange) {// 状态改变打开状态
			boolean newState;
			mVelocityTracker.computeCurrentVelocity(1000);// 1秒运动像素，速度单位
			float xvel = mVelocityTracker.getXVelocity();// x方向的速度
			if (xvel > mMinFlingVelocity) {//达到一定速度就去转化状态
				newState = xvel > 0;
			} else {//未达到就去判断现在的位置
				newState = getTargetCheckedState();
			}
			animateThumbToCheckedState(newState);// 设置状态进行重绘
		} else {
			animateThumbToCheckedState(isChecked());
		}
	}

	private void animateThumbToCheckedState(boolean newState) {
		// TODO Auto-generated method stub
		setChecked(newState);
	}

	/**
	 * 结束事件
	 * 
	 * @param event
	 */
	private void cancleSupertouch(MotionEvent event) {
		// TODO Auto-generated method stub
		MotionEvent cancleEvent = MotionEvent.obtain(event);
		cancleEvent.setAction(MotionEvent.ACTION_CANCEL);
		super.onTouchEvent(cancleEvent);
		cancleEvent.recycle();

	}

	/**
	 * 判断ＸＹ按下坐标是否在按钮之中
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean hitThumb(float x, float y) {
		// TODO Auto-generated method stub
		mThumbDrawable.getPadding(mTempRect);
		final int thumbLeft = mSwitchLeft + (int) (mThumbPosition + 0.5f)
				- mTouchSlop;
		final int thumbTop = mSwitchTop - mTouchSlop;
		final int thumbRight = thumbLeft + mThumbWidth + mTempRect.right
				+ mTempRect.left + mTouchSlop;
		final int thumbBottom = mSwitchBottom + mTouchSlop;
		return x > thumbLeft && x < thumbRight && y > thumbTop
				&& y < thumbBottom;
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		populateAccessibilityEvent(event);
		return false;
	}

	public void populateAccessibilityEvent(AccessibilityEvent event) {
		if (isChecked()) {
			CharSequence text = mOnLayout.getText();
			if (TextUtils.isEmpty(text)) {
				text = context.getString(R.string.switch_on);
			}
			event.getText().add(text);
		} else {
			CharSequence text = mOffLayout.getText();
			if (TextUtils.isEmpty(text)) {
				text = context.getString(R.string.switch_off);
			}
			event.getText().add(text);
		}
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mThumbDrawable
				|| who == mTrackDrawable;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	/** 获取选中状态下的显示文字 */
	public CharSequence getTextOn() {
		return mTextOnCharSequence;
	}

	/** 设定选中状态下的显示文字 */
	public void setTextOn(CharSequence textOn) {
		mTextOnCharSequence = textOn;
		requestLayout();
	}

	/** 获取非选中状态下的显示文字 */
	public CharSequence getTextOff() {
		return mTextOffCharSequence;
	}

	/** 设定非选中状态下的显示文字 */
	public void setTextOff(CharSequence textOff) {
		mTextOffCharSequence = textOff;
		requestLayout();
	}
}
