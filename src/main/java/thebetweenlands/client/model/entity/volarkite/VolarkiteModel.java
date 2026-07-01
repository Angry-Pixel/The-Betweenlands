package thebetweenlands.client.model.entity.volarkite;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.VolarkiteEntity;

public class VolarkiteModel<T extends VolarkiteEntity> extends MowzieModelBase<T> {

	public VolarkiteModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pad1 = root.addOrReplaceChild("pad1", CubeListBuilder.create().texOffs(0, 18).addBox(-15.0F, -1.0F, -15.0F, 30.0F, 1.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, 0.0002F, 0.0F, 0.0F));

		PartDefinition pad1ar = pad1.addOrReplaceChild("pad1ar", CubeListBuilder.create().texOffs(77, 50).addBox(-2.0F, -1.0F, -13.0F, 2.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.9F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0456F));

		PartDefinition pad1ar2 = pad1ar.addOrReplaceChild("pad1ar2", CubeListBuilder.create().texOffs(23, 65).addBox(-2.0F, -1.0F, -9.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition pad1al = pad1.addOrReplaceChild("pad1al", CubeListBuilder.create().texOffs(46, 50).addBox(0.0F, -1.0F, -13.0F, 2.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.9F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0456F));

		PartDefinition pad1al2 = pad1al.addOrReplaceChild("pad1al2", CubeListBuilder.create().texOffs(0, 65).addBox(0.0F, -1.0F, -9.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition pad1b = pad1.addOrReplaceChild("pad1b", CubeListBuilder.create().texOffs(0, 35).addBox(-15.0F, -1.0F, 0.0F, 30.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1bl = pad1b.addOrReplaceChild("pad1bl", CubeListBuilder.create().texOffs(0, 54).addBox(0.0F, -1.0F, 0.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.9F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0456F));

		PartDefinition pad1bl2 = pad1bl.addOrReplaceChild("pad1bl2", CubeListBuilder.create().texOffs(46, 65).addBox(0.0F, -1.0F, 0.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition pad1br = pad1b.addOrReplaceChild("pad1br", CubeListBuilder.create().texOffs(23, 54).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.9F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0456F));

		PartDefinition pad1br2 = pad1br.addOrReplaceChild("pad1br2", CubeListBuilder.create().texOffs(69, 65).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition pad1dl = pad1b.addOrReplaceChild("pad1dl", CubeListBuilder.create().texOffs(37, 83).addBox(0.0F, -1.0F, 0.0F, 14.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 8.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1el = pad1dl.addOrReplaceChild("pad1el", CubeListBuilder.create().texOffs(0, 90).addBox(0.0F, -1.0F, 0.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 4.0F));

		PartDefinition pad1fl = pad1el.addOrReplaceChild("pad1fl", CubeListBuilder.create().texOffs(0, 96).addBox(0.0F, -1.0F, 0.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1gl = pad1fl.addOrReplaceChild("pad1gl", CubeListBuilder.create().texOffs(0, 100).addBox(0.0F, -1.0F, 0.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition pad1hl = pad1gl.addOrReplaceChild("pad1hl", CubeListBuilder.create().texOffs(0, 104).addBox(0.0F, -1.0F, 0.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1ChildChildChild = pad1dl.addOrReplaceChild("pad1ChildChildChild", CubeListBuilder.create().texOffs(0, 77).addBox(0.0F, -1.0F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.9F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0456F));

		PartDefinition pad1dr = pad1b.addOrReplaceChild("pad1dr", CubeListBuilder.create().texOffs(0, 83).addBox(-14.0F, -1.0F, 0.0F, 14.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 8.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1dr2 = pad1dr.addOrReplaceChild("pad1dr2", CubeListBuilder.create().texOffs(13, 77).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.9F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0456F));

		PartDefinition pad1er = pad1dr.addOrReplaceChild("pad1er", CubeListBuilder.create().texOffs(33, 90).addBox(-12.0F, -1.0F, 0.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 4.0F));

		PartDefinition pad1fr = pad1er.addOrReplaceChild("pad1fr", CubeListBuilder.create().texOffs(25, 96).addBox(-10.0F, -1.0F, 0.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition pad1gr = pad1fr.addOrReplaceChild("pad1gr", CubeListBuilder.create().texOffs(21, 100).addBox(-8.0F, -1.0F, 0.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition pad1hr = pad1gr.addOrReplaceChild("pad1hr", CubeListBuilder.create().texOffs(13, 104).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.9F, -0.0456F, 0.0F, 0.0F));

		PartDefinition beamleft1 = pad1.addOrReplaceChild("beamleft1", CubeListBuilder.create().texOffs(116, 0).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -0.0026F, -15.0F, -0.0002F, 0.1785F, 0.0F));

		PartDefinition beamleft2 = beamleft1.addOrReplaceChild("beamleft2", CubeListBuilder.create().texOffs(154, 22).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 16.0F, -0.0456F, -0.1785F, 0.0F));

		PartDefinition beamleft3 = beamleft1.addOrReplaceChild("beamleft3", CubeListBuilder.create().texOffs(154, 22).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 2.0F, 13.0F, 0.0F, 0.0F, -0.2306F));

		PartDefinition pad1a = pad1.addOrReplaceChild("pad1a", CubeListBuilder.create().texOffs(0, 46).addBox(-13.0F, -1.0F, -2.0F, 26.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -14.9F, 0.0911F, 0.0F, 0.0F));

		PartDefinition pad1c = pad1a.addOrReplaceChild("pad1c", CubeListBuilder.create().texOffs(0, 50).addBox(-9.0F, -1.0F, -2.0F, 18.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

		PartDefinition beamright1 = pad1.addOrReplaceChild("beamright1", CubeListBuilder.create().texOffs(78, 0).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -0.0026F, -15.0F, -0.0002F, -0.1785F, 0.0F));

		PartDefinition midbeamfront = beamright1.addOrReplaceChild("midbeamfront", CubeListBuilder.create().texOffs(0, 6).addBox(-20.0F, 0.0F, 0.0F, 20.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.02F, -0.01F, 0.0F, 0.0F, 0.1785F, 0.0F));

		PartDefinition beamright2 = beamright1.addOrReplaceChild("beamright2", CubeListBuilder.create().texOffs(154, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 16.0F, -0.0456F, 0.1785F, 0.0F));

		PartDefinition beamright3 = beamright1.addOrReplaceChild("beamright3", CubeListBuilder.create().texOffs(154, 0).addBox(0.0F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 2.0F, 13.0F, 0.0F, 0.0F, 0.2306F));

		PartDefinition midbar = root.addOrReplaceChild("midbar", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -1.0F, -1.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}
}