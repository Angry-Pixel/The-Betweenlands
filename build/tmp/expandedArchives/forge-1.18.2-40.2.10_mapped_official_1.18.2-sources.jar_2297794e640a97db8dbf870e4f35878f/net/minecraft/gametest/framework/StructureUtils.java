package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
   public static String testStructuresDir = "gameteststructures";
   private static final int HOW_MANY_CHUNKS_TO_LOAD_IN_EACH_DIRECTION_OF_STRUCTURE = 4;

   public static Rotation getRotationForRotationSteps(int p_127836_) {
      switch(p_127836_) {
      case 0:
         return Rotation.NONE;
      case 1:
         return Rotation.CLOCKWISE_90;
      case 2:
         return Rotation.CLOCKWISE_180;
      case 3:
         return Rotation.COUNTERCLOCKWISE_90;
      default:
         throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + p_127836_);
      }
   }

   public static int getRotationStepsForRotation(Rotation p_177752_) {
      switch(p_177752_) {
      case NONE:
         return 0;
      case CLOCKWISE_90:
         return 1;
      case CLOCKWISE_180:
         return 2;
      case COUNTERCLOCKWISE_90:
         return 3;
      default:
         throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + p_177752_);
      }
   }

   public static void main(String[] p_177771_) throws IOException {
      Bootstrap.bootStrap();
      Files.walk(Paths.get(testStructuresDir)).filter((p_177775_) -> {
         return p_177775_.toString().endsWith(".snbt");
      }).forEach((p_177773_) -> {
         try {
            String s = new String(Files.readAllBytes(p_177773_), StandardCharsets.UTF_8);
            CompoundTag compoundtag = NbtUtils.snbtToStructure(s);
            CompoundTag compoundtag1 = StructureUpdater.update(p_177773_.toString(), compoundtag);
            NbtToSnbt.writeSnbt(p_177773_, NbtUtils.structureToSnbt(compoundtag1));
         } catch (IOException | CommandSyntaxException commandsyntaxexception) {
            LOGGER.error("Something went wrong upgrading: {}", p_177773_, commandsyntaxexception);
         }

      });
   }

   public static AABB getStructureBounds(StructureBlockEntity p_127848_) {
      BlockPos blockpos = p_127848_.getBlockPos();
      BlockPos blockpos1 = blockpos.offset(p_127848_.getStructureSize().offset(-1, -1, -1));
      BlockPos blockpos2 = StructureTemplate.transform(blockpos1, Mirror.NONE, p_127848_.getRotation(), blockpos);
      return new AABB(blockpos, blockpos2);
   }

   public static BoundingBox getStructureBoundingBox(StructureBlockEntity p_127905_) {
      BlockPos blockpos = p_127905_.getBlockPos();
      BlockPos blockpos1 = blockpos.offset(p_127905_.getStructureSize().offset(-1, -1, -1));
      BlockPos blockpos2 = StructureTemplate.transform(blockpos1, Mirror.NONE, p_127905_.getRotation(), blockpos);
      return BoundingBox.fromCorners(blockpos, blockpos2);
   }

   public static void addCommandBlockAndButtonToStartTest(BlockPos p_127876_, BlockPos p_127877_, Rotation p_127878_, ServerLevel p_127879_) {
      BlockPos blockpos = StructureTemplate.transform(p_127876_.offset(p_127877_), Mirror.NONE, p_127878_, p_127876_);
      p_127879_.setBlockAndUpdate(blockpos, Blocks.COMMAND_BLOCK.defaultBlockState());
      CommandBlockEntity commandblockentity = (CommandBlockEntity)p_127879_.getBlockEntity(blockpos);
      commandblockentity.getCommandBlock().setCommand("test runthis");
      BlockPos blockpos1 = StructureTemplate.transform(blockpos.offset(0, 0, -1), Mirror.NONE, p_127878_, blockpos);
      p_127879_.setBlockAndUpdate(blockpos1, Blocks.STONE_BUTTON.defaultBlockState().rotate(p_127878_));
   }

   public static void createNewEmptyStructureBlock(String p_177765_, BlockPos p_177766_, Vec3i p_177767_, Rotation p_177768_, ServerLevel p_177769_) {
      BoundingBox boundingbox = getStructureBoundingBox(p_177766_, p_177767_, p_177768_);
      clearSpaceForStructure(boundingbox, p_177766_.getY(), p_177769_);
      p_177769_.setBlockAndUpdate(p_177766_, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity structureblockentity = (StructureBlockEntity)p_177769_.getBlockEntity(p_177766_);
      structureblockentity.setIgnoreEntities(false);
      structureblockentity.setStructureName(new ResourceLocation(p_177765_));
      structureblockentity.setStructureSize(p_177767_);
      structureblockentity.setMode(StructureMode.SAVE);
      structureblockentity.setShowBoundingBox(true);
   }

   public static StructureBlockEntity spawnStructure(String p_127884_, BlockPos p_127885_, Rotation p_127886_, int p_127887_, ServerLevel p_127888_, boolean p_127889_) {
      Vec3i vec3i = getStructureTemplate(p_127884_, p_127888_).getSize();
      BoundingBox boundingbox = getStructureBoundingBox(p_127885_, vec3i, p_127886_);
      BlockPos blockpos;
      if (p_127886_ == Rotation.NONE) {
         blockpos = p_127885_;
      } else if (p_127886_ == Rotation.CLOCKWISE_90) {
         blockpos = p_127885_.offset(vec3i.getZ() - 1, 0, 0);
      } else if (p_127886_ == Rotation.CLOCKWISE_180) {
         blockpos = p_127885_.offset(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
      } else {
         if (p_127886_ != Rotation.COUNTERCLOCKWISE_90) {
            throw new IllegalArgumentException("Invalid rotation: " + p_127886_);
         }

         blockpos = p_127885_.offset(0, 0, vec3i.getX() - 1);
      }

      forceLoadChunks(p_127885_, p_127888_);
      clearSpaceForStructure(boundingbox, p_127885_.getY(), p_127888_);
      StructureBlockEntity structureblockentity = createStructureBlock(p_127884_, blockpos, p_127886_, p_127888_, p_127889_);
      p_127888_.getBlockTicks().clearArea(boundingbox);
      p_127888_.clearBlockEvents(boundingbox);
      return structureblockentity;
   }

   private static void forceLoadChunks(BlockPos p_127858_, ServerLevel p_127859_) {
      ChunkPos chunkpos = new ChunkPos(p_127858_);

      for(int i = -1; i < 4; ++i) {
         for(int j = -1; j < 4; ++j) {
            int k = chunkpos.x + i;
            int l = chunkpos.z + j;
            p_127859_.setChunkForced(k, l, true);
         }
      }

   }

   public static void clearSpaceForStructure(BoundingBox p_127850_, int p_127851_, ServerLevel p_127852_) {
      BoundingBox boundingbox = new BoundingBox(p_127850_.minX() - 2, p_127850_.minY() - 3, p_127850_.minZ() - 3, p_127850_.maxX() + 3, p_127850_.maxY() + 20, p_127850_.maxZ() + 3);
      BlockPos.betweenClosedStream(boundingbox).forEach((p_177748_) -> {
         clearBlock(p_127851_, p_177748_, p_127852_);
      });
      p_127852_.getBlockTicks().clearArea(boundingbox);
      p_127852_.clearBlockEvents(boundingbox);
      AABB aabb = new AABB((double)boundingbox.minX(), (double)boundingbox.minY(), (double)boundingbox.minZ(), (double)boundingbox.maxX(), (double)boundingbox.maxY(), (double)boundingbox.maxZ());
      List<Entity> list = p_127852_.getEntitiesOfClass(Entity.class, aabb, (p_177750_) -> {
         return !(p_177750_ instanceof Player);
      });
      list.forEach(Entity::discard);
   }

   public static BoundingBox getStructureBoundingBox(BlockPos p_177761_, Vec3i p_177762_, Rotation p_177763_) {
      BlockPos blockpos = p_177761_.offset(p_177762_).offset(-1, -1, -1);
      BlockPos blockpos1 = StructureTemplate.transform(blockpos, Mirror.NONE, p_177763_, p_177761_);
      BoundingBox boundingbox = BoundingBox.fromCorners(p_177761_, blockpos1);
      int i = Math.min(boundingbox.minX(), boundingbox.maxX());
      int j = Math.min(boundingbox.minZ(), boundingbox.maxZ());
      return boundingbox.move(p_177761_.getX() - i, 0, p_177761_.getZ() - j);
   }

   public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos p_127854_, int p_127855_, ServerLevel p_127856_) {
      return findStructureBlocks(p_127854_, p_127855_, p_127856_).stream().filter((p_177756_) -> {
         return doesStructureContain(p_177756_, p_127854_, p_127856_);
      }).findFirst();
   }

   @Nullable
   public static BlockPos findNearestStructureBlock(BlockPos p_127907_, int p_127908_, ServerLevel p_127909_) {
      Comparator<BlockPos> comparator = Comparator.comparingInt((p_177759_) -> {
         return p_177759_.distManhattan(p_127907_);
      });
      Collection<BlockPos> collection = findStructureBlocks(p_127907_, p_127908_, p_127909_);
      Optional<BlockPos> optional = collection.stream().min(comparator);
      return optional.orElse((BlockPos)null);
   }

   public static Collection<BlockPos> findStructureBlocks(BlockPos p_127911_, int p_127912_, ServerLevel p_127913_) {
      Collection<BlockPos> collection = Lists.newArrayList();
      AABB aabb = new AABB(p_127911_);
      aabb = aabb.inflate((double)p_127912_);

      for(int i = (int)aabb.minX; i <= (int)aabb.maxX; ++i) {
         for(int j = (int)aabb.minY; j <= (int)aabb.maxY; ++j) {
            for(int k = (int)aabb.minZ; k <= (int)aabb.maxZ; ++k) {
               BlockPos blockpos = new BlockPos(i, j, k);
               BlockState blockstate = p_127913_.getBlockState(blockpos);
               if (blockstate.is(Blocks.STRUCTURE_BLOCK)) {
                  collection.add(blockpos);
               }
            }
         }
      }

      return collection;
   }

   private static StructureTemplate getStructureTemplate(String p_127881_, ServerLevel p_127882_) {
      StructureManager structuremanager = p_127882_.getStructureManager();
      Optional<StructureTemplate> optional = structuremanager.get(new ResourceLocation(p_127881_));
      if (optional.isPresent()) {
         return optional.get();
      } else {
         String s = new ResourceLocation(p_127881_).getPath() + ".snbt";
         Path path = Paths.get(testStructuresDir, s);
         CompoundTag compoundtag = tryLoadStructure(path);
         if (compoundtag == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
         } else {
            return structuremanager.readStructure(compoundtag);
         }
      }
   }

   private static StructureBlockEntity createStructureBlock(String p_127891_, BlockPos p_127892_, Rotation p_127893_, ServerLevel p_127894_, boolean p_127895_) {
      p_127894_.setBlockAndUpdate(p_127892_, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockEntity structureblockentity = (StructureBlockEntity)p_127894_.getBlockEntity(p_127892_);
      structureblockentity.setMode(StructureMode.LOAD);
      structureblockentity.setRotation(p_127893_);
      structureblockentity.setIgnoreEntities(false);
      structureblockentity.setStructureName(new ResourceLocation(p_127891_));
      structureblockentity.loadStructure(p_127894_, p_127895_);
      if (structureblockentity.getStructureSize() != Vec3i.ZERO) {
         return structureblockentity;
      } else {
         StructureTemplate structuretemplate = getStructureTemplate(p_127891_, p_127894_);
         structureblockentity.loadStructure(p_127894_, p_127895_, structuretemplate);
         if (structureblockentity.getStructureSize() == Vec3i.ZERO) {
            throw new RuntimeException("Failed to load structure " + p_127891_);
         } else {
            return structureblockentity;
         }
      }
   }

   @Nullable
   private static CompoundTag tryLoadStructure(Path p_127903_) {
      try {
         BufferedReader bufferedreader = Files.newBufferedReader(p_127903_);
         String s = IOUtils.toString((Reader)bufferedreader);
         return NbtUtils.snbtToStructure(s);
      } catch (IOException ioexception) {
         return null;
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new RuntimeException("Error while trying to load structure " + p_127903_, commandsyntaxexception);
      }
   }

   private static void clearBlock(int p_127842_, BlockPos p_127843_, ServerLevel p_127844_) {
      BlockState blockstate = null;
      FlatLevelGeneratorSettings flatlevelgeneratorsettings = FlatLevelGeneratorSettings.getDefault(p_127844_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), p_127844_.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY));
      List<BlockState> list = flatlevelgeneratorsettings.getLayers();
      int i = p_127843_.getY() - p_127844_.getMinBuildHeight();
      if (p_127843_.getY() < p_127842_ && i > 0 && i <= list.size()) {
         blockstate = list.get(i - 1);
      }

      if (blockstate == null) {
         blockstate = Blocks.AIR.defaultBlockState();
      }

      BlockInput blockinput = new BlockInput(blockstate, Collections.emptySet(), (CompoundTag)null);
      blockinput.place(p_127844_, p_127843_, 2);
      p_127844_.blockUpdated(p_127843_, blockstate.getBlock());
   }

   private static boolean doesStructureContain(BlockPos p_127868_, BlockPos p_127869_, ServerLevel p_127870_) {
      StructureBlockEntity structureblockentity = (StructureBlockEntity)p_127870_.getBlockEntity(p_127868_);
      AABB aabb = getStructureBounds(structureblockentity).inflate(1.0D);
      return aabb.contains(Vec3.atCenterOf(p_127869_));
   }
}
