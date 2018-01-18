package com.android.settingslib;

import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import android.view.InflateException;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SuggestionParser
{
  private static final String DISMISS_INDEX = "_dismiss_index";
  private static final String IS_DISMISSED = "_is_dismissed";
  public static final String META_DATA_DISMISS_CONTROL = "com.android.settings.dismiss";
  private static final String META_DATA_IS_SUPPORTED = "com.android.settings.is_supported";
  private static final String META_DATA_REQUIRE_ACCOUNT = "com.android.settings.require_account";
  public static final String META_DATA_REQUIRE_FEATURE = "com.android.settings.require_feature";
  private static final long MILLIS_IN_DAY = 86400000L;
  private static final String SETUP_TIME = "_setup_time";
  private static final String TAG = "SuggestionParser";
  private final ArrayMap<Pair<String, String>, Tile> addCache = new ArrayMap();
  private final Context mContext;
  private final SharedPreferences mSharedPrefs;
  private final List<SuggestionCategory> mSuggestionList;
  
  public SuggestionParser(Context paramContext, SharedPreferences paramSharedPreferences, int paramInt)
  {
    this.mContext = paramContext;
    this.mSuggestionList = ((List)new SuggestionOrderInflater(this.mContext).parse(paramInt));
    this.mSharedPrefs = paramSharedPreferences;
  }
  
  private long getEndTime(long paramLong, int paramInt)
  {
    return paramLong + paramInt * 86400000L;
  }
  
  private boolean isAvailable(Tile paramTile)
  {
    paramTile = paramTile.metaData.getString("com.android.settings.require_feature");
    if (paramTile != null) {
      return this.mContext.getPackageManager().hasSystemFeature(paramTile);
    }
    return true;
  }
  
  private boolean isDismissed(Tile paramTile)
  {
    Object localObject = paramTile.metaData.get("com.android.settings.dismiss");
    if (localObject == null) {
      return false;
    }
    localObject = String.valueOf(localObject);
    paramTile = paramTile.intent.getComponent().flattenToShortString();
    if (!this.mSharedPrefs.contains(paramTile + "_setup_time")) {
      this.mSharedPrefs.edit().putLong(paramTile + "_setup_time", System.currentTimeMillis()).commit();
    }
    if (!this.mSharedPrefs.getBoolean(paramTile + "_is_dismissed", true)) {
      return false;
    }
    int i = this.mSharedPrefs.getInt(paramTile + "_dismiss_index", 0);
    int j = parseDismissString(localObject)[i];
    long l = getEndTime(this.mSharedPrefs.getLong(paramTile + "_setup_time", 0L), j);
    if (System.currentTimeMillis() >= l)
    {
      this.mSharedPrefs.edit().putBoolean(paramTile + "_is_dismissed", false).putInt(paramTile + "_dismiss_index", i + 1).commit();
      return false;
    }
    return true;
  }
  
  private int[] parseDismissString(String paramString)
  {
    paramString = paramString.split(",");
    int[] arrayOfInt = new int[paramString.length];
    int i = 0;
    while (i < paramString.length)
    {
      arrayOfInt[i] = Integer.parseInt(paramString[i]);
      i += 1;
    }
    return arrayOfInt;
  }
  
  private void readSuggestions(SuggestionCategory paramSuggestionCategory, List<Tile> paramList)
  {
    int k = paramList.size();
    Object localObject = new Intent("android.intent.action.MAIN");
    ((Intent)localObject).addCategory(paramSuggestionCategory.category);
    if (paramSuggestionCategory.pkg != null) {
      ((Intent)localObject).setPackage(paramSuggestionCategory.pkg);
    }
    TileUtils.getTilesForIntent(this.mContext, new UserHandle(UserHandle.myUserId()), (Intent)localObject, this.addCache, null, paramList, true, false);
    int j;
    for (int i = k; i < paramList.size(); i = j + 1) {
      if ((isAvailable((Tile)paramList.get(i))) && (isSupported((Tile)paramList.get(i))) && (satisfiesRequiredAccount((Tile)paramList.get(i))))
      {
        j = i;
        if (!isDismissed((Tile)paramList.get(i))) {}
      }
      else
      {
        paramList.remove(i);
        j = i - 1;
      }
    }
    if ((!paramSuggestionCategory.multiple) && (paramList.size() > k + 1))
    {
      localObject = (Tile)paramList.remove(paramList.size() - 1);
      while (paramList.size() > k)
      {
        Tile localTile = (Tile)paramList.remove(paramList.size() - 1);
        if (localTile.priority > ((Tile)localObject).priority) {
          localObject = localTile;
        }
      }
      if (!isCategoryDone(paramSuggestionCategory.category)) {
        paramList.add(localObject);
      }
    }
  }
  
  public boolean dismissSuggestion(Tile paramTile)
  {
    String str = paramTile.intent.getComponent().flattenToShortString();
    int i = this.mSharedPrefs.getInt(str + "_dismiss_index", 0);
    paramTile = paramTile.metaData.getString("com.android.settings.dismiss");
    if ((paramTile == null) || (parseDismissString(paramTile).length == i)) {
      return true;
    }
    this.mSharedPrefs.edit().putBoolean(str + "_is_dismissed", true).commit();
    return false;
  }
  
  public List<Tile> getSuggestions()
  {
    ArrayList localArrayList = new ArrayList();
    int j = this.mSuggestionList.size();
    int i = 0;
    while (i < j)
    {
      readSuggestions((SuggestionCategory)this.mSuggestionList.get(i), localArrayList);
      i += 1;
    }
    return localArrayList;
  }
  
  public boolean isCategoryDone(String paramString)
  {
    boolean bool = false;
    paramString = "suggested.completed_category." + paramString;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), paramString, 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSupported(Tile paramTile)
  {
    int i = paramTile.metaData.getInt("com.android.settings.is_supported");
    try
    {
      if (paramTile.intent == null) {
        return false;
      }
      Resources localResources = this.mContext.getPackageManager().getResourcesForActivity(paramTile.intent.getComponent());
      if (i != 0)
      {
        boolean bool = localResources.getBoolean(i);
        return bool;
      }
      return true;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("SuggestionParser", "Cannot find resources for " + paramTile.intent.getComponent(), localNotFoundException);
      return false;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("SuggestionParser", "Cannot find resources for " + paramTile.intent.getComponent());
    }
    return false;
  }
  
  public void markCategoryDone(String paramString)
  {
    paramString = "suggested.completed_category." + paramString;
    Settings.Secure.putInt(this.mContext.getContentResolver(), paramString, 1);
  }
  
  public boolean satisfiesRequiredAccount(Tile paramTile)
  {
    paramTile = paramTile.metaData.getString("com.android.settings.require_account");
    if (paramTile == null) {
      return true;
    }
    return AccountManager.get(this.mContext).getAccountsByType(paramTile).length > 0;
  }
  
  private static class SuggestionCategory
  {
    public String category;
    public boolean multiple;
    public String pkg;
  }
  
  private static class SuggestionOrderInflater
  {
    private static final String ATTR_CATEGORY = "category";
    private static final String ATTR_MULTIPLE = "multiple";
    private static final String ATTR_PACKAGE = "package";
    private static final String TAG_ITEM = "step";
    private static final String TAG_LIST = "optional-steps";
    private final Context mContext;
    
    public SuggestionOrderInflater(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private void rParse(XmlPullParser paramXmlPullParser, Object paramObject, AttributeSet paramAttributeSet)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
          break;
        }
        if (j == 2)
        {
          Object localObject = onCreateItem(paramXmlPullParser.getName(), paramAttributeSet);
          onAddChildItem(paramObject, localObject);
          rParse(paramXmlPullParser, localObject, paramAttributeSet);
        }
      }
    }
    
    protected void onAddChildItem(Object paramObject1, Object paramObject2)
    {
      if (((paramObject1 instanceof List)) && ((paramObject2 instanceof SuggestionParser.SuggestionCategory)))
      {
        ((List)paramObject1).add((SuggestionParser.SuggestionCategory)paramObject2);
        return;
      }
      throw new IllegalArgumentException("Parent was not a list");
    }
    
    protected Object onCreateItem(String paramString, AttributeSet paramAttributeSet)
    {
      if (paramString.equals("optional-steps")) {
        return new ArrayList();
      }
      if (paramString.equals("step"))
      {
        paramString = new SuggestionParser.SuggestionCategory(null);
        paramString.category = paramAttributeSet.getAttributeValue(null, "category");
        paramString.pkg = paramAttributeSet.getAttributeValue(null, "package");
        paramAttributeSet = paramAttributeSet.getAttributeValue(null, "multiple");
        if (!TextUtils.isEmpty(paramAttributeSet)) {}
        for (boolean bool = Boolean.parseBoolean(paramAttributeSet);; bool = false)
        {
          paramString.multiple = bool;
          return paramString;
        }
      }
      throw new IllegalArgumentException("Unknown item " + paramString);
    }
    
    public Object parse(int paramInt)
    {
      XmlResourceParser localXmlResourceParser = this.mContext.getResources().getXml(paramInt);
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      try
      {
        int i;
        do
        {
          i = localXmlResourceParser.next();
        } while ((i != 2) && (i != 1));
        if (i != 2) {
          throw new InflateException(localXmlResourceParser.getPositionDescription() + ": No start tag found!");
        }
      }
      catch (XmlPullParserException|IOException localXmlPullParserException)
      {
        Log.w("SuggestionParser", "Problem parser resource " + paramInt, localXmlPullParserException);
        return null;
      }
      Object localObject = onCreateItem(localXmlPullParserException.getName(), localAttributeSet);
      rParse(localXmlPullParserException, localObject, localAttributeSet);
      return localObject;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\SuggestionParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */