package bz.pei.driver.retro.responsemodel;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 12/12/17.
 */

public class AreaModel {
    @Expose
    public String  id;
    @Expose
    public String  admin_name;
    @Expose
    public String  area_name;

    @Override
    public String toString() {
        return area_name;
    }
}
