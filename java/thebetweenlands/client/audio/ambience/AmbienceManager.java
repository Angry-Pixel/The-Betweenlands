package thebetweenlands.client.audio.ambience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.audio.ambience.AmbienceType.EnumAmbienceLayer;

@SideOnly(Side.CLIENT)
public class AmbienceManager {
	public static final AmbienceManager INSTANCE = new AmbienceManager();

	private static final Comparator<AmbienceType> PRIORITY_COMPARATOR = new Comparator<AmbienceType>() {
		@Override
		public int compare(AmbienceType a1, AmbienceType a2) {
			return Integer.compare(a2.getPriority(), a1.getPriority());
		}
	};

	private final Map<EnumAmbienceLayer, List<AmbienceType>> ambienceRegistry = new HashMap<EnumAmbienceLayer, List<AmbienceType>>();
	private final List<AmbienceSound> delayedAmbiences = new ArrayList<AmbienceSound>();
	private final List<AmbienceSound> playingAmbiences = new ArrayList<AmbienceSound>();

	public void registerAmbience(AmbienceType type) {
		List<AmbienceType> types = this.ambienceRegistry.get(type.getAmbienceLayer());
		if(types == null)
			this.ambienceRegistry.put(type.getAmbienceLayer(), types = new ArrayList<AmbienceType>());
		types.add(type);
	}

	private List<AmbienceType> getTypes(EnumAmbienceLayer layer) {
		List<AmbienceType> types = this.ambienceRegistry.get(layer);
		if(types == null)
			types = new ArrayList<AmbienceType>();
		return types;
	}

	private List<AmbienceType> getTypes() {
		List<AmbienceType> types = new ArrayList<AmbienceType>();
		for(Entry<EnumAmbienceLayer, List<AmbienceType>> entry : this.ambienceRegistry.entrySet()) {
			types.addAll(entry.getValue());
		}
		return types;
	}

	private List<AmbienceType> sortByPriority(List<AmbienceType> types) {
		List<AmbienceType> copy = new ArrayList<AmbienceType>();
		copy.addAll(types);
		Collections.sort(copy, PRIORITY_COMPARATOR);
		return copy;
	}

	public void update() {
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
			for(EnumAmbienceLayer layer : EnumAmbienceLayer.TYPES) {
				List<AmbienceType> availableAmbiences = this.getTypes(layer);

				//Update player
				for(AmbienceType type : availableAmbiences) {
					type.setPlayer(player);
				}

				int tracks = layer.tracks;
				if(!availableAmbiences.isEmpty()) {
					List<AmbienceType> sorted = this.sortByPriority(availableAmbiences);

					//Stop any ambient tracks that don't have priority or shouldn't play
					for(AmbienceSound sound : this.playingAmbiences) {
						if(!sound.isStopping()) {
							boolean hasPriority = false;
							int index = 0;
							for(int i = 0; i < sorted.size() && index < tracks; i++) {
								AmbienceType type = sorted.get(i);
								if(type.isActive()) {
									index++;
									if(sound.type.equals(type)) {
										hasPriority = true;
										break;
									}
								}
							}
							if(!hasPriority)
								sound.stop();
						}
					}

					//Add ambient tracks that should be playing
					int index = 0;
					for(int i = 0; i < sorted.size() && index < tracks; i++) {
						AmbienceType type = sorted.get(i);
						if(type.isActive()) {
							index++;
							boolean isPlaying = false;
							for(AmbienceSound sound : this.playingAmbiences) {
								if(type.equals(sound.type)) {
									isPlaying = true;
									if(sound.isFadingOut())
										sound.cancelFade();
									break;
								}
							}
							if(!isPlaying) {
								this.playSound(new AmbienceSound(type, player, type.getSound()), type.getDelay());
							}
						}
					}
				}
			}
		}
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

	public void stopAll() {
		for(AmbienceSound sound : this.playingAmbiences) {
			sound.stopImmediately();
		}
		this.playingAmbiences.clear();
	}
}
