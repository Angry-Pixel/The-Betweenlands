package net.minecraft.data.models.model;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
   /** @deprecated */
   @Deprecated
   public static ResourceLocation decorateBlockModelLocation(String p_125582_) {
      return new ResourceLocation("minecraft", "block/" + p_125582_);
   }

   public static ResourceLocation decorateItemModelLocation(String p_125584_) {
      return new ResourceLocation("minecraft", "item/" + p_125584_);
   }

   public static ResourceLocation getModelLocation(Block p_125579_, String p_125580_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_125579_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath() + p_125580_);
   }

   public static ResourceLocation getModelLocation(Block p_125577_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_125577_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath());
   }

   public static ResourceLocation getModelLocation(Item p_125572_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_125572_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
   }

   public static ResourceLocation getModelLocation(Item p_125574_, String p_125575_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_125574_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath() + p_125575_);
   }
}