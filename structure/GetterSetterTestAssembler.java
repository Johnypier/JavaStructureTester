package de.tum.cit.fop.structure;

import org.junit.jupiter.api.DynamicTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Ivan Parmacli (ivan.parmacli@proton.me)
 * @version 1.2 (06.11.2024)
 * <br><br>
 * Contains the methods that allow the generation of dynamic tests for getters and setters of the target class.
 */
class GetterSetterTestAssembler {
    // Used for debugging.
    private static final Logger LOGGER = Logger.getLogger("structure");

    /**
     * First the method verifies if the class exists and the instance of the class can be created with the given parameters.
     * If the above checks fail, the list dynamic tests will be returned to fail the test.
     * <p></p>
     * Creates a dynamic test for each expected value to verify that getters of the target class return the correct values.
     *
     * @param targetClassName             Target class name including package.
     * @param targetConstructorArgs       Arguments passed to the target class constructor to create an instance of this class.
     *                                    <b>Can be null, if the class has a constructor without any parameters.</b>
     * @param targetConstructorParamTypes Array of class objects to retrieve the correct target class constructor.
     *                                    It's easier to pass it manually than automatically find one, due to JAVA
     *                                    primitive types issue. <b>Can be null, if the class has a constructor without
     *                                    any parameters.</b>
     * @param expectedGetterValues        Map that contains getter method names mapped to expected values.
     * @return List that contains dynamic tests for each getter of the target class w.r.t. expected values.
     */
    protected static List<DynamicTest> generateGetterTests(String targetClassName, List<?> targetConstructorArgs,
                                                           Class<?>[] targetConstructorParamTypes,
                                                           Map<String, Object> expectedGetterValues) {
        // Verify the class existence and get the reference.
        final Class<?> targetClass;
        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warning(e.getMessage());
            return createFailingDynamicTests(expectedGetterValues, "GetterTest[", targetClassName,
                                             "Could not find the \"" +
                                             targetClassName +
                                             "\" class within the submission. Make sure it is implemented properly.");
        }
        // Initialize the class with the given constructor arguments.
        final Object targetInstance;
        try {
            if (targetConstructorArgs == null || targetConstructorParamTypes == null) {
                targetInstance = targetClass.getConstructors()[0].newInstance();
            } else {
                targetInstance = Arrays.stream(targetClass.getDeclaredConstructors())
                                       .filter(constructor -> Arrays.equals(constructor.getParameterTypes(),
                                                                            targetConstructorParamTypes))
                                       .findFirst().orElseThrow().newInstance(targetConstructorArgs.toArray());
            }
        } catch (NoSuchElementException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
            return createFailingDynamicTests(expectedGetterValues, "GetterTest[", targetClassName,
                                             "Could not initialize the \"" +
                                             targetClassName +
                                             "\" class. Make sure that it is implemented properly.");
        }
        // Create dynamic tests for each expected value.
        return expectedGetterValues.keySet().stream()
                                   .map(key -> createGetterDynamicTest(targetInstance, key, targetClassName,
                                                                       expectedGetterValues.get(key)))
                                   .toList();
    }

    /**
     * Creates a dynamic test for each expected value to verify that getters of the target class return the correct values
     *
     * @param targetInstance   Instance of the target class that will be used to invoke the getter methods.
     * @param targetMethodName Name of the getter to test.
     * @param className        The target class name including package.
     * @param expectedValue    The value that is expected to be returned from the getter.
     * @return DynamicTest object for the given expected value.
     */
    private static DynamicTest createGetterDynamicTest(Object targetInstance, String targetMethodName, String className,
                                                       Object expectedValue) {
        return DynamicTest.dynamicTest("GetterTest[" + className + "|" + targetMethodName + "]",
                                       () -> {
                                           assertThat(Arrays.stream(targetInstance.getClass().getMethods())
                                                            .filter(method -> {
                                                                method.setAccessible(true);
                                                                if (method.getName().equals(targetMethodName)) {
                                                                    try {
                                                                        // Verify values.
                                                                        if (Objects.equals(
                                                                                method.invoke(targetInstance),
                                                                                expectedValue)) {
                                                                            return true;
                                                                        }
                                                                    } catch (IllegalAccessException |
                                                                             InvocationTargetException e) {
                                                                        LOGGER.warning(e.getMessage());
                                                                        fail("Could not invoke the \"" +
                                                                             method.getName() +
                                                                             "\" method due to exception during the method execution.");
                                                                    }
                                                                }
                                                                return false;
                                                            }).toList())
                                                   .withFailMessage(
                                                           "Could not find the \"" + targetMethodName +
                                                           "\" method or it did not return the correct value. Make sure that it is implemented properly.")
                                                   .isNotEmpty();
                                       });
    }

    /**
     * First the method verifies if the class exists and the instance of the class can be created with the given parameters.
     * If the above checks fail, the list with one dynamic test will be returned to fail the test.
     * <p></p>
     * Creates a dynamic test for each expected value to verify that setters of the target class work as expected.
     *
     * @param targetClassName             Target class name including package.
     * @param targetConstructorArgs       Arguments passed to the target class constructor to create an instance of this class.
     *                                    <b>Can be null, if the class has a constructor without any parameters.</b>
     * @param targetConstructorParamTypes Array of class objects to retrieve the correct target class constructor.
     *                                    It's easier to pass it manually than automatically find one, due to JAVA
     *                                    primitive types issue. <b>Can be null, if the class has a constructor without
     *                                    any parameters.</b>
     * @param valuesToSet                 Map that contains setter method names mapped to values to be set.
     * @param expectedNewValues           List that contains the expected new values after the set method was called, may be null
     *                                    if the set methods do not have any special implementation. Example when this should be provided,
     *                                    when at least one the set methods does not simply replace the value, such as
     *                                    "oldValue = oldValue + newValue".
     * @return List that contains dynamic tests for each setter of the target class w.r.t. expected values.
     */
    protected static List<DynamicTest> generateSetterTests(String targetClassName, List<?> targetConstructorArgs,
                                                           Class<?>[] targetConstructorParamTypes,
                                                           Map<String, Object> valuesToSet, List<?> expectedNewValues) {
        // Verify the class existence and get the reference.
        final Class<?> targetClass;
        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warning(e.getMessage());
            return createFailingDynamicTests(valuesToSet, "SetterTest[", targetClassName,
                                             "Could not find the \"" +
                                             targetClassName +
                                             "\" class within the submission. Make sure it is implemented properly.");
        }

        // Initialize the class with the given constructor arguments.
        final Object targetInstance;
        try {
            if (targetConstructorArgs == null || targetConstructorParamTypes == null) {
                targetInstance = targetClass.getConstructors()[0].newInstance();
            } else {
                targetInstance = Arrays.stream(targetClass.getDeclaredConstructors())
                                       .filter(constructor -> Arrays.equals(constructor.getParameterTypes(),
                                                                            targetConstructorParamTypes))
                                       .findFirst().orElseThrow().newInstance(targetConstructorArgs.toArray());
            }
        } catch (NoSuchElementException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
            return createFailingDynamicTests(valuesToSet, "SetterTest[", targetClassName,
                                             "Could not initialize the \"" + targetClassName +
                                             "\" class. Make sure that it is implemented properly.");
        }
        // Create dynamic tests for each setter.
        return createSetterDynamicTests(targetInstance, targetClassName, valuesToSet, expectedNewValues);
    }

    /**
     * Creates a dynamic test for each expected value to verify that getters of the target class return the correct values
     *
     * @param targetInstance    Instance of the target class that will be used to invoke the getter methods.
     * @param className         The target class name including package.
     * @param valuesToSet       Map that contains setter method names mapped to values to be set.
     * @param expectedNewValues List that contains the expected new values after the set method was called, may be null
     *                          if the set methods do not have any special implementation. Example when this should be provided,
     *                          when at least one the set methods does not simply replace the value, such as
     *                          "oldValue = oldValue + newValue".
     * @return DynamicTest object for the given expected value.
     */
    private static List<DynamicTest> createSetterDynamicTests(Object targetInstance, String className,
                                                              Map<String, Object> valuesToSet,
                                                              List<?> expectedNewValues) {
        List<?> keysList = new ArrayList<>(valuesToSet.keySet());
        return valuesToSet.keySet().stream().map(key -> {
            Method targetMethod = Arrays.stream(targetInstance.getClass().getMethods())
                                        .filter(method -> method.getName().equals(key))
                                        .findFirst()
                                        .orElse(null);
            return DynamicTest.dynamicTest("SetterTest[" + className + "|" + key + "]",
                                           () -> {
                                               // Verify that the method exists.
                                               assertThat(targetMethod)
                                                       .withFailMessage("Could not find the \"" + key +
                                                                        "()\" method within the submission.")
                                                       .isNotNull();
                                               targetMethod.invoke(targetInstance, valuesToSet.get(key));
                                               // Get the updated fields.
                                               List<Object> targetClassAttributes = new ArrayList<>();
                                               List<?> currentInstanceAttr = Arrays.stream(
                                                       targetInstance.getClass().getDeclaredFields()).map(field -> {
                                                   field.setAccessible(true);
                                                   try {
                                                       return field.get(targetInstance);
                                                   } catch (IllegalAccessException e) {
                                                       LOGGER.warning(e.getMessage());
                                                       return null;
                                                   }
                                               }).toList();
                                               if (targetInstance.getClass().getSuperclass() != null) {
                                                   // Also get the attributes of super class if available.
                                                   List<?> superClassAttributes = Arrays
                                                           .stream(
                                                                   targetInstance.getClass()
                                                                                 .getSuperclass()
                                                                                 .getDeclaredFields())
                                                           .map(field -> {
                                                               field.setAccessible(true);
                                                               try {
                                                                   return field.get(
                                                                           targetInstance);
                                                               } catch (
                                                                       IllegalAccessException e) {
                                                                   LOGGER.warning(
                                                                           e.getMessage());
                                                                   return null;
                                                               }
                                                           }).toList();
                                                   targetClassAttributes.addAll(currentInstanceAttr);
                                                   targetClassAttributes.addAll(superClassAttributes);
                                               } else {
                                                   targetClassAttributes.addAll(currentInstanceAttr);
                                               }
                                               // Verify the new value.
                                               String failMessage = "The \"" + key +
                                                                    "()\" method is not implemented properly. Please read the problem statement again.";
                                               if (expectedNewValues == null) {
                                                   if (!targetClassAttributes.contains(valuesToSet.get(key))) {
                                                       fail(failMessage);
                                                   }
                                               } else {
                                                   if (targetClassAttributes.contains(
                                                           expectedNewValues.get(keysList.indexOf(key)))) {
                                                       fail(failMessage);
                                                   }
                                               }
                                           });
        }).toList();
    }

    /**
     * Creates a list of dynamic tests for each method name that is contained in the map object, required for better
     * feedback, otherwise only one test will be shown as failed and others won't be executed.
     *
     * @param values     Map which keys represent the target method names.
     * @param methodType Either `GetterTest[` or `SetterTest[`.
     * @param className  The target class name including package.
     * @param message    Fail message.
     * @return List with dynamic tests that will fail.
     */
    private static List<DynamicTest> createFailingDynamicTests(Map<String, Object> values, String methodType,
                                                               String className,
                                                               String message) {
        return values.keySet()
                     .stream()
                     .map(methodName -> DynamicTest.dynamicTest(methodType + className + "|" + methodName + "]",
                                                                () -> fail(message)))
                     .toList();
    }
}
