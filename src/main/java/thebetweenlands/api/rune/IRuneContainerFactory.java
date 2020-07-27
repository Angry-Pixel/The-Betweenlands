package thebetweenlands.api.rune;

import net.minecraft.util.ResourceLocation;

public interface IRuneContainerFactory {
	public IRuneContainer createContainer();

	public ResourceLocation getId();
	
	public IRuneGui createGui(RuneMenuType menu);
}
