package com.oneplus.settings.laboratory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import com.oneplus.settings.utils.OPUtils;

public class OPLabFeatureDetailActivity
  extends Activity
  implements View.OnClickListener, OPRadioButtinGroup.OnRadioGroupClickListener
{
  private static final int ONEPLUS_LAB_FEATURE_DISLIKE = -1;
  private static final String ONEPLUS_LAB_FEATURE_KEY = "oneplus_lab_feature_key";
  private static final int ONEPLUS_LAB_FEATURE_LIKE = 1;
  private static final String ONEPLUS_LAB_FEATURE_SUMMARY = "oneplus_lab_feature_Summary";
  private static final String ONEPLUS_LAB_FEATURE_TITLE = "oneplus_lab_feature_title";
  private static final String ONEPLUS_LAB_FEATURE_TOGGLE_COUNT = "oneplus_lab_feature_toggle_count";
  private static final String ONEPLUS_LAB_FEATURE_TOGGLE_NAMES = "oneplus_lab_feature_toggle_names";
  private static final String SHOW_IMPORTANCE_SLIDER = "show_importance_slider";
  private View mActiviteFeatureToggle;
  private TextView mCommunirySummary;
  private TextView mCommuniryTitle;
  private TextView mDescriptionSummary;
  private TextView mDescriptionTitle;
  private ImageButton mDislikeImageButton;
  private String[] mFeatureToggleNames;
  private Intent mIntent;
  private ImageButton mLikeImageButton;
  private OPRadioButtinGroup mMultiToggleGroup;
  private String mOneplusLabFeatureKey;
  private String mOneplusLabFeatureTitle;
  private int mOneplusLabFeatureToggleCount;
  private SharedPreferences mSharedPreferences;
  private Switch mSwitch;
  
  private void highlightUserChoose(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mLikeImageButton.getBackground().setTint(getColor(2131493788));
      this.mDislikeImageButton.getBackground().setTint(getColor(2131493789));
      return;
    }
    if (paramInt == -1)
    {
      this.mLikeImageButton.getBackground().setTint(getColor(2131493789));
      this.mDislikeImageButton.getBackground().setTint(getColor(2131493788));
      return;
    }
    this.mLikeImageButton.getBackground().setTint(getColor(2131493789));
    this.mDislikeImageButton.getBackground().setTint(getColor(2131493789));
  }
  
  private void initIntent()
  {
    this.mIntent = getIntent();
    this.mOneplusLabFeatureToggleCount = this.mIntent.getIntExtra("oneplus_lab_feature_toggle_count", 2);
    this.mFeatureToggleNames = this.mIntent.getStringArrayExtra("oneplus_lab_feature_toggle_names");
    this.mOneplusLabFeatureTitle = this.mIntent.getStringExtra("oneplus_lab_feature_title");
    this.mOneplusLabFeatureKey = this.mIntent.getStringExtra("oneplus_lab_feature_key");
    setTitle(this.mOneplusLabFeatureTitle);
  }
  
  private void initView()
  {
    boolean bool = true;
    this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    this.mDescriptionTitle = ((TextView)findViewById(2131362344));
    this.mDescriptionSummary = ((TextView)findViewById(2131362345));
    this.mCommuniryTitle = ((TextView)findViewById(2131362346));
    this.mCommunirySummary = ((TextView)findViewById(2131362347));
    this.mActiviteFeatureToggle = findViewById(2131362341);
    this.mSwitch = ((Switch)findViewById(2131362342));
    Object localObject;
    if ("show_importance_slider".equals(this.mOneplusLabFeatureKey))
    {
      localObject = this.mSwitch;
      if (Settings.Secure.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0) == 1)
      {
        bool = true;
        ((Switch)localObject).setChecked(bool);
        this.mMultiToggleGroup = ((OPRadioButtinGroup)findViewById(2131362343));
        if (!isMultiToggle()) {
          break label308;
        }
        this.mMultiToggleGroup.addChild(this.mOneplusLabFeatureToggleCount, this.mFeatureToggleNames);
        this.mMultiToggleGroup.setOnRadioGroupClickListener(this);
        this.mMultiToggleGroup.setSelect(Settings.System.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0));
        this.mActiviteFeatureToggle.setVisibility(8);
      }
    }
    for (;;)
    {
      this.mLikeImageButton = ((ImageButton)findViewById(2131362348));
      this.mDislikeImageButton = ((ImageButton)findViewById(2131362349));
      this.mActiviteFeatureToggle.setOnClickListener(this);
      this.mLikeImageButton.setOnClickListener(this);
      this.mDislikeImageButton.setOnClickListener(this);
      localObject = this.mIntent.getStringExtra("oneplus_lab_feature_Summary");
      this.mDescriptionSummary.setText((CharSequence)localObject);
      setLikeOrDislike();
      return;
      bool = false;
      break;
      localObject = this.mSwitch;
      if (Settings.System.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0) == 1) {}
      for (;;)
      {
        ((Switch)localObject).setChecked(bool);
        break;
        bool = false;
      }
      label308:
      this.mMultiToggleGroup.setVisibility(8);
    }
  }
  
  private void saveActitiveHistory(int paramInt)
  {
    if (!this.mSharedPreferences.contains(this.mOneplusLabFeatureKey))
    {
      OPUtils.sendAppTracker(this.mOneplusLabFeatureTitle, paramInt);
      SharedPreferences.Editor localEditor = this.mSharedPreferences.edit();
      localEditor.putInt(this.mOneplusLabFeatureKey, paramInt);
      localEditor.commit();
    }
    setLikeOrDislike();
  }
  
  private void setLikeOrDislike()
  {
    if (this.mSharedPreferences.contains(this.mOneplusLabFeatureKey))
    {
      highlightUserChoose(this.mSharedPreferences.getInt(this.mOneplusLabFeatureKey, 1));
      this.mLikeImageButton.setEnabled(false);
      this.mDislikeImageButton.setEnabled(false);
      return;
    }
    this.mLikeImageButton.getBackground().setTint(getColor(2131493789));
    this.mDislikeImageButton.getBackground().setTint(getColor(2131493789));
  }
  
  public void OnRadioGroupClick(int paramInt)
  {
    Settings.System.putInt(getContentResolver(), this.mOneplusLabFeatureKey, paramInt);
  }
  
  public boolean isMultiToggle()
  {
    return this.mOneplusLabFeatureToggleCount > 2;
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default: 
      return;
    case 2131362341: 
      if (this.mSwitch.isChecked())
      {
        this.mSwitch.setChecked(false);
        if (!this.mSwitch.isChecked()) {
          break label108;
        }
      }
      for (int i = 1;; i = 0)
      {
        if (!"show_importance_slider".equals(this.mOneplusLabFeatureKey)) {
          break label113;
        }
        Settings.Secure.putInt(getContentResolver(), this.mOneplusLabFeatureKey, i);
        return;
        this.mSwitch.setChecked(true);
        break;
      }
      Settings.System.putInt(getContentResolver(), this.mOneplusLabFeatureKey, i);
      return;
    case 2131362348: 
      label108:
      label113:
      saveActitiveHistory(1);
      return;
    }
    saveActitiveHistory(-1);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968817);
    initIntent();
    initView();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    finish();
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\laboratory\OPLabFeatureDetailActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */