package com.airbnb.lottie;

import android.support.annotation.RestrictTo;
import android.support.v4.os.TraceCompat;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
public class L
{
  public static final boolean DBG = false;
  private static final int MAX_DEPTH = 20;
  public static final String TAG = "LOTTIE";
  private static int depthPastMaxDepth = 0;
  private static String[] sections;
  private static long[] startTimeNs;
  private static int traceDepth;
  private static boolean traceEnabled = false;
  
  static
  {
    traceDepth = 0;
  }
  
  public static void beginSection(String paramString)
  {
    if (traceEnabled)
    {
      if (traceDepth != 20)
      {
        sections[traceDepth] = paramString;
        startTimeNs[traceDepth] = System.nanoTime();
        TraceCompat.beginSection(paramString);
        traceDepth += 1;
      }
    }
    else {
      return;
    }
    depthPastMaxDepth += 1;
  }
  
  public static float endSection(String paramString)
  {
    if (depthPastMaxDepth <= 0)
    {
      if (traceEnabled)
      {
        traceDepth -= 1;
        if (traceDepth == -1) {
          break label72;
        }
        if (!paramString.equals(sections[traceDepth])) {
          break label82;
        }
        TraceCompat.endSection();
        return (float)(System.nanoTime() - startTimeNs[traceDepth]) / 1000000.0F;
      }
    }
    else
    {
      depthPastMaxDepth -= 1;
      return 0.0F;
    }
    return 0.0F;
    label72:
    throw new IllegalStateException("Can't end trace section. There are none.");
    label82:
    throw new IllegalStateException("Unbalanced trace call " + paramString + ". Expected " + sections[traceDepth] + ".");
  }
  
  public static void setTraceEnabled(boolean paramBoolean)
  {
    if (traceEnabled != paramBoolean)
    {
      traceEnabled = paramBoolean;
      if (traceEnabled) {}
    }
    else
    {
      return;
    }
    sections = new String[20];
    startTimeNs = new long[20];
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\L.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */