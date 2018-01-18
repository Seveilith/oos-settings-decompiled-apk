package com.android.settings.datausage;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class SpinnerPreference
  extends Preference
  implements CycleAdapter.SpinnerInterface
{
  private CycleAdapter mAdapter;
  private Object mCurrentObject;
  private AdapterView.OnItemSelectedListener mListener;
  private final AdapterView.OnItemSelectedListener mOnSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (SpinnerPreference.-get2(SpinnerPreference.this) == paramAnonymousInt) {
        return;
      }
      SpinnerPreference.-set1(SpinnerPreference.this, paramAnonymousInt);
      SpinnerPreference.-set0(SpinnerPreference.this, SpinnerPreference.-get0(SpinnerPreference.this).getItem(paramAnonymousInt));
      SpinnerPreference.-get1(SpinnerPreference.this).onItemSelected(paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView)
    {
      SpinnerPreference.-get1(SpinnerPreference.this).onNothingSelected(paramAnonymousAdapterView);
    }
  };
  private int mPosition;
  
  public SpinnerPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968675);
  }
  
  public Object getSelectedItem()
  {
    return this.mCurrentObject;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (Spinner)paramPreferenceViewHolder.findViewById(2131362083);
    paramPreferenceViewHolder.setAdapter(this.mAdapter);
    paramPreferenceViewHolder.setSelection(this.mPosition);
    paramPreferenceViewHolder.setOnItemSelectedListener(this.mOnSelectedListener);
  }
  
  protected void performClick(View paramView)
  {
    paramView.findViewById(2131362083).performClick();
  }
  
  public void setAdapter(CycleAdapter paramCycleAdapter)
  {
    this.mAdapter = paramCycleAdapter;
    notifyChanged();
  }
  
  public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mListener = paramOnItemSelectedListener;
  }
  
  public void setSelection(int paramInt)
  {
    this.mPosition = paramInt;
    this.mCurrentObject = this.mAdapter.getItem(this.mPosition);
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\SpinnerPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */