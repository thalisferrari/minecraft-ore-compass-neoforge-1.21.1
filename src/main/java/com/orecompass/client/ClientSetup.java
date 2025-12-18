package com.orecompass.client;

import com.orecompass.OreCompass;
import com.orecompass.OreCompassItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = OreCompass.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static final ResourceLocation ANGLE_PROPERTY = ResourceLocation.fromNamespaceAndPath(OreCompass.MODID, "angle");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            OreCompassPropertyFunction propertyFunction = new OreCompassPropertyFunction();

            ItemProperties.register(
                OreCompass.BASIC_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                propertyFunction
            );

            ItemProperties.register(
                OreCompass.ADVANCED_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                propertyFunction
            );

            ItemProperties.register(
                OreCompass.MASTER_ORE_COMPASS.get(),
                ANGLE_PROPERTY,
                propertyFunction
            );
        });
    }
}
