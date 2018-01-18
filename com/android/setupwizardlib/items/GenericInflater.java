package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class GenericInflater<T>
{
  private static final boolean DEBUG = false;
  private static final String TAG = "GenericInflater";
  private static final Class[] mConstructorSignature = { Context.class, AttributeSet.class };
  private static final HashMap<String, Constructor<?>> sConstructorMap = new HashMap();
  private final Object[] mConstructorArgs = new Object[2];
  protected final Context mContext;
  private String mDefaultPackage;
  private Factory<T> mFactory;
  private boolean mFactorySet;
  
  protected GenericInflater(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  protected GenericInflater(GenericInflater<T> paramGenericInflater, Context paramContext)
  {
    this.mContext = paramContext;
    this.mFactory = paramGenericInflater.mFactory;
  }
  
  private T createItemFromTag(XmlPullParser paramXmlPullParser, String paramString, AttributeSet paramAttributeSet)
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
        onAddChildItem(paramT, localObject);
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
    //   0: getstatic 49	com/android/setupwizardlib/items/GenericInflater:sConstructorMap	Ljava/util/HashMap;
    //   3: aload_1
    //   4: invokevirtual 150	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7: checkcast 152	java/lang/reflect/Constructor
    //   10: astore 5
    //   12: aload 5
    //   14: astore 4
    //   16: aload 5
    //   18: ifnonnull +67 -> 85
    //   21: aload_0
    //   22: getfield 56	com/android/setupwizardlib/items/GenericInflater:mContext	Landroid/content/Context;
    //   25: invokevirtual 156	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   28: astore 5
    //   30: aload_2
    //   31: ifnull +77 -> 108
    //   34: new 86	java/lang/StringBuilder
    //   37: dup
    //   38: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   41: aload_2
    //   42: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_1
    //   46: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   52: astore 4
    //   54: aload 5
    //   56: aload 4
    //   58: invokevirtual 162	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   61: getstatic 42	com/android/setupwizardlib/items/GenericInflater:mConstructorSignature	[Ljava/lang/Class;
    //   64: invokevirtual 166	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   67: astore 4
    //   69: aload 4
    //   71: iconst_1
    //   72: invokevirtual 170	java/lang/reflect/Constructor:setAccessible	(Z)V
    //   75: getstatic 49	com/android/setupwizardlib/items/GenericInflater:sConstructorMap	Ljava/util/HashMap;
    //   78: aload_1
    //   79: aload 4
    //   81: invokevirtual 174	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   84: pop
    //   85: aload_0
    //   86: getfield 54	com/android/setupwizardlib/items/GenericInflater:mConstructorArgs	[Ljava/lang/Object;
    //   89: astore 5
    //   91: aload 5
    //   93: iconst_1
    //   94: aload_3
    //   95: aastore
    //   96: aload 4
    //   98: aload 5
    //   100: invokevirtual 178	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   103: astore 4
    //   105: aload 4
    //   107: areturn
    //   108: aload_1
    //   109: astore 4
    //   111: goto -57 -> 54
    //   114: astore 4
    //   116: new 86	java/lang/StringBuilder
    //   119: dup
    //   120: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   123: aload_3
    //   124: invokeinterface 91 1 0
    //   129: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: ldc 97
    //   134: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: astore 5
    //   139: aload_1
    //   140: astore_3
    //   141: aload_2
    //   142: ifnull +22 -> 164
    //   145: new 86	java/lang/StringBuilder
    //   148: dup
    //   149: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   152: aload_2
    //   153: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: aload_1
    //   157: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: astore_3
    //   164: new 65	android/view/InflateException
    //   167: dup
    //   168: aload 5
    //   170: aload_3
    //   171: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: invokespecial 103	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   180: astore_1
    //   181: aload_1
    //   182: aload 4
    //   184: invokevirtual 107	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   187: pop
    //   188: aload_1
    //   189: athrow
    //   190: astore_1
    //   191: aload_1
    //   192: athrow
    //   193: astore 4
    //   195: new 86	java/lang/StringBuilder
    //   198: dup
    //   199: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   202: aload_3
    //   203: invokeinterface 91 1 0
    //   208: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: ldc 97
    //   213: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   216: astore 5
    //   218: aload_1
    //   219: astore_3
    //   220: aload_2
    //   221: ifnull +22 -> 243
    //   224: new 86	java/lang/StringBuilder
    //   227: dup
    //   228: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   231: aload_2
    //   232: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: aload_1
    //   236: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   242: astore_3
    //   243: new 65	android/view/InflateException
    //   246: dup
    //   247: aload 5
    //   249: aload_3
    //   250: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   256: invokespecial 103	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   259: astore_1
    //   260: aload_1
    //   261: aload 4
    //   263: invokevirtual 107	android/view/InflateException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   266: pop
    //   267: aload_1
    //   268: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	269	0	this	GenericInflater
    //   0	269	1	paramString1	String
    //   0	269	2	paramString2	String
    //   0	269	3	paramAttributeSet	AttributeSet
    //   14	96	4	localObject1	Object
    //   114	69	4	localException	Exception
    //   193	69	4	localNoSuchMethodException	NoSuchMethodException
    //   10	238	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   21	30	114	java/lang/Exception
    //   34	54	114	java/lang/Exception
    //   54	85	114	java/lang/Exception
    //   85	91	114	java/lang/Exception
    //   96	105	114	java/lang/Exception
    //   21	30	190	java/lang/ClassNotFoundException
    //   34	54	190	java/lang/ClassNotFoundException
    //   54	85	190	java/lang/ClassNotFoundException
    //   85	91	190	java/lang/ClassNotFoundException
    //   96	105	190	java/lang/ClassNotFoundException
    //   21	30	193	java/lang/NoSuchMethodException
    //   34	54	193	java/lang/NoSuchMethodException
    //   54	85	193	java/lang/NoSuchMethodException
    //   85	91	193	java/lang/NoSuchMethodException
    //   96	105	193	java/lang/NoSuchMethodException
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
  
  public T inflate(int paramInt)
  {
    return (T)inflate(paramInt, null);
  }
  
  public T inflate(int paramInt, T paramT)
  {
    if (paramT != null) {}
    for (boolean bool = true;; bool = false) {
      return (T)inflate(paramInt, paramT, bool);
    }
  }
  
  public T inflate(int paramInt, T paramT, boolean paramBoolean)
  {
    XmlResourceParser localXmlResourceParser = getContext().getResources().getXml(paramInt);
    try
    {
      paramT = inflate(localXmlResourceParser, paramT, paramBoolean);
      return paramT;
    }
    finally
    {
      localXmlResourceParser.close();
    }
  }
  
  public T inflate(XmlPullParser paramXmlPullParser, T paramT)
  {
    if (paramT != null) {}
    for (boolean bool = true;; bool = false) {
      return (T)inflate(paramXmlPullParser, paramT, bool);
    }
  }
  
  public T inflate(XmlPullParser paramXmlPullParser, T paramT, boolean paramBoolean)
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
    catch (XmlPullParserException paramXmlPullParser)
    {
      paramT = new InflateException(paramXmlPullParser.getMessage());
      paramT.initCause(paramXmlPullParser);
      throw paramT;
      paramXmlPullParser = finally;
      throw paramXmlPullParser;
      paramT = onMergeRoots(paramT, paramBoolean, createItemFromTag(paramXmlPullParser, paramXmlPullParser.getName(), localAttributeSet));
      rInflate(paramXmlPullParser, paramT, localAttributeSet);
      return paramT;
    }
    catch (IOException paramT)
    {
      paramXmlPullParser = new InflateException(paramXmlPullParser.getPositionDescription() + ": " + paramT.getMessage());
      paramXmlPullParser.initCause(paramT);
      throw paramXmlPullParser;
    }
  }
  
  protected abstract void onAddChildItem(T paramT1, T paramT2);
  
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
  
  protected T onMergeRoots(T paramT1, boolean paramBoolean, T paramT2)
  {
    return paramT2;
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
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\items\GenericInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */