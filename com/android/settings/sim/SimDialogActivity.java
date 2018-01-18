package com.android.settings.sim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SimDialogActivity
  extends Activity
{
  public static final int CALLS_PICK = 1;
  public static final int DATA_PICK = 0;
  public static String DIALOG_TYPE_KEY = "dialog_type";
  public static final int INVALID_PICK = -1;
  public static final int PREFERRED_PICK = 3;
  public static String PREFERRED_SIM;
  public static final int SMS_PICK = 2;
  private static String TAG = "SimDialogActivity";
  
  static
  {
    PREFERRED_SIM = "preferred_sim";
  }
  
  private void displayPreferredDialog(int paramInt)
  {
    Resources localResources = getResources();
    final Context localContext = getApplicationContext();
    final SubscriptionInfo localSubscriptionInfo = SubscriptionManager.from(localContext).getActiveSubscriptionInfoForSimSlotIndex(paramInt);
    if (localSubscriptionInfo != null)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
      localBuilder.setTitle(2131691670);
      localBuilder.setMessage(localResources.getString(2131691671, new Object[] { localSubscriptionInfo.getDisplayName() }));
      localBuilder.setPositiveButton(2131690771, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousInt = localSubscriptionInfo.getSubscriptionId();
          paramAnonymousDialogInterface = SimDialogActivity.-wrap0(SimDialogActivity.this, paramAnonymousInt);
          SimDialogActivity.-wrap1(localContext, paramAnonymousInt);
          SimDialogActivity.-wrap2(localContext, paramAnonymousInt);
          SimDialogActivity.-wrap3(SimDialogActivity.this, paramAnonymousDialogInterface);
          SimDialogActivity.this.finish();
        }
      });
      localBuilder.setNegativeButton(2131690772, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          SimDialogActivity.this.finish();
        }
      });
      localBuilder.create().show();
      return;
    }
    finish();
  }
  
  private static void setDefaultDataSubId(Context paramContext, int paramInt)
  {
    SubscriptionManager.from(paramContext).setDefaultDataSubId(paramInt);
    Toast.makeText(paramContext, 2131693086, 1).show();
  }
  
  private static void setDefaultSmsSubId(Context paramContext, int paramInt)
  {
    SubscriptionManager.from(paramContext).setDefaultSmsSubId(paramInt);
  }
  
  private void setUserSelectedOutgoingPhoneAccount(PhoneAccountHandle paramPhoneAccountHandle)
  {
    TelecomManager.from(this).setUserSelectedOutgoingPhoneAccount(paramPhoneAccountHandle);
  }
  
  private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int paramInt)
  {
    TelecomManager localTelecomManager = TelecomManager.from(this);
    TelephonyManager localTelephonyManager = TelephonyManager.from(this);
    ListIterator localListIterator = localTelecomManager.getCallCapablePhoneAccounts().listIterator();
    while (localListIterator.hasNext())
    {
      PhoneAccountHandle localPhoneAccountHandle = (PhoneAccountHandle)localListIterator.next();
      if (paramInt == localTelephonyManager.getSubIdForPhoneAccount(localTelecomManager.getPhoneAccount(localPhoneAccountHandle))) {
        return localPhoneAccountHandle;
      }
    }
    return null;
  }
  
  public Dialog createDialog(final Context paramContext, final int paramInt)
  {
    ArrayList localArrayList2 = new ArrayList();
    final List localList = SubscriptionManager.from(paramContext).getActiveSubscriptionInfoList();
    int i;
    DialogInterface.OnClickListener local3;
    DialogInterface.OnKeyListener local4;
    ArrayList localArrayList1;
    ListIterator localListIterator;
    if (localList == null)
    {
      i = 0;
      local3 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          switch (paramInt)
          {
          default: 
            throw new IllegalArgumentException("Invalid dialog type " + paramInt + " in SIM dialog.");
          case 0: 
            paramAnonymousDialogInterface = (SubscriptionInfo)localList.get(paramAnonymousInt);
            SimDialogActivity.-wrap1(paramContext, paramAnonymousDialogInterface.getSubscriptionId());
          }
          for (;;)
          {
            SimDialogActivity.this.finish();
            return;
            paramAnonymousDialogInterface = TelecomManager.from(paramContext).getCallCapablePhoneAccounts();
            SimDialogActivity localSimDialogActivity = SimDialogActivity.this;
            if (paramAnonymousInt < 1) {}
            for (paramAnonymousDialogInterface = null;; paramAnonymousDialogInterface = (PhoneAccountHandle)paramAnonymousDialogInterface.get(paramAnonymousInt - 1))
            {
              SimDialogActivity.-wrap3(localSimDialogActivity, paramAnonymousDialogInterface);
              break;
            }
            paramAnonymousDialogInterface = (SubscriptionInfo)localList.get(paramAnonymousInt);
            SimDialogActivity.-wrap2(paramContext, paramAnonymousDialogInterface.getSubscriptionId());
          }
        }
      };
      local4 = new DialogInterface.OnKeyListener()
      {
        public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 4) {
            SimDialogActivity.this.finish();
          }
          return true;
        }
      };
      localArrayList1 = new ArrayList();
      if (paramInt == 1)
      {
        localObject1 = TelecomManager.from(paramContext);
        localObject2 = TelephonyManager.from(paramContext);
        localListIterator = ((TelecomManager)localObject1).getCallCapablePhoneAccounts().listIterator();
        localArrayList2.add(getResources().getString(2131693113));
        localArrayList1.add(null);
      }
    }
    else
    {
      for (;;)
      {
        if (!localListIterator.hasNext()) {
          break label262;
        }
        PhoneAccount localPhoneAccount = ((TelecomManager)localObject1).getPhoneAccount((PhoneAccountHandle)localListIterator.next());
        localArrayList2.add((String)localPhoneAccount.getLabel());
        i = ((TelephonyManager)localObject2).getSubIdForPhoneAccount(localPhoneAccount);
        if (i != -1)
        {
          localArrayList1.add(SubscriptionManager.from(paramContext).getActiveSubscriptionInfo(i));
          continue;
          i = localList.size();
          break;
        }
        localArrayList1.add(null);
      }
    }
    int j = 0;
    while (j < i)
    {
      localObject2 = ((SubscriptionInfo)localList.get(j)).getDisplayName();
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = "";
      }
      localArrayList2.add(((CharSequence)localObject1).toString());
      j += 1;
    }
    label262:
    Object localObject1 = (String[])localArrayList2.toArray(new String[0]);
    Object localObject2 = new AlertDialog.Builder(paramContext);
    if (paramInt == 1) {}
    for (paramContext = localArrayList1;; paramContext = localList)
    {
      paramContext = new SelectAccountListAdapter(paramContext, ((AlertDialog.Builder)localObject2).getContext(), 2130968977, (String[])localObject1, paramInt);
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Invalid dialog type " + paramInt + " in SIM dialog.");
      }
    }
    ((AlertDialog.Builder)localObject2).setTitle(2131693085);
    for (;;)
    {
      paramContext = ((AlertDialog.Builder)localObject2).setAdapter(paramContext, local3).create();
      paramContext.setOnKeyListener(local4);
      paramContext.setOnCancelListener(new DialogInterface.OnCancelListener()
      {
        public void onCancel(DialogInterface paramAnonymousDialogInterface)
        {
          SimDialogActivity.this.finish();
        }
      });
      return paramContext;
      ((AlertDialog.Builder)localObject2).setTitle(2131693087);
      continue;
      ((AlertDialog.Builder)localObject2).setTitle(2131693097);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent().getExtras();
    int i = paramBundle.getInt(DIALOG_TYPE_KEY, -1);
    if (!SystemProperties.getBoolean("persist.radio.aosp_usr_pref_sel", false))
    {
      finish();
      return;
    }
    switch (i)
    {
    default: 
      throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
    case 0: 
    case 1: 
    case 2: 
      createDialog(this, i).show();
      return;
    }
    displayPreferredDialog(paramBundle.getInt(PREFERRED_SIM));
  }
  
  private class SelectAccountListAdapter
    extends ArrayAdapter<String>
  {
    private final float OPACITY = 0.54F;
    private Context mContext;
    private int mDialogId;
    private int mResId;
    private List<SubscriptionInfo> mSubInfoList;
    
    public SelectAccountListAdapter(Context paramContext, int paramInt1, String[] paramArrayOfString, int paramInt2)
    {
      super(paramArrayOfString, paramInt2);
      this.mContext = paramInt1;
      this.mResId = paramArrayOfString;
      int i;
      this.mDialogId = i;
      this.mSubInfoList = paramContext;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
      if (paramView == null)
      {
        paramViewGroup = paramViewGroup.inflate(this.mResId, null);
        paramView = new ViewHolder(null);
        paramView.title = ((TextView)paramViewGroup.findViewById(2131361894));
        paramView.summary = ((TextView)paramViewGroup.findViewById(2131362024));
        paramView.icon = ((ImageView)paramViewGroup.findViewById(2131361793));
        paramViewGroup.setTag(paramView);
      }
      SubscriptionInfo localSubscriptionInfo;
      for (;;)
      {
        localSubscriptionInfo = (SubscriptionInfo)this.mSubInfoList.get(paramInt);
        if (localSubscriptionInfo != null) {
          break;
        }
        paramView.title.setText((CharSequence)getItem(paramInt));
        paramView.summary.setText("");
        paramView.icon.setImageDrawable(SimDialogActivity.this.getResources().getDrawable(2130837992));
        paramView.icon.setAlpha(0.54F);
        return paramViewGroup;
        paramViewGroup = paramView;
        paramView = (ViewHolder)paramView.getTag();
      }
      paramView.title.setText(localSubscriptionInfo.getDisplayName());
      paramView.summary.setText(localSubscriptionInfo.getNumber());
      paramView.icon.setImageBitmap(localSubscriptionInfo.createIconBitmap(this.mContext));
      return paramViewGroup;
    }
    
    private class ViewHolder
    {
      ImageView icon;
      TextView summary;
      TextView title;
      
      private ViewHolder() {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\sim\SimDialogActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */