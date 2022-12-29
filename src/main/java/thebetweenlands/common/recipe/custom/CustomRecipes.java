package thebetweenlands.common.recipe.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class CustomRecipes<C> {
	private final String name;
	private final List<C> loadedRecipes = new ArrayList<>();
	private IRecipeRegistrar<C> recipeRegistrar;

	public static class InvalidRecipeException extends RuntimeException {
		private static final long serialVersionUID = -6724435505685374630L;

		public InvalidRecipeException(String msg) {
			super(msg);
		}
	}

	public static interface IRecipeRegistrar<C> {
		/**
		 * Registers the recipe
		 * @param recipe
		 * @return Return true if successful
		 */
		public boolean register(C recipe);

		/**
		 * Unregisters the recipe
		 * @param recipe
		 * @return Return true if successful
		 */
		public boolean unregister(C recipe);
	}

	@FunctionalInterface
	protected static interface IRecipeEntry<T> {
		/**
		 * Creates the result of the argument
		 * @return
		 */
		public default T create() {
			return this.create(null, null, null);
		}

		/**
		 * Creates the result of the argument
		 * @param world
		 * @param pos
		 * @param crafter
		 * @return
		 */
		public T create(@Nullable World world, @Nullable Vec3d pos, @Nullable EntityPlayer crafter);
	}

	protected abstract static class RecipeArg<T> {
		/**
		 * Parses the specified json element
		 * @param element
		 * @return
		 */
		public abstract IRecipeEntry<T> parse(JsonElement element);

		public static final RecipeArg<ItemStack> ITEM_INPUT = new RecipeArg<ItemStack>() {
			@Override
			public IRecipeEntry<ItemStack> parse(JsonElement element) {
				return parseItemStack(element, true);
			}
		};

		public static final RecipeArg<FluidStack> FLUID_INPUT = new RecipeArg<FluidStack>() {
			@Override
			public IRecipeEntry<FluidStack> parse(JsonElement element) {
				return RecipeArg.parseFluidStack(element, true);
			}
		};

		public static final RecipeArg<ItemStack> ITEM_OUTPUT = new RecipeArg<ItemStack>() {
			@Override
			public IRecipeEntry<ItemStack> parse(JsonElement element) {
				return parseItemStack(element, false);
			}
		};

		public static final RecipeArg<FluidStack> FLUID_OUTPUT = new RecipeArg<FluidStack>() {
			@Override
			public IRecipeEntry<FluidStack> parse(JsonElement element) {
				return RecipeArg.parseFluidStack(element, false);
			}
		};

		private static IRecipeEntry<ItemStack> parseItemStack(JsonElement element, boolean input) {
			JsonObject obj = element.getAsJsonObject();
			String id = obj.get("id").getAsString();
			int meta = obj.has("meta") ? obj.get("meta").getAsInt() : (input ? OreDictionary.WILDCARD_VALUE : 0);
			NBTTagCompound nbt = null;
			if(obj.has("nbt")) {
				nbt = NBT.parse(obj.get("nbt")).create();
			}
			int size = obj.has("size") ? obj.get("size").getAsInt() : 1;
			ItemStack stack = new ItemStack(Item.getByNameOrId(id), size, meta);
			stack.setTagCompound(nbt);
			return (world, pos, crafter) -> stack.copy();
		}

		private static IRecipeEntry<FluidStack> parseFluidStack(JsonElement element, boolean input) {
			JsonObject obj = element.getAsJsonObject();
			String id = obj.get("id").getAsString();
			int meta = obj.has("meta") ? obj.get("meta").getAsInt() : (input ? OreDictionary.WILDCARD_VALUE : 0);
			NBTTagCompound nbt = null;
			if(obj.has("nbt")) {
				nbt = NBT.parse(obj.get("nbt")).create();
			}
			int size = obj.has("size") ? obj.get("size").getAsInt() : 1;
			Fluid fluid = FluidRegistry.getFluid(id);
			FluidStack stack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
			stack.tag = nbt;
			return (world, pos, crafter) -> stack.copy();
		}

		public static final RecipeArg<Entity> ENTITY = new RecipeArg<Entity>() {
			@Override
			public IRecipeEntry<Entity> parse(JsonElement element) {
				JsonObject obj = element.getAsJsonObject();
				String id = obj.get("id").getAsString();
				NBTTagCompound nbt = null;
				if(obj.has("nbt")) {
					nbt = NBT.parse(obj.get("nbt")).create();
				} else {
					nbt = new NBTTagCompound();
				}
				nbt.setString("id", id);
				final NBTTagCompound finalNbt = nbt;
				return (world, pos, crafter) -> AnvilChunkLoader.readWorldEntityPos(finalNbt, world, pos.x, pos.y, pos.z, true);
			}
		};

		public static final RecipeArg<NBTTagCompound> NBT = new RecipeArg<NBTTagCompound>() {
			@Override
			public IRecipeEntry<NBTTagCompound> parse(JsonElement element) {
				try {
					NBTTagCompound nbt = JsonToNBT.getTagFromJson(element.getAsString());
					return (world, pos, crafter) -> nbt;
				} catch (NBTException e) {
					throw new RuntimeException(e);
				}
			}
		};

		public static final RecipeArg<Integer> INT = new RecipeArg<Integer>() {
			@Override
			public IRecipeEntry<Integer> parse(JsonElement element) {
				int i = element.getAsInt();
				return (world, pos, crafter) -> i;
			}
		};

		public static final RecipeArg<Boolean> BOOL = new RecipeArg<Boolean>() {
			@Override
			public IRecipeEntry<Boolean> parse(JsonElement element) {
				boolean b = element.getAsBoolean();
				return (world, pos, crafter) -> b;
			}
		};

		public static final RecipeArg<Double> DOUBLE = new RecipeArg<Double>() {
			@Override
			public IRecipeEntry<Double> parse(JsonElement element) {
				double d = element.getAsDouble();
				return (world, pos, crafter) -> d;
			}
		};

		public static final RecipeArg<String> STRING = new RecipeArg<String>() {
			@Override
			public IRecipeEntry<String> parse(JsonElement element) {
				String s = element.getAsString();
				return (world, pos, crafter) -> s;
			}
		};
	}

	private final Map<String, Optional<IRecipeEntry<?>>> loadedEntries = new HashMap<>();
	private final Map<String, RecipeArg<?>> args;
	private final Map<String, RecipeArg<?>> optionalArgs;

	public CustomRecipes(String name, Map<String, RecipeArg<?>> args, Map<String, RecipeArg<?>> optionalArgs) {
		this.name = name;
		this.args = args;
		this.optionalArgs = optionalArgs;
	}

	/**
	 * Parses and loads the recipes
	 * @param array
	 */
	public final void parse(JsonArray array) {
		this.clear();
		array.forEach(element -> {
			this.parseRecipe(element);
			this.loadedRecipes.add(this.load());
		});
		this.recipeRegistrar = this.createRegistrar();
	}

	private final void parseRecipe(JsonElement element) {
		this.loadedEntries.clear();

		for(Entry<String, RecipeArg<?>> entry : this.args.entrySet()) {
			JsonElement entryJson = this.getElement(entry.getKey(), element);
			if(entryJson == null) {
				throw new InvalidRecipeException(String.format("Missing entry %s for recipe %s", entry.getKey(), this.getName()));
			}
			this.loadedEntries.put(entry.getKey(), Optional.of(entry.getValue().parse(entryJson)));
		}

		for(Entry<String, RecipeArg<?>> entry : this.optionalArgs.entrySet()) {
			JsonElement entryJson = this.getElement(entry.getKey(), element);
			if(entryJson != null) {
				this.loadedEntries.put(entry.getKey(), Optional.of(entry.getValue().parse(entryJson)));
			}
		}
	}

	private final JsonElement getElement(String entry, JsonElement parent) {
		String[] path = entry.split("/");
		JsonElement element = parent;
		for(String str : path) {
			if(element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				if(obj.has(str)) {
					element = obj.get(str);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return element;
	}

	/**
	 * Loads the recipe
	 */
	protected abstract C load();

	/**
	 * Creates the recipe registrar
	 * @return
	 */
	protected abstract IRecipeRegistrar<C> createRegistrar();

	/**
	 * Returns the recipe registrar
	 * @return
	 */
	protected final IRecipeRegistrar<C> getRegistrar() {
		return this.recipeRegistrar;
	}

	/**
	 * Registers all loaded recipes
	 */
	public final void registerRecipes() {
		for(C recipe : this.loadedRecipes) {
			this.getRegistrar().register(recipe);
		}
	}

	/**
	 * Unregisters all loaded recipes
	 */
	public final void unregisterRecipes() {
		for(C recipe : this.loadedRecipes) {
			this.getRegistrar().unregister(recipe);
		}
	}

	/**
	 * Returns a list of all loaded recipes
	 * @return
	 */
	public final List<C> getRecipes() {
		return Collections.unmodifiableList(this.loadedRecipes);
	}

	/**
	 * Clears the loaded recipes
	 */
	public final void clear() {
		this.loadedRecipes.clear();
	}

	/**
	 * Throws an invalid recipe exception
	 * @param msg
	 */
	protected final void throwException(String msg) {
		throw new InvalidRecipeException(msg);
	}

	/**
	 * Returns the value of the specified entry
	 * @param id
	 * @param arg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T, F extends RecipeArg<T>> Optional<IRecipeEntry<T>> get(String id, RecipeArg<T> arg) {
		Optional<IRecipeEntry<?>> entry = this.loadedEntries.get(id);
		if(entry != null && entry.isPresent()) {
			return Optional.of((IRecipeEntry<T>) entry.get());
		}
		return Optional.absent();
	}

	/**
	 * Returns the name of this recipe type
	 * @return
	 */
	public final String getName() {
		return this.name;
	}
}
