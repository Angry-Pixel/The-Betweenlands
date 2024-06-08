package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float power;
   private final List<BlockPos> toBlow;
   private final float knockbackX;
   private final float knockbackY;
   private final float knockbackZ;

   public ClientboundExplodePacket(double p_132115_, double p_132116_, double p_132117_, float p_132118_, List<BlockPos> p_132119_, @Nullable Vec3 p_132120_) {
      this.x = p_132115_;
      this.y = p_132116_;
      this.z = p_132117_;
      this.power = p_132118_;
      this.toBlow = Lists.newArrayList(p_132119_);
      if (p_132120_ != null) {
         this.knockbackX = (float)p_132120_.x;
         this.knockbackY = (float)p_132120_.y;
         this.knockbackZ = (float)p_132120_.z;
      } else {
         this.knockbackX = 0.0F;
         this.knockbackY = 0.0F;
         this.knockbackZ = 0.0F;
      }

   }

   public ClientboundExplodePacket(FriendlyByteBuf p_178845_) {
      this.x = (double)p_178845_.readFloat();
      this.y = (double)p_178845_.readFloat();
      this.z = (double)p_178845_.readFloat();
      this.power = p_178845_.readFloat();
      int i = Mth.floor(this.x);
      int j = Mth.floor(this.y);
      int k = Mth.floor(this.z);
      this.toBlow = p_178845_.readList((p_178850_) -> {
         int l = p_178850_.readByte() + i;
         int i1 = p_178850_.readByte() + j;
         int j1 = p_178850_.readByte() + k;
         return new BlockPos(l, i1, j1);
      });
      this.knockbackX = p_178845_.readFloat();
      this.knockbackY = p_178845_.readFloat();
      this.knockbackZ = p_178845_.readFloat();
   }

   public void write(FriendlyByteBuf p_132129_) {
      p_132129_.writeFloat((float)this.x);
      p_132129_.writeFloat((float)this.y);
      p_132129_.writeFloat((float)this.z);
      p_132129_.writeFloat(this.power);
      int i = Mth.floor(this.x);
      int j = Mth.floor(this.y);
      int k = Mth.floor(this.z);
      p_132129_.writeCollection(this.toBlow, (p_178855_, p_178856_) -> {
         int l = p_178856_.getX() - i;
         int i1 = p_178856_.getY() - j;
         int j1 = p_178856_.getZ() - k;
         p_178855_.writeByte(l);
         p_178855_.writeByte(i1);
         p_178855_.writeByte(j1);
      });
      p_132129_.writeFloat(this.knockbackX);
      p_132129_.writeFloat(this.knockbackY);
      p_132129_.writeFloat(this.knockbackZ);
   }

   public void handle(ClientGamePacketListener p_132126_) {
      p_132126_.handleExplosion(this);
   }

   public float getKnockbackX() {
      return this.knockbackX;
   }

   public float getKnockbackY() {
      return this.knockbackY;
   }

   public float getKnockbackZ() {
      return this.knockbackZ;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getPower() {
      return this.power;
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }
}