package com.oneplus.settings.others;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class OPUserExperiencePreference
  extends Preference
{
  private static final String KEY_FROM_SETTINGS = "key_from_settings";
  private static final String KEY_NOTICES_TYPE = "op_legal_notices_type";
  private static final int KEY_USER_EXPERIENCE_TYPE = 5;
  private static final String OPLEGAL_NOTICES_ACTION = "android.oem.intent.action.OP_LEGAL";
  private Context mContext;
  
  public OPUserExperiencePreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPUserExperiencePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPUserExperiencePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(2130968874);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    boolean bool = true;
    super.onBindViewHolder(paramPreferenceViewHolder);
    ((LinearLayout)paramPreferenceViewHolder.findViewById(2131362392)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = new Intent("android.oem.intent.action.OP_LEGAL");
        paramAnonymousView.putExtra("op_legal_notices_type", 5);
        paramAnonymousView.putExtra("key_from_settings", true);
        OPUserExperiencePreference.-get0(OPUserExperiencePreference.this).startActivity(paramAnonymousView);
      }
    });
    final CheckBox localCheckBox = (CheckBox)paramPreferenceViewHolder.findViewById(2131362394);
    for (;;)
    {
      try
      {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_join_user_plan_settings") != 1) {
          continue;
        }
        localCheckBox.setChecked(bool);
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        localSettingNotFoundException.printStackTrace();
        continue;
      }
      ((LinearLayout)paramPreferenceViewHolder.findViewById(2131362393)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = localCheckBox;
          if (localCheckBox.isChecked()) {}
          for (boolean bool = false;; bool = true)
          {
            paramAnonymousView.setChecked(bool);
            return;
          }
        }
      });
      localCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          paramAnonymousCompoundButton = OPUserExperiencePreference.-get0(OPUserExperiencePreference.this).getContentResolver();
          if (paramAnonymousBoolean) {}
          for (int i = 1;; i = 0)
          {
            Settings.System.putInt(paramAnonymousCompoundButton, "oem_join_user_plan_settings", i);
            if (!paramAnonymousBoolean) {
              break;
            }
            paramAnonymousCompoundButton = new Intent();
            paramAnonymousCompoundButton.setAction("INTENT_START_ODM");
            OPUserExperiencePreference.-get0(OPUserExperiencePreference.this).sendOrderedBroadcast(paramAnonymousCompoundButton, null);
            return;
          }
          paramAnonymousCompoundButton = new Intent();
          paramAnonymousCompoundButton.setAction("INTENT_STOP_ODM");
          OPUserExperiencePreference.-get0(OPUserExperiencePreference.this).sendOrderedBroadcast(paramAnonymousCompoundButton, null);
        }
      });
      return;
      bool = false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\others\OPUserExperiencePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */