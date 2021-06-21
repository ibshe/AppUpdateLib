package com.boge.update.base;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.boge.update.R;
import com.boge.update.utils.AnimatorHelper;
import com.boge.update.widget.DialogViewHolder;
import com.boge.update.widget.OutsideClickDialog;

/**
 * @Author ibshen@aliyun.com
 */
public abstract class BsDialog {
    private OutsideClickDialog mDialog;
    private Window mDialogWindow;
    private DialogViewHolder dilaogVh;
    private View mRootView;

    private boolean cancelable = false;
    private boolean cancelableOnTouchOutside = false;

    private boolean isCustomAnima = false;

    private int mInAnimaType;
    private int mOutAnimaType;

    public BsDialog(final Context context, int layoutId) {
        dilaogVh = DialogViewHolder.get(context, layoutId);
        mRootView = dilaogVh.getConvertView();
        mDialog = new OutsideClickDialog(context, R.style.dialog) {
            @Override
            protected void onTouchOutside() {
                startOutAinma(cancelableOnTouchOutside);
            }
        };

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (isCustomAnima) {
                    AnimatorHelper.getAnimator(mRootView, mInAnimaType).start();
                }
            }
        });

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    startOutAinma(cancelable);
                    return true;
                }
                return false;
            }
        });
        mDialog.setContentView(mRootView);
        mDialogWindow = mDialog.getWindow();
        onBindViewHolder(dilaogVh);
    }

    public abstract void onBindViewHolder(DialogViewHolder holder);

    public BsDialog setBackground(Drawable drawable) {
        if (mDialog != null && mRootView!=null) {
            mRootView.setBackground(drawable);
        }
        return this;
    }

    /**
     * 显示dialog
     */
    public BsDialog showDialog() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        return this;
    }

    /**
     * @param style 显示一个Dialog自定义一个弹出方式  具体怎么写 可以模仿上面的
     * @return
     */
    public BsDialog showDialog(@StyleRes int style) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialogWindow.setWindowAnimations(style);
            mDialog.show();
        }
        return this;
    }

    /**
     * @param isAnimation 如果为true 就显示默认的一个缩放动画
     * @return
     */
    public BsDialog showDialog(boolean isAnimation) {
        if (mDialog != null && !mDialog.isShowing()) {
            if (isAnimation) {
                mDialogWindow.setWindowAnimations(R.style.dialog_scale_animstyle);
            }
            mDialog.show();
        }
        return this;
    }

    /**
     * @param light 弹出时背景亮度 值为0.0~1.0    1.0表示全黑  0.0表示全白
     * @return
     */
    public BsDialog backgroundLight(double light) {
        if (mDialogWindow != null) {
            if (light < 0.0 || light > 1.0) {
                return this;
            }
            WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
            lp.dimAmount = (float) light;
            mDialogWindow.setAttributes(lp);
        }
        return this;
    }

    /**
     * 上方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewTop(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = location[0] + (view.getWidth() / 2) - (dilaogVh.getWidth() / 2);
            params.y = location[1] - dilaogVh.getHeight() - view.getHeight();
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 下方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewBottom(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = location[0] + (view.getWidth() / 2) - (dilaogVh.getWidth() / 2);
            params.y = location[1] + 5;
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 左方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewLeft(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = location[0] - dilaogVh.getWidth();
            params.y = location[1] - (dilaogVh.getHeight() / 2) - (view.getHeight() / 2);
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 右方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewRigh(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = dilaogVh.getScreenWidth() - location[0] - dilaogVh.getWidth() - view.getWidth();
            params.y = location[1] - (dilaogVh.getHeight() / 2) - (view.getHeight() / 2);
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 左下方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewLeftBottom(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = location[0];
            params.y = location[1] + 5;
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 右下方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewRighBottom(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = dilaogVh.getScreenWidth() - location[0] - view.getWidth();
            params.y = location[1] + 5;
//            LogUtils.d("setViewRighBottom =" + params.x + " " + dilaogVh.getScreenWidth() + " " + location[0] + " " + view.getWidth());
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 左上方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewLeftTop(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = location[0];
            params.y = location[1] - dilaogVh.getHeight() - view.getHeight() - 5;
            mDialogWindow.setAttributes(params);
        }
        return this;
    }

    /**
     * 右上方
     *
     * @param view 在哪个控件位置显示
     * @return
     */
    public BsDialog setViewRighTop(View view) {
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            WindowManager.LayoutParams params = mDialogWindow.getAttributes();
            params.x = dilaogVh.getScreenWidth() - location[0] - view.getWidth();
            params.y = location[1] - dilaogVh.getHeight() - view.getHeight() - 5;
            mDialogWindow.setAttributes(params);
        }
        return this;
    }


    /**
     * 自定义设置动画
     */
    public BsDialog setCustomAnimations(final int inAnimType, int outAnimType) {
        isCustomAnima = true;
        this.mInAnimaType = inAnimType;
        this.mOutAnimaType = outAnimType;
        return this;
    }


    /**
     * 从底部一直弹到中间
     */
    @SuppressLint("NewApi")
    public BsDialog fromBottomToMiddle() {
        if (mDialogWindow != null) {
            mDialogWindow.setWindowAnimations(R.style.window_bottom_in_bottom_out);
        }
        return this;
    }

    /**
     * 从底部弹出
     */
    public BsDialog fromBottom() {
        if (mDialogWindow != null) {
            fromBottomToMiddle();
            mDialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        }
        return this;
    }

    /**
     * 从左边一直弹到中间退出也是到左边
     */
    public BsDialog fromLeftToMiddle() {
        if (mDialogWindow != null) {
            mDialogWindow.setWindowAnimations(R.style.window_left_in_left_out);
            mDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialogWindow.setGravity(Gravity.CENTER | Gravity.LEFT);
        }
        return this;
    }

    /**
     * 从右边一直弹到中间退出也是到右边
     *
     * @return
     */
    public BsDialog fromRightToMiddle() {
        if (mDialogWindow != null) {
            mDialogWindow.setWindowAnimations(R.style.window_right_in_right_out);
            mDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialogWindow.setGravity(Gravity.RIGHT);
        }
        return this;
    }

    /**
     * 从顶部弹出 从顶部弹出  保持在顶部
     *
     * @return
     */
    public BsDialog fromTop() {
        if (mDialogWindow != null) {
            fromTopToMiddle();
            mDialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        }
        return this;
    }

    /**
     * 从顶部谈到中间  从顶部弹出  保持在中间
     *
     * @return
     */
    public BsDialog fromTopToMiddle() {
        if (mDialogWindow != null) {
            mDialogWindow.setWindowAnimations(R.style.window_top_in_top_out);
            mDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return this;
    }


    /**
     * 全屏显示
     */
    public BsDialog fullScreen() {
        if (mDialogWindow != null) {
            WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
            wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mDialog.onWindowAttributesChanged(wl);
        }
        return this;
    }


    public BsDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        if (mDialogWindow != null) {
            mDialog.setOnKeyListener(onKeyListener);
        }
        return this;
    }

    /**
     * 全屏宽度
     */
    public BsDialog fullWidth() {
        if (mDialogWindow != null) {
            WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mDialog.onWindowAttributesChanged(wl);
        }
        return this;
    }

    /**
     * 全屏高度
     */
    public BsDialog fullHeight() {
        if (mDialogWindow != null) {
            WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
            wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mDialog.onWindowAttributesChanged(wl);
        }
        return this;
    }

    /**
     * @param width  自定义的宽度
     * @param height 自定义的高度
     * @return
     */
    public BsDialog setWidthAndHeight(int width, int height) {
        if (mDialogWindow != null) {
            WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
            wl.width = width;
            wl.height = height;
            mDialog.onWindowAttributesChanged(wl);
        }
        return this;
    }

    /**
     * 取消
     */
    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            startOutAinma(true);
        }
    }

    /**
     * 设置监听
     */
    public BsDialog setDialogDismissListener(OnDismissListener listener) {
        if (mDialog != null) {
            mDialog.setOnDismissListener(listener);
        }
        return this;
    }

    /**
     * 设置监听
     */
    public BsDialog setOnCancelListener(OnCancelListener listener) {
        if (mDialog != null) {
            mDialog.setOnCancelListener(listener);
        }
        return this;
    }

    /**
     * 设置是否能取消
     */
    public BsDialog setCancelAble(boolean cancel) {
        if (mDialog != null) {
            cancelable = cancel;
        }
        return this;
    }


    /**
     * 设置触摸其他地方是否能取消
     */
    public BsDialog setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            cancelableOnTouchOutside = cancel;
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    private void startOutAinma(final boolean isCancelable) {
        if (isCustomAnima) {// 自定义动画
            if (cancelable || cancelableOnTouchOutside) {
                Animator animator = AnimatorHelper.getAnimator(mRootView, mOutAnimaType);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
//                        LogUtils.d("BsDialog onAnimationStart ");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        LogUtils.d("BsDialog onAnimationEnd ");
                        mDialog.dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
//                        LogUtils.d("BsDialog onAnimationCancel ");

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
//                        LogUtils.d("BsDialog onAnimationRepeat ");

                    }
                });
                if (isCancelable) {//从返回键点击 设置true时执行 否则不执行
                    animator.start();
                }
            }
        } else {
            if (isCancelable) {//从返回键点击 设置true时执行 否则不执行
                mDialog.dismiss();
            }
        }
    }


}

