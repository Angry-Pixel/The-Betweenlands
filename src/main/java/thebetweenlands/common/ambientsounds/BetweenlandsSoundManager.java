package thebetweenlands.common.ambientsounds;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.TheBetweenlands;

// The betweenlands sound manager (realy only adds a couple of sound features such as a playsound func that loops)
// betweenlands uses this insted of biome ambence loop tracks

// As like most of my code it is set too public to alow for extends from third partys
@OnlyIn(Dist.CLIENT)
public class BetweenlandsSoundManager {
	
	public BetweenlandsSoundManager() {
	}
	
	// The loop sound cache (holds LoopSoundInstance var to alow for stoping of the loop with ease)
	public BetweenlandsLoopedSound loopCache = null;
	
	// Currently only supports one looped sound
	public void playLoopedSound(SoundEvent sound) {
		// if this curent instance hasent called stopLoopedSound() this func doesn't continue
		if (loopCache == null)
		{
			TheBetweenlands.LOGGER.info("playing loop");
			
			loopCache = new BetweenlandsLoopedSound(sound, SoundSource.BLOCKS, 1, 1);
			
			Minecraft.getInstance().getSoundManager().play(loopCache);
		}
	}
	
	public void stopLoopedSound() {
		if (loopCache != null) {
			Minecraft.getInstance().getSoundManager().stop(loopCache);
			loopCache = null;
		}
	}
}
