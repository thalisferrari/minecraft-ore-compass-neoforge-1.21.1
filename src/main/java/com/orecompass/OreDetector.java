package com.orecompass;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Handles ore detection logic for the Ore Compass
 */
public class OreDetector {

    /**
     * Find the highest priority ore within the given range
     * Priority is based on ore rarity (lower priority number = more valuable)
     * Among same priority ores, the nearest one is chosen
     *
     * @param level The world/level
     * @param playerPos The player's position
     * @param range The detection range in blocks
     * @param targetOres List of ore types to detect
     * @return BlockPos of highest priority ore, or null if none found
     */
    @Nullable
    public static BlockPos findNearestOre(Level level, BlockPos playerPos, int range, List<OreType> targetOres) {
        BlockPos bestPos = null;
        int bestPriority = Integer.MAX_VALUE;
        double bestDistance = Double.MAX_VALUE;

        // Scan in a cube around the player
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    // Check if this block matches any of our target ores
                    for (OreType oreType : targetOres) {
                        if (oreType.matches(block)) {
                            int priority = oreType.getPriority();
                            double distance = playerPos.distSqr(checkPos);

                            // Select if: higher priority (lower number) OR same priority but closer
                            if (priority < bestPriority || (priority == bestPriority && distance < bestDistance)) {
                                bestPriority = priority;
                                bestDistance = distance;
                                bestPos = checkPos;
                            }
                            break; // Found a match, no need to check other ore types
                        }
                    }
                }
            }
        }

        return bestPos;
    }

    /**
     * Find the nearest ore of a specific single type
     */
    @Nullable
    public static BlockPos findNearestOre(Level level, BlockPos playerPos, int range, OreType targetOre) {
        return findNearestOre(level, playerPos, range, List.of(targetOre));
    }

    /**
     * Count how many ores of specified types are in range
     */
    public static int countOresInRange(Level level, BlockPos playerPos, int range, List<OreType> targetOres) {
        int count = 0;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    for (OreType oreType : targetOres) {
                        if (oreType.matches(block)) {
                            count++;
                            break;
                        }
                    }
                }
            }
        }

        return count;
    }

    /**
     * Get the angle from player to target position (for compass needle rendering)
     */
    public static double getAngleToTarget(Player player, BlockPos target) {
        BlockPos playerPos = player.blockPosition();
        double dx = target.getX() - playerPos.getX();
        double dz = target.getZ() - playerPos.getZ();

        // Calculate angle in radians
        double angle = Math.atan2(dz, dx);

        // Convert to degrees and normalize
        double degrees = Math.toDegrees(angle);
        degrees = (degrees + 90) % 360; // Adjust so 0 is north
        if (degrees < 0) degrees += 360;

        return degrees;
    }
}
