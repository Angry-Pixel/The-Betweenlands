package thebetweenlands.common.entity.draeton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.client.audio.DraetonBurnerSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.network.bidirectional.MessageUpdateDraetonPhysicsPart;
import thebetweenlands.common.network.bidirectional.MessageUpdateDraetonPhysicsPart.Action;
import thebetweenlands.common.network.serverbound.MessageSetDraetonAnchorPos;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityDraetonFurnace;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.PlayerUtil;

public class EntityDraeton extends Entity implements IEntityMultiPart {
	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> ANCHOR_DEPLOYED = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ANCHOR_FIXATED = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final DataParameter<BlockPos> ANCHOR_POS = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BLOCK_POS);

	private static final DataParameter<ItemStack> UPGRADE_1_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> UPGRADE_2_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> UPGRADE_3_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> UPGRADE_4_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> UPGRADE_5_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> UPGRADE_6_CONTENT = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.ITEM_STACK);
	private static final ImmutableList<DataParameter<ItemStack>> UPGRADE_CONTENT = ImmutableList.of(UPGRADE_1_CONTENT, UPGRADE_2_CONTENT, UPGRADE_3_CONTENT, UPGRADE_4_CONTENT, UPGRADE_5_CONTENT, UPGRADE_6_CONTENT);

	private static final DataParameter<Boolean> STORAGE_1_OPEN = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> STORAGE_2_OPEN = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> STORAGE_3_OPEN = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> STORAGE_4_OPEN = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.BOOLEAN);
	private static final ImmutableList<DataParameter<Boolean>> STORAGE_OPEN = ImmutableList.of(STORAGE_1_OPEN, STORAGE_2_OPEN, STORAGE_3_OPEN, STORAGE_4_OPEN);

	private static final DataParameter<Integer> BURNER_FUEL = EntityDataManager.createKey(EntityDraeton.class, DataSerializers.VARINT);

	private Vec3d prevBalloonPos = Vec3d.ZERO;
	private Vec3d balloonPos = Vec3d.ZERO;
	private Vec3d balloonMotion = Vec3d.ZERO;

	private final InventoryBasic upgradesInventory = new InventoryBasic("container.bl.draeton_upgrades", false, 6) {
		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			//Drop furnace content if furnace upgrade is being removed
			if(!EntityDraeton.this.world.isRemote && index >= 0 && index < 4 && EntityDraeton.this.isFurnaceUpgrade(this.getStackInSlot(index)) && !EntityDraeton.this.isFurnaceUpgrade(stack)) {
				EntityDraeton.this.dropFurnaceContent(index);
			}

			super.setInventorySlotContents(index, stack);
		}
	};
	private final InventoryBasic pullersInventory = new InventoryBasic("container.bl.dreaton_pullers", false, 6) {
		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			super.setInventorySlotContents(index, stack);
			EntityDraeton.this.onPullerSlotChanged(index);
		}
	};
	private final InventoryBasic burnerInventory = new InventoryBasic("container.bl.draeton_burner", false, 1);

	private final NonNullList<ItemStack> furnacesInventory = NonNullList.withSize(16, ItemStack.EMPTY);
	private final TileEntityDraetonFurnace[] furnaces = new TileEntityDraetonFurnace[4];

	public final List<DraetonPhysicsPart> physicsParts = new ArrayList<>();

	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYaw;
	private double lerpPitch;

	private List<Entity> loadedPullerEntities = new ArrayList<>();

	private int nextPhysicsPartId = 0;

	public float prevRotationRoll, rotationRoll;

	private boolean descend = false;

	private final EntityDraetonInteractionPart[] parts;
	public final EntityDraetonInteractionPart guiPart;
	public final EntityDraetonInteractionPart upgradePart1;
	public final EntityDraetonInteractionPart upgradePart2;
	public final EntityDraetonInteractionPart upgradePart3;
	public final EntityDraetonInteractionPart upgradePart4;
	public final EntityDraetonInteractionPart upgradeAnchorPart;
	public final EntityDraetonInteractionPart upgradeFramePart;
	public final EntityDraetonInteractionPart burnerPart;
	public final EntityDraetonInteractionPart balloonFront;
	public final EntityDraetonInteractionPart balloonMiddle;
	public final EntityDraetonInteractionPart balloonBack;

	private DraetonPhysicsPart anchorPhysicsPart;

	private final EntityItemFrame dummyFrame;

	protected boolean dropContentsWhenDead = true;

	public float upgradeCounterRoll = 0.0f;
	public float upgradeOpenTicks = 0.0f;

	private final int[] storageUsers = new int[4];
	private final int[] prevStorageOpenTicks = new int[4];
	private final int[] storageOpenTicks = new int[4];

	private boolean turnSoundRoll = false;
	private boolean turnSoundPitch = false;

	protected int maxFuel = 1000;

	private boolean wasBurnerRunning = false;

	public EntityDraeton(World world) {
		super(world);
		this.setSize(1.5F, 1.5f);

		this.parts = new EntityDraetonInteractionPart[]{ 
				this.guiPart = new EntityDraetonInteractionPart(this, "gui", 0.5f, 0.5f, false),
						this.upgradePart1 = new EntityDraetonInteractionPart(this, "upgrade_1", 0.5f, 0.75f, false), this.upgradePart2 = new EntityDraetonInteractionPart(this, "upgrade_2", 0.5f, 0.75f, false),
						this.upgradePart3 = new EntityDraetonInteractionPart(this, "upgrade_3", 0.5f, 0.75f, false), this.upgradePart4 = new EntityDraetonInteractionPart(this, "upgrade_4", 0.5f, 0.75f, false),
						this.burnerPart = new EntityDraetonInteractionPart(this, "burner", 0.7f, 0.5f, false),
						this.upgradeAnchorPart = new EntityDraetonInteractionPart(this, "upgrade_anchor", 0.5f, 0.5f, false),
						this.upgradeFramePart = new EntityDraetonInteractionPart(this, "upgrade_frame", 0.5f, 0.5f, false),
						this.balloonFront = new EntityDraetonInteractionPart(this, "balloon_front", 1.5f, 1.25f, true), this.balloonMiddle = new EntityDraetonInteractionPart(this, "balloon_middle", 1.5f, 1.25f, true), this.balloonBack = new EntityDraetonInteractionPart(this, "balloon_back", 1.5f, 1.25f, true)
		};

		this.dummyFrame = new EntityItemFrame(this.world) {
			@Override
			public BlockPos getHangingPosition() {
				return new BlockPos(EntityDraeton.this);
			}
		};
		this.dummyFrame.facingDirection = EnumFacing.NORTH;

		for(int i = 0; i < 4; i++) {
			this.furnaces[i] = TileEntityDraetonFurnace.create(this.furnacesInventory, i);
			this.furnaces[i].setWorld(world);
		}
	}

	@Nullable
	public DraetonPhysicsPart getAnchorPhysicsPart() {
		return this.anchorPhysicsPart;
	}

	public DraetonPhysicsPart getPhysicsPartById(int id) {
		for(DraetonPhysicsPart part : this.physicsParts) {
			if(part.id == id) {
				return part;
			}
		}
		return null;
	}

	public DraetonPhysicsPart getPhysicsPartBySlot(int slot, DraetonPhysicsPart.Type type) {
		for(DraetonPhysicsPart part : this.physicsParts) {
			if(part.slot == slot && part.type == type) {
				return part;
			}
		}
		return null;
	}

	/**
	 * Add physics part on client side
	 */
	public DraetonPhysicsPart addPhysicsPart(MessageUpdateDraetonPhysicsPart.Position pos) {
		DraetonPhysicsPart part = new DraetonPhysicsPart(pos.type, this, pos.id, pos.slot);

		part.lerpX = part.x = pos.x + this.posX;
		part.lerpY = part.y = pos.y + this.posY;
		part.lerpZ = part.z = pos.z + this.posZ;

		part.motionX = pos.mx;
		part.motionY = pos.my;
		part.motionZ = pos.mz;

		this.physicsParts.add(part);

		if(part.type == DraetonPhysicsPart.Type.ANCHOR) {
			this.anchorPhysicsPart = part;
		}

		return part;
	}

	/**
	 * Remove physics part on client side
	 */
	public boolean removePhysicsPartById(int id) {
		Iterator<DraetonPhysicsPart> it = this.physicsParts.iterator();
		while(it.hasNext()) {
			DraetonPhysicsPart part = it.next();
			if(part.id == id) {
				part.isActive = false;
				it.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(TIME_SINCE_HIT, 0);
		this.dataManager.register(DAMAGE_TAKEN, 0.0f);
		this.dataManager.register(ANCHOR_DEPLOYED, false);
		this.dataManager.register(ANCHOR_FIXATED, false);
		this.dataManager.register(ANCHOR_POS, BlockPos.ORIGIN);
		for(DataParameter<ItemStack> param : UPGRADE_CONTENT) {
			this.dataManager.register(param, ItemStack.EMPTY);
		}
		for(DataParameter<Boolean> param : STORAGE_OPEN) {
			this.dataManager.register(param, false);
		}
		this.dataManager.register(BURNER_FUEL, 0);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);

		if(this.world.isRemote) {
			for(int i = 0; i < 6; i++) {
				DataParameter<ItemStack> param = UPGRADE_CONTENT.get(i);
				if(param.equals(key)) {
					this.getUpgradesInventory().setInventorySlotContents(i, this.dataManager.get(param));
				}
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		//Initialise lerp to real position
		this.lerpX = this.posX;
		this.lerpY = this.posY;
		this.lerpZ = this.posZ;
		this.lerpYaw = this.rotationYaw;
		this.lerpPitch = this.rotationPitch;

		this.loadedPullerEntities.clear();

		this.physicsParts.clear();

		NBTTagList list = nbt.getTagList("PhysicsParts", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			DraetonPhysicsPart part = new DraetonPhysicsPart(DraetonPhysicsPart.Type.PULLER, this, this.nextPhysicsPartId++, tag.getInteger("Slot"));

			Vec3d partPos = new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));

			part.x = partPos.x;
			part.y = partPos.y;
			part.z = partPos.z;

			final Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ).add(this.getPullPoint(part, 1));

			//Ensure that the part is within valid range, otherwise puller entities may become immediately unloaded after spawning
			Vec3d diff = partPos.subtract(pos);
			if(diff.length() > this.getMaxTetherLength(part)) {
				partPos = pos.add(diff.normalize().scale(this.getMaxTetherLength(part)));
			}

			part.x = partPos.x;
			part.y = partPos.y;
			part.z = partPos.z;

			if(tag.hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
				NBTTagCompound entityNbt = tag.getCompoundTag("Entity");

				Entity entity = EntityList.createEntityFromNBT(entityNbt, this.world);

				if(entity instanceof IPullerEntity) {
					((IPullerEntity) entity).setPuller(this, part);
					part.setEntity((IPullerEntity) entity);

					entity.setPosition(part.x, part.y, part.z);

					this.loadedPullerEntities.add(entity);
				} else {
					entity.setDropItemsWhenDead(false);
					entity.setDead();
				}
			}

			this.physicsParts.add(part);
		}

		this.dataManager.set(ANCHOR_DEPLOYED, nbt.getBoolean("AnchorDeployed"));
		this.setAnchorPos(NBTUtil.getPosFromTag(nbt.getCompoundTag("AnchorPos")), nbt.getBoolean("AnchorFixated"));

		this.upgradesInventory.clear();
		if (nbt.hasKey("Upgrades", Constants.NBT.TAG_COMPOUND)) {
			NBTHelper.loadAllItems(nbt.getCompoundTag("Upgrades"), this.upgradesInventory);
		}

		this.pullersInventory.clear();
		if (nbt.hasKey("Pullers", Constants.NBT.TAG_COMPOUND)) {
			NBTHelper.loadAllItems(nbt.getCompoundTag("Pullers"), this.pullersInventory);
		}

		this.furnacesInventory.clear();
		if (nbt.hasKey("FurnacesInventory", Constants.NBT.TAG_COMPOUND)) {
			ItemStackHelper.loadAllItems(nbt.getCompoundTag("FurnacesInventory"), this.furnacesInventory);
		}

		this.burnerInventory.clear();
		if (nbt.hasKey("BurnerInventory", Constants.NBT.TAG_COMPOUND)) {
			NBTHelper.loadAllItems(nbt.getCompoundTag("BurnerInventory"), this.burnerInventory);
		}

		NBTTagCompound furnacesNbt = nbt.getCompoundTag("Furnaces");
		for(int i = 0; i < 4; i++) {
			this.getFurnace(i).readDreatonFurnaceData(furnacesNbt.getCompoundTag("Furnace" + i));
		}

		this.setBurnerFuel(nbt.getInteger("BurnerFuel"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for(DraetonPhysicsPart part : this.physicsParts) {
			if(part.type == DraetonPhysicsPart.Type.PULLER) {
				NBTTagCompound tag = new NBTTagCompound();

				tag.setInteger("Slot", part.slot);

				tag.setDouble("x", part.x);
				tag.setDouble("y", part.y);
				tag.setDouble("z", part.z);

				Entity entity = part.getEntity();
				if(entity != null && entity.isEntityAlive()) {
					ResourceLocation id = EntityList.getKey(entity);

					if(id != null) {
						NBTTagCompound entityNbt = new NBTTagCompound();

						entity.writeToNBT(entityNbt);

						entityNbt.setString("id", id.toString());

						tag.setTag("Entity", entityNbt);
					}
				}

				list.appendTag(tag);
			}
		}
		nbt.setTag("PhysicsParts", list);

		nbt.setBoolean("AnchorDeployed", this.dataManager.get(ANCHOR_DEPLOYED));
		nbt.setBoolean("AnchorFixated", this.dataManager.get(ANCHOR_FIXATED));
		nbt.setTag("AnchorPos", NBTUtil.createPosTag(this.dataManager.get(ANCHOR_POS)));

		nbt.setTag("Upgrades", NBTHelper.saveAllItems(new NBTTagCompound(), this.upgradesInventory, false));
		nbt.setTag("Pullers", NBTHelper.saveAllItems(new NBTTagCompound(), this.pullersInventory, false));
		nbt.setTag("FurnacesInventory", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.furnacesInventory, false));
		nbt.setTag("BurnerInventory", NBTHelper.saveAllItems(new NBTTagCompound(), this.burnerInventory, false));

		NBTTagCompound furnacesNbt = new NBTTagCompound();
		for(int i = 0; i < 4; i++) {
			furnacesNbt.setTag("Furnace" + i, this.getFurnace(i).writeDreatonFurnaceData(new NBTTagCompound()));
		}
		nbt.setTag("Furnaces", furnacesNbt);

		nbt.setInteger("BurnerFuel", this.getBurnerFuel());
	}

	@Override
	public double getMountedYOffset() {
		return 0.3D;
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();

		//Initialise lerp to real position
		this.lerpX = this.posX;
		this.lerpY = this.posY;
		this.lerpZ = this.posZ;
		this.lerpYaw = this.rotationYaw;
		this.lerpPitch = this.rotationPitch;

		this.prevBalloonPos = this.balloonPos = this.getPositionVector().add(0, 2, 0);

		if(!world.isRemote) {
			this.anchorPhysicsPart = new DraetonPhysicsPart(DraetonPhysicsPart.Type.ANCHOR, this, this.nextPhysicsPartId++, 0);
			this.physicsParts.add(this.anchorPhysicsPart);
		}
	}

	public Vec3d getBalloonPos(float partialTicks) {
		return this.prevBalloonPos.add(this.balloonPos.subtract(this.prevBalloonPos).scale(partialTicks));
	}

	public Vec3d getCarriageRopeConnection(int i, float partialTicks) {
		Vec3d connectionPoint;
		switch(i) {
		default:
		case 4:
		case 0:
			connectionPoint = this.getRotatedCarriagePoint(new Vec3d(0.6f, 1.05f, 0.61f), partialTicks);
			break;
		case 5:
		case 1:
			connectionPoint = this.getRotatedCarriagePoint(new Vec3d(-0.6f, 1.05f, 0.61f), partialTicks);
			break;
		case 6:
		case 2:
			connectionPoint = this.getRotatedCarriagePoint(new Vec3d(0.6f, 1.05f, -0.63f), partialTicks);
			break;
		case 7:
		case 3:
			connectionPoint = this.getRotatedCarriagePoint(new Vec3d(-0.6f, 1.05f, -0.63f), partialTicks);
			break;
		}
		return connectionPoint;
	}

	public Vec3d getBalloonRopeConnection(int i, float partialTicks) {
		Vec3d balloonPos = this.getBalloonPos(partialTicks);
		switch(i) {
		default:
		case 0:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(1.0f, -0.425f, 1.5f), partialTicks));
			break;
		case 1:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(-1.0f, -0.425f, 1.5f), partialTicks));
			break;
		case 2:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(1.0f, -0.425f, -1.5f), partialTicks));
			break;
		case 3:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(-1.0f, -0.425f, -1.5f), partialTicks));
			break;
		case 4:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(1.0f, -0.35f, 0.7f), partialTicks));
			break;
		case 5:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(-1.0f, -0.35f, 0.7f), partialTicks));
			break;
		case 6:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(1.0f, -0.35f, -0.7f), partialTicks));
			break;
		case 7:
			balloonPos = balloonPos.add(this.getRotatedBalloonPoint(new Vec3d(-1.0f, -0.35f, -0.7f), partialTicks));
			break;
		}
		return balloonPos;
	}

	public Vec3d getUpgradePoint(int i, float offset) {
		Vec3d offsetVec = new Vec3d(offset, 0, 0);

		Matrix mat = new Matrix();
		mat.rotate(Math.toRadians(this.getUpgradeRotY(i)), 0, 1, 0);

		offsetVec = mat.transform(offsetVec);

		switch(i) {
		default:
		case 0:
			return new Vec3d(10 / 16.0f, 14 / 16.0f, 8 / 16.0f).add(offsetVec);
		case 1:
			return new Vec3d(-10 / 16.0f, 14 / 16.0f, 8 / 16.0f).add(offsetVec);
		case 2:
			return new Vec3d(10 / 16.0f, 14 / 16.0f, -8 / 16.0f).add(offsetVec);
		case 3:
			return new Vec3d(-10 / 16.0f, 14 / 16.0f, -8 / 16.0f).add(offsetVec);
		}
	}

	public float getUpgradeRotY(int i) {
		switch(i) {
		default:
		case 0:
		case 2:
			return 0.0f;
		case 1:
		case 3:
			return 180.0f;
		}
	}

	public float getUpgradeCounterRoll(int i, float partialTicks) {
		float roll = this.prevRotationRoll + (this.rotationRoll - this.prevRotationRoll) * partialTicks;
		switch(i) {
		default:
		case 0:
		case 2:
			return -roll;
		case 1:
		case 3:
			return roll;
		}
	}

	public float getUpgradeOpenTicks(int i, float partialTicks) {
		return this.prevStorageOpenTicks[i] + (this.storageOpenTicks[i] - this.prevStorageOpenTicks[i]) * partialTicks;
	}

	@Override
	public void onEntityUpdate() {
		if(!this.world.isRemote) {
			//Spawn puller entities that were loaded from nbt
			Iterator<Entity> entityIt = this.loadedPullerEntities.iterator();
			while(entityIt.hasNext()) {
				Entity entity = entityIt.next();

				//Spawning can fail if a chunk isn't loaded yet so keep trying until it works
				if(this.world.spawnEntity(entity)) {
					entityIt.remove();
				}
			}

			//Remove dead pullers
			Iterator<DraetonPhysicsPart> it = this.physicsParts.iterator();
			while(it.hasNext()) {
				DraetonPhysicsPart part = it.next();

				if(part.type == DraetonPhysicsPart.Type.PULLER) {
					Entity entity = part.getEntity();
					if(entity == null || !entity.isEntityAlive()) {
						part.isActive = false;
						it.remove();

						TheBetweenlands.networkWrapper.sendToAllTracking(new MessageUpdateDraetonPhysicsPart(this, part, Action.REMOVE), this);
					}
				}
			}
		}

		for(DraetonPhysicsPart part : this.physicsParts) {
			part.prevX = part.x;
			part.prevY = part.y;
			part.prevZ = part.z;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		float drag = 0.98f;
		for(DraetonPhysicsPart part : this.physicsParts) {
			if(part.getEntity() != null) {
				drag = part.getEntity().getCarriageDrag(drag);
			}
		}

		if(this.onGround) {
			drag *= 0.8f;
		}

		this.motionY *= drag;
		this.motionX *= drag;
		this.motionZ *= drag;

		this.handleWaterMovement();
		this.move(MoverType.SELF, this.motionX, this.motionY - 0.0000001f, this.motionZ);
		this.pushOutOfBlocks(this.posX, this.posY, this.posZ);

		if(this.canPassengerSteer()) {
			Entity controller = this.getControllingPassenger();

			if(this.world.isRemote && controller instanceof EntityLivingBase) {
				controller.fallDistance = 0;

				this.handleControllerMovement((EntityLivingBase) controller);
			}

			this.updateCarriage();

			if(!this.world.isRemote) {
				this.lerpX = this.posX;
				this.lerpY = this.posY;
				this.lerpZ = this.posZ;
				this.lerpYaw = this.rotationYaw;
				this.lerpPitch = this.rotationPitch;

				for(DraetonPhysicsPart part : this.physicsParts) {
					part.lerpX = part.x;
					part.lerpY = part.y;
					part.lerpZ = part.z;
				}
			}
		} else if(this.world.isRemote) {
			this.motionX = this.motionY = this.motionZ = 0;

			for(DraetonPhysicsPart part : this.physicsParts) {
				part.tickLerp();
			}
		}

		if(this.canPassengerSteer()) {
			if(this.getPassengers().isEmpty() || this.physicsParts.isEmpty()) {
				this.motionY -= 0.005f;
			}
			if(!this.isBurnerRunning()) {
				this.motionY -= 0.0175f;
			}
		}

		this.updateBurner();

		this.updatePullerSlots();

		if(this.world.isRemote) {
			this.balloonMotion = this.balloonMotion.add(0, 0.125f, 0).scale(0.9f);

			this.prevBalloonPos = this.balloonPos;
			this.balloonPos = this.balloonPos.add(this.balloonMotion);

			for(int i = 0; i < 8; i++) {
				Vec3d balloonConnection = this.getBalloonRopeConnection(i, 1);
				Vec3d tetherPos = this.getPositionVector().add(this.getCarriageRopeConnection(i, 1));

				Vec3d diff = balloonConnection.subtract(tetherPos);

				float tetherLength = 2.0f + (float)Math.sin(this.ticksExisted * 0.1f) * 0.05f;

				if(diff.length() > 6.0f) {
					this.balloonPos = this.getPositionVector().add(0, 1, 0);
				} else if(diff.length() > tetherLength) {
					Vec3d correction = diff.normalize().scale(-(diff.length() - tetherLength));
					this.balloonPos = this.balloonPos.add(correction.scale(0.75f));

					this.balloonMotion = this.balloonMotion.add(correction.scale(1.25f));
				}
			}
		} else {
			this.balloonMotion = Vec3d.ZERO;
			this.prevBalloonPos = this.balloonPos = this.getPositionVector().add(0, 2.5f, 0);
		}

		if(this.world instanceof WorldServer) {
			//Send server state of parts to non-controller players
			if(this.ticksExisted % 10 == 0) {
				for(DraetonPhysicsPart part : this.physicsParts) {
					MessageUpdateDraetonPhysicsPart msg = new MessageUpdateDraetonPhysicsPart(this, part, MessageUpdateDraetonPhysicsPart.Action.UPDATE);

					Set<? extends EntityPlayer> tracking = ((WorldServer) this.world).getEntityTracker().getTrackingPlayers(this);
					for(EntityPlayer player : tracking) {
						//Don't send to controller
						if(player instanceof EntityPlayerMP && player != this.getControllingPassenger()) {
							TheBetweenlands.networkWrapper.sendTo(msg, (EntityPlayerMP) player);
						}
					}
				}
			}
		}

		this.updateParts();

		this.firstUpdate = false;
	}

	protected void updateBurner() {
		if(!this.world.isRemote) {
			if(this.isBurnerRunning()) {
				this.setBurnerFuel(Math.max(0, this.getBurnerFuel() - 1));
			}

			if(this.maxFuel - this.getBurnerFuel() > 100) {
				ItemStack burnerStack = this.burnerInventory.getStackInSlot(0);

				//TODO Fuel item
				if(!burnerStack.isEmpty() && EnumItemMisc.SULFUR.isItemOf(burnerStack)) {
					burnerStack.shrink(1);

					this.setBurnerFuel(this.getBurnerFuel() + 100);
				}
			}
		} else {
			if(!this.wasBurnerRunning && this.isBurnerRunning()) {
				this.playBurnerSound();
			}

			this.wasBurnerRunning = this.isBurnerRunning();

			if(this.isBurnerRunning()) {
				this.spawnBurnerFlame();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnBurnerFlame() {
		BLParticles.DRAETON_BURNER_FLAME.spawn(this.world, 0, 0, 0, ParticleArgs.get().withMotion(0, 0.1D, 0).withData(this));
	}

	@SideOnly(Side.CLIENT)
	protected void playBurnerSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(new DraetonBurnerSound(this));
	}

	protected void updatePullerSlots() {
		if(!this.world.isRemote) {
			for(int i = 0; i < 6; i++) {
				ItemStack stack = this.getPullersInventory().getStackInSlot(i);
				if(!stack.isEmpty()) {
					DraetonPhysicsPart puller = this.getPhysicsPartBySlot(i, DraetonPhysicsPart.Type.PULLER);

					if(puller == null) {
						//Remove puller item
						this.getPullersInventory().setInventorySlotContents(i, ItemStack.EMPTY);
					} else if(puller.getEntity() != null) {
						Entity entity = puller.getEntity().createReleasedEntity();

						if(entity != null) {
							//Update puller item
							stack = ItemRegistry.CRITTER.capture(entity);
							this.getPullersInventory().setInventorySlotContents(i, stack);
						}
					}
				}
			}
		}
	}

	protected void updateParts() {
		IInventory inventory = this.getUpgradesInventory();

		//Sync upgrades to client
		if(!this.world.isRemote) {
			for(int i = 0; i < 6; i++) {
				this.dataManager.set(UPGRADE_CONTENT.get(i), inventory.getStackInSlot(i));
			}
		}

		//Disable upgrade parts if they're not used so they can't be interacted with
		this.upgradePart1.setEnabled(!inventory.getStackInSlot(0).isEmpty());
		this.upgradePart2.setEnabled(!inventory.getStackInSlot(1).isEmpty());
		this.upgradePart3.setEnabled(!inventory.getStackInSlot(2).isEmpty());
		this.upgradePart4.setEnabled(!inventory.getStackInSlot(3).isEmpty());
		this.upgradeAnchorPart.setEnabled(!inventory.getStackInSlot(4).isEmpty());

		for(int i = 0; i < 4; i++) {
			this.prevStorageOpenTicks[i] = this.storageOpenTicks[i];

			if(this.dataManager.get(STORAGE_OPEN.get(i))) {
				this.storageOpenTicks[i] = Math.min(5, this.storageOpenTicks[i] + 1);
			} else {
				this.storageOpenTicks[i] = Math.max(0, this.storageOpenTicks[i] - 1);
			}
		}

		for(Entity entity : this.parts) {
			entity.onUpdate();
		}

		Vec3d guiPos = this.getRotatedCarriagePoint(new Vec3d(0, 0.05D, 0.25D), 1).add(this.posX, this.posY, this.posZ);
		this.guiPart.setPosition(guiPos.x, guiPos.y, guiPos.z);

		Vec3d upgradePos1 = this.getRotatedCarriagePoint(this.getUpgradePoint(0, 0.25f), 1).add(this.posX, this.posY - this.upgradePart1.height + 0.05f, this.posZ);
		this.upgradePart1.setPosition(upgradePos1.x, upgradePos1.y, upgradePos1.z);

		Vec3d upgradePos2 = this.getRotatedCarriagePoint(this.getUpgradePoint(1, 0.25f), 1).add(this.posX, this.posY - this.upgradePart2.height + 0.05f, this.posZ);
		this.upgradePart2.setPosition(upgradePos2.x, upgradePos2.y, upgradePos2.z);

		Vec3d upgradePos3 = this.getRotatedCarriagePoint(this.getUpgradePoint(2, 0.25f), 1).add(this.posX, this.posY - this.upgradePart3.height + 0.05f, this.posZ);
		this.upgradePart3.setPosition(upgradePos3.x, upgradePos3.y, upgradePos3.z);

		Vec3d upgradePos4 = this.getRotatedCarriagePoint(this.getUpgradePoint(3, 0.25f), 1).add(this.posX, this.posY - this.upgradePart4.height + 0.05f, this.posZ);
		this.upgradePart4.setPosition(upgradePos4.x, upgradePos4.y, upgradePos4.z);

		Vec3d anchorPos = this.getRotatedCarriagePoint(new Vec3d(0.0f, 0.4f, 0.975f), 1).add(this.posX, this.posY, this.posZ);
		this.upgradeAnchorPart.setPosition(anchorPos.x, anchorPos.y - this.upgradeAnchorPart.height / 2, anchorPos.z);

		Vec3d burnerPos = this.getBalloonPos(1).add(this.getRotatedBalloonPoint(new Vec3d(0, -0.5f, 0), 1));
		this.burnerPart.setPosition(burnerPos.x, burnerPos.y - this.burnerPart.height / 2, burnerPos.z);

		Matrix balloonRot = new Matrix();
		balloonRot.rotate((float)-Math.toRadians(this.rotationYaw), 0, 1, 0);

		Vec3d balloonFrontPos = balloonRot.transform(new Vec3d(0, 3, 1.5f)).add(this.posX, this.posY, this.posZ);
		this.balloonFront.setPosition(balloonFrontPos.x, balloonFrontPos.y, balloonFrontPos.z);

		Vec3d balloonMiddlePos = balloonRot.transform(new Vec3d(0, 3, 0)).add(this.posX, this.posY, this.posZ);
		this.balloonMiddle.setPosition(balloonMiddlePos.x, balloonMiddlePos.y, balloonMiddlePos.z);

		Vec3d balloonBackPos = balloonRot.transform(new Vec3d(0, 3, -1.5f)).add(this.posX, this.posY, this.posZ);
		this.balloonBack.setPosition(balloonBackPos.x, balloonBackPos.y, balloonBackPos.z);
	}

	@Override
	public void onUpdate() {
		this.prevRotationRoll = this.rotationRoll;

		if(this.getControllingPassenger() == null) {
			this.descend = false;
		}

		if (this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0.0F) {
			this.setDamageTaken(this.getDamageTaken() - 1.0F);
		}

		if(!this.world.isRemote) {
			//Un-deploy anchor if anchor upgrade is not present
			if(!this.upgradeAnchorPart.isEnabled()) {
				this.dataManager.set(ANCHOR_DEPLOYED, false);
			}

			//Un-fixate anchor if no longer deployed
			if(!this.dataManager.get(ANCHOR_DEPLOYED)) {
				this.setAnchorPos(BlockPos.ORIGIN, false);
			}

			//Update map upgrade item
			ItemStack mapStack = this.getUpgradesInventory().getStackInSlot(5);
			if(!mapStack.isEmpty() && mapStack.getItem() instanceof ItemMap) {
				ItemMap map = (ItemMap) mapStack.getItem();

				//Pretend to be an item frame
				mapStack.setItemFrame(this.dummyFrame);
				this.dummyFrame.facingDirection = EnumFacing.fromAngle(this.rotationYaw);

				MapData mapData = map.getMapData(mapStack, this.world);
				if(mapData != null) {
					//Update map data for rider
					if(this.getControllingPassenger() instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) this.getControllingPassenger();

						mapData.updateVisiblePlayers(player, mapStack);
						map.updateMapData(this.world, player, mapData);
					}

					//Sync map data to tracking players
					if(this.ticksExisted % 10 == 0 && this.world instanceof WorldServer) {
						Set<? extends EntityPlayer> trackers = ((WorldServer) this.world).getEntityTracker().getTrackingPlayers(this);
						for(EntityPlayer tracker : trackers) {
							if(tracker instanceof EntityPlayerMP) {
								EntityPlayerMP player = (EntityPlayerMP) tracker;

								mapData.updateVisiblePlayers(player, mapStack);

								Packet<?> packet = map.createMapDataPacket(mapStack, this.world, player);

								if(packet != null) {
									player.connection.sendPacket(packet);
								}
							}
						}
					}
				}
			}
		}

		//Update furnaces
		for(int i = 0; i < 4; i++) {
			this.furnaces[i].setPos(this.getPosition());
			this.furnaces[i].update();
		}

		super.onUpdate();

		double dx = this.motionX;
		double dz = this.motionZ;
		double dy = this.motionY;

		float targetYaw;
		float targetPitch;
		float targetRoll;

		float adjustStrength = 0.1f;

		float speed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);

		if(speed > 0.1f) {
			targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
			float horizontal = (float)Math.sqrt(dx * dx + dz * dz);
			targetPitch = MathHelper.clamp(((float) -Math.toDegrees(Math.atan2(horizontal, dy)) + 90) * MathHelper.clamp(horizontal * 2.0f, 0, 1.0f), -30, 30);
			targetRoll = MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - this.rotationYaw) * 10.0f, -20, 20);
			adjustStrength = MathHelper.clamp(speed * 0.5f, 0.05f, 0.5f);
		} else {
			targetYaw = this.rotationYaw;
			targetPitch = 0.0f;
			targetRoll = 0.0f;
		}

		float yawOffset = (float) MathHelper.wrapDegrees(targetYaw - this.rotationYaw);
		this.rotationYaw = this.rotationYaw + yawOffset * adjustStrength;

		float pitchOffset = (float) MathHelper.wrapDegrees(targetPitch - this.rotationPitch);
		this.rotationPitch = this.rotationPitch + pitchOffset * adjustStrength * 0.5f;

		float rollOffset = (float) MathHelper.wrapDegrees(targetRoll - this.rotationRoll);
		this.rotationRoll = this.rotationRoll + rollOffset * adjustStrength * 0.5f;

		//Play heavy turn sound
		if(Math.abs(this.rotationRoll) + Math.abs(rollOffset) > 14.0f && !this.turnSoundRoll) {
			this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.DRAETON_TURN, SoundCategory.NEUTRAL, 1, 1 - Math.min(Math.abs(rollOffset), 15.0f) / 15.0f * 0.2f, false);
			this.turnSoundRoll = true;
		} else if(Math.abs(this.rotationRoll) < 3.0f) {
			this.turnSoundRoll = false;
		}
		if(Math.abs(this.rotationPitch) + Math.abs(pitchOffset) > 6.0f && !this.turnSoundPitch) {
			this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.DRAETON_TURN, SoundCategory.NEUTRAL, 1, 1, false);
			this.turnSoundPitch = true;
		} else if(Math.abs(this.rotationPitch) < 1.5f) {
			this.turnSoundPitch = false;
		}

		this.tickLerp();

		if(this.dataManager.get(ANCHOR_FIXATED) && this.anchorPhysicsPart != null) {
			BlockPos anchorPos = this.getAnchorPos();
			this.anchorPhysicsPart.prevX = this.anchorPhysicsPart.x = anchorPos.getX() + 0.5f;
			this.anchorPhysicsPart.prevY = this.anchorPhysicsPart.y = anchorPos.getY();
			this.anchorPhysicsPart.prevZ = this.anchorPhysicsPart.z = anchorPos.getZ() + 0.5f;
		}

		//Send anchor pos to server when fixated
		if(this.world.isRemote && this.canPassengerSteer() && this.anchorPhysicsPart != null && this.dataManager.get(ANCHOR_DEPLOYED) && this.isFixated(this.anchorPhysicsPart) && this.ticksExisted % 5 == 0) {
			TheBetweenlands.networkWrapper.sendToServer(new MessageSetDraetonAnchorPos(this));
		}
	}

	private void tickLerp() {
		if (this.lerpSteps > 0 && this.world.isRemote && !this.canPassengerSteer()) {
			double x = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
			double y = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
			double z = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;

			//If the carriage is player controlled pull the parts along
			//so they don't lag behind
			if(this.getControllingPassenger() != null) {
				double dx = x - this.posX;
				double dy = y - this.posY;
				double dz = z - this.posZ;

				for(DraetonPhysicsPart part : this.physicsParts) {
					part.x += dx;
					part.y += dy;
					part.z += dz;
					part.lerpX += dx;
					part.lerpY += dy;
					part.lerpZ += dz;
				}
			}

			double yaw = MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw);
			this.rotationYaw = (float)(this.rotationYaw + yaw / this.lerpSteps);
			this.rotationPitch = (float)(this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);

			--this.lerpSteps;

			this.setPosition(x, y, z);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	@Override
	public void setDropItemsWhenDead(boolean dropWhenDead) {
		this.dropContentsWhenDead = dropWhenDead;
	}

	@Override
	public Entity changeDimension(int dimensionIn) {
		this.dropContentsWhenDead = false;
		return super.changeDimension(dimensionIn);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if(this.isPassenger(passenger)) {
			int index = this.getPassengers().indexOf(passenger);

			float yOff = 0.5f;
			Vec3d mountOffset;

			if(index <= 1) {
				mountOffset = this.getRotatedCarriagePoint(new Vec3d(0, yOff, 0.25f - index * 0.75f), 1);
			} else {
				mountOffset = this.getRotatedCarriagePoint(new Vec3d(0, yOff, 0), 1);
			}

			passenger.setPosition(this.posX + mountOffset.x, this.posY + mountOffset.y - yOff + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + mountOffset.z);
		}

		if(passenger == this.getControllingPassenger()) {
			this.descend = passenger.isSprinting();
		}
		passenger.setSprinting(false);

		PlayerUtil.resetFloating(passenger);
		PlayerUtil.resetVehicleFloating(passenger);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		double startX = this.posX;
		double startY = this.posY;
		double startZ = this.posZ;

		super.move(type, x, y, z);

		for(DraetonPhysicsPart part : this.physicsParts) {
			if(part.type == DraetonPhysicsPart.Type.PULLER) {
				float drag = part.getEntity() != null ? part.getEntity().getDrag(0.25f) : 0.25f;

				part.move((this.posX - startX) * drag, (this.posY - startY) * drag, (this.posZ - startZ) * drag);
			}
		}
	}

	public void setPacketRelativePartPosition(DraetonPhysicsPart part, float x, float y, float z, float mx, float my, float mz) {
		Entity entity = this.getControllingPassenger();

		//Only set position for non-controlling watching players
		if (entity instanceof EntityPlayer == false || !((EntityPlayer)entity).isUser()) {
			if(this.world.isRemote) {
				//interpolate on client side
				part.lerpX = this.lerpX + x;
				part.lerpY = this.lerpY + y;
				part.lerpZ = this.lerpZ + z;
				part.lerpSteps = 10;
			} else {
				part.lerpX = part.x = this.posX + x;
				part.lerpY = part.y = this.posY + y;
				part.lerpZ = part.z = this.posZ + z;
			}

			part.motionX = mx;
			part.motionY = my;
			part.motionZ = mz;
		}
	}

	protected void handleControllerMovement(EntityLivingBase controller) {
		double dx = 0;
		double dz = 0;

		boolean input = false;

		if(controller.moveForward > 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw + 90));
			dz += Math.sin(Math.toRadians(controller.rotationYaw + 90));
			input = true;
		}
		if(controller.moveForward < 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw - 90));
			dz += Math.sin(Math.toRadians(controller.rotationYaw - 90));
			input = true;
		}
		if(controller.moveStrafing > 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw));
			dz += Math.sin(Math.toRadians(controller.rotationYaw));
			input = true;
		} 
		if(controller.moveStrafing < 0){
			dx += Math.cos(Math.toRadians(controller.rotationYaw + 180));
			dz += Math.sin(Math.toRadians(controller.rotationYaw + 180));
			input = true;
		}

		if(input) {
			Vec3d dir = new Vec3d(dx, Math.sin(Math.toRadians(MathHelper.clamp(/*-controller.rotationPitch + */(controller.isJumping ? 45 : 0) + (this.descend ? -45 : 0), -90, 90))), dz).normalize();

			double moveStrength = 0.1D;

			for(DraetonPhysicsPart part : this.physicsParts) {
				if(part.type == DraetonPhysicsPart.Type.PULLER) {
					part.motionX += dir.x * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
					part.motionZ += dir.z * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
					part.motionY += dir.y * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
				}
			}
		}
	}

	protected void updateCarriage() {
		for(DraetonPhysicsPart part : this.physicsParts) {
			Entity entity = part.getEntity();

			if(part.type == DraetonPhysicsPart.Type.PULLER) {
				float pullerDrag = entity != null ? part.getEntity().getDrag(0.9f) : 0.9f;

				part.motionX *= pullerDrag;
				part.motionY *= pullerDrag;
				part.motionZ *= pullerDrag;

				if(!this.isControlling(part)) {
					part.motionX = part.motionY = part.motionZ = 0;

					part.x = entity.posX;
					part.y = entity.posY;
					part.z = entity.posZ;
				} else {
					float speed = (float) Math.sqrt(part.motionX * part.motionX + part.motionY * part.motionY + part.motionZ * part.motionZ);
					float maxSpeed = this.getMaxPullerSpeed();
					if(speed > maxSpeed) {
						part.motionX *= 1.0f / speed * maxSpeed;
						part.motionY *= 1.0f / speed * maxSpeed;
						part.motionZ *= 1.0f / speed * maxSpeed;
					}

					part.move(part.motionX, part.motionY, part.motionZ);
				}

				Vec3d pullerPos = new Vec3d(part.x, part.y, part.z);

				for(DraetonPhysicsPart otherPart : this.physicsParts) {
					if(otherPart.type == DraetonPhysicsPart.Type.PULLER) {
						Vec3d otherPullerPos = new Vec3d(otherPart.x, otherPart.y, otherPart.z);

						Vec3d diff = pullerPos.subtract(otherPullerPos);

						double dist = diff.length();

						float minDist = 1.5f;

						if(dist < minDist) {
							float pushStr = 0.75f;

							part.motionX += diff.x * (minDist - dist) / minDist * pushStr;
							part.motionY += diff.y * (minDist - dist) / minDist * pushStr;
							part.motionZ += diff.z * (minDist - dist) / minDist * pushStr;
						}
					}
				}
			} else {
				if(part.grounded) {
					part.motionX *= 0.8f;
					part.motionZ *= 0.8f;
					part.motionY = 0;
				} else {
					part.motionX *= 0.98f;
					part.motionY *= 0.98f;
					part.motionZ *= 0.98f;
				}

				if(this.isFixated(part)) {
					part.motionX = 0;
					part.motionY = 0;
					part.motionZ = 0;
				}

				part.motionY -= 0.1f;

				part.move(part.motionX, part.motionY, part.motionZ);
			}

			Vec3d tether = new Vec3d(part.x, part.y, part.z);

			Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ).add(this.getPullPoint(part, 1));

			Vec3d diff = tether.subtract(pos);

			double dist = diff.length();

			float tetherLength = this.getMaxTetherLength(part);

			//Teleport part to carriage if it gets too far away
			//somehow
			if(dist > tetherLength + 6) {
				part.lerpX = part.x = this.posX;
				part.lerpY = part.y = this.posY;
				part.lerpZ = part.z = this.posZ;
				dist = 0;
			}

			if(dist > tetherLength) {
				if(part.type == DraetonPhysicsPart.Type.PULLER || this.isFixated(part)) {
					float pullStrength = entity != null ? part.getEntity().getPull(0.01f) : 0.05f;

					Vec3d motion = diff.normalize().scale((dist - tetherLength) * pullStrength);
					this.motionX += motion.x;
					this.motionY += motion.y;
					this.motionZ += motion.z;
				}

				if(!this.isFixated(part)) {
					if(part.type == DraetonPhysicsPart.Type.PULLER) {
						Vec3d constrainedTetherPos = pos.add(diff.normalize().scale(tetherLength));

						part.move(constrainedTetherPos.x - part.x, constrainedTetherPos.y - part.y, constrainedTetherPos.z - part.z);

						Vec3d correction = diff.normalize().scale((dist - tetherLength) * 0.01f);

						part.motionX -= correction.x;
						part.motionY -= correction.y;
						part.motionZ -= correction.z;
					} else {
						Vec3d constrainedTetherPos = pos.add(diff.normalize().scale(tetherLength));

						part.move(constrainedTetherPos.x - part.x, constrainedTetherPos.y - part.y, constrainedTetherPos.z - part.z);

						Vec3d motion = new Vec3d(part.motionX, part.motionY, part.motionZ);

						motion = motion.subtract(diff.normalize().scale(motion.dotProduct(diff.normalize())));

						part.motionX = motion.x;
						part.motionY = motion.y;
						part.motionZ = motion.z;
					}
				}
			}
		}

		//Send client state of parts to server
		if(this.world.isRemote && this.canPassengerSteer() && this.ticksExisted % 10 == 0) {
			for(DraetonPhysicsPart part : this.physicsParts) {
				TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateDraetonPhysicsPart(this, part, MessageUpdateDraetonPhysicsPart.Action.UPDATE));
			}
		}
	}

	public float getMaxTetherLength(DraetonPhysicsPart part) {
		if(part.type == DraetonPhysicsPart.Type.ANCHOR) {
			return this.dataManager.get(ANCHOR_DEPLOYED) ? 8.0f : 0.25f;
		}
		return 6.0f;
	}

	public boolean isFixated(DraetonPhysicsPart part) {
		if(part.type == DraetonPhysicsPart.Type.ANCHOR) {
			return this.dataManager.get(ANCHOR_DEPLOYED) && part.grounded;
		}
		return false;
	}

	public boolean isControlling(DraetonPhysicsPart part) {
		Entity entity = part.getEntity();
		if(entity != null) {
			Vec3d tether = new Vec3d(entity.posX, entity.posY, entity.posZ);
			Vec3d pos = this.getPositionVector().add(this.getPullPoint(part, 1));
			Vec3d diff = tether.subtract(pos);
			if(diff.length() > this.getMaxTetherLength(part)) {
				return true;
			}
		}
		return this.getControllingPassenger() != null && (entity == null || entity.getRidingEntity() == null);
	}

	public float getMaxPullerSpeed() {
		return 3.0f;
	}

	public Vec3d getPullPoint(DraetonPhysicsPart part, float partialTicks) {
		if(part.type == DraetonPhysicsPart.Type.ANCHOR) {
			return this.getRotatedCarriagePoint(new Vec3d(0.0f, 0.025f, 1.25f), partialTicks);
		}

		int index = part.slot;

		Vec3d basePoint;
		switch(index % 3) {
		default:
		case 1:
			//middle
			basePoint = new Vec3d(0, 0.875f, 1.75f);
			break;
		case 0:
			//left
			basePoint = new Vec3d(0.6f, 0.75f, 1.55f);
			break;
		case 2:
			//right
			basePoint = new Vec3d(-0.6f, 0.75f, 1.55f);
			break;
		}
		return this.getRotatedCarriagePoint(basePoint, partialTicks);
	}

	public Vec3d getRotatedCarriagePoint(Vec3d pos, float partialTicks) {
		Matrix mat = new Matrix();
		mat.translate(0, 1.5f, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks), 0, 1, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks), 1, 0, 0);
		mat.rotate((float)Math.toRadians(this.prevRotationRoll + (this.rotationRoll - this.prevRotationRoll) * partialTicks), 0, 0, 1);
		mat.translate(0, -1.5f, 0);
		return mat.transform(pos);
	}

	public Vec3d getRotatedBalloonPoint(Vec3d pos, float partialTicks) {
		Matrix mat = new Matrix();
		mat.rotate((float)-Math.toRadians(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks), 0, 1, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks), 1, 0, 0);
		mat.rotate((float)Math.toRadians(this.prevRotationRoll + (this.rotationRoll - this.prevRotationRoll) * partialTicks), 0, 0, 1);
		return mat.transform(pos);
	}

	public void setAnchorPos(BlockPos pos, boolean fixated) {
		this.dataManager.set(ANCHOR_POS, pos);
		this.dataManager.set(ANCHOR_FIXATED, fixated);
	}

	public BlockPos getAnchorPos() {
		return this.dataManager.get(ANCHOR_POS);
	}

	public boolean isBurnerRunning() {
		return this.dataManager.get(BURNER_FUEL) > 0 && (!this.onGround || (this.getControllingPassenger() instanceof EntityLivingBase && ((EntityLivingBase)this.getControllingPassenger()).isJumping));
	}

	public int getBurnerFuel() {
		return this.dataManager.get(BURNER_FUEL);
	}

	public void setBurnerFuel(int fuel) {
		this.dataManager.set(BURNER_FUEL, fuel);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYaw = (double)yaw;
		this.lerpPitch = (double)pitch;
		this.lerpSteps = 10;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		double dx = x - this.posX;
		double dy = y - this.posY;
		double dz = z - this.posZ;

		super.setPositionAndRotation(x, y, z, yaw, pitch);

		//Also move all physics parts along
		for(DraetonPhysicsPart part : this.physicsParts) {
			part.lerpX += dx;
			part.lerpY += dy;
			part.lerpZ += dz;
			part.x += dx;
			part.y += dy;
			part.z += dz;
			part.prevX = part.x;
			part.prevY = part.y;
			part.prevZ = part.z;
		}

		this.prevBalloonPos = this.balloonPos = this.balloonPos.add(dx, dy, dz);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		//don't get pushed by puller entities
		if(entityIn instanceof IPullerEntity == false) {
			super.applyEntityCollision(entityIn);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean shouldRiderSit() {
		return true;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (!this.world.isRemote && !this.isDead) {
			if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
				return false;
			} else {
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);

				boolean isCreative = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;

				if (isCreative || this.getDamageTaken() > 40.0F) {
					if (!isCreative && this.world.getGameRules().getBoolean("doEntityDrops")) {
						//TODO Drop item
					}

					this.setDead();
				}

				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.DRAETON_DAMAGE, SoundCategory.NEUTRAL, 1, 1);

				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setDead() {
		super.setDead();

		//Check if contents should be dropped.
		//This is false when entity is being recreated while teleporting to another dim
		//or if entity is being ridden by player.
		if(this.dropContentsWhenDead) {
			//Release all puller entities
			Iterator<DraetonPhysicsPart> partsIt = this.physicsParts.iterator();
			while(partsIt.hasNext()) {
				DraetonPhysicsPart part = partsIt.next();
				if(part.getEntity() != null) {
					part.getEntity().spawnReleasedEntity();
					partsIt.remove();
				}
			}

			//Drop inventory items
			if(!this.world.isRemote) {
				for(int i = 0; i < 4; i++) {
					this.dropFurnaceContent(i);
				}
				InventoryHelper.dropInventoryItems(this.world, this, this.upgradesInventory);
				InventoryHelper.dropInventoryItems(this.world, this, this.burnerInventory);
			}
		}
	}

	@Override
	public boolean canRiderInteract() {
		return false;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!this.world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(!player.isSneaking() && !player.isRidingSameEntity(this)) {
				player.startRiding(this);
			} else {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_UPGRADES, player.getEntityWorld(), this.getEntityId(), 0, 0);
			}
			return true;
		}
		return false;
	}

	public void setDamageTaken(float damageTaken) {
		this.dataManager.set(DAMAGE_TAKEN, damageTaken);
	}

	public float getDamageTaken() {
		return this.dataManager.get(DAMAGE_TAKEN);
	}

	public void setTimeSinceHit(int timeSinceHit) {
		this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
	}

	public int getTimeSinceHit() {
		return this.dataManager.get(TIME_SINCE_HIT);
	}

	public IInventory getUpgradesInventory() {
		return this.upgradesInventory;
	}

	public IInventory getPullersInventory() {
		return this.pullersInventory;
	}

	public IInventory getBurnerInventory() {
		return this.burnerInventory;
	}

	public TileEntityDraetonFurnace getFurnace(int index) {
		return this.furnaces[index];
	}

	protected void dropFurnaceContent(int index) {
		Vec3d dropPos = this.getRotatedCarriagePoint(this.getUpgradePoint(index, 0.25f), 1).add(this.posX, this.posY, this.posZ);

		IInventory furnaceInv = this.getFurnace(index);
		for(int i = 0; i < furnaceInv.getSizeInventory(); i++) {
			ItemStack stack = furnaceInv.getStackInSlot(i);
			if(!stack.isEmpty()) {
				InventoryHelper.spawnItemStack(this.world, dropPos.x, dropPos.y, dropPos.z, stack);
			}
			furnaceInv.setInventorySlotContents(index, ItemStack.EMPTY);
		}
	}

	public void openStorage(EntityPlayer player, int index) {
		if(!this.world.isRemote) {
			if(this.storageUsers[index] == 0 && this.isStorageUpgrade(this.getUpgradesInventory().getStackInSlot(index))) {
				Vec3d upgradePos = this.getUpgradePoint(index, 0.25f).add(this.getPositionVector());
				this.world.playSound(null, upgradePos.x, upgradePos.y, upgradePos.z, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}

			this.storageUsers[index]++;

			this.dataManager.set(STORAGE_OPEN.get(index), true);
		}
	}

	public void closeStorage(EntityPlayer player, int index) {
		if(!this.world.isRemote) {
			this.storageUsers[index] = Math.max(0, this.storageUsers[index] - 1);

			if(this.storageUsers[index] == 0) {
				if(this.isStorageUpgrade(this.getUpgradesInventory().getStackInSlot(index))) {
					Vec3d upgradePos = this.getUpgradePoint(index, 0.25f).add(this.getPositionVector());
					this.world.playSound(null, upgradePos.x, upgradePos.y, upgradePos.z, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.NEUTRAL, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
				}

				this.dataManager.set(STORAGE_OPEN.get(index), false);
			}
		}
	}

	@Override
	public Entity[] getParts() {
		return this.parts;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		if(part == this.balloonFront || part == this.balloonMiddle || part == this.balloonBack) {
			this.attackEntityFrom(source, damage);
		}
		return false;
	}

	protected boolean interactFromMultipart(EntityDraetonInteractionPart part, EntityPlayer player, EnumHand hand) {
		if(!this.world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(part == this.guiPart) {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_UPGRADES, player.getEntityWorld(), this.getEntityId(), 0, 0);
			} else if(part == this.upgradePart1) {
				this.interactWithUpgrade(part, player, hand, 0);
			} else if(part == this.upgradePart2) {
				this.interactWithUpgrade(part, player, hand, 1);
			} else if(part == this.upgradePart3) {
				this.interactWithUpgrade(part, player, hand, 2);
			} else if(part == this.upgradePart4) {
				this.interactWithUpgrade(part, player, hand, 3);
			} else if(part == this.burnerPart) {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_BURNER, player.getEntityWorld(), getEntityId(), 0, 0);
				player.swingArm(hand);
			} else if(part == this.upgradeAnchorPart) {
				this.dataManager.set(ANCHOR_DEPLOYED, !this.dataManager.get(ANCHOR_DEPLOYED));
				player.swingArm(hand);
			}
		}
		return true;
	}

	protected void interactWithUpgrade(EntityDraetonInteractionPart part, EntityPlayer player, EnumHand hand, int index) {
		ItemStack stack = this.getUpgradesInventory().getStackInSlot(index);
		if(!stack.isEmpty()) {
			if(this.isFurnaceUpgrade(stack)) {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_FURNACE, player.getEntityWorld(), this.getEntityId(), index, 0);
				player.swingArm(hand);
			} else if(this.isStorageUpgrade(stack)) {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_POUCH, player.getEntityWorld(), this.getEntityId(), index, 0);
				player.swingArm(hand);
			} else if(this.isCraftingUpgrade(stack)) {
				player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_CRAFTING, player.getEntityWorld(), this.getEntityId(), index, 0);
				player.swingArm(hand);
			}
		}
	}

	protected void onPullerSlotChanged(int index) {
		if(!this.world.isRemote) {
			ItemStack stack = this.getPullersInventory().getStackInSlot(index);

			if(!stack.isEmpty() && stack.getItem() == ItemRegistry.CRITTER) {
				if(this.getPhysicsPartBySlot(index, DraetonPhysicsPart.Type.PULLER) == null) {

					Entity capturedEntity = ItemRegistry.CRITTER.createCapturedEntity(this.world, this.posX, this.posY, this.posZ, stack);

					if(capturedEntity instanceof EntityDragonFly) {
						DraetonPhysicsPart puller = new DraetonPhysicsPart(DraetonPhysicsPart.Type.PULLER, this, this.nextPhysicsPartId++, index);

						Vec3d pos = this.getPullPoint(puller, 1).add(this.getPositionVector());

						puller.lerpX = puller.x = pos.x;
						puller.lerpY = puller.y = pos.y;
						puller.lerpZ = puller.z = pos.z;
						this.physicsParts.add(puller);

						EntityPullerDragonfly dragonfly = new EntityPullerDragonfly(this.world, this, puller);
						dragonfly.readFromNBT(capturedEntity.writeToNBT(new NBTTagCompound()));
						dragonfly.setLocationAndAngles(pos.x, pos.y, pos.z, 0, 0);

						puller.setEntity(dragonfly);

						this.world.spawnEntity(dragonfly);

						TheBetweenlands.networkWrapper.sendToAllTracking(new MessageUpdateDraetonPhysicsPart(this, puller, MessageUpdateDraetonPhysicsPart.Action.ADD), this);
					} else if(capturedEntity instanceof EntityFirefly) {
						DraetonPhysicsPart puller = new DraetonPhysicsPart(DraetonPhysicsPart.Type.PULLER, this, this.nextPhysicsPartId++, index);

						Vec3d pos = this.getPullPoint(puller, 1).add(this.getPositionVector());

						puller.lerpX = puller.x = pos.x;
						puller.lerpY = puller.y = pos.y;
						puller.lerpZ = puller.z = pos.z;
						this.physicsParts.add(puller);

						EntityPullerFirefly firefly = new EntityPullerFirefly(this.world, this, puller);
						firefly.readFromNBT(capturedEntity.writeToNBT(new NBTTagCompound()));
						firefly.setLocationAndAngles(pos.x, pos.y, pos.z, 0, 0);

						puller.setEntity(firefly);

						this.world.spawnEntity(firefly);

						TheBetweenlands.networkWrapper.sendToAllTracking(new MessageUpdateDraetonPhysicsPart(this, puller, MessageUpdateDraetonPhysicsPart.Action.ADD), this);
					}
				}
			} else {

				DraetonPhysicsPart puller = this.getPhysicsPartBySlot(index, DraetonPhysicsPart.Type.PULLER);
				if(puller != null) {
					Entity entity = puller.getEntity();
					if(entity != null) {
						entity.setDropItemsWhenDead(false);
						entity.setDead();
					}

					this.physicsParts.remove(puller);

					TheBetweenlands.networkWrapper.sendToAllTracking(new MessageUpdateDraetonPhysicsPart(this, puller, MessageUpdateDraetonPhysicsPart.Action.REMOVE), this);
				}

			}
		}
	}

	public boolean isFurnaceUpgrade(ItemStack stack) {
		return stack.getItem() == Item.getItemFromBlock(BlockRegistry.SULFUR_FURNACE);
	}

	public boolean isStorageUpgrade(ItemStack stack) {
		return stack.getItem() == ItemRegistry.LURKER_SKIN_POUCH;
	}

	public boolean isCraftingUpgrade(ItemStack stack) {
		return stack.getItem() == Item.getItemFromBlock(BlockRegistry.WEEDWOOD_WORKBENCH);
	}

	public boolean isAnchorUpgrade(ItemStack stack) {
		return stack.getItem() == ItemRegistry.GRAPPLING_HOOK;
	}
}
