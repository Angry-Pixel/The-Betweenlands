package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.client.audio.RainSoundInstance;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AmbienceHandler {

	private static class RainPosition {
		private static final int MAX_TIMER = 40;

		private final Vec3 position;
		private final boolean isAbove;
		private int timer;

		private RainPosition(Vec3 position, boolean isAbove) {
			this.position = position;
			this.isAbove = isAbove;
			this.timer = MAX_TIMER;
		}
	}

	private static final List<RainPosition> rainPositions = new ArrayList<>();

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

	public static void init() {
		NeoForge.EVENT_BUS.addListener(AmbienceHandler::tickAmbience);
		NeoForge.EVENT_BUS.addListener(AmbienceHandler::preventMusicDuringAmbience);
		NeoForge.EVENT_BUS.addListener(AmbienceHandler::replaceVanillaRainSounds);
		NeoForge.EVENT_BUS.addListener(AmbienceHandler::unloadAmbientSounds);
	}

	private static void tickAmbience(PlayerTickEvent.Pre event) {
		if (event.getEntity().level().isClientSide() && !Minecraft.getInstance().isPaused()) {
			Player player = event.getEntity();
			AmbienceManager.INSTANCE.update();

			float rx = 0.0f;
			float ry = 0.0f;
			float rz = 0.0f;
			float isAbove = 0.0f;
			float rd = Float.MAX_VALUE;

			if (!rainPositions.isEmpty()) {
				float totalWeight = 0.0f;

				boolean playSound = false;
				if (rainSoundTimer-- < 0) {
					rainSoundTimer = 30 + player.level().getRandom().nextInt(20);
					playSound = true;
				}

				Iterator<RainPosition> it = rainPositions.iterator();
				while (it.hasNext()) {
					RainPosition position = it.next();

					float weight = (position.timer + 1) / (float) (RainPosition.MAX_TIMER + 1);
					totalWeight += weight;

					rx += (float) position.position.x * weight;
					ry += (float) position.position.y * weight;
					rz += (float) position.position.z * weight;
					isAbove += (position.isAbove ? 1 : 0) * weight;
					rd = Math.min(rd, (float) Math.sqrt(position.position.length()));

					if (playSound && position.timer > RainPosition.MAX_TIMER - 12) {
						SoundInstance sound = new SimpleSoundInstance(
							getSoundForDistance((float) position.position.length() - 3f, position.isAbove ? 1 : 0),
							SoundSource.WEATHER,
							position.isAbove ? 0.05f : 0.1f,
							position.isAbove ? 0.5f : 0.9f,
							SoundInstance.createUnseededRandom(),
							(float) position.position.x + (float) player.getX(),
							(float) position.position.y + (float) player.getY(),
							(float) position.position.z + (float) player.getZ());
						Minecraft.getInstance().getSoundManager().play(sound);
					}

					if (--position.timer < 0) {
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
				rainVolume = (1 - blend) * rainVolume + blend * Mth.clamp(1.0f / (rd - 0.5f), 0, 1);

				if (playSound) {
					SoundInstance rainSound = new RainSoundInstance(getSoundForDistance(rd, rainAbove * 2.0f), SoundSource.WEATHER);
					Minecraft.getInstance().getSoundManager().play(rainSound);
				}
			}
		}
	}

	private static void preventMusicDuringAmbience(PlaySoundSourceEvent event) {
		if (event.getSound().getSource() == SoundSource.MUSIC && AmbienceManager.INSTANCE.shouldStopMusic()) {
			Minecraft.getInstance().getSoundManager().stop(event.getSound());
		}
	}

	private static void replaceVanillaRainSounds(PlaySoundEvent event) {
		SoundInstance sound = event.getSound();

		if (sound != null) {
			boolean isWeatherSound = SoundEvents.WEATHER_RAIN.getLocation().equals(sound.getLocation());
			boolean isWeatherSoundAbove = SoundEvents.WEATHER_RAIN_ABOVE.getLocation().equals(sound.getLocation());

			if (isWeatherSound || isWeatherSoundAbove) {
				Entity view = Minecraft.getInstance().getCameraEntity();
				if (view != null) {
					if (rainPositions.size() < 100) {
						rainPositions.add(new RainPosition(new Vec3(sound.getX() - (float) view.getX(), sound.getY() - (float) view.getY(), sound.getZ() - (float) view.getZ()), isWeatherSoundAbove));
					}
				}
				event.setSound(null);
			}
		}
	}

	private static SoundEvent getSoundForDistance(float distance, float rainAbove) {
		float value = distance + rainAbove * 2.0f;
		if (value >= 3.0f) {
			return SoundRegistry.RAIN_DRIPPING.get();
		} else if (value >= 2.0f) {
			return SoundRegistry.RAIN_WEAK.get();
		} else if (value >= 1.0f) {
			return SoundRegistry.RAIN_MEDIUM.get();
		} else {
			return SoundRegistry.RAIN_STRONG.get();
		}
	}

	private static void unloadAmbientSounds(LevelEvent.Unload event) {
		if (event.getLevel().isClientSide()) {
			AmbienceManager.INSTANCE.stopAll();
		}
	}
}
