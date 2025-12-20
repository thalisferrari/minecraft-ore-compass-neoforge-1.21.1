"""
Script to separate ore compass textures into body and needle layers.
The needle will be converted to white for tinting support.
Body textures will have the gray background where the needle was.
Only one set of needle textures is created (shared across all tiers).
"""

from PIL import Image
import os

# Paths
TEXTURES_PATH = "../src/main/resources/assets/ore_compass/textures/item"

# Compass tiers
TIERS = ["basic", "advanced", "master"]

# Number of frames per compass
FRAMES = 32

# Background color (gray center of compass) - will be detected from texture
BACKGROUND_COLOR = (68, 68, 68, 255)  # Approximate gray, will refine

def is_needle_pixel(r, g, b, a):
    """
    Detect if a pixel is part of the needle (bright red tones).
    Needle pixels have high red, very low green/blue.
    Must be restrictive to avoid detecting copper/brown border pixels.
    """
    if a == 0:
        return False

    # Bright red pixels: R is very high, G and B are very low
    # The needle is bright red (~180+ R, ~60 or less G/B)
    # Copper/brown border is more like (180, 120, 80) - has more G/B
    is_bright_red = r > 150 and g < 100 and b < 100 and r > g * 2.5 and r > b * 2.5

    return is_bright_red

def get_background_color(img):
    """
    Get the background color from the center of the compass.
    Sample a pixel that should be the gray background.
    """
    width, height = img.size
    pixels = img.load()

    # Sample from center area, look for gray pixels
    center_x, center_y = width // 2, height // 2

    # Search around center for a non-red, non-brown pixel (the gray background)
    for dy in range(-5, 6):
        for dx in range(-5, 6):
            x, y = center_x + dx, center_y + dy
            if 0 <= x < width and 0 <= y < height:
                r, g, b, a = pixels[x, y]
                if a > 0:
                    # Look for grayish pixels (R, G, B similar values, not red)
                    if abs(r - g) < 30 and abs(g - b) < 30 and abs(r - b) < 30:
                        if r < 150:  # Dark gray
                            return (r, g, b, a)

    # Fallback to a standard gray
    return (68, 68, 68, 255)

def create_body_texture(input_path, output_path, bg_color):
    """
    Create body texture with gray background where needle was.
    """
    img = Image.open(input_path).convert("RGBA")
    width, height = img.size

    body_img = Image.new("RGBA", (width, height), (0, 0, 0, 0))

    pixels = img.load()
    body_pixels = body_img.load()

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]

            if a == 0:
                continue

            if is_needle_pixel(r, g, b, a):
                # Replace needle with background color
                body_pixels[x, y] = bg_color
            else:
                # Keep original color
                body_pixels[x, y] = (r, g, b, a)

    body_img.save(output_path)

def create_needle_texture(input_path, output_path):
    """
    Create needle texture (white needle for tinting).
    """
    img = Image.open(input_path).convert("RGBA")
    width, height = img.size

    needle_img = Image.new("RGBA", (width, height), (0, 0, 0, 0))

    pixels = img.load()
    needle_pixels = needle_img.load()

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]

            if a > 0 and is_needle_pixel(r, g, b, a):
                # Convert to white for tinting
                needle_pixels[x, y] = (255, 255, 255, a)

    needle_img.save(output_path)

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    textures_dir = os.path.normpath(os.path.join(script_dir, TEXTURES_PATH))

    print(f"Processing textures in: {textures_dir}")

    # First, detect background color from basic compass
    first_frame_path = os.path.join(textures_dir, "basic_ore_compass_00.png")
    if os.path.exists(first_frame_path):
        img = Image.open(first_frame_path).convert("RGBA")
        bg_color = get_background_color(img)
        print(f"Detected background color: RGB{bg_color[:3]}")
    else:
        bg_color = BACKGROUND_COLOR
        print(f"Using default background color: RGB{bg_color[:3]}")

    # Create body textures for each tier
    for tier in TIERS:
        print(f"\nProcessing {tier} ore compass body...")

        first_frame_path = os.path.join(textures_dir, f"{tier}_ore_compass_00.png")
        body_output_path = os.path.join(textures_dir, f"{tier}_ore_compass_body.png")

        if os.path.exists(first_frame_path):
            create_body_texture(first_frame_path, body_output_path, bg_color)
            print(f"  Created: {tier}_ore_compass_body.png")
        else:
            print(f"  Warning: {first_frame_path} not found")

    # Create only ONE set of needle textures (from basic tier, shared by all)
    print(f"\nCreating shared needle textures...")

    for frame in range(FRAMES):
        frame_str = f"{frame:02d}"
        # Use basic tier as source for needle
        input_path = os.path.join(textures_dir, f"basic_ore_compass_{frame_str}.png")
        needle_output_path = os.path.join(textures_dir, f"ore_compass_needle_{frame_str}.png")

        if os.path.exists(input_path):
            create_needle_texture(input_path, needle_output_path)
            print(f"  Created: ore_compass_needle_{frame_str}.png")
        else:
            print(f"  Warning: {input_path} not found")

    # Clean up old tier-specific needle textures
    print(f"\nCleaning up old tier-specific needle textures...")
    for tier in TIERS:
        for frame in range(FRAMES):
            frame_str = f"{frame:02d}"
            old_needle_path = os.path.join(textures_dir, f"{tier}_ore_compass_needle_{frame_str}.png")
            if os.path.exists(old_needle_path):
                os.remove(old_needle_path)
                print(f"  Removed: {tier}_ore_compass_needle_{frame_str}.png")

    print("\nDone! Texture separation complete.")

if __name__ == "__main__":
    main()
