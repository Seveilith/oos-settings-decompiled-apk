package android.support.v4.print;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class PrintHelperKitkat
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  private static final String LOG_TAG = "PrintHelperKitkat";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  protected boolean mIsMinMarginsHandlingCorrect = true;
  private final Object mLock = new Object();
  int mOrientation;
  protected boolean mPrintActivityRespectsOrientation = true;
  int mScaleMode = 2;
  
  PrintHelperKitkat(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private Bitmap convertBitmapForColorMode(Bitmap paramBitmap, int paramInt)
  {
    if (paramInt != 1) {
      return paramBitmap;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    ColorMatrix localColorMatrix = new ColorMatrix();
    localColorMatrix.setSaturation(0.0F);
    localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f = paramRectF.width() / paramInt1;
    if (paramInt3 == 2) {}
    for (f = Math.max(f, paramRectF.height() / paramInt2);; f = Math.min(f, paramRectF.height() / paramInt2))
    {
      localMatrix.postScale(f, f);
      localMatrix.postTranslate((paramRectF.width() - paramInt1 * f) / 2.0F, (paramRectF.height() - paramInt2 * f) / 2.0F);
      return localMatrix;
    }
  }
  
  private static boolean isPortrait(Bitmap paramBitmap)
  {
    return paramBitmap.getWidth() <= paramBitmap.getHeight();
  }
  
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    if ((paramUri == null) || (this.mContext == null)) {
      throw new IllegalArgumentException("bad argument to loadBitmap");
    }
    localUri = null;
    try
    {
      paramUri = this.mContext.getContentResolver().openInputStream(paramUri);
      localUri = paramUri;
      paramOptions = BitmapFactory.decodeStream(paramUri, null, paramOptions);
      if (paramUri != null) {}
      try
      {
        paramUri.close();
        return paramOptions;
      }
      catch (IOException paramUri)
      {
        Log.w("PrintHelperKitkat", "close fail ", paramUri);
        return paramOptions;
      }
      try
      {
        localUri.close();
        throw paramUri;
      }
      catch (IOException paramOptions)
      {
        for (;;)
        {
          Log.w("PrintHelperKitkat", "close fail ", paramOptions);
        }
      }
    }
    finally
    {
      if (localUri == null) {}
    }
  }
  
  /* Error */
  private Bitmap loadConstrainedBitmap(Uri arg1, int paramInt)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: iload_2
    //   1: ifle +7 -> 8
    //   4: aload_1
    //   5: ifnonnull +13 -> 18
    //   8: new 181	java/lang/IllegalArgumentException
    //   11: dup
    //   12: ldc -36
    //   14: invokespecial 186	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_0
    //   19: getfield 95	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
    //   22: ifnull -14 -> 8
    //   25: new 222	android/graphics/BitmapFactory$Options
    //   28: dup
    //   29: invokespecial 223	android/graphics/BitmapFactory$Options:<init>	()V
    //   32: astore 7
    //   34: aload 7
    //   36: iconst_1
    //   37: putfield 226	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   40: aload_0
    //   41: aload_1
    //   42: aload 7
    //   44: invokespecial 228	android/support/v4/print/PrintHelperKitkat:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   47: pop
    //   48: aload 7
    //   50: getfield 231	android/graphics/BitmapFactory$Options:outWidth	I
    //   53: istore 5
    //   55: aload 7
    //   57: getfield 234	android/graphics/BitmapFactory$Options:outHeight	I
    //   60: istore 6
    //   62: iload 5
    //   64: ifle +8 -> 72
    //   67: iload 6
    //   69: ifgt +5 -> 74
    //   72: aconst_null
    //   73: areturn
    //   74: iload 5
    //   76: iload 6
    //   78: invokestatic 237	java/lang/Math:max	(II)I
    //   81: istore 4
    //   83: iconst_1
    //   84: istore_3
    //   85: iload 4
    //   87: iload_2
    //   88: if_icmple +16 -> 104
    //   91: iload 4
    //   93: iconst_1
    //   94: iushr
    //   95: istore 4
    //   97: iload_3
    //   98: iconst_1
    //   99: ishl
    //   100: istore_3
    //   101: goto -16 -> 85
    //   104: iload_3
    //   105: ifle +15 -> 120
    //   108: iload 5
    //   110: iload 6
    //   112: invokestatic 239	java/lang/Math:min	(II)I
    //   115: iload_3
    //   116: idiv
    //   117: ifgt +5 -> 122
    //   120: aconst_null
    //   121: areturn
    //   122: aload_0
    //   123: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   126: astore 7
    //   128: aload 7
    //   130: monitorenter
    //   131: aload_0
    //   132: new 222	android/graphics/BitmapFactory$Options
    //   135: dup
    //   136: invokespecial 223	android/graphics/BitmapFactory$Options:<init>	()V
    //   139: putfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   142: aload_0
    //   143: getfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   146: iconst_1
    //   147: putfield 242	android/graphics/BitmapFactory$Options:inMutable	Z
    //   150: aload_0
    //   151: getfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   154: iload_3
    //   155: putfield 245	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   158: aload_0
    //   159: getfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   162: astore 8
    //   164: aload 7
    //   166: monitorexit
    //   167: aload_0
    //   168: aload_1
    //   169: aload 8
    //   171: invokespecial 228	android/support/v4/print/PrintHelperKitkat:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   174: astore 7
    //   176: aload_0
    //   177: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   180: astore_1
    //   181: aload_1
    //   182: monitorenter
    //   183: aload_0
    //   184: aconst_null
    //   185: putfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   188: aload_1
    //   189: monitorexit
    //   190: aload 7
    //   192: areturn
    //   193: astore_1
    //   194: aload 7
    //   196: monitorexit
    //   197: aload_1
    //   198: athrow
    //   199: astore 7
    //   201: aload_1
    //   202: monitorexit
    //   203: aload 7
    //   205: athrow
    //   206: astore 7
    //   208: aload_0
    //   209: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   212: astore_1
    //   213: aload_1
    //   214: monitorenter
    //   215: aload_0
    //   216: aconst_null
    //   217: putfield 85	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   220: aload_1
    //   221: monitorexit
    //   222: aload 7
    //   224: athrow
    //   225: astore 7
    //   227: aload_1
    //   228: monitorexit
    //   229: aload 7
    //   231: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	PrintHelperKitkat
    //   0	232	2	paramInt	int
    //   84	71	3	i	int
    //   81	15	4	j	int
    //   53	56	5	k	int
    //   60	51	6	m	int
    //   32	163	7	localObject1	Object
    //   199	5	7	localObject2	Object
    //   206	17	7	localObject3	Object
    //   225	5	7	localObject4	Object
    //   162	8	8	localOptions	BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   131	164	193	finally
    //   183	188	199	finally
    //   167	176	206	finally
    //   215	220	225	finally
  }
  
  private void writeBitmap(final PrintAttributes paramPrintAttributes, final int paramInt, final Bitmap paramBitmap, final ParcelFileDescriptor paramParcelFileDescriptor, final CancellationSignal paramCancellationSignal, final PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
  {
    if (this.mIsMinMarginsHandlingCorrect) {}
    for (final PrintAttributes localPrintAttributes = paramPrintAttributes;; localPrintAttributes = copyAttributes(paramPrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build())
    {
      new AsyncTask()
      {
        /* Error */
        protected Throwable doInBackground(Void... paramAnonymousVarArgs)
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   4: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   7: ifeq +5 -> 12
          //   10: aconst_null
          //   11: areturn
          //   12: new 66	android/print/pdf/PrintedPdfDocument
          //   15: dup
          //   16: aload_0
          //   17: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   20: getfield 70	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   23: aload_0
          //   24: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   27: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   30: astore 4
          //   32: aload_0
          //   33: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   36: aload_0
          //   37: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   40: aload_0
          //   41: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   44: invokevirtual 79	android/print/PrintAttributes:getColorMode	()I
          //   47: invokestatic 83	android/support/v4/print/PrintHelperKitkat:-wrap0	(Landroid/support/v4/print/PrintHelperKitkat;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
          //   50: astore_3
          //   51: aload_0
          //   52: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   55: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   58: istore_2
          //   59: iload_2
          //   60: ifeq +5 -> 65
          //   63: aconst_null
          //   64: areturn
          //   65: aload 4
          //   67: iconst_1
          //   68: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   71: astore 5
          //   73: aload_0
          //   74: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   77: getfield 91	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   80: ifeq +117 -> 197
          //   83: new 93	android/graphics/RectF
          //   86: dup
          //   87: aload 5
          //   89: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   92: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   95: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   98: astore_1
          //   99: aload_0
          //   100: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   103: aload_3
          //   104: invokevirtual 113	android/graphics/Bitmap:getWidth	()I
          //   107: aload_3
          //   108: invokevirtual 116	android/graphics/Bitmap:getHeight	()I
          //   111: aload_1
          //   112: aload_0
          //   113: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fittingMode	I
          //   116: invokestatic 120	android/support/v4/print/PrintHelperKitkat:-wrap2	(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
          //   119: astore 6
          //   121: aload_0
          //   122: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   125: getfield 91	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   128: ifeq +169 -> 297
          //   131: aload 5
          //   133: invokevirtual 124	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   136: aload_3
          //   137: aload 6
          //   139: aconst_null
          //   140: invokevirtual 130	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
          //   143: aload 4
          //   145: aload 5
          //   147: invokevirtual 134	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   150: aload_0
          //   151: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   154: invokevirtual 64	android/os/CancellationSignal:isCanceled	()Z
          //   157: istore_2
          //   158: iload_2
          //   159: ifeq +165 -> 324
          //   162: aload 4
          //   164: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   167: aload_0
          //   168: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   171: astore_1
          //   172: aload_1
          //   173: ifnull +10 -> 183
          //   176: aload_0
          //   177: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   180: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   183: aload_3
          //   184: aload_0
          //   185: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   188: if_acmpeq +7 -> 195
          //   191: aload_3
          //   192: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   195: aconst_null
          //   196: areturn
          //   197: new 66	android/print/pdf/PrintedPdfDocument
          //   200: dup
          //   201: aload_0
          //   202: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   205: getfield 70	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   208: aload_0
          //   209: getfield 39	android/support/v4/print/PrintHelperKitkat$2:val$attributes	Landroid/print/PrintAttributes;
          //   212: invokespecial 73	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   215: astore 6
          //   217: aload 6
          //   219: iconst_1
          //   220: invokevirtual 87	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   223: astore 7
          //   225: new 93	android/graphics/RectF
          //   228: dup
          //   229: aload 7
          //   231: invokevirtual 99	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   234: invokevirtual 105	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   237: invokespecial 108	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   240: astore_1
          //   241: aload 6
          //   243: aload 7
          //   245: invokevirtual 134	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   248: aload 6
          //   250: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   253: goto -154 -> 99
          //   256: astore_1
          //   257: aload 4
          //   259: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   262: aload_0
          //   263: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   266: astore 4
          //   268: aload 4
          //   270: ifnull +10 -> 280
          //   273: aload_0
          //   274: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   277: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   280: aload_3
          //   281: aload_0
          //   282: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   285: if_acmpeq +7 -> 292
          //   288: aload_3
          //   289: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   292: aload_1
          //   293: athrow
          //   294: astore_1
          //   295: aload_1
          //   296: areturn
          //   297: aload 6
          //   299: aload_1
          //   300: getfield 147	android/graphics/RectF:left	F
          //   303: aload_1
          //   304: getfield 150	android/graphics/RectF:top	F
          //   307: invokevirtual 156	android/graphics/Matrix:postTranslate	(FF)Z
          //   310: pop
          //   311: aload 5
          //   313: invokevirtual 124	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   316: aload_1
          //   317: invokevirtual 160	android/graphics/Canvas:clipRect	(Landroid/graphics/RectF;)Z
          //   320: pop
          //   321: goto -190 -> 131
          //   324: aload 4
          //   326: new 162	java/io/FileOutputStream
          //   329: dup
          //   330: aload_0
          //   331: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   334: invokevirtual 166	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
          //   337: invokespecial 169	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
          //   340: invokevirtual 173	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
          //   343: aload 4
          //   345: invokevirtual 137	android/print/pdf/PrintedPdfDocument:close	()V
          //   348: aload_0
          //   349: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   352: astore_1
          //   353: aload_1
          //   354: ifnull +10 -> 364
          //   357: aload_0
          //   358: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   361: invokevirtual 140	android/os/ParcelFileDescriptor:close	()V
          //   364: aload_3
          //   365: aload_0
          //   366: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   369: if_acmpeq +7 -> 376
          //   372: aload_3
          //   373: invokevirtual 143	android/graphics/Bitmap:recycle	()V
          //   376: aconst_null
          //   377: areturn
          //   378: astore 4
          //   380: goto -100 -> 280
          //   383: astore_1
          //   384: goto -20 -> 364
          //   387: astore_1
          //   388: goto -205 -> 183
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	391	0	this	2
          //   0	391	1	paramAnonymousVarArgs	Void[]
          //   58	101	2	bool	boolean
          //   50	323	3	localBitmap	Bitmap
          //   30	314	4	localObject1	Object
          //   378	1	4	localIOException	IOException
          //   71	241	5	localPage1	android.graphics.pdf.PdfDocument.Page
          //   119	179	6	localObject2	Object
          //   223	21	7	localPage2	android.graphics.pdf.PdfDocument.Page
          // Exception table:
          //   from	to	target	type
          //   65	99	256	finally
          //   99	131	256	finally
          //   131	158	256	finally
          //   197	253	256	finally
          //   297	321	256	finally
          //   324	343	256	finally
          //   0	10	294	java/lang/Throwable
          //   12	59	294	java/lang/Throwable
          //   162	172	294	java/lang/Throwable
          //   176	183	294	java/lang/Throwable
          //   183	195	294	java/lang/Throwable
          //   257	268	294	java/lang/Throwable
          //   273	280	294	java/lang/Throwable
          //   280	292	294	java/lang/Throwable
          //   292	294	294	java/lang/Throwable
          //   343	353	294	java/lang/Throwable
          //   357	364	294	java/lang/Throwable
          //   364	376	294	java/lang/Throwable
          //   273	280	378	java/io/IOException
          //   357	364	383	java/io/IOException
          //   176	183	387	java/io/IOException
        }
        
        protected void onPostExecute(Throwable paramAnonymousThrowable)
        {
          if (paramCancellationSignal.isCanceled())
          {
            paramWriteResultCallback.onWriteCancelled();
            return;
          }
          if (paramAnonymousThrowable == null)
          {
            paramWriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
            return;
          }
          Log.e("PrintHelperKitkat", "Error writing printed content", paramAnonymousThrowable);
          paramWriteResultCallback.onWriteFailed(null);
        }
      }.execute(new Void[0]);
      return;
    }
  }
  
  protected PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
  {
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder().setMediaSize(paramPrintAttributes.getMediaSize()).setResolution(paramPrintAttributes.getResolution()).setMinMargins(paramPrintAttributes.getMinMargins());
    if (paramPrintAttributes.getColorMode() != 0) {
      localBuilder.setColorMode(paramPrintAttributes.getColorMode());
    }
    return localBuilder;
  }
  
  public int getColorMode()
  {
    return this.mColorMode;
  }
  
  public int getOrientation()
  {
    if (this.mOrientation == 0) {
      return 1;
    }
    return this.mOrientation;
  }
  
  public int getScaleMode()
  {
    return this.mScaleMode;
  }
  
  public void printBitmap(final String paramString, final Bitmap paramBitmap, final OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    if (paramBitmap == null) {
      return;
    }
    final int i = this.mScaleMode;
    PrintManager localPrintManager = (PrintManager)this.mContext.getSystemService("print");
    if (isPortrait(paramBitmap)) {}
    for (Object localObject = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;; localObject = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)
    {
      localObject = new PrintAttributes.Builder().setMediaSize((PrintAttributes.MediaSize)localObject).setColorMode(this.mColorMode).build();
      localPrintManager.print(paramString, new PrintDocumentAdapter()
      {
        private PrintAttributes mAttributes;
        
        public void onFinish()
        {
          if (paramOnPrintFinishCallback != null) {
            paramOnPrintFinishCallback.onFinish();
          }
        }
        
        public void onLayout(PrintAttributes paramAnonymousPrintAttributes1, PrintAttributes paramAnonymousPrintAttributes2, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
        {
          boolean bool = true;
          this.mAttributes = paramAnonymousPrintAttributes2;
          paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
          if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {
            bool = false;
          }
          paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
        }
        
        public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
        {
          PrintHelperKitkat.-wrap4(PrintHelperKitkat.this, this.mAttributes, i, paramBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
        }
      }, (PrintAttributes)localObject);
      return;
    }
  }
  
  public void printBitmap(final String paramString, final Uri paramUri, final OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    paramUri = new PrintDocumentAdapter()
    {
      private PrintAttributes mAttributes;
      Bitmap mBitmap = null;
      AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
      
      private void cancelLoad()
      {
        synchronized (PrintHelperKitkat.-get0(PrintHelperKitkat.this))
        {
          if (PrintHelperKitkat.this.mDecodeOptions != null)
          {
            PrintHelperKitkat.this.mDecodeOptions.requestCancelDecode();
            PrintHelperKitkat.this.mDecodeOptions = null;
          }
          return;
        }
      }
      
      public void onFinish()
      {
        super.onFinish();
        cancelLoad();
        if (this.mLoadBitmap != null) {
          this.mLoadBitmap.cancel(true);
        }
        if (paramOnPrintFinishCallback != null) {
          paramOnPrintFinishCallback.onFinish();
        }
        if (this.mBitmap != null)
        {
          this.mBitmap.recycle();
          this.mBitmap = null;
        }
      }
      
      public void onLayout(final PrintAttributes paramAnonymousPrintAttributes1, final PrintAttributes paramAnonymousPrintAttributes2, final CancellationSignal paramAnonymousCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
      {
        boolean bool = false;
        try
        {
          this.mAttributes = paramAnonymousPrintAttributes2;
          if (paramAnonymousCancellationSignal.isCanceled())
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            return;
          }
        }
        finally {}
        if (this.mBitmap != null)
        {
          paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
          if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
            return;
            bool = true;
          }
        }
        this.mLoadBitmap = new AsyncTask()
        {
          protected Bitmap doInBackground(Uri... paramAnonymous2VarArgs)
          {
            try
            {
              paramAnonymous2VarArgs = PrintHelperKitkat.-wrap1(PrintHelperKitkat.this, this.val$imageFile, 3500);
              return paramAnonymous2VarArgs;
            }
            catch (FileNotFoundException paramAnonymous2VarArgs) {}
            return null;
          }
          
          protected void onCancelled(Bitmap paramAnonymous2Bitmap)
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            PrintHelperKitkat.3.this.mLoadBitmap = null;
          }
          
          protected void onPostExecute(Bitmap paramAnonymous2Bitmap)
          {
            super.onPostExecute(paramAnonymous2Bitmap);
            Object localObject = paramAnonymous2Bitmap;
            if (paramAnonymous2Bitmap != null) {
              if (PrintHelperKitkat.this.mPrintActivityRespectsOrientation)
              {
                localObject = paramAnonymous2Bitmap;
                if (PrintHelperKitkat.this.mOrientation != 0) {
                  break label108;
                }
              }
            }
            for (;;)
            {
              try
              {
                PrintAttributes.MediaSize localMediaSize = PrintHelperKitkat.3.-get0(PrintHelperKitkat.3.this).getMediaSize();
                localObject = paramAnonymous2Bitmap;
                if (localMediaSize != null)
                {
                  localObject = paramAnonymous2Bitmap;
                  if (localMediaSize.isPortrait() != PrintHelperKitkat.-wrap3(paramAnonymous2Bitmap))
                  {
                    localObject = new Matrix();
                    ((Matrix)localObject).postRotate(90.0F);
                    localObject = Bitmap.createBitmap(paramAnonymous2Bitmap, 0, 0, paramAnonymous2Bitmap.getWidth(), paramAnonymous2Bitmap.getHeight(), (Matrix)localObject, true);
                  }
                }
                label108:
                PrintHelperKitkat.3.this.mBitmap = ((Bitmap)localObject);
                if (localObject == null) {
                  break label187;
                }
                paramAnonymous2Bitmap = new PrintDocumentInfo.Builder(this.val$jobName).setContentType(1).setPageCount(1).build();
                if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1))
                {
                  bool = false;
                  paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymous2Bitmap, bool);
                  PrintHelperKitkat.3.this.mLoadBitmap = null;
                  return;
                }
              }
              finally {}
              boolean bool = true;
              continue;
              label187:
              paramAnonymousLayoutResultCallback.onLayoutFailed(null);
            }
          }
          
          protected void onPreExecute()
          {
            paramAnonymousCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
            {
              public void onCancel()
              {
                PrintHelperKitkat.3.-wrap0(PrintHelperKitkat.3.this);
                PrintHelperKitkat.3.1.this.cancel(false);
              }
            });
          }
        }.execute(new Uri[0]);
      }
      
      public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
      {
        PrintHelperKitkat.-wrap4(PrintHelperKitkat.this, this.mAttributes, this.val$fittingMode, this.mBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
      }
    };
    paramOnPrintFinishCallback = (PrintManager)this.mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(this.mColorMode);
    if ((this.mOrientation == 1) || (this.mOrientation == 0)) {
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
    }
    for (;;)
    {
      paramOnPrintFinishCallback.print(paramString, paramUri, localBuilder.build());
      return;
      if (this.mOrientation == 2) {
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }
  }
  
  public void setColorMode(int paramInt)
  {
    this.mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    this.mScaleMode = paramInt;
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\print\PrintHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */