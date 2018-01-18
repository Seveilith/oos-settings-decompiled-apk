package com.android.settings;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.Display;
import java.util.ArrayList;

public class ColorModePreference
  extends SwitchPreference
  implements DisplayManager.DisplayListener
{
  private int mCurrentIndex;
  private ArrayList<ColorModeDescription> mDescriptions;
  private Display mDisplay;
  private DisplayManager mDisplayManager = (DisplayManager)getContext().getSystemService(DisplayManager.class);
  
  public ColorModePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public int getColorModeCount()
  {
    return this.mDescriptions.size();
  }
  
  public void onDisplayAdded(int paramInt)
  {
    if (paramInt == 0) {
      updateCurrentAndSupported();
    }
  }
  
  public void onDisplayChanged(int paramInt)
  {
    if (paramInt == 0) {
      updateCurrentAndSupported();
    }
  }
  
  public void onDisplayRemoved(int paramInt) {}
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    Object localObject;
    if (this.mDescriptions.size() == 2)
    {
      localObject = this.mDescriptions;
      if (!paramBoolean) {
        break label56;
      }
    }
    label56:
    for (int i = 1;; i = 0)
    {
      localObject = (ColorModeDescription)((ArrayList)localObject).get(i);
      this.mDisplay.requestColorMode(ColorModeDescription.-get0((ColorModeDescription)localObject));
      this.mCurrentIndex = this.mDescriptions.indexOf(localObject);
      return true;
    }
  }
  
  public void startListening()
  {
    this.mDisplayManager.registerDisplayListener(this, new Handler(Looper.getMainLooper()));
  }
  
  public void stopListening()
  {
    this.mDisplayManager.unregisterDisplayListener(this);
  }
  
  public void updateCurrentAndSupported()
  {
    this.mDisplay = this.mDisplayManager.getDisplay(0);
    this.mDescriptions = new ArrayList();
    Object localObject = getContext().getResources();
    int[] arrayOfInt = ((Resources)localObject).getIntArray(2131427362);
    String[] arrayOfString = ((Resources)localObject).getStringArray(2131427368);
    localObject = ((Resources)localObject).getStringArray(2131427369);
    int i = 0;
    while (i < arrayOfInt.length)
    {
      if ((arrayOfInt[i] != -1) && (i != 1))
      {
        ColorModeDescription localColorModeDescription = new ColorModeDescription(null);
        ColorModeDescription.-set0(localColorModeDescription, arrayOfInt[i]);
        ColorModeDescription.-set2(localColorModeDescription, arrayOfString[i]);
        ColorModeDescription.-set1(localColorModeDescription, localObject[i]);
        this.mDescriptions.add(localColorModeDescription);
      }
      i += 1;
    }
    int j = this.mDisplay.getColorMode();
    this.mCurrentIndex = -1;
    i = 0;
    if (i < this.mDescriptions.size())
    {
      if (ColorModeDescription.-get0((ColorModeDescription)this.mDescriptions.get(i)) == j) {
        this.mCurrentIndex = i;
      }
    }
    else {
      if (this.mCurrentIndex != 1) {
        break label210;
      }
    }
    label210:
    for (boolean bool = true;; bool = false)
    {
      setChecked(bool);
      return;
      i += 1;
      break;
    }
  }
  
  private static class ColorModeDescription
  {
    private int colorMode;
    private String summary;
    private String title;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ColorModePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */