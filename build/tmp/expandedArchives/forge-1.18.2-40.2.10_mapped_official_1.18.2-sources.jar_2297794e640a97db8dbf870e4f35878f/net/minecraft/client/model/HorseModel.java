package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseModel<T extends AbstractHorse> extends AgeableListModel<T> {
   private static final float DEG_125 = 2.1816616F;
   private static final float DEG_60 = ((float)Math.PI / 3F);
   private static final float DEG_45 = ((float)Math.PI / 4F);
   private static final float DEG_30 = ((float)Math.PI / 6F);
   private static final float DEG_15 = 0.2617994F;
   protected static final String HEAD_PARTS = "head_parts";
   private static final String LEFT_HIND_BABY_LEG = "left_hind_baby_leg";
   private static final String RIGHT_HIND_BABY_LEG = "right_hind_baby_leg";
   private static final String LEFT_FRONT_BABY_LEG = "left_front_baby_leg";
   private static final String RIGHT_FRONT_BABY_LEG = "right_front_baby_leg";
   private static final String SADDLE = "saddle";
   private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
   private static final String LEFT_SADDLE_LINE = "left_saddle_line";
   private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
   private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
   private static final String HEAD_SADDLE = "head_saddle";
   private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightHindBabyLeg;
   private final ModelPart leftHindBabyLeg;
   private final ModelPart rightFrontBabyLeg;
   private final ModelPart leftFrontBabyLeg;
   private final ModelPart tail;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public HorseModel(ModelPart p_170668_) {
      super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
      this.body = p_170668_.getChild("body");
      this.headParts = p_170668_.getChild("head_parts");
      this.rightHindLeg = p_170668_.getChild("right_hind_leg");
      this.leftHindLeg = p_170668_.getChild("left_hind_leg");
      this.rightFrontLeg = p_170668_.getChild("right_front_leg");
      this.leftFrontLeg = p_170668_.getChild("left_front_leg");
      this.rightHindBabyLeg = p_170668_.getChild("right_hind_baby_leg");
      this.leftHindBabyLeg = p_170668_.getChild("left_hind_baby_leg");
      this.rightFrontBabyLeg = p_170668_.getChild("right_front_baby_leg");
      this.leftFrontBabyLeg = p_170668_.getChild("left_front_baby_leg");
      this.tail = this.body.getChild("tail");
      ModelPart modelpart = this.body.getChild("saddle");
      ModelPart modelpart1 = this.headParts.getChild("left_saddle_mouth");
      ModelPart modelpart2 = this.headParts.getChild("right_saddle_mouth");
      ModelPart modelpart3 = this.headParts.getChild("left_saddle_line");
      ModelPart modelpart4 = this.headParts.getChild("right_saddle_line");
      ModelPart modelpart5 = this.headParts.getChild("head_saddle");
      ModelPart modelpart6 = this.headParts.getChild("mouth_saddle_wrap");
      this.saddleParts = new ModelPart[]{modelpart, modelpart1, modelpart2, modelpart5, modelpart6};
      this.ridingParts = new ModelPart[]{modelpart3, modelpart4};
   }

   public static MeshDefinition createBodyMesh(CubeDeformation p_170670_) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 11.0F, 5.0F));
      PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("head_parts", CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F), PartPose.offsetAndRotation(0.0F, 4.0F, -12.0F, ((float)Math.PI / 6F), 0.0F, 0.0F));
      PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, p_170670_), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, p_170670_), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, p_170670_), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_170670_), PartPose.offset(4.0F, 14.0F, 7.0F));
      partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_170670_), PartPose.offset(-4.0F, 14.0F, 7.0F));
      partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_170670_), PartPose.offset(4.0F, 14.0F, -12.0F));
      partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_170670_), PartPose.offset(-4.0F, 14.0F, -12.0F));
      CubeDeformation cubedeformation = p_170670_.extend(0.0F, 5.5F, 0.0F);
      partdefinition.addOrReplaceChild("left_hind_baby_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, cubedeformation), PartPose.offset(4.0F, 14.0F, 7.0F));
      partdefinition.addOrReplaceChild("right_hind_baby_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, cubedeformation), PartPose.offset(-4.0F, 14.0F, 7.0F));
      partdefinition.addOrReplaceChild("left_front_baby_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, cubedeformation), PartPose.offset(4.0F, 14.0F, -12.0F));
      partdefinition.addOrReplaceChild("right_front_baby_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, cubedeformation), PartPose.offset(-4.0F, 14.0F, -12.0F));
      partdefinition1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, p_170670_), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, ((float)Math.PI / 6F), 0.0F, 0.0F));
      partdefinition1.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("left_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, p_170670_), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("right_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, p_170670_), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("left_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, p_170670_), PartPose.rotation((-(float)Math.PI / 6F), 0.0F, 0.0F));
      partdefinition2.addOrReplaceChild("right_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, p_170670_), PartPose.rotation((-(float)Math.PI / 6F), 0.0F, 0.0F));
      partdefinition2.addOrReplaceChild("head_saddle", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
      partdefinition2.addOrReplaceChild("mouth_saddle_wrap", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
      partdefinition3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      partdefinition3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      return meshdefinition;
   }

   public void setupAnim(T p_102785_, float p_102786_, float p_102787_, float p_102788_, float p_102789_, float p_102790_) {
      boolean flag = p_102785_.isSaddled();
      boolean flag1 = p_102785_.isVehicle();

      for(ModelPart modelpart : this.saddleParts) {
         modelpart.visible = flag;
      }

      for(ModelPart modelpart1 : this.ridingParts) {
         modelpart1.visible = flag1 && flag;
      }

      this.body.y = 11.0F;
   }

   public Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.headParts);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightHindBabyLeg, this.leftHindBabyLeg, this.rightFrontBabyLeg, this.leftFrontBabyLeg);
   }

   public void prepareMobModel(T p_102780_, float p_102781_, float p_102782_, float p_102783_) {
      super.prepareMobModel(p_102780_, p_102781_, p_102782_, p_102783_);
      float f = Mth.rotlerp(p_102780_.yBodyRotO, p_102780_.yBodyRot, p_102783_);
      float f1 = Mth.rotlerp(p_102780_.yHeadRotO, p_102780_.yHeadRot, p_102783_);
      float f2 = Mth.lerp(p_102783_, p_102780_.xRotO, p_102780_.getXRot());
      float f3 = f1 - f;
      float f4 = f2 * ((float)Math.PI / 180F);
      if (f3 > 20.0F) {
         f3 = 20.0F;
      }

      if (f3 < -20.0F) {
         f3 = -20.0F;
      }

      if (p_102782_ > 0.2F) {
         f4 += Mth.cos(p_102781_ * 0.4F) * 0.15F * p_102782_;
      }

      float f5 = p_102780_.getEatAnim(p_102783_);
      float f6 = p_102780_.getStandAnim(p_102783_);
      float f7 = 1.0F - f6;
      float f8 = p_102780_.getMouthAnim(p_102783_);
      boolean flag = p_102780_.tailCounter != 0;
      float f9 = (float)p_102780_.tickCount + p_102783_;
      this.headParts.y = 4.0F;
      this.headParts.z = -12.0F;
      this.body.xRot = 0.0F;
      this.headParts.xRot = ((float)Math.PI / 6F) + f4;
      this.headParts.yRot = f3 * ((float)Math.PI / 180F);
      float f10 = p_102780_.isInWater() ? 0.2F : 1.0F;
      float f11 = Mth.cos(f10 * p_102781_ * 0.6662F + (float)Math.PI);
      float f12 = f11 * 0.8F * p_102782_;
      float f13 = (1.0F - Math.max(f6, f5)) * (((float)Math.PI / 6F) + f4 + f8 * Mth.sin(f9) * 0.05F);
      this.headParts.xRot = f6 * (0.2617994F + f4) + f5 * (2.1816616F + Mth.sin(f9) * 0.05F) + f13;
      this.headParts.yRot = f6 * f3 * ((float)Math.PI / 180F) + (1.0F - Math.max(f6, f5)) * this.headParts.yRot;
      this.headParts.y = f6 * -4.0F + f5 * 11.0F + (1.0F - Math.max(f6, f5)) * this.headParts.y;
      this.headParts.z = f6 * -4.0F + f5 * -12.0F + (1.0F - Math.max(f6, f5)) * this.headParts.z;
      this.body.xRot = f6 * (-(float)Math.PI / 4F) + f7 * this.body.xRot;
      float f14 = 0.2617994F * f6;
      float f15 = Mth.cos(f9 * 0.6F + (float)Math.PI);
      this.leftFrontLeg.y = 2.0F * f6 + 14.0F * f7;
      this.leftFrontLeg.z = -6.0F * f6 - 10.0F * f7;
      this.rightFrontLeg.y = this.leftFrontLeg.y;
      this.rightFrontLeg.z = this.leftFrontLeg.z;
      float f16 = ((-(float)Math.PI / 3F) + f15) * f6 + f12 * f7;
      float f17 = ((-(float)Math.PI / 3F) - f15) * f6 - f12 * f7;
      this.leftHindLeg.xRot = f14 - f11 * 0.5F * p_102782_ * f7;
      this.rightHindLeg.xRot = f14 + f11 * 0.5F * p_102782_ * f7;
      this.leftFrontLeg.xRot = f16;
      this.rightFrontLeg.xRot = f17;
      this.tail.xRot = ((float)Math.PI / 6F) + p_102782_ * 0.75F;
      this.tail.y = -5.0F + p_102782_;
      this.tail.z = 2.0F + p_102782_ * 2.0F;
      if (flag) {
         this.tail.yRot = Mth.cos(f9 * 0.7F);
      } else {
         this.tail.yRot = 0.0F;
      }

      this.rightHindBabyLeg.y = this.rightHindLeg.y;
      this.rightHindBabyLeg.z = this.rightHindLeg.z;
      this.rightHindBabyLeg.xRot = this.rightHindLeg.xRot;
      this.leftHindBabyLeg.y = this.leftHindLeg.y;
      this.leftHindBabyLeg.z = this.leftHindLeg.z;
      this.leftHindBabyLeg.xRot = this.leftHindLeg.xRot;
      this.rightFrontBabyLeg.y = this.rightFrontLeg.y;
      this.rightFrontBabyLeg.z = this.rightFrontLeg.z;
      this.rightFrontBabyLeg.xRot = this.rightFrontLeg.xRot;
      this.leftFrontBabyLeg.y = this.leftFrontLeg.y;
      this.leftFrontBabyLeg.z = this.leftFrontLeg.z;
      this.leftFrontBabyLeg.xRot = this.leftFrontLeg.xRot;
      boolean flag1 = p_102780_.isBaby();
      this.rightHindLeg.visible = !flag1;
      this.leftHindLeg.visible = !flag1;
      this.rightFrontLeg.visible = !flag1;
      this.leftFrontLeg.visible = !flag1;
      this.rightHindBabyLeg.visible = flag1;
      this.leftHindBabyLeg.visible = flag1;
      this.rightFrontBabyLeg.visible = flag1;
      this.leftFrontBabyLeg.visible = flag1;
      this.body.y = flag1 ? 10.8F : 0.0F;
   }
}