package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			builder.out(descriptor).type(builder.in(descriptor).type(Object.class).collection().key()).register();
			
			return ImmutableList.of(builder.build());
		}

		@Override
		public RuneSelectRandom create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelectRandom(this, index, composition, (RuneConfiguration) configuration);
		}
		
		@Override
		protected Subject activate(RuneSelectRandom state, IRuneExecutionContext context, INodeIO io) {

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

	private RuneSelectRandom(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
