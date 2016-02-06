package thebetweenlands.client.model.lights;

import thebetweenlands.client.model.AdvancedModelRenderer;
import thebetweenlands.utils.MathUtils;

public class ModelLightWeedwoodLantern extends ModelLight {
	public ModelLightWeedwoodLantern() {
		AdvancedModelRenderer lanternTop = new AdvancedModelRenderer(this, 58, 16);
		lanternTop.setRotationPoint(0, -4, 0);
		lanternTop.addBox(-3, -2, -3, 6, 2, 6, 0);
		lanternTop.rotateAngleX = MathUtils.PI;
		AdvancedModelRenderer bottomedge2 = new AdvancedModelRenderer(this, 82, 0);
		bottomedge2.setRotationPoint(-3, 0, 0);
		bottomedge2.addBox(-2, 0, -2.99F, 2, 2, 6, 0);
		bottomedge2.setRotationAngles(0, 0, -0.40980330836826856F);
		AdvancedModelRenderer woodgrid3 = new AdvancedModelRenderer(this, 98, 0);
		woodgrid3.setRotationPoint(0, 0, 2);
		woodgrid3.addBox(-3, -11, -1, 6, 11, 1, 0);
		woodgrid3.setRotationAngles(0.36425021489121656F, 0, 0);
		AdvancedModelRenderer roofmid = new AdvancedModelRenderer(this, 82, 8);
		roofmid.setRotationPoint(0, -11, 0);
		roofmid.addBox(-2.5F, -2, -2.5F, 5, 2, 5, 0);
		AdvancedModelRenderer topedge3 = new AdvancedModelRenderer(this, 76, 16);
		topedge3.setRotationPoint(0, 0, 3);
		topedge3.addBox(-2.99F, -2, 0, 6, 2, 3, 0);
		topedge3.setRotationAngles(0.18203784098300857F, 0, 0);
		AdvancedModelRenderer roofpart2 = new AdvancedModelRenderer(this, 99, 12);
		roofpart2.setRotationPoint(-6.8F, -11.8F, 0);
		roofpart2.addBox(0, 0, -1.5F, 5, 2, 3, 0);
		roofpart2.setRotationAngles(0, 0, -0.18203784098300857F);
		AdvancedModelRenderer topedge4 = new AdvancedModelRenderer(this, 63, 24);
		topedge4.setRotationPoint(3, 0, 0);
		topedge4.addBox(0, -2, -3.01F, 3, 2, 6, 0);
		topedge4.setRotationAngles(0, 0, -0.18203784098300857F);
		AdvancedModelRenderer connectionpiece = new AdvancedModelRenderer(this, 94, 17);
		connectionpiece.setRotationPoint(0, -2, 0);
		connectionpiece.addBox(-1.5F, -2, -1.5F, 3, 2, 3, 0);
		AdvancedModelRenderer topedge1b = new AdvancedModelRenderer(this, 82, 21);
		topedge1b.setRotationPoint(0, 0, -3);
		topedge1b.addBox(-2, -2, -2, 4, 2, 2, 0);
		topedge1b.setRotationAngles(-0.091106186954104F, 0, 0);
		AdvancedModelRenderer roofpart1 = new AdvancedModelRenderer(this, 84, 57);
		roofpart1.setRotationPoint(0, -11.8F, -6.8F);
		roofpart1.addBox(-1.5F, 0, 0, 3, 2, 5, 0);
		roofpart1.setRotationAngles(0.18203784098300857F, 0, 0);
		AdvancedModelRenderer woodgrid1 = new AdvancedModelRenderer(this, 112, 0);
		woodgrid1.setRotationPoint(0, 0, -2);
		woodgrid1.addBox(-3, -11, 0, 6, 11, 1, 0);
		woodgrid1.setRotationAngles(-0.36425021489121656F, 0, 0);
		AdvancedModelRenderer bottomedge3 = new AdvancedModelRenderer(this, 75, 25);
		bottomedge3.setRotationPoint(0, 0, 3);
		bottomedge3.addBox(-2.99F, 0, 0, 6, 2, 2, 0);
		bottomedge3.setRotationAngles(-0.40980330836826856F, 0, 0);
		AdvancedModelRenderer woodgrid4 = new AdvancedModelRenderer(this, 62, 34);
		woodgrid4.setRotationPoint(2, 0, 0);
		woodgrid4.addBox(-1, -11, -3, 1, 11, 6, 0);
		woodgrid4.setRotationAngles(0, 0, -0.36425021489121656F);
		AdvancedModelRenderer bottomedge1 = new AdvancedModelRenderer(this, 62, 51);
		bottomedge1.setRotationPoint(0, 0, -3);
		bottomedge1.addBox(-3.01F, 0, -2, 6, 2, 2, 0);
		bottomedge1.setRotationAngles(0.40980330836826856F, 0, 0);
		AdvancedModelRenderer topedge2 = new AdvancedModelRenderer(this, 109, 12);
		topedge2.setRotationPoint(-3, 0, 0);
		topedge2.addBox(-3, -2, -2.99F, 3, 2, 6, 0);
		topedge2.setRotationAngles(0, 0, 0.18203784098300857F);
		AdvancedModelRenderer topedge2b = new AdvancedModelRenderer(this, 91, 22);
		topedge2b.setRotationPoint(-3, 0, 0);
		topedge2b.addBox(-2, -2, -2, 2, 2, 4, 0);
		topedge2b.setRotationAngles(0, 0, 0.091106186954104F);
		AdvancedModelRenderer topedge1 = new AdvancedModelRenderer(this, 103, 20);
		topedge1.setRotationPoint(0, 0, -3);
		topedge1.addBox(-3.01F, -2, -3, 6, 2, 3, 0);
		topedge1.setRotationAngles(-0.18203784098300857F, 0, 0);
		AdvancedModelRenderer topedge4b = new AdvancedModelRenderer(this, 82, 51);
		topedge4b.setRotationPoint(3, 0, 0);
		topedge4b.addBox(0, -2, -2, 2, 2, 4, 0);
		topedge4b.setRotationAngles(0, 0, -0.091106186954104F);
		AdvancedModelRenderer lantern = new AdvancedModelRenderer(this, 100, 33);
		lantern.setRotationPoint(0, -13.01F, 0);
		lantern.addBox(-3.5F, -9, -3.5F, 7, 9, 7, 0);
		lantern.rotateAngleX = MathUtils.PI;
		AdvancedModelRenderer lanternBottom = new AdvancedModelRenderer(this, 100, 56);
		lanternBottom.setRotationPoint(0, -13, 0);
		lanternBottom.addBox(-3, 0, -3, 6, 2, 6, 0);
		lanternBottom.rotateAngleX = MathUtils.PI;
		AdvancedModelRenderer roofpart3 = new AdvancedModelRenderer(this, 100, 49);
		roofpart3.setRotationPoint(0, -11.8F, 6.8F);
		roofpart3.addBox(-1.5F, 0, -5, 3, 2, 5, 0);
		roofpart3.setRotationAngles(-0.18203784098300857F, 0, 0);
		AdvancedModelRenderer woodgrid2 = new AdvancedModelRenderer(this, 76, 34);
		woodgrid2.setRotationPoint(-2, 0, 0);
		woodgrid2.addBox(0, -11, -3, 1, 11, 6, 0);
		woodgrid2.setRotationAngles(0, 0, 0.36425021489121656F);
		AdvancedModelRenderer bottomedge4 = new AdvancedModelRenderer(this, 72, 51);
		bottomedge4.setRotationPoint(3, 0, 0);
		bottomedge4.addBox(0, 0, -3.01F, 2, 2, 6, 0);
		bottomedge4.setRotationAngles(0, 0, 0.40980330836826856F);
		AdvancedModelRenderer roofpart4 = new AdvancedModelRenderer(this, 111, 49);
		roofpart4.setRotationPoint(6.8F, -11.8F, 0);
		roofpart4.addBox(-5, 0, -1.5F, 5, 2, 3, 0);
		roofpart4.setRotationAngles(0, 0, 0.18203784098300857F);
		AdvancedModelRenderer topedge3b = new AdvancedModelRenderer(this, 103, 25);
		topedge3b.setRotationPoint(0, 0, 3);
		topedge3b.addBox(-2, -2, 0, 4, 2, 2, 0);
		topedge3b.setRotationAngles(0.091106186954104F, 0, 0);
		AdvancedModelRenderer light = new AdvancedModelRenderer(this, 87, 28);
		light.setRotationPoint(0, -5, 0);
		light.addBox(-2.5F, 0, -2.5F, 5, 7, 5, 0.25F);
		light.rotateAngleX = MathUtils.PI;
		lanternBottom.addChild(bottomedge2);
		bottomedge3.addChild(woodgrid3);
		lantern.addChild(roofmid);
		lanternTop.addChild(topedge3);
		lantern.addChild(roofpart2);
		lanternTop.addChild(topedge4);
		roofmid.addChild(connectionpiece);
		topedge1.addChild(topedge1b);
		lantern.addChild(roofpart1);
		bottomedge1.addChild(woodgrid1);
		lanternBottom.addChild(bottomedge3);
		bottomedge4.addChild(woodgrid4);
		lanternBottom.addChild(bottomedge1);
		lanternTop.addChild(topedge2);
		topedge2.addChild(topedge2b);
		lanternTop.addChild(topedge1);
		topedge4.addChild(topedge4b);
		lantern.addChild(roofpart3);
		bottomedge2.addChild(woodgrid2);
		lanternBottom.addChild(bottomedge4);
		lantern.addChild(roofpart4);
		topedge3.addChild(topedge3b);
		amutachromicParts.addChild(lanternTop);
		amutachromicParts.addChild(lantern);
		amutachromicParts.addChild(lanternBottom);
		colorableParts.addChild(light);
	}

	@Override
	public boolean shouldParallelCord() {
		return false;
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
