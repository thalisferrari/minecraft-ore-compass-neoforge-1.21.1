package com.orecompass;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(OreCompass.MODID)
public class OreCompass {
    public static final String MODID = "ore_compass";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    // Ore Compass Items - Tier 1: Basic (16 block range)
    public static final DeferredHolder<Item, OreCompassItem> BASIC_ORE_COMPASS = ITEMS.register("basic_ore_compass",
            () -> new OreCompassItem(1, 16, new Item.Properties().stacksTo(1)));

    // Ore Compass Items - Tier 2: Advanced (32 block range)
    public static final DeferredHolder<Item, OreCompassItem> ADVANCED_ORE_COMPASS = ITEMS.register("advanced_ore_compass",
            () -> new OreCompassItem(2, 32, new Item.Properties().stacksTo(1)));

    // Ore Compass Items - Tier 3: Master (64 block range)
    public static final DeferredHolder<Item, OreCompassItem> MASTER_ORE_COMPASS = ITEMS.register("master_ore_compass",
            () -> new OreCompassItem(3, 64, new Item.Properties().stacksTo(1)));

    // Recipe Serializers
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TuneCompassRecipe>> TUNE_COMPASS_SERIALIZER =
            RECIPE_SERIALIZERS.register("tune_compass", TuneCompassRecipe.Serializer::new);

    // Creative Tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ORE_COMPASS_TAB = CREATIVE_MODE_TABS.register("ore_compass_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.TOOLS_AND_UTILITIES)
                    .icon(() -> MASTER_ORE_COMPASS.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.ore_compass.ore_compass_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(BASIC_ORE_COMPASS.get());
                        output.accept(ADVANCED_ORE_COMPASS.get());
                        output.accept(MASTER_ORE_COMPASS.get());
                    }).build());

    public OreCompass(IEventBus modEventBus, ModContainer modContainer) {
        // Register Deferred Registers
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("Ore Compass mod initialized!");
    }
}
