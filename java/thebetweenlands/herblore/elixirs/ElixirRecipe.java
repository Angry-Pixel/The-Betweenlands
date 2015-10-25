package thebetweenlands.herblore.elixirs;

import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.IAspect;

public class ElixirRecipe {
	public final String name;
	public final int infusionFinishedColor;
	public final int infusionGradient;
	public final int infusionFailedColor;
	public final int idealInfusionTime;
	public final int infusionTimeVariation;
	public final ItemStack positiveElixir;
	public final ItemStack negativeElixir;
	public final IAspect strengthAspect;
	public final IAspect durationAspect;
	public final IAspect[] aspects;

	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, ItemStack positiveElixir, ItemStack negativeElixir, IAspect[] aspects) {
		this(name, infusionGradient, infusionFinishedColor, infusionFailedColor, idealInfusionTime, infusionTimeVariation, positiveElixir, negativeElixir, null, null, aspects);
	}
	
	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, ItemStack positiveElixir, ItemStack negativeElixir, IAspect strengthAspect, IAspect durationAspect, IAspect[] aspects) {
		this.name = name;
		this.infusionGradient = infusionGradient;
		this.infusionFinishedColor = infusionFinishedColor;
		this.infusionFailedColor = infusionFailedColor;
		this.idealInfusionTime = idealInfusionTime;
		this.infusionTimeVariation = infusionTimeVariation;
		this.positiveElixir = positiveElixir;
		this.negativeElixir = negativeElixir;
		this.strengthAspect = strengthAspect;
		this.durationAspect = durationAspect;
		this.aspects = aspects;
	}

	public float[] getRGBA(int color) {
		float a = (float)(color >> 24 & 255) / 255.0F;
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		return new float[] {r, g, b, a};
	}
}