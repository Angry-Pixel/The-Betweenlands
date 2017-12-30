package thebetweenlands.client.render.model.tile;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityPossessedBlock;

@SideOnly(Side.CLIENT)
public class ModelPossessedBlock extends ModelBase {
    ModelRenderer head;
    ModelRenderer foreHead;
    ModelRenderer nose;
    ModelRenderer cheekLeft;
    ModelRenderer cheekRight;
    ModelRenderer topJaw;
    ModelRenderer leftTopFang;
    ModelRenderer rightTopFang;
    ModelRenderer tongueTop;
    ModelRenderer tongueBottom;
    ModelRenderer bottomJaw;
    ModelRenderer leftBottomFang;
    ModelRenderer rightBottomFang;
    ModelRenderer body;
    ModelRenderer leftArm1;
    ModelRenderer leftArm2;
    ModelRenderer leftPalm;
    ModelRenderer leftOutFinger;
    ModelRenderer leftInFinger;
    ModelRenderer leftThumb;
    ModelRenderer rightArm1;
    ModelRenderer rightArm2;
    ModelRenderer rightPalm;
    ModelRenderer rightOutFinger;
    ModelRenderer rightInFinger;
    ModelRenderer rightThumb;
  
  public ModelPossessedBlock() {
    textureWidth = 64;
    textureHeight = 64;
    
      head = new ModelRenderer(this, 22, 0);
      head.addBox(-2.5F, -5.5F, -4.5F, 5, 4, 5);
      head.setRotationPoint(0F, 16F, 0F);
      setRotation(head, -0.5235988F, 0F, 0.2617994F);
      foreHead = new ModelRenderer(this, 27, 10);
      foreHead.addBox(-2F, -5F, -5.5F, 4, 2, 1);
      foreHead.setRotationPoint(0F, 16F, 0F);
      setRotation(foreHead, -0.5235988F, 0F, 0.2617994F);
      nose = new ModelRenderer(this, 30, 14);
      nose.addBox(-0.5F, -3F, -5.5F, 1, 1, 1);
      nose.setRotationPoint(0F, 16F, 0F);
      setRotation(nose, -0.5235988F, 0F, 0.2617994F);
      cheekLeft = new ModelRenderer(this, 38, 10);
      cheekLeft.addBox(2F, -4F, -5.5F, 1, 3, 5);
      cheekLeft.setRotationPoint(0F, 16F, 0F);
      setRotation(cheekLeft, -0.5235988F, 0F, 0.2617994F);
      cheekRight = new ModelRenderer(this, 13, 10);
      cheekRight.addBox(-3F, -4F, -5.5F, 1, 3, 5);
      cheekRight.setRotationPoint(0F, 16F, 0F);
      setRotation(cheekRight, -0.5235988F, 0F, 0.2617994F);
      topJaw = new ModelRenderer(this, 27, 17);
      topJaw.addBox(-2F, -2F, -5.5F, 4, 2, 1);
      topJaw.setRotationPoint(0F, 16F, 0F);
      setRotation(topJaw, -0.5235988F, 0F, 0.2617994F);
      leftTopFang = new ModelRenderer(this, 33, 21);
      leftTopFang.addBox(0.5F, -2.8F, -4.7F, 1, 2, 1);
      leftTopFang.setRotationPoint(0F, 16F, 0F);
      setRotation(leftTopFang, 0F, 0F, 0.2617994F);
      rightTopFang = new ModelRenderer(this, 27, 21);
      rightTopFang.addBox(-1.5F, -2.8F, -4.7F, 1, 2, 1);
      rightTopFang.setRotationPoint(0F, 16F, 0F);
      setRotation(rightTopFang, 0F, 0F, 0.2617994F);
      tongueTop = new ModelRenderer(this, 26, 25);
      tongueTop.addBox(-1F, -2.1F, -1.5F, 2, 1, 4);
      tongueTop.setRotationPoint(0F, 16F, 0F);
      setRotation(tongueTop, 1.047198F, 0F, 0.2617994F);
      tongueBottom = new ModelRenderer(this, 27, 31);
      tongueBottom.addBox(-1F, -2.6F, -2.8F, 2, 1, 3);
      tongueBottom.setRotationPoint(0F, 16F, 0F);
      setRotation(tongueBottom, 1.745329F, 0F, 0.2617994F);
      bottomJaw = new ModelRenderer(this, 24, 36);
      bottomJaw.addBox(-2F, -1.2F, -1.5F, 4, 1, 4);
      bottomJaw.setRotationPoint(0F, 16F, 0F);
      setRotation(bottomJaw, 1.047198F, 0F, 0.2617994F);
      leftBottomFang = new ModelRenderer(this, 36, 42);
      leftBottomFang.addBox(1F, -2.2F, -2.5F, 1, 2, 1);
      leftBottomFang.setRotationPoint(0F, 16F, 0F);
      setRotation(leftBottomFang, 1.029744F, 0F, 0.2617994F);
      rightBottomFang = new ModelRenderer(this, 24, 42);
      rightBottomFang.addBox(-2F, -2.2F, -2.5F, 1, 2, 1);
      rightBottomFang.setRotationPoint(0F, 16F, 0F);
      setRotation(rightBottomFang, 1.047198F, 0F, 0.2617994F);
      body = new ModelRenderer(this, 22, 46);
      body.addBox(-3F, -0.5F, -0.5F, 6, 8, 4);
      body.setRotationPoint(0F, 16F, 0F);
      setRotation(body, 0.715585F, 0F, 0F);
      leftArm1 = new ModelRenderer(this, 47, 32);
      leftArm1.addBox(-0.5F, 0F, -1F, 2, 6, 2);
      leftArm1.setRotationPoint(3F, 15F, 1F);
      setRotation(leftArm1, 0.6981317F, 0.1396263F, -0.4886922F);
      leftArm2 = new ModelRenderer(this, 43, 41);
      leftArm2.addBox(0.5F, 4F, -3.5F, 2, 2, 6);
      leftArm2.setRotationPoint(3F, 15F, 1F);
      setRotation(leftArm2, 0.3490659F, 0.3490659F, 0F);
      leftPalm = new ModelRenderer(this, 47, 50);
      leftPalm.addBox(0F, 3F, -4F, 3, 3, 1);
      leftPalm.setRotationPoint(3F, 15F, 1F);
      setRotation(leftPalm, 0.3490659F, 0.3490659F, 0F);
      leftOutFinger = new ModelRenderer(this, 56, 50);
      leftOutFinger.addBox(2F, 4F, -5F, 1, 1, 3);
      leftOutFinger.setRotationPoint(3F, 15F, 1F);
      setRotation(leftOutFinger, 0F, 0.3490659F, 0F);
      leftInFinger = new ModelRenderer(this, 56, 55);
      leftInFinger.addBox(0F, 4F, -5F, 1, 1, 3);
      leftInFinger.setRotationPoint(3F, 15F, 1F);
      setRotation(leftInFinger, 0F, 0.3490659F, 0F);
      leftThumb = new ModelRenderer(this, 43, 55);
      leftThumb.addBox(0.5F, 5F, -5F, 1, 1, 2);
      leftThumb.setRotationPoint(3F, 15F, 1F);
      setRotation(leftThumb, 0.3490659F, 0.6981317F, 0F);
      rightArm1 = new ModelRenderer(this, 8, 32);
      rightArm1.addBox(-2F, -0.5F, -1F, 2, 6, 2);
      rightArm1.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightArm1, 0.5235988F, -0.5235988F, 0.3316126F);
      rightArm2 = new ModelRenderer(this, 4, 41);
      rightArm2.addBox(-4F, 3F, -2.8F, 2, 2, 6);
      rightArm2.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightArm2, -0.2617994F, -0.3490659F, 0F);
      rightPalm = new ModelRenderer(this, 9, 50);
      rightPalm.addBox(-4.5F, 1F, -4.5F, 3, 3, 1);
      rightPalm.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightPalm, 0.0174533F, -0.3490659F, 0F);
      rightOutFinger = new ModelRenderer(this, 0, 50);
      rightOutFinger.addBox(-4.5F, 2.3F, -6.5F, 1, 1, 3);
      rightOutFinger.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightOutFinger, -0.3490659F, -0.3490659F, 0F);
      rightInFinger = new ModelRenderer(this, 0, 55);
      rightInFinger.addBox(-2.5F, 2.333333F, -6.5F, 1, 1, 3);
      rightInFinger.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightInFinger, -0.3316126F, -0.3490659F, 0F);
      rightInFinger.mirror = false;
      rightThumb = new ModelRenderer(this, 15, 55);
      rightThumb.addBox(-3.8F, 1.5F, -6F, 1, 1, 2);
      rightThumb.setRotationPoint(-3F, 15F, 1F);
      setRotation(rightThumb, 0.3490659F, -0.6981317F, 0F);
  }
  
	public void render(TileEntityPossessedBlock tile) {
		if (tile.active || !tile.active && tile.animationTicks > 8) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if (tile.animationTicks <= 8)
				GlStateManager.translate(0F, 0F, 0F - 1F / 8 * tile.animationTicks);
			if (tile.animationTicks > 8)
				GlStateManager.translate(0F, 0F, 0F - 1F);
			if (tile.animationTicks > 1) {
				GlStateManager.pushMatrix();
				GlStateManager.rotate(tile.moveProgress, 0, 1, 0);
				head.render(0.0625F);
				foreHead.render(0.0625F);
				nose.render(0.0625F);
				cheekLeft.render(0.0625F);
				cheekRight.render(0.0625F);
				topJaw.render(0.0625F);
				leftTopFang.render(0.0625F);
				rightTopFang.render(0.0625F);
				tongueTop.render(0.0625F);
				tongueBottom.render(0.0625F);
				bottomJaw.render(0.0625F);
				leftBottomFang.render(0.0625F);
				rightBottomFang.render(0.0625F);
				GlStateManager.popMatrix();
				
				body.render(0.0625F);
				GlStateManager.pushMatrix();
				if (tile.animationTicks > 8)
					GlStateManager.rotate(10F - tile.animationTicks, 0, 1, 0);
				else
					GlStateManager.rotate(-0, 0, 1, 0);
				rightArm1.render(0.0625F);
				rightArm2.render(0.0625F);
				rightPalm.render(0.0625F);
				rightOutFinger.render(0.0625F);
				rightInFinger.render(0.0625F);
				rightThumb.render(0.0625F);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				if (tile.animationTicks > 8)
					GlStateManager.rotate(-10F + tile.animationTicks, 0, 1, 0);
				else
					GlStateManager.rotate(0, 0, 1, 0);
				leftArm1.render(0.0625F);
				leftArm2.render(0.0625F);
				leftPalm.render(0.0625F);
				leftOutFinger.render(0.0625F);
				leftInFinger.render(0.0625F);
				leftThumb.render(0.0625F);
				GlStateManager.popMatrix();
			}
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
