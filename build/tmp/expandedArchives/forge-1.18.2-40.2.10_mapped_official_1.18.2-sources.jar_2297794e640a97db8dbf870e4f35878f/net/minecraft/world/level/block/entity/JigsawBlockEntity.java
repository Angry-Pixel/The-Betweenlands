package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class JigsawBlockEntity extends BlockEntity {
   public static final String TARGET = "target";
   public static final String POOL = "pool";
   public static final String JOINT = "joint";
   public static final String NAME = "name";
   public static final String FINAL_STATE = "final_state";
   private ResourceLocation name = new ResourceLocation("empty");
   private ResourceLocation target = new ResourceLocation("empty");
   private ResourceLocation pool = new ResourceLocation("empty");
   private JigsawBlockEntity.JointType joint = JigsawBlockEntity.JointType.ROLLABLE;
   private String finalState = "minecraft:air";

   public JigsawBlockEntity(BlockPos p_155605_, BlockState p_155606_) {
      super(BlockEntityType.JIGSAW, p_155605_, p_155606_);
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawBlockEntity.JointType getJoint() {
      return this.joint;
   }

   public void setName(ResourceLocation p_59436_) {
      this.name = p_59436_;
   }

   public void setTarget(ResourceLocation p_59439_) {
      this.target = p_59439_;
   }

   public void setPool(ResourceLocation p_59441_) {
      this.pool = p_59441_;
   }

   public void setFinalState(String p_59432_) {
      this.finalState = p_59432_;
   }

   public void setJoint(JigsawBlockEntity.JointType p_59425_) {
      this.joint = p_59425_;
   }

   protected void saveAdditional(CompoundTag p_187504_) {
      super.saveAdditional(p_187504_);
      p_187504_.putString("name", this.name.toString());
      p_187504_.putString("target", this.target.toString());
      p_187504_.putString("pool", this.pool.toString());
      p_187504_.putString("final_state", this.finalState);
      p_187504_.putString("joint", this.joint.getSerializedName());
   }

   public void load(CompoundTag p_155608_) {
      super.load(p_155608_);
      this.name = new ResourceLocation(p_155608_.getString("name"));
      this.target = new ResourceLocation(p_155608_.getString("target"));
      this.pool = new ResourceLocation(p_155608_.getString("pool"));
      this.finalState = p_155608_.getString("final_state");
      this.joint = JigsawBlockEntity.JointType.byName(p_155608_.getString("joint")).orElseGet(() -> {
         return JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
      });
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void generate(ServerLevel p_59421_, int p_59422_, boolean p_59423_) {
      ChunkGenerator chunkgenerator = p_59421_.getChunkSource().getGenerator();
      StructureManager structuremanager = p_59421_.getStructureManager();
      StructureFeatureManager structurefeaturemanager = p_59421_.structureFeatureManager();
      Random random = p_59421_.getRandom();
      BlockPos blockpos = this.getBlockPos();
      List<PoolElementStructurePiece> list = Lists.newArrayList();
      StructureTemplate structuretemplate = new StructureTemplate();
      structuretemplate.fillFromWorld(p_59421_, blockpos, new Vec3i(1, 1, 1), false, (Block)null);
      StructurePoolElement structurepoolelement = new SinglePoolElement(structuretemplate);
      PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(structuremanager, structurepoolelement, blockpos, 1, Rotation.NONE, new BoundingBox(blockpos));
      JigsawPlacement.addPieces(p_59421_.registryAccess(), poolelementstructurepiece, p_59422_, PoolElementStructurePiece::new, chunkgenerator, structuremanager, list, random, p_59421_);

      for(PoolElementStructurePiece poolelementstructurepiece1 : list) {
         poolelementstructurepiece1.place(p_59421_, structurefeaturemanager, chunkgenerator, random, BoundingBox.infinite(), blockpos, p_59423_);
      }

   }

   public static enum JointType implements StringRepresentable {
      ROLLABLE("rollable"),
      ALIGNED("aligned");

      private final String name;

      private JointType(String p_59455_) {
         this.name = p_59455_;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Optional<JigsawBlockEntity.JointType> byName(String p_59458_) {
         return Arrays.stream(values()).filter((p_59461_) -> {
            return p_59461_.getSerializedName().equals(p_59458_);
         }).findFirst();
      }

      public Component getTranslatedName() {
         return new TranslatableComponent("jigsaw_block.joint." + this.name);
      }
   }
}