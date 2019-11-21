package bz.pei.driver.retro.base;


import bz.pei.driver.utilz.Exception.CustomException;

/**
 * Created by guru on 1/6/2017.
 */

public interface IModelListener<T> {

    void onSuccessfulApi(long taskId, T response);

    void onFailureApi(long taskId, CustomException e);
}
