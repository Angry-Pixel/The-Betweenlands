package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneItemStackAccess;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InventoryRuneItemStackAccess;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;

public final class TokenRuneItem extends AbstractRune<TokenRuneItem> {

	public static final class Blueprint extends AbstractRune.Blueprint<TokenRuneItem> {
		private final Item item;

		public Blueprint(RuneStats stats, Item item) {
			super(stats);
			this.item = item;
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final OutputPort<IRuneItemStackAccess> OUT_ITEM_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final OutputPort<Collection<IRuneItemStackAccess>> OUT_ITEMS_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			OUT_ITEM_1 = builder.out(RuneTokenDescriptors.ITEM, IRuneItemStackAccess.class);
			CONFIGURATION_1 = builder.build();

			OUT_ITEMS_2 = builder.multiOut(RuneTokenDescriptors.ITEM, IRuneItemStackAccess.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public TokenRuneItem create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new TokenRuneItem(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(TokenRuneItem state, RuneExecutionContext context, INodeIO io) {

			IInventory inventory = context.getUser().getInventory();

			if(inventory != null) {
				List<IRuneItemStackAccess> accesses = new ArrayList<>();

				io.schedule(scheduler -> {
					int i = scheduler.getUpdateCount();

					if(i < inventory.getSizeInventory()) {
						ItemStack stack = inventory.getStackInSlot(i);

						if(!stack.isEmpty() && stack.getItem() == this.item) {
							Entity entity = context.getUser().getEntity();

							IRuneItemStackAccess access = new InventoryRuneItemStackAccess(inventory, i,
									s -> !s.isEmpty() && s.getItem() == this.item && (entity == null || entity.isEntityAlive()),
									s -> s.isEmpty() && (entity == null || entity.isEntityAlive()));

							if(state.getConfiguration() == CONFIGURATION_1) {
								OUT_ITEM_1.set(io, access);
								scheduler.terminate();
							} else {
								accesses.add(access);
							}

						}

						scheduler.sleep(0.05f);
					} else {
						if(state.getConfiguration() == CONFIGURATION_2) {
							OUT_ITEMS_2.set(io, accesses);
						}

						scheduler.terminate();
					}
				});
			} else {
				io.fail();
			}

			return null;
		}
	}

	private TokenRuneItem(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
