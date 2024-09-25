package thebetweenlands.common.config;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BetweenlandsCommonConfig {

	public final OverworldItems OVERWORLD = new OverworldItems();
	
	public BetweenlandsCommonConfig(ModConfigSpec.Builder builder) {
		builder.comment("Configure handling for items outside of The Betweenlands").translation("config.thebetweenlands.general").push("Overworld");
		{
			OVERWORLD.rottenFoodWhitelist = builder
					.comment("Items that should never be converted to rotten food")
					.translation("config.thebetweenlands.rotten_food_whitelist")
					.defineListAllowEmpty("rottenFoodWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.rottenFoodBlacklist = builder
					.comment("Items that should be converted to Rotten Food inside The Betweenlands")
					.translation("config.thebetweenlands.rotten_food_blacklist")
					.defineListAllowEmpty("rottenFoodBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			
			
			OVERWORLD.taintingWhitelist = builder
					.comment("Items that should never be converted to Tainted Potions")
					.translation("config.thebetweenlands.tainting_whitelist")
					.defineListAllowEmpty("taintingWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.taintingBlacklist = builder
					.comment("Items that should be converted to Tainted Potions inside The Betweenlands")
					.translation("config.thebetweenlands.tainting_blacklist")
					.defineListAllowEmpty("taintingBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			
			
			OVERWORLD.fireToolWhitelist = builder
					.comment("Fire tools that can activate in The Betweenlands")
					.translation("config.thebetweenlands.fire_tool_whitelist")
					.defineListAllowEmpty("fireToolWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.fireToolBlacklist = builder
					.comment("Fire tools that can't activate in The Betweenlands")
					.translation("config.thebetweenlands.fire_tool_blacklist")
					.defineListAllowEmpty("fireToolBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			
			
			OVERWORLD.fertilizerWhitelist = builder
					.comment("Fertilizer items that work in The Betweenlands")
					.translation("config.thebetweenlands.fertilizer_whitelist")
					.defineListAllowEmpty("fertilizerWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.fertilizerBlacklist = builder
					.comment("Fertilizer items that don't work in The Betweenlands")
					.translation("config.thebetweenlands.fertilizer_blacklist")
					.defineListAllowEmpty("fertilizerBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			

			OVERWORLD.toolWeaknessWhitelist = builder
					.comment("Tools that should bypass tool weakening")
					.translation("config.thebetweenlands.tool_weakness_whitelist")
					.defineListAllowEmpty("toolWeaknessWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.toolWeaknessBlacklist = builder
					.comment("Tools that should be weakened inside The Betweenlands")
					.translation("config.thebetweenlands.tool_weakness_blacklist")
					.defineListAllowEmpty("toolWeaknessBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			
			
			OVERWORLD.torchWhitelist = builder
					.comment("Torches that shouldn't be extinguished inside The Betweenlands")
					.translation("config.thebetweenlands.torch_whitelist")
					.defineListAllowEmpty("torchWhitelist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);

			OVERWORLD.torchBlacklist = builder
					.comment("Torches that should be extinguished inside The Betweenlands")
					.translation("config.thebetweenlands.torch_blacklist")
					.defineListAllowEmpty("torchBlacklist", new ArrayList<String>(), () -> "", ItemListProperty::isValidString);
		}
		builder.pop();
	}
	
	public static final class OverworldItems {
		public ModConfigSpec.ConfigValue<List<? extends String>> rottenFoodWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> rottenFoodBlacklist;

		public ModConfigSpec.ConfigValue<List<? extends String>> taintingWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> taintingBlacklist;

		public ModConfigSpec.ConfigValue<List<? extends String>> fireToolWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> fireToolBlacklist;

		public ModConfigSpec.ConfigValue<List<? extends String>> fertilizerWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> fertilizerBlacklist;

		public ModConfigSpec.ConfigValue<List<? extends String>> toolWeaknessWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> toolWeaknessBlacklist;
		
		public ModConfigSpec.ConfigValue<List<? extends String>> torchWhitelist;
		public ModConfigSpec.ConfigValue<List<? extends String>> torchBlacklist;
	}
}
