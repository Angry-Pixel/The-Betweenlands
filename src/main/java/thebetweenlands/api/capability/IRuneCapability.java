package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.IRuneContainerFactory;

public interface IRuneCapability {
	@Nullable
	public IRuneContainerFactory getRuneContainerFactory();
}
