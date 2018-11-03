package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;

@SideOnly(Side.CLIENT)
public class ModelDungeonDoorRunesLayer extends ModelBase {

	public ModelRenderer top_overlay;
	public ModelRenderer mid_overlay;
	public ModelRenderer bottom_overlay;

	public ModelDungeonDoorRunesLayer() {
		textureWidth = 32;
		textureHeight = 32;

		top_overlay = new ModelRenderer(this, 0, 0);
		top_overlay.setRotationPoint(0.0F, 11.5F, -5.5F);
		top_overlay.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);

		mid_overlay = new ModelRenderer(this, 1, 11);
		mid_overlay.setRotationPoint(0.0F, 16.0F, -6.0F);
		mid_overlay.addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4, 0.0F);

		bottom_overlay = new ModelRenderer(this, 0, 20);
		bottom_overlay.setRotationPoint(0.0F, 20.5F, -5.5F);
		bottom_overlay.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
	}

	public void renderTopOverlay(TileEntityDungeonDoorRunes tile, float scale) {
		top_overlay.rotateAngleX = 0F + tile.top_rotate / (180F / (float) Math.PI);
		top_overlay.render(scale);
	}

	public void renderMidOverlay(TileEntityDungeonDoorRunes tile, float scale) {
		mid_overlay.rotateAngleX = 0F + tile.mid_rotate / (180F / (float) Math.PI);
		mid_overlay.render(scale);
	}

	public void renderBottomOverlay(TileEntityDungeonDoorRunes tile, float scale) {
		bottom_overlay.rotateAngleX = 0F + tile.bottom_rotate / (180F / (float) Math.PI);
		bottom_overlay.render(scale);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
