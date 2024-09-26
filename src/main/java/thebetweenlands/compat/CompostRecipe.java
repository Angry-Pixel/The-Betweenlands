package thebetweenlands.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record CompostRecipe(Item input, int time, int amount, ResourceLocation uid) {
}
