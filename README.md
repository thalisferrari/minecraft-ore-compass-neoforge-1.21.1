# Ore Compass

A Minecraft NeoForge mod (1.21.1) that adds magical compasses capable of detecting and pointing towards nearby ores.

## Features

- **Animated Compass Needle**: The compass needle rotates smoothly to point towards detected ores, just like the vanilla compass
- **Color-Coded Needle**: The needle color changes to match the tracked ore (cyan for diamond, red for redstone, etc.)
- **Three Compass Tiers**: Basic, Advanced, and Master compasses with increasing detection ranges
- **Priority-Based Detection**: Detects the rarest ore first, not just the closest one
- **Ore Tuning**: Tune your compass to search for a specific ore type

## Compass Tiers

| Tier | Name | Range | Detectable Ores |
|------|------|-------|-----------------|
| 1 | Basic Ore Compass | 16 blocks | Coal, Iron, Copper |
| 2 | Advanced Ore Compass | 32 blocks | All Tier 1 + Gold, Diamond, Lapis, Redstone, Emerald |
| 3 | Master Ore Compass | 64 blocks | All Tier 2 + Ancient Debris, Nether Quartz |

## Ore Detection Priority

When multiple ores are in range, the compass prioritizes by rarity (not distance):

1. Ancient Debris (highest priority)
2. Diamond
3. Nether Quartz
4. Emerald
5. Iron
6. Lapis Lazuli
7. Gold
8. Coal
9. Redstone
10. Copper (lowest priority)

## Crafting Recipes

### Basic Ore Compass

```
[Copper Ingot] [Lapis Lazuli] [Copper Ingot]
[Iron Ingot]   [Compass]      [Iron Ingot]
[Redstone]     [Quartz]       [Redstone]
```

### Advanced Ore Compass

```
[Gold Ingot]      [Diamond]            [Gold Ingot]
[Lapis Lazuli]    [Basic Ore Compass]  [Emerald]
[Redstone Block]  [Amethyst Shard]     [Redstone Block]
```

### Master Ore Compass

```
[Netherite Scrap]  [Echo Shard]             [Netherite Scrap]
[Ancient Debris]   [Advanced Ore Compass]   [Ancient Debris]
[Netherite Scrap]  [Lodestone]              [Netherite Scrap]
```

### Tuning Recipes

To tune a compass to detect only a specific ore type, combine any Ore Compass with 4 of the target material in a crafting table:

| Target Ore | Required Items |
|------------|----------------|
| Coal | 4x Coal |
| Iron | 4x Raw Iron |
| Copper | 4x Raw Copper |
| Gold | 4x Raw Gold |
| Diamond | 4x Diamond |
| Lapis Lazuli | 4x Lapis Lazuli |
| Redstone | 4x Redstone |
| Emerald | 4x Emerald |
| Ancient Debris | 4x Ancient Debris |
| Nether Quartz | 4x Quartz |

## How to Use

1. **Craft a compass** of the desired tier
2. **Hold the compass** in your main hand or off-hand
3. **Watch the needle** - it will point towards the nearest ore (by priority) within range
4. **Check the needle color** - the color indicates which ore is being tracked
5. **Optional: Tune the compass** to search for a specific ore type

## Compass Behavior

- **Ore Found**: The needle points steadily towards the ore location and changes color to match the ore
- **No Ore in Range**: The needle spins randomly with a muted red color
- **Tuned Compass**: Only searches for the specified ore type, ignoring all others

## Needle Colors

| Ore | Needle Color |
|-----|--------------|
| Coal | Dark gray |
| Iron | Beige/salmon |
| Copper | Orange |
| Gold | Yellow |
| Diamond | Cyan |
| Lapis Lazuli | Dark blue |
| Redstone | Red |
| Emerald | Green |
| Ancient Debris | Brown |
| Nether Quartz | White/cream |
| No target | Muted red |

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1
2. Download the Ore Compass mod JAR file
3. Place the JAR file in your `mods` folder
4. Launch Minecraft with the NeoForge profile

## Compatibility

- **Minecraft Version**: 1.21.1
- **Mod Loader**: NeoForge 21.1.x
- **JEI**: Fully compatible - all recipes are visible in JEI

## License

All Rights Reserved

## Author

thalislf
