package thebetweenlands.client.render.model.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelSunkenLootPot2 extends ModelBase {
	ModelRenderer foot;
	ModelRenderer tarBlock;
	ModelRenderer footChild;
	ModelRenderer footChild_1;
	ModelRenderer footChild_2;
	ModelRenderer footChild_3;
	ModelRenderer footChild_4;
	ModelRenderer footChild_5;
	ModelRenderer footChildChild;
	ModelRenderer footChildChild_1;
	ModelRenderer footChildChild_2;
	ModelRenderer footChildChild_3;

	public ModelSunkenLootPot2() {
		textureWidth = 128;
		textureHeight = 64;
		footChild_5 = new ModelRenderer(this, 0, 11);
		footChild_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		footChild_5.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
		footChild_1 = new ModelRenderer(this, 50, 27);
		footChild_1.setRotationPoint(-6.0F, -12.0F, 0.0F);
		footChild_1.addBox(-4.0F, -1.0F, -1.5F, 4, 8, 3, 0.0F);
		setRotateAngle(footChild_1, 0.0F, 0.0F, -0.27314403653144836F);
		footChild_3 = new ModelRenderer(this, 40, 0);
		footChild_3.setRotationPoint(0.0F, -12.0F, 0.0F);
		footChild_3.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
		footChild_2 = new ModelRenderer(this, 0, 20);
		footChild_2.setRotationPoint(0.0F, -4.0F, 0.0F);
		footChild_2.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
		footChild_4 = new ModelRenderer(this, 0, 33);
		footChild_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		footChild_4.addBox(-6.0F, -6.0F, -6.0F, 12, 6, 12, 0.0F);
		footChildChild_3 = new ModelRenderer(this, 40, 11);
		footChildChild_3.setRotationPoint(0.0F, -2.0F, -3.0F);
		footChildChild_3.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2, 0.0F);
		footChildChild_1 = new ModelRenderer(this, 40, 16);
		footChildChild_1.setRotationPoint(3.0F, -2.0F, 0.0F);
		footChildChild_1.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8, 0.0F);
		footChildChild_2 = new ModelRenderer(this, 61, 16);
		footChildChild_2.setRotationPoint(-3.0F, -2.0F, 0.0F);
		footChildChild_2.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8, 0.0F);
		footChildChild = new ModelRenderer(this, 61, 11);
		footChildChild.setRotationPoint(0.0F, -2.0F, 3.0F);
		footChildChild.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
		footChild = new ModelRenderer(this, 65, 27);
		footChild.setRotationPoint(6.0F, -12.0F, 0.0F);
		footChild.addBox(0.0F, -1.0F, -1.5F, 4, 8, 3, 0.0F);
		setRotateAngle(footChild, 0.0F, 0.0F, 0.27314403653144836F);
		foot = new ModelRenderer(this, 0, 0);
		foot.setRotationPoint(-1.2F, 21.3F, 0.0F);
		foot.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8, 0.0F);
		setRotateAngle(foot, 0.0F, 0.5235987755982988F, 0.17453292519943295F);
		tarBlock = new ModelRenderer(this, 64, 32);
		tarBlock.setRotationPoint(0.0F, 24.0F, 0.0F);
		tarBlock.addBox(-8.0F, -16.0F, -8.0F, 16, 16, 16, 0.0F);
		foot.addChild(footChild_5);
		foot.addChild(footChild_1);
		foot.addChild(footChild_3);
		foot.addChild(footChild_2);
		foot.addChild(footChild_4);
		footChild_3.addChild(footChildChild_3);
		footChild_3.addChild(footChildChild_1);
		footChild_3.addChild(footChildChild_2);
		footChild_3.addChild(footChildChild);
		foot.addChild(footChild);
	}

	public void render() { 
		GL11.glPushMatrix();
		GL11.glTranslatef(foot.offsetX, foot.offsetY, foot.offsetZ);
		GL11.glTranslatef(foot.rotationPointX * 0.0625F, foot.rotationPointY * 0.0625F, foot.rotationPointZ * 0.0625F);
		GL11.glScaled(0.8D, 1.0D, 0.8D);
		GL11.glTranslatef(-foot.offsetX, -foot.offsetY, -foot.offsetZ);
		GL11.glTranslatef(-foot.rotationPointX * 0.0625F, -foot.rotationPointY * 0.0625F, -foot.rotationPointZ * 0.0625F);
		foot.render(0.0625F);
		GL11.glPopMatrix();
		tarBlock.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
