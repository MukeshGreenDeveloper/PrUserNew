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


import bz.pei.driver.ui.acceptreject.AcceptRejectActivity;
import bz.pei.driver.ui.drawerscreen.DrawerAct;
import bz.pei.driver.ui.drawerscreen.fragmentz.DialogFragmentProvider;
import bz.pei.driver.ui.drawerscreen.fragmentz.MapFragmentProvider;
import bz.pei.driver.ui.forgot.ForgetPwdActivity;
import bz.pei.driver.ui.history.HistoryActivity;
import bz.pei.driver.ui.login.LoginActivity;
import bz.pei.driver.ui.login.loginviaotp.LoginOTPActivity;
import bz.pei.driver.ui.login.otpscreen.OTPActivity;
import bz.pei.driver.ui.login.otpscreen.OtpDaggerModel;
import bz.pei.driver.ui.main.MainActivity;
import bz.pei.driver.ui.signupscreen.fragmentz.SignupFragmentProvider;
import bz.pei.driver.ui.signupscreen.SignupActivity;
import bz.pei.driver.ui.splash.SplashActivity;

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
