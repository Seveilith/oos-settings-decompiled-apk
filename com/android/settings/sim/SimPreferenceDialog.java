package com.android.settings.sim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SimPreferenceDialog
  extends Activity
{
  private final String SIM_NAME = "sim_name";
  private final String TINT_POS = "tint_pos";
  AlertDialog.Builder mBuilder;
  private String[] mColorStrings;
  private Context mContext;
  View mDialogLayout;
  private int mSlotId;
  private SubscriptionInfo mSubInfoRecord;
  private SubscriptionManager mSubscriptionManager;
  private int[] mTintArr;
  private int mTintSelectorPos;
  
  private void createEditDialog(Bundle paramBundle)
  {
    Resources localResources = this.mContext.getResources();
    ((EditText)this.mDialogLayout.findViewById(2131362213)).setText(this.mSubInfoRecord.getDisplayName());
    final Spinner localSpinner = (Spinner)this.mDialogLayout.findViewById(2131362214);
    paramBundle = new SelectColorAdapter(this.mContext, 2130968983, this.mColorStrings);
    paramBundle.setDropDownViewResource(17367049);
    localSpinner.setAdapter(paramBundle);
    int i = 0;
    TextView localTextView;
    String str;
    if (i < this.mTintArr.length)
    {
      if (this.mTintArr[i] == this.mSubInfoRecord.getIconTint())
      {
        localSpinner.setSelection(i);
        this.mTintSelectorPos = i;
      }
    }
    else
    {
      localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
        public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          localSpinner.setSelection(paramAnonymousInt);
          SimPreferenceDialog.-set0(SimPreferenceDialog.this, paramAnonymousInt);
        }
        
        public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
      });
      paramBundle = (TelephonyManager)this.mContext.getSystemService("phone");
      localTextView = (TextView)this.mDialogLayout.findViewById(2131362216);
      str = paramBundle.getLine1Number(this.mSubInfoRecord.getSubscriptionId());
      if (!TextUtils.isEmpty(str)) {
        break label318;
      }
      localTextView.setText(localResources.getString(17039374));
      label187:
      paramBundle = paramBundle.getSimOperatorName(this.mSubInfoRecord.getSubscriptionId());
      localTextView = (TextView)this.mDialogLayout.findViewById(2131362215);
      if (TextUtils.isEmpty(paramBundle)) {
        break label331;
      }
    }
    for (;;)
    {
      localTextView.setText(paramBundle);
      this.mBuilder.setTitle(String.format(localResources.getString(2131693093), new Object[] { Integer.valueOf(this.mSubInfoRecord.getSimSlotIndex() + 1) }));
      this.mBuilder.setPositiveButton(2131690994, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          String str = ((EditText)SimPreferenceDialog.this.mDialogLayout.findViewById(2131362213)).getText().toString();
          paramAnonymousInt = SimPreferenceDialog.-get0(SimPreferenceDialog.this).getSubscriptionId();
          SimPreferenceDialog.-get0(SimPreferenceDialog.this).setDisplayName(str);
          SimPreferenceDialog.-get1(SimPreferenceDialog.this).setDisplayName(str, paramAnonymousInt, 2L);
          int i = localSpinner.getSelectedItemPosition();
          paramAnonymousInt = SimPreferenceDialog.-get0(SimPreferenceDialog.this).getSubscriptionId();
          i = SimPreferenceDialog.-get2(SimPreferenceDialog.this)[i];
          SimPreferenceDialog.-get0(SimPreferenceDialog.this).setIconTint(i);
          SimPreferenceDialog.-get1(SimPreferenceDialog.this).setIconTint(i, paramAnonymousInt);
          paramAnonymousDialogInterface.dismiss();
          SimPreferenceDialog.this.finish();
        }
      });
      this.mBuilder.setNegativeButton(2131690993, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.dismiss();
          SimPreferenceDialog.this.finish();
        }
      });
      this.mBuilder.create().show();
      return;
      i += 1;
      break;
      label318:
      localTextView.setText(PhoneNumberUtils.formatNumber(str));
      break label187;
      label331:
      paramBundle = this.mContext.getString(17039374);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = this;
    this.mSlotId = getIntent().getExtras().getInt("slot_id", -1);
    this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
    this.mSubInfoRecord = this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(this.mSlotId);
    this.mTintArr = this.mContext.getResources().getIntArray(17235979);
    this.mColorStrings = this.mContext.getResources().getStringArray(2131427445);
    this.mTintSelectorPos = 0;
    this.mBuilder = new AlertDialog.Builder(this.mContext);
    this.mDialogLayout = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(2130968752, null);
    this.mBuilder.setView(this.mDialogLayout);
    createEditDialog(paramBundle);
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    int i = paramBundle.getInt("tint_pos");
    ((Spinner)this.mDialogLayout.findViewById(2131362214)).setSelection(i);
    this.mTintSelectorPos = i;
    ((EditText)this.mDialogLayout.findViewById(2131362213)).setText(paramBundle.getString("sim_name"));
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("tint_pos", this.mTintSelectorPos);
    paramBundle.putString("sim_name", ((EditText)this.mDialogLayout.findViewById(2131362213)).getText().toString());
    super.onSaveInstanceState(paramBundle);
  }
  
  private class SelectColorAdapter
    extends ArrayAdapter<CharSequence>
  {
    private Context mContext;
    private int mResId;
    
    public SelectColorAdapter(Context paramContext, int paramInt, String[] paramArrayOfString)
    {
      super(paramInt, paramArrayOfString);
      this.mContext = paramContext;
      this.mResId = paramInt;
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = getView(paramInt, paramView, paramViewGroup);
      paramViewGroup = (ViewHolder)paramView.getTag();
      if (SimPreferenceDialog.-get3(SimPreferenceDialog.this) == paramInt) {
        paramViewGroup.swatch.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
      }
      for (;;)
      {
        paramViewGroup.icon.setVisibility(0);
        return paramView;
        paramViewGroup.swatch.getPaint().setStyle(Paint.Style.STROKE);
      }
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
      Object localObject = this.mContext.getResources();
      int i = ((Resources)localObject).getDimensionPixelSize(2131755566);
      int j = ((Resources)localObject).getDimensionPixelSize(2131755567);
      if (paramView == null)
      {
        paramViewGroup = paramViewGroup.inflate(this.mResId, null);
        paramView = new ViewHolder(null);
        localObject = new ShapeDrawable(new OvalShape());
        ((ShapeDrawable)localObject).setIntrinsicHeight(i);
        ((ShapeDrawable)localObject).setIntrinsicWidth(i);
        ((ShapeDrawable)localObject).getPaint().setStrokeWidth(j);
        paramView.label = ((TextView)paramViewGroup.findViewById(2131362545));
        paramView.icon = ((ImageView)paramViewGroup.findViewById(2131362544));
        paramView.swatch = ((ShapeDrawable)localObject);
        paramViewGroup.setTag(paramView);
      }
      for (;;)
      {
        paramView.label.setText((CharSequence)getItem(paramInt));
        paramView.swatch.getPaint().setColor(SimPreferenceDialog.-get2(SimPreferenceDialog.this)[paramInt]);
        paramView.swatch.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        paramView.icon.setVisibility(0);
        paramView.icon.setImageDrawable(paramView.swatch);
        return paramViewGroup;
        paramViewGroup = paramView;
        paramView = (ViewHolder)paramView.getTag();
      }
    }
    
    private class ViewHolder
    {
      ImageView icon;
      TextView label;
      ShapeDrawable swatch;
      
      private ViewHolder() {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\sim\SimPreferenceDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */