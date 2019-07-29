package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitChain;
import thebetweenlands.common.block.structure.BlockDecayPitGroundChain;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityDecayPitGroundChain;
import thebetweenlands.util.StatePropertyHelper;
@SideOnly(Side.CLIENT)
public class RenderDecayPitGroundChain extends TileEntitySpecialRenderer<TileEntityDecayPitGroundChain> {
	public static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_chain.png");
	private final ModelDecayPitChain CHAIN_MODEL = new ModelDecayPitChain();

	@Override
	public void render(TileEntityDecayPitGroundChain tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile == null || !tile.hasWorld())
			return;

		float scroll = tile.animationTicksChainPrev * 0.0078125F + (tile.animationTicksChain * 0.0078125F - tile.animationTicksChainPrev * 0.0078125F) * partialTicks;
		EnumFacing facing = StatePropertyHelper.getStatePropertySafely(tile, BlockDecayPitGroundChain.class, BlockDecayPitGroundChain.FACING, EnumFacing.NORTH);
		int easyFacingIndex = 0;
		// S = 0, W = 1, N = 2, E = 3

		switch (facing) {
		case UP:
			easyFacingIndex = 2;
			break;
		case DOWN:
			easyFacingIndex = 2;
			break;
		case NORTH:
			easyFacingIndex = 2;
			break;
		case SOUTH:
			easyFacingIndex = 0;
			break;
		case WEST:
			easyFacingIndex = 1;
			break;
		case EAST:
			easyFacingIndex = 3;
			break;
		}

		bindTexture(CHAIN_TEXTURE);

		if (tile.isMoving()) {
			GlStateManager.pushMatrix();
			if (tile.isRaising()) {
				GlStateManager.translate(x+ 0.5F, y + 0.5F + scroll, z+ 0.5F);
				GlStateManager.scale(-1F, -1F, 1F);
				GlStateManager.rotate(easyFacingIndex * 90F, 0F, 1F, 0F);
				CHAIN_MODEL.render(0.0625F);
			}
			if (!tile.isRaising()) {
				GlStateManager.translate(x+ 0.5F, y + tile.getLength() + 1.5F - scroll, z+ 0.5F);
				GlStateManager.scale(-1F, -1F, 1F);
				GlStateManager.rotate(easyFacingIndex * 90F, 0F, 1F, 0F);
				CHAIN_MODEL.render(0.0625F);
			}
			GlStateManager.popMatrix();
		}

		for (int len = 1; len <= tile.getLength(); len++) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x+ 0.5F, y + len + 0.5F + (tile.isRaising() ? scroll : -scroll), z+ 0.5F);
			GlStateManager.scale(-1F, -1F, 1F);
			GlStateManager.rotate(easyFacingIndex * 90F, 0F, 1F, 0F);
			CHAIN_MODEL.render(0.0625F);
			GlStateManager.popMatrix();
		}

	}

	@Override
	public boolean isGlobalRenderer(TileEntityDecayPitGroundChain tile) {
		return true;
	}
}