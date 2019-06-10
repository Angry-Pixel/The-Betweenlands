package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

@SideOnly(Side.CLIENT)
public class ModelSpikeBlock extends ModelBase {
    ModelRenderer shaft1;
    ModelRenderer shaft2;
    ModelRenderer shaft3;
    ModelRenderer shaft4;
    ModelRenderer shaft5;
    ModelRenderer shaft6;
    ModelRenderer shaft7;
    ModelRenderer shaft8;
    ModelRenderer shaft9;

    public ModelSpikeBlock() {
        textureWidth = 64;
        textureHeight = 64;
        shaft4 = new ModelRenderer(this, 27, 0);
        shaft4.setRotationPoint(-4.0F, 25.0F, 3.0F);
        shaft4.addBox(-1.0F, -11.0F, -1.0F, 2, 11, 2, 0.0F);
        setRotation(shaft4, -0.18203784098300857F, 0.091106186954104F, -0.18203784098300857F);
        shaft3 = new ModelRenderer(this, 18, 0);
        shaft3.setRotationPoint(-4.0F, 25.0F, -3.0F);
        shaft3.addBox(-1.0F, -13.0F, -1.0F, 2, 13, 2, 0.0F);
        setRotation(shaft3, 0.18203784098300857F, 0.6373942428283291F, 0.0F);
        shaft8 = new ModelRenderer(this, 45, 12);
        shaft8.setRotationPoint(0.0F, 25.0F, -5.0F);
        shaft8.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(shaft8, 0.18203784098300857F, 0.4553564018453205F, 0.091106186954104F);
        shaft6 = new ModelRenderer(this, 45, 0);
        shaft6.setRotationPoint(0.0F, 25.0F, 5.0F);
        shaft6.addBox(-1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F);
        setRotation(shaft6, -0.27314402793711257F, -0.6373942428283291F, 0.18203784098300857F);
        shaft1 = new ModelRenderer(this, 0, 0);
        shaft1.setRotationPoint(0.0F, 24.0F, 0.0F);
        shaft1.addBox(-1.0F, -15.0F, -1.0F, 2, 16, 2, 0.0F);
        setRotation(shaft1, -0.091106186954104F, 0.6373942428283291F, 0.0F);
        shaft7 = new ModelRenderer(this, 54, 0);
        shaft7.setRotationPoint(5.0F, 25.0F, 0.0F);
        shaft7.addBox(-1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F);
        setRotation(shaft7, 0.0F, -0.40980330836826856F, 0.136659280431156F);
        shaft9 = new ModelRenderer(this, 54, 12);
        shaft9.setRotationPoint(-5.0F, 25.0F, 0.0F);
        shaft9.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(shaft9, 0.0F, 0.7740535232594852F, -0.22759093446006054F);
        shaft5 = new ModelRenderer(this, 36, 0);
        shaft5.setRotationPoint(4.0F, 25.0F, 4.0F);
        shaft5.addBox(-1.0F, -11.0F, -1.0F, 2, 11, 2, 0.0F);
        setRotation(shaft5, -0.136659280431156F, 0.40980330836826856F, 0.045553093477052F);
        shaft2 = new ModelRenderer(this, 9, 0);
        shaft2.setRotationPoint(4.0F, 25.0F, -4.0F);
        shaft2.addBox(-1.1F, -13.0F, -1.0F, 2, 13, 2, 0.0F);
        setRotation(shaft2, 0.136659280431156F, 0.31869712141416456F, 0.136659280431156F);
    }

	public void renderSpikes(TileEntitySpikeTrap tile, float partialTicks) {
		float interpolatedAnimationTicks = tile.prevAnimationTicks + (tile.animationTicks - tile.prevAnimationTicks) * partialTicks;
		if (tile.active || !tile.active && interpolatedAnimationTicks > 0) {
			if (interpolatedAnimationTicks <= 5)
				GlStateManager.translate(0F, 0F - 1F / 5 * interpolatedAnimationTicks, 0F);
			if (interpolatedAnimationTicks > 5)
				GlStateManager.translate(0F, - 1F, 0F);
	        shaft4.render(0.0625F);
	        shaft3.render(0.0625F);
	        shaft8.render(0.0625F);
	        shaft6.render(0.0625F);
	        shaft1.render(0.0625F);
	        shaft7.render(0.0625F);
	        shaft9.render(0.0625F);
	        shaft5.render(0.0625F);
	        shaft2.render(0.0625F);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
