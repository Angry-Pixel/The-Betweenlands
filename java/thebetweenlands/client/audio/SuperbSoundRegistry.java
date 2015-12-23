package thebetweenlands.client.audio;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.base.Throwables;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.util.ResourceLocation;

public class SuperbSoundRegistry extends SoundRegistry {
	private static final Field SOUNDS_FIELD = ReflectionHelper.findField(SoundEventAccessorComposite.class, "soundPool", "field_148736_a", "a");

	private static final Field VOLUME_FIELD = ReflectionHelper.findField(SoundEventAccessorComposite.class, "eventVolume", "field_148731_f", "f");

	private static final Field PITCH_FIELD = ReflectionHelper.findField(SoundEventAccessorComposite.class, "eventPitch", "field_148733_e", "e");

	@Override
	public void registerSound(SoundEventAccessorComposite sound) {
		super.registerSound(new SuperbSoundEventAccessorComposite(sound));
	}

	private static class SuperbSoundEventAccessorComposite extends SoundEventAccessorComposite {
		private List<ISoundEventAccessor> sounds = new ArrayList<ISoundEventAccessor>();

		private ISoundEventAccessor lastEntry;
	
		private Random rand = new Random();

		private ResourceLocation location;

		private SoundCategory category;

		private double volume;

		private double pitch;

		public SuperbSoundEventAccessorComposite(SoundEventAccessorComposite sound) {
			super(null, 0, 0, null);
			try {
				sounds = (List<ISoundEventAccessor>) SOUNDS_FIELD.get(sound);
				volume = VOLUME_FIELD.getDouble(sound);
				pitch = PITCH_FIELD.getDouble(sound);
			} catch (Exception e) {
				Throwables.propagate(e);
			}
			location = sound.getSoundEventLocation();
			category = sound.getSoundCategory();
		}

		@Override
		public int func_148721_a() {
			return calculateWeight(getSoundsExcludingLastEntry());
		}

		private int calculateWeight(List<ISoundEventAccessor> sounds) {
			int weight = 0;
			Iterator<ISoundEventAccessor> soundIter = sounds.iterator();
			while (soundIter.hasNext()) {
				weight += soundIter.next().func_148721_a();
			}
			return weight;
		}

		private List<ISoundEventAccessor> getSoundsExcludingLastEntry() {
			if (lastEntry == null) {
				return sounds;
			}
			List<ISoundEventAccessor> newSounds = new ArrayList<ISoundEventAccessor>(sounds);
			newSounds.remove(lastEntry);	
			return newSounds;
		}

		@Override
		public SoundPoolEntry func_148720_g() {
			List<ISoundEventAccessor> soundChoices = getSoundsExcludingLastEntry();
			int weight = calculateWeight(soundChoices);
			if (!soundChoices.isEmpty() && weight != 0) {
				int choice = rand.nextInt(weight);
				Iterator<ISoundEventAccessor> entryIter = soundChoices.iterator();
				ISoundEventAccessor entry;
				do {
					if (!entryIter.hasNext()) {
						return SoundHandler.missing_sound;
					}
					entry = entryIter.next();
					choice -= entry.func_148721_a();
				} while (choice >= 0);
				if (sounds.size() > 1) {
					lastEntry = entry;
				}
				SoundPoolEntry poolEntry = (SoundPoolEntry) entry.func_148720_g();
				poolEntry.setPitch(poolEntry.getPitch() * pitch);
				poolEntry.setVolume(poolEntry.getVolume() * volume);
				return poolEntry;
			} else {
				return SoundHandler.missing_sound;
			}
		}

		@Override
		public void addSoundToEventPool(ISoundEventAccessor sound) {
			sounds.add(sound);
		}

		@Override
		public ResourceLocation getSoundEventLocation() {
			return location;
		}

		@Override
		public SoundCategory getSoundCategory() {
			return category;
		}
	}
}
