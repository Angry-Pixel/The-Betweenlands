package thebetweenlands.items;

import net.minecraft.util.IIcon;

public interface IDecayable {
	public IIcon[] getIcons();

	public void setDecayIcons(IIcon[][] decayIcons);
}
