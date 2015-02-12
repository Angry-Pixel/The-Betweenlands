package thebetweenlands.entities.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.mobs.models.ModelDarkDruid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDarkDruid extends RenderLiving {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/darkDruid.png");

    public RenderDarkDruid() {
        super(new ModelDarkDruid(), 0.7F);
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}