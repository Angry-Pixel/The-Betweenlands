package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
   public float attackTime;
   public boolean riding;
   public boolean young = true;

   protected EntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected EntityModel(Function<ResourceLocation, RenderType> p_102613_) {
      super(p_102613_);
   }

   public abstract void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

   public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
   }

   public void copyPropertiesTo(EntityModel<T> p_102625_) {
      p_102625_.attackTime = this.attackTime;
      p_102625_.riding = this.riding;
      p_102625_.young = this.young;
   }
}