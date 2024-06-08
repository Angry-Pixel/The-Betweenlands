package net.minecraft.data.models.model;

import javax.annotation.Nullable;

public final class TextureSlot {
   public static final TextureSlot ALL = create("all");
   public static final TextureSlot TEXTURE = create("texture", ALL);
   public static final TextureSlot PARTICLE = create("particle", TEXTURE);
   public static final TextureSlot END = create("end", ALL);
   public static final TextureSlot BOTTOM = create("bottom", END);
   public static final TextureSlot TOP = create("top", END);
   public static final TextureSlot FRONT = create("front", ALL);
   public static final TextureSlot BACK = create("back", ALL);
   public static final TextureSlot SIDE = create("side", ALL);
   public static final TextureSlot NORTH = create("north", SIDE);
   public static final TextureSlot SOUTH = create("south", SIDE);
   public static final TextureSlot EAST = create("east", SIDE);
   public static final TextureSlot WEST = create("west", SIDE);
   public static final TextureSlot UP = create("up");
   public static final TextureSlot DOWN = create("down");
   public static final TextureSlot CROSS = create("cross");
   public static final TextureSlot PLANT = create("plant");
   public static final TextureSlot WALL = create("wall", ALL);
   public static final TextureSlot RAIL = create("rail");
   public static final TextureSlot WOOL = create("wool");
   public static final TextureSlot PATTERN = create("pattern");
   public static final TextureSlot PANE = create("pane");
   public static final TextureSlot EDGE = create("edge");
   public static final TextureSlot FAN = create("fan");
   public static final TextureSlot STEM = create("stem");
   public static final TextureSlot UPPER_STEM = create("upperstem");
   public static final TextureSlot CROP = create("crop");
   public static final TextureSlot DIRT = create("dirt");
   public static final TextureSlot FIRE = create("fire");
   public static final TextureSlot LANTERN = create("lantern");
   public static final TextureSlot PLATFORM = create("platform");
   public static final TextureSlot UNSTICKY = create("unsticky");
   public static final TextureSlot TORCH = create("torch");
   public static final TextureSlot LAYER0 = create("layer0");
   public static final TextureSlot LIT_LOG = create("lit_log");
   public static final TextureSlot CANDLE = create("candle");
   public static final TextureSlot INSIDE = create("inside");
   public static final TextureSlot CONTENT = create("content");
   private final String id;
   @Nullable
   private final TextureSlot parent;

   private static TextureSlot create(String p_125899_) {
      return new TextureSlot(p_125899_, (TextureSlot)null);
   }

   private static TextureSlot create(String p_125901_, TextureSlot p_125902_) {
      return new TextureSlot(p_125901_, p_125902_);
   }

   private TextureSlot(String p_125895_, @Nullable TextureSlot p_125896_) {
      this.id = p_125895_;
      this.parent = p_125896_;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public TextureSlot getParent() {
      return this.parent;
   }

   public String toString() {
      return "#" + this.id;
   }
}