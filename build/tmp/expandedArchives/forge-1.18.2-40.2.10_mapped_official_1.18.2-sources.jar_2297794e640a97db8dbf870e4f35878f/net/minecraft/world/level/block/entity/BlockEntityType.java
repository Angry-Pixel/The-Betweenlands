package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class BlockEntityType<T extends BlockEntity> extends net.minecraftforge.registries.ForgeRegistryEntry<BlockEntityType<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BlockEntityType<FurnaceBlockEntity> FURNACE = register("furnace", BlockEntityType.Builder.of(FurnaceBlockEntity::new, Blocks.FURNACE));
   public static final BlockEntityType<ChestBlockEntity> CHEST = register("chest", BlockEntityType.Builder.of(ChestBlockEntity::new, Blocks.CHEST));
   public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = register("trapped_chest", BlockEntityType.Builder.of(TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST));
   public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = register("ender_chest", BlockEntityType.Builder.of(EnderChestBlockEntity::new, Blocks.ENDER_CHEST));
   public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = register("jukebox", BlockEntityType.Builder.of(JukeboxBlockEntity::new, Blocks.JUKEBOX));
   public static final BlockEntityType<DispenserBlockEntity> DISPENSER = register("dispenser", BlockEntityType.Builder.of(DispenserBlockEntity::new, Blocks.DISPENSER));
   public static final BlockEntityType<DropperBlockEntity> DROPPER = register("dropper", BlockEntityType.Builder.of(DropperBlockEntity::new, Blocks.DROPPER));
   public static final BlockEntityType<SignBlockEntity> SIGN = register("sign", BlockEntityType.Builder.of(SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN));
   public static final BlockEntityType<SpawnerBlockEntity> MOB_SPAWNER = register("mob_spawner", BlockEntityType.Builder.of(SpawnerBlockEntity::new, Blocks.SPAWNER));
   public static final BlockEntityType<PistonMovingBlockEntity> PISTON = register("piston", BlockEntityType.Builder.of(PistonMovingBlockEntity::new, Blocks.MOVING_PISTON));
   public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = register("brewing_stand", BlockEntityType.Builder.of(BrewingStandBlockEntity::new, Blocks.BREWING_STAND));
   public static final BlockEntityType<EnchantmentTableBlockEntity> ENCHANTING_TABLE = register("enchanting_table", BlockEntityType.Builder.of(EnchantmentTableBlockEntity::new, Blocks.ENCHANTING_TABLE));
   public static final BlockEntityType<TheEndPortalBlockEntity> END_PORTAL = register("end_portal", BlockEntityType.Builder.of(TheEndPortalBlockEntity::new, Blocks.END_PORTAL));
   public static final BlockEntityType<BeaconBlockEntity> BEACON = register("beacon", BlockEntityType.Builder.of(BeaconBlockEntity::new, Blocks.BEACON));
   public static final BlockEntityType<SkullBlockEntity> SKULL = register("skull", BlockEntityType.Builder.of(SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
   public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = register("daylight_detector", BlockEntityType.Builder.of(DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR));
   public static final BlockEntityType<HopperBlockEntity> HOPPER = register("hopper", BlockEntityType.Builder.of(HopperBlockEntity::new, Blocks.HOPPER));
   public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = register("comparator", BlockEntityType.Builder.of(ComparatorBlockEntity::new, Blocks.COMPARATOR));
   public static final BlockEntityType<BannerBlockEntity> BANNER = register("banner", BlockEntityType.Builder.of(BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
   public static final BlockEntityType<StructureBlockEntity> STRUCTURE_BLOCK = register("structure_block", BlockEntityType.Builder.of(StructureBlockEntity::new, Blocks.STRUCTURE_BLOCK));
   public static final BlockEntityType<TheEndGatewayBlockEntity> END_GATEWAY = register("end_gateway", BlockEntityType.Builder.of(TheEndGatewayBlockEntity::new, Blocks.END_GATEWAY));
   public static final BlockEntityType<CommandBlockEntity> COMMAND_BLOCK = register("command_block", BlockEntityType.Builder.of(CommandBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
   public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = register("shulker_box", BlockEntityType.Builder.of(ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
   public static final BlockEntityType<BedBlockEntity> BED = register("bed", BlockEntityType.Builder.of(BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
   public static final BlockEntityType<ConduitBlockEntity> CONDUIT = register("conduit", BlockEntityType.Builder.of(ConduitBlockEntity::new, Blocks.CONDUIT));
   public static final BlockEntityType<BarrelBlockEntity> BARREL = register("barrel", BlockEntityType.Builder.of(BarrelBlockEntity::new, Blocks.BARREL));
   public static final BlockEntityType<SmokerBlockEntity> SMOKER = register("smoker", BlockEntityType.Builder.of(SmokerBlockEntity::new, Blocks.SMOKER));
   public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = register("blast_furnace", BlockEntityType.Builder.of(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE));
   public static final BlockEntityType<LecternBlockEntity> LECTERN = register("lectern", BlockEntityType.Builder.of(LecternBlockEntity::new, Blocks.LECTERN));
   public static final BlockEntityType<BellBlockEntity> BELL = register("bell", BlockEntityType.Builder.of(BellBlockEntity::new, Blocks.BELL));
   public static final BlockEntityType<JigsawBlockEntity> JIGSAW = register("jigsaw", BlockEntityType.Builder.of(JigsawBlockEntity::new, Blocks.JIGSAW));
   public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = register("campfire", BlockEntityType.Builder.of(CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
   public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = register("beehive", BlockEntityType.Builder.of(BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
   public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = register("sculk_sensor", BlockEntityType.Builder.of(SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR));
   private final BlockEntityType.BlockEntitySupplier<? extends T> factory;
   private final Set<Block> validBlocks;
   private final Type<?> dataType;

   @Nullable
   public static ResourceLocation getKey(BlockEntityType<?> p_58955_) {
      return Registry.BLOCK_ENTITY_TYPE.getKey(p_58955_);
   }

   private static <T extends BlockEntity> BlockEntityType<T> register(String p_58957_, BlockEntityType.Builder<T> p_58958_) {
      if (p_58958_.validBlocks.isEmpty()) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)p_58957_);
      }

      Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, p_58957_);
      return Registry.register(Registry.BLOCK_ENTITY_TYPE, p_58957_, p_58958_.build(type));
   }

   public BlockEntityType(BlockEntityType.BlockEntitySupplier<? extends T> p_155259_, Set<Block> p_155260_, Type<?> p_155261_) {
      this.factory = p_155259_;
      this.validBlocks = p_155260_;
      this.dataType = p_155261_;
   }

   @Nullable
   public T create(BlockPos p_155265_, BlockState p_155266_) {
      return this.factory.create(p_155265_, p_155266_);
   }

   public boolean isValid(BlockState p_155263_) {
      return this.validBlocks.contains(p_155263_.getBlock());
   }

   @Nullable
   public T getBlockEntity(BlockGetter p_58950_, BlockPos p_58951_) {
      BlockEntity blockentity = p_58950_.getBlockEntity(p_58951_);
      return (T)(blockentity != null && blockentity.getType() == this ? blockentity : null);
   }

   @FunctionalInterface
   public interface BlockEntitySupplier<T extends BlockEntity> {
      T create(BlockPos p_155268_, BlockState p_155269_);
   }

   public static final class Builder<T extends BlockEntity> {
      private final BlockEntityType.BlockEntitySupplier<? extends T> factory;
      final Set<Block> validBlocks;

      private Builder(BlockEntityType.BlockEntitySupplier<? extends T> p_155271_, Set<Block> p_155272_) {
         this.factory = p_155271_;
         this.validBlocks = p_155272_;
      }

      public static <T extends BlockEntity> BlockEntityType.Builder<T> of(BlockEntityType.BlockEntitySupplier<? extends T> p_155274_, Block... p_155275_) {
         return new BlockEntityType.Builder<>(p_155274_, ImmutableSet.copyOf(p_155275_));
      }

      public BlockEntityType<T> build(Type<?> p_58967_) {
         return new BlockEntityType<>(this.factory, this.validBlocks, p_58967_);
      }
   }
}
