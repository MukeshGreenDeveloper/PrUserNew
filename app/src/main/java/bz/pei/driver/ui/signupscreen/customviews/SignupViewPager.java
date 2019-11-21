package bz.pei.driver.ui.signupscreen.customviews;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by root on 11/6/17.
 */

public class SignupViewPager extends ViewPager {
//Stop
        private boolean isPagingEnabled = false;

        public SignupViewPager(Context context) {
            super(context);
        }

        public SignupViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return this.isPagingEnabled && super.onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        }

        public void setPagingEnabled(boolean b) {
            this.isPagingEnabled = b;
        }
    }
