package com.android.settings;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.telephony.SmsApplication.SmsApplicationData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SmsDefaultDialog
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private SmsApplication.SmsApplicationData mNewSmsApplicationData;
  
  private boolean buildDialog(String paramString)
  {
    if (!((TelephonyManager)getSystemService("phone")).isSmsCapable()) {
      return false;
    }
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131691933);
    this.mNewSmsApplicationData = SmsApplication.getSmsApplicationData(paramString, this);
    if (this.mNewSmsApplicationData != null)
    {
      paramString = null;
      Object localObject = SmsApplication.getDefaultSmsApplication(this, true);
      if (localObject != null)
      {
        localObject = SmsApplication.getSmsApplicationData(((ComponentName)localObject).getPackageName(), this);
        paramString = (String)localObject;
        if (((SmsApplication.SmsApplicationData)localObject).mPackageName.equals(this.mNewSmsApplicationData.mPackageName)) {
          return false;
        }
      }
      if (paramString != null)
      {
        localAlertParams.mMessage = getString(2131691934, new Object[] { this.mNewSmsApplicationData.getApplicationName(this), paramString.getApplicationName(this) });
        localAlertParams.mPositiveButtonText = getString(2131690771);
        localAlertParams.mNegativeButtonText = getString(2131690772);
        localAlertParams.mPositiveButtonListener = this;
      }
    }
    for (localAlertParams.mNegativeButtonListener = this;; localAlertParams.mNegativeButtonListener = this)
    {
      setupAlert();
      return true;
      localAlertParams.mMessage = getString(2131691935, new Object[] { this.mNewSmsApplicationData.getApplicationName(this) });
      break;
      localAlertParams.mAdapter = new AppListAdapter();
      localAlertParams.mOnClickListener = this;
      localAlertParams.mNegativeButtonText = getString(2131690993);
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      if (paramInt >= 0)
      {
        paramDialogInterface = (AppListAdapter)this.mAlertParams.mAdapter;
        if (!paramDialogInterface.isSelected(paramInt))
        {
          paramDialogInterface = paramDialogInterface.getPackageName(paramInt);
          if (!TextUtils.isEmpty(paramDialogInterface))
          {
            SmsApplication.setDefaultApplication(paramDialogInterface, this);
            setResult(-1);
          }
        }
      }
    case -2: 
      return;
    }
    SmsApplication.setDefaultApplication(this.mNewSmsApplicationData.mPackageName, this);
    setResult(-1);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent().getStringExtra("package");
    setResult(0);
    if (!buildDialog(paramBundle)) {
      finish();
    }
  }
  
  private class AppListAdapter
    extends BaseAdapter
  {
    private final List<Item> mItems = getItems();
    private final int mSelectedIndex;
    
    public AppListAdapter()
    {
      int j = getSelectedIndex();
      int i = j;
      if (j > 0)
      {
        this$1 = (Item)this.mItems.remove(j);
        this.mItems.add(0, SmsDefaultDialog.this);
        i = 0;
      }
      this.mSelectedIndex = i;
    }
    
    private List<Item> getItems()
    {
      PackageManager localPackageManager = SmsDefaultDialog.this.getPackageManager();
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = SmsApplication.getApplicationCollection(SmsDefaultDialog.this).iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (SmsApplication.SmsApplicationData)localIterator.next();
        try
        {
          localObject = ((SmsApplication.SmsApplicationData)localObject).mPackageName;
          ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo((String)localObject, 0);
          if (localApplicationInfo != null) {
            localArrayList.add(new Item(localApplicationInfo.loadLabel(localPackageManager).toString(), localApplicationInfo.loadIcon(localPackageManager), (String)localObject));
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
      }
      return localArrayList;
    }
    
    private int getSelectedIndex()
    {
      Object localObject = SmsApplication.getDefaultSmsApplication(SmsDefaultDialog.this, true);
      if (localObject != null)
      {
        localObject = ((ComponentName)localObject).getPackageName();
        if (!TextUtils.isEmpty((CharSequence)localObject))
        {
          int i = 0;
          while (i < this.mItems.size())
          {
            if (TextUtils.equals(((Item)this.mItems.get(i)).packgeName, (CharSequence)localObject)) {
              return i;
            }
            i += 1;
          }
        }
      }
      return -1;
    }
    
    public int getCount()
    {
      if (this.mItems != null) {
        return this.mItems.size();
      }
      return 0;
    }
    
    public Object getItem(int paramInt)
    {
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (this.mItems != null)
      {
        localObject1 = localObject2;
        if (paramInt < this.mItems.size()) {
          localObject1 = this.mItems.get(paramInt);
        }
      }
      return localObject1;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public String getPackageName(int paramInt)
    {
      Item localItem = (Item)getItem(paramInt);
      if (localItem != null) {
        return localItem.packgeName;
      }
      return null;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = (Item)getItem(paramInt);
      paramViewGroup = SmsDefaultDialog.this.getLayoutInflater().inflate(2130968619, paramViewGroup, false);
      ((TextView)paramViewGroup.findViewById(16908310)).setText(paramView.label);
      if (paramInt == this.mSelectedIndex) {
        paramViewGroup.findViewById(2131361964).setVisibility(0);
      }
      for (;;)
      {
        ((ImageView)paramViewGroup.findViewById(16908294)).setImageDrawable(paramView.icon);
        return paramViewGroup;
        paramViewGroup.findViewById(2131361964).setVisibility(8);
      }
    }
    
    public boolean isSelected(int paramInt)
    {
      return paramInt == this.mSelectedIndex;
    }
    
    private class Item
    {
      final Drawable icon;
      final String label;
      final String packgeName;
      
      public Item(String paramString1, Drawable paramDrawable, String paramString2)
      {
        this.label = paramString1;
        this.icon = paramDrawable;
        this.packgeName = paramString2;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SmsDefaultDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */