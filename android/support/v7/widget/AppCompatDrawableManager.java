package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.LruCache;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.color;
import android.support.v7.appcompat.R.drawable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public final class AppCompatDrawableManager
{
  private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY;
  private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED;
  private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL;
  private static final ColorFilterLruCache COLOR_FILTER_CACHE;
  private static final boolean DEBUG = false;
  private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
  private static AppCompatDrawableManager INSTANCE;
  private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
  private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
  private static final String TAG = "AppCompatDrawableManager";
  private static final int[] TINT_CHECKABLE_BUTTON_LIST = { R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material };
  private static final int[] TINT_COLOR_CONTROL_NORMAL;
  private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
  private ArrayMap<String, InflateDelegate> mDelegates;
  private final Object mDrawableCacheLock = new Object();
  private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable.ConstantState>>> mDrawableCaches = new WeakHashMap(0);
  private boolean mHasCheckedVectorDrawableSetup;
  private SparseArray<String> mKnownDrawableIdTags;
  private WeakHashMap<Context, SparseArray<ColorStateList>> mTintLists;
  private TypedValue mTypedValue;
  
  static
  {
    COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    COLORFILTER_TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha };
    TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_seekbar_tick_mark_material, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha };
    COLORFILTER_COLOR_CONTROL_ACTIVATED = new int[] { R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material, R.drawable.abc_text_select_handle_left_mtrl_dark, R.drawable.abc_text_select_handle_middle_mtrl_dark, R.drawable.abc_text_select_handle_right_mtrl_dark, R.drawable.abc_text_select_handle_left_mtrl_light, R.drawable.abc_text_select_handle_middle_mtrl_light, R.drawable.abc_text_select_handle_right_mtrl_light };
    COLORFILTER_COLOR_BACKGROUND_MULTIPLY = new int[] { R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult };
    TINT_COLOR_CONTROL_STATE_LIST = new int[] { R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material };
  }
  
  private void addDelegate(@NonNull String paramString, @NonNull InflateDelegate paramInflateDelegate)
  {
    if (this.mDelegates == null) {
      this.mDelegates = new ArrayMap();
    }
    this.mDelegates.put(paramString, paramInflateDelegate);
  }
  
  private boolean addDrawableToCache(@NonNull Context paramContext, long paramLong, @NonNull Drawable paramDrawable)
  {
    Drawable.ConstantState localConstantState = paramDrawable.getConstantState();
    if (localConstantState != null) {
      synchronized (this.mDrawableCacheLock)
      {
        LongSparseArray localLongSparseArray = (LongSparseArray)this.mDrawableCaches.get(paramContext);
        paramDrawable = localLongSparseArray;
        if (localLongSparseArray == null)
        {
          paramDrawable = new LongSparseArray();
          this.mDrawableCaches.put(paramContext, paramDrawable);
        }
        paramDrawable.put(paramLong, new WeakReference(localConstantState));
        return true;
      }
    }
    return false;
  }
  
  private void addTintListToCache(@NonNull Context paramContext, @DrawableRes int paramInt, @NonNull ColorStateList paramColorStateList)
  {
    if (this.mTintLists == null) {
      this.mTintLists = new WeakHashMap();
    }
    SparseArray localSparseArray2 = (SparseArray)this.mTintLists.get(paramContext);
    SparseArray localSparseArray1 = localSparseArray2;
    if (localSparseArray2 == null)
    {
      localSparseArray1 = new SparseArray();
      this.mTintLists.put(paramContext, localSparseArray1);
    }
    localSparseArray1.append(paramInt, paramColorStateList);
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void checkVectorDrawableSetup(@NonNull Context paramContext)
  {
    if (this.mHasCheckedVectorDrawableSetup) {
      return;
    }
    this.mHasCheckedVectorDrawableSetup = true;
    paramContext = getDrawable(paramContext, R.drawable.abc_vector_test);
    if ((paramContext != null) && (isVectorDrawable(paramContext))) {
      return;
    }
    this.mHasCheckedVectorDrawableSetup = false;
    throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
  }
  
  private ColorStateList createBorderlessButtonColorStateList(@NonNull Context paramContext, @Nullable ColorStateList paramColorStateList)
  {
    return createButtonColorStateList(paramContext, 0, null);
  }
  
  private ColorStateList createButtonColorStateList(@NonNull Context paramContext, @ColorInt int paramInt, @Nullable ColorStateList paramColorStateList)
  {
    int[][] arrayOfInt = new int[4][];
    int[] arrayOfInt1 = new int[4];
    int j = ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlHighlight);
    int i = ThemeUtils.getDisabledThemeAttrColor(paramContext, R.attr.colorButtonNormal);
    arrayOfInt[0] = ThemeUtils.DISABLED_STATE_SET;
    label60:
    int k;
    if (paramColorStateList == null)
    {
      arrayOfInt1[0] = i;
      arrayOfInt[1] = ThemeUtils.PRESSED_STATE_SET;
      if (paramColorStateList != null) {
        break label153;
      }
      i = paramInt;
      arrayOfInt1[1] = ColorUtils.compositeColors(j, i);
      k = 1 + 1;
      arrayOfInt[k] = ThemeUtils.FOCUSED_STATE_SET;
      if (paramColorStateList != null) {
        break label167;
      }
      i = paramInt;
      label91:
      arrayOfInt1[k] = ColorUtils.compositeColors(j, i);
      i = k + 1;
      arrayOfInt[i] = ThemeUtils.EMPTY_STATE_SET;
      if (paramColorStateList != null) {
        break label182;
      }
    }
    for (;;)
    {
      arrayOfInt1[i] = paramInt;
      return new ColorStateList(arrayOfInt, arrayOfInt1);
      i = paramColorStateList.getColorForState(arrayOfInt[0], 0);
      break;
      label153:
      i = paramColorStateList.getColorForState(arrayOfInt[1], 0);
      break label60;
      label167:
      i = paramColorStateList.getColorForState(arrayOfInt[k], 0);
      break label91;
      label182:
      paramInt = paramColorStateList.getColorForState(arrayOfInt[i], 0);
    }
  }
  
  private static long createCacheKey(TypedValue paramTypedValue)
  {
    return paramTypedValue.assetCookie << 32 | paramTypedValue.data;
  }
  
  private ColorStateList createColoredButtonColorStateList(@NonNull Context paramContext, @Nullable ColorStateList paramColorStateList)
  {
    return createButtonColorStateList(paramContext, ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorAccent), paramColorStateList);
  }
  
  private ColorStateList createDefaultButtonColorStateList(@NonNull Context paramContext, @Nullable ColorStateList paramColorStateList)
  {
    return createButtonColorStateList(paramContext, ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorButtonNormal), paramColorStateList);
  }
  
  private Drawable createDrawableIfNeeded(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    if (this.mTypedValue == null) {
      this.mTypedValue = new TypedValue();
    }
    TypedValue localTypedValue = this.mTypedValue;
    paramContext.getResources().getValue(paramInt, localTypedValue, true);
    long l = createCacheKey(localTypedValue);
    Object localObject = getCachedDrawable(paramContext, l);
    if (localObject != null) {
      return (Drawable)localObject;
    }
    if (paramInt == R.drawable.abc_cab_background_top_material) {
      localObject = new LayerDrawable(new Drawable[] { getDrawable(paramContext, R.drawable.abc_cab_background_internal_bg), getDrawable(paramContext, R.drawable.abc_cab_background_top_mtrl_alpha) });
    }
    if (localObject != null)
    {
      ((Drawable)localObject).setChangingConfigurations(localTypedValue.changingConfigurations);
      addDrawableToCache(paramContext, l, (Drawable)localObject);
    }
    return (Drawable)localObject;
  }
  
  private static PorterDuffColorFilter createTintFilter(ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int[] paramArrayOfInt)
  {
    if ((paramColorStateList == null) || (paramMode == null)) {
      return null;
    }
    return getPorterDuffColorFilter(paramColorStateList.getColorForState(paramArrayOfInt, 0), paramMode);
  }
  
  public static AppCompatDrawableManager get()
  {
    if (INSTANCE == null)
    {
      INSTANCE = new AppCompatDrawableManager();
      installDefaultInflateDelegates(INSTANCE);
    }
    return INSTANCE;
  }
  
  private Drawable getCachedDrawable(@NonNull Context paramContext, long paramLong)
  {
    synchronized (this.mDrawableCacheLock)
    {
      LongSparseArray localLongSparseArray = (LongSparseArray)this.mDrawableCaches.get(paramContext);
      if (localLongSparseArray == null) {
        return null;
      }
      Object localObject2 = (WeakReference)localLongSparseArray.get(paramLong);
      if (localObject2 != null)
      {
        localObject2 = (Drawable.ConstantState)((WeakReference)localObject2).get();
        if (localObject2 != null)
        {
          paramContext = ((Drawable.ConstantState)localObject2).newDrawable(paramContext.getResources());
          return paramContext;
        }
        localLongSparseArray.delete(paramLong);
      }
      return null;
    }
  }
  
  public static PorterDuffColorFilter getPorterDuffColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    PorterDuffColorFilter localPorterDuffColorFilter2 = COLOR_FILTER_CACHE.get(paramInt, paramMode);
    PorterDuffColorFilter localPorterDuffColorFilter1 = localPorterDuffColorFilter2;
    if (localPorterDuffColorFilter2 == null)
    {
      localPorterDuffColorFilter1 = new PorterDuffColorFilter(paramInt, paramMode);
      COLOR_FILTER_CACHE.put(paramInt, paramMode, localPorterDuffColorFilter1);
    }
    return localPorterDuffColorFilter1;
  }
  
  private ColorStateList getTintListFromCache(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    Object localObject = null;
    if (this.mTintLists != null)
    {
      SparseArray localSparseArray = (SparseArray)this.mTintLists.get(paramContext);
      paramContext = (Context)localObject;
      if (localSparseArray != null) {
        paramContext = (ColorStateList)localSparseArray.get(paramInt);
      }
      return paramContext;
    }
    return null;
  }
  
  static PorterDuff.Mode getTintMode(int paramInt)
  {
    PorterDuff.Mode localMode = null;
    if (paramInt == R.drawable.abc_switch_thumb_material) {
      localMode = PorterDuff.Mode.MULTIPLY;
    }
    return localMode;
  }
  
  private static void installDefaultInflateDelegates(@NonNull AppCompatDrawableManager paramAppCompatDrawableManager)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 24)
    {
      paramAppCompatDrawableManager.addDelegate("vector", new VdcInflateDelegate());
      if (i >= 11) {
        paramAppCompatDrawableManager.addDelegate("animated-vector", new AvdcInflateDelegate());
      }
    }
  }
  
  private static boolean isVectorDrawable(@NonNull Drawable paramDrawable)
  {
    if (!(paramDrawable instanceof VectorDrawableCompat)) {
      return "android.graphics.drawable.VectorDrawable".equals(paramDrawable.getClass().getName());
    }
    return true;
  }
  
  private Drawable loadDrawableFromDelegates(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    if ((this.mDelegates == null) || (this.mDelegates.isEmpty())) {
      return null;
    }
    if (this.mKnownDrawableIdTags != null)
    {
      localObject1 = (String)this.mKnownDrawableIdTags.get(paramInt);
      if (("appcompat_skip_skip".equals(localObject1)) || ((localObject1 != null) && (this.mDelegates.get(localObject1) == null))) {
        return null;
      }
    }
    else
    {
      this.mKnownDrawableIdTags = new SparseArray();
    }
    if (this.mTypedValue == null) {
      this.mTypedValue = new TypedValue();
    }
    TypedValue localTypedValue = this.mTypedValue;
    Object localObject1 = paramContext.getResources();
    ((Resources)localObject1).getValue(paramInt, localTypedValue, true);
    long l = createCacheKey(localTypedValue);
    Drawable localDrawable = getCachedDrawable(paramContext, l);
    if (localDrawable != null) {
      return localDrawable;
    }
    Object localObject2 = localDrawable;
    XmlResourceParser localXmlResourceParser;
    AttributeSet localAttributeSet;
    if (localTypedValue.string != null)
    {
      localObject2 = localDrawable;
      if (localTypedValue.string.toString().endsWith(".xml"))
      {
        localObject2 = localDrawable;
        try
        {
          localXmlResourceParser = ((Resources)localObject1).getXml(paramInt);
          localObject2 = localDrawable;
          localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
          int i;
          do
          {
            localObject2 = localDrawable;
            i = localXmlResourceParser.next();
          } while ((i != 2) && (i != 1));
          if (i != 2)
          {
            localObject2 = localDrawable;
            throw new XmlPullParserException("No start tag found");
          }
        }
        catch (Exception paramContext)
        {
          Log.e("AppCompatDrawableManager", "Exception while inflating drawable", paramContext);
        }
      }
    }
    for (;;)
    {
      if (localObject2 == null) {
        this.mKnownDrawableIdTags.append(paramInt, "appcompat_skip_skip");
      }
      return (Drawable)localObject2;
      localObject2 = localDrawable;
      localObject1 = localXmlResourceParser.getName();
      localObject2 = localDrawable;
      this.mKnownDrawableIdTags.append(paramInt, localObject1);
      localObject2 = localDrawable;
      InflateDelegate localInflateDelegate = (InflateDelegate)this.mDelegates.get(localObject1);
      localObject1 = localDrawable;
      if (localInflateDelegate != null)
      {
        localObject2 = localDrawable;
        localObject1 = localInflateDelegate.createFromXmlInner(paramContext, localXmlResourceParser, localAttributeSet, paramContext.getTheme());
      }
      localObject2 = localObject1;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        ((Drawable)localObject1).setChangingConfigurations(localTypedValue.changingConfigurations);
        localObject2 = localObject1;
        boolean bool = addDrawableToCache(paramContext, l, (Drawable)localObject1);
        localObject2 = localObject1;
        if (bool) {
          localObject2 = localObject1;
        }
      }
    }
  }
  
  private void removeDelegate(@NonNull String paramString, @NonNull InflateDelegate paramInflateDelegate)
  {
    if ((this.mDelegates != null) && (this.mDelegates.get(paramString) == paramInflateDelegate)) {
      this.mDelegates.remove(paramString);
    }
  }
  
  private static void setPorterDuffColorFilter(Drawable paramDrawable, int paramInt, PorterDuff.Mode paramMode)
  {
    Drawable localDrawable = paramDrawable;
    if (DrawableUtils.canSafelyMutateDrawable(paramDrawable)) {
      localDrawable = paramDrawable.mutate();
    }
    paramDrawable = paramMode;
    if (paramMode == null) {
      paramDrawable = DEFAULT_MODE;
    }
    localDrawable.setColorFilter(getPorterDuffColorFilter(paramInt, paramDrawable));
  }
  
  private Drawable tintDrawable(@NonNull Context paramContext, @DrawableRes int paramInt, boolean paramBoolean, @NonNull Drawable paramDrawable)
  {
    Object localObject = getTintList(paramContext, paramInt);
    if (localObject != null)
    {
      paramContext = paramDrawable;
      if (DrawableUtils.canSafelyMutateDrawable(paramDrawable)) {
        paramContext = paramDrawable.mutate();
      }
      paramContext = DrawableCompat.wrap(paramContext);
      DrawableCompat.setTintList(paramContext, (ColorStateList)localObject);
      paramDrawable = getTintMode(paramInt);
      localObject = paramContext;
      if (paramDrawable != null)
      {
        DrawableCompat.setTintMode(paramContext, paramDrawable);
        localObject = paramContext;
      }
    }
    do
    {
      do
      {
        return (Drawable)localObject;
        if (paramInt == R.drawable.abc_seekbar_track_material)
        {
          localObject = (LayerDrawable)paramDrawable;
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908288), ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlNormal), DEFAULT_MODE);
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlNormal), DEFAULT_MODE);
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlActivated), DEFAULT_MODE);
          return paramDrawable;
        }
        if ((paramInt == R.drawable.abc_ratingbar_material) || (paramInt == R.drawable.abc_ratingbar_indicator_material)) {}
        while (paramInt == R.drawable.abc_ratingbar_small_material)
        {
          localObject = (LayerDrawable)paramDrawable;
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908288), ThemeUtils.getDisabledThemeAttrColor(paramContext, R.attr.colorControlNormal), DEFAULT_MODE);
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlActivated), DEFAULT_MODE);
          setPorterDuffColorFilter(((LayerDrawable)localObject).findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlActivated), DEFAULT_MODE);
          return paramDrawable;
        }
        localObject = paramDrawable;
      } while (tintDrawableUsingColorFilter(paramContext, paramInt, paramDrawable));
      localObject = paramDrawable;
    } while (!paramBoolean);
    return null;
  }
  
  static void tintDrawable(Drawable paramDrawable, TintInfo paramTintInfo, int[] paramArrayOfInt)
  {
    if ((DrawableUtils.canSafelyMutateDrawable(paramDrawable)) && (paramDrawable.mutate() != paramDrawable))
    {
      Log.d("AppCompatDrawableManager", "Mutated drawable is not the same instance as the input.");
      return;
    }
    ColorStateList localColorStateList;
    if ((paramTintInfo.mHasTintList) || (paramTintInfo.mHasTintMode)) {
      if (paramTintInfo.mHasTintList)
      {
        localColorStateList = paramTintInfo.mTintList;
        if (!paramTintInfo.mHasTintMode) {
          break label91;
        }
        paramTintInfo = paramTintInfo.mTintMode;
        label63:
        paramDrawable.setColorFilter(createTintFilter(localColorStateList, paramTintInfo, paramArrayOfInt));
      }
    }
    for (;;)
    {
      if (Build.VERSION.SDK_INT <= 23) {
        paramDrawable.invalidateSelf();
      }
      return;
      localColorStateList = null;
      break;
      label91:
      paramTintInfo = DEFAULT_MODE;
      break label63;
      paramDrawable.clearColorFilter();
    }
  }
  
  static boolean tintDrawableUsingColorFilter(@NonNull Context paramContext, @DrawableRes int paramInt, @NonNull Drawable paramDrawable)
  {
    Object localObject2 = DEFAULT_MODE;
    int j = 0;
    int i = 0;
    int m = -1;
    Object localObject1;
    int k;
    if (arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, paramInt))
    {
      i = R.attr.colorControlNormal;
      j = 1;
      localObject1 = localObject2;
      k = m;
    }
    while (j != 0)
    {
      localObject2 = paramDrawable;
      if (DrawableUtils.canSafelyMutateDrawable(paramDrawable)) {
        localObject2 = paramDrawable.mutate();
      }
      ((Drawable)localObject2).setColorFilter(getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(paramContext, i), (PorterDuff.Mode)localObject1));
      if (k != -1) {
        ((Drawable)localObject2).setAlpha(k);
      }
      return true;
      if (arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, paramInt))
      {
        i = R.attr.colorControlActivated;
        j = 1;
        k = m;
        localObject1 = localObject2;
      }
      else if (arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, paramInt))
      {
        i = 16842801;
        j = 1;
        localObject1 = PorterDuff.Mode.MULTIPLY;
        k = m;
      }
      else if (paramInt == R.drawable.abc_list_divider_mtrl_alpha)
      {
        i = 16842800;
        j = 1;
        k = Math.round(40.8F);
        localObject1 = localObject2;
      }
      else
      {
        k = m;
        localObject1 = localObject2;
        if (paramInt == R.drawable.abc_dialog_material_background)
        {
          i = 16842801;
          j = 1;
          k = m;
          localObject1 = localObject2;
        }
      }
    }
    return false;
  }
  
  public Drawable getDrawable(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    return getDrawable(paramContext, paramInt, false);
  }
  
  Drawable getDrawable(@NonNull Context paramContext, @DrawableRes int paramInt, boolean paramBoolean)
  {
    checkVectorDrawableSetup(paramContext);
    Object localObject2 = loadDrawableFromDelegates(paramContext, paramInt);
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = createDrawableIfNeeded(paramContext, paramInt);
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = ContextCompat.getDrawable(paramContext, paramInt);
    }
    localObject1 = localObject2;
    if (localObject2 != null) {
      localObject1 = tintDrawable(paramContext, paramInt, paramBoolean, (Drawable)localObject2);
    }
    if (localObject1 != null) {
      DrawableUtils.fixDrawable((Drawable)localObject1);
    }
    return (Drawable)localObject1;
  }
  
  ColorStateList getTintList(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    return getTintList(paramContext, paramInt, null);
  }
  
  ColorStateList getTintList(@NonNull Context paramContext, @DrawableRes int paramInt, @Nullable ColorStateList paramColorStateList)
  {
    ColorStateList localColorStateList1 = null;
    int i;
    ColorStateList localColorStateList2;
    if (paramColorStateList == null)
    {
      i = 1;
      if (i != 0) {
        localColorStateList1 = getTintListFromCache(paramContext, paramInt);
      }
      localColorStateList2 = localColorStateList1;
      if (localColorStateList1 == null)
      {
        if (paramInt != R.drawable.abc_edit_text_material) {
          break label81;
        }
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_edittext);
      }
    }
    for (;;)
    {
      localColorStateList2 = paramColorStateList;
      if (i != 0)
      {
        localColorStateList2 = paramColorStateList;
        if (paramColorStateList != null)
        {
          addTintListToCache(paramContext, paramInt, paramColorStateList);
          localColorStateList2 = paramColorStateList;
        }
      }
      return localColorStateList2;
      i = 0;
      break;
      label81:
      if (paramInt == R.drawable.abc_switch_track_mtrl_alpha)
      {
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_switch_track);
      }
      else if (paramInt == R.drawable.abc_switch_thumb_material)
      {
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_switch_thumb);
      }
      else if (paramInt == R.drawable.abc_btn_default_mtrl_shape)
      {
        paramColorStateList = createDefaultButtonColorStateList(paramContext, paramColorStateList);
      }
      else if (paramInt == R.drawable.abc_btn_borderless_material)
      {
        paramColorStateList = createBorderlessButtonColorStateList(paramContext, paramColorStateList);
      }
      else if (paramInt == R.drawable.abc_btn_colored_material)
      {
        paramColorStateList = createColoredButtonColorStateList(paramContext, paramColorStateList);
      }
      else if ((paramInt == R.drawable.abc_spinner_mtrl_am_alpha) || (paramInt == R.drawable.abc_spinner_textfield_background_material))
      {
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_spinner);
      }
      else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, paramInt))
      {
        paramColorStateList = ThemeUtils.getThemeAttrColorStateList(paramContext, R.attr.colorControlNormal);
      }
      else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, paramInt))
      {
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_default);
      }
      else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, paramInt))
      {
        paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_btn_checkable);
      }
      else
      {
        paramColorStateList = localColorStateList1;
        if (paramInt == R.drawable.abc_seekbar_thumb_material) {
          paramColorStateList = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_seek_thumb);
        }
      }
    }
  }
  
  public void onConfigurationChanged(@NonNull Context paramContext)
  {
    synchronized (this.mDrawableCacheLock)
    {
      paramContext = (LongSparseArray)this.mDrawableCaches.get(paramContext);
      if (paramContext != null) {
        paramContext.clear();
      }
      return;
    }
  }
  
  Drawable onDrawableLoadedFromResources(@NonNull Context paramContext, @NonNull VectorEnabledTintResources paramVectorEnabledTintResources, @DrawableRes int paramInt)
  {
    Drawable localDrawable2 = loadDrawableFromDelegates(paramContext, paramInt);
    Drawable localDrawable1 = localDrawable2;
    if (localDrawable2 == null) {
      localDrawable1 = paramVectorEnabledTintResources.superGetDrawable(paramInt);
    }
    if (localDrawable1 != null) {
      return tintDrawable(paramContext, paramInt, false, localDrawable1);
    }
    return null;
  }
  
  private static class AvdcInflateDelegate
    implements AppCompatDrawableManager.InflateDelegate
  {
    public Drawable createFromXmlInner(@NonNull Context paramContext, @NonNull XmlPullParser paramXmlPullParser, @NonNull AttributeSet paramAttributeSet, @Nullable Resources.Theme paramTheme)
    {
      try
      {
        paramContext = AnimatedVectorDrawableCompat.createFromXmlInner(paramContext, paramContext.getResources(), paramXmlPullParser, paramAttributeSet, paramTheme);
        return paramContext;
      }
      catch (Exception paramContext)
      {
        Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", paramContext);
      }
      return null;
    }
  }
  
  private static class ColorFilterLruCache
    extends LruCache<Integer, PorterDuffColorFilter>
  {
    public ColorFilterLruCache(int paramInt)
    {
      super();
    }
    
    private static int generateCacheKey(int paramInt, PorterDuff.Mode paramMode)
    {
      return (paramInt + 31) * 31 + paramMode.hashCode();
    }
    
    PorterDuffColorFilter get(int paramInt, PorterDuff.Mode paramMode)
    {
      return (PorterDuffColorFilter)get(Integer.valueOf(generateCacheKey(paramInt, paramMode)));
    }
    
    PorterDuffColorFilter put(int paramInt, PorterDuff.Mode paramMode, PorterDuffColorFilter paramPorterDuffColorFilter)
    {
      return (PorterDuffColorFilter)put(Integer.valueOf(generateCacheKey(paramInt, paramMode)), paramPorterDuffColorFilter);
    }
  }
  
  private static abstract interface InflateDelegate
  {
    public abstract Drawable createFromXmlInner(@NonNull Context paramContext, @NonNull XmlPullParser paramXmlPullParser, @NonNull AttributeSet paramAttributeSet, @Nullable Resources.Theme paramTheme);
  }
  
  private static class VdcInflateDelegate
    implements AppCompatDrawableManager.InflateDelegate
  {
    public Drawable createFromXmlInner(@NonNull Context paramContext, @NonNull XmlPullParser paramXmlPullParser, @NonNull AttributeSet paramAttributeSet, @Nullable Resources.Theme paramTheme)
    {
      try
      {
        paramContext = VectorDrawableCompat.createFromXmlInner(paramContext.getResources(), paramXmlPullParser, paramAttributeSet, paramTheme);
        return paramContext;
      }
      catch (Exception paramContext)
      {
        Log.e("VdcInflateDelegate", "Exception while inflating <vector>", paramContext);
      }
      return null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\AppCompatDrawableManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */