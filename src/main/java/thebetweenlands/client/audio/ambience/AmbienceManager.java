package thebetweenlands.client.audio.ambience;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.client.BetweenlandsClient;

import java.util.*;

public class AmbienceManager {
	public static final AmbienceManager INSTANCE = new AmbienceManager();

	private static final Comparator<AmbienceType> PRIORITY_COMPARATOR = (a1, a2) -> Integer.compare(a2.getPriority(), a1.getPriority());

	private final Map<AmbienceLayer, List<AmbienceType>> ambienceRegistry = new HashMap<>();
	private final List<AmbienceSoundInstance> delayedAmbiences = new ArrayList<>();
	private final List<AmbienceSoundInstance> playingAmbiences = new ArrayList<>();

	private int lastSoundPlayTicks = 0;

	public void registerAmbience(AmbienceType type) {
		List<AmbienceType> types = this.ambienceRegistry.computeIfAbsent(type.getAmbienceLayer(), k -> new ArrayList<>());
		types.add(type);
	}

	private List<AmbienceType> getTypes(AmbienceLayer layer) {
		List<AmbienceType> types = this.ambienceRegistry.get(layer);
		if (types == null)
			types = new ArrayList<>();
		return types;
	}

	private List<AmbienceType> sortByPriority(List<AmbienceType> types) {
		List<AmbienceType> copy = new ArrayList<>(types);
		copy.sort(PRIORITY_COMPARATOR);
		return copy;
	}

	public void update() {
		if (this.lastSoundPlayTicks > 0)
			this.lastSoundPlayTicks--;

		//Keep track of delayed sounds
		Iterator<AmbienceSoundInstance> delayedAmbiencesIT = this.delayedAmbiences.iterator();
		while (delayedAmbiencesIT.hasNext()) {
			AmbienceSoundInstance sound = delayedAmbiencesIT.next();
			boolean soundPlaying = Minecraft.getInstance().getSoundManager().isActive(sound) || sound.isStopped();
			if (soundPlaying)
				delayedAmbiencesIT.remove();
		}

		//Remove completely stopped ambient tracks
		Iterator<AmbienceSoundInstance> playingAmbiencesIT = this.playingAmbiences.iterator();
		while (playingAmbiencesIT.hasNext()) {
			AmbienceSoundInstance sound = playingAmbiencesIT.next();
			boolean soundPlaying = Minecraft.getInstance().getSoundManager().isActive(sound) || this.delayedAmbiences.contains(sound); //Check if sound is playing or scheduled
			if (sound.isStopped() || !soundPlaying) {
				if (soundPlaying)
					Minecraft.getInstance().getSoundManager().stop(sound); //Stop sound because it's not supposed to play anymore
				playingAmbiencesIT.remove();
			}
		}

		//Update ambient tracks
		Player player = BetweenlandsClient.getClientPlayer();
		if (player != null) {
			for (AmbienceLayer layer : this.ambienceRegistry.keySet()) {
				List<AmbienceType> availableAmbiences = this.getTypes(layer);

				//Update player
				for (AmbienceType type : availableAmbiences) {
					type.setPlayer(player);
				}

				int maxTracks = layer.getMaxTracks();
				if (!availableAmbiences.isEmpty()) {
					List<AmbienceType> sorted = this.sortByPriority(availableAmbiences);

					//Check if other ambient tracks on this layer are allowed to play
					int lowestPlayedAmbience = Integer.MAX_VALUE;
					for (int typeIndex = 0, i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
						AmbienceType type = sorted.get(i);
						if (type.isActive() && type.isActiveInWorld(player.level())) {
							typeIndex++;
							if (type.getLowerPriorityVolume() <= 0.0F) {
								lowestPlayedAmbience = typeIndex;
								break;
							}
						}
					}

					//Add ambient tracks that should be playing
					for (int typeIndex = 0, i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
						AmbienceType type = sorted.get(i);
						if (type.isActive() && type.isActiveInWorld(player.level())) {
							typeIndex++;
							boolean isPlaying = false;
							for (AmbienceSoundInstance sound : this.playingAmbiences) {
								if (type == sound.type) {
									isPlaying = true;
									break;
								}
							}
							if (!isPlaying && typeIndex <= lowestPlayedAmbience) {
								if (this.lastSoundPlayTicks <= 0) {
									this.playSound(new AmbienceSoundInstance(type.getSound(), type.getCategory(), type, player, this), type.getDelay());
									this.lastSoundPlayTicks = 1;
								} else {
									break;
								}
							}
						}
					}

					//Stop or set any ambient tracks to lower priority if they don't have priority or shouldn't play
					for (AmbienceSoundInstance sound : this.playingAmbiences) {
						if (sound.type.getAmbienceLayer() != layer) {
							continue;
						}

						//Whether the sound has a lower priority and has to use a lower volume
						boolean lowerPriority = false;

						int typeIndex = 0;
						for (int i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
							AmbienceType otherType = sorted.get(i);
							if (otherType.isActive() && otherType.isActiveInWorld(player.level())) {
								typeIndex++;

								if (sound.type == otherType)
									break;

								if (otherType.getLowerPriorityVolume() > 0.0F && otherType.getLowerPriorityVolume() < 1.0F)
									lowerPriority = true;
							}
						}

						if (typeIndex <= lowestPlayedAmbience && sound.isFadingOut()) {
							//Stop fading out, the sound can play again
							sound.cancelFade();
						}

						if (!sound.isStopped()) {
							if (typeIndex > lowestPlayedAmbience) {
								//The sound is not allowed to play, stop
								sound.stopSound();
							} else {
								//The sound has lower priority, use decreased volume
								sound.setLowPriority(lowerPriority);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the highest volume to be used by any lower priority ambiences
	 *
	 * @return
	 */
	float getLowerPriorityVolume() {
		float lowest = Float.MAX_VALUE;
		for (AmbienceSoundInstance sound : this.playingAmbiences) {
			if (sound.type.getLowerPriorityVolume() > 0.0F && sound.type.getLowerPriorityVolume() < lowest)
				lowest = sound.type.getLowerPriorityVolume();
		}
		return lowest;
	}

	private void playSound(AmbienceSoundInstance sound, int delay) {
		this.playingAmbiences.add(sound);
		if (sound.type.getSound() != null) {
			if (delay == 0) {
				Minecraft.getInstance().getSoundManager().play(sound);
			} else {
				this.delayedAmbiences.add(sound);
				Minecraft.getInstance().getSoundManager().playDelayed(sound, delay);
			}
		}
	}

	/**
	 * Returns whether music should be stopped
	 *
	 * @return
	 */
	public boolean shouldStopMusic() {
		Player player = BetweenlandsClient.getClientPlayer();
		if (player != null) {
			for (AmbienceSoundInstance sound : this.playingAmbiences) {
				if (sound.type.stopsMusic())
					return true;
			}
		}
		return false;
	}

	/**
	 * Stops all tracks
	 */
	public void stopAll() {
		for (AmbienceSoundInstance sound : this.playingAmbiences) {
			Minecraft.getInstance().getSoundManager().stop(sound);
			sound.stopSound();
		}
		for (AmbienceSoundInstance sound : this.delayedAmbiences) {
			Minecraft.getInstance().getSoundManager().stop(sound);
			sound.stopSound();
		}
		this.playingAmbiences.clear();
		this.delayedAmbiences.clear();
	}
}
