package thebetweenlands.client.render.model.loader;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public abstract class LoaderArgs {
	/**
	 * Returns the name of this loader argument
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns the model to load based on the specified arguments.
	 * @param original
	 * @param location
	 * @param arg
	 * @return
	 */
	public abstract IModel loadModel(IModel original, ResourceLocation location, String arg);

	/**
	 * Returns a replacement for the specified resource location and baked model.
	 * <p><b>Do NOT return an {@link IBakedModel} from an {@link IModel} that wasn't obtained through {@link ModelLoaderRegistry#getModel(ResourceLocation)} at some point!</b>
	 * @param location
	 * @param original
	 * @return
	 */
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		return null;
	}

	/**
	 * Throws an {@link IllegalArgumentException}
	 * @param reason
	 */
	public final void throwInvalidArgs(String reason) {
		throw new IllegalArgumentException(String.format("Illegal arguments for %s. Reason: %s", this.getName(), reason));
	}
	
	/**
	 * Throws an {@link IllegalArgumentException}
	 * @param reason
	 * @param cause
	 */
	public final void throwInvalidArgs(String reason, Exception cause) {
		throw new IllegalArgumentException(String.format("Illegal arguments for %s. Reason: %s", this.getName(), reason), cause);
	}
}
