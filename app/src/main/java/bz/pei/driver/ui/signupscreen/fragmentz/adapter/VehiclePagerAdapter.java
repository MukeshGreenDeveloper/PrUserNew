package bz.pei.driver.ui.signupscreen.fragmentz.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import bz.pei.driver.R;
import bz.pei.driver.retro.responsemodel.VehicleTypeModel;

import java.util.List;

/**
 * Created by root on 10/26/17.
 */

public class VehiclePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private List<VehicleTypeModel.Type> types;
    private static int selectedPosition = -1;
    private VehicleTypeSelectionListner listner;


    public static void clearSelection() {
        selectedPosition = -1;
    }

    public VehiclePagerAdapter(Context context, List<VehicleTypeModel.Type> types, VehicleTypeSelectionListner listner) {
        mContext = context;
        this.types = types;
        this.listner = listner;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final View view = inflater.inflate(R.layout.item_vehicletype, collection, false);
        ((TextView) view.findViewById(R.id.txt_title_vehicle_info)).setText(types.get(position).name);
//        view.findViewById(R.id.corner_triangle_selected).setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        Glide.with(mContext).load(types.get(position).icon).into((ImageView) view.findViewById(R.id.img_vehicle_vehicle_info));
        collection.addView(view);
        view.setTag(types.get(position).id + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                if (view.getTag() != null)
                    listner.onVehicleItemClicked((String) view.getTag());
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return types.get(position).name;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (types != null)
            if (types.size() > 0) {
                if (types.get(position) != null) {
                    selectedPosition = position;
                    listner.onVehicleItemClicked(types.get(position).id + "");
                }
            }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
