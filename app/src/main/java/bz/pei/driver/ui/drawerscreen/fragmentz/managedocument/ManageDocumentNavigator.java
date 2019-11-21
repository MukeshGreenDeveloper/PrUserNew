package bz.pei.driver.ui.drawerscreen.fragmentz.managedocument;

import bz.pei.driver.retro.responsemodel.RequestModel;
import bz.pei.driver.ui.Base.BaseActivity;
import bz.pei.driver.ui.Base.BaseView;

/**
 * Created by root on 12/28/17.
 */

public interface ManageDocumentNavigator extends BaseView {
    public BaseActivity getAttachedContext();
    public void openDocUploadDialog(RequestModel.Documents document_Item, boolean expiryAvailable, boolean numberAvailable);
    public void refreshToHome();
    public void logoutApp();
}
