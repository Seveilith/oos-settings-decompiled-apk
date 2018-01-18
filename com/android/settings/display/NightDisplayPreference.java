package com.android.settings.display;

import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.app.NightDisplayController.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;

public class NightDisplayPreference
  extends SwitchPreference
  implements NightDisplayController.Callback
{
  private NightDisplayController mController;
  private java.text.DateFormat mTimeFormatter;
  
  public NightDisplayPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mController = new NightDisplayController(paramContext);
    this.mTimeFormatter = android.text.format.DateFormat.getTimeFormat(paramContext);
    this.mTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  private String getFormattedTimeString(NightDisplayController.LocalTime paramLocalTime)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeZone(this.mTimeFormatter.getTimeZone());
    localCalendar.set(11, paramLocalTime.hourOfDay);
    localCalendar.set(12, paramLocalTime.minute);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    return this.mTimeFormatter.format(localCalendar.getTime());
  }
  
  private void updateSummary()
  {
    Context localContext = getContext();
    boolean bool = this.mController.isActivated();
    String str;
    switch (this.mController.getAutoMode())
    {
    case 0: 
    default: 
      if (bool)
      {
        i = 2131691620;
        str = localContext.getString(i);
        label62:
        if (!bool) {
          break label179;
        }
      }
      break;
    }
    label179:
    for (int i = 2131691619;; i = 2131691615)
    {
      setSummary(localContext.getString(i, new Object[] { str }));
      return;
      i = 2131691616;
      break;
      if (bool)
      {
        str = localContext.getString(2131691621, new Object[] { getFormattedTimeString(this.mController.getCustomEndTime()) });
        break label62;
      }
      str = localContext.getString(2131691617, new Object[] { getFormattedTimeString(this.mController.getCustomStartTime()) });
      break label62;
      if (bool) {}
      for (i = 2131691622;; i = 2131691618)
      {
        str = localContext.getString(i);
        break;
      }
    }
  }
  
  public void onActivated(boolean paramBoolean)
  {
    updateSummary();
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mController.setListener(this);
    updateSummary();
  }
  
  public void onAutoModeChanged(int paramInt)
  {
    updateSummary();
  }
  
  public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    updateSummary();
  }
  
  public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    updateSummary();
  }
  
  public void onDetached()
  {
    super.onDetached();
    this.mController.setListener(null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\NightDisplayPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */