package thebetweenlands.client.rendering.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.rendering.model.MowzieModelBase;
import thebetweenlands.common.entitys.BetweenlandsEntity;

public class ModelGecko<T extends BetweenlandsEntity> extends MowzieModelBase<T> implements HeadedModel {

	public static int textureWidth = 64;
	public static int textureHeight = 32;
	
	public ModelPart body_base;

	public ModelPart head;

	public ModelPart legleft_f1;

	public ModelPart legright_f1;

	public ModelPart legleft_b1;

	public ModelPart legright_b1;

	public ModelPart tail1;

	public ModelPart tail2;

	public ModelPart tail3;

	public ModelPart crane;

	public ModelPart tongue;

	public ModelPart legleft_f2;

	public ModelPart legright_f2;

	public ModelPart legleft_b2;

	public ModelPart legright_b2;
	
	public ModelPart ModelCore;

	private ModelPart[] tail;
	
	// constructs the model part herarcy
    public ModelGecko(ModelPart core) {
    	
    	ModelCore = core;
    	body_base = core.getChild("body_base");
    	tail1 = body_base.getChild("tail1");
    	tail2 = tail1.getChild("tail2");
    	tail3 = tail2.getChild("tail3");
    	head = core.getChild("head");
    	tongue = head.getChild("tongue");
    	crane = head.getChild("crane");
    	legleft_f1 = core.getChild("legleft_f1");
    	legleft_b1 = core.getChild("legleft_b1");
    	legright_f1 = core.getChild("legright_f1");
    	legright_b1 = core.getChild("legright_b1");
    	legleft_f2 = legleft_f1.getChild("legleft_f2");
    	legleft_b2 = legleft_b1.getChild("legleft_b2");
    	legright_f2 = legright_f1.getChild("legright_f2");
    	legright_b2 = legright_b1.getChild("legright_b2");
    	
    	tail = new ModelPart[]{tail3, tail2, tail1};
	}
	
	@Override
	public void renderToBuffer(PoseStack p_102034_, VertexConsumer p_102035_, int p_102036_, int p_102037_,
			float p_102038_, float p_102039_, float p_102040_, float p_102041_) {
		super.renderToBuffer(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
	}

	public static LayerDefinition createModelLayer() {
	      MeshDefinition meshdefinition = new MeshDefinition();
	      PartDefinition body = meshdefinition.getRoot();
	      PartDefinition body_base = body.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, -0.091106186954104F, 0.0F, 0.0F));
	      
	      // Tail
	      PartDefinition tail1 = body_base.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.27314402793711257F, 0.0F, 0.0F));
	      PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));
	      tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 20).addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));
	      
	      // Head
	      PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 0).addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4), PartPose.offsetAndRotation(0.0F, 21.5F, -3.0F, 0.22759093446006054F, 0.0F, 0.0F));
	      head.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(19, 12).addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2), PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.36425021489121656F, 0.0F, 0.0F));
	      head.addOrReplaceChild("crane", CubeListBuilder.create().texOffs(19, 7).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));
	      
	      // Legs
	      PartDefinition legright_b1 = body.addOrReplaceChild("legright_b1", CubeListBuilder.create().texOffs(34, 13).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(-2.0F, 22.0F, 0.5F, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F));
	      PartDefinition legright_f1 = body.addOrReplaceChild("legright_f1", CubeListBuilder.create().texOffs(34, 4).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(-2.0F, 21.5F, -3.0F, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F));
	      PartDefinition legleft_f1 = body.addOrReplaceChild("legleft_f1", CubeListBuilder.create().texOffs(34, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(2.0F, 21.5F, -3.0F, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F));
	      PartDefinition legleft_b1 = body.addOrReplaceChild("legleft_b1", CubeListBuilder.create().texOffs(34, 8).addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1), PartPose.offsetAndRotation(2.0F, 22.0F, 0.5F, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F));
	      
	      legright_b1.addOrReplaceChild("legright_b2", CubeListBuilder.create().texOffs(39, 13).addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));
	      legright_f1.addOrReplaceChild("legright_f2", CubeListBuilder.create().texOffs(39, 4).addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2), PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));
	      legleft_f1.addOrReplaceChild("legleft_f2", CubeListBuilder.create().texOffs(39, 0).addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2), PartPose.offsetAndRotation(0.0F, 1.5F, -0.5F, 0.8196066167365371F, 0.0F, 0.0F));
	      legleft_b1.addOrReplaceChild("legleft_b2", CubeListBuilder.create().texOffs(39, 8).addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));
	      
	      return LayerDefinition.create(meshdefinition, textureWidth, textureHeight);
	   }
	
	@Override
	public ModelPart root() {
		return ModelCore;
	}
	
	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAngle, float p_102621_, float p_102622_,
			float p_102623_) {
		faceTarget(head, 1, p_102622_, p_102623_);
		
		if (limbSwingAngle > 0.4) {
			limbSwingAngle = 0.4F;
		}

		float globalSpeed = 1, globalDegree = 1, frontOffset = 0.85F;

		walk(legleft_f1, globalSpeed, globalDegree, false, frontOffset, 0, limbSwing, limbSwingAngle);
		walk(legleft_f2, globalSpeed, 1.5F * globalDegree, false, 2 + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAngle);
		walk(legright_f1, globalSpeed, globalDegree, true, frontOffset, 0, limbSwing, limbSwingAngle);
		walk(legright_f2, globalSpeed, 1.5F * globalDegree, true, 2 + frontOffset, -0.4F * globalDegree, limbSwing, limbSwingAngle);

		walk(legleft_b1, globalSpeed, globalDegree, false, 0, -2.5F * globalDegree, limbSwing, limbSwingAngle);
		walk(legleft_b2, globalSpeed, globalDegree, false, -1, 2.5F * globalDegree, limbSwing, limbSwingAngle);
		walk(legright_b1, globalSpeed, globalDegree, true, 0, -2.5F * globalDegree, limbSwing, limbSwingAngle);
		walk(legright_b2, globalSpeed, globalDegree, true, -1, 2.5F * globalDegree, limbSwing, limbSwingAngle);
		
		float frame = entity.tickCount + p_102621_;
		float tongueControl = (int) ((Math.sin(0.15F * frame - Math.cos(0.15F * frame)) + 1) / 2 + 0.5F);
		tongue.visible = tongueControl == 1;
		walk(tongue, 2, 1, false, 0, 0, frame, 1);
		chainWave(tail, 0.2F, 0.1f, 0, frame, 1);
		chainSwing(tail, 0.4F, 0.15f, 3, frame, 1);
	}
	
	public static MeshDefinition createBodyMesh(CubeDeformation none) {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
		return meshdefinition;
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}
