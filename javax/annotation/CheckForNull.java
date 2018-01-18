package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull(when=When.MAYBE)
@TypeQualifierNickname
public @interface CheckForNull {}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\javax\annotation\CheckForNull.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */