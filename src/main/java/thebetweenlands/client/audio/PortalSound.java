package thebetweenlands.client.audio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

@OnlyIn(Dist.CLIENT)
public class PortalSound extends EntitySound<EntityPlayer> {
	public PortalSound(SoundEvent sound, SoundCategory category, EntityPlayer player) {
		super(sound, category, player, (entity) -> {
			IPortalCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PORTAL).orElse(null);

			if(cap != null && cap.isInPortal()) {
				return true;
			}

			return false;
		});
		this.volume = 0.3F;
		this.pitch = 0.8F;
	}
}
