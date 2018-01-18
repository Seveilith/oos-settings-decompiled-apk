package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import java.util.Iterator;

public final class PaymentDefaultDialog
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private static final int PAYMENT_APP_MAX_CAPTION_LENGTH = 40;
  public static final String TAG = "PaymentDefaultDialog";
  private PaymentBackend mBackend;
  private ComponentName mNewDefault;
  
  private boolean buildDialog(ComponentName paramComponentName, String paramString)
  {
    if ((paramComponentName == null) || (paramString == null))
    {
      Log.e("PaymentDefaultDialog", "Component or category are null");
      return false;
    }
    if (!"payment".equals(paramString))
    {
      Log.e("PaymentDefaultDialog", "Don't support defaults for category " + paramString);
      return false;
    }
    Object localObject1 = null;
    paramString = null;
    Iterator localIterator = this.mBackend.getPaymentAppInfos().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (PaymentBackend.PaymentAppInfo)localIterator.next();
      Object localObject3 = localObject1;
      if (paramComponentName.equals(((PaymentBackend.PaymentAppInfo)localObject2).componentName)) {
        localObject3 = localObject2;
      }
      localObject1 = localObject3;
      if (((PaymentBackend.PaymentAppInfo)localObject2).isDefault)
      {
        paramString = (String)localObject2;
        localObject1 = localObject3;
      }
    }
    if (localObject1 == null)
    {
      Log.e("PaymentDefaultDialog", "Component " + paramComponentName + " is not a registered payment service.");
      return false;
    }
    Object localObject2 = this.mBackend.getDefaultPaymentApp();
    if ((localObject2 != null) && (((ComponentName)localObject2).equals(paramComponentName)))
    {
      Log.e("PaymentDefaultDialog", "Component " + paramComponentName + " is already default.");
      return false;
    }
    this.mNewDefault = paramComponentName;
    paramComponentName = this.mAlertParams;
    paramComponentName.mTitle = getString(2131692991);
    if (paramString == null) {}
    for (paramComponentName.mMessage = String.format(getString(2131692992), new Object[] { sanitizePaymentAppCaption(((PaymentBackend.PaymentAppInfo)localObject1).label.toString()) });; paramComponentName.mMessage = String.format(getString(2131692993), new Object[] { sanitizePaymentAppCaption(((PaymentBackend.PaymentAppInfo)localObject1).label.toString()), sanitizePaymentAppCaption(paramString.label.toString()) }))
    {
      paramComponentName.mPositiveButtonText = getString(2131690771);
      paramComponentName.mNegativeButtonText = getString(2131690772);
      paramComponentName.mPositiveButtonListener = this;
      paramComponentName.mNegativeButtonListener = this;
      setupAlert();
      return true;
    }
  }
  
  private String sanitizePaymentAppCaption(String paramString)
  {
    paramString = paramString.replace('\n', ' ').replace('\r', ' ').trim();
    if (paramString.length() > 40) {
      return paramString.substring(0, 40);
    }
    return paramString;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    case -2: 
    default: 
      return;
    }
    this.mBackend.setDefaultPaymentApp(this.mNewDefault);
    setResult(-1);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mBackend = new PaymentBackend(this);
    Object localObject = getIntent();
    paramBundle = (ComponentName)((Intent)localObject).getParcelableExtra("component");
    localObject = ((Intent)localObject).getStringExtra("category");
    setResult(0);
    if (!buildDialog(paramBundle, (String)localObject)) {
      finish();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\PaymentDefaultDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */