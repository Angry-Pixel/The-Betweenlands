package thebetweenlands.common.world.storage.location;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class LocationChiromawMatriarchNest extends LocationGuarded {
	private static final int RESPAWN_TIME = 20 * 6; //20 * 6 * 10s = 20min.

	private BlockPos nest;

	private int respawnCounter = 0;

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
	}

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region, BlockPos nest) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
		this.setNestPosition(nest);
	}

	public void setNestPosition(BlockPos nest) {
		this.nest = nest;
	}

	public BlockPos getNestPosition() {
		return this.nest;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		if(this.nest != null) {
			nbt.setInteger("NestX", this.nest.getX());
			nbt.setInteger("NestY", this.nest.getY());
			nbt.setInteger("NestZ", this.nest.getZ());
		}
		nbt.setInteger("RespawnCounter", this.respawnCounter);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.nest = new BlockPos(nbt.getInteger("NestX"), nbt.getInteger("NestY"), nbt.getInteger("NestZ"));
		this.respawnCounter = nbt.getInteger("RespawnCounter");
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();
		if(!world.isRemote && this.nest != null && !this.getGuard().isClear(world)) {
			//Check for player claiming
			if(!world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.nest), player -> !player.isCreative() && !player.isSpectator()).isEmpty()) {
				this.getGuard().clear(world);

				this.setVisible(false);

				for(EntityPlayerMP player : world.getEntitiesWithinAABB(EntityPlayerMP.class, this.getBoundingBox())) {
					player.sendStatusMessage(new TextComponentTranslation("chat.chiromaw_matriarch_nest.tainted"), false);

					AdvancementCriterionRegistry.CHIROMAW_MATRIARCH_NEST_CLAIMED.trigger(player);
				}
			}

			//Check for respawn
			if(world.getTotalWorldTime() % 200 == 0 && world.getEntitiesWithinAABB(EntityChiromawMatriarch.class, this.getBoundingBox().grow(160)).isEmpty()) {
				this.respawnCounter++;

				if(this.respawnCounter >= RESPAWN_TIME) {
					this.respawnCounter = 0;

					EntityChiromawMatriarch matriarch = new EntityChiromawMatriarch(world);
					matriarch.setPosition(this.nest.getX() + 0.5D, this.nest.getY() + 0.01D, this.nest.getZ() + 0.5D);

					if(matriarch.isNotColliding()) {
						matriarch.onInitialSpawn(world.getDifficultyForLocation(this.nest), null);
						world.spawnEntity(matriarch);
					} else {
						matriarch.setDead();
					}

					if(world.getEntitiesWithinAABB(EntityChiromawHatchling.class, this.getBoundingBox()).isEmpty()) {
						for(EnumFacing facing : EnumFacing.HORIZONTALS) {
							if(world.rand.nextBoolean()) {
								BlockPos pos = this.nest.offset(facing).down();

								EntityChiromawHatchling egg = new EntityChiromawHatchling(world);
								egg.setPosition(pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ() + 0.5D);

								if(egg.isNotColliding()) {
									world.spawnEntity(egg);
								} else {
									egg.setDead();
								}
							}
						}
					}
				}
			}
		}
	}
}
