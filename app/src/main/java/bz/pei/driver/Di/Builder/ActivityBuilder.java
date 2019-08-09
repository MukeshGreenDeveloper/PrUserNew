/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package bz.pei.driver.Di.Builder;


import bz.pei.driver.ui.AcceptReject.AcceptRejectActivity;
import bz.pei.driver.ui.DrawerScreen.DrawerAct;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.DialogFragmentProvider;
import bz.pei.driver.ui.DrawerScreen.Fragmentz.MapFragmentProvider;
import bz.pei.driver.ui.Forgot.ForgetPwdActivity;
import bz.pei.driver.ui.History.HistoryActivity;
import bz.pei.driver.ui.Login.LoginActivity;
import bz.pei.driver.ui.Login.LoginViaOTP.LoginOTPActivity;
import bz.pei.driver.ui.Login.OtpScreen.OTPActivity;
import bz.pei.driver.ui.Login.OtpScreen.OtpDaggerModel;
import bz.pei.driver.ui.Main.MainActivity;
import bz.pei.driver.ui.SignupScreen.Fragmentz.SignupFragmentProvider;
import bz.pei.driver.ui.SignupScreen.SignupActivity;
import bz.pei.driver.ui.Splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


//Here bind the subcomponent of Activitie's and

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = SignupFragmentProvider.class)
    abstract SignupActivity bindSignupActivity();

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = {MapFragmentProvider.class, DialogFragmentProvider.class})
    abstract DrawerAct bindDrawerActivity();

//    @ContributesAndroidInjector
//    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector
    abstract ForgetPwdActivity bindForgetPwdActivity();

    @ContributesAndroidInjector
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector
    abstract HistoryActivity bindHistoryActivity();

    @ContributesAndroidInjector(modules = DialogFragmentProvider.class)
    abstract AcceptRejectActivity bindAcceptRejectActivity();

    @ContributesAndroidInjector
    abstract LoginOTPActivity bindLoginOTPActivity();

    @ContributesAndroidInjector(modules = OtpDaggerModel.class)
    abstract OTPActivity bindOTPActivity();

}
