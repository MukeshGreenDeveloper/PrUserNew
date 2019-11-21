package bz.pei.driver.ui.signupscreen.fragmentz;

import com.google.gson.Gson;
import bz.pei.driver.retro.GitHubCountryCode;
import bz.pei.driver.retro.GitHubService;
import bz.pei.driver.utilz.Constants;
import bz.pei.driver.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 10/11/17.
 */

@Module
public class SignupDaggerModel {
    @Provides
    SignupFragmentViewModel provideSignupFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                                                           SharedPrefence sharedPrefence,
                                                           Gson gson ) {
        return new SignupFragmentViewModel(gitHubService,gitHubCountryCode,sharedPrefence,gson);
    }

    @Provides
    VehicleInfoViewModel provideInfoViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                              SharedPrefence sharedPrefence,
                                              Gson gson) {
        return new VehicleInfoViewModel(gitHubService,sharedPrefence,gson);
    }

    @Provides
    DocUploadViewModel provideDocUploadViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                 SharedPrefence sharedPrefence,
                                                 Gson gson) {
        return new DocUploadViewModel(gitHubService,sharedPrefence,gson);
    }
    @Provides
    DocDialogViewModel provideDocDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                 SharedPrefence sharedPrefence,
                                                 Gson gson) {
        return new DocDialogViewModel(gitHubService,sharedPrefence,gson);
    }
    @Provides
    @Named("HashMapData")
    static HashMap<String, String> provideData(SignupFragment fragment) {
        return fragment.Bindabledata;
    }

}
