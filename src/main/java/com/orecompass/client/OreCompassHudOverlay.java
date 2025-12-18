package com.orecompass.client;

import com.orecompass.OreCompass;
import com.orecompass.OreCompassItem;
import com.orecompass.OreType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = OreCompass.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OreCompassHudOverlay {

    public static final ResourceLocation ORE_COMPASS_OVERLAY = ResourceLocation.fromNamespaceAndPath(OreCompass.MODID, "ore_compass_overlay");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            VanillaGuiLayers.HOTBAR,
            ORE_COMPASS_OVERLAY,
            new OreCompassLayer()
        );
    }

    private static class OreCompassLayer implements LayeredDraw.Layer {
        @Override
        public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null || minecraft.options.hideGui) {
                return;
            }

            ItemStack compass = getHeldCompass(player);
            if (compass == null) {
                return;
            }

            BlockPos targetPos = OreCompassItem.getTargetPos(compass);
            if (targetPos == null) {
                return;
            }

            String oreDisplayName = getOreDisplayName(compass);
            if (oreDisplayName == null) {
                return;
            }

            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();

            int textWidth = minecraft.font.width(oreDisplayName);
            int x = (screenWidth - textWidth) / 2;
            int y = screenHeight - 59;

            guiGraphics.drawString(
                minecraft.font,
                oreDisplayName,
                x, y,
                0xFFFFFF,
                true
            );
        }

        private ItemStack getHeldCompass(Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof OreCompassItem) {
                return mainHand;
            }

            ItemStack offHand = player.getOffhandItem();
            if (offHand.getItem() instanceof OreCompassItem) {
                return offHand;
            }

            return null;
        }

        private String getOreDisplayName(ItemStack stack) {
            // First check if tuned to specific ore
            OreType tunedOre = OreCompassItem.getTunedOre(stack);
            if (tunedOre != null) {
                return formatOreName(tunedOre.getName());
            }

            // For non-tuned compass, get the detected ore type
            OreType detectedOre = OreCompassItem.getDetectedOre(stack);
            if (detectedOre != null) {
                return formatOreName(detectedOre.getName());
            }

            return null;
        }

        private String formatOreName(String name) {
            String[] parts = name.toLowerCase().split("_");
            StringBuilder result = new StringBuilder();

            for (String part : parts) {
                if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0)));
                    result.append(part.substring(1));
                    result.append(" ");
                }
            }

            return result.toString().trim();
        }
    }
}
