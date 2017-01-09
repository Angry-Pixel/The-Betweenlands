package thebetweenlands.client.render.tile;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.event.handler.WorldRenderHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleWisp;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.util.config.ConfigHandler;

public class RenderWisp extends TileEntitySpecialRenderer<TileEntityWisp> {
	@Override
	public void renderTileEntityAt(TileEntityWisp tileEntity, double x, double y, double z, float partialTicks, int destroyProgress) {
		WorldRenderHandler.WISP_TILE_LIST.add(Pair.of(Pair.of(this, tileEntity), new Vec3d(x, y, z)));

		List<Object> particleList = tileEntity.particleList;

		double renderViewX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		double renderViewY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		double renderViewZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		if(!BlockWisp.canSee(tileEntity.getWorld(), tileEntity.getPos())) {
			double dist = Minecraft.getMinecraft().getRenderViewEntity().getDistance(x + renderViewX, y + renderViewY, z + renderViewZ);
			if(dist > 50 || dist < 10) {
				return;
			}
		}

		if(particleList.size() < 1000 && !Minecraft.getMinecraft().isGamePaused()) {
			if(System.nanoTime() - ((TileEntityWisp)tileEntity).lastSpawn >= (500f - 500.0f * ConfigHandler.wispQuality / 150.0f) * 1000000L) {
				((TileEntityWisp)tileEntity).lastSpawn = System.nanoTime();

				IBlockState blockState = tileEntity.getWorld().getBlockState(tileEntity.getPos());

				if(blockState.getBlock() == BlockRegistry.WISP) {
					int colorIndex = blockState.getValue(BlockWisp.COLOR);

					int color = BlockWisp.COLORS[colorIndex * 2];
					float r = (color >> 16 & 0xFF) / 255F;
					float g = (color >> 8 & 0xFF) / 255F;
					float b = (color & 0xFF) / 255F;

					particleList.add(BLParticles.WISP.create(tileEntity.getWorld(), 
							x + 0.5 + renderViewX, 
							y + 0.5 + renderViewY, 
							z + 0.5 + renderViewZ, 
							ParticleArgs.get().withColor(r, g, b, 1.0F).withScale(3.0F)));

					color = BlockWisp.COLORS[colorIndex * 2 + 1];
					r = (color >> 16 & 0xFF) / 255F;
					g = (color >> 8 & 0xFF) / 255F;
					b = (color & 0xFF) / 255F;

					particleList.add(BLParticles.WISP.create(tileEntity.getWorld(), 
							x + 0.5 + renderViewX, 
							y + 0.5 + renderViewY, 
							z + 0.5 + renderViewZ, 
							ParticleArgs.get().withColor(r, g, b, 1.0F).withScale(2.0F)));
				}
			}
		}
	}

	/**
	 * Renders the wisp particles
	 * @param tileEntity
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTicks
	 */
	public void renderWispParticles(VertexBuffer vertexBuffer, TileEntityWisp tileEntity, double x, double y, double z, float partialTicks) {
		List<Object> particleList = tileEntity.particleList;

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();

		Particle.interpPosX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * (double)partialTicks;
		Particle.interpPosY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * (double)partialTicks;
		Particle.interpPosZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * (double)partialTicks;
		Particle.field_190016_K = viewer.getLook(partialTicks);

		for(Object particle : particleList){
			ParticleWisp wisp = (ParticleWisp) particle;
			wisp.renderParticle(vertexBuffer, viewer, partialTicks, 
					ActiveRenderInfo.getRotationX(),
					ActiveRenderInfo.getRotationXZ(),
					ActiveRenderInfo.getRotationZ(),
					ActiveRenderInfo.getRotationYZ(),
					ActiveRenderInfo.getRotationXY());
		}
	}
}
