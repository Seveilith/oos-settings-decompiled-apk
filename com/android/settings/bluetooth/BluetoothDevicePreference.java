package com.android.settings.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.android.settings.search.Index;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback;
import com.android.settingslib.bluetooth.HidProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import java.util.Iterator;

public final class BluetoothDevicePreference
  extends Preference
  implements CachedBluetoothDevice.Callback, View.OnClickListener
{
  private static final String TAG = "BluetoothDevicePreference";
  private static int sDimAlpha = Integer.MIN_VALUE;
  public final String BLUETOOTH = this.r.getString(2131693751);
  public final String COMPUTER = this.r.getString(2131693745);
  public final String HEADPHONE = this.r.getString(2131693749);
  public final String HEADSET = this.r.getString(2131693746);
  public final String IMAGING = this.r.getString(2131693748);
  public final String INPUT_PERIPHERAL = this.r.getString(2131693750);
  public final String PHONE = this.r.getString(2131693747);
  private String contentDescription = null;
  private final CachedBluetoothDevice mCachedDevice;
  private AlertDialog mDisconnectDialog;
  private View.OnClickListener mOnSettingsClickListener;
  Resources r = getContext().getResources();
  
  public BluetoothDevicePreference(Context paramContext, CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    super(paramContext);
    if (sDimAlpha == Integer.MIN_VALUE)
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(16842803, localTypedValue, true);
      sDimAlpha = (int)(localTypedValue.getFloat() * 255.0F);
    }
    this.mCachedDevice = paramCachedBluetoothDevice;
    setLayoutResource(2130968892);
    if ((paramCachedBluetoothDevice.getBondState() == 12) && (!((UserManager)paramContext.getSystemService("user")).hasUserRestriction("no_config_bluetooth"))) {
      setWidgetLayoutResource(2130968891);
    }
    this.mCachedDevice.registerCallback(this);
    onDeviceAttributesChanged();
  }
  
  private void askDisconnect()
  {
    Context localContext = getContext();
    String str2 = this.mCachedDevice.getName();
    String str1 = str2;
    if (TextUtils.isEmpty(str2)) {
      str1 = localContext.getString(2131690867);
    }
    str1 = localContext.getString(2131690859, new Object[] { str1 });
    str2 = localContext.getString(2131690858);
    DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        BluetoothDevicePreference.-get0(BluetoothDevicePreference.this).disconnect();
      }
    };
    this.mDisconnectDialog = Utils.showDisconnectDialog(localContext, this.mDisconnectDialog, local1, str2, Html.fromHtml(str1));
  }
  
  private Pair<Integer, String> getBtClassDrawableWithDescription()
  {
    BluetoothClass localBluetoothClass = this.mCachedDevice.getBtClass();
    if (localBluetoothClass != null) {
      switch (localBluetoothClass.getMajorDeviceClass())
      {
      }
    }
    for (;;)
    {
      Iterator localIterator = this.mCachedDevice.getProfiles().iterator();
      int i;
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        i = ((LocalBluetoothProfile)localIterator.next()).getDrawableResource(localBluetoothClass);
      } while (i == 0);
      return new Pair(Integer.valueOf(i), null);
      return new Pair(Integer.valueOf(2130837952), this.COMPUTER);
      return new Pair(Integer.valueOf(2130837947), this.PHONE);
      return new Pair(Integer.valueOf(HidProfile.getHidClassDrawable(localBluetoothClass)), this.INPUT_PERIPHERAL);
      return new Pair(Integer.valueOf(2130837951), this.IMAGING);
      Log.w("BluetoothDevicePreference", "mBtClass is null");
    }
    if (localBluetoothClass != null)
    {
      if (localBluetoothClass.doesClassMatch(1)) {
        return new Pair(Integer.valueOf(2130837949), this.HEADPHONE);
      }
      if (localBluetoothClass.doesClassMatch(0)) {
        return new Pair(Integer.valueOf(2130837950), this.HEADSET);
      }
    }
    return new Pair(Integer.valueOf(2130838037), this.BLUETOOTH);
  }
  
  private void pair()
  {
    if (!this.mCachedDevice.startPairing())
    {
      Utils.showError(getContext(), this.mCachedDevice.getName(), 2131689571);
      return;
    }
    Context localContext = getContext();
    SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(localContext);
    localSearchIndexableRaw.className = BluetoothSettings.class.getName();
    localSearchIndexableRaw.title = this.mCachedDevice.getName();
    localSearchIndexableRaw.screenTitle = localContext.getResources().getString(2131691235);
    localSearchIndexableRaw.iconResId = 2130838037;
    localSearchIndexableRaw.enabled = true;
    Index.getInstance(localContext).updateFromSearchIndexableData(localSearchIndexableRaw);
  }
  
  public int compareTo(Preference paramPreference)
  {
    if (!(paramPreference instanceof BluetoothDevicePreference)) {
      return super.compareTo(paramPreference);
    }
    return this.mCachedDevice.compareTo(((BluetoothDevicePreference)paramPreference).mCachedDevice);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof BluetoothDevicePreference))) {
      return this.mCachedDevice.equals(((BluetoothDevicePreference)paramObject).mCachedDevice);
    }
    return false;
  }
  
  CachedBluetoothDevice getCachedDevice()
  {
    return this.mCachedDevice;
  }
  
  public int hashCode()
  {
    return this.mCachedDevice.hashCode();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    if (findPreferenceInHierarchy("bt_checkbox") != null) {
      setDependency("bt_checkbox");
    }
    if (this.mCachedDevice.getBondState() == 12)
    {
      localImageView = (ImageView)paramPreferenceViewHolder.findViewById(2131362413);
      if (localImageView != null)
      {
        localImageView.setOnClickListener(this);
        localImageView.setTag(this.mCachedDevice);
      }
    }
    ImageView localImageView = (ImageView)paramPreferenceViewHolder.findViewById(16908294);
    if (localImageView != null) {
      localImageView.setContentDescription(this.contentDescription);
    }
    super.onBindViewHolder(paramPreferenceViewHolder);
  }
  
  public void onClick(View paramView)
  {
    if (this.mOnSettingsClickListener != null) {
      this.mOnSettingsClickListener.onClick(paramView);
    }
  }
  
  void onClicked()
  {
    int i = this.mCachedDevice.getBondState();
    if (this.mCachedDevice.isConnected()) {
      askDisconnect();
    }
    do
    {
      return;
      if (i == 12)
      {
        this.mCachedDevice.connect(true);
        return;
      }
    } while (i != 10);
    pair();
  }
  
  public void onDeviceAttributesChanged()
  {
    setTitle(this.mCachedDevice.getName());
    int i = this.mCachedDevice.getConnectionSummary();
    if (i != 0)
    {
      setSummary(i);
      Pair localPair = getBtClassDrawableWithDescription();
      if (((Integer)localPair.first).intValue() != 0)
      {
        Drawable localDrawable = getContext().getResources().getDrawable(((Integer)localPair.first).intValue(), getContext().getTheme());
        localDrawable.setTint(getContext().getResources().getColor(2131493777));
        setIcon(localDrawable);
        this.contentDescription = ((String)localPair.second);
      }
      if (!this.mCachedDevice.isBusy()) {
        break label140;
      }
    }
    label140:
    for (boolean bool = false;; bool = true)
    {
      setEnabled(bool);
      notifyHierarchyChanged();
      return;
      setSummary(null);
      break;
    }
  }
  
  protected void onPrepareForRemoval()
  {
    super.onPrepareForRemoval();
    this.mCachedDevice.unregisterCallback(this);
    if (this.mDisconnectDialog != null)
    {
      this.mDisconnectDialog.dismiss();
      this.mDisconnectDialog = null;
    }
  }
  
  void rebind()
  {
    notifyChanged();
  }
  
  public void setOnSettingsClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnSettingsClickListener = paramOnClickListener;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothDevicePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */