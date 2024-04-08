package thebetweenlands.common.network.serverbound;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.network.MessageEntity;

public class MessageExtendedReach extends MessageEntity {
	public MessageExtendedReach() {}

	public MessageExtendedReach(List<Entity> entities) {
		for(Entity entity : entities) {
			this.addEntity(entity);
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack heldItem = player.getHeldItemMainhand();

		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IExtendedReach) {
			((IExtendedReach) heldItem.getItem()).onLeftClick(player, player.getHeldItemMainhand());

			List<Entity> entities = this.getEntities();
			for(Entity entity : entities) {
				if (entity != null && entity.isEntityAlive()) {
					double reach = ((IExtendedReach) heldItem.getItem()).getReach(player, heldItem);
					if (player.isCreative() || reach * reach >= player.getPositionEyes(1.0F).squareDistanceTo(entity.getPositionVector())) {
						player.attackTargetEntityWithCurrentItem(entity);
					}
				}
			}
		}
		return null;
	}
}
