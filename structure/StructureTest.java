package .structure;

import de.tum.in.test.api.jupiter.Public;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.support.ModifierSupport;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static .structure.StructureParser.*;
import static .structure.StructureChecker.*;

/**
 * @author Ivan Parmacli (ivan.parmacli@proton.me)
 * @version 1.2 (30.10.2024)
 * <br><br>
 * This test evaluates whether the specified classes, attributes, constructors, and methods in a JSON structure file
 * are implemented correctly.
 * Use @Disabled annotation to disable a test if it is not needed.
 */
@Structure // For Ares security manager.
class StructureTest {
    private static final Logger logger = Logger.getLogger("structure");
    private static final List<JSONClass> classes;

    static {
        String errorMessage = "Could not create a list of classes from the structure file. Make sure it exists and has a correct format.";
        try {
            classes = retrieveStructureFromJSON();
        } catch (URISyntaxException | IOException | ClassNotFoundException e) {
            logger.warning(e.getMessage());
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * This method creates a dynamic test for each class according to the class structure obtained from the JSON file.
     * Verifies class type, superclass, enum constants.
     *
     * @return A dynamic test stream containing the test for each class which is then executed by JUnit.
     */
    @TestFactory
    @Timeout(5)
    @Public // For Ares security manager.
    Stream<DynamicTest> classTest() {
        return classes.stream().map(cl -> DynamicTest.dynamicTest("ClassTest[" + cl.name + "]", () -> {
            // Verify the class existence.
            String currentType =
                    cl.isAbstract ? "abstract class" : cl.isInterface ? "interface" : cl.isEnum ? "enum" : "class";
            try {
                Class.forName(cl.packageName + "." + cl.name);
            } catch (ClassNotFoundException e) {
                fail("Could not find the \"" + cl.packageName + "." + cl.name +
                     "\" " + currentType + " within the submission. Make sure it is implemented properly.");
            }

            // Verify superclass.
            Class<?> targetClass = Class.forName(cl.packageName + "." + cl.name);
            if (cl.superclass != null) {
                assertThat(targetClass.getSuperclass().getSimpleName()).withFailMessage(
                                                                               "The \"" + cl.name + "\" should have a superclass \"" + cl.superclass + "\"")
                                                                       .isEqualTo(cl.superclass);
            }

            // Verify enum constants.
            if (cl.isEnum) {
                assertThat(targetClass.isEnum()).withFailMessage("The \"" + cl.name + "\" should be an enum.").isTrue();
                assertThat(Arrays.stream(targetClass.getEnumConstants())
                                 .map(Object::toString)
                                 .filter(cl.enumValues::contains)
                                 .toList()).withFailMessage("The \"" + cl.name + "\" enum with " + cl.enumValues +
                                                            " enum constant was not found within the submission. Make sure that it exists and is implemented properly.")
                                           .isNotEmpty().hasSize(cl.enumValues.size());
            }

            // Verify class type.
            if (cl.isAbstract) {
                assertThat(ModifierSupport.isAbstract(targetClass)).withFailMessage(
                        "The \"" + cl.name + "\" should be abstract.").isTrue();
            }
            if (cl.isInterface) {
                assertThat(targetClass.isInterface()).withFailMessage("The \"" + cl.name + "\" should be an interface.")
                                                     .isTrue();
            }
        }));
    }

    /**
     * This method creates a dynamic test for each class according to the class structure obtained from the JSON file.
     * Ensures that the class declares attributes specified in the structure file.
     *
     * @return A dynamic test stream containing the test for each class which is then executed by JUnit.
     */
    @TestFactory
    @Timeout(5)
    @Public // For Ares security manager.
    Stream<DynamicTest> attributeTest() {
        return classes.stream().map(cl -> DynamicTest.dynamicTest("AttributeTest[" + cl.name + "]", () -> {
            // Verify the class existence.
            String currentType =
                    cl.isAbstract ? "abstract class" : cl.isInterface ? "interface" : cl.isEnum ? "enum" : "class";
            try {
                Class.forName(cl.packageName + "." + cl.name);
            } catch (ClassNotFoundException e) {
                fail("Could not find the \"" + cl.packageName + "." + cl.name +
                     "\" " + currentType + " within the submission. Make sure it is implemented properly.");
            }

            // Skip the test if there are no expected elements in the list.
            Assumptions.assumeFalse(cl.attributes.isEmpty(), "The \"" + cl.packageName + "." + cl.name +
                                                             "\" " + currentType + " attributes list is empty.");

            // Verify attributes.
            Class<?> targetClass = Class.forName(cl.packageName + "." + cl.name);
            Field[] targetFields = targetClass.getDeclaredFields();
            List<JSONAttribute> expectedAttributesFound = new LinkedList<>();
            int attributesFound = Arrays.stream(targetFields)
                                        .filter(field -> !cl.attributes.stream().filter(expectedAttribute -> {
                                            JSONAttribute ja = attributeCheck(field, expectedAttribute);
                                            if (ja == null) {
                                                return false;
                                            }
                                            expectedAttributesFound.add(expectedAttribute);
                                            return true;
                                        }).toList().isEmpty())
                                        .toList()
                                        .size();

            if (attributesFound != cl.attributes.size()) {
                cl.attributes.removeAll(expectedAttributesFound);
                cl.attributes.forEach(
                        attribute -> fail(
                                "Could not find an attribute of the " + cl.name + " " + currentType +
                                " with:\nName: " + attribute.name + "\nType: " + attribute.type +
                                (attribute.modifiers.isEmpty() ? "" : "\nModifiers: " + attribute.modifiers) +
                                (attribute.annotations.isEmpty() ? ""
                                                                 : "\nAnnotations: " + attribute.annotations)));
            }
        }));
    }

    /**
     * This method creates a dynamic test for each class according to the class structure obtained from the JSON file.
     * Ensures that the class declares constructors specified in the structure file.
     *
     * @return A dynamic test stream containing the test for each class which is then executed by JUnit.
     */
    @TestFactory
    @Timeout(5)
    @Public // For Ares security manager.
    Stream<DynamicTest> constructorTest() {
        return classes.stream().map(cl -> DynamicTest.dynamicTest("ConstructorTest[" + cl.name + "]", () -> {
            // Verify the class existence.
            String currentType =
                    cl.isAbstract ? "abstract class" : cl.isInterface ? "interface" : cl.isEnum ? "enum" : "class";
            try {
                Class.forName(cl.packageName + "." + cl.name);
            } catch (ClassNotFoundException e) {
                fail("Could not find the \"" + cl.packageName + "." + cl.name +
                     "\" " + currentType + " within the submission. Make sure it is implemented properly.");
            }

            // Skip the test if there are no expected elements in the list.
            Assumptions.assumeFalse(cl.constructors.isEmpty(), "The \"" + cl.packageName + "." + cl.name +
                                                               "\" " + currentType + " constructors list is empty.");

            // Verify constructors.
            Class<?> targetClass = Class.forName(cl.packageName + "." + cl.name);
            List<JSONConstructor> expectedConstructorsFound = new LinkedList<>();
            Constructor<?>[] targetClassConstructors = targetClass.getDeclaredConstructors();
            int constructorsFound = Arrays.stream(targetClassConstructors)
                                          .filter(constructor -> !cl.constructors.stream().filter(expectedCon -> {
                                              JSONConstructor jc = constructorsCheck(constructor, expectedCon);
                                              if (jc == null) {
                                                  return false;
                                              }
                                              expectedConstructorsFound.add(jc);
                                              return true;
                                          }).toList().isEmpty())
                                          .toList()
                                          .size();
            if (constructorsFound != cl.constructors.size()) {
                cl.constructors.removeAll(expectedConstructorsFound);
                cl.constructors.forEach(
                        constructor -> fail(
                                "Could not find a constructor of the " + cl.name + " " + currentType +
                                " with: " +
                                (constructor.modifiers.isEmpty() ? "" : "\nModifiers: " + constructor.modifiers) +
                                (constructor.parametersTypes.isEmpty() ? "" : "\nParameters: " +
                                                                              constructor.parametersTypes) +
                                (constructor.annotations.isEmpty() ? ""
                                                                   : "\nAnnotations: " + constructor.annotations)));
            }
        }));
    }

    /**
     * This method creates a dynamic test for each class according to the class structure obtained from the JSON file.
     * Ensures that the class declares methods specified in the structure file.
     *
     * @return A dynamic test stream containing the test for each class which is then executed by JUnit.
     */
    @TestFactory
    @Timeout(5)
    @Public // For Ares security manager.
    Stream<DynamicTest> methodTest() {
        return classes.stream().map(cl -> DynamicTest.dynamicTest("MethodTest[" + cl.name + "]", () -> {
            // Verify the class existence.
            String currentType =
                    cl.isAbstract ? "abstract class" : cl.isInterface ? "interface" : cl.isEnum ? "enum" : "class";
            try {
                Class.forName(cl.packageName + "." + cl.name);
            } catch (ClassNotFoundException e) {
                fail("Could not find the \"" + cl.packageName + "." + cl.name +
                     "\" " + currentType + " within the submission. Make sure it is implemented properly.");
            }

            // Skip the test if there are no expected elements in the list.
            Assumptions.assumeFalse(cl.methods.isEmpty(), "The \"" + cl.packageName + "." + cl.name +
                                                          "\" " + currentType + " methods list is empty.");

            // Verify attributes.
            Class<?> targetClass = Class.forName(cl.packageName + "." + cl.name);
            Method[] targetMethods = targetClass.getDeclaredMethods();
            List<JSONMethod> expectedMethodsFound = new LinkedList<>();
            int methodsFound = Arrays.stream(targetMethods)
                                     .filter(method -> !cl.methods.stream().filter(expectedMethod -> {
                                         JSONMethod jm = methodCheck(method, expectedMethod);
                                         if (jm == null) {
                                             return false;
                                         }
                                         expectedMethodsFound.add(expectedMethod);
                                         return true;
                                     }).toList().isEmpty())
                                     .toList()
                                     .size();

            if (methodsFound != cl.methods.size()) {
                cl.methods.removeAll(expectedMethodsFound);
                cl.methods.forEach(method -> fail("Could not find a method of the " + cl.name + " " + currentType +
                                                  " with:\nName: " + method.name + "\nReturn Type: " +
                                                  method.returnType +
                                                  (method.parameters.isEmpty() ? ""
                                                                               : "\nParameters: " + method.parameters) +
                                                  (method.modifiers.isEmpty() ? ""
                                                                              : "\nModifiers: " + method.modifiers) +
                                                  (method.annotations.isEmpty() ? "" : "\nAnnotations: " +
                                                                                       method.annotations)));
            }
        }));
    }
}
