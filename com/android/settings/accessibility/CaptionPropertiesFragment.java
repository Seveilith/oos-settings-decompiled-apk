package com.android.settings.accessibility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.provider.Settings.Secure;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import com.android.internal.widget.SubtitleView;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.Locale;

public class CaptionPropertiesFragment
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, ListDialogPreference.OnValueChangedListener
{
  private static final float LINE_HEIGHT_RATIO = 0.0533F;
  private static final String PREF_BACKGROUND_COLOR = "captioning_background_color";
  private static final String PREF_BACKGROUND_OPACITY = "captioning_background_opacity";
  private static final String PREF_CUSTOM = "custom";
  private static final String PREF_EDGE_COLOR = "captioning_edge_color";
  private static final String PREF_EDGE_TYPE = "captioning_edge_type";
  private static final String PREF_FONT_SIZE = "captioning_font_size";
  private static final String PREF_FOREGROUND_COLOR = "captioning_foreground_color";
  private static final String PREF_FOREGROUND_OPACITY = "captioning_foreground_opacity";
  private static final String PREF_LOCALE = "captioning_locale";
  private static final String PREF_PRESET = "captioning_preset";
  private static final String PREF_TYPEFACE = "captioning_typeface";
  private static final String PREF_WINDOW_COLOR = "captioning_window_color";
  private static final String PREF_WINDOW_OPACITY = "captioning_window_opacity";
  private ColorPreference mBackgroundColor;
  private ColorPreference mBackgroundOpacity;
  private CaptioningManager mCaptioningManager;
  private PreferenceCategory mCustom;
  private ColorPreference mEdgeColor;
  private EdgeTypePreference mEdgeType;
  private ListPreference mFontSize;
  private ColorPreference mForegroundColor;
  private ColorPreference mForegroundOpacity;
  private LocalePreference mLocale;
  private PresetPreference mPreset;
  private SubtitleView mPreviewText;
  private View mPreviewViewport;
  private View mPreviewWindow;
  private boolean mShowingCustom;
  private SwitchBar mSwitchBar;
  private ToggleSwitch mToggleSwitch;
  private ListPreference mTypeface;
  private ColorPreference mWindowColor;
  private ColorPreference mWindowOpacity;
  
  public static void applyCaptionProperties(CaptioningManager paramCaptioningManager, SubtitleView paramSubtitleView, View paramView, int paramInt)
  {
    paramSubtitleView.setStyle(paramInt);
    Context localContext = paramSubtitleView.getContext();
    localContext.getContentResolver();
    float f = paramCaptioningManager.getFontScale();
    if (paramView != null) {
      paramSubtitleView.setTextSize(0.0533F * (Math.max(paramView.getWidth() * 9, paramView.getHeight() * 16) / 16.0F) * f);
    }
    for (;;)
    {
      paramCaptioningManager = paramCaptioningManager.getLocale();
      if (paramCaptioningManager == null) {
        break;
      }
      paramSubtitleView.setText(AccessibilityUtils.getTextForLocale(localContext, paramCaptioningManager, 2131692371));
      return;
      paramSubtitleView.setTextSize(localContext.getResources().getDimension(2131755501) * f);
    }
    paramSubtitleView.setText(2131692371);
  }
  
  private void initializeAllPreferences()
  {
    this.mLocale = ((LocalePreference)findPreference("captioning_locale"));
    this.mFontSize = ((ListPreference)findPreference("captioning_font_size"));
    Object localObject = getResources();
    int[] arrayOfInt1 = ((Resources)localObject).getIntArray(2131427431);
    String[] arrayOfString1 = ((Resources)localObject).getStringArray(2131427430);
    this.mPreset = ((PresetPreference)findPreference("captioning_preset"));
    this.mPreset.setValues(arrayOfInt1);
    this.mPreset.setTitles(arrayOfString1);
    this.mCustom = ((PreferenceCategory)findPreference("custom"));
    this.mShowingCustom = true;
    arrayOfInt1 = ((Resources)localObject).getIntArray(2131427427);
    arrayOfString1 = ((Resources)localObject).getStringArray(2131427426);
    this.mForegroundColor = ((ColorPreference)this.mCustom.findPreference("captioning_foreground_color"));
    this.mForegroundColor.setTitles(arrayOfString1);
    this.mForegroundColor.setValues(arrayOfInt1);
    int[] arrayOfInt2 = ((Resources)localObject).getIntArray(2131427429);
    localObject = ((Resources)localObject).getStringArray(2131427428);
    this.mForegroundOpacity = ((ColorPreference)this.mCustom.findPreference("captioning_foreground_opacity"));
    this.mForegroundOpacity.setTitles((CharSequence[])localObject);
    this.mForegroundOpacity.setValues(arrayOfInt2);
    this.mEdgeColor = ((ColorPreference)this.mCustom.findPreference("captioning_edge_color"));
    this.mEdgeColor.setTitles(arrayOfString1);
    this.mEdgeColor.setValues(arrayOfInt1);
    int[] arrayOfInt3 = new int[arrayOfInt1.length + 1];
    String[] arrayOfString2 = new String[arrayOfString1.length + 1];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt3, 1, arrayOfInt1.length);
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, arrayOfString1.length);
    arrayOfInt3[0] = 0;
    arrayOfString2[0] = getString(2131692375);
    this.mBackgroundColor = ((ColorPreference)this.mCustom.findPreference("captioning_background_color"));
    this.mBackgroundColor.setTitles(arrayOfString2);
    this.mBackgroundColor.setValues(arrayOfInt3);
    this.mBackgroundOpacity = ((ColorPreference)this.mCustom.findPreference("captioning_background_opacity"));
    this.mBackgroundOpacity.setTitles((CharSequence[])localObject);
    this.mBackgroundOpacity.setValues(arrayOfInt2);
    this.mWindowColor = ((ColorPreference)this.mCustom.findPreference("captioning_window_color"));
    this.mWindowColor.setTitles(arrayOfString2);
    this.mWindowColor.setValues(arrayOfInt3);
    this.mWindowOpacity = ((ColorPreference)this.mCustom.findPreference("captioning_window_opacity"));
    this.mWindowOpacity.setTitles((CharSequence[])localObject);
    this.mWindowOpacity.setValues(arrayOfInt2);
    this.mEdgeType = ((EdgeTypePreference)this.mCustom.findPreference("captioning_edge_type"));
    this.mTypeface = ((ListPreference)this.mCustom.findPreference("captioning_typeface"));
  }
  
  private void installSwitchBarToggleSwitch()
  {
    onInstallSwitchBarToggleSwitch();
    this.mSwitchBar.show();
  }
  
  private void installUpdateListeners()
  {
    this.mPreset.setOnValueChangedListener(this);
    this.mForegroundColor.setOnValueChangedListener(this);
    this.mForegroundOpacity.setOnValueChangedListener(this);
    this.mEdgeColor.setOnValueChangedListener(this);
    this.mBackgroundColor.setOnValueChangedListener(this);
    this.mBackgroundOpacity.setOnValueChangedListener(this);
    this.mWindowColor.setOnValueChangedListener(this);
    this.mWindowOpacity.setOnValueChangedListener(this);
    this.mEdgeType.setOnValueChangedListener(this);
    this.mTypeface.setOnPreferenceChangeListener(this);
    this.mFontSize.setOnPreferenceChangeListener(this);
    this.mLocale.setOnPreferenceChangeListener(this);
  }
  
  private int mergeColorOpacity(ColorPreference paramColorPreference1, ColorPreference paramColorPreference2)
  {
    int i = paramColorPreference1.getValue();
    int j = paramColorPreference2.getValue();
    if (!CaptioningManager.CaptionStyle.hasColor(i)) {
      return 0xFFFF00 | Color.alpha(j);
    }
    if (i == 0) {
      return Color.alpha(j);
    }
    return 0xFFFFFF & i | 0xFF000000 & j;
  }
  
  private void parseColorOpacity(ColorPreference paramColorPreference1, ColorPreference paramColorPreference2, int paramInt)
  {
    int j;
    int i;
    if (!CaptioningManager.CaptionStyle.hasColor(paramInt))
    {
      j = 16777215;
      i = (paramInt & 0xFF) << 24;
      paramInt = j;
    }
    for (;;)
    {
      paramColorPreference2.setValue(0xFFFFFF | i);
      paramColorPreference1.setValue(paramInt);
      return;
      if (paramInt >>> 24 == 0)
      {
        i = 0;
        j = (paramInt & 0xFF) << 24;
        paramInt = i;
        i = j;
      }
      else
      {
        i = paramInt | 0xFF000000;
        j = paramInt & 0xFF000000;
        paramInt = i;
        i = j;
      }
    }
  }
  
  private void refreshPreviewText()
  {
    Object localObject = getActivity();
    if (localObject == null) {
      return;
    }
    SubtitleView localSubtitleView = this.mPreviewText;
    if (localSubtitleView != null)
    {
      int i = this.mCaptioningManager.getRawUserStyle();
      applyCaptionProperties(this.mCaptioningManager, localSubtitleView, this.mPreviewViewport, i);
      Locale localLocale = this.mCaptioningManager.getLocale();
      if (localLocale == null) {
        break label94;
      }
      localSubtitleView.setText(AccessibilityUtils.getTextForLocale((Context)localObject, localLocale, 2131692370));
    }
    for (;;)
    {
      localObject = this.mCaptioningManager.getUserStyle();
      if (!((CaptioningManager.CaptionStyle)localObject).hasWindowColor()) {
        break;
      }
      this.mPreviewWindow.setBackgroundColor(((CaptioningManager.CaptionStyle)localObject).windowColor);
      return;
      label94:
      localSubtitleView.setText(2131692370);
    }
    localObject = CaptioningManager.CaptionStyle.DEFAULT;
    this.mPreviewWindow.setBackgroundColor(((CaptioningManager.CaptionStyle)localObject).windowColor);
  }
  
  private void refreshShowingCustom()
  {
    int i;
    if (this.mPreset.getValue() == -1)
    {
      i = 1;
      if ((i != 0) || (!this.mShowingCustom)) {
        break label47;
      }
      getPreferenceScreen().removePreference(this.mCustom);
      this.mShowingCustom = false;
    }
    label47:
    while ((i == 0) || (this.mShowingCustom))
    {
      return;
      i = 0;
      break;
    }
    getPreferenceScreen().addPreference(this.mCustom);
    this.mShowingCustom = true;
  }
  
  private void removeSwitchBarToggleSwitch()
  {
    this.mSwitchBar.hide();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
  }
  
  private void updateAllPreferences()
  {
    int i = this.mCaptioningManager.getRawUserStyle();
    this.mPreset.setValue(i);
    float f = this.mCaptioningManager.getFontScale();
    this.mFontSize.setValue(Float.toString(f));
    Object localObject1 = CaptioningManager.CaptionStyle.getCustomStyle(getContentResolver());
    this.mEdgeType.setValue(((CaptioningManager.CaptionStyle)localObject1).edgeType);
    this.mEdgeColor.setValue(((CaptioningManager.CaptionStyle)localObject1).edgeColor);
    if (((CaptioningManager.CaptionStyle)localObject1).hasForegroundColor())
    {
      i = ((CaptioningManager.CaptionStyle)localObject1).foregroundColor;
      parseColorOpacity(this.mForegroundColor, this.mForegroundOpacity, i);
      if (!((CaptioningManager.CaptionStyle)localObject1).hasBackgroundColor()) {
        break label211;
      }
      i = ((CaptioningManager.CaptionStyle)localObject1).backgroundColor;
      label102:
      parseColorOpacity(this.mBackgroundColor, this.mBackgroundOpacity, i);
      if (!((CaptioningManager.CaptionStyle)localObject1).hasWindowColor()) {
        break label218;
      }
    }
    label211:
    label218:
    for (i = ((CaptioningManager.CaptionStyle)localObject1).windowColor;; i = 16777215)
    {
      parseColorOpacity(this.mWindowColor, this.mWindowOpacity, i);
      String str = ((CaptioningManager.CaptionStyle)localObject1).mRawTypeface;
      Object localObject2 = this.mTypeface;
      localObject1 = str;
      if (str == null) {
        localObject1 = "";
      }
      ((ListPreference)localObject2).setValue((String)localObject1);
      str = this.mCaptioningManager.getRawLocale();
      localObject2 = this.mLocale;
      localObject1 = str;
      if (str == null) {
        localObject1 = "";
      }
      ((LocalePreference)localObject2).setValue((String)localObject1);
      return;
      i = 16777215;
      break;
      i = 16777215;
      break label102;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 3;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    boolean bool = this.mCaptioningManager.isEnabled();
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.setCheckedInternal(bool);
    this.mToggleSwitch = this.mSwitchBar.getSwitch();
    getPreferenceScreen().setEnabled(bool);
    refreshPreviewText();
    installSwitchBarToggleSwitch();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mCaptioningManager = ((CaptioningManager)getSystemService("captioning"));
    addPreferencesFromResource(2131230745);
    initializeAllPreferences();
    updateAllPreferences();
    refreshShowingCustom();
    installUpdateListeners();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968636, paramViewGroup, false);
    if ((paramViewGroup instanceof PreferenceFrameLayout)) {
      ((PreferenceFrameLayout.LayoutParams)localView.getLayoutParams()).removeBorders = true;
    }
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    ((ViewGroup)localView.findViewById(2131362009)).addView(paramLayoutInflater, -1, -1);
    return localView;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    removeSwitchBarToggleSwitch();
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        CaptionPropertiesFragment.-get1(CaptionPropertiesFragment.this).setCheckedInternal(paramAnonymousBoolean);
        paramAnonymousToggleSwitch = CaptionPropertiesFragment.this.getActivity().getContentResolver();
        if (paramAnonymousBoolean)
        {
          i = 1;
          Settings.Secure.putInt(paramAnonymousToggleSwitch, "accessibility_captioning_enabled", i);
          CaptionPropertiesFragment.this.getPreferenceScreen().setEnabled(paramAnonymousBoolean);
          if (CaptionPropertiesFragment.-get0(CaptionPropertiesFragment.this) != null)
          {
            paramAnonymousToggleSwitch = CaptionPropertiesFragment.-get0(CaptionPropertiesFragment.this);
            if (!paramAnonymousBoolean) {
              break label83;
            }
          }
        }
        label83:
        for (int i = 0;; i = 4)
        {
          paramAnonymousToggleSwitch.setVisibility(i);
          return false;
          i = 0;
          break;
        }
      }
    });
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mTypeface == paramPreference) {
      Settings.Secure.putString(localContentResolver, "accessibility_captioning_typeface", (String)paramObject);
    }
    for (;;)
    {
      refreshPreviewText();
      return true;
      if (this.mFontSize == paramPreference) {
        Settings.Secure.putFloat(localContentResolver, "accessibility_captioning_font_scale", Float.parseFloat((String)paramObject));
      } else if (this.mLocale == paramPreference) {
        Settings.Secure.putString(localContentResolver, "accessibility_captioning_locale", (String)paramObject);
      }
    }
  }
  
  public void onValueChanged(ListDialogPreference paramListDialogPreference, int paramInt)
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if ((this.mForegroundColor == paramListDialogPreference) || (this.mForegroundOpacity == paramListDialogPreference)) {
      Settings.Secure.putInt(localContentResolver, "accessibility_captioning_foreground_color", mergeColorOpacity(this.mForegroundColor, this.mForegroundOpacity));
    }
    for (;;)
    {
      refreshPreviewText();
      return;
      if ((this.mBackgroundColor == paramListDialogPreference) || (this.mBackgroundOpacity == paramListDialogPreference))
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_background_color", mergeColorOpacity(this.mBackgroundColor, this.mBackgroundOpacity));
      }
      else if ((this.mWindowColor == paramListDialogPreference) || (this.mWindowOpacity == paramListDialogPreference))
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_window_color", mergeColorOpacity(this.mWindowColor, this.mWindowOpacity));
      }
      else if (this.mEdgeColor == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_edge_color", paramInt);
      }
      else if (this.mPreset == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_preset", paramInt);
        refreshShowingCustom();
      }
      else if (this.mEdgeType == paramListDialogPreference)
      {
        Settings.Secure.putInt(localContentResolver, "accessibility_captioning_edge_type", paramInt);
      }
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    boolean bool = this.mCaptioningManager.isEnabled();
    this.mPreviewText = ((SubtitleView)paramView.findViewById(2131362008));
    paramBundle = this.mPreviewText;
    if (bool) {}
    for (int i = 0;; i = 4)
    {
      paramBundle.setVisibility(i);
      this.mPreviewWindow = paramView.findViewById(2131362007);
      this.mPreviewViewport = paramView.findViewById(2131362006);
      this.mPreviewViewport.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          CaptionPropertiesFragment.-wrap0(CaptionPropertiesFragment.this);
        }
      });
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\CaptionPropertiesFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */