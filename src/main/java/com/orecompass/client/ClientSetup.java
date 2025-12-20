package com.orecompass.client;

import com.orecompass.OreCompass;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = OreCompass.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static final ResourceLocation ANGLE_PROPERTY = ResourceLocation.fromNamespaceAndPath(OreCompass.MODID, "angle");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Create separate PropertyFunction for each compass to avoid shared state
            ItemProperties.register(
                OreCompass.BASIC_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                new OreCompassPropertyFunction()
            );

            ItemProperties.register(
                OreCompass.ADVANCED_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                new OreCompassPropertyFunction()
            );

            ItemProperties.register(
                OreCompass.MASTER_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                new OreCompassPropertyFunction()
            );
        });
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        OreCompassItemColor colorHandler = new OreCompassItemColor();
        event.register(colorHandler,
            OreCompass.BASIC_ORE_COMPASS.get(),
            OreCompass.ADVANCED_ORE_COMPASS.get(),
            OreCompass.MASTER_ORE_COMPASS.get()
        );
    }
}
