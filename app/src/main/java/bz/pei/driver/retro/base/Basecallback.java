package bz.pei.driver.retro.base;

import bz.pei.driver.utilz.Exception.CustomException;

/**
 * Created by root on 9/27/17.
 */

public interface Basecallback<T>   {

    public void onSuccessfulApi(long taskId,T response);
    public void onFailureApi(long taskId, CustomException e);
}
