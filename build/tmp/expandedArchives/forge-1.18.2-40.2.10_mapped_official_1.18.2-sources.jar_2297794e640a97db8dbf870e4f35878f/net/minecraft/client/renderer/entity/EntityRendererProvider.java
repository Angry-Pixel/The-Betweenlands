package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface EntityRendererProvider<T extends Entity> {
   EntityRenderer<T> create(EntityRendererProvider.Context p_174010_);

   @OnlyIn(Dist.CLIENT)
   public static class Context {
      private final EntityRenderDispatcher entityRenderDispatcher;
      private final ItemRenderer itemRenderer;
      private final ResourceManager resourceManager;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(EntityRenderDispatcher p_174017_, ItemRenderer p_174018_, ResourceManager p_174019_, EntityModelSet p_174020_, Font p_174021_) {
         this.entityRenderDispatcher = p_174017_;
         this.itemRenderer = p_174018_;
         this.resourceManager = p_174019_;
         this.modelSet = p_174020_;
         this.font = p_174021_;
      }

      public EntityRenderDispatcher getEntityRenderDispatcher() {
         return this.entityRenderDispatcher;
      }

      public ItemRenderer getItemRenderer() {
         return this.itemRenderer;
      }

      public ResourceManager getResourceManager() {
         return this.resourceManager;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelPart bakeLayer(ModelLayerLocation p_174024_) {
         return this.modelSet.bakeLayer(p_174024_);
      }

      public Font getFont() {
         return this.font;
      }
   }
}