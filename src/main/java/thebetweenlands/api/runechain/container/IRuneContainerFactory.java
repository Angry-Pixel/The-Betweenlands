package thebetweenlands.api.runechain.container;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.container.gui.IRuneGui;
import thebetweenlands.api.runechain.container.gui.RuneMenuType;

public interface IRuneContainerFactory {
	public IRuneContainer createContainer();

	public ResourceLocation getId();
	
	public IRuneGui createGui(RuneMenuType menu);
}
