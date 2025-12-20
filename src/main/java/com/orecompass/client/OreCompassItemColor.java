package com.orecompass.client;

import com.orecompass.OreCompassItem;
import com.orecompass.OreType;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

/**
 * Handles dynamic tinting of the ore compass needle based on the detected ore.
 */
public class OreCompassItemColor implements ItemColor {

    // Muted red color when no ore is being tracked
    private static final int DEFAULT_NEEDLE_COLOR = 0x8B4A4A;

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        // Layer 0 (body) - no tinting
        if (tintIndex != 1) {
            return 0xFFFFFF;
        }

        // Layer 1 (needle) - apply ore color
        OreType ore = OreCompassItem.getTunedOre(stack);
        if (ore == null) {
            ore = OreCompassItem.getDetectedOre(stack);
        }

        if (ore != null) {
            return ore.getColor();
        }

        return DEFAULT_NEEDLE_COLOR;
    }
}
