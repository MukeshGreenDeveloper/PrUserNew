package bz.pei.driver.Pojos;

import bz.pei.driver.ui.signupscreen.fragmentz.adapter.VehiclePagerAdapter;

/**
 * Created by root on 11/2/17.
 */

public class RegisterationModel {

    public static RegisterationModel Instance = null;

    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String phonenumber;
    public String country_code;
    public String admin_ID;
    public String countryShortName;
    public String profile_pic;
    public String vehicleModel;
    public String vehicleNumber;
    public String vehicleManufacturer;
    public String vehicleYear;
    public String vehicleType;

    public String license_photo;
    public String license_desc;
    public String license_exp;

    public String insurance_photo;
    public String insurance_desc;
    public String insurance_exp;


    public String rcBook_photo;
    public String rcBook_desc;
    public String rcBook_exp;


    public static RegisterationModel getInstance() {
        if (Instance == null)
            Instance = new RegisterationModel();

        return Instance;
    }

    public void clearLicence() {
        if (Instance == null)
            Instance = new RegisterationModel();
        license_exp = license_desc = license_photo = null;
    }

    public void clearInsurance() {
        if (Instance == null)
            Instance = new RegisterationModel();
        insurance_exp = insurance_desc = insurance_photo = null;
    }

    public void clearRcbook() {
        if (Instance == null)
            Instance = new RegisterationModel();
        rcBook_exp = rcBook_desc = rcBook_photo = null;
    }

    /**
     * Clear the satic content of this Model variables
     */
    public static void clearInstance() {
        Instance = new RegisterationModel();
        VehiclePagerAdapter.clearSelection();
    }


}
