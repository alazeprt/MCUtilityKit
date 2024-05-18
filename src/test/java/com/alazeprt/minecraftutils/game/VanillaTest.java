package com.alazeprt.minecraftutils.game;

import org.junit.jupiter.api.Test;

public class VanillaTest {
    @Test
    public void extractNativesTest() throws Exception {
        Vanilla.extractNatives(".minecraft", "24w20a");
    }
}
