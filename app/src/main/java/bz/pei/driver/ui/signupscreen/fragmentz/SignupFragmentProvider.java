package bz.pei.driver.ui.signupscreen.fragmentz;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by root on 10/11/17.
 */
@Module
public abstract class SignupFragmentProvider {
    @ContributesAndroidInjector(modules = SignupDaggerModel.class)
    abstract SignupFragment provideSignupFragmentProviderFactory();
    @ContributesAndroidInjector(modules = SignupDaggerModel.class)
    abstract VehicleInfoFragment provideVehicleInfoFragmentProviderFactory();

    @ContributesAndroidInjector(modules = SignupDaggerModel.class)
    abstract DocUploadFragment provideDocUploadFragmentProviderFactory();

}
