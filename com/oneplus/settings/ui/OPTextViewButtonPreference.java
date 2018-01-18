package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPTextViewButtonPreference
  extends RestrictedPreference
{
  private boolean mButtonEnable;
  private String mButtonString;
  private boolean mButtonVisible;
  private Context mContext;
  private Drawable mIcon;
  private ImageView mLeftIcon;
  private View.OnClickListener mOnClickListener;
  private TextView mRightButton;
  private ColorStateList mTextButtonColor;
  private TextView mTextTitle;
  private String mTextTitleString;
  private int resid = 2130968870;
  
  public OPTextViewButtonPreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPTextViewButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPTextViewButtonPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.resid);
    this.mTextTitleString = "";
    this.mButtonString = "";
    this.mIcon = null;
    this.mButtonEnable = false;
    this.mButtonVisible = true;
    this.mTextButtonColor = OPUtils.creatOneplusPrimaryColorStateList(this.mContext);
  }
  
  public String getButtonString()
  {
    return this.mButtonString;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public TextView getLeftButton()
  {
    return this.mRightButton;
  }
  
  public String getLeftTextTitle()
  {
    return this.mTextTitleString;
  }
  
  public CharSequence getTitle()
  {
    return this.mTextTitleString;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mLeftIcon = ((ImageView)paramPreferenceViewHolder.findViewById(2131362277));
    this.mRightButton = ((TextView)paramPreferenceViewHolder.findViewById(2131362282));
    TextView localTextView = this.mRightButton;
    if (this.mButtonVisible) {}
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      this.mRightButton.setTextColor(this.mTextButtonColor);
      this.mRightButton.setOnClickListener(this.mOnClickListener);
      this.mRightButton.setEnabled(this.mButtonEnable);
      this.mRightButton.setText(this.mButtonString);
      this.mTextTitle = ((TextView)paramPreferenceViewHolder.findViewById(2131362279));
      this.mTextTitle.setText(this.mTextTitleString);
      if (this.mIcon != null) {
        this.mLeftIcon.setImageDrawable(this.mIcon);
      }
      return;
    }
  }
  
  public void setButtonEnable(boolean paramBoolean)
  {
    this.mButtonEnable = paramBoolean;
    notifyChanged();
  }
  
  public void setButtonString(String paramString)
  {
    this.mButtonString = paramString;
    notifyChanged();
  }
  
  public void setButtonVisible(boolean paramBoolean)
  {
    this.mButtonVisible = paramBoolean;
    notifyChanged();
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    notifyChanged();
  }
  
  public void setLeftTextTitle(String paramString)
  {
    this.mTextTitleString = paramString;
    notifyChanged();
  }
  
  public void setOnButtonClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnClickListener = paramOnClickListener;
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    setLeftTextTitle(paramCharSequence.toString());
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPTextViewButtonPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */