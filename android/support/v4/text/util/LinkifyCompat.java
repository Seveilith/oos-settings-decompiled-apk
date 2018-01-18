package android.support.v4.text.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat
{
  private static final Comparator<LinkSpec> COMPARATOR = new Comparator()
  {
    public final int compare(LinkifyCompat.LinkSpec paramAnonymousLinkSpec1, LinkifyCompat.LinkSpec paramAnonymousLinkSpec2)
    {
      if (paramAnonymousLinkSpec1.start < paramAnonymousLinkSpec2.start) {
        return -1;
      }
      if (paramAnonymousLinkSpec1.start > paramAnonymousLinkSpec2.start) {
        return 1;
      }
      if (paramAnonymousLinkSpec1.end < paramAnonymousLinkSpec2.end) {
        return 1;
      }
      if (paramAnonymousLinkSpec1.end > paramAnonymousLinkSpec2.end) {
        return -1;
      }
      return 0;
    }
  };
  private static final String[] EMPTY_STRING = new String[0];
  
  private static void addLinkMovementMethod(@NonNull TextView paramTextView)
  {
    MovementMethod localMovementMethod = paramTextView.getMovementMethod();
    if ((localMovementMethod != null) && ((localMovementMethod instanceof LinkMovementMethod))) {}
    while (!paramTextView.getLinksClickable()) {
      return;
    }
    paramTextView.setMovementMethod(LinkMovementMethod.getInstance());
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    addLinks(paramTextView, paramPattern, paramString, null, null, null);
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    addLinks(paramTextView, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    SpannableString localSpannableString = SpannableString.valueOf(paramTextView.getText());
    if (addLinks(localSpannableString, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter))
    {
      paramTextView.setText(localSpannableString);
      addLinkMovementMethod(paramTextView);
    }
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, int paramInt)
  {
    if (paramInt == 0) {
      return false;
    }
    Object localObject1 = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    int i = localObject1.length - 1;
    while (i >= 0)
    {
      paramSpannable.removeSpan(localObject1[i]);
      i -= 1;
    }
    if ((paramInt & 0x4) != 0) {
      Linkify.addLinks(paramSpannable, 4);
    }
    localObject1 = new ArrayList();
    Object localObject2;
    if ((paramInt & 0x1) != 0)
    {
      localObject2 = PatternsCompat.AUTOLINK_WEB_URL;
      Linkify.MatchFilter localMatchFilter = Linkify.sUrlMatchFilter;
      gatherLinks((ArrayList)localObject1, paramSpannable, (Pattern)localObject2, new String[] { "http://", "https://", "rtsp://" }, localMatchFilter, null);
    }
    if ((paramInt & 0x2) != 0) {
      gatherLinks((ArrayList)localObject1, paramSpannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[] { "mailto:" }, null, null);
    }
    if ((paramInt & 0x8) != 0) {
      gatherMapLinks((ArrayList)localObject1, paramSpannable);
    }
    pruneOverlaps((ArrayList)localObject1, paramSpannable);
    if (((ArrayList)localObject1).size() == 0) {
      return false;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (LinkSpec)((Iterator)localObject1).next();
      if (((LinkSpec)localObject2).frameworkAddedSpan == null) {
        applyLink(((LinkSpec)localObject2).url, ((LinkSpec)localObject2).start, ((LinkSpec)localObject2).end, paramSpannable);
      }
    }
    return true;
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    return addLinks(paramSpannable, paramPattern, paramString, null, null, null);
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    return addLinks(paramSpannable, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    if (paramArrayOfString != null)
    {
      paramString = paramArrayOfString;
      if (paramArrayOfString.length >= 1) {}
    }
    else
    {
      paramString = EMPTY_STRING;
    }
    String[] arrayOfString = new String[paramString.length + 1];
    arrayOfString[0] = str.toLowerCase(Locale.ROOT);
    int i = 0;
    if (i < paramString.length)
    {
      paramArrayOfString = paramString[i];
      if (paramArrayOfString == null) {}
      for (paramArrayOfString = "";; paramArrayOfString = paramArrayOfString.toLowerCase(Locale.ROOT))
      {
        arrayOfString[(i + 1)] = paramArrayOfString;
        i += 1;
        break;
      }
    }
    boolean bool1 = false;
    paramPattern = paramPattern.matcher(paramSpannable);
    while (paramPattern.find())
    {
      i = paramPattern.start();
      int j = paramPattern.end();
      boolean bool2 = true;
      if (paramMatchFilter != null) {
        bool2 = paramMatchFilter.acceptMatch(paramSpannable, i, j);
      }
      if (bool2)
      {
        applyLink(makeUrl(paramPattern.group(0), arrayOfString, paramPattern, paramTransformFilter), i, j, paramSpannable);
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static final boolean addLinks(@NonNull TextView paramTextView, int paramInt)
  {
    if (paramInt == 0) {
      return false;
    }
    Object localObject = paramTextView.getText();
    if ((localObject instanceof Spannable))
    {
      if (addLinks((Spannable)localObject, paramInt))
      {
        addLinkMovementMethod(paramTextView);
        return true;
      }
      return false;
    }
    localObject = SpannableString.valueOf((CharSequence)localObject);
    if (addLinks((Spannable)localObject, paramInt))
    {
      addLinkMovementMethod(paramTextView);
      paramTextView.setText((CharSequence)localObject);
      return true;
    }
    return false;
  }
  
  private static void applyLink(String paramString, int paramInt1, int paramInt2, Spannable paramSpannable)
  {
    paramSpannable.setSpan(new URLSpan(paramString), paramInt1, paramInt2, 33);
  }
  
  private static void gatherLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable, Pattern paramPattern, String[] paramArrayOfString, Linkify.MatchFilter paramMatchFilter, Linkify.TransformFilter paramTransformFilter)
  {
    paramPattern = paramPattern.matcher(paramSpannable);
    while (paramPattern.find())
    {
      int i = paramPattern.start();
      int j = paramPattern.end();
      if ((paramMatchFilter == null) || (paramMatchFilter.acceptMatch(paramSpannable, i, j)))
      {
        LinkSpec localLinkSpec = new LinkSpec();
        localLinkSpec.url = makeUrl(paramPattern.group(0), paramArrayOfString, paramPattern, paramTransformFilter);
        localLinkSpec.start = i;
        localLinkSpec.end = j;
        paramArrayList.add(localLinkSpec);
      }
    }
  }
  
  /* Error */
  private static final void gatherMapLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 241 1 0
    //   6: astore_1
    //   7: iconst_0
    //   8: istore_2
    //   9: aload_1
    //   10: invokestatic 247	android/webkit/WebView:findAddress	(Ljava/lang/String;)Ljava/lang/String;
    //   13: astore 6
    //   15: aload 6
    //   17: ifnull +115 -> 132
    //   20: aload_1
    //   21: aload 6
    //   23: invokevirtual 251	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   26: istore_3
    //   27: iload_3
    //   28: ifge +4 -> 32
    //   31: return
    //   32: new 8	android/support/v4/text/util/LinkifyCompat$LinkSpec
    //   35: dup
    //   36: invokespecial 227	android/support/v4/text/util/LinkifyCompat$LinkSpec:<init>	()V
    //   39: astore 5
    //   41: iload_3
    //   42: aload 6
    //   44: invokevirtual 252	java/lang/String:length	()I
    //   47: iadd
    //   48: istore 4
    //   50: aload 5
    //   52: iload_2
    //   53: iload_3
    //   54: iadd
    //   55: putfield 167	android/support/v4/text/util/LinkifyCompat$LinkSpec:start	I
    //   58: aload 5
    //   60: iload_2
    //   61: iload 4
    //   63: iadd
    //   64: putfield 170	android/support/v4/text/util/LinkifyCompat$LinkSpec:end	I
    //   67: aload_1
    //   68: iload 4
    //   70: invokevirtual 255	java/lang/String:substring	(I)Ljava/lang/String;
    //   73: astore_1
    //   74: iload_2
    //   75: iload 4
    //   77: iadd
    //   78: istore_2
    //   79: aload 6
    //   81: ldc_w 257
    //   84: invokestatic 263	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   87: astore 6
    //   89: aload 5
    //   91: new 265	java/lang/StringBuilder
    //   94: dup
    //   95: invokespecial 266	java/lang/StringBuilder:<init>	()V
    //   98: ldc_w 268
    //   101: invokevirtual 272	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: aload 6
    //   106: invokevirtual 272	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   112: putfield 163	android/support/v4/text/util/LinkifyCompat$LinkSpec:url	Ljava/lang/String;
    //   115: aload_0
    //   116: aload 5
    //   118: invokevirtual 231	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   121: pop
    //   122: goto -113 -> 9
    //   125: astore_0
    //   126: return
    //   127: astore 5
    //   129: goto -120 -> 9
    //   132: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	paramArrayList	ArrayList<LinkSpec>
    //   0	133	1	paramSpannable	Spannable
    //   8	71	2	i	int
    //   26	29	3	j	int
    //   48	30	4	k	int
    //   39	78	5	localLinkSpec	LinkSpec
    //   127	1	5	localUnsupportedEncodingException	java.io.UnsupportedEncodingException
    //   13	92	6	str	String
    // Exception table:
    //   from	to	target	type
    //   9	15	125	java/lang/UnsupportedOperationException
    //   20	27	125	java/lang/UnsupportedOperationException
    //   32	74	125	java/lang/UnsupportedOperationException
    //   79	89	125	java/lang/UnsupportedOperationException
    //   89	122	125	java/lang/UnsupportedOperationException
    //   79	89	127	java/io/UnsupportedEncodingException
  }
  
  private static String makeUrl(@NonNull String paramString, @NonNull String[] paramArrayOfString, Matcher paramMatcher, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    String str = paramString;
    if (paramTransformFilter != null) {
      str = paramTransformFilter.transformUrl(paramMatcher, paramString);
    }
    int k = 0;
    int i = 0;
    for (;;)
    {
      int j = k;
      paramString = str;
      if (i < paramArrayOfString.length)
      {
        if (!str.regionMatches(true, 0, paramArrayOfString[i], 0, paramArrayOfString[i].length())) {
          break label165;
        }
        k = 1;
        j = k;
        paramString = str;
        if (!str.regionMatches(false, 0, paramArrayOfString[i], 0, paramArrayOfString[i].length()))
        {
          paramString = paramArrayOfString[i] + str.substring(paramArrayOfString[i].length());
          j = k;
        }
      }
      paramMatcher = paramString;
      if (j == 0)
      {
        paramMatcher = paramString;
        if (paramArrayOfString.length > 0) {
          paramMatcher = paramArrayOfString[0] + paramString;
        }
      }
      return paramMatcher;
      label165:
      i += 1;
    }
  }
  
  private static final void pruneOverlaps(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    Object localObject = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    int i = 0;
    LinkSpec localLinkSpec;
    while (i < localObject.length)
    {
      localLinkSpec = new LinkSpec();
      localLinkSpec.frameworkAddedSpan = localObject[i];
      localLinkSpec.start = paramSpannable.getSpanStart(localObject[i]);
      localLinkSpec.end = paramSpannable.getSpanEnd(localObject[i]);
      paramArrayList.add(localLinkSpec);
      i += 1;
    }
    Collections.sort(paramArrayList, COMPARATOR);
    int k = paramArrayList.size();
    int j = 0;
    while (j < k - 1)
    {
      localObject = (LinkSpec)paramArrayList.get(j);
      localLinkSpec = (LinkSpec)paramArrayList.get(j + 1);
      i = -1;
      if ((((LinkSpec)localObject).start <= localLinkSpec.start) && (((LinkSpec)localObject).end > localLinkSpec.start))
      {
        if (localLinkSpec.end <= ((LinkSpec)localObject).end) {
          i = j + 1;
        }
        for (;;)
        {
          if (i == -1) {
            break label289;
          }
          localObject = ((LinkSpec)paramArrayList.get(i)).frameworkAddedSpan;
          if (localObject != null) {
            paramSpannable.removeSpan(localObject);
          }
          paramArrayList.remove(i);
          k -= 1;
          break;
          if (((LinkSpec)localObject).end - ((LinkSpec)localObject).start > localLinkSpec.end - localLinkSpec.start) {
            i = j + 1;
          } else if (((LinkSpec)localObject).end - ((LinkSpec)localObject).start < localLinkSpec.end - localLinkSpec.start) {
            i = j;
          }
        }
      }
      label289:
      j += 1;
    }
  }
  
  private static class LinkSpec
  {
    int end;
    URLSpan frameworkAddedSpan;
    int start;
    String url;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LinkifyMask {}
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\text\util\LinkifyCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */