package com.android.settings.nfc;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import com.android.settings.CustomDialogPreference;
import java.util.List;

public class NfcPaymentPreference
  extends CustomDialogPreference
  implements PaymentBackend.Callback, View.OnClickListener
{
  private static final String TAG = "NfcPaymentPreference";
  private final NfcPaymentAdapter mAdapter;
  private final Context mContext;
  private final LayoutInflater mLayoutInflater;
  private final PaymentBackend mPaymentBackend;
  private ImageView mSettingsButtonView;
  
  public NfcPaymentPreference(Context paramContext, PaymentBackend paramPaymentBackend)
  {
    super(paramContext, null);
    this.mPaymentBackend = paramPaymentBackend;
    this.mContext = paramContext;
    paramPaymentBackend.registerCallback(this);
    this.mAdapter = new NfcPaymentAdapter();
    setDialogTitle(paramContext.getString(2131692986));
    this.mLayoutInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    setWidgetLayoutResource(2130968931);
    refresh();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSettingsButtonView = ((ImageView)paramPreferenceViewHolder.findViewById(2131362454));
    this.mSettingsButtonView.setOnClickListener(this);
    updateSettingsVisibility();
  }
  
  public void onClick(View paramView)
  {
    paramView = this.mPaymentBackend.getDefaultApp();
    Intent localIntent;
    if ((paramView != null) && (paramView.settingsComponent != null))
    {
      localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setComponent(paramView.settingsComponent);
      localIntent.addFlags(268435456);
    }
    try
    {
      this.mContext.startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException paramView)
    {
      Log.e("NfcPaymentPreference", "Settings activity not found.");
    }
  }
  
  public void onPaymentAppsChanged()
  {
    refresh();
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    super.onPrepareDialogBuilder(paramBuilder, paramOnClickListener);
    paramBuilder.setSingleChoiceItems(this.mAdapter, 0, paramOnClickListener);
  }
  
  public void refresh()
  {
    Object localObject = this.mPaymentBackend.getPaymentAppInfos();
    PaymentBackend.PaymentAppInfo localPaymentAppInfo = this.mPaymentBackend.getDefaultApp();
    if (localObject != null)
    {
      localObject = (PaymentBackend.PaymentAppInfo[])((List)localObject).toArray(new PaymentBackend.PaymentAppInfo[((List)localObject).size()]);
      this.mAdapter.updateApps((PaymentBackend.PaymentAppInfo[])localObject, localPaymentAppInfo);
    }
    setTitle(2131692980);
    if (localPaymentAppInfo != null) {
      setSummary(localPaymentAppInfo.label);
    }
    for (;;)
    {
      updateSettingsVisibility();
      return;
      setSummary(this.mContext.getString(2131692981));
    }
  }
  
  void updateSettingsVisibility()
  {
    if (this.mSettingsButtonView != null)
    {
      PaymentBackend.PaymentAppInfo localPaymentAppInfo = this.mPaymentBackend.getDefaultApp();
      if ((localPaymentAppInfo == null) || (localPaymentAppInfo.settingsComponent == null)) {
        this.mSettingsButtonView.setVisibility(8);
      }
    }
    else
    {
      return;
    }
    this.mSettingsButtonView.setVisibility(0);
  }
  
  class NfcPaymentAdapter
    extends BaseAdapter
    implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
  {
    private PaymentBackend.PaymentAppInfo[] appInfos;
    
    public NfcPaymentAdapter() {}
    
    public int getCount()
    {
      return this.appInfos.length;
    }
    
    public PaymentBackend.PaymentAppInfo getItem(int paramInt)
    {
      return this.appInfos[paramInt];
    }
    
    public long getItemId(int paramInt)
    {
      return this.appInfos[paramInt].componentName.hashCode();
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      PaymentBackend.PaymentAppInfo localPaymentAppInfo = this.appInfos[paramInt];
      if (paramView == null)
      {
        paramView = NfcPaymentPreference.-get0(NfcPaymentPreference.this).inflate(2130968757, paramViewGroup, false);
        paramViewGroup = new ViewHolder();
        paramViewGroup.imageView = ((ImageView)paramView.findViewById(2131362228));
        paramViewGroup.radioButton = ((RadioButton)paramView.findViewById(2131362227));
        paramView.setTag(paramViewGroup);
      }
      for (;;)
      {
        paramViewGroup.imageView.setImageDrawable(localPaymentAppInfo.banner);
        paramViewGroup.imageView.setTag(localPaymentAppInfo);
        paramViewGroup.imageView.setContentDescription(localPaymentAppInfo.label);
        paramViewGroup.imageView.setOnClickListener(this);
        paramViewGroup.radioButton.setOnCheckedChangeListener(null);
        paramViewGroup.radioButton.setChecked(localPaymentAppInfo.isDefault);
        paramViewGroup.radioButton.setContentDescription(localPaymentAppInfo.label);
        paramViewGroup.radioButton.setOnCheckedChangeListener(this);
        paramViewGroup.radioButton.setTag(localPaymentAppInfo);
        return paramView;
        paramViewGroup = (ViewHolder)paramView.getTag();
      }
    }
    
    void makeDefault(PaymentBackend.PaymentAppInfo paramPaymentAppInfo)
    {
      if (!paramPaymentAppInfo.isDefault) {
        NfcPaymentPreference.-get1(NfcPaymentPreference.this).setDefaultPaymentApp(paramPaymentAppInfo.componentName);
      }
      NfcPaymentPreference.this.getDialog().dismiss();
    }
    
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      makeDefault((PaymentBackend.PaymentAppInfo)paramCompoundButton.getTag());
    }
    
    public void onClick(View paramView)
    {
      makeDefault((PaymentBackend.PaymentAppInfo)paramView.getTag());
    }
    
    public void updateApps(PaymentBackend.PaymentAppInfo[] paramArrayOfPaymentAppInfo, PaymentBackend.PaymentAppInfo paramPaymentAppInfo)
    {
      this.appInfos = paramArrayOfPaymentAppInfo;
      notifyDataSetChanged();
    }
    
    public class ViewHolder
    {
      public ImageView imageView;
      public RadioButton radioButton;
      
      public ViewHolder() {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\NfcPaymentPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */