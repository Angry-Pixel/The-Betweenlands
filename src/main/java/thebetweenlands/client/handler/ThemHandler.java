package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

@SideOnly(Side.CLIENT)
public class ThemHandler {
	private static final List<Particle> activeParticles = new ArrayList<Particle>();

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
			World world = TheBetweenlands.proxy.getClientWorld();
			Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
			if(world != null && viewer != null && viewer.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && FogHandler.hasDenseFog(world) && (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 < 65.0F) {
				Iterator<Particle> it = activeParticles.iterator();
				while(it.hasNext()) {
					Particle particle = it.next();
					if(!particle.isAlive()) {
						it.remove();
					}
				}
				if(activeParticles.size() < 4) {
					BlockPos worldHeight = world.getHeight(viewer.getPosition());
					if(viewer.posY >= worldHeight.getY() - 3 && SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(world.getBlockState(worldHeight.down()))) {
						int probability = (int) (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 * 10 + 60;
						if(world.rand.nextInt(probability) == 0) {
							double xOff = world.rand.nextInt(50) - 25;
							double zOff = world.rand.nextInt(50) - 25;
							double sx = viewer.posX + xOff;
							double sz = viewer.posZ + zOff;
							double sy = worldHeight.getY() + world.rand.nextFloat() * 0.75f;
							Particle particle = BLParticles.THEM.spawn(world, sx, sy, sz);
							activeParticles.add(particle);
						}
					}
				}
			}
		}
	}
}
