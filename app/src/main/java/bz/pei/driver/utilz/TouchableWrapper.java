package bz.pei.driver.utilz;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    Boolean mMapIsTouched;

    public TouchableWrapper(@NonNull Context context) {
        super(context);
    }

    @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mMapIsTouched = true;
                    break;
                case MotionEvent.ACTION_UP:
                    mMapIsTouched = false;
                    break;
            }

            return super.dispatchTouchEvent(ev);

        }

    }