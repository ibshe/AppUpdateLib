package com.boge.update.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class OutsideClickDialog extends Dialog {

    public OutsideClickDialog(Context context) {
        super(context);
    }

    public OutsideClickDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected OutsideClickDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected abstract void onTouchOutside();


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //点击弹窗外部区域
        if (isOutOfBounds(getContext(), event)) {
            onTouchOutside();
            return true;//拦截触摸事件，防止dialog执行dismiss
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();//相对弹窗左上角的x坐标
        final int y = (int) event.getY();//相对弹窗左上角的y坐标
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();//最小识别距离
        final View decorView = getWindow().getDecorView();//弹窗的根View
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }
}
