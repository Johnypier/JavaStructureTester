package de.tum.cit.fop.structure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ivan Parmacli (ivan.parmacli@proton.me)
 * @version 1.0 (15.10.2024)
 * <br><br>
 * This class contains methods to parse JSON structure file and generate a list that contains element which represent
 * the expected structure.
 */
class StructureParser {
    private static final String NAME_KEY = "name";
    private static final String ANNOTATIONS_KEY = "annotations";
    private static final String MODIFIERS_KEY = "modifiers";
    private static final String PARAMETERS_KEY = "parameters";

    /**
     * This method parses the initial data of the class entry that is required to create a JSONClass object then
     * calls other helper methods to extract the expected attributes, methods and constructors.
     *
     * @return A list with JSONClass objects that represent the expected structure of the target project.
     * @throws URISyntaxException     thrown if the URI can not be created from a path to the 'test.json' file.
     * @throws IOException            thrown if the 'test.json' is not found or there was an error during the reading process.
     * @throws ClassNotFoundException thrown if the 'test.json' contains an invalid entry, where the class is defined
     *                                incorrectly.
     */
    protected static List<JSONClass> retrieveStructureFromJSON() throws URISyntaxException, IOException,
                                                              ClassNotFoundException {
        URL jsonFileURL = de.tum.cit.fop.structure.StructureTest.class.getResource("../test.json");
        if (jsonFileURL == null) {
            throw new FileNotFoundException("Could not find the structure file, make sure that it exists!");
        }
        JSONArray input = new JSONArray(
                Files.readString(Path.of(jsonFileURL.toURI())));
        List<JSONClass> output = new LinkedList<>();
        for (Object o : input) {
            // Select first entry in the json array. Basically the first class.
            JSONObject jsonObject = (JSONObject) o;

            // Collect class general data.
            JSONObject classObj = jsonObject.has("class") ? jsonObject.getJSONObject("class") : null;
            if (classObj == null) {
                throw new ClassNotFoundException(
                        "JSON structure file should include the correct class entry for the tests to be executed.");
            }

            String className = classObj.has(NAME_KEY) ? classObj.getString(NAME_KEY) : null;
            String classPackage = classObj.has("package") ? classObj.getString("package") : null;
            String superclass = classObj.has("superclass") ? classObj.getString("superclass") : null;
            boolean isInterface = classObj.has("isInterface") && classObj.getBoolean("isInterface");
            boolean isEnum = classObj.has("isEnum") && classObj.getBoolean("isEnum");
            boolean isAbstract = classObj.has("isAbstract") && classObj.getBoolean("isAbstract");

            // Collect enum values if the class is Enum.
            JSONArray enumValues = jsonObject.has("enumValues") ? jsonObject.getJSONArray("enumValues") : null;
            List<String> enumValuesString = null;
            if (enumValues != null) {
                enumValuesString = enumValues.toList().stream().map(obj -> (String) obj).toList();
            }

            // Collect class constructors.
            List<JSONConstructor> jsonConstructors = extractConstructorsFromJSON(
                    jsonObject.has("constructors") ? jsonObject.getJSONArray("constructors") : null);

            // Collect class attributes.
            List<JSONAttribute> jsonAttributes = extractAttributesFromJSON(
                    jsonObject.has("constructors") ? jsonObject.getJSONArray("attributes") : null);

            // Collect class methods.
            List<JSONMethod> jsonMethods = extractMethodsFromJSON(
                    jsonObject.has("methods") ? jsonObject.getJSONArray("methods") : null);

            output.add(new JSONClass(classPackage, className, superclass, isInterface, isEnum, isAbstract,
                                     enumValuesString, null, jsonMethods, jsonConstructors,
                                     jsonAttributes));
        }
        return output;
    }

    /**
     * This method parses the JSONArray to collect the expected attributes of the expected class.
     *
     * @param attributesArray JSONArray from the JSON object that contains expected class attributes.
     * @return A list with JSONAttribute objects that represent real attributes of the expected class.
     */
    private static List<JSONAttribute> extractAttributesFromJSON(JSONArray attributesArray) {
        if (attributesArray != null) {
            List<JSONAttribute> attributes = new LinkedList<>();
            for (Object o : attributesArray) {
                JSONObject attribute = (JSONObject) o;
                String name = attribute.has(NAME_KEY) ? attribute.getString("name") : null;
                String type = attribute.has("type") ? attribute.getString("type") : null;
                List<String> modifiers =
                        attribute.has(MODIFIERS_KEY) ? attribute.getJSONArray(MODIFIERS_KEY).toList().stream()
                                                                .map(obj -> (String) obj)
                                                                .toList() : new LinkedList<>();
                List<String> annotations =
                        attribute.has(ANNOTATIONS_KEY) ? attribute.getJSONArray(ANNOTATIONS_KEY).toList().stream()
                                                                  .map(obj -> (String) obj)
                                                                  .toList() : new LinkedList<>();
                attributes.add(new JSONAttribute(name, type, modifiers, annotations));
            }
            return attributes;
        }
        return new LinkedList<>();
    }

    /**
     * This method parses the JSONArray to collect the expected constructors of the expected class.
     *
     * @param constructorsArray JSONArray from the JSON object that contains expected class constructors.
     * @return A list with JSONConstructor objects that represent real constructors of the expected class.
     */
    private static List<JSONConstructor> extractConstructorsFromJSON(JSONArray constructorsArray) {
        if (constructorsArray != null) {
            List<JSONConstructor> jsonConstructors = new LinkedList<>();
            for (Object c : constructorsArray) {
                JSONObject constructor = (JSONObject) c;
                List<String> constructorModifiers =
                        constructor.has(MODIFIERS_KEY) ? constructor.getJSONArray(MODIFIERS_KEY).toList().stream()
                                                                    .map(obj -> (String) obj)
                                                                    .toList() : new LinkedList<>();
                List<String> constructorAnnotations =
                        constructor.has(ANNOTATIONS_KEY) ? constructor.getJSONArray(ANNOTATIONS_KEY).toList().stream()
                                                                      .map(obj -> (String) obj)
                                                                      .toList() : new LinkedList<>();
                List<String> constructorParameterTypes =
                        constructor.has(PARAMETERS_KEY) ? constructor.getJSONArray(PARAMETERS_KEY).toList().stream()
                                                                     .map(obj -> (String) obj)
                                                                     .toList() : new LinkedList<>();
                jsonConstructors.add(new JSONConstructor(constructorModifiers, constructorParameterTypes,
                                                         constructorAnnotations));
            }
            return jsonConstructors;
        }
        return new LinkedList<>();
    }

    /**
     * This method parses the JSONArray to collect the expected methods of the expected class.
     *
     * @param methodsArray JSONArray from the JSON object that contains expected class methods.
     * @return A list with JSONMethod objects that represent real methods of the expected class.
     */
    private static List<JSONMethod> extractMethodsFromJSON(JSONArray methodsArray) {
        if (methodsArray != null) {
            List<JSONMethod> methods = new LinkedList<>();
            for (Object o : methodsArray) {
                JSONObject m = (JSONObject) o;
                String name = m.has(NAME_KEY) ? m.getString(NAME_KEY) : null;
                String returnType = m.has("returnType") ? m.getString("returnType") : null;
                List<String> modifiers = m.has(MODIFIERS_KEY) ? m.getJSONArray(MODIFIERS_KEY).toList().stream()
                                                                 .map(obj -> (String) obj).toList()
                                                              : new LinkedList<>();
                List<String> parameters = m.has(PARAMETERS_KEY) ? m.getJSONArray(PARAMETERS_KEY).toList().stream()
                                                                   .map(obj -> (String) obj)
                                                                   .toList() : new LinkedList<>();
                List<String> annotations = m.has(ANNOTATIONS_KEY) ? m.getJSONArray(ANNOTATIONS_KEY).toList().stream()
                                                                     .map(obj -> (String) obj)
                                                                     .toList() : new LinkedList<>();
                methods.add(new JSONMethod(name, returnType, modifiers, parameters, annotations));
            }
            return methods;
        }
        return new LinkedList<>();
    }

    protected static class JSONClass {
        String packageName;
        String name;
        String superclass;
        boolean isInterface;
        boolean isEnum;
        boolean isAbstract;
        List<String> enumValues;
        List<String> annotations;
        List<JSONMethod> methods;
        List<JSONConstructor> constructors;
        List<JSONAttribute> attributes;

        public JSONClass(String packageName, String name, String superclass, boolean isInterface,
                         boolean isEnum, boolean isAbstract, List<String> enumValues,
                         List<String> annotations, List<JSONMethod> methods,
                         List<JSONConstructor> constructors,
                         List<JSONAttribute> attributes) {
            this.packageName = packageName;
            this.name = name;
            this.superclass = superclass;
            this.isInterface = isInterface;
            this.isAbstract = isAbstract;
            this.isEnum = isEnum;
            this.enumValues = enumValues;
            this.annotations = annotations;
            this.methods = methods;
            this.constructors = constructors;
            this.attributes = attributes;
        }
    }

    protected static class JSONMethod {
        String name;
        String returnType;
        List<String> modifiers;
        List<String> parameters;
        List<String> annotations;

        public JSONMethod(String name, String returnType, List<String> modifiers, List<String> parameters,
                          List<String> annotations) {
            this.name = name;
            this.returnType = returnType;
            this.modifiers = modifiers;
            this.parameters = parameters;
            this.annotations = annotations;
        }
    }

    protected static class JSONAttribute {
        String name;
        String type;
        List<String> modifiers;
        List<String> annotations;

        public JSONAttribute(String name, String type, List<String> modifiers, List<String> annotations) {
            this.name = name;
            this.type = type;
            this.modifiers = modifiers;
            this.annotations = annotations;
        }
    }

    protected static class JSONConstructor {
        List<String> modifiers;
        List<String> parametersTypes;
        List<String> annotations;

        public JSONConstructor(List<String> modifiers, List<String> parametersTypes, List<String> annotations) {
            this.annotations = annotations;
            this.modifiers = modifiers;
            this.parametersTypes = parametersTypes;
        }
    }
}
