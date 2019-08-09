package bz.pei.driver.utilz;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by root on 10/12/17.
 */

public class MyMapView  extends RelativeLayout{
    public MyMapView(Context context) {
        super(context);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("++ACTION_DOWN++++");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("+++ACTION_UP++");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    /*private OnMovedListener mOnMovedListener;

    public void setOnMovedListener(OnMovedListener mOnMovedListener) {
        this.mOnMovedListener= mOnMovedListener;
    }*/
}
