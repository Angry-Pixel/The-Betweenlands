package thebetweenlands.common.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityAnimalBurrow extends Entity {

	private static final DataParameter<ItemStack> BURROW_ITEM = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Integer> RESTING_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RECALL_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.STONE.getDefaultState());

	public EntityAnimalBurrow (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(BURROW_ITEM, ItemStack.EMPTY);
		dataManager.register(RESTING_COOLDOWN, 0);
		dataManager.register(RECALL_COOLDOWN, 0);
	}
	
	@SideOnly(Side.CLIENT)
	protected void updateWallSprite() {
		BlockPos pos = this.getPosition();
		IBlockState state = this.world.getBlockState(pos);
		state = state.getActualState(this.world, pos);
		if(state.isFullCube()) {
			this.wallSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
		}
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}

	@Override
	 public void onUpdate() {
		super.onUpdate();
		if(world.isRemote) {
			updateWallSprite();
		}

		if(!world.isRemote) {
			if(getBurrowItem().isEmpty() && world.getTotalWorldTime()%20 == 0 && getRecallTimer() <= 0) {
				lureCloseCrab();
				if(checkCatch() != null) {
					ItemStack itemMob = ((ItemMob) new ItemStack(ItemRegistry.CRITTER).getItem()).capture(checkCatch());
					checkCatch().setDead();
					setBurrowItem(itemMob);
					setRestingTimer(120);
				}
			}
			
			if(!getBurrowItem().isEmpty() && getRestingTimer() <= 0) {
				if(getEntity() != null) {
					getEntityWorld().spawnEntity(getEntity());
					getEntity().setPositionAndRotation(posX, posY + 1D, posZ, 0F, 0F);
					setBurrowItem(ItemStack.EMPTY);
					setRecallTimer(360);
				}
			}

			if(getBurrowItem().isEmpty() && getRecallTimer() > 0)
				setRecallTimer(getRecallTimer() - 1);

			if(!getBurrowItem().isEmpty() && getRestingTimer() > 0)
				setRestingTimer(getRestingTimer() - 1);
		}
	}
	
	private Entity checkCatch() {
		Entity entity = null;
		List<EntityGecko> list = getEntityWorld().getEntitiesWithinAABB(EntityGecko.class, new AxisAlignedBB(getPosition()).grow(0.125D));
		if (!list.isEmpty())
			entity = list.get(0);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public void lureCloseCrab() {
		List<EntityGecko> list = getEntityWorld().getEntitiesWithinAABB(EntityGecko.class, extendRangeBox());
		for (Iterator<EntityGecko> iterator = list.iterator(); iterator.hasNext();) {
			EntityGecko gecko = iterator.next();
			if (gecko.isInWater())
				iterator.remove();
		}
		if (list.isEmpty())
			return;
		if (!list.isEmpty()) {
			Collections.shuffle(list);
			EntityGecko gecko = list.get(0);
			gecko.getNavigator().tryMoveToXYZ(posX, posY, posZ, 1D);
		}
	}

	public AxisAlignedBB extendRangeBox() {
		return  new AxisAlignedBB(getPosition()).grow(8D, 1D, 8D);
	}

	@Override
    public boolean canBeCollidedWith() {
        return true;
    }

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
				if (!player.getHeldItem(hand).isEmpty() && getBurrowItem().isEmpty()) {
					ItemStack stack = player.getHeldItem(hand).splitStack(1);
					if (!stack.isEmpty()) {
						setBurrowItem(stack);
						return true;
					}
				} else if (!getBurrowItem().isEmpty()) {
					ItemStack extracted = getBurrowItem();
					if (!extracted.isEmpty()) {
						EntityItem item = new EntityItem(world, posX, posY + 1.0D, posZ, extracted);
						item.motionX = item.motionY = item.motionZ = 0D;
						world.spawnEntity(item);
						setBurrowItem(ItemStack.EMPTY);
						return true;
					}
			}
		}
		return true;
	}

	public void setBurrowItem(ItemStack itemStack) {
		dataManager.set(BURROW_ITEM, itemStack);
	}

	public ItemStack getBurrowItem() {
		return dataManager.get(BURROW_ITEM);
	}
	
	public void setRestingTimer(int resting) {
		dataManager.set(RESTING_COOLDOWN, resting);
	}

	public int getRestingTimer() {
		return dataManager.get(RESTING_COOLDOWN);
	}
	
	public void setRecallTimer(int recall) {
		dataManager.set(RECALL_COOLDOWN, recall);
	}

	public int getRecallTimer() {
		return dataManager.get(RECALL_COOLDOWN);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (BURROW_ITEM.equals(key))
			setBurrowItem(getBurrowItem());
		super.notifyDataManagerChange(key);
	}

	public Entity getEntity() {
		Entity entity = null;
		if (getBurrowItem() != null && getBurrowItem().getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			entity = EntityList.createEntityFromNBT(getBurrowItem().getTagCompound().getCompoundTag("Entity"), getEntityWorld());
			//entity.setPositionAndRotation(0D, 0D, 0D, 0F, 0F);
		}
		return entity;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		NBTTagCompound burrowItem = (NBTTagCompound) nbt.getTag("burrowItem");
		ItemStack stackBurrow = ItemStack.EMPTY;
		if(burrowItem != null)
			stackBurrow = new ItemStack(burrowItem);
		setBurrowItem(stackBurrow);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTTagCompound burrowItem= new NBTTagCompound();
		getBurrowItem().writeToNBT(burrowItem);
		nbt.setTag("headItem", burrowItem);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

}
