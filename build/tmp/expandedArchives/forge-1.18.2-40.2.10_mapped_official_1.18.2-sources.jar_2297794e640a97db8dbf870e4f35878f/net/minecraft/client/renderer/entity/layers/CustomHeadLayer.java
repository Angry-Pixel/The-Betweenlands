package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
   private final float scaleX;
   private final float scaleY;
   private final float scaleZ;
   private final Map<SkullBlock.Type, SkullModelBase> skullModels;

   public CustomHeadLayer(RenderLayerParent<T, M> p_174475_, EntityModelSet p_174476_) {
      this(p_174475_, p_174476_, 1.0F, 1.0F, 1.0F);
   }

   public CustomHeadLayer(RenderLayerParent<T, M> p_174478_, EntityModelSet p_174479_, float p_174480_, float p_174481_, float p_174482_) {
      super(p_174478_);
      this.scaleX = p_174480_;
      this.scaleY = p_174481_;
      this.scaleZ = p_174482_;
      this.skullModels = SkullBlockRenderer.createSkullRenderers(p_174479_);
   }

   public void render(PoseStack p_116731_, MultiBufferSource p_116732_, int p_116733_, T p_116734_, float p_116735_, float p_116736_, float p_116737_, float p_116738_, float p_116739_, float p_116740_) {
      ItemStack itemstack = p_116734_.getItemBySlot(EquipmentSlot.HEAD);
      if (!itemstack.isEmpty()) {
         Item item = itemstack.getItem();
         p_116731_.pushPose();
         p_116731_.scale(this.scaleX, this.scaleY, this.scaleZ);
         boolean flag = p_116734_ instanceof Villager || p_116734_ instanceof ZombieVillager;
         if (p_116734_.isBaby() && !(p_116734_ instanceof Villager)) {
            float f = 2.0F;
            float f1 = 1.4F;
            p_116731_.translate(0.0D, 0.03125D, 0.0D);
            p_116731_.scale(0.7F, 0.7F, 0.7F);
            p_116731_.translate(0.0D, 1.0D, 0.0D);
         }

         this.getParentModel().getHead().translateAndRotate(p_116731_);
         if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            float f2 = 1.1875F;
            p_116731_.scale(1.1875F, -1.1875F, -1.1875F);
            if (flag) {
               p_116731_.translate(0.0D, 0.0625D, 0.0D);
            }

            GameProfile gameprofile = null;
            if (itemstack.hasTag()) {
               CompoundTag compoundtag = itemstack.getTag();
               if (compoundtag.contains("SkullOwner", 10)) {
                  gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
               }
            }

            p_116731_.translate(-0.5D, 0.0D, -0.5D);
            SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
            SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
            RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, gameprofile);
            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, p_116735_, p_116731_, p_116732_, p_116733_, skullmodelbase, rendertype);
         } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlot() != EquipmentSlot.HEAD) {
            translateToHead(p_116731_, flag);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(p_116734_, itemstack, ItemTransforms.TransformType.HEAD, false, p_116731_, p_116732_, p_116733_);
         }

         p_116731_.popPose();
      }
   }

   public static void translateToHead(PoseStack p_174484_, boolean p_174485_) {
      float f = 0.625F;
      p_174484_.translate(0.0D, -0.25D, 0.0D);
      p_174484_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      p_174484_.scale(0.625F, -0.625F, -0.625F);
      if (p_174485_) {
         p_174484_.translate(0.0D, 0.1875D, 0.0D);
      }

   }
}