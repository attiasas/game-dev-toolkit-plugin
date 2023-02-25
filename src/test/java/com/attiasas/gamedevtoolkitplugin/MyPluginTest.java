package com.attiasas.gamedevtoolkitplugin;

import com.intellij.testFramework.HeavyPlatformTestCase;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class MyPluginTest extends HeavyPlatformTestCase {
    private static final Path TEST_DATA_ROOT = Paths.get(".").toAbsolutePath().normalize().resolve(Paths.get("src", "test", "resources"));

    public void testExists() {
        assertTrue(TEST_DATA_ROOT.resolve("foo.xml").toFile().exists());
    }
}
