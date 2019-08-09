package bz.pei.driver.Retro.ResponseModel;

import com.google.gson.annotations.Expose;
import bz.pei.driver.Retro.Base.BaseResponse;

import java.util.List;

public class ComplaintsModel extends BaseResponse {
    @Expose
    public List<CompLaint> complaint_list;

    public class CompLaint {
        @Expose
        public String id;
        @Expose
        public String title;

        @Override
        public String toString() {
            return title;
        }
    }


}