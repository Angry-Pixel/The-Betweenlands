package thebetweenlands.client.render.block.crops;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import thebetweenlands.utils.ModelConverter;

@SideOnly(Side.CLIENT)
public class CropRenderer {
	private ModelBase[] cropModels = null;
	private ModelConverter[] convertedModels = null;
	private int[] textureDimensions = null;

	public ModelConverter getCropModel(int meta) {
		return this.convertedModels[meta >> 1];
	}

	public int[] getTextureDimensions(int meta) {
		if (meta <= 7) {
			return new int[]{this.textureDimensions[(meta >> 1)*2], this.textureDimensions[(meta >> 1)*2+1]};
		} else
			return new int[]{this.textureDimensions[8], this.textureDimensions[9]};
	}

	public void setCropModels(ModelBase[] models, int[] textureDimensions) {
		this.cropModels = models;
		this.convertedModels = new ModelConverter[5];
		for(int i = 0; i < this.cropModels.length; i++) {
			this.convertedModels[i] = new ModelConverter(this.cropModels[i], 0.065D, true);
		}
		this.textureDimensions = textureDimensions;
	}
}
