package com.oneplus.lib.preference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import org.xmlpull.v1.XmlSerializer;

public class FastXmlSerializer
  implements XmlSerializer
{
  private static final int BUFFER_LEN = 8192;
  private static final String[] ESCAPE_TABLE = { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&quot;", null, null, null, "&amp;", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&lt;", null, "&gt;", null };
  private static String sSpace = "                                                              ";
  private ByteBuffer mBytes = ByteBuffer.allocate(8192);
  private CharsetEncoder mCharset;
  private boolean mInTag;
  private boolean mIndent = false;
  private boolean mLineStart = true;
  private int mNesting = 0;
  private OutputStream mOutputStream;
  private int mPos;
  private final char[] mText = new char[' '];
  private Writer mWriter;
  
  private void append(char paramChar)
    throws IOException
  {
    int j = this.mPos;
    int i = j;
    if (j >= 8191)
    {
      flush();
      i = this.mPos;
    }
    this.mText[i] = paramChar;
    this.mPos = (i + 1);
  }
  
  private void append(String paramString)
    throws IOException
  {
    append(paramString, 0, paramString.length());
  }
  
  private void append(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > 8192)
    {
      j = paramInt1 + paramInt2;
      if (paramInt1 < j)
      {
        i = paramInt1 + 8192;
        if (i < j) {}
        for (paramInt2 = 8192;; paramInt2 = j - paramInt1)
        {
          append(paramString, paramInt1, paramInt2);
          paramInt1 = i;
          break;
        }
      }
      return;
    }
    int j = this.mPos;
    int i = j;
    if (j + paramInt2 > 8192)
    {
      flush();
      i = this.mPos;
    }
    paramString.getChars(paramInt1, paramInt1 + paramInt2, this.mText, i);
    this.mPos = (i + paramInt2);
  }
  
  private void append(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > 8192)
    {
      j = paramInt1 + paramInt2;
      if (paramInt1 < j)
      {
        i = paramInt1 + 8192;
        if (i < j) {}
        for (paramInt2 = 8192;; paramInt2 = j - paramInt1)
        {
          append(paramArrayOfChar, paramInt1, paramInt2);
          paramInt1 = i;
          break;
        }
      }
      return;
    }
    int j = this.mPos;
    int i = j;
    if (j + paramInt2 > 8192)
    {
      flush();
      i = this.mPos;
    }
    System.arraycopy(paramArrayOfChar, paramInt1, this.mText, i, paramInt2);
    this.mPos = (i + paramInt2);
  }
  
  private void appendIndent(int paramInt)
    throws IOException
  {
    int i = paramInt * 4;
    paramInt = i;
    if (i > sSpace.length()) {
      paramInt = sSpace.length();
    }
    append(sSpace, 0, paramInt);
  }
  
  private void escapeAndAppendString(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return;
    }
    int m = paramString.length();
    int n = (char)ESCAPE_TABLE.length;
    String[] arrayOfString = ESCAPE_TABLE;
    int j = 0;
    int i = 0;
    if (i < m)
    {
      int k = paramString.charAt(i);
      if (k >= n) {
        k = j;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        String str = arrayOfString[k];
        k = j;
        if (str != null)
        {
          if (j < i) {
            append(paramString, j, i - j);
          }
          k = i + 1;
          append(str);
        }
      }
    }
    if (j < i) {
      append(paramString, j, i - j);
    }
  }
  
  private void escapeAndAppendString(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int m = (char)ESCAPE_TABLE.length;
    String[] arrayOfString = ESCAPE_TABLE;
    int j = paramInt1;
    int i = paramInt1;
    if (i < paramInt1 + paramInt2)
    {
      int k = paramArrayOfChar[i];
      if (k >= m) {
        k = j;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        String str = arrayOfString[k];
        k = j;
        if (str != null)
        {
          if (j < i) {
            append(paramArrayOfChar, j, i - j);
          }
          k = i + 1;
          append(str);
        }
      }
    }
    if (j < i) {
      append(paramArrayOfChar, j, i - j);
    }
  }
  
  private void flushBytes()
    throws IOException
  {
    int i = this.mBytes.position();
    if (i > 0)
    {
      this.mBytes.flip();
      this.mOutputStream.write(this.mBytes.array(), 0, i);
      this.mBytes.clear();
    }
  }
  
  public XmlSerializer attribute(String paramString1, String paramString2, String paramString3)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    append(' ');
    if (paramString1 != null)
    {
      append(paramString1);
      append(':');
    }
    append(paramString2);
    append("=\"");
    escapeAndAppendString(paramString3);
    append('"');
    this.mLineStart = false;
    return this;
  }
  
  public void cdsect(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void comment(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void docdecl(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void endDocument()
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    flush();
  }
  
  public XmlSerializer endTag(String paramString1, String paramString2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    this.mNesting -= 1;
    if (this.mInTag) {
      append(" />\n");
    }
    for (;;)
    {
      this.mLineStart = true;
      this.mInTag = false;
      return this;
      if ((this.mIndent) && (this.mLineStart)) {
        appendIndent(this.mNesting);
      }
      append("</");
      if (paramString1 != null)
      {
        append(paramString1);
        append(':');
      }
      append(paramString2);
      append(">\n");
    }
  }
  
  public void entityRef(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void flush()
    throws IOException
  {
    if (this.mPos > 0)
    {
      if (this.mOutputStream == null) {
        break label105;
      }
      CharBuffer localCharBuffer = CharBuffer.wrap(this.mText, 0, this.mPos);
      for (CoderResult localCoderResult = this.mCharset.encode(localCharBuffer, this.mBytes, true);; localCoderResult = this.mCharset.encode(localCharBuffer, this.mBytes, true))
      {
        if (localCoderResult.isError()) {
          throw new IOException(localCoderResult.toString());
        }
        if (!localCoderResult.isOverflow()) {
          break;
        }
        flushBytes();
      }
      flushBytes();
      this.mOutputStream.flush();
    }
    for (;;)
    {
      this.mPos = 0;
      return;
      label105:
      this.mWriter.write(this.mText, 0, this.mPos);
      this.mWriter.flush();
    }
  }
  
  public int getDepth()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean getFeature(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getName()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getNamespace()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getPrefix(String paramString, boolean paramBoolean)
    throws IllegalArgumentException
  {
    throw new UnsupportedOperationException();
  }
  
  public Object getProperty(String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public void ignorableWhitespace(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void processingInstruction(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws IllegalArgumentException, IllegalStateException
  {
    if (paramString.equals("http://xmlpull.org/v1/doc/features.html#indent-output"))
    {
      this.mIndent = true;
      return;
    }
    throw new UnsupportedOperationException();
  }
  
  public void setOutput(OutputStream paramOutputStream, String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException();
    }
    try
    {
      this.mCharset = Charset.forName(paramString).newEncoder();
      this.mOutputStream = paramOutputStream;
      return;
    }
    catch (UnsupportedCharsetException paramOutputStream)
    {
      throw ((UnsupportedEncodingException)new UnsupportedEncodingException(paramString).initCause(paramOutputStream));
    }
    catch (IllegalCharsetNameException paramOutputStream)
    {
      throw ((UnsupportedEncodingException)new UnsupportedEncodingException(paramString).initCause(paramOutputStream));
    }
  }
  
  public void setOutput(Writer paramWriter)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    this.mWriter = paramWriter;
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws IllegalArgumentException, IllegalStateException
  {
    throw new UnsupportedOperationException();
  }
  
  public void startDocument(String paramString, Boolean paramBoolean)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    StringBuilder localStringBuilder = new StringBuilder().append("<?xml version='1.0' encoding='utf-8' standalone='");
    if (paramBoolean.booleanValue()) {}
    for (paramString = "yes";; paramString = "no")
    {
      append(paramString + "' ?>\n");
      this.mLineStart = true;
      return;
    }
  }
  
  public XmlSerializer startTag(String paramString1, String paramString2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    if (this.mInTag) {
      append(">\n");
    }
    if (this.mIndent) {
      appendIndent(this.mNesting);
    }
    this.mNesting += 1;
    append('<');
    if (paramString1 != null)
    {
      append(paramString1);
      append(':');
    }
    append(paramString2);
    this.mInTag = true;
    this.mLineStart = false;
    return this;
  }
  
  public XmlSerializer text(String paramString)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    boolean bool2 = false;
    if (this.mInTag)
    {
      append(">");
      this.mInTag = false;
    }
    escapeAndAppendString(paramString);
    if (this.mIndent)
    {
      boolean bool1 = bool2;
      if (paramString.length() > 0)
      {
        bool1 = bool2;
        if (paramString.charAt(paramString.length() - 1) == '\n') {
          bool1 = true;
        }
      }
      this.mLineStart = bool1;
    }
    return this;
  }
  
  public XmlSerializer text(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    boolean bool = false;
    if (this.mInTag)
    {
      append(">");
      this.mInTag = false;
    }
    escapeAndAppendString(paramArrayOfChar, paramInt1, paramInt2);
    if (this.mIndent)
    {
      if (paramArrayOfChar[(paramInt1 + paramInt2 - 1)] == '\n') {
        bool = true;
      }
      this.mLineStart = bool;
    }
    return this;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\FastXmlSerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */