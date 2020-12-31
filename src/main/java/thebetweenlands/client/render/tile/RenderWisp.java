package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.util.StatePropertyHelper;

public class RenderWisp extends TileEntitySpecialRenderer<TileEntityWisp> {
	@Override
	public void render(TileEntityWisp tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		double renderViewX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		double renderViewY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		double renderViewZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();

		if(!StatePropertyHelper.getStatePropertySafely(tileEntity, BlockWisp.class, BlockWisp.VISIBLE, false)) {
			double dist = renderView != null ? renderView.getDistance(x + renderViewX, y + renderViewY, z + renderViewZ) : 0.0D;
			if(dist > 50 || dist < 10) {
				return;
			}
		}

		int colorIndex = StatePropertyHelper.getStatePropertySafely(tileEntity, BlockWisp.class, BlockWisp.COLOR, 0);

		if(!Minecraft.getMinecraft().isGamePaused()) {
			if(System.nanoTime() - ((TileEntityWisp)tileEntity).lastSpawn >= (500f - 500.0f * BetweenlandsConfig.RENDERING.wispQuality / 150.0f) * 1000000L) {
				((TileEntityWisp)tileEntity).lastSpawn = System.nanoTime();

				int color = BlockWisp.COLORS[colorIndex * 2];
				float r = (color >> 16 & 0xFF) / 255F;
				float g = (color >> 8 & 0xFF) / 255F;
				float b = (color & 0xFF) / 255F;

				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.WISPS, BLParticles.WISP.create(tileEntity.getWorld(), 
						x + 0.5 + renderViewX, 
						y + 0.5 + renderViewY, 
						z + 0.5 + renderViewZ, 
						ParticleArgs.get().withColor(r, g, b, 1.0F).withScale(3.0F)));

				color = BlockWisp.COLORS[colorIndex * 2 + 1];
				r = (color >> 16 & 0xFF) / 255F;
				g = (color >> 8 & 0xFF) / 255F;
				b = (color & 0xFF) / 255F;

				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.WISPS, BLParticles.WISP.create(tileEntity.getWorld(), 
						x + 0.5 + renderViewX, 
						y + 0.5 + renderViewY, 
						z + 0.5 + renderViewZ, 
						ParticleArgs.get().withColor(r, g, b, 1.0F).withScale(2.0F)));
			}
		}

		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			
			for(int i = 0; i < 2; i++) {
				int color = BlockWisp.COLORS[colorIndex * 2 + i];
				float r = (color >> 16 & 0xFF) / 255F;
				float g = (color >> 8 & 0xFF) / 255F;
				float b = (color & 0xFF) / 255F;

				float size = 1.5f;

				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(tileEntity.getPos().getX() + 0.5f, tileEntity.getPos().getY(), tileEntity.getPos().getZ() + 0.5f,
						i == 0 ? size : size * 0.5F,
								r * (i == 0 ? 6 : 3),
								g * (i == 0 ? 6 : 3),
								b * (i == 0 ? 6 : 3)));
			}
		}
	}
}
