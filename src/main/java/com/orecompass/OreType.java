package com.orecompass;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing different ore types that can be detected by the Ore Compass
 * Priority: lower number = higher priority (detected first)
 */
public enum OreType {
    // Tier 1 - Basic ores
    COAL("coal", 1, 8, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE),
    IRON("iron", 1, 5, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE),
    COPPER("copper", 1, 10, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE),

    // Tier 2 - Advanced ores
    GOLD("gold", 2, 7, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE),
    DIAMOND("diamond", 2, 2, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE),
    LAPIS("lapis", 2, 6, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE),
    REDSTONE("redstone", 2, 9, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE),
    EMERALD("emerald", 2, 4, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE),

    // Tier 3 - Master ores
    ANCIENT_DEBRIS("ancient_debris", 3, 1, Blocks.ANCIENT_DEBRIS),
    NETHER_QUARTZ("nether_quartz", 3, 3, Blocks.NETHER_QUARTZ_ORE);

    private final String name;
    private final int tier;
    private final int priority; // Lower = higher priority
    private final List<Block> blocks;

    OreType(String name, int tier, int priority, Block... blocks) {
        this.name = name;
        this.tier = tier;
        this.priority = priority;
        this.blocks = Arrays.asList(blocks);
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public int getPriority() {
        return priority;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * Check if a block is one of the ore blocks for this type
     */
    public boolean matches(Block block) {
        return blocks.contains(block);
    }

    /**
     * Get all ores available for a specific tier
     */
    public static List<OreType> getOresForTier(int tier) {
        return Arrays.stream(values())
                .filter(ore -> ore.getTier() <= tier)
                .toList();
    }

    /**
     * Get ore type by name
     */
    public static OreType fromName(String name) {
        return Arrays.stream(values())
                .filter(ore -> ore.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
