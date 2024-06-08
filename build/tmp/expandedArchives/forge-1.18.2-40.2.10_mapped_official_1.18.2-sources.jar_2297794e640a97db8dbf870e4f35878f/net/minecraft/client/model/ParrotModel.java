package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotModel extends HierarchicalModel<Parrot> {
   private static final String FEATHER = "feather";
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart feather;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(ModelPart p_170780_) {
      this.root = p_170780_;
      this.body = p_170780_.getChild("body");
      this.tail = p_170780_.getChild("tail");
      this.leftWing = p_170780_.getChild("left_wing");
      this.rightWing = p_170780_.getChild("right_wing");
      this.head = p_170780_.getChild("head");
      this.feather = this.head.getChild("feather");
      this.leftLeg = p_170780_.getChild("left_leg");
      this.rightLeg = p_170780_.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(0.0F, 16.5F, -3.0F));
      partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 21.07F, 1.16F));
      partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offset(1.5F, 16.94F, -2.76F));
      partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offset(-1.5F, 16.94F, -2.76F));
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F));
      partdefinition1.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F));
      partdefinition1.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F));
      partdefinition1.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F));
      partdefinition1.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offset(0.0F, -2.15F, 0.15F));
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      partdefinition.addOrReplaceChild("left_leg", cubelistbuilder, PartPose.offset(1.0F, 22.0F, -1.05F));
      partdefinition.addOrReplaceChild("right_leg", cubelistbuilder, PartPose.offset(-1.0F, 22.0F, -1.05F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Parrot p_103217_, float p_103218_, float p_103219_, float p_103220_, float p_103221_, float p_103222_) {
      this.setupAnim(getState(p_103217_), p_103217_.tickCount, p_103218_, p_103219_, p_103220_, p_103221_, p_103222_);
   }

   public void prepareMobModel(Parrot p_103212_, float p_103213_, float p_103214_, float p_103215_) {
      this.prepare(getState(p_103212_));
   }

   public void renderOnShoulder(PoseStack p_103224_, VertexConsumer p_103225_, int p_103226_, int p_103227_, float p_103228_, float p_103229_, float p_103230_, float p_103231_, int p_103232_) {
      this.prepare(ParrotModel.State.ON_SHOULDER);
      this.setupAnim(ParrotModel.State.ON_SHOULDER, p_103232_, p_103228_, p_103229_, 0.0F, p_103230_, p_103231_);
      this.root.render(p_103224_, p_103225_, p_103226_, p_103227_);
   }

   private void setupAnim(ParrotModel.State p_103242_, int p_103243_, float p_103244_, float p_103245_, float p_103246_, float p_103247_, float p_103248_) {
      this.head.xRot = p_103248_ * ((float)Math.PI / 180F);
      this.head.yRot = p_103247_ * ((float)Math.PI / 180F);
      this.head.zRot = 0.0F;
      this.head.x = 0.0F;
      this.body.x = 0.0F;
      this.tail.x = 0.0F;
      this.rightWing.x = -1.5F;
      this.leftWing.x = 1.5F;
      switch(p_103242_) {
      case SITTING:
         break;
      case PARTY:
         float f = Mth.cos((float)p_103243_);
         float f1 = Mth.sin((float)p_103243_);
         this.head.x = f;
         this.head.y = 15.69F + f1;
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         this.head.zRot = Mth.sin((float)p_103243_) * 0.4F;
         this.body.x = f;
         this.body.y = 16.5F + f1;
         this.leftWing.zRot = -0.0873F - p_103246_;
         this.leftWing.x = 1.5F + f;
         this.leftWing.y = 16.94F + f1;
         this.rightWing.zRot = 0.0873F + p_103246_;
         this.rightWing.x = -1.5F + f;
         this.rightWing.y = 16.94F + f1;
         this.tail.x = f;
         this.tail.y = 21.07F + f1;
         break;
      case STANDING:
         this.leftLeg.xRot += Mth.cos(p_103244_ * 0.6662F) * 1.4F * p_103245_;
         this.rightLeg.xRot += Mth.cos(p_103244_ * 0.6662F + (float)Math.PI) * 1.4F * p_103245_;
      case FLYING:
      case ON_SHOULDER:
      default:
         float f2 = p_103246_ * 0.3F;
         this.head.y = 15.69F + f2;
         this.tail.xRot = 1.015F + Mth.cos(p_103244_ * 0.6662F) * 0.3F * p_103245_;
         this.tail.y = 21.07F + f2;
         this.body.y = 16.5F + f2;
         this.leftWing.zRot = -0.0873F - p_103246_;
         this.leftWing.y = 16.94F + f2;
         this.rightWing.zRot = 0.0873F + p_103246_;
         this.rightWing.y = 16.94F + f2;
         this.leftLeg.y = 22.0F + f2;
         this.rightLeg.y = 22.0F + f2;
      }

   }

   private void prepare(ParrotModel.State p_103240_) {
      this.feather.xRot = -0.2214F;
      this.body.xRot = 0.4937F;
      this.leftWing.xRot = -0.6981F;
      this.leftWing.yRot = -(float)Math.PI;
      this.rightWing.xRot = -0.6981F;
      this.rightWing.yRot = -(float)Math.PI;
      this.leftLeg.xRot = -0.0299F;
      this.rightLeg.xRot = -0.0299F;
      this.leftLeg.y = 22.0F;
      this.rightLeg.y = 22.0F;
      this.leftLeg.zRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      switch(p_103240_) {
      case SITTING:
         float f = 1.9F;
         this.head.y = 17.59F;
         this.tail.xRot = 1.5388988F;
         this.tail.y = 22.97F;
         this.body.y = 18.4F;
         this.leftWing.zRot = -0.0873F;
         this.leftWing.y = 18.84F;
         this.rightWing.zRot = 0.0873F;
         this.rightWing.y = 18.84F;
         ++this.leftLeg.y;
         ++this.rightLeg.y;
         ++this.leftLeg.xRot;
         ++this.rightLeg.xRot;
         break;
      case PARTY:
         this.leftLeg.zRot = -0.34906584F;
         this.rightLeg.zRot = 0.34906584F;
      case STANDING:
      case ON_SHOULDER:
      default:
         break;
      case FLYING:
         this.leftLeg.xRot += 0.6981317F;
         this.rightLeg.xRot += 0.6981317F;
      }

   }

   private static ParrotModel.State getState(Parrot p_103210_) {
      if (p_103210_.isPartyParrot()) {
         return ParrotModel.State.PARTY;
      } else if (p_103210_.isInSittingPose()) {
         return ParrotModel.State.SITTING;
      } else {
         return p_103210_.isFlying() ? ParrotModel.State.FLYING : ParrotModel.State.STANDING;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum State {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;
   }
}