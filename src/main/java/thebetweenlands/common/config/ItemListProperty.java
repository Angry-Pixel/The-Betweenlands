package thebetweenlands.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.spongepowered.include.com.google.common.base.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.ItemComponentTagParser;
import thebetweenlands.util.ItemComponentTagParser.ComponentVisitor;

public class ItemListProperty {
	public static final short WILDCARD_VALUE = 32767;
	
	protected static ResourceLocation getItemRegistryName(ItemStack stack) {
		Holder<Item> item = stack.getItemHolder();
		Preconditions.checkNotNull(item);
		ResourceKey<Item> key = item.getKey();
		Preconditions.checkNotNull(key);
		return key.location();
	}
	
	// Note: behaviour is different to that of DataComponentPredicate
	public static class DataComponentTagPredicate implements Predicate<DataComponentMap> {

		public static record TypedDataComponentTag<T>(DataComponentType<T> type, Tag value) {}
		
		public static class Builder {
	        private final List<TypedDataComponentTag<?>> expectedComponents = new ArrayList<>();

	        public Builder() {}

	        public <T> DataComponentTagPredicate.Builder expect(DataComponentType<? super T> component, Tag value) {
	            for (TypedDataComponentTag<?> typeddatacomponent : this.expectedComponents) {
	                if (typeddatacomponent.type() == component) {
	                    throw new IllegalArgumentException("Predicate already has component of type: '" + component + "'");
	                }
	            }

	            this.expectedComponents.add(new TypedDataComponentTag<>(component, value));
	            return this;
	        }

	        public <T> DataComponentTagPredicate.Builder without(DataComponentType<? super T> component) {
	            this.expectedComponents.add(new TypedDataComponentTag<>(component, null));
	            return this;
	        }

	        public DataComponentTagPredicate build(HolderLookup.Provider registries) {
	        	if(registries == null) {
		            return new DataComponentTagPredicate(ImmutableList.copyOf(this.expectedComponents));
	        	} else {
		            return new DataComponentTagPredicate(ImmutableList.copyOf(this.expectedComponents), registries);
	        	}
	        }
		}
		
		public static final DataComponentTagPredicate EMPTY = new DataComponentTagPredicate(List.of());
	    private final List<TypedDataComponentTag<?>> expectedComponents;
	    private DynamicOps<Tag> dynamicOps = null;
	    private HolderLookup.Provider registries;

		public DataComponentTagPredicate(List<TypedDataComponentTag<?>> expectedComponents) {
			this(expectedComponents, HolderLookup.Provider.create(Stream.of(BuiltInRegistries.DATA_COMPONENT_TYPE.asLookup())));
		}
		
		public DataComponentTagPredicate(List<TypedDataComponentTag<?>> expectedComponents, HolderLookup.Provider registries) {
			this.expectedComponents = expectedComponents;
			this.registries = registries;
			this.dynamicOps = registries.createSerializationContext(NbtOps.INSTANCE);
		}

	    @Override
	    public boolean equals(Object other) {
	        if (other instanceof DataComponentTagPredicate datacomponentpredicate && this.expectedComponents.equals(datacomponentpredicate.expectedComponents)) {
	            return true;
	        }

	        return false;
	    }

	    @Override
	    public int hashCode() {
	        return this.expectedComponents.hashCode();
	    }

	    @Override
	    public String toString() {
	        return this.expectedComponents.toString();
	    }

	    public boolean alwaysMatches() {
	        return this.expectedComponents.isEmpty();
	    }
	    
		@Override
		public boolean test(DataComponentMap tagMap) {
			if(!this.alwaysMatches()) {
				Preconditions.checkNotNull(this.dynamicOps);
				for(TypedDataComponentTag<?> entry : this.expectedComponents) {
					final DataComponentType<?> componentType = entry.type();
					final Tag expectedValue = entry.value();
					final boolean mapHasValue = tagMap.has(componentType);
					if(mapHasValue && expectedValue == null) {
						return false;
					}
					
					if(!mapHasValue) {
						if(expectedValue != null) {
							return false;
						} else {
							continue;
						}
					}
					
					TypedDataComponent<?> typed = tagMap.getTyped(componentType);
					Tag componentData = typed.encodeValue(this.dynamicOps).getOrThrow();
					
					if(!compareTags(expectedValue, componentData)) return false;
				}
			}
			return true;
		}

		public boolean test(ItemStack itemstack) {
			return this.test(itemstack.getComponents());
		}

		public boolean setRegistries(HolderLookup.Provider registries) {
			if(this.registries != registries) {
				this.registries = registries;
				this.dynamicOps = registries.createSerializationContext(NbtOps.INSTANCE);
				return true;
			} else {
				return false;
			}
		}
		
		public boolean test(ItemStack itemstack, HolderLookup.Provider registries) {
			this.setRegistries(registries);
			return this.test(itemstack);
		}
		
		
		/**
		 * Whether or not Tag B is equal to or a "subset" of Tag A
		 * <br/>
		 * For example: if Tag A is {foo: "bar"}, then:
		 * <ul>
		 * 	<li>{foo: "bar", baz: "qux"} is a subset of A</li>
		 * 	<li>{foo: "qux", baz: "bar"} is <em>not</em> a subset of A</li>
		 * </ul>
		 * <br/>
		 * Note: <strong>NOT</strong> identical to {@link net.minecraft.nbt.NbtUtils.compareNbt NbtUtils.compareNbt(Tag, Tag, boolean)}
		 * @param tagA
		 * @param tagB
		 * @return
		 */
		protected static boolean compareTags(Tag tagA, Tag tagB) {
			final byte typeId = tagA.getId();
			final TagType<?> tagType = tagA.getType();
			if(typeId != tagB.getId() || !tagType.equals(tagB.getType())) { // Different types
				if(isNumericTag(typeId) && isNumericTag(tagB.getId()) && tagA instanceof NumericTag numA && tagB instanceof NumericTag numB) {
//					TheBetweenlands.LOGGER.info("Two different tag types: {} {}", tagA, tagB);
					return compareMixedNumbers(numA, numB);
				}
				return false;
			}
			
			if(tagType.isValue() || typeId == Tag.TAG_BYTE_ARRAY || typeId == Tag.TAG_INT_ARRAY || typeId == Tag.TAG_LONG_ARRAY) { // Primitives
				return tagA.equals(tagB);
			}
			
			if(typeId == Tag.TAG_COMPOUND) {
				return compareCompoundTags((CompoundTag)tagA, (CompoundTag)tagB);
			}
			
			// TODO lists & NbtPath support
			
			return tagA.equals(tagB);
		}

		protected static boolean isNumericTag(final byte typeId) {
			return typeId == Tag.TAG_ANY_NUMERIC || typeId == Tag.TAG_BYTE || typeId == Tag.TAG_SHORT || typeId == Tag.TAG_INT || typeId == Tag.TAG_LONG || typeId == Tag.TAG_FLOAT || typeId == Tag.TAG_DOUBLE;
		}
		
		protected static boolean isIntegral(Number number) {
			return number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long;
		}
		
		protected static boolean compareMixedNumbers(NumericTag tagA, NumericTag tagB) {
			final Number numberA = tagA.getAsNumber();
			final Number numberB = tagB.getAsNumber();

			final boolean isAIntegral = isIntegral(numberA);
			final boolean isBIntegral = isIntegral(numberB);
			
			if(isAIntegral && isBIntegral) {
				return numberA.longValue() == numberB.longValue();
			} else if(!isAIntegral && !isBIntegral) {
				return numberA.doubleValue() == numberB.doubleValue();
			} else if(isAIntegral && !isBIntegral) {
				return numberA.longValue() == numberB.doubleValue();
			} else if(!isAIntegral && isBIntegral) {
				return numberA.doubleValue() == numberB.longValue();
			} else {
				// This should never happen
				return false;
			}
		}
		
		protected static boolean compareCompoundTags(CompoundTag tagA, CompoundTag tagB) {
			for(String key : tagA.getAllKeys()) {
				Tag tag = tagA.get(key);
				final byte tagId = tag.getId();
				if(tagId == Tag.TAG_COMPOUND) {
					if(!tagB.contains(key, tagId)) return false;
					if(!compareCompoundTags((CompoundTag) tag, tagB.getCompound(key))) return false;
				} else {
					if(!tagB.contains(key)) return false;
					if(!compareTags(tag, tagB.get(key))) return false;
				}
			}
			return true;
		}
	}
	
	public static class ComparableItemStack {
		public final ResourceLocation item;
		public final int meta;
		public final DataComponentTagPredicate components;

		public ComparableItemStack(ResourceLocation item) {
			this(item, WILDCARD_VALUE);
		}

		public ComparableItemStack(ResourceLocation item, int meta) {
			this(item, meta, null);
		}

		public ComparableItemStack(ResourceLocation item, int meta, DataComponentTagPredicate components) {
			this.item = item;
			this.meta = meta;
			this.components = components;
		}
		
//		public ComparableItemStack(ItemStack stack) {
////			stack.getItemHolder().getDelegate().createSerializationContext(NbtOps.INSTANCE);
//			this.item = stack.getItemHolder().getKey().location();
//			this.meta = stack.getDamageValue();
//		}

		// We doing weird set hacks below
		public boolean equals(Object other) {
			return this.equalsWildcard(other);
		}

		public boolean equalsNonWildcard(Object other) {
			if(this == other) return true;

			return other instanceof ComparableItemStack comparable && this.item.equals(comparable.item) && this.meta == comparable.meta && Objects.equal(this.components, comparable.components);
		}
		
		
		public boolean equalsWildcard(Object other) {
			if(this == other) return true;

			return other instanceof ComparableItemStack comparable && this.item.equals(comparable.item) && (this.meta == comparable.meta || this.meta == WILDCARD_VALUE || comparable.meta == WILDCARD_VALUE) && Objects.equal(this.components, comparable.components);
		}
		
		public boolean test(ItemStack stack) {
			return this.item.equals(stack.getItemHolder().getKey().location()) && (this.meta == stack.getDamageValue() || this.meta == WILDCARD_VALUE) && (this.components == null || this.components.test(stack));
		}
	}
	
	private final Supplier<String[]> unparsed;

	private Map<ResourceLocation, Set<ComparableItemStack>> itemList;

	private Set<ResourceLocation> itemTags;
	private Set<DataComponentTagPredicate> itemComponents;

	private boolean cacheBuilt = false;
	private HolderLookup.Provider registryAccessCache;
	private Set<TagKey<Item>> itemTagCache;
	
	public ItemListProperty(Supplier<String[]> unparsed) {
		this.itemList = new HashMap<ResourceLocation, Set<ComparableItemStack>>();
		this.itemTags = new HashSet<ResourceLocation>();
		this.itemComponents = new HashSet<DataComponentTagPredicate>();
		this.itemTagCache = new HashSet<TagKey<Item>>();
		this.unparsed = unparsed;
	}
	
	public boolean isCacheBuilt() {
		return cacheBuilt;
	}
	
	@SuppressWarnings("deprecation")
	public void buildCache(HolderLookup.Provider registryAccess) {
		if(registryAccess == null) {
			registryAccess = HolderLookup.Provider.create(Stream.of(BuiltInRegistries.ITEM.asLookup(), BuiltInRegistries.BLOCK.asLookup(), BuiltInRegistries.DATA_COMPONENT_TYPE.asLookup()));
		}
		
		this.registryAccessCache = registryAccess;
		this.itemTagCache.clear();
		
		for(ResourceLocation location : this.itemTags) {
			this.itemTagCache.add(new TagKey<Item>(Registries.ITEM, location));
		}
		
		for(DataComponentTagPredicate predicate : itemComponents) {
			predicate.setRegistries(registryAccess);
		}
		
		for(final Set<ComparableItemStack> set : this.itemList.values()) {
			for(final ComparableItemStack stack : set) {
				final DataComponentTagPredicate predicate = stack.components;
				if(predicate != null)
					predicate.setRegistries(registryAccess);
			}
		}
		
		cacheBuilt = true;
	}
	
	public void clearCache() {
		this.registryAccessCache = null;
		this.itemTagCache.clear();
		this.cacheBuilt = true;
	}
	
	public void parseData() {
		this.clearCache();
		
		itemList.clear();
		itemTags.clear();
		itemComponents.clear();
		
		String[] items = unparsed.get();
		
		for(String string : items) {
			string = string.trim();
			if(string.length() == 0) continue;
			if(string.charAt(0) == '#')
				processTag(string);
			else if(string.charAt(0) == '[')
				processComponents(string);
			else
				processItem(string);
		}
	}

	protected void processTag(String string) {
		try {
			ResourceLocation parsed = ResourceLocation.tryParse(string.substring(1));
			Preconditions.checkNotNull(parsed);
			itemTags.add(parsed);
		} catch(Exception e) {
			TheBetweenlands.LOGGER.warn("Failed to parse tag string {}", string);
		}
	}
	
	protected void processComponents(String string) {
		final StringReader reader = new StringReader(string);
		
		DataComponentTagPredicate.Builder builder = new DataComponentTagPredicate.Builder();
		ItemComponentTagParser parser = new ItemComponentTagParser(reader, new ComponentVisitor() {
			@Override
			public <T> void visitRemovedComponent(DataComponentType<T> componentType) {
				builder.without(componentType);
			}
			
			@Override
			public <T> void visitComponent(DataComponentType<T> componentType, Tag value) {
				builder.expect(componentType, value);
			}
		});
		
		try {
			parser.visitComponents();
		} catch (CommandSyntaxException e) {
			TheBetweenlands.LOGGER.warn("Failed to process components from string {}", string);
			e.printStackTrace();
		}
		
		this.itemComponents.add(builder.build(this.registryAccessCache));
	}
	
	protected void processItem(String string) {
		try {
			final StringReader reader = new StringReader(string);
			
			final int bracketIndex = string.indexOf('[');
			final int closingBracketIndex = string.indexOf('[');
			final int firstColonIndex = string.indexOf(':');
			final int lastColonIndex = string.lastIndexOf(':');
			
			final boolean hasComponents = bracketIndex != -1;
			if(hasComponents && closingBracketIndex == -1) {
				reader.setCursor(string.length() - 1);
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, ']');
			}
			final boolean hasFirstColon = firstColonIndex != -1 && (!hasComponents || firstColonIndex < bracketIndex);
			final boolean hasLastColon = lastColonIndex != -1 && (!hasComponents || lastColonIndex > closingBracketIndex);
			final boolean onlyOneColon = hasFirstColon && hasLastColon && firstColonIndex == lastColonIndex;
			
			// For Debugging
			// TheBetweenlands.LOGGER.info("Processing Item String: {}. Values: {} {} {} {} {} {} {} {}", string, bracketIndex, closingBracketIndex, firstColonIndex, lastColonIndex, hasComponents, hasFirstColon, hasLastColon, onlyOneColon);
			
			ResourceLocation location = null;
			int meta = WILDCARD_VALUE;
			DataComponentTagPredicate componentPredicate = null;
			if(hasComponents) {
				location = ResourceLocation.read(reader);

				DataComponentTagPredicate.Builder builder = new DataComponentTagPredicate.Builder();
				ItemComponentTagParser parser = new ItemComponentTagParser(reader, new ComponentVisitor() {
					@Override
					public <T> void visitRemovedComponent(DataComponentType<T> componentType) {
						builder.without(componentType);
					}
					
					@Override
					public <T> void visitComponent(DataComponentType<T> componentType, Tag value) {
						builder.expect(componentType, value);
					}
				});
				try {
					parser.visitComponents();
				} catch (CommandSyntaxException e) {
					TheBetweenlands.LOGGER.warn("Failed to process components of string {}", string);
					e.printStackTrace();
				}
				componentPredicate = builder.build(this.registryAccessCache);
				
				if(hasLastColon && reader.canRead() && reader.peek() == ':') {
					reader.skip();
					if(reader.peek() == '*') {
						meta = WILDCARD_VALUE;
					} else {
						meta = reader.readInt();
					}
				}
				
			} else {
				if(onlyOneColon) {  // steak:1, but could also be thebetweenlands:16612, maybe check somehow?
					int i = reader.getCursor();
					// Resource locations don't read '*', so we have a separate check
					if(string.charAt(firstColonIndex + 1) == '*') {
						meta = WILDCARD_VALUE;
						location = ResourceLocation.withDefaultNamespace(string.substring(i, firstColonIndex));
						reader.setCursor(firstColonIndex + 2);
					} else {
						location = ResourceLocation.read(reader);
						try {
							meta = Integer.parseInt(location.getPath());
							location = ResourceLocation.withDefaultNamespace(string.substring(i, firstColonIndex));
						} catch(NumberFormatException e) {
							meta = WILDCARD_VALUE;
						}
					}
				} else if(hasFirstColon) { // minecraft:steak
					location = readResourceLocationSmart(reader);
					reader.skipWhitespace();
					if(hasLastColon && reader.canRead() && reader.peek() == ':') { // minecraft:steak:1
						reader.skip();
						reader.skipWhitespace();
						if(reader.peek() == '*') {
							reader.skip();
							meta = WILDCARD_VALUE;
						} else {
							meta = reader.readInt();
						}
					}
				} else { // no colon (e.g. "steak")
					location = ResourceLocation.withDefaultNamespace(reader.getRemaining());
				}
			}
			
			if(!itemList.containsKey(location)) {
				itemList.put(location, new HashSet<ItemListProperty.ComparableItemStack>());
			}
			ComparableItemStack stack = new ComparableItemStack(location, meta, componentPredicate);
			addToSet(stack, itemList.get(location));
		} catch (CommandSyntaxException e) {
			TheBetweenlands.LOGGER.warn("Failed to process item string of {}", string);
			TheBetweenlands.LOGGER.error(e);
		}
	}

	protected static ResourceLocation readResourceLocationSmart(StringReader reader) {
		return readResourceLocationSmart(reader, ':');
	}
	
	protected static ResourceLocation readResourceLocationSmart(StringReader reader, char delimiter) {
		boolean hasBeenDelimited = false;
		final int i = reader.getCursor();
		
		char c;
		while(reader.canRead() && ((c = reader.peek()) == delimiter || ResourceLocation.isAllowedInResourceLocation(c))) {
			if(c == delimiter) {
				if(hasBeenDelimited) break;
				else hasBeenDelimited = true;
			}
			reader.skip();
		}
		
		return ResourceLocation.bySeparator(reader.getString().substring(i, reader.getCursor()), delimiter);
	}
	
//	protected ObjectIntImmutablePair<ResourceLocation> parseItemBaseString(String string) {
//		final int lastColon = string.lastIndexOf(':');
//		String firstPart = string;
//		String lastPart = "";
//		if(lastColon != -1) {
//			firstPart = string.substring(0, lastColon);
//			lastPart = string.substring(lastColon + 1);
//		}
//		
//		int meta = WILDCARD_VALUE;
//
//		if(!lastPart.isBlank()) {
//			if(lastPart.equals("*")) {
//				meta = WILDCARD_VALUE;
//			} else try {
//				meta = Integer.parseInt(lastPart);
//			} catch(NumberFormatException exception) {
//				firstPart = String.join(firstPart, ":", lastPart);
//			}
//		}
//		
//		final ResourceLocation location = ResourceLocation.tryParse(firstPart);
//		
//		return new ObjectIntImmutablePair<>(location, meta);
//	}
//	
//	private void processItemWithComponents(String string) {
//		final ResourceLocation location;
//		final int meta;
//		final String componentString;
//		{
//			final int semicolonindex = string.indexOf(';');
//			
//			final ObjectIntImmutablePair<ResourceLocation> data = parseItemBaseString(string.substring(0, semicolonindex));
//			location = data.left();
//			meta = data.rightInt();
//			
//			componentString = string.substring(semicolonindex + 1);
//		}
//		
//		DataComponentTagPredicate.Builder builder = new DataComponentTagPredicate.Builder();
//		ItemComponentTagParser parser = new ItemComponentTagParser(componentString, new ComponentVisitor() {
//			@Override
//			public <T> void visitRemovedComponent(DataComponentType<T> componentType) {
//				builder.without(componentType);
//			}
//			
//			@Override
//			public <T> void visitComponent(DataComponentType<T> componentType, Tag value) {
//				builder.expect(componentType, value);
//			}
//		});
//		try {
//			parser.visitComponents();
//		} catch (CommandSyntaxException e) {
//			TheBetweenlands.LOGGER.warn("Failed to process item list config option of {}", string);
//			e.printStackTrace();
//		}
//		
//		if(!itemList.containsKey(location)) {
//			itemList.put(location, new HashSet<ItemListProperty.ComparableItemStack>());
//		}
//
//		ComparableItemStack stack = new ComparableItemStack(location, meta, builder.build(this.registryAccessCache));
//		addToSet(stack, itemList.get(location));
//		
//	}
	
	private void addToSet(ComparableItemStack comparable, Set<ComparableItemStack> set) {
		if(comparable.meta == WILDCARD_VALUE) {
			set.removeIf(c -> comparable.equalsWildcard(c));
		}
		if(!set.contains(comparable))
			set.add(comparable);
	}

	public boolean isListed(ItemStack stack) {
		for(TagKey<Item> tag : itemTagCache) {
			if(stack.is(tag)) return true;
		}

		for(DataComponentTagPredicate predicate : itemComponents) {
			if(predicate.test(stack)) return true;
		}
		
		ResourceLocation location = stack.getItemHolder().getKey().location();
//		return itemList.containsKey(location) && itemList.get(location).contains(new ComparableItemStack(stack));
		if(!itemList.containsKey(location))
			return false;
		
		Set<ComparableItemStack> set = itemList.get(location);
		for(ComparableItemStack comparable : set) {
			if(comparable.test(stack)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isValidString(Object object) {
		if(!(object instanceof String string)) {
			return false;
		}
		
		if(string.length() == 0) return false;
		
		if(string.charAt(0) == '#') {
			return ResourceLocation.tryParse(string.substring(1)) != null;
		} else {
			// TODO finish, is temp true for testing
			return true;
		}
	}
	
	

//	// Used when making changes to the ItemListProperty comparator
//	// (Config options set to ["minecraft:cooked_beef", "minecraft:diamond_axe:40", "minecraft:diamond_sword[minecraft:food={saturation:10}]"] and ["minecraft:diamond_axe[!minecraft:food]", "minecraft:diamond_sword[minecraft:food={nutrition:10,saturation:9}]"])
//	@SubscribeEvent
//	public static void debugTest(ClientChatEvent event) {
////		final Level level = event.getPlayer().level();
////		BetweenlandsConfig.Overworld.rottenFoodWhitelist.buildCache(level.registryAccess());
////		BetweenlandsConfig.Overworld.rottenFoodBlacklist.buildCache(level.registryAccess());
//
//		ItemStack stack = new ItemStack(Items.COOKED_BEEF, 1);
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:cooked_beef");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: True, False
//	
//		stack = new ItemStack(Items.DIAMOND_AXE, 1);
//		stack.set(DataComponents.DAMAGE, 40);
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_axe[minecraft:damage=40]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: True, True
//
//		stack = new ItemStack(Items.DIAMOND_AXE, 1);
//		stack.set(DataComponents.DAMAGE, 40);
//		stack.set(DataComponents.FOOD, new FoodProperties(10, 10.0F, false, 1.6F, Optional.empty(), List.of()));
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_axe[minecraft:damage=40,minecraft:food={nutrition:10,saturation:10}]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: True, False
//
//		stack = new ItemStack(Items.DIAMOND_AXE, 1);
//		stack.set(DataComponents.FOOD, new FoodProperties(10, 10.0F, false, 1.6F, Optional.empty(), List.of()));
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_axe[minecraft:food={nutrition:10,saturation:10}]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: False, False
//
//		stack = new ItemStack(Items.DIAMOND_SWORD, 1);
//		stack.set(DataComponents.FOOD, new FoodProperties(9, 10.0F, false, 1.6F, Optional.empty(), List.of()));
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_sword[minecraft:food={nutrition:9,saturation:10}]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: True, False
//
//		stack = new ItemStack(Items.DIAMOND_SWORD, 1);
//		stack.set(DataComponents.FOOD, new FoodProperties(10, 10.0F, false, 1.6F, Optional.empty(), List.of()));
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_sword[minecraft:food={nutrition:10,saturation:10}]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: True, False
//
//		stack = new ItemStack(Items.DIAMOND_SWORD, 1);
//		stack.set(DataComponents.FOOD, new FoodProperties(10, 9.0F, false, 1.6F, Optional.empty(), List.of()));
//		TheBetweenlands.LOGGER.info("TESTINGINGING ItemListProperty: minecraft:diamond_sword[minecraft:food={nutrition:10,saturation:9}]");
//		TheBetweenlands.LOGGER.info("Whitelist: {}, Blacklist: {}", BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack), BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
////		Expect: False, True
//
//	}
}
