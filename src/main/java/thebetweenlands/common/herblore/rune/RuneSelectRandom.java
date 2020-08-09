package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeBlueprint.IConfigurationLinkAccess;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneSelectRandom extends AbstractRune<RuneSelectRandom> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneSelectRandom> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.1f, 0.01f)
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
			
			builder.out(Object.class, builder.multiIn(descriptor, null, Object.class));
			
			return ImmutableList.of(builder.build());
		}

		@Override
		public RuneSelectRandom create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelectRandom(this, index, composition, (RuneConfiguration) configuration);
		}
		
		@Override
		protected RuneEffectModifier.Subject activate(RuneSelectRandom state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration().getId() == 0) {
				
				@SuppressWarnings("unchecked")
				List<Object> inputs = new ArrayList<>((Collection<Object>)io.get(0));
				if(!inputs.isEmpty()) {
					Object obj = inputs.get(context.getUser().getWorld().rand.nextInt(inputs.size()));
					io.set(0, obj);
				} else {
					io.fail();
				}
			}

			return null;
		}
	}
	
	private Map<Integer, Object> randomPicks = new HashMap<>();

	private RuneSelectRandom(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
