package net.minecraft.world.level.material;

import com.google.common.base.Preconditions;

public class MaterialColor {
   private static final MaterialColor[] MATERIAL_COLORS = new MaterialColor[64];
   public static final MaterialColor NONE = new MaterialColor(0, 0);
   public static final MaterialColor GRASS = new MaterialColor(1, 8368696);
   public static final MaterialColor SAND = new MaterialColor(2, 16247203);
   public static final MaterialColor WOOL = new MaterialColor(3, 13092807);
   public static final MaterialColor FIRE = new MaterialColor(4, 16711680);
   public static final MaterialColor ICE = new MaterialColor(5, 10526975);
   public static final MaterialColor METAL = new MaterialColor(6, 10987431);
   public static final MaterialColor PLANT = new MaterialColor(7, 31744);
   public static final MaterialColor SNOW = new MaterialColor(8, 16777215);
   public static final MaterialColor CLAY = new MaterialColor(9, 10791096);
   public static final MaterialColor DIRT = new MaterialColor(10, 9923917);
   public static final MaterialColor STONE = new MaterialColor(11, 7368816);
   public static final MaterialColor WATER = new MaterialColor(12, 4210943);
   public static final MaterialColor WOOD = new MaterialColor(13, 9402184);
   public static final MaterialColor QUARTZ = new MaterialColor(14, 16776437);
   public static final MaterialColor COLOR_ORANGE = new MaterialColor(15, 14188339);
   public static final MaterialColor COLOR_MAGENTA = new MaterialColor(16, 11685080);
   public static final MaterialColor COLOR_LIGHT_BLUE = new MaterialColor(17, 6724056);
   public static final MaterialColor COLOR_YELLOW = new MaterialColor(18, 15066419);
   public static final MaterialColor COLOR_LIGHT_GREEN = new MaterialColor(19, 8375321);
   public static final MaterialColor COLOR_PINK = new MaterialColor(20, 15892389);
   public static final MaterialColor COLOR_GRAY = new MaterialColor(21, 5000268);
   public static final MaterialColor COLOR_LIGHT_GRAY = new MaterialColor(22, 10066329);
   public static final MaterialColor COLOR_CYAN = new MaterialColor(23, 5013401);
   public static final MaterialColor COLOR_PURPLE = new MaterialColor(24, 8339378);
   public static final MaterialColor COLOR_BLUE = new MaterialColor(25, 3361970);
   public static final MaterialColor COLOR_BROWN = new MaterialColor(26, 6704179);
   public static final MaterialColor COLOR_GREEN = new MaterialColor(27, 6717235);
   public static final MaterialColor COLOR_RED = new MaterialColor(28, 10040115);
   public static final MaterialColor COLOR_BLACK = new MaterialColor(29, 1644825);
   public static final MaterialColor GOLD = new MaterialColor(30, 16445005);
   public static final MaterialColor DIAMOND = new MaterialColor(31, 6085589);
   public static final MaterialColor LAPIS = new MaterialColor(32, 4882687);
   public static final MaterialColor EMERALD = new MaterialColor(33, 55610);
   public static final MaterialColor PODZOL = new MaterialColor(34, 8476209);
   public static final MaterialColor NETHER = new MaterialColor(35, 7340544);
   public static final MaterialColor TERRACOTTA_WHITE = new MaterialColor(36, 13742497);
   public static final MaterialColor TERRACOTTA_ORANGE = new MaterialColor(37, 10441252);
   public static final MaterialColor TERRACOTTA_MAGENTA = new MaterialColor(38, 9787244);
   public static final MaterialColor TERRACOTTA_LIGHT_BLUE = new MaterialColor(39, 7367818);
   public static final MaterialColor TERRACOTTA_YELLOW = new MaterialColor(40, 12223780);
   public static final MaterialColor TERRACOTTA_LIGHT_GREEN = new MaterialColor(41, 6780213);
   public static final MaterialColor TERRACOTTA_PINK = new MaterialColor(42, 10505550);
   public static final MaterialColor TERRACOTTA_GRAY = new MaterialColor(43, 3746083);
   public static final MaterialColor TERRACOTTA_LIGHT_GRAY = new MaterialColor(44, 8874850);
   public static final MaterialColor TERRACOTTA_CYAN = new MaterialColor(45, 5725276);
   public static final MaterialColor TERRACOTTA_PURPLE = new MaterialColor(46, 8014168);
   public static final MaterialColor TERRACOTTA_BLUE = new MaterialColor(47, 4996700);
   public static final MaterialColor TERRACOTTA_BROWN = new MaterialColor(48, 4993571);
   public static final MaterialColor TERRACOTTA_GREEN = new MaterialColor(49, 5001770);
   public static final MaterialColor TERRACOTTA_RED = new MaterialColor(50, 9321518);
   public static final MaterialColor TERRACOTTA_BLACK = new MaterialColor(51, 2430480);
   public static final MaterialColor CRIMSON_NYLIUM = new MaterialColor(52, 12398641);
   public static final MaterialColor CRIMSON_STEM = new MaterialColor(53, 9715553);
   public static final MaterialColor CRIMSON_HYPHAE = new MaterialColor(54, 6035741);
   public static final MaterialColor WARPED_NYLIUM = new MaterialColor(55, 1474182);
   public static final MaterialColor WARPED_STEM = new MaterialColor(56, 3837580);
   public static final MaterialColor WARPED_HYPHAE = new MaterialColor(57, 5647422);
   public static final MaterialColor WARPED_WART_BLOCK = new MaterialColor(58, 1356933);
   public static final MaterialColor DEEPSLATE = new MaterialColor(59, 6579300);
   public static final MaterialColor RAW_IRON = new MaterialColor(60, 14200723);
   public static final MaterialColor GLOW_LICHEN = new MaterialColor(61, 8365974);
   public final int col;
   public final int id;

   private MaterialColor(int p_76425_, int p_76426_) {
      if (p_76425_ >= 0 && p_76425_ <= 63) {
         this.id = p_76425_;
         this.col = p_76426_;
         MATERIAL_COLORS[p_76425_] = this;
      } else {
         throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
      }
   }

   public int calculateRGBColor(MaterialColor.Brightness p_192922_) {
      if (this == NONE) {
         return 0;
      } else {
         int i = p_192922_.modifier;
         int j = (this.col >> 16 & 255) * i / 255;
         int k = (this.col >> 8 & 255) * i / 255;
         int l = (this.col & 255) * i / 255;
         return -16777216 | l << 16 | k << 8 | j;
      }
   }

   public static MaterialColor byId(int p_192920_) {
      Preconditions.checkPositionIndex(p_192920_, MATERIAL_COLORS.length, "material id");
      return byIdUnsafe(p_192920_);
   }

   private static MaterialColor byIdUnsafe(int p_192928_) {
      MaterialColor materialcolor = MATERIAL_COLORS[p_192928_];
      return materialcolor != null ? materialcolor : NONE;
   }

   public static int getColorFromPackedId(int p_192924_) {
      int i = p_192924_ & 255;
      return byIdUnsafe(i >> 2).calculateRGBColor(MaterialColor.Brightness.byIdUnsafe(i & 3));
   }

   public byte getPackedId(MaterialColor.Brightness p_192926_) {
      return (byte)(this.id << 2 | p_192926_.id & 3);
   }

   public static enum Brightness {
      LOW(0, 180),
      NORMAL(1, 220),
      HIGH(2, 255),
      LOWEST(3, 135);

      private static final MaterialColor.Brightness[] VALUES = new MaterialColor.Brightness[]{LOW, NORMAL, HIGH, LOWEST};
      public final int id;
      public final int modifier;

      private Brightness(int p_192941_, int p_192942_) {
         this.id = p_192941_;
         this.modifier = p_192942_;
      }

      public static MaterialColor.Brightness byId(int p_192945_) {
         Preconditions.checkPositionIndex(p_192945_, VALUES.length, "brightness id");
         return byIdUnsafe(p_192945_);
      }

      static MaterialColor.Brightness byIdUnsafe(int p_192947_) {
         return VALUES[p_192947_];
      }
   }
}