package thebetweenlands.items;

import net.minecraft.util.IIcon;

public interface ICorrodible {
	public IIcon[] getIcons();

	public void setCorrosionIcons(IIcon[][] corrosionIcons);
}
