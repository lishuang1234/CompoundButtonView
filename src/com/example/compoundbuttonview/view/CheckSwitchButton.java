package com.example.compoundbuttonview.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.compoundbuttonview.R;
import com.example.compoundbuttonview.util.FramAnimationController;

public class CheckSwitchButton extends CheckBox {
	private Paint mPaint;
	private ViewParent mViewParent;
	private Bitmap mBottomBitmap;
	private Bitmap mCurBitPicBitmap;
	private Bitmap mBtnPressedBitmap;
	private Bitmap mBtnNormalBitmap;
	private Bitmap mFrameBitmap;
	private Bitmap mMaskBitmap;
	private RectF mSaveLayeRectF;
	private PorterDuffXfermode mXfermode;
	private float mFirstDownX;
	private float mFirstDownY;
	private float mRealPos;
	private float mBtnPos;
	private float mBtnOnPos;
	private float mBtnOffPos;
	private float mMaskWidth;
	private float mMaskHeight;
	private float mBtnWidth;
	private float initBtnPos;
	private int mClickTimeOut;
	private int mTouchSlop;
	private final int MAX_ALPHA = 256;
	private int mAlpha = MAX_ALPHA;
	private boolean mChecked = false;
	private boolean mBroadCasting;
	private boolean mTurningOn;
	private PerformClick performClick;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private OnCheckedChangeListener mOnWidgrtCheckedChangeListener;
	private boolean mAnimaing;
	private final float VELOCITY = 350;
	private float mVelocity;
	private final float EXTEND_OFFSET_Y = 15;
	private float mExtendOffSetY;
	private float mAnimationPosition;
	private float mAnimationVelocity;

	public CheckSwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.checkboxStyle);
		// TODO Auto-generated constructor stub
	}

	public CheckSwitchButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub

	}

	public CheckSwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		Resources resources = context.getResources();
		mClickTimeOut = ViewConfiguration.getPressedStateDuration()
				+ ViewConfiguration.getTapTimeout();// ����¼�
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//
		mBottomBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.checkswitch_bottom);
		mBtnPressedBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.checkswitch_btn_pressed);
		mBtnNormalBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.checkswitch_btn_unpressed);
		mFrameBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.checkswitch_frame);
		mMaskBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.checkswitch_mask);
		mCurBitPicBitmap = mBtnNormalBitmap;
		mBtnWidth = mBtnPressedBitmap.getWidth();
		mMaskHeight = mMaskBitmap.getHeight();
		mMaskWidth = mMaskBitmap.getWidth();
		mBtnOffPos = mBtnWidth / 2;
		mBtnOnPos = mMaskWidth - mBtnWidth/2;// ע��ON<OFF
		mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
		mRealPos = getRealPos(mBtnPos);// ��û�ͼ����
		System.out.println("mBtnOn--" + mBtnOnPos);
		System.out.println("mBtnOff--" + mBtnOffPos);
		final float density = resources.getDisplayMetrics().density;
		mVelocity = (int) (VELOCITY * density + 0.5f);// �����ٶ�
		mExtendOffSetY = (int) (EXTEND_OFFSET_Y * density + 0.5f);
		mSaveLayeRectF = new RectF(0, mExtendOffSetY, mMaskWidth, mMaskHeight
				+ mExtendOffSetY);// ��ť�Ĳ���
		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);// ��϶���ͼƬ����

	}

	private float getRealPos(float mBtnPos2) {
		// TODO Auto-generated method stub
		return mBtnPos2 - mBtnWidth / 2;
	}

	@Override
	public boolean performClick() {// ģ�����¼�
		// TODO Auto-generated method stub
		startAnima(mChecked);
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.saveLayerAlpha(mSaveLayeRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.CLIP_TO_LAYER_SAVE_FLAG);// ����ͼ��
		canvas.drawBitmap(mMaskBitmap, 0, mExtendOffSetY, mPaint);// ��ɫ��Ӱ
		mPaint.setXfermode(mXfermode);
		canvas.drawBitmap(mBottomBitmap, mRealPos, mExtendOffSetY, mPaint);// ��ť�µ�ͼƬ
		mPaint.setXfermode(null);
		canvas.drawBitmap(mFrameBitmap, 0, mExtendOffSetY, mPaint);// ��ť��Χ���
		canvas.drawBitmap(mCurBitPicBitmap, mRealPos, mExtendOffSetY, mPaint);// ���ư�ť

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		float deltaX = Math.abs(x - mFirstDownX);
		float deltaY = Math.abs(y - mFirstDownY);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			attemptClaimDrag();
			mFirstDownX = x;
			mFirstDownY = y;
			mCurBitPicBitmap = mBtnPressedBitmap;
			initBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
			break;
		case MotionEvent.ACTION_MOVE:
			float time = event.getEventTime() - event.getDownTime();// ����ʱ��
			mBtnPos = initBtnPos + event.getX() - mFirstDownX;//
			if (mBtnPos <= mBtnOnPos) {
				mBtnPos = mBtnOnPos;
			}
			if (mBtnPos >= mBtnOffPos) {
				mBtnPos = mBtnOffPos;
			}
			mTurningOn = mBtnPos > mBtnOnPos - (mBtnOnPos - mBtnOffPos) / 2;
			mRealPos = getRealPos(mBtnPos);
			break;
		case MotionEvent.ACTION_UP:
			mCurBitPicBitmap = mBtnNormalBitmap;
			time = event.getEventTime() - event.getDownTime();
			if (deltaX < mTouchSlop && deltaY < mTouchSlop
					&& time < mClickTimeOut) {// ��������¼�
				if (performClick == null) {
					performClick = new PerformClick();
				}
				if (!post(performClick)) {
					performClick();// ģ�����������¼�
				}
			} else {
				startAnima(mTurningOn);// ��ʼ����
			}
			break;
		default:
			break;
		}
		invalidate();
		return isEnabled();

	}

	private void startAnima(boolean b) {
		// TODO Auto-generated method stub
		mAnimaing = true;
		mAnimationVelocity = b ? mVelocity : -mVelocity;// �򿪻�ر�
		mAnimationPosition = mBtnPos;
		new SwitchAnima().run();

	}

	class SwitchAnima implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!mAnimaing) {
				return;
			}
			doAnima();// ���ƶ���
			FramAnimationController.requestAnimationFrame(this);// ѭ������
		}
	}

	private void attemptClaimDrag() {
		// TODO Auto-generated method stub
		mViewParent = getParent();
		if (mViewParent != null) {
			mViewParent.requestDisallowInterceptTouchEvent(true);
		}

	}

	public void doAnima() {
		// TODO Auto-generated method stub
		mAnimationPosition += mAnimationVelocity
				* FramAnimationController.ANIMATINO_FRAME_DURATION / 1000;// �������ݼ�
		if (mAnimationPosition <= mBtnOnPos) {
			stopAnima();
			mAnimationPosition = mBtnOnPos;
			setChewckedDelayed(true);
		} else if (mAnimationPosition >= mBtnOffPos) {
			stopAnima();
			mAnimationPosition = mBtnOffPos;
			setChewckedDelayed(false);// �������ÿ���״̬
		}
		moveAnima(mAnimationPosition);// ����
	}

	private void moveAnima(float mAnimationPosition2) {
		// TODO Auto-generated method stub
		mBtnPos = mAnimationPosition2;
		mRealPos = getRealPos(mBtnPos);
		invalidate();
	}

	private void setChewckedDelayed(final boolean b) {
		// TODO Auto-generated method stub
		this.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setChecked(b);
			}
		}, 10);

	}

	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
		}
		mBtnPos = checked ? mBtnOnPos : mBtnOffPos;
		mRealPos = getRealPos(mBtnPos);
		invalidate();
		if (mBroadCasting) {
			return;
		}
		mBroadCasting = true;
		if (mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(CheckSwitchButton.this,
					mChecked);
		}
		if (mOnWidgrtCheckedChangeListener != null) {
			mOnWidgrtCheckedChangeListener.onCheckedChanged(
					CheckSwitchButton.this, mChecked);

		}
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		// TODO Auto-generated method stub
		mOnCheckedChangeListener = listener;
	}

	void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
		mOnWidgrtCheckedChangeListener = listener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		mAlpha = enabled ? MAX_ALPHA : MAX_ALPHA / 2;
		super.setEnabled(enabled);
	}

	private void stopAnima() {
		// TODO Auto-generated method stub
		mAnimaing = false;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension((int) mMaskWidth,
				(int) (mMaskHeight + 2 * mExtendOffSetY));
	}

	private final class PerformClick implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			performClick();
		}

	}
}
