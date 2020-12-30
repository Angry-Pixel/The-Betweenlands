package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import thebetweenlands.api.runechain.container.IRuneContainerFactory;

public interface IRuneCapability {
	@Nullable
	public IRuneContainerFactory getRuneContainerFactory();
}
