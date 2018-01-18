package com.oneplus.lib.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

abstract class GenericInflater<T, P extends Parent>
{
  private static final Class[] mConstructorSignature = { Context.class, AttributeSet.class };
  private static final HashMap sConstructorMap = new HashMap();
  private final boolean DEBUG = false;
  private final Object[] mConstructorArgs = new Object[2];
  protected final Context mContext;
  private String mDefaultPackage;
  private Factory<T> mFactory;
  private boolean mFactorySet;
  
  protected GenericInflater(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  protected GenericInflater(GenericInflater<T, P> paramGenericInflater, Context paramContext)
  {
    this.mContext = paramContext;
    this.mFactory = paramGenericInflater.mFactory;
  }
  
  private final T createItemFromTag(XmlPullParser paramXmlPullParser, String paramString, AttributeSet paramAttributeSet)
  {
    try
    {
      if (this.mFactory == null) {
        paramXmlPullParser = null;
      }
      while (paramXmlPullParser == null) {
        if (-1 == paramString.indexOf('.'))
        {
          return (T)onCreateItem(paramString, paramAttributeSet);
          paramXmlPullParser = this.mFactory.onCreateItem(paramString, this.mContext, paramAttributeSet);
        }
        else
        {
          paramXmlPullParser = createItem(paramString, null, paramAttributeSet);
          return paramXmlPullParser;
        }
      }
    }
    catch (Exception paramXmlPullParser)
    {
      paramString = new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class " + paramString);
      paramString.initCause(paramXmlPullParser);
      throw paramString;
    }
    catch (ClassNotFoundException paramXmlPullParser)
    {
      paramString = new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class " + paramString);
      paramString.initCause(paramXmlPullParser);
      throw paramString;
    }
    catch (InflateException paramXmlPullParser)
    {
      throw paramXmlPullParser;
    }
    return paramXmlPullParser;
  }
  
  private void rInflate(XmlPullParser paramXmlPullParser, T paramT, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        break;
      }
      if ((j == 2) && (!onCreateCustomFromTag(paramXmlPullParser, paramT, paramAttributeSet)))
      {
        Object localObject = createItemFromTag(paramXmlPullParser, paramXmlPullParser.getName(), paramAttributeSet);
        ((Parent)paramT).addItemFromInflater(localObject);
        rInflate(paramXmlPullParser, localObject, paramAttributeSet);
      }
    }
  }
  
  public abstract GenericInflater cloneInContext(Context paramContext);
  
  /* Error */
  public final T createItem(String paramString1, String paramString2, AttributeSet paramAttributeSet)
    throws ClassNotFoundException, InflateException
  {
    // Byte code:
    //   0: getstatic 47	com/oneplus/lib/preference/GenericInflater:sConstructorMap	Ljava/util/HashMap;
    //   3: aload_1
    //   4: invokevirtual 150	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7: checkcast 152	java/lang/reflect/Constructor
    //   10: astore 6
    //   12: aload 6
    //   14: astore 5
    //   16: aload 6
    //   18: ifnonnull +87 -> 105
    //   21: aload 6
    //   23: astore 4
    //   25: aload_0
    //   26: getfield 56	com/oneplus/lib/preference/GenericInflater:mContext	Landroid/content/Context;
    //   29: invokevirtual 156	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   32: astore 7
    //   34: aload_2
    //   35: ifnull +101 -> 136
    //   38: aload 6
    //   40: astore 4
    //   42: new 88	java/lang/StringBuilder
    //   45: dup
    //   46: invokespecial 89	java/lang/StringBuilder:<init>	()V
    //   49: aload_2
    //   50: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_1
    //   54: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: invokevirtual 102	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: astore 5
    //   62: aload 6
    //   64: astore 4
    //   66: aload 7
    //   68: aload 5
    //   70: invokevirtual 162	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   73: getstatic 40	com/oneplus/lib/preference/GenericInflater:mConstructorSignature	[Ljava/lang/Class;
    //   76: invokevirtual 166	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   79: astore 5
    //   81: aload 5
    //   83: astore 4
    //   85: aload 5
    //   87: iconst_1
    //   88: invokevirtual 170	java/lang/reflect/Constructor:setAccessible	(Z)V
    //   91: aload 5
    //   93: astore 4
    //   95: getstatic 47	com/oneplus/lib/preference/GenericInflater:sConstructorMap	Ljava/util/HashMap;
    //   98: aload_1
    //   99: aload 5
    //   101: invokevirtual 174	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   104: pop
    //   105: aload 5
    //   107: astore 4
    //   109: aload_0
    //   110: getfield 54	com/oneplus/lib/preference/GenericInflater:mConstructorArgs	[Ljava/lang/Object;
    //   113: astore 6
    //   115: aload 6
    //   117: iconst_1
    //   118: aload_3
    //   119: aastore
    //   120: aload 5
    //   122: astore 4
    //   124: aload 5
    //   126: aload 6
    //   128: invokevirtual 178	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   131: astore 5
    //   133: aload 5
    //   135: areturn
    //   136: aload_1
    //   137: astore 5
    //   139: goto -77 -> 62
    //   142: astore_1
    //   143: new 65	android/view/InflateException
    //   146: dup
    //   147: new 88	java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial 89	java/lang/StringBuilder:<init>	()V
    //   154: aload_3
    //   155: invokeinterface 93 1 0
    //   160: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: ldc 99
    //   165: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: aload 4
    //   170: invokevirtual 182	java/lang/reflect/Constructor:getClass	()Ljava/lang/Class;
    //   173: invokevirtual 183	java/lang/Class:getName	()Ljava/lang/String;
    //   176: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: invokevirtual 102	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokespecial 105	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   185: astore_2
    //   186: aload_2
    //   187: aload_1
    //   188: invokevirtual 109	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   191: pop
    //   192: aload_2
    //   193: athrow
    //   194: astore_1
    //   195: aload_1
    //   196: athrow
    //   197: astore 4
    //   199: new 88	java/lang/StringBuilder
    //   202: dup
    //   203: invokespecial 89	java/lang/StringBuilder:<init>	()V
    //   206: aload_3
    //   207: invokeinterface 93 1 0
    //   212: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: ldc 99
    //   217: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: astore 5
    //   222: aload_1
    //   223: astore_3
    //   224: aload_2
    //   225: ifnull +22 -> 247
    //   228: new 88	java/lang/StringBuilder
    //   231: dup
    //   232: invokespecial 89	java/lang/StringBuilder:<init>	()V
    //   235: aload_2
    //   236: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: aload_1
    //   240: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: invokevirtual 102	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   246: astore_3
    //   247: new 65	android/view/InflateException
    //   250: dup
    //   251: aload 5
    //   253: aload_3
    //   254: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   257: invokevirtual 102	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   260: invokespecial 105	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   263: astore_1
    //   264: aload_1
    //   265: aload 4
    //   267: invokevirtual 109	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   270: pop
    //   271: aload_1
    //   272: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	273	0	this	GenericInflater
    //   0	273	1	paramString1	String
    //   0	273	2	paramString2	String
    //   0	273	3	paramAttributeSet	AttributeSet
    //   23	146	4	localObject1	Object
    //   197	69	4	localNoSuchMethodException	NoSuchMethodException
    //   14	238	5	localObject2	Object
    //   10	117	6	localObject3	Object
    //   32	35	7	localClassLoader	ClassLoader
    // Exception table:
    //   from	to	target	type
    //   25	34	142	java/lang/Exception
    //   42	62	142	java/lang/Exception
    //   66	81	142	java/lang/Exception
    //   85	91	142	java/lang/Exception
    //   95	105	142	java/lang/Exception
    //   109	115	142	java/lang/Exception
    //   124	133	142	java/lang/Exception
    //   25	34	194	java/lang/ClassNotFoundException
    //   42	62	194	java/lang/ClassNotFoundException
    //   66	81	194	java/lang/ClassNotFoundException
    //   85	91	194	java/lang/ClassNotFoundException
    //   95	105	194	java/lang/ClassNotFoundException
    //   109	115	194	java/lang/ClassNotFoundException
    //   124	133	194	java/lang/ClassNotFoundException
    //   25	34	197	java/lang/NoSuchMethodException
    //   42	62	197	java/lang/NoSuchMethodException
    //   66	81	197	java/lang/NoSuchMethodException
    //   85	91	197	java/lang/NoSuchMethodException
    //   95	105	197	java/lang/NoSuchMethodException
    //   109	115	197	java/lang/NoSuchMethodException
    //   124	133	197	java/lang/NoSuchMethodException
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public String getDefaultPackage()
  {
    return this.mDefaultPackage;
  }
  
  public final Factory<T> getFactory()
  {
    return this.mFactory;
  }
  
  public T inflate(int paramInt, P paramP)
  {
    if (paramP != null) {}
    for (boolean bool = true;; bool = false) {
      return (T)inflate(paramInt, paramP, bool);
    }
  }
  
  public T inflate(int paramInt, P paramP, boolean paramBoolean)
  {
    XmlResourceParser localXmlResourceParser = getContext().getResources().getXml(paramInt);
    try
    {
      paramP = inflate(localXmlResourceParser, paramP, paramBoolean);
      return paramP;
    }
    finally
    {
      localXmlResourceParser.close();
    }
  }
  
  public T inflate(XmlPullParser paramXmlPullParser, P paramP)
  {
    if (paramP != null) {}
    for (boolean bool = true;; bool = false) {
      return (T)inflate(paramXmlPullParser, paramP, bool);
    }
  }
  
  public T inflate(XmlPullParser paramXmlPullParser, P paramP, boolean paramBoolean)
  {
    AttributeSet localAttributeSet;
    synchronized (this.mConstructorArgs)
    {
      localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
      this.mConstructorArgs[0] = this.mContext;
    }
    try
    {
      int i;
      do
      {
        i = paramXmlPullParser.next();
      } while ((i != 2) && (i != 1));
      if (i != 2) {
        throw new InflateException(paramXmlPullParser.getPositionDescription() + ": No start tag found!");
      }
    }
    catch (InflateException paramXmlPullParser)
    {
      throw paramXmlPullParser;
      paramXmlPullParser = finally;
      throw paramXmlPullParser;
      paramP = onMergeRoots(paramP, paramBoolean, (Parent)createItemFromTag(paramXmlPullParser, paramXmlPullParser.getName(), localAttributeSet));
      rInflate(paramXmlPullParser, paramP, localAttributeSet);
      return paramP;
    }
    catch (IOException paramP)
    {
      paramXmlPullParser = new InflateException(paramXmlPullParser.getPositionDescription() + ": " + paramP.getMessage());
      paramXmlPullParser.initCause(paramP);
      throw paramXmlPullParser;
    }
    catch (XmlPullParserException paramXmlPullParser)
    {
      paramP = new InflateException(paramXmlPullParser.getMessage());
      paramP.initCause(paramXmlPullParser);
      throw paramP;
    }
  }
  
  protected boolean onCreateCustomFromTag(XmlPullParser paramXmlPullParser, T paramT, AttributeSet paramAttributeSet)
    throws XmlPullParserException
  {
    return false;
  }
  
  protected T onCreateItem(String paramString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException
  {
    return (T)createItem(paramString, this.mDefaultPackage, paramAttributeSet);
  }
  
  protected P onMergeRoots(P paramP1, boolean paramBoolean, P paramP2)
  {
    return paramP2;
  }
  
  public void setDefaultPackage(String paramString)
  {
    this.mDefaultPackage = paramString;
  }
  
  public void setFactory(Factory<T> paramFactory)
  {
    if (this.mFactorySet) {
      throw new IllegalStateException("A factory has already been set on this inflater");
    }
    if (paramFactory == null) {
      throw new NullPointerException("Given factory can not be null");
    }
    this.mFactorySet = true;
    if (this.mFactory == null)
    {
      this.mFactory = paramFactory;
      return;
    }
    this.mFactory = new FactoryMerger(paramFactory, this.mFactory);
  }
  
  public static abstract interface Factory<T>
  {
    public abstract T onCreateItem(String paramString, Context paramContext, AttributeSet paramAttributeSet);
  }
  
  private static class FactoryMerger<T>
    implements GenericInflater.Factory<T>
  {
    private final GenericInflater.Factory<T> mF1;
    private final GenericInflater.Factory<T> mF2;
    
    FactoryMerger(GenericInflater.Factory<T> paramFactory1, GenericInflater.Factory<T> paramFactory2)
    {
      this.mF1 = paramFactory1;
      this.mF2 = paramFactory2;
    }
    
    public T onCreateItem(String paramString, Context paramContext, AttributeSet paramAttributeSet)
    {
      Object localObject = this.mF1.onCreateItem(paramString, paramContext, paramAttributeSet);
      if (localObject != null) {
        return (T)localObject;
      }
      return (T)this.mF2.onCreateItem(paramString, paramContext, paramAttributeSet);
    }
  }
  
  public static abstract interface Parent<T>
  {
    public abstract void addItemFromInflater(T paramT);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\GenericInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */