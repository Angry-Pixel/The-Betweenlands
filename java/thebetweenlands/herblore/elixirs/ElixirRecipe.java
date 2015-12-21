package thebetweenlands.herblore.elixirs;

import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;

public class ElixirRecipe {
	public final String name;
	public final int infusionFinishedColor;
	public final int infusionGradient;
	public final int infusionFailedColor;
	public final int idealInfusionTime;
	public final int infusionTimeVariation;
	public final int baseDuration;
	public final ElixirEffect positiveElixir;
	public final ElixirEffect negativeElixir;
	public final IAspectType strengthAspect;
	public final IAspectType durationAspect;
	public final IAspectType[] aspects;

	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, int baseDuration, ElixirEffect positiveElixir, ElixirEffect negativeElixir, IAspectType[] aspects) {
		this(name, infusionGradient, infusionFinishedColor, infusionFailedColor, idealInfusionTime, infusionTimeVariation, baseDuration, positiveElixir, negativeElixir, null, null, aspects);
	}
	
	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, int baseDuration, ElixirEffect positiveElixir, ElixirEffect negativeElixir, IAspectType strengthAspect, IAspectType durationAspect, IAspectType[] aspects) {
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
		this.baseDuration = baseDuration;
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