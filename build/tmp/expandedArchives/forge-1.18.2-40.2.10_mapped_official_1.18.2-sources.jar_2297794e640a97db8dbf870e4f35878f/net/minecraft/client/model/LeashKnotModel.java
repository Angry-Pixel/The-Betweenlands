package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeashKnotModel<T extends Entity> extends HierarchicalModel<T> {
   private static final String KNOT = "knot";
   private final ModelPart root;
   private final ModelPart knot;

   public LeashKnotModel(ModelPart p_170714_) {
      this.root = p_170714_;
      this.knot = p_170714_.getChild("knot");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("knot", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T p_103003_, float p_103004_, float p_103005_, float p_103006_, float p_103007_, float p_103008_) {
      this.knot.yRot = p_103007_ * ((float)Math.PI / 180F);
      this.knot.xRot = p_103008_ * ((float)Math.PI / 180F);
   }
}