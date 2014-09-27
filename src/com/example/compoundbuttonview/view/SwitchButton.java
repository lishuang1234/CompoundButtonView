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
	private static final int BOLD = 1;// ����Ӵ�
	private static final int ITALIC = 2;// ������б

	private Drawable mThumbDrawable;// ��ť���������켣
	private Drawable mTrackDrawable;// ��ť

	private int mThumbTextPadding;// ��ť�ϵ������С
	private int mSwitchMinWidth;// ��ť��С���
	private int mSwitchPadding;// ��ťPadding����ֵ

	private CharSequence mTextOnCharSequence;// ��ť������ʵ������
	private CharSequence mTextOffCharSequence;

	private int mTouchMode;// ��ť������ģʽ
	private int mTouchSlop;
	private float mTouchX;
	private float mTouchY;
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
	private int mMinFlingVelocity;
	private float mThumbPosition;// ��ťλ��
	private int mThumbWidth;

	private int mSwitchWidth;// ���ؿռ���
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
		//��ʼ�������Դ
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SwitchButton, defStyle, 0);
		mThumbDrawable = ta.getDrawable(R.styleable.SwitchButton_thumb);// ��ȡ���õĹ켣��Դ
		mTrackDrawable = ta.getDrawable(R.styleable.SwitchButton_track);// ��ȡ���õİ�ť��Դ
		mTextOffCharSequence = ta.getText(R.styleable.SwitchButton_textOff);// ��ȡ��ť�ر��µ�������ʾ
		mTextOnCharSequence = ta.getText(R.styleable.SwitchButton_textOn);// ��ȡ��ť��ʱ���������ʾ
		mThumbTextPadding = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_thumbTextPadding, 0);// ��ȡ���õİ�ť�����ִ�С
		mSwitchMinWidth = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_switchMinWidth, 0);// ��ȡ�����еĿؼ���С���
		mSwitchPadding = ta.getDimensionPixelSize(
				R.styleable.SwitchButton_switchPadding, 0);// ��ȡ�����ļ��ؼ���Padding����
		int appearance = ta.getResourceId(
				R.styleable.SwitchButton_switchTextAppearance, 0);// ��ȡ�����ļ��е���ʾ�����ʽ
		if (appearance != 0) {
			setSwitchAppearance(context, appearance);// ������������
		}
		ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
		mTouchSlop = viewConfiguration.getScaledTouchSlop();// ����ܹ��������ƻ����ľ���
		mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();// �������ִ��һ��fling���ƶ�������С�ٶ�ֵ
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
 * ��ť�˶���Χ
 * @return
 */
	private int getThumbScrollRange() {
		// TODO Auto-generated method stub
		if (mThumbDrawable == null)
			return 0;
		mTrackDrawable.getPadding(mTempRect);// ����������������
		return mSwitchWidth - mThumbWidth - mTempRect.left - mTempRect.right;// //???????????????
	}

	private void setSwitchAppearance(Context context, int appearance) {
		// TODO Auto-generated method stub
		TypedArray ta = context.obtainStyledAttributes(appearance,
				R.styleable.TextApperance);// ����1��������ʽ��Դ������2����ϸ��ʽ��Դ����
		ColorStateList colorStateList;
		int ts;
		colorStateList = ta
				.getColorStateList(R.styleable.TextApperance_textClolor);
		if (colorStateList != null) {
			mTextColorStateList = colorStateList;

		} else {
			mTextColorStateList = getTextColors();
		}
		ts = ta.getDimensionPixelSize(R.styleable.TextApperance_textSize, 0);// ��ȡ����Ĵ�С
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
			tf = Typeface.SANS_SERIF;// ģ�������������
			break;
		case SERIF:
			tf = Typeface.SERIF;// Ĭ�ϵĳ���������
			break;
		case MONOSPACE:
			tf = Typeface.MONOSPACE;// Ĭ�ϵĵȿ�������
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
			setSwitchTypeFace(tf);// ����TYpe
			int typeFaceStyle = tf != null ? tf.getStyle() : 0;
			int need = styleIndex & ~typeFaceStyle;
			mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);// ����������б
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
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);//����ģʽ
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);//�߶�ģʽ
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (mOffLayout == null) {
			mOffLayout = maleLyout(mTextOffCharSequence);//�½�����
		}
		if (mOnLayout == null) {
			mOnLayout = maleLyout(mTextOnCharSequence);
		}
		mTrackDrawable.getPadding(mTempRect);
		final int maxTextWidth = Math.max(mOffLayout.getWidth(),
				mOnLayout.getWidth());//������岼��
		final int switchWidth = Math.max(mSwitchMinWidth, maxTextWidth * 2
				+ mThumbTextPadding * 4 + mTempRect.left + mTempRect.right);
		final int switchHeight = mTrackDrawable.getIntrinsicHeight();// �������ڵĸ߶�
		mThumbWidth = mThumbTextPadding * 2 + maxTextWidth;// ��ť���
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
		final int measuredHeiht = getMeasuredHeight();// ��Ŀؼ��߶�
		if (measuredHeiht < switchHeight) {
			setMeasuredDimension(getMeasuredWidth(), switchHeight);
		}
	}

	/**
	 * �������壺 1.�ַ�������Դ 2 .���ʶ��� 3.layout�Ŀ�ȣ��ַ����������ʱ�Զ����С�
	 * 4.layout����ʽ����ALIGN_CENTER�� ALIGN_NORMAL�� ALIGN_OPPOSITE ���֡�
	 * 5.����м�࣬��������С��1.5f��ʾ�м��Ϊ1.5��������߶ȡ� 6.����м�࣬0��ʾ0�����ء� ʵ���м����������ߵĺ͡�
	 * 7.����֪����ʲô��˼����������boolean includepad��
	 * ��Ҫָ���������layout��Ĭ�ϻ���Canvas��(0,0)��ģ������Ҫ����λ��ֻ����draw֮ǰ��Canvas����ʼ����
	 * canvas.translate(x,y);
	 * 
	 * @param mTextCharSequence
	 * @return
	 */
	private Layout maleLyout(CharSequence mTextCharSequence) {
		// TODO Auto-generated method stub
		System.out.println("text��" + mTextCharSequence);
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
		mThumbPosition = isChecked() ? getThumbScrollRange() : 0;//��ʼ���İ�ťλ��
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
		//��ȡ�ؼ��������ҵ�λ��
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
		canvas.save();// save��restore֮�䣬�������ӵ��Ƕ�Canvas�����������
		mTrackDrawable.getPadding(mTempRect);
		//�켣��Χ����������λ��
		int switchInnerLeft = switchLeft + mTempRect.left;
		int switchInnerTop = switchTop + mTempRect.top;
		int switchInnerRight = switchRight - mTempRect.right;
		int switchInnerBottom = switchBottom - mTempRect.bottom;
		canvas.clipRect(switchInnerLeft, switchInnerTop, switchInnerRight,
				switchInnerBottom);// �ü��켣��ʾ����
		mThumbDrawable.getPadding(mTempRect);// ��ť
		final int thumbPos = (int) (mThumbPosition + 0.5f);
		int thumbLeft = switchInnerLeft + thumbPos - mTempRect.left;
		int thumbRight = switchInnerLeft + thumbPos + mTempRect.right
				+ mThumbWidth;
		mThumbDrawable.setBounds(thumbLeft, switchInnerTop, thumbRight,
				switchInnerBottom);//Χ�ư�ť�ľ���
		mThumbDrawable.draw(canvas);// ������ť

		if (mTextColorStateList != null) {// ����������ɫ
			mTextPaint.setColor(mTextColorStateList.getColorForState(
					getDrawableState(), mTextColorStateList.getDefaultColor()));
		}
		mTextPaint.drawableState = getDrawableState();
		Layout switchTextLayout = getTargetCheckedState() ? mOnLayout
				: mOffLayout;// �������ֲ���
		canvas.translate(
				(thumbLeft + thumbRight) / 2 - switchTextLayout.getWidth() / 2,
				(switchInnerTop + switchInnerBottom) / 2
						- switchTextLayout.getHeight() / 2);
		switchTextLayout.draw(canvas);
		canvas.restore();

	}
/**
 * �ж�λ���Ƿ���м�
 */
	private boolean getTargetCheckedState() {
		// TODO Auto-generated method stub
		return mThumbPosition > getThumbScrollRange() / 2;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mVelocityTracker.addMovement(event);// ����¼�������ٶ�
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {// �����¼�
			final float x = event.getX();
			final float y = event.getY();
			if (isEnabled() && hitThumb(x, y)) {//����ʹ�ã��ڰ��o���ο�ȵ��
				mTouchMode = TOUCH_MODE_DOWN;
				mTouchX = x;
				mTouchY = y;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			switch (mTouchMode) {
			case TOUCH_MODE_IDLE:// �޶���
				return true;
			case TOUCH_MODE_DOWN: {// �Ѱ���
				final float x2 = event.getX();
				final float y2 = event.getY();
				if (Math.abs(x2 - mTouchX) > mTouchSlop
						|| Math.abs(y2 - mTouchY) > mTouchSlop) {// �϶�����
					mTouchMode = TOUCH_MODE_DRAGGING;// �϶�ģʽ
					getParent().requestDisallowInterceptTouchEvent(true);// ���ؼ������϶��¼�
					mTouchX = x2;
					mTouchY = y2;
					return true;
				}
				break;
			}
			case TOUCH_MODE_DRAGGING: {// �϶�
				final float x3 = event.getX();
				final float dx = x3 - mTouchX;// �϶�����
				float newPos = Math.max(0,
						Math.min(mThumbPosition + dx, getThumbScrollRange()));// ��ð�ť������λ��
				if (newPos != mThumbPosition) {// �����ı��ػ�
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
		case MotionEvent.ACTION_CANCEL:// �¼������ؼ��ض�,���������ؼ�֮�ϴ���
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
	 * �����¼����ҹ�λ
	 * @param event
	 */
	private void stopDrg(MotionEvent event) {
		// TODO Auto-generated method stub
		mTouchMode = TOUCH_MODE_IDLE;
		boolean commitChange = event.getAction() == MotionEvent.ACTION_UP
				&& isEnabled();
		cancleSupertouch(event);
		if (commitChange) {// ״̬�ı��״̬
			boolean newState;
			mVelocityTracker.computeCurrentVelocity(1000);// 1���˶����أ��ٶȵ�λ
			float xvel = mVelocityTracker.getXVelocity();// x������ٶ�
			if (xvel > mMinFlingVelocity) {//�ﵽһ���ٶȾ�ȥת��״̬
				newState = xvel > 0;
			} else {//δ�ﵽ��ȥ�ж����ڵ�λ��
				newState = getTargetCheckedState();
			}
			animateThumbToCheckedState(newState);// ����״̬�����ػ�
		} else {
			animateThumbToCheckedState(isChecked());
		}
	}

	private void animateThumbToCheckedState(boolean newState) {
		// TODO Auto-generated method stub
		setChecked(newState);
	}

	/**
	 * �����¼�
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
	 * �жϣأٰ��������Ƿ��ڰ�ť֮��
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

	/** ��ȡѡ��״̬�µ���ʾ���� */
	public CharSequence getTextOn() {
		return mTextOnCharSequence;
	}

	/** �趨ѡ��״̬�µ���ʾ���� */
	public void setTextOn(CharSequence textOn) {
		mTextOnCharSequence = textOn;
		requestLayout();
	}

	/** ��ȡ��ѡ��״̬�µ���ʾ���� */
	public CharSequence getTextOff() {
		return mTextOffCharSequence;
	}

	/** �趨��ѡ��״̬�µ���ʾ���� */
	public void setTextOff(CharSequence textOff) {
		mTextOffCharSequence = textOff;
		requestLayout();
	}
}
