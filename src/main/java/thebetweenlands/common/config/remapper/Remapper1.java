package thebetweenlands.common.config.remapper;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Remaps any config file with no version to 1.0.0
 */
public class Remapper1 extends ConfigRemapper {
	private String currentCategory;
	private Property currentProperty;
	private Configuration currentRemappedValues;

	public Remapper1() {
		super(null, "1.0.0");
	}

	@Override
	public boolean remap(String category, Property prop, Configuration remappedValues) {
		this.currentCategory = category;
		this.currentProperty = prop;
		this.currentRemappedValues = remappedValues;

		//Compat
		if(this.doSimpleRemap("compat", "JEI - Show Non BL Gem Recipes", "compatibility", "show_non_bl_gem_recipes")) return true;

		//Debug
		if(this.doSimpleRemap("debug", "Debug mode", "debug", "debug_mode")) return true;
		if(this.doSimpleRemap("debug", "Model loader debug", "debug", "debug_model_loader")) return true;

		//General
		if(this.doSimpleRemap("general", "Betweenlands Main Menu", "general", "bl_main_menu")) return true;
		if(this.doSimpleRemap("general", "Caving Rope Indicator", "general", "caving_rope_indicator")) return true;
		if(this.doSimpleRemap("general", "Food Sickness", "general", "use_food_sickness")) return true;
		if(this.doSimpleRemap("general", "Override Conflicting Recipes", "general", "override_conflicting_vanilla_recipes")) return true;
		if(this.doSimpleRemap("general", "Rotten Food Whitelist", "general", "rotten_food_whitelist")) return true;
		if(this.doSimpleRemap("general", "Rowboat view", "general", "rowboat_view")) return true;
		if(this.doSimpleRemap("general", "Show Non BL Buckets", "compatibility", "show_non_bl_fluids")) return true;

		//Mob spawning
		if(this.doSimpleRemap("mob spawning", "Max. entities per loaded area", "mob_spawning", "max_entities_per_loaded_area")) return true;
		if(this.doSimpleRemap("mob spawning", "Max. entities per world", "mob_spawning", "hard_entity_limit")) return true;

		//Online environment event overrides
		if(this.doSimpleRemap("online environment event overrides", "Check Interval", "online_event_overrides", "check_interval")) return true;
		if(this.doSimpleRemap("online environment event overrides", "Default Remote Reset Time", "online_event_overrides", "default_remote_reset_time")) return true;
		if(this.doSimpleRemap("online environment event overrides", "Enabled", "online_event_overrides", "online_event_overrides_enabled")) return true;
		if(this.doSimpleRemap("online environment event overrides", "Failed Recheck Count", "online_event_overrides", "failed_recheck_count")) return true;
		if(this.doSimpleRemap("online environment event overrides", "Failed Recheck Interval", "online_event_overrides", "failed_recheck_interval")) return true;

		//Rendering
		if(this.doSimpleRemap("rendering", "Full brightness blocks", "rendering", "fullbright_blocks")) return true;
		if(this.doSimpleRemap("rendering", "Sky texture resolution", "rendering", "sky_texture_resolution")) return true;
		if(this.doSimpleRemap("rendering", "Use shaders for rendering", "rendering", "use_shader")) return true;
		if(this.doSimpleRemap("rendering", "Wisp Rendering Quality (0-100)", "rendering", "wisp_quality")) return true;

		//World and dimension
		if(this.doSimpleRemap("world and dimension", "Dimension brightness (0-100)", "world_and_dimension", "dimension_brightness")) return true;
		if(this.doSimpleRemap("world and dimension", "Enable Seasonal Events", "world_and_dimension", "enable_seasonal_events")) return true;
		if(this.doSimpleRemap("world and dimension", "Frequency of Druid Circles", "world_and_dimension", "druid_circle_frequency")) return true;
		if(this.doSimpleRemap("world and dimension", "The Betweenlands Dimension ID", "world_and_dimension", "dimension_id")) return true;

		return false;
	}

	private boolean doSimpleRemap(String category, String property, String newCategory, String newProperty) {
		if(category.equals(this.currentCategory) && property.equals(this.currentProperty.getName())) {
			if(this.currentProperty.isList()) {
				this.currentRemappedValues.get(newCategory, newProperty, this.currentProperty.getStringList()).set(this.currentProperty.getStringList());
			} else {
				this.currentRemappedValues.get(newCategory, newProperty, this.currentProperty.getString()).set(this.currentProperty.getString());
			}
			return true;
		}
		return false;
	}
}
