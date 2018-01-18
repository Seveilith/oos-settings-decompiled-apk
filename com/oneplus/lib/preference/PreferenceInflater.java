package com.oneplus.lib.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import com.oneplus.lib.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater
  extends GenericInflater<Preference, PreferenceGroup>
{
  private static final String EXTRA_TAG_NAME = "extra";
  private static final String INTENT_TAG_NAME = "intent";
  private static final String TAG = "PreferenceInflater";
  private PreferenceManager mPreferenceManager;
  
  public PreferenceInflater(Context paramContext, PreferenceManager paramPreferenceManager)
  {
    super(paramContext);
    init(paramPreferenceManager);
  }
  
  PreferenceInflater(GenericInflater<Preference, PreferenceGroup> paramGenericInflater, PreferenceManager paramPreferenceManager, Context paramContext)
  {
    super(paramGenericInflater, paramContext);
    init(paramPreferenceManager);
  }
  
  private void init(PreferenceManager paramPreferenceManager)
  {
    this.mPreferenceManager = paramPreferenceManager;
    setDefaultPackage("com.oneplus.lib.preference.");
  }
  
  public GenericInflater<Preference, PreferenceGroup> cloneInContext(Context paramContext)
  {
    return new PreferenceInflater(this, this.mPreferenceManager, paramContext);
  }
  
  protected boolean onCreateCustomFromTag(XmlPullParser paramXmlPullParser, Preference paramPreference, AttributeSet paramAttributeSet)
    throws XmlPullParserException
  {
    String str = paramXmlPullParser.getName();
    if (str.equals("intent")) {
      try
      {
        paramXmlPullParser = Intent.parseIntent(getContext().getResources(), paramXmlPullParser, paramAttributeSet);
        if (paramXmlPullParser != null) {
          paramPreference.setIntent(paramXmlPullParser);
        }
        return true;
      }
      catch (IOException paramXmlPullParser)
      {
        paramPreference = new XmlPullParserException("Error parsing preference");
        paramPreference.initCause(paramXmlPullParser);
        throw paramPreference;
      }
    }
    if (str.equals("extra"))
    {
      getContext().getResources().parseBundleExtra("extra", paramAttributeSet, paramPreference.getExtras());
      try
      {
        XmlUtils.skipCurrentTag(paramXmlPullParser);
        return true;
      }
      catch (IOException paramXmlPullParser)
      {
        paramPreference = new XmlPullParserException("Error parsing preference");
        paramPreference.initCause(paramXmlPullParser);
        throw paramPreference;
      }
    }
    return false;
  }
  
  protected PreferenceGroup onMergeRoots(PreferenceGroup paramPreferenceGroup1, boolean paramBoolean, PreferenceGroup paramPreferenceGroup2)
  {
    if (paramPreferenceGroup1 == null)
    {
      paramPreferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
      return paramPreferenceGroup2;
    }
    return paramPreferenceGroup1;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */