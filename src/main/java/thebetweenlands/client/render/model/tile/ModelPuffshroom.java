package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityPuffshroom;

@SideOnly(Side.CLIENT)

public class ModelPuffshroom extends ModelBase {

	ModelRenderer base;
	ModelRenderer stalk;
	ModelRenderer cap1;
	ModelRenderer cap2;
	ModelRenderer topBack;
	ModelRenderer topFront;
	ModelRenderer topRight;
	ModelRenderer topLeft;

	public ModelPuffshroom() {
		textureWidth = 64;
		textureHeight = 64;
		cap1 = new ModelRenderer(this, 24, 10);
		cap1.setRotationPoint(0.0F, 0.0F, 0.0F);
		cap1.addBox(-5.0F, -5.9F, -5.0F, 10, 3, 10, 0.0F);
		cap2 = new ModelRenderer(this, 0, 0);
		cap2.setRotationPoint(0.0F, 0.0F, 0.0F);
		cap2.addBox(-4.0F, -6.9F, -4.0F, 8, 1, 8, 0.0F);
		topBack = new ModelRenderer(this, 47, 0);
		topBack.setRotationPoint(0.0F, 0.0F, 0.0F);
		topBack.addBox(-1.0F, -7.9F, 1.0F, 2, 1, 1, 0.0F);
		topFront = new ModelRenderer(this, 47, 6);
		topFront.setRotationPoint(0.0F, 0.0F, 0.0F);
		topFront.addBox(-1.0F, -7.9F, -2.0F, 2, 1, 1, 0.0F);
		base = new ModelRenderer(this, 0, 24);
		base.setRotationPoint(0.0F, 16.0F, 0.0F);
		base.addBox(-3.0F, -0.1F, -3.0F, 6, 1, 6, 0.0F);
		topLeft = new ModelRenderer(this, 36, 0);
		topLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		topLeft.addBox(1.0F, -7.9F, -2.0F, 1, 1, 4, 0.0F);
		stalk = new ModelRenderer(this, 4, 16);
		stalk.setRotationPoint(0.0F, 0.0F, 0.0F);
		stalk.addBox(-2.0F, -2.9F, -2.0F, 4, 3, 4, 0.0F);
		topRight = new ModelRenderer(this, 54, 0);
		topRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		topRight.addBox(-2.0F, -7.9F, -2.0F, 1, 1, 4, 0.0F);
		base.addChild(cap1);
		base.addChild(cap2);
		base.addChild(topBack);
		base.addChild(topFront);
		base.addChild(topLeft);
		base.addChild(stalk);
		base.addChild(topRight);
	}

	public void render(TileEntityPuffshroom tile, float partialTickTime) {
		float animationTicks = tile.animationTicks;
		float prevAnimationTicks = tile.prevAnimationTicks;
		float interAnimationTicks = animationTicks + (animationTicks - prevAnimationTicks) * partialTickTime;

		GlStateManager.pushMatrix();
		if (animationTicks <= 8)
			GlStateManager.translate(0F, 0F - 1F * 0.125F * interAnimationTicks * 0.5F, 0F);
		if (animationTicks > 8)
			GlStateManager.translate(0F, 0F - 0.5F, 0F);
		if (animationTicks > 1) {
			if (animationTicks > 8 && animationTicks < 12)
				GlStateManager.scale(1 + (1F * 0.0625F * interAnimationTicks * 0.25F), 1F, 1 + (1F * 0.0625F * interAnimationTicks * 0.25F));
			if (animationTicks >= 12)
				GlStateManager.scale(1 - (1F * 0.0625F * interAnimationTicks * 0.25F), 1F, 1 - (1F * 0.0625F * interAnimationTicks * 0.25F));
			base.render(0.0625F);
		}
		GlStateManager.popMatrix();
	}
}