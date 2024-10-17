package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.common.entity.monster.Shambler;

public class ShamblerModel<T extends Shambler> extends HierarchicalModel<T> {
	public ModelPart root;
	public ModelPart body_base;
	public ModelPart body2;
	public ModelPart head1;
	public ModelPart body3;
	public ModelPart hindleg_right1;
	public ModelPart hindleg_left1;
	public ModelPart weird_butt;
	public ModelPart surprise_tail;
	public ModelPart tail2;
	public ModelPart tail3;
	public ModelPart tail4;
	public ModelPart tail5;
	public ModelPart tail6;
	public ModelPart hindleg_right2;
	public ModelPart hindleg_right3;
	public ModelPart foot_right1;
	public ModelPart toe_right1;
	public ModelPart toe_right2;
	public ModelPart toe_right3;
	public ModelPart hindleg_left2;
	public ModelPart hindleg_left3;
	public ModelPart foot_left1;
	public ModelPart toe_left1;
	public ModelPart toe_left2;
	public ModelPart toe_left3;
	public ModelPart mouth;
	public ModelPart mouth_arm1a;
	public ModelPart mouth_arm2a;
	public ModelPart mouth_arm3a;
	public ModelPart mouth_arm4a;
	public ModelPart cranialthing1;
	public ModelPart mouth_arm1b;
	public ModelPart mouth_arm1c;
	public ModelPart mouth_arm2b;
	public ModelPart mouth_arm2c;
	public ModelPart mouth_arm3b;
	public ModelPart mouth_arm3c;
	public ModelPart mouth_arm4b;
	public ModelPart mouth_arm4c;
	public ModelPart cranialthing2;

	public ModelPart tongue_part;

	public ModelPart tongue_end;
	public ModelPart tongue1;
	public ModelPart tongue2;
	public ModelPart teeth1;
	public ModelPart teeth2;

	public ShamblerModel(ModelPart root) {
		this.root = root;
		body_base = root.getChild("root").getChild("body_base");
		body2 = body_base.getChild("body2");
		hindleg_right1 = body2.getChild("hindleg_right1");
		hindleg_left1 = body2.getChild("hindleg_left1");
		body3 = body2.getChild("body3");
		weird_butt = body3.getChild("weird_butt");
		surprise_tail = weird_butt.getChild("surprise_tail");
		tail2 = surprise_tail.getChild("tail2 ");
		tail3 = tail2.getChild("tail3");
		tail4 = tail3.getChild("tail4");
		tail5 = tail5.getChild("tail5");
		tail6 = tail6.getChild("tail6");
		head1 = body_base.getChild("head1");
		cranialthing1 = head1.getChild("cranialthing1");
		cranialthing2 = cranialthing1.getChild("cranialthing2");
		mouth = head1.getChild("mouth");
		mouth_arm1a = head1.getChild("mouth_arm1a");
		mouth_arm2a = head1.getChild("mouth_arm2a");
		mouth_arm3a = head1.getChild("mouth_arm3a");
		mouth_arm4a = head1.getChild("mouth_arm4a");
		mouth_arm1b = mouth_arm1a.getChild("mouth_arm1b");
		mouth_arm1c = mouth_arm1b.getChild("mouth_arm1c");
		mouth_arm2b = mouth_arm2a.getChild("mouth_arm2b");
		mouth_arm2c = mouth_arm2b.getChild("mouth_arm2c");
		mouth_arm3b = mouth_arm3a.getChild("mouth_arm3b");
		mouth_arm3c = mouth_arm3b.getChild("mouth_arm3c");
		mouth_arm4b = mouth_arm4a.getChild("mouth_arm4b");
		mouth_arm4c = mouth_arm4b.getChild("mouth_arm4c");
		hindleg_right2 = hindleg_right1.getChild("hindleg_right2");
		hindleg_right3 = hindleg_right2.getChild("hindleg_right3");
		foot_right1 = hindleg_right3.getChild("foot_right1");
		toe_right1 = foot_right1.getChild("toe_right1");
		toe_right2 = foot_right1.getChild("toe_right2");
		toe_right3 = foot_right1.getChild("toe_right3");
		hindleg_left2 = hindleg_left1.getChild("hindleg_left2");
		hindleg_left3 = hindleg_left2.getChild("hindleg_left3");
		foot_left1 = hindleg_left3.getChild("foot_left1");
		toe_left1 = foot_left1.getChild("toe_left1");
		toe_left2 = foot_left1.getChild("toe_left2");
		toe_left3 = foot_left1.getChild("toe_left3");
		tongue_part = root.getChild("tongue_part");
		tongue_end = root.getChild("tongue_end");
		tongue1 = tongue_end.getChild("tongue1");
		tongue2 = tongue_end.getChild("tongue2");
		teeth1 = tongue1.getChild("teeth1");
		teeth2 = tongue2.getChild("teeth2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();
		
		PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body_base = root.addOrReplaceChild("body_base",
				CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 8.0F, 7.0F),
				PartPose.offset(0.0F, 8.2F, -3.0F));

		PartDefinition body2 = body_base.addOrReplaceChild("body2",
				CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 8.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, -0.3643F, 0.0F, 0.0F));

		PartDefinition body3 = body2.addOrReplaceChild("body3",
				CubeListBuilder.create().texOffs(0, 30).addBox(-4.01F, 0.0F, 0.0F, 8, 8, 3),
				PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.2731F, 0.0F, 0.0F));

		PartDefinition weird_butt = body3.addOrReplaceChild("weird_butt",
				CubeListBuilder.create().texOffs(0, 42).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 4.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.5009F, 0.0F, 0.0F));

		PartDefinition surprise_tail = weird_butt.addOrReplaceChild("surprise_tail",
				CubeListBuilder.create().texOffs(0, 53).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 3.0F),
				PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, 0.6829F, 0.0F, 0.0F));

		PartDefinition tail2 = surprise_tail.addOrReplaceChild("tail2",
				CubeListBuilder.create().texOffs(0, 61).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 5.0F),
				PartPose.offsetAndRotation(-0.015F, 0.0F, 3.0F, 0.5009F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
				CubeListBuilder.create().texOffs(0, 71).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition tail4 = tail3.addOrReplaceChild("tail4",
				CubeListBuilder.create().texOffs(0, 80).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 4.0F),
				PartPose.offsetAndRotation(-0.015F, 0.0F, 5.0F, 0.8652F, 0.0F, 0.0F));

		PartDefinition tail5 = tail4.addOrReplaceChild("tail5",
				CubeListBuilder.create().texOffs(0, 88).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 4.0F),
				PartPose.offsetAndRotation(-0.015F, 0.0F, 4.0F, 0.8652F, 0.0F, 0.0F));

		PartDefinition tail6 = tail5.addOrReplaceChild("tail6",
				CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F),
				PartPose.offsetAndRotation(-0.015F, 0.0F, 4.0F, 0.8196F, 0.0F, 0.0F));

		PartDefinition hindleg_right1 = body2.addOrReplaceChild("hindleg_right1",
				CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -3.0F, -5.0F, 3.0F, 6.0F, 7.0F),
				PartPose.offsetAndRotation(-4.0F, 5.0F, 3.0F, 0.5235987755982988F, 0F, 0F));

		PartDefinition hindleg_right2 = hindleg_right1.addOrReplaceChild("hindleg_right2",
				CubeListBuilder.create().texOffs(40, 14).addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 4.0F),
				PartPose.offsetAndRotation(-1.99F, 3.0F, -5.0F, 0.4363323129985824F, 0.0F, 0.0F));

		PartDefinition hindleg_right3 = hindleg_right2.addOrReplaceChild("hindleg_right3",
				CubeListBuilder.create().texOffs(40, 25).addBox(0.02F, 0.0F, -4.0F, 3, 4, 4),
				PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, -1.0471975511965976F, 0.0F, 0.0F));

		PartDefinition foot_right1 = hindleg_right3.addOrReplaceChild("foot_right1",
				CubeListBuilder.create().texOffs(40, 34).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 4.0F),
				PartPose.offsetAndRotation(1.5F, 4.0F, -4.0F, 0.4886921905584123F, 0.0F, 0.0F));

		PartDefinition toe_right1 = foot_right1.addOrReplaceChild("toe_right1",
				CubeListBuilder.create().texOffs(40, 41).addBox(-2.0F, 0.0F, -4.0F, 2, 2, 5),
				PartPose.offsetAndRotation(-0.5F, 0.0F, 2.0F, -0.045553093477052F, 0.18203784098300857F, -0.091106186954104F));

		PartDefinition toe_right2 = foot_right1.addOrReplaceChild("toe_right2",
				CubeListBuilder.create().texOffs(40, 49).addBox(-1.0F, 0.0F, -2.5F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		PartDefinition toe_right3 = foot_right1.addOrReplaceChild("toe_right3",
				CubeListBuilder.create().texOffs(40, 55).addBox(0.0F, 0.0F, -5.0F, 2, 2, 6),
				PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F));

		PartDefinition hindleg_left1 = body2.addOrReplaceChild("hindleg_left1",
				CubeListBuilder.create().texOffs(61, 0).addBox(-1.0F, -3.0F, -5.0F, 3, 6, 7),
				PartPose.offsetAndRotation(4.0F, 5.0F, 3.0F, 0.5235987755982988F, 0.0F, 0.0F));

		PartDefinition hindleg_left2 = hindleg_left1.addOrReplaceChild("hindleg_left2",
				CubeListBuilder.create().texOffs(61, 14).addBox(0.0F, 0.0F, 0.0F, 3, 6, 4),
				PartPose.offsetAndRotation(-1.02F, 3.0F, -5.0F, 0.4363323129985824F, 0.0F, 0.0F));

		PartDefinition hindleg_left3 = hindleg_left2.addOrReplaceChild("hindleg_left3",
				CubeListBuilder.create().texOffs(61, 25).addBox(-3.02F, 0.0F, -4.0F, 3, 4, 4),
				PartPose.offsetAndRotation(3.0F, 6.0F, 4.0F, -1.0471975511965976F, 0.0F, 0.0F));

		PartDefinition foot_left1 = hindleg_left3.addOrReplaceChild("foot_left1",
				CubeListBuilder.create().texOffs(61, 34).addBox(-1.5F, 0.0F, 0.0F, 3, 2, 4),
				PartPose.offsetAndRotation(-1.5F, 4.0F, -4.0F, 0.4886921905584123F, 0.0F, 0.0F));

		PartDefinition toe_left1 = foot_left1.addOrReplaceChild("toe_left1",
				CubeListBuilder.create().texOffs(61, 41).addBox(0.0F, 0.0F, -4.0F, 2, 2, 5),
				PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F));

		PartDefinition toe_left2 = foot_left1.addOrReplaceChild("toe_left2",
				CubeListBuilder.create().texOffs(61, 49).addBox(-1.0F, 0.0F, -2.5F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		PartDefinition toe_left3 = foot_left1.addOrReplaceChild("toe_left3",
				CubeListBuilder.create().texOffs(61, 55).addBox(-2.0F, 0.0F, -5.0F, 2, 2, 6),
				PartPose.offsetAndRotation(-0.5F, 0.0F, 2.0F, -0.045553093477052F, 0.18203784098300857F,
						-0.091106186954104F));

		PartDefinition head1 = body_base.addOrReplaceChild("head1",
				CubeListBuilder.create().texOffs(85, 0).addBox(-4.0F, -3.5F, -6.0F, 8, 8, 7),
				PartPose.offset(0.0F, 1.0F, 2.0F));

		PartDefinition mouth = head1.addOrReplaceChild("mouth",
				CubeListBuilder.create().texOffs(85, 16).addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2),
				PartPose.offset(0.0F, -2.5F, -6.0F));

		PartDefinition mouth_arm1a = head1.addOrReplaceChild("mouth_arm1a",
				CubeListBuilder.create().texOffs(85, 25).addBox(0.0F, 0.0F, -4.0F, 3, 3, 5),
				PartPose.offsetAndRotation(-3.9F, -3.3F, -5.0F, 0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm1b = mouth_arm1a.addOrReplaceChild("mouth_arm1b",
				CubeListBuilder.create().texOffs(85, 34).addBox(0.0F, 0.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.5F, 0.5F, -4.0F, 0.36425021489121656F, -0.36425021489121656F, -0.045553093477052F));

		PartDefinition mouth_arm1c = mouth_arm1b.addOrReplaceChild("mouth_arm1c",
				CubeListBuilder.create().texOffs(85, 40).addBox(0.0F, 0.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, 0.01F, -3.0F, 0.0F, -0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm2a = head1.addOrReplaceChild("mouth_arm2a",
				CubeListBuilder.create().texOffs(102, 25).addBox(-3.0F, 0.0F, -4.0F, 3, 3, 5),
				PartPose.offsetAndRotation(3.9F, -3.3F, -5.0F, 0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm2b = mouth_arm2a.addOrReplaceChild("mouth_arm2b",
				CubeListBuilder.create().texOffs(102, 34).addBox(-2.0F, 0.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(-0.5F, 0.5F, -4.0F, 0.36425021489121656F, 0.36425021489121656F,
						0.045553093477052F));

		PartDefinition mouth_arm2c = mouth_arm2b.addOrReplaceChild("mouth_arm2c",
				CubeListBuilder.create().texOffs(102, 40).addBox(-2.0F, 0.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, 0.01F, -3.0F, 0.0F, 0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm3a = head1.addOrReplaceChild("mouth_arm3a",
				CubeListBuilder.create().texOffs(85, 46).addBox(0.0F, -3.0F, -4.0F, 3, 3, 5),
				PartPose.offsetAndRotation(-3.9F, 4.3F, -5.0F, -0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm3b = mouth_arm3a.addOrReplaceChild("mouth_arm3b",
				CubeListBuilder.create().texOffs(85, 55).addBox(0.0F, -2.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.5F, -0.5F, -4.0F, -0.36425021489121656F, -0.36425021489121656F,
						0.045553093477052F));

		PartDefinition mouth_arm3c = mouth_arm3b.addOrReplaceChild("mouth_arm3c",
				CubeListBuilder.create().texOffs(85, 61).addBox(0.0F, -2.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, -0.01F, -3.0F, 0.0F, -0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm4a = head1.addOrReplaceChild("mouth_arm4a",
				CubeListBuilder.create().texOffs(102, 46).addBox(-3.0F, -3.0F, -4.0F, 3, 3, 5),
				PartPose.offsetAndRotation(3.9F, 4.3F, -5.0F, -0.0890117918517108F, 0.0F, 0.0F));

		PartDefinition mouth_arm4b = mouth_arm4a.addOrReplaceChild("mouth_arm4b",
				CubeListBuilder.create().texOffs(102, 55).addBox(-2.0F, -2.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(-0.5F, -0.5F, -4.0F, -0.36425021489121656F, 0.36425021489121656F,
						-0.045553093477052F));

		PartDefinition mouth_arm4c = mouth_arm4b.addOrReplaceChild("mouth_arm4c",
				CubeListBuilder.create().texOffs(102, 61).addBox(-2.0F, -2.0F, -3.0F, 2, 2, 3),
				PartPose.offsetAndRotation(0.0F, -0.01F, -3.0F, 0.0F, 0.5462880558742251F, 0.0F));

		PartDefinition cranialthing1 = head1.addOrReplaceChild("cranialthing1",
				CubeListBuilder.create().texOffs(85, 74).addBox(-4.0F, -2.0F, 0.0F, 8, 2, 3),
				PartPose.offsetAndRotation(0.0F, -1.5F, 1.0F, 0.18203784098300857F, 0.0F, 0.0F));

		PartDefinition cranialthing2 = cranialthing1.addOrReplaceChild("cranialthing2",
				CubeListBuilder.create().texOffs(85, 80).addBox(-3.0F, -2.0F, 0.0F, 6, 2, 2),
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));

		PartDefinition tongue_part = root.addOrReplaceChild("tongue_part",
				CubeListBuilder.create().texOffs(85, 86).addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4),
				PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition tongue_end = root.addOrReplaceChild("tongue_end",
				CubeListBuilder.create().texOffs(85, 86).addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4),
				PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition tongue1 = tongue_end.addOrReplaceChild("tongue1",
				CubeListBuilder.create().texOffs(85, 93).addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2),
				PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.5009094953223726F, 0.0F, 0.0F));

		PartDefinition teeth1 = tongue1.addOrReplaceChild("teeth1",
				CubeListBuilder.create().texOffs(85, 98).addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2),
				PartPose.offset(0.0F, 2.0F, -2.0F));

		PartDefinition tongue2 = tongue_end.addOrReplaceChild("tongue2",
				CubeListBuilder.create().texOffs(96, 93).addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2),
				PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5009094953223726F, 0.0F, 0.0F));

		PartDefinition teeth2 = tongue2.addOrReplaceChild("teeth2",
				CubeListBuilder.create().texOffs(96, 98).addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2),
				PartPose.offset(0.0F, -2.0F, -2.0F));

		
  /*     
        tail2 = new ModelRenderer(this, 0, 61);
        tail2.setRotationPoint(0.015F, 0.0F, 3.0F);
        tail2.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 5, 0.0F);
        setRotateAngle(tail2, 0.5009094953223726F, 0.0F, 0.0F);
       
               tail3 = new ModelRenderer(this, 0, 71);
        tail3.setRotationPoint(0.0F, 0.0F, 5.0F);
        tail3.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5, 0.0F);
        setRotateAngle(tail3, 0.7740535232594852F, 0.0F, 0.0F);
        
        tail4 = new ModelRenderer(this, 0, 80);
        tail4.setRotationPoint(0.015F, 0.0F, 5.0F);
        tail4.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 4, 0.0F);
        setRotateAngle(tail4, 0.8651597102135892F, 0.0F, 0.0F);
       
        tail5 = new ModelRenderer(this, 0, 88);
        tail5.setRotationPoint(0.015F, 0.0F, 4.0F);
        tail5.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 4, 0.0F);
        setRotateAngle(tail5, 0.8651597102135892F, 0.0F, 0.0F);
        
        tail6 = new ModelRenderer(this, 0, 96);
        tail6.setRotationPoint(0.015F, 0.0F, 4.0F);
        tail6.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        setRotateAngle(tail6, 0.8196066167365371F, 0.0F, 0.0F);
*/
		return LayerDefinition.create(definition, 128, 128);
    }

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		body_base.render(stack, consumer, light, overlay, colour);
	}

	public void renderTonguePart(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(180F));
		tongue_part.render(stack, consumer, colour, colour, colour);
		stack.popPose();
	}

	public void renderTongueEnd(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(180F));
		tongue_end.render(stack, consumer, colour, colour, colour);
		stack.popPose();
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head1.yRot = (float) Math.clamp(Math.toRadians(netHeadYaw), -60, 60);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTickTime) {

		float animation = (float) (Math.cos((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F);
		float animation2 = (float) (Math.sin((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F);
		float flap = (float) (Math.sin((entity.tickCount + partialTickTime) * 0.3F) * 0.8F);
		float smoothedAngle = entity.smoothedAngle(partialTickTime);
		float headX = 0F + entity.getYRot() / (180F / (float) Math.PI);

		hindleg_left1.xRot = 0.5235987755982988F - (animation2 * 14F) + flap * 0.1F - flap * 0.075F / (180F / (float) Math.PI);
		hindleg_right1.xRot = 0.5235987755982988F - (animation * 14F) + flap * 0.1F - flap * 0.075F / (180F / (float) Math.PI);

		hindleg_left2.xRot = 0.4363323129985824F + (animation2 * 8F) - flap * 0.05F + flap * 0.075F / (180F / (float) Math.PI);
		hindleg_right2.xRot = 0.4363323129985824F + (animation * 8F) - flap * 0.05F + flap * 0.075F / (180F / (float) Math.PI);

		hindleg_left3.xRot = -1.0471975511965976F + (animation2 * 4F) + flap * 0.05F - flap * 0.075F / (180F / (float) Math.PI);
		hindleg_right3.xRot = -1.0471975511965976F + (animation * 4F) + flap * 0.05F - flap * 0.075F / (180F / (float) Math.PI);

		foot_left1.xRot = 0.4886921905584123F - (animation2 * 2F) - flap * 0.05F - flap * 0.075F / (180F / (float) Math.PI);
		foot_right1.xRot = 0.4886921905584123F - (animation * 2F) - flap * 0.05F - flap * 0.075F / (180F / (float) Math.PI);

		body_base.xRot = 0F - (animation2 * 3F) - flap * 0.05F;
		head1.xRot = headX + (animation2 * 4F) + flap * 0.1F;

		body_base.zRot = 0F - (animation2 * 2F);
		head1.zRot = 0F + (animation2 * 4F);

		hindleg_left1.zRot = 0F + (animation2 * 2F);
		hindleg_right1.zRot = 0F + (animation * 2F);

		mouth_arm1a.yRot = 0F + smoothedAngle / (180F / (float) Math.PI) * 4F - (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		mouth_arm1a.xRot = 0.08726646259971647F - smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		mouth_arm2a.yRot = 0F - smoothedAngle / (180F / (float) Math.PI) * 4F + (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		mouth_arm2a.xRot = 0.08726646259971647F - smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		mouth_arm3a.yRot = 0F + smoothedAngle / (180F / (float) Math.PI) * 4F - (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		mouth_arm3a.xRot = -0.08726646259971647F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		mouth_arm4a.yRot = 0F - smoothedAngle / (180F / (float) Math.PI) * 4F + (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		mouth_arm4a.xRot = -0.08726646259971647F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		mouth_arm1c.yRot = -0.5462880558742251F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		mouth_arm2c.yRot = 0.5462880558742251F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		mouth_arm3c.yRot = -0.5462880558742251F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		mouth_arm4c.yRot = 0.5462880558742251F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!entity.jawsAreOpen() ? 0F : flap * 0.5F);

		tail2.xRot = (float) (0.5009094953223726F - Math.sin((animation) * 0.5009094953223726F) * 1F - 1F / 9 * entity.getTongueLength() * 0.5F);
		tail3.xRot = (float) (0.7740535232594852F - Math.sin((animation) * 0.7740535232594852F) * 1F - 1F / 9 * entity.getTongueLength() * 0.5F);
		tail4.xRot = (float) (0.8651597102135892F - Math.sin((animation) * 0.8651597102135892F) * 1F - 1F / 9 * entity.getTongueLength());
		tail5.xRot = (float) (0.8651597102135892F - Math.sin((animation) * 0.8651597102135892F) * 1F - 1F / 9 * entity.getTongueLength());
		tail6.xRot = (float) (0.8196066167365371F - Math.sin((animation) * 0.8196066167365371F) * 1F - 1F / 9 * entity.getTongueLength() * 0.75F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

}