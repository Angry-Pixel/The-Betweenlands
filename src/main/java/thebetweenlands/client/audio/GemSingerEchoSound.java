package thebetweenlands.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.registries.SoundRegistry;

public class GemSingerEchoSound extends SafeStreamSound {
	private Vec3d source;

	public GemSingerEchoSound(Vec3d source) {
		super(SoundRegistry.GEM_SINGER_ECHO, SoundCategory.PLAYERS);
		this.attenuationType = ISound.AttenuationType.NONE;
		this.xPosF = (float)source.x;
		this.yPosF = (float)source.y;
		this.zPosF = (float)source.z;
	}

	public GemSingerEchoSound setVolumeAndPitch(float volume, float pitch) {
		this.volume = volume;
		this.pitch = pitch;
		return this;
	}
}
