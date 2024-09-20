package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.ShockParticlePacket;

public class ElixirShocked extends ElixirEffect {

	public ElixirShocked(ResourceLocation icon) {
		super(icon);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("shocked_slowdown"), -0.95f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	@Override
	public void performEffect(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide() && entity.level().getRandom().nextInt(20) == 0) {
			PacketDistributor.sendToPlayersTrackingEntity(entity, new ShockParticlePacket(entity.getId()));
		}
	}

}
