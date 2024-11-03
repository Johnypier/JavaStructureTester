package de.tum.cit.fop.structure;

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
 * @version 1.0 (03.11.2024)
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
//        Map<String, Object> expV1 = new HashMap<>();
//        expV1.put("getName", "testName");
//        expV1.put("getPhoneNumber", "testPhoneNumber");
//        expV1.put("getIsBayernPlayer", true);
//        Map<String, Object> expV2 = new HashMap<>();
//        expV2.put("getContacts", List.of());
//        List<DynamicTest> temp = new LinkedList<>();
//        temp.addAll(generateGetterTests("de.tum.cit.fop.Contact", List.of("testName", "testPhoneNumber", true),
//                                        new Class[]{String.class, String.class, boolean.class}, expV1));
//        // Target class has a default constructor without parameters.
//        temp.addAll(generateGetterTests("de.tum.cit.fop.PhoneBook", null,
//                                        null, expV2));
//        return temp;
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
//        // Your code here.
//        Map<String, Object> valuesToSet1 = new HashMap<>();
//        valuesToSet1.put("setName", "NotTestName");
//        valuesToSet1.put("setPhoneNumber", "NotTestPhoneNumber");
//        valuesToSet1.put("setIsBayernPlayer", false);
//        Map<String, Object> valuesToSet2 = new HashMap<>();
//        valuesToSet2.put("setContacts", null);
//        List<DynamicTest> temp = new LinkedList<>();
//        temp.addAll(generateSetterTests("de.tum.cit.fop.Contact", List.of("testName", "testPhoneNumber", true),
//                                        new Class[]{String.class, String.class, boolean.class}, valuesToSet1, null));
//        // Target class has a default constructor without parameters.
//        temp.addAll(generateSetterTests("de.tum.cit.fop.PhoneBook", null, null,
//                                        valuesToSet2, null));
//        return temp;
    }
}
