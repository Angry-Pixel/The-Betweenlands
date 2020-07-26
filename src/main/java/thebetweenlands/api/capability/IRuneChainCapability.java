package thebetweenlands.api.capability;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.AbstractRune.Blueprint.InitiationPhase;
import thebetweenlands.api.rune.impl.AbstractRune.Blueprint.InitiationState;
import thebetweenlands.api.rune.impl.RuneChainComposition;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;

public interface IRuneChainCapability {
	public void setData(@Nullable IRuneChainData data);

	@Nullable
	public IRuneChainData getData();

	@Nullable
	public RuneChainComposition.Blueprint getBlueprint();

	public void setInitiationState(@Nullable InitiationState state);

	@Nullable
	public InitiationState getInitiationState();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public default int checkInitiationAndRun(IRuneChainUserCapability userCap, InitiationPhase state, Consumer<RuneChainComposition> initializer) {
		IRuneChainData data = this.getData();
		RuneChainComposition.Blueprint blueprint = this.getBlueprint();

		if(data != null && blueprint != null && blueprint.getNodeBlueprints() > 0) {
			INodeBlueprint<?, RuneExecutionContext> node = blueprint.getNodeBlueprint(0);

			if(node instanceof AbstractRune.Blueprint) {
				InitiationState initiation = ((AbstractRune.Blueprint<?>) node).checkInitiation(userCap.getUser(), state, this.getInitiationState());

				this.setInitiationState(initiation);

				if(initiation != null && initiation.isSuccess()) {
					int id = userCap.addRuneChain(data);

					RuneChainComposition composition = userCap.getRuneChain(id);

					initializer.accept(composition);

					((AbstractRune.Blueprint) node).initiate(userCap.getUser(), initiation, (AbstractRune) composition.getNode(0));

					composition.run(userCap.getUser());

					userCap.setUpdating(id, true, true);

					return id;
				}
			}
		}

		return -1;
	}
}
