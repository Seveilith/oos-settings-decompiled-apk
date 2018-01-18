package com.android.setupwizardlib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.setupwizardlib.util.RequireScrollHelper;
import com.android.setupwizardlib.view.BottomScrollView;
import com.android.setupwizardlib.view.Illustration;
import com.android.setupwizardlib.view.NavigationBar;

public class SetupWizardLayout
  extends TemplateLayout
{
  private static final String TAG = "SetupWizardLayout";
  private ColorStateList mProgressBarColor;
  
  public SetupWizardLayout(Context paramContext)
  {
    super(paramContext, 0, 0);
    init(null, R.attr.suwLayoutTheme);
  }
  
  public SetupWizardLayout(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0);
  }
  
  public SetupWizardLayout(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    init(null, R.attr.suwLayoutTheme);
  }
  
  public SetupWizardLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, R.attr.suwLayoutTheme);
  }
  
  @TargetApi(11)
  public SetupWizardLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private Drawable getIllustration(int paramInt1, int paramInt2)
  {
    Context localContext = getContext();
    return getIllustration(localContext.getResources().getDrawable(paramInt1), localContext.getResources().getDrawable(paramInt2));
  }
  
  @SuppressLint({"RtlHardcoded"})
  private Drawable getIllustration(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    if (getContext().getResources().getBoolean(R.bool.suwUseTabletLayout))
    {
      if ((paramDrawable2 instanceof BitmapDrawable))
      {
        ((BitmapDrawable)paramDrawable2).setTileModeX(Shader.TileMode.REPEAT);
        ((BitmapDrawable)paramDrawable2).setGravity(48);
      }
      if ((paramDrawable1 instanceof BitmapDrawable)) {
        ((BitmapDrawable)paramDrawable1).setGravity(51);
      }
      paramDrawable1 = new LayerDrawable(new Drawable[] { paramDrawable2, paramDrawable1 });
      if (Build.VERSION.SDK_INT >= 19) {
        paramDrawable1.setAutoMirrored(true);
      }
      return paramDrawable1;
    }
    if (Build.VERSION.SDK_INT >= 19) {
      paramDrawable1.setAutoMirrored(true);
    }
    return paramDrawable1;
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.SuwSetupWizardLayout, paramInt, 0);
    Object localObject = paramAttributeSet.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackground);
    if (localObject != null)
    {
      setLayoutBackground((Drawable)localObject);
      localObject = paramAttributeSet.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustration);
      if (localObject == null) {
        break label192;
      }
      setIllustration((Drawable)localObject);
    }
    for (;;)
    {
      int i = paramAttributeSet.getDimensionPixelSize(R.styleable.SuwSetupWizardLayout_suwDecorPaddingTop, -1);
      paramInt = i;
      if (i == -1) {
        paramInt = getResources().getDimensionPixelSize(R.dimen.suw_decor_padding_top);
      }
      setDecorPaddingTop(paramInt);
      float f2 = paramAttributeSet.getFloat(R.styleable.SuwSetupWizardLayout_suwIllustrationAspectRatio, -1.0F);
      float f1 = f2;
      if (f2 == -1.0F)
      {
        localObject = new TypedValue();
        getResources().getValue(R.dimen.suw_illustration_aspect_ratio, (TypedValue)localObject, true);
        f1 = ((TypedValue)localObject).getFloat();
      }
      setIllustrationAspectRatio(f1);
      localObject = paramAttributeSet.getText(R.styleable.SuwSetupWizardLayout_suwHeaderText);
      if (localObject != null) {
        setHeaderText((CharSequence)localObject);
      }
      paramAttributeSet.recycle();
      return;
      localObject = paramAttributeSet.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackgroundTile);
      if (localObject == null) {
        break;
      }
      setBackgroundTile((Drawable)localObject);
      break;
      label192:
      localObject = paramAttributeSet.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustrationImage);
      Drawable localDrawable = paramAttributeSet.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustrationHorizontalTile);
      if ((localObject != null) && (localDrawable != null)) {
        setIllustration((Drawable)localObject, localDrawable);
      }
    }
  }
  
  private void setBackgroundTile(Drawable paramDrawable)
  {
    if ((paramDrawable instanceof BitmapDrawable)) {
      ((BitmapDrawable)paramDrawable).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }
    setLayoutBackground(paramDrawable);
  }
  
  private void setIllustration(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if ((localView instanceof Illustration)) {
      ((Illustration)localView).setIllustration(getIllustration(paramDrawable1, paramDrawable2));
    }
  }
  
  protected ViewGroup findContainer(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.id.suw_layout_content;
    }
    return super.findContainer(i);
  }
  
  protected View findManagedViewById(int paramInt)
  {
    return findViewById(paramInt);
  }
  
  public CharSequence getHeaderText()
  {
    CharSequence localCharSequence = null;
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localCharSequence = localTextView.getText();
    }
    return localCharSequence;
  }
  
  public TextView getHeaderTextView()
  {
    return (TextView)findManagedViewById(R.id.suw_layout_title);
  }
  
  public NavigationBar getNavigationBar()
  {
    View localView = findManagedViewById(R.id.suw_layout_navigation_bar);
    if ((localView instanceof NavigationBar)) {
      return (NavigationBar)localView;
    }
    return null;
  }
  
  public ColorStateList getProgressBarColor()
  {
    return this.mProgressBarColor;
  }
  
  public ScrollView getScrollView()
  {
    View localView = findManagedViewById(R.id.suw_bottom_scroll_view);
    if ((localView instanceof ScrollView)) {
      return (ScrollView)localView;
    }
    return null;
  }
  
  @Deprecated
  public void hideProgressBar()
  {
    setProgressBarShown(false);
  }
  
  public boolean isProgressBarShown()
  {
    boolean bool2 = false;
    View localView = findManagedViewById(R.id.suw_layout_progress);
    boolean bool1 = bool2;
    if (localView != null)
    {
      bool1 = bool2;
      if (localView.getVisibility() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected View onInflateTemplate(LayoutInflater paramLayoutInflater, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = R.layout.suw_template;
    }
    try
    {
      paramLayoutInflater = super.onInflateTemplate(paramLayoutInflater, i);
      return paramLayoutInflater;
    }
    catch (RuntimeException paramLayoutInflater)
    {
      throw new InflateException("Unable to inflate layout. Are you using @style/SuwThemeMaterial (or its descendant) as your theme?", paramLayoutInflater);
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      Log.w("SetupWizardLayout", "Ignoring restore instance state " + paramParcelable);
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (paramParcelable.mIsProgressBarShown)
    {
      showProgressBar();
      return;
    }
    hideProgressBar();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.mIsProgressBarShown = isProgressBarShown();
    return localSavedState;
  }
  
  public void requireScrollToBottom()
  {
    NavigationBar localNavigationBar = getNavigationBar();
    ScrollView localScrollView = getScrollView();
    if ((localNavigationBar != null) && ((localScrollView instanceof BottomScrollView)))
    {
      RequireScrollHelper.requireScroll(localNavigationBar, (BottomScrollView)localScrollView);
      return;
    }
    Log.e("SetupWizardLayout", "Both suw_layout_navigation_bar and suw_bottom_scroll_view must exist in the template to require scrolling.");
  }
  
  public void setBackgroundTile(int paramInt)
  {
    setBackgroundTile(getContext().getResources().getDrawable(paramInt));
  }
  
  public void setDecorPaddingTop(int paramInt)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if (localView != null) {
      localView.setPadding(localView.getPaddingLeft(), paramInt, localView.getPaddingRight(), localView.getPaddingBottom());
    }
  }
  
  public void setHeaderText(int paramInt)
  {
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localTextView.setText(paramInt);
    }
  }
  
  public void setHeaderText(CharSequence paramCharSequence)
  {
    TextView localTextView = getHeaderTextView();
    if (localTextView != null) {
      localTextView.setText(paramCharSequence);
    }
  }
  
  public void setIllustration(int paramInt1, int paramInt2)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if ((localView instanceof Illustration)) {
      ((Illustration)localView).setIllustration(getIllustration(paramInt1, paramInt2));
    }
  }
  
  public void setIllustration(Drawable paramDrawable)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if ((localView instanceof Illustration)) {
      ((Illustration)localView).setIllustration(paramDrawable);
    }
  }
  
  public void setIllustrationAspectRatio(float paramFloat)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if ((localView instanceof Illustration)) {
      ((Illustration)localView).setAspectRatio(paramFloat);
    }
  }
  
  public void setLayoutBackground(Drawable paramDrawable)
  {
    View localView = findManagedViewById(R.id.suw_layout_decor);
    if (localView != null) {
      localView.setBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setProgressBarColor(ColorStateList paramColorStateList)
  {
    this.mProgressBarColor = paramColorStateList;
    if (Build.VERSION.SDK_INT >= 21)
    {
      ProgressBar localProgressBar = (ProgressBar)findViewById(R.id.suw_layout_progress);
      if (localProgressBar != null) {
        localProgressBar.setIndeterminateTintList(paramColorStateList);
      }
    }
  }
  
  public void setProgressBarShown(boolean paramBoolean)
  {
    Object localObject = findManagedViewById(R.id.suw_layout_progress);
    int i;
    if (localObject != null) {
      if (paramBoolean)
      {
        i = 0;
        ((View)localObject).setVisibility(i);
      }
    }
    do
    {
      do
      {
        return;
        i = 8;
        break;
      } while (!paramBoolean);
      localObject = (ViewStub)findManagedViewById(R.id.suw_layout_progress_stub);
      if (localObject != null) {
        ((ViewStub)localObject).inflate();
      }
    } while (this.mProgressBarColor == null);
    setProgressBarColor(this.mProgressBarColor);
  }
  
  @Deprecated
  public void showProgressBar()
  {
    setProgressBarShown(true);
  }
  
  protected static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SetupWizardLayout.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SetupWizardLayout.SavedState(paramAnonymousParcel);
      }
      
      public SetupWizardLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SetupWizardLayout.SavedState[paramAnonymousInt];
      }
    };
    boolean mIsProgressBarShown = false;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.mIsProgressBarShown = bool;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.mIsProgressBarShown) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\SetupWizardLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */