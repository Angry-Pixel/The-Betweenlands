package thebetweenlands.common.loot.shared;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.loot.LootTableView;
import thebetweenlands.common.TheBetweenlands;

public class SharedLootTableView extends LootTableView {
	private static final Field f_LootTable_pools;
	private static final Field f_LootPool_lootEntries;
	private static final Field f_LootPool_poolConditions;
	private static final Field f_LootEntry_conditions;

	static {
		f_LootTable_pools = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c", "c");
		f_LootPool_lootEntries = ReflectionHelper.findField(LootPool.class, "lootEntries", "field_186453_a", "a");
		f_LootPool_poolConditions = ReflectionHelper.findField(LootPool.class, "poolConditions", "field_186454_b", "b");
		f_LootEntry_conditions = ReflectionHelper.findField(LootEntry.class, "conditions", "field_186366_e", "e");
	}

	protected final ISharedLootPool primarySharedPool;
	protected final List<LootTableView> sharedPoolViews = new ArrayList<>();
	protected final int maxRolls, maxItems;

	public SharedLootTableView(ISharedLootPool primarySharedPool, int maxRolls, int maxItems) {
		Preconditions.checkNotNull(primarySharedPool);

		this.primarySharedPool = primarySharedPool;
		this.maxRolls = maxRolls;
		this.maxItems = maxItems;
	}

	public SharedLootTableView(List<LootTableView> sharedPoolViews, int maxRolls, int maxItems) {
		this.primarySharedPool = sharedPoolViews.get(0).getPrimarySharedLootPool();
		this.sharedPoolViews.addAll(sharedPoolViews);
		this.maxRolls = maxRolls;
		this.maxItems = maxItems;
	}

	@Override
	public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
		return this.generateLootFromSharedPool(rand, context);
	}

	protected List<ItemStack> generateLootFromSharedPool(Random rand, LootContext context) {
		List<ItemStack> stacks = new ArrayList<>();

		LootGenerationConstraints constraints = new LootGenerationConstraints();

		constraints.rollsLeft = this.maxRolls;
		constraints.itemsLeft = this.maxItems;

		List<Tuple<LootTableView, LootPool>> pools = this.getPools(context.getLootTableManager());

		Collections.shuffle(pools, rand);

		for(Tuple<LootTableView, LootPool> pool : pools) {
			this.generateLootForPoolFromSharedPool(stacks, rand, context, pool.getFirst(), pool.getSecond(), constraints);

			if(constraints.rollsLeft <= 0 || constraints.itemsLeft <= 0) {
				break;
			}
		}

		return stacks;
	}

	protected void generateLootForPoolFromSharedPool(Collection<ItemStack> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, LootGenerationConstraints constraints) {
		//TODO Implement maxSlots and maxItems

		if(context.addLootTable(lootTable)) {
			this.generateLootFromSharedPool(stacks, rand, context, lootTable, pool, constraints);

			context.removeLootTable(lootTable);
		} else {
			TheBetweenlands.logger.warn("Detected infinite loop in loot tables");
		}
	}

	@SuppressWarnings("unchecked")
	protected void generateLootFromSharedPool(Collection<ItemStack> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, LootGenerationConstraints constraints) {
		List<LootCondition> poolConditions;
		try {
			poolConditions = (List<LootCondition>) f_LootPool_poolConditions.get(pool);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		RandomValueRange rolls = pool.getRolls();
		RandomValueRange bonusRolls = pool.getBonusRolls();

		if(LootConditionManager.testAllConditions(poolConditions, rand, context)) {
			//Count must be consistently the same for the same pool
			Random poolRand = new Random(lootTable.getPrimarySharedLootPool().getLootPoolSeed(rand, pool, -1));
			int count = rolls.generateInt(poolRand) + MathHelper.floor(bonusRolls.generateFloat(poolRand) * context.getLuck());

			for(int i = 0; i < count; ++i) {
				if(this.createLootRollFromSharedPool(stacks, rand, context, lootTable, pool, i, constraints)) {
					if(--constraints.rollsLeft <= 0) {
						break;
					}
				}
				if(constraints.itemsLeft <= 0) {
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean createLootRollFromSharedPool(Collection<ItemStack> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, int poolRoll, LootGenerationConstraints constraints) {
		int totalWeights = 0;

		List<LootEntry> lootEntries;
		try {
			lootEntries = (List<LootEntry>) f_LootPool_lootEntries.get(pool);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		Map<LootEntry, List<ItemStack>> potentialLootEntries = new HashMap<>();

		for(LootEntry lootEntry : lootEntries) {
			LootCondition[] conditions;
			try {
				conditions = (LootCondition[]) f_LootEntry_conditions.get(lootEntry);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			if(LootConditionManager.testAllConditions(conditions, rand, context)) {
				int weight = lootEntry.getEffectiveWeight(context.getLuck());

				if(weight > 0) {
					List<ItemStack> generatedStacks = new ArrayList<>();

					//Generated loot must be consistently the same for the same pool, roll and entry
					Random entryRand = new Random(lootTable.getPrimarySharedLootPool().getLootEntrySeed(rand, pool, poolRoll, lootEntry));
					lootEntry.addLoot(generatedStacks, entryRand, context);

					potentialLootEntries.put(lootEntry, generatedStacks);
					totalWeights += weight;
				}
			}
		}

		if(totalWeights != 0 && !potentialLootEntries.isEmpty()) {
			//randomWeight must be consistently the same for the same pool and roll
			Random poolRand = new Random(lootTable.getPrimarySharedLootPool().getLootPoolSeed(rand, pool, poolRoll));
			int randomWeight = poolRand.nextInt(totalWeights);

			for(Entry<LootEntry, List<ItemStack>> potentialEntry : potentialLootEntries.entrySet()) {
				LootEntry lootEntry = potentialEntry.getKey();

				randomWeight -= lootEntry.getEffectiveWeight(context.getLuck());

				if(randomWeight < 0) {
					List<ItemStack> loot = potentialEntry.getValue();

					int removedItems = lootTable.getPrimarySharedLootPool().getRemovedItems(pool, poolRoll, lootEntry);

					boolean isLootEntryStillAvailable;

					if(!loot.isEmpty()) {
						//Not empty entry

						this.removeRandomItems(rand, loot, removedItems);

						if(!loot.isEmpty()) {
							int count = 0;
							for(ItemStack stack : loot) {
								count += stack.getCount();
							}

							if(count > constraints.itemsLeft) {
								count -= this.removeRandomItems(rand, loot, count - constraints.itemsLeft);
							}

							constraints.itemsLeft -= count;

							lootTable.getPrimarySharedLootPool().setRemovedItems(pool, poolRoll, lootEntry, removedItems + count);

							isLootEntryStillAvailable = true;
						} else {
							isLootEntryStillAvailable = false;
						}
					} else {
						//Empty entry

						if(removedItems <= 0) {
							lootTable.getPrimarySharedLootPool().setRemovedItems(pool, poolRoll, lootEntry, removedItems + 1);
							isLootEntryStillAvailable = true;
						} else {
							isLootEntryStillAvailable = false;
						}
					}

					if(isLootEntryStillAvailable) {
						stacks.addAll(loot);
						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	protected int removeRandomItems(Random rand, List<ItemStack> loot, int count) {
		if(loot.isEmpty()) {
			return 0;
		}

		for(int i = 0; i < count; i++) {
			int randStack = rand.nextInt(loot.size());
			ItemStack stack = loot.get(randStack);

			stack.shrink(1);

			if(stack.isEmpty()) {
				loot.remove(randStack);
			}

			if(loot.isEmpty()) {
				return i;
			}
		}

		return count;
	}

	@Nullable
	protected LootTable getSharedLootTableInstance(ISharedLootPool pool, LootTableManager manager) {
		if(pool.getLootTable() != null) {
			return manager.getLootTableFromLocation(pool.getLootTable());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tuple<LootTableView, LootPool>> getPools(LootTableManager manager) {
		List<Tuple<LootTableView, LootPool>> pools = new ArrayList<>();

		if(this.sharedPoolViews.isEmpty()) {
			LootTable sharedLootTable = this.getSharedLootTableInstance(this.primarySharedPool, manager);

			if(sharedLootTable != null) {
				List<LootPool> sharedLootTablePools;
				try {
					sharedLootTablePools = (List<LootPool>) f_LootTable_pools.get(sharedLootTable);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				for(LootPool pool : sharedLootTablePools) {
					pools.add(new Tuple<>(this, pool));
				}
			}
		} else {
			for(LootTableView view : this.sharedPoolViews) {
				pools.addAll(view.getPools(manager));
			}
		}

		return pools;
	}

	@Override
	public ISharedLootPool getPrimarySharedLootPool() {
		return this.primarySharedPool;
	}

	protected static class LootGenerationConstraints {
		protected int rollsLeft;
		protected int itemsLeft;
	}
}
