package com.oneplus.lib.preference;

import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.bool;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.styleable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PreferenceActivity
  extends ListActivity
  implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragment.OnPreferenceStartFragmentCallback
{
  private static final String BACK_STACK_PREFS = ":android:prefs";
  private static final String CUR_HEADER_TAG = ":android:cur_header";
  public static final String EXTRA_NO_HEADERS = ":android:no_headers";
  private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
  private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
  private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
  private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
  public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
  public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";
  public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";
  public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";
  private static final int FIRST_REQUEST_CODE = 100;
  private static final String HEADERS_TAG = ":android:headers";
  public static final long HEADER_ID_UNDEFINED = -1L;
  private static final int MSG_BIND_PREFERENCES = 1;
  private static final int MSG_BUILD_HEADERS = 2;
  private static final String PREFERENCES_TAG = ":android:preferences";
  private static final String TAG = "PreferenceActivity";
  private Header mCurHeader;
  private FragmentBreadCrumbs mFragmentBreadCrumbs;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        do
        {
          Object localObject;
          do
          {
            return;
            PreferenceActivity.-wrap0(PreferenceActivity.this);
            return;
            localObject = new ArrayList(PreferenceActivity.-get1(PreferenceActivity.this));
            PreferenceActivity.-get1(PreferenceActivity.this).clear();
            PreferenceActivity.this.onBuildHeaders(PreferenceActivity.-get1(PreferenceActivity.this));
            if ((PreferenceActivity.this.getListAdapter() instanceof BaseAdapter)) {
              ((BaseAdapter)PreferenceActivity.this.getListAdapter()).notifyDataSetChanged();
            }
            paramAnonymousMessage = PreferenceActivity.this.onGetNewHeader();
            if ((paramAnonymousMessage == null) || (paramAnonymousMessage.fragment == null)) {
              break;
            }
            localObject = PreferenceActivity.this.findBestMatchingHeader(paramAnonymousMessage, (ArrayList)localObject);
          } while ((localObject != null) && (PreferenceActivity.-get0(PreferenceActivity.this) == localObject));
          PreferenceActivity.this.switchToHeader(paramAnonymousMessage);
          return;
        } while (PreferenceActivity.-get0(PreferenceActivity.this) == null);
        paramAnonymousMessage = PreferenceActivity.this.findBestMatchingHeader(PreferenceActivity.-get0(PreferenceActivity.this), PreferenceActivity.-get1(PreferenceActivity.this));
      } while (paramAnonymousMessage == null);
      PreferenceActivity.this.setSelectedHeader(paramAnonymousMessage);
    }
  };
  private final ArrayList<Header> mHeaders = new ArrayList();
  private FrameLayout mListFooter;
  private Button mNextButton;
  private int mPreferenceHeaderItemResId = 0;
  private boolean mPreferenceHeaderRemoveEmptyIcon = false;
  private PreferenceManager mPreferenceManager;
  private ViewGroup mPrefsContainer;
  private Bundle mSavedInstanceState;
  private boolean mSinglePane;
  
  private void bindPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null)
    {
      localPreferenceScreen.bind(getListView());
      if (this.mSavedInstanceState != null)
      {
        super.onRestoreInstanceState(this.mSavedInstanceState);
        this.mSavedInstanceState = null;
      }
    }
  }
  
  private void postBindPreferences()
  {
    if (this.mHandler.hasMessages(1)) {
      return;
    }
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  private void requirePreferenceManager()
  {
    if (this.mPreferenceManager == null)
    {
      if (getListAdapter() == null) {
        throw new RuntimeException("This should be called after super.onCreate.");
      }
      throw new RuntimeException("Modern two-pane PreferenceActivity requires use of a PreferenceFragment");
    }
  }
  
  private void switchToHeaderInner(String paramString, Bundle paramBundle)
  {
    getFragmentManager().popBackStack(":android:prefs", 1);
    if (!isValidFragment(paramString)) {
      throw new IllegalArgumentException("Invalid fragment for this activity: " + paramString);
    }
    paramString = Fragment.instantiate(this, paramString, paramBundle);
    paramBundle = getFragmentManager().beginTransaction();
    paramBundle.setTransition(4099);
    paramBundle.replace(R.id.prefs, paramString);
    paramBundle.commitAllowingStateLoss();
  }
  
  @Deprecated
  public void addPreferencesFromIntent(Intent paramIntent)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromIntent(paramIntent, getPreferenceScreen()));
  }
  
  @Deprecated
  public void addPreferencesFromResource(int paramInt)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromResource(this, paramInt, getPreferenceScreen()));
  }
  
  Header findBestMatchingHeader(Header paramHeader, ArrayList<Header> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Header localHeader;
    int j;
    if (i < paramArrayList.size())
    {
      localHeader = (Header)paramArrayList.get(i);
      if ((paramHeader == localHeader) || ((paramHeader.id != -1L) && (paramHeader.id == localHeader.id)))
      {
        localArrayList.clear();
        localArrayList.add(localHeader);
      }
    }
    else
    {
      j = localArrayList.size();
      if (j != 1) {
        break label198;
      }
      return (Header)localArrayList.get(0);
    }
    if (paramHeader.fragment != null) {
      if (paramHeader.fragment.equals(localHeader.fragment)) {
        localArrayList.add(localHeader);
      }
    }
    for (;;)
    {
      i += 1;
      break;
      if (paramHeader.intent != null)
      {
        if (paramHeader.intent.equals(localHeader.intent)) {
          localArrayList.add(localHeader);
        }
      }
      else if ((paramHeader.title != null) && (paramHeader.title.equals(localHeader.title))) {
        localArrayList.add(localHeader);
      }
    }
    label198:
    if (j > 1)
    {
      i = 0;
      while (i < j)
      {
        paramArrayList = (Header)localArrayList.get(i);
        if ((paramHeader.fragmentArguments != null) && (paramHeader.fragmentArguments.equals(paramArrayList.fragmentArguments))) {
          return paramArrayList;
        }
        if ((paramHeader.extras != null) && (paramHeader.extras.equals(paramArrayList.extras))) {
          return paramArrayList;
        }
        if ((paramHeader.title != null) && (paramHeader.title.equals(paramArrayList.title))) {
          return paramArrayList;
        }
        i += 1;
      }
    }
    return null;
  }
  
  @Deprecated
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramCharSequence);
  }
  
  public void finishPreferencePanel(Fragment paramFragment, int paramInt, Intent paramIntent)
  {
    if (this.mSinglePane)
    {
      setResult(paramInt, paramIntent);
      finish();
    }
    do
    {
      return;
      onBackPressed();
    } while ((paramFragment == null) || (paramFragment.getTargetFragment() == null));
    paramFragment.getTargetFragment().onActivityResult(paramFragment.getTargetRequestCode(), paramInt, paramIntent);
  }
  
  public List<Header> getHeaders()
  {
    return this.mHeaders;
  }
  
  protected Button getNextButton()
  {
    return this.mNextButton;
  }
  
  @Deprecated
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  @Deprecated
  public PreferenceScreen getPreferenceScreen()
  {
    if (this.mPreferenceManager != null) {
      return this.mPreferenceManager.getPreferenceScreen();
    }
    return null;
  }
  
  public boolean hasHeaders()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (getListView().getVisibility() == 0)
    {
      bool1 = bool2;
      if (this.mPreferenceManager == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean hasNextButton()
  {
    return this.mNextButton != null;
  }
  
  public void invalidateHeaders()
  {
    if (!this.mHandler.hasMessages(2)) {
      this.mHandler.sendEmptyMessage(2);
    }
  }
  
  public boolean isMultiPane()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (hasHeaders())
    {
      bool1 = bool2;
      if (this.mPrefsContainer.getVisibility() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    if (getApplicationInfo().targetSdkVersion >= 19) {
      throw new RuntimeException("Subclasses of PreferenceActivity must override isValidFragment(String) to verify that the Fragment class is valid! " + getClass().getName() + " has not checked if fragment " + paramString + " is valid.");
    }
    return true;
  }
  
  /* Error */
  public void loadHeadersFromResource(int paramInt, List<Header> paramList)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 6
    //   9: aload_0
    //   10: invokevirtual 413	com/oneplus/lib/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   13: iload_1
    //   14: invokevirtual 419	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   17: astore 7
    //   19: aload 7
    //   21: astore 6
    //   23: aload 7
    //   25: astore 5
    //   27: aload 7
    //   29: astore 8
    //   31: aload 7
    //   33: invokestatic 425	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   36: astore 11
    //   38: aload 7
    //   40: astore 6
    //   42: aload 7
    //   44: astore 5
    //   46: aload 7
    //   48: astore 8
    //   50: aload 7
    //   52: invokeinterface 430 1 0
    //   57: istore_1
    //   58: iload_1
    //   59: iconst_1
    //   60: if_icmpeq +8 -> 68
    //   63: iload_1
    //   64: iconst_2
    //   65: if_icmpne -27 -> 38
    //   68: aload 7
    //   70: astore 6
    //   72: aload 7
    //   74: astore 5
    //   76: aload 7
    //   78: astore 8
    //   80: aload 7
    //   82: invokeinterface 431 1 0
    //   87: astore 9
    //   89: aload 7
    //   91: astore 6
    //   93: aload 7
    //   95: astore 5
    //   97: aload 7
    //   99: astore 8
    //   101: ldc_w 433
    //   104: aload 9
    //   106: invokevirtual 295	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   109: ifne +92 -> 201
    //   112: aload 7
    //   114: astore 6
    //   116: aload 7
    //   118: astore 5
    //   120: aload 7
    //   122: astore 8
    //   124: new 178	java/lang/RuntimeException
    //   127: dup
    //   128: new 205	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 206	java/lang/StringBuilder:<init>	()V
    //   135: ldc_w 435
    //   138: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload 9
    //   143: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: ldc_w 437
    //   149: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload 7
    //   154: invokeinterface 440 1 0
    //   159: invokevirtual 212	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   165: invokespecial 183	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   168: athrow
    //   169: astore_2
    //   170: aload 6
    //   172: astore 5
    //   174: new 178	java/lang/RuntimeException
    //   177: dup
    //   178: ldc_w 442
    //   181: aload_2
    //   182: invokespecial 445	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   185: athrow
    //   186: astore_2
    //   187: aload 5
    //   189: ifnull +10 -> 199
    //   192: aload 5
    //   194: invokeinterface 448 1 0
    //   199: aload_2
    //   200: athrow
    //   201: aconst_null
    //   202: astore 9
    //   204: aload 7
    //   206: astore 6
    //   208: aload 7
    //   210: astore 5
    //   212: aload 7
    //   214: astore 8
    //   216: aload 7
    //   218: invokeinterface 451 1 0
    //   223: istore_1
    //   224: aload 7
    //   226: astore 6
    //   228: aload 7
    //   230: astore 5
    //   232: aload 7
    //   234: astore 8
    //   236: aload 7
    //   238: invokeinterface 430 1 0
    //   243: istore_3
    //   244: iload_3
    //   245: iconst_1
    //   246: if_icmpeq +1057 -> 1303
    //   249: iload_3
    //   250: iconst_3
    //   251: if_icmpne +26 -> 277
    //   254: aload 7
    //   256: astore 6
    //   258: aload 7
    //   260: astore 5
    //   262: aload 7
    //   264: astore 8
    //   266: aload 7
    //   268: invokeinterface 451 1 0
    //   273: iload_1
    //   274: if_icmple +1029 -> 1303
    //   277: iload_3
    //   278: iconst_3
    //   279: if_icmpeq -55 -> 224
    //   282: iload_3
    //   283: iconst_4
    //   284: if_icmpeq -60 -> 224
    //   287: aload 7
    //   289: astore 6
    //   291: aload 7
    //   293: astore 5
    //   295: aload 7
    //   297: astore 8
    //   299: ldc_w 453
    //   302: aload 7
    //   304: invokeinterface 431 1 0
    //   309: invokevirtual 295	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   312: ifeq +971 -> 1283
    //   315: aload 7
    //   317: astore 6
    //   319: aload 7
    //   321: astore 5
    //   323: aload 7
    //   325: astore 8
    //   327: new 18	com/oneplus/lib/preference/PreferenceActivity$Header
    //   330: dup
    //   331: invokespecial 454	com/oneplus/lib/preference/PreferenceActivity$Header:<init>	()V
    //   334: astore 12
    //   336: aload 7
    //   338: astore 6
    //   340: aload 7
    //   342: astore 5
    //   344: aload 7
    //   346: astore 8
    //   348: aload_0
    //   349: aload 11
    //   351: getstatic 460	com/oneplus/commonctrl/R$styleable:PreferenceHeader	[I
    //   354: invokevirtual 464	com/oneplus/lib/preference/PreferenceActivity:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   357: astore 10
    //   359: aload 7
    //   361: astore 6
    //   363: aload 7
    //   365: astore 5
    //   367: aload 7
    //   369: astore 8
    //   371: aload 12
    //   373: aload 10
    //   375: getstatic 467	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_id	I
    //   378: iconst_m1
    //   379: invokevirtual 473	android/content/res/TypedArray:getResourceId	(II)I
    //   382: i2l
    //   383: putfield 280	com/oneplus/lib/preference/PreferenceActivity$Header:id	J
    //   386: aload 7
    //   388: astore 6
    //   390: aload 7
    //   392: astore 5
    //   394: aload 7
    //   396: astore 8
    //   398: aload 10
    //   400: getstatic 476	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_title	I
    //   403: invokevirtual 480	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   406: astore 13
    //   408: aload 13
    //   410: ifnull +66 -> 476
    //   413: aload 7
    //   415: astore 6
    //   417: aload 7
    //   419: astore 5
    //   421: aload 7
    //   423: astore 8
    //   425: aload 13
    //   427: getfield 485	android/util/TypedValue:type	I
    //   430: iconst_3
    //   431: if_icmpne +45 -> 476
    //   434: aload 7
    //   436: astore 6
    //   438: aload 7
    //   440: astore 5
    //   442: aload 7
    //   444: astore 8
    //   446: aload 13
    //   448: getfield 488	android/util/TypedValue:resourceId	I
    //   451: ifeq +588 -> 1039
    //   454: aload 7
    //   456: astore 6
    //   458: aload 7
    //   460: astore 5
    //   462: aload 7
    //   464: astore 8
    //   466: aload 12
    //   468: aload 13
    //   470: getfield 488	android/util/TypedValue:resourceId	I
    //   473: putfield 491	com/oneplus/lib/preference/PreferenceActivity$Header:titleRes	I
    //   476: aload 7
    //   478: astore 6
    //   480: aload 7
    //   482: astore 5
    //   484: aload 7
    //   486: astore 8
    //   488: aload 10
    //   490: getstatic 494	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_summary	I
    //   493: invokevirtual 480	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   496: astore 13
    //   498: aload 13
    //   500: ifnull +66 -> 566
    //   503: aload 7
    //   505: astore 6
    //   507: aload 7
    //   509: astore 5
    //   511: aload 7
    //   513: astore 8
    //   515: aload 13
    //   517: getfield 485	android/util/TypedValue:type	I
    //   520: iconst_3
    //   521: if_icmpne +45 -> 566
    //   524: aload 7
    //   526: astore 6
    //   528: aload 7
    //   530: astore 5
    //   532: aload 7
    //   534: astore 8
    //   536: aload 13
    //   538: getfield 488	android/util/TypedValue:resourceId	I
    //   541: ifeq +523 -> 1064
    //   544: aload 7
    //   546: astore 6
    //   548: aload 7
    //   550: astore 5
    //   552: aload 7
    //   554: astore 8
    //   556: aload 12
    //   558: aload 13
    //   560: getfield 488	android/util/TypedValue:resourceId	I
    //   563: putfield 497	com/oneplus/lib/preference/PreferenceActivity$Header:summaryRes	I
    //   566: aload 7
    //   568: astore 6
    //   570: aload 7
    //   572: astore 5
    //   574: aload 7
    //   576: astore 8
    //   578: aload 10
    //   580: getstatic 500	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_breadCrumbTitle	I
    //   583: invokevirtual 480	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   586: astore 13
    //   588: aload 13
    //   590: ifnull +66 -> 656
    //   593: aload 7
    //   595: astore 6
    //   597: aload 7
    //   599: astore 5
    //   601: aload 7
    //   603: astore 8
    //   605: aload 13
    //   607: getfield 485	android/util/TypedValue:type	I
    //   610: iconst_3
    //   611: if_icmpne +45 -> 656
    //   614: aload 7
    //   616: astore 6
    //   618: aload 7
    //   620: astore 5
    //   622: aload 7
    //   624: astore 8
    //   626: aload 13
    //   628: getfield 488	android/util/TypedValue:resourceId	I
    //   631: ifeq +458 -> 1089
    //   634: aload 7
    //   636: astore 6
    //   638: aload 7
    //   640: astore 5
    //   642: aload 7
    //   644: astore 8
    //   646: aload 12
    //   648: aload 13
    //   650: getfield 488	android/util/TypedValue:resourceId	I
    //   653: putfield 503	com/oneplus/lib/preference/PreferenceActivity$Header:breadCrumbTitleRes	I
    //   656: aload 7
    //   658: astore 6
    //   660: aload 7
    //   662: astore 5
    //   664: aload 7
    //   666: astore 8
    //   668: aload 10
    //   670: getstatic 506	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_breadCrumbShortTitle	I
    //   673: invokevirtual 480	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   676: astore 13
    //   678: aload 13
    //   680: ifnull +66 -> 746
    //   683: aload 7
    //   685: astore 6
    //   687: aload 7
    //   689: astore 5
    //   691: aload 7
    //   693: astore 8
    //   695: aload 13
    //   697: getfield 485	android/util/TypedValue:type	I
    //   700: iconst_3
    //   701: if_icmpne +45 -> 746
    //   704: aload 7
    //   706: astore 6
    //   708: aload 7
    //   710: astore 5
    //   712: aload 7
    //   714: astore 8
    //   716: aload 13
    //   718: getfield 488	android/util/TypedValue:resourceId	I
    //   721: ifeq +393 -> 1114
    //   724: aload 7
    //   726: astore 6
    //   728: aload 7
    //   730: astore 5
    //   732: aload 7
    //   734: astore 8
    //   736: aload 12
    //   738: aload 13
    //   740: getfield 488	android/util/TypedValue:resourceId	I
    //   743: putfield 509	com/oneplus/lib/preference/PreferenceActivity$Header:breadCrumbShortTitleRes	I
    //   746: aload 7
    //   748: astore 6
    //   750: aload 7
    //   752: astore 5
    //   754: aload 7
    //   756: astore 8
    //   758: aload 12
    //   760: aload 10
    //   762: getstatic 512	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_icon	I
    //   765: iconst_0
    //   766: invokevirtual 473	android/content/res/TypedArray:getResourceId	(II)I
    //   769: putfield 515	com/oneplus/lib/preference/PreferenceActivity$Header:iconRes	I
    //   772: aload 7
    //   774: astore 6
    //   776: aload 7
    //   778: astore 5
    //   780: aload 7
    //   782: astore 8
    //   784: aload 12
    //   786: aload 10
    //   788: getstatic 518	com/oneplus/commonctrl/R$styleable:PreferenceHeader_android_fragment	I
    //   791: invokevirtual 522	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   794: putfield 290	com/oneplus/lib/preference/PreferenceActivity$Header:fragment	Ljava/lang/String;
    //   797: aload 7
    //   799: astore 6
    //   801: aload 7
    //   803: astore 5
    //   805: aload 7
    //   807: astore 8
    //   809: aload 10
    //   811: invokevirtual 525	android/content/res/TypedArray:recycle	()V
    //   814: aload 9
    //   816: astore 10
    //   818: aload 9
    //   820: ifnonnull +24 -> 844
    //   823: aload 7
    //   825: astore 6
    //   827: aload 7
    //   829: astore 5
    //   831: aload 7
    //   833: astore 8
    //   835: new 314	android/os/Bundle
    //   838: dup
    //   839: invokespecial 526	android/os/Bundle:<init>	()V
    //   842: astore 10
    //   844: aload 7
    //   846: astore 6
    //   848: aload 7
    //   850: astore 5
    //   852: aload 7
    //   854: astore 8
    //   856: aload 7
    //   858: invokeinterface 451 1 0
    //   863: istore_3
    //   864: aload 7
    //   866: astore 6
    //   868: aload 7
    //   870: astore 5
    //   872: aload 7
    //   874: astore 8
    //   876: aload 7
    //   878: invokeinterface 430 1 0
    //   883: istore 4
    //   885: iload 4
    //   887: iconst_1
    //   888: if_icmpeq +325 -> 1213
    //   891: iload 4
    //   893: iconst_3
    //   894: if_icmpne +26 -> 920
    //   897: aload 7
    //   899: astore 6
    //   901: aload 7
    //   903: astore 5
    //   905: aload 7
    //   907: astore 8
    //   909: aload 7
    //   911: invokeinterface 451 1 0
    //   916: iload_3
    //   917: if_icmple +296 -> 1213
    //   920: iload 4
    //   922: iconst_3
    //   923: if_icmpeq -59 -> 864
    //   926: iload 4
    //   928: iconst_4
    //   929: if_icmpeq -65 -> 864
    //   932: aload 7
    //   934: astore 6
    //   936: aload 7
    //   938: astore 5
    //   940: aload 7
    //   942: astore 8
    //   944: aload 7
    //   946: invokeinterface 431 1 0
    //   951: astore 9
    //   953: aload 7
    //   955: astore 6
    //   957: aload 7
    //   959: astore 5
    //   961: aload 7
    //   963: astore 8
    //   965: aload 9
    //   967: ldc_w 528
    //   970: invokevirtual 295	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   973: ifeq +166 -> 1139
    //   976: aload 7
    //   978: astore 6
    //   980: aload 7
    //   982: astore 5
    //   984: aload 7
    //   986: astore 8
    //   988: aload_0
    //   989: invokevirtual 413	com/oneplus/lib/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   992: ldc_w 528
    //   995: aload 11
    //   997: aload 10
    //   999: invokevirtual 532	android/content/res/Resources:parseBundleExtra	(Ljava/lang/String;Landroid/util/AttributeSet;Landroid/os/Bundle;)V
    //   1002: aload 7
    //   1004: astore 6
    //   1006: aload 7
    //   1008: astore 5
    //   1010: aload 7
    //   1012: astore 8
    //   1014: aload 7
    //   1016: invokestatic 538	com/oneplus/lib/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1019: goto -155 -> 864
    //   1022: astore_2
    //   1023: aload 8
    //   1025: astore 5
    //   1027: new 178	java/lang/RuntimeException
    //   1030: dup
    //   1031: ldc_w 442
    //   1034: aload_2
    //   1035: invokespecial 445	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1038: athrow
    //   1039: aload 7
    //   1041: astore 6
    //   1043: aload 7
    //   1045: astore 5
    //   1047: aload 7
    //   1049: astore 8
    //   1051: aload 12
    //   1053: aload 13
    //   1055: getfield 541	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1058: putfield 306	com/oneplus/lib/preference/PreferenceActivity$Header:title	Ljava/lang/CharSequence;
    //   1061: goto -585 -> 476
    //   1064: aload 7
    //   1066: astore 6
    //   1068: aload 7
    //   1070: astore 5
    //   1072: aload 7
    //   1074: astore 8
    //   1076: aload 12
    //   1078: aload 13
    //   1080: getfield 541	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1083: putfield 544	com/oneplus/lib/preference/PreferenceActivity$Header:summary	Ljava/lang/CharSequence;
    //   1086: goto -520 -> 566
    //   1089: aload 7
    //   1091: astore 6
    //   1093: aload 7
    //   1095: astore 5
    //   1097: aload 7
    //   1099: astore 8
    //   1101: aload 12
    //   1103: aload 13
    //   1105: getfield 541	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1108: putfield 547	com/oneplus/lib/preference/PreferenceActivity$Header:breadCrumbTitle	Ljava/lang/CharSequence;
    //   1111: goto -455 -> 656
    //   1114: aload 7
    //   1116: astore 6
    //   1118: aload 7
    //   1120: astore 5
    //   1122: aload 7
    //   1124: astore 8
    //   1126: aload 12
    //   1128: aload 13
    //   1130: getfield 541	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1133: putfield 550	com/oneplus/lib/preference/PreferenceActivity$Header:breadCrumbShortTitle	Ljava/lang/CharSequence;
    //   1136: goto -390 -> 746
    //   1139: aload 7
    //   1141: astore 6
    //   1143: aload 7
    //   1145: astore 5
    //   1147: aload 7
    //   1149: astore 8
    //   1151: aload 9
    //   1153: ldc_w 551
    //   1156: invokevirtual 295	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1159: ifeq +34 -> 1193
    //   1162: aload 7
    //   1164: astore 6
    //   1166: aload 7
    //   1168: astore 5
    //   1170: aload 7
    //   1172: astore 8
    //   1174: aload 12
    //   1176: aload_0
    //   1177: invokevirtual 413	com/oneplus/lib/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   1180: aload 7
    //   1182: aload 11
    //   1184: invokestatic 555	android/content/Intent:parseIntent	(Landroid/content/res/Resources;Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;)Landroid/content/Intent;
    //   1187: putfield 299	com/oneplus/lib/preference/PreferenceActivity$Header:intent	Landroid/content/Intent;
    //   1190: goto -326 -> 864
    //   1193: aload 7
    //   1195: astore 6
    //   1197: aload 7
    //   1199: astore 5
    //   1201: aload 7
    //   1203: astore 8
    //   1205: aload 7
    //   1207: invokestatic 538	com/oneplus/lib/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1210: goto -346 -> 864
    //   1213: aload 7
    //   1215: astore 6
    //   1217: aload 7
    //   1219: astore 5
    //   1221: aload 7
    //   1223: astore 8
    //   1225: aload 10
    //   1227: astore 9
    //   1229: aload 10
    //   1231: invokevirtual 556	android/os/Bundle:size	()I
    //   1234: ifle +25 -> 1259
    //   1237: aload 7
    //   1239: astore 6
    //   1241: aload 7
    //   1243: astore 5
    //   1245: aload 7
    //   1247: astore 8
    //   1249: aload 12
    //   1251: aload 10
    //   1253: putfield 312	com/oneplus/lib/preference/PreferenceActivity$Header:fragmentArguments	Landroid/os/Bundle;
    //   1256: aconst_null
    //   1257: astore 9
    //   1259: aload 7
    //   1261: astore 6
    //   1263: aload 7
    //   1265: astore 5
    //   1267: aload 7
    //   1269: astore 8
    //   1271: aload_2
    //   1272: aload 12
    //   1274: invokeinterface 559 2 0
    //   1279: pop
    //   1280: goto -1056 -> 224
    //   1283: aload 7
    //   1285: astore 6
    //   1287: aload 7
    //   1289: astore 5
    //   1291: aload 7
    //   1293: astore 8
    //   1295: aload 7
    //   1297: invokestatic 538	com/oneplus/lib/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1300: goto -1076 -> 224
    //   1303: aload 7
    //   1305: ifnull +10 -> 1315
    //   1308: aload 7
    //   1310: invokeinterface 448 1 0
    //   1315: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1316	0	this	PreferenceActivity
    //   0	1316	1	paramInt	int
    //   0	1316	2	paramList	List<Header>
    //   243	675	3	i	int
    //   883	47	4	j	int
    //   1	1289	5	localObject1	Object
    //   7	1279	6	localObject2	Object
    //   17	1292	7	localXmlResourceParser	android.content.res.XmlResourceParser
    //   4	1290	8	localObject3	Object
    //   87	1171	9	localObject4	Object
    //   357	895	10	localObject5	Object
    //   36	1147	11	localAttributeSet	android.util.AttributeSet
    //   334	939	12	localHeader	Header
    //   406	723	13	localTypedValue	android.util.TypedValue
    // Exception table:
    //   from	to	target	type
    //   9	19	169	org/xmlpull/v1/XmlPullParserException
    //   31	38	169	org/xmlpull/v1/XmlPullParserException
    //   50	58	169	org/xmlpull/v1/XmlPullParserException
    //   80	89	169	org/xmlpull/v1/XmlPullParserException
    //   101	112	169	org/xmlpull/v1/XmlPullParserException
    //   124	169	169	org/xmlpull/v1/XmlPullParserException
    //   216	224	169	org/xmlpull/v1/XmlPullParserException
    //   236	244	169	org/xmlpull/v1/XmlPullParserException
    //   266	277	169	org/xmlpull/v1/XmlPullParserException
    //   299	315	169	org/xmlpull/v1/XmlPullParserException
    //   327	336	169	org/xmlpull/v1/XmlPullParserException
    //   348	359	169	org/xmlpull/v1/XmlPullParserException
    //   371	386	169	org/xmlpull/v1/XmlPullParserException
    //   398	408	169	org/xmlpull/v1/XmlPullParserException
    //   425	434	169	org/xmlpull/v1/XmlPullParserException
    //   446	454	169	org/xmlpull/v1/XmlPullParserException
    //   466	476	169	org/xmlpull/v1/XmlPullParserException
    //   488	498	169	org/xmlpull/v1/XmlPullParserException
    //   515	524	169	org/xmlpull/v1/XmlPullParserException
    //   536	544	169	org/xmlpull/v1/XmlPullParserException
    //   556	566	169	org/xmlpull/v1/XmlPullParserException
    //   578	588	169	org/xmlpull/v1/XmlPullParserException
    //   605	614	169	org/xmlpull/v1/XmlPullParserException
    //   626	634	169	org/xmlpull/v1/XmlPullParserException
    //   646	656	169	org/xmlpull/v1/XmlPullParserException
    //   668	678	169	org/xmlpull/v1/XmlPullParserException
    //   695	704	169	org/xmlpull/v1/XmlPullParserException
    //   716	724	169	org/xmlpull/v1/XmlPullParserException
    //   736	746	169	org/xmlpull/v1/XmlPullParserException
    //   758	772	169	org/xmlpull/v1/XmlPullParserException
    //   784	797	169	org/xmlpull/v1/XmlPullParserException
    //   809	814	169	org/xmlpull/v1/XmlPullParserException
    //   835	844	169	org/xmlpull/v1/XmlPullParserException
    //   856	864	169	org/xmlpull/v1/XmlPullParserException
    //   876	885	169	org/xmlpull/v1/XmlPullParserException
    //   909	920	169	org/xmlpull/v1/XmlPullParserException
    //   944	953	169	org/xmlpull/v1/XmlPullParserException
    //   965	976	169	org/xmlpull/v1/XmlPullParserException
    //   988	1002	169	org/xmlpull/v1/XmlPullParserException
    //   1014	1019	169	org/xmlpull/v1/XmlPullParserException
    //   1051	1061	169	org/xmlpull/v1/XmlPullParserException
    //   1076	1086	169	org/xmlpull/v1/XmlPullParserException
    //   1101	1111	169	org/xmlpull/v1/XmlPullParserException
    //   1126	1136	169	org/xmlpull/v1/XmlPullParserException
    //   1151	1162	169	org/xmlpull/v1/XmlPullParserException
    //   1174	1190	169	org/xmlpull/v1/XmlPullParserException
    //   1205	1210	169	org/xmlpull/v1/XmlPullParserException
    //   1229	1237	169	org/xmlpull/v1/XmlPullParserException
    //   1249	1256	169	org/xmlpull/v1/XmlPullParserException
    //   1271	1280	169	org/xmlpull/v1/XmlPullParserException
    //   1295	1300	169	org/xmlpull/v1/XmlPullParserException
    //   9	19	186	finally
    //   31	38	186	finally
    //   50	58	186	finally
    //   80	89	186	finally
    //   101	112	186	finally
    //   124	169	186	finally
    //   174	186	186	finally
    //   216	224	186	finally
    //   236	244	186	finally
    //   266	277	186	finally
    //   299	315	186	finally
    //   327	336	186	finally
    //   348	359	186	finally
    //   371	386	186	finally
    //   398	408	186	finally
    //   425	434	186	finally
    //   446	454	186	finally
    //   466	476	186	finally
    //   488	498	186	finally
    //   515	524	186	finally
    //   536	544	186	finally
    //   556	566	186	finally
    //   578	588	186	finally
    //   605	614	186	finally
    //   626	634	186	finally
    //   646	656	186	finally
    //   668	678	186	finally
    //   695	704	186	finally
    //   716	724	186	finally
    //   736	746	186	finally
    //   758	772	186	finally
    //   784	797	186	finally
    //   809	814	186	finally
    //   835	844	186	finally
    //   856	864	186	finally
    //   876	885	186	finally
    //   909	920	186	finally
    //   944	953	186	finally
    //   965	976	186	finally
    //   988	1002	186	finally
    //   1014	1019	186	finally
    //   1027	1039	186	finally
    //   1051	1061	186	finally
    //   1076	1086	186	finally
    //   1101	1111	186	finally
    //   1126	1136	186	finally
    //   1151	1162	186	finally
    //   1174	1190	186	finally
    //   1205	1210	186	finally
    //   1229	1237	186	finally
    //   1249	1256	186	finally
    //   1271	1280	186	finally
    //   1295	1300	186	finally
    //   9	19	1022	java/io/IOException
    //   31	38	1022	java/io/IOException
    //   50	58	1022	java/io/IOException
    //   80	89	1022	java/io/IOException
    //   101	112	1022	java/io/IOException
    //   124	169	1022	java/io/IOException
    //   216	224	1022	java/io/IOException
    //   236	244	1022	java/io/IOException
    //   266	277	1022	java/io/IOException
    //   299	315	1022	java/io/IOException
    //   327	336	1022	java/io/IOException
    //   348	359	1022	java/io/IOException
    //   371	386	1022	java/io/IOException
    //   398	408	1022	java/io/IOException
    //   425	434	1022	java/io/IOException
    //   446	454	1022	java/io/IOException
    //   466	476	1022	java/io/IOException
    //   488	498	1022	java/io/IOException
    //   515	524	1022	java/io/IOException
    //   536	544	1022	java/io/IOException
    //   556	566	1022	java/io/IOException
    //   578	588	1022	java/io/IOException
    //   605	614	1022	java/io/IOException
    //   626	634	1022	java/io/IOException
    //   646	656	1022	java/io/IOException
    //   668	678	1022	java/io/IOException
    //   695	704	1022	java/io/IOException
    //   716	724	1022	java/io/IOException
    //   736	746	1022	java/io/IOException
    //   758	772	1022	java/io/IOException
    //   784	797	1022	java/io/IOException
    //   809	814	1022	java/io/IOException
    //   835	844	1022	java/io/IOException
    //   856	864	1022	java/io/IOException
    //   876	885	1022	java/io/IOException
    //   909	920	1022	java/io/IOException
    //   944	953	1022	java/io/IOException
    //   965	976	1022	java/io/IOException
    //   988	1002	1022	java/io/IOException
    //   1014	1019	1022	java/io/IOException
    //   1051	1061	1022	java/io/IOException
    //   1076	1086	1022	java/io/IOException
    //   1101	1111	1022	java/io/IOException
    //   1126	1136	1022	java/io/IOException
    //   1151	1162	1022	java/io/IOException
    //   1174	1190	1022	java/io/IOException
    //   1205	1210	1022	java/io/IOException
    //   1229	1237	1022	java/io/IOException
    //   1249	1256	1022	java/io/IOException
    //   1271	1280	1022	java/io/IOException
    //   1295	1300	1022	java/io/IOException
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onBuildHeaders(List<Header> paramList) {}
  
  public Intent onBuildStartFragmentIntent(String paramString, Bundle paramBundle, int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(this, getClass());
    localIntent.putExtra(":android:show_fragment", paramString);
    localIntent.putExtra(":android:show_fragment_args", paramBundle);
    localIntent.putExtra(":android:show_fragment_title", paramInt1);
    localIntent.putExtra(":android:show_fragment_short_title", paramInt2);
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }
  
  public void onContentChanged()
  {
    super.onContentChanged();
    if (this.mPreferenceManager != null) {
      postBindPreferences();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject1 = obtainStyledAttributes(null, R.styleable.PreferenceActivity, R.attr.op_preferenceActivityStyle, 0);
    int i = ((TypedArray)localObject1).getResourceId(R.styleable.PreferenceActivity_android_layout, R.layout.preference_list_content);
    this.mPreferenceHeaderItemResId = ((TypedArray)localObject1).getResourceId(R.styleable.PreferenceActivity_oneplusHeaderLayout, R.layout.preference_header_item);
    this.mPreferenceHeaderRemoveEmptyIcon = ((TypedArray)localObject1).getBoolean(R.styleable.PreferenceActivity_headerRemoveIconIfEmpty, false);
    ((TypedArray)localObject1).recycle();
    setContentView(i);
    this.mListFooter = ((FrameLayout)findViewById(R.id.list_footer));
    this.mPrefsContainer = ((ViewGroup)findViewById(R.id.prefs_frame));
    boolean bool;
    Object localObject2;
    int j;
    label233:
    label286:
    label293:
    String str;
    if ((!onIsHidingHeaders()) && (onIsMultiPane()))
    {
      bool = false;
      this.mSinglePane = bool;
      localObject1 = getIntent().getStringExtra(":android:show_fragment");
      localObject2 = getIntent().getBundleExtra(":android:show_fragment_args");
      i = getIntent().getIntExtra(":android:show_fragment_title", 0);
      j = getIntent().getIntExtra(":android:show_fragment_short_title", 0);
      if (paramBundle == null) {
        break label487;
      }
      localObject2 = paramBundle.getParcelableArrayList(":android:headers");
      if (localObject2 != null)
      {
        this.mHeaders.addAll((Collection)localObject2);
        int k = paramBundle.getInt(":android:cur_header", -1);
        if ((k >= 0) && (k < this.mHeaders.size())) {
          setSelectedHeader((Header)this.mHeaders.get(k));
        }
      }
      if ((localObject1 == null) || (!this.mSinglePane)) {
        break label600;
      }
      findViewById(R.id.headers).setVisibility(8);
      this.mPrefsContainer.setVisibility(0);
      if (i != 0)
      {
        localObject1 = getText(i);
        if (j == 0) {
          break label595;
        }
        paramBundle = getText(j);
        showBreadCrumbs((CharSequence)localObject1, paramBundle);
      }
      paramBundle = getIntent();
      if (paramBundle.getBooleanExtra("extra_prefs_show_button_bar", false))
      {
        findViewById(R.id.button_bar).setVisibility(0);
        localObject1 = (Button)findViewById(R.id.back_button);
        ((Button)localObject1).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(0);
            PreferenceActivity.this.finish();
          }
        });
        localObject2 = (Button)findViewById(R.id.skip_button);
        ((Button)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(-1);
            PreferenceActivity.this.finish();
          }
        });
        this.mNextButton = ((Button)findViewById(R.id.next_button));
        this.mNextButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(-1);
            PreferenceActivity.this.finish();
          }
        });
        if (paramBundle.hasExtra("extra_prefs_set_next_text"))
        {
          str = paramBundle.getStringExtra("extra_prefs_set_next_text");
          if (!TextUtils.isEmpty(str)) {
            break label735;
          }
          this.mNextButton.setVisibility(8);
        }
        label432:
        if (paramBundle.hasExtra("extra_prefs_set_back_text"))
        {
          str = paramBundle.getStringExtra("extra_prefs_set_back_text");
          if (!TextUtils.isEmpty(str)) {
            break label747;
          }
          ((Button)localObject1).setVisibility(8);
        }
      }
    }
    for (;;)
    {
      if (paramBundle.getBooleanExtra("extra_prefs_show_skip", false)) {
        ((Button)localObject2).setVisibility(0);
      }
      return;
      bool = true;
      break;
      label487:
      if ((localObject1 != null) && (this.mSinglePane))
      {
        switchToHeader((String)localObject1, (Bundle)localObject2);
        if (i == 0) {
          break label233;
        }
        localObject2 = getText(i);
        if (j != 0) {}
        for (paramBundle = getText(j);; paramBundle = null)
        {
          showBreadCrumbs((CharSequence)localObject2, paramBundle);
          break;
        }
      }
      onBuildHeaders(this.mHeaders);
      if ((this.mHeaders.size() <= 0) || (this.mSinglePane)) {
        break label233;
      }
      if (localObject1 == null)
      {
        switchToHeader(onGetInitialHeader());
        break label233;
      }
      switchToHeader((String)localObject1, (Bundle)localObject2);
      break label233;
      label595:
      paramBundle = null;
      break label286;
      label600:
      if (this.mHeaders.size() > 0)
      {
        setListAdapter(new HeaderAdapter(this, this.mHeaders, this.mPreferenceHeaderItemResId, this.mPreferenceHeaderRemoveEmptyIcon));
        if (this.mSinglePane) {
          break label293;
        }
        getListView().setChoiceMode(1);
        if (this.mCurHeader != null) {
          setSelectedHeader(this.mCurHeader);
        }
        this.mPrefsContainer.setVisibility(0);
        break label293;
      }
      setContentView(R.layout.preference_list_content_single);
      this.mListFooter = ((FrameLayout)findViewById(R.id.list_footer));
      this.mPrefsContainer = ((ViewGroup)findViewById(R.id.prefs));
      this.mPreferenceManager = new PreferenceManager(this, 100);
      this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
      break label293;
      label735:
      this.mNextButton.setText(str);
      break label432;
      label747:
      ((Button)localObject1).setText(str);
    }
  }
  
  protected void onDestroy()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    super.onDestroy();
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityDestroy();
    }
  }
  
  public Header onGetInitialHeader()
  {
    int i = 0;
    while (i < this.mHeaders.size())
    {
      Header localHeader = (Header)this.mHeaders.get(i);
      if (localHeader.fragment != null) {
        return localHeader;
      }
      i += 1;
    }
    throw new IllegalStateException("Must have at least one header with a fragment");
  }
  
  public Header onGetNewHeader()
  {
    return null;
  }
  
  public void onHeaderClick(Header paramHeader, int paramInt)
  {
    if (paramHeader.fragment != null) {
      if (this.mSinglePane)
      {
        j = paramHeader.breadCrumbTitleRes;
        i = paramHeader.breadCrumbShortTitleRes;
        paramInt = j;
        if (j == 0)
        {
          paramInt = paramHeader.titleRes;
          i = 0;
        }
        startWithFragment(paramHeader.fragment, paramHeader.fragmentArguments, null, 0, paramInt, i);
      }
    }
    while (paramHeader.intent == null)
    {
      int j;
      int i;
      return;
      switchToHeader(paramHeader);
      return;
    }
    startActivity(paramHeader.intent);
  }
  
  public boolean onIsHidingHeaders()
  {
    return getIntent().getBooleanExtra(":android:no_headers", false);
  }
  
  public boolean onIsMultiPane()
  {
    return getResources().getBoolean(R.bool.preferences_prefer_dual_pane);
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    super.onListItemClick(paramListView, paramView, paramInt, paramLong);
    if (getListAdapter() != null)
    {
      paramListView = getListAdapter().getItem(paramInt);
      if ((paramListView instanceof Header)) {
        onHeaderClick((Header)paramListView, paramInt);
      }
    }
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchNewIntent(paramIntent);
    }
  }
  
  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    startPreferencePanel(paramPreference.getFragment(), paramPreference.getExtras(), paramPreference.getTitleRes(), paramPreference.getTitle(), null, 0);
    return true;
  }
  
  @Deprecated
  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    return false;
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    if (this.mPreferenceManager != null)
    {
      Bundle localBundle = paramBundle.getBundle(":android:preferences");
      if (localBundle != null)
      {
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        if (localPreferenceScreen != null)
        {
          localPreferenceScreen.restoreHierarchyState(localBundle);
          this.mSavedInstanceState = paramBundle;
          return;
        }
      }
    }
    super.onRestoreInstanceState(paramBundle);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mHeaders.size() > 0)
    {
      paramBundle.putParcelableArrayList(":android:headers", this.mHeaders);
      if (this.mCurHeader != null)
      {
        int i = this.mHeaders.indexOf(this.mCurHeader);
        if (i >= 0) {
          paramBundle.putInt(":android:cur_header", i);
        }
      }
    }
    if (this.mPreferenceManager != null)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      if (localPreferenceScreen != null)
      {
        Bundle localBundle = new Bundle();
        localPreferenceScreen.saveHierarchyState(localBundle);
        paramBundle.putBundle(":android:preferences", localBundle);
      }
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityStop();
    }
  }
  
  public void setListFooter(View paramView)
  {
    this.mListFooter.removeAllViews();
    this.mListFooter.addView(paramView, new FrameLayout.LayoutParams(-1, -2));
  }
  
  public void setParentTitle(CharSequence paramCharSequence1, CharSequence paramCharSequence2, View.OnClickListener paramOnClickListener)
  {
    if (this.mFragmentBreadCrumbs != null) {
      this.mFragmentBreadCrumbs.setParentTitle(paramCharSequence1, paramCharSequence2, paramOnClickListener);
    }
  }
  
  @Deprecated
  public void setPreferenceScreen(PreferenceScreen paramPreferenceScreen)
  {
    requirePreferenceManager();
    if ((this.mPreferenceManager.setPreferences(paramPreferenceScreen)) && (paramPreferenceScreen != null))
    {
      postBindPreferences();
      paramPreferenceScreen = getPreferenceScreen().getTitle();
      if (paramPreferenceScreen != null) {
        setTitle(paramPreferenceScreen);
      }
    }
  }
  
  void setSelectedHeader(Header paramHeader)
  {
    this.mCurHeader = paramHeader;
    int i = this.mHeaders.indexOf(paramHeader);
    if (i >= 0) {
      getListView().setItemChecked(i, true);
    }
    for (;;)
    {
      showBreadCrumbs(paramHeader);
      return;
      getListView().clearChoices();
    }
  }
  
  void showBreadCrumbs(Header paramHeader)
  {
    if (paramHeader != null)
    {
      Object localObject2 = paramHeader.getBreadCrumbTitle(getResources());
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = paramHeader.getTitle(getResources());
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = getTitle();
      }
      showBreadCrumbs((CharSequence)localObject2, paramHeader.getBreadCrumbShortTitle(getResources()));
      return;
    }
    showBreadCrumbs(getTitle(), null);
  }
  
  public void showBreadCrumbs(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (this.mFragmentBreadCrumbs == null)
    {
      View localView = findViewById(16908310);
      try
      {
        this.mFragmentBreadCrumbs = ((FragmentBreadCrumbs)localView);
        if (this.mFragmentBreadCrumbs == null)
        {
          if (paramCharSequence1 != null) {
            setTitle(paramCharSequence1);
          }
          return;
        }
      }
      catch (ClassCastException paramCharSequence2)
      {
        setTitle(paramCharSequence1);
        return;
      }
      if (this.mSinglePane)
      {
        this.mFragmentBreadCrumbs.setVisibility(8);
        localView = findViewById(R.id.breadcrumb_section);
        if (localView != null) {
          localView.setVisibility(8);
        }
        setTitle(paramCharSequence1);
      }
      this.mFragmentBreadCrumbs.setMaxVisible(2);
      this.mFragmentBreadCrumbs.setActivity(this);
    }
    if (this.mFragmentBreadCrumbs.getVisibility() != 0)
    {
      setTitle(paramCharSequence1);
      return;
    }
    this.mFragmentBreadCrumbs.setTitle(paramCharSequence1, paramCharSequence2);
    this.mFragmentBreadCrumbs.setParentTitle(null, null, null);
  }
  
  public void startPreferenceFragment(Fragment paramFragment, boolean paramBoolean)
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.replace(R.id.prefs, paramFragment);
    if (paramBoolean)
    {
      localFragmentTransaction.setTransition(4097);
      localFragmentTransaction.addToBackStack(":android:prefs");
    }
    for (;;)
    {
      localFragmentTransaction.commitAllowingStateLoss();
      return;
      localFragmentTransaction.setTransition(4099);
    }
  }
  
  public void startPreferencePanel(String paramString, Bundle paramBundle, int paramInt1, CharSequence paramCharSequence, Fragment paramFragment, int paramInt2)
  {
    if (this.mSinglePane)
    {
      startWithFragment(paramString, paramBundle, paramFragment, paramInt2, paramInt1, 0);
      return;
    }
    paramString = Fragment.instantiate(this, paramString, paramBundle);
    if (paramFragment != null) {
      paramString.setTargetFragment(paramFragment, paramInt2);
    }
    paramBundle = getFragmentManager().beginTransaction();
    paramBundle.replace(R.id.prefs, paramString);
    if (paramInt1 != 0) {
      paramBundle.setBreadCrumbTitle(paramInt1);
    }
    for (;;)
    {
      paramBundle.setTransition(4097);
      paramBundle.addToBackStack(":android:prefs");
      paramBundle.commitAllowingStateLoss();
      return;
      if (paramCharSequence != null) {
        paramBundle.setBreadCrumbTitle(paramCharSequence);
      }
    }
  }
  
  public void startWithFragment(String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt)
  {
    startWithFragment(paramString, paramBundle, paramFragment, paramInt, 0, 0);
  }
  
  public void startWithFragment(String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3)
  {
    paramString = onBuildStartFragmentIntent(paramString, paramBundle, paramInt2, paramInt3);
    if (paramFragment == null)
    {
      startActivity(paramString);
      return;
    }
    paramFragment.startActivityForResult(paramString, paramInt1);
  }
  
  public void switchToHeader(Header paramHeader)
  {
    if (this.mCurHeader == paramHeader)
    {
      getFragmentManager().popBackStack(":android:prefs", 1);
      return;
    }
    if (paramHeader.fragment == null) {
      throw new IllegalStateException("can't switch to header that has no fragment");
    }
    switchToHeaderInner(paramHeader.fragment, paramHeader.fragmentArguments);
    setSelectedHeader(paramHeader);
  }
  
  public void switchToHeader(String paramString, Bundle paramBundle)
  {
    Object localObject2 = null;
    int i = 0;
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < this.mHeaders.size())
      {
        if (paramString.equals(((Header)this.mHeaders.get(i)).fragment)) {
          localObject1 = (Header)this.mHeaders.get(i);
        }
      }
      else
      {
        setSelectedHeader((Header)localObject1);
        switchToHeaderInner(paramString, paramBundle);
        return;
      }
      i += 1;
    }
  }
  
  public static final class Header
    implements Parcelable
  {
    public static final Parcelable.Creator<Header> CREATOR = new Parcelable.Creator()
    {
      public PreferenceActivity.Header createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PreferenceActivity.Header(paramAnonymousParcel);
      }
      
      public PreferenceActivity.Header[] newArray(int paramAnonymousInt)
      {
        return new PreferenceActivity.Header[paramAnonymousInt];
      }
    };
    public CharSequence breadCrumbShortTitle;
    public int breadCrumbShortTitleRes;
    public CharSequence breadCrumbTitle;
    public int breadCrumbTitleRes;
    public Bundle extras;
    public String fragment;
    public Bundle fragmentArguments;
    public int iconRes;
    public long id = -1L;
    public Intent intent;
    public CharSequence summary;
    public int summaryRes;
    public CharSequence title;
    public int titleRes;
    
    public Header() {}
    
    Header(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public CharSequence getBreadCrumbShortTitle(Resources paramResources)
    {
      if (this.breadCrumbShortTitleRes != 0) {
        return paramResources.getText(this.breadCrumbShortTitleRes);
      }
      return this.breadCrumbShortTitle;
    }
    
    public CharSequence getBreadCrumbTitle(Resources paramResources)
    {
      if (this.breadCrumbTitleRes != 0) {
        return paramResources.getText(this.breadCrumbTitleRes);
      }
      return this.breadCrumbTitle;
    }
    
    public CharSequence getSummary(Resources paramResources)
    {
      if (this.summaryRes != 0) {
        return paramResources.getText(this.summaryRes);
      }
      return this.summary;
    }
    
    public CharSequence getTitle(Resources paramResources)
    {
      if (this.titleRes != 0) {
        return paramResources.getText(this.titleRes);
      }
      return this.title;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.id = paramParcel.readLong();
      this.titleRes = paramParcel.readInt();
      this.title = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.summaryRes = paramParcel.readInt();
      this.summary = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.breadCrumbTitleRes = paramParcel.readInt();
      this.breadCrumbTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.breadCrumbShortTitleRes = paramParcel.readInt();
      this.breadCrumbShortTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.iconRes = paramParcel.readInt();
      this.fragment = paramParcel.readString();
      this.fragmentArguments = paramParcel.readBundle();
      if (paramParcel.readInt() != 0) {
        this.intent = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
      }
      this.extras = paramParcel.readBundle();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.id);
      paramParcel.writeInt(this.titleRes);
      TextUtils.writeToParcel(this.title, paramParcel, paramInt);
      paramParcel.writeInt(this.summaryRes);
      TextUtils.writeToParcel(this.summary, paramParcel, paramInt);
      paramParcel.writeInt(this.breadCrumbTitleRes);
      TextUtils.writeToParcel(this.breadCrumbTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.breadCrumbShortTitleRes);
      TextUtils.writeToParcel(this.breadCrumbShortTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.iconRes);
      paramParcel.writeString(this.fragment);
      paramParcel.writeBundle(this.fragmentArguments);
      if (this.intent != null)
      {
        paramParcel.writeInt(1);
        this.intent.writeToParcel(paramParcel, paramInt);
      }
      for (;;)
      {
        paramParcel.writeBundle(this.extras);
        return;
        paramParcel.writeInt(0);
      }
    }
  }
  
  private static class HeaderAdapter
    extends ArrayAdapter<PreferenceActivity.Header>
  {
    private LayoutInflater mInflater;
    private int mLayoutResId;
    private boolean mRemoveIconIfEmpty;
    
    public HeaderAdapter(Context paramContext, List<PreferenceActivity.Header> paramList, int paramInt, boolean paramBoolean)
    {
      super(0, paramList);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mLayoutResId = paramInt;
      this.mRemoveIconIfEmpty = paramBoolean;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject;
      if (paramView == null)
      {
        paramViewGroup = this.mInflater.inflate(this.mLayoutResId, paramViewGroup, false);
        paramView = new HeaderViewHolder(null);
        paramView.icon = ((ImageView)paramViewGroup.findViewById(16908294));
        paramView.title = ((TextView)paramViewGroup.findViewById(16908310));
        paramView.summary = ((TextView)paramViewGroup.findViewById(16908304));
        paramViewGroup.setTag(paramView);
        localObject = (PreferenceActivity.Header)getItem(paramInt);
        if (!this.mRemoveIconIfEmpty) {
          break label201;
        }
        if (((PreferenceActivity.Header)localObject).iconRes != 0) {
          break label178;
        }
        paramView.icon.setVisibility(8);
      }
      for (;;)
      {
        paramView.title.setText(((PreferenceActivity.Header)localObject).getTitle(getContext().getResources()));
        localObject = ((PreferenceActivity.Header)localObject).getSummary(getContext().getResources());
        if (TextUtils.isEmpty((CharSequence)localObject)) {
          break label216;
        }
        paramView.summary.setVisibility(0);
        paramView.summary.setText((CharSequence)localObject);
        return paramViewGroup;
        paramViewGroup = paramView;
        paramView = (HeaderViewHolder)paramView.getTag();
        break;
        label178:
        paramView.icon.setVisibility(0);
        paramView.icon.setImageResource(((PreferenceActivity.Header)localObject).iconRes);
        continue;
        label201:
        paramView.icon.setImageResource(((PreferenceActivity.Header)localObject).iconRes);
      }
      label216:
      paramView.summary.setVisibility(8);
      return paramViewGroup;
    }
    
    private static class HeaderViewHolder
    {
      ImageView icon;
      TextView summary;
      TextView title;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */