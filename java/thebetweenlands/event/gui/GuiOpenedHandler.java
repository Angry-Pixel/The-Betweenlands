package thebetweenlands.event.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import thebetweenlands.client.gui.GuiBLMainMenu;

@SideOnly(Side.CLIENT)
public class GuiOpenedHandler {
    public static final GuiOpenedHandler INSTANCE = new GuiOpenedHandler();

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
    	if (event.gui instanceof GuiMainMenu && !(event.gui instanceof GuiBLMainMenu)) {
            event.gui = new GuiBLMainMenu();
        }
    }
}
