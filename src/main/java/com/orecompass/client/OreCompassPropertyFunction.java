package com.orecompass.client;

import com.orecompass.OreCompassItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class OreCompassPropertyFunction implements ClampedItemPropertyFunction {

    private final WobbleState wobbleState = new WobbleState();

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (entity == null || level == null) {
            return 0.0f;
        }

        BlockPos targetPos = OreCompassItem.getTargetPos(stack);

        if (targetPos == null) {
            return getSpinningValue(level);
        }

        double targetAngle = getAngleToTarget(entity, targetPos);
        double playerRotation = entity.getYRot();
        double relativeAngle = targetAngle - playerRotation;

        relativeAngle = normalizeAngle(relativeAngle);

        double wobbledAngle = wobbleState.update(level.getGameTime(), relativeAngle);

        return (float) (wobbledAngle / 360.0);
    }

    private double getAngleToTarget(Entity entity, BlockPos target) {
        double dx = target.getX() + 0.5 - entity.getX();
        double dz = target.getZ() + 0.5 - entity.getZ();
        // atan2 returns angle where 0 = east, 90 = north
        // Minecraft yaw: 0 = south, 90 = west, 180 = north, 270 = east
        double angle = Math.toDegrees(Math.atan2(-dx, dz));
        return angle;
    }

    private double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    private float getSpinningValue(ClientLevel level) {
        return (float) ((level.getGameTime() * 3 % 360) / 360.0);
    }

    private static class WobbleState {
        private double currentRotation = 0.0;
        private double rotationSpeed = 0.0;
        private long lastUpdateTick = -1;

        private static final double WOBBLE_DAMPENING = 0.9;
        private static final double WOBBLE_FORCE = 0.05;

        public double update(long gameTime, double targetAngle) {
            if (lastUpdateTick != gameTime) {
                double angleDiff = targetAngle - currentRotation;

                if (angleDiff > 180) angleDiff -= 360;
                if (angleDiff < -180) angleDiff += 360;

                rotationSpeed += angleDiff * WOBBLE_FORCE;
                rotationSpeed *= WOBBLE_DAMPENING;
                currentRotation += rotationSpeed;

                currentRotation = ((currentRotation % 360) + 360) % 360;
                lastUpdateTick = gameTime;
            }

            return currentRotation;
        }
    }
}
