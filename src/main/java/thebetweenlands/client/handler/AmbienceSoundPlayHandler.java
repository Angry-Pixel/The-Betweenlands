package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.audio.RainBackgroundSound;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

public class AmbienceSoundPlayHandler {
	private static class RainPosition {
		private static final int MAX_TIMER = 40;

		private final Vec3d position;
		private final boolean isAbove;
		private int timer;

		private RainPosition(Vec3d position, boolean isAbove) {
			this.position = position;
			this.isAbove = isAbove;
			this.timer = MAX_TIMER;
		}
	}

	private static List<RainPosition> rainPositions = new ArrayList<>();

	private static float relRainX = 0.0f;
	private static float relRainY = 0.0f;
	private static float relRainZ = 0.0f;
	private static float rainAbove = 0.0f;
	private static float rainVolume = 0.0f;

	public static float getRelativeRainX() {
		return relRainX;
	}

	public static float getRelativeRainY() {
		return relRainY;
	}

	public static float getRelativeRainZ() {
		return relRainZ;
	}

	public static float getRainAbove() {
		return rainAbove;
	}

	public static float getRainVolume() {
		return rainVolume;
	}

	private static int rainSoundTimer = 30;

	@SubscribeEvent
	public static void onPlayerCltTick(PlayerTickEvent event) {
		if(event.phase == Phase.START && event.side == Side.CLIENT && event.player == TheBetweenlands.proxy.getClientPlayer()) {
			AmbienceManager.INSTANCE.update();

			float rx = 0.0f;
			float ry = 0.0f;
			float rz = 0.0f;
			float isAbove = 0.0f;
			float rd = Float.MAX_VALUE;

			if(!rainPositions.isEmpty()) {
				float totalWeight = 0.0f;

				boolean playSound = false;
				if(rainSoundTimer-- < 0) {
					rainSoundTimer = 30 + event.player.world.rand.nextInt(20);
					playSound = true;
				}
				
				Iterator<RainPosition> it = rainPositions.iterator();
				while(it.hasNext()) {
					RainPosition position = it.next();

					float weight = (position.timer + 1) / (float)(RainPosition.MAX_TIMER + 1);
					totalWeight += weight;

					rx += (float)position.position.x * weight;
					ry += (float)position.position.y * weight;
					rz += (float)position.position.z * weight;
					isAbove += (position.isAbove ? 1 : 0) * weight;
					rd = Math.min(rd, (float)Math.sqrt(position.position.length()));

					if(playSound && position.timer > RainPosition.MAX_TIMER - 12) {
						ISound sound = new PositionedSoundRecord(getSoundForDistance((float)position.position.length() - 3f, position.isAbove ? 1 : 0), SoundCategory.WEATHER, position.isAbove ? 0.05f : 0.1f, position.isAbove ? 0.5f : 0.9f, (float)position.position.x + (float)event.player.posX, (float)position.position.y + (float)event.player.posY, (float)position.position.z + (float)event.player.posZ);
						Minecraft.getMinecraft().getSoundHandler().playSound(sound);
					}
					
					if(--position.timer < 0) {
						it.remove();
					}
				}

				rx /= totalWeight;
				ry /= totalWeight;
				rz /= totalWeight;
				isAbove /= totalWeight;

				float blend = 0.25f;

				relRainX = (1 - blend) * relRainX + blend * rx;
				relRainY = (1 - blend) * relRainY + blend * ry;
				relRainZ = (1 - blend) * relRainZ + blend * rz;

				rainAbove = (1 - blend) * rainAbove + blend * isAbove;
				rainVolume = (1 - blend) * rainVolume + blend * MathHelper.clamp(1.0f / (rd - 0.5f), 0, 1);

				if(playSound) {
					ISound rainSound = new RainBackgroundSound(getSoundForDistance(rd, rainAbove * 2.0f), SoundCategory.WEATHER);
					Minecraft.getMinecraft().getSoundHandler().playSound(rainSound);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlaySoundSource(SoundSourceEvent event) {
		if(event.getSound().getCategory() == SoundCategory.MUSIC && AmbienceManager.INSTANCE.shouldStopMusic()) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(event.getSound());
		}
	}

	@SubscribeEvent
	public static void onPlaySound(PlaySoundEvent event) {
		ISound sound = event.getResultSound();

		if(sound != null) {
			boolean isWeatherSound = SoundEvents.WEATHER_RAIN.getSoundName().equals(sound.getSoundLocation());
			boolean isWeatherSoundAbove = SoundEvents.WEATHER_RAIN_ABOVE.getSoundName().equals(sound.getSoundLocation());

			if(isWeatherSound || isWeatherSoundAbove) {
				Entity view = Minecraft.getMinecraft().getRenderViewEntity();
				if(view != null) {
					if(rainPositions.size() < 100) {
						rainPositions.add(new RainPosition(new Vec3d(sound.getXPosF() - (float)view.posX, sound.getYPosF() - (float)view.posY, sound.getZPosF() - (float)view.posZ), isWeatherSoundAbove));
					}
				}
				event.setResultSound(null);
			}
		}
	}

	private static SoundEvent getSoundForDistance(float distance, float rainAbove) {
		float value = distance + rainAbove * 2.0f;
		if(value >= 3.0f) {
			return SoundRegistry.RAIN_DRIPPING;
		} else if(value >= 2.0f) {
			return SoundRegistry.RAIN_WEAK;
		} else if(value >= 1.0f) {
			return SoundRegistry.RAIN_MEDIUM;
		} else {
			return SoundRegistry.RAIN_STRONG;
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		if(event.getWorld().isRemote) {
			AmbienceManager.INSTANCE.stopAll();
		}
	}
}
