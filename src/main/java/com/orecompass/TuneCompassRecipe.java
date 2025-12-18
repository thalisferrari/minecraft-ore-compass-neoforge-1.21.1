package com.orecompass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Custom recipe for tuning an Ore Compass to a specific ore type
 * Requires: 1 Ore Compass + 4 of the target ore
 */
public class TuneCompassRecipe implements CraftingRecipe {
    private final Ingredient compass;
    private final Ingredient ore;
    private final String oreType;
    private final int oreCount;

    public TuneCompassRecipe(Ingredient compass, Ingredient ore, String oreType, int oreCount) {
        this.compass = compass;
        this.ore = ore;
        this.oreType = oreType;
        this.oreCount = oreCount;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasCompass = false;
        int oreMatches = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (compass.test(stack)) {
                if (hasCompass) {
                    return false; // Only one compass allowed
                }
                hasCompass = true;
            } else if (ore.test(stack)) {
                oreMatches++;
            } else {
                return false; // Invalid item in crafting grid
            }
        }

        return hasCompass && oreMatches == oreCount;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack compassStack = ItemStack.EMPTY;

        // Find the compass in the crafting grid
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (compass.test(stack)) {
                compassStack = stack.copy();
                break;
            }
        }

        if (compassStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Set the tuned ore type
        OreType targetOre = OreType.fromName(oreType);
        if (targetOre != null) {
            OreCompassItem.setTunedOre(compassStack, targetOre);
        }

        return compassStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 5; // Need at least 5 slots (1 compass + 4 ore)
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // Return a generic compass as display (actual result is dynamic)
        ItemStack result = new ItemStack(OreCompass.BASIC_ORE_COMPASS.get());
        OreType targetOre = OreType.fromName(oreType);
        if (targetOre != null) {
            OreCompassItem.setTunedOre(result, targetOre);
        }
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(compass);
        for (int i = 0; i < oreCount; i++) {
            ingredients.add(ore);
        }
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return OreCompass.TUNE_COMPASS_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    public Ingredient getCompass() {
        return compass;
    }

    public Ingredient getOre() {
        return ore;
    }

    public String getOreType() {
        return oreType;
    }

    public int getOreCount() {
        return oreCount;
    }

    public static class Serializer implements RecipeSerializer<TuneCompassRecipe> {
        public static final MapCodec<TuneCompassRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("compass").forGetter(TuneCompassRecipe::getCompass),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ore").forGetter(TuneCompassRecipe::getOre),
                        Codec.STRING.fieldOf("ore_type").forGetter(TuneCompassRecipe::getOreType),
                        Codec.INT.optionalFieldOf("ore_count", 4).forGetter(TuneCompassRecipe::getOreCount)
                ).apply(instance, TuneCompassRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TuneCompassRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TuneCompassRecipe::getCompass,
                Ingredient.CONTENTS_STREAM_CODEC, TuneCompassRecipe::getOre,
                ByteBufCodecs.STRING_UTF8, TuneCompassRecipe::getOreType,
                ByteBufCodecs.INT, TuneCompassRecipe::getOreCount,
                TuneCompassRecipe::new
        );

        @Override
        public MapCodec<TuneCompassRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TuneCompassRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
