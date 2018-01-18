package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull
@TypeQualifierDefault({java.lang.annotation.ElementType.PARAMETER})
public @interface ParametersAreNonnullByDefault {}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\javax\annotation\ParametersAreNonnullByDefault.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */