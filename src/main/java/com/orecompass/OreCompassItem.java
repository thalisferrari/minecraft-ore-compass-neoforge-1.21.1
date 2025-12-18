package com.orecompass;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for Ore Compass items
 */
public class OreCompassItem extends Item {
    private static final String NBT_TUNED_ORE = "TunedOre";
    private static final String NBT_TARGET_X = "TargetX";
    private static final String NBT_TARGET_Y = "TargetY";
    private static final String NBT_TARGET_Z = "TargetZ";
    private static final String NBT_DETECTED_ORE = "DetectedOre";

    private final int tier;
    private final int range;

    public OreCompassItem(int tier, int range, Properties properties) {
        super(properties);
        this.tier = tier;
        this.range = range;
    }

    public int getTier() {
        return tier;
    }

    public int getRange() {
        return range;
    }

    /**
     * Get custom data from ItemStack, or empty if none
     */
    private static CompoundTag getCustomData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return new CompoundTag();
    }

    /**
     * Set custom data on ItemStack
     */
    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * Set the tuned ore type for this compass
     */
    public static void setTunedOre(ItemStack stack, OreType oreType) {
        CompoundTag tag = getCustomData(stack);
        tag.putString(NBT_TUNED_ORE, oreType.getName());
        setCustomData(stack, tag);
    }

    /**
     * Get the tuned ore type from this compass
     */
    @Nullable
    public static OreType getTunedOre(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        if (tag.contains(NBT_TUNED_ORE)) {
            return OreType.fromName(tag.getString(NBT_TUNED_ORE));
        }
        return null;
    }

    /**
     * Check if this compass is tuned to a specific ore
     */
    public static boolean isTuned(ItemStack stack) {
        return getTunedOre(stack) != null;
    }

    /**
     * Store the target position in NBT
     */
    private static void setTargetPos(ItemStack stack, BlockPos pos) {
        CompoundTag tag = getCustomData(stack);
        tag.putInt(NBT_TARGET_X, pos.getX());
        tag.putInt(NBT_TARGET_Y, pos.getY());
        tag.putInt(NBT_TARGET_Z, pos.getZ());
        setCustomData(stack, tag);
    }

    /**
     * Get the stored target position
     */
    @Nullable
    public static BlockPos getTargetPos(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        if (tag.contains(NBT_TARGET_X)) {
            return new BlockPos(
                    tag.getInt(NBT_TARGET_X),
                    tag.getInt(NBT_TARGET_Y),
                    tag.getInt(NBT_TARGET_Z)
            );
        }
        return null;
    }

    /**
     * Set the detected ore type
     */
    private static void setDetectedOre(ItemStack stack, OreType oreType) {
        CompoundTag tag = getCustomData(stack);
        tag.putString(NBT_DETECTED_ORE, oreType.getName());
        setCustomData(stack, tag);
    }

    /**
     * Get the detected ore type (for non-tuned compasses)
     */
    @Nullable
    public static OreType getDetectedOre(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        if (tag.contains(NBT_DETECTED_ORE)) {
            return OreType.fromName(tag.getString(NBT_DETECTED_ORE));
        }
        return null;
    }

    /**
     * Clear the detected ore
     */
    private static void clearDetectedOre(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        tag.remove(NBT_DETECTED_ORE);
        setCustomData(stack, tag);
    }

    /**
     * Clear the stored target position
     */
    private static void clearTargetPos(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        tag.remove(NBT_TARGET_X);
        tag.remove(NBT_TARGET_Y);
        tag.remove(NBT_TARGET_Z);
        setCustomData(stack, tag);
    }

    /**
     * Check if the block at the given position is still a valid ore of the specified type
     */
    private boolean isValidOreAt(Level level, BlockPos pos, @Nullable OreType oreType) {
        if (pos == null || oreType == null) {
            return false;
        }
        return oreType.matches(level.getBlockState(pos).getBlock());
    }

    /**
     * Check if the block at the given position matches any ore in the list
     */
    private boolean isValidOreAt(Level level, BlockPos pos, List<OreType> oreTypes) {
        if (pos == null || oreTypes == null || oreTypes.isEmpty()) {
            return false;
        }
        for (OreType oreType : oreTypes) {
            if (oreType.matches(level.getBlockState(pos).getBlock())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            // Update every 20 ticks (1 second)
            if (level.getGameTime() % 20 == 0) {
                updateCompass(stack, level, player);
            }
        }
    }

    /**
     * Update the compass to point to the nearest ore
     */
    private void updateCompass(ItemStack stack, Level level, Player player) {
        BlockPos playerPos = player.blockPosition();
        BlockPos storedPos = getTargetPos(stack);

        OreType tunedOre = getTunedOre(stack);

        // First, validate the stored position is still valid
        if (storedPos != null) {
            boolean stillValid = false;
            if (tunedOre != null) {
                stillValid = isValidOreAt(level, storedPos, tunedOre);
            } else {
                List<OreType> detectableOres = OreType.getOresForTier(tier);
                stillValid = isValidOreAt(level, storedPos, detectableOres);
            }

            // If stored position is no longer valid, clear it
            if (!stillValid) {
                clearTargetPos(stack);
                storedPos = null;
            }
        }

        // Find nearest ore
        BlockPos targetPos = null;
        OreType detectedOreType = null;

        if (tunedOre != null) {
            // Tuned to specific ore
            targetPos = OreDetector.findNearestOre(level, playerPos, range, tunedOre);
            if (targetPos != null) {
                detectedOreType = tunedOre;
            }
        } else {
            // Detect all ores for this tier
            List<OreType> detectableOres = OreType.getOresForTier(tier);
            targetPos = OreDetector.findNearestOre(level, playerPos, range, detectableOres);

            // Identify which ore type was found
            if (targetPos != null) {
                for (OreType oreType : detectableOres) {
                    if (oreType.matches(level.getBlockState(targetPos).getBlock())) {
                        detectedOreType = oreType;
                        break;
                    }
                }
            }
        }

        // Update or clear position and detected ore
        if (targetPos != null && detectedOreType != null) {
            setTargetPos(stack, targetPos);
            setDetectedOre(stack, detectedOreType);
        } else {
            // No ore found, clear any stored position
            clearTargetPos(stack);
            clearDetectedOre(stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Force update when used
            updateCompass(stack, level, player);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // Show tier
        tooltip.add(Component.literal("Tier: " + tier)
                .withStyle(ChatFormatting.GRAY));

        // Show range
        tooltip.add(Component.literal("Range: " + range + " blocks")
                .withStyle(ChatFormatting.GRAY));

        // Show tuned ore if applicable
        OreType tunedOre = getTunedOre(stack);
        if (tunedOre != null) {
            tooltip.add(Component.literal("Tuned to: " + tunedOre.getName().toUpperCase())
                    .withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(Component.literal("Detects: All Tier " + tier + " ores")
                    .withStyle(ChatFormatting.AQUA));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Enchanted glow if tuned
        return isTuned(stack);
    }
}
