package thebetweenlands.client.audio.ambience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;

@SideOnly(Side.CLIENT)
public class AmbienceManager {
	public static final AmbienceManager INSTANCE = new AmbienceManager();

	private static final Comparator<AmbienceType> PRIORITY_COMPARATOR = new Comparator<AmbienceType>() {
		@Override
		public int compare(AmbienceType a1, AmbienceType a2) {
			return Integer.compare(a2.getPriority(), a1.getPriority());
		}
	};

	private final Map<AmbienceLayer, List<AmbienceType>> ambienceRegistry = new HashMap<AmbienceLayer, List<AmbienceType>>();
	private final List<AmbienceSound> delayedAmbiences = new ArrayList<AmbienceSound>();
	private final List<AmbienceSound> playingAmbiences = new ArrayList<AmbienceSound>();

	private int lastSoundPlayTicks = 0;

	public void registerAmbience(AmbienceType type) {
		List<AmbienceType> types = this.ambienceRegistry.get(type.getAmbienceLayer());
		if(types == null)
			this.ambienceRegistry.put(type.getAmbienceLayer(), types = new ArrayList<AmbienceType>());
		types.add(type);
	}

	private List<AmbienceType> getTypes(AmbienceLayer layer) {
		List<AmbienceType> types = this.ambienceRegistry.get(layer);
		if(types == null)
			types = new ArrayList<AmbienceType>();
		return types;
	}

	private List<AmbienceType> sortByPriority(List<AmbienceType> types) {
		List<AmbienceType> copy = new ArrayList<AmbienceType>();
		copy.addAll(types);
		Collections.sort(copy, PRIORITY_COMPARATOR);
		return copy;
	}

	public void update() {
		if(this.lastSoundPlayTicks > 0)
			this.lastSoundPlayTicks--;

		//Keep track of delayed sounds
		Iterator<AmbienceSound> delayedAmbiencesIT = this.delayedAmbiences.iterator();
		while(delayedAmbiencesIT.hasNext()) {
			AmbienceSound sound = delayedAmbiencesIT.next();
			boolean soundPlaying = Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound) || sound.isDonePlaying();
			if(soundPlaying)
				delayedAmbiencesIT.remove();
		}

		//Remove completely stopped ambient tracks
		Iterator<AmbienceSound> playingAmbiencesIT = this.playingAmbiences.iterator();
		while(playingAmbiencesIT.hasNext()) {
			AmbienceSound sound = playingAmbiencesIT.next();
			boolean soundPlaying = Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound) || this.delayedAmbiences.contains(sound); //Check if sound is playing or scheduled
			if(sound.isDonePlaying() || !soundPlaying) {
				if(soundPlaying)
					Minecraft.getMinecraft().getSoundHandler().stopSound(sound); //Stop sound because it's not supposed to play anymore
				playingAmbiencesIT.remove();
			}
		}

		//Update ambient tracks
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			for(AmbienceLayer layer : this.ambienceRegistry.keySet()) {
				List<AmbienceType> availableAmbiences = this.getTypes(layer);

				//Update player
				for(AmbienceType type : availableAmbiences) {
					type.setPlayer(player);
				}

				int maxTracks = layer.getMaxTracks();
				if(!availableAmbiences.isEmpty()) {
					List<AmbienceType> sorted = this.sortByPriority(availableAmbiences);

					//Check if other ambient tracks on this layer are allowed to play
					int lowestPlayedAmbience = Integer.MAX_VALUE;
					for(int typeIndex = 0, i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
						AmbienceType type = sorted.get(i);
						if(type.isActive() && type.isActiveInWorld(player.world)) {
							typeIndex++;
							if(type.getLowerPriorityVolume() <= 0.0F) {
								lowestPlayedAmbience = typeIndex;
								break;
							}
						}
					}

					//Add ambient tracks that should be playing
					for(int typeIndex = 0, i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
						AmbienceType type = sorted.get(i);
						if(type.isActive() && type.isActiveInWorld(player.world)) {
							typeIndex++;
							boolean isPlaying = false;
							for(AmbienceSound sound : this.playingAmbiences) {
								if(type == sound.type) {
									isPlaying = true;
									break;
								}
							}
							if(!isPlaying && typeIndex <= lowestPlayedAmbience) {
								if(this.lastSoundPlayTicks <= 0) {
									this.playSound(new AmbienceSound(type.getSound(), type.getCategory(), type, player, this), type.getDelay());
									this.lastSoundPlayTicks = 1;
								} else {
									break;
								}
							}
						}
					}

					//Stop or set any ambient tracks to lower priority if they don't have priority or shouldn't play
					for(AmbienceSound sound : this.playingAmbiences) {
						if(sound.type.getAmbienceLayer() != layer) {
							continue;
						}
						
						//Whether the sound has a lower priority and has to use a lower volume
						boolean lowerPriority = false;

						int typeIndex = 0;
						for(int i = 0; i < sorted.size() && typeIndex < maxTracks; i++) {
							AmbienceType otherType = sorted.get(i);
							if(otherType.isActive() && otherType.isActiveInWorld(player.world)) {
								typeIndex++;

								if(sound.type == otherType)
									break;

								if(otherType.getLowerPriorityVolume() > 0.0F && otherType.getLowerPriorityVolume() < 1.0F)
									lowerPriority = true;
							}
						}

						if(typeIndex <= lowestPlayedAmbience && sound.isFadingOut()) {
							//Stop fading out, the sound can play again
							sound.cancelFade();
						}

						if(!sound.isStopping()) {
							if(lowestPlayedAmbience != Integer.MAX_VALUE && typeIndex > lowestPlayedAmbience) {
								//The sound is not allowed to play, stop
								sound.stop();
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
	 * @return
	 */
	float getLowerPriorityVolume() {
		float lowest = Float.MAX_VALUE;
		for(AmbienceSound sound : this.playingAmbiences) {
			if(sound.type.getLowerPriorityVolume() > 0.0F && sound.type.getLowerPriorityVolume() < lowest)
				lowest = sound.type.getLowerPriorityVolume();
		}
		return lowest;
	}

	private void playSound(AmbienceSound sound, int delay) {
		this.playingAmbiences.add(sound);
		if(sound.type.getSound() != null) {
			if(delay == 0) {
				Minecraft.getMinecraft().getSoundHandler().playSound(sound);
			} else {
				this.delayedAmbiences.add(sound);
				Minecraft.getMinecraft().getSoundHandler().playDelayedSound(sound, delay);
			}
		}
	}

	/**
	 * Returns whether music should be stopped
	 * @return
	 */
	public boolean shouldStopMusic() {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			for(AmbienceSound sound : this.playingAmbiences) {
				if(sound.type.stopsMusic())
					return true;
			}
		}
		return false;
	}

	/**
	 * Stops all tracks
	 */
	public void stopAll() {
		for(AmbienceSound sound : this.playingAmbiences) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
			sound.stopImmediately();
		}
		for(AmbienceSound sound : this.delayedAmbiences) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
			sound.stopImmediately();
		}
		this.playingAmbiences.clear();
		this.delayedAmbiences.clear();
	}
}
