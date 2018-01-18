package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryAddWordContents
{
  public static final String EXTRA_LOCALE = "locale";
  public static final String EXTRA_MODE = "mode";
  public static final String EXTRA_ORIGINAL_SHORTCUT = "originalShortcut";
  public static final String EXTRA_ORIGINAL_WORD = "originalWord";
  public static final String EXTRA_SHORTCUT = "shortcut";
  public static final String EXTRA_WORD = "word";
  private static final int FREQUENCY_FOR_USER_DICTIONARY_ADDS = 250;
  private static final String[] HAS_WORD_PROJECTION = { "word" };
  private static final String HAS_WORD_SELECTION_ALL_LOCALES = "word=? AND locale is null";
  private static final String HAS_WORD_SELECTION_ONE_LOCALE = "word=? AND locale=?";
  public static final int MODE_EDIT = 0;
  public static final int MODE_INSERT = 1;
  private String mLocale;
  private final int mMode;
  private final String mOldShortcut;
  private final String mOldWord;
  private String mSavedShortcut;
  private String mSavedWord;
  private final EditText mShortcutEditText;
  private final EditText mWordEditText;
  
  UserDictionaryAddWordContents(View paramView, Bundle paramBundle)
  {
    this.mWordEditText = ((EditText)paramView.findViewById(2131362671));
    this.mShortcutEditText = ((EditText)paramView.findViewById(2131362674));
    paramView = paramBundle.getString("word");
    if (paramView != null)
    {
      this.mWordEditText.setText(paramView);
      this.mWordEditText.setSelection(this.mWordEditText.getText().length());
    }
    paramView = paramBundle.getString("shortcut");
    if ((paramView != null) && (this.mShortcutEditText != null)) {
      this.mShortcutEditText.setText(paramView);
    }
    this.mMode = paramBundle.getInt("mode");
    this.mOldWord = paramBundle.getString("word");
    this.mOldShortcut = paramBundle.getString("shortcut");
    updateLocale(paramBundle.getString("locale"));
  }
  
  UserDictionaryAddWordContents(View paramView, UserDictionaryAddWordContents paramUserDictionaryAddWordContents)
  {
    this.mWordEditText = ((EditText)paramView.findViewById(2131362671));
    this.mShortcutEditText = ((EditText)paramView.findViewById(2131362674));
    this.mMode = 0;
    this.mOldWord = paramUserDictionaryAddWordContents.mSavedWord;
    this.mOldShortcut = paramUserDictionaryAddWordContents.mSavedShortcut;
    updateLocale(paramUserDictionaryAddWordContents.getCurrentUserDictionaryLocale());
  }
  
  private static void addLocaleDisplayNameToList(Context paramContext, ArrayList<LocaleRenderer> paramArrayList, String paramString)
  {
    if (paramString != null) {
      paramArrayList.add(new LocaleRenderer(paramContext, paramString));
    }
  }
  
  /* Error */
  private boolean hasWord(String paramString, Context paramContext)
  {
    // Byte code:
    //   0: ldc -117
    //   2: aload_0
    //   3: getfield 141	com/android/settings/inputmethod/UserDictionaryAddWordContents:mLocale	Ljava/lang/String;
    //   6: invokevirtual 144	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   9: ifeq +44 -> 53
    //   12: aload_2
    //   13: invokevirtual 150	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   16: getstatic 156	android/provider/UserDictionary$Words:CONTENT_URI	Landroid/net/Uri;
    //   19: getstatic 56	com/android/settings/inputmethod/UserDictionaryAddWordContents:HAS_WORD_PROJECTION	[Ljava/lang/String;
    //   22: ldc 34
    //   24: iconst_1
    //   25: anewarray 54	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: aload_1
    //   31: aastore
    //   32: aconst_null
    //   33: invokevirtual 162	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   36: astore_1
    //   37: aload_1
    //   38: ifnonnull +50 -> 88
    //   41: aload_1
    //   42: ifnull +9 -> 51
    //   45: aload_1
    //   46: invokeinterface 167 1 0
    //   51: iconst_0
    //   52: ireturn
    //   53: aload_2
    //   54: invokevirtual 150	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   57: getstatic 156	android/provider/UserDictionary$Words:CONTENT_URI	Landroid/net/Uri;
    //   60: getstatic 56	com/android/settings/inputmethod/UserDictionaryAddWordContents:HAS_WORD_PROJECTION	[Ljava/lang/String;
    //   63: ldc 37
    //   65: iconst_2
    //   66: anewarray 54	java/lang/String
    //   69: dup
    //   70: iconst_0
    //   71: aload_1
    //   72: aastore
    //   73: dup
    //   74: iconst_1
    //   75: aload_0
    //   76: getfield 141	com/android/settings/inputmethod/UserDictionaryAddWordContents:mLocale	Ljava/lang/String;
    //   79: aastore
    //   80: aconst_null
    //   81: invokevirtual 162	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   84: astore_1
    //   85: goto -48 -> 37
    //   88: aload_1
    //   89: invokeinterface 170 1 0
    //   94: istore_3
    //   95: iload_3
    //   96: ifle +19 -> 115
    //   99: iconst_1
    //   100: istore 4
    //   102: aload_1
    //   103: ifnull +9 -> 112
    //   106: aload_1
    //   107: invokeinterface 167 1 0
    //   112: iload 4
    //   114: ireturn
    //   115: iconst_0
    //   116: istore 4
    //   118: goto -16 -> 102
    //   121: astore_2
    //   122: aload_1
    //   123: ifnull +9 -> 132
    //   126: aload_1
    //   127: invokeinterface 167 1 0
    //   132: aload_2
    //   133: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	UserDictionaryAddWordContents
    //   0	134	1	paramString	String
    //   0	134	2	paramContext	Context
    //   94	2	3	i	int
    //   100	17	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   88	95	121	finally
  }
  
  int apply(Context paramContext, Bundle paramBundle)
  {
    Locale localLocale = null;
    if (paramBundle != null) {
      saveStateIntoBundle(paramBundle);
    }
    Object localObject = paramContext.getContentResolver();
    String str;
    if ((this.mMode != 0) || (TextUtils.isEmpty(this.mOldWord)))
    {
      str = this.mWordEditText.getText().toString();
      if (this.mShortcutEditText != null) {
        break label83;
      }
      paramBundle = null;
    }
    for (;;)
    {
      if (!TextUtils.isEmpty(str)) {
        break label111;
      }
      return 1;
      UserDictionarySettings.deleteWord(this.mOldWord, this.mOldShortcut, (ContentResolver)localObject);
      break;
      label83:
      paramBundle = this.mShortcutEditText.getText().toString();
      if (TextUtils.isEmpty(paramBundle)) {
        paramBundle = null;
      }
    }
    label111:
    this.mSavedWord = str;
    this.mSavedShortcut = paramBundle;
    if ((TextUtils.isEmpty(paramBundle)) && (hasWord(str, paramContext))) {
      return 2;
    }
    UserDictionarySettings.deleteWord(str, null, (ContentResolver)localObject);
    if (!TextUtils.isEmpty(paramBundle)) {
      UserDictionarySettings.deleteWord(str, paramBundle, (ContentResolver)localObject);
    }
    localObject = str.toString();
    if (TextUtils.isEmpty(this.mLocale)) {}
    for (;;)
    {
      UserDictionary.Words.addWord(paramContext, (String)localObject, 250, paramBundle, localLocale);
      return 0;
      localLocale = Utils.createLocaleFromString(this.mLocale);
    }
  }
  
  void delete(Context paramContext)
  {
    if ((this.mMode != 0) || (TextUtils.isEmpty(this.mOldWord))) {
      return;
    }
    paramContext = paramContext.getContentResolver();
    UserDictionarySettings.deleteWord(this.mOldWord, this.mOldShortcut, paramContext);
  }
  
  public String getCurrentUserDictionaryLocale()
  {
    return this.mLocale;
  }
  
  public ArrayList<LocaleRenderer> getLocalesList(Activity paramActivity)
  {
    Object localObject = UserDictionaryList.getUserDictionaryLocalesSet(paramActivity);
    ((TreeSet)localObject).remove(this.mLocale);
    String str = Locale.getDefault().toString();
    ((TreeSet)localObject).remove(str);
    ((TreeSet)localObject).remove("");
    ArrayList localArrayList = new ArrayList();
    addLocaleDisplayNameToList(paramActivity, localArrayList, this.mLocale);
    if (!str.equals(this.mLocale)) {
      addLocaleDisplayNameToList(paramActivity, localArrayList, str);
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      addLocaleDisplayNameToList(paramActivity, localArrayList, (String)((Iterator)localObject).next());
    }
    if (!"".equals(this.mLocale)) {
      addLocaleDisplayNameToList(paramActivity, localArrayList, "");
    }
    localArrayList.add(new LocaleRenderer(paramActivity, null));
    return localArrayList;
  }
  
  void saveStateIntoBundle(Bundle paramBundle)
  {
    paramBundle.putString("word", this.mWordEditText.getText().toString());
    paramBundle.putString("originalWord", this.mOldWord);
    if (this.mShortcutEditText != null) {
      paramBundle.putString("shortcut", this.mShortcutEditText.getText().toString());
    }
    if (this.mOldShortcut != null) {
      paramBundle.putString("originalShortcut", this.mOldShortcut);
    }
    paramBundle.putString("locale", this.mLocale);
  }
  
  void updateLocale(String paramString)
  {
    String str = paramString;
    if (paramString == null) {
      str = Locale.getDefault().toString();
    }
    this.mLocale = str;
  }
  
  public static class LocaleRenderer
  {
    private final String mDescription;
    private final String mLocaleString;
    
    public LocaleRenderer(Context paramContext, String paramString)
    {
      this.mLocaleString = paramString;
      if (paramString == null)
      {
        this.mDescription = paramContext.getString(2131692281);
        return;
      }
      if ("".equals(paramString))
      {
        this.mDescription = paramContext.getString(2131692280);
        return;
      }
      this.mDescription = Utils.createLocaleFromString(paramString).getDisplayName();
    }
    
    public String getLocaleString()
    {
      return this.mLocaleString;
    }
    
    public boolean isMoreLanguages()
    {
      return this.mLocaleString == null;
    }
    
    public String toString()
    {
      return this.mDescription;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionaryAddWordContents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */