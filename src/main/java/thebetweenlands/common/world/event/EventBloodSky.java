package thebetweenlands.common.world.event;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class EventBloodSky extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "blood_sky");
	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();
	private boolean soundPlayed = true;
	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;

	public EventBloodSky(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	public void setSkyTransparency(float transparency) {
		this.lastSkyTransparency = this.skyTransparency;
		this.skyTransparency = transparency;
	}

	public float getSkyTransparency(float partialTicks) {
		return this.skyTransparency + (this.skyTransparency - this.lastSkyTransparency) * partialTicks;
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(400000) + 500000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(8000) + 20000;
	}

	@Override
	public void update(World world) {
		super.update(world);
		
		if(world.isRemote) {
			if(this.isActive()) {
				if(this.skyTransparency < 1.0F) {
					this.setSkyTransparency(this.skyTransparency + 0.003F);
				}
				if(this.skyTransparency > 1.0F) {
					this.setSkyTransparency(1.0F);
				}
			} else {
				if(this.skyTransparency > 0.0F) {
					this.setSkyTransparency(this.skyTransparency - 0.003F);
				}
				if(this.skyTransparency < 0.0F) {
					this.setSkyTransparency(0.0F);
				}
			}
		}
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if(active) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && world.isRemote && !this.soundPlayed) {
				world.playSound(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ, SoundRegistry.AMBIENT_BLOOD_SKY_ROAR, SoundCategory.AMBIENT, 100.0F, 1.0F, false);
			}
			this.soundPlayed = true;
		} else {
			this.soundPlayed = false;
		}
	}
}
