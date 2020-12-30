package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.InputKey;
import thebetweenlands.api.runechain.io.OutputKey;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneCollecting extends AbstractRune<RuneCollecting> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneCollecting> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.005f)
					.build());
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {

			RuneConfiguration.Builder builder = RuneConfiguration.create(RuneTokenDescriptors.ANY);

			ResourceLocation descriptor = RuneTokenDescriptors.ANY;

			InputKey<?, ?> baseInput = null;

			if(linkAccess != null) {
				OutputKey<?, ?> linkedOutputPort = null;
				int numInputs = 0;

				for(int i = 0; i < 9; i++) {
					IConfigurationOutput linkedOutput = linkAccess.getLinkedOutput(i);

					if(linkedOutput != null) {
						numInputs = i + 1;

						if(linkedOutput instanceof OutputKey) {
							linkedOutputPort = (OutputKey<?, ?>) linkedOutput;
						}
					}
				}

				if(linkedOutputPort != null) {
					baseInput = builder.in(linkedOutputPort.getDescriptor()).type(linkedOutputPort).key();

					//TODO Temporary for testing
					String[] split = linkedOutputPort.getDescriptor().split("\\.");
					descriptor = new ResourceLocation(split[0], split[1]);

					for(int i = 0; i < Math.min(numInputs + (provisional ? 0 : -1), 8); i++) {
						builder.in(linkedOutputPort.getDescriptor()).type(linkedOutputPort).register();
					}
				}
			}

			if(baseInput == null) {
				baseInput = builder.in(descriptor).type(Object.class).key();
			}

			builder.out(baseInput.getDescriptor()).type(baseInput).collection().register();

			return ImmutableList.of(builder.build());
		}

		@Override
		public RuneCollecting create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneCollecting(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneCollecting state, IRuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration().getId() == 0) {
				state.outputs.add(io);
				
				for(int i = 0; i < state.getConfiguration().getInputs().size(); i++) {
					Object input = io.get(i);
					if(input instanceof Collection) {
						state.outputValues.addAll((Collection<?>)input);
					} else {
						state.outputValues.add(input);
					}
				}

				if(context.getInputIndex() == context.getInputIndexCount() - 1) {
					for(INodeIO output : state.outputs) {
						output.set(0, state.outputValues);
					}
				}
			}

			return null;
		}
	}

	private List<INodeIO> outputs = new ArrayList<>();
	private Set<Object> outputValues = new HashSet<>();

	private RuneCollecting(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
