package thebetweenlands.common.capability.item;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.impl.RuneChainComposition;
import thebetweenlands.common.herblore.rune.RuneChainData;

public interface IRuneChainCapability {
	public void setData(@Nullable RuneChainData data);
	
	@Nullable
	public RuneChainData getData();

	@Nullable
	public RuneChainComposition.Blueprint getBlueprint();
}
