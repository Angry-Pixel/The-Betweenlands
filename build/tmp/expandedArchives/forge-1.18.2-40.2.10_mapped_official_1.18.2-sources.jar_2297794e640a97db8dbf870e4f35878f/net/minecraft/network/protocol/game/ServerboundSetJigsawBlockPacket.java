package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
   private final BlockPos pos;
   private final ResourceLocation name;
   private final ResourceLocation target;
   private final ResourceLocation pool;
   private final String finalState;
   private final JigsawBlockEntity.JointType joint;

   public ServerboundSetJigsawBlockPacket(BlockPos p_134573_, ResourceLocation p_134574_, ResourceLocation p_134575_, ResourceLocation p_134576_, String p_134577_, JigsawBlockEntity.JointType p_134578_) {
      this.pos = p_134573_;
      this.name = p_134574_;
      this.target = p_134575_;
      this.pool = p_134576_;
      this.finalState = p_134577_;
      this.joint = p_134578_;
   }

   public ServerboundSetJigsawBlockPacket(FriendlyByteBuf p_179766_) {
      this.pos = p_179766_.readBlockPos();
      this.name = p_179766_.readResourceLocation();
      this.target = p_179766_.readResourceLocation();
      this.pool = p_179766_.readResourceLocation();
      this.finalState = p_179766_.readUtf();
      this.joint = JigsawBlockEntity.JointType.byName(p_179766_.readUtf()).orElse(JigsawBlockEntity.JointType.ALIGNED);
   }

   public void write(FriendlyByteBuf p_134587_) {
      p_134587_.writeBlockPos(this.pos);
      p_134587_.writeResourceLocation(this.name);
      p_134587_.writeResourceLocation(this.target);
      p_134587_.writeResourceLocation(this.pool);
      p_134587_.writeUtf(this.finalState);
      p_134587_.writeUtf(this.joint.getSerializedName());
   }

   public void handle(ServerGamePacketListener p_134584_) {
      p_134584_.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
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
}