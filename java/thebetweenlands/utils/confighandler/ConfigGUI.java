package thebetweenlands.utils.confighandler;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import thebetweenlands.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class ConfigGUI
        extends GuiConfig
{
	public ConfigGUI(GuiScreen parent) {
		super(parent, getElements(), ModInfo.ID, ModInfo.ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.INSTANCE.config.toString()));
	}

	@SuppressWarnings({ "rawtypes" })
	private static List<IConfigElement> getElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>(ConfigHandler.usedCategories.length);
		for( String category : ConfigHandler.usedCategories ) {
            list.add(new ConfigElement(ConfigHandler.INSTANCE.config.getCategory(category.toLowerCase())));
        }

		return list;
	}
}
