package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SingleQuadParticle extends Particle {
   protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected SingleQuadParticle(ClientLevel p_107665_, double p_107666_, double p_107667_, double p_107668_) {
      super(p_107665_, p_107666_, p_107667_, p_107668_);
   }

   protected SingleQuadParticle(ClientLevel p_107670_, double p_107671_, double p_107672_, double p_107673_, double p_107674_, double p_107675_, double p_107676_) {
      super(p_107670_, p_107671_, p_107672_, p_107673_, p_107674_, p_107675_, p_107676_);
   }

   public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
      Vec3 vec3 = p_107679_.getPosition();
      float f = (float)(Mth.lerp((double)p_107680_, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp((double)p_107680_, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp((double)p_107680_, this.zo, this.z) - vec3.z());
      Quaternion quaternion;
      if (this.roll == 0.0F) {
         quaternion = p_107679_.rotation();
      } else {
         quaternion = new Quaternion(p_107679_.rotation());
         float f3 = Mth.lerp(p_107680_, this.oRoll, this.roll);
         quaternion.mul(Vector3f.ZP.rotation(f3));
      }

      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f4 = this.getQuadSize(p_107680_);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f = avector3f[i];
         vector3f.transform(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      float f7 = this.getU0();
      float f8 = this.getU1();
      float f5 = this.getV0();
      float f6 = this.getV1();
      int j = this.getLightColor(p_107680_);
      p_107678_.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_107678_.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_107678_.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_107678_.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
   }

   public float getQuadSize(float p_107681_) {
      return this.quadSize;
   }

   public Particle scale(float p_107683_) {
      this.quadSize *= p_107683_;
      return super.scale(p_107683_);
   }

   protected abstract float getU0();

   protected abstract float getU1();

   protected abstract float getV0();

   protected abstract float getV1();
}