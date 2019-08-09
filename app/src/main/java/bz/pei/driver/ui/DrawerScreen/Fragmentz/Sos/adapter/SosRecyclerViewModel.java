package bz.pei.driver.ui.DrawerScreen.Fragmentz.Sos.adapter;

import android.databinding.ObservableField;

/**
 * Created by root on 12/12/17.
 */

public class SosRecyclerViewModel {
    public ObservableField<String> titleObservable = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();

    public SosRecyclerViewModel() {
    }

    public SosRecyclerViewModel(ObservableField<String> titleObservable, ObservableField<String> phone) {
        this.titleObservable = titleObservable;
        this.phone = phone;
    }

}
