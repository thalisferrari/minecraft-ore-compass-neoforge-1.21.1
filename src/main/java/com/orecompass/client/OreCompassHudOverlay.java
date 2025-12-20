package com.orecompass.client;

import com.orecompass.OreCompass;
import com.orecompass.OreCompassItem;
import com.orecompass.OreType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = OreCompass.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OreCompassHudOverlay {

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            ResourceLocation.fromNamespaceAndPath(OreCompass.MODID, "ore_compass_hud"),
            OreCompassHudOverlay::render
        );
    }

    private static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            return;
        }

        // Check if player is holding an ore compass
        ItemStack compassStack = null;

        if (player.getMainHandItem().getItem() instanceof OreCompassItem) {
            compassStack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof OreCompassItem) {
            compassStack = player.getOffhandItem();
        }

        if (compassStack == null) {
            return;
        }

        // Get the tracked ore
        OreType ore = OreCompassItem.getDetectedOre(compassStack);
        if (ore == null) {
            ore = OreCompassItem.getTunedOre(compassStack);
        }

        if (ore == null) {
            return;
        }

        // Render the ore block icon in the bottom-right corner at 32x32
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int size = 32;
        int margin = 5;
        int x = screenWidth - size - margin;
        int y = screenHeight - size - margin;

        ItemStack oreBlockStack = new ItemStack(ore.getOreBlock());

        // Scale up to 32x32 (default is 16x16)
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(2.0f, 2.0f, 1.0f);
        guiGraphics.renderItem(oreBlockStack, 0, 0);
        guiGraphics.pose().popPose();
    }
}
