package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier(applicableTo=Number.class)
public @interface Nonnegative
{
  When when() default When.ALWAYS;
  
  public static class Checker
    implements TypeQualifierValidator<Nonnegative>
  {
    public When forConstantValue(Nonnegative paramNonnegative, Object paramObject)
    {
      if (!(paramObject instanceof Number)) {
        return When.NEVER;
      }
      paramNonnegative = (Number)paramObject;
      int i;
      if ((paramNonnegative instanceof Long)) {
        if (paramNonnegative.longValue() < 0L) {
          i = 1;
        }
      }
      while (i != 0)
      {
        return When.NEVER;
        i = 0;
        continue;
        if ((paramNonnegative instanceof Double))
        {
          if (paramNonnegative.doubleValue() < 0.0D) {
            i = 1;
          } else {
            i = 0;
          }
        }
        else if ((paramNonnegative instanceof Float))
        {
          if (paramNonnegative.floatValue() < 0.0F) {
            i = 1;
          } else {
            i = 0;
          }
        }
        else if (paramNonnegative.intValue() < 0) {
          i = 1;
        } else {
          i = 0;
        }
      }
      return When.ALWAYS;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\javax\annotation\Nonnegative.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */