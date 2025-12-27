package thebetweenlands.client.model.entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.RockSnot;

public class RockSnotModel extends MowzieModelBase<RockSnot> {
	 	public ModelPart shell_left_main1a;
	    public ModelPart shell_right_main1a;
	    public ModelPart connectingbit_front1a;
	    public ModelPart connectingbit_back1a;
	    public ModelPart shell_left_main1b;
	    public ModelPart shell_left_side_f1a;
	    public ModelPart shell_left_side_b1a;
	    public ModelPart shell_left_side_f1b;
	    public ModelPart shell_left_side_b1b;
	    public ModelPart thorn_left3a;
	    public ModelPart mantle_left_main1a;
	    public ModelPart thorn_left1a;
	    public ModelPart mantle_left_sidef1a;
	    public ModelPart thorn_left1b;
	    public ModelPart mantle_left_sidef1b;
	    public ModelPart mantle_left_sidef1c;
	    public ModelPart thorn_left2a;
	    public ModelPart mantle_left_sideb1a;
	    public ModelPart thorn_left2b;
	    public ModelPart mantle_left_sideb1b;
	    public ModelPart mantle_left_sideb1c;
	    public ModelPart thorn_left3b;
	    public ModelPart thorn_left3c;
	    public ModelPart mantle_left_main1b;
	    public ModelPart mantle_left_main1c;
	    public ModelPart shell_right_main1b;
	    public ModelPart shell_right_side_f1a;
	    public ModelPart shell_right_side_b1a;
	    public ModelPart shell_right_side_f1b;
	    public ModelPart shell_right_side_b1b;
	    public ModelPart thorn_right3a;
	    public ModelPart mantle_right_main1a;
	    public ModelPart thorn_right1a;
	    public ModelPart mantle_right_sidef1a;
	    public ModelPart thorn_right1b;
	   public ModelPart mantle_right_sidef1b;
	    public ModelPart mantle_right_sidef1c;
	    public ModelPart thorn_right2a;
	    public ModelPart mantle_right_sideb1a;
	    public ModelPart thorn_right2b;
	    public ModelPart mantle_right_sideb1b;
	    public ModelPart mantle_right_sideb1c;
	    public ModelPart thorn_right3b;
	    public ModelPart thorn_right3c;
	    public ModelPart mantle_right_main1b;
	    public ModelPart mantle_right_main1c;
	    public ModelPart connectingbit_front1b;
	    public ModelPart connectingbit_back1b;

	public RockSnotModel(ModelPart root) {
		super(root, RenderType::entityTranslucent);
		shell_left_main1a = root.getChild("shell_left_main1a");
		shell_right_main1a = root.getChild("shell_right_main1a");
		connectingbit_front1a = root.getChild("connectingbit_front1a");
		connectingbit_back1a = root.getChild("connectingbit_back1a");
		shell_right_main1b = shell_right_main1a.getChild("shell_right_main1b");
		shell_left_main1b = shell_left_main1a.getChild("shell_left_main1b");
		shell_right_side_f1b = shell_right_main1b.getChild("shell_right_side_f1b");
		thorn_right1a = shell_right_side_f1b.getChild("thorn_right1a");
		thorn_right1b = thorn_right1a.getChild("thorn_right1b");
		shell_left_side_b1b = shell_left_main1b.getChild("shell_left_side_b1b");
		thorn_left3a = shell_left_main1b.getChild("thorn_left3a");
		thorn_left3c = thorn_left3a.getChild("thorn_left3c");
		
		mantle_right_main1a = shell_right_main1b.getChild("mantle_right_main1a");
		mantle_right_main1b = mantle_right_main1a.getChild("mantle_right_main1b");
		mantle_right_main1c = mantle_right_main1b.getChild("mantle_right_main1c");
		shell_right_side_b1b = shell_right_main1b.getChild("shell_right_side_b1b");
		mantle_right_sideb1a = shell_right_side_b1b.getChild("mantle_right_sideb1a");
		
		mantle_right_sideb1b = mantle_right_sideb1a.getChild("mantle_right_sideb1b");
		mantle_left_main1a = shell_left_main1b.getChild("mantle_left_main1a");
		shell_left_side_f1a = shell_left_main1a.getChild("shell_left_side_f1a");
		thorn_left3b = thorn_left3a.getChild("thorn_left3b");
		connectingbit_front1b = connectingbit_front1a.getChild("connectingbit_front1b");
		thorn_right3a = shell_right_main1b.getChild("thorn_right3a");
		mantle_right_sidef1a = shell_right_side_f1b.getChild("mantle_right_sidef1a");
		
		shell_left_side_f1b = shell_left_main1b.getChild("shell_left_side_f1b");
		mantle_left_sidef1a = shell_left_side_f1b.getChild("mantle_left_sidef1a");
		mantle_left_sidef1b = mantle_left_sidef1a.getChild("mantle_left_sidef1b");
		thorn_right1a = shell_right_side_f1b.getChild("thorn_right1a");
		mantle_left_sideb1a = shell_left_side_b1b.getChild("mantle_left_sideb1a");
		mantle_right_sidef1b = mantle_right_sidef1a.getChild("mantle_right_sidef1b");
		mantle_left_sidef1c = mantle_left_sidef1b.getChild("mantle_left_sidef1c");
		thorn_right3b = thorn_right3a.getChild("thorn_right3b");
		
		
		thorn_left2a = shell_left_side_b1b.getChild("thorn_left2a");
		mantle_left_main1b = mantle_left_main1a.getChild("mantle_left_main1b");
		mantle_right_sideb1c = mantle_right_sideb1b.getChild("mantle_right_sideb1c");
		mantle_left_main1c = mantle_left_main1b.getChild("mantle_left_main1c");
		connectingbit_back1b = connectingbit_back1a.getChild("connectingbit_back1b");
		
		thorn_right2a = shell_right_side_b1b.getChild("thorn_right2a");
		thorn_right2b = thorn_right2a.getChild("thorn_right2b");
		thorn_left2b = thorn_left2a.getChild("thorn_left2b");
		
		shell_right_side_b1a = shell_right_main1a.getChild("shell_right_side_b1a");
		thorn_left1a = shell_left_side_f1b.getChild("thorn_left1a");
		thorn_left1b = thorn_left1a.getChild("thorn_left1b");
		thorn_right3c = thorn_right3a.getChild("thorn_right3c");
		mantle_right_sidef1c = mantle_right_sidef1b.getChild("mantle_right_sidef1c");
		
		mantle_left_sideb1b = mantle_left_sideb1a.getChild("mantle_left_sideb1b");
		mantle_left_sideb1c = mantle_left_sideb1b.getChild("mantle_left_sideb1c");
		shell_right_side_f1a = shell_right_main1a.getChild("shell_right_side_f1a");
		shell_left_side_b1a = shell_left_main1a.getChild("shell_left_side_b1a");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var shell_left_main1a = partDefinition.addOrReplaceChild("shell_left_main1a", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 24.0F, 0.0F, 0.0F, 0.0F, 0.1367F));
		var shell_left_main1b = shell_left_main1a.addOrReplaceChild("shell_left_main1b", CubeListBuilder.create().texOffs(0, 9).addBox(0.0F, -4.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.2731F));
		var shell_left_side_f1b = shell_left_main1b.addOrReplaceChild("shell_left_side_f1b", CubeListBuilder.create().texOffs(17, 6).addBox(0.0F, -4.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, -0.2731F, 0.0F));
		var thorn_left1a = shell_left_side_f1b.addOrReplaceChild("thorn_left1a", CubeListBuilder.create().texOffs(39, 0).addBox(0.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3643F));
		var thorn_left1b = thorn_left1a.addOrReplaceChild("thorn_left1b", CubeListBuilder.create().texOffs(39, 5).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, -1.0F, -0.0911F, 0.0F, 0.0F));
		var mantle_left_sidef1a = shell_left_side_f1b.addOrReplaceChild("mantle_left_sidef1a", CubeListBuilder.create().texOffs(8, 40).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.3187F));
		var mantle_left_sidef1b = mantle_left_sidef1a.addOrReplaceChild("mantle_left_sidef1b", CubeListBuilder.create().texOffs(11, 40).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4098F));
		var mantle_left_sidef1c = mantle_left_sidef1b.addOrReplaceChild("mantle_left_sidef1c", CubeListBuilder.create().texOffs(14, 40).addBox(-2.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		var shell_left_side_b1b = shell_left_main1b.addOrReplaceChild("shell_left_side_b1b", CubeListBuilder.create().texOffs(28, 6).addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.2731F, 0.0F));
		var thorn_left2a = shell_left_side_b1b.addOrReplaceChild("thorn_left2a", CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3643F));
		var thorn_left2b = thorn_left2a.addOrReplaceChild("thorn_left2b", CubeListBuilder.create().texOffs(48, 5).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 1.0F, 0.0911F, 0.0F, 0.0F));
		var mantle_left_sideb1a = shell_left_side_b1b.addOrReplaceChild("mantle_left_sideb1a", CubeListBuilder.create().texOffs(19, 40).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.3187F));
		var mantle_left_sideb1b = mantle_left_sideb1a.addOrReplaceChild("mantle_left_sideb1b", CubeListBuilder.create().texOffs(22, 40).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4098F));
		var mantle_left_sideb1c = mantle_left_sideb1b.addOrReplaceChild("mantle_left_sideb1c", CubeListBuilder.create().texOffs(25, 40).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		var thorn_left3a = shell_left_main1b.addOrReplaceChild("thorn_left3a", CubeListBuilder.create().texOffs(33, 9).addBox(0.0F, 0.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3643F));
		var thorn_left3b = thorn_left3a.addOrReplaceChild("thorn_left3b", CubeListBuilder.create().texOffs(42, 9).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, -1.0F, -0.0911F, 0.0F, 0.0F));
		var thorn_left3c = thorn_left3a.addOrReplaceChild("thorn_left3c", CubeListBuilder.create().texOffs(49, 9).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 1.0F, 0.0911F, 0.0F, 0.0F));
		var mantle_left_main1a = shell_left_main1b.addOrReplaceChild("mantle_left_main1a", CubeListBuilder.create().texOffs(-6, 40).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.3187F));
		var mantle_left_main1b = mantle_left_main1a.addOrReplaceChild("mantle_left_main1b", CubeListBuilder.create().texOffs(-3, 40).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4098F));
		var mantle_left_main1c = mantle_left_main1b.addOrReplaceChild("mantle_left_main1c", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, 0.0F, -3.0F, 2.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0472F));
		var shell_left_side_f1a = shell_left_main1a.addOrReplaceChild("shell_left_side_f1a", CubeListBuilder.create().texOffs(17, 0).addBox(0.0F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -3.0F, 0.0F, -0.2731F, 0.0F));
		var shell_left_side_b1a = shell_left_main1a.addOrReplaceChild("shell_left_side_b1a", CubeListBuilder.create().texOffs(28, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 3.0F, 0.0F, 0.2731F, 0.0F));
		var shell_right_main1a = partDefinition.addOrReplaceChild("shell_right_main1a", CubeListBuilder.create().texOffs(0, 20).addBox(0.0F, -2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 24.0F, 0.0F, 0.0F, 0.0F, -0.1367F));
		var shell_right_main1b = shell_right_main1a.addOrReplaceChild("shell_right_main1b", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -4.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.2731F));
		var shell_right_side_f1b = shell_right_main1b.addOrReplaceChild("shell_right_side_f1b", CubeListBuilder.create().texOffs(17, 26).addBox(-2.0F, -4.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.2731F, 0.0F));
		var thorn_right1a = shell_right_side_f1b.addOrReplaceChild("thorn_right1a", CubeListBuilder.create().texOffs(39, 20).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3643F));
		var thorn_right1b = thorn_right1a.addOrReplaceChild("thorn_right1b", CubeListBuilder.create().texOffs(39, 25).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, -0.0911F, 0.0F, 0.0F));
		var mantle_right_sidef1a = shell_right_side_f1b.addOrReplaceChild("mantle_right_sidef1a", CubeListBuilder.create().texOffs(8, 47).addBox(0.0F, 0.0F, -3.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3187F));
		var mantle_right_sidef1b = mantle_right_sidef1a.addOrReplaceChild("mantle_right_sidef1b", CubeListBuilder.create().texOffs(11, 47).addBox(0.0F, 0.0F, -3.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4098F));
		var mantle_right_sidef1c = mantle_right_sidef1b.addOrReplaceChild("mantle_right_sidef1c", CubeListBuilder.create().texOffs(14, 47).addBox(0.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		var shell_right_side_b1b = shell_right_main1b.addOrReplaceChild("shell_right_side_b1b", CubeListBuilder.create().texOffs(28, 26).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.2731F, 0.0F));
		var thorn_right2a = shell_right_side_b1b.addOrReplaceChild("thorn_right2a", CubeListBuilder.create().texOffs(48, 20).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3643F));
		var thorn_right2b = thorn_right2a.addOrReplaceChild("thorn_right2b", CubeListBuilder.create().texOffs(48, 25).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 1.0F, 0.0911F, 0.0F, 0.0F));
		var mantle_right_sideb1a = shell_right_side_b1b.addOrReplaceChild("mantle_right_sideb1a", CubeListBuilder.create().texOffs(19, 47).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3187F));
		var mantle_right_sideb1b = mantle_right_sideb1a.addOrReplaceChild("mantle_right_sideb1b", CubeListBuilder.create().texOffs(22, 47).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4098F));
		var mantle_right_sideb1c = mantle_right_sideb1b.addOrReplaceChild("mantle_right_sideb1c", CubeListBuilder.create().texOffs(25, 47).addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		var thorn_right3a = shell_right_main1b.addOrReplaceChild("thorn_right3a", CubeListBuilder.create().texOffs(33, 29).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3643F));
		var thorn_right3b = thorn_right3a.addOrReplaceChild("thorn_right3b", CubeListBuilder.create().texOffs(42, 29).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, -0.0911F, 0.0F, 0.0F));
		var thorn_right3c = thorn_right3a.addOrReplaceChild("thorn_right3c", CubeListBuilder.create().texOffs(49, 29).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 1.0F, 0.0911F, 0.0F, 0.0F));
		var mantle_right_main1a = shell_right_main1b.addOrReplaceChild("mantle_right_main1a", CubeListBuilder.create().texOffs(-6, 47).addBox(0.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3187F));
		var mantle_right_main1b = mantle_right_main1a.addOrReplaceChild("mantle_right_main1b", CubeListBuilder.create().texOffs(-3, 47).addBox(0.0F, 0.0F, -3.0F, 1.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4098F));
		var mantle_right_main1c = mantle_right_main1b.addOrReplaceChild("mantle_right_main1c", CubeListBuilder.create().texOffs(0, 47).addBox(0.0F, 0.0F, -3.0F, 2.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0472F));
		var shell_right_side_f1a = shell_right_main1a.addOrReplaceChild("shell_right_side_f1a", CubeListBuilder.create().texOffs(17, 20).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -3.0F, 0.0F, 0.2731F, 0.0F));
		var shell_right_side_b1a = shell_right_main1a.addOrReplaceChild("shell_right_side_b1a", CubeListBuilder.create().texOffs(28, 20).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 3.0F, 0.0F, -0.2731F, 0.0F));
		var connectingbit_front1a = partDefinition.addOrReplaceChild("connectingbit_front1a", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, -4.5F, 0.2276F, 0.0F, 0.0F));
		var connectingbit_front1b = connectingbit_front1a.addOrReplaceChild("connectingbit_front1b", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -2.0F, 0.3187F, 0.0F, 0.0F));
		var connectingbit_back1a = partDefinition.addOrReplaceChild("connectingbit_back1a", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 4.5F, -0.2276F, 0.0F, 0.0F));
		var connectingbit_back1b = connectingbit_back1a.addOrReplaceChild("connectingbit_back1b", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, -0.3187F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void setupAnim(RockSnot snot, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float chomp = (float) (Mth.sin((ageInTicks + partialTick) * 0.5F) * 0.8F);
		float chomp2 = (float) (Mth.sin((ageInTicks + partialTick) * 0.25F) * 0.6F);
		shell_right_main1a.xRot = 0F;
		shell_left_main1a.xRot = 0F;
		shell_right_main1a.zRot = 0.136659280431156F +convertDegtoRad(snot.getJawAngle());
		shell_left_main1a.zRot = -0.136659280431156F - convertDegtoRad(snot.getJawAngle());
		shell_right_main1a.y = 24.0F;
		shell_left_main1a.y = 24.0F;
		shell_right_main1a.x = -0.5F;
		shell_left_main1a.x = 0.5F;
		shell_right_main1a.z = 0F;
		shell_left_main1a.z = 0F;

		if(snot.isVehicle() && snot.getJawAngle() == 16) {
			shell_right_main1a.xRot = 0F - chomp2 * 0.125F;
			shell_left_main1a.xRot = 0F + chomp2 * 0.125F;
			shell_right_main1a.y = 24.0F + chomp2 * 1.5F;
			shell_left_main1a.y = 24.0F + chomp2 * 1.5F;
			shell_right_main1a.zRot = 0.136659280431156F + convertDegtoRad(snot.getJawAngle()) + chomp * 0.125F;
			shell_left_main1a.zRot = -0.136659280431156F - convertDegtoRad(snot.getJawAngle()) - chomp * 0.125F;
		}

		if(snot.getPearlTimer() > 30 && snot.level().getGameTime()%2 == 0) {
			shell_right_main1a.x = -0.5F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.01F;
			shell_left_main1a.x = 0.5F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.01F;
			shell_right_main1a.z = 0F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.01F;
			shell_left_main1a.z = 0F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.01F;
			shell_right_main1a.y = 24F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.015F;
			shell_left_main1a.y = 24F + (snot.level().getRandom().nextFloat()) * (120 - snot.getPearlTimer()) * 0.015F;
		}

		if(snot.getPearlTimer() <= 30 && snot.getPearlTimer() > 10) {
			shell_right_main1a.zRot = 0.136659280431156F + convertDegtoRad(30 -snot.getPearlTimer());
			shell_left_main1a.zRot = -0.136659280431156F - convertDegtoRad(30 -snot.getPearlTimer());
			shell_right_main1a.y = 24.0F + (30 - snot.getPearlTimer())* 0.1F;
			shell_left_main1a.y = 24.0F + (30 - snot.getPearlTimer()) * 0.1F;
		}

		if(snot.getPearlTimer() <= 10 && snot.getPearlTimer() >= 1) {
			shell_right_main1a.zRot = 0.136659280431156F + convertDegtoRad(10F) + convertDegtoRad(snot.getPearlTimer());
			shell_left_main1a.zRot = -0.136659280431156F - convertDegtoRad(10F) - convertDegtoRad(snot.getPearlTimer());
			shell_right_main1a.y = 25.0F + snot.getPearlTimer() * 0.1F;
			shell_left_main1a.y = 25.0F + snot.getPearlTimer() * 0.1F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
        connectingbit_back1a.render(stack, consumer, light, overlay, colour);
        connectingbit_front1a.render(stack, consumer, light, overlay, colour);
        shell_left_main1a.render(stack, consumer, light, overlay, colour);
        shell_right_main1a.render(stack, consumer, light, overlay, colour);
	}

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}
}