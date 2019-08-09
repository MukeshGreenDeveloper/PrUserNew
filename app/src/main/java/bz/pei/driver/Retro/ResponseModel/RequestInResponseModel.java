package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import bz.pei.driver.Retro.Base.BaseResponse;

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
