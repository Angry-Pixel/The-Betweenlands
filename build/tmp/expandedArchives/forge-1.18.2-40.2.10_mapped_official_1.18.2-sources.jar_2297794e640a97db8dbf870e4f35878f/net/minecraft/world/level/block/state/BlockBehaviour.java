package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

//TODO, Delegates are weird here now, because Block extends this.
public abstract class BlockBehaviour extends net.minecraftforge.registries.ForgeRegistryEntry<Block> {
   protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   protected final Material material;
   protected final boolean hasCollision;
   protected final float explosionResistance;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final float friction;
   protected final float speedFactor;
   protected final float jumpFactor;
   protected final boolean dynamicShape;
   protected final BlockBehaviour.Properties properties;
   @Nullable
   protected ResourceLocation drops;

   public BlockBehaviour(BlockBehaviour.Properties p_60452_) {
      this.material = p_60452_.material;
      this.hasCollision = p_60452_.hasCollision;
      this.drops = p_60452_.drops;
      this.explosionResistance = p_60452_.explosionResistance;
      this.isRandomlyTicking = p_60452_.isRandomlyTicking;
      this.soundType = p_60452_.soundType;
      this.friction = p_60452_.friction;
      this.speedFactor = p_60452_.speedFactor;
      this.jumpFactor = p_60452_.jumpFactor;
      this.dynamicShape = p_60452_.dynamicShape;
      this.properties = p_60452_;
      final ResourceLocation lootTableCache = p_60452_.drops;
      this.lootTableSupplier = lootTableCache != null ? () -> lootTableCache : p_60452_.lootTableSupplier != null ? p_60452_.lootTableSupplier : () -> new ResourceLocation(this.getRegistryName().getNamespace(), "blocks/" + this.getRegistryName().getPath());
   }

   /** @deprecated */
   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState p_60520_, LevelAccessor p_60521_, BlockPos p_60522_, int p_60523_, int p_60524_) {
   }

   /** @deprecated */
   @Deprecated
   public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
      switch(p_60478_) {
      case LAND:
         return !p_60475_.isCollisionShapeFullBlock(p_60476_, p_60477_);
      case WATER:
         return p_60476_.getFluidState(p_60477_).is(FluidTags.WATER);
      case AIR:
         return !p_60475_.isCollisionShapeFullBlock(p_60476_, p_60477_);
      default:
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
      return p_60541_;
   }

   /** @deprecated */
   @Deprecated
   public boolean skipRendering(BlockState p_60532_, BlockState p_60533_, Direction p_60534_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(BlockState p_60509_, Level p_60510_, BlockPos p_60511_, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
      DebugPackets.sendNeighborsUpdatePacket(p_60510_, p_60511_);
   }

   /** @deprecated */
   @Deprecated
   public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
   }

   /** @deprecated */
   @Deprecated
   public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
      if (p_60515_.hasBlockEntity() && (!p_60515_.is(p_60518_.getBlock()) || !p_60518_.hasBlockEntity())) {
         p_60516_.removeBlockEntity(p_60517_);
      }

   }

   /** @deprecated */
   @Deprecated
   public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
      return InteractionResult.PASS;
   }

   /** @deprecated */
   @Deprecated
   public boolean triggerEvent(BlockState p_60490_, Level p_60491_, BlockPos p_60492_, int p_60493_, int p_60494_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public RenderShape getRenderShape(BlockState p_60550_) {
      return RenderShape.MODEL;
   }

   /** @deprecated */
   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState p_60576_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isSignalSource(BlockState p_60571_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public PushReaction getPistonPushReaction(BlockState p_60584_) {
      return this.material.getPushReaction();
   }

   /** @deprecated */
   @Deprecated
   public FluidState getFluidState(BlockState p_60577_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState p_60457_) {
      return false;
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.NONE;
   }

   public float getMaxHorizontalOffset() {
      return 0.25F;
   }

   public float getMaxVerticalOffset() {
      return 0.2F;
   }

   /** @deprecated */
   @Deprecated
   public BlockState rotate(BlockState p_60530_, Rotation p_60531_) {
      return p_60530_;
   }

   /** @deprecated */
   @Deprecated
   public BlockState mirror(BlockState p_60528_, Mirror p_60529_) {
      return p_60528_;
   }

   /** @deprecated */
   @Deprecated
   public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
      return p_60470_.getMaterial().isReplaceable() && (p_60471_.getItemInHand().isEmpty() || p_60471_.getItemInHand().getItem() != this.asItem());
   }

   /** @deprecated */
   @Deprecated
   public boolean canBeReplaced(BlockState p_60535_, Fluid p_60536_) {
      return this.material.isReplaceable() || !this.material.isSolid();
   }

   /** @deprecated */
   @Deprecated
   public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
      ResourceLocation resourcelocation = this.getLootTable();
      if (resourcelocation == BuiltInLootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext lootcontext = p_60538_.withParameter(LootContextParams.BLOCK_STATE, p_60537_).create(LootContextParamSets.BLOCK);
         ServerLevel serverlevel = lootcontext.getLevel();
         LootTable loottable = serverlevel.getServer().getLootTables().get(resourcelocation);
         return loottable.getRandomItems(lootcontext);
      }
   }

   /** @deprecated */
   @Deprecated
   public long getSeed(BlockState p_60539_, BlockPos p_60540_) {
      return Mth.getSeed(p_60540_);
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
      return p_60578_.getShape(p_60579_, p_60580_);
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
      return this.getCollisionShape(p_60581_, p_60582_, p_60583_, CollisionContext.empty());
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
      return Shapes.empty();
   }

   /** @deprecated */
   @Deprecated
   public int getLightBlock(BlockState p_60585_, BlockGetter p_60586_, BlockPos p_60587_) {
      if (p_60585_.isSolidRender(p_60586_, p_60587_)) {
         return p_60586_.getMaxLightLevel();
      } else {
         return p_60585_.propagatesSkylightDown(p_60586_, p_60587_) ? 0 : 1;
      }
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public MenuProvider getMenuProvider(BlockState p_60563_, Level p_60564_, BlockPos p_60565_) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public boolean canSurvive(BlockState p_60525_, LevelReader p_60526_, BlockPos p_60527_) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_) {
      return p_60472_.isCollisionShapeFullBlock(p_60473_, p_60474_) ? 0.2F : 1.0F;
   }

   /** @deprecated */
   @Deprecated
   public int getAnalogOutputSignal(BlockState p_60487_, Level p_60488_, BlockPos p_60489_) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
      return Shapes.block();
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
      return this.hasCollision ? p_60572_.getShape(p_60573_, p_60574_) : Shapes.empty();
   }

   /** @deprecated */
   @Deprecated
   public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_) {
      return Block.isShapeFullBlock(p_181242_.getCollisionShape(p_181243_, p_181244_));
   }

   /** @deprecated */
   @Deprecated
   public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
      return this.getCollisionShape(p_60479_, p_60480_, p_60481_, p_60482_);
   }

   /** @deprecated */
   @Deprecated
   public void randomTick(BlockState p_60551_, ServerLevel p_60552_, BlockPos p_60553_, Random p_60554_) {
      this.tick(p_60551_, p_60552_, p_60553_, p_60554_);
   }

   /** @deprecated */
   @Deprecated
   public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, Random p_60465_) {
   }

   /** @deprecated */
   @Deprecated
   public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
      float f = p_60466_.getDestroySpeed(p_60468_, p_60469_);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = net.minecraftforge.common.ForgeHooks.isCorrectToolForDrops(p_60466_, p_60467_) ? 30 : 100;
         return p_60467_.getDigSpeed(p_60466_, p_60469_) / f / (float)i;
      }
   }

   /** @deprecated */
   @Deprecated
   public void spawnAfterBreak(BlockState p_60458_, ServerLevel p_60459_, BlockPos p_60460_, ItemStack p_60461_) {
   }

   /** @deprecated */
   @Deprecated
   public void attack(BlockState p_60499_, Level p_60500_, BlockPos p_60501_, Player p_60502_) {
   }

   /** @deprecated */
   @Deprecated
   public int getSignal(BlockState p_60483_, BlockGetter p_60484_, BlockPos p_60485_, Direction p_60486_) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public void entityInside(BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
   }

   /** @deprecated */
   @Deprecated
   public int getDirectSignal(BlockState p_60559_, BlockGetter p_60560_, BlockPos p_60561_, Direction p_60562_) {
      return 0;
   }

   public final ResourceLocation getLootTable() {
      if (this.drops == null) {
         this.drops = this.lootTableSupplier.get();
      }

      return this.drops;
   }

   /** @deprecated */
   @Deprecated
   public void onProjectileHit(Level p_60453_, BlockState p_60454_, BlockHitResult p_60455_, Projectile p_60456_) {
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MaterialColor defaultMaterialColor() {
      return this.properties.materialColor.apply(this.asBlock().defaultBlockState());
   }

   public float defaultDestroyTime() {
      return this.properties.destroyTime;
   }

   protected boolean isAir(BlockState state) {
      return ((BlockStateBase)state).isAir;
   }

   /* ======================================== FORGE START ===================================== */
   private final java.util.function.Supplier<ResourceLocation> lootTableSupplier;
   /* ========================================= FORGE END ====================================== */

   public abstract static class BlockStateBase extends StateHolder<Block, BlockState> {
      private final int lightEmission;
      private final boolean useShapeForLightOcclusion;
      private final boolean isAir;
      private final Material material;
      private final MaterialColor materialColor;
      private final float destroySpeed;
      private final boolean requiresCorrectToolForDrops;
      private final boolean canOcclude;
      private final BlockBehaviour.StatePredicate isRedstoneConductor;
      private final BlockBehaviour.StatePredicate isSuffocating;
      private final BlockBehaviour.StatePredicate isViewBlocking;
      private final BlockBehaviour.StatePredicate hasPostProcess;
      private final BlockBehaviour.StatePredicate emissiveRendering;
      @Nullable
      protected BlockBehaviour.BlockStateBase.Cache cache;

      protected BlockStateBase(Block p_60608_, ImmutableMap<Property<?>, Comparable<?>> p_60609_, MapCodec<BlockState> p_60610_) {
         super(p_60608_, p_60609_, p_60610_);
         BlockBehaviour.Properties blockbehaviour$properties = p_60608_.properties;
         this.lightEmission = blockbehaviour$properties.lightEmission.applyAsInt(this.asState());
         this.useShapeForLightOcclusion = p_60608_.useShapeForLightOcclusion(this.asState());
         this.isAir = blockbehaviour$properties.isAir;
         this.material = blockbehaviour$properties.material;
         this.materialColor = blockbehaviour$properties.materialColor.apply(this.asState());
         this.destroySpeed = blockbehaviour$properties.destroyTime;
         this.requiresCorrectToolForDrops = blockbehaviour$properties.requiresCorrectToolForDrops;
         this.canOcclude = blockbehaviour$properties.canOcclude;
         this.isRedstoneConductor = blockbehaviour$properties.isRedstoneConductor;
         this.isSuffocating = blockbehaviour$properties.isSuffocating;
         this.isViewBlocking = blockbehaviour$properties.isViewBlocking;
         this.hasPostProcess = blockbehaviour$properties.hasPostProcess;
         this.emissiveRendering = blockbehaviour$properties.emissiveRendering;
      }

      public void initCache() {
         if (!this.getBlock().hasDynamicShape()) {
            this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
         }

      }

      public Block getBlock() {
         return this.owner;
      }

      public Material getMaterial() {
         return this.material;
      }

      public boolean isValidSpawn(BlockGetter p_60644_, BlockPos p_60645_, EntityType<?> p_60646_) {
         return this.getBlock().properties.isValidSpawn.test(this.asState(), p_60644_, p_60645_, p_60646_);
      }

      public boolean propagatesSkylightDown(BlockGetter p_60632_, BlockPos p_60633_) {
         return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.asState(), p_60632_, p_60633_);
      }

      public int getLightBlock(BlockGetter p_60740_, BlockPos p_60741_) {
         return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this.asState(), p_60740_, p_60741_);
      }

      public VoxelShape getFaceOcclusionShape(BlockGetter p_60656_, BlockPos p_60657_, Direction p_60658_) {
         return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[p_60658_.ordinal()] : Shapes.getFaceShape(this.getOcclusionShape(p_60656_, p_60657_), p_60658_);
      }

      public VoxelShape getOcclusionShape(BlockGetter p_60769_, BlockPos p_60770_) {
         return this.getBlock().getOcclusionShape(this.asState(), p_60769_, p_60770_);
      }

      public boolean hasLargeCollisionShape() {
         return this.cache == null || this.cache.largeCollisionShape;
      }

      public boolean useShapeForLightOcclusion() {
         return this.useShapeForLightOcclusion;
      }

      /** @deprecated use {@link BlockState#getLightEmission(BlockGetter, BlockPos)} */
      @Deprecated
      public int getLightEmission() {
         return this.lightEmission;
      }

      public boolean isAir() {
         return this.getBlock().isAir((BlockState)this);
      }

      public MaterialColor getMapColor(BlockGetter p_60781_, BlockPos p_60782_) {
         return getBlock().getMapColor(this.asState(), p_60781_, p_60782_, this.materialColor);
      }

      /** @deprecated use {@link BlockState#rotate(LevelAccessor, BlockPos, Rotation)} */
      @Deprecated
      public BlockState rotate(Rotation p_60718_) {
         return this.getBlock().rotate(this.asState(), p_60718_);
      }

      public BlockState mirror(Mirror p_60716_) {
         return this.getBlock().mirror(this.asState(), p_60716_);
      }

      public RenderShape getRenderShape() {
         return this.getBlock().getRenderShape(this.asState());
      }

      public boolean emissiveRendering(BlockGetter p_60789_, BlockPos p_60790_) {
         return this.emissiveRendering.test(this.asState(), p_60789_, p_60790_);
      }

      public float getShadeBrightness(BlockGetter p_60793_, BlockPos p_60794_) {
         return this.getBlock().getShadeBrightness(this.asState(), p_60793_, p_60794_);
      }

      public boolean isRedstoneConductor(BlockGetter p_60797_, BlockPos p_60798_) {
         return this.isRedstoneConductor.test(this.asState(), p_60797_, p_60798_);
      }

      public boolean isSignalSource() {
         return this.getBlock().isSignalSource(this.asState());
      }

      public int getSignal(BlockGetter p_60747_, BlockPos p_60748_, Direction p_60749_) {
         return this.getBlock().getSignal(this.asState(), p_60747_, p_60748_, p_60749_);
      }

      public boolean hasAnalogOutputSignal() {
         return this.getBlock().hasAnalogOutputSignal(this.asState());
      }

      public int getAnalogOutputSignal(Level p_60675_, BlockPos p_60676_) {
         return this.getBlock().getAnalogOutputSignal(this.asState(), p_60675_, p_60676_);
      }

      public float getDestroySpeed(BlockGetter p_60801_, BlockPos p_60802_) {
         return this.destroySpeed;
      }

      public float getDestroyProgress(Player p_60626_, BlockGetter p_60627_, BlockPos p_60628_) {
         return this.getBlock().getDestroyProgress(this.asState(), p_60626_, p_60627_, p_60628_);
      }

      public int getDirectSignal(BlockGetter p_60776_, BlockPos p_60777_, Direction p_60778_) {
         return this.getBlock().getDirectSignal(this.asState(), p_60776_, p_60777_, p_60778_);
      }

      public PushReaction getPistonPushReaction() {
         return this.getBlock().getPistonPushReaction(this.asState());
      }

      public boolean isSolidRender(BlockGetter p_60805_, BlockPos p_60806_) {
         if (this.cache != null) {
            return this.cache.solidRender;
         } else {
            BlockState blockstate = this.asState();
            return blockstate.canOcclude() ? Block.isShapeFullBlock(blockstate.getOcclusionShape(p_60805_, p_60806_)) : false;
         }
      }

      public boolean canOcclude() {
         return this.canOcclude;
      }

      public boolean skipRendering(BlockState p_60720_, Direction p_60721_) {
         return this.getBlock().skipRendering(this.asState(), p_60720_, p_60721_);
      }

      public VoxelShape getShape(BlockGetter p_60809_, BlockPos p_60810_) {
         return this.getShape(p_60809_, p_60810_, CollisionContext.empty());
      }

      public VoxelShape getShape(BlockGetter p_60652_, BlockPos p_60653_, CollisionContext p_60654_) {
         return this.getBlock().getShape(this.asState(), p_60652_, p_60653_, p_60654_);
      }

      public VoxelShape getCollisionShape(BlockGetter p_60813_, BlockPos p_60814_) {
         return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(p_60813_, p_60814_, CollisionContext.empty());
      }

      public VoxelShape getCollisionShape(BlockGetter p_60743_, BlockPos p_60744_, CollisionContext p_60745_) {
         return this.getBlock().getCollisionShape(this.asState(), p_60743_, p_60744_, p_60745_);
      }

      public VoxelShape getBlockSupportShape(BlockGetter p_60817_, BlockPos p_60818_) {
         return this.getBlock().getBlockSupportShape(this.asState(), p_60817_, p_60818_);
      }

      public VoxelShape getVisualShape(BlockGetter p_60772_, BlockPos p_60773_, CollisionContext p_60774_) {
         return this.getBlock().getVisualShape(this.asState(), p_60772_, p_60773_, p_60774_);
      }

      public VoxelShape getInteractionShape(BlockGetter p_60821_, BlockPos p_60822_) {
         return this.getBlock().getInteractionShape(this.asState(), p_60821_, p_60822_);
      }

      public final boolean entityCanStandOn(BlockGetter p_60635_, BlockPos p_60636_, Entity p_60637_) {
         return this.entityCanStandOnFace(p_60635_, p_60636_, p_60637_, Direction.UP);
      }

      public final boolean entityCanStandOnFace(BlockGetter p_60639_, BlockPos p_60640_, Entity p_60641_, Direction p_60642_) {
         return Block.isFaceFull(this.getCollisionShape(p_60639_, p_60640_, CollisionContext.of(p_60641_)), p_60642_);
      }

      public Vec3 getOffset(BlockGetter p_60825_, BlockPos p_60826_) {
         Block block = this.getBlock();
         BlockBehaviour.OffsetType blockbehaviour$offsettype = block.getOffsetType();
         if (blockbehaviour$offsettype == BlockBehaviour.OffsetType.NONE) {
            return Vec3.ZERO;
         } else {
            long i = Mth.getSeed(p_60826_.getX(), 0, p_60826_.getZ());
            float f = block.getMaxHorizontalOffset();
            double d0 = Mth.clamp(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, (double)(-f), (double)f);
            double d1 = blockbehaviour$offsettype == BlockBehaviour.OffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * (double)block.getMaxVerticalOffset() : 0.0D;
            double d2 = Mth.clamp(((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D, (double)(-f), (double)f);
            return new Vec3(d0, d1, d2);
         }
      }

      public boolean triggerEvent(Level p_60678_, BlockPos p_60679_, int p_60680_, int p_60681_) {
         return this.getBlock().triggerEvent(this.asState(), p_60678_, p_60679_, p_60680_, p_60681_);
      }

      public void neighborChanged(Level p_60691_, BlockPos p_60692_, Block p_60693_, BlockPos p_60694_, boolean p_60695_) {
         this.getBlock().neighborChanged(this.asState(), p_60691_, p_60692_, p_60693_, p_60694_, p_60695_);
      }

      public final void updateNeighbourShapes(LevelAccessor p_60702_, BlockPos p_60703_, int p_60704_) {
         this.updateNeighbourShapes(p_60702_, p_60703_, p_60704_, 512);
      }

      public final void updateNeighbourShapes(LevelAccessor p_60706_, BlockPos p_60707_, int p_60708_, int p_60709_) {
         this.getBlock();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(Direction direction : BlockBehaviour.UPDATE_SHAPE_ORDER) {
            blockpos$mutableblockpos.setWithOffset(p_60707_, direction);
            BlockState blockstate = p_60706_.getBlockState(blockpos$mutableblockpos);
            BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), this.asState(), p_60706_, blockpos$mutableblockpos, p_60707_);
            Block.updateOrDestroy(blockstate, blockstate1, p_60706_, blockpos$mutableblockpos, p_60708_, p_60709_);
         }

      }

      public final void updateIndirectNeighbourShapes(LevelAccessor p_60759_, BlockPos p_60760_, int p_60761_) {
         this.updateIndirectNeighbourShapes(p_60759_, p_60760_, p_60761_, 512);
      }

      public void updateIndirectNeighbourShapes(LevelAccessor p_60763_, BlockPos p_60764_, int p_60765_, int p_60766_) {
         this.getBlock().updateIndirectNeighbourShapes(this.asState(), p_60763_, p_60764_, p_60765_, p_60766_);
      }

      public void onPlace(Level p_60697_, BlockPos p_60698_, BlockState p_60699_, boolean p_60700_) {
         this.getBlock().onPlace(this.asState(), p_60697_, p_60698_, p_60699_, p_60700_);
      }

      public void onRemove(Level p_60754_, BlockPos p_60755_, BlockState p_60756_, boolean p_60757_) {
         this.getBlock().onRemove(this.asState(), p_60754_, p_60755_, p_60756_, p_60757_);
      }

      public void tick(ServerLevel p_60617_, BlockPos p_60618_, Random p_60619_) {
         this.getBlock().tick(this.asState(), p_60617_, p_60618_, p_60619_);
      }

      public void randomTick(ServerLevel p_60736_, BlockPos p_60737_, Random p_60738_) {
         this.getBlock().randomTick(this.asState(), p_60736_, p_60737_, p_60738_);
      }

      public void entityInside(Level p_60683_, BlockPos p_60684_, Entity p_60685_) {
         this.getBlock().entityInside(this.asState(), p_60683_, p_60684_, p_60685_);
      }

      public void spawnAfterBreak(ServerLevel p_60613_, BlockPos p_60614_, ItemStack p_60615_) {
         this.getBlock().spawnAfterBreak(this.asState(), p_60613_, p_60614_, p_60615_);
      }

      public List<ItemStack> getDrops(LootContext.Builder p_60725_) {
         return this.getBlock().getDrops(this.asState(), p_60725_);
      }

      public InteractionResult use(Level p_60665_, Player p_60666_, InteractionHand p_60667_, BlockHitResult p_60668_) {
         return this.getBlock().use(this.asState(), p_60665_, p_60668_.getBlockPos(), p_60666_, p_60667_, p_60668_);
      }

      public void attack(Level p_60687_, BlockPos p_60688_, Player p_60689_) {
         this.getBlock().attack(this.asState(), p_60687_, p_60688_, p_60689_);
      }

      public boolean isSuffocating(BlockGetter p_60829_, BlockPos p_60830_) {
         return this.isSuffocating.test(this.asState(), p_60829_, p_60830_);
      }

      public boolean isViewBlocking(BlockGetter p_60832_, BlockPos p_60833_) {
         return this.isViewBlocking.test(this.asState(), p_60832_, p_60833_);
      }

      public BlockState updateShape(Direction p_60729_, BlockState p_60730_, LevelAccessor p_60731_, BlockPos p_60732_, BlockPos p_60733_) {
         return this.getBlock().updateShape(this.asState(), p_60729_, p_60730_, p_60731_, p_60732_, p_60733_);
      }

      public boolean isPathfindable(BlockGetter p_60648_, BlockPos p_60649_, PathComputationType p_60650_) {
         return this.getBlock().isPathfindable(this.asState(), p_60648_, p_60649_, p_60650_);
      }

      public boolean canBeReplaced(BlockPlaceContext p_60630_) {
         return this.getBlock().canBeReplaced(this.asState(), p_60630_);
      }

      public boolean canBeReplaced(Fluid p_60723_) {
         return this.getBlock().canBeReplaced(this.asState(), p_60723_);
      }

      public boolean canSurvive(LevelReader p_60711_, BlockPos p_60712_) {
         return this.getBlock().canSurvive(this.asState(), p_60711_, p_60712_);
      }

      public boolean hasPostProcess(BlockGetter p_60836_, BlockPos p_60837_) {
         return this.hasPostProcess.test(this.asState(), p_60836_, p_60837_);
      }

      @Nullable
      public MenuProvider getMenuProvider(Level p_60751_, BlockPos p_60752_) {
         return this.getBlock().getMenuProvider(this.asState(), p_60751_, p_60752_);
      }

      public boolean is(TagKey<Block> p_204337_) {
         return this.getBlock().builtInRegistryHolder().is(p_204337_);
      }

      public boolean is(TagKey<Block> p_204339_, Predicate<BlockBehaviour.BlockStateBase> p_204340_) {
         return this.is(p_204339_) && p_204340_.test(this);
      }

      public boolean is(HolderSet<Block> p_204342_) {
         return p_204342_.contains(this.getBlock().builtInRegistryHolder());
      }

      public Stream<TagKey<Block>> getTags() {
         return this.getBlock().builtInRegistryHolder().tags();
      }

      public boolean hasBlockEntity() {
         return this.getBlock() instanceof EntityBlock;
      }

      @Nullable
      public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_155945_, BlockEntityType<T> p_155946_) {
         return this.getBlock() instanceof EntityBlock ? ((EntityBlock)this.getBlock()).getTicker(p_155945_, this.asState(), p_155946_) : null;
      }

      public boolean is(Block p_60714_) {
         return this.getBlock() == p_60714_;
      }

      public FluidState getFluidState() {
         return this.getBlock().getFluidState(this.asState());
      }

      public boolean isRandomlyTicking() {
         return this.getBlock().isRandomlyTicking(this.asState());
      }

      public long getSeed(BlockPos p_60727_) {
         return this.getBlock().getSeed(this.asState(), p_60727_);
      }

      public SoundType getSoundType() {
         return this.getBlock().getSoundType(this.asState());
      }

      public void onProjectileHit(Level p_60670_, BlockState p_60671_, BlockHitResult p_60672_, Projectile p_60673_) {
         this.getBlock().onProjectileHit(p_60670_, p_60671_, p_60672_, p_60673_);
      }

      public boolean isFaceSturdy(BlockGetter p_60784_, BlockPos p_60785_, Direction p_60786_) {
         return this.isFaceSturdy(p_60784_, p_60785_, p_60786_, SupportType.FULL);
      }

      public boolean isFaceSturdy(BlockGetter p_60660_, BlockPos p_60661_, Direction p_60662_, SupportType p_60663_) {
         return this.cache != null ? this.cache.isFaceSturdy(p_60662_, p_60663_) : p_60663_.isSupporting(this.asState(), p_60660_, p_60661_, p_60662_);
      }

      public boolean isCollisionShapeFullBlock(BlockGetter p_60839_, BlockPos p_60840_) {
         return this.cache != null ? this.cache.isCollisionShapeFullBlock : this.getBlock().isCollisionShapeFullBlock(this.asState(), p_60839_, p_60840_);
      }

      protected abstract BlockState asState();

      public boolean requiresCorrectToolForDrops() {
         return this.requiresCorrectToolForDrops;
      }

      static final class Cache {
         private static final Direction[] DIRECTIONS = Direction.values();
         private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
         protected final boolean solidRender;
         final boolean propagatesSkylightDown;
         final int lightBlock;
         @Nullable
         final VoxelShape[] occlusionShapes;
         protected final VoxelShape collisionShape;
         protected final boolean largeCollisionShape;
         private final boolean[] faceSturdy;
         protected final boolean isCollisionShapeFullBlock;

         Cache(BlockState p_60853_) {
            Block block = p_60853_.getBlock();
            this.solidRender = p_60853_.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.propagatesSkylightDown = block.propagatesSkylightDown(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.lightBlock = block.getLightBlock(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (!p_60853_.canOcclude()) {
               this.occlusionShapes = null;
            } else {
               this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
               VoxelShape voxelshape = block.getOcclusionShape(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

               for(Direction direction : DIRECTIONS) {
                  this.occlusionShapes[direction.ordinal()] = Shapes.getFaceShape(voxelshape, direction);
               }
            }

            this.collisionShape = block.getCollisionShape(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
            if (!this.collisionShape.isEmpty() && block.getOffsetType() != BlockBehaviour.OffsetType.NONE) {
               throw new IllegalStateException(String.format("%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registry.BLOCK.getKey(block)));
            } else {
               this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((p_60860_) -> {
                  return this.collisionShape.min(p_60860_) < 0.0D || this.collisionShape.max(p_60860_) > 1.0D;
               });
               this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];

               for(Direction direction1 : DIRECTIONS) {
                  for(SupportType supporttype : SupportType.values()) {
                     this.faceSturdy[getFaceSupportIndex(direction1, supporttype)] = supporttype.isSupporting(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, direction1);
                  }
               }

               this.isCollisionShapeFullBlock = Block.isShapeFullBlock(p_60853_.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
            }
         }

         public boolean isFaceSturdy(Direction p_60862_, SupportType p_60863_) {
            return this.faceSturdy[getFaceSupportIndex(p_60862_, p_60863_)];
         }

         private static int getFaceSupportIndex(Direction p_60867_, SupportType p_60868_) {
            return p_60867_.ordinal() * SUPPORT_TYPE_COUNT + p_60868_.ordinal();
         }
      }
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Properties {
      Material material;
      Function<BlockState, MaterialColor> materialColor;
      boolean hasCollision = true;
      SoundType soundType = SoundType.STONE;
      ToIntFunction<BlockState> lightEmission = (p_60929_) -> {
         return 0;
      };
      float explosionResistance;
      float destroyTime;
      boolean requiresCorrectToolForDrops;
      boolean isRandomlyTicking;
      float friction = 0.6F;
      float speedFactor = 1.0F;
      float jumpFactor = 1.0F;
      ResourceLocation drops;
      boolean canOcclude = true;
      boolean isAir;
      private java.util.function.Supplier<ResourceLocation> lootTableSupplier;
      BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn = (p_60935_, p_60936_, p_60937_, p_60938_) -> {
         return p_60935_.isFaceSturdy(p_60936_, p_60937_, Direction.UP) && p_60935_.getLightEmission(p_60936_, p_60937_) < 14;
      };
      BlockBehaviour.StatePredicate isRedstoneConductor = (p_60985_, p_60986_, p_60987_) -> {
         return p_60985_.getMaterial().isSolidBlocking() && p_60985_.isCollisionShapeFullBlock(p_60986_, p_60987_);
      };
      BlockBehaviour.StatePredicate isSuffocating = (p_60974_, p_60975_, p_60976_) -> {
         return this.material.blocksMotion() && p_60974_.isCollisionShapeFullBlock(p_60975_, p_60976_);
      };
      BlockBehaviour.StatePredicate isViewBlocking = this.isSuffocating;
      BlockBehaviour.StatePredicate hasPostProcess = (p_60963_, p_60964_, p_60965_) -> {
         return false;
      };
      BlockBehaviour.StatePredicate emissiveRendering = (p_60931_, p_60932_, p_60933_) -> {
         return false;
      };
      boolean dynamicShape;

      private Properties(Material p_60905_, MaterialColor p_60906_) {
         this(p_60905_, (p_60952_) -> {
            return p_60906_;
         });
      }

      private Properties(Material p_60908_, Function<BlockState, MaterialColor> p_60909_) {
         this.material = p_60908_;
         this.materialColor = p_60909_;
      }

      public static BlockBehaviour.Properties of(Material p_60940_) {
         return of(p_60940_, p_60940_.getColor());
      }

      public static BlockBehaviour.Properties of(Material p_60942_, DyeColor p_60943_) {
         return of(p_60942_, p_60943_.getMaterialColor());
      }

      public static BlockBehaviour.Properties of(Material p_60945_, MaterialColor p_60946_) {
         return new BlockBehaviour.Properties(p_60945_, p_60946_);
      }

      public static BlockBehaviour.Properties of(Material p_60948_, Function<BlockState, MaterialColor> p_60949_) {
         return new BlockBehaviour.Properties(p_60948_, p_60949_);
      }

      public static BlockBehaviour.Properties copy(BlockBehaviour p_60927_) {
         BlockBehaviour.Properties blockbehaviour$properties = new BlockBehaviour.Properties(p_60927_.material, p_60927_.properties.materialColor);
         blockbehaviour$properties.material = p_60927_.properties.material;
         blockbehaviour$properties.destroyTime = p_60927_.properties.destroyTime;
         blockbehaviour$properties.explosionResistance = p_60927_.properties.explosionResistance;
         blockbehaviour$properties.hasCollision = p_60927_.properties.hasCollision;
         blockbehaviour$properties.isRandomlyTicking = p_60927_.properties.isRandomlyTicking;
         blockbehaviour$properties.lightEmission = p_60927_.properties.lightEmission;
         blockbehaviour$properties.materialColor = p_60927_.properties.materialColor;
         blockbehaviour$properties.soundType = p_60927_.properties.soundType;
         blockbehaviour$properties.friction = p_60927_.properties.friction;
         blockbehaviour$properties.speedFactor = p_60927_.properties.speedFactor;
         blockbehaviour$properties.dynamicShape = p_60927_.properties.dynamicShape;
         blockbehaviour$properties.canOcclude = p_60927_.properties.canOcclude;
         blockbehaviour$properties.isAir = p_60927_.properties.isAir;
         blockbehaviour$properties.requiresCorrectToolForDrops = p_60927_.properties.requiresCorrectToolForDrops;
         return blockbehaviour$properties;
      }

      public BlockBehaviour.Properties noCollission() {
         this.hasCollision = false;
         this.canOcclude = false;
         return this;
      }

      public BlockBehaviour.Properties noOcclusion() {
         this.canOcclude = false;
         return this;
      }

      public BlockBehaviour.Properties friction(float p_60912_) {
         this.friction = p_60912_;
         return this;
      }

      public BlockBehaviour.Properties speedFactor(float p_60957_) {
         this.speedFactor = p_60957_;
         return this;
      }

      public BlockBehaviour.Properties jumpFactor(float p_60968_) {
         this.jumpFactor = p_60968_;
         return this;
      }

      public BlockBehaviour.Properties sound(SoundType p_60919_) {
         this.soundType = p_60919_;
         return this;
      }

      public BlockBehaviour.Properties lightLevel(ToIntFunction<BlockState> p_60954_) {
         this.lightEmission = p_60954_;
         return this;
      }

      public BlockBehaviour.Properties strength(float p_60914_, float p_60915_) {
         return this.destroyTime(p_60914_).explosionResistance(p_60915_);
      }

      public BlockBehaviour.Properties instabreak() {
         return this.strength(0.0F);
      }

      public BlockBehaviour.Properties strength(float p_60979_) {
         this.strength(p_60979_, p_60979_);
         return this;
      }

      public BlockBehaviour.Properties randomTicks() {
         this.isRandomlyTicking = true;
         return this;
      }

      public BlockBehaviour.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      public BlockBehaviour.Properties noDrops() {
         this.drops = BuiltInLootTables.EMPTY;
         return this;
      }

      @Deprecated // FORGE: Use the variant that takes a Supplier below
      public BlockBehaviour.Properties dropsLike(Block p_60917_) {
         this.lootTableSupplier = () -> p_60917_.delegate.get().getLootTable();
         return this;
      }

      public BlockBehaviour.Properties lootFrom(java.util.function.Supplier<? extends Block> blockIn) {
          this.lootTableSupplier = () -> blockIn.get().getLootTable();
          return this;
      }

      public BlockBehaviour.Properties air() {
         this.isAir = true;
         return this;
      }

      public BlockBehaviour.Properties isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> p_60923_) {
         this.isValidSpawn = p_60923_;
         return this;
      }

      public BlockBehaviour.Properties isRedstoneConductor(BlockBehaviour.StatePredicate p_60925_) {
         this.isRedstoneConductor = p_60925_;
         return this;
      }

      public BlockBehaviour.Properties isSuffocating(BlockBehaviour.StatePredicate p_60961_) {
         this.isSuffocating = p_60961_;
         return this;
      }

      public BlockBehaviour.Properties isViewBlocking(BlockBehaviour.StatePredicate p_60972_) {
         this.isViewBlocking = p_60972_;
         return this;
      }

      public BlockBehaviour.Properties hasPostProcess(BlockBehaviour.StatePredicate p_60983_) {
         this.hasPostProcess = p_60983_;
         return this;
      }

      public BlockBehaviour.Properties emissiveRendering(BlockBehaviour.StatePredicate p_60992_) {
         this.emissiveRendering = p_60992_;
         return this;
      }

      public BlockBehaviour.Properties requiresCorrectToolForDrops() {
         this.requiresCorrectToolForDrops = true;
         return this;
      }

      public BlockBehaviour.Properties color(MaterialColor p_155950_) {
         this.materialColor = (p_155953_) -> {
            return p_155950_;
         };
         return this;
      }

      public BlockBehaviour.Properties destroyTime(float p_155955_) {
         this.destroyTime = p_155955_;
         return this;
      }

      public BlockBehaviour.Properties explosionResistance(float p_155957_) {
         this.explosionResistance = Math.max(0.0F, p_155957_);
         return this;
      }
   }

   public interface StateArgumentPredicate<A> {
      boolean test(BlockState p_61031_, BlockGetter p_61032_, BlockPos p_61033_, A p_61034_);
   }

   public interface StatePredicate {
      boolean test(BlockState p_61036_, BlockGetter p_61037_, BlockPos p_61038_);
   }
}
