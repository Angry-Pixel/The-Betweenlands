package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.impl.RuneChainComposition;

public interface IRuneChainCapability {
	public void setData(@Nullable IRuneChainData data);

	@Nullable
	public IRuneChainData getData();

	@Nullable
	public RuneChainComposition.Blueprint getBlueprint();
}
