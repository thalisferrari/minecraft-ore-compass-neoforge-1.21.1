"""
Script to update ore compass model JSON files to use two layers (body + shared needle).
"""

import json
import os

# Paths
MODELS_PATH = "../src/main/resources/assets/ore_compass/models/item"

# Compass tiers
TIERS = ["basic", "advanced", "master"]

# Number of frames per compass
FRAMES = 32

def update_frame_model(file_path, tier, frame):
    """
    Update a single frame model to use body + shared needle layers.
    """
    frame_str = f"{frame:02d}"

    new_model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"ore_compass:item/{tier}_ore_compass_body",
            "layer1": f"ore_compass:item/ore_compass_needle_{frame_str}"
        }
    }

    with open(file_path, 'w') as f:
        json.dump(new_model, f, indent=2)

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    models_dir = os.path.normpath(os.path.join(script_dir, MODELS_PATH))

    print(f"Updating models in: {models_dir}")

    for tier in TIERS:
        print(f"\nProcessing {tier} ore compass...")

        for frame in range(FRAMES):
            frame_str = f"{frame:02d}"
            file_path = os.path.join(models_dir, f"{tier}_ore_compass_{frame_str}.json")

            if os.path.exists(file_path):
                update_frame_model(file_path, tier, frame)
                print(f"  Updated: {tier}_ore_compass_{frame_str}.json")
            else:
                print(f"  Warning: {file_path} not found")

    print("\nDone! Model files updated.")

if __name__ == "__main__":
    main()
