package com.android.settings.datausage;

import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.net.NetworkTemplate;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.support.v7.preference.Preference.BaseSavedState;
import android.support.v7.preference.PreferenceViewHolder;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.CustomDialogPreference;

public class CellDataPreference
  extends CustomDialogPreference
  implements TemplatePreference
{
  private static final String CARRIER_MODE_CT_CLASS_A = "ct_class_a";
  private static final String TAG = "CellDataPreference";
  private String mCarrierMode = SystemProperties.get("persist.radio.carrier_mode", "default");
  public boolean mChecked;
  private boolean mIsCTClassA = this.mCarrierMode.equals("ct_class_a");
  private final DataStateListener mListener = new DataStateListener()
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      CellDataPreference.-wrap0(CellDataPreference.this);
    }
  };
  public int mSubId = -1;
  private SubscriptionManager mSubscriptionManager;
  private TelephonyManager mTelephonyManager;
  
  public CellDataPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16843629);
  }
  
  private void setChecked(boolean paramBoolean)
  {
    if (this.mChecked == paramBoolean) {
      return;
    }
    this.mChecked = paramBoolean;
    notifyChanged();
  }
  
  private void setMobileDataEnabled(boolean paramBoolean)
  {
    this.mTelephonyManager.setDataEnabled(this.mSubId, paramBoolean);
    setChecked(paramBoolean);
  }
  
  private void showDisableDialog(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    paramBuilder.setTitle(null).setMessage(2131692783).setPositiveButton(17039370, paramOnClickListener).setNegativeButton(17039360, null);
  }
  
  private void updateChecked()
  {
    setChecked(this.mTelephonyManager.getDataEnabled(this.mSubId));
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mListener.setListener(true, this.mSubId, getContext());
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(16908352);
    paramPreferenceViewHolder.setClickable(false);
    ((Checkable)paramPreferenceViewHolder).setChecked(this.mChecked);
  }
  
  protected void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt != -1) {
      return;
    }
    setMobileDataEnabled(false);
  }
  
  public void onDetached()
  {
    this.mListener.setListener(false, this.mSubId, getContext());
    super.onDetached();
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    showDisableDialog(paramBuilder, paramOnClickListener);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (CellDataState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mTelephonyManager = TelephonyManager.from(getContext());
    this.mChecked = paramParcelable.mChecked;
    if (this.mSubId == -1)
    {
      this.mSubId = paramParcelable.mSubId;
      setKey(getKey() + this.mSubId);
    }
    notifyChanged();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    CellDataState localCellDataState = new CellDataState(super.onSaveInstanceState());
    localCellDataState.mChecked = this.mChecked;
    localCellDataState.mSubId = this.mSubId;
    return localCellDataState;
  }
  
  protected void performClick(View paramView)
  {
    Context localContext = getContext();
    if (this.mChecked) {}
    for (boolean bool = false;; bool = true)
    {
      MetricsLogger.action(localContext, 178, bool);
      if (!this.mChecked) {
        break;
      }
      super.performClick(paramView);
      return;
    }
    setMobileDataEnabled(true);
  }
  
  public void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, TemplatePreference.NetworkServices paramNetworkServices)
  {
    if (paramInt == -1) {
      throw new IllegalArgumentException("CellDataPreference needs a SubscriptionInfo");
    }
    this.mSubscriptionManager = SubscriptionManager.from(getContext());
    this.mTelephonyManager = TelephonyManager.from(getContext());
    if (this.mSubId == -1)
    {
      this.mSubId = paramInt;
      setKey(getKey() + paramInt);
    }
    updateChecked();
  }
  
  public static class CellDataState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<CellDataState> CREATOR = new Parcelable.Creator()
    {
      public CellDataPreference.CellDataState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new CellDataPreference.CellDataState(paramAnonymousParcel);
      }
      
      public CellDataPreference.CellDataState[] newArray(int paramAnonymousInt)
      {
        return new CellDataPreference.CellDataState[paramAnonymousInt];
      }
    };
    public boolean mChecked;
    public int mSubId;
    
    public CellDataState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readByte() != 0) {
        bool = true;
      }
      this.mChecked = bool;
      this.mSubId = paramParcel.readInt();
    }
    
    public CellDataState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.mChecked) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        paramParcel.writeInt(this.mSubId);
        return;
      }
    }
  }
  
  public static abstract class DataStateListener
    extends ContentObserver
  {
    public DataStateListener()
    {
      super();
    }
    
    public void setListener(boolean paramBoolean, int paramInt, Context paramContext)
    {
      if (paramBoolean)
      {
        Uri localUri = Settings.Global.getUriFor("mobile_data");
        if (TelephonyManager.getDefault().getSimCount() != 1) {
          localUri = Settings.Global.getUriFor("mobile_data" + paramInt);
        }
        paramContext.getContentResolver().registerContentObserver(localUri, false, this);
        return;
      }
      paramContext.getContentResolver().unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\CellDataPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */