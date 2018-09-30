package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.entity.layer.LayerTarBeastEffects;
import thebetweenlands.client.render.model.entity.ModelTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
	

@SideOnly(Side.CLIENT)
public class RenderTarBeast extends RenderLiving<EntityTarBeast> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tar_beast.png");
	public static final ResourceLocation EYE_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tar_beast_eyes.png");

	public RenderTarBeast(RenderManager manager) {
		super(manager, new ModelTarBeast(), 0.7F);
		this.addLayer(new LayerOverlay<EntityTarBeast>(this, EYE_TEXTURE).setGlow(true));
		this.addLayer(new LayerTarBeastEffects(this));
	}

	@Override
	public ResourceLocation getEntityTexture(EntityTarBeast entity) {
		return TEXTURE;
	}
}