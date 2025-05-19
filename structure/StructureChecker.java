package youPackage.structure;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static de.tum.cit.fop.structure.StructureParser.*;

/**
 * @author Ivan Parmacli (ivan.parmacli@proton.me)
 * @version 1.1 (04.02.2025)
 * <br><br>
 * This class contains methods used to compare attributes, constructors, and structural test methods.
 */
class StructureChecker {
    /**
     * This method checks if the target constructor has the same annotations, modifiers and parameter types as the
     * expected constructor in the structure file.
     *
     * @return Null if the target and expected constructors are not equals, returns expected constructor otherwise.
     */
    protected static JSONConstructor constructorsCheck(Constructor<?> targetConstructor,
                                                       JSONConstructor expectedConstructor) {
        // Required for testing.
        targetConstructor.setAccessible(true);

        // Check annotations.
        if (targetConstructor.getAnnotations().length != expectedConstructor.annotations.size()) {
            return null;
        }
        List<String> annotationTypeNames = Arrays.stream(targetConstructor.getAnnotations())
                                                 .map(annotation -> annotation.annotationType().getSimpleName())
                                                 .toList();
        if (expectedConstructor.annotations.stream()
                                           .filter(annotationTypeNames::contains)
                                           .toList()
                                           .size() != expectedConstructor.annotations.size()) {
            return null;
        }


        // Check modifiers.
        String targetModifiers = Modifier.toString(targetConstructor.getModifiers());
        for (String modifier : expectedConstructor.modifiers) {
            if (!targetModifiers.contains(modifier)) {
                return null;
            }
        }

        // Generic types included

        // Check parameter types.
        if (expectedConstructor.parametersTypes.size() != targetConstructor.getParameterCount()) {
            return null;
        }

        if (Stream.concat(Arrays.stream(targetConstructor.getGenericParameterTypes()).map(Type::getTypeName),
                          Arrays.stream(targetConstructor.getParameterTypes()).map(Class::getSimpleName))
                  .filter(expectedConstructor.parametersTypes::contains)
                  .toList()
                  .size() !=
            expectedConstructor.parametersTypes.size()) {
            return null;
        }

        return expectedConstructor;
    }

    /**
     * This method checks if the target attribute has the same name, annotations, modifiers and type as the
     * expected attribute in the structure file.
     *
     * @return Null if the target and expected attributes are not equals, returns expected attribute otherwise.
     */
    protected static JSONAttribute attributeCheck(Field targetAttribute, JSONAttribute expectedAttribute) {
        // Required for testing.
        targetAttribute.setAccessible(true);

        // Check name.
        if (expectedAttribute.name != null && !targetAttribute.getName().equals(expectedAttribute.name)) {
            return null;
        }

        // Check annotations.
        if (targetAttribute.getAnnotations().length != expectedAttribute.annotations.size()) {
            return null;
        }
        List<String> annotationTypeNames = Arrays.stream(targetAttribute.getAnnotations())
                                                 .map(annotation -> annotation.annotationType().getSimpleName())
                                                 .toList();
        if (expectedAttribute.annotations.stream()
                                         .filter(annotationTypeNames::contains)
                                         .toList()
                                         .size() != expectedAttribute.annotations.size()) {
            return null;
        }


        // Check modifiers.
        String targetModifiers = Modifier.toString(targetAttribute.getModifiers());
        for (String modifier : expectedAttribute.modifiers) {
            if (!targetModifiers.contains(modifier)) {
                return null;
            }
        }

        // Generic types included

        // Check type.
        if (expectedAttribute.type != null &&
            !targetAttribute.getType().getSimpleName().equals(expectedAttribute.type) &&
            !targetAttribute.getGenericType().getTypeName().contains(expectedAttribute.type)) {
            return null;
        }

        return expectedAttribute;
    }

    /**
     * This method checks if the target method has the same name, annotations, modifiers, parameter types and return type
     * as the expected method in the structure file.
     *
     * @return Null if the target and expected methods are not equals, returns expected method otherwise.
     */
    protected static JSONMethod methodCheck(Method targetMethod, JSONMethod expectedMethod) {
        // Required for testing.
        targetMethod.setAccessible(true);

        // Check name.
        if (expectedMethod.name != null && !targetMethod.getName().equals(expectedMethod.name)) {
            return null;
        }

        // Check annotations.
        if (targetMethod.getAnnotations().length != expectedMethod.annotations.size()) {
            return null;
        }
        List<String> annotationTypeNames = Arrays.stream(targetMethod.getAnnotations())
                                                 .map(annotation -> annotation.annotationType().getSimpleName())
                                                 .toList();
        if (expectedMethod.annotations.stream()
                                      .filter(annotationTypeNames::contains)
                                      .toList()
                                      .size() != expectedMethod.annotations.size()) {
            return null;
        }


        // Check modifiers.
        String targetModifiers = Modifier.toString(targetMethod.getModifiers());
        for (String modifier : expectedMethod.modifiers) {
            if (!targetModifiers.contains(modifier)) {
                return null;
            }
        }


        // Generic types included

        // Check return type.
        if (expectedMethod.returnType != null &&
            !targetMethod.getReturnType().getSimpleName().equals(expectedMethod.returnType) &&
            !targetMethod.getGenericReturnType().getTypeName().contains(expectedMethod.returnType)) {
            return null;
        }

        // Check parameter types.
        if (expectedMethod.parameters.size() != targetMethod.getParameterCount()) {
            return null;
        }

        if (Stream.concat(Arrays.stream(targetMethod.getGenericParameterTypes()).map(Type::getTypeName),
                          Arrays.stream(targetMethod.getParameterTypes()).map(Class::getSimpleName))
                  .filter(expectedMethod.parameters::contains)
                  .toList()
                  .size() !=
            expectedMethod.parameters.size()) {
            return null;
        }

        return expectedMethod;
    }
}
