package net.minecraft.client.model.geom.builders;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDefinition {
   private final MeshDefinition mesh;
   private final MaterialDefinition material;

   private LayerDefinition(MeshDefinition p_171562_, MaterialDefinition p_171563_) {
      this.mesh = p_171562_;
      this.material = p_171563_;
   }

   public ModelPart bakeRoot() {
      return this.mesh.getRoot().bake(this.material.xTexSize, this.material.yTexSize);
   }

   public static LayerDefinition create(MeshDefinition p_171566_, int p_171567_, int p_171568_) {
      return new LayerDefinition(p_171566_, new MaterialDefinition(p_171567_, p_171568_));
   }
}