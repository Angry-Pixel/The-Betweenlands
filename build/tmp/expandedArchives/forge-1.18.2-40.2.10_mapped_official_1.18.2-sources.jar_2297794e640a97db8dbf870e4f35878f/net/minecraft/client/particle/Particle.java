package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Particle {
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0D);
   protected final ClientLevel level;
   protected double xo;
   protected double yo;
   protected double zo;
   protected double x;
   protected double y;
   protected double z;
   protected double xd;
   protected double yd;
   protected double zd;
   private AABB bb = INITIAL_AABB;
   protected boolean onGround;
   protected boolean hasPhysics = true;
   private boolean stoppedByCollision;
   protected boolean removed;
   protected float bbWidth = 0.6F;
   protected float bbHeight = 1.8F;
   protected final Random random = new Random();
   protected int age;
   protected int lifetime;
   protected float gravity;
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float alpha = 1.0F;
   protected float roll;
   protected float oRoll;
   protected float friction = 0.98F;
   protected boolean speedUpWhenYMotionIsBlocked = false;

   protected Particle(ClientLevel p_107234_, double p_107235_, double p_107236_, double p_107237_) {
      this.level = p_107234_;
      this.setSize(0.2F, 0.2F);
      this.setPos(p_107235_, p_107236_, p_107237_);
      this.xo = p_107235_;
      this.yo = p_107236_;
      this.zo = p_107237_;
      this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(ClientLevel p_107239_, double p_107240_, double p_107241_, double p_107242_, double p_107243_, double p_107244_, double p_107245_) {
      this(p_107239_, p_107240_, p_107241_, p_107242_);
      this.xd = p_107243_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.yd = p_107244_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.zd = p_107245_ + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      double d0 = (Math.random() + Math.random() + 1.0D) * (double)0.15F;
      double d1 = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / d1 * d0 * (double)0.4F;
      this.yd = this.yd / d1 * d0 * (double)0.4F + (double)0.1F;
      this.zd = this.zd / d1 * d0 * (double)0.4F;
   }

   public Particle setPower(float p_107269_) {
      this.xd *= (double)p_107269_;
      this.yd = (this.yd - (double)0.1F) * (double)p_107269_ + (double)0.1F;
      this.zd *= (double)p_107269_;
      return this;
   }

   public void setParticleSpeed(double p_172261_, double p_172262_, double p_172263_) {
      this.xd = p_172261_;
      this.yd = p_172262_;
      this.zd = p_172263_;
   }

   public Particle scale(float p_107270_) {
      this.setSize(0.2F * p_107270_, 0.2F * p_107270_);
      return this;
   }

   public void setColor(float p_107254_, float p_107255_, float p_107256_) {
      this.rCol = p_107254_;
      this.gCol = p_107255_;
      this.bCol = p_107256_;
   }

   protected void setAlpha(float p_107272_) {
      this.alpha = p_107272_;
   }

   public void setLifetime(int p_107258_) {
      this.lifetime = p_107258_;
   }

   public int getLifetime() {
      return this.lifetime;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.04D * (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= (double)this.friction;
         this.yd *= (double)this.friction;
         this.zd *= (double)this.friction;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   public abstract void render(VertexConsumer p_107261_, Camera p_107262_, float p_107263_);

   public abstract ParticleRenderType getRenderType();

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public void remove() {
      this.removed = true;
   }

   protected void setSize(float p_107251_, float p_107252_) {
      if (p_107251_ != this.bbWidth || p_107252_ != this.bbHeight) {
         this.bbWidth = p_107251_;
         this.bbHeight = p_107252_;
         AABB aabb = this.getBoundingBox();
         double d0 = (aabb.minX + aabb.maxX - (double)p_107251_) / 2.0D;
         double d1 = (aabb.minZ + aabb.maxZ - (double)p_107251_) / 2.0D;
         this.setBoundingBox(new AABB(d0, aabb.minY, d1, d0 + (double)this.bbWidth, aabb.minY + (double)this.bbHeight, d1 + (double)this.bbWidth));
      }

   }

   public void setPos(double p_107265_, double p_107266_, double p_107267_) {
      this.x = p_107265_;
      this.y = p_107266_;
      this.z = p_107267_;
      float f = this.bbWidth / 2.0F;
      float f1 = this.bbHeight;
      this.setBoundingBox(new AABB(p_107265_ - (double)f, p_107266_, p_107267_ - (double)f, p_107265_ + (double)f, p_107266_ + (double)f1, p_107267_ + (double)f));
   }

   public void move(double p_107246_, double p_107247_, double p_107248_) {
      if (!this.stoppedByCollision) {
         double d0 = p_107246_;
         double d1 = p_107247_;
         double d2 = p_107248_;
         if (this.hasPhysics && (p_107246_ != 0.0D || p_107247_ != 0.0D || p_107248_ != 0.0D) && p_107246_ * p_107246_ + p_107247_ * p_107247_ + p_107248_ * p_107248_ < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 vec3 = Entity.collideBoundingBox((Entity)null, new Vec3(p_107246_, p_107247_, p_107248_), this.getBoundingBox(), this.level, List.of());
            p_107246_ = vec3.x;
            p_107247_ = vec3.y;
            p_107248_ = vec3.z;
         }

         if (p_107246_ != 0.0D || p_107247_ != 0.0D || p_107248_ != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().move(p_107246_, p_107247_, p_107248_));
            this.setLocationFromBoundingbox();
         }

         if (Math.abs(d1) >= (double)1.0E-5F && Math.abs(p_107247_) < (double)1.0E-5F) {
            this.stoppedByCollision = true;
         }

         this.onGround = d1 != p_107247_ && d1 < 0.0D;
         if (d0 != p_107246_) {
            this.xd = 0.0D;
         }

         if (d2 != p_107248_) {
            this.zd = 0.0D;
         }

      }
   }

   protected void setLocationFromBoundingbox() {
      AABB aabb = this.getBoundingBox();
      this.x = (aabb.minX + aabb.maxX) / 2.0D;
      this.y = aabb.minY;
      this.z = (aabb.minZ + aabb.maxZ) / 2.0D;
   }

   protected int getLightColor(float p_107249_) {
      BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
      return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public AABB getBoundingBox() {
      return this.bb;
   }

   public void setBoundingBox(AABB p_107260_) {
      this.bb = p_107260_;
   }

   public Optional<ParticleGroup> getParticleGroup() {
      return Optional.empty();
   }

    /**
     * Forge added method that controls if a particle should be culled to it's bounding box.
     * Default behaviour is culling enabled
     */
    public boolean shouldCull() {
        return true;
    }
}
