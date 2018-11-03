package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
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

	public void renderTopOverlay(TileEntity tile, float scale) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			top_overlay.rotateAngleX = 0F + tileDoor.top_rotate / (180F / (float) Math.PI);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -1.001F, -0.001F);
			top_overlay.render(scale);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -0.001F, -0.001F);
			top_overlay.render(scale);
			GlStateManager.popMatrix();
		}
	}

	public void renderMidOverlay(TileEntity tile, float scale) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			mid_overlay.rotateAngleX = 0F + tileDoor.mid_rotate / (180F / (float) Math.PI);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -1.001F, -0.001F);
			mid_overlay.render(scale);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -0.001F, -0.001F);
			mid_overlay.render(scale);
			GlStateManager.popMatrix();
		}
	}

	public void renderBottomOverlay(TileEntity tile, float scale) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			bottom_overlay.rotateAngleX = 0F + tileDoor.bottom_rotate / (180F / (float) Math.PI);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -1.001F, -0.001F);
			bottom_overlay.render(scale);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -0.001F, -0.001F);
			bottom_overlay.render(scale);
			GlStateManager.popMatrix();
		}
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
