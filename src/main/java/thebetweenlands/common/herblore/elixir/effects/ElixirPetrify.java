package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import thebetweenlands.common.TheBetweenlands;

public class ElixirPetrify extends ElixirEffect {
	public ElixirPetrify(ResourceLocation icon) {
		super(icon);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("petrify_slowdown"), -1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		CompoundTag nbt = entity.getPersistentData();

		if(nbt.getInt("thebetweenlands.petrify.ticks") != entity.tickCount - 1) {
			nbt.putFloat("thebetweenlands.petrify.yaw", entity.getYRot());
			nbt.putFloat("thebetweenlands.petrify.yawHead", entity.getYHeadRot());
			nbt.putFloat("thebetweenlands.petrify.pitch", entity.getXRot());
		}
		nbt.putInt("thebetweenlands.petrify.ticks", entity.tickCount);

		entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), nbt.getFloat("thebetweenlands.petrify.yaw"), nbt.getFloat("thebetweenlands.petrify.pitch"));
		entity.setYHeadRot(nbt.getFloat("thebetweenlands.petrify.yawHead"));
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
