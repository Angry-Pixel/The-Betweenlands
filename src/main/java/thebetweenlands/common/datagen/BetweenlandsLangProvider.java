package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import thebetweenlands.common.TheBetweenlands;

public class BetweenlandsLangProvider extends LanguageProvider {
	public BetweenlandsLangProvider(PackOutput output) {
		super(output, TheBetweenlands.ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.add("itemGroup.thebetweenlands.blocks", "The Betweenlands Blocks");
		this.add("itemGroup.thebetweenlands.items", "The Betweenlands Items");
		this.add("itemGroup.thebetweenlands.gear", "The Betweenlands Gear");
		this.add("itemGroup.thebetweenlands.specials", "The Betweenlands Specials");
		this.add("itemGroup.thebetweenlands.plants", "The Betweenlands Plants");
		this.add("itemGroup.thebetweenlands.herblore", "The Betweenlands Herblore");

		this.add("item.thebetweenlands.amate_map.invalid", "Can't create an Amate Map outside of The Betweenlands");
	}
}
