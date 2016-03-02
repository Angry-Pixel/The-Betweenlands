package thebetweenlands.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiMainMenu;

@SideOnly(Side.CLIENT)
public class GuiBLMainMenu extends GuiMainMenu {
    public void drawScreen(int mouseX, int mouseY, float delta) {
        super.drawScreen(mouseX, mouseY, delta);
        fontRendererObj.drawString("Break all the things!™", 2, 2, 0xFFFFFF);
    }
}
