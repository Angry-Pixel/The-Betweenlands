package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemMob extends Item {
	private final Class<? extends Entity> defaultMob;

	/**
	 * @param maxStackSize Max stack size of the item. If this is > 1 then only the entity's ID and no additional NBT is stored.
	 * @param defaultMob Default mob of this item
	 */
	public ItemMob(int maxStackSize, @Nullable Class<? extends Entity> defaultMob) {
		this.maxStackSize = maxStackSize;
		this.defaultMob = defaultMob;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}
	
	public ItemStack capture(Class<? extends Entity> cls) {
		return this.capture(cls, null);
	}

	public ItemStack capture(Class<? extends Entity> cls, @Nullable NBTTagCompound nbt) {
		ResourceLocation id = EntityList.getKey(cls);
		if(id != null) {
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setString("id", id.toString());

			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public ItemStack capture(Entity entity) {
		NBTTagCompound nbt = new NBTTagCompound();

		if(this.maxStackSize == 1) {
			entity.writeToNBTOptional(nbt);
		} else {
			NBTTagCompound entityNbt = new NBTTagCompound();
			if(entity.writeToNBTOptional(entityNbt) && entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				nbt.setString("id", entityNbt.getString("id"));
			}
		}

		if(!nbt.isEmpty()) {
			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			if(entity.hasCustomName()) {
				stack.setStackDisplayName(entity.getCustomNameTag());
			}

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public boolean isCapturedEntity(ItemStack stack, Class<? extends Entity> cls) {
		if(stack.getItem() != this) {
			return false;
		}

		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null && nbt.hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound entityNbt = nbt.getCompoundTag("Entity");

			if(entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				Class<? extends Entity> entityCls = EntityList.getClass(new ResourceLocation(entityNbt.getString("id")));
				return entityCls != null && cls.isAssignableFrom(entityCls);
			}
		}

		return this.defaultMob != null && cls.isAssignableFrom(this.defaultMob);
	}

	@Nullable
	public ResourceLocation getCapturedEntityId(ItemStack stack) {
		if(stack.getItem() != this) {
			return null;
		}

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound entityNbt = stack.getTagCompound().getCompoundTag("Entity");

			if(entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				return new ResourceLocation(entityNbt.getString("id"));
			}
		}

		if(this.defaultMob != null) {
			return EntityList.getKey(this.defaultMob);
		}

		return null;
	}

	@Nullable
	public Entity createCapturedEntity(World world, double x, double y, double z, ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			return this.createCapturedEntityFromNBT(world, x, y, z, stack.getTagCompound().getCompoundTag("Entity"));
		}

		if(this.defaultMob != null) {
			ResourceLocation id = EntityList.getKey(this.defaultMob);
			if(id != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("id", id.toString());
				return this.createCapturedEntityFromNBT(world, x, y, z, nbt);
			}
		}

		return null;
	}

	@Nullable
	protected Entity createCapturedEntityFromNBT(World world, double x, double y, double z, NBTTagCompound nbt) {
		Entity entity = EntityList.createEntityFromNBT(nbt, world);

		if(entity != null) {
			entity.setUniqueId(UUID.randomUUID());
			entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0f, 0);
			entity.motionX = entity.motionY = entity.motionZ = 0;
			return entity;
		}

		return null;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.defaultMob != null && this.isInCreativeTab(tab)) {
			items.add(this.capture(this.defaultMob));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			if(entity != null) {
				if(facing.getXOffset() != 0) {
					entity.setPosition(entity.posX + facing.getXOffset() * entity.width * 0.5f, entity.posY, entity.posZ);
				}
				if(facing.getYOffset() < 0) {
					entity.setPosition(entity.posX, entity.posY - entity.height, entity.posZ);
				}
				if(facing.getZOffset() != 0) {
					entity.setPosition(entity.posX, entity.posY, entity.posZ + facing.getZOffset() * entity.width * 0.5f);
				}

				if(world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
					world.spawnEntity(entity);
					if(entity instanceof EntityLiving) {
						((EntityLiving) entity).playLivingSound();
					}
					if (entity instanceof EntityChiromawHatchling) {
						((EntityChiromawHatchling) entity).setOwnerId(player.getUniqueID());
						((EntityChiromawHatchling) entity).setFoodCraved(((EntityChiromawHatchling) entity).chooseNewFoodFromLootTable());
					}

 					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		ResourceLocation id = this.getCapturedEntityId(stack);
		if(id != null) {
			return "entity." + id.getNamespace() + "." + id.getPath();
		}
		return super.getTranslationKey(stack);
	}

	@Override
	public boolean hasCustomProperties() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(worldIn != null) {
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack);
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() == ItemRegistry.CHIROMAW_EGG_LIGHTNING)
			return true;
		return false;
	}
}
