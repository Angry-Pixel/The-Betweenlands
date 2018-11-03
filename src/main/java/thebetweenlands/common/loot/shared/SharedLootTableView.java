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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.api.loot.ISharedLootCondition;
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

	protected final SharedLootPool primarySharedPool;

	public SharedLootTableView(SharedLootPool primarySharedPool) {
		Preconditions.checkNotNull(primarySharedPool);

		this.primarySharedPool = primarySharedPool;
	}

	@Override
	public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
		return this.generateLootFromSharedPool(rand, context);
	}

	@SuppressWarnings("unchecked")
	protected List<ItemStack> generateLootFromSharedPool(Random rand, LootContext context) {
		List<ItemStack> stacks = new ArrayList<>();

		LootTable lootTable = this.getSharedLootTableInstance(this.primarySharedPool, context.getLootTableManager());

		if(lootTable != null) {
			List<LootPool> pools = new ArrayList<>();
			try {
				pools.addAll((List<LootPool>) f_LootTable_pools.get(lootTable));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			List<LootPool> sharedPools = new ArrayList<>();

			Iterator<LootPool> poolsIT = pools.iterator();
			while(poolsIT.hasNext()) {
				LootPool pool = poolsIT.next();

				if(LootConditionSharedPool.isSharedPool(rand, context, this.getPoolConditions(pool, true))) {
					sharedPools.add(pool);

					poolsIT.remove();
				}
			}

			Collections.shuffle(pools, rand);

			this.primarySharedPool.incrementGuaranteeCounter();

			Random sharedPoolRNG;
			long sharedPoolSeed = rand.nextLong(); //Always generate this long so that the generation order doesn't matter
			long setSharedPoolSeed = this.primarySharedPool.getLootTableSeed();
			if(setSharedPoolSeed != 0) {
				sharedPoolRNG = new Random(setSharedPoolSeed);
			} else {
				sharedPoolRNG = new Random(sharedPoolSeed);
			}


			//Generate all items from shared pools
			Map<EntryKey, List<ItemStack>> sharedPoolItems = new HashMap<>();
			for(LootPool pool : sharedPools) {
				this.generateLootForPoolFromSharedPool(sharedPoolItems, sharedPoolRNG, context, pool, true);
			}

			//Generate all items for this individual instance
			Map<EntryKey, List<ItemStack>> poolItems = new HashMap<>();
			for(LootPool pool : pools) {
				this.generateLootForPoolFromSharedPool(poolItems, rand, context, pool, false);
			}

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

								int removedItems = this.primarySharedPool.getRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry);
								this.primarySharedPool.setRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry, removedItems + 1);

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

									int removedItems = this.primarySharedPool.getRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry);
									this.primarySharedPool.setRemovedItems(sharedLootEntryKey.pool, sharedLootEntryKey.poolRoll, sharedLootEntryKey.entry, removedItems + additionalAvailableCount);

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

	protected boolean testAllConditions(Iterable<LootCondition> conditions, Random rand, LootContext context) {
		if(conditions == null) {
			return true;
		}

		for(LootCondition condition : conditions) {
			if(this.primarySharedPool != null && condition instanceof ISharedLootCondition) {
				if(!((ISharedLootCondition) condition).testCondition(rand, context, this.primarySharedPool)) {
					return false;
				}
			} else {
				if(!condition.testCondition(rand, context)) {
					return false;
				}
			}
		}
		return true;
	}

	protected void generateLootForPoolFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootPool pool, boolean isSharedPool) {
		if(context.addLootTable(this)) {
			this.generateLootFromSharedPool(stacks, rand, context, pool, isSharedPool);

			context.removeLootTable(this);
		} else {
			TheBetweenlands.logger.warn("Detected infinite loop in loot tables");
		}
	}

	protected void generateLootFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootPool pool, boolean isSharedPool) {
		List<LootCondition> poolConditions = this.getPoolConditions(pool, false);

		RandomValueRange rolls = pool.getRolls();
		RandomValueRange bonusRolls = pool.getBonusRolls();

		if(this.testAllConditions(poolConditions, rand, context)) {
			Random poolRand = rand;
			if(isSharedPool) {
				//Count must be consistently the same for the same shared pool
				poolRand = new Random(this.primarySharedPool.getLootPoolSeed(rand, pool.getName(), -1));
			}
			int count = rolls.generateInt(poolRand) + MathHelper.floor(bonusRolls.generateFloat(poolRand) * context.getLuck());

			for(int i = 0; i < count; ++i) {
				this.createLootRollFromSharedPool(stacks, rand, context, pool, isSharedPool, i);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean createLootRollFromSharedPool(Map<EntryKey, List<ItemStack>> stacks, Random rand, LootContext context, LootPool pool, boolean isSharedPool, int poolRoll) {
		int totalWeights = 0;

		List<LootEntry> lootEntries;
		try {
			lootEntries = (List<LootEntry>) f_LootPool_lootEntries.get(pool);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		List<Tuple<LootEntry, List<ItemStack>>> potentialLootEntries = new ArrayList<>();

		List<Tuple<LootEntry, List<ItemStack>>> guaranteedEntries = new ArrayList<>();

		final int guaranteeCounter = this.primarySharedPool.getGuaranteeCounter();
		final float guaranteePercentage = this.primarySharedPool.getGuaranteePercentage();

		for(LootEntry lootEntry : lootEntries) {
			LootConditionFromSharedPool fromSharedPoolCondition = LootConditionFromSharedPool.getCondition(rand, context, this.getEntryConditions(lootEntry, true));

			boolean isGuaranteed = !isSharedPool && fromSharedPoolCondition != null && fromSharedPoolCondition.isGuaranteed(guaranteePercentage, guaranteeCounter);

			if(isGuaranteed || this.testAllConditions(this.getEntryConditions(lootEntry, false), rand, context)) {
				int weight = lootEntry.getEffectiveWeight(context.getLuck());

				if(isGuaranteed || weight > 0) {
					List<ItemStack> generatedStacks = new ArrayList<>();

					Random entryRand = rand;
					if(isSharedPool) {
						//Generated loot must be consistently the same for the same shared pool, roll and entry
						entryRand = new Random(this.primarySharedPool.getLootEntrySeed(rand, pool.getName(), poolRoll, lootEntry.getEntryName()));
					}
					lootEntry.addLoot(generatedStacks, entryRand, context);

					if(isGuaranteed) {
						guaranteedEntries.add(new Tuple<>(lootEntry, generatedStacks));
					} else {
						potentialLootEntries.add(new Tuple<>(lootEntry, generatedStacks));

						totalWeights += weight;
					}
				}
			}
		}

		List<Tuple<LootEntry, List<ItemStack>>> validEntries = new ArrayList<>();

		if(!guaranteedEntries.isEmpty()) {
			validEntries.addAll(guaranteedEntries);
		} else {
			if(totalWeights != 0 && !potentialLootEntries.isEmpty()) {
				Random poolRand = rand;
				if(isSharedPool) {
					//randomWeight must be consistently the same for the same shared pool and roll
					poolRand = new Random(this.primarySharedPool.getLootPoolSeed(rand, pool.getName(), poolRoll));
				}
				int randomWeight = poolRand.nextInt(totalWeights);

				for(Tuple<LootEntry, List<ItemStack>> potentialEntry : potentialLootEntries) {
					randomWeight -= potentialEntry.getFirst().getEffectiveWeight(context.getLuck());

					if(randomWeight < 0) {
						validEntries.add(potentialEntry);
						break;
					}
				}
			}
		}

		if(!validEntries.isEmpty()) {
			for(Tuple<LootEntry, List<ItemStack>> validEntry : validEntries) {
				LootEntry lootEntry = validEntry.getFirst();
				List<ItemStack> loot = validEntry.getSecond();

				boolean isLootEntryStillAvailable = true;

				if(isSharedPool) {
					int removedItems = this.primarySharedPool.getRemovedItems(pool.getName(), poolRoll, lootEntry.getEntryName());

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
						key = new EntryKey(pool.getName(), poolRoll, lootEntry.getEntryName(), false);
					} else {
						key = new EntryKey(pool.getName(), poolRoll, lootEntry.getEntryName(), !LootConditionFromSharedPool.isFromSharedPool(rand, context, this.getEntryConditions(lootEntry, true)));
					}

					List<ItemStack> entryStacks = stacks.get(key);
					if(entryStacks == null) {
						stacks.put(key, entryStacks = new ArrayList<>());
					}

					entryStacks.addAll(loot);

					return true;
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

	protected static class EntryKey {
		protected final String pool;
		protected final String entry;

		protected final int poolRoll;
		protected final boolean isNonSharedEntry;

		private final int hashCode;

		protected EntryKey(String pool, int poolRoll, String entry, boolean isNonSharedEntry) {
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
