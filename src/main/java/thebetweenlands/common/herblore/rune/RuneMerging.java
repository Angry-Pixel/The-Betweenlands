package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.OutputKey;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
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
			
			RuneConfiguration.Builder builder = RuneConfiguration.create(RuneTokenDescriptors.ANY);

			ResourceLocation descriptor = RuneTokenDescriptors.ANY;
			
			if(linkAccess != null) {
				IConfigurationOutput linkedOutput = linkAccess.getLinkedOutput(0);
				
				if(linkedOutput instanceof OutputKey) {
					//TODO Temporary for testing
					String[] split = ((OutputKey<?, ?>) linkedOutput).getDescriptor().split("\\.");
					descriptor = new ResourceLocation(split[0], split[1]);
				}
			}
			
			builder.out(descriptor).type(builder.in(descriptor).type(Object.class).key()).collection().register();
			
			return ImmutableList.of(builder.build());
		}

		@Override
		public RuneMerging create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneMerging(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneMerging state, IRuneExecutionContext context, INodeIO io) {
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
	
	private RuneMerging(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
