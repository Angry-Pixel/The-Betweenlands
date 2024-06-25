package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entities.BetweenlandsEntity;
import thebetweenlands.common.entities.EntitySwampHag;

public class ModelSwampHag<T extends BetweenlandsEntity> extends MowzieModelBase<T> implements HeadedModel {

	public static int textureWidth = 128;
	public static int textureHeight = 64;

	ModelPart body_base;
	ModelPart neck;
	ModelPart body_top;
	ModelPart toadstool1;
	ModelPart mushroomstem;
	ModelPart armright;
	ModelPart legright1;
	ModelPart legleft1;
	ModelPart body_inner;
	ModelPart vines2;
	ModelPart toadstool1b;
	ModelPart toadstool2;
	ModelPart toadstool3;
	ModelPart vines1;
	ModelPart toadstool2b;
	ModelPart mushroomhat;
	ModelPart mushroomhat2;
	ModelPart legright2;
	ModelPart legleft2;
	ModelPart head1;
	ModelPart head2;
	ModelPart jaw;
	ModelPart toadstool4;
	ModelPart brain;
	ModelPart modelCore;

	ModelPart dat_detailed_hot_bod;
	ModelPart dat_detailed_hot_bod_2;
	ModelPart head;
	ModelPart dat_detailed_hot_bod_3;
	ModelPart cute_lil_butt;
	ModelPart spoopy_stinger;
	ModelPart beak_right;
	ModelPart beak_left;

	// constructs the model part herarcy
	public ModelSwampHag(ModelPart core) {

		modelCore = core;
		body_base = core.getChild("body_base");
		body_top = body_base.getChild("body_top");
		armright = body_base.getChild("armright");
		neck = body_top.getChild("neck");
		head1 = neck.getChild("head1");
		head2 = head1.getChild("head2");
		jaw = head2.getChild("jaw");

		legleft1 = body_base.getChild("legleft1");
		legright1 = body_base.getChild("legright1");
		legleft2 = legleft1.getChild("legleft2");
		legright2 = legright1.getChild("legright2");
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();

		// Body
		PartDefinition body_base = body.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.5F, -1.0F, 10, 10, 6), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("body_inner", CubeListBuilder.create().texOffs(42, 44).addBox(-4.0F, -1.5F, -0.5F, 8, 9, 5), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		PartDefinition body_top = body_base.addOrReplaceChild("body_top", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, -8.0F, -2.0F, 12, 8, 8, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.18203784098300857F, 0.136659280431156F, -0.091106186954104F));

		// Some sick drip
		body_base.addOrReplaceChild("vines2", CubeListBuilder.create().texOffs(69, 42).addBox(-2.5F, 0.0F, -3.9F, 5, 14, 7), PartPose.offsetAndRotation(3.0F, -2.0F, 2.0F, 0.0F, 0.0F, 0.0F));
		PartDefinition toadstool1 = body_top.addOrReplaceChild("toadstool1", CubeListBuilder.create().texOffs(0, 34).addBox(-4.3F, -1.0F, -3.5F, 8, 2, 4), PartPose.offsetAndRotation(4.5F, -4.0F, 0.0F, 0.0F, -1.0471975511965976F, 0.0F));
		toadstool1.addOrReplaceChild("vines1", CubeListBuilder.create().texOffs(0, 47).addBox(0.0F, 0.0F, -2.5F, 0, 10, 5), PartPose.offsetAndRotation(1.5F, 1.0F, -1.5F, 0.0F, 1.1383037381507017F, 0.0F));
		toadstool1.addOrReplaceChild("toadstool2", CubeListBuilder.create().texOffs(0, 41).addBox(-2.5F, 0.0F, -3.0F, 6, 1, 3), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, -0.22759093446006054F, 0.0F));
		toadstool1.addOrReplaceChild("toadstool3", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, 0.0F, -3.0F, 4, 1, 4), PartPose.offsetAndRotation(-3.0F, 2.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.0F));
		toadstool1.addOrReplaceChild("toadstool1b", CubeListBuilder.create().texOffs(25, 34).addBox(-3.3F, -1.0F, -4.5F, 6, 2, 1), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition mushroomstem = body_top.addOrReplaceChild("mushroomstem", CubeListBuilder.create().texOffs(11, 52).addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1), PartPose.offsetAndRotation(5.4F, -9.0F, 3.3F, -0.045553093477052F, 0.136659280431156F, 0.31869712141416456F));
		mushroomstem.addOrReplaceChild("mushroomhat", CubeListBuilder.create().texOffs(11, 57).addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.045553093477052F, -0.136659280431156F, -0.31869712141416456F));
		mushroomstem.addOrReplaceChild("mushroomhat2", CubeListBuilder.create().texOffs(16, 52).addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2), PartPose.offsetAndRotation(-0.8F, 1.2F, -2.5F, 0.045553093477052F, 0.136659280431156F, -0.31869712141416456F));

		// Arms and legs
		body_base.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(42, 0).addBox(-0.5F, -1.0F, -1.5F, 2, 16, 3), PartPose.offsetAndRotation(-7.0F, -6.0F, 2.0F, 0.0F, 0.0F, 0.0F));
		PartDefinition legleft = body_base.addOrReplaceChild("legleft1", CubeListBuilder.create().texOffs(42, 32).addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3), PartPose.offsetAndRotation(3.0F, 8.0F, 2.0F, -0.18203784098300857F, -0.18203784098300857F, 0.0F));
		PartDefinition legright = body_base.addOrReplaceChild("legright1", CubeListBuilder.create().texOffs(42, 20).addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3), PartPose.offsetAndRotation(-3.0F, 8.0F, 2.0F, -0.18203784098300857F, 0.18203784098300857F, 0.0F));
		legleft.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(55, 32).addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.18203784098300857F, 0.0F, 0.0F));
		legright.addOrReplaceChild("legright2", CubeListBuilder.create().texOffs(55, 20).addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.18203784098300857F, 0.0F, 0.0F));


		// Neck and head
		PartDefinition neck = body_top.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(70, 0).addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4), PartPose.offsetAndRotation(-0.7F, -7.4F, 0.0F, 0.8196066167365371F, 0.045553093477052F, -0.045553093477052F));
		PartDefinition head1 = neck.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(70, 10).addBox(-4.0F, -6.0F, -8.5F, 8, 6, 8), PartPose.offsetAndRotation(0.0F, -2.0F, 0.5F, -0.9560913642424937F, 0.045553093477052F, 0.045553093477052F));
		PartDefinition head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(70, 25).addBox(-3.5F, 0.0F, -3.5F, 7, 3, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("brain", CubeListBuilder.create().texOffs(90, 35).addBox(-3.5F, -5.5F, -8.0F, 7, 5, 7), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head2.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(87, 0).addBox(-3.0F, -1.0F, -7.0F, 6, 2, 7), PartPose.offsetAndRotation(0.0F, 0.8F, -1.5F, 1.0016444577195458F, 0.0F, 0.0F));
		head1.addOrReplaceChild("toadstool4", CubeListBuilder.create().texOffs(20, 45).addBox(-2.7F, 0.0F, -0.9F, 4, 1, 3), PartPose.offsetAndRotation(4.0F, -4.0F, -0.4F, 0.0F, -2.231054382824351F, 0.0F));

		return LayerDefinition.create(meshdefinition, textureWidth, textureHeight);
	}

	@Override
	public ModelPart root() {
		return modelCore;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAngle, float p_102621_, float p_102622_,
						  float p_102623_) {

		EntitySwampHag hag = (EntitySwampHag) entity;
		limbSwingAngle = Math.min(limbSwingAngle, 0.25F);

		//this.jaw.xRot = hag.getJawFloat(p_102621_);

		// Applying rotation to head2 rather then jaw to better suit the model
		this.head2.xRot = -(0.8196066167365371F - hag.jawFloat);
		//Betweenlands.LOGGER.info("partal float is: " + (p_102621_ - entity.tickCount));

		this.head1.yRot = p_102622_ / (180F / (float) Math.PI) - 0.045553093477052F;
		this.head1.xRot = p_102623_ / (180F / (float) Math.PI) - 0.8196066167365371F;
		this.head1.zRot = p_102623_ / (180F / (float) Math.PI) + 0.045553093477052F;

		float globalSpeed = 1.7f;
		float globalDegree = 1.8f;
		float legDegree = 1.8f;
		float inverseFrequency = 4 / (globalSpeed);
		float limpSwing = (float) ((-(inverseFrequency * Math.sin((2 * limbSwing - 1) / inverseFrequency) - 4 * limbSwing + Math.PI) / 4) - 0.5 * Math.PI);

		limbSwing = limpSwing;

		walk(body_base, 1F * globalSpeed, 0.1F * globalDegree, true, 0, 0.05F * globalDegree, limbSwing, limbSwingAngle);
		walk(legleft1, 1F * globalSpeed, 0.1F * globalDegree, false, 0, 0F, limbSwing, limbSwingAngle);
		walk(legright1, 1F * globalSpeed, 0.1F * globalDegree, false, 0, 0F, limbSwing, limbSwingAngle);
		walk(body_top, 1F * globalSpeed, 0.1F * globalDegree, true, -1f, 0.05F * globalDegree, limbSwing, limbSwingAngle);
		walk(neck, 1F * globalSpeed, 0.1F * globalDegree, true, -2f, 0.05F * globalDegree, limbSwing, limbSwingAngle);
		walk(head1, 1F * globalSpeed, 0.1F * globalDegree, true, -3f, -0.15F * globalDegree, limbSwing, limbSwingAngle);

		walk(legleft1, 0.5F * globalSpeed, 0.7F * legDegree, false, 0, 0F, limbSwing, limbSwingAngle);
		walk(legleft2, 0.5F * globalSpeed, 0.8F * legDegree, false, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);

		walk(legright1, 0.5F * globalSpeed, 0.7F * legDegree, true, 0, 0F, limbSwing, limbSwingAngle);
		walk(legright2, 0.5F * globalSpeed, 0.8F * legDegree, true, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);

		walk(armright, 1 * globalSpeed, 0.2f * globalDegree, true, -1f, -0.3f * globalDegree, limbSwing, limbSwingAngle);
		flap(modelCore, 0.5f * globalSpeed, 0.2f * globalDegree, false, 3f, 0.4f, limbSwing, limbSwingAngle);
		flap(body_top, 0.5f * globalSpeed, 0.1f * globalDegree, false, 1f, 0.1f, limbSwing, limbSwingAngle);
		flap(armright, 0.5f * globalSpeed, 0.2f * globalDegree, true, 0.5f, 0.3f, limbSwing, limbSwingAngle);
		flap(neck, 0.5f * globalSpeed, 0.1f * globalDegree, false, 0.5f, 0.1f, limbSwing, limbSwingAngle);
		flap(head1, 0.5f * globalSpeed, 0.1f * globalDegree, false, 0f, 0.1f, limbSwing, limbSwingAngle);
		body_base.xRot = (float) -(Math.cos((limbSwing - 3) * 0.5 * globalSpeed) * limbSwingAngle);


		// No setScaleX like thing exists anymore
		// looking into ways to bring this functinality back

		float frame = entity.tickCount + p_102621_;
		//body_top.setScaleX((float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//body_top.setScaleY((float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//body_top.setScaleZ((float) (1 + 0.08 * Math.sin(frame * 0.07)));

		//neck.setScaleX(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//neck.setScaleY(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//neck.setScaleZ(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));

		//armright.setScaleX(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//armright.setScaleY(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
		//armright.setScaleZ(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));

		walk(body_top, 0.07f, 0.05f, false, 1, 0, frame, 1);
		walk(neck, 0.07f, 0.05f, false, 0.5f, 0, frame, 1);
		walk(head1, 0.07f, 0.05f, false, 0f, 0, frame, 1);
		walk(armright, 0.07f, 0.1f, false, 0.5f, -0.1f, frame, 1);
		flap(armright, 0.07f, 0.1f, true, 0.5f, 0.15f, frame, 1);
		//chainWave(partsWormWiggle, 0.3f, 0.2f, 2f, frame, 1);
		//chainSwing(partsWormWiggle, 0.2f, 0.2f, 2f, frame, 1);
		//swing(beak_right, 0.5f, 0.3f, false, 1, 0, frame, 1);
		//swing(beak_left, 0.5f, 0.3f, true, 1, 0, frame, 1);

		// Dont have peat mummys implimented yet so this is untested :(
        /*
        if (this.riding) {
			legright1.xRot = -1.4137167F;
			legright1.yRot = ((float) Math.PI / 10F);
			legright1.zRot = 0.07853982F;
			legleft1.xRot = -1.4137167F;
			legleft1.yRot = -((float) Math.PI / 10F);
			legleft1.zRot = -0.07853982F;
			if (hag.isRidingMummy()) {
				if (hag.getThrowTimer() < 90) {
					armright.xRot = (float) (-((float) Math.PI / 5F) - Math.toRadians(hag.getThrowTimer()) * 0.35F);
					armright.yRot = (float) (0F + Math.toRadians(hag.getThrowTimer()));
				}

				if (hag.getThrowTimer() >= 10 && hag.getThrowTimer() <= 99)
					dat_detailed_hot_bod.visible = true;

				if (hag.getThrowTimer() >= 90) {
					armright.xRot = -((float) Math.PI / 5F);
					armright.yRot = (float) (Math.toRadians(90F) - Math.toRadians(hag.getThrowTimer() - 90F) * 9F);
				}
			} else {
				armright.yRot = 0F;
				armright.xRot = -((float) Math.PI / 5F);
			}
		}
		*/
	}

	public static MeshDefinition createBodyMesh(CubeDeformation none) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		return meshdefinition;
	}

	@Override
	public ModelPart getHead() {
		return head1;
	}
}
