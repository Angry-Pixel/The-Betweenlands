package thebetweenlands.common.recipe.custom;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;

public class CustomAnimatorRecipe extends CustomRecipe {
	public CustomAnimatorRecipe() {
		super("animator", ImmutableMap.of("input/item", RecipeArg.ITEM_INPUT, "input/fuel", RecipeArg.INT, "input/life", RecipeArg.INT), ImmutableMap.of("output", RecipeArg.ITEM_OUTPUT, "output_entity", RecipeArg.ENTITY, "rendered_entity", RecipeArg.STRING));
	}

	@Override
	public void register() {
		ItemStack input = this.get("input/item", RecipeArg.ITEM_INPUT).get().create();
		Optional<IRecipeEntry<ItemStack>> outputItem = this.get("output", RecipeArg.ITEM_OUTPUT);
		Optional<IRecipeEntry<Entity>> outputEntity = this.get("output_entity", RecipeArg.ENTITY);
		Optional<IRecipeEntry<String>> renderedEntity = this.get("rendered_entity", RecipeArg.STRING);
		int fuel = this.get("input/fuel", RecipeArg.INT).get().create();
		int life = this.get("input/life", RecipeArg.INT).get().create();

		if(!outputItem.isPresent() && !outputEntity.isPresent()) {
			this.throwException("At least one output item or entity must be specified");
		}

		ItemStack output = outputItem.isPresent() ? outputItem.get().create() : null;
		AnimatorRecipe recipe;

		if(outputEntity.isPresent()) {
			recipe = new AnimatorRecipe(input, fuel, life, output) {
				@Override
				public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof TileEntityAnimator) {
						TileEntityAnimator animator = (TileEntityAnimator) te;
						animator.setInventorySlotContents(0, output == null ? null : output.copy());
						Entity entity = outputEntity.get().create(world, new Vec3d(pos), null);
						if(entity != null) {
							entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0, 0);
							world.spawnEntityInWorld(entity);
							return false;
						}
						return true;
					}
					return true;
				}
			};
			if(renderedEntity.isPresent()) {
				recipe.setRenderEntity(renderedEntity.get().create());
			}
		} else {
			recipe = new AnimatorRecipe(input, fuel, life, output);
		}

		AnimatorRecipe.addRecipe(recipe);
	}
}
