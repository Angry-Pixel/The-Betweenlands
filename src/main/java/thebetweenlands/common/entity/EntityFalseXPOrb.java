package thebetweenlands.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.item.equipment.ItemRing;

public class EntityFalseXPOrb extends EntityXPOrb implements IEntityAdditionalSpawnData {
	private UUID playerUuid;

	public EntityFalseXPOrb(World worldIn) {
		super(worldIn);
	}

	public EntityFalseXPOrb(World worldIn, double x, double y, double z, int expValue, @Nullable UUID playerUuid) {
		super(worldIn, x, y, z, expValue);
		this.playerUuid = playerUuid;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if(this.playerUuid != null) {
			compound.setUniqueId("PlayerUuid", this.playerUuid);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if(compound.hasUniqueId("PlayerUuid")) {
			this.playerUuid = compound.getUniqueId("PlayerUuid");
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(this.playerUuid == null || this.playerUuid.equals(player.getUniqueID())) {
			if(this.xpValue < 0 && !this.world.isRemote && this.delayBeforeCanPickup == 0 && player.xpCooldown == 0) {
				if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerPickupXpEvent(player, this))) {
					return;
				}

				player.xpCooldown = 2;
				player.onItemPickup(this, 1);

				this.xpValue = Math.min(this.xpValue + ItemRing.removeXp(player, -this.xpValue), 0);

				if(this.xpValue >= 0) {
					this.setDead();
				}
			} else {
				super.onCollideWithPlayer(player);
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.xpValue);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.xpValue = Math.abs(buffer.readInt());
	}
}
