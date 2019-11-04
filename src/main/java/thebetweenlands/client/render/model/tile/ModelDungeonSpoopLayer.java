package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;

@SideOnly(Side.CLIENT)
public class ModelDungeonSpoopLayer extends ModelBase {
    ModelRenderer face;

    public ModelDungeonSpoopLayer() {
        textureWidth = 64;
        textureHeight = 32;
        face = new ModelRenderer(this, 0, 0);
        face.setRotationPoint(0.0F, 16.0F, 0.0F);
        face.addBox(-8.0F, 7.01F, -8.0F, 16, 1, 16, 0.0F);
    }

	public void renderSpoop(TileEntityMudBricksSpikeTrap tile, float partialTicks) {
		float interpolatedAnimationTicks = tile.prevSpoopAnimationTicks + (tile.spoopAnimationTicks - tile.prevSpoopAnimationTicks) * partialTicks;
		if (tile.activeSpoop || !tile.activeSpoop && interpolatedAnimationTicks > 0) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1F, 1F, 1F, 1F - 1F + 0.0375F * interpolatedAnimationTicks);
			face.render(0.0625F);
			GlStateManager.disableBlend();
		}
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
