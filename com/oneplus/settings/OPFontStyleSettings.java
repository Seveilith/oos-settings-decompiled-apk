package com.oneplus.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class OPFontStyleSettings
  extends SettingsPreferenceFragment
  implements View.OnClickListener, Indexable
{
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      Object localObject = paramAnonymousContext.getResources();
      paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
      paramAnonymousContext.title = ((Resources)localObject).getString(2131690463);
      paramAnonymousContext.screenTitle = ((Resources)localObject).getString(2131690463);
      paramAnonymousContext.keywords = ((Resources)localObject).getString(2131690464);
      localObject = new ArrayList(1);
      ((List)localObject).add(paramAnonymousContext);
      return (List<SearchIndexableRaw>)localObject;
    }
  };
  private View mSlateFont;
  private RadioButton mSlateFontButton;
  private View mSystemFont;
  private RadioButton mSystemFontButton;
  
  private void setFontStyle(final int paramInt)
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          Thread.sleep(300L);
          Settings.System.putInt(OPFontStyleSettings.-wrap0(OPFontStyleSettings.this), "oem_font_mode", paramInt);
          Intent localIntent = new Intent("android.settings.OEM_FONT_MODE");
          localIntent.putExtra("oem_font_mode", paramInt);
          localIntent.addFlags(268435456);
          OPFontStyleSettings.-wrap1(OPFontStyleSettings.this).sendBroadcast(localIntent);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
          }
        }
      }
    }).start();
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131362328)
    {
      if (Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0) == 1) {
        return;
      }
      setFontStyle(1);
      this.mSystemFontButton.setChecked(true);
      this.mSlateFontButton.setChecked(false);
    }
    while (paramView.getId() != 2131362331) {
      return;
    }
    if (Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0) == 2) {
      return;
    }
    setFontStyle(2);
    this.mSlateFontButton.setChecked(true);
    this.mSystemFontButton.setChecked(false);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    paramBundle = (ViewGroup)paramViewGroup.findViewById(16908351);
    paramBundle.removeAllViews();
    paramLayoutInflater = paramLayoutInflater.inflate(2130968811, paramBundle, false);
    paramBundle.addView(paramLayoutInflater);
    this.mSystemFontButton = ((RadioButton)paramLayoutInflater.findViewById(2131362330));
    this.mSlateFontButton = ((RadioButton)paramLayoutInflater.findViewById(2131362333));
    this.mSystemFont = paramLayoutInflater.findViewById(2131362328);
    this.mSlateFont = paramLayoutInflater.findViewById(2131362331);
    this.mSystemFont.setOnClickListener(this);
    this.mSlateFont.setOnClickListener(this);
    return paramViewGroup;
  }
  
  public void onResume()
  {
    boolean bool2 = true;
    super.onResume();
    int i = Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0);
    RadioButton localRadioButton = this.mSystemFontButton;
    if (i == 1)
    {
      bool1 = true;
      localRadioButton.setChecked(bool1);
      localRadioButton = this.mSlateFontButton;
      if (i != 2) {
        break label62;
      }
    }
    label62:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localRadioButton.setChecked(bool1);
      return;
      bool1 = false;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPFontStyleSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */