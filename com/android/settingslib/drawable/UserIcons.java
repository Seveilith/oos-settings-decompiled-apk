package com.android.settingslib.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

public class UserIcons
{
  private static final int[] USER_ICON_COLORS = { Color.parseColor("#FFCC6F4E"), Color.parseColor("#FFEB9413"), Color.parseColor("#FF8BC34A"), Color.parseColor("#FF673AB7"), Color.parseColor("#FF02BCD4"), Color.parseColor("#FFE91E63"), Color.parseColor("#FF9C27B0") };
  
  public static Bitmap convertToBitmap(Drawable paramDrawable)
  {
    if (paramDrawable == null) {
      return null;
    }
    int i = paramDrawable.getIntrinsicWidth();
    int j = paramDrawable.getIntrinsicHeight();
    Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    paramDrawable.setBounds(0, 0, i, j);
    paramDrawable.draw(localCanvas);
    return localBitmap;
  }
  
  public static Drawable getDefaultUserIcon(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      i = Color.parseColor("#FFFFFFFF");
      if (paramInt != 55536) {
        if (paramInt != 0) {
          break label73;
        }
      }
    }
    label73:
    for (int i = Color.parseColor("#FF2196F3");; i = USER_ICON_COLORS[(paramInt % USER_ICON_COLORS.length)])
    {
      Drawable localDrawable = Resources.getSystem().getDrawable(17302247, null).mutate();
      localDrawable.setColorFilter(i, PorterDuff.Mode.SRC_IN);
      localDrawable.setBounds(0, 0, localDrawable.getIntrinsicWidth(), localDrawable.getIntrinsicHeight());
      return localDrawable;
      i = Color.parseColor("#FF9E9E9E");
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawable\UserIcons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */