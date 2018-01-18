package com.android.settings;

import android.R.styleable;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class Display
  extends Activity
  implements View.OnClickListener
{
  private DisplayMetrics mDisplayMetrics;
  private float mFontScale = 1.0F;
  private Spinner mFontSize;
  private AdapterView.OnItemSelectedListener mFontSizeChanged = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (paramAnonymousInt == 0) {
        Display.-set0(Display.this, 0.75F);
      }
      for (;;)
      {
        Display.-wrap0(Display.this);
        return;
        if (paramAnonymousInt == 2) {
          Display.-set0(Display.this, 1.25F);
        } else {
          Display.-set0(Display.this, 1.0F);
        }
      }
    }
    
    public void onNothingSelected(AdapterView paramAnonymousAdapterView) {}
  };
  private TextView mPreview;
  private TypedValue mTextSizeTyped;
  
  private void updateFontScale()
  {
    this.mDisplayMetrics.scaledDensity = (this.mDisplayMetrics.density * this.mFontScale);
    float f = this.mTextSizeTyped.getDimension(this.mDisplayMetrics);
    this.mPreview.setTextSize(0, f);
  }
  
  public void onClick(View paramView)
  {
    Settings.System.putFloat(getContentResolver(), "font_scale", this.mFontScale);
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968688);
    this.mFontSize = ((Spinner)findViewById(2131362125));
    this.mFontSize.setOnItemSelectedListener(this.mFontSizeChanged);
    paramBundle = getResources();
    Object localObject = new ArrayAdapter(this, 17367048, new String[] { paramBundle.getString(2131690830), paramBundle.getString(2131690831), paramBundle.getString(2131690832) });
    ((ArrayAdapter)localObject).setDropDownViewResource(17367049);
    this.mFontSize.setAdapter((SpinnerAdapter)localObject);
    this.mPreview = ((TextView)findViewById(2131362126));
    this.mPreview.setText(paramBundle.getText(2131690835));
    localObject = (Button)findViewById(2131362127);
    ((Button)localObject).setText(paramBundle.getText(2131690840));
    ((Button)localObject).setOnClickListener(this);
    this.mTextSizeTyped = new TypedValue();
    paramBundle = obtainStyledAttributes(R.styleable.TextView);
    paramBundle.getValue(2, this.mTextSizeTyped);
    localObject = getResources().getDisplayMetrics();
    this.mDisplayMetrics = new DisplayMetrics();
    this.mDisplayMetrics.density = ((DisplayMetrics)localObject).density;
    this.mDisplayMetrics.heightPixels = ((DisplayMetrics)localObject).heightPixels;
    this.mDisplayMetrics.scaledDensity = ((DisplayMetrics)localObject).scaledDensity;
    this.mDisplayMetrics.widthPixels = ((DisplayMetrics)localObject).widthPixels;
    this.mDisplayMetrics.xdpi = ((DisplayMetrics)localObject).xdpi;
    this.mDisplayMetrics.ydpi = ((DisplayMetrics)localObject).ydpi;
    paramBundle.recycle();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mFontScale = Settings.System.getFloat(getContentResolver(), "font_scale", 1.0F);
    if (this.mFontScale < 1.0F) {
      this.mFontSize.setSelection(0);
    }
    for (;;)
    {
      updateFontScale();
      return;
      if (this.mFontScale > 1.0F) {
        this.mFontSize.setSelection(2);
      } else {
        this.mFontSize.setSelection(1);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\Display.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */