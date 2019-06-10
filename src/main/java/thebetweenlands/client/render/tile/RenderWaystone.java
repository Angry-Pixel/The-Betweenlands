package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import thebetweenlands.client.render.model.tile.ModelWaystone;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.util.StatePropertyHelper;

public class RenderWaystone extends TileEntitySpecialRenderer<TileEntityWaystone> {
	private static final ModelWaystone MODEL = new ModelWaystone();

	private static final ResourceLocation TEXTURE_ACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/waystone_active.png");
	private static final ResourceLocation TEXTURE_INACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/waystone_inactive.png");
	private static final ResourceLocation TEXTURE_GRASS = new ResourceLocation("thebetweenlands:textures/tiles/waystone_grass.png");

	@Override
	public void render(TileEntityWaystone te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockWaystone.Part part = StatePropertyHelper.getStatePropertySafely(te, BlockWaystone.class, BlockWaystone.PART, BlockWaystone.Part.BOTTOM);
		boolean active = StatePropertyHelper.getStatePropertySafely(te, BlockWaystone.class, BlockWaystone.ACTIVE, false);

		if(destroyStage < 0 || te == null) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);

		boolean isBreakingAnimation = te != null && destroyStage >= 0;

		if(part == BlockWaystone.Part.BOTTOM || isBreakingAnimation) {
			float rotation = 0.0F;;

			if(isBreakingAnimation) {
				this.bindTexture(DESTROY_STAGES[destroyStage]);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.scale(8.0F, 8.0F, 1.0F);
				GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);

				TileEntity master = null;

				switch(part) {
				case TOP:
					GlStateManager.translate(0, 2, 0);
					if(te.getWorld() != null) master = te.getWorld().getTileEntity(te.getPos().down(2));
					break;
				case MIDDLE:
					GlStateManager.translate(0, 1, 0);
					if(te.getWorld() != null) master = te.getWorld().getTileEntity(te.getPos().down(1));
					break;
				case BOTTOM:
					master = te;
					break;
				}

				if(master instanceof TileEntityWaystone) {
					rotation = ((TileEntityWaystone) master).getRotation();
				}
			} else {
				if(active) {
					this.bindTexture(TEXTURE_ACTIVE);
				} else {
					this.bindTexture(TEXTURE_INACTIVE);
				}

				rotation = te != null ? te.getRotation() : 0;

				if(active && te != null && ShaderHelper.INSTANCE.isWorldShaderActive()) {
					BlockPos pos = te.getPos();
					double px = pos.getX() + 0.5D;
					double py = pos.getY();
					double pz = pos.getZ() + 0.5D;

					EntityPlayer closestPlayer = te.getWorld().getClosestPlayer(px, py, pz, 4.0D, false);

					if(closestPlayer != null) {
						ShaderHelper.INSTANCE.require();

						float brightness = 3.0F * (float)(1.0D - closestPlayer.getDistance(px, py, pz) / 4.0D);

						ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(px, py, pz, 8F, 0.44f * brightness, 0.8f * brightness, 1.0f * brightness));
					}
				}
			}

			GlStateManager.rotate(rotation, 0, 1, 0);

			MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);

			if(isBreakingAnimation) {
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			} else {
				this.bindTexture(TEXTURE_GRASS);

				int grassColor = te != null ? BiomeColorHelper.getGrassColorAtPos(te.getWorld(), te.getPos()) : ColorizerGrass.getGrassColor(0.5D, 1.0D);

				GlStateManager.color((float)(grassColor >> 16 & 0xff) / 255F, (float)(grassColor >> 8 & 0xff) / 255F, (float)(grassColor & 0xff) / 255F);

				GlStateManager.enablePolygonOffset();
				GlStateManager.doPolygonOffset(-3, -3);

				MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);

				GlStateManager.disablePolygonOffset();
			}
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityWaystone te) {
		return StatePropertyHelper.getStatePropertySafely(te, BlockWaystone.class, BlockWaystone.ACTIVE, false);
	}
}
