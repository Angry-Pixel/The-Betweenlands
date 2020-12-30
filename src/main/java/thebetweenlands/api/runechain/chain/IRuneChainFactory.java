package thebetweenlands.api.runechain.chain;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IRuneChainFactory {
	public IRuneChainBlueprint create(@Nullable IRuneChainData data);
}
