package thebetweenlands.api.capability;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.chain.IRuneChain;
import thebetweenlands.api.runechain.chain.IRuneChainBlueprint;
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.initiation.InitiationPhase;
import thebetweenlands.api.runechain.initiation.InitiationState;
import thebetweenlands.api.runechain.rune.AbstractRune;

public interface IRuneChainCapability {
	public void setData(@Nullable IRuneChainData data);

	@Nullable
	public IRuneChainData getData();

	@Nullable
	public IRuneChainBlueprint getBlueprint();

	public void setInitiationState(@Nullable InitiationState<?> state);

	@Nullable
	public InitiationState<?> getInitiationState();

	/**
	 * Checks the rune chain's initiation state for a given initiation phase (e.g. interaction, right-click, etc).
	 * @param userCap Rune chain user
	 * @param initiationPhase Initiation phase (e.g. interaction, right-click, etc)
	 * @param initializer Initializer that is called when the rune chain is initiated and before it is run
	 * @param runOnInitiation Whether the rune chain should be run when the initiation state is true
	 * @return If runOnInitiation is true: ID of the initiated rune chain if initiation state is true or else -1. If runOnInitiation is false: 0 if initiation state is true or else -1.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public default int checkInitiation(IRuneChainUserCapability userCap, InitiationPhase initiationPhase, Consumer<IRuneChain> initializer, boolean runOnInitiation) {
		IRuneChainData data = this.getData();
		IRuneChainBlueprint blueprint = this.getBlueprint();

		if(data != null && blueprint != null && blueprint.getNodeBlueprints() > 0) {
			INodeBlueprint<?, IRuneExecutionContext> node = blueprint.getNodeBlueprint(0);

			if(node instanceof AbstractRune.Blueprint) {
				InitiationState initiation = ((AbstractRune.Blueprint) node).checkInitiation(userCap.getUser(), initiationPhase, this.getInitiationState());

				this.setInitiationState(initiation);

				if(initiation != null && initiation.isSuccess()) {
					if(runOnInitiation) {
						int id = userCap.addRuneChain(data);

						IRuneChain composition = userCap.getRuneChain(id);

						initializer.accept(composition);

						initiation.initiate((AbstractRune) composition.getNode(0));

						composition.run(userCap.getUser());

						userCap.setUpdating(id, true, true);

						return id;
					} else {
						return 0;
					}
				}
			}
		}

		return -1;
	}
}
