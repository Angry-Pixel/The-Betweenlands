package thebetweenlands.common.loot.shared;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import thebetweenlands.common.loot.LootConditionFromSharedPool;
import thebetweenlands.common.loot.LootConditionSharedPool;

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

	public SharedLootTableView(ISharedLootPool primarySharedPool) {
		Preconditions.checkNotNull(primarySharedPool);

		this.primarySharedPool = primarySharedPool;
	}

	public SharedLootTableView(List<LootTableView> sharedPoolViews) {
		this.primarySharedPool = sharedPoolViews.get(0).getPrimarySharedLootPool();
		this.sharedPoolViews.addAll(sharedPoolViews);
	}

	@Override
	public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
		return this.generateLootFromSharedPool(rand, context);
	}

	protected List<ItemStack> generateLootFromSharedPool(Random rand, LootContext context) {
		List<Tuple<LootTableView, LootPool>> pools = this.getPools(context.getLootTableManager());

		List<Tuple<LootTableView, LootPool>> sharedPools = new ArrayList<>();

		Iterator<Tuple<LootTableView, LootPool>> poolsIT = pools.iterator();
		while(poolsIT.hasNext()) {
			Tuple<LootTableView, LootPool> pool = poolsIT.next();

			if(LootConditionSharedPool.isSharedPool(rand, context, this.getPoolConditions(pool.getSecond(), true))) {
				sharedPools.add(pool);

				poolsIT.remove();
			}
		}

		Collections.shuffle(pools, rand);

		//Generate all items from shared pools
		Map<EntryKey, List<ItemStack>> sharedPoolItems = new HashMap<>();
		for(Tuple<LootTableView, LootPool> pool : sharedPools) {
			this.generateLootForPoolFromSharedPool(sharedPoolItems, rand, context, pool.getFirst(), pool.getSecond(), true);
		}

		//Generate all items for this individual instance
		Map<EntryKey, List<ItemStack>> poolItems = new HashMap<>();
		for(Tuple<LootTableView, LootPool> pool : pools) {
			this.generateLootForPoolFromSharedPool(poolItems, rand, context, pool.getFirst(), pool.getSecond(), false);
		}

		List<ItemStack> stacks = new ArrayList<>();

		for(EntryKey lootEntryKey : poolItems.keySet()) {
			List<ItemStack> loot = poolItems.get(lootEntryKey);

			if(lootEntryKey.isNonSharedEntry) {
				stacks.addAll(loot);
				continue;
			}

			Iterator<ItemStack> lootStackIT = loot.iterator();

			lootStackLoop: while(lootStackIT.hasNext()) {
				ItemStack lootStack = lootStackIT.next();

				int availableLootCount = 0;

				sharedStackLoop: for(Entry<EntryKey, List<ItemStack>> sharedLootEntry : sharedPoolItems.entrySet()) {
					EntryKey sharedLootEntryKey = sharedLootEntry.getKey();
					List<ItemStack> sharedLoot = sharedLootEntry.getValue();

					Iterator<ItemStack> sharedLootStackIT = sharedLoot.iterator();

					while(sharedLootStackIT.hasNext()) {
						ItemStack sharedLootStack = sharedLootStackIT.next();

						if(lootStack.isEmpty() && sharedLootStack.isEmpty()) {
							sharedLootStackIT.remove();

							int removedItems = sharedLootEntryKey.lootTable.getPrimarySharedLootPool().getRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry);
							sharedLootEntryKey.lootTable.getPrimarySharedLootPool().setRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry, removedItems + 1);

							sharedLootStackIT.remove();

							stacks.add(ItemStack.EMPTY);

							//Adding of empty stack is already handled, continue with next loot stack
							continue lootStackLoop;
						} else if(!lootStack.isEmpty() && !sharedLootStack.isEmpty() && lootStack.getItem() == sharedLootStack.getItem()) {
							int additionalAvailableCount = Math.min(lootStack.getCount() - availableLootCount, sharedLootStack.getCount());

							if(additionalAvailableCount > 0) {
								availableLootCount += additionalAvailableCount;

								sharedLootStack.shrink(additionalAvailableCount);

								if(sharedLootStack.isEmpty()) {
									sharedLootStackIT.remove();
								}

								int removedItems = sharedLootEntryKey.lootTable.getPrimarySharedLootPool().getRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry);
								sharedLootEntryKey.lootTable.getPrimarySharedLootPool().setRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry, removedItems + additionalAvailableCount);

								if(availableLootCount >= lootStack.getCount()) {
									//Loot stack is fully available, can stop checking now
									break sharedStackLoop;
								}
							}
						}
					}
				}

				if(availableLootCount > 0) {
					lootStack.setCount(availableLootCount);

					stacks.add(lootStack.copy());
				}
			}
		}

		return stacks;
	}

	@SuppressWarnings("unchecked")
	protected List<LootCondition> getPoolConditions(LootPool pool, boolean sharedConditions) {
		List<LootCondition> allConditions;
		try {
			allConditions = (List<LootCondition>) f_LootPool_poolConditions.get(pool);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		List<LootCondition> conditions = new ArrayList<>();
		for(LootCondition condition : allConditions) {
			if(condition instanceof LootConditionSharedPool == sharedConditions) {
				conditions.add(condition);
			}
		}
		return conditions;
	}

	protected List<LootCondition> getEntryConditions(LootEntry entry, boolean sharedConditions) {
		LootCondition[] allConditions;
		try {
			allConditions = (LootCondition[]) f_LootEntry_conditions.get(entry);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		List<LootCondition> conditions = new ArrayList<>();
		for(LootCondition condition : allConditions) {
			if(condition instanceof LootConditionFromSharedPool == sharedConditions) {
				conditions.add(condition);
			}
		}
		return conditions;
	}

	protected void generateLootForPoolFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, boolean isSharedPool) {
		if(context.addLootTable(lootTable)) {
			this.generateLootFromSharedPool(stacks, rand, context, lootTable, pool, isSharedPool);

			context.removeLootTable(lootTable);
		} else {
			TheBetweenlands.logger.warn("Detected infinite loop in loot tables");
		}
	}

	protected void generateLootFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, boolean isSharedPool) {
		List<LootCondition> poolConditions = this.getPoolConditions(pool, false);

		RandomValueRange rolls = pool.getRolls();
		RandomValueRange bonusRolls = pool.getBonusRolls();

		if(LootConditionManager.testAllConditions(poolConditions, rand, context)) {
			Random poolRand = rand;
			if(isSharedPool) {
				//Count must be consistently the same for the same shared pool
				poolRand = new Random(lootTable.getPrimarySharedLootPool().getLootPoolSeed(rand, pool.getName(), -1));
			}
			int count = rolls.generateInt(poolRand) + MathHelper.floor(bonusRolls.generateFloat(poolRand) * context.getLuck());

			for(int i = 0; i < count; ++i) {
				this.createLootRollFromSharedPool(stacks, rand, context, lootTable, pool, isSharedPool, i);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean createLootRollFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootTableView lootTable, LootPool pool, boolean isSharedPool, int poolRoll) {
		int totalWeights = 0;

		List<LootEntry> lootEntries;
		try {
			lootEntries = (List<LootEntry>) f_LootPool_lootEntries.get(pool);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		Map<LootEntry, List<ItemStack>> potentialLootEntries = new HashMap<>();

		for(LootEntry lootEntry : lootEntries) {
			if(LootConditionManager.testAllConditions(this.getEntryConditions(lootEntry, false), rand, context)) {
				int weight = lootEntry.getEffectiveWeight(context.getLuck());

				if(weight > 0) {
					List<ItemStack> generatedStacks = new ArrayList<>();

					Random entryRand = rand;
					if(isSharedPool) {
						//Generated loot must be consistently the same for the same shared pool, roll and entry
						entryRand = new Random(lootTable.getPrimarySharedLootPool().getLootEntrySeed(rand, pool.getName(), poolRoll, lootEntry.getEntryName()));
					}
					lootEntry.addLoot(generatedStacks, entryRand, context);

					potentialLootEntries.put(lootEntry, generatedStacks);
					totalWeights += weight;
				}
			}
		}

		if(totalWeights != 0 && !potentialLootEntries.isEmpty()) {
			Random poolRand = rand;
			if(isSharedPool) {
				//randomWeight must be consistently the same for the same shared pool and roll
				poolRand = new Random(lootTable.getPrimarySharedLootPool().getLootPoolSeed(rand, pool.getName(), poolRoll));
			}
			int randomWeight = poolRand.nextInt(totalWeights);

			for(Entry<LootEntry, List<ItemStack>> potentialEntry : potentialLootEntries.entrySet()) {
				LootEntry lootEntry = potentialEntry.getKey();

				randomWeight -= lootEntry.getEffectiveWeight(context.getLuck());

				if(randomWeight < 0) {
					List<ItemStack> loot = potentialEntry.getValue();

					boolean isLootEntryStillAvailable = true;

					if(isSharedPool) {
						int removedItems = lootTable.getPrimarySharedLootPool().getRemovedItems(pool.getName(), poolRoll, lootEntry.getEntryName());

						if(!loot.isEmpty()) {
							//Not empty entry

							this.removeRandomItems(rand, loot, removedItems);

							if(!loot.isEmpty()) {
								int count = 0;
								for(ItemStack stack : loot) {
									count += stack.getCount();
								}

								if(count > 0) {

									isLootEntryStillAvailable = true;
								} else {
									isLootEntryStillAvailable = false;
								}
							} else {
								isLootEntryStillAvailable = false;
							}
						} else {
							//Empty entry

							if(removedItems <= 0) {
								isLootEntryStillAvailable = true;
							} else {
								isLootEntryStillAvailable = false;
							}
						}
					}

					if(isLootEntryStillAvailable) {
						final EntryKey key;

						if(isSharedPool) {
							key = new EntryKey(lootTable, pool.getName(), poolRoll, lootEntry.getEntryName(), false);
						} else {
							key = new EntryKey(lootTable, pool.getName(), poolRoll, lootEntry.getEntryName(), !LootConditionFromSharedPool.isFromSharedPool(poolRand, context, this.getEntryConditions(lootEntry, true)));
						}

						List<ItemStack> entryStacks = stacks.get(key);
						if(entryStacks == null) {
							stacks.put(key, entryStacks = new ArrayList<>());
						}

						entryStacks.addAll(loot);

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

	protected static class EntryKey {
		protected final String pool;
		protected final String entry;

		protected final LootTableView lootTable;
		protected final int poolRoll;
		protected final boolean isNonSharedEntry;

		private final int hashCode;

		protected EntryKey(LootTableView lootTable, String pool, int poolRoll, String entry, boolean isNonSharedEntry) {
			this.lootTable = lootTable;
			this.pool = pool;
			this.poolRoll = poolRoll;
			this.entry = entry;
			this.isNonSharedEntry = isNonSharedEntry;

			final int prime = 31;
			int hashCode = 1;
			hashCode = prime * hashCode + this.entry.hashCode();
			hashCode = prime * hashCode + this.pool.hashCode();
			this.hashCode = hashCode;
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			EntryKey other = (EntryKey) obj;
			return other.entry.equals(this.entry) && other.pool.equals(this.pool);
		}
	}
}
