package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.setupwizardlib.R.id;
import com.android.setupwizardlib.R.layout;
import com.android.setupwizardlib.R.styleable;

public class Item
  extends AbstractItem
{
  private boolean mEnabled = true;
  private Drawable mIcon;
  private int mLayoutRes;
  private CharSequence mSummary;
  private CharSequence mTitle;
  private boolean mVisible = true;
  
  public Item()
  {
    this.mLayoutRes = getDefaultLayoutResource();
  }
  
  public Item(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SuwItem);
    this.mEnabled = paramContext.getBoolean(R.styleable.SuwItem_android_enabled, true);
    this.mIcon = paramContext.getDrawable(R.styleable.SuwItem_android_icon);
    this.mTitle = paramContext.getText(R.styleable.SuwItem_android_title);
    this.mSummary = paramContext.getText(R.styleable.SuwItem_android_summary);
    this.mLayoutRes = paramContext.getResourceId(R.styleable.SuwItem_android_layout, getDefaultLayoutResource());
    this.mVisible = paramContext.getBoolean(R.styleable.SuwItem_android_visible, true);
    paramContext.recycle();
  }
  
  public int getCount()
  {
    if (isVisible()) {
      return 1;
    }
    return 0;
  }
  
  protected int getDefaultLayoutResource()
  {
    return R.layout.suw_items_default;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public int getLayoutResource()
  {
    return this.mLayoutRes;
  }
  
  public CharSequence getSummary()
  {
    return this.mSummary;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public int getViewId()
  {
    return getId();
  }
  
  public boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  public void onBindView(View paramView)
  {
    ((TextView)paramView.findViewById(R.id.suw_items_title)).setText(getTitle());
    Object localObject1 = (TextView)paramView.findViewById(R.id.suw_items_summary);
    Object localObject2 = getSummary();
    if ((localObject2 != null) && (((CharSequence)localObject2).length() > 0))
    {
      ((TextView)localObject1).setText((CharSequence)localObject2);
      ((TextView)localObject1).setVisibility(0);
      localObject1 = paramView.findViewById(R.id.suw_items_icon_container);
      localObject2 = getIcon();
      if (localObject2 == null) {
        break label139;
      }
      ImageView localImageView = (ImageView)paramView.findViewById(R.id.suw_items_icon);
      localImageView.setImageDrawable(null);
      localImageView.setImageState(((Drawable)localObject2).getState(), false);
      localImageView.setImageLevel(((Drawable)localObject2).getLevel());
      localImageView.setImageDrawable((Drawable)localObject2);
      ((View)localObject1).setVisibility(0);
    }
    for (;;)
    {
      paramView.setId(getViewId());
      return;
      ((TextView)localObject1).setVisibility(8);
      break;
      label139:
      ((View)localObject1).setVisibility(8);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
  }
  
  public void setLayoutResource(int paramInt)
  {
    this.mLayoutRes = paramInt;
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    this.mSummary = paramCharSequence;
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\Item.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */