package bz.pei.driver.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.retro.base.BaseResponse;

import java.util.List;

/**
 * Created by root on 12/21/17.
 */

public class RequestInResponseModel extends BaseResponse {
    @Expose
    public DriverModel driver_status;
    @SerializedName("sos")
    public List<SosModel> sosModelList;
}
