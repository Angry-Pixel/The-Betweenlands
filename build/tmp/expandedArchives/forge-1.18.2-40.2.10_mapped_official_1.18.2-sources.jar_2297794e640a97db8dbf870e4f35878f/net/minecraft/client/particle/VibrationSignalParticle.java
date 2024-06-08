package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VibrationSignalParticle extends TextureSheetParticle {
   private final VibrationPath vibrationPath;
   private float yRot;
   private float yRotO;

   VibrationSignalParticle(ClientLevel p_172464_, VibrationPath p_172465_, int p_172466_) {
      super(p_172464_, (double)((float)p_172465_.getOrigin().getX() + 0.5F), (double)((float)p_172465_.getOrigin().getY() + 0.5F), (double)((float)p_172465_.getOrigin().getZ() + 0.5F), 0.0D, 0.0D, 0.0D);
      this.quadSize = 0.3F;
      this.vibrationPath = p_172465_;
      this.lifetime = p_172466_;
   }

   public void render(VertexConsumer p_172475_, Camera p_172476_, float p_172477_) {
      float f = Mth.sin(((float)this.age + p_172477_ - ((float)Math.PI * 2F)) * 0.05F) * 2.0F;
      float f1 = Mth.lerp(p_172477_, this.yRotO, this.yRot);
      float f2 = 1.0472F;
      this.renderSignal(p_172475_, p_172476_, p_172477_, (p_172487_) -> {
         p_172487_.mul(Vector3f.YP.rotation(f1));
         p_172487_.mul(Vector3f.XP.rotation(-1.0472F));
         p_172487_.mul(Vector3f.YP.rotation(f));
      });
      this.renderSignal(p_172475_, p_172476_, p_172477_, (p_172473_) -> {
         p_172473_.mul(Vector3f.YP.rotation(-(float)Math.PI + f1));
         p_172473_.mul(Vector3f.XP.rotation(1.0472F));
         p_172473_.mul(Vector3f.YP.rotation(f));
      });
   }

   private void renderSignal(VertexConsumer p_172479_, Camera p_172480_, float p_172481_, Consumer<Quaternion> p_172482_) {
      Vec3 vec3 = p_172480_.getPosition();
      float f = (float)(Mth.lerp((double)p_172481_, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp((double)p_172481_, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp((double)p_172481_, this.zo, this.z) - vec3.z());
      Vector3f vector3f = new Vector3f(0.5F, 0.5F, 0.5F);
      vector3f.normalize();
      Quaternion quaternion = new Quaternion(vector3f, 0.0F, true);
      p_172482_.accept(quaternion);
      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f3 = this.getQuadSize(p_172481_);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f2 = avector3f[i];
         vector3f2.transform(quaternion);
         vector3f2.mul(f3);
         vector3f2.add(f, f1, f2);
      }

      float f6 = this.getU0();
      float f7 = this.getU1();
      float f4 = this.getV0();
      float f5 = this.getV1();
      int j = this.getLightColor(p_172481_);
      p_172479_.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
   }

   public int getLightColor(float p_172469_) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      Optional<BlockPos> optional = this.vibrationPath.getDestination().getPosition(this.level);
      if (!optional.isPresent()) {
         this.remove();
      } else {
         double d0 = (double)this.age / (double)this.lifetime;
         BlockPos blockpos = this.vibrationPath.getOrigin();
         BlockPos blockpos1 = optional.get();
         this.x = Mth.lerp(d0, (double)blockpos.getX() + 0.5D, (double)blockpos1.getX() + 0.5D);
         this.y = Mth.lerp(d0, (double)blockpos.getY() + 0.5D, (double)blockpos1.getY() + 0.5D);
         this.z = Mth.lerp(d0, (double)blockpos.getZ() + 0.5D, (double)blockpos1.getZ() + 0.5D);
         this.yRotO = this.yRot;
         this.yRot = (float)Mth.atan2(this.x - (double)blockpos1.getX(), this.z - (double)blockpos1.getZ());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<VibrationParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_172490_) {
         this.sprite = p_172490_;
      }

      public Particle createParticle(VibrationParticleOption p_172501_, ClientLevel p_172502_, double p_172503_, double p_172504_, double p_172505_, double p_172506_, double p_172507_, double p_172508_) {
         VibrationSignalParticle vibrationsignalparticle = new VibrationSignalParticle(p_172502_, p_172501_.getVibrationPath(), p_172501_.getVibrationPath().getArrivalInTicks());
         vibrationsignalparticle.pickSprite(this.sprite);
         vibrationsignalparticle.setAlpha(1.0F);
         return vibrationsignalparticle;
      }
   }
}