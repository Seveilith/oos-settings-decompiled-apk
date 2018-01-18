package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RegulatoryInfoDisplayActivity
  extends Activity
  implements DialogInterface.OnDismissListener
{
  private final String REGULATORY_INFO_RESOURCE = "regulatory_info";
  
  private int getResourceId()
  {
    int j = getResources().getIdentifier("regulatory_info", "drawable", getPackageName());
    String str = SystemProperties.get("ro.boot.hardware.sku", "");
    int i = j;
    if (!TextUtils.isEmpty(str))
    {
      str = "regulatory_info_" + str.toLowerCase();
      int k = getResources().getIdentifier(str, "drawable", getPackageName());
      i = j;
      if (k != 0) {
        i = k;
      }
    }
    return i;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = getResources();
    if (!((Resources)localObject).getBoolean(2131558419)) {
      finish();
    }
    paramBundle = new AlertDialog.Builder(this).setTitle(2131691983).setOnDismissListener(this);
    int i = 0;
    int j = getResourceId();
    if (j != 0) {}
    for (;;)
    {
      try
      {
        Drawable localDrawable = getDrawable(j);
        if (localDrawable.getIntrinsicWidth() <= 2) {
          continue;
        }
        i = localDrawable.getIntrinsicHeight();
        if (i <= 2) {
          continue;
        }
        i = 1;
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        i = 0;
        continue;
        if (((CharSequence)localObject).length() <= 0) {
          continue;
        }
        paramBundle.setMessage((CharSequence)localObject);
        ((TextView)paramBundle.show().findViewById(16908299)).setGravity(17);
        return;
        finish();
      }
      localObject = ((Resources)localObject).getText(2131693076);
      if (i == 0) {
        continue;
      }
      localObject = getLayoutInflater().inflate(2130968947, null);
      ((ImageView)((View)localObject).findViewById(2131362502)).setImageResource(j);
      paramBundle.setView((View)localObject);
      paramBundle.show();
      return;
      i = 0;
      continue;
      i = 0;
    }
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RegulatoryInfoDisplayActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */