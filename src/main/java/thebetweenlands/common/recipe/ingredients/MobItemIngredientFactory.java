package thebetweenlands.common.recipe.ingredients;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import thebetweenlands.common.registries.ItemRegistry;

public class MobItemIngredientFactory implements IIngredientFactory {
	private static class MobItemIngredient extends Ingredient {
		private final Class<? extends Entity> entityCls;

		private MobItemIngredient(ItemStack stack, Class<? extends Entity> entityCls) {
			super(stack);
			this.entityCls = entityCls;
		}

		@Override
		public boolean isSimple() {
			return false;
		}

		@Override
		public boolean apply(ItemStack other) {
			return super.apply(other) && ItemRegistry.CRITTER.isCapturedEntity(other, this.entityCls);
		}
	}

	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		ResourceLocation id = new ResourceLocation(JsonUtils.getString(json, "entity"));
		Class<? extends Entity> entityCls = EntityList.getClass(id);

		if(entityCls == null) {
			throw new JsonSyntaxException("Entity with ID '" + id + "' does not exist");
		}

		NBTTagCompound nbt = null;
		if(json.has("nbt")) {
			try {
				nbt = JsonToNBT.getTagFromJson(json.get("nbt").getAsString());
			} catch(NBTException ex) {
				throw new JsonSyntaxException("Invalid NBT", ex);
			}
		}

		return new MobItemIngredient(ItemRegistry.CRITTER.capture(entityCls, nbt), entityCls);
	}
}
