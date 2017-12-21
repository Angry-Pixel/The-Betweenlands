package thebetweenlands.client.audio;

import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySound<T extends TileEntity> extends SafeStreamSound {
	public final T tile;
	public final BlockPos pos;
	public final Predicate<T> isPlaying;

	private boolean fadeOut = false;

	public TileEntitySound(SoundEvent sound, SoundCategory category, T tile, Predicate<T> isPlaying) {
		super(sound, category);
		this.repeat = true;
		this.attenuationType = AttenuationType.LINEAR;
		this.tile = tile;
		this.isPlaying = isPlaying;
		this.pos = tile.getPos();
		this.xPosF = this.pos.getX() + 0.5F;
		this.yPosF = this.pos.getY() + 0.5F;
		this.zPosF = this.pos.getZ() + 0.5F;
	}

	@Override
	public void update() {
		super.update();
		
		if(this.fadeOut || this.tile == null || !this.tile.hasWorld() || !this.tile.getWorld().isBlockLoaded(this.tile.getPos())
				|| this.tile.getWorld().getTileEntity(this.tile.getPos()) != this.tile || !this.isPlaying.test(this.tile)) {
			this.repeat = false;
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.donePlaying = true;
				this.volume = 0;
			}
		}
	}

	/**
	 * Stops the sound immediately without fading out
	 */
	public void stopImmediately() {
		this.donePlaying = true;
		this.repeat = false;
	}

	/**
	 * Stops the sound and makes it fade out
	 */
	public void stop() {
		this.fadeOut = true;
	}

	/**
	 * Cancels the fade out
	 */
	public void cancelFade() {
		this.fadeOut = false;
	}

	/**
	 * Returns whether this sound is currently stopped or fading out
	 * @return
	 */
	public boolean isStopping() {
		return this.donePlaying || this.fadeOut;
	}
}
