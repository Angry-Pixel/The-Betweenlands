package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float EYE_BED_OFFSET = 0.1F;
   protected M model;
   protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

   public LivingEntityRenderer(EntityRendererProvider.Context p_174289_, M p_174290_, float p_174291_) {
      super(p_174289_);
      this.model = p_174290_;
      this.shadowRadius = p_174291_;
   }

   public final boolean addLayer(RenderLayer<T, M> p_115327_) {
      return this.layers.add(p_115327_);
   }

   public M getModel() {
      return this.model;
   }

   public void render(T p_115308_, float p_115309_, float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<T, M>(p_115308_, this, p_115310_, p_115311_, p_115312_, p_115313_))) return;
      p_115311_.pushPose();
      this.model.attackTime = this.getAttackAnim(p_115308_, p_115310_);

      boolean shouldSit = p_115308_.isPassenger() && (p_115308_.getVehicle() != null && p_115308_.getVehicle().shouldRiderSit());
      this.model.riding = shouldSit;
      this.model.young = p_115308_.isBaby();
      float f = Mth.rotLerp(p_115310_, p_115308_.yBodyRotO, p_115308_.yBodyRot);
      float f1 = Mth.rotLerp(p_115310_, p_115308_.yHeadRotO, p_115308_.yHeadRot);
      float f2 = f1 - f;
      if (shouldSit && p_115308_.getVehicle() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_115308_.getVehicle();
         f = Mth.rotLerp(p_115310_, livingentity.yBodyRotO, livingentity.yBodyRot);
         f2 = f1 - f;
         float f3 = Mth.wrapDegrees(f2);
         if (f3 < -85.0F) {
            f3 = -85.0F;
         }

         if (f3 >= 85.0F) {
            f3 = 85.0F;
         }

         f = f1 - f3;
         if (f3 * f3 > 2500.0F) {
            f += f3 * 0.2F;
         }

         f2 = f1 - f;
      }

      float f6 = Mth.lerp(p_115310_, p_115308_.xRotO, p_115308_.getXRot());
      if (isEntityUpsideDown(p_115308_)) {
         f6 *= -1.0F;
         f2 *= -1.0F;
      }

      if (p_115308_.getPose() == Pose.SLEEPING) {
         Direction direction = p_115308_.getBedOrientation();
         if (direction != null) {
            float f4 = p_115308_.getEyeHeight(Pose.STANDING) - 0.1F;
            p_115311_.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
         }
      }

      float f7 = this.getBob(p_115308_, p_115310_);
      this.setupRotations(p_115308_, p_115311_, f7, f, p_115310_);
      p_115311_.scale(-1.0F, -1.0F, 1.0F);
      this.scale(p_115308_, p_115311_, p_115310_);
      p_115311_.translate(0.0D, (double)-1.501F, 0.0D);
      float f8 = 0.0F;
      float f5 = 0.0F;
      if (!shouldSit && p_115308_.isAlive()) {
         f8 = Mth.lerp(p_115310_, p_115308_.animationSpeedOld, p_115308_.animationSpeed);
         f5 = p_115308_.animationPosition - p_115308_.animationSpeed * (1.0F - p_115310_);
         if (p_115308_.isBaby()) {
            f5 *= 3.0F;
         }

         if (f8 > 1.0F) {
            f8 = 1.0F;
         }
      }

      this.model.prepareMobModel(p_115308_, f5, f8, p_115310_);
      this.model.setupAnim(p_115308_, f5, f8, f7, f2, f6);
      Minecraft minecraft = Minecraft.getInstance();
      boolean flag = this.isBodyVisible(p_115308_);
      boolean flag1 = !flag && !p_115308_.isInvisibleTo(minecraft.player);
      boolean flag2 = minecraft.shouldEntityAppearGlowing(p_115308_);
      RenderType rendertype = this.getRenderType(p_115308_, flag, flag1, flag2);
      if (rendertype != null) {
         VertexConsumer vertexconsumer = p_115312_.getBuffer(rendertype);
         int i = getOverlayCoords(p_115308_, this.getWhiteOverlayProgress(p_115308_, p_115310_));
         this.model.renderToBuffer(p_115311_, vertexconsumer, p_115313_, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
      }

      if (!p_115308_.isSpectator()) {
         for(RenderLayer<T, M> renderlayer : this.layers) {
            renderlayer.render(p_115311_, p_115312_, p_115313_, p_115308_, f5, f8, p_115310_, f7, f2, f6);
         }
      }

      p_115311_.popPose();
      super.render(p_115308_, p_115309_, p_115310_, p_115311_, p_115312_, p_115313_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<T, M>(p_115308_, this, p_115310_, p_115311_, p_115312_, p_115313_));
   }

   @Nullable
   protected RenderType getRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
      ResourceLocation resourcelocation = this.getTextureLocation(p_115322_);
      if (p_115324_) {
         return RenderType.itemEntityTranslucentCull(resourcelocation);
      } else if (p_115323_) {
         return this.model.renderType(resourcelocation);
      } else {
         return p_115325_ ? RenderType.outline(resourcelocation) : null;
      }
   }

   public static int getOverlayCoords(LivingEntity p_115339_, float p_115340_) {
      return OverlayTexture.pack(OverlayTexture.u(p_115340_), OverlayTexture.v(p_115339_.hurtTime > 0 || p_115339_.deathTime > 0));
   }

   protected boolean isBodyVisible(T p_115341_) {
      return !p_115341_.isInvisible();
   }

   private static float sleepDirectionToRotation(Direction p_115329_) {
      switch(p_115329_) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected boolean isShaking(T p_115304_) {
      return p_115304_.isFullyFrozen();
   }

   protected void setupRotations(T p_115317_, PoseStack p_115318_, float p_115319_, float p_115320_, float p_115321_) {
      if (this.isShaking(p_115317_)) {
         p_115320_ += (float)(Math.cos((double)p_115317_.tickCount * 3.25D) * Math.PI * (double)0.4F);
      }

      Pose pose = p_115317_.getPose();
      if (pose != Pose.SLEEPING) {
         p_115318_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_115320_));
      }

      if (p_115317_.deathTime > 0) {
         float f = ((float)p_115317_.deathTime + p_115321_ - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         p_115318_.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(p_115317_)));
      } else if (p_115317_.isAutoSpinAttack()) {
         p_115318_.mulPose(Vector3f.XP.rotationDegrees(-90.0F - p_115317_.getXRot()));
         p_115318_.mulPose(Vector3f.YP.rotationDegrees(((float)p_115317_.tickCount + p_115321_) * -75.0F));
      } else if (pose == Pose.SLEEPING) {
         Direction direction = p_115317_.getBedOrientation();
         float f1 = direction != null ? sleepDirectionToRotation(direction) : p_115320_;
         p_115318_.mulPose(Vector3f.YP.rotationDegrees(f1));
         p_115318_.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(p_115317_)));
         p_115318_.mulPose(Vector3f.YP.rotationDegrees(270.0F));
      } else if (isEntityUpsideDown(p_115317_)) {
         p_115318_.translate(0.0D, (double)(p_115317_.getBbHeight() + 0.1F), 0.0D);
         p_115318_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      }

   }

   protected float getAttackAnim(T p_115343_, float p_115344_) {
      return p_115343_.getAttackAnim(p_115344_);
   }

   protected float getBob(T p_115305_, float p_115306_) {
      return (float)p_115305_.tickCount + p_115306_;
   }

   protected float getFlipDegrees(T p_115337_) {
      return 90.0F;
   }

   protected float getWhiteOverlayProgress(T p_115334_, float p_115335_) {
      return 0.0F;
   }

   protected void scale(T p_115314_, PoseStack p_115315_, float p_115316_) {
   }

   protected boolean shouldShowName(T p_115333_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_115333_);
      float f = p_115333_.isDiscrete() ? 32.0F : 64.0F;
      if (d0 >= (double)(f * f)) {
         return false;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer localplayer = minecraft.player;
         boolean flag = !p_115333_.isInvisibleTo(localplayer);
         if (p_115333_ != localplayer) {
            Team team = p_115333_.getTeam();
            Team team1 = localplayer.getTeam();
            if (team != null) {
               Team.Visibility team$visibility = team.getNameTagVisibility();
               switch(team$visibility) {
               case ALWAYS:
                  return flag;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team1 == null ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
               case HIDE_FOR_OWN_TEAM:
                  return team1 == null ? flag : !team.isAlliedTo(team1) && flag;
               default:
                  return true;
               }
            }
         }

         return Minecraft.renderNames() && p_115333_ != minecraft.getCameraEntity() && flag && !p_115333_.isVehicle();
      }
   }

   public static boolean isEntityUpsideDown(LivingEntity p_194454_) {
      if (p_194454_ instanceof Player || p_194454_.hasCustomName()) {
         String s = ChatFormatting.stripFormatting(p_194454_.getName().getString());
         if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
            return !(p_194454_ instanceof Player) || ((Player)p_194454_).isModelPartShown(PlayerModelPart.CAPE);
         }
      }

      return false;
   }
}
