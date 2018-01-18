package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityPicker
  extends AlertActivity
  implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener
{
  private PickAdapter mAdapter;
  private Intent mBaseIntent;
  
  protected Intent getIntentForPosition(int paramInt)
  {
    return ((ActivityPicker.PickAdapter.Item)this.mAdapter.getItem(paramInt)).getIntent(this.mBaseIntent);
  }
  
  protected List<ActivityPicker.PickAdapter.Item> getItems()
  {
    PackageManager localPackageManager = getPackageManager();
    ArrayList localArrayList1 = new ArrayList();
    Object localObject1 = getIntent();
    ArrayList localArrayList2 = ((Intent)localObject1).getStringArrayListExtra("android.intent.extra.shortcut.NAME");
    ArrayList localArrayList3 = ((Intent)localObject1).getParcelableArrayListExtra("android.intent.extra.shortcut.ICON_RESOURCE");
    if ((localArrayList2 != null) && (localArrayList3 != null) && (localArrayList2.size() == localArrayList3.size()))
    {
      int i = 0;
      while (i < localArrayList2.size())
      {
        String str = (String)localArrayList2.get(i);
        localObject1 = null;
        try
        {
          Object localObject2 = (Intent.ShortcutIconResource)localArrayList3.get(i);
          Resources localResources = localPackageManager.getResourcesForApplication(((Intent.ShortcutIconResource)localObject2).packageName);
          localObject2 = localResources.getDrawable(localResources.getIdentifier(((Intent.ShortcutIconResource)localObject2).resourceName, null, null), null);
          localObject1 = localObject2;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;) {}
        }
        localArrayList1.add(new ActivityPicker.PickAdapter.Item(this, str, (Drawable)localObject1));
        i += 1;
      }
    }
    else
    {
      if (this.mBaseIntent != null) {
        putIntentItems(this.mBaseIntent, localArrayList1);
      }
      return localArrayList1;
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    setResult(0);
    finish();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    setResult(-1, getIntentForPosition(paramInt));
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent();
    Object localObject = paramBundle.getParcelableExtra("android.intent.extra.INTENT");
    if ((localObject instanceof Intent))
    {
      this.mBaseIntent = ((Intent)localObject);
      this.mBaseIntent.setFlags(this.mBaseIntent.getFlags() & 0xFF3C);
      localObject = this.mAlertParams;
      ((AlertController.AlertParams)localObject).mOnClickListener = this;
      ((AlertController.AlertParams)localObject).mOnCancelListener = this;
      if (!paramBundle.hasExtra("android.intent.extra.TITLE")) {
        break label141;
      }
    }
    label141:
    for (((AlertController.AlertParams)localObject).mTitle = paramBundle.getStringExtra("android.intent.extra.TITLE");; ((AlertController.AlertParams)localObject).mTitle = getTitle())
    {
      this.mAdapter = new PickAdapter(this, getItems());
      ((AlertController.AlertParams)localObject).mAdapter = this.mAdapter;
      setupAlert();
      return;
      this.mBaseIntent = new Intent("android.intent.action.MAIN", null);
      this.mBaseIntent.addCategory("android.intent.category.DEFAULT");
      break;
    }
  }
  
  protected void putIntentItems(Intent paramIntent, List<ActivityPicker.PickAdapter.Item> paramList)
  {
    PackageManager localPackageManager = getPackageManager();
    paramIntent = localPackageManager.queryIntentActivities(paramIntent, 0);
    Collections.sort(paramIntent, new ResolveInfo.DisplayNameComparator(localPackageManager));
    int j = paramIntent.size();
    int i = 0;
    while (i < j)
    {
      paramList.add(new ActivityPicker.PickAdapter.Item(this, localPackageManager, (ResolveInfo)paramIntent.get(i)));
      i += 1;
    }
  }
  
  private static class EmptyDrawable
    extends Drawable
  {
    private final int mHeight;
    private final int mWidth;
    
    EmptyDrawable(int paramInt1, int paramInt2)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
    }
    
    public void draw(Canvas paramCanvas) {}
    
    public int getIntrinsicHeight()
    {
      return this.mHeight;
    }
    
    public int getIntrinsicWidth()
    {
      return this.mWidth;
    }
    
    public int getMinimumHeight()
    {
      return this.mHeight;
    }
    
    public int getMinimumWidth()
    {
      return this.mWidth;
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
  
  private static class IconResizer
  {
    private final Canvas mCanvas = new Canvas();
    private final int mIconHeight;
    private final int mIconWidth;
    private final DisplayMetrics mMetrics;
    private final Rect mOldBounds = new Rect();
    
    public IconResizer(int paramInt1, int paramInt2, DisplayMetrics paramDisplayMetrics)
    {
      this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
      this.mMetrics = paramDisplayMetrics;
      this.mIconWidth = paramInt1;
      this.mIconHeight = paramInt2;
    }
    
    public Drawable createIconThumbnail(Drawable paramDrawable)
    {
      int j = this.mIconWidth;
      int m = this.mIconHeight;
      if (paramDrawable == null) {
        return new ActivityPicker.EmptyDrawable(j, m);
      }
      k = m;
      int n = j;
      int i1;
      int i2;
      label510:
      label534:
      do
      {
        do
        {
          for (;;)
          {
            float f;
            try
            {
              if ((paramDrawable instanceof PaintDrawable))
              {
                k = m;
                n = j;
                localObject = (PaintDrawable)paramDrawable;
                k = m;
                n = j;
                ((PaintDrawable)localObject).setIntrinsicWidth(j);
                k = m;
                n = j;
                ((PaintDrawable)localObject).setIntrinsicHeight(m);
                k = m;
                n = j;
                i1 = paramDrawable.getIntrinsicWidth();
                k = m;
                n = j;
                i2 = paramDrawable.getIntrinsicHeight();
                localObject = paramDrawable;
                if (i1 > 0)
                {
                  localObject = paramDrawable;
                  if (i2 > 0)
                  {
                    if ((j >= i1) && (m >= i2)) {
                      break;
                    }
                    k = m;
                    n = j;
                    f = i1 / i2;
                    if (i1 <= i2) {
                      break label510;
                    }
                    k = m;
                    n = j;
                    i = (int)(j / f);
                    k = i;
                    n = j;
                    if (paramDrawable.getOpacity() == -1) {
                      break label534;
                    }
                    k = i;
                    n = j;
                    localObject = Bitmap.Config.ARGB_8888;
                    k = i;
                    n = j;
                    localObject = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, (Bitmap.Config)localObject);
                    k = i;
                    n = j;
                    localCanvas = this.mCanvas;
                    k = i;
                    n = j;
                    localCanvas.setBitmap((Bitmap)localObject);
                    k = i;
                    n = j;
                    this.mOldBounds.set(paramDrawable.getBounds());
                    k = i;
                    n = j;
                    m = (this.mIconWidth - j) / 2;
                    k = i;
                    n = j;
                    i1 = (this.mIconHeight - i) / 2;
                    k = i;
                    n = j;
                    paramDrawable.setBounds(m, i1, m + j, i1 + i);
                    k = i;
                    n = j;
                    paramDrawable.draw(localCanvas);
                    k = i;
                    n = j;
                    paramDrawable.setBounds(this.mOldBounds);
                    k = i;
                    n = j;
                    localObject = new BitmapDrawable((Bitmap)localObject);
                    n = i;
                    paramDrawable = (Drawable)localObject;
                    k = j;
                  }
                }
              }
            }
            catch (Throwable paramDrawable)
            {
              i = n;
              n = k;
            }
            try
            {
              ((BitmapDrawable)localObject).setTargetDensity(this.mMetrics);
              n = i;
              paramDrawable = (Drawable)localObject;
              k = j;
              localCanvas.setBitmap(null);
              return (Drawable)localObject;
            }
            catch (Throwable paramDrawable)
            {
              for (;;)
              {
                int i3;
                i = k;
              }
            }
            k = m;
            n = j;
            if ((paramDrawable instanceof BitmapDrawable))
            {
              k = m;
              n = j;
              localObject = (BitmapDrawable)paramDrawable;
              k = m;
              n = j;
              if (((BitmapDrawable)localObject).getBitmap().getDensity() == 0)
              {
                k = m;
                n = j;
                ((BitmapDrawable)localObject).setTargetDensity(this.mMetrics);
                continue;
                return new ActivityPicker.EmptyDrawable(i, n);
                i = m;
                if (i2 > i1)
                {
                  j = (int)(m * f);
                  i = m;
                  continue;
                  k = i;
                  n = j;
                  localObject = Bitmap.Config.RGB_565;
                }
              }
            }
          }
          localObject = paramDrawable;
        } while (i1 >= j);
        localObject = paramDrawable;
      } while (i2 >= m);
      k = m;
      n = j;
      Object localObject = Bitmap.Config.ARGB_8888;
      k = m;
      n = j;
      localObject = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, (Bitmap.Config)localObject);
      k = m;
      n = j;
      Canvas localCanvas = this.mCanvas;
      k = m;
      n = j;
      localCanvas.setBitmap((Bitmap)localObject);
      k = m;
      n = j;
      this.mOldBounds.set(paramDrawable.getBounds());
      k = m;
      n = j;
      i = (j - i1) / 2;
      k = m;
      n = j;
      i3 = (m - i2) / 2;
      k = m;
      n = j;
      paramDrawable.setBounds(i, i3, i + i1, i3 + i2);
      k = m;
      n = j;
      paramDrawable.draw(localCanvas);
      k = m;
      n = j;
      paramDrawable.setBounds(this.mOldBounds);
      k = m;
      n = j;
      localObject = new BitmapDrawable((Bitmap)localObject);
      n = m;
      paramDrawable = (Drawable)localObject;
      k = j;
      ((BitmapDrawable)localObject).setTargetDensity(this.mMetrics);
      n = m;
      paramDrawable = (Drawable)localObject;
      k = j;
      localCanvas.setBitmap(null);
      return (Drawable)localObject;
    }
  }
  
  protected static class PickAdapter
    extends BaseAdapter
  {
    private final LayoutInflater mInflater;
    private final List<Item> mItems;
    
    public PickAdapter(Context paramContext, List<Item> paramList)
    {
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mItems = paramList;
    }
    
    public int getCount()
    {
      return this.mItems.size();
    }
    
    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      View localView = paramView;
      if (paramView == null) {
        localView = this.mInflater.inflate(2130968887, paramViewGroup, false);
      }
      paramView = (Item)getItem(paramInt);
      paramViewGroup = (TextView)localView;
      paramViewGroup.setText(paramView.label);
      paramViewGroup.setCompoundDrawablesWithIntrinsicBounds(paramView.icon, null, null, null);
      return localView;
    }
    
    public static class Item
      implements AppWidgetLoader.LabelledItem
    {
      protected static ActivityPicker.IconResizer sResizer;
      String className;
      Bundle extras;
      Drawable icon;
      CharSequence label;
      String packageName;
      
      Item(Context paramContext, PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
      {
        this.label = paramResolveInfo.loadLabel(paramPackageManager);
        if ((this.label == null) && (paramResolveInfo.activityInfo != null)) {
          this.label = paramResolveInfo.activityInfo.name;
        }
        this.icon = getResizer(paramContext).createIconThumbnail(paramResolveInfo.loadIcon(paramPackageManager));
        this.packageName = paramResolveInfo.activityInfo.applicationInfo.packageName;
        this.className = paramResolveInfo.activityInfo.name;
      }
      
      Item(Context paramContext, CharSequence paramCharSequence, Drawable paramDrawable)
      {
        this.label = paramCharSequence;
        this.icon = getResizer(paramContext).createIconThumbnail(paramDrawable);
      }
      
      Intent getIntent(Intent paramIntent)
      {
        paramIntent = new Intent(paramIntent);
        if ((this.packageName != null) && (this.className != null))
        {
          paramIntent.setClassName(this.packageName, this.className);
          if (this.extras != null) {
            paramIntent.putExtras(this.extras);
          }
          return paramIntent;
        }
        paramIntent.setAction("android.intent.action.CREATE_SHORTCUT");
        paramIntent.putExtra("android.intent.extra.shortcut.NAME", this.label);
        return paramIntent;
      }
      
      public CharSequence getLabel()
      {
        return this.label;
      }
      
      protected ActivityPicker.IconResizer getResizer(Context paramContext)
      {
        if (sResizer == null)
        {
          paramContext = paramContext.getResources();
          int i = (int)paramContext.getDimension(17104896);
          sResizer = new ActivityPicker.IconResizer(i, i, paramContext.getDisplayMetrics());
        }
        return sResizer;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ActivityPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */