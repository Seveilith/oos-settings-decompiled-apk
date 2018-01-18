package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ZenModeScheduleDaysSelection
  extends ScrollView
{
  public static final int[] DAYS = { 1, 2, 3, 4, 5, 6, 7 };
  private final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
  private final SparseBooleanArray mDays = new SparseBooleanArray();
  private final LinearLayout mLayout = new LinearLayout(this.mContext);
  
  public ZenModeScheduleDaysSelection(Context paramContext, int[] paramArrayOfInt)
  {
    super(paramContext);
    int i = paramContext.getResources().getDimensionPixelSize(2131755500);
    this.mLayout.setPadding(i, 0, i, 0);
    addView(this.mLayout);
    if (paramArrayOfInt != null)
    {
      i = 0;
      while (i < paramArrayOfInt.length)
      {
        this.mDays.put(paramArrayOfInt[i], true);
        i += 1;
      }
    }
    this.mLayout.setOrientation(1);
    paramArrayOfInt = Calendar.getInstance();
    paramContext = LayoutInflater.from(paramContext);
    i = 0;
    while (i < DAYS.length)
    {
      final int j = DAYS[i];
      CheckBox localCheckBox = (CheckBox)paramContext.inflate(2130969132, this, false);
      paramArrayOfInt.set(7, j);
      localCheckBox.setText(this.mDayFormat.format(paramArrayOfInt.getTime()));
      localCheckBox.setChecked(this.mDays.get(j));
      localCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
        {
          ZenModeScheduleDaysSelection.-get0(ZenModeScheduleDaysSelection.this).put(j, paramAnonymousBoolean);
          ZenModeScheduleDaysSelection.this.onChanged(ZenModeScheduleDaysSelection.-wrap0(ZenModeScheduleDaysSelection.this));
        }
      });
      this.mLayout.addView(localCheckBox);
      i += 1;
    }
  }
  
  private int[] getDays()
  {
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray(this.mDays.size());
    int i = 0;
    if (i < this.mDays.size())
    {
      int j = this.mDays.keyAt(i);
      if (!this.mDays.valueAt(i)) {}
      for (;;)
      {
        i += 1;
        break;
        localSparseBooleanArray.put(j, true);
      }
    }
    int[] arrayOfInt = new int[localSparseBooleanArray.size()];
    i = 0;
    while (i < arrayOfInt.length)
    {
      arrayOfInt[i] = localSparseBooleanArray.keyAt(i);
      i += 1;
    }
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }
  
  protected void onChanged(int[] paramArrayOfInt) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeScheduleDaysSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */