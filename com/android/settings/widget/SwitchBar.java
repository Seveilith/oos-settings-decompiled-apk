package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.ArrayList;

public class SwitchBar
  extends LinearLayout
  implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
  private static int[] XML_ATTRIBUTES = { 2130772455, 2130772456, 2130772457 };
  private boolean mDisabledByAdmin = false;
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin = null;
  private String mLabel;
  private String mMetricsTag;
  private View mRestrictedIcon;
  private String mSummary;
  private final TextAppearanceSpan mSummarySpan;
  private ToggleSwitch mSwitch;
  private ArrayList<OnSwitchChangeListener> mSwitchChangeListeners = new ArrayList();
  private TextView mTextView;
  
  public SwitchBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SwitchBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SwitchBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    LayoutInflater.from(paramContext).inflate(2130969075, this);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, XML_ATTRIBUTES);
    paramInt1 = (int)paramContext.getDimension(0, 0.0F);
    paramInt2 = (int)paramContext.getDimension(1, 0.0F);
    paramContext.getColor(2, 0);
    paramContext.recycle();
    this.mTextView = ((TextView)findViewById(2131362606));
    this.mTextView.setImportantForAccessibility(2);
    this.mLabel = getResources().getString(2131693336);
    this.mSummarySpan = new TextAppearanceSpan(this.mContext, 2131821555);
    updateText();
    ((ViewGroup.MarginLayoutParams)this.mTextView.getLayoutParams()).setMarginStart(paramInt1);
    this.mSwitch = ((ToggleSwitch)findViewById(2131362607));
    this.mSwitch.setSaveEnabled(false);
    this.mSwitch.setImportantForAccessibility(2);
    ((ViewGroup.MarginLayoutParams)this.mSwitch.getLayoutParams()).setMarginEnd(paramInt2);
    addOnSwitchChangeListener(new OnSwitchChangeListener()
    {
      public void onSwitchChanged(Switch paramAnonymousSwitch, boolean paramAnonymousBoolean)
      {
        SwitchBar.this.setTextViewLabel(paramAnonymousBoolean);
      }
    });
    this.mRestrictedIcon = findViewById(2131362509);
    setOnClickListener(this);
    setVisibility(8);
  }
  
  private void updateText()
  {
    if (TextUtils.isEmpty(this.mSummary))
    {
      this.mTextView.setText(this.mLabel);
      return;
    }
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(this.mLabel).append('\n');
    int i = localSpannableStringBuilder.length();
    localSpannableStringBuilder.append(this.mSummary);
    localSpannableStringBuilder.setSpan(this.mSummarySpan, i, localSpannableStringBuilder.length(), 0);
    this.mTextView.setText(localSpannableStringBuilder);
  }
  
  public void addOnSwitchChangeListener(OnSwitchChangeListener paramOnSwitchChangeListener)
  {
    if (this.mSwitchChangeListeners.contains(paramOnSwitchChangeListener)) {
      throw new IllegalStateException("Cannot add twice the same OnSwitchChangeListener");
    }
    this.mSwitchChangeListeners.add(paramOnSwitchChangeListener);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return Switch.class.getName();
  }
  
  public final ToggleSwitch getSwitch()
  {
    return this.mSwitch;
  }
  
  public void hide()
  {
    if (isShowing())
    {
      setVisibility(8);
      this.mSwitch.setOnCheckedChangeListener(null);
    }
  }
  
  public boolean isChecked()
  {
    return this.mSwitch.isChecked();
  }
  
  public boolean isShowing()
  {
    boolean bool = false;
    if (getVisibility() == 0) {
      bool = true;
    }
    return bool;
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    propagateChecked(paramBoolean);
  }
  
  public void onClick(View paramView)
  {
    if (this.mDisabledByAdmin)
    {
      MetricsLogger.count(this.mContext, this.mMetricsTag + "/switch_bar|restricted", 1);
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
      return;
    }
    if (this.mSwitch.isChecked()) {}
    for (boolean bool = false;; bool = true)
    {
      MetricsLogger.count(this.mContext, this.mMetricsTag + "/switch_bar|" + bool, 1);
      setChecked(bool);
      return;
    }
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setContentDescription(this.mTextView.getText());
    paramAccessibilityEvent.setChecked(this.mSwitch.isChecked());
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setText(this.mTextView.getText());
    paramAccessibilityNodeInfo.setCheckable(true);
    paramAccessibilityNodeInfo.setChecked(this.mSwitch.isChecked());
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mSwitch.setCheckedInternal(paramParcelable.checked);
    setTextViewLabel(paramParcelable.checked);
    int i;
    ToggleSwitch localToggleSwitch;
    if (paramParcelable.visible)
    {
      i = 0;
      setVisibility(i);
      localToggleSwitch = this.mSwitch;
      if (!paramParcelable.visible) {
        break label76;
      }
    }
    label76:
    for (paramParcelable = this;; paramParcelable = null)
    {
      localToggleSwitch.setOnCheckedChangeListener(paramParcelable);
      requestLayout();
      return;
      i = 8;
      break;
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.checked = this.mSwitch.isChecked();
    localSavedState.visible = isShowing();
    return localSavedState;
  }
  
  public void propagateChecked(boolean paramBoolean)
  {
    int j = this.mSwitchChangeListeners.size();
    int i = 0;
    while (i < j)
    {
      ((OnSwitchChangeListener)this.mSwitchChangeListeners.get(i)).onSwitchChanged(this.mSwitch, paramBoolean);
      i += 1;
    }
  }
  
  public void removeOnSwitchChangeListener(OnSwitchChangeListener paramOnSwitchChangeListener)
  {
    if (!this.mSwitchChangeListeners.contains(paramOnSwitchChangeListener)) {
      throw new IllegalStateException("Cannot remove OnSwitchChangeListener");
    }
    this.mSwitchChangeListeners.remove(paramOnSwitchChangeListener);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    setTextViewLabel(paramBoolean);
    this.mSwitch.setChecked(paramBoolean);
  }
  
  public void setCheckedInternal(boolean paramBoolean)
  {
    setTextViewLabel(paramBoolean);
    this.mSwitch.setCheckedInternal(paramBoolean);
  }
  
  public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    this.mEnforcedAdmin = paramEnforcedAdmin;
    if (paramEnforcedAdmin != null)
    {
      super.setEnabled(true);
      this.mDisabledByAdmin = true;
      this.mTextView.setEnabled(false);
      this.mSwitch.setEnabled(false);
      this.mSwitch.setVisibility(8);
      this.mRestrictedIcon.setVisibility(0);
      return;
    }
    this.mDisabledByAdmin = false;
    this.mSwitch.setVisibility(0);
    this.mRestrictedIcon.setVisibility(8);
    setEnabled(true);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mDisabledByAdmin))
    {
      setDisabledByAdmin(null);
      return;
    }
    super.setEnabled(paramBoolean);
    this.mTextView.setEnabled(paramBoolean);
    this.mSwitch.setEnabled(paramBoolean);
  }
  
  public void setMetricsTag(String paramString)
  {
    this.mMetricsTag = paramString;
  }
  
  public void setSummary(String paramString)
  {
    this.mSummary = paramString;
    updateText();
  }
  
  public void setTextViewLabel(boolean paramBoolean)
  {
    Resources localResources = getResources();
    if (paramBoolean) {}
    for (int i = 2131693335;; i = 2131693336)
    {
      this.mLabel = localResources.getString(i);
      updateText();
      return;
    }
  }
  
  public void show()
  {
    if (!isShowing())
    {
      setVisibility(0);
      this.mSwitch.setOnCheckedChangeListener(this);
    }
  }
  
  public static abstract interface OnSwitchChangeListener
  {
    public abstract void onSwitchChanged(Switch paramSwitch, boolean paramBoolean);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SwitchBar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SwitchBar.SavedState(paramAnonymousParcel, null);
      }
      
      public SwitchBar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SwitchBar.SavedState[paramAnonymousInt];
      }
    };
    boolean checked;
    boolean visible;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.checked = ((Boolean)paramParcel.readValue(null)).booleanValue();
      this.visible = ((Boolean)paramParcel.readValue(null)).booleanValue();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "SwitchBar.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + " visible=" + this.visible + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeValue(Boolean.valueOf(this.checked));
      paramParcel.writeValue(Boolean.valueOf(this.visible));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\SwitchBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */