package de.danielbechler.diff.node;

import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Strings;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This visitor can take the differencies and output the value from the modified object.
 * You can either get the result as a {@link Map} or get it as a JSON-string.
 * @author Patrick Fust (patrickfust)
 */
public class DiffJSONVisitor extends PrintingVisitor {

    private final Map<String, Object> messages = new LinkedHashMap<String, Object>();

    public DiffJSONVisitor(Object working, Object base) {
        super(working, base);
    }

    @Override
    protected String differenceToString(DiffNode node, Object base, Object modified) {
        String text = Strings.toSingleLineString(node.canonicalGet(modified));
        NodePath nodePath = node.getPath();
        getMapFromPath(nodePath).put(getLastName(nodePath), text);
        return text;
    }

    public Map<String, Object> getMessagesAsMap() {
        return messages;
    }

    public String getAsJSON() throws IOException {
        return new ObjectMapper().writeValueAsString(messages);
    }

    @Override
    protected void print(final String text) {
    }

    private Map<String, Object> getMapFromPath(NodePath path) {
        Map<String, Object> resultMap = messages;
        List<ElementSelector> elementSelectors = path.getElementSelectors();
        int idx = 0;
        for (ElementSelector elementSelector : elementSelectors) {
            if (elementSelector instanceof BeanPropertyElementSelector) {
                BeanPropertyElementSelector beanPropertyElementSelector = (BeanPropertyElementSelector) elementSelector;
                if (idx != elementSelectors.size() - 1) { // Has more -> go deeper
                    String key = beanPropertyElementSelector.getPropertyName();
                    Map<String, Object> resultMapTmp = (Map<String, Object>) resultMap.get(key);
                    if (resultMapTmp == null) {
                        resultMapTmp = new LinkedHashMap<String, Object>();
                        resultMap.put(key, resultMapTmp);
                        resultMap = resultMapTmp;
                    }
                }
            }
            idx++;
        }
        return resultMap;
    }

    private String getLastName(NodePath path) {
        return ((BeanPropertyElementSelector) path.getLastElementSelector()).getPropertyName();
    }
}
