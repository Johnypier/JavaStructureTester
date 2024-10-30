package .structure;

import de.tum.in.test.api.AddTrustedPackage;
import de.tum.in.test.api.WhitelistPath;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@WhitelistPath("target")
@AddTrustedPackage("**.structure**")
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE})
public @interface Structure {
}
