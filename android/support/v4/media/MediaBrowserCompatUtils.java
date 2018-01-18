package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class MediaBrowserCompatUtils
{
  public static boolean areSameOptions(Bundle paramBundle1, Bundle paramBundle2)
  {
    boolean bool3 = false;
    boolean bool2 = false;
    if (paramBundle1 == paramBundle2) {
      return true;
    }
    if (paramBundle1 == null)
    {
      if (paramBundle2.getInt("android.media.browse.extra.PAGE", -1) == -1) {
        return paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1;
      }
      return false;
    }
    if (paramBundle2 == null)
    {
      bool1 = bool2;
      if (paramBundle1.getInt("android.media.browse.extra.PAGE", -1) == -1)
      {
        bool1 = bool2;
        if (paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1) {
          bool1 = true;
        }
      }
      return bool1;
    }
    boolean bool1 = bool3;
    if (paramBundle1.getInt("android.media.browse.extra.PAGE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE", -1))
    {
      bool1 = bool3;
      if (paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1)) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean hasDuplicatedItems(Bundle paramBundle1, Bundle paramBundle2)
  {
    int k;
    int i;
    label13:
    int m;
    label20:
    int j;
    if (paramBundle1 == null)
    {
      k = -1;
      if (paramBundle2 != null) {
        break label86;
      }
      i = -1;
      if (paramBundle1 != null) {
        break label97;
      }
      m = -1;
      if (paramBundle2 != null) {
        break label109;
      }
      j = -1;
      label26:
      if ((k != -1) && (m != -1)) {
        break label120;
      }
      m = 0;
      k = Integer.MAX_VALUE;
      label45:
      if ((i != -1) && (j != -1)) {
        break label143;
      }
      j = 0;
      i = Integer.MAX_VALUE;
    }
    for (;;)
    {
      if ((m > j) || (j > k)) {
        break label161;
      }
      return true;
      k = paramBundle1.getInt("android.media.browse.extra.PAGE", -1);
      break;
      label86:
      i = paramBundle2.getInt("android.media.browse.extra.PAGE", -1);
      break label13;
      label97:
      m = paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1);
      break label20;
      label109:
      j = paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1);
      break label26;
      label120:
      int n = m * k;
      k = n + m - 1;
      m = n;
      break label45;
      label143:
      n = j * i;
      i = n + j - 1;
      j = n;
    }
    label161:
    return (m <= i) && (i <= k);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\MediaBrowserCompatUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */