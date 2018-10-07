package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityBeamRelay;

@SideOnly(Side.CLIENT)
public class RenderBeamRelay extends TileEntitySpecialRenderer<TileEntityBeamRelay> {
	@Override
	public void render(TileEntityBeamRelay tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());

		if (tile == null || !tile.hasWorld())
			return;

		if (state == null || state.getBlock() != BlockRegistry.BETWEENSTONE_BRICKS_BEAM_RELAY)
			return;

		if (!tile.showRenderBox)
			return;

		EnumFacing facing = state.getValue(BlockBeamRelay.FACING);
/*
		double now = 0;
		if (tile.getWorld() != null)
			now = (tile.getWorld().getTotalWorldTime() % Short.MAX_VALUE) + partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
			GlStateManager.rotate((float) -now * 2F, 0F, 1F, 0F);
		if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
			GlStateManager.rotate((float) -now * 2F, 1F, 0F, 0F);
		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
			GlStateManager.rotate((float) -now * 2F, 0F, 0F, 1F);
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5D, -0.5D, -0.5D);
		GlStateManager.scale(0.999D, 0.999D, 0.999D);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableCull();
		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		RenderGlobal.renderFilledBox(tile.getAABBForRender(), 0F, 0F, 1F, 0.75F);
		RenderGlobal.drawSelectionBoundingBox(tile.getAABBForRender(), 1F, 1F, 1F, 1F);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();*/
	}

}