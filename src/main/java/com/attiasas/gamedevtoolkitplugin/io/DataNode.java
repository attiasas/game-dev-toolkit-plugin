package com.attiasas.gamedevtoolkitplugin.io;


import com.attiasas.gamedevtoolkitplugin.utils.Pair;
import com.attiasas.gamedevtoolkitplugin.utils.RegexBuilder;

import java.util.*;
import java.util.function.Consumer;

/**
 * @Author: Assaf, On 2/28/2023
 * @Description:
 **/
public class DataNode implements Iterable {

    private static class ObjectReference extends Pair<Integer,DataNode> {
        public String indicator;
        public ObjectReference(DataNode node) {
            this.second = node;
        }
        public ObjectReference(int index, String indicator, DataNode node) {
            this.first = index;
            this.second = node;
            this.indicator = indicator;
        }
    }

    public static final String DEFAULT_ID_DELIMITER = new RegexBuilder().literal(".").build();

    private final ObjectReference myContainer;
    // single value or an ordered list of values
    private List<Object> content;
    // attribute indicator (name) -> object
    private Map<String, ObjectReference> objectReferences;

    public DataNode() {
        myContainer = new ObjectReference(this);
        content = new ArrayList<>();
        objectReferences = new HashMap<>();
    }

    public DataNode(String indicator) {
        this();
        myContainer.indicator = indicator;
    }

    // set/replace DataNode content
    public static DataNode putOrReplace(DataNode parent, String indicator, DataNode node, boolean keepPositionIfExists) {
        ObjectReference reference = parent.objectReferences.get(indicator);
        DataNode old = null;
        int nextIndexInList = parent.content.size();
        if (reference == null) {
            // create new reference and add node to content
            reference = new ObjectReference(nextIndexInList, indicator,node);
            parent.objectReferences.put(indicator,reference);
            parent.content.add(reference);
        } else {
            old = reference.second;
            reference.second = node;
            if (!keepPositionIfExists) {
                // remove and append reference from ordered content and update index
                parent.content.remove(reference.first);
                reference.first = nextIndexInList;
                parent.content.add(reference);
            }
        }
        return old;
    }

    public DataNode putOrReplace(String indicator, DataNode node, boolean keepPositionIfExists) {
        return putOrReplace(this, indicator, node, keepPositionIfExists);
    }

    public void add(Object... primitiveContents) {
        for (Object content : primitiveContents) {
            set(content,size());
        }
    }

    // set/replace content as array for all objects including anonymous DataNode
    public <T extends Object> T set(T primitiveContent, int index) {
        // TODO: change to store datanode (anonymus object or array), string, int or float
        // TODO: if adding and objReference exist after need to update index that needed for remove, maybe change that
        if (primitiveContent instanceof Object[]) {
            for (Object obj : ((Object[]) primitiveContent)) {
                this.content.add(index,obj);
                index++;
            }
            return null;
        }
        if (index == this.content.size()) {
            this.content.add(primitiveContent);
            return null;
        }
        return (T) this.content.set(index, primitiveContent);
    }

    // set/replace the content of the data (first data of array or single data content)
    public <T extends Object> T set(T primitiveContent) { return set(primitiveContent,0);}

    public int size() { return this.content.size(); }

    public boolean isEmpty() { return size() == 0; }

    public boolean has(String... indicators) {
        return has(Arrays.asList(indicators), DEFAULT_ID_DELIMITER);
    }

    public boolean has(List<String> indicators, String delimiterRegex) {
        indicators = splitIndicators(indicators,delimiterRegex);
        ObjectReference crawler = this.myContainer;
        for (String indicator : indicators) {
            crawler = crawler.second.objectReferences.get(indicator);
            if (crawler == null) {
                return false;
            }
        }
        return true;
    }

    public DataNode get(String... indicators) {
        return get(Arrays.asList(indicators),DEFAULT_ID_DELIMITER);
    }

    public DataNode get(List<String> indicators, String delimiterRegex) {
        indicators = splitIndicators(indicators,delimiterRegex);
        ObjectReference crawler = this.myContainer;
        for (String indicator : indicators) {
            // add new object if not exists to allow creating object attributes dynamically with data.get("att.newData.other")
            if (!crawler.second.objectReferences.containsKey(indicator)) {
                putOrReplace(crawler.second, indicator, new DataNode(indicator),false);
            }
            crawler = crawler.second.objectReferences.get(indicator);
        }
        return crawler.second;
    }

    private List<String> splitIndicators(List<String> indicators, String delimiterRegex) {
        List<String> result = new ArrayList<>();
        for (String indicator : indicators) {
            String [] noDelimiter = indicator.split(delimiterRegex);
            if (noDelimiter.length > 0) {
                for (int i = 0; i < noDelimiter.length; i++) {
                    result.add(noDelimiter[i]);
                }
            } else {
                result.add(indicator);
            }
        }
        return result;
    }

    public  <T extends Object> T get(int index) {
        Object o = this.content.get(index);
        if (o instanceof ObjectReference) {
            o = ((ObjectReference)o).second;
        }
        return (T)o;
    }

    public  <T extends Object> T get() {
        return get(0);
    }

    public String getString(int index) { return get(index).toString(); }

    public String getString() {
        return getString(0);
    }

    public float getFloat(int index) {
        return Float.parseFloat(getString(index));
    }

    public float getFloat() {
        return getFloat(0);
    }

    public int getInt(int index) {
        return Integer.parseInt(getString(index));
    }

    public int getInt() {
        return getInt(0);
    }

    public String indicator() {
        return myContainer.indicator;
    }

    @Override
    public Iterator iterator() {
        return this.content.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        this.content.forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        return this.content.spliterator();
    }

    public static String writeAsString(DataNode node, int indentLevel, String equalSymbol, String indent, String sep, String sepList, String surroundString, String startSurroundNode, String endSurroundNode, String startSurroundArray, String endSurroundArray, String endLine) {
        String agg = "";
        for (Object data : node.content) {
            if (data instanceof ObjectReference) {
                ObjectReference reference = (ObjectReference) data;
                DataNode child = reference.second;
                if (child.objectReferences.size() == 0) {
                    if (child.isEmpty()) {
                        // Empty attribute
                        agg += indent.repeat(indentLevel) + reference.indicator + endLine;
                    } else {
                        // The node is primitive or array
                        agg += writeValuesAsString(child, reference.indicator, indentLevel, equalSymbol, indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
                    }
                }
                else {
                    // The node is object with data
                    agg += writeObjAsString(child,reference.indicator,indentLevel,equalSymbol,indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
                }

            } else if (data instanceof DataNode) {
                agg += writeAnonymousAsString((DataNode) data, indentLevel, equalSymbol, indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
            }
        }
        return agg;
    }

    private static String writeAnonymousAsString(DataNode node, int indentLevel, String equalSymbol, String indent, String sep, String sepList, String surroundString, String startSurroundNode, String endSurroundNode, String startSurroundArray, String endSurroundArray, String endLine) {
        if (node.objectReferences.isEmpty()) {
            // Anonymous array or primitive value of anonymous
            return writeValuesAsString(node, null, indentLevel, equalSymbol, indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
        }
        // Anonymous object
        return writeObjAsString(node,null,indentLevel,equalSymbol,indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
    }

    private static String writeObjAsString(DataNode child, String indicator, int indentLevel, String equalSymbol, String indent, String sep, String sepList, String surroundString, String startSurroundNode, String endSurroundNode, String startSurroundArray, String endSurroundArray, String endLine) {
        String agg = indent.repeat(indentLevel) + (indicator != null ? indicator + sep : "") + startSurroundNode + endLine;
        agg += writeAsString(child,indentLevel + 1,equalSymbol,indent,sep,sepList,surroundString,startSurroundNode,endSurroundNode, startSurroundArray, endSurroundArray, endLine);
        agg += indent.repeat(indentLevel) + endSurroundNode + (indicator != null ? endLine : "");
        return agg + (indicator != null ? endLine : "");
    }

    // Anonymous array or primitive value of anonymous
    private static String writeValuesAsString(DataNode valueNode, String indicator, int indentLevel, String equalSymbol, String indent, String sep, String sepList, String surroundString, String startSurroundNode, String endSurroundNode, String startSurroundArray, String endSurroundArray, String endLine) {
        int nItemsLeft = valueNode.size();
        boolean hasNode = valueNode.content.stream().anyMatch(node -> node instanceof DataNode);
        boolean prevNode = hasNode;
        String agg = indent.repeat(indentLevel) + (indicator != null ? indicator + sep + equalSymbol + sep : "") + (nItemsLeft > 1 ? startSurroundArray : "");
        for (int i = 0; i < valueNode.size(); i++) {
            Object value = valueNode.get(i);
            if (value instanceof DataNode) {
                // Anonymous obj/array value
                prevNode = true;
                agg += endLine;
                agg += writeAnonymousAsString((DataNode) value, indentLevel + 1, equalSymbol, indent, sep, sepList, surroundString, startSurroundNode, endSurroundNode, startSurroundArray, endSurroundArray, endLine);
                agg += (nItemsLeft > 1 ? sepList + sep : "") + indent.repeat(indentLevel + (i == valueNode.size() - 1 ? 0 : 1));
            } else {
                // Primitive string/int/number
                if (prevNode) {
                    agg += endLine + indent.repeat(indentLevel + 1);
                }
                prevNode = false;
                agg += getPrimitiveValue(value, surroundString) + (nItemsLeft > 1 ? sepList + sep : "");
            }
            nItemsLeft--;
        }
        return agg + (hasNode ? endLine + indent.repeat(indentLevel) : "") + (valueNode.size() > 1 ? endSurroundArray : "") + (indicator != null ? endLine : "");
    }

    private static String getPrimitiveValue(Object value, String surroundString) {
        String asString = value.toString();
        if ((value instanceof Float) || (value instanceof Integer)) {
            return asString;
        }
        try {
            Float.parseFloat(asString);
            return asString;
        } catch (Exception e) {}
        try {
            Integer.parseInt(asString);
            return asString;
        } catch (Exception e) {}
        return surroundString + asString + surroundString;
    }

    @Override
    public String toString() {
        return writeAsString(this,0,"=","\t"," ",",","\"","{","}", "[", "]","\n");
    }

    public static void main(String[] args) {
        DataNode data = new DataNode();
        // add single string
        data.get("Version").set("1.0.0");
        // add numbers
        data.get("TestName.age").set(16);
        data.get("TestName.height").set(1.66);
        // add multiple objects
        DataNode friends = data.get("object2.friends");
        friends.get("Friend1Name.job").set("Developer");
        // add arrays
        friends.get("Friend1Name.numbers").set(new Integer[]{1,4,5});
        friends.get("Friend2Name.pets").add("cat","dog","frog");

        System.out.println(data);
    }
}
