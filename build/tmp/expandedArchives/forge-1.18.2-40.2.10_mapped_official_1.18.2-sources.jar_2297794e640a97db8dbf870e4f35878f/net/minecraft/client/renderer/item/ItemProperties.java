package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemProperties {
   private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
   private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
   private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = (p_174660_, p_174661_, p_174662_, p_174663_) -> {
      return p_174660_.isDamaged() ? 1.0F : 0.0F;
   };
   private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = (p_174655_, p_174656_, p_174657_, p_174658_) -> {
      return Mth.clamp((float)p_174655_.getDamageValue() / (float)p_174655_.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

   public static ItemPropertyFunction registerGeneric(ResourceLocation p_174582_, ItemPropertyFunction p_174583_) {
      GENERIC_PROPERTIES.put(p_174582_, p_174583_);
      return p_174583_;
   }

   private static void registerCustomModelData(ItemPropertyFunction p_174580_) {
      GENERIC_PROPERTIES.put(new ResourceLocation("custom_model_data"), p_174580_);
   }

   public static void register(Item p_174571_, ResourceLocation p_174572_, ItemPropertyFunction p_174573_) {
      PROPERTIES.computeIfAbsent(p_174571_, (p_117828_) -> {
         return Maps.newHashMap();
      }).put(p_174572_, p_174573_);
   }

   @Nullable
   public static ItemPropertyFunction getProperty(Item p_117830_, ResourceLocation p_117831_) {
      if (p_117830_.getMaxDamage() > 0) {
         if (DAMAGE.equals(p_117831_)) {
            return PROPERTY_DAMAGE;
         }

         if (DAMAGED.equals(p_117831_)) {
            return PROPERTY_DAMAGED;
         }
      }

      ItemPropertyFunction itempropertyfunction = GENERIC_PROPERTIES.get(p_117831_);
      if (itempropertyfunction != null) {
         return itempropertyfunction;
      } else {
         Map<ResourceLocation, ItemPropertyFunction> map = PROPERTIES.get(p_117830_);
         return map == null ? null : map.get(p_117831_);
      }
   }

   static {
      registerGeneric(new ResourceLocation("lefthanded"), (p_174650_, p_174651_, p_174652_, p_174653_) -> {
         return p_174652_ != null && p_174652_.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F;
      });
      registerGeneric(new ResourceLocation("cooldown"), (p_174645_, p_174646_, p_174647_, p_174648_) -> {
         return p_174647_ instanceof Player ? ((Player)p_174647_).getCooldowns().getCooldownPercent(p_174645_.getItem(), 0.0F) : 0.0F;
      });
      registerCustomModelData((p_174640_, p_174641_, p_174642_, p_174643_) -> {
         return p_174640_.hasTag() ? (float)p_174640_.getTag().getInt("CustomModelData") : 0.0F;
      });
      register(Items.BOW, new ResourceLocation("pull"), (p_174635_, p_174636_, p_174637_, p_174638_) -> {
         if (p_174637_ == null) {
            return 0.0F;
         } else {
            return p_174637_.getUseItem() != p_174635_ ? 0.0F : (float)(p_174635_.getUseDuration() - p_174637_.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(Items.BOW, new ResourceLocation("pulling"), (p_174630_, p_174631_, p_174632_, p_174633_) -> {
         return p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F;
      });
      register(Items.BUNDLE, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> {
         return BundleItem.getFullnessDisplay(p_174625_);
      });
      register(Items.CLOCK, new ResourceLocation("time"), new ClampedItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float unclampedCall(ItemStack p_174665_, @Nullable ClientLevel p_174666_, @Nullable LivingEntity p_174667_, int p_174668_) {
            Entity entity = (Entity)(p_174667_ != null ? p_174667_ : p_174665_.getEntityRepresentation());
            if (entity == null) {
               return 0.0F;
            } else {
               if (p_174666_ == null && entity.level instanceof ClientLevel) {
                  p_174666_ = (ClientLevel)entity.level;
               }

               if (p_174666_ == null) {
                  return 0.0F;
               } else {
                  double d0;
                  if (p_174666_.dimensionType().natural()) {
                     d0 = (double)p_174666_.getTimeOfDay(1.0F);
                  } else {
                     d0 = Math.random();
                  }

                  d0 = this.wobble(p_174666_, d0);
                  return (float)d0;
               }
            }
         }

         private double wobble(Level p_117904_, double p_117905_) {
            if (p_117904_.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = p_117904_.getGameTime();
               double d0 = p_117905_ - this.rotation;
               d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
               this.rota += d0 * 0.1D;
               this.rota *= 0.9D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
      register(Items.COMPASS, new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
         private final ItemProperties.CompassWobble wobble = new ItemProperties.CompassWobble();
         private final ItemProperties.CompassWobble wobbleRandom = new ItemProperties.CompassWobble();

         public float unclampedCall(ItemStack p_174672_, @Nullable ClientLevel p_174673_, @Nullable LivingEntity p_174674_, int p_174675_) {
            Entity entity = (Entity)(p_174674_ != null ? p_174674_ : p_174672_.getEntityRepresentation());
            if (entity == null) {
               return 0.0F;
            } else {
               if (p_174673_ == null && entity.level instanceof ClientLevel) {
                  p_174673_ = (ClientLevel)entity.level;
               }

               BlockPos blockpos = CompassItem.isLodestoneCompass(p_174672_) ? this.getLodestonePosition(p_174673_, p_174672_.getOrCreateTag()) : this.getSpawnPosition(p_174673_);
               long i = p_174673_.getGameTime();
               if (blockpos != null && !(entity.position().distanceToSqr((double)blockpos.getX() + 0.5D, entity.position().y(), (double)blockpos.getZ() + 0.5D) < (double)1.0E-5F)) {
                  boolean flag = p_174674_ instanceof Player && ((Player)p_174674_).isLocalPlayer();
                  double d1 = 0.0D;
                  if (flag) {
                     d1 = (double)p_174674_.getYRot();
                  } else if (entity instanceof ItemFrame) {
                     d1 = this.getFrameRotation((ItemFrame)entity);
                  } else if (entity instanceof ItemEntity) {
                     d1 = (double)(180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F);
                  } else if (p_174674_ != null) {
                     d1 = (double)p_174674_.yBodyRot;
                  }

                  d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
                  double d2 = this.getAngleTo(Vec3.atCenterOf(blockpos), entity) / (double)((float)Math.PI * 2F);
                  double d3;
                  if (flag) {
                     if (this.wobble.shouldUpdate(i)) {
                        this.wobble.update(i, 0.5D - (d1 - 0.25D));
                     }

                     d3 = d2 + this.wobble.rotation;
                  } else {
                     d3 = 0.5D - (d1 - 0.25D - d2);
                  }

                  return Mth.positiveModulo((float)d3, 1.0F);
               } else {
                  if (this.wobbleRandom.shouldUpdate(i)) {
                     this.wobbleRandom.update(i, Math.random());
                  }

                  double d0 = this.wobbleRandom.rotation + (double)((float)this.hash(p_174675_) / 2.14748365E9F);
                  return Mth.positiveModulo((float)d0, 1.0F);
               }
            }
         }

         private int hash(int p_174670_) {
            return p_174670_ * 1327217883;
         }

         @Nullable
         private BlockPos getSpawnPosition(ClientLevel p_117922_) {
            return p_117922_.dimensionType().natural() ? p_117922_.getSharedSpawnPos() : null;
         }

         @Nullable
         private BlockPos getLodestonePosition(Level p_117916_, CompoundTag p_117917_) {
            boolean flag = p_117917_.contains("LodestonePos");
            boolean flag1 = p_117917_.contains("LodestoneDimension");
            if (flag && flag1) {
               Optional<ResourceKey<Level>> optional = CompassItem.getLodestoneDimension(p_117917_);
               if (optional.isPresent() && p_117916_.dimension() == optional.get()) {
                  return NbtUtils.readBlockPos(p_117917_.getCompound("LodestonePos"));
               }
            }

            return null;
         }

         private double getFrameRotation(ItemFrame p_117914_) {
            Direction direction = p_117914_.getDirection();
            int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
            return (double)Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + p_117914_.getRotation() * 45 + i);
         }

         private double getAngleTo(Vec3 p_117919_, Entity p_117920_) {
            return Math.atan2(p_117919_.z() - p_117920_.getZ(), p_117919_.x() - p_117920_.getX());
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pull"), (p_174620_, p_174621_, p_174622_, p_174623_) -> {
         if (p_174622_ == null) {
            return 0.0F;
         } else {
            return CrossbowItem.isCharged(p_174620_) ? 0.0F : (float)(p_174620_.getUseDuration() - p_174622_.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(p_174620_);
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pulling"), (p_174615_, p_174616_, p_174617_, p_174618_) -> {
         return p_174617_ != null && p_174617_.isUsingItem() && p_174617_.getUseItem() == p_174615_ && !CrossbowItem.isCharged(p_174615_) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("charged"), (p_174610_, p_174611_, p_174612_, p_174613_) -> {
         return p_174612_ != null && CrossbowItem.isCharged(p_174610_) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("firework"), (p_174605_, p_174606_, p_174607_, p_174608_) -> {
         return p_174607_ != null && CrossbowItem.isCharged(p_174605_) && CrossbowItem.containsChargedProjectile(p_174605_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
      register(Items.ELYTRA, new ResourceLocation("broken"), (p_174600_, p_174601_, p_174602_, p_174603_) -> {
         return ElytraItem.isFlyEnabled(p_174600_) ? 0.0F : 1.0F;
      });
      register(Items.FISHING_ROD, new ResourceLocation("cast"), (p_174595_, p_174596_, p_174597_, p_174598_) -> {
         if (p_174597_ == null) {
            return 0.0F;
         } else {
            boolean flag = p_174597_.getMainHandItem() == p_174595_;
            boolean flag1 = p_174597_.getOffhandItem() == p_174595_;
            if (p_174597_.getMainHandItem().getItem() instanceof FishingRodItem) {
               flag1 = false;
            }

            return (flag || flag1) && p_174597_ instanceof Player && ((Player)p_174597_).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(Items.SHIELD, new ResourceLocation("blocking"), (p_174590_, p_174591_, p_174592_, p_174593_) -> {
         return p_174592_ != null && p_174592_.isUsingItem() && p_174592_.getUseItem() == p_174590_ ? 1.0F : 0.0F;
      });
      register(Items.TRIDENT, new ResourceLocation("throwing"), (p_174585_, p_174586_, p_174587_, p_174588_) -> {
         return p_174587_ != null && p_174587_.isUsingItem() && p_174587_.getUseItem() == p_174585_ ? 1.0F : 0.0F;
      });
      register(Items.LIGHT, new ResourceLocation("level"), (p_174575_, p_174576_, p_174577_, p_174578_) -> {
         CompoundTag compoundtag = p_174575_.getTagElement("BlockStateTag");

         try {
            if (compoundtag != null) {
               Tag tag = compoundtag.get(LightBlock.LEVEL.getName());
               if (tag != null) {
                  return (float)Integer.parseInt(tag.getAsString()) / 16.0F;
               }
            }
         } catch (NumberFormatException numberformatexception) {
         }

         return 1.0F;
      });
   }

   @OnlyIn(Dist.CLIENT)
   static class CompassWobble {
      double rotation;
      private double deltaRotation;
      private long lastUpdateTick;

      boolean shouldUpdate(long p_117934_) {
         return this.lastUpdateTick != p_117934_;
      }

      void update(long p_117936_, double p_117937_) {
         this.lastUpdateTick = p_117936_;
         double d0 = p_117937_ - this.rotation;
         d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
         this.deltaRotation += d0 * 0.1D;
         this.deltaRotation *= 0.8D;
         this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
      }
   }
}
