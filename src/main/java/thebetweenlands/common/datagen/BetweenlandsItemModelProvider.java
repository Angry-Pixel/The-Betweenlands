package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class BetweenlandsItemModelProvider extends ItemModelProvider {
	public BetweenlandsItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	protected void registerModels() {
		this.basicItem(ItemRegistry.RECORD_ANCIENT);
		this.basicItem(ItemRegistry.RECORD_ASTATOS);
		this.basicItem(ItemRegistry.RECORD_BENEATH_A_GREEN_SKY);
		this.basicItem(ItemRegistry.RECORD_BETWEEN_YOU_AND_ME);
		this.basicItem(ItemRegistry.RECORD_CHRISTMAS_ON_THE_MARSH);
		this.otherTextureItem(ItemRegistry.RECORD_DEEP_WATER_THEME, this.modLoc("freshwater_urchin"));
		this.basicItem(ItemRegistry.RECORD_DJ_WIGHTS_MIXTAPE);
		this.basicItem(ItemRegistry.RECORD_HAG_DANCE);
		this.basicItem(ItemRegistry.RECORD_LONELY_FIRE);
		this.basicItem(ItemRegistry.RECORD_ONWARDS);
		this.basicItem(ItemRegistry.RECORD_STUCK_IN_THE_MUD);
		this.basicItem(ItemRegistry.RECORD_THE_EXPLORER);
		this.basicItem(ItemRegistry.RECORD_WANDERING_WISPS);
		this.basicItem(ItemRegistry.RECORD_WATERLOGGED);
		this.basicItem(ItemRegistry.MYSTERIOUS_RECORD);
		this.basicItem(ItemRegistry.GERTS_DONUT);
		this.basicItem(ItemRegistry.AMATE_MAP);
		this.basicItem(ItemRegistry.FILLED_AMATE_MAP);
	}

	public ItemModelBuilder basicItem(DeferredItem<Item> item) {
		return this.basicItem(item.get());
	}

	public ItemModelBuilder otherTextureItem(DeferredItem<Item> item, ResourceLocation texture) {
		return this.getBuilder(item.getId().toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "item/" + texture.getPath()));
	}
}
