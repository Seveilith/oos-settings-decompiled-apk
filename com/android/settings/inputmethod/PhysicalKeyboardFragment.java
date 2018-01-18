package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils.InputMethodSettings;
import com.android.internal.util.Preconditions;
import com.android.settings.Settings.KeyboardLayoutPickerActivity;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class PhysicalKeyboardFragment
  extends SettingsPreferenceFragment
  implements InputManager.InputDeviceListener
{
  private static final String IM_SUBTYPE_MODE_KEYBOARD = "keyboard";
  private static final String KEYBOARD_ASSISTANCE_CATEGORY = "keyboard_assistance_category";
  private static final String KEYBOARD_SHORTCUTS_HELPER = "keyboard_shortcuts_helper";
  private static final String SHOW_VIRTUAL_KEYBOARD_SWITCH = "show_virtual_keyboard_switch";
  private final ContentObserver mContentObserver = new ContentObserver(new Handler(true))
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      PhysicalKeyboardFragment.-wrap1(PhysicalKeyboardFragment.this);
    }
  };
  private InputManager mIm;
  private PreferenceCategory mKeyboardAssistanceCategory;
  private final List<HardKeyboardDeviceInfo> mLastHardKeyboards = new ArrayList();
  private final HashSet<Integer> mLoaderIDs = new HashSet();
  private int mNextLoaderId = 0;
  private InputMethodUtils.InputMethodSettings mSettings;
  private SwitchPreference mShowVirtualKeyboardSwitch;
  private final Preference.OnPreferenceChangeListener mShowVirtualKeyboardSwitchPreferenceChangeListener = new Preference.OnPreferenceChangeListener()
  {
    public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
    {
      PhysicalKeyboardFragment.-get0(PhysicalKeyboardFragment.this).setShowImeWithHardKeyboard(((Boolean)paramAnonymousObject).booleanValue());
      return true;
    }
  };
  private final List<KeyboardInfoPreference> mTempKeyboardInfoList = new ArrayList();
  
  private void clearLoader()
  {
    Iterator localIterator = this.mLoaderIDs.iterator();
    while (localIterator.hasNext())
    {
      int i = ((Integer)localIterator.next()).intValue();
      getLoaderManager().destroyLoader(i);
    }
    this.mLoaderIDs.clear();
  }
  
  private static ArrayList<HardKeyboardDeviceInfo> getHardKeyboards()
  {
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt = InputDevice.getDeviceIds();
    int i = 0;
    int j = arrayOfInt.length;
    if (i < j)
    {
      InputDevice localInputDevice = InputDevice.getDevice(arrayOfInt[i]);
      if ((localInputDevice == null) || (localInputDevice.isVirtual())) {}
      for (;;)
      {
        i += 1;
        break;
        if (localInputDevice.isFullKeyboard()) {
          localArrayList.add(new HardKeyboardDeviceInfo(localInputDevice.getName(), localInputDevice.getIdentifier()));
        }
      }
    }
    return localArrayList;
  }
  
  private void registerShowVirtualKeyboardSettingsObserver()
  {
    unregisterShowVirtualKeyboardSettingsObserver();
    getActivity().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("show_ime_with_hard_keyboard"), false, this.mContentObserver, UserHandle.myUserId());
    updateShowVirtualKeyboardSwitch();
  }
  
  private void showKeyboardLayoutScreen(InputDeviceIdentifier paramInputDeviceIdentifier, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(getActivity(), Settings.KeyboardLayoutPickerActivity.class);
    localIntent.putExtra("input_device_identifier", paramInputDeviceIdentifier);
    localIntent.putExtra("input_method_info", paramInputMethodInfo);
    localIntent.putExtra("input_method_subtype", paramInputMethodSubtype);
    startActivity(localIntent);
  }
  
  private void toggleKeyboardShortcutsMenu()
  {
    getActivity().requestShowKeyboardShortcuts();
  }
  
  private void unregisterShowVirtualKeyboardSettingsObserver()
  {
    getActivity().getContentResolver().unregisterContentObserver(this.mContentObserver);
  }
  
  private void updateHardKeyboards()
  {
    ArrayList localArrayList = getHardKeyboards();
    if (!Objects.equals(localArrayList, this.mLastHardKeyboards))
    {
      clearLoader();
      this.mLastHardKeyboards.clear();
      this.mLastHardKeyboards.addAll(localArrayList);
      this.mLoaderIDs.add(Integer.valueOf(this.mNextLoaderId));
      getLoaderManager().initLoader(this.mNextLoaderId, null, new Callbacks(getContext(), this, this.mLastHardKeyboards));
      this.mNextLoaderId += 1;
    }
  }
  
  private void updateShowVirtualKeyboardSwitch()
  {
    this.mShowVirtualKeyboardSwitch.setChecked(this.mSettings.isShowImeWithHardKeyboardEnabled());
  }
  
  protected int getMetricsCategory()
  {
    return 346;
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = (Activity)Preconditions.checkNotNull(getActivity());
    addPreferencesFromResource(2131230825);
    this.mIm = ((InputManager)Preconditions.checkNotNull((InputManager)paramBundle.getSystemService(InputManager.class)));
    this.mSettings = new InputMethodUtils.InputMethodSettings(paramBundle.getResources(), getContentResolver(), new HashMap(), new ArrayList(), UserHandle.myUserId(), false);
    this.mKeyboardAssistanceCategory = ((PreferenceCategory)Preconditions.checkNotNull((PreferenceCategory)findPreference("keyboard_assistance_category")));
    this.mShowVirtualKeyboardSwitch = ((SwitchPreference)Preconditions.checkNotNull((SwitchPreference)this.mKeyboardAssistanceCategory.findPreference("show_virtual_keyboard_switch")));
    findPreference("keyboard_shortcuts_helper").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        PhysicalKeyboardFragment.-wrap0(PhysicalKeyboardFragment.this);
        return true;
      }
    });
  }
  
  public void onInputDeviceAdded(int paramInt)
  {
    updateHardKeyboards();
  }
  
  public void onInputDeviceChanged(int paramInt)
  {
    updateHardKeyboards();
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    updateHardKeyboards();
  }
  
  public void onLoadFinishedInternal(int paramInt, List<Keyboards> paramList)
  {
    if (!this.mLoaderIDs.remove(Integer.valueOf(paramInt))) {
      return;
    }
    Collections.sort(paramList);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    paramList = paramList.iterator();
    if (paramList.hasNext())
    {
      Keyboards localKeyboards = (Keyboards)paramList.next();
      PreferenceCategory localPreferenceCategory = new PreferenceCategory(getPrefContext(), null);
      localPreferenceCategory.setTitle(localKeyboards.mDeviceInfo.mDeviceName);
      localPreferenceCategory.setOrder(0);
      localPreferenceScreen.addPreference(localPreferenceCategory);
      Iterator localIterator = localKeyboards.mKeyboardInfoList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = (PhysicalKeyboardFragment.Keyboards.KeyboardInfo)localIterator.next();
        this.mTempKeyboardInfoList.clear();
        Object localObject1 = ((PhysicalKeyboardFragment.Keyboards.KeyboardInfo)localObject2).mImi;
        InputMethodSubtype localInputMethodSubtype = ((PhysicalKeyboardFragment.Keyboards.KeyboardInfo)localObject2).mImSubtype;
        if (localObject1 != null)
        {
          localObject2 = new KeyboardInfoPreference(getPrefContext(), (PhysicalKeyboardFragment.Keyboards.KeyboardInfo)localObject2, null);
          ((KeyboardInfoPreference)localObject2).setOnPreferenceClickListener(new -void_onLoadFinishedInternal_int_loaderId_java_util_List_keyboardsList_LambdaImpl0(localKeyboards, (InputMethodInfo)localObject1, localInputMethodSubtype));
          this.mTempKeyboardInfoList.add(localObject2);
          Collections.sort(this.mTempKeyboardInfoList);
        }
        localObject1 = this.mTempKeyboardInfoList.iterator();
        while (((Iterator)localObject1).hasNext()) {
          localPreferenceCategory.addPreference((KeyboardInfoPreference)((Iterator)localObject1).next());
        }
      }
    }
    this.mTempKeyboardInfoList.clear();
    this.mKeyboardAssistanceCategory.setOrder(1);
    localPreferenceScreen.addPreference(this.mKeyboardAssistanceCategory);
    updateShowVirtualKeyboardSwitch();
  }
  
  public void onPause()
  {
    super.onPause();
    clearLoader();
    this.mLastHardKeyboards.clear();
    this.mIm.unregisterInputDeviceListener(this);
    this.mShowVirtualKeyboardSwitch.setOnPreferenceChangeListener(null);
    unregisterShowVirtualKeyboardSettingsObserver();
  }
  
  public void onResume()
  {
    super.onResume();
    clearLoader();
    this.mLastHardKeyboards.clear();
    updateHardKeyboards();
    this.mIm.registerInputDeviceListener(this, null);
    this.mShowVirtualKeyboardSwitch.setOnPreferenceChangeListener(this.mShowVirtualKeyboardSwitchPreferenceChangeListener);
    registerShowVirtualKeyboardSettingsObserver();
  }
  
  private static final class Callbacks
    implements LoaderManager.LoaderCallbacks<List<PhysicalKeyboardFragment.Keyboards>>
  {
    final Context mContext;
    final List<PhysicalKeyboardFragment.HardKeyboardDeviceInfo> mHardKeyboards;
    final PhysicalKeyboardFragment mPhysicalKeyboardFragment;
    
    public Callbacks(Context paramContext, PhysicalKeyboardFragment paramPhysicalKeyboardFragment, List<PhysicalKeyboardFragment.HardKeyboardDeviceInfo> paramList)
    {
      this.mContext = paramContext;
      this.mPhysicalKeyboardFragment = paramPhysicalKeyboardFragment;
      this.mHardKeyboards = paramList;
    }
    
    public Loader<List<PhysicalKeyboardFragment.Keyboards>> onCreateLoader(int paramInt, Bundle paramBundle)
    {
      return new PhysicalKeyboardFragment.KeyboardLayoutLoader(this.mContext, this.mHardKeyboards);
    }
    
    public void onLoadFinished(Loader<List<PhysicalKeyboardFragment.Keyboards>> paramLoader, List<PhysicalKeyboardFragment.Keyboards> paramList)
    {
      this.mPhysicalKeyboardFragment.onLoadFinishedInternal(paramLoader.getId(), paramList);
    }
    
    public void onLoaderReset(Loader<List<PhysicalKeyboardFragment.Keyboards>> paramLoader) {}
  }
  
  public static final class HardKeyboardDeviceInfo
  {
    public final InputDeviceIdentifier mDeviceIdentifier;
    public final String mDeviceName;
    
    public HardKeyboardDeviceInfo(String paramString, InputDeviceIdentifier paramInputDeviceIdentifier)
    {
      if (paramString != null) {}
      for (;;)
      {
        this.mDeviceName = paramString;
        this.mDeviceIdentifier = paramInputDeviceIdentifier;
        return;
        paramString = "";
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (!(paramObject instanceof HardKeyboardDeviceInfo)) {
        return false;
      }
      paramObject = (HardKeyboardDeviceInfo)paramObject;
      if (!TextUtils.equals(this.mDeviceName, ((HardKeyboardDeviceInfo)paramObject).mDeviceName)) {
        return false;
      }
      if (this.mDeviceIdentifier.getVendorId() != ((HardKeyboardDeviceInfo)paramObject).mDeviceIdentifier.getVendorId()) {
        return false;
      }
      if (this.mDeviceIdentifier.getProductId() != ((HardKeyboardDeviceInfo)paramObject).mDeviceIdentifier.getProductId()) {
        return false;
      }
      return TextUtils.equals(this.mDeviceIdentifier.getDescriptor(), ((HardKeyboardDeviceInfo)paramObject).mDeviceIdentifier.getDescriptor());
    }
  }
  
  static final class KeyboardInfoPreference
    extends Preference
  {
    private final Collator collator = Collator.getInstance();
    private final CharSequence mImSubtypeName;
    private final CharSequence mImeName;
    
    private KeyboardInfoPreference(Context paramContext, PhysicalKeyboardFragment.Keyboards.KeyboardInfo paramKeyboardInfo)
    {
      super();
      this.mImeName = paramKeyboardInfo.mImi.loadLabel(paramContext.getPackageManager());
      this.mImSubtypeName = getImSubtypeName(paramContext, paramKeyboardInfo.mImi, paramKeyboardInfo.mImSubtype);
      setTitle(formatDisplayName(paramContext, this.mImeName, this.mImSubtypeName));
      if (paramKeyboardInfo.mLayout != null) {
        setSummary(paramKeyboardInfo.mLayout.getLabel());
      }
    }
    
    private int compare(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    {
      if ((TextUtils.isEmpty(paramCharSequence1)) || (TextUtils.isEmpty(paramCharSequence2)))
      {
        if ((TextUtils.isEmpty(paramCharSequence1)) && (TextUtils.isEmpty(paramCharSequence2))) {
          return 0;
        }
      }
      else {
        return this.collator.compare(paramCharSequence1.toString(), paramCharSequence2.toString());
      }
      if (!TextUtils.isEmpty(paramCharSequence1)) {
        return -1;
      }
      return 1;
    }
    
    private static CharSequence formatDisplayName(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    {
      if (paramCharSequence2 == null) {
        return paramCharSequence1;
      }
      return String.format(paramContext.getString(2131692251), new Object[] { paramCharSequence1, paramCharSequence2 });
    }
    
    static CharSequence getDisplayName(Context paramContext, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
    {
      return formatDisplayName(paramContext, paramInputMethodInfo.loadLabel(paramContext.getPackageManager()), getImSubtypeName(paramContext, paramInputMethodInfo, paramInputMethodSubtype));
    }
    
    private static CharSequence getImSubtypeName(Context paramContext, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype)
    {
      if (paramInputMethodSubtype != null) {
        return InputMethodAndSubtypeUtil.getSubtypeLocaleNameAsSentence(paramInputMethodSubtype, paramContext, paramInputMethodInfo);
      }
      return null;
    }
    
    public int compareTo(Preference paramPreference)
    {
      if (!(paramPreference instanceof KeyboardInfoPreference)) {
        return super.compareTo(paramPreference);
      }
      paramPreference = (KeyboardInfoPreference)paramPreference;
      int j = compare(this.mImeName, paramPreference.mImeName);
      int i = j;
      if (j == 0) {
        i = compare(this.mImSubtypeName, paramPreference.mImSubtypeName);
      }
      return i;
    }
  }
  
  private static final class KeyboardLayoutLoader
    extends AsyncTaskLoader<List<PhysicalKeyboardFragment.Keyboards>>
  {
    private final List<PhysicalKeyboardFragment.HardKeyboardDeviceInfo> mHardKeyboards;
    
    public KeyboardLayoutLoader(Context paramContext, List<PhysicalKeyboardFragment.HardKeyboardDeviceInfo> paramList)
    {
      super();
      this.mHardKeyboards = ((List)Preconditions.checkNotNull(paramList));
    }
    
    private PhysicalKeyboardFragment.Keyboards loadInBackground(PhysicalKeyboardFragment.HardKeyboardDeviceInfo paramHardKeyboardDeviceInfo)
    {
      ArrayList localArrayList = new ArrayList();
      InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
      InputManager localInputManager = (InputManager)getContext().getSystemService(InputManager.class);
      if ((localInputMethodManager != null) && (localInputManager != null))
      {
        Iterator localIterator = localInputMethodManager.getEnabledInputMethodList().iterator();
        while (localIterator.hasNext())
        {
          InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
          List localList = localInputMethodManager.getEnabledInputMethodSubtypeList(localInputMethodInfo, true);
          if (localList.isEmpty())
          {
            localArrayList.add(new PhysicalKeyboardFragment.Keyboards.KeyboardInfo(localInputMethodInfo, null, localInputManager.getKeyboardLayoutForInputDevice(paramHardKeyboardDeviceInfo.mDeviceIdentifier, localInputMethodInfo, null)));
          }
          else
          {
            int j = localList.size();
            int i = 0;
            label142:
            InputMethodSubtype localInputMethodSubtype;
            if (i < j)
            {
              localInputMethodSubtype = (InputMethodSubtype)localList.get(i);
              if ("keyboard".equalsIgnoreCase(localInputMethodSubtype.getMode())) {
                break label180;
              }
            }
            for (;;)
            {
              i += 1;
              break label142;
              break;
              label180:
              localArrayList.add(new PhysicalKeyboardFragment.Keyboards.KeyboardInfo(localInputMethodInfo, localInputMethodSubtype, localInputManager.getKeyboardLayoutForInputDevice(paramHardKeyboardDeviceInfo.mDeviceIdentifier, localInputMethodInfo, localInputMethodSubtype)));
            }
          }
        }
      }
      return new PhysicalKeyboardFragment.Keyboards(paramHardKeyboardDeviceInfo, localArrayList);
    }
    
    public List<PhysicalKeyboardFragment.Keyboards> loadInBackground()
    {
      ArrayList localArrayList = new ArrayList(this.mHardKeyboards.size());
      Iterator localIterator = this.mHardKeyboards.iterator();
      while (localIterator.hasNext()) {
        localArrayList.add(loadInBackground((PhysicalKeyboardFragment.HardKeyboardDeviceInfo)localIterator.next()));
      }
      return localArrayList;
    }
    
    protected void onStartLoading()
    {
      super.onStartLoading();
      forceLoad();
    }
    
    protected void onStopLoading()
    {
      super.onStopLoading();
      cancelLoad();
    }
  }
  
  public static final class Keyboards
    implements Comparable<Keyboards>
  {
    public final Collator mCollator = Collator.getInstance();
    public final PhysicalKeyboardFragment.HardKeyboardDeviceInfo mDeviceInfo;
    public final ArrayList<KeyboardInfo> mKeyboardInfoList;
    
    public Keyboards(PhysicalKeyboardFragment.HardKeyboardDeviceInfo paramHardKeyboardDeviceInfo, ArrayList<KeyboardInfo> paramArrayList)
    {
      this.mDeviceInfo = paramHardKeyboardDeviceInfo;
      this.mKeyboardInfoList = paramArrayList;
    }
    
    public int compareTo(Keyboards paramKeyboards)
    {
      return this.mCollator.compare(this.mDeviceInfo.mDeviceName, paramKeyboards.mDeviceInfo.mDeviceName);
    }
    
    public static final class KeyboardInfo
    {
      public final InputMethodSubtype mImSubtype;
      public final InputMethodInfo mImi;
      public final KeyboardLayout mLayout;
      
      public KeyboardInfo(InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype, KeyboardLayout paramKeyboardLayout)
      {
        this.mImi = paramInputMethodInfo;
        this.mImSubtype = paramInputMethodSubtype;
        this.mLayout = paramKeyboardLayout;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\PhysicalKeyboardFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */