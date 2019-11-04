package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;

@SideOnly(Side.CLIENT)
public class ModelDungeonDoorRunes extends ModelBase {

	public ModelRenderer slate1;
	public ModelRenderer slate2;
	public ModelRenderer slate3;
	public ModelRenderer top;
	public ModelRenderer mid;
	public ModelRenderer bottom;
	public ModelRenderer frame_top;
	public ModelRenderer frame_bottom;
	public ModelRenderer frame_left;
	public ModelRenderer frame_right;
	public ModelRenderer slate2b;
	public ModelRenderer slate2c;
	public ModelRenderer slate3b;
	public ModelRenderer slate3c;
	public ModelRenderer slate3d;
	public ModelRenderer behind;
	public ModelRenderer tile_block_mid;
	public ModelRenderer tile_block_left;
	public ModelRenderer tile_block_right;

	public ModelDungeonDoorRunes() {
		textureWidth = 256;
		textureHeight = 256;
		mid = new ModelRenderer(this, 41, 96);
		mid.setRotationPoint(0.0F, 0.0F, -6.0F);
		mid.addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4, 0.0F);
		frame_right = new ModelRenderer(this, 68, 121);
		frame_right.setRotationPoint(0.0F, 0.0F, 0.0F);
		frame_right.addBox(6.9F, -7.0F, -9.0F, 1, 14, 2, 0.0F);
		slate2 = new ModelRenderer(this, 0, 55);
		slate2.setRotationPoint(-8.0F, 24.0F, -4.0F);
		slate2.addBox(-16.0F, -48.0F, -1.0F, 16, 48, 4, 0.0F);
		setRotateAngle(slate2, 0.0F, 0.045553093477052F, 0.0F);
		slate2b = new ModelRenderer(this, 0, 108);
		slate2b.setRotationPoint(0.0F, 0.0F, 0.0F);
		slate2b.addBox(-16.0F, -48.0F, -3.0F, 16, 38, 2, 0.0F);
		slate1 = new ModelRenderer(this, 0, 0);
		slate1.setRotationPoint(0.0F, 24.0F, -4.0F);
		slate1.addBox(-8.0F, -48.0F, -3.0F, 16, 48, 6, 0.0F);
		bottom = new ModelRenderer(this, 41, 105);
		bottom.setRotationPoint(0.0F, 4.5F, -5.5F);
		bottom.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
		slate3c = new ModelRenderer(this, 125, 0);
		slate3c.setRotationPoint(0.0F, 0.0F, 0.0F);
		slate3c.addBox(-6.0F, -36.0F, 0.0F, 14, 36, 2, 0.0F);
		slate2c = new ModelRenderer(this, 0, 149);
		slate2c.setRotationPoint(0.0F, 0.0F, 0.0F);
		slate2c.addBox(-16.0F, -10.0F, -3.0F, 12, 10, 2, 0.0F);
		slate3 = new ModelRenderer(this, 45, 0);
		slate3.setRotationPoint(16.0F, 24.0F, -7.0F);
		slate3.addBox(-8.0F, -40.0F, 2.0F, 16, 40, 4, 0.0F);
		setRotateAngle(slate3, -0.017453292519943295F, 0.0F, 0.0F);
		frame_left = new ModelRenderer(this, 44, 121);
		frame_left.setRotationPoint(0.0F, 0.0F, 0.0F);
		frame_left.addBox(-8.0F, -7.0F, -9.0F, 1, 14, 2, 0.0F);
		slate3b = new ModelRenderer(this, 86, 0);
		slate3b.setRotationPoint(0.0F, -40.0F, 0.0F);
		slate3b.addBox(-5.0F, -8.0F, 0.0F, 13, 8, 6, 0.0F);
		top = new ModelRenderer(this, 41, 85);
		top.setRotationPoint(0.0F, -4.5F, -5.5F);
		top.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
		frame_top = new ModelRenderer(this, 44, 116);
		frame_top.setRotationPoint(0.0F, 0.0F, 0.0F);
		frame_top.addBox(-7.0F, -8.0F, -9.0F, 14, 1, 2, 0.0F);
		frame_bottom = new ModelRenderer(this, 44, 138);
		frame_bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
		frame_bottom.addBox(-7.0F, 7.0F, -9.0F, 14, 1, 2, 0.0F);
		slate3d = new ModelRenderer(this, 158, 0);
		slate3d.setRotationPoint(0.0F, 0.0F, 0.0F);
		slate3d.addBox(-3.0F, -40.0F, 0.0F, 11, 4, 2, 0.0F);
		behind = new ModelRenderer(this, 81, 46);
        behind.setRotationPoint(0.0F, 0.0F, 0.0F);
        behind.addBox(-24.0F, -24.0F, -1.0F, 48, 48, 9, 0.0F);
        tile_block_left = new ModelRenderer(this, 0, 162);
        tile_block_left.setRotationPoint(-16.0F, 24.0F, 0.0F);
        tile_block_left.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16, 0.0F);
        tile_block_right = new ModelRenderer(this, 130, 162);
        tile_block_right.setRotationPoint(16.0F, 24.0F, 0.0F);
        tile_block_right.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16, 0.0F);
        tile_block_mid = new ModelRenderer(this, 65, 162);
        tile_block_mid.setRotationPoint(-0.0F, 24.0F, 0.0F);
        tile_block_mid.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16, 0.0F);

		slate2.addChild(slate2b);
		slate2.addChild(slate2c);
		slate3.addChild(slate3b);
		slate3.addChild(slate3c);
		slate3.addChild(slate3d);
	}

	public void render(TileEntityDungeonDoorRunes tile, float scale, float partialTicks) {
		top.rotateAngleX = 0F + (tile.lastTickTopRotate + (tile.top_rotate - tile.lastTickTopRotate) * partialTicks) / (180F / (float) Math.PI);
		mid.rotateAngleX = 0F + (tile.lastTickMidRotate + (tile.mid_rotate - tile.lastTickMidRotate) * partialTicks) / (180F / (float) Math.PI);
		bottom.rotateAngleX = 0F + (tile.lastTickBottomRotate + (tile.bottom_rotate - tile.lastTickBottomRotate) * partialTicks) / (180F / (float) Math.PI);

		if(tile.isMimic()) {
			slate1.rotateAngleX = 0F + (tile.last_tick_slate_1_rotate + (tile.slate_1_rotate - tile.last_tick_slate_1_rotate) * partialTicks) / (180F / (float) Math.PI);
			slate2.rotateAngleX = 0F + (tile.last_tick_slate_2_rotate + (tile.slate_2_rotate - tile.last_tick_slate_2_rotate) * partialTicks) / (180F / (float) Math.PI);
			slate3.rotateAngleX = 0F + (tile.last_tick_slate_3_rotate + (tile.slate_3_rotate - tile.last_tick_slate_3_rotate) * partialTicks) / (180F / (float) Math.PI);
		}
		if(!tile.isMimic()) {
			slate1.rotateAngleX = 0F;
			slate2.rotateAngleX = 0F;
			slate3.rotateAngleX = 0F;
		}
		if(!tile.hide_slate_1) {
			if(!tile.isMimic()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_1_rotate + (tile.slate_1_rotate - tile.last_tick_slate_1_rotate) * partialTicks) * scale, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTicks) * scale);
				slate1.render(scale);
				GlStateManager.popMatrix();
			}
			else
				slate1.render(scale);
		}
		if(!tile.hide_slate_2) {
			if(!tile.isMimic()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_2_rotate + (tile.slate_2_rotate - tile.last_tick_slate_2_rotate) * partialTicks) * scale, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTicks) * scale);
				slate2.render(scale);
				GlStateManager.popMatrix();
			}
		else
			slate2.render(scale);
		}
		if(!tile.hide_slate_3) {
			if(!tile.isMimic()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_3_rotate + (tile.slate_3_rotate - tile.last_tick_slate_3_rotate) * partialTicks) * scale, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTicks) * scale);
				slate3.render(scale);
				GlStateManager.popMatrix();
			}
			else
				slate3.render(scale);
		}
		if (!tile.hide_lock) {
			if(!tile.isMimic()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_1_rotate + (tile.slate_1_rotate - tile.last_tick_slate_1_rotate) * partialTicks) * scale, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTicks) * scale);
				renderLockParts(tile, scale);
				GlStateManager.popMatrix();
			}
			else {
				renderLockParts(tile, scale);
			}
		}
		if (!tile.hide_back_wall) {
			if(!tile.isMimic()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F + 0.1375F * (tile.last_tick_slate_1_rotate + (tile.slate_1_rotate - tile.last_tick_slate_1_rotate) * partialTicks) * scale, 0F + 0.275F * (tile.last_tick_recess_pos + (tile.recess_pos - tile.last_tick_recess_pos) * partialTicks) * scale);
				GlStateManager.scale(0.99F, 1F, 1F);
				behind.render(scale);
				GlStateManager.popMatrix();
			}
			else {
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.99F, 1F, 1F);
				behind.render(scale);
				GlStateManager.popMatrix();
			}
		}

		if(!tile.isMimic() && tile.is_in_dungeon) {
			if(tile.animate_tile_recess) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F, 0F + 0.275F * (tile.last_tick_recess_pos_tile_1 + (tile.tile_1_recess_pos - tile.last_tick_recess_pos_tile_1) * partialTicks) * scale);
				tile_block_right.render(scale);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F, 0F + 0.275F * (tile.last_tick_recess_pos_tile_2 + (tile.tile_2_recess_pos - tile.last_tick_recess_pos_tile_2) * partialTicks) * scale);
				tile_block_mid.render(scale);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0F, 0F, 0F + 0.275F * (tile.last_tick_recess_pos_tile_3 + (tile.tile_3_recess_pos - tile.last_tick_recess_pos_tile_3) * partialTicks) * scale);
				tile_block_left.render(scale);
				GlStateManager.popMatrix();
			}
			else {
				tile_block_left.render(scale);
				tile_block_mid.render(scale);
				tile_block_right.render(scale);
			}
		}
	}

	public void renderLockParts(TileEntityDungeonDoorRunes tile, float scale) {
		if (!tile.is_gate_entrance) {
			frame_right.render(scale);
			frame_left.render(scale);
			frame_top.render(scale);
			frame_bottom.render(scale);
			top.render(scale);
			mid.render(scale);
			bottom.render(scale);
		}
	}

	public void renderItem(float scale) {
		slate2.render(scale);
		slate1.render(scale);
		slate3.render(scale);
		frame_right.render(scale);
		frame_left.render(scale);
		frame_top.render(scale);
		frame_bottom.render(scale);
		mid.render(scale);
		top.render(scale);
		bottom.render(scale);
		behind.render(scale);
		tile_block_left.render(scale);
		tile_block_right.render(scale);
		tile_block_mid.render(scale);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
