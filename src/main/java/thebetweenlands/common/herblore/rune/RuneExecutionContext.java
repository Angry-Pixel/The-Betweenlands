package thebetweenlands.common.herblore.rune;

import thebetweenlands.api.runechain.IAspectBuffer;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.chain.IRuneChain;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;

public class RuneExecutionContext implements IRuneExecutionContext {
	private final RuneChainComposition runeChain;
	private final IRuneChainUser user;

	int inputIndexCount;
	int inputIndex;
	int branchCount;
	int branch;

	public RuneExecutionContext(RuneChainComposition runeChainComposition, IRuneChainUser user) {
		this.runeChain = runeChainComposition;
		this.user = user;
	}

	@Override
	public IRuneChain getRuneChain() {
		return this.runeChain;
	}

	@Override
	public IRuneChainUser getUser() {
		return this.user;
	}

	@Override
	public IAspectBuffer getAspectBuffer() {
		return this.runeChain.getAspectBuffer();
	}

	@Override
	public int getBranchIndexCount() {
		return this.branchCount;
	}

	@Override
	public int getBranchIndex() {
		return this.branch;
	}

	@Override
	public int getInputIndexCount() {
		return this.inputIndexCount;
	}

	@Override
	public int getInputIndex() {
		return this.inputIndex;
	}

	@Override
	public int getRune() {
		return this.runeChain.getCurrentNode();
	}
}