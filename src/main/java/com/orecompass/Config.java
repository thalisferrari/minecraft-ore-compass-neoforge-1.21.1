package com.orecompass;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = OreCompass.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Basic Ore Compass settings
    private static final ModConfigSpec.IntValue BASIC_RANGE = BUILDER
            .comment("Detection range for Basic Ore Compass (in blocks)")
            .defineInRange("basicRange", 16, 8, 64);

    // Advanced Ore Compass settings
    private static final ModConfigSpec.IntValue ADVANCED_RANGE = BUILDER
            .comment("Detection range for Advanced Ore Compass (in blocks)")
            .defineInRange("advancedRange", 32, 16, 128);

    // Master Ore Compass settings
    private static final ModConfigSpec.IntValue MASTER_RANGE = BUILDER
            .comment("Detection range for Master Ore Compass (in blocks)")
            .defineInRange("masterRange", 64, 32, 256);

    // Update interval
    private static final ModConfigSpec.IntValue UPDATE_INTERVAL = BUILDER
            .comment("How often the compass updates in ticks (20 ticks = 1 second)")
            .defineInRange("updateInterval", 20, 5, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    // Runtime values
    public static int basicRange;
    public static int advancedRange;
    public static int masterRange;
    public static int updateInterval;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        basicRange = BASIC_RANGE.get();
        advancedRange = ADVANCED_RANGE.get();
        masterRange = MASTER_RANGE.get();
        updateInterval = UPDATE_INTERVAL.get();
    }
}
