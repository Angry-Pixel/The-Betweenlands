package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventHeavyRain extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "heavy_rain");

	public EventHeavyRain(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(50000) + 60000;
	}
	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(5000) + 8000;
	}

	@Override
	public void setActive(boolean active) {
		if((active && !this.getRegistry().winter.isActive()) || !active) {
			super.setActive(active);
		}
	}

	@Override
	public void update(World world) {
		super.update(world);

		if(!world.isRemote && this.getRegistry().winter.isActive()) {
			this.setActive(false);
		}

		if(this.isActive() && world.provider instanceof WorldProviderBetweenlands && world.rand.nextInt(20) == 0) {
			if(!world.isRemote && world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer)world;
				for (Iterator<Chunk> iterator = worldServer.getPersistentChunkIterable(worldServer.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); ) {
					Chunk chunk = iterator.next();
					if(world.rand.nextInt(4) == 0) {
						int cbx = world.rand.nextInt(16);
						int cbz = world.rand.nextInt(16);
						BlockPos pos = chunk.getPrecipitationHeight(new BlockPos(chunk.getPos().getXStart() + cbx, -999, chunk.getPos().getZStart() + cbz));
						if(world.getBlockState(pos.add(0, -1, 0)).getBlock() != BlockRegistry.PUDDLE && BlockRegistry.PUDDLE.canPlaceBlockAt(world, pos)) {
							world.setBlockState(pos, BlockRegistry.PUDDLE.getDefaultState());
						}
					}
				}
			}
		}

		if(world.isRemote) {
			this.spawnWaterRipples(world);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnWaterRipples(World world) {
		Minecraft mc = Minecraft.getMinecraft();

		float particleStrength = world.getRainStrength(1.0F);

		if(!mc.gameSettings.fancyGraphics) {
			particleStrength /= 2.0F;
		}

		if(particleStrength > 0.001F) {
			Entity entity = mc.getRenderViewEntity();

			if(entity != null) {
				BlockPos center = new BlockPos(entity);
				int numParticles = (int)(25.0F * particleStrength * particleStrength);

				if(mc.gameSettings.particleSetting == 1) {
					numParticles >>= 1;
				} else if(mc.gameSettings.particleSetting == 2) {
					numParticles = 0;
				}

				for(int l = 0; l < numParticles; ++l) {
					BlockPos pos = world.getPrecipitationHeight(center.add(world.rand.nextInt(10) - world.rand.nextInt(10), 0, world.rand.nextInt(10) - world.rand.nextInt(10)));

					BlockPos below = pos.down();
					IBlockState stateBelow = world.getBlockState(below);
					Biome biome = world.getBiome(pos);

					if(pos.getY() <= center.getY() + 10 && pos.getY() >= center.getY() - 10 && biome.canRain() && biome.getTemperature(pos) >= 0.15F) {
						AxisAlignedBB blockAABB = stateBelow.getBoundingBox(world, below);

						float rangeX = (float)(blockAABB.maxX - blockAABB.minX);
						float rangeZ = (float)(blockAABB.maxZ - blockAABB.minZ);

						float size = (0.025f + world.rand.nextFloat() * 0.4f);

						if(size * 2 < rangeX && size * 2 < rangeZ) {
							double rx = world.rand.nextDouble() * (rangeX - size * 2) + size + blockAABB.minX;
							double ry = world.rand.nextDouble() * (rangeZ - size * 2) + size + blockAABB.minZ;

							if(stateBelow.getMaterial() != Material.LAVA && stateBelow.getBlock() != Blocks.MAGMA && stateBelow.getMaterial() != Material.AIR) {
								for(int i = 0; i < 4 + world.rand.nextInt(6); i++) {
									world.spawnParticle(EnumParticleTypes.WATER_DROP, (double)below.getX() + rx, (double)((float)below.getY() + 0.1F) + blockAABB.maxY, (double)below.getZ() + ry, 0.0D, 0.0D, 0.0D, new int[0]);
								}

								int waterColor = BiomeColorHelper.getWaterColorAtPos(world, pos);

								float r = (waterColor >> 16 & 255) / 255.0f;
								float g = (waterColor >> 8 & 255) / 255.0f;
								float b = (waterColor & 255) / 255.0f;

								BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
										BLParticles.WATER_RIPPLE.create(world, (double)below.getX() + rx, (double)((float)below.getY() + 0.1F) + blockAABB.maxY, (double)below.getZ() + ry,
												ParticleArgs.get()
												.withScale(size * 10)
												.withColor(r * 1.15f, g * 1.15f, b * 1.15f, 1)
												));
							}
						}
					}
				}
			}
		}
	}
}
