package thebetweenlands.common.herblore.elixir;

import javax.annotation.Nullable;

import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;

public class ElixirRecipe {
	public final String name;
	public final int infusionFinishedColor;
	public final int infusionGradient;
	public final int infusionFailedColor;
	public final int idealInfusionTime;
	public final int infusionTimeVariation;
	public final int baseDuration;
	public final int durationModifier;
	public final int negativeBaseDuration;
	public final int negativeDurationModifier;

	public final ElixirEffect positiveElixir;
	public final ElixirEffect negativeElixir;
	public final IAspectType strengthAspect;
	public final IAspectType durationAspect;
	public final IAspectType[] aspects;

	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, int baseDuration, int durationModifier, int negativeBaseDuration, int negativeDurationModifier, 
			ElixirEffect positiveElixir, ElixirEffect negativeElixir, IAspectType[] aspects) {
		this(name, infusionGradient, infusionFinishedColor, infusionFailedColor, idealInfusionTime, 
				infusionTimeVariation, baseDuration, durationModifier, negativeBaseDuration, negativeDurationModifier,
				positiveElixir, negativeElixir, null, null, aspects);
	}

	public ElixirRecipe(String name, int infusionGradient, int infusionFinishedColor, int infusionFailedColor, int idealInfusionTime, 
			int infusionTimeVariation, int baseDuration, int durationModifier, int negativeBaseDuration, int negativeDurationModifier, 
			ElixirEffect positiveElixir, ElixirEffect negativeElixir, IAspectType strengthAspect, IAspectType durationAspect, IAspectType[] aspects) {
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
		this.durationModifier = durationModifier;
		this.negativeBaseDuration = negativeBaseDuration;
		this.negativeDurationModifier = negativeDurationModifier;
		this.aspects = aspects;
	}

	public float[] getRGBA(int color) {
		float a = (float)(color >> 24 & 255) / 255.0F;
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		return new float[] {r, g, b, a};
	}

	public static float[] getInfusionColor(@Nullable ElixirRecipe recipe, int infusionTime) {
		if(recipe != null) {
			if(infusionTime > recipe.idealInfusionTime + recipe.infusionTimeVariation) {
				return recipe.getRGBA(recipe.infusionFailedColor);
			} else if(infusionTime > recipe.idealInfusionTime - recipe.infusionTimeVariation
					&& infusionTime <= recipe.idealInfusionTime + recipe.infusionTimeVariation) {
				return recipe.getRGBA(recipe.infusionFinishedColor);
			} else {
				float startR = 0.2F;
				float startG = 0.6F;
				float startB = 0.4F;
				float startA = 0.9F;
				float[] targetColor = recipe.getRGBA(recipe.infusionGradient);
				int targetTime = recipe.idealInfusionTime - recipe.infusionTimeVariation;
				float infusingPercentage = (float)infusionTime / (float)targetTime;
				float interpR = startR + (targetColor[0] - startR) * infusingPercentage;
				float interpG = startG + (targetColor[1] - startG) * infusingPercentage;
				float interpB = startB + (targetColor[2] - startB) * infusingPercentage;
				float interpA = startA + (targetColor[3] - startA) * infusingPercentage;
				return new float[]{interpR, interpG, interpB, interpA};
			}
		} else {
			return new float[]{0.8F, 0.0F, 0.8F, 1.0F};
		}
	}
}