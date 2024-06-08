package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureTemplate {
   public static final String PALETTE_TAG = "palette";
   public static final String PALETTE_LIST_TAG = "palettes";
   public static final String ENTITIES_TAG = "entities";
   public static final String BLOCKS_TAG = "blocks";
   public static final String BLOCK_TAG_POS = "pos";
   public static final String BLOCK_TAG_STATE = "state";
   public static final String BLOCK_TAG_NBT = "nbt";
   public static final String ENTITY_TAG_POS = "pos";
   public static final String ENTITY_TAG_BLOCKPOS = "blockPos";
   public static final String ENTITY_TAG_NBT = "nbt";
   public static final String SIZE_TAG = "size";
   static final int CHUNK_SIZE = 16;
   private final List<StructureTemplate.Palette> palettes = Lists.newArrayList();
   private final List<StructureTemplate.StructureEntityInfo> entityInfoList = Lists.newArrayList();
   private Vec3i size = Vec3i.ZERO;
   private String author = "?";

   public Vec3i getSize() {
      return this.size;
   }

   public void setAuthor(String p_74613_) {
      this.author = p_74613_;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(Level p_163803_, BlockPos p_163804_, Vec3i p_163805_, boolean p_163806_, @Nullable Block p_163807_) {
      if (p_163805_.getX() >= 1 && p_163805_.getY() >= 1 && p_163805_.getZ() >= 1) {
         BlockPos blockpos = p_163804_.offset(p_163805_).offset(-1, -1, -1);
         List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
         List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();
         List<StructureTemplate.StructureBlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(p_163804_.getX(), blockpos.getX()), Math.min(p_163804_.getY(), blockpos.getY()), Math.min(p_163804_.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(p_163804_.getX(), blockpos.getX()), Math.max(p_163804_.getY(), blockpos.getY()), Math.max(p_163804_.getZ(), blockpos.getZ()));
         this.size = p_163805_;

         for(BlockPos blockpos3 : BlockPos.betweenClosed(blockpos1, blockpos2)) {
            BlockPos blockpos4 = blockpos3.subtract(blockpos1);
            BlockState blockstate = p_163803_.getBlockState(blockpos3);
            if (p_163807_ == null || !blockstate.is(p_163807_)) {
               BlockEntity blockentity = p_163803_.getBlockEntity(blockpos3);
               StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo;
               if (blockentity != null) {
                  structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockpos4, blockstate, blockentity.saveWithId());
               } else {
                  structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockpos4, blockstate, (CompoundTag)null);
               }

               addToLists(structuretemplate$structureblockinfo, list, list1, list2);
            }
         }

         List<StructureTemplate.StructureBlockInfo> list3 = buildInfoList(list, list1, list2);
         this.palettes.clear();
         this.palettes.add(new StructureTemplate.Palette(list3));
         if (p_163806_) {
            this.fillEntityList(p_163803_, blockpos1, blockpos2.offset(1, 1, 1));
         } else {
            this.entityInfoList.clear();
         }

      }
   }

   private static void addToLists(StructureTemplate.StructureBlockInfo p_74574_, List<StructureTemplate.StructureBlockInfo> p_74575_, List<StructureTemplate.StructureBlockInfo> p_74576_, List<StructureTemplate.StructureBlockInfo> p_74577_) {
      if (p_74574_.nbt != null) {
         p_74576_.add(p_74574_);
      } else if (!p_74574_.state.getBlock().hasDynamicShape() && p_74574_.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
         p_74575_.add(p_74574_);
      } else {
         p_74577_.add(p_74574_);
      }

   }

   private static List<StructureTemplate.StructureBlockInfo> buildInfoList(List<StructureTemplate.StructureBlockInfo> p_74615_, List<StructureTemplate.StructureBlockInfo> p_74616_, List<StructureTemplate.StructureBlockInfo> p_74617_) {
      Comparator<StructureTemplate.StructureBlockInfo> comparator = Comparator.<StructureTemplate.StructureBlockInfo>comparingInt((p_74641_) -> {
         return p_74641_.pos.getY();
      }).thenComparingInt((p_74637_) -> {
         return p_74637_.pos.getX();
      }).thenComparingInt((p_74572_) -> {
         return p_74572_.pos.getZ();
      });
      p_74615_.sort(comparator);
      p_74617_.sort(comparator);
      p_74616_.sort(comparator);
      List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
      list.addAll(p_74615_);
      list.addAll(p_74617_);
      list.addAll(p_74616_);
      return list;
   }

   private void fillEntityList(Level p_74501_, BlockPos p_74502_, BlockPos p_74503_) {
      List<Entity> list = p_74501_.getEntitiesOfClass(Entity.class, new AABB(p_74502_, p_74503_), (p_74499_) -> {
         return !(p_74499_ instanceof Player);
      });
      this.entityInfoList.clear();

      for(Entity entity : list) {
         Vec3 vec3 = new Vec3(entity.getX() - (double)p_74502_.getX(), entity.getY() - (double)p_74502_.getY(), entity.getZ() - (double)p_74502_.getZ());
         CompoundTag compoundtag = new CompoundTag();
         entity.save(compoundtag);
         BlockPos blockpos;
         if (entity instanceof Painting) {
            blockpos = ((Painting)entity).getPos().subtract(p_74502_);
         } else {
            blockpos = new BlockPos(vec3);
         }

         this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(vec3, blockpos, compoundtag.copy()));
      }

   }

   public List<StructureTemplate.StructureBlockInfo> filterBlocks(BlockPos p_74604_, StructurePlaceSettings p_74605_, Block p_74606_) {
      return this.filterBlocks(p_74604_, p_74605_, p_74606_, true);
   }

   public List<StructureTemplate.StructureBlockInfo> filterBlocks(BlockPos p_74608_, StructurePlaceSettings p_74609_, Block p_74610_, boolean p_74611_) {
      List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
      BoundingBox boundingbox = p_74609_.getBoundingBox();
      if (this.palettes.isEmpty()) {
         return Collections.emptyList();
      } else {
         for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : p_74609_.getRandomPalette(this.palettes, p_74608_).blocks(p_74610_)) {
            BlockPos blockpos = p_74611_ ? calculateRelativePosition(p_74609_, structuretemplate$structureblockinfo.pos).offset(p_74608_) : structuretemplate$structureblockinfo.pos;
            if (boundingbox == null || boundingbox.isInside(blockpos)) {
               list.add(new StructureTemplate.StructureBlockInfo(blockpos, structuretemplate$structureblockinfo.state.rotate(p_74609_.getRotation()), structuretemplate$structureblockinfo.nbt));
            }
         }

         return list;
      }
   }

   public BlockPos calculateConnectedPosition(StructurePlaceSettings p_74567_, BlockPos p_74568_, StructurePlaceSettings p_74569_, BlockPos p_74570_) {
      BlockPos blockpos = calculateRelativePosition(p_74567_, p_74568_);
      BlockPos blockpos1 = calculateRelativePosition(p_74569_, p_74570_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos calculateRelativePosition(StructurePlaceSettings p_74564_, BlockPos p_74565_) {
      return transform(p_74565_, p_74564_.getMirror(), p_74564_.getRotation(), p_74564_.getRotationPivot());
   }

   public static Vec3 transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos) {
      return transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
   }

   public boolean placeInWorld(ServerLevelAccessor p_74537_, BlockPos p_74538_, BlockPos p_74539_, StructurePlaceSettings p_74540_, Random p_74541_, int p_74542_) {
      if (this.palettes.isEmpty()) {
         return false;
      } else {
         List<StructureTemplate.StructureBlockInfo> list = p_74540_.getRandomPalette(this.palettes, p_74538_).blocks();
         if ((!list.isEmpty() || !p_74540_.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BoundingBox boundingbox = p_74540_.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(p_74540_.shouldKeepLiquids() ? list.size() : 0);
            List<BlockPos> list2 = Lists.newArrayListWithCapacity(p_74540_.shouldKeepLiquids() ? list.size() : 0);
            List<Pair<BlockPos, CompoundTag>> list3 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;

            for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : processBlockInfos(p_74537_, p_74538_, p_74539_, p_74540_, list, this)) {
               BlockPos blockpos = structuretemplate$structureblockinfo.pos;
               if (boundingbox == null || boundingbox.isInside(blockpos)) {
                  FluidState fluidstate = p_74540_.shouldKeepLiquids() ? p_74537_.getFluidState(blockpos) : null;
                  BlockState blockstate = structuretemplate$structureblockinfo.state.mirror(p_74540_.getMirror()).rotate(p_74540_.getRotation());
                  if (structuretemplate$structureblockinfo.nbt != null) {
                     BlockEntity blockentity = p_74537_.getBlockEntity(blockpos);
                     Clearable.tryClear(blockentity);
                     p_74537_.setBlock(blockpos, Blocks.BARRIER.defaultBlockState(), 20);
                  }

                  if (p_74537_.setBlock(blockpos, blockstate, p_74542_)) {
                     i = Math.min(i, blockpos.getX());
                     j = Math.min(j, blockpos.getY());
                     k = Math.min(k, blockpos.getZ());
                     l = Math.max(l, blockpos.getX());
                     i1 = Math.max(i1, blockpos.getY());
                     j1 = Math.max(j1, blockpos.getZ());
                     list3.add(Pair.of(blockpos, structuretemplate$structureblockinfo.nbt));
                     if (structuretemplate$structureblockinfo.nbt != null) {
                        BlockEntity blockentity1 = p_74537_.getBlockEntity(blockpos);
                        if (blockentity1 != null) {
                           if (blockentity1 instanceof RandomizableContainerBlockEntity) {
                              structuretemplate$structureblockinfo.nbt.putLong("LootTableSeed", p_74541_.nextLong());
                           }

                           blockentity1.load(structuretemplate$structureblockinfo.nbt);
                        }
                     }

                     if (fluidstate != null) {
                        if (blockstate.getFluidState().isSource()) {
                           list2.add(blockpos);
                        } else if (blockstate.getBlock() instanceof LiquidBlockContainer) {
                           ((LiquidBlockContainer)blockstate.getBlock()).placeLiquid(p_74537_, blockpos, blockstate, fluidstate);
                           if (!fluidstate.isSource()) {
                              list1.add(blockpos);
                           }
                        }
                     }
                  }
               }
            }

            boolean flag = true;
            Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

            while(flag && !list1.isEmpty()) {
               flag = false;
               Iterator<BlockPos> iterator = list1.iterator();

               while(iterator.hasNext()) {
                  BlockPos blockpos3 = iterator.next();
                  FluidState fluidstate2 = p_74537_.getFluidState(blockpos3);

                  for(int i2 = 0; i2 < adirection.length && !fluidstate2.isSource(); ++i2) {
                     BlockPos blockpos1 = blockpos3.relative(adirection[i2]);
                     FluidState fluidstate1 = p_74537_.getFluidState(blockpos1);
                     if (fluidstate1.isSource() && !list2.contains(blockpos1)) {
                        fluidstate2 = fluidstate1;
                     }
                  }

                  if (fluidstate2.isSource()) {
                     BlockState blockstate1 = p_74537_.getBlockState(blockpos3);
                     Block block = blockstate1.getBlock();
                     if (block instanceof LiquidBlockContainer) {
                        ((LiquidBlockContainer)block).placeLiquid(p_74537_, blockpos3, blockstate1, fluidstate2);
                        flag = true;
                        iterator.remove();
                     }
                  }
               }
            }

            if (i <= l) {
               if (!p_74540_.getKnownShape()) {
                  DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(l - i + 1, i1 - j + 1, j1 - k + 1);
                  int k1 = i;
                  int l1 = j;
                  int j2 = k;

                  for(Pair<BlockPos, CompoundTag> pair1 : list3) {
                     BlockPos blockpos2 = pair1.getFirst();
                     discretevoxelshape.fill(blockpos2.getX() - k1, blockpos2.getY() - l1, blockpos2.getZ() - j2);
                  }

                  updateShapeAtEdge(p_74537_, p_74542_, discretevoxelshape, k1, l1, j2);
               }

               for(Pair<BlockPos, CompoundTag> pair : list3) {
                  BlockPos blockpos4 = pair.getFirst();
                  if (!p_74540_.getKnownShape()) {
                     BlockState blockstate2 = p_74537_.getBlockState(blockpos4);
                     BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate2, p_74537_, blockpos4);
                     if (blockstate2 != blockstate3) {
                        p_74537_.setBlock(blockpos4, blockstate3, p_74542_ & -2 | 16);
                     }

                     p_74537_.blockUpdated(blockpos4, blockstate3.getBlock());
                  }

                  if (pair.getSecond() != null) {
                     BlockEntity blockentity2 = p_74537_.getBlockEntity(blockpos4);
                     if (blockentity2 != null) {
                        blockentity2.setChanged();
                     }
                  }
               }
            }

            if (!p_74540_.isIgnoreEntities()) {
               this.addEntitiesToWorld(p_74537_, p_74538_, p_74540_);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(LevelAccessor p_74511_, int p_74512_, DiscreteVoxelShape p_74513_, int p_74514_, int p_74515_, int p_74516_) {
      p_74513_.forAllFaces((p_74494_, p_74495_, p_74496_, p_74497_) -> {
         BlockPos blockpos = new BlockPos(p_74514_ + p_74495_, p_74515_ + p_74496_, p_74516_ + p_74497_);
         BlockPos blockpos1 = blockpos.relative(p_74494_);
         BlockState blockstate = p_74511_.getBlockState(blockpos);
         BlockState blockstate1 = p_74511_.getBlockState(blockpos1);
         BlockState blockstate2 = blockstate.updateShape(p_74494_, blockstate1, p_74511_, blockpos, blockpos1);
         if (blockstate != blockstate2) {
            p_74511_.setBlock(blockpos, blockstate2, p_74512_ & -2);
         }

         BlockState blockstate3 = blockstate1.updateShape(p_74494_.getOpposite(), blockstate2, p_74511_, blockpos1, blockpos);
         if (blockstate1 != blockstate3) {
            p_74511_.setBlock(blockpos1, blockstate3, p_74512_ & -2);
         }

      });
   }

   @Deprecated //Use Forge version
   public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(LevelAccessor p_74518_, BlockPos p_74519_, BlockPos p_74520_, StructurePlaceSettings p_74521_, List<StructureTemplate.StructureBlockInfo> p_74522_) {
      return processBlockInfos(p_74518_, p_74519_, p_74520_, p_74521_, p_74522_, null);
   }

   public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(LevelAccessor p_74518_, BlockPos p_74519_, BlockPos p_74520_, StructurePlaceSettings p_74521_, List<StructureTemplate.StructureBlockInfo> p_74522_, @Nullable StructureTemplate template) {
      List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();

      for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : p_74522_) {
         BlockPos blockpos = calculateRelativePosition(p_74521_, structuretemplate$structureblockinfo.pos).offset(p_74519_);
         StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1 = new StructureTemplate.StructureBlockInfo(blockpos, structuretemplate$structureblockinfo.state, structuretemplate$structureblockinfo.nbt != null ? structuretemplate$structureblockinfo.nbt.copy() : null);

         for(Iterator<StructureProcessor> iterator = p_74521_.getProcessors().iterator(); structuretemplate$structureblockinfo1 != null && iterator.hasNext(); structuretemplate$structureblockinfo1 = iterator.next().process(p_74518_, p_74519_, p_74520_, structuretemplate$structureblockinfo, structuretemplate$structureblockinfo1, p_74521_, template)) {
         }

         if (structuretemplate$structureblockinfo1 != null) {
            list.add(structuretemplate$structureblockinfo1);
         }
      }

      return list;
   }

   public static List<StructureTemplate.StructureEntityInfo> processEntityInfos(@Nullable StructureTemplate template, LevelAccessor p_215387_0_, BlockPos p_215387_1_, StructurePlaceSettings p_215387_2_, List<StructureTemplate.StructureEntityInfo> p_215387_3_) {
      List<StructureTemplate.StructureEntityInfo> list = Lists.newArrayList();
      for(StructureTemplate.StructureEntityInfo entityInfo : p_215387_3_) {
         Vec3 pos = transformedVec3d(p_215387_2_, entityInfo.pos).add(Vec3.atLowerCornerOf(p_215387_1_));
         BlockPos blockpos = calculateRelativePosition(p_215387_2_, entityInfo.blockPos).offset(p_215387_1_);
         StructureTemplate.StructureEntityInfo info = new StructureTemplate.StructureEntityInfo(pos, blockpos, entityInfo.nbt);
         for (StructureProcessor proc : p_215387_2_.getProcessors()) {
            info = proc.processEntity(p_215387_0_, p_215387_1_, entityInfo, info, p_215387_2_, template);
            if (info == null)
               break;
         }
         if (info != null)
            list.add(info);
      }
      return list;
   }

   private void addEntitiesToWorld(ServerLevelAccessor p_74524_, BlockPos p_74525_, StructurePlaceSettings placementIn) {
      for(StructureTemplate.StructureEntityInfo structuretemplate$structureentityinfo : processEntityInfos(this, p_74524_, p_74525_, placementIn, this.entityInfoList)) {
         BlockPos blockpos = transform(structuretemplate$structureentityinfo.blockPos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot()).offset(p_74525_);
         blockpos = structuretemplate$structureentityinfo.blockPos; // FORGE: Position will have already been transformed by processEntityInfos
         if (placementIn.getBoundingBox() == null || placementIn.getBoundingBox().isInside(blockpos)) {
            CompoundTag compoundtag = structuretemplate$structureentityinfo.nbt.copy();
            Vec3 vec31 = structuretemplate$structureentityinfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
            ListTag listtag = new ListTag();
            listtag.add(DoubleTag.valueOf(vec31.x));
            listtag.add(DoubleTag.valueOf(vec31.y));
            listtag.add(DoubleTag.valueOf(vec31.z));
            compoundtag.put("Pos", listtag);
            compoundtag.remove("UUID");
            createEntityIgnoreException(p_74524_, compoundtag).ifPresent((p_205061_) -> {
               float f = p_205061_.rotate(placementIn.getRotation());
               f += p_205061_.mirror(placementIn.getMirror()) - p_205061_.getYRot();
               p_205061_.moveTo(vec31.x, vec31.y, vec31.z, f, p_205061_.getXRot());
               if (placementIn.shouldFinalizeEntities() && p_205061_ instanceof Mob) {
                  ((Mob)p_205061_).finalizeSpawn(p_74524_, p_74524_.getCurrentDifficultyAt(new BlockPos(vec31)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, compoundtag);
               }

               p_74524_.addFreshEntityWithPassengers(p_205061_);
            });
         }
      }

   }

   private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor p_74544_, CompoundTag p_74545_) {
      try {
         return EntityType.create(p_74545_, p_74544_.getLevel());
      } catch (Exception exception) {
         return Optional.empty();
      }
   }

   public Vec3i getSize(Rotation p_163809_) {
      switch(p_163809_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos transform(BlockPos p_74594_, Mirror p_74595_, Rotation p_74596_, BlockPos p_74597_) {
      int i = p_74594_.getX();
      int j = p_74594_.getY();
      int k = p_74594_.getZ();
      boolean flag = true;
      switch(p_74595_) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = p_74597_.getX();
      int i1 = p_74597_.getZ();
      switch(p_74596_) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : p_74594_;
      }
   }

   public static Vec3 transform(Vec3 p_74579_, Mirror p_74580_, Rotation p_74581_, BlockPos p_74582_) {
      double d0 = p_74579_.x;
      double d1 = p_74579_.y;
      double d2 = p_74579_.z;
      boolean flag = true;
      switch(p_74580_) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = p_74582_.getX();
      int j = p_74582_.getZ();
      switch(p_74581_) {
      case COUNTERCLOCKWISE_90:
         return new Vec3((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vec3((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vec3((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vec3(d0, d1, d2) : p_74579_;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_74584_, Mirror p_74585_, Rotation p_74586_) {
      return getZeroPositionWithTransform(p_74584_, p_74585_, p_74586_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_74588_, Mirror p_74589_, Rotation p_74590_, int p_74591_, int p_74592_) {
      --p_74591_;
      --p_74592_;
      int i = p_74589_ == Mirror.FRONT_BACK ? p_74591_ : 0;
      int j = p_74589_ == Mirror.LEFT_RIGHT ? p_74592_ : 0;
      BlockPos blockpos = p_74588_;
      switch(p_74590_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_74588_.offset(j, 0, p_74591_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_74588_.offset(p_74592_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_74588_.offset(p_74591_ - i, 0, p_74592_ - j);
         break;
      case NONE:
         blockpos = p_74588_.offset(i, 0, j);
      }

      return blockpos;
   }

   public BoundingBox getBoundingBox(StructurePlaceSettings p_74634_, BlockPos p_74635_) {
      return this.getBoundingBox(p_74635_, p_74634_.getRotation(), p_74634_.getRotationPivot(), p_74634_.getMirror());
   }

   public BoundingBox getBoundingBox(BlockPos p_74599_, Rotation p_74600_, BlockPos p_74601_, Mirror p_74602_) {
      return getBoundingBox(p_74599_, p_74600_, p_74601_, p_74602_, this.size);
   }

   @VisibleForTesting
   protected static BoundingBox getBoundingBox(BlockPos p_163811_, Rotation p_163812_, BlockPos p_163813_, Mirror p_163814_, Vec3i p_163815_) {
      Vec3i vec3i = p_163815_.offset(-1, -1, -1);
      BlockPos blockpos = transform(BlockPos.ZERO, p_163814_, p_163812_, p_163813_);
      BlockPos blockpos1 = transform(BlockPos.ZERO.offset(vec3i), p_163814_, p_163812_, p_163813_);
      return BoundingBox.fromCorners(blockpos, blockpos1).move(p_163811_);
   }

   public CompoundTag save(CompoundTag p_74619_) {
      if (this.palettes.isEmpty()) {
         p_74619_.put("blocks", new ListTag());
         p_74619_.put("palette", new ListTag());
      } else {
         List<StructureTemplate.SimplePalette> list = Lists.newArrayList();
         StructureTemplate.SimplePalette structuretemplate$simplepalette = new StructureTemplate.SimplePalette();
         list.add(structuretemplate$simplepalette);

         for(int i = 1; i < this.palettes.size(); ++i) {
            list.add(new StructureTemplate.SimplePalette());
         }

         ListTag listtag1 = new ListTag();
         List<StructureTemplate.StructureBlockInfo> list1 = this.palettes.get(0).blocks();

         for(int j = 0; j < list1.size(); ++j) {
            StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo = list1.get(j);
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.put("pos", this.newIntegerList(structuretemplate$structureblockinfo.pos.getX(), structuretemplate$structureblockinfo.pos.getY(), structuretemplate$structureblockinfo.pos.getZ()));
            int k = structuretemplate$simplepalette.idFor(structuretemplate$structureblockinfo.state);
            compoundtag.putInt("state", k);
            if (structuretemplate$structureblockinfo.nbt != null) {
               compoundtag.put("nbt", structuretemplate$structureblockinfo.nbt);
            }

            listtag1.add(compoundtag);

            for(int l = 1; l < this.palettes.size(); ++l) {
               StructureTemplate.SimplePalette structuretemplate$simplepalette1 = list.get(l);
               structuretemplate$simplepalette1.addMapping((this.palettes.get(l).blocks().get(j)).state, k);
            }
         }

         p_74619_.put("blocks", listtag1);
         if (list.size() == 1) {
            ListTag listtag2 = new ListTag();

            for(BlockState blockstate : structuretemplate$simplepalette) {
               listtag2.add(NbtUtils.writeBlockState(blockstate));
            }

            p_74619_.put("palette", listtag2);
         } else {
            ListTag listtag3 = new ListTag();

            for(StructureTemplate.SimplePalette structuretemplate$simplepalette2 : list) {
               ListTag listtag4 = new ListTag();

               for(BlockState blockstate1 : structuretemplate$simplepalette2) {
                  listtag4.add(NbtUtils.writeBlockState(blockstate1));
               }

               listtag3.add(listtag4);
            }

            p_74619_.put("palettes", listtag3);
         }
      }

      ListTag listtag = new ListTag();

      for(StructureTemplate.StructureEntityInfo structuretemplate$structureentityinfo : this.entityInfoList) {
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("pos", this.newDoubleList(structuretemplate$structureentityinfo.pos.x, structuretemplate$structureentityinfo.pos.y, structuretemplate$structureentityinfo.pos.z));
         compoundtag1.put("blockPos", this.newIntegerList(structuretemplate$structureentityinfo.blockPos.getX(), structuretemplate$structureentityinfo.blockPos.getY(), structuretemplate$structureentityinfo.blockPos.getZ()));
         if (structuretemplate$structureentityinfo.nbt != null) {
            compoundtag1.put("nbt", structuretemplate$structureentityinfo.nbt);
         }

         listtag.add(compoundtag1);
      }

      p_74619_.put("entities", listtag);
      p_74619_.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      p_74619_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return p_74619_;
   }

   public void load(CompoundTag p_74639_) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag listtag = p_74639_.getList("size", 3);
      this.size = new Vec3i(listtag.getInt(0), listtag.getInt(1), listtag.getInt(2));
      ListTag listtag1 = p_74639_.getList("blocks", 10);
      if (p_74639_.contains("palettes", 9)) {
         ListTag listtag2 = p_74639_.getList("palettes", 9);

         for(int i = 0; i < listtag2.size(); ++i) {
            this.loadPalette(listtag2.getList(i), listtag1);
         }
      } else {
         this.loadPalette(p_74639_.getList("palette", 10), listtag1);
      }

      ListTag listtag5 = p_74639_.getList("entities", 10);

      for(int j = 0; j < listtag5.size(); ++j) {
         CompoundTag compoundtag = listtag5.getCompound(j);
         ListTag listtag3 = compoundtag.getList("pos", 6);
         Vec3 vec3 = new Vec3(listtag3.getDouble(0), listtag3.getDouble(1), listtag3.getDouble(2));
         ListTag listtag4 = compoundtag.getList("blockPos", 3);
         BlockPos blockpos = new BlockPos(listtag4.getInt(0), listtag4.getInt(1), listtag4.getInt(2));
         if (compoundtag.contains("nbt")) {
            CompoundTag compoundtag1 = compoundtag.getCompound("nbt");
            this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(vec3, blockpos, compoundtag1));
         }
      }

   }

   private void loadPalette(ListTag p_74621_, ListTag p_74622_) {
      StructureTemplate.SimplePalette structuretemplate$simplepalette = new StructureTemplate.SimplePalette();

      for(int i = 0; i < p_74621_.size(); ++i) {
         structuretemplate$simplepalette.addMapping(NbtUtils.readBlockState(p_74621_.getCompound(i)), i);
      }

      List<StructureTemplate.StructureBlockInfo> list2 = Lists.newArrayList();
      List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
      List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();

      for(int j = 0; j < p_74622_.size(); ++j) {
         CompoundTag compoundtag = p_74622_.getCompound(j);
         ListTag listtag = compoundtag.getList("pos", 3);
         BlockPos blockpos = new BlockPos(listtag.getInt(0), listtag.getInt(1), listtag.getInt(2));
         BlockState blockstate = structuretemplate$simplepalette.stateFor(compoundtag.getInt("state"));
         CompoundTag compoundtag1;
         if (compoundtag.contains("nbt")) {
            compoundtag1 = compoundtag.getCompound("nbt");
         } else {
            compoundtag1 = null;
         }

         StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo = new StructureTemplate.StructureBlockInfo(blockpos, blockstate, compoundtag1);
         addToLists(structuretemplate$structureblockinfo, list2, list, list1);
      }

      List<StructureTemplate.StructureBlockInfo> list3 = buildInfoList(list2, list, list1);
      this.palettes.add(new StructureTemplate.Palette(list3));
   }

   private ListTag newIntegerList(int... p_74626_) {
      ListTag listtag = new ListTag();

      for(int i : p_74626_) {
         listtag.add(IntTag.valueOf(i));
      }

      return listtag;
   }

   private ListTag newDoubleList(double... p_74624_) {
      ListTag listtag = new ListTag();

      for(double d0 : p_74624_) {
         listtag.add(DoubleTag.valueOf(d0));
      }

      return listtag;
   }

   public static final class Palette {
      private final List<StructureTemplate.StructureBlockInfo> blocks;
      private final Map<Block, List<StructureTemplate.StructureBlockInfo>> cache = Maps.newHashMap();

      Palette(List<StructureTemplate.StructureBlockInfo> p_74648_) {
         this.blocks = p_74648_;
      }

      public List<StructureTemplate.StructureBlockInfo> blocks() {
         return this.blocks;
      }

      public List<StructureTemplate.StructureBlockInfo> blocks(Block p_74654_) {
         return this.cache.computeIfAbsent(p_74654_, (p_74659_) -> {
            return this.blocks.stream().filter((p_163818_) -> {
               return p_163818_.state.is(p_74659_);
            }).collect(Collectors.toList());
         });
      }
   }

   static class SimplePalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      private final IdMapper<BlockState> ids = new IdMapper<>(16);
      private int lastId;

      public int idFor(BlockState p_74670_) {
         int i = this.ids.getId(p_74670_);
         if (i == -1) {
            i = this.lastId++;
            this.ids.addMapping(p_74670_, i);
         }

         return i;
      }

      @Nullable
      public BlockState stateFor(int p_74668_) {
         BlockState blockstate = this.ids.byId(p_74668_);
         return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState p_74672_, int p_74673_) {
         this.ids.addMapping(p_74672_, p_74673_);
      }
   }

   public static class StructureBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundTag nbt;

      public StructureBlockInfo(BlockPos p_74679_, BlockState p_74680_, @Nullable CompoundTag p_74681_) {
         this.pos = p_74679_;
         this.state = p_74680_;
         this.nbt = p_74681_;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   public static class StructureEntityInfo {
      public final Vec3 pos;
      public final BlockPos blockPos;
      public final CompoundTag nbt;

      public StructureEntityInfo(Vec3 p_74687_, BlockPos p_74688_, CompoundTag p_74689_) {
         this.pos = p_74687_;
         this.blockPos = p_74688_;
         this.nbt = p_74689_;
      }
   }
}
