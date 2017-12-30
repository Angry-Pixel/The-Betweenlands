package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;

public class AnimatorRecipe implements IAnimatorRecipe {
	private final ItemStack input;
	private int requiredFuel, requiredLife;
	private ItemStack result = ItemStack.EMPTY;
	private Class<? extends Entity> spawnEntity = null;
	private ResourceLocation renderEntity = null;
	private Entity renderEntityInstance = null;
	private boolean closeOnFinish = false;
	private ResourceLocation lootTable = null;


	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife) {
		this.input = input;
		this.requiredFuel = requiredFuel;
		this.requiredLife = requiredLife;
	}

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, ResourceLocation lootTable) {
		this.input = input;
		this.requiredFuel = requiredFuel;
		this.requiredLife = requiredLife;
		this.lootTable = lootTable;
	}

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, ItemStack result) {
		this(input, requiredFuel, requiredLife);
		this.result = result;
	}

	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, Class<? extends Entity> result) {
		this(input, requiredFuel, requiredLife);
		this.spawnEntity = result;
		this.closeOnFinish = true;
	}
	
	public AnimatorRecipe(ItemStack input, int requiredFuel, int requiredLife, ItemStack result, Class<? extends Entity> resultEntity) {
		this(input, requiredFuel, requiredLife);
		this.spawnEntity = resultEntity;
		this.result = result;
		this.closeOnFinish = true;
	}

	public AnimatorRecipe setRenderEntity(ResourceLocation entity) {
		this.renderEntity = entity;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Entity getRenderEntity(ItemStack stack) {
		if(this.renderEntity != null && this.renderEntity.toString().length() > 0) {
			if(this.renderEntityInstance == null) {
				this.renderEntityInstance = EntityList.createEntityByIDFromName(this.renderEntity, Minecraft.getMinecraft().world);
			}
			return this.renderEntityInstance;
		}
		return null;
	}

	@Override
	public ItemStack getResult(ItemStack stack) {
		return this.result;
	}

	@Override
	public Class<? extends Entity> getSpawnEntityClass(ItemStack stack) {
		return this.spawnEntity;
	}

	/*** Also used to spawn entities from animator once animated
	 * @param world
	 * @param pos
	 * @return
	 */
	@Override
	public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
		return ItemStack.EMPTY;
	}

	/**
	 * Called when the animator has finished animating and is right clicked.
	 * Return true if GUI should be opened on first click
	 * @param world
	 * @param pos
	 */
	@Override
	public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityAnimator) {
			TileEntityAnimator animator = (TileEntityAnimator) te;
			Class<? extends Entity> spawnEntity = this.getSpawnEntityClass(stack);
			if(spawnEntity != null) {
				Entity entity = null;
				try {
					entity = (Entity)spawnEntity.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
				} catch (Exception exception) {
					exception.printStackTrace();
					return true;
				}
				entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0, 0);
				world.spawnEntity(entity);
				animator.setInventorySlotContents(0, ItemStack.EMPTY);
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * Returns whether the GUI should close when the animator has finished
	 * @return
	 */
	@Override
	public boolean getCloseOnFinish(ItemStack stack) {
		return this.closeOnFinish;
	}

	/**
	 * Sets whether the GUI should close when the animator has finished
	 * @param close
	 * @return
	 */
	public AnimatorRecipe setCloseOnFinish(boolean close) {
		this.closeOnFinish = close;
		return this;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return this.input.getItemDamage() == OreDictionary.WILDCARD_VALUE ? this.input.getItem() == stack.getItem() : this.input.getItem() == stack.getItem() && this.input.getItemDamage() == stack.getItemDamage();
	}

	@Override
	public int getRequiredFuel(ItemStack stack) {
		return this.requiredFuel;
	}

	@Override
	public int getRequiredLife(ItemStack stack) {
		return this.requiredLife;
	}

	private static final List<IAnimatorRecipe> RECIPES = new ArrayList<IAnimatorRecipe>();

	public static void addRecipe(IAnimatorRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static void removeRecipe(IAnimatorRecipe recipe) {
		RECIPES.remove(recipe);
	}

	public static List<IAnimatorRecipe> getRecipes() {
		return RECIPES;
	}

	public static IAnimatorRecipe getRecipe(ItemStack input) {
		if(!input.isEmpty()) {
			for(IAnimatorRecipe recipe : AnimatorRecipe.getRecipes()) {
				if(recipe.matchesInput(input)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Deprecated
	public static IAnimatorRecipe getRecipeFromOutput(ItemStack output) {
		return null;
	}


	public ItemStack getInput() {
		return input;
	}

	public ResourceLocation getLootTable() {
		return lootTable;
	}
}
