package thebetweenlands.client.gui;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonNoClickSound extends GuiButton {

	public GuiButtonNoClickSound(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
	
    public GuiButtonNoClickSound(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

	@Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        // shush your clicky noises up!
    }
}
