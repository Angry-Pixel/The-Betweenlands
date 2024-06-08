package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HierarchicalModel<E extends Entity> extends EntityModel<E> {
   public HierarchicalModel() {
      this(RenderType::entityCutoutNoCull);
   }

   public HierarchicalModel(Function<ResourceLocation, RenderType> p_170623_) {
      super(p_170623_);
   }

   public void renderToBuffer(PoseStack p_170625_, VertexConsumer p_170626_, int p_170627_, int p_170628_, float p_170629_, float p_170630_, float p_170631_, float p_170632_) {
      this.root().render(p_170625_, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, p_170632_);
   }

   public abstract ModelPart root();
}