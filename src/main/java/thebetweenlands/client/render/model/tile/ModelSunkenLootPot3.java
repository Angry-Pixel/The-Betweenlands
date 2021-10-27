package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSunkenLootPot3 extends ModelBase {
	ModelRenderer foot;
	ModelRenderer tarBlock;
	ModelRenderer footChild;
	ModelRenderer footChild_1;
	ModelRenderer footChildChild;
	ModelRenderer footChildChild_1;
	ModelRenderer footChildChild_2;
	ModelRenderer footChildChild_3;

	public ModelSunkenLootPot3() {
		textureWidth = 128;
		textureHeight = 64;
		foot = new ModelRenderer(this, 0, 0);
		foot.setRotationPoint(-0.6F, 21.2F, 0.6F);
		foot.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
		setRotateAngle(foot, 0.08726646259971647F, 0.06981317007977318F, 0.06981317007977318F);
		footChildChild_2 = new ModelRenderer(this, 50, 5);
		footChildChild_2.setRotationPoint(3.0F, -2.0F, 0.0F);
		footChildChild_2.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8, 0.0F);
		tarBlock = new ModelRenderer(this, 60, 16);
		tarBlock.setRotationPoint(0.0F, 24.0F, 0.0F);
		tarBlock.addBox(-8.0F, -16.0F, -8.0F, 16, 16, 16, 0.0F);
		footChild_1 = new ModelRenderer(this, 0, 38);
		footChild_1.setRotationPoint(0.0F, -12.0F, 0.0F);
		footChild_1.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
		footChildChild_1 = new ModelRenderer(this, 50, 0);
		footChildChild_1.setRotationPoint(0.0F, -2.0F, -3.0F);
		footChildChild_1.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
		footChildChild = new ModelRenderer(this, 71, 0);
		footChildChild.setRotationPoint(0.0F, -2.0F, 3.0F);
		footChildChild.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
		footChildChild_3 = new ModelRenderer(this, 71, 5);
		footChildChild_3.setRotationPoint(-3.0F, -2.0F, 0.0F);
		footChildChild_3.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8, 0.0F);
		footChild = new ModelRenderer(this, 0, 13);
		footChild.setRotationPoint(0.0F, -2.0F, 0.0F);
		footChild.addBox(-7.0F, -10.0F, -7.0F, 14, 10, 14, 0.0F);
		footChild_1.addChild(footChildChild_2);
		foot.addChild(footChild_1);
		footChild_1.addChild(footChildChild_1);
		footChild_1.addChild(footChildChild);
		footChild_1.addChild(footChildChild_3);
		foot.addChild(footChild);
	}

	public void render() { 
		foot.render(0.0625F);
		tarBlock.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
