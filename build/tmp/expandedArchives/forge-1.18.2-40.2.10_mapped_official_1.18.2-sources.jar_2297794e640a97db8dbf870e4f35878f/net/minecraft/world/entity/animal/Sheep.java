package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Sheep extends Animal implements Shearable, net.minecraftforge.common.IForgeShearable {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final EntityDataAccessor<Byte> DATA_WOOL_ID = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BYTE);
   private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) -> {
      p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
      p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
      p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
      p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR = Maps.<DyeColor, float[]>newEnumMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap((p_29868_) -> {
      return p_29868_;
   }, Sheep::createSheepColor)));
   private int eatAnimationTick;
   private EatBlockGoal eatBlockGoal;

   private static float[] createSheepColor(DyeColor p_29866_) {
      if (p_29866_ == DyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] afloat = p_29866_.getTextureDiffuseColors();
         float f = 0.75F;
         return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
      }
   }

   public static float[] getColorArray(DyeColor p_29830_) {
      return COLORARRAY_BY_COLOR.get(p_29830_);
   }

   public Sheep(EntityType<? extends Sheep> p_29806_, Level p_29807_) {
      super(p_29806_, p_29807_);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatBlockGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   protected void customServerAiStep() {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep();
   }

   public void aiStep() {
      if (this.level.isClientSide) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_WOOL_ID, (byte)0);
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.isSheared()) {
         return this.getType().getDefaultLootTable();
      } else {
         switch(this.getColor()) {
         case WHITE:
         default:
            return BuiltInLootTables.SHEEP_WHITE;
         case ORANGE:
            return BuiltInLootTables.SHEEP_ORANGE;
         case MAGENTA:
            return BuiltInLootTables.SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return BuiltInLootTables.SHEEP_LIGHT_BLUE;
         case YELLOW:
            return BuiltInLootTables.SHEEP_YELLOW;
         case LIME:
            return BuiltInLootTables.SHEEP_LIME;
         case PINK:
            return BuiltInLootTables.SHEEP_PINK;
         case GRAY:
            return BuiltInLootTables.SHEEP_GRAY;
         case LIGHT_GRAY:
            return BuiltInLootTables.SHEEP_LIGHT_GRAY;
         case CYAN:
            return BuiltInLootTables.SHEEP_CYAN;
         case PURPLE:
            return BuiltInLootTables.SHEEP_PURPLE;
         case BLUE:
            return BuiltInLootTables.SHEEP_BLUE;
         case BROWN:
            return BuiltInLootTables.SHEEP_BROWN;
         case GREEN:
            return BuiltInLootTables.SHEEP_GREEN;
         case RED:
            return BuiltInLootTables.SHEEP_RED;
         case BLACK:
            return BuiltInLootTables.SHEEP_BLACK;
         }
      }
   }

   public void handleEntityEvent(byte p_29814_) {
      if (p_29814_ == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(p_29814_);
      }

   }

   public float getHeadEatPositionScale(float p_29881_) {
      if (this.eatAnimationTick <= 0) {
         return 0.0F;
      } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         return 1.0F;
      } else {
         return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - p_29881_) / 4.0F : -((float)(this.eatAnimationTick - 40) - p_29881_) / 4.0F;
      }
   }

   public float getHeadEatAngleScale(float p_29883_) {
      if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float f = ((float)(this.eatAnimationTick - 4) - p_29883_) / 32.0F;
         return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
      } else {
         return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
      }
   }

   public InteractionResult mobInteract(Player p_29853_, InteractionHand p_29854_) {
      ItemStack itemstack = p_29853_.getItemInHand(p_29854_);
      if (false && itemstack.getItem() == Items.SHEARS) { //Forge: Moved to onSheared
         if (!this.level.isClientSide && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, p_29853_);
            itemstack.hurtAndBreak(1, p_29853_, (p_29822_) -> {
               p_29822_.broadcastBreakEvent(p_29854_);
            });
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return super.mobInteract(p_29853_, p_29854_);
      }
   }

   public void shear(SoundSource p_29819_) {
      this.level.playSound((Player)null, this, SoundEvents.SHEEP_SHEAR, p_29819_, 1.0F, 1.0F);
      this.setSheared(true);
      int i = 1 + this.random.nextInt(3);

      for(int j = 0; j < i; ++j) {
         ItemEntity itementity = this.spawnAtLocation(ITEM_BY_DYE.get(this.getColor()), 1);
         if (itementity != null) {
            itementity.setDeltaMovement(itementity.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
         }
      }

   }

   public boolean readyForShearing() {
      return this.isAlive() && !this.isSheared() && !this.isBaby();
   }

   public void addAdditionalSaveData(CompoundTag p_29864_) {
      super.addAdditionalSaveData(p_29864_);
      p_29864_.putBoolean("Sheared", this.isSheared());
      p_29864_.putByte("Color", (byte)this.getColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag p_29845_) {
      super.readAdditionalSaveData(p_29845_);
      this.setSheared(p_29845_.getBoolean("Sheared"));
      this.setColor(DyeColor.byId(p_29845_.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_29872_) {
      return SoundEvents.SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos p_29861_, BlockState p_29862_) {
      this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byId(this.entityData.get(DATA_WOOL_ID) & 15);
   }

   public void setColor(DyeColor p_29856_) {
      byte b0 = this.entityData.get(DATA_WOOL_ID);
      this.entityData.set(DATA_WOOL_ID, (byte)(b0 & 240 | p_29856_.getId() & 15));
   }

   public boolean isSheared() {
      return (this.entityData.get(DATA_WOOL_ID) & 16) != 0;
   }

   public void setSheared(boolean p_29879_) {
      byte b0 = this.entityData.get(DATA_WOOL_ID);
      if (p_29879_) {
         this.entityData.set(DATA_WOOL_ID, (byte)(b0 | 16));
      } else {
         this.entityData.set(DATA_WOOL_ID, (byte)(b0 & -17));
      }

   }

   public static DyeColor getRandomSheepColor(Random p_29843_) {
      int i = p_29843_.nextInt(100);
      if (i < 5) {
         return DyeColor.BLACK;
      } else if (i < 10) {
         return DyeColor.GRAY;
      } else if (i < 15) {
         return DyeColor.LIGHT_GRAY;
      } else if (i < 18) {
         return DyeColor.BROWN;
      } else {
         return p_29843_.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
      }
   }

   public Sheep getBreedOffspring(ServerLevel p_149044_, AgeableMob p_149045_) {
      Sheep sheep = (Sheep)p_149045_;
      Sheep sheep1 = EntityType.SHEEP.create(p_149044_);
      sheep1.setColor(this.getOffspringColor(this, sheep));
      return sheep1;
   }

   public void ate() {
      this.setSheared(false);
      if (this.isBaby()) {
         this.ageUp(60);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29835_, DifficultyInstance p_29836_, MobSpawnType p_29837_, @Nullable SpawnGroupData p_29838_, @Nullable CompoundTag p_29839_) {
      this.setColor(getRandomSheepColor(p_29835_.getRandom()));
      return super.finalizeSpawn(p_29835_, p_29836_, p_29837_, p_29838_, p_29839_);
   }

   private DyeColor getOffspringColor(Animal p_29824_, Animal p_29825_) {
      DyeColor dyecolor = ((Sheep)p_29824_).getColor();
      DyeColor dyecolor1 = ((Sheep)p_29825_).getColor();
      CraftingContainer craftingcontainer = makeContainer(dyecolor, dyecolor1);
      return this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, this.level).map((p_29828_) -> {
         return p_29828_.assemble(craftingcontainer);
      }).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return this.level.random.nextBoolean() ? dyecolor : dyecolor1;
      });
   }

   private static CraftingContainer makeContainer(DyeColor p_29832_, DyeColor p_29833_) {
      CraftingContainer craftingcontainer = new CraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
         public boolean stillValid(Player p_29888_) {
            return false;
         }
      }, 2, 1);
      craftingcontainer.setItem(0, new ItemStack(DyeItem.byColor(p_29832_)));
      craftingcontainer.setItem(1, new ItemStack(DyeItem.byColor(p_29833_)));
      return craftingcontainer;
   }

   protected float getStandingEyeHeight(Pose p_29850_, EntityDimensions p_29851_) {
      return 0.95F * p_29851_.height;
   }

   @Override
   public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
      return readyForShearing();
   }

   @javax.annotation.Nonnull
   @Override
   public java.util.List<ItemStack> onSheared(@Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
      world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      this.gameEvent(GameEvent.SHEAR, player);
      if (!world.isClientSide) {
         this.setSheared(true);
         int i = 1 + this.random.nextInt(3);

         java.util.List<ItemStack> items = new java.util.ArrayList<>();
         for (int j = 0; j < i; ++j) {
            items.add(new ItemStack(ITEM_BY_DYE.get(this.getColor())));
         }
         return items;
      }
      return java.util.Collections.emptyList();
   }
}
