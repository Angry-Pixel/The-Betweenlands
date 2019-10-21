package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMultipartDummy extends EntityLiving {
	protected float sizePadding = 0.01F;

	//For sync with client.
	//Multiparts do not exist in the world so they need to be synced via the owner entity.
	public static final DataParameter<String> PARENT_PART_NAME = EntityDataManager.createKey(EntityMultipartDummy.class, DataSerializers.STRING);
	public static final DataParameter<Integer> PARENT_OWNER_ID = EntityDataManager.createKey(EntityMultipartDummy.class, DataSerializers.VARINT);

	private int cachedPartOwnerId = -1;
	private String cachedParentPartName = "";

	private MultiPartEntityPart parent = null;

	public EntityMultipartDummy(World world) {
		super(world);
		this.setSize(0, 0);
		this.experienceValue = 0;
	}

	public EntityMultipartDummy(World world, MultiPartEntityPart parent) {
		this(world);
		this.parent = parent;
		this.setPosition(parent.posX, parent.posY, parent.posZ);
	}

	public void updatePositioning() {
		if(!this.world.isRemote && this.parent != null) {
			this.dataManager.set(PARENT_OWNER_ID, this.parent.parent instanceof Entity ? ((Entity) this.parent.parent).getEntityId() : -1); 
			this.dataManager.set(PARENT_PART_NAME, this.parent.partName);
		}

		if(this.world.isRemote) {
			int partOwnerId = this.dataManager.get(PARENT_OWNER_ID);
			String parentPartName = this.dataManager.get(PARENT_PART_NAME);

			if(partOwnerId >= 0 && parentPartName.length() > 0 && (this.cachedPartOwnerId != partOwnerId || !this.cachedParentPartName.equals(parentPartName))) {
				Entity parentOwner = this.world.getEntityByID(partOwnerId);

				if(parentOwner != null) {
					for(Entity part : parentOwner.getParts()) {
						if(part instanceof MultiPartEntityPart) {
							if(parentPartName.equals(((MultiPartEntityPart) part).partName)) {
								this.parent = (MultiPartEntityPart) part;
								this.cachedPartOwnerId = partOwnerId;
								this.cachedParentPartName = parentPartName;
							}
						}
					}
				}
			}
		}

		if(this.parent == null || this.parent.parent instanceof Entity == false || !this.parent.isEntityAlive() || !((Entity)this.parent.parent).isEntityAlive()) {
			if(!this.world.isRemote) {
				this.setDead();
			}
		} else {
			this.setSize(this.parent.width + this.sizePadding, this.parent.height + this.sizePadding);
			this.setPositionAndUpdate(this.parent.posX, this.parent.posY - this.sizePadding / 2.0f, this.parent.posZ);
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(PARENT_PART_NAME, "");
		this.dataManager.register(PARENT_OWNER_ID, -1);
	}

	public MultiPartEntityPart getParent() {
		return this.parent;
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {
		return false; //Don't write to disk
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.lastTickPosX = this.posX;
		this.prevPosY = this.lastTickPosY = this.posY;
		this.prevPosZ = this.lastTickPosZ = this.posZ;

		this.updatePositioning();

		this.firstUpdate = false;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if(this.parent != null) {
			return this.parent.processInitialInteract(player, hand);
		}
		return false;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.parent != null) {
			return this.parent.attackEntityFrom(source, amount);
		}
		return false;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public boolean isSilent() {
		return true;
	}

	@Override
	public boolean isInvisible() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean getIsInvulnerable() {
		if(this.parent != null) {
			return this.parent.getIsInvulnerable();
		}
		return true;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		if(this.parent != null) {
			return this.parent.canBeAttackedWithItem();
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		if(this.parent != null) {
			return this.parent.canBeCollidedWith();
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(this.parent != null) {
			return this.parent.getRenderBoundingBox();
		}
		return super.getRenderBoundingBox();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		if(this.parent != null) {
			return this.parent.isInRangeToRender3d(x, y, z);
		}
		return super.isInRangeToRender3d(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		if(this.parent != null) {
			return this.parent.isInRangeToRenderDist(distance);
		}
		return super.isInRangeToRenderDist(distance);
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return false;
	}
}
