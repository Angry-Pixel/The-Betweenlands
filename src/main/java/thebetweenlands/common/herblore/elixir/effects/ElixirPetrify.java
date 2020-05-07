package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ElixirPetrify extends ElixirEffect {
	public ElixirPetrify(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.addAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "c28c1955-14a1-49fb-9444-5fea9d83c75e", -1.0D, 2);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		NBTTagCompound nbt = entity.getEntityData();
		
		if(nbt.getInteger("thebetweenlands.petrify.ticks") != entity.ticksExisted - 1) {
			nbt.setFloat("thebetweenlands.petrify.yaw", entity.rotationYaw);
			nbt.setFloat("thebetweenlands.petrify.yawHead", entity.rotationYawHead);
			nbt.setFloat("thebetweenlands.petrify.pitch", entity.rotationPitch);
		}
		nbt.setInteger("thebetweenlands.petrify.ticks", entity.ticksExisted);
		
		entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, nbt.getFloat("thebetweenlands.petrify.yaw"), nbt.getFloat("thebetweenlands.petrify.pitch"));
		entity.rotationYawHead = nbt.getFloat("thebetweenlands.petrify.yawHead");
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
