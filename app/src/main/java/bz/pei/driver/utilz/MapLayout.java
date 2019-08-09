package bz.pei.driver.utilz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapLayout extends FrameLayout {
public MapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public MapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MapLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE:

//                        layoutMarker.setVisibility(View.GONE);
                Log.e("Map", "Touch sdf -------------------------");
                break;

            case MotionEvent.ACTION_UP:

                Log.e("Map", "NoTouch sdf -------------------------");
                break;
        }

         return true;
    }
}