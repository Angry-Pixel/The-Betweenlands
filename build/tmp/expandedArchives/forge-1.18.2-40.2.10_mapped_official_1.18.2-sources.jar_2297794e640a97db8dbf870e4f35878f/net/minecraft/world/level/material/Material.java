package net.minecraft.world.level.material;

public final class Material {
   public static final Material AIR = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
   public static final Material STRUCTURAL_AIR = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
   public static final Material PORTAL = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().notPushable().build();
   public static final Material CLOTH_DECORATION = (new Material.Builder(MaterialColor.WOOL)).noCollider().notSolidBlocking().nonSolid().flammable().build();
   public static final Material PLANT = (new Material.Builder(MaterialColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material WATER_PLANT = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material REPLACEABLE_PLANT = (new Material.Builder(MaterialColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().flammable().build();
   public static final Material REPLACEABLE_FIREPROOF_PLANT = (new Material.Builder(MaterialColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material REPLACEABLE_WATER_PLANT = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material WATER = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material BUBBLE_COLUMN = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material LAVA = (new Material.Builder(MaterialColor.FIRE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material TOP_SNOW = (new Material.Builder(MaterialColor.SNOW)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material FIRE = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material DECORATION = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material WEB = (new Material.Builder(MaterialColor.WOOL)).noCollider().notSolidBlocking().destroyOnPush().build();
   public static final Material SCULK = (new Material.Builder(MaterialColor.COLOR_BLACK)).build();
   public static final Material BUILDABLE_GLASS = (new Material.Builder(MaterialColor.NONE)).build();
   public static final Material CLAY = (new Material.Builder(MaterialColor.CLAY)).build();
   public static final Material DIRT = (new Material.Builder(MaterialColor.DIRT)).build();
   public static final Material GRASS = (new Material.Builder(MaterialColor.GRASS)).build();
   public static final Material ICE_SOLID = (new Material.Builder(MaterialColor.ICE)).build();
   public static final Material SAND = (new Material.Builder(MaterialColor.SAND)).build();
   public static final Material SPONGE = (new Material.Builder(MaterialColor.COLOR_YELLOW)).build();
   public static final Material SHULKER_SHELL = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
   public static final Material WOOD = (new Material.Builder(MaterialColor.WOOD)).flammable().build();
   public static final Material NETHER_WOOD = (new Material.Builder(MaterialColor.WOOD)).build();
   public static final Material BAMBOO_SAPLING = (new Material.Builder(MaterialColor.WOOD)).flammable().destroyOnPush().noCollider().build();
   public static final Material BAMBOO = (new Material.Builder(MaterialColor.WOOD)).flammable().destroyOnPush().build();
   public static final Material WOOL = (new Material.Builder(MaterialColor.WOOL)).flammable().build();
   public static final Material EXPLOSIVE = (new Material.Builder(MaterialColor.FIRE)).flammable().notSolidBlocking().build();
   public static final Material LEAVES = (new Material.Builder(MaterialColor.PLANT)).flammable().notSolidBlocking().destroyOnPush().build();
   public static final Material GLASS = (new Material.Builder(MaterialColor.NONE)).notSolidBlocking().build();
   public static final Material ICE = (new Material.Builder(MaterialColor.ICE)).notSolidBlocking().build();
   public static final Material CACTUS = (new Material.Builder(MaterialColor.PLANT)).notSolidBlocking().destroyOnPush().build();
   public static final Material STONE = (new Material.Builder(MaterialColor.STONE)).build();
   public static final Material METAL = (new Material.Builder(MaterialColor.METAL)).build();
   public static final Material SNOW = (new Material.Builder(MaterialColor.SNOW)).build();
   public static final Material HEAVY_METAL = (new Material.Builder(MaterialColor.METAL)).notPushable().build();
   public static final Material BARRIER = (new Material.Builder(MaterialColor.NONE)).notPushable().build();
   public static final Material PISTON = (new Material.Builder(MaterialColor.STONE)).notPushable().build();
   public static final Material MOSS = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material VEGETABLE = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material EGG = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material CAKE = (new Material.Builder(MaterialColor.NONE)).destroyOnPush().build();
   public static final Material AMETHYST = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
   public static final Material POWDER_SNOW = (new Material.Builder(MaterialColor.SNOW)).nonSolid().noCollider().build();
   private final MaterialColor color;
   private final PushReaction pushReaction;
   private final boolean blocksMotion;
   private final boolean flammable;
   private final boolean liquid;
   private final boolean solidBlocking;
   private final boolean replaceable;
   private final boolean solid;

   public Material(MaterialColor p_76324_, boolean p_76325_, boolean p_76326_, boolean p_76327_, boolean p_76328_, boolean p_76329_, boolean p_76330_, PushReaction p_76331_) {
      this.color = p_76324_;
      this.liquid = p_76325_;
      this.solid = p_76326_;
      this.blocksMotion = p_76327_;
      this.solidBlocking = p_76328_;
      this.flammable = p_76329_;
      this.replaceable = p_76330_;
      this.pushReaction = p_76331_;
   }

   public boolean isLiquid() {
      return this.liquid;
   }

   public boolean isSolid() {
      return this.solid;
   }

   public boolean blocksMotion() {
      return this.blocksMotion;
   }

   public boolean isFlammable() {
      return this.flammable;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isSolidBlocking() {
      return this.solidBlocking;
   }

   public PushReaction getPushReaction() {
      return this.pushReaction;
   }

   public MaterialColor getColor() {
      return this.color;
   }

   public static class Builder {
      private PushReaction pushReaction = PushReaction.NORMAL;
      private boolean blocksMotion = true;
      private boolean flammable;
      private boolean liquid;
      private boolean replaceable;
      private boolean solid = true;
      private final MaterialColor color;
      private boolean solidBlocking = true;

      public Builder(MaterialColor p_76349_) {
         this.color = p_76349_;
      }

      public Material.Builder liquid() {
         this.liquid = true;
         return this;
      }

      public Material.Builder nonSolid() {
         this.solid = false;
         return this;
      }

      public Material.Builder noCollider() {
         this.blocksMotion = false;
         return this;
      }

      Material.Builder notSolidBlocking() {
         this.solidBlocking = false;
         return this;
      }

      protected Material.Builder flammable() {
         this.flammable = true;
         return this;
      }

      public Material.Builder replaceable() {
         this.replaceable = true;
         return this;
      }

      protected Material.Builder destroyOnPush() {
         this.pushReaction = PushReaction.DESTROY;
         return this;
      }

      protected Material.Builder notPushable() {
         this.pushReaction = PushReaction.BLOCK;
         return this;
      }

      public Material build() {
         return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.flammable, this.replaceable, this.pushReaction);
      }
   }
}