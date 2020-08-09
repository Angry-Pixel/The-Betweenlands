package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint.INodeIO;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneMerging extends AbstractRune<RuneMerging> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneMerging> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.005f)
					.build());
		}
		
		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			
			RuneConfiguration.Builder builder = RuneConfiguration.builder(RuneTokenDescriptors.ANY);

			ResourceLocation descriptor = RuneTokenDescriptors.ANY;
			
			if(linkAccess != null) {
				IConfigurationOutput linkedOutput = linkAccess.getLinkedOutput(0);
				
				if(linkedOutput instanceof OutputPort) {
					//TODO Temporary for testing
					String[] split = ((OutputPort<?>) linkedOutput).getDescriptor().split("\\.");
					descriptor = new ResourceLocation(split[0], split[1]);
				}
			}
			
			builder.multiOutFromIn(Object.class, builder.in(descriptor, null, Object.class));
			
			return ImmutableList.of(builder.build());
		}

		@Override
		public RuneMerging create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMerging(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneMerging state, RuneExecutionContext context, INodeIO io) {
			if(state.getConfiguration().getId() == 0) {
				state.outputs.add(io);
				state.outputValues.add(io.get(0));
				
				if(context.getInputIndex() == context.getInputIndexCount() - 1 && context.getBranchIndex() == context.getBranchIndexCount() - 1) {
					for(INodeIO output : state.outputs) {
						output.set(0, state.outputValues);
					}
				}
			}
			
			//TODO Other configs
			
			return null;
		}
	}

	private List<INodeIO> outputs = new ArrayList<>();
	private List<Object> outputValues = new ArrayList<>();
	
	private RuneMerging(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
