package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestedHorseModel<T extends AbstractChestedHorse> extends HorseModel<T> {
   private final ModelPart leftChest = this.body.getChild("left_chest");
   private final ModelPart rightChest = this.body.getChild("right_chest");

   public ChestedHorseModel(ModelPart p_170482_) {
      super(p_170482_);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HorseModel.createBodyMesh(CubeDeformation.NONE);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.getChild("body");
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(26, 21).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      partdefinition1.addOrReplaceChild("left_chest", cubelistbuilder, PartPose.offsetAndRotation(6.0F, -8.0F, 0.0F, 0.0F, (-(float)Math.PI / 2F), 0.0F));
      partdefinition1.addOrReplaceChild("right_chest", cubelistbuilder, PartPose.offsetAndRotation(-6.0F, -8.0F, 0.0F, 0.0F, ((float)Math.PI / 2F), 0.0F));
      PartDefinition partdefinition2 = partdefinition.getChild("head_parts").getChild("head");
      CubeListBuilder cubelistbuilder1 = CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      partdefinition2.addOrReplaceChild("left_ear", cubelistbuilder1, PartPose.offsetAndRotation(1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, 0.2617994F));
      partdefinition2.addOrReplaceChild("right_ear", cubelistbuilder1, PartPose.offsetAndRotation(-1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, -0.2617994F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T p_102366_, float p_102367_, float p_102368_, float p_102369_, float p_102370_, float p_102371_) {
      super.setupAnim(p_102366_, p_102367_, p_102368_, p_102369_, p_102370_, p_102371_);
      if (p_102366_.hasChest()) {
         this.leftChest.visible = true;
         this.rightChest.visible = true;
      } else {
         this.leftChest.visible = false;
         this.rightChest.visible = false;
      }

   }
}