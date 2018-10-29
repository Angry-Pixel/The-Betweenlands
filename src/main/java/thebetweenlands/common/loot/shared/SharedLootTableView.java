package thebetweenlands.common.loot.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;

public class SharedLootTableView extends LootTable {
	protected final SharedLootPool sharedPool;
	protected final List<LootTable> views = new ArrayList<>();
	protected final int maxRolls;

	public SharedLootTableView(SharedLootPool sharedPool, int maxRolls) {
		super(new LootPool[0]);
		this.sharedPool = sharedPool;
		this.maxRolls = maxRolls;
	}

	public SharedLootTableView(List<LootTable> views, int maxRolls) {
		super(new LootPool[0]);
		this.sharedPool = null;
		this.views.addAll(views);
		this.maxRolls = maxRolls;
	}

	@Override
	public boolean isFrozen() {
		return true;
	}

	@Override
	public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
		int viewsCount = this.views.size() + (this.sharedPool != null ? 1 : 0);

		if(viewsCount == 0) {
			return Collections.emptyList();
		}

		//Pick random view or self. No weighted random here because that
		//is already handled by the shared pool
		int i = rand.nextInt(viewsCount);

		if(i >= this.views.size()) {
			return this.generateLootFromSharedPool(rand, context);
		} else {
			return this.views.get(i).generateLootForPools(rand, context);
		}
	}

	protected List<ItemStack> generateLootFromSharedPool(Random rand, LootContext context) {
		LootTable sharedLootTable = this.sharedPool.getSharedLootTableInstance(context.getLootTableManager());

		if(sharedLootTable != null) {
			List<LootPool> pools = null; //TODO sharedLootTable.pools
			return this.generateLootForPoolsFromSharedPool(rand, context, pools);
		}

		return Collections.emptyList();
	}

	protected List<ItemStack> generateLootForPoolsFromSharedPool(Random rand, LootContext context, List<LootPool> pools) {
		List<ItemStack> stacks = new ArrayList<>();

		if(context.addLootTable(this)) {
			for(LootPool lootpool : pools) {
				this.generateLootFromSharedPool(stacks, rand, context, lootpool);
			}

			context.removeLootTable(this);
		} else {
			//TODO
			//LOGGER.warn("Detected infinite loop in loot tables");
		}

		return stacks;
	}

	protected void generateLootFromSharedPool(Collection<ItemStack> stacks, Random rand, LootContext context, LootPool pool) {
		List<LootCondition> poolConditions = null; //TODO pool.poolConditions
		RandomValueRange rolls = null; //TODO pool.rolls
		RandomValueRange bonusRolls = null; //TODO pool.bonusRolls

		if(LootConditionManager.testAllConditions(poolConditions, rand, context)) {
			int count = rolls.generateInt(rand) + MathHelper.floor(bonusRolls.generateFloat(rand) * context.getLuck());

			for(int i = 0; i < count; ++i) {
				this.createLootRollFromSharedPool(stacks, rand, context, pool);
			}
		}
	}

	protected void createLootRollFromSharedPool(Collection<ItemStack> stacks, Random rand, LootContext context, LootPool pool) {
		//List<LootEntry> potentialLootEntries = Lists.<LootEntry>newArrayList();
		int totalWeights = 0;

		List<LootEntry> lootEntries = null; //TODO pool.lootEntries

		Map<LootEntry, List<ItemStack>> potentialLootEntries = new HashMap<>();

		for(LootEntry lootEntry : lootEntries) {
			List<LootCondition> conditions = null; //TODO lootEntry.conditions

			if(LootConditionManager.testAllConditions(conditions, rand, context)) {
				int weight = lootEntry.getEffectiveWeight(context.getLuck());

				if(weight > 0) {
					List<ItemStack> generatedStacks = new ArrayList<>();

					lootEntry.addLoot(generatedStacks, rand, context);

					int removedEntries = this.sharedPool.getRemovedEntries(pool, lootEntry);

					//TODO Optimize this
					for(int i = 0; i < removedEntries; i++) {
						if(generatedStacks.isEmpty()) {
							break;
						}

						int randStack = rand.nextInt(generatedStacks.size());
						ItemStack stack = generatedStacks.get(randStack);

						stack.shrink(1);

						if(stack.isEmpty()) {
							generatedStacks.remove(randStack);
						}
					}

					if(!generatedStacks.isEmpty()) {
						potentialLootEntries.put(lootEntry, generatedStacks);
						totalWeights += weight;
					}
				}
			}
		}

		if(totalWeights != 0 && !potentialLootEntries.isEmpty()) {
			int randomWeight = rand.nextInt(totalWeights);

			for(Entry<LootEntry, List<ItemStack>> potentialEntry : potentialLootEntries.entrySet()) {
				LootEntry lootEntry = potentialEntry.getKey();

				randomWeight -= lootEntry.getEffectiveWeight(context.getLuck());

				if(randomWeight < 0) {
					List<ItemStack> loot = potentialEntry.getValue();

					stacks.addAll(loot);

					int count = 0;
					for(ItemStack stack : loot) {
						count += stack.getCount();
					}

					this.sharedPool.setRemovedEntries(pool, lootEntry, this.sharedPool.getRemovedEntries(pool, lootEntry) + count);

					return;
				}
			}
		}
	}
}
