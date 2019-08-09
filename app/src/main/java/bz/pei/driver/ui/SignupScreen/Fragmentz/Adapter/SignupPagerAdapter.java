package bz.pei.driver.ui.SignupScreen.Fragmentz.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bz.pei.driver.ui.SignupScreen.Fragmentz.SignupFragment;
import bz.pei.driver.ui.SignupScreen.Fragmentz.VehicleInfoFragment;


/**
 * Created by root on 11/1/17.
 */

public class SignupPagerAdapter extends FragmentStatePagerAdapter {

    public SignupPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SignupFragment.newInstance();
            case 1:
                return VehicleInfoFragment.newInstance();/*
            case 2:
                return DocUploadFragment.newInstance();*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
