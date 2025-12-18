"""
Script to generate rotated compass textures for the Ore Compass mod.
Creates 32 rotated frames for each compass tier.
Only rotates the needle (red pixels), keeping the background static.

Requirements: pip install Pillow
"""

from PIL import Image
import os
import math

COMPASS_TIERS = [
    "basic_ore_compass",
    "advanced_ore_compass",
    "master_ore_compass"
]

NUM_FRAMES = 32
DEGREES_PER_FRAME = 360.0 / NUM_FRAMES

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
TEXTURES_DIR = os.path.join(PROJECT_ROOT, "src", "main", "resources", "assets", "ore_compass", "textures", "item")


def is_needle_pixel(r, g, b, a):
    """
    Detect if a pixel is part of the needle.
    Needle pixels are typically red (high R, low G, low B).
    """
    if a == 0:
        return False

    # Red pixels (needle) - includes all red shades used in compass
    # Covers: NEEDLE_TIP (255, 60, 60), NEEDLE_MID (220, 45, 45), NEEDLE_BASE (180, 35, 35)
    if r > 150 and g <= 70 and b <= 70 and r > g * 2 and r > b * 2:
        return True

    return False


def get_background_color(image, x, y):
    """
    Get the background color to use when removing a needle pixel.
    Samples neighboring pixels to find a suitable background color.
    """
    width, height = image.size

    # Check neighbors in order of preference
    neighbors = [
        (x - 1, y), (x + 1, y),  # horizontal neighbors first
        (x, y - 1), (x, y + 1),  # vertical neighbors
        (x - 1, y - 1), (x + 1, y - 1), (x - 1, y + 1), (x + 1, y + 1)  # diagonals
    ]

    for nx, ny in neighbors:
        if 0 <= nx < width and 0 <= ny < height:
            pixel = image.getpixel((nx, ny))
            r, g, b, a = pixel
            # Use this pixel if it's not part of the needle and not transparent
            if not is_needle_pixel(r, g, b, a) and a > 0:
                return pixel

    # Fallback to dark gray (common background color)
    return (47, 47, 47, 255)


def extract_needle_and_background(image):
    """
    Separate the needle pixels from the background.
    Returns (background_image, needle_pixels_list)
    where needle_pixels_list contains (x, y, color) tuples.
    """
    width, height = image.size
    background = image.copy()
    needle_pixels = []

    # First pass: identify needle pixels
    for y in range(height):
        for x in range(width):
            pixel = image.getpixel((x, y))
            r, g, b, a = pixel

            if is_needle_pixel(r, g, b, a):
                needle_pixels.append((x, y, pixel))

    # Second pass: replace needle pixels with background color
    for x, y, _ in needle_pixels:
        bg_color = get_background_color(image, x, y)
        background.putpixel((x, y), bg_color)

    return background, needle_pixels


def rotate_point(x, y, cx, cy, angle_degrees):
    """
    Rotate a point (x, y) around center (cx, cy) by angle_degrees.
    Returns new (x, y) coordinates.
    """
    angle_rad = math.radians(angle_degrees)
    cos_a = math.cos(angle_rad)
    sin_a = math.sin(angle_rad)

    # Translate to origin
    dx = x - cx
    dy = y - cy

    # Rotate
    new_dx = dx * cos_a - dy * sin_a
    new_dy = dx * sin_a + dy * cos_a

    # Translate back
    new_x = new_dx + cx
    new_y = new_dy + cy

    return new_x, new_y


def is_drawable_area(pixel):
    """
    Check if a pixel position is in the drawable area for the needle.
    The needle should only be drawn over the dark gray center area.
    """
    r, g, b, a = pixel
    if a == 0:
        return True

    # Allow drawing over dark gray areas (the center dial area)
    # These are typically grayscale values between 40-110
    if r == g == b and 40 <= r <= 110:
        return True

    # Also allow drawing over pixels that look like they were filled from background
    if r == g == b and 40 <= r <= 55:
        return True

    return False


def generate_rotated_frame(background, needle_pixels, angle, center, pivot_color):
    """
    Generate a single frame with the needle rotated by angle degrees.
    Draws the needle as a 2-pixel wide line from center outward, filling gaps.
    """
    frame = background.copy()
    cx, cy = center

    # Get colors and distances from original needle
    needle_data = []
    for x, y, color in needle_pixels:
        dist = math.sqrt((x + 0.5 - cx) ** 2 + (y + 0.5 - cy) ** 2)
        needle_data.append((dist, color))

    # Get min and max distance
    min_dist = min(d[0] for d in needle_data)
    max_dist = max(d[0] for d in needle_data)

    # Get colors sorted by distance
    needle_data.sort(key=lambda x: x[0])

    # Create color lookup by distance
    def get_color_for_dist(d):
        for dist, color in needle_data:
            if d <= dist:
                return color
        return needle_data[-1][1]

    # Convert angle to radians (0 degrees = up/north = -Y direction)
    angle_rad = math.radians(angle)

    # Direction vector (pointing outward from center)
    dir_x = math.sin(angle_rad)
    dir_y = -math.cos(angle_rad)

    # Perpendicular vector for width
    perp_x = math.cos(angle_rad)
    perp_y = math.sin(angle_rad)

    # Draw needle with small steps to avoid gaps
    step_size = 0.4  # Smaller than 1 pixel to ensure no gaps
    dist = min_dist

    while dist <= max_dist:
        color = get_color_for_dist(dist)

        # Calculate center position at this distance
        px = cx + dir_x * dist
        py = cy + dir_y * dist

        # Draw 2 pixels wide (perpendicular to needle direction)
        for offset in [-0.5, 0.5]:
            pixel_x = int(px + perp_x * offset)
            pixel_y = int(py + perp_y * offset)

            if 0 <= pixel_x < frame.width and 0 <= pixel_y < frame.height:
                existing = frame.getpixel((pixel_x, pixel_y))
                if is_drawable_area(existing):
                    frame.putpixel((pixel_x, pixel_y), color)

        dist += step_size

    return frame


def find_needle_center(needle_pixels, image_size):
    """
    Find the center point for rotation.
    Uses the center of the image as the pivot point.
    """
    width, height = image_size
    # Center of a 16x16 image
    return (width / 2, height / 2)


def generate_rotated_textures(tier_name: str):
    """
    Generate 32 rotated versions of a compass texture.
    """
    input_path = os.path.join(TEXTURES_DIR, f"{tier_name}.png")

    if not os.path.exists(input_path):
        print(f"Warning: Source texture not found: {input_path}")
        return False

    original = Image.open(input_path).convert("RGBA")

    print(f"Processing {tier_name} ({original.size[0]}x{original.size[1]})")

    # Extract needle and background
    background, needle_pixels = extract_needle_and_background(original)

    if not needle_pixels:
        print(f"  Warning: No needle pixels detected in {tier_name}")
        return False

    print(f"  Found {len(needle_pixels)} needle pixels")

    # Find rotation center
    center = find_needle_center(needle_pixels, original.size)
    print(f"  Rotation center: {center}")

    for frame in range(NUM_FRAMES):
        angle = frame * DEGREES_PER_FRAME

        # Generate rotated frame
        rotated_frame = generate_rotated_frame(background, needle_pixels, angle, center, None)

        output_path = os.path.join(TEXTURES_DIR, f"{tier_name}_{frame:02d}.png")
        rotated_frame.save(output_path, "PNG")

    print(f"  Generated {NUM_FRAMES} frames for {tier_name}")
    return True


def cleanup_old_textures():
    """
    Remove previously generated rotated textures.
    """
    for tier in COMPASS_TIERS:
        for frame in range(NUM_FRAMES):
            path = os.path.join(TEXTURES_DIR, f"{tier}_{frame:02d}.png")
            if os.path.exists(path):
                os.remove(path)


def main():
    print("=" * 50)
    print("Ore Compass Texture Generator")
    print("=" * 50)
    print(f"Output directory: {TEXTURES_DIR}")
    print(f"Frames per compass: {NUM_FRAMES}")
    print(f"Degrees per frame: {DEGREES_PER_FRAME}")
    print()

    if not os.path.exists(TEXTURES_DIR):
        print(f"Error: Textures directory not found: {TEXTURES_DIR}")
        return

    print("Cleaning up old textures...")
    cleanup_old_textures()
    print()

    success_count = 0
    for tier in COMPASS_TIERS:
        if generate_rotated_textures(tier):
            success_count += 1
        print()

    print(f"Done! Generated textures for {success_count}/{len(COMPASS_TIERS)} compass tiers.")
    print(f"Total textures created: {success_count * NUM_FRAMES}")


if __name__ == "__main__":
    main()
