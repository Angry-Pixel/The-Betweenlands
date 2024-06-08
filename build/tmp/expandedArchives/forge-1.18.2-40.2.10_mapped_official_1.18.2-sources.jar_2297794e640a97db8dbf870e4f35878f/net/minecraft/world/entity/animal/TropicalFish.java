package net.minecraft.world.entity.animal;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class TropicalFish extends AbstractSchoolingFish {
   public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
   public static final int BASE_SMALL = 0;
   public static final int BASE_LARGE = 1;
   private static final int BASES = 2;
   private static final ResourceLocation[] BASE_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
   private static final ResourceLocation[] PATTERN_A_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
   private static final ResourceLocation[] PATTERN_B_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
   private static final int PATTERNS = 6;
   private static final int COLORS = 15;
   public static final int[] COMMON_VARIANTS = new int[]{calculateVariant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), calculateVariant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
   private boolean isSchool = true;

   private static int calculateVariant(TropicalFish.Pattern p_30019_, DyeColor p_30020_, DyeColor p_30021_) {
      return p_30019_.getBase() & 255 | (p_30019_.getIndex() & 255) << 8 | (p_30020_.getId() & 255) << 16 | (p_30021_.getId() & 255) << 24;
   }

   public TropicalFish(EntityType<? extends TropicalFish> p_30015_, Level p_30016_) {
      super(p_30015_, p_30016_);
   }

   public static String getPredefinedName(int p_30031_) {
      return "entity.minecraft.tropical_fish.predefined." + p_30031_;
   }

   public static DyeColor getBaseColor(int p_30051_) {
      return DyeColor.byId(getBaseColorIdx(p_30051_));
   }

   public static DyeColor getPatternColor(int p_30053_) {
      return DyeColor.byId(getPatternColorIdx(p_30053_));
   }

   public static String getFishTypeName(int p_30055_) {
      int i = getBaseVariant(p_30055_);
      int j = getPatternVariant(p_30055_);
      return "entity.minecraft.tropical_fish.type." + TropicalFish.Pattern.getPatternName(i, j);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_30033_) {
      super.addAdditionalSaveData(p_30033_);
      p_30033_.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag p_30029_) {
      super.readAdditionalSaveData(p_30029_);
      this.setVariant(p_30029_.getInt("Variant"));
   }

   public void setVariant(int p_30057_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30057_);
   }

   public boolean isMaxGroupSizeReached(int p_30035_) {
      return !this.isSchool;
   }

   public int getVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void saveToBucketTag(ItemStack p_30049_) {
      super.saveToBucketTag(p_30049_);
      CompoundTag compoundtag = p_30049_.getOrCreateTag();
      compoundtag.putInt("BucketVariantTag", this.getVariant());
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_30039_) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   private static int getBaseColorIdx(int p_30061_) {
      return (p_30061_ & 16711680) >> 16;
   }

   public float[] getBaseColor() {
      return DyeColor.byId(getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   private static int getPatternColorIdx(int p_30063_) {
      return (p_30063_ & -16777216) >> 24;
   }

   public float[] getPatternColor() {
      return DyeColor.byId(getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   public static int getBaseVariant(int p_30059_) {
      return Math.min(p_30059_ & 255, 1);
   }

   public int getBaseVariant() {
      return getBaseVariant(this.getVariant());
   }

   private static int getPatternVariant(int p_30065_) {
      return Math.min((p_30065_ & '\uff00') >> 8, 5);
   }

   public ResourceLocation getPatternTextureLocation() {
      return getBaseVariant(this.getVariant()) == 0 ? PATTERN_A_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())] : PATTERN_B_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
   }

   public ResourceLocation getBaseTextureLocation() {
      return BASE_TEXTURE_LOCATIONS[getBaseVariant(this.getVariant())];
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30023_, DifficultyInstance p_30024_, MobSpawnType p_30025_, @Nullable SpawnGroupData p_30026_, @Nullable CompoundTag p_30027_) {
      p_30026_ = super.finalizeSpawn(p_30023_, p_30024_, p_30025_, p_30026_, p_30027_);
      if (p_30025_ == MobSpawnType.BUCKET && p_30027_ != null && p_30027_.contains("BucketVariantTag", 3)) {
         this.setVariant(p_30027_.getInt("BucketVariantTag"));
         return p_30026_;
      } else {
         int i;
         int j;
         int k;
         int l;
         if (p_30026_ instanceof TropicalFish.TropicalFishGroupData) {
            TropicalFish.TropicalFishGroupData tropicalfish$tropicalfishgroupdata = (TropicalFish.TropicalFishGroupData)p_30026_;
            i = tropicalfish$tropicalfishgroupdata.base;
            j = tropicalfish$tropicalfishgroupdata.pattern;
            k = tropicalfish$tropicalfishgroupdata.baseColor;
            l = tropicalfish$tropicalfishgroupdata.patternColor;
         } else if ((double)this.random.nextFloat() < 0.9D) {
            int i1 = Util.getRandom(COMMON_VARIANTS, this.random);
            i = i1 & 255;
            j = (i1 & '\uff00') >> 8;
            k = (i1 & 16711680) >> 16;
            l = (i1 & -16777216) >> 24;
            p_30026_ = new TropicalFish.TropicalFishGroupData(this, i, j, k, l);
         } else {
            this.isSchool = false;
            i = this.random.nextInt(2);
            j = this.random.nextInt(6);
            k = this.random.nextInt(15);
            l = this.random.nextInt(15);
         }

         this.setVariant(i | j << 8 | k << 16 | l << 24);
         return p_30026_;
      }
   }

   public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> p_186232_, LevelAccessor p_186233_, MobSpawnType p_186234_, BlockPos p_186235_, Random p_186236_) {
      return p_186233_.getFluidState(p_186235_.below()).is(FluidTags.WATER) && p_186233_.getBlockState(p_186235_.above()).is(Blocks.WATER) && (p_186233_.getBiome(p_186235_).is(Biomes.LUSH_CAVES) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(p_186232_, p_186233_, p_186234_, p_186235_, p_186236_));
   }

   static enum Pattern {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int base;
      private final int index;
      private static final TropicalFish.Pattern[] VALUES = values();

      private Pattern(int p_30086_, int p_30087_) {
         this.base = p_30086_;
         this.index = p_30087_;
      }

      public int getBase() {
         return this.base;
      }

      public int getIndex() {
         return this.index;
      }

      public static String getPatternName(int p_30090_, int p_30091_) {
         return VALUES[p_30091_ + 6 * p_30090_].getName();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      final int base;
      final int pattern;
      final int baseColor;
      final int patternColor;

      TropicalFishGroupData(TropicalFish p_30102_, int p_30103_, int p_30104_, int p_30105_, int p_30106_) {
         super(p_30102_);
         this.base = p_30103_;
         this.pattern = p_30104_;
         this.baseColor = p_30105_;
         this.patternColor = p_30106_;
      }
   }
}