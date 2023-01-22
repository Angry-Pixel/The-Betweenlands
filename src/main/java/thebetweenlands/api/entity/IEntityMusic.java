package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.client.audio.EntityMusicSound;
import thebetweenlands.common.sound.BLSoundEvent;

public interface IEntityMusic {
	@Nullable
	@Deprecated
	public BLSoundEvent getMusicFile(EntityPlayer listener);

	public double getMusicRange(EntityPlayer listener);

	public boolean isMusicActive(EntityPlayer listener);

	@SideOnly(Side.CLIENT)
	@Nullable
	public default IEntitySound getMusicSound(EntityPlayer listener) {
		BLSoundEvent sound = this.getMusicFile(listener);
		return new EntityMusicSound<Entity>(sound, SoundCategory.MUSIC, (Entity) this, this, 1, AttenuationType.NONE);
	}

	public default int getMusicLayer(EntityPlayer listener) {
		return 0;
	}
	
	public default boolean canInterruptOtherEntityMusic(EntityPlayer listener) {
		return true;
	}
}
