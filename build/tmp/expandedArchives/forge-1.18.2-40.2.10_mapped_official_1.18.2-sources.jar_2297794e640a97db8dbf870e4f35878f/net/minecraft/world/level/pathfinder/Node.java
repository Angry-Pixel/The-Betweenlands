package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Node {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   @Nullable
   public Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public BlockPathTypes type = BlockPathTypes.BLOCKED;

   public Node(int p_77285_, int p_77286_, int p_77287_) {
      this.x = p_77285_;
      this.y = p_77286_;
      this.z = p_77287_;
      this.hash = createHash(p_77285_, p_77286_, p_77287_);
   }

   public Node cloneAndMove(int p_77290_, int p_77291_, int p_77292_) {
      Node node = new Node(p_77290_, p_77291_, p_77292_);
      node.heapIdx = this.heapIdx;
      node.g = this.g;
      node.h = this.h;
      node.f = this.f;
      node.cameFrom = this.cameFrom;
      node.closed = this.closed;
      node.walkedDistance = this.walkedDistance;
      node.costMalus = this.costMalus;
      node.type = this.type;
      return node;
   }

   public static int createHash(int p_77296_, int p_77297_, int p_77298_) {
      return p_77297_ & 255 | (p_77296_ & 32767) << 8 | (p_77298_ & 32767) << 24 | (p_77296_ < 0 ? Integer.MIN_VALUE : 0) | (p_77298_ < 0 ? '\u8000' : 0);
   }

   public float distanceTo(Node p_77294_) {
      float f = (float)(p_77294_.x - this.x);
      float f1 = (float)(p_77294_.y - this.y);
      float f2 = (float)(p_77294_.z - this.z);
      return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public float distanceTo(BlockPos p_164698_) {
      float f = (float)(p_164698_.getX() - this.x);
      float f1 = (float)(p_164698_.getY() - this.y);
      float f2 = (float)(p_164698_.getZ() - this.z);
      return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public float distanceToSqr(Node p_77300_) {
      float f = (float)(p_77300_.x - this.x);
      float f1 = (float)(p_77300_.y - this.y);
      float f2 = (float)(p_77300_.z - this.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public float distanceToSqr(BlockPos p_164703_) {
      float f = (float)(p_164703_.getX() - this.x);
      float f1 = (float)(p_164703_.getY() - this.y);
      float f2 = (float)(p_164703_.getZ() - this.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public float distanceManhattan(Node p_77305_) {
      float f = (float)Math.abs(p_77305_.x - this.x);
      float f1 = (float)Math.abs(p_77305_.y - this.y);
      float f2 = (float)Math.abs(p_77305_.z - this.z);
      return f + f1 + f2;
   }

   public float distanceManhattan(BlockPos p_77307_) {
      float f = (float)Math.abs(p_77307_.getX() - this.x);
      float f1 = (float)Math.abs(p_77307_.getY() - this.y);
      float f2 = (float)Math.abs(p_77307_.getZ() - this.z);
      return f + f1 + f2;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public Vec3 asVec3() {
      return new Vec3((double)this.x, (double)this.y, (double)this.z);
   }

   public boolean equals(Object p_77309_) {
      if (!(p_77309_ instanceof Node)) {
         return false;
      } else {
         Node node = (Node)p_77309_;
         return this.hash == node.hash && this.x == node.x && this.y == node.y && this.z == node.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
   }

   public void writeToStream(FriendlyByteBuf p_164700_) {
      p_164700_.writeInt(this.x);
      p_164700_.writeInt(this.y);
      p_164700_.writeInt(this.z);
      p_164700_.writeFloat(this.walkedDistance);
      p_164700_.writeFloat(this.costMalus);
      p_164700_.writeBoolean(this.closed);
      p_164700_.writeInt(this.type.ordinal());
      p_164700_.writeFloat(this.f);
   }

   public static Node createFromStream(FriendlyByteBuf p_77302_) {
      Node node = new Node(p_77302_.readInt(), p_77302_.readInt(), p_77302_.readInt());
      node.walkedDistance = p_77302_.readFloat();
      node.costMalus = p_77302_.readFloat();
      node.closed = p_77302_.readBoolean();
      node.type = BlockPathTypes.values()[p_77302_.readInt()];
      node.f = p_77302_.readFloat();
      return node;
   }
}