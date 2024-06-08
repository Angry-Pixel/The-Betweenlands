package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
   private final A innerModel;
   private final A outerModel;

   public HumanoidArmorLayer(RenderLayerParent<T, M> p_117075_, A p_117076_, A p_117077_) {
      super(p_117075_);
      this.innerModel = p_117076_;
      this.outerModel = p_117077_;
   }

   public void render(PoseStack p_117096_, MultiBufferSource p_117097_, int p_117098_, T p_117099_, float p_117100_, float p_117101_, float p_117102_, float p_117103_, float p_117104_, float p_117105_) {
      this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.CHEST, p_117098_, this.getArmorModel(EquipmentSlot.CHEST));
      this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.LEGS, p_117098_, this.getArmorModel(EquipmentSlot.LEGS));
      this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.FEET, p_117098_, this.getArmorModel(EquipmentSlot.FEET));
      this.renderArmorPiece(p_117096_, p_117097_, p_117099_, EquipmentSlot.HEAD, p_117098_, this.getArmorModel(EquipmentSlot.HEAD));
   }

   private void renderArmorPiece(PoseStack p_117119_, MultiBufferSource p_117120_, T p_117121_, EquipmentSlot p_117122_, int p_117123_, A p_117124_) {
      ItemStack itemstack = p_117121_.getItemBySlot(p_117122_);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getSlot() == p_117122_) {
            this.getParentModel().copyPropertiesTo(p_117124_);
            this.setPartVisibility(p_117124_, p_117122_);
            net.minecraft.client.model.Model model = getArmorModelHook(p_117121_, itemstack, p_117122_, p_117124_);
            boolean flag = this.usesInnerModel(p_117122_);
            boolean flag1 = itemstack.hasFoil();
            if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) {
               int i = ((net.minecraft.world.item.DyeableLeatherItem)armoritem).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, f, f1, f2, this.getArmorResource(p_117121_, itemstack, p_117122_, null));
               this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_117121_, itemstack, p_117122_, "overlay"));
            } else {
               this.renderModel(p_117119_, p_117120_, p_117123_, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_117121_, itemstack, p_117122_, null));
            }

         }
      }
   }

   protected void setPartVisibility(A p_117126_, EquipmentSlot p_117127_) {
      p_117126_.setAllVisible(false);
      switch(p_117127_) {
      case HEAD:
         p_117126_.head.visible = true;
         p_117126_.hat.visible = true;
         break;
      case CHEST:
         p_117126_.body.visible = true;
         p_117126_.rightArm.visible = true;
         p_117126_.leftArm.visible = true;
         break;
      case LEGS:
         p_117126_.body.visible = true;
         p_117126_.rightLeg.visible = true;
         p_117126_.leftLeg.visible = true;
         break;
      case FEET:
         p_117126_.rightLeg.visible = true;
         p_117126_.leftLeg.visible = true;
      }

   }

   private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, ArmorItem p_117110_, boolean p_117111_, A p_117112_, boolean p_117113_, float p_117114_, float p_117115_, float p_117116_, @Nullable String p_117117_) {
       renderModel(p_117107_, p_117108_, p_117109_, p_117111_, p_117112_, p_117114_, p_117115_, p_117116_, this.getArmorLocation(p_117110_, p_117113_, p_117117_));
   }
   private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
      VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
      p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, p_117114_, p_117115_, p_117116_, 1.0F);
   }

   private A getArmorModel(EquipmentSlot p_117079_) {
      return (A)(this.usesInnerModel(p_117079_) ? this.innerModel : this.outerModel);
   }

   private boolean usesInnerModel(EquipmentSlot p_117129_) {
      return p_117129_ == EquipmentSlot.LEGS;
   }

   @Deprecated //Use the more sensitive version getArmorResource below
   private ResourceLocation getArmorLocation(ArmorItem p_117081_, boolean p_117082_, @Nullable String p_117083_) {
      String s = "textures/models/armor/" + p_117081_.getMaterial().getName() + "_layer_" + (p_117082_ ? 2 : 1) + (p_117083_ == null ? "" : "_" + p_117083_) + ".png";
      return ARMOR_LOCATION_CACHE.computeIfAbsent(s, ResourceLocation::new);
   }

   /*=================================== FORGE START =========================================*/

   /**
    * Hook to allow item-sensitive armor model. for HumanoidArmorLayer.
    */
   protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }

   /**
    * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
    *
    * @param entity Entity wearing the armor
    * @param stack ItemStack for the armor
    * @param slot Slot ID that the item is in
    * @param type Subtype, can be null or "overlay"
    * @return ResourceLocation pointing at the armor's texture
    */
   public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getMaterial().getName();
      String domain = "minecraft";
      int idx = texture.indexOf(':');
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

      s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
      }

      return resourcelocation;
   }
   /*=================================== FORGE END ===========================================*/
}
