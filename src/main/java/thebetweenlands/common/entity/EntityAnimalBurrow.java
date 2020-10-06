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
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class EntityAnimalBurrow extends Entity {
	private static final DataParameter<Boolean> OCCUPIED = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RESTING_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RECALL_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);
	private NBTTagCompound entityNBT;
	public Entity cachedEntity;

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.STONE.getDefaultState());

	public EntityAnimalBurrow (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(OCCUPIED, true);
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
			if(!getOccupied() && world.getTotalWorldTime()%20 == 0 && getRecallTimer() <= 0) {
				lureCloseEntity();
				if(burrowOwnerEntity() != null) {
					burrowOwnerEntity().setDead();
					setOccupied(true);
					setRestingTimer(120);
				}
			}

			if(getOccupied() && getRestingTimer() <= 0) {
				spawnBurrowEntity();
				setOccupied(false);
				setRecallTimer(360);
			}

			if(!getOccupied() && getRecallTimer() > 0)
				setRecallTimer(getRecallTimer() - 1);

			if(getOccupied() && getRestingTimer() > 0)
				setRestingTimer(getRestingTimer() - 1);
		}
	}
	
	public NBTTagCompound createBurrowEntityNBT(Entity entity) {
		NBTTagCompound entityNBT = new NBTTagCompound();
		entity.writeToNBT(entityNBT);
		String mobName = EntityList.getKey(entity).toString();
		entityNBT.setString("id", mobName);
		return entityNBT;
	}
	
	private EntityLiving burrowOwnerEntity() {
		EntityLiving entity = null;
		List<EntityLiving > list = getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(getPosition()).grow(0.125D));
		for (Iterator<EntityLiving> iterator = list.iterator(); iterator.hasNext();) {
			entity = iterator.next();
			if (cachedEntity != null && entity.getUniqueID() != cachedEntity.getUniqueID())
				iterator.remove();
		}
		if (!list.isEmpty())
			return entity;
		return null;
	}

	@SuppressWarnings("unchecked")
	public void lureCloseEntity() {
		if (cachedEntity != null)
			((EntityLiving) cachedEntity).getNavigator().tryMoveToXYZ(posX, posY, posZ, 1D);
	}

	public AxisAlignedBB extendRangeBox() {
		return  new AxisAlignedBB(getPosition()).grow(8D, 1D, 8D);
	}

	@Override
    public boolean canBeCollidedWith() {
        return true;
    }

	public NBTTagCompound getEntityNBT() {
		return entityNBT;
	}

	public void setEntityNBT(NBTTagCompound entityNBT) {
		this.entityNBT = entityNBT;
	}
	
	public void setOccupied(boolean occupied) {
		dataManager.set(OCCUPIED, occupied);
	}

	public boolean getOccupied() {
		return dataManager.get(OCCUPIED);
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
		if (OCCUPIED.equals(key))
			setOccupied(getOccupied());
		super.notifyDataManagerChange(key);
	}
	
	public Entity getCachedEntity() {
		if(cachedEntity == null && entityNBT != null)
			cachedEntity = EntityList.createEntityFromNBT(entityNBT, getEntityWorld());
		return cachedEntity;
	}

	public void spawnBurrowEntity() {
		if (getEntityWorld().isRemote || entityNBT == null)
			return;
		Entity entity = EntityList.createEntityFromNBT(entityNBT, getEntityWorld());
		entity.setPositionAndRotation(posX, posY + 1D, posZ, 0F, 0F);
		getEntityWorld().spawnEntity(entity);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		entityNBT = nbt.getCompoundTag("EntityNBT");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setTag("EntityNBT", entityNBT);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

}
