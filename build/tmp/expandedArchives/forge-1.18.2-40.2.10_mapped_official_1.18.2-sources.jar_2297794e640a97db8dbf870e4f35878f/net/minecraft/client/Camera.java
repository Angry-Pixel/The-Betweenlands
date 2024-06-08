package net.minecraft.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Camera {
   private boolean initialized;
   private BlockGetter level;
   private Entity entity;
   private Vec3 position = Vec3.ZERO;
   private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
   private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
   private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
   private float xRot;
   private float yRot;
   private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private boolean detached;
   private float eyeHeight;
   private float eyeHeightOld;
   public static final float FOG_DISTANCE_SCALE = 0.083333336F;

   public void setup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_) {
      this.initialized = true;
      this.level = p_90576_;
      this.entity = p_90577_;
      this.detached = p_90578_;
      this.setRotation(p_90577_.getViewYRot(p_90580_), p_90577_.getViewXRot(p_90580_));
      this.setPosition(Mth.lerp((double)p_90580_, p_90577_.xo, p_90577_.getX()), Mth.lerp((double)p_90580_, p_90577_.yo, p_90577_.getY()) + (double)Mth.lerp(p_90580_, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)p_90580_, p_90577_.zo, p_90577_.getZ()));
      if (p_90578_) {
         if (p_90579_) {
            this.setRotation(this.yRot + 180.0F, -this.xRot);
         }

         this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
      } else if (p_90577_ instanceof LivingEntity && ((LivingEntity)p_90577_).isSleeping()) {
         Direction direction = ((LivingEntity)p_90577_).getBedOrientation();
         this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
         this.move(0.0D, 0.3D, 0.0D);
      }

   }

   public void tick() {
      if (this.entity != null) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F;
      }

   }

   private double getMaxZoom(double p_90567_) {
      for(int i = 0; i < 8; ++i) {
         float f = (float)((i & 1) * 2 - 1);
         float f1 = (float)((i >> 1 & 1) * 2 - 1);
         float f2 = (float)((i >> 2 & 1) * 2 - 1);
         f *= 0.1F;
         f1 *= 0.1F;
         f2 *= 0.1F;
         Vec3 vec3 = this.position.add((double)f, (double)f1, (double)f2);
         Vec3 vec31 = new Vec3(this.position.x - (double)this.forwards.x() * p_90567_ + (double)f + (double)f2, this.position.y - (double)this.forwards.y() * p_90567_ + (double)f1, this.position.z - (double)this.forwards.z() * p_90567_ + (double)f2);
         HitResult hitresult = this.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
         if (hitresult.getType() != HitResult.Type.MISS) {
            double d0 = hitresult.getLocation().distanceTo(this.position);
            if (d0 < p_90567_) {
               p_90567_ = d0;
            }
         }
      }

      return p_90567_;
   }

   protected void move(double p_90569_, double p_90570_, double p_90571_) {
      double d0 = (double)this.forwards.x() * p_90569_ + (double)this.up.x() * p_90570_ + (double)this.left.x() * p_90571_;
      double d1 = (double)this.forwards.y() * p_90569_ + (double)this.up.y() * p_90570_ + (double)this.left.y() * p_90571_;
      double d2 = (double)this.forwards.z() * p_90569_ + (double)this.up.z() * p_90570_ + (double)this.left.z() * p_90571_;
      this.setPosition(new Vec3(this.position.x + d0, this.position.y + d1, this.position.z + d2));
   }

   protected void setRotation(float p_90573_, float p_90574_) {
      this.xRot = p_90574_;
      this.yRot = p_90573_;
      this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
      this.rotation.mul(Vector3f.YP.rotationDegrees(-p_90573_));
      this.rotation.mul(Vector3f.XP.rotationDegrees(p_90574_));
      this.forwards.set(0.0F, 0.0F, 1.0F);
      this.forwards.transform(this.rotation);
      this.up.set(0.0F, 1.0F, 0.0F);
      this.up.transform(this.rotation);
      this.left.set(1.0F, 0.0F, 0.0F);
      this.left.transform(this.rotation);
   }

   protected void setPosition(double p_90585_, double p_90586_, double p_90587_) {
      this.setPosition(new Vec3(p_90585_, p_90586_, p_90587_));
   }

   protected void setPosition(Vec3 p_90582_) {
      this.position = p_90582_;
      this.blockPosition.set(p_90582_.x, p_90582_.y, p_90582_.z);
   }

   public Vec3 getPosition() {
      return this.position;
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public float getXRot() {
      return this.xRot;
   }

   public float getYRot() {
      return this.yRot;
   }

   public Quaternion rotation() {
      return this.rotation;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public boolean isDetached() {
      return this.detached;
   }

   public Camera.NearPlane getNearPlane() {
      Minecraft minecraft = Minecraft.getInstance();
      double d0 = (double)minecraft.getWindow().getWidth() / (double)minecraft.getWindow().getHeight();
      double d1 = Math.tan(minecraft.options.fov * (double)((float)Math.PI / 180F) / 2.0D) * (double)0.05F;
      double d2 = d1 * d0;
      Vec3 vec3 = (new Vec3(this.forwards)).scale((double)0.05F);
      Vec3 vec31 = (new Vec3(this.left)).scale(d2);
      Vec3 vec32 = (new Vec3(this.up)).scale(d1);
      return new Camera.NearPlane(vec3, vec31, vec32);
   }

   public FogType getFluidInCamera() {
      if (!this.initialized) {
         return FogType.NONE;
      } else {
         FluidState fluidstate = this.level.getFluidState(this.blockPosition);
         if (fluidstate.is(FluidTags.WATER) && this.position.y < (double)((float)this.blockPosition.getY() + fluidstate.getHeight(this.level, this.blockPosition))) {
            return FogType.WATER;
         } else {
            Camera.NearPlane camera$nearplane = this.getNearPlane();

            for(Vec3 vec3 : Arrays.asList(camera$nearplane.forward, camera$nearplane.getTopLeft(), camera$nearplane.getTopRight(), camera$nearplane.getBottomLeft(), camera$nearplane.getBottomRight())) {
               Vec3 vec31 = this.position.add(vec3);
               BlockPos blockpos = new BlockPos(vec31);
               FluidState fluidstate1 = this.level.getFluidState(blockpos);
               if (fluidstate1.is(FluidTags.LAVA)) {
                  if (vec31.y <= (double)(fluidstate1.getHeight(this.level, blockpos) + (float)blockpos.getY())) {
                     return FogType.LAVA;
                  }
               } else {
                  BlockState blockstate = this.level.getBlockState(blockpos);
                  if (blockstate.is(Blocks.POWDER_SNOW)) {
                     return FogType.POWDER_SNOW;
                  }
               }
            }

            return FogType.NONE;
         }
      }
   }

   public final Vector3f getLookVector() {
      return this.forwards;
   }

   public final Vector3f getUpVector() {
      return this.up;
   }

   public final Vector3f getLeftVector() {
      return this.left;
   }

   public void reset() {
      this.level = null;
      this.entity = null;
      this.initialized = false;
   }

   public void setAnglesInternal(float yaw, float pitch) {
      this.yRot = yaw;
      this.xRot = pitch;
   }

   public net.minecraft.world.level.block.state.BlockState getBlockAtCamera() {
      if (!this.initialized)
         return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
      else
         return this.level.getBlockState(this.blockPosition).getStateAtViewpoint(this.level, this.blockPosition, this.position);
   }

   @OnlyIn(Dist.CLIENT)
   public static class NearPlane {
      final Vec3 forward;
      private final Vec3 left;
      private final Vec3 up;

      NearPlane(Vec3 p_167691_, Vec3 p_167692_, Vec3 p_167693_) {
         this.forward = p_167691_;
         this.left = p_167692_;
         this.up = p_167693_;
      }

      public Vec3 getTopLeft() {
         return this.forward.add(this.up).add(this.left);
      }

      public Vec3 getTopRight() {
         return this.forward.add(this.up).subtract(this.left);
      }

      public Vec3 getBottomLeft() {
         return this.forward.subtract(this.up).add(this.left);
      }

      public Vec3 getBottomRight() {
         return this.forward.subtract(this.up).subtract(this.left);
      }

      public Vec3 getPointOnPlane(float p_167696_, float p_167697_) {
         return this.forward.add(this.up.scale((double)p_167697_)).subtract(this.left.scale((double)p_167696_));
      }
   }
}
