package com.attiasas.gamedevtoolkitplugin.io.format;

import com.attiasas.gamedevtoolkitplugin.io.DataNode;

import java.nio.file.Paths;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class GDFormat implements IFormat<DataNode> {

    // Attributes indicator definition - at least one alphanumeric char and _.- also allowed
    public static String  alphaNumericWithSpecial() { return "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9_.-]+$"; }

    @Override
    public DataNode parse(String input) throws FormatException {
        return null;
    }

    @Override
    public String toString(DataNode data) {
        return null;
    }

    public static void main(String[] args) {
        GDFormat format = new GDFormat();
        try {
            DataNode data = format.parse(Paths.get("src/test/resources/testGDFormat.gdis"));
            System.out.println("Data:\n" + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
