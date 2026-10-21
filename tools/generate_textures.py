"""
Generates every texture for Emerald Gear with Pillow:
  - 9 item icons (16x16)
  - 2 armor layers (64x32) that get wrapped onto the player model
  - the mod icon (128x128)

Run from the project root:   python3 tools/generate_textures.py

If you hand-edit a texture in an image editor, either stop running this script
or copy your edited file somewhere safe first, because it overwrites the PNGs.
"""
import sys

# The textures in this project were redrawn from a vanilla reference, so running this script
# would overwrite them with the old generated look. Pass --force if you really want that.
if "--force" not in sys.argv:
    print("Refusing to run: this would overwrite the current textures. Use --force to continue.")
    sys.exit(1)

import math
import os

from PIL import Image

RES = os.path.join("src", "main", "resources")
ITEM_DIR = os.path.join(RES, "assets", "emeraldgear", "textures", "item")
ARMOR_DIR = os.path.join(RES, "assets", "minecraft", "textures", "models", "armor")
ICON_PATH = os.path.join(RES, "assets", "emeraldgear", "icon.png")

# ---------------------------------------------------------------------------------------------
# palette
# ---------------------------------------------------------------------------------------------
OUT = (5, 38, 22, 255)      # outline
D1 = (11, 84, 46, 255)      # darkest green
D2 = (15, 124, 65, 255)     # dark green
M = (22, 170, 88, 255)      # base green
L = (48, 216, 116, 255)     # light green
H = (138, 247, 184, 255)    # highlight
W = (224, 255, 236, 255)    # sparkle
B0 = (52, 33, 16, 255)      # stick, darkest
B1 = (88, 56, 27, 255)      # stick, mid
B2 = (124, 82, 40, 255)     # stick, light


# ---------------------------------------------------------------------------------------------
# tiny canvas helpers
# ---------------------------------------------------------------------------------------------
class Canvas:
    def __init__(self, w, h):
        self.w = w
        self.h = h
        self.px = {}

    def put(self, x, y, color):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[(x, y)] = color

    def paint(self, colored):
        for (x, y), c in colored.items():
            self.put(x, y, c)

    def outline(self, color=OUT, diagonal=False):
        """Adds a 1px outline around everything already drawn."""
        steps = [(1, 0), (-1, 0), (0, 1), (0, -1)]
        if diagonal:
            steps += [(1, 1), (-1, -1), (1, -1), (-1, 1)]
        new = {}
        for (x, y) in self.px:
            for dx, dy in steps:
                p = (x + dx, y + dy)
                if p not in self.px and 0 <= p[0] < self.w and 0 <= p[1] < self.h:
                    new[p] = color
        self.px.update(new)

    def image(self):
        img = Image.new("RGBA", (self.w, self.h), (0, 0, 0, 0))
        for (x, y), c in self.px.items():
            img.putpixel((x, y), c)
        return img


def rect(x, y, w, h):
    return {(i, j) for i in range(x, x + w) for j in range(y, y + h)}


def noise(x, y, salt=0):
    """Deterministic pseudo random number in [0, 1) so textures are the same every run."""
    n = (x * 73856093) ^ (y * 19349663) ^ (salt * 83492791)
    n = (n ^ (n >> 13)) * 1274126177
    n = n ^ (n >> 16)
    return (n & 0xFFFF) / 65536.0


def bevel(shape, salt=0, speckle=True):
    """
    Colors a set of pixels like a little beveled plate:
    light from the top-left, shadow on the bottom-right, a bit of speckle in the middle.
    """
    out = {}
    for (x, y) in shape:
        left_open = (x - 1, y) not in shape
        top_open = (x, y - 1) not in shape
        right_open = (x + 1, y) not in shape
        bottom_open = (x, y + 1) not in shape

        lit = left_open or top_open
        shaded = right_open or bottom_open

        if lit and shaded:
            color = M
        elif lit:
            color = H if (left_open and top_open) else L
        elif shaded:
            color = D1 if (right_open and bottom_open) else D2
        else:
            r = noise(x, y, salt)
            if not speckle:
                color = M
            elif r < 0.16:
                color = L
            elif r < 0.30:
                color = D2
            else:
                color = M
        out[(x, y)] = color
    return out


def stick(points):
    """Colors a list of handle pixels in two-tone brown, darker towards the bottom."""
    out = {}
    for i, p in enumerate(points):
        out[p] = B1 if i % 2 == 0 else B2
    if points:
        out[points[0]] = B0
    return out


# ---------------------------------------------------------------------------------------------
# tools (16x16)
# ---------------------------------------------------------------------------------------------
def sword():
    c = Canvas(16, 16)

    # handle (x + y = 14), pommel at the bottom
    c.paint(stick([(0, 14), (1, 13), (2, 12), (3, 11)]))

    # guard: a short diagonal bar crossing the handle, two pixels thick
    guard = set()
    for d in range(-2, 3):
        guard.add((4 + d, 10 + d))
        guard.add((3 + d, 10 + d))
    guard_colors = {}
    for p in guard:
        guard_colors[p] = B2 if (p[0] + p[1]) % 2 == 0 else B1
    c.paint(guard_colors)

    # blade: three pixels wide on a diagonal, tapering to a point
    blade = set()
    for y in range(1, 10):
        cx = 14 - y
        for dx in (-1, 0, 1):
            blade.add((cx + dx, y))
    blade.discard((12, 1))      # pointy tip
    blade.discard((13, 0))
    colored = bevel(blade, salt=1, speckle=False)
    # shiny edge down the middle
    for y in range(2, 9):
        colored[(14 - y, y)] = L if y % 3 else H
    c.paint(colored)
    c.outline()
    return c


def pickaxe():
    c = Canvas(16, 16)

    handle = [(1 + i, 13 - i) for i in range(0, 9)]     # x + y = 14
    c.paint(stick(handle))

    cx, cy = 3.0, 11.0
    radius = 8.5
    head = set()
    for y in range(16):
        for x in range(16):
            dx, dy = x - cx, y - cy
            d = math.hypot(dx, dy)
            ang = math.degrees(math.atan2(dy, dx))
            if -102 <= ang <= 10:
                u = max(-1.0, min(1.0, (ang + 46) / 56.0))
                half_width = 0.55 + 1.05 * math.cos(u * math.pi / 2)
                if abs(d - radius) <= half_width:
                    head.add((x, y))
    c.paint(bevel(head, salt=2))
    c.outline()
    return c


def axe():
    c = Canvas(16, 16)

    handle = [(1 + i, 13 - i) for i in range(0, 10)]    # x + y = 14
    c.paint(stick(handle))

    rows = {
        1: (7, 10),
        2: (6, 11),
        3: (5, 12),
        4: (4, 12),
        5: (3, 11),
        6: (3, 9),
        7: (3, 7),
        8: (3, 5),
    }
    head = set()
    for y, (x0, x1) in rows.items():
        for x in range(x0, x1 + 1):
            head.add((x, y))
    colored = bevel(head, salt=3)
    # keen edge on the left side of the blade
    for y in (4, 5, 6, 7, 8):
        x0 = rows[y][0]
        colored[(x0, y)] = H
    c.paint(colored)
    c.outline()
    return c


def shovel():
    c = Canvas(16, 16)

    handle = [(1 + i, 13 - i) for i in range(0, 7)]     # x + y = 14
    c.paint(stick(handle))

    # spade blade: an oval stretched along the up-right diagonal
    cx, cy = 10.5, 4.5
    head = set()
    for y in range(16):
        for x in range(16):
            dx, dy = x - cx, y - cy
            along = (dx - dy) / math.sqrt(2)     # towards the tip
            across = (dx + dy) / math.sqrt(2)
            if (along / 5.2) ** 2 + (across / 2.9) ** 2 <= 1.0:
                head.add((x, y))
    colored = bevel(head, salt=4, speckle=False)
    # ridge down the middle of the spade
    for t in range(-3, 4):
        p = (int(round(cx + t)), int(round(cy - t)))
        if p in colored:
            colored[p] = H if t == 1 else L
    c.paint(colored)
    c.outline()
    return c


def hoe():
    c = Canvas(16, 16)

    handle = [(1 + i, 13 - i) for i in range(0, 11)]    # x + y = 14
    c.paint(stick(handle))

    head = set()
    for x in range(6, 12):
        head.add((x, 1))
    for x in range(5, 13):
        head.add((x, 2))
    for x in range(5, 10):
        head.add((x, 3))
    head.add((5, 4))
    head.add((6, 4))
    head.add((5, 5))
    colored = bevel(head, salt=5)
    colored[(5, 5)] = H
    colored[(5, 4)] = L
    c.paint(colored)
    c.outline()
    return c


# ---------------------------------------------------------------------------------------------
# armor icons (16x16)
# ---------------------------------------------------------------------------------------------
def helmet():
    c = Canvas(16, 16)
    shape = set()
    spans = {
        3: (5, 10),
        4: (4, 11),
        5: (3, 12),
        6: (3, 12),
        7: (3, 12),
        8: (3, 12),
    }
    for y, (x0, x1) in spans.items():
        for x in range(x0, x1 + 1):
            shape.add((x, y))
    # cheek guards with an open face between them
    for y in (9, 10, 11):
        for x in (3, 4, 5, 10, 11, 12):
            shape.add((x, y))
    shape.discard((3, 11))
    shape.discard((12, 11))
    colored = bevel(shape, salt=6)
    # brow band and a little gem
    for x in range(4, 12):
        colored[(x, 7)] = D2
    colored[(7, 5)] = W
    colored[(8, 5)] = H
    colored[(7, 6)] = L
    colored[(8, 6)] = M
    c.paint(colored)
    c.outline()
    return c


def chestplate():
    c = Canvas(16, 16)
    shape = set()
    # shoulders
    for y in (2, 3):
        for x in (2, 3, 4, 5, 10, 11, 12, 13):
            shape.add((x, y))
    shape.add((1, 3))
    shape.add((14, 3))
    # chest and sleeves
    for y in range(4, 9):
        for x in range(1, 15):
            shape.add((x, y))
    for y in range(9, 14):
        for x in range(4, 12):
            shape.add((x, y))
    # neck hole
    for y in (2, 3):
        for x in (6, 7, 8, 9):
            shape.discard((x, y))
    colored = bevel(shape, salt=7)
    # sleeve seams
    for y in range(5, 9):
        colored[(3, y)] = D2
        colored[(12, y)] = D2
    # cuffs
    for x in (1, 2, 3, 12, 13, 14):
        colored[(x, 8)] = D1
    # gem in the middle of the chest
    gem = {(7, 6): L, (8, 6): L, (6, 7): M, (7, 7): H, (8, 7): W, (9, 7): M,
           (7, 8): M, (8, 8): L, (7, 9): D2, (8, 9): M}
    for p, col in gem.items():
        colored[p] = col
    # belt line
    for x in range(4, 12):
        colored[(x, 12)] = D2
    c.paint(colored)
    c.outline()
    return c


def leggings():
    c = Canvas(16, 16)
    shape = set()
    for y in range(2, 5):
        for x in range(3, 13):
            shape.add((x, y))
    for y in (5, 6):
        for x in range(3, 13):
            shape.add((x, y))
    for y in range(7, 14):
        for x in (3, 4, 5, 6, 9, 10, 11, 12):
            shape.add((x, y))
    colored = bevel(shape, salt=8)
    # belt with a buckle gem
    for x in range(3, 13):
        colored[(x, 4)] = D2
    for x in (7, 8):
        colored[(x, 2)] = L
        colored[(x, 3)] = H if x == 7 else W
    # knee plates
    for p in ((4, 9), (5, 9), (10, 9), (11, 9)):
        colored[p] = L
    # cuffs
    for x in (3, 4, 5, 6, 9, 10, 11, 12):
        colored[(x, 13)] = D1
    c.paint(colored)
    c.outline()
    return c


def boots():
    c = Canvas(16, 16)
    shape = set()
    # left boot: shaft plus a foot pointing left
    for y in range(4, 10):
        for x in (3, 4, 5, 6):
            shape.add((x, y))
    for y in (10, 11, 12):
        for x in range(1, 7):
            shape.add((x, y))
    # right boot: shaft plus a foot pointing right
    for y in range(4, 10):
        for x in (9, 10, 11, 12):
            shape.add((x, y))
    for y in (10, 11, 12):
        for x in range(9, 15):
            shape.add((x, y))
    colored = bevel(shape, salt=9)
    # cuffs
    for x in (3, 4, 5, 6, 9, 10, 11, 12):
        colored[(x, 4)] = H if x in (3, 9) else L
        colored[(x, 5)] = D2
    # soles
    for x in range(1, 7):
        colored[(x, 12)] = D1
    for x in range(9, 15):
        colored[(x, 12)] = D1
    c.paint(colored)
    c.outline()
    return c


# ---------------------------------------------------------------------------------------------
# armor layers (64x32), laid out exactly like the vanilla armor model UVs
# ---------------------------------------------------------------------------------------------
def armor_layer_1():
    """Helmet, chestplate (body and arms) and boots."""
    c = Canvas(64, 32)

    # ---- helmet: head cube at (0,0) ----
    for name, (x, y, w, h) in {
        "right": (0, 8, 8, 8),
        "left": (16, 8, 8, 8),
        "back": (24, 8, 8, 8),
        "top": (8, 0, 8, 8),
    }.items():
        c.paint(bevel(rect(x, y, w, h), salt=10 + len(name)))

    # front face: brow band, cheek pillars and a short nose guard, open in between
    front = rect(8, 8, 8, 3)
    front |= rect(8, 11, 2, 5) | rect(14, 11, 2, 5)
    front |= rect(11, 11, 2, 3)
    front_colored = bevel(front, salt=20)
    # little gem on the brow
    front_colored[(11, 9)] = H
    front_colored[(12, 9)] = W
    c.paint(front_colored)

    # ---- chestplate: body cube at (16,16) ----
    body_faces = {
        "right": (16, 20, 4, 12),
        "front": (20, 20, 8, 12),
        "left": (28, 20, 4, 12),
        "back": (32, 20, 8, 12),
        "top": (20, 16, 8, 4),
    }
    for name, (x, y, w, h) in body_faces.items():
        c.paint(bevel(rect(x, y, w, h), salt=30 + len(name)))

    # gem on the chest front (front face spans x 20..27, y 20..31)
    gem = {(23, 22): L, (24, 22): L,
           (22, 23): M, (23, 23): H, (24, 23): W, (25, 23): M,
           (22, 24): M, (23, 24): L, (24, 24): H, (25, 24): M,
           (23, 25): D2, (24, 25): M}
    for p, col in gem.items():
        c.put(p[0], p[1], col)
    # belt line at the bottom of the torso
    for x in range(20, 28):
        c.put(x, 29, D2)
        c.put(x, 30, D1)
    for x in range(32, 40):
        c.put(x, 29, D2)
        c.put(x, 30, D1)

    # ---- arms: arm cube at (40,16), shoulder pad on top, cuff near the elbow ----
    arm_faces = {
        "outer": (40, 20, 4, 8),
        "front": (44, 20, 4, 8),
        "inner": (48, 20, 4, 8),
        "back": (52, 20, 4, 8),
    }
    for name, (x, y, w, h) in arm_faces.items():
        c.paint(bevel(rect(x, y, w, h), salt=40 + len(name)))
    c.paint(bevel(rect(44, 16, 4, 4), salt=50, speckle=False))
    for x0 in (40, 44, 48, 52):
        for x in range(x0, x0 + 4):
            c.put(x, 27, D1)
            c.put(x, 26, D2)
    # shoulder pad highlight on the outer face
    for x in range(40, 44):
        c.put(x, 20, H)
        c.put(x, 21, L)

    # ---- boots: leg cube at (0,16), only the lower 5 rows are painted ----
    leg_faces = {
        "outer": (0, 27, 4, 5),
        "front": (4, 27, 4, 5),
        "inner": (8, 27, 4, 5),
        "back": (12, 27, 4, 5),
    }
    for name, (x, y, w, h) in leg_faces.items():
        c.paint(bevel(rect(x, y, w, h), salt=60 + len(name)))
        for x2 in range(x, x + w):
            c.put(x2, 27, H if x2 == x else L)
            c.put(x2, 28, D2)
    c.paint(bevel(rect(8, 16, 4, 4), salt=70, speckle=False))
    for x in range(8, 12):
        for y in range(16, 20):
            c.put(x, y, D1 if (x + y) % 2 else D2)
    return c


def armor_layer_2():
    """Leggings: a belt around the hips plus the legs."""
    c = Canvas(64, 32)

    # ---- belt: bottom four rows of the body cube ----
    belt_faces = {
        "right": (16, 28, 4, 4),
        "front": (20, 28, 8, 4),
        "left": (28, 28, 4, 4),
        "back": (32, 28, 8, 4),
    }
    for name, (x, y, w, h) in belt_faces.items():
        c.paint(bevel(rect(x, y, w, h), salt=80 + len(name)))
    # buckle gem on the front of the belt
    c.put(23, 29, H)
    c.put(24, 29, W)
    c.put(23, 30, L)
    c.put(24, 30, M)

    # ---- legs: upper nine rows of the leg cube ----
    leg_faces = {
        "outer": (0, 20, 4, 9),
        "front": (4, 20, 4, 9),
        "inner": (8, 20, 4, 9),
        "back": (12, 20, 4, 9),
    }
    for name, (x, y, w, h) in leg_faces.items():
        c.paint(bevel(rect(x, y, w, h), salt=90 + len(name)))
        # knee plate
        for x2 in range(x, x + w):
            c.put(x2, 25, L if x2 > x else H)
            c.put(x2, 26, D2)
        # cuff line
        for x2 in range(x, x + w):
            c.put(x2, 28, D1)
    # hip top so the legs do not look hollow from above
    c.paint(bevel(rect(4, 16, 4, 4), salt=100, speckle=False))
    return c


# ---------------------------------------------------------------------------------------------
# mod icon (128x128): a big faceted emerald with sparkles
# ---------------------------------------------------------------------------------------------
def mod_icon():
    size = 128
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    # rounded dark background with a soft vertical gradient
    for y in range(size):
        for x in range(size):
            dx = max(abs(x - 63.5) - 50, 0)
            dy = max(abs(y - 63.5) - 50, 0)
            if math.hypot(dx, dy) <= 14:
                t = y / float(size - 1)
                r = int(10 + 14 * t)
                g = int(48 + 40 * (1 - t))
                b = int(34 + 20 * t)
                img.putpixel((x, y), (r, g, b, 255))

    from PIL import ImageDraw
    draw = ImageDraw.Draw(img)

    def poly(points, color):
        draw.polygon(points, fill=color)

    # emerald cut octagon, facets from light (top-left) to dark (bottom-right)
    cx, cy = 64, 66
    o = [(cx - 26, cy - 40), (cx + 26, cy - 40), (cx + 44, cy - 20), (cx + 44, cy + 20),
         (cx + 26, cy + 40), (cx - 26, cy + 40), (cx - 44, cy + 20), (cx - 44, cy - 20)]
    poly(o, D1)
    i = [(cx - 16, cy - 26), (cx + 16, cy - 26), (cx + 28, cy - 12), (cx + 28, cy + 12),
         (cx + 16, cy + 26), (cx - 16, cy + 26), (cx - 28, cy + 12), (cx - 28, cy - 12)]
    # outer facets
    poly([o[0], o[1], i[1], i[0]], L)
    poly([o[1], o[2], i[2], i[1]], M)
    poly([o[2], o[3], i[3], i[2]], D2)
    poly([o[3], o[4], i[4], i[3]], D2)
    poly([o[4], o[5], i[5], i[4]], D1)
    poly([o[5], o[6], i[6], i[5]], D2)
    poly([o[6], o[7], i[7], i[6]], M)
    poly([o[7], o[0], i[0], i[7]], L)
    # table (the flat top of the gem): lighter at the top, darker at the bottom
    poly(i, M)
    poly([i[0], i[1], i[2], (cx + 28, cy - 12), (cx - 28, cy - 12), i[7]], L)
    poly([i[6], i[3], i[4], i[5]], D2)
    # two glare stripes
    poly([(cx - 12, cy - 24), (cx - 5, cy - 24), (cx - 25, cy - 4), (cx - 27, cy - 11)], H)
    poly([(cx - 1, cy - 24), (cx + 2, cy - 24), (cx - 20, cy), (cx - 22, cy - 3)], H)

    # glints
    for gx, gy, r in ((cx - 30, cy - 30, 5), (cx + 34, cy + 30, 3), (cx - 6, cy - 14, 3)):
        draw.polygon([(gx, gy - r * 2), (gx + r // 2 + 1, gy - 1), (gx + r * 2, gy), (gx + r // 2 + 1, gy + 1),
                      (gx, gy + r * 2), (gx - r // 2 - 1, gy + 1), (gx - r * 2, gy), (gx - r // 2 - 1, gy - 1)],
                     fill=W)
    return img


def save(canvas_or_image, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img = canvas_or_image.image() if isinstance(canvas_or_image, Canvas) else canvas_or_image
    img.save(path)


def main():
    items = {
        "emerald_sword": sword,
        "emerald_pickaxe": pickaxe,
        "emerald_axe": axe,
        "emerald_shovel": shovel,
        "emerald_hoe": hoe,
        "emerald_helmet": helmet,
        "emerald_chestplate": chestplate,
        "emerald_leggings": leggings,
        "emerald_boots": boots,
    }
    for name, fn in items.items():
        save(fn(), os.path.join(ITEM_DIR, name + ".png"))

    save(armor_layer_1(), os.path.join(ARMOR_DIR, "emeraldgear_emerald_layer_1.png"))
    save(armor_layer_2(), os.path.join(ARMOR_DIR, "emeraldgear_emerald_layer_2.png"))
    save(mod_icon(), ICON_PATH)
    print("textures generated")


if __name__ == "__main__":
    main()
