package thebetweenlands.common.item.tools;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.TranslationHelper;

public class ItemBLFishingRod extends Item {

	public ItemBLFishingRod() {
		setMaxDamage(256);
		setMaxStackSize(1);
		setCreativeTab(BLCreativeTabs.GEARS);

		addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					boolean mainHand = entityIn.getHeldItemMainhand() == stack;
					boolean offHand = entityIn.getHeldItemOffhand() == stack;
					if (entityIn.getHeldItemMainhand().getItem() instanceof ItemBLFishingRod)
						offHand = false;
					return (mainHand || offHand) && entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).fishEntity != null && ((EntityPlayer)entityIn).fishEntity instanceof EntityBLFishHook ? 1.0F : 0.0F;
				}
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		if(stack.getItemDamage() == stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.tool.broken_rod", stack.getDisplayName()), 0));
		}
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("baited")) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fishing_rod.baited", stack.getTagCompound().getBoolean("baited")));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isFull3D() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!world.isRemote) {
			NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
			
			if(!nbt.hasKey("baited") || stack.getItemDamage() == getMaxDamage(stack)) {
				nbt.setBoolean("baited", false);
			}
			
			if(entityIn instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entityIn;
				
				if(player.getHeldItemMainhand() == stack) {
					if(!nbt.getBoolean("bl.fishing_rod.equipped") && player.getHeldItemOffhand().isEmpty()) {
						nbt.setInteger("bl.fishing_rod.equipped_slot", -1);
						
						for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
							ItemStack invStack = player.inventory.getStackInSlot(i);
							
							if(!invStack.isEmpty() && invStack.getItem() == ItemRegistry.NET) {
								player.setHeldItem(EnumHand.OFF_HAND, invStack);
								player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
								
								nbt.setInteger("bl.fishing_rod.equipped_slot", i);
								
								break;
							}
						}
					}
					
					nbt.setBoolean("bl.fishing_rod.equipped", true);
				} else if(nbt.getBoolean("bl.fishing_rod.equipped")) {
					int equippedSlot = nbt.getInteger("bl.fishing_rod.equipped_slot");
					if(equippedSlot >= 0 && equippedSlot < player.inventory.getSizeInventory() && !player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == ItemRegistry.NET) {
						if(player.inventory.getStackInSlot(equippedSlot).isEmpty()) {
							player.inventory.setInventorySlotContents(equippedSlot, player.getHeldItemOffhand());
							player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
						} else {
							ItemStack net = player.getHeldItemOffhand();
							player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
							if(!player.inventory.addItemStackToInventory(net)) {
								player.setHeldItem(EnumHand.OFF_HAND, net);
							}
						}
					}
					
					nbt.setBoolean("bl.fishing_rod.equipped", false);
					nbt.setInteger("bl.fishing_rod.equipped_slot", -1);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		
		if(stack.getItemDamage() == getMaxDamage(stack)) {
			if (!world.isRemote && player.fishEntity != null) {
				player.fishEntity.setDead();
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 1F, 1F);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("baited"))
				stack.getTagCompound().setBoolean("baited", false);
		}

		if (player.fishEntity != null) {
			int i = player.fishEntity.handleHookRetraction();
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundRegistry.BL_FISHING_REEL, SoundCategory.NEUTRAL, 1.0F, 1F);

			if (!world.isRemote && player.fishEntity != null) {
				//fixes stupid entity MobItem still being ridden after netted
				if(player.fishEntity.caughtEntity != null && !player.fishEntity.isRiding())
					player.fishEntity.caughtEntity = null;
				
				if (player.fishEntity.caughtEntity != null && stack.getTagCompound().hasKey("baited")) {
					if (stack.getTagCompound().getBoolean("baited"))
						stack.getTagCompound().setBoolean("baited", false);
					if (((EntityAnadia) player.fishEntity.caughtEntity).getStaminaTicks() % 20 == 0 && ((EntityAnadia) player.fishEntity.caughtEntity).getStaminaTicks() != 0) {
						stack.damageItem(i, player);
						world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundRegistry.BL_FISHING_ROD_CREAK, SoundCategory.NEUTRAL, 0.2F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					}
				}
				if (player.fishEntity.caughtEntity == null && (int)player.fishEntity.getDistance(player.fishEntity.getAngler()) > 0)
					stack.damageItem(i, player);

				if ((int)player.fishEntity.getDistance(player.fishEntity.getAngler()) <= 0 && !player.fishEntity.isRiding()) {
					player.fishEntity.setDead();
				}
			}
		} else {
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundRegistry.BL_FISHING_CAST, SoundCategory.NEUTRAL, 0.2F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote && player.fishEntity == null) {
				EntityBLFishHook entityFishHook = new EntityBLFishHook(world, player);
				if(stack.getTagCompound().getBoolean("baited"))
					entityFishHook.setBaited(true);
				world.spawnEntity(entityFishHook);
			}

			player.swingArm(handIn);
			player.addStat(StatList.getObjectUseStats(this));
		}
		
		ItemStack otherStack;
		if(handIn == EnumHand.MAIN_HAND) {
			otherStack = player.getHeldItemOffhand();
		} else {
			otherStack = player.getHeldItemMainhand();
		}
		
		if(!otherStack.isEmpty() && otherStack.getItem() == ItemRegistry.NET) {
			// Allow net to be used to catch fish
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the rod break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("baited");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
	
	private static Field f_ItemRenderer_equippedProgressMainHand;
	private static Field f_ItemRenderer_prevEquippedProgressMainHand;
	private static Field f_ItemRenderer_equippedProgressOffHand;
	private static Field f_ItemRenderer_prevEquippedProgressOffHand;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        
        if(player != null && player.fishEntity != null) {
        	ItemRenderer itemRenderer = Minecraft.getMinecraft().entityRenderer.itemRenderer;
        	
        	try {
        		if(f_ItemRenderer_equippedProgressMainHand == null) {
        			f_ItemRenderer_equippedProgressMainHand = ReflectionHelper.findField(ItemRenderer.class, "equippedProgressMainHand", "field_187469_f", "f");
        			f_ItemRenderer_prevEquippedProgressMainHand = ReflectionHelper.findField(ItemRenderer.class, "prevEquippedProgressMainHand", "field_187470_g", "g");
        			f_ItemRenderer_equippedProgressOffHand = ReflectionHelper.findField(ItemRenderer.class, "equippedProgressOffHand", "field_187471_h", "h");
        			f_ItemRenderer_prevEquippedProgressOffHand = ReflectionHelper.findField(ItemRenderer.class, "prevEquippedProgressOffHand", "field_187472_i", "i");
        		}
        		
		        ItemStack stackMainhand = player.getHeldItemMainhand();
		        if(!stackMainhand.isEmpty() && stackMainhand.getItem() instanceof ItemBLFishingRod) {
					f_ItemRenderer_equippedProgressMainHand.set(itemRenderer, 1.0f);
					f_ItemRenderer_prevEquippedProgressMainHand.set(itemRenderer, 1.0f);
		        }
		        
		        ItemStack stackOffhand = player.getHeldItemOffhand();
		        if(!stackOffhand.isEmpty() && stackOffhand.getItem() instanceof ItemBLFishingRod) {
		        	f_ItemRenderer_equippedProgressOffHand.set(itemRenderer, 1.0f);
		        	f_ItemRenderer_prevEquippedProgressOffHand.set(itemRenderer, 1.0f);
		        }
			} catch(IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
	}
}