package thebetweenlands.entities.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDarkDruid extends RenderLiving {
    private static final ResourceLocation texture = new ResourceLocation(ModInfo.ID.toLowerCase() + ":" + "textures/mobs/DarkDruid.png");

    public RenderDarkDruid(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return texture;
	}
}