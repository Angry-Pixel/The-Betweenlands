package thebetweenlands.client.audio.ambience.list;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.world.WorldProviderBetweenlands;

public class CaveAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		return this.getPlayer().posY <= WorldProviderBetweenlands.CAVE_START;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.LAYER1;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public ResourceLocation getSound() {
		return new ResourceLocation("thebetweenlands:ambientCave");
	}
}
