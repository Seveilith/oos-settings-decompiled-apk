package com.android.settings.search;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.SearchIndexableData;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import java.lang.reflect.Field;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Index
{
  private static final String BASE_AUTHORITY = "com.android.settings";
  private static final String CLOUD_PACKAGE_NAME = "com.oneplus.cloud";
  public static final int COLUMN_INDEX_CLASS_NAME = 6;
  public static final int COLUMN_INDEX_ENABLED = 12;
  public static final int COLUMN_INDEX_ENTRIES = 4;
  public static final int COLUMN_INDEX_ICON = 8;
  public static final int COLUMN_INDEX_INTENT_ACTION = 9;
  public static final int COLUMN_INDEX_INTENT_ACTION_TARGET_CLASS = 11;
  public static final int COLUMN_INDEX_INTENT_ACTION_TARGET_PACKAGE = 10;
  public static final int COLUMN_INDEX_KEY = 13;
  public static final int COLUMN_INDEX_KEYWORDS = 5;
  public static final int COLUMN_INDEX_RANK = 0;
  public static final int COLUMN_INDEX_SCREEN_TITLE = 7;
  public static final int COLUMN_INDEX_SUMMARY_OFF = 3;
  public static final int COLUMN_INDEX_SUMMARY_ON = 2;
  public static final int COLUMN_INDEX_TITLE = 1;
  public static final int COLUMN_INDEX_USER_ID = 14;
  private static final String EMPTY = "";
  private static final List<String> EMPTY_LIST = Collections.emptyList();
  public static final String ENTRIES_SEPARATOR = "|";
  private static final String FIELD_NAME_SEARCH_INDEX_DATA_PROVIDER = "SEARCH_INDEX_DATA_PROVIDER";
  private static final String HYPHEN = "-";
  private static final String LIST_DELIMITERS = "[,]\\s*";
  private static final String LOG_TAG = "Index";
  private static final String[] MATCH_COLUMNS_PRIMARY;
  private static final String[] MATCH_COLUMNS_SECONDARY;
  private static final int MAX_PROPOSED_SUGGESTIONS = 5;
  private static long MAX_SAVED_SEARCH_QUERY = 0L;
  private static final String NODE_NAME_CHECK_BOX_PREFERENCE = "CheckBoxPreference";
  private static final String NODE_NAME_LIST_PREFERENCE = "ListPreference";
  private static final String NODE_NAME_PREFERENCE_SCREEN = "PreferenceScreen";
  private static final String NON_BREAKING_HYPHEN = "‑";
  private static final String OPBACKUP_PACKAGE_NAME = "com.oneplus.opbackup";
  private static final Pattern REMOVE_DIACRITICALS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  private static final String SECURITY_PACKAGE_NAME = "com.oneplus.security";
  private static final String[] SELECT_COLUMNS = { "data_rank", "data_title", "data_summary_on", "data_summary_off", "data_entries", "data_keywords", "class_name", "screen_title", "icon", "intent_action", "intent_target_package", "intent_target_class", "enabled", "data_key_reference" };
  private static final String SPACE = " ";
  private static Index sInstance;
  private final String mBaseAuthority;
  private Context mContext;
  private final UpdateData mDataToProcess = new UpdateData();
  private final AtomicBoolean mIsAvailable = new AtomicBoolean(false);
  
  static
  {
    MATCH_COLUMNS_PRIMARY = new String[] { "data_title", "data_title_normalized", "data_keywords" };
    MATCH_COLUMNS_SECONDARY = new String[] { "data_summary_on", "data_summary_on_normalized", "data_summary_off", "data_summary_off_normalized", "data_entries" };
    MAX_SAVED_SEARCH_QUERY = 64L;
  }
  
  public Index(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mBaseAuthority = paramString;
  }
  
  private void addIndexablesForRawDataUri(Context paramContext, String paramString, Uri paramUri, String[] paramArrayOfString, int paramInt)
  {
    paramArrayOfString = paramContext.getContentResolver().query(paramUri, paramArrayOfString, null, null, null);
    if (paramArrayOfString == null)
    {
      Log.w("Index", "Cannot add index data for Uri: " + paramUri.toString());
      return;
    }
    for (;;)
    {
      try
      {
        if (paramArrayOfString.getCount() <= 0) {
          break;
        }
        if (!paramArrayOfString.moveToNext()) {
          break;
        }
        int i = paramArrayOfString.getInt(0);
        if (i > 0)
        {
          i = paramInt + i;
          paramUri = paramArrayOfString.getString(1);
          String str1 = paramArrayOfString.getString(2);
          String str2 = paramArrayOfString.getString(3);
          String str3 = paramArrayOfString.getString(4);
          String str4 = paramArrayOfString.getString(5);
          String str5 = paramArrayOfString.getString(6);
          String str6 = paramArrayOfString.getString(7);
          int j = paramArrayOfString.getInt(8);
          String str7 = paramArrayOfString.getString(9);
          String str8 = paramArrayOfString.getString(10);
          String str9 = paramArrayOfString.getString(11);
          String str10 = paramArrayOfString.getString(12);
          int k = paramArrayOfString.getInt(13);
          SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramContext);
          localSearchIndexableRaw.rank = i;
          localSearchIndexableRaw.title = paramUri;
          localSearchIndexableRaw.summaryOn = str1;
          localSearchIndexableRaw.summaryOff = str2;
          localSearchIndexableRaw.entries = str3;
          localSearchIndexableRaw.keywords = str4;
          localSearchIndexableRaw.screenTitle = str5;
          localSearchIndexableRaw.className = str6;
          localSearchIndexableRaw.packageName = paramString;
          localSearchIndexableRaw.iconResId = j;
          localSearchIndexableRaw.intentAction = str7;
          localSearchIndexableRaw.intentTargetPackage = str8;
          localSearchIndexableRaw.intentTargetClass = str9;
          localSearchIndexableRaw.key = str10;
          localSearchIndexableRaw.userId = k;
          addIndexableData(localSearchIndexableRaw);
        }
        else
        {
          i = paramInt;
        }
      }
      finally
      {
        paramArrayOfString.close();
      }
    }
    paramArrayOfString.close();
  }
  
  private void addIndexablesForXmlResourceUri(Context paramContext, String paramString, Uri paramUri, String[] paramArrayOfString, int paramInt)
  {
    paramArrayOfString = paramContext.getContentResolver().query(paramUri, paramArrayOfString, null, null, null);
    if (paramArrayOfString == null)
    {
      Log.w("Index", "Cannot add index data for Uri: " + paramUri.toString());
      return;
    }
    for (;;)
    {
      try
      {
        if (paramArrayOfString.getCount() <= 0) {
          break;
        }
        if (!paramArrayOfString.moveToNext()) {
          break;
        }
        int i = paramArrayOfString.getInt(0);
        if (i > 0)
        {
          i = paramInt + i;
          int j = paramArrayOfString.getInt(1);
          paramUri = paramArrayOfString.getString(2);
          int k = paramArrayOfString.getInt(3);
          String str1 = paramArrayOfString.getString(4);
          String str2 = paramArrayOfString.getString(5);
          String str3 = paramArrayOfString.getString(6);
          SearchIndexableResource localSearchIndexableResource = new SearchIndexableResource(paramContext);
          localSearchIndexableResource.rank = i;
          localSearchIndexableResource.xmlResId = j;
          localSearchIndexableResource.className = paramUri;
          localSearchIndexableResource.packageName = paramString;
          localSearchIndexableResource.iconResId = k;
          localSearchIndexableResource.intentAction = str1;
          localSearchIndexableResource.intentTargetPackage = str2;
          localSearchIndexableResource.intentTargetClass = str3;
          addIndexableData(localSearchIndexableResource);
        }
        else
        {
          i = paramInt;
        }
      }
      finally
      {
        paramArrayOfString.close();
      }
    }
    paramArrayOfString.close();
  }
  
  private boolean addIndexablesFromRemoteProvider(String paramString1, String paramString2)
  {
    try
    {
      int i = Ranking.getBaseRankForAuthority(paramString2);
      if (this.mBaseAuthority.equals(paramString2)) {}
      for (Context localContext = this.mContext;; localContext = this.mContext.createPackageContext(paramString1, 0))
      {
        addIndexablesForXmlResourceUri(localContext, paramString1, buildUriForXmlResources(paramString2), SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS, i);
        addIndexablesForRawDataUri(localContext, paramString1, buildUriForRawData(paramString2), SearchIndexablesContract.INDEXABLES_RAW_COLUMNS, i);
        return true;
      }
      return false;
    }
    catch (PackageManager.NameNotFoundException paramString2)
    {
      Log.w("Index", "Could not create context for " + paramString1 + ": " + Log.getStackTraceString(paramString2));
    }
  }
  
  private void addNonIndexablesKeysFromRemoteProvider(String paramString1, String paramString2)
  {
    addNonIndexableKeys(paramString1, getNonIndexablesKeysFromRemoteProvider(paramString1, paramString2));
  }
  
  private void addSoftwareVersionCode(List<String> paramList)
  {
    Log.d("Index", "addSoftwareVersionCode begin");
    if (manipulateUpgradedSoftwareInfo(paramList))
    {
      this.mDataToProcess.softwareUpgraded = true;
      Log.d("Index", "sw Updated");
    }
    Log.d("Index", "addSoftwareVersionCode end");
  }
  
  private String buildSearchMatchStringForColumns(String paramString, String[] paramArrayOfString)
  {
    paramString = paramString + "*";
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append(paramArrayOfString[i]);
      localStringBuilder.append(":");
      localStringBuilder.append(paramString);
      if (i < j - 1) {
        localStringBuilder.append(" OR ");
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private String buildSearchSQL(String paramString, String[] paramArrayOfString, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(buildSearchSQLForColumn(paramString, paramArrayOfString));
    if (paramBoolean)
    {
      localStringBuilder.append(" ORDER BY ");
      localStringBuilder.append("data_rank");
    }
    return localStringBuilder.toString();
  }
  
  private String buildSearchSQLForColumn(String paramString, String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SELECT ");
    int i = 0;
    while (i < SELECT_COLUMNS.length)
    {
      localStringBuilder.append(SELECT_COLUMNS[i]);
      if (i < SELECT_COLUMNS.length - 1) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    localStringBuilder.append(" FROM ");
    localStringBuilder.append("prefs_index");
    localStringBuilder.append(" WHERE ");
    localStringBuilder.append(buildSearchWhereStringForColumns(paramString, paramArrayOfString));
    return localStringBuilder.toString();
  }
  
  private String buildSearchWhereStringForColumns(String paramString, String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder("prefs_index");
    localStringBuilder.append(" MATCH ");
    DatabaseUtils.appendEscapedSQLString(localStringBuilder, buildSearchMatchStringForColumns(paramString, paramArrayOfString));
    localStringBuilder.append(" AND ");
    localStringBuilder.append("locale");
    localStringBuilder.append(" = ");
    paramArrayOfString = this.mContext.getResources().getConfiguration().locale;
    paramString = Locale.getDefault().toString();
    if (paramArrayOfString != null) {
      paramString = paramArrayOfString.toString();
    }
    DatabaseUtils.appendEscapedSQLString(localStringBuilder, paramString);
    localStringBuilder.append(" AND ");
    localStringBuilder.append("enabled");
    localStringBuilder.append(" = 1");
    return localStringBuilder.toString();
  }
  
  private String buildSuggestionsSQL(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SELECT ");
    localStringBuilder.append("query");
    localStringBuilder.append(" FROM ");
    localStringBuilder.append("saved_queries");
    if (TextUtils.isEmpty(paramString)) {
      localStringBuilder.append(" ORDER BY rowId DESC");
    }
    for (;;)
    {
      localStringBuilder.append(" LIMIT ");
      localStringBuilder.append(5);
      return localStringBuilder.toString();
      localStringBuilder.append(" WHERE ");
      localStringBuilder.append("query");
      localStringBuilder.append(" LIKE ");
      localStringBuilder.append("'");
      localStringBuilder.append(paramString);
      localStringBuilder.append("%");
      localStringBuilder.append("'");
    }
  }
  
  private static Uri buildUriForNonIndexableKeys(String paramString)
  {
    return Uri.parse("content://" + paramString + "/" + "settings/non_indexables_key");
  }
  
  private static Uri buildUriForRawData(String paramString)
  {
    return Uri.parse("content://" + paramString + "/" + "settings/indexables_raw");
  }
  
  private static Uri buildUriForXmlResources(String paramString)
  {
    return Uri.parse("content://" + paramString + "/" + "settings/indexables_xml_res");
  }
  
  private String getData(Context paramContext, AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt)
  {
    Object localObject = null;
    TypedValue localTypedValue = paramContext.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt).peekValue(paramInt);
    paramArrayOfInt = null;
    paramAttributeSet = paramArrayOfInt;
    if (localTypedValue != null)
    {
      paramAttributeSet = paramArrayOfInt;
      if (localTypedValue.type == 3) {
        if (localTypedValue.resourceId == 0) {
          break label70;
        }
      }
    }
    label70:
    for (paramAttributeSet = paramContext.getText(localTypedValue.resourceId);; paramAttributeSet = localTypedValue.string)
    {
      paramContext = (Context)localObject;
      if (paramAttributeSet != null) {
        paramContext = paramAttributeSet.toString();
      }
      return paramContext;
    }
  }
  
  private String getDataEntries(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getDataEntries(paramContext, paramAttributeSet, com.android.internal.R.styleable.ListPreference, 0);
  }
  
  private String getDataEntries(Context paramContext, AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    TypedValue localTypedValue = paramContext.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt).peekValue(paramInt);
    paramArrayOfInt = null;
    paramAttributeSet = paramArrayOfInt;
    if (localTypedValue != null)
    {
      paramAttributeSet = paramArrayOfInt;
      if (localTypedValue.type == 1)
      {
        paramAttributeSet = paramArrayOfInt;
        if (localTypedValue.resourceId != 0) {
          paramAttributeSet = paramContext.getResources().getStringArray(localTypedValue.resourceId);
        }
      }
    }
    if (paramAttributeSet == null) {}
    for (paramInt = i; paramInt == 0; paramInt = paramAttributeSet.length) {
      return null;
    }
    paramContext = new StringBuilder();
    i = 0;
    while (i < paramInt)
    {
      paramContext.append(paramAttributeSet[i]);
      paramContext.append("|");
      i += 1;
    }
    return paramContext.toString();
  }
  
  private String getDataKey(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.internal.R.styleable.Preference, 6);
  }
  
  private String getDataKeywords(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.settings.R.styleable.Preference, 28);
  }
  
  private String getDataSummary(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.internal.R.styleable.Preference, 7);
  }
  
  private String getDataSummaryOff(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.internal.R.styleable.CheckBoxPreference, 1);
  }
  
  private String getDataSummaryOn(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.internal.R.styleable.CheckBoxPreference, 0);
  }
  
  private String getDataTitle(Context paramContext, AttributeSet paramAttributeSet)
  {
    return getData(paramContext, paramAttributeSet, com.android.internal.R.styleable.Preference, 4);
  }
  
  private static Class<?> getIndexableClass(String paramString)
  {
    try
    {
      Class localClass = Class.forName(paramString);
      if (isIndexableClass(localClass)) {
        return localClass;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Log.d("Index", "Cannot find class: " + paramString);
      return null;
    }
    return null;
  }
  
  public static Index getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new Index(paramContext.getApplicationContext(), "com.android.settings");
    }
    return sInstance;
  }
  
  private List<String> getNonIndexablesKeys(Context paramContext, Uri paramUri, String[] paramArrayOfString)
  {
    paramContext = paramContext.getContentResolver().query(paramUri, paramArrayOfString, null, null, null);
    if (paramContext == null)
    {
      Log.w("Index", "Cannot add index data for Uri: " + paramUri.toString());
      return EMPTY_LIST;
    }
    paramUri = new ArrayList();
    try
    {
      if (paramContext.getCount() > 0) {
        while (paramContext.moveToNext()) {
          paramUri.add(paramContext.getString(0));
        }
      }
    }
    finally
    {
      paramContext.close();
    }
    return paramUri;
  }
  
  private List<String> getNonIndexablesKeysFromRemoteProvider(String paramString1, String paramString2)
  {
    try
    {
      paramString2 = getNonIndexablesKeys(this.mContext.createPackageContext(paramString1, 0), buildUriForNonIndexableKeys(paramString2), SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
      return paramString2;
    }
    catch (PackageManager.NameNotFoundException paramString2)
    {
      Log.w("Index", "Could not create context for " + paramString1 + ": " + Log.getStackTraceString(paramString2));
    }
    return EMPTY_LIST;
  }
  
  private SQLiteDatabase getReadableDatabase()
  {
    return IndexDatabaseHelper.getInstance(this.mContext).getReadableDatabase();
  }
  
  private int getResId(Context paramContext, AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt).peekValue(paramInt);
    if ((paramContext != null) && (paramContext.type == 3)) {
      return paramContext.resourceId;
    }
    return 0;
  }
  
  private Indexable.SearchIndexProvider getSearchIndexProvider(Class<?> paramClass)
  {
    try
    {
      paramClass = (Indexable.SearchIndexProvider)paramClass.getField("SEARCH_INDEX_DATA_PROVIDER").get(null);
      return paramClass;
    }
    catch (IllegalArgumentException paramClass)
    {
      Log.d("Index", "Illegal argument when accessing field 'SEARCH_INDEX_DATA_PROVIDER'");
      return null;
    }
    catch (IllegalAccessException paramClass)
    {
      Log.d("Index", "Illegal access to field 'SEARCH_INDEX_DATA_PROVIDER'");
      return null;
    }
    catch (SecurityException paramClass)
    {
      Log.d("Index", "Security exception for field 'SEARCH_INDEX_DATA_PROVIDER'");
      return null;
    }
    catch (NoSuchFieldException paramClass)
    {
      Log.d("Index", "Cannot find field 'SEARCH_INDEX_DATA_PROVIDER'");
    }
    return null;
  }
  
  private SQLiteDatabase getWritableDatabase()
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = IndexDatabaseHelper.getInstance(this.mContext).getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      Log.e("Index", "Cannot open writable database", localSQLiteException);
    }
    return null;
  }
  
  private void indexFromProvider(Context paramContext, SQLiteDatabase paramSQLiteDatabase, String paramString1, Indexable.SearchIndexProvider paramSearchIndexProvider, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean, List<String> paramList)
  {
    if (paramSearchIndexProvider == null)
    {
      Log.w("Index", "Cannot find provider: " + paramString2);
      return;
    }
    List localList = paramSearchIndexProvider.getRawDataToIndex(paramContext, paramBoolean);
    int j;
    int i;
    Object localObject;
    if (localList != null)
    {
      j = localList.size();
      i = 0;
      if (i < j)
      {
        localObject = (SearchIndexableRaw)localList.get(i);
        if (!((SearchIndexableRaw)localObject).locale.toString().equalsIgnoreCase(paramString1)) {}
        for (;;)
        {
          i += 1;
          break;
          if (!paramList.contains(((SearchIndexableRaw)localObject).key)) {
            updateOneRowWithFilteredData(paramSQLiteDatabase, paramString1, ((SearchIndexableRaw)localObject).title, ((SearchIndexableRaw)localObject).summaryOn, ((SearchIndexableRaw)localObject).summaryOff, ((SearchIndexableRaw)localObject).entries, paramString2, ((SearchIndexableRaw)localObject).screenTitle, paramInt1, paramInt2, ((SearchIndexableRaw)localObject).keywords, ((SearchIndexableRaw)localObject).intentAction, ((SearchIndexableRaw)localObject).intentTargetPackage, ((SearchIndexableRaw)localObject).intentTargetClass, ((SearchIndexableRaw)localObject).enabled, ((SearchIndexableRaw)localObject).key, ((SearchIndexableRaw)localObject).userId);
          }
        }
      }
    }
    localList = paramSearchIndexProvider.getXmlResourcesToIndex(paramContext, paramBoolean);
    if (localList != null)
    {
      int m = localList.size();
      i = 0;
      while (i < m)
      {
        localObject = (SearchIndexableResource)localList.get(i);
        if (!((SearchIndexableResource)localObject).locale.toString().equalsIgnoreCase(paramString1))
        {
          i += 1;
        }
        else
        {
          label283:
          int k;
          if (((SearchIndexableResource)localObject).iconResId == 0)
          {
            j = paramInt1;
            if (((SearchIndexableResource)localObject).rank != 0) {
              break label358;
            }
            k = paramInt2;
            label295:
            if (!TextUtils.isEmpty(((SearchIndexableResource)localObject).className)) {
              break label368;
            }
          }
          label358:
          label368:
          for (paramSearchIndexProvider = paramString2;; paramSearchIndexProvider = ((SearchIndexableResource)localObject).className)
          {
            indexFromResource(paramContext, paramSQLiteDatabase, paramString1, ((SearchIndexableResource)localObject).xmlResId, paramSearchIndexProvider, j, k, ((SearchIndexableResource)localObject).intentAction, ((SearchIndexableResource)localObject).intentTargetPackage, ((SearchIndexableResource)localObject).intentTargetClass, paramList);
            break;
            j = ((SearchIndexableResource)localObject).iconResId;
            break label283;
            k = ((SearchIndexableResource)localObject).rank;
            break label295;
          }
        }
      }
    }
  }
  
  /* Error */
  private void indexFromResource(Context paramContext, SQLiteDatabase paramSQLiteDatabase, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, String paramString3, String paramString4, String paramString5, List<String> paramList)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 13
    //   3: aconst_null
    //   4: astore 16
    //   6: aconst_null
    //   7: astore 14
    //   9: aload_1
    //   10: invokevirtual 499	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   13: iload 4
    //   15: invokevirtual 757	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   18: astore 15
    //   20: aload 15
    //   22: astore 14
    //   24: aload 15
    //   26: astore 13
    //   28: aload 15
    //   30: astore 16
    //   32: aload 15
    //   34: invokeinterface 762 1 0
    //   39: istore 4
    //   41: iload 4
    //   43: iconst_1
    //   44: if_icmpeq +9 -> 53
    //   47: iload 4
    //   49: iconst_2
    //   50: if_icmpne -30 -> 20
    //   53: aload 15
    //   55: astore 14
    //   57: aload 15
    //   59: astore 13
    //   61: aload 15
    //   63: astore 16
    //   65: aload 15
    //   67: invokeinterface 765 1 0
    //   72: pop
    //   73: aload 15
    //   75: astore 14
    //   77: aload 15
    //   79: astore 13
    //   81: aload 15
    //   83: astore 16
    //   85: aload 15
    //   87: invokeinterface 768 1 0
    //   92: istore 4
    //   94: aload 15
    //   96: astore 14
    //   98: aload 15
    //   100: astore 13
    //   102: aload 15
    //   104: astore 16
    //   106: aload 15
    //   108: invokestatic 774	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   111: astore 19
    //   113: aload 15
    //   115: astore 14
    //   117: aload 15
    //   119: astore 13
    //   121: aload 15
    //   123: astore 16
    //   125: aload_0
    //   126: aload_1
    //   127: aload 19
    //   129: invokespecial 776	com/android/settings/search/Index:getDataTitle	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   132: astore 20
    //   134: aload 15
    //   136: astore 14
    //   138: aload 15
    //   140: astore 13
    //   142: aload 15
    //   144: astore 16
    //   146: aload_0
    //   147: aload_1
    //   148: aload 19
    //   150: invokespecial 778	com/android/settings/search/Index:getDataKey	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   153: astore 17
    //   155: aload 15
    //   157: astore 14
    //   159: aload 15
    //   161: astore 13
    //   163: aload 15
    //   165: astore 16
    //   167: aload 11
    //   169: aload 17
    //   171: invokeinterface 734 2 0
    //   176: ifne +62 -> 238
    //   179: aload 15
    //   181: astore 14
    //   183: aload 15
    //   185: astore 13
    //   187: aload 15
    //   189: astore 16
    //   191: aload_0
    //   192: aload_2
    //   193: aload_3
    //   194: aload_0
    //   195: aload_1
    //   196: aload 19
    //   198: invokespecial 776	com/android/settings/search/Index:getDataTitle	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   201: aload_0
    //   202: aload_1
    //   203: aload 19
    //   205: invokespecial 780	com/android/settings/search/Index:getDataSummary	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   208: aconst_null
    //   209: aconst_null
    //   210: aload 5
    //   212: aload 20
    //   214: iload 6
    //   216: iload 7
    //   218: aload_0
    //   219: aload_1
    //   220: aload 19
    //   222: invokespecial 782	com/android/settings/search/Index:getDataKeywords	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   225: aload 8
    //   227: aload 9
    //   229: aload 10
    //   231: iconst_1
    //   232: aload 17
    //   234: iconst_m1
    //   235: invokespecial 740	com/android/settings/search/Index:updateOneRowWithFilteredData	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;I)V
    //   238: aload 15
    //   240: astore 14
    //   242: aload 15
    //   244: astore 13
    //   246: aload 15
    //   248: astore 16
    //   250: aload 15
    //   252: invokeinterface 762 1 0
    //   257: istore 12
    //   259: iload 12
    //   261: iconst_1
    //   262: if_icmpeq +498 -> 760
    //   265: iload 12
    //   267: iconst_3
    //   268: if_icmpne +27 -> 295
    //   271: aload 15
    //   273: astore 14
    //   275: aload 15
    //   277: astore 13
    //   279: aload 15
    //   281: astore 16
    //   283: aload 15
    //   285: invokeinterface 768 1 0
    //   290: iload 4
    //   292: if_icmple +468 -> 760
    //   295: iload 12
    //   297: iconst_3
    //   298: if_icmpeq -60 -> 238
    //   301: iload 12
    //   303: iconst_4
    //   304: if_icmpeq -66 -> 238
    //   307: aload 15
    //   309: astore 14
    //   311: aload 15
    //   313: astore 13
    //   315: aload 15
    //   317: astore 16
    //   319: aload 15
    //   321: invokeinterface 765 1 0
    //   326: astore 24
    //   328: aload 15
    //   330: astore 14
    //   332: aload 15
    //   334: astore 13
    //   336: aload 15
    //   338: astore 16
    //   340: aload_0
    //   341: aload_1
    //   342: aload 19
    //   344: invokespecial 778	com/android/settings/search/Index:getDataKey	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   347: astore 21
    //   349: aload 15
    //   351: astore 14
    //   353: aload 15
    //   355: astore 13
    //   357: aload 15
    //   359: astore 16
    //   361: aload 11
    //   363: aload 21
    //   365: invokeinterface 734 2 0
    //   370: ifne -132 -> 238
    //   373: aload 15
    //   375: astore 14
    //   377: aload 15
    //   379: astore 13
    //   381: aload 15
    //   383: astore 16
    //   385: aload_0
    //   386: aload_1
    //   387: aload 19
    //   389: invokespecial 776	com/android/settings/search/Index:getDataTitle	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   392: astore 22
    //   394: aload 15
    //   396: astore 14
    //   398: aload 15
    //   400: astore 13
    //   402: aload 15
    //   404: astore 16
    //   406: aload_0
    //   407: aload_1
    //   408: aload 19
    //   410: invokespecial 782	com/android/settings/search/Index:getDataKeywords	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   413: astore 23
    //   415: aload 15
    //   417: astore 14
    //   419: aload 15
    //   421: astore 13
    //   423: aload 15
    //   425: astore 16
    //   427: aload 24
    //   429: ldc 89
    //   431: invokevirtual 390	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   434: ifne +150 -> 584
    //   437: aload 15
    //   439: astore 14
    //   441: aload 15
    //   443: astore 13
    //   445: aload 15
    //   447: astore 16
    //   449: aload_0
    //   450: aload_1
    //   451: aload 19
    //   453: invokespecial 780	com/android/settings/search/Index:getDataSummary	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   456: astore 18
    //   458: aconst_null
    //   459: astore 17
    //   461: aload 15
    //   463: astore 14
    //   465: aload 15
    //   467: astore 13
    //   469: aload 15
    //   471: astore 16
    //   473: aload 24
    //   475: ldc 92
    //   477: invokevirtual 785	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   480: ifeq +24 -> 504
    //   483: aload 15
    //   485: astore 14
    //   487: aload 15
    //   489: astore 13
    //   491: aload 15
    //   493: astore 16
    //   495: aload_0
    //   496: aload_1
    //   497: aload 19
    //   499: invokespecial 787	com/android/settings/search/Index:getDataEntries	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   502: astore 17
    //   504: aload 15
    //   506: astore 14
    //   508: aload 15
    //   510: astore 13
    //   512: aload 15
    //   514: astore 16
    //   516: aload_0
    //   517: aload_2
    //   518: aload_3
    //   519: aload 22
    //   521: aload 18
    //   523: aconst_null
    //   524: aload 17
    //   526: aload 5
    //   528: aload 20
    //   530: iload 6
    //   532: iload 7
    //   534: aload 23
    //   536: aload 8
    //   538: aload 9
    //   540: aload 10
    //   542: iconst_1
    //   543: aload 21
    //   545: iconst_m1
    //   546: invokespecial 740	com/android/settings/search/Index:updateOneRowWithFilteredData	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;I)V
    //   549: goto -311 -> 238
    //   552: astore_1
    //   553: aload 14
    //   555: astore 13
    //   557: new 789	java/lang/RuntimeException
    //   560: dup
    //   561: ldc_w 791
    //   564: aload_1
    //   565: invokespecial 794	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   568: athrow
    //   569: astore_1
    //   570: aload 13
    //   572: ifnull +10 -> 582
    //   575: aload 13
    //   577: invokeinterface 795 1 0
    //   582: aload_1
    //   583: athrow
    //   584: aload 15
    //   586: astore 14
    //   588: aload 15
    //   590: astore 13
    //   592: aload 15
    //   594: astore 16
    //   596: aload_0
    //   597: aload_1
    //   598: aload 19
    //   600: invokespecial 797	com/android/settings/search/Index:getDataSummaryOn	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   603: astore 18
    //   605: aload 15
    //   607: astore 14
    //   609: aload 15
    //   611: astore 13
    //   613: aload 15
    //   615: astore 16
    //   617: aload_0
    //   618: aload_1
    //   619: aload 19
    //   621: invokespecial 799	com/android/settings/search/Index:getDataSummaryOff	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   624: astore 24
    //   626: aload 15
    //   628: astore 14
    //   630: aload 15
    //   632: astore 13
    //   634: aload 18
    //   636: astore 17
    //   638: aload 15
    //   640: astore 16
    //   642: aload 18
    //   644: invokestatic 530	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   647: ifeq +48 -> 695
    //   650: aload 15
    //   652: astore 14
    //   654: aload 15
    //   656: astore 13
    //   658: aload 18
    //   660: astore 17
    //   662: aload 15
    //   664: astore 16
    //   666: aload 24
    //   668: invokestatic 530	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   671: ifeq +24 -> 695
    //   674: aload 15
    //   676: astore 14
    //   678: aload 15
    //   680: astore 13
    //   682: aload 15
    //   684: astore 16
    //   686: aload_0
    //   687: aload_1
    //   688: aload 19
    //   690: invokespecial 780	com/android/settings/search/Index:getDataSummary	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   693: astore 17
    //   695: aload 15
    //   697: astore 14
    //   699: aload 15
    //   701: astore 13
    //   703: aload 15
    //   705: astore 16
    //   707: aload_0
    //   708: aload_2
    //   709: aload_3
    //   710: aload 22
    //   712: aload 17
    //   714: aload 24
    //   716: aconst_null
    //   717: aload 5
    //   719: aload 20
    //   721: iload 6
    //   723: iload 7
    //   725: aload 23
    //   727: aload 8
    //   729: aload 9
    //   731: aload 10
    //   733: iconst_1
    //   734: aload 21
    //   736: iconst_m1
    //   737: invokespecial 740	com/android/settings/search/Index:updateOneRowWithFilteredData	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;I)V
    //   740: goto -502 -> 238
    //   743: astore_1
    //   744: aload 16
    //   746: astore 13
    //   748: new 789	java/lang/RuntimeException
    //   751: dup
    //   752: ldc_w 791
    //   755: aload_1
    //   756: invokespecial 794	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   759: athrow
    //   760: aload 15
    //   762: ifnull +10 -> 772
    //   765: aload 15
    //   767: invokeinterface 795 1 0
    //   772: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	773	0	this	Index
    //   0	773	1	paramContext	Context
    //   0	773	2	paramSQLiteDatabase	SQLiteDatabase
    //   0	773	3	paramString1	String
    //   0	773	4	paramInt1	int
    //   0	773	5	paramString2	String
    //   0	773	6	paramInt2	int
    //   0	773	7	paramInt3	int
    //   0	773	8	paramString3	String
    //   0	773	9	paramString4	String
    //   0	773	10	paramString5	String
    //   0	773	11	paramList	List<String>
    //   257	48	12	i	int
    //   1	746	13	localObject1	Object
    //   7	691	14	localObject2	Object
    //   18	748	15	localXmlResourceParser	android.content.res.XmlResourceParser
    //   4	741	16	localObject3	Object
    //   153	560	17	localObject4	Object
    //   456	203	18	str1	String
    //   111	578	19	localAttributeSet	AttributeSet
    //   132	588	20	str2	String
    //   347	388	21	str3	String
    //   392	319	22	str4	String
    //   413	313	23	str5	String
    //   326	389	24	str6	String
    // Exception table:
    //   from	to	target	type
    //   9	20	552	org/xmlpull/v1/XmlPullParserException
    //   32	41	552	org/xmlpull/v1/XmlPullParserException
    //   65	73	552	org/xmlpull/v1/XmlPullParserException
    //   85	94	552	org/xmlpull/v1/XmlPullParserException
    //   106	113	552	org/xmlpull/v1/XmlPullParserException
    //   125	134	552	org/xmlpull/v1/XmlPullParserException
    //   146	155	552	org/xmlpull/v1/XmlPullParserException
    //   167	179	552	org/xmlpull/v1/XmlPullParserException
    //   191	238	552	org/xmlpull/v1/XmlPullParserException
    //   250	259	552	org/xmlpull/v1/XmlPullParserException
    //   283	295	552	org/xmlpull/v1/XmlPullParserException
    //   319	328	552	org/xmlpull/v1/XmlPullParserException
    //   340	349	552	org/xmlpull/v1/XmlPullParserException
    //   361	373	552	org/xmlpull/v1/XmlPullParserException
    //   385	394	552	org/xmlpull/v1/XmlPullParserException
    //   406	415	552	org/xmlpull/v1/XmlPullParserException
    //   427	437	552	org/xmlpull/v1/XmlPullParserException
    //   449	458	552	org/xmlpull/v1/XmlPullParserException
    //   473	483	552	org/xmlpull/v1/XmlPullParserException
    //   495	504	552	org/xmlpull/v1/XmlPullParserException
    //   516	549	552	org/xmlpull/v1/XmlPullParserException
    //   596	605	552	org/xmlpull/v1/XmlPullParserException
    //   617	626	552	org/xmlpull/v1/XmlPullParserException
    //   642	650	552	org/xmlpull/v1/XmlPullParserException
    //   666	674	552	org/xmlpull/v1/XmlPullParserException
    //   686	695	552	org/xmlpull/v1/XmlPullParserException
    //   707	740	552	org/xmlpull/v1/XmlPullParserException
    //   9	20	569	finally
    //   32	41	569	finally
    //   65	73	569	finally
    //   85	94	569	finally
    //   106	113	569	finally
    //   125	134	569	finally
    //   146	155	569	finally
    //   167	179	569	finally
    //   191	238	569	finally
    //   250	259	569	finally
    //   283	295	569	finally
    //   319	328	569	finally
    //   340	349	569	finally
    //   361	373	569	finally
    //   385	394	569	finally
    //   406	415	569	finally
    //   427	437	569	finally
    //   449	458	569	finally
    //   473	483	569	finally
    //   495	504	569	finally
    //   516	549	569	finally
    //   557	569	569	finally
    //   596	605	569	finally
    //   617	626	569	finally
    //   642	650	569	finally
    //   666	674	569	finally
    //   686	695	569	finally
    //   707	740	569	finally
    //   748	760	569	finally
    //   9	20	743	java/io/IOException
    //   32	41	743	java/io/IOException
    //   65	73	743	java/io/IOException
    //   85	94	743	java/io/IOException
    //   106	113	743	java/io/IOException
    //   125	134	743	java/io/IOException
    //   146	155	743	java/io/IOException
    //   167	179	743	java/io/IOException
    //   191	238	743	java/io/IOException
    //   250	259	743	java/io/IOException
    //   283	295	743	java/io/IOException
    //   319	328	743	java/io/IOException
    //   340	349	743	java/io/IOException
    //   361	373	743	java/io/IOException
    //   385	394	743	java/io/IOException
    //   406	415	743	java/io/IOException
    //   427	437	743	java/io/IOException
    //   449	458	743	java/io/IOException
    //   473	483	743	java/io/IOException
    //   495	504	743	java/io/IOException
    //   516	549	743	java/io/IOException
    //   596	605	743	java/io/IOException
    //   617	626	743	java/io/IOException
    //   642	650	743	java/io/IOException
    //   666	674	743	java/io/IOException
    //   686	695	743	java/io/IOException
    //   707	740	743	java/io/IOException
  }
  
  private void indexOneRaw(SQLiteDatabase paramSQLiteDatabase, String paramString, SearchIndexableRaw paramSearchIndexableRaw)
  {
    if (!paramSearchIndexableRaw.locale.toString().equalsIgnoreCase(paramString)) {
      return;
    }
    updateOneRowWithFilteredData(paramSQLiteDatabase, paramString, paramSearchIndexableRaw.title, paramSearchIndexableRaw.summaryOn, paramSearchIndexableRaw.summaryOff, paramSearchIndexableRaw.entries, paramSearchIndexableRaw.className, paramSearchIndexableRaw.screenTitle, paramSearchIndexableRaw.iconResId, paramSearchIndexableRaw.rank, paramSearchIndexableRaw.keywords, paramSearchIndexableRaw.intentAction, paramSearchIndexableRaw.intentTargetPackage, paramSearchIndexableRaw.intentTargetClass, paramSearchIndexableRaw.enabled, paramSearchIndexableRaw.key, paramSearchIndexableRaw.userId);
  }
  
  private void indexOneResource(SQLiteDatabase paramSQLiteDatabase, String paramString, SearchIndexableResource paramSearchIndexableResource, Map<String, List<String>> paramMap)
  {
    if (paramSearchIndexableResource == null)
    {
      Log.e("Index", "Cannot index a null resource!");
      return;
    }
    ArrayList localArrayList = new ArrayList();
    if (paramSearchIndexableResource.xmlResId > SearchIndexableResources.NO_DATA_RES_ID)
    {
      paramMap = (List)paramMap.get(paramSearchIndexableResource.packageName);
      if ((paramMap != null) && (paramMap.size() > 0)) {
        localArrayList.addAll(paramMap);
      }
      indexFromResource(paramSearchIndexableResource.context, paramSQLiteDatabase, paramString, paramSearchIndexableResource.xmlResId, paramSearchIndexableResource.className, paramSearchIndexableResource.iconResId, paramSearchIndexableResource.rank, paramSearchIndexableResource.intentAction, paramSearchIndexableResource.intentTargetPackage, paramSearchIndexableResource.intentTargetClass, localArrayList);
    }
    do
    {
      return;
      if (TextUtils.isEmpty(paramSearchIndexableResource.className))
      {
        Log.w("Index", "Cannot index an empty Search Provider name!");
        return;
      }
      paramMap = getIndexableClass(paramSearchIndexableResource.className);
      if (paramMap == null)
      {
        Log.d("Index", "SearchIndexableResource '" + paramSearchIndexableResource.className + "' should implement the " + Indexable.class.getName() + " interface!");
        return;
      }
      paramMap = getSearchIndexProvider(paramMap);
    } while (paramMap == null);
    List localList = paramMap.getNonIndexableKeys(paramSearchIndexableResource.context);
    if ((localList != null) && (localList.size() > 0)) {
      localArrayList.addAll(localList);
    }
    indexFromProvider(this.mContext, paramSQLiteDatabase, paramString, paramMap, paramSearchIndexableResource.className, paramSearchIndexableResource.iconResId, paramSearchIndexableResource.rank, paramSearchIndexableResource.enabled, localArrayList);
  }
  
  private void indexOneSearchIndexableData(SQLiteDatabase paramSQLiteDatabase, String paramString, SearchIndexableData paramSearchIndexableData, Map<String, List<String>> paramMap)
  {
    if ((paramSearchIndexableData instanceof SearchIndexableResource)) {
      indexOneResource(paramSQLiteDatabase, paramString, (SearchIndexableResource)paramSearchIndexableData, paramMap);
    }
    while (!(paramSearchIndexableData instanceof SearchIndexableRaw)) {
      return;
    }
    indexOneRaw(paramSQLiteDatabase, paramString, (SearchIndexableRaw)paramSearchIndexableData);
  }
  
  private static boolean isIndexableClass(Class<?> paramClass)
  {
    if (paramClass != null) {
      return Indexable.class.isAssignableFrom(paramClass);
    }
    return false;
  }
  
  private boolean isPrivilegedPackage(String paramString)
  {
    boolean bool = false;
    PackageManager localPackageManager = this.mContext.getPackageManager();
    try
    {
      int i = localPackageManager.getPackageInfo(paramString, 0).applicationInfo.privateFlags;
      if ((i & 0x8) != 0) {
        bool = true;
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return false;
  }
  
  private boolean isWellKnownProvider(ResolveInfo paramResolveInfo)
  {
    String str2 = paramResolveInfo.providerInfo.authority;
    String str1 = paramResolveInfo.providerInfo.applicationInfo.packageName;
    if ((TextUtils.isEmpty(str2)) || (TextUtils.isEmpty(str1))) {
      return false;
    }
    str2 = paramResolveInfo.providerInfo.readPermission;
    paramResolveInfo = paramResolveInfo.providerInfo.writePermission;
    if ((TextUtils.isEmpty(str2)) || (TextUtils.isEmpty(paramResolveInfo))) {
      return false;
    }
    if (("android.permission.READ_SEARCH_INDEXABLES".equals(str2)) && ("android.permission.READ_SEARCH_INDEXABLES".equals(paramResolveInfo)))
    {
      if (("com.oneplus.opbackup".equals(str1)) || ("com.oneplus.security".equals(str1)) || ("com.oneplus.cloud".equals(str1))) {
        return true;
      }
    }
    else {
      return false;
    }
    return isPrivilegedPackage(str1);
  }
  
  private boolean manipulateUpgradedSoftwareInfo(List<String> paramList)
  {
    boolean bool = false;
    if (!IndexDatabaseHelper.isPackageNamesMatched(this.mContext, paramList)) {
      bool = true;
    }
    PackageManager localPackageManager = this.mContext.getPackageManager();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      String str = (String)paramList.next();
      try
      {
        PackageInfo localPackageInfo = localPackageManager.getPackageInfo(str, 0);
        if (!IndexDatabaseHelper.isVersionCodeMatched(this.mContext, str, String.valueOf(localPackageInfo.versionCode)))
        {
          Log.d("Index", "version mismatched: packageName " + str + ", versionName " + localPackageInfo.versionName + ", versionCode " + localPackageInfo.versionCode);
          bool = true;
        }
        this.mDataToProcess.versionInfo.put(str, String.valueOf(localPackageInfo.versionCode));
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        localNameNotFoundException.printStackTrace();
        Log.e("Index", "NameNotFoundException, package should be deleted on new SW");
        bool = true;
      }
    }
    return bool;
  }
  
  private static String normalizeHyphen(String paramString)
  {
    if (paramString != null) {
      return paramString.replaceAll("‑", "-");
    }
    return "";
  }
  
  private static String normalizeKeywords(String paramString)
  {
    if (paramString != null) {
      return paramString.replaceAll("[,]\\s*", " ");
    }
    return "";
  }
  
  private static String normalizeString(String paramString)
  {
    if (paramString != null) {}
    for (paramString = paramString.replaceAll("-", "");; paramString = "")
    {
      paramString = Normalizer.normalize(paramString, Normalizer.Form.NFD);
      return REMOVE_DIACRITICALS_PATTERN.matcher(paramString).replaceAll("").toLowerCase();
    }
  }
  
  private void updateFromRemoteProvider(String paramString1, String paramString2)
  {
    if (addIndexablesFromRemoteProvider(paramString1, paramString2)) {
      updateInternal();
    }
  }
  
  private void updateInternal()
  {
    synchronized (this.mDataToProcess)
    {
      new UpdateIndexTask(null).execute(new UpdateData[] { this.mDataToProcess.copy() });
      this.mDataToProcess.clear();
      return;
    }
  }
  
  private void updateOneRow(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, int paramInt1, int paramInt2, String paramString11, String paramString12, String paramString13, String paramString14, boolean paramBoolean, String paramString15, int paramInt3)
  {
    if (TextUtils.isEmpty(paramString2)) {
      return;
    }
    Object localObject = new StringBuilder(paramString2);
    ((StringBuilder)localObject).append(paramString10);
    ((StringBuilder)localObject).append(paramString1);
    int i = ((StringBuilder)localObject).toString().hashCode();
    localObject = new ContentValues();
    ((ContentValues)localObject).put("docid", Integer.valueOf(i));
    ((ContentValues)localObject).put("locale", paramString1);
    ((ContentValues)localObject).put("data_rank", Integer.valueOf(paramInt2));
    ((ContentValues)localObject).put("data_title", paramString2);
    ((ContentValues)localObject).put("data_title_normalized", paramString3);
    ((ContentValues)localObject).put("data_summary_on", paramString4);
    ((ContentValues)localObject).put("data_summary_on_normalized", paramString5);
    ((ContentValues)localObject).put("data_summary_off", paramString6);
    ((ContentValues)localObject).put("data_summary_off_normalized", paramString7);
    ((ContentValues)localObject).put("data_entries", paramString8);
    ((ContentValues)localObject).put("data_keywords", paramString11);
    ((ContentValues)localObject).put("class_name", paramString9);
    ((ContentValues)localObject).put("screen_title", paramString10);
    ((ContentValues)localObject).put("intent_action", paramString12);
    ((ContentValues)localObject).put("intent_target_package", paramString13);
    ((ContentValues)localObject).put("intent_target_class", paramString14);
    ((ContentValues)localObject).put("icon", Integer.valueOf(paramInt1));
    ((ContentValues)localObject).put("enabled", Boolean.valueOf(paramBoolean));
    ((ContentValues)localObject).put("data_key_reference", paramString15);
    ((ContentValues)localObject).put("user_id", Integer.valueOf(paramInt3));
    paramSQLiteDatabase.replaceOrThrow("prefs_index", null, (ContentValues)localObject);
  }
  
  private void updateOneRowWithFilteredData(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt1, int paramInt2, String paramString8, String paramString9, String paramString10, String paramString11, boolean paramBoolean, String paramString12, int paramInt3)
  {
    paramString2 = normalizeHyphen(paramString2);
    paramString3 = normalizeHyphen(paramString3);
    paramString4 = normalizeHyphen(paramString4);
    updateOneRow(paramSQLiteDatabase, paramString1, paramString2, normalizeString(paramString2), paramString3, normalizeString(paramString3), paramString4, normalizeString(paramString4), paramString5, paramString6, paramString7, paramInt1, paramInt2, normalizeKeywords(paramString8), paramString9, paramString10, paramString11, paramBoolean, paramString12, paramInt3);
  }
  
  public void addIndexableData(SearchIndexableData paramSearchIndexableData)
  {
    synchronized (this.mDataToProcess)
    {
      this.mDataToProcess.dataToUpdate.add(paramSearchIndexableData);
      return;
    }
  }
  
  public void addIndexableData(SearchIndexableResource[] paramArrayOfSearchIndexableResource)
  {
    synchronized (this.mDataToProcess)
    {
      int j = paramArrayOfSearchIndexableResource.length;
      int i = 0;
      while (i < j)
      {
        this.mDataToProcess.dataToUpdate.add(paramArrayOfSearchIndexableResource[i]);
        i += 1;
      }
      return;
    }
  }
  
  public void addNonIndexableKeys(String paramString, List<String> paramList)
  {
    synchronized (this.mDataToProcess)
    {
      this.mDataToProcess.nonIndexableKeys.put(paramString, paramList);
      return;
    }
  }
  
  public long addSavedQuery(String paramString)
  {
    SaveSearchQueryTask localSaveSearchQueryTask = new SaveSearchQueryTask(null);
    localSaveSearchQueryTask.execute(new String[] { paramString });
    try
    {
      long l = ((Long)localSaveSearchQueryTask.get()).longValue();
      return l;
    }
    catch (ExecutionException localExecutionException)
    {
      Log.e("Index", "Cannot insert saved query: " + paramString, localExecutionException);
      return -1L;
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.e("Index", "Cannot insert saved query: " + paramString, localInterruptedException);
    }
    return -1L;
  }
  
  public void deleteIndexableData(SearchIndexableData paramSearchIndexableData)
  {
    synchronized (this.mDataToProcess)
    {
      this.mDataToProcess.dataToDelete.add(paramSearchIndexableData);
      return;
    }
  }
  
  public Cursor getSuggestions(String paramString)
  {
    paramString = buildSuggestionsSQL(paramString);
    Log.d("Index", "Suggestions query: " + paramString);
    return getReadableDatabase().rawQuery(paramString, null);
  }
  
  public boolean isAvailable()
  {
    return this.mIsAvailable.get();
  }
  
  public Cursor search(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    String str = buildSearchSQL(paramString, MATCH_COLUMNS_PRIMARY, true);
    Log.d("Index", "Search primary query: " + str);
    Cursor localCursor = localSQLiteDatabase.rawQuery(str, null);
    paramString = new StringBuilder(buildSearchSQL(paramString, MATCH_COLUMNS_SECONDARY, false));
    paramString.append(" EXCEPT ");
    paramString.append(str);
    paramString = paramString.toString();
    Log.d("Index", "Search secondary query: " + paramString);
    long l1 = System.currentTimeMillis();
    paramString = localSQLiteDatabase.rawQuery(paramString, null);
    long l2 = System.currentTimeMillis();
    Log.d("Index", "search elapsed" + (l2 - l1) + " millis");
    return new MergeCursor(new Cursor[] { localCursor, paramString });
  }
  
  public void setContext(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public void update()
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        Object localObject1 = new Intent("android.content.action.SEARCH_INDEXABLES_PROVIDER");
        localObject1 = Index.-get1(Index.this).getPackageManager().queryIntentContentProviders((Intent)localObject1, 0);
        ArrayList localArrayList = new ArrayList();
        int j = ((List)localObject1).size();
        int i = 0;
        if (i < j)
        {
          Object localObject2 = (ResolveInfo)((List)localObject1).get(i);
          if (!Index.-wrap2(Index.this, (ResolveInfo)localObject2)) {}
          for (;;)
          {
            i += 1;
            break;
            String str = ((ResolveInfo)localObject2).providerInfo.authority;
            localObject2 = ((ResolveInfo)localObject2).providerInfo.packageName;
            localArrayList.add(localObject2);
            Index.-wrap1(Index.this, (String)localObject2, str);
            Index.-wrap3(Index.this, (String)localObject2, str);
          }
        }
        Index.-wrap4(Index.this, localArrayList);
        localArrayList.clear();
        Index.-get2(Index.this).fullIndex = true;
        Index.-wrap6(Index.this);
      }
    });
  }
  
  public void updateFromClassNameResource(String paramString, final boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("class name cannot be null!");
    }
    final SearchIndexableResource localSearchIndexableResource = SearchIndexableResources.getResourceByName(paramString);
    if (localSearchIndexableResource == null)
    {
      Log.e("Index", "Cannot find SearchIndexableResources for class name: " + paramString);
      return;
    }
    localSearchIndexableResource.context = this.mContext;
    localSearchIndexableResource.enabled = paramBoolean2;
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        if (paramBoolean1) {
          Index.this.deleteIndexableData(localSearchIndexableResource);
        }
        Index.this.addIndexableData(localSearchIndexableResource);
        Index.-get2(Index.this).forceUpdate = true;
        Index.-wrap6(Index.this);
        localSearchIndexableResource.enabled = false;
      }
    });
  }
  
  public void updateFromSearchIndexableData(final SearchIndexableData paramSearchIndexableData)
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        Index.this.addIndexableData(paramSearchIndexableData);
        Index.-get2(Index.this).forceUpdate = true;
        Index.-wrap6(Index.this);
      }
    });
  }
  
  private class SaveSearchQueryTask
    extends AsyncTask<String, Void, Long>
  {
    private SaveSearchQueryTask() {}
    
    protected Long doInBackground(String... paramVarArgs)
    {
      long l1 = new Date().getTime();
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("query", paramVarArgs[0]);
      localContentValues.put("timestamp", Long.valueOf(l1));
      SQLiteDatabase localSQLiteDatabase = Index.-wrap0(Index.this);
      if (localSQLiteDatabase == null)
      {
        Log.e("Index", "Cannot save Search queries as I cannot get a writable database");
        return Long.valueOf(-1L);
      }
      long l2 = -1L;
      l1 = l2;
      try
      {
        localSQLiteDatabase.delete("saved_queries", "query = ?", new String[] { paramVarArgs[0] });
        l1 = l2;
        l2 = localSQLiteDatabase.insertOrThrow("saved_queries", null, localContentValues);
        l1 = l2;
        long l3 = l2 - Index.-get0();
        l1 = l2;
        if (l3 > 0L)
        {
          l1 = l2;
          int i = localSQLiteDatabase.delete("saved_queries", "rowId <= ?", new String[] { Long.toString(l3) });
          l1 = l2;
          Log.d("Index", "Deleted '" + i + "' saved Search query(ies)");
          l1 = l2;
        }
      }
      catch (Exception paramVarArgs)
      {
        for (;;)
        {
          Log.d("Index", "Cannot update saved Search queries", paramVarArgs);
        }
      }
      return Long.valueOf(l1);
    }
  }
  
  private static class UpdateData
  {
    public List<SearchIndexableData> dataToDelete;
    public List<SearchIndexableData> dataToUpdate;
    public boolean forceUpdate = false;
    public boolean fullIndex = true;
    public Map<String, List<String>> nonIndexableKeys;
    boolean softwareUpgraded = false;
    public ArrayMap<String, String> versionInfo;
    
    public UpdateData()
    {
      this.dataToUpdate = new ArrayList();
      this.dataToDelete = new ArrayList();
      this.nonIndexableKeys = new HashMap();
      this.versionInfo = new ArrayMap();
    }
    
    public UpdateData(UpdateData paramUpdateData)
    {
      this.dataToUpdate = new ArrayList(paramUpdateData.dataToUpdate);
      this.dataToDelete = new ArrayList(paramUpdateData.dataToDelete);
      this.nonIndexableKeys = new HashMap(paramUpdateData.nonIndexableKeys);
      this.forceUpdate = paramUpdateData.forceUpdate;
      this.fullIndex = paramUpdateData.fullIndex;
      this.versionInfo = new ArrayMap(paramUpdateData.versionInfo);
      this.softwareUpgraded = paramUpdateData.softwareUpgraded;
    }
    
    public void clear()
    {
      this.dataToUpdate.clear();
      this.dataToDelete.clear();
      this.nonIndexableKeys.clear();
      this.forceUpdate = false;
      this.fullIndex = false;
      this.versionInfo.clear();
      this.softwareUpgraded = false;
    }
    
    public UpdateData copy()
    {
      return new UpdateData(this);
    }
  }
  
  private class UpdateIndexTask
    extends AsyncTask<Index.UpdateData, Integer, Void>
  {
    private UpdateIndexTask() {}
    
    private int delete(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2)
    {
      return paramSQLiteDatabase.delete("prefs_index", paramString1 + "=?", new String[] { paramString2 });
    }
    
    private boolean processDataToDelete(SQLiteDatabase paramSQLiteDatabase, String paramString, List<SearchIndexableData> paramList)
    {
      long l1 = System.currentTimeMillis();
      int j = paramList.size();
      int i = 0;
      if (i < j)
      {
        Object localObject = (SearchIndexableData)paramList.get(i);
        if (localObject == null) {}
        for (;;)
        {
          i += 1;
          break;
          if (!TextUtils.isEmpty(((SearchIndexableData)localObject).className))
          {
            delete(paramSQLiteDatabase, "class_name", ((SearchIndexableData)localObject).className);
          }
          else if ((localObject instanceof SearchIndexableRaw))
          {
            localObject = (SearchIndexableRaw)localObject;
            if (!TextUtils.isEmpty(((SearchIndexableRaw)localObject).title)) {
              delete(paramSQLiteDatabase, "data_title", ((SearchIndexableRaw)localObject).title);
            }
          }
        }
      }
      long l2 = System.currentTimeMillis();
      Log.d("Index", "Deleting data for locale '" + paramString + "' took " + (l2 - l1) + " millis");
      return false;
    }
    
    private boolean processDataToUpdate(SQLiteDatabase paramSQLiteDatabase, String paramString, List<SearchIndexableData> paramList, Map<String, List<String>> paramMap, boolean paramBoolean)
    {
      if ((!paramBoolean) && (IndexDatabaseHelper.isLocaleAlreadyIndexed(Index.-get1(Index.this), paramString)))
      {
        Log.d("Index", "Locale '" + paramString + "' is already indexed");
        return true;
      }
      long l1 = System.currentTimeMillis();
      int j = paramList.size();
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          SearchIndexableData localSearchIndexableData = (SearchIndexableData)paramList.get(i);
          try
          {
            Index.-wrap5(Index.this, paramSQLiteDatabase, paramString, localSearchIndexableData, paramMap);
            i += 1;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              StringBuilder localStringBuilder = new StringBuilder().append("Cannot index: ");
              Object localObject = localSearchIndexableData;
              if (localSearchIndexableData != null) {
                localObject = localSearchIndexableData.className;
              }
              Log.e("Index", localObject + " for locale: " + paramString, localException);
            }
          }
        }
      }
      long l2 = System.currentTimeMillis();
      Log.d("Index", "Indexing locale '" + paramString + "' took " + (l2 - l1) + " millis");
      return false;
    }
    
    /* Error */
    protected Void doInBackground(Index.UpdateData... paramVarArgs)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: getfield 156	com/android/settings/search/Index$UpdateData:dataToUpdate	Ljava/util/List;
      //   6: astore 6
      //   8: aload_1
      //   9: iconst_0
      //   10: aaload
      //   11: getfield 159	com/android/settings/search/Index$UpdateData:dataToDelete	Ljava/util/List;
      //   14: astore 7
      //   16: aload_1
      //   17: iconst_0
      //   18: aaload
      //   19: getfield 163	com/android/settings/search/Index$UpdateData:nonIndexableKeys	Ljava/util/Map;
      //   22: astore 8
      //   24: aload_1
      //   25: iconst_0
      //   26: aaload
      //   27: getfield 167	com/android/settings/search/Index$UpdateData:forceUpdate	Z
      //   30: istore_2
      //   31: aload_1
      //   32: iconst_0
      //   33: aaload
      //   34: getfield 170	com/android/settings/search/Index$UpdateData:fullIndex	Z
      //   37: istore 4
      //   39: aload_1
      //   40: iconst_0
      //   41: aaload
      //   42: getfield 173	com/android/settings/search/Index$UpdateData:softwareUpgraded	Z
      //   45: istore_3
      //   46: aload_1
      //   47: iconst_0
      //   48: aaload
      //   49: getfield 177	com/android/settings/search/Index$UpdateData:versionInfo	Landroid/util/ArrayMap;
      //   52: astore 5
      //   54: aload_0
      //   55: getfield 14	com/android/settings/search/Index$UpdateIndexTask:this$0	Lcom/android/settings/search/Index;
      //   58: invokestatic 181	com/android/settings/search/Index:-wrap0	(Lcom/android/settings/search/Index;)Landroid/database/sqlite/SQLiteDatabase;
      //   61: astore 9
      //   63: aload 9
      //   65: ifnonnull +13 -> 78
      //   68: ldc 88
      //   70: ldc -73
      //   72: invokestatic 185	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   75: pop
      //   76: aconst_null
      //   77: areturn
      //   78: aload_0
      //   79: getfield 14	com/android/settings/search/Index$UpdateIndexTask:this$0	Lcom/android/settings/search/Index;
      //   82: invokestatic 113	com/android/settings/search/Index:-get1	(Lcom/android/settings/search/Index;)Landroid/content/Context;
      //   85: invokevirtual 191	android/content/Context:getResources	()Landroid/content/res/Resources;
      //   88: invokevirtual 197	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
      //   91: getfield 203	android/content/res/Configuration:locale	Ljava/util/Locale;
      //   94: astore 10
      //   96: invokestatic 209	java/util/Locale:getDefault	()Ljava/util/Locale;
      //   99: invokevirtual 210	java/util/Locale:toString	()Ljava/lang/String;
      //   102: astore_1
      //   103: aload 10
      //   105: ifnull +9 -> 114
      //   108: aload 10
      //   110: invokevirtual 210	java/util/Locale:toString	()Ljava/lang/String;
      //   113: astore_1
      //   114: iload_2
      //   115: ifne +186 -> 301
      //   118: iload_3
      //   119: istore_2
      //   120: aload 9
      //   122: invokevirtual 213	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
      //   125: iload_3
      //   126: ifeq +15 -> 141
      //   129: aload_0
      //   130: getfield 14	com/android/settings/search/Index$UpdateIndexTask:this$0	Lcom/android/settings/search/Index;
      //   133: invokestatic 113	com/android/settings/search/Index:-get1	(Lcom/android/settings/search/Index;)Landroid/content/Context;
      //   136: aload 9
      //   138: invokestatic 217	com/android/settings/search/IndexDatabaseHelper:reconstructDB	(Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;)V
      //   141: aload 7
      //   143: invokeinterface 59 1 0
      //   148: ifle +13 -> 161
      //   151: aload_0
      //   152: aload 9
      //   154: aload_1
      //   155: aload 7
      //   157: invokespecial 219	com/android/settings/search/Index$UpdateIndexTask:processDataToDelete	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/util/List;)Z
      //   160: pop
      //   161: aload 6
      //   163: invokeinterface 59 1 0
      //   168: ifle +16 -> 184
      //   171: aload_0
      //   172: aload 9
      //   174: aload_1
      //   175: aload 6
      //   177: aload 8
      //   179: iload_2
      //   180: invokespecial 221	com/android/settings/search/Index$UpdateIndexTask:processDataToUpdate	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/util/List;Ljava/util/Map;Z)Z
      //   183: pop
      //   184: aload 9
      //   186: invokevirtual 224	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
      //   189: aload 9
      //   191: invokevirtual 227	android/database/sqlite/SQLiteDatabase:endTransaction	()V
      //   194: iload 4
      //   196: ifeq +14 -> 210
      //   199: aload_0
      //   200: getfield 14	com/android/settings/search/Index$UpdateIndexTask:this$0	Lcom/android/settings/search/Index;
      //   203: invokestatic 113	com/android/settings/search/Index:-get1	(Lcom/android/settings/search/Index;)Landroid/content/Context;
      //   206: aload_1
      //   207: invokestatic 231	com/android/settings/search/IndexDatabaseHelper:setLocaleIndexed	(Landroid/content/Context;Ljava/lang/String;)V
      //   210: iload_3
      //   211: ifeq +88 -> 299
      //   214: ldc 88
      //   216: ldc -23
      //   218: invokestatic 103	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   221: pop
      //   222: aload 5
      //   224: invokeinterface 239 1 0
      //   229: invokeinterface 245 1 0
      //   234: astore_1
      //   235: aload_1
      //   236: invokeinterface 251 1 0
      //   241: ifeq +73 -> 314
      //   244: aload_1
      //   245: invokeinterface 255 1 0
      //   250: checkcast 257	java/util/Map$Entry
      //   253: astore 6
      //   255: aload_0
      //   256: getfield 14	com/android/settings/search/Index$UpdateIndexTask:this$0	Lcom/android/settings/search/Index;
      //   259: invokestatic 113	com/android/settings/search/Index:-get1	(Lcom/android/settings/search/Index;)Landroid/content/Context;
      //   262: aload 6
      //   264: invokeinterface 260 1 0
      //   269: checkcast 40	java/lang/String
      //   272: aload 6
      //   274: invokeinterface 263 1 0
      //   279: checkcast 40	java/lang/String
      //   282: invokestatic 267	com/android/settings/search/IndexDatabaseHelper:setAppVersionCode	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V
      //   285: goto -50 -> 235
      //   288: astore_1
      //   289: ldc 88
      //   291: ldc_w 269
      //   294: aload_1
      //   295: invokestatic 138	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   298: pop
      //   299: aconst_null
      //   300: areturn
      //   301: iconst_1
      //   302: istore_2
      //   303: goto -183 -> 120
      //   306: astore_1
      //   307: aload 9
      //   309: invokevirtual 227	android/database/sqlite/SQLiteDatabase:endTransaction	()V
      //   312: aload_1
      //   313: athrow
      //   314: aload 5
      //   316: invokeinterface 272 1 0
      //   321: goto -22 -> 299
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	324	0	this	UpdateIndexTask
      //   0	324	1	paramVarArgs	Index.UpdateData[]
      //   30	273	2	bool1	boolean
      //   45	166	3	bool2	boolean
      //   37	158	4	bool3	boolean
      //   52	263	5	localArrayMap	ArrayMap
      //   6	267	6	localObject	Object
      //   14	142	7	localList	List
      //   22	156	8	localMap	Map
      //   61	247	9	localSQLiteDatabase	SQLiteDatabase
      //   94	15	10	localLocale	Locale
      // Exception table:
      //   from	to	target	type
      //   0	63	288	android/database/sqlite/SQLiteFullException
      //   68	76	288	android/database/sqlite/SQLiteFullException
      //   78	103	288	android/database/sqlite/SQLiteFullException
      //   108	114	288	android/database/sqlite/SQLiteFullException
      //   189	194	288	android/database/sqlite/SQLiteFullException
      //   199	210	288	android/database/sqlite/SQLiteFullException
      //   214	235	288	android/database/sqlite/SQLiteFullException
      //   235	285	288	android/database/sqlite/SQLiteFullException
      //   307	314	288	android/database/sqlite/SQLiteFullException
      //   314	321	288	android/database/sqlite/SQLiteFullException
      //   120	125	306	finally
      //   129	141	306	finally
      //   141	161	306	finally
      //   161	184	306	finally
      //   184	189	306	finally
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      super.onPostExecute(paramVoid);
      Index.-get3(Index.this).set(true);
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      Index.-get3(Index.this).set(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\Index.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */