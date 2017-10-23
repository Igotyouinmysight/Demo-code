package com.example.mysimpleviewpager.view;

import android.content.Context;
import android.media.MediaRouter.VolumeCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SimpleViewPager extends ViewGroup {

	private int oldY;
	private int oldX;
	private int lastX;
	private int childMeasuredWidth;
	private Scroller scroller;
	private VelocityTracker vTracker;

	public SimpleViewPager(Context context) {
		super(context);
		init();
	}

	public SimpleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SimpleViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		scroller = new Scroller(getContext());
		vTracker = VelocityTracker.obtain(); 
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//先测量子控件，再测量自己；
		//子控件的measureSpec由该View的measureSpec，子View的布局参数决定；
		//假设每一个的子控件宽高都是一样的，直接调用measureChildren测量所有子View即可；
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		//分析FrameLayout的onMeasure(w,h)可知，测量自己需要判断specMode，然后取值。
		//获取宽高的模式，大小
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		
		//获取子view的个数
		int childCount = getChildCount();
		if (childCount == 0) {
			//如果没有子元素，则设置宽高大小为0(应该根据布局参数的宽高来设置值，这里演示不会让子元素个数为0)
			setMeasuredDimension(0, 0);
			return;
		}
		
		//获取子View的宽高
		View childAt = getChildAt(0);
		int childMeasuredWidth = childAt.getMeasuredWidth();
		int childMeasuredHeight = childAt.getMeasuredHeight();
		
		//分四种情况讨论
		if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
			//宽度设置为3个子view宽度相加，高度设置为一个子View高度
			setMeasuredDimension(childMeasuredWidth * 3, childMeasuredHeight);
		} else if (widthSpecMode == MeasureSpec.AT_MOST) {
			//宽度设置为3个子View宽度相加；
			//高度为exactly模式，直接取测量高度大小即可(分析ViewGroup$getChildMeasureSpec源码可知)
			
			setMeasuredDimension(childMeasuredWidth * 3, heightSpecSize);
		} else if (heightSpecMode == MeasureSpec.AT_MOST){
			//宽度为exactly模式，直接取测量的宽度值；高度为一个子View高度
			
			setMeasuredDimension(widthSpecSize, childMeasuredHeight);
		} else {
			//宽高都是exactly模式，则直接使用父view给的建议值大小
			
			setMeasuredDimension(widthSpecSize, heightSpecSize);
		}
		
		/*分析FrameLayout的onMeasure(w,h)源码可知，容器控件测量自己大小时，与子控件测量大小，容器控件的padding,
		 子控件的margin有关。在这里，仅仅是与子控件的测量宽高有关，并没有讨论容器控件的padding,子控件margin的影响。
		若完善一个继承自ViewGroup的自定义控件，就要考虑到这些影响了。*/
		
	}
	
	
	/* 执行到onLayout方法，说明layout已经在执行中了，那么给自己设置布局的操作已经完成，
	onLayout只需要给子控件设置布局。(见源码View$layout(l,t,r,b)) */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//设置子元素left初始值
		int left = 0;
		
		View childAt = getChildAt(0);
		childMeasuredWidth = childAt.getMeasuredWidth();
		int childMeasuredHeight = childAt.getMeasuredHeight();
		
		//给所有的子控件设置布局
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			//子view的上下左右的值，均是相对于父控件的
			child.layout(left, 0, left + childMeasuredWidth, childMeasuredHeight);
			left += childMeasuredWidth;
		}
	}
	/*
		事实上，容器控件给子元素设置布局时，参考FrameLayout$onLayout方法源码可知，影响因素还有容器控件的padding,
		以及子控件的margin。若需要完善继承ViewGroup的自定义控件的布局流程，需要考虑到这些影响。
	 */

	//处理滑动冲突
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean intercept = false;
		
		//x,y值均相对于view的左上角顶点位置
		int newX = (int) ev.getX();
		int newY = (int) ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//不能拦截action_down事件，否则action_move/up事件不会向子元素传递
			//(这里直接给结论，原因见源码ViewGroup$dispatchTouchEvent(ev))
			Log.e("wang", "onInterceptTouchEvent_action_down");
			intercept =  false;
		    break;
		case MotionEvent.ACTION_MOVE:
			int disX = newX - oldX;
			int disY = newY - oldY;
			//当触摸移动水平方向距离>竖直方向时，拦截事件
			if (Math.abs(disX) > Math.abs(disY)) {
				Log.e("wang", "onInterceptTouchEvent_action_move");
				intercept =  true;
			} else {
				intercept =  false;
			}
			break;
		case MotionEvent.ACTION_UP:
			//如果拦截了up事件，那么子元素的无法正常处理up事件；但并不影响SimpleViewPager处理up事件
			//(这里直接给结论，原因见ViewGroup$dispatchTouchEvent(ev)的源码)
			Log.e("wang", "onInterceptTouchEvent_action_up");
			intercept =  false;
			break;
		default:
			break;
		}
		lastX = newX;
		Log.e("wang", "oninter_lastX:" + lastX);
		oldX = newX;
		oldY = newY;
		return intercept;
	}
	
	//事件拦截后，调用View$dispatchTouchEvent方法，于是重写如下方法真正处理事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		vTracker.addMovement(event);
		int newX = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//因为没有拦截，正常情况action_down不会执行，导致lastX值为0，需要在拦截时给lastX设置初始值
			break;
		case MotionEvent.ACTION_MOVE:
			//触摸移动多少，控件就滑动多少
			int disx = newX - lastX;
			
			Log.e("wang", "ACTION_MOVE_newX:" + newX);
			Log.e("wang", "ACTION_MOVE_lastX:" + lastX);
			Log.e("wang", "ACTION_MOVE_disx:" + disx);
			
			Log.e("wang", "getScrollX():" + getScrollX());
			if (getScrollX() < 0 || getScrollX() > childMeasuredWidth * 2) {
				break;
			}
			
			//scrollBy指的是某一小段时间内，滑动的距离，有方向
			this.scrollBy(- disx, 0);//注意方向，加负号
			break;
		case MotionEvent.ACTION_UP:
			//在弹起后，若超出边界，使View滑动到边界
			if (getScrollX() < 0) {
				this.scrollTo(0, 0);
				break;
			}
			if (getScrollX() > childMeasuredWidth * 2) {
				this.scrollTo(childMeasuredWidth * 2, 0);
				break;
			}
			
			//获取已滑动的距离,getScrollX可以理解为物理中的：总位移(包括多个事件序列中，相对于未滑动时的位移)
			int hasScrollX = this.getScrollX();
			//手指弹起后，要完成一个页面切换的滑动，使用Scroller
			
			vTracker.computeCurrentVelocity(1000);//单位为1000ms滑动的像素点
			float xVelocity = vTracker.getXVelocity();
			//速度方向与getScrollX方向相反，与触摸滑动方向相同
			Log.e("wang", "xVelocity:" + xVelocity);
			//速度大于50px/1000ms时，根据方向，展示下一个页面
			if (xVelocity < - 50) {
				if (hasScrollX < childMeasuredWidth) {
					//页面2
					scroller.startScroll(hasScrollX, 0, childMeasuredWidth - hasScrollX, 0, 1000);
				} else if (hasScrollX > childMeasuredWidth || hasScrollX < childMeasuredWidth * 2) {
					//页面3
					scroller.startScroll(hasScrollX, 0, 2 * childMeasuredWidth - hasScrollX, 0, 1000);
				}
			} else if (xVelocity > 50) {
				if (hasScrollX < childMeasuredWidth) {
					//页面1
					scroller.startScroll(hasScrollX, 0, -hasScrollX, 0, 1000);
				} else if (hasScrollX > childMeasuredWidth || hasScrollX < childMeasuredWidth * 2) {
					//页面2
					scroller.startScroll(hasScrollX, 0, childMeasuredWidth - hasScrollX, 0, 1000);
				}
			} else {
				//不考虑速度
				//弹起时，判断已滑动距离，选择三个页面中一个展示
				if (hasScrollX < childMeasuredWidth / 2) {
					//页面1
					scroller.startScroll(hasScrollX, 0, -hasScrollX, 0, 1000);
				} else if (hasScrollX >= childMeasuredWidth / 2 && hasScrollX < childMeasuredWidth * 3 / 2) {
					//页面2
					scroller.startScroll(hasScrollX, 0, childMeasuredWidth - hasScrollX, 0, 1000);
				} else {
					//页面3
					scroller.startScroll(hasScrollX, 0, 2 * childMeasuredWidth - hasScrollX, 0, 1000);
				}
			}
			invalidate();
			break;

		default:
			break;
		}
		lastX = newX;
		return true;
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			int currX = scroller.getCurrX();
			this.scrollTo(currX, 0);
			postInvalidate();
		}
	}
	
}
