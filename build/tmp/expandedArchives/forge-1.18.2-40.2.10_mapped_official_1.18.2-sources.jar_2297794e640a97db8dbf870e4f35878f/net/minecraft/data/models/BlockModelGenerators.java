package net.minecraft.data.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.block.state.properties.WallSide;

public class BlockModelGenerators {
   final Consumer<BlockStateGenerator> blockStateOutput;
   final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
   private final Consumer<Item> skippedAutoModelsOutput;
   final List<Block> nonOrientableTrapdoor = ImmutableList.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
   final Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> fullBlockModelCustomGenerators = ImmutableMap.<Block, BlockModelGenerators.BlockStateGeneratorSupplier>builder().put(Blocks.STONE, BlockModelGenerators::createMirroredCubeGenerator).put(Blocks.DEEPSLATE, BlockModelGenerators::createMirroredColumnGenerator).build();
   final Map<Block, TexturedModel> texturedModels = ImmutableMap.<Block, TexturedModel>builder().put(Blocks.SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE)).put(Blocks.RED_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE)).put(Blocks.SMOOTH_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"))).put(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"))).put(Blocks.CUT_SANDSTONE, TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures((p_176223_) -> {
      p_176223_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE));
   })).put(Blocks.CUT_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.RED_SANDSTONE).updateTextures((p_176211_) -> {
      p_176211_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE));
   })).put(Blocks.QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.QUARTZ_BLOCK)).put(Blocks.SMOOTH_QUARTZ, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom"))).put(Blocks.BLACKSTONE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.BLACKSTONE)).put(Blocks.DEEPSLATE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.DEEPSLATE)).put(Blocks.CHISELED_QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).updateTextures((p_176202_) -> {
      p_176202_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK));
   })).put(Blocks.CHISELED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures((p_176190_) -> {
      p_176190_.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
      p_176190_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
   })).put(Blocks.CHISELED_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures((p_176145_) -> {
      p_176145_.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
      p_176145_.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
   })).build();
   static final Map<BlockFamily.Variant, BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block>> SHAPE_CONSUMERS = ImmutableMap.<BlockFamily.Variant, BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block>>builder().put(BlockFamily.Variant.BUTTON, BlockModelGenerators.BlockFamilyProvider::button).put(BlockFamily.Variant.DOOR, BlockModelGenerators.BlockFamilyProvider::door).put(BlockFamily.Variant.CHISELED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.CRACKED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.FENCE, BlockModelGenerators.BlockFamilyProvider::fence).put(BlockFamily.Variant.FENCE_GATE, BlockModelGenerators.BlockFamilyProvider::fenceGate).put(BlockFamily.Variant.SIGN, BlockModelGenerators.BlockFamilyProvider::sign).put(BlockFamily.Variant.SLAB, BlockModelGenerators.BlockFamilyProvider::slab).put(BlockFamily.Variant.STAIRS, BlockModelGenerators.BlockFamilyProvider::stairs).put(BlockFamily.Variant.PRESSURE_PLATE, BlockModelGenerators.BlockFamilyProvider::pressurePlate).put(BlockFamily.Variant.TRAPDOOR, BlockModelGenerators.BlockFamilyProvider::trapdoor).put(BlockFamily.Variant.WALL, BlockModelGenerators.BlockFamilyProvider::wall).build();
   public static final Map<BooleanProperty, Function<ResourceLocation, Variant>> MULTIFACE_GENERATOR = Util.make(Maps.newHashMap(), (p_176141_) -> {
      p_176141_.put(BlockStateProperties.NORTH, (p_176234_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176234_);
      });
      p_176141_.put(BlockStateProperties.EAST, (p_176229_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176229_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true);
      });
      p_176141_.put(BlockStateProperties.SOUTH, (p_176225_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176225_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true);
      });
      p_176141_.put(BlockStateProperties.WEST, (p_176213_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176213_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true);
      });
      p_176141_.put(BlockStateProperties.UP, (p_176204_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176204_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true);
      });
      p_176141_.put(BlockStateProperties.DOWN, (p_176195_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176195_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true);
      });
   });

   private static BlockStateGenerator createMirroredCubeGenerator(Block p_176110_, ResourceLocation p_176111_, TextureMapping p_176112_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_176113_) {
      ResourceLocation resourcelocation = ModelTemplates.CUBE_MIRRORED_ALL.create(p_176110_, p_176112_, p_176113_);
      return createRotatedVariant(p_176110_, p_176111_, resourcelocation);
   }

   private static BlockStateGenerator createMirroredColumnGenerator(Block p_176180_, ResourceLocation p_176181_, TextureMapping p_176182_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_176183_) {
      ResourceLocation resourcelocation = ModelTemplates.CUBE_COLUMN_MIRRORED.create(p_176180_, p_176182_, p_176183_);
      return createRotatedVariant(p_176180_, p_176181_, resourcelocation).with(createRotatedPillar());
   }

   public BlockModelGenerators(Consumer<BlockStateGenerator> p_124481_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_124482_, Consumer<Item> p_124483_) {
      this.blockStateOutput = p_124481_;
      this.modelOutput = p_124482_;
      this.skippedAutoModelsOutput = p_124483_;
   }

   void skipAutoItemBlock(Block p_124525_) {
      this.skippedAutoModelsOutput.accept(p_124525_.asItem());
   }

   void delegateItemModel(Block p_124798_, ResourceLocation p_124799_) {
      this.modelOutput.accept(ModelLocationUtils.getModelLocation(p_124798_.asItem()), new DelegatedModel(p_124799_));
   }

   private void delegateItemModel(Item p_124520_, ResourceLocation p_124521_) {
      this.modelOutput.accept(ModelLocationUtils.getModelLocation(p_124520_), new DelegatedModel(p_124521_));
   }

   void createSimpleFlatItemModel(Item p_124518_) {
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(p_124518_), TextureMapping.layer0(p_124518_), this.modelOutput);
   }

   private void createSimpleFlatItemModel(Block p_124729_) {
      Item item = p_124729_.asItem();
      if (item != Items.AIR) {
         ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(p_124729_), this.modelOutput);
      }

   }

   private void createSimpleFlatItemModel(Block p_124576_, String p_124577_) {
      Item item = p_124576_.asItem();
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(TextureMapping.getBlockTexture(p_124576_, p_124577_)), this.modelOutput);
   }

   private static PropertyDispatch createHorizontalFacingDispatch() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant());
   }

   private static PropertyDispatch createHorizontalFacingDispatchAlt() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, Variant.variant()).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
   }

   private static PropertyDispatch createTorchHorizontalDispatch() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
   }

   private static PropertyDispatch createFacingDispatch() {
      return PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
   }

   private static MultiVariantGenerator createRotatedVariant(Block p_124832_, ResourceLocation p_124833_) {
      return MultiVariantGenerator.multiVariant(p_124832_, createRotatedVariants(p_124833_));
   }

   private static Variant[] createRotatedVariants(ResourceLocation p_124689_) {
      return new Variant[]{Variant.variant().with(VariantProperties.MODEL, p_124689_), Variant.variant().with(VariantProperties.MODEL, p_124689_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, p_124689_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, p_124689_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)};
   }

   private static MultiVariantGenerator createRotatedVariant(Block p_124863_, ResourceLocation p_124864_, ResourceLocation p_124865_) {
      return MultiVariantGenerator.multiVariant(p_124863_, Variant.variant().with(VariantProperties.MODEL, p_124864_), Variant.variant().with(VariantProperties.MODEL, p_124865_), Variant.variant().with(VariantProperties.MODEL, p_124864_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, p_124865_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
   }

   private static PropertyDispatch createBooleanModelDispatch(BooleanProperty p_124623_, ResourceLocation p_124624_, ResourceLocation p_124625_) {
      return PropertyDispatch.property(p_124623_).select(true, Variant.variant().with(VariantProperties.MODEL, p_124624_)).select(false, Variant.variant().with(VariantProperties.MODEL, p_124625_));
   }

   private void createRotatedMirroredVariantBlock(Block p_124787_) {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(p_124787_, this.modelOutput);
      ResourceLocation resourcelocation1 = TexturedModel.CUBE_MIRRORED.create(p_124787_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(p_124787_, resourcelocation, resourcelocation1));
   }

   private void createRotatedVariantBlock(Block p_124824_) {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(p_124824_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(p_124824_, resourcelocation));
   }

   static BlockStateGenerator createButton(Block p_124885_, ResourceLocation p_124886_, ResourceLocation p_124887_) {
      return MultiVariantGenerator.multiVariant(p_124885_).with(PropertyDispatch.property(BlockStateProperties.POWERED).select(false, Variant.variant().with(VariantProperties.MODEL, p_124886_)).select(true, Variant.variant().with(VariantProperties.MODEL, p_124887_))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)));
   }

   private static PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> p_124651_, DoubleBlockHalf p_124652_, ResourceLocation p_124653_, ResourceLocation p_124654_) {
      return p_124651_.select(Direction.EAST, p_124652_, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, p_124653_)).select(Direction.SOUTH, p_124652_, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, p_124652_, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, p_124652_, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, p_124652_, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, p_124654_)).select(Direction.SOUTH, p_124652_, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, p_124652_, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, p_124652_, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, p_124652_, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, p_124652_, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, p_124652_, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, p_124654_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, p_124652_, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, p_124654_)).select(Direction.EAST, p_124652_, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.SOUTH, p_124652_, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, p_124653_)).select(Direction.WEST, p_124652_, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, p_124652_, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, p_124653_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
   }

   private static BlockStateGenerator createDoor(Block p_124760_, ResourceLocation p_124761_, ResourceLocation p_124762_, ResourceLocation p_124763_, ResourceLocation p_124764_) {
      return MultiVariantGenerator.multiVariant(p_124760_).with(configureDoorHalf(configureDoorHalf(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN), DoubleBlockHalf.LOWER, p_124761_, p_124762_), DoubleBlockHalf.UPPER, p_124763_, p_124764_));
   }

   static BlockStateGenerator createFence(Block p_124905_, ResourceLocation p_124906_, ResourceLocation p_124907_) {
      return MultiPartGenerator.multiPart(p_124905_).with(Variant.variant().with(VariantProperties.MODEL, p_124906_)).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, p_124907_).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, p_124907_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, p_124907_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, p_124907_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
   }

   static BlockStateGenerator createWall(Block p_124839_, ResourceLocation p_124840_, ResourceLocation p_124841_, ResourceLocation p_124842_) {
      return MultiPartGenerator.multiPart(p_124839_).with(Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, p_124840_)).with(Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, p_124841_).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, p_124841_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, p_124841_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, p_124841_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, p_124842_).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, p_124842_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, p_124842_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, p_124842_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
   }

   static BlockStateGenerator createFenceGate(Block p_124810_, ResourceLocation p_124811_, ResourceLocation p_124812_, ResourceLocation p_124813_, ResourceLocation p_124814_) {
      return MultiVariantGenerator.multiVariant(p_124810_, Variant.variant().with(VariantProperties.UV_LOCK, true)).with(createHorizontalFacingDispatchAlt()).with(PropertyDispatch.properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select(false, false, Variant.variant().with(VariantProperties.MODEL, p_124812_)).select(true, false, Variant.variant().with(VariantProperties.MODEL, p_124814_)).select(false, true, Variant.variant().with(VariantProperties.MODEL, p_124811_)).select(true, true, Variant.variant().with(VariantProperties.MODEL, p_124813_)));
   }

   static BlockStateGenerator createStairs(Block p_124867_, ResourceLocation p_124868_, ResourceLocation p_124869_, ResourceLocation p_124870_) {
      return MultiVariantGenerator.multiVariant(p_124867_).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_)).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, p_124869_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124870_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, p_124868_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)));
   }

   private static BlockStateGenerator createOrientableTrapdoor(Block p_124889_, ResourceLocation p_124890_, ResourceLocation p_124891_, ResourceLocation p_124892_) {
      return MultiVariantGenerator.multiVariant(p_124889_).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124891_)).select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124891_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124891_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124891_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124890_)).select(Direction.SOUTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124890_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124890_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124890_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124892_)).select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.SOUTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)).select(Direction.EAST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.WEST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124892_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
   }

   private static BlockStateGenerator createTrapdoor(Block p_124909_, ResourceLocation p_124910_, ResourceLocation p_124911_, ResourceLocation p_124912_) {
      return MultiVariantGenerator.multiVariant(p_124909_).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124911_)).select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124911_)).select(Direction.EAST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124911_)).select(Direction.WEST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, p_124911_)).select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124910_)).select(Direction.SOUTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124910_)).select(Direction.EAST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124910_)).select(Direction.WEST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, p_124910_)).select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124912_)).select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124912_)).select(Direction.SOUTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, p_124912_).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
   }

   static MultiVariantGenerator createSimpleBlock(Block p_124860_, ResourceLocation p_124861_) {
      return MultiVariantGenerator.multiVariant(p_124860_, Variant.variant().with(VariantProperties.MODEL, p_124861_));
   }

   private static PropertyDispatch createRotatedPillar() {
      return PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant()).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
   }

   static BlockStateGenerator createAxisAlignedPillarBlock(Block p_124882_, ResourceLocation p_124883_) {
      return MultiVariantGenerator.multiVariant(p_124882_, Variant.variant().with(VariantProperties.MODEL, p_124883_)).with(createRotatedPillar());
   }

   private void createAxisAlignedPillarBlockCustomModel(Block p_124902_, ResourceLocation p_124903_) {
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(p_124902_, p_124903_));
   }

   private void createAxisAlignedPillarBlock(Block p_124587_, TexturedModel.Provider p_124588_) {
      ResourceLocation resourcelocation = p_124588_.create(p_124587_, this.modelOutput);
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(p_124587_, resourcelocation));
   }

   private void createHorizontallyRotatedBlock(Block p_124745_, TexturedModel.Provider p_124746_) {
      ResourceLocation resourcelocation = p_124746_.create(p_124745_, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124745_, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block p_124925_, ResourceLocation p_124926_, ResourceLocation p_124927_) {
      return MultiVariantGenerator.multiVariant(p_124925_).with(PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, p_124926_)).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, p_124927_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, p_124927_).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
   }

   private void createRotatedPillarWithHorizontalVariant(Block p_124590_, TexturedModel.Provider p_124591_, TexturedModel.Provider p_124592_) {
      ResourceLocation resourcelocation = p_124591_.create(p_124590_, this.modelOutput);
      ResourceLocation resourcelocation1 = p_124592_.create(p_124590_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedPillarWithHorizontalVariant(p_124590_, resourcelocation, resourcelocation1));
   }

   private ResourceLocation createSuffixedVariant(Block p_124579_, String p_124580_, ModelTemplate p_124581_, Function<ResourceLocation, TextureMapping> p_124582_) {
      return p_124581_.createWithSuffix(p_124579_, p_124580_, p_124582_.apply(TextureMapping.getBlockTexture(p_124579_, p_124580_)), this.modelOutput);
   }

   static BlockStateGenerator createPressurePlate(Block p_124942_, ResourceLocation p_124943_, ResourceLocation p_124944_) {
      return MultiVariantGenerator.multiVariant(p_124942_).with(createBooleanModelDispatch(BlockStateProperties.POWERED, p_124944_, p_124943_));
   }

   static BlockStateGenerator createSlab(Block p_124929_, ResourceLocation p_124930_, ResourceLocation p_124931_, ResourceLocation p_124932_) {
      return MultiVariantGenerator.multiVariant(p_124929_).with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, p_124930_)).select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, p_124931_)).select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, p_124932_)));
   }

   private void createTrivialCube(Block p_124852_) {
      this.createTrivialBlock(p_124852_, TexturedModel.CUBE);
   }

   private void createTrivialBlock(Block p_124795_, TexturedModel.Provider p_124796_) {
      this.blockStateOutput.accept(createSimpleBlock(p_124795_, p_124796_.create(p_124795_, this.modelOutput)));
   }

   private void createTrivialBlock(Block p_124568_, TextureMapping p_124569_, ModelTemplate p_124570_) {
      ResourceLocation resourcelocation = p_124570_.create(p_124568_, p_124569_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124568_, resourcelocation));
   }

   private BlockModelGenerators.BlockFamilyProvider family(Block p_124877_) {
      TexturedModel texturedmodel = this.texturedModels.getOrDefault(p_124877_, TexturedModel.CUBE.get(p_124877_));
      return (new BlockModelGenerators.BlockFamilyProvider(texturedmodel.getMapping())).fullBlock(p_124877_, texturedmodel.getTemplate());
   }

   void createDoor(Block p_124897_) {
      TextureMapping texturemapping = TextureMapping.door(p_124897_);
      ResourceLocation resourcelocation = ModelTemplates.DOOR_BOTTOM.create(p_124897_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.DOOR_BOTTOM_HINGE.create(p_124897_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.DOOR_TOP.create(p_124897_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.DOOR_TOP_HINGE.create(p_124897_, texturemapping, this.modelOutput);
      this.createSimpleFlatItemModel(p_124897_.asItem());
      this.blockStateOutput.accept(createDoor(p_124897_, resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3));
   }

   void createOrientableTrapdoor(Block p_124917_) {
      TextureMapping texturemapping = TextureMapping.defaultTexture(p_124917_);
      ResourceLocation resourcelocation = ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create(p_124917_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create(p_124917_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create(p_124917_, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(createOrientableTrapdoor(p_124917_, resourcelocation, resourcelocation1, resourcelocation2));
      this.delegateItemModel(p_124917_, resourcelocation1);
   }

   void createTrapdoor(Block p_124937_) {
      TextureMapping texturemapping = TextureMapping.defaultTexture(p_124937_);
      ResourceLocation resourcelocation = ModelTemplates.TRAPDOOR_TOP.create(p_124937_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.TRAPDOOR_BOTTOM.create(p_124937_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.TRAPDOOR_OPEN.create(p_124937_, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(createTrapdoor(p_124937_, resourcelocation, resourcelocation1, resourcelocation2));
      this.delegateItemModel(p_124937_, resourcelocation1);
   }

   private void createBigDripLeafBlock() {
      this.skipAutoItemBlock(Blocks.BIG_DRIPLEAF);
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_partial_tilt");
      ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_full_tilt");
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.BIG_DRIPLEAF).with(createHorizontalFacingDispatch()).with(PropertyDispatch.property(BlockStateProperties.TILT).select(Tilt.NONE, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(Tilt.UNSTABLE, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(Tilt.PARTIAL, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(Tilt.FULL, Variant.variant().with(VariantProperties.MODEL, resourcelocation2))));
   }

   private BlockModelGenerators.WoodProvider woodProvider(Block p_124949_) {
      return new BlockModelGenerators.WoodProvider(TextureMapping.logColumn(p_124949_));
   }

   private void createNonTemplateModelBlock(Block p_124961_) {
      this.createNonTemplateModelBlock(p_124961_, p_124961_);
   }

   private void createNonTemplateModelBlock(Block p_124534_, Block p_124535_) {
      this.blockStateOutput.accept(createSimpleBlock(p_124534_, ModelLocationUtils.getModelLocation(p_124535_)));
   }

   private void createCrossBlockWithDefaultItem(Block p_124558_, BlockModelGenerators.TintState p_124559_) {
      this.createSimpleFlatItemModel(p_124558_);
      this.createCrossBlock(p_124558_, p_124559_);
   }

   private void createCrossBlockWithDefaultItem(Block p_124561_, BlockModelGenerators.TintState p_124562_, TextureMapping p_124563_) {
      this.createSimpleFlatItemModel(p_124561_);
      this.createCrossBlock(p_124561_, p_124562_, p_124563_);
   }

   private void createCrossBlock(Block p_124738_, BlockModelGenerators.TintState p_124739_) {
      TextureMapping texturemapping = TextureMapping.cross(p_124738_);
      this.createCrossBlock(p_124738_, p_124739_, texturemapping);
   }

   private void createCrossBlock(Block p_124741_, BlockModelGenerators.TintState p_124742_, TextureMapping p_124743_) {
      ResourceLocation resourcelocation = p_124742_.getCross().create(p_124741_, p_124743_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124741_, resourcelocation));
   }

   private void createPlant(Block p_124546_, Block p_124547_, BlockModelGenerators.TintState p_124548_) {
      this.createCrossBlockWithDefaultItem(p_124546_, p_124548_);
      TextureMapping texturemapping = TextureMapping.plant(p_124546_);
      ResourceLocation resourcelocation = p_124548_.getCrossPot().create(p_124547_, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124547_, resourcelocation));
   }

   private void createCoralFans(Block p_124731_, Block p_124732_) {
      TexturedModel texturedmodel = TexturedModel.CORAL_FAN.get(p_124731_);
      ResourceLocation resourcelocation = texturedmodel.create(p_124731_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124731_, resourcelocation));
      ResourceLocation resourcelocation1 = ModelTemplates.CORAL_WALL_FAN.create(p_124732_, texturedmodel.getMapping(), this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124732_, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).with(createHorizontalFacingDispatch()));
      this.createSimpleFlatItemModel(p_124731_);
   }

   private void createStems(Block p_124789_, Block p_124790_) {
      this.createSimpleFlatItemModel(p_124789_.asItem());
      TextureMapping texturemapping = TextureMapping.stem(p_124789_);
      TextureMapping texturemapping1 = TextureMapping.attachedStem(p_124789_, p_124790_);
      ResourceLocation resourcelocation = ModelTemplates.ATTACHED_STEM.create(p_124790_, texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124790_, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.WEST, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124789_).with(PropertyDispatch.property(BlockStateProperties.AGE_7).generate((p_176108_) -> {
         return Variant.variant().with(VariantProperties.MODEL, ModelTemplates.STEMS[p_176108_].create(p_124789_, texturemapping, this.modelOutput));
      })));
   }

   private void createCoral(Block p_124537_, Block p_124538_, Block p_124539_, Block p_124540_, Block p_124541_, Block p_124542_, Block p_124543_, Block p_124544_) {
      this.createCrossBlockWithDefaultItem(p_124537_, BlockModelGenerators.TintState.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(p_124538_, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialCube(p_124539_);
      this.createTrivialCube(p_124540_);
      this.createCoralFans(p_124541_, p_124543_);
      this.createCoralFans(p_124542_, p_124544_);
   }

   private void createDoublePlant(Block p_124792_, BlockModelGenerators.TintState p_124793_) {
      this.createSimpleFlatItemModel(p_124792_, "_top");
      ResourceLocation resourcelocation = this.createSuffixedVariant(p_124792_, "_top", p_124793_.getCross(), TextureMapping::cross);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_124792_, "_bottom", p_124793_.getCross(), TextureMapping::cross);
      this.createDoubleBlock(p_124792_, resourcelocation, resourcelocation1);
   }

   private void createSunflower() {
      this.createSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.SUNFLOWER, "_top");
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.SUNFLOWER, "_bottom", BlockModelGenerators.TintState.NOT_TINTED.getCross(), TextureMapping::cross);
      this.createDoubleBlock(Blocks.SUNFLOWER, resourcelocation, resourcelocation1);
   }

   private void createTallSeagrass() {
      ResourceLocation resourcelocation = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture);
      this.createDoubleBlock(Blocks.TALL_SEAGRASS, resourcelocation, resourcelocation1);
   }

   private void createSmallDripleaf() {
      this.skipAutoItemBlock(Blocks.SMALL_DRIPLEAF);
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_top");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_bottom");
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SMALL_DRIPLEAF).with(createHorizontalFacingDispatch()).with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, resourcelocation))));
   }

   private void createDoubleBlock(Block p_124954_, ResourceLocation p_124955_, ResourceLocation p_124956_) {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124954_).with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, p_124956_)).select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, p_124955_))));
   }

   private void createPassiveRail(Block p_124969_) {
      TextureMapping texturemapping = TextureMapping.rail(p_124969_);
      TextureMapping texturemapping1 = TextureMapping.rail(TextureMapping.getBlockTexture(p_124969_, "_corner"));
      ResourceLocation resourcelocation = ModelTemplates.RAIL_FLAT.create(p_124969_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.RAIL_CURVED.create(p_124969_, texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.RAIL_RAISED_NE.create(p_124969_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.RAIL_RAISED_SW.create(p_124969_, texturemapping, this.modelOutput);
      this.createSimpleFlatItemModel(p_124969_);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124969_).with(PropertyDispatch.property(BlockStateProperties.RAIL_SHAPE).select(RailShape.NORTH_SOUTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(RailShape.EAST_WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_EAST, Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_NORTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation2)).select(RailShape.ASCENDING_SOUTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation3)).select(RailShape.SOUTH_EAST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(RailShape.SOUTH_WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.NORTH_WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(RailShape.NORTH_EAST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
   }

   private void createActiveRail(Block p_124975_) {
      ResourceLocation resourcelocation = this.createSuffixedVariant(p_124975_, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_124975_, "", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail);
      ResourceLocation resourcelocation2 = this.createSuffixedVariant(p_124975_, "", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail);
      ResourceLocation resourcelocation3 = this.createSuffixedVariant(p_124975_, "_on", ModelTemplates.RAIL_FLAT, TextureMapping::rail);
      ResourceLocation resourcelocation4 = this.createSuffixedVariant(p_124975_, "_on", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail);
      ResourceLocation resourcelocation5 = this.createSuffixedVariant(p_124975_, "_on", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail);
      PropertyDispatch propertydispatch = PropertyDispatch.properties(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((p_176166_, p_176167_) -> {
         switch(p_176167_) {
         case NORTH_SOUTH:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation3 : resourcelocation);
         case EAST_WEST:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation3 : resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         case ASCENDING_EAST:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation4 : resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         case ASCENDING_WEST:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation5 : resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         case ASCENDING_NORTH:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation4 : resourcelocation1);
         case ASCENDING_SOUTH:
            return Variant.variant().with(VariantProperties.MODEL, p_176166_ ? resourcelocation5 : resourcelocation2);
         default:
            throw new UnsupportedOperationException("Fix you generator!");
         }
      });
      this.createSimpleFlatItemModel(p_124975_);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124975_).with(propertydispatch));
   }

   private BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(ResourceLocation p_124691_, Block p_124692_) {
      return new BlockModelGenerators.BlockEntityModelGenerator(p_124691_, p_124692_);
   }

   private BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(Block p_124826_, Block p_124827_) {
      return new BlockModelGenerators.BlockEntityModelGenerator(ModelLocationUtils.getModelLocation(p_124826_), p_124827_);
   }

   private void createAirLikeBlock(Block p_124531_, Item p_124532_) {
      ResourceLocation resourcelocation = ModelTemplates.PARTICLE_ONLY.create(p_124531_, TextureMapping.particleFromItem(p_124532_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124531_, resourcelocation));
   }

   private void createAirLikeBlock(Block p_124922_, ResourceLocation p_124923_) {
      ResourceLocation resourcelocation = ModelTemplates.PARTICLE_ONLY.create(p_124922_, TextureMapping.particle(p_124923_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124922_, resourcelocation));
   }

   private void createFullAndCarpetBlocks(Block p_176218_, Block p_176219_) {
      this.createTrivialCube(p_176218_);
      ResourceLocation resourcelocation = TexturedModel.CARPET.get(p_176218_).create(p_176219_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_176219_, resourcelocation));
   }

   private void createColoredBlockWithRandomRotations(TexturedModel.Provider p_124686_, Block... p_124687_) {
      for(Block block : p_124687_) {
         ResourceLocation resourcelocation = p_124686_.create(block, this.modelOutput);
         this.blockStateOutput.accept(createRotatedVariant(block, resourcelocation));
      }

   }

   private void createColoredBlockWithStateRotations(TexturedModel.Provider p_124778_, Block... p_124779_) {
      for(Block block : p_124779_) {
         ResourceLocation resourcelocation = p_124778_.create(block, this.modelOutput);
         this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(createHorizontalFacingDispatchAlt()));
      }

   }

   private void createGlassBlocks(Block p_124879_, Block p_124880_) {
      this.createTrivialCube(p_124879_);
      TextureMapping texturemapping = TextureMapping.pane(p_124879_, p_124880_);
      ResourceLocation resourcelocation = ModelTemplates.STAINED_GLASS_PANE_POST.create(p_124880_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(p_124880_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(p_124880_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(p_124880_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(p_124880_, texturemapping, this.modelOutput);
      Item item = p_124880_.asItem();
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(p_124879_), this.modelOutput);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(p_124880_).with(Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation3)).with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation4)).with(Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
   }

   private void createCommandBlock(Block p_124978_) {
      TextureMapping texturemapping = TextureMapping.commandBlock(p_124978_);
      ResourceLocation resourcelocation = ModelTemplates.COMMAND_BLOCK.create(p_124978_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_124978_, "_conditional", ModelTemplates.COMMAND_BLOCK, (p_176193_) -> {
         return texturemapping.copyAndUpdate(TextureSlot.SIDE, p_176193_);
      });
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124978_).with(createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, resourcelocation1, resourcelocation)).with(createFacingDispatch()));
   }

   private void createAnvil(Block p_124981_) {
      ResourceLocation resourcelocation = TexturedModel.ANVIL.create(p_124981_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124981_, resourcelocation).with(createHorizontalFacingDispatchAlt()));
   }

   private List<Variant> createBambooModels(int p_124512_) {
      String s = "_age" + p_124512_;
      return IntStream.range(1, 5).mapToObj((p_176139_) -> {
         return Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, p_176139_ + s));
      }).collect(Collectors.toList());
   }

   private void createBamboo() {
      this.skipAutoItemBlock(Blocks.BAMBOO);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BAMBOO).with(Condition.condition().term(BlockStateProperties.AGE_1, 0), this.createBambooModels(0)).with(Condition.condition().term(BlockStateProperties.AGE_1, 1), this.createBambooModels(1)).with(Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_small_leaves"))).with(Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_large_leaves"))));
   }

   private PropertyDispatch createColumnWithFacing() {
      return PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(Direction.UP, Variant.variant()).select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
   }

   private void createBarrel() {
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.BARREL).with(this.createColumnWithFacing()).with(PropertyDispatch.property(BlockStateProperties.OPEN).select(false, Variant.variant().with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput))).select(true, Variant.variant().with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.BARREL).updateTextures((p_176216_) -> {
         p_176216_.put(TextureSlot.TOP, resourcelocation);
      }).createWithSuffix(Blocks.BARREL, "_open", this.modelOutput)))));
   }

   private static <T extends Comparable<T>> PropertyDispatch createEmptyOrFullDispatch(Property<T> p_124627_, T p_124628_, ResourceLocation p_124629_, ResourceLocation p_124630_) {
      Variant variant = Variant.variant().with(VariantProperties.MODEL, p_124629_);
      Variant variant1 = Variant.variant().with(VariantProperties.MODEL, p_124630_);
      return PropertyDispatch.property(p_124627_).generate((p_176130_) -> {
         boolean flag = p_176130_.compareTo(p_124628_) >= 0;
         return flag ? variant : variant1;
      });
   }

   private void createBeeNest(Block p_124584_, Function<Block, TextureMapping> p_124585_) {
      TextureMapping texturemapping = p_124585_.apply(p_124584_).copyForced(TextureSlot.SIDE, TextureSlot.PARTICLE);
      TextureMapping texturemapping1 = texturemapping.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(p_124584_, "_front_honey"));
      ResourceLocation resourcelocation = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(p_124584_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(p_124584_, "_honey", texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124584_).with(createHorizontalFacingDispatch()).with(createEmptyOrFullDispatch(BlockStateProperties.LEVEL_HONEY, 5, resourcelocation1, resourcelocation)));
   }

   private void createCropBlock(Block p_124554_, Property<Integer> p_124555_, int... p_124556_) {
      if (p_124555_.getPossibleValues().size() != p_124556_.length) {
         throw new IllegalArgumentException();
      } else {
         Int2ObjectMap<ResourceLocation> int2objectmap = new Int2ObjectOpenHashMap<>();
         PropertyDispatch propertydispatch = PropertyDispatch.property(p_124555_).generate((p_176172_) -> {
            int i = p_124556_[p_176172_];
            ResourceLocation resourcelocation = int2objectmap.computeIfAbsent(i, (p_176098_) -> {
               return this.createSuffixedVariant(p_124554_, "_stage" + i, ModelTemplates.CROP, TextureMapping::crop);
            });
            return Variant.variant().with(VariantProperties.MODEL, resourcelocation);
         });
         this.createSimpleFlatItemModel(p_124554_.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124554_).with(propertydispatch));
      }
   }

   private void createBell() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.BELL, "_floor");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_ceiling");
      ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_wall");
      ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_between_walls");
      this.createSimpleFlatItemModel(Items.BELL);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.BELL).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT).select(Direction.NORTH, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(Direction.SOUTH, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(Direction.SOUTH, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.SOUTH, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.EAST, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation2)).select(Direction.WEST, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.SOUTH, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation3)).select(Direction.WEST, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))));
   }

   private void createGrindstone() {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.GRINDSTONE, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.GRINDSTONE))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
   }

   private void createFurnace(Block p_124857_, TexturedModel.Provider p_124858_) {
      ResourceLocation resourcelocation = p_124858_.create(p_124857_, this.modelOutput);
      ResourceLocation resourcelocation1 = TextureMapping.getBlockTexture(p_124857_, "_front_on");
      ResourceLocation resourcelocation2 = p_124858_.get(p_124857_).updateTextures((p_176207_) -> {
         p_176207_.put(TextureSlot.FRONT, resourcelocation1);
      }).createWithSuffix(p_124857_, "_on", this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124857_).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation2, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   private void createCampfires(Block... p_124714_) {
      ResourceLocation resourcelocation = ModelLocationUtils.decorateBlockModelLocation("campfire_off");

      for(Block block : p_124714_) {
         ResourceLocation resourcelocation1 = ModelTemplates.CAMPFIRE.create(block, TextureMapping.campfire(block), this.modelOutput);
         this.createSimpleFlatItemModel(block.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation1, resourcelocation)).with(createHorizontalFacingDispatchAlt()));
      }

   }

   private void createAzalea(Block p_176248_) {
      ResourceLocation resourcelocation = ModelTemplates.AZALEA.create(p_176248_, TextureMapping.cubeTop(p_176248_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_176248_, resourcelocation));
   }

   private void createPottedAzalea(Block p_176250_) {
      ResourceLocation resourcelocation = ModelTemplates.POTTED_AZALEA.create(p_176250_, TextureMapping.cubeTop(p_176250_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_176250_, resourcelocation));
   }

   private void createBookshelf() {
      TextureMapping texturemapping = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.BOOKSHELF), TextureMapping.getBlockTexture(Blocks.OAK_PLANKS));
      ResourceLocation resourcelocation = ModelTemplates.CUBE_COLUMN.create(Blocks.BOOKSHELF, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.BOOKSHELF, resourcelocation));
   }

   private void createRedstoneWire() {
      this.createSimpleFlatItemModel(Items.REDSTONE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.REDSTONE_WIRE).with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE), Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_dot"))).with(Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side0"))).with(Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt0"))).with(Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt1")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).with(Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side1")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).with(Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))).with(Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).with(Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
   }

   private void createComparator() {
      this.createSimpleFlatItemModel(Items.COMPARATOR);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.COMPARATOR).with(createHorizontalFacingDispatchAlt()).with(PropertyDispatch.properties(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED).select(ComparatorMode.COMPARE, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR))).select(ComparatorMode.COMPARE, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on"))).select(ComparatorMode.SUBTRACT, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_subtract"))).select(ComparatorMode.SUBTRACT, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on_subtract")))));
   }

   private void createSmoothStoneSlab() {
      TextureMapping texturemapping = TextureMapping.cube(Blocks.SMOOTH_STONE);
      TextureMapping texturemapping1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), texturemapping.get(TextureSlot.TOP));
      ResourceLocation resourcelocation = ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(createSlab(Blocks.SMOOTH_STONE_SLAB, resourcelocation, resourcelocation1, resourcelocation2));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMOOTH_STONE, ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, texturemapping, this.modelOutput)));
   }

   private void createBrewingStand() {
      this.createSimpleFlatItemModel(Items.BREWING_STAND);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BREWING_STAND).with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))).with(Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))));
   }

   private void createMushroomBlock(Block p_124984_) {
      ResourceLocation resourcelocation = ModelTemplates.SINGLE_FACE.create(p_124984_, TextureMapping.defaultTexture(p_124984_), this.modelOutput);
      ResourceLocation resourcelocation1 = ModelLocationUtils.decorateBlockModelLocation("mushroom_block_inside");
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(p_124984_).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.DOWN, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, false)).with(Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, false)).with(Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, false)).with(Condition.condition().term(BlockStateProperties.UP, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, false)).with(Condition.condition().term(BlockStateProperties.DOWN, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, false)));
      this.delegateItemModel(p_124984_, TexturedModel.CUBE.createWithSuffix(p_124984_, "_inventory", this.modelOutput));
   }

   private void createCakeBlock() {
      this.createSimpleFlatItemModel(Items.CAKE);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.CAKE).with(PropertyDispatch.property(BlockStateProperties.BITES).select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE))).select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice1"))).select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice2"))).select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice3"))).select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice4"))).select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice5"))).select(6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice6")))));
   }

   private void createCartographyTable() {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.DARK_OAK_PLANKS)).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, ModelTemplates.CUBE.create(Blocks.CARTOGRAPHY_TABLE, texturemapping, this.modelOutput)));
   }

   private void createSmithingTable() {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom")).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMITHING_TABLE, ModelTemplates.CUBE.create(Blocks.SMITHING_TABLE, texturemapping, this.modelOutput)));
   }

   private void createCraftingTableLike(Block p_124550_, Block p_124551_, BiFunction<Block, Block, TextureMapping> p_124552_) {
      TextureMapping texturemapping = p_124552_.apply(p_124550_, p_124551_);
      this.blockStateOutput.accept(createSimpleBlock(p_124550_, ModelTemplates.CUBE.create(p_124550_, texturemapping, this.modelOutput)));
   }

   private void createPumpkins() {
      TextureMapping texturemapping = TextureMapping.column(Blocks.PUMPKIN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.PUMPKIN, ModelLocationUtils.getModelLocation(Blocks.PUMPKIN)));
      this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, texturemapping);
      this.createPumpkinVariant(Blocks.JACK_O_LANTERN, texturemapping);
   }

   private void createPumpkinVariant(Block p_124565_, TextureMapping p_124566_) {
      ResourceLocation resourcelocation = ModelTemplates.CUBE_ORIENTABLE.create(p_124565_, p_124566_.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(p_124565_)), this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124565_, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   private void createCauldrons() {
      this.createSimpleFlatItemModel(Items.CAULDRON);
      this.createNonTemplateModelBlock(Blocks.CAULDRON);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.LAVA_CAULDRON, ModelTemplates.CAULDRON_FULL.create(Blocks.LAVA_CAULDRON, TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.LAVA, "_still")), this.modelOutput)));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.WATER_CAULDRON).with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL).select(1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.WATER_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.WATER_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(3, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.WATER_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.POWDER_SNOW_CAULDRON).with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL).select(1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(3, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput)))));
   }

   private void createChorusFlower() {
      TextureMapping texturemapping = TextureMapping.defaultTexture(Blocks.CHORUS_FLOWER);
      ResourceLocation resourcelocation = ModelTemplates.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.CHORUS_FLOWER, "_dead", ModelTemplates.CHORUS_FLOWER, (p_176148_) -> {
         return texturemapping.copyAndUpdate(TextureSlot.TEXTURE, p_176148_);
      });
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.CHORUS_FLOWER).with(createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, resourcelocation1, resourcelocation)));
   }

   private void createDispenserBlock(Block p_124987_) {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(p_124987_, "_front"));
      TextureMapping texturemapping1 = (new TextureMapping()).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(p_124987_, "_front_vertical"));
      ResourceLocation resourcelocation = ModelTemplates.CUBE_ORIENTABLE.create(p_124987_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(p_124987_, texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124987_).with(PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(Direction.UP, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
   }

   private void createEndPortalFrame() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled");
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.END_PORTAL_FRAME).with(PropertyDispatch.property(BlockStateProperties.EYE).select(false, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(true, Variant.variant().with(VariantProperties.MODEL, resourcelocation1))).with(createHorizontalFacingDispatchAlt()));
   }

   private void createChorusPlant() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_side");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside");
      ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside1");
      ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside2");
      ResourceLocation resourcelocation4 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside3");
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.CHORUS_PLANT).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.DOWN, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2), Variant.variant().with(VariantProperties.MODEL, resourcelocation2), Variant.variant().with(VariantProperties.MODEL, resourcelocation3), Variant.variant().with(VariantProperties.MODEL, resourcelocation4)).with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.UP, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.DOWN, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)));
   }

   private void createComposter() {
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.COMPOSTER).with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7"))).with(Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
   }

   private void createAmethystCluster(Block p_176252_) {
      this.skipAutoItemBlock(p_176252_);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_176252_, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CROSS.create(p_176252_, TextureMapping.cross(p_176252_), this.modelOutput))).with(this.createColumnWithFacing()));
   }

   private void createAmethystClusters() {
      this.createAmethystCluster(Blocks.SMALL_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.MEDIUM_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.LARGE_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.AMETHYST_CLUSTER);
   }

   private void createPointedDripstone() {
      this.skipAutoItemBlock(Blocks.POINTED_DRIPSTONE);
      PropertyDispatch.C2<Direction, DripstoneThickness> c2 = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);

      for(DripstoneThickness dripstonethickness : DripstoneThickness.values()) {
         c2.select(Direction.UP, dripstonethickness, this.createPointedDripstoneVariant(Direction.UP, dripstonethickness));
      }

      for(DripstoneThickness dripstonethickness1 : DripstoneThickness.values()) {
         c2.select(Direction.DOWN, dripstonethickness1, this.createPointedDripstoneVariant(Direction.DOWN, dripstonethickness1));
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.POINTED_DRIPSTONE).with(c2));
   }

   private Variant createPointedDripstoneVariant(Direction p_176117_, DripstoneThickness p_176118_) {
      String s = "_" + p_176117_.getSerializedName() + "_" + p_176118_.getSerializedName();
      TextureMapping texturemapping = TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.POINTED_DRIPSTONE, s));
      return Variant.variant().with(VariantProperties.MODEL, ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(Blocks.POINTED_DRIPSTONE, s, texturemapping, this.modelOutput));
   }

   private void createNyliumBlock(Block p_124990_) {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.NETHERRACK)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(p_124990_)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(p_124990_, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(p_124990_, ModelTemplates.CUBE_BOTTOM_TOP.create(p_124990_, texturemapping, this.modelOutput)));
   }

   private void createDaylightDetector() {
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureSlot.SIDE, resourcelocation);
      TextureMapping texturemapping1 = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureSlot.SIDE, resourcelocation);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.DAYLIGHT_DETECTOR).with(PropertyDispatch.property(BlockStateProperties.INVERTED).select(false, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, texturemapping, this.modelOutput))).select(true, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.DAYLIGHT_DETECTOR.create(ModelLocationUtils.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), texturemapping1, this.modelOutput)))));
   }

   private void createRotatableColumn(Block p_124993_) {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124993_, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(p_124993_))).with(this.createColumnWithFacing()));
   }

   private void createLightningRod() {
      Block block = Blocks.LIGHTNING_ROD;
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block, "_on");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block))).with(this.createColumnWithFacing()).with(createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation, resourcelocation1)));
   }

   private void createFarmland() {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND));
      TextureMapping texturemapping1 = (new TextureMapping()).put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"));
      ResourceLocation resourcelocation = ModelTemplates.FARMLAND.create(Blocks.FARMLAND, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"), texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, resourcelocation1, resourcelocation)));
   }

   private List<ResourceLocation> createFloorFireModels(Block p_124996_) {
      ResourceLocation resourcelocation = ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation(p_124996_, "_floor0"), TextureMapping.fire0(p_124996_), this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation(p_124996_, "_floor1"), TextureMapping.fire1(p_124996_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1);
   }

   private List<ResourceLocation> createSideFireModels(Block p_124999_) {
      ResourceLocation resourcelocation = ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation(p_124999_, "_side0"), TextureMapping.fire0(p_124999_), this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation(p_124999_, "_side1"), TextureMapping.fire1(p_124999_), this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation(p_124999_, "_side_alt0"), TextureMapping.fire0(p_124999_), this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation(p_124999_, "_side_alt1"), TextureMapping.fire1(p_124999_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
   }

   private List<ResourceLocation> createTopFireModels(Block p_125002_) {
      ResourceLocation resourcelocation = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(p_125002_, "_up0"), TextureMapping.fire0(p_125002_), this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(p_125002_, "_up1"), TextureMapping.fire1(p_125002_), this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation(p_125002_, "_up_alt0"), TextureMapping.fire0(p_125002_), this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation(p_125002_, "_up_alt1"), TextureMapping.fire1(p_125002_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
   }

   private static List<Variant> wrapModels(List<ResourceLocation> p_124683_, UnaryOperator<Variant> p_124684_) {
      return p_124683_.stream().map((p_176238_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176238_);
      }).map(p_124684_).collect(Collectors.toList());
   }

   private void createFire() {
      Condition condition = Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false).term(BlockStateProperties.UP, false);
      List<ResourceLocation> list = this.createFloorFireModels(Blocks.FIRE);
      List<ResourceLocation> list1 = this.createSideFireModels(Blocks.FIRE);
      List<ResourceLocation> list2 = this.createTopFireModels(Blocks.FIRE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.FIRE).with(condition, wrapModels(list, (p_124894_) -> {
         return p_124894_;
      })).with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH, true), condition), wrapModels(list1, (p_176243_) -> {
         return p_176243_;
      })).with(Condition.or(Condition.condition().term(BlockStateProperties.EAST, true), condition), wrapModels(list1, (p_176240_) -> {
         return p_176240_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
      })).with(Condition.or(Condition.condition().term(BlockStateProperties.SOUTH, true), condition), wrapModels(list1, (p_176236_) -> {
         return p_176236_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
      })).with(Condition.or(Condition.condition().term(BlockStateProperties.WEST, true), condition), wrapModels(list1, (p_176232_) -> {
         return p_176232_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
      })).with(Condition.condition().term(BlockStateProperties.UP, true), wrapModels(list2, (p_176227_) -> {
         return p_176227_;
      })));
   }

   private void createSoulFire() {
      List<ResourceLocation> list = this.createFloorFireModels(Blocks.SOUL_FIRE);
      List<ResourceLocation> list1 = this.createSideFireModels(Blocks.SOUL_FIRE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.SOUL_FIRE).with(wrapModels(list, (p_176221_) -> {
         return p_176221_;
      })).with(wrapModels(list1, (p_176209_) -> {
         return p_176209_;
      })).with(wrapModels(list1, (p_176200_) -> {
         return p_176200_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
      })).with(wrapModels(list1, (p_176188_) -> {
         return p_176188_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
      })).with(wrapModels(list1, (p_176143_) -> {
         return p_176143_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
      })));
   }

   private void createLantern(Block p_125005_) {
      ResourceLocation resourcelocation = TexturedModel.LANTERN.create(p_125005_, this.modelOutput);
      ResourceLocation resourcelocation1 = TexturedModel.HANGING_LANTERN.create(p_125005_, this.modelOutput);
      this.createSimpleFlatItemModel(p_125005_.asItem());
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_125005_).with(createBooleanModelDispatch(BlockStateProperties.HANGING, resourcelocation1, resourcelocation)));
   }

   private void createFrostedIce() {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.FROSTED_ICE).with(PropertyDispatch.property(BlockStateProperties.AGE_3).select(0, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(1, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(2, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(3, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", ModelTemplates.CUBE_ALL, TextureMapping::cube)))));
   }

   private void createGrassBlocks() {
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.DIRT);
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.BOTTOM, resourcelocation).copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
      Variant variant = Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", texturemapping, this.modelOutput));
      this.createGrassLikeBlock(Blocks.GRASS_BLOCK, ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK), variant);
      ResourceLocation resourcelocation1 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.MYCELIUM).updateTextures((p_176198_) -> {
         p_176198_.put(TextureSlot.BOTTOM, resourcelocation);
      }).create(Blocks.MYCELIUM, this.modelOutput);
      this.createGrassLikeBlock(Blocks.MYCELIUM, resourcelocation1, variant);
      ResourceLocation resourcelocation2 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.PODZOL).updateTextures((p_176154_) -> {
         p_176154_.put(TextureSlot.BOTTOM, resourcelocation);
      }).create(Blocks.PODZOL, this.modelOutput);
      this.createGrassLikeBlock(Blocks.PODZOL, resourcelocation2, variant);
   }

   private void createGrassLikeBlock(Block p_124600_, ResourceLocation p_124601_, Variant p_124602_) {
      List<Variant> list = Arrays.asList(createRotatedVariants(p_124601_));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124600_).with(PropertyDispatch.property(BlockStateProperties.SNOWY).select(true, p_124602_).select(false, list)));
   }

   private void createCocoa() {
      this.createSimpleFlatItemModel(Items.COCOA_BEANS);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.COCOA).with(PropertyDispatch.property(BlockStateProperties.AGE_2).select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage0"))).select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage1"))).select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage2")))).with(createHorizontalFacingDispatchAlt()));
   }

   private void createDirtPath() {
      this.blockStateOutput.accept(createRotatedVariant(Blocks.DIRT_PATH, ModelLocationUtils.getModelLocation(Blocks.DIRT_PATH)));
   }

   private void createWeightedPressurePlate(Block p_124919_, Block p_124920_) {
      TextureMapping texturemapping = TextureMapping.defaultTexture(p_124920_);
      ResourceLocation resourcelocation = ModelTemplates.PRESSURE_PLATE_UP.create(p_124919_, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.PRESSURE_PLATE_DOWN.create(p_124919_, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124919_).with(createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, resourcelocation1, resourcelocation)));
   }

   private void createHopper() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.HOPPER);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.HOPPER, "_side");
      this.createSimpleFlatItemModel(Items.HOPPER);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.HOPPER).with(PropertyDispatch.property(BlockStateProperties.FACING_HOPPER).select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, resourcelocation1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
   }

   private void copyModel(Block p_124939_, Block p_124940_) {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(p_124939_);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124940_, Variant.variant().with(VariantProperties.MODEL, resourcelocation)));
      this.delegateItemModel(p_124940_, resourcelocation);
   }

   private void createIronBars() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post_ends");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post");
      ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap");
      ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap_alt");
      ResourceLocation resourcelocation4 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side");
      ResourceLocation resourcelocation5 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side_alt");
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.IRON_BARS).with(Variant.variant().with(VariantProperties.MODEL, resourcelocation)).with(Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).with(Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation2)).with(Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, resourcelocation3)).with(Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation5)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
      this.createSimpleFlatItemModel(Blocks.IRON_BARS);
   }

   private void createNonTemplateHorizontalBlock(Block p_125008_) {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_125008_, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(p_125008_))).with(createHorizontalFacingDispatch()));
   }

   private void createLever() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.LEVER);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on");
      this.createSimpleFlatItemModel(Blocks.LEVER);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.LEVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation, resourcelocation1)).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
   }

   private void createLilyPad() {
      this.createSimpleFlatItemModel(Blocks.LILY_PAD);
      this.blockStateOutput.accept(createRotatedVariant(Blocks.LILY_PAD, ModelLocationUtils.getModelLocation(Blocks.LILY_PAD)));
   }

   private void createNetherPortalBlock() {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.NETHER_PORTAL).with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ew")))));
   }

   private void createNetherrack() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.NETHERRACK, Variant.variant().with(VariantProperties.MODEL, resourcelocation), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, resourcelocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)));
   }

   private void createObserver() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.OBSERVER);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.OBSERVER, "_on");
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.OBSERVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation1, resourcelocation)).with(createFacingDispatch()));
   }

   private void createPistons() {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.PISTON, "_bottom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky");
      ResourceLocation resourcelocation1 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top");
      TextureMapping texturemapping1 = texturemapping.copyAndUpdate(TextureSlot.PLATFORM, resourcelocation);
      TextureMapping texturemapping2 = texturemapping.copyAndUpdate(TextureSlot.PLATFORM, resourcelocation1);
      ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(Blocks.PISTON, "_base");
      this.createPistonVariant(Blocks.PISTON, resourcelocation2, texturemapping2);
      this.createPistonVariant(Blocks.STICKY_PISTON, resourcelocation2, texturemapping1);
      ResourceLocation resourcelocation3 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.PISTON, "_inventory", texturemapping.copyAndUpdate(TextureSlot.TOP, resourcelocation1), this.modelOutput);
      ResourceLocation resourcelocation4 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.STICKY_PISTON, "_inventory", texturemapping.copyAndUpdate(TextureSlot.TOP, resourcelocation), this.modelOutput);
      this.delegateItemModel(Blocks.PISTON, resourcelocation3);
      this.delegateItemModel(Blocks.STICKY_PISTON, resourcelocation4);
   }

   private void createPistonVariant(Block p_124604_, ResourceLocation p_124605_, TextureMapping p_124606_) {
      ResourceLocation resourcelocation = ModelTemplates.PISTON.create(p_124604_, p_124606_, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124604_).with(createBooleanModelDispatch(BlockStateProperties.EXTENDED, p_124605_, resourcelocation)).with(createFacingDispatch()));
   }

   private void createPistonHeads() {
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      TextureMapping texturemapping1 = texturemapping.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
      TextureMapping texturemapping2 = texturemapping.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.PISTON_HEAD).with(PropertyDispatch.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE).select(false, PistonType.DEFAULT, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", texturemapping2, this.modelOutput))).select(false, PistonType.STICKY, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", texturemapping1, this.modelOutput))).select(true, PistonType.DEFAULT, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", texturemapping2, this.modelOutput))).select(true, PistonType.STICKY, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", texturemapping1, this.modelOutput)))).with(createFacingDispatch()));
   }

   private void createSculkSensor() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_inactive");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_active");
      this.delegateItemModel(Blocks.SCULK_SENSOR, resourcelocation);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SCULK_SENSOR).with(PropertyDispatch.property(BlockStateProperties.SCULK_SENSOR_PHASE).generate((p_176158_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176158_ == SculkSensorPhase.ACTIVE ? resourcelocation1 : resourcelocation);
      })));
   }

   private void createScaffolding() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_stable");
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_unstable");
      this.delegateItemModel(Blocks.SCAFFOLDING, resourcelocation);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SCAFFOLDING).with(createBooleanModelDispatch(BlockStateProperties.BOTTOM, resourcelocation1, resourcelocation)));
   }

   private void createCaveVines() {
      ResourceLocation resourcelocation = this.createSuffixedVariant(Blocks.CAVE_VINES, "", ModelTemplates.CROSS, TextureMapping::cross);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.CAVE_VINES, "_lit", ModelTemplates.CROSS, TextureMapping::cross);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, resourcelocation1, resourcelocation)));
      ResourceLocation resourcelocation2 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "", ModelTemplates.CROSS, TextureMapping::cross);
      ResourceLocation resourcelocation3 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "_lit", ModelTemplates.CROSS, TextureMapping::cross);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES_PLANT).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, resourcelocation3, resourcelocation2)));
   }

   private void createRedstoneLamp() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.REDSTONE_LAMP).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation1, resourcelocation)));
   }

   private void createNormalTorch(Block p_124951_, Block p_124952_) {
      TextureMapping texturemapping = TextureMapping.torch(p_124951_);
      this.blockStateOutput.accept(createSimpleBlock(p_124951_, ModelTemplates.TORCH.create(p_124951_, texturemapping, this.modelOutput)));
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_124952_, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.WALL_TORCH.create(p_124952_, texturemapping, this.modelOutput))).with(createTorchHorizontalDispatch()));
      this.createSimpleFlatItemModel(p_124951_);
      this.skipAutoItemBlock(p_124952_);
   }

   private void createRedstoneTorch() {
      TextureMapping texturemapping = TextureMapping.torch(Blocks.REDSTONE_TORCH);
      TextureMapping texturemapping1 = TextureMapping.torch(TextureMapping.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
      ResourceLocation resourcelocation = ModelTemplates.TORCH.create(Blocks.REDSTONE_TORCH, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.TORCH.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.REDSTONE_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation, resourcelocation1)));
      ResourceLocation resourcelocation2 = ModelTemplates.WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, texturemapping, this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.WALL_TORCH.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.REDSTONE_WALL_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation2, resourcelocation3)).with(createTorchHorizontalDispatch()));
      this.createSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
      this.skipAutoItemBlock(Blocks.REDSTONE_WALL_TORCH);
   }

   private void createRepeater() {
      this.createSimpleFlatItemModel(Items.REPEATER);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.REPEATER).with(PropertyDispatch.properties(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED).generate((p_176134_, p_176135_, p_176136_) -> {
         StringBuilder stringbuilder = new StringBuilder();
         stringbuilder.append('_').append((Object)p_176134_).append("tick");
         if (p_176136_) {
            stringbuilder.append("_on");
         }

         if (p_176135_) {
            stringbuilder.append("_locked");
         }

         return Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.REPEATER, stringbuilder.toString()));
      })).with(createHorizontalFacingDispatchAlt()));
   }

   private void createSeaPickle() {
      this.createSimpleFlatItemModel(Items.SEA_PICKLE);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SEA_PICKLE).with(PropertyDispatch.properties(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED).select(1, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("dead_sea_pickle")))).select(2, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_dead_sea_pickles")))).select(3, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_dead_sea_pickles")))).select(4, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_dead_sea_pickles")))).select(1, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("sea_pickle")))).select(2, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_sea_pickles")))).select(3, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_sea_pickles")))).select(4, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_sea_pickles"))))));
   }

   private void createSnowBlocks() {
      TextureMapping texturemapping = TextureMapping.cube(Blocks.SNOW);
      ResourceLocation resourcelocation = ModelTemplates.CUBE_ALL.create(Blocks.SNOW_BLOCK, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SNOW).with(PropertyDispatch.property(BlockStateProperties.LAYERS).generate((p_176151_) -> {
         return Variant.variant().with(VariantProperties.MODEL, p_176151_ < 8 ? ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height" + p_176151_ * 2) : resourcelocation);
      })));
      this.delegateItemModel(Blocks.SNOW, ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SNOW_BLOCK, resourcelocation));
   }

   private void createStonecutter() {
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.STONECUTTER, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.STONECUTTER))).with(createHorizontalFacingDispatch()));
   }

   private void createStructureBlock() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
      this.delegateItemModel(Blocks.STRUCTURE_BLOCK, resourcelocation);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.STRUCTURE_BLOCK).with(PropertyDispatch.property(BlockStateProperties.STRUCTUREBLOCK_MODE).generate((p_176115_) -> {
         return Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.STRUCTURE_BLOCK, "_" + p_176115_.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube));
      })));
   }

   private void createSweetBerryBush() {
      this.createSimpleFlatItemModel(Items.SWEET_BERRIES);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.SWEET_BERRY_BUSH).with(PropertyDispatch.property(BlockStateProperties.AGE_3).generate((p_176132_) -> {
         return Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + p_176132_, ModelTemplates.CROSS, TextureMapping::cross));
      })));
   }

   private void createTripwire() {
      this.createSimpleFlatItemModel(Items.STRING);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE).with(PropertyDispatch.properties(BlockStateProperties.ATTACHED, BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST).select(false, false, false, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(false, false, true, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))).select(false, false, false, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(false, false, false, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(false, true, true, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))).select(false, true, false, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(false, false, false, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(false, false, true, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(false, false, true, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(false, true, true, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))).select(false, true, false, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(false, false, true, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(false, true, true, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(false, true, true, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nsew"))).select(true, false, false, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, false, true, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))).select(true, false, false, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(true, true, false, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(true, false, false, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(true, true, true, false, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))).select(true, true, false, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(true, false, false, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(true, false, true, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(true, false, true, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, true, false, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(true, true, true, true, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))).select(true, true, false, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(true, false, true, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(true, true, true, false, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(true, true, true, true, true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew")))));
   }

   private void createTripwireHook() {
      this.createSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE_HOOK).with(PropertyDispatch.properties(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED).generate((p_176124_, p_176125_) -> {
         return Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.TRIPWIRE_HOOK, (p_176124_ ? "_attached" : "") + (p_176125_ ? "_on" : "")));
      })).with(createHorizontalFacingDispatch()));
   }

   private ResourceLocation createTurtleEggModel(int p_124514_, String p_124515_, TextureMapping p_124516_) {
      switch(p_124514_) {
      case 1:
         return ModelTemplates.TURTLE_EGG.create(ModelLocationUtils.decorateBlockModelLocation(p_124515_ + "turtle_egg"), p_124516_, this.modelOutput);
      case 2:
         return ModelTemplates.TWO_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("two_" + p_124515_ + "turtle_eggs"), p_124516_, this.modelOutput);
      case 3:
         return ModelTemplates.THREE_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("three_" + p_124515_ + "turtle_eggs"), p_124516_, this.modelOutput);
      case 4:
         return ModelTemplates.FOUR_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("four_" + p_124515_ + "turtle_eggs"), p_124516_, this.modelOutput);
      default:
         throw new UnsupportedOperationException();
      }
   }

   private ResourceLocation createTurtleEggModel(Integer p_124677_, Integer p_124678_) {
      switch(p_124678_) {
      case 0:
         return this.createTurtleEggModel(p_124677_, "", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG)));
      case 1:
         return this.createTurtleEggModel(p_124677_, "slightly_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked")));
      case 2:
         return this.createTurtleEggModel(p_124677_, "very_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
      default:
         throw new UnsupportedOperationException();
      }
   }

   private void createTurtleEgg() {
      this.createSimpleFlatItemModel(Items.TURTLE_EGG);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.TURTLE_EGG).with(PropertyDispatch.properties(BlockStateProperties.EGGS, BlockStateProperties.HATCH).generateList((p_176185_, p_176186_) -> {
         return Arrays.asList(createRotatedVariants(this.createTurtleEggModel(p_176185_, p_176186_)));
      })));
   }

   private void createMultiface(Block p_176086_) {
      this.createSimpleFlatItemModel(p_176086_);
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(p_176086_);
      MultiPartGenerator multipartgenerator = MultiPartGenerator.multiPart(p_176086_);
      Condition.TerminalCondition condition$terminalcondition = Util.make(Condition.condition(), (p_181456_) -> {
         MULTIFACE_GENERATOR.forEach((p_181460_, p_181461_) -> {
            if (p_176086_.defaultBlockState().hasProperty(p_181460_)) {
               p_181456_.term(p_181460_, false);
            }

         });
      });
      MULTIFACE_GENERATOR.forEach((p_181467_, p_181468_) -> {
         if (p_176086_.defaultBlockState().hasProperty(p_181467_)) {
            multipartgenerator.with(Condition.condition().term(p_181467_, true), p_181468_.apply(resourcelocation));
            multipartgenerator.with(condition$terminalcondition, p_181468_.apply(resourcelocation));
         }

      });
      this.blockStateOutput.accept(multipartgenerator);
   }

   private void createMagmaBlock() {
      this.blockStateOutput.accept(createSimpleBlock(Blocks.MAGMA_BLOCK, ModelTemplates.CUBE_ALL.create(Blocks.MAGMA_BLOCK, TextureMapping.cube(ModelLocationUtils.decorateBlockModelLocation("magma")), this.modelOutput)));
   }

   private void createShulkerBox(Block p_125011_) {
      this.createTrivialBlock(p_125011_, TexturedModel.PARTICLE_ONLY);
      ModelTemplates.SHULKER_BOX_INVENTORY.create(ModelLocationUtils.getModelLocation(p_125011_.asItem()), TextureMapping.particle(p_125011_), this.modelOutput);
   }

   private void createGrowingPlant(Block p_124734_, Block p_124735_, BlockModelGenerators.TintState p_124736_) {
      this.createCrossBlock(p_124734_, p_124736_);
      this.createCrossBlock(p_124735_, p_124736_);
   }

   private void createBedItem(Block p_124963_, Block p_124964_) {
      ModelTemplates.BED_INVENTORY.create(ModelLocationUtils.getModelLocation(p_124963_.asItem()), TextureMapping.particle(p_124964_), this.modelOutput);
   }

   private void createInfestedStone() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.STONE);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored");
      this.blockStateOutput.accept(createRotatedVariant(Blocks.INFESTED_STONE, resourcelocation, resourcelocation1));
      this.delegateItemModel(Blocks.INFESTED_STONE, resourcelocation);
   }

   private void createInfestedDeepslate() {
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE);
      ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE, "_mirrored");
      this.blockStateOutput.accept(createRotatedVariant(Blocks.INFESTED_DEEPSLATE, resourcelocation, resourcelocation1).with(createRotatedPillar()));
      this.delegateItemModel(Blocks.INFESTED_DEEPSLATE, resourcelocation);
   }

   private void createNetherRoots(Block p_124971_, Block p_124972_) {
      this.createCrossBlockWithDefaultItem(p_124971_, BlockModelGenerators.TintState.NOT_TINTED);
      TextureMapping texturemapping = TextureMapping.plant(TextureMapping.getBlockTexture(p_124971_, "_pot"));
      ResourceLocation resourcelocation = BlockModelGenerators.TintState.NOT_TINTED.getCrossPot().create(p_124972_, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_124972_, resourcelocation));
   }

   private void createRespawnAnchor() {
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
      ResourceLocation resourcelocation1 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
      ResourceLocation resourcelocation2 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
      ResourceLocation[] aresourcelocation = new ResourceLocation[5];

      for(int i = 0; i < 5; ++i) {
         TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.BOTTOM, resourcelocation).put(TextureSlot.TOP, i == 0 ? resourcelocation1 : resourcelocation2).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + i));
         aresourcelocation[i] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + i, texturemapping, this.modelOutput);
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.RESPAWN_ANCHOR).with(PropertyDispatch.property(BlockStateProperties.RESPAWN_ANCHOR_CHARGES).generate((p_176175_) -> {
         return Variant.variant().with(VariantProperties.MODEL, aresourcelocation[p_176175_]);
      })));
      this.delegateItemModel(Items.RESPAWN_ANCHOR, aresourcelocation[0]);
   }

   private Variant applyRotation(FrontAndTop p_124636_, Variant p_124637_) {
      switch(p_124636_) {
      case DOWN_NORTH:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90);
      case DOWN_SOUTH:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
      case DOWN_WEST:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
      case DOWN_EAST:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
      case UP_NORTH:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
      case UP_SOUTH:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270);
      case UP_WEST:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
      case UP_EAST:
         return p_124637_.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
      case NORTH_UP:
         return p_124637_;
      case SOUTH_UP:
         return p_124637_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
      case WEST_UP:
         return p_124637_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
      case EAST_UP:
         return p_124637_.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
      default:
         throw new UnsupportedOperationException("Rotation " + p_124636_ + " can't be expressed with existing x and y values");
      }
   }

   private void createJigsaw() {
      ResourceLocation resourcelocation = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_top");
      ResourceLocation resourcelocation1 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_bottom");
      ResourceLocation resourcelocation2 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_side");
      ResourceLocation resourcelocation3 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_lock");
      TextureMapping texturemapping = (new TextureMapping()).put(TextureSlot.DOWN, resourcelocation2).put(TextureSlot.WEST, resourcelocation2).put(TextureSlot.EAST, resourcelocation2).put(TextureSlot.PARTICLE, resourcelocation).put(TextureSlot.NORTH, resourcelocation).put(TextureSlot.SOUTH, resourcelocation1).put(TextureSlot.UP, resourcelocation3);
      ResourceLocation resourcelocation4 = ModelTemplates.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, texturemapping, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.JIGSAW, Variant.variant().with(VariantProperties.MODEL, resourcelocation4)).with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate((p_176120_) -> {
         return this.applyRotation(p_176120_, Variant.variant());
      })));
   }

   private void createPetrifiedOakSlab() {
      Block block = Blocks.OAK_PLANKS;
      ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block);
      TexturedModel texturedmodel = TexturedModel.CUBE.get(block);
      Block block1 = Blocks.PETRIFIED_OAK_SLAB;
      ResourceLocation resourcelocation1 = ModelTemplates.SLAB_BOTTOM.create(block1, texturedmodel.getMapping(), this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.SLAB_TOP.create(block1, texturedmodel.getMapping(), this.modelOutput);
      this.blockStateOutput.accept(createSlab(block1, resourcelocation1, resourcelocation2, resourcelocation));
   }

   public void run() {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach((p_176122_) -> {
         this.family(p_176122_.getBaseBlock()).generateFor(p_176122_);
      });
      this.family(Blocks.CUT_COPPER).generateFor(BlockFamilies.CUT_COPPER).fullBlockCopies(Blocks.WAXED_CUT_COPPER).generateFor(BlockFamilies.WAXED_CUT_COPPER);
      this.family(Blocks.EXPOSED_CUT_COPPER).generateFor(BlockFamilies.EXPOSED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_EXPOSED_CUT_COPPER).generateFor(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
      this.family(Blocks.WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WEATHERED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
      this.family(Blocks.OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.OXIDIZED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
      this.createNonTemplateModelBlock(Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.CAVE_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.VOID_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.BEACON);
      this.createNonTemplateModelBlock(Blocks.CACTUS);
      this.createNonTemplateModelBlock(Blocks.BUBBLE_COLUMN, Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.DRAGON_EGG);
      this.createNonTemplateModelBlock(Blocks.DRIED_KELP_BLOCK);
      this.createNonTemplateModelBlock(Blocks.ENCHANTING_TABLE);
      this.createNonTemplateModelBlock(Blocks.FLOWER_POT);
      this.createSimpleFlatItemModel(Items.FLOWER_POT);
      this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
      this.createNonTemplateModelBlock(Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.LAVA);
      this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
      this.createSimpleFlatItemModel(Items.CHAIN);
      this.createCandleAndCandleCake(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.CANDLE, Blocks.CANDLE_CAKE);
      this.createNonTemplateModelBlock(Blocks.POTTED_BAMBOO);
      this.createNonTemplateModelBlock(Blocks.POTTED_CACTUS);
      this.createNonTemplateModelBlock(Blocks.POWDER_SNOW);
      this.createNonTemplateModelBlock(Blocks.SPORE_BLOSSOM);
      this.createAzalea(Blocks.AZALEA);
      this.createAzalea(Blocks.FLOWERING_AZALEA);
      this.createPottedAzalea(Blocks.POTTED_AZALEA);
      this.createPottedAzalea(Blocks.POTTED_FLOWERING_AZALEA);
      this.createCaveVines();
      this.createFullAndCarpetBlocks(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
      this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
      this.createSimpleFlatItemModel(Items.BARRIER);
      this.createLightBlock();
      this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
      this.createSimpleFlatItemModel(Items.STRUCTURE_VOID);
      this.createAirLikeBlock(Blocks.MOVING_PISTON, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      this.createTrivialCube(Blocks.COAL_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_COAL_ORE);
      this.createTrivialCube(Blocks.COAL_BLOCK);
      this.createTrivialCube(Blocks.DIAMOND_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_DIAMOND_ORE);
      this.createTrivialCube(Blocks.DIAMOND_BLOCK);
      this.createTrivialCube(Blocks.EMERALD_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_EMERALD_ORE);
      this.createTrivialCube(Blocks.EMERALD_BLOCK);
      this.createTrivialCube(Blocks.GOLD_ORE);
      this.createTrivialCube(Blocks.NETHER_GOLD_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_GOLD_ORE);
      this.createTrivialCube(Blocks.GOLD_BLOCK);
      this.createTrivialCube(Blocks.IRON_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_IRON_ORE);
      this.createTrivialCube(Blocks.IRON_BLOCK);
      this.createTrivialBlock(Blocks.ANCIENT_DEBRIS, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.NETHERITE_BLOCK);
      this.createTrivialCube(Blocks.LAPIS_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_LAPIS_ORE);
      this.createTrivialCube(Blocks.LAPIS_BLOCK);
      this.createTrivialCube(Blocks.NETHER_QUARTZ_ORE);
      this.createTrivialCube(Blocks.REDSTONE_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_REDSTONE_ORE);
      this.createTrivialCube(Blocks.REDSTONE_BLOCK);
      this.createTrivialCube(Blocks.GILDED_BLACKSTONE);
      this.createTrivialCube(Blocks.BLUE_ICE);
      this.createTrivialCube(Blocks.CLAY);
      this.createTrivialCube(Blocks.COARSE_DIRT);
      this.createTrivialCube(Blocks.CRYING_OBSIDIAN);
      this.createTrivialCube(Blocks.END_STONE);
      this.createTrivialCube(Blocks.GLOWSTONE);
      this.createTrivialCube(Blocks.GRAVEL);
      this.createTrivialCube(Blocks.HONEYCOMB_BLOCK);
      this.createTrivialCube(Blocks.ICE);
      this.createTrivialBlock(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
      this.createTrivialBlock(Blocks.LODESTONE, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.MELON, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.NETHER_WART_BLOCK);
      this.createTrivialCube(Blocks.NOTE_BLOCK);
      this.createTrivialCube(Blocks.PACKED_ICE);
      this.createTrivialCube(Blocks.OBSIDIAN);
      this.createTrivialCube(Blocks.QUARTZ_BRICKS);
      this.createTrivialCube(Blocks.SEA_LANTERN);
      this.createTrivialCube(Blocks.SHROOMLIGHT);
      this.createTrivialCube(Blocks.SOUL_SAND);
      this.createTrivialCube(Blocks.SOUL_SOIL);
      this.createTrivialCube(Blocks.SPAWNER);
      this.createTrivialCube(Blocks.SPONGE);
      this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
      this.createSimpleFlatItemModel(Items.SEAGRASS);
      this.createTrivialBlock(Blocks.TNT, TexturedModel.CUBE_TOP_BOTTOM);
      this.createTrivialBlock(Blocks.TARGET, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.WARPED_WART_BLOCK);
      this.createTrivialCube(Blocks.WET_SPONGE);
      this.createTrivialCube(Blocks.AMETHYST_BLOCK);
      this.createTrivialCube(Blocks.BUDDING_AMETHYST);
      this.createTrivialCube(Blocks.CALCITE);
      this.createTrivialCube(Blocks.TUFF);
      this.createTrivialCube(Blocks.DRIPSTONE_BLOCK);
      this.createTrivialCube(Blocks.RAW_IRON_BLOCK);
      this.createTrivialCube(Blocks.RAW_COPPER_BLOCK);
      this.createTrivialCube(Blocks.RAW_GOLD_BLOCK);
      this.createPetrifiedOakSlab();
      this.createTrivialCube(Blocks.COPPER_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_COPPER_ORE);
      this.createTrivialCube(Blocks.COPPER_BLOCK);
      this.createTrivialCube(Blocks.EXPOSED_COPPER);
      this.createTrivialCube(Blocks.WEATHERED_COPPER);
      this.createTrivialCube(Blocks.OXIDIZED_COPPER);
      this.copyModel(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
      this.copyModel(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
      this.copyModel(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
      this.copyModel(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
      this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
      this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
      this.createAmethystClusters();
      this.createBookshelf();
      this.createBrewingStand();
      this.createCakeBlock();
      this.createCampfires(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
      this.createCartographyTable();
      this.createCauldrons();
      this.createChorusFlower();
      this.createChorusPlant();
      this.createComposter();
      this.createDaylightDetector();
      this.createEndPortalFrame();
      this.createRotatableColumn(Blocks.END_ROD);
      this.createLightningRod();
      this.createFarmland();
      this.createFire();
      this.createSoulFire();
      this.createFrostedIce();
      this.createGrassBlocks();
      this.createCocoa();
      this.createDirtPath();
      this.createGrindstone();
      this.createHopper();
      this.createIronBars();
      this.createLever();
      this.createLilyPad();
      this.createNetherPortalBlock();
      this.createNetherrack();
      this.createObserver();
      this.createPistons();
      this.createPistonHeads();
      this.createScaffolding();
      this.createRedstoneTorch();
      this.createRedstoneLamp();
      this.createRepeater();
      this.createSeaPickle();
      this.createSmithingTable();
      this.createSnowBlocks();
      this.createStonecutter();
      this.createStructureBlock();
      this.createSweetBerryBush();
      this.createTripwire();
      this.createTripwireHook();
      this.createTurtleEgg();
      this.createMultiface(Blocks.VINE);
      this.createMultiface(Blocks.GLOW_LICHEN);
      this.createMagmaBlock();
      this.createJigsaw();
      this.createSculkSensor();
      this.createNonTemplateHorizontalBlock(Blocks.LADDER);
      this.createSimpleFlatItemModel(Blocks.LADDER);
      this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
      this.createBigDripLeafBlock();
      this.createNonTemplateHorizontalBlock(Blocks.BIG_DRIPLEAF_STEM);
      this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
      this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
      this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMapping::craftingTable);
      this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMapping::fletchingTable);
      this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
      this.createNyliumBlock(Blocks.WARPED_NYLIUM);
      this.createDispenserBlock(Blocks.DISPENSER);
      this.createDispenserBlock(Blocks.DROPPER);
      this.createLantern(Blocks.LANTERN);
      this.createLantern(Blocks.SOUL_LANTERN);
      this.createAxisAlignedPillarBlockCustomModel(Blocks.CHAIN, ModelLocationUtils.getModelLocation(Blocks.CHAIN));
      this.createAxisAlignedPillarBlock(Blocks.BASALT, TexturedModel.COLUMN);
      this.createAxisAlignedPillarBlock(Blocks.POLISHED_BASALT, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.SMOOTH_BASALT);
      this.createAxisAlignedPillarBlock(Blocks.BONE_BLOCK, TexturedModel.COLUMN);
      this.createRotatedVariantBlock(Blocks.DIRT);
      this.createRotatedVariantBlock(Blocks.ROOTED_DIRT);
      this.createRotatedVariantBlock(Blocks.SAND);
      this.createRotatedVariantBlock(Blocks.RED_SAND);
      this.createRotatedMirroredVariantBlock(Blocks.BEDROCK);
      this.createRotatedPillarWithHorizontalVariant(Blocks.HAY_BLOCK, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createRotatedPillarWithHorizontalVariant(Blocks.PURPUR_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createRotatedPillarWithHorizontalVariant(Blocks.QUARTZ_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createHorizontallyRotatedBlock(Blocks.LOOM, TexturedModel.ORIENTABLE);
      this.createPumpkins();
      this.createBeeNest(Blocks.BEE_NEST, TextureMapping::orientableCube);
      this.createBeeNest(Blocks.BEEHIVE, TextureMapping::orientableCubeSameEnds);
      this.createCropBlock(Blocks.BEETROOTS, BlockStateProperties.AGE_3, 0, 1, 2, 3);
      this.createCropBlock(Blocks.CARROTS, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.NETHER_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
      this.createCropBlock(Blocks.POTATOES, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.WHEAT, BlockStateProperties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("banner"), Blocks.OAK_PLANKS).createWithCustomBlockItemModel(ModelTemplates.BANNER_INVENTORY, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER).createWithoutBlockItem(Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("bed"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED, Blocks.RED_BED, Blocks.BLACK_BED);
      this.createBedItem(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
      this.createBedItem(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
      this.createBedItem(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      this.createBedItem(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      this.createBedItem(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
      this.createBedItem(Blocks.LIME_BED, Blocks.LIME_WOOL);
      this.createBedItem(Blocks.PINK_BED, Blocks.PINK_WOOL);
      this.createBedItem(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
      this.createBedItem(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      this.createBedItem(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
      this.createBedItem(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
      this.createBedItem(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
      this.createBedItem(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
      this.createBedItem(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
      this.createBedItem(Blocks.RED_BED, Blocks.RED_WOOL);
      this.createBedItem(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("skull"), Blocks.SOUL_SAND).createWithCustomBlockItemModel(ModelTemplates.SKULL_INVENTORY, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL).create(Blocks.DRAGON_HEAD).createWithoutBlockItem(Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL);
      this.createShulkerBox(Blocks.SHULKER_BOX);
      this.createShulkerBox(Blocks.WHITE_SHULKER_BOX);
      this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX);
      this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIME_SHULKER_BOX);
      this.createShulkerBox(Blocks.PINK_SHULKER_BOX);
      this.createShulkerBox(Blocks.GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.CYAN_SHULKER_BOX);
      this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BROWN_SHULKER_BOX);
      this.createShulkerBox(Blocks.GREEN_SHULKER_BOX);
      this.createShulkerBox(Blocks.RED_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLACK_SHULKER_BOX);
      this.createTrivialBlock(Blocks.CONDUIT, TexturedModel.PARTICLE_ONLY);
      this.skipAutoItemBlock(Blocks.CONDUIT);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("chest"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("ender_chest"), Blocks.OBSIDIAN).createWithoutBlockItem(Blocks.ENDER_CHEST);
      this.blockEntityModels(Blocks.END_PORTAL, Blocks.OBSIDIAN).create(Blocks.END_PORTAL, Blocks.END_GATEWAY);
      this.createTrivialCube(Blocks.AZALEA_LEAVES);
      this.createTrivialCube(Blocks.FLOWERING_AZALEA_LEAVES);
      this.createTrivialCube(Blocks.WHITE_CONCRETE);
      this.createTrivialCube(Blocks.ORANGE_CONCRETE);
      this.createTrivialCube(Blocks.MAGENTA_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_BLUE_CONCRETE);
      this.createTrivialCube(Blocks.YELLOW_CONCRETE);
      this.createTrivialCube(Blocks.LIME_CONCRETE);
      this.createTrivialCube(Blocks.PINK_CONCRETE);
      this.createTrivialCube(Blocks.GRAY_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_GRAY_CONCRETE);
      this.createTrivialCube(Blocks.CYAN_CONCRETE);
      this.createTrivialCube(Blocks.PURPLE_CONCRETE);
      this.createTrivialCube(Blocks.BLUE_CONCRETE);
      this.createTrivialCube(Blocks.BROWN_CONCRETE);
      this.createTrivialCube(Blocks.GREEN_CONCRETE);
      this.createTrivialCube(Blocks.RED_CONCRETE);
      this.createTrivialCube(Blocks.BLACK_CONCRETE);
      this.createColoredBlockWithRandomRotations(TexturedModel.CUBE, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
      this.createTrivialCube(Blocks.TERRACOTTA);
      this.createTrivialCube(Blocks.WHITE_TERRACOTTA);
      this.createTrivialCube(Blocks.ORANGE_TERRACOTTA);
      this.createTrivialCube(Blocks.MAGENTA_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.YELLOW_TERRACOTTA);
      this.createTrivialCube(Blocks.LIME_TERRACOTTA);
      this.createTrivialCube(Blocks.PINK_TERRACOTTA);
      this.createTrivialCube(Blocks.GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.CYAN_TERRACOTTA);
      this.createTrivialCube(Blocks.PURPLE_TERRACOTTA);
      this.createTrivialCube(Blocks.BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.BROWN_TERRACOTTA);
      this.createTrivialCube(Blocks.GREEN_TERRACOTTA);
      this.createTrivialCube(Blocks.RED_TERRACOTTA);
      this.createTrivialCube(Blocks.BLACK_TERRACOTTA);
      this.createTrivialCube(Blocks.TINTED_GLASS);
      this.createGlassBlocks(Blocks.GLASS, Blocks.GLASS_PANE);
      this.createGlassBlocks(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
      this.createColoredBlockWithStateRotations(TexturedModel.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
      this.createFullAndCarpetBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
      this.createFullAndCarpetBlocks(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
      this.createFullAndCarpetBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
      this.createFullAndCarpetBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.RED_WOOL, Blocks.RED_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
      this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, BlockModelGenerators.TintState.TINTED);
      this.createPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.POPPY, Blocks.POTTED_POPPY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPointedDripstone();
      this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.MUSHROOM_STEM);
      this.createCrossBlockWithDefaultItem(Blocks.GRASS, BlockModelGenerators.TintState.TINTED);
      this.createCrossBlock(Blocks.SUGAR_CANE, BlockModelGenerators.TintState.TINTED);
      this.createSimpleFlatItemModel(Items.SUGAR_CANE);
      this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, BlockModelGenerators.TintState.TINTED);
      this.createSimpleFlatItemModel(Items.KELP);
      this.skipAutoItemBlock(Blocks.KELP_PLANT);
      this.createCrossBlock(Blocks.HANGING_ROOTS, BlockModelGenerators.TintState.NOT_TINTED);
      this.skipAutoItemBlock(Blocks.HANGING_ROOTS);
      this.skipAutoItemBlock(Blocks.CAVE_VINES_PLANT);
      this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockModelGenerators.TintState.NOT_TINTED);
      this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, BlockModelGenerators.TintState.NOT_TINTED);
      this.createSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.WEEPING_VINES_PLANT);
      this.createSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.TWISTING_VINES_PLANT);
      this.createCrossBlockWithDefaultItem(Blocks.BAMBOO_SAPLING, BlockModelGenerators.TintState.TINTED, TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.BAMBOO, "_stage0")));
      this.createBamboo();
      this.createCrossBlockWithDefaultItem(Blocks.COBWEB, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.LILAC, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.ROSE_BUSH, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.PEONY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.TALL_GRASS, BlockModelGenerators.TintState.TINTED);
      this.createDoublePlant(Blocks.LARGE_FERN, BlockModelGenerators.TintState.TINTED);
      this.createSunflower();
      this.createTallSeagrass();
      this.createSmallDripleaf();
      this.createCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
      this.createCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
      this.createCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
      this.createCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
      this.createCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
      this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
      this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
      this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
      this.createPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
      this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
      this.createPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
      this.createPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
      this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
      this.createPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.createPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
      this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
      this.createPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.createPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
      this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
      this.createPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
      this.createCrossBlock(Blocks.NETHER_SPROUTS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createSimpleFlatItemModel(Items.NETHER_SPROUTS);
      this.createDoor(Blocks.IRON_DOOR);
      this.createTrapdoor(Blocks.IRON_TRAPDOOR);
      this.createSmoothStoneSlab();
      this.createPassiveRail(Blocks.RAIL);
      this.createActiveRail(Blocks.POWERED_RAIL);
      this.createActiveRail(Blocks.DETECTOR_RAIL);
      this.createActiveRail(Blocks.ACTIVATOR_RAIL);
      this.createComparator();
      this.createCommandBlock(Blocks.COMMAND_BLOCK);
      this.createCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
      this.createCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
      this.createAnvil(Blocks.ANVIL);
      this.createAnvil(Blocks.CHIPPED_ANVIL);
      this.createAnvil(Blocks.DAMAGED_ANVIL);
      this.createBarrel();
      this.createBell();
      this.createFurnace(Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.SMOKER, TexturedModel.ORIENTABLE);
      this.createRedstoneWire();
      this.createRespawnAnchor();
      this.copyModel(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
      this.copyModel(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
      this.copyModel(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
      this.copyModel(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
      this.createInfestedStone();
      this.copyModel(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
      this.createInfestedDeepslate();
      SpawnEggItem.eggs().forEach((p_176094_) -> {
         this.delegateItemModel(p_176094_, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
      });
   }

   private void createLightBlock() {
      this.skipAutoItemBlock(Blocks.LIGHT);
      PropertyDispatch.C1<Integer> c1 = PropertyDispatch.property(BlockStateProperties.LEVEL);

      for(int i = 0; i < 16; ++i) {
         String s = String.format("_%02d", i);
         ResourceLocation resourcelocation = TextureMapping.getItemTexture(Items.LIGHT, s);
         c1.select(i, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PARTICLE_ONLY.createWithSuffix(Blocks.LIGHT, s, TextureMapping.particle(resourcelocation), this.modelOutput)));
         ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(Items.LIGHT, s), TextureMapping.layer0(resourcelocation), this.modelOutput);
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.LIGHT).with(c1));
   }

   private void createCandleAndCandleCake(Block p_176245_, Block p_176246_) {
      this.createSimpleFlatItemModel(p_176245_.asItem());
      TextureMapping texturemapping = TextureMapping.cube(TextureMapping.getBlockTexture(p_176245_));
      TextureMapping texturemapping1 = TextureMapping.cube(TextureMapping.getBlockTexture(p_176245_, "_lit"));
      ResourceLocation resourcelocation = ModelTemplates.CANDLE.createWithSuffix(p_176245_, "_one_candle", texturemapping, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTemplates.TWO_CANDLES.createWithSuffix(p_176245_, "_two_candles", texturemapping, this.modelOutput);
      ResourceLocation resourcelocation2 = ModelTemplates.THREE_CANDLES.createWithSuffix(p_176245_, "_three_candles", texturemapping, this.modelOutput);
      ResourceLocation resourcelocation3 = ModelTemplates.FOUR_CANDLES.createWithSuffix(p_176245_, "_four_candles", texturemapping, this.modelOutput);
      ResourceLocation resourcelocation4 = ModelTemplates.CANDLE.createWithSuffix(p_176245_, "_one_candle_lit", texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation5 = ModelTemplates.TWO_CANDLES.createWithSuffix(p_176245_, "_two_candles_lit", texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation6 = ModelTemplates.THREE_CANDLES.createWithSuffix(p_176245_, "_three_candles_lit", texturemapping1, this.modelOutput);
      ResourceLocation resourcelocation7 = ModelTemplates.FOUR_CANDLES.createWithSuffix(p_176245_, "_four_candles_lit", texturemapping1, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_176245_).with(PropertyDispatch.properties(BlockStateProperties.CANDLES, BlockStateProperties.LIT).select(1, false, Variant.variant().with(VariantProperties.MODEL, resourcelocation)).select(2, false, Variant.variant().with(VariantProperties.MODEL, resourcelocation1)).select(3, false, Variant.variant().with(VariantProperties.MODEL, resourcelocation2)).select(4, false, Variant.variant().with(VariantProperties.MODEL, resourcelocation3)).select(1, true, Variant.variant().with(VariantProperties.MODEL, resourcelocation4)).select(2, true, Variant.variant().with(VariantProperties.MODEL, resourcelocation5)).select(3, true, Variant.variant().with(VariantProperties.MODEL, resourcelocation6)).select(4, true, Variant.variant().with(VariantProperties.MODEL, resourcelocation7))));
      ResourceLocation resourcelocation8 = ModelTemplates.CANDLE_CAKE.create(p_176246_, TextureMapping.candleCake(p_176245_, false), this.modelOutput);
      ResourceLocation resourcelocation9 = ModelTemplates.CANDLE_CAKE.createWithSuffix(p_176246_, "_lit", TextureMapping.candleCake(p_176245_, true), this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(p_176246_).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation9, resourcelocation8)));
   }

   class BlockEntityModelGenerator {
      private final ResourceLocation baseModel;

      public BlockEntityModelGenerator(ResourceLocation p_125020_, Block p_125021_) {
         this.baseModel = ModelTemplates.PARTICLE_ONLY.create(p_125020_, TextureMapping.particle(p_125021_), BlockModelGenerators.this.modelOutput);
      }

      public BlockModelGenerators.BlockEntityModelGenerator create(Block... p_125026_) {
         for(Block block : p_125026_) {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, this.baseModel));
         }

         return this;
      }

      public BlockModelGenerators.BlockEntityModelGenerator createWithoutBlockItem(Block... p_125028_) {
         for(Block block : p_125028_) {
            BlockModelGenerators.this.skipAutoItemBlock(block);
         }

         return this.create(p_125028_);
      }

      public BlockModelGenerators.BlockEntityModelGenerator createWithCustomBlockItemModel(ModelTemplate p_125023_, Block... p_125024_) {
         for(Block block : p_125024_) {
            p_125023_.create(ModelLocationUtils.getModelLocation(block.asItem()), TextureMapping.particle(block), BlockModelGenerators.this.modelOutput);
         }

         return this.create(p_125024_);
      }
   }

   class BlockFamilyProvider {
      private final TextureMapping mapping;
      private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();
      @Nullable
      private BlockFamily family;
      @Nullable
      private ResourceLocation fullBlock;

      public BlockFamilyProvider(TextureMapping p_125034_) {
         this.mapping = p_125034_;
      }

      public BlockModelGenerators.BlockFamilyProvider fullBlock(Block p_125041_, ModelTemplate p_125042_) {
         this.fullBlock = p_125042_.create(p_125041_, this.mapping, BlockModelGenerators.this.modelOutput);
         if (BlockModelGenerators.this.fullBlockModelCustomGenerators.containsKey(p_125041_)) {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.this.fullBlockModelCustomGenerators.get(p_125041_).create(p_125041_, this.fullBlock, this.mapping, BlockModelGenerators.this.modelOutput));
         } else {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(p_125041_, this.fullBlock));
         }

         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider fullBlockCopies(Block... p_176265_) {
         if (this.fullBlock == null) {
            throw new IllegalStateException("Full block not generated yet");
         } else {
            for(Block block : p_176265_) {
               BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, this.fullBlock));
               BlockModelGenerators.this.delegateItemModel(block, this.fullBlock);
            }

            return this;
         }
      }

      public BlockModelGenerators.BlockFamilyProvider button(Block p_125036_) {
         ResourceLocation resourcelocation = ModelTemplates.BUTTON.create(p_125036_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.BUTTON_PRESSED.create(p_125036_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createButton(p_125036_, resourcelocation, resourcelocation1));
         ResourceLocation resourcelocation2 = ModelTemplates.BUTTON_INVENTORY.create(p_125036_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(p_125036_, resourcelocation2);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider wall(Block p_125046_) {
         ResourceLocation resourcelocation = ModelTemplates.WALL_POST.create(p_125046_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.WALL_LOW_SIDE.create(p_125046_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation2 = ModelTemplates.WALL_TALL_SIDE.create(p_125046_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createWall(p_125046_, resourcelocation, resourcelocation1, resourcelocation2));
         ResourceLocation resourcelocation3 = ModelTemplates.WALL_INVENTORY.create(p_125046_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(p_125046_, resourcelocation3);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider fence(Block p_125048_) {
         ResourceLocation resourcelocation = ModelTemplates.FENCE_POST.create(p_125048_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.FENCE_SIDE.create(p_125048_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFence(p_125048_, resourcelocation, resourcelocation1));
         ResourceLocation resourcelocation2 = ModelTemplates.FENCE_INVENTORY.create(p_125048_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(p_125048_, resourcelocation2);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider fenceGate(Block p_125050_) {
         ResourceLocation resourcelocation = ModelTemplates.FENCE_GATE_OPEN.create(p_125050_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.FENCE_GATE_CLOSED.create(p_125050_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation2 = ModelTemplates.FENCE_GATE_WALL_OPEN.create(p_125050_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation3 = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(p_125050_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate(p_125050_, resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3));
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider pressurePlate(Block p_125052_) {
         ResourceLocation resourcelocation = ModelTemplates.PRESSURE_PLATE_UP.create(p_125052_, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.PRESSURE_PLATE_DOWN.create(p_125052_, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(p_125052_, resourcelocation, resourcelocation1));
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider sign(Block p_176270_) {
         if (this.family == null) {
            throw new IllegalStateException("Family not defined");
         } else {
            Block block = this.family.getVariants().get(BlockFamily.Variant.WALL_SIGN);
            ResourceLocation resourcelocation = ModelTemplates.PARTICLE_ONLY.create(p_176270_, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(p_176270_, resourcelocation));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourcelocation));
            BlockModelGenerators.this.createSimpleFlatItemModel(p_176270_.asItem());
            BlockModelGenerators.this.skipAutoItemBlock(block);
            return this;
         }
      }

      public BlockModelGenerators.BlockFamilyProvider slab(Block p_125054_) {
         if (this.fullBlock == null) {
            throw new IllegalStateException("Full block not generated yet");
         } else {
            ResourceLocation resourcelocation = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, p_125054_);
            ResourceLocation resourcelocation1 = this.getOrCreateModel(ModelTemplates.SLAB_TOP, p_125054_);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSlab(p_125054_, resourcelocation, resourcelocation1, this.fullBlock));
            BlockModelGenerators.this.delegateItemModel(p_125054_, resourcelocation);
            return this;
         }
      }

      public BlockModelGenerators.BlockFamilyProvider stairs(Block p_125056_) {
         ResourceLocation resourcelocation = this.getOrCreateModel(ModelTemplates.STAIRS_INNER, p_125056_);
         ResourceLocation resourcelocation1 = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, p_125056_);
         ResourceLocation resourcelocation2 = this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, p_125056_);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createStairs(p_125056_, resourcelocation, resourcelocation1, resourcelocation2));
         BlockModelGenerators.this.delegateItemModel(p_125056_, resourcelocation1);
         return this;
      }

      private BlockModelGenerators.BlockFamilyProvider fullBlockVariant(Block p_176272_) {
         TexturedModel texturedmodel = BlockModelGenerators.this.texturedModels.getOrDefault(p_176272_, TexturedModel.CUBE.get(p_176272_));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(p_176272_, texturedmodel.create(p_176272_, BlockModelGenerators.this.modelOutput)));
         return this;
      }

      private BlockModelGenerators.BlockFamilyProvider door(Block p_176274_) {
         BlockModelGenerators.this.createDoor(p_176274_);
         return this;
      }

      private void trapdoor(Block p_176276_) {
         if (BlockModelGenerators.this.nonOrientableTrapdoor.contains(p_176276_)) {
            BlockModelGenerators.this.createTrapdoor(p_176276_);
         } else {
            BlockModelGenerators.this.createOrientableTrapdoor(p_176276_);
         }

      }

      private ResourceLocation getOrCreateModel(ModelTemplate p_176262_, Block p_176263_) {
         return this.models.computeIfAbsent(p_176262_, (p_176268_) -> {
            return p_176268_.create(p_176263_, this.mapping, BlockModelGenerators.this.modelOutput);
         });
      }

      public BlockModelGenerators.BlockFamilyProvider generateFor(BlockFamily p_176260_) {
         this.family = p_176260_;
         p_176260_.getVariants().forEach((p_176257_, p_176258_) -> {
            BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block> biconsumer = BlockModelGenerators.SHAPE_CONSUMERS.get(p_176257_);
            if (biconsumer != null) {
               biconsumer.accept(this, p_176258_);
            }

         });
         return this;
      }
   }

   @FunctionalInterface
   interface BlockStateGeneratorSupplier {
      BlockStateGenerator create(Block p_176278_, ResourceLocation p_176279_, TextureMapping p_176280_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_176281_);
   }

   static enum TintState {
      TINTED,
      NOT_TINTED;

      public ModelTemplate getCross() {
         return this == TINTED ? ModelTemplates.TINTED_CROSS : ModelTemplates.CROSS;
      }

      public ModelTemplate getCrossPot() {
         return this == TINTED ? ModelTemplates.TINTED_FLOWER_POT_CROSS : ModelTemplates.FLOWER_POT_CROSS;
      }
   }

   class WoodProvider {
      private final TextureMapping logMapping;

      public WoodProvider(TextureMapping p_125073_) {
         this.logMapping = p_125073_;
      }

      public BlockModelGenerators.WoodProvider wood(Block p_125075_) {
         TextureMapping texturemapping = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
         ResourceLocation resourcelocation = ModelTemplates.CUBE_COLUMN.create(p_125075_, texturemapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(p_125075_, resourcelocation));
         return this;
      }

      public BlockModelGenerators.WoodProvider log(Block p_125077_) {
         ResourceLocation resourcelocation = ModelTemplates.CUBE_COLUMN.create(p_125077_, this.logMapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(p_125077_, resourcelocation));
         return this;
      }

      public BlockModelGenerators.WoodProvider logWithHorizontal(Block p_125079_) {
         ResourceLocation resourcelocation = ModelTemplates.CUBE_COLUMN.create(p_125079_, this.logMapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation resourcelocation1 = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(p_125079_, this.logMapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(p_125079_, resourcelocation, resourcelocation1));
         return this;
      }
   }
}