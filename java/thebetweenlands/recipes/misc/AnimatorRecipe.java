package thebetweenlands.recipes.misc;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityAnimator;

public class AnimatorRecipe {
	public final ItemStack input;
	public final int requiredFuel, requiredLife;
	private ItemStack result = null;
	private Class<? extends Entity> spawnEntity = null;
	private String renderEntity = null;
	private Entity renderEntityInstance = null;

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife) {
		this.input = input;
		this.requiredFuel = requiredFuel;
		this.requiredLife = requiredLife;
	}

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, ItemStack result) {
		this(input, requiredFuel, requiredLife);
		this.result = result;
	}

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, Class<? extends Entity> result) {
		this(input, requiredFuel, requiredLife);
		this.spawnEntity = result;
	}

	public AnimatorRecipe setRenderEntity(String entity) {
		this.renderEntity = entity;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public Entity getRenderEntity() {
		if(this.renderEntity != null && this.renderEntity.length() > 0) {
			if(this.renderEntityInstance == null) {
				Entity entity = EntityList.createEntityByName(this.renderEntity, (World)null);
				this.renderEntityInstance = entity;
			}
			return this.renderEntityInstance;
		}
		return null;
	}

	public ItemStack getResult() {
		return this.result;
	}

	public Class<? extends Entity> getSpawnEntityClass() {
		return this.spawnEntity;
	}

	/**
	 * Called when the item is animated. Can return the resulting ItemStack (overrides {@link AnimatorRecipe#getResult()}).
	 * Also used to spawn entities from animator once animated
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public ItemStack onAnimated(World world, int x, int y, int z) {
		return null;
	}

	/**
	 * Called when the animator has finished animating and is right clicked.
	 * Return true if GUI should be opened on first click
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public boolean onRetrieved(TileEntityAnimator tile, World world, int x, int y, int z) {
		Class<? extends Entity> spawnEntity = this.getSpawnEntityClass();
		if(spawnEntity != null) {
			Entity entity = null;
			try {
				entity = (Entity)spawnEntity.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
			} catch (Exception exception) {
				exception.printStackTrace();
				return true;
			}
			entity.setLocationAndAngles(x + 0.5D, y + 1.0D, z + 0.5D, 0, 0);
			world.spawnEntityInWorld(entity);
			tile.setInventorySlotContents(0, null);
			return false;
		}
		return true;
	}

	private static final List<AnimatorRecipe> RECIPES = new ArrayList<AnimatorRecipe>();

	public static void addRecipe(AnimatorRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static List<AnimatorRecipe> getRecipes() {
		return RECIPES;
	}

	public static AnimatorRecipe getRecipe(ItemStack input) {
		for(AnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
			if(input.isItemEqual(recipe.input)) {
				return recipe;
			}
		}
		return null;
	}

	public static AnimatorRecipe getRecipeFromOutput(ItemStack output) {
		for(AnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
			if(recipe.result != null && output.isItemEqual(recipe.result)) {
				return recipe;
			}
		}
		return null;
	}
}
