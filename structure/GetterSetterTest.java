package youPackage.structure;

import de.tum.cit.fop.structure.Structure;
import de.tum.in.test.api.MirrorOutput;
import de.tum.in.test.api.jupiter.Public;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Timeout;

import java.util.*;

import static de.tum.cit.fop.structure.GetterSetterTestAssembler.generateGetterTests;
import static de.tum.cit.fop.structure.GetterSetterTestAssembler.generateSetterTests;

/**
 * @author Ivan Parmacli (ivan.parmacli@proton.me)
 * @version 1.1 (05.11.2024)
 * <br><br>
 * This test verifies if the getter and setter methods of the target class are implemented properly.
 * Use @Disabled annotation to disable a test if it is not needed.
 */
@Structure
class GetterSetterTest {
    /**
     * Verifies that the getters of the target class return the correct values.
     *
     * @return A dynamic test list containing the test for each getter for the specified class(es).
     */
    @TestFactory
    @Timeout(10)
    @Public
    @MirrorOutput
    List<DynamicTest> gettersTest() {
        // Your code here.
        // Example usage:
        // Create a map for each class that you want to test, e.g.Map<String, Object> example = new HashMap<>();
        // Put the target method name and the value that is expected to be returned by this method, e.g.
        // example.put("getName", "exampleName");
        // Tests generator will look for a method "getName()" and will expect it to return "exampleName" String.
        // If you are testing one class, you can simply return the method call - generateGetterTests(...)
        // If you are testing multiple classes, create a list that will contain the generated tests for each class, e.g.
        // List<DynamicTest> temp = new LinkedList<>();
        // Add all tests for each class, e.g. temp.addAll(generateGetterTests(...));
        // Finally, you can return this list - return temp;
        return null;
    }

    /**
     * Verifies that the setters of the target class update the attributes properly.
     *
     * @return A dynamic test list containing the test for each setter for the specified class(es).
     */
    @TestFactory
    @Timeout(10)
    @Public
    @MirrorOutput
    List<DynamicTest> settersTest() {
        // Your code here.
        // Example usage:
        // Create a map for each class that you want to test, e.g.Map<String, Object> example = new HashMap<>();
        // Put the target method name and the value that is expected to be returned by this method, e.g.
        // example.put("setName", "exampleName");
        // Tests generator will look for a method "setName()" and will expect it to set new value - "exampleName".
        // If you are testing one class, you can simply return the method call - generateSetterTests(...)
        // If you are testing multiple classes, create a list that will contain the generated tests for each class, e.g.
        // List<DynamicTest> temp = new LinkedList<>();
        // Add all tests for each class, e.g. temp.addAll(generateSetterTests(...));
        // Finally, you can return this list - return temp;
        // Don't forget that some setters may have special logic, so you need to provide a list of expected values in
        // addition to the map, so the test will use the list to verify the updated values. See the JavaDoc.
        return null;
    }
}
