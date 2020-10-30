package thebetweenlands.common.capability.swarmed;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class SwarmedCapability extends EntityCapability<SwarmedCapability, ISwarmedCapability, EntityPlayer> implements ISwarmedCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "swarmed");
	}

	@Override
	protected Capability<ISwarmedCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_SWARMED;
	}

	@Override
	protected Class<ISwarmedCapability> getCapabilityClass() {
		return ISwarmedCapability.class;
	}

	@Override
	protected SwarmedCapability getDefaultCapabilityImplementation() {
		return new SwarmedCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}


	private float strength;
	private int hurtTimer;

	private int damageTimer;
	private float damage;

	@Override
	public void setSwarmedStrength(float strength) {
		float newStrength = MathHelper.clamp(strength, 0, 1);
		if(newStrength != this.strength) {
			this.strength = newStrength;
			this.markDirty();
		}
	}

	@Override
	public float getSwarmedStrength() {
		return this.strength;
	}

	@Override
	public void setHurtTimer(int timer) {
		if(timer != this.hurtTimer) {
			this.hurtTimer = timer;
			this.markDirty();
		}
	}

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public void setDamageTimer(int timer) {
		this.damageTimer = timer;
	}

	@Override
	public int getDamageTimer() {
		return this.damageTimer;
	}

	@Override
	public int getHurtTimer() {
		return this.hurtTimer;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("strength", this.strength);
		nbt.setInteger("hurtTimer", this.hurtTimer);
		nbt.setInteger("damageTimer", this.damageTimer);
		nbt.setFloat("damage", this.damage);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.strength = nbt.getFloat("strength");
		this.hurtTimer = nbt.getInteger("hurtTimer");
		this.damageTimer = nbt.getInteger("damageTimer");
		this.damage = nbt.getFloat("damage");
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !event.player.world.isRemote) {
			ISwarmedCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

			if(cap != null) {
				if(cap.getHurtTimer() > 0) {
					cap.setHurtTimer(cap.getHurtTimer() - 1);
				}

				if(cap.getSwarmedStrength() > 0) {
					if(event.player.isInsideOfMaterial(Material.WATER) || event.player.isBurning()) {
						cap.setSwarmedStrength(0);
					} else if(event.player.isSwingInProgress || (event.player.posY - event.player.prevPosY) > 0.1f || event.player.isSneaking()) {
						cap.setSwarmedStrength(cap.getSwarmedStrength() - 0.01f);
						cap.setHurtTimer(5);
						event.player.setSneaking(false);
					}
				}

				if(cap.getSwarmedStrength() < 0.1f) {
					cap.setSwarmedStrength(cap.getSwarmedStrength() - 0.0005f);
				} else {
					cap.setDamageTimer(cap.getDamageTimer() + 1);

					if(cap.getDamageTimer() > 15 + (1.0f - cap.getSwarmedStrength()) * 75) {
						cap.setDamageTimer(0);

						event.player.attackEntityFrom(new DamageSource("bl.swarm").setDamageBypassesArmor(), cap.getDamage());
					}
				}
			}
		}
	}
}