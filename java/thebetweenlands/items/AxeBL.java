package thebetweenlands.items;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import thebetweenlands.client.event.DecayTextureStitchHandler;
import thebetweenlands.utils.IDecayFood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class AxeBL extends ItemAxe implements IDecayable {
	private IIcon decayIcon;

	public AxeBL(ToolMaterial material) {
		super(material);
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if (pass == 1) {
			// TODO: change how decayable item icons are rendered
			float decay = 127F / 255;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glColor4f(1, 1, 1, decay);
			return decayIcon;
		}
		return super.getIcon(stack, pass);
	}

	@Override
	public IIcon[] getIcons() {
		return new IIcon[] { itemIcon };
	}

	@Override
	public void setDecayIcons(IIcon[] decayIcons) {
		decayIcon = decayIcons[0];
	}
}