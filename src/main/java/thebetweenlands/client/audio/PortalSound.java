package thebetweenlands.client.audio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

@SideOnly(Side.CLIENT)
public class PortalSound extends EntitySound<EntityPlayer> {
	public PortalSound(SoundEvent sound, SoundCategory category, EntityPlayer player) {
		super(sound, category, player, (entity) -> {
			IPortalCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
			if (cap != null) {

				if(cap.isInPortal()) {
					return true;
				}
			}

			return false;
		});
		this.volume = 0.3F;
		this.pitch = 0.8F;
	}
}
