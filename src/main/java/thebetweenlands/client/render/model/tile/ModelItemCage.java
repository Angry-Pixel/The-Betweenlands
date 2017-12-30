package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelItemCage extends ModelBase {
	ModelRenderer bottombase;
	ModelRenderer base;
	ModelRenderer bar1;
	ModelRenderer bar2;
	ModelRenderer bar3;
	ModelRenderer bar4;
	ModelRenderer topbase;
	ModelRenderer top;
	ModelRenderer bar5;
	ModelRenderer bar6;
	ModelRenderer bar7;
	ModelRenderer bar8;
	ModelRenderer chainpiece;
	ModelRenderer connection;

	public ModelItemCage() {
		textureWidth = 128;
		textureHeight = 64;
		bar2 = new ModelRenderer(this, 11, 24);
		bar2.setRotationPoint(-6.0F, 20.0F, 6.0F);
		bar2.addBox(0.0F, -6.0F, -1.0F, 1, 7, 1, 0.0F);
		setRotation(bar2, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F);
		bar3 = new ModelRenderer(this, 20, 24);
		bar3.setRotationPoint(6.0F, 20.0F, 6.0F);
		bar3.addBox(-1.0F, -6.0F, -1.0F, 1, 7, 1, 0.0F);
		setRotation(bar3, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F);
		bar4 = new ModelRenderer(this, 29, 24);
		bar4.setRotationPoint(6.0F, 20.0F, -6.0F);
		bar4.addBox(-1.0F, -6.0F, 0.0F, 1, 7, 1, 0.0F);
		setRotation(bar4, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F);
		topbase = new ModelRenderer(this, 0, 34);
		topbase.setRotationPoint(0.0F, 14.5F, 0.0F);
		topbase.addBox(-7.5F, -2.0F, -7.5F, 15, 2, 15, 0.0F);
		bar5 = new ModelRenderer(this, 38, 24);
		bar5.setRotationPoint(0.0F, 20.0F, -6.0F);
		bar5.addBox(-0.5F, -6.0F, 0.0F, 1, 7, 1, 0.0F);
		setRotation(bar5, 0.18203784098300857F, 0.0F, 0.0F);
		bar8 = new ModelRenderer(this, 65, 24);
		bar8.setRotationPoint(6.0F, 20.0F, 0.0F);
		bar8.addBox(-1.0F, -6.0F, -0.5F, 1, 7, 1, 0.0F);
		setRotation(bar8, 0.0F, 0.0F, 0.18203784098300857F);
		top = new ModelRenderer(this, 61, 38);
		top.setRotationPoint(0.0F, 12.5F, 0.0F);
		top.addBox(-5.5F, -2.0F, -5.5F, 11, 2, 11, 0.0F);
		bottombase = new ModelRenderer(this, 0, 0);
		bottombase.setRotationPoint(0.0F, 24.0F, 0.0F);
		bottombase.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
		connection = new ModelRenderer(this, 37, 0);
		connection.setRotationPoint(0.0F, 9.0F, 0.0F);
		connection.addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4, 0.0F);
		bar7 = new ModelRenderer(this, 56, 24);
		bar7.setRotationPoint(0.0F, 20.0F, 6.0F);
		bar7.addBox(-0.5F, -6.0F, -1.0F, 1, 7, 1, 0.0F);
		setRotation(bar7, -0.18203784098300857F, 0.0F, 0.0F);
		chainpiece = new ModelRenderer(this, 24, 0);
		chainpiece.setRotationPoint(0.0F, 10.5F, 0.0F);
		chainpiece.addBox(-1.5F, -1.5F, -1.5F, 3, 2, 3, 0.0F);
		base = new ModelRenderer(this, 0, 9);
		base.setRotationPoint(0.0F, 22.0F, 0.0F);
		base.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
		bar1 = new ModelRenderer(this, 2, 24);
		bar1.setRotationPoint(-6.0F, 20.0F, -6.0F);
		bar1.addBox(0.0F, -6.0F, 0.0F, 1, 7, 1, 0.0F);
		setRotation(bar1, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F);
		bar6 = new ModelRenderer(this, 47, 24);
		bar6.setRotationPoint(-6.0F, 20.0F, 0.0F);
		bar6.addBox(0.0F, -6.0F, -0.5F, 1, 7, 1, 0.0F);
		setRotation(bar6, 0.0F, 0.0F, -0.18203784098300857F);

	}

	public void renderSolid() {
		top.render(0.0625F);
		topbase.render(0.0625F);
		bottombase.render(0.0625F);
		connection.render(0.0625F);
		base.render(0.0625F);
		chainpiece.render(0.0625F);
	}

	public void renderBars() {
		bar1.render(0.0625F);
		bar2.render(0.0625F);
		bar3.render(0.0625F);
		bar4.render(0.0625F);
		bar5.render(0.0625F);
		bar6.render(0.0625F);
		bar7.render(0.0625F);
		bar8.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
