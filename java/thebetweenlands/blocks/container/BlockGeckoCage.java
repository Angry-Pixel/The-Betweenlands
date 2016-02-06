package thebetweenlands.blocks.container;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectManager.AspectItem;
import thebetweenlands.herblore.aspects.DiscoveryContainer;
import thebetweenlands.herblore.aspects.DiscoveryContainer.AspectDiscovery;
import thebetweenlands.herblore.aspects.DiscoveryContainer.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityGeckoCage;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class BlockGeckoCage extends BlockContainer {
	public BlockGeckoCage() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.geckoCage");
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockTextureName("thebetweenlands:weedwood");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.weedwood.getIcon(0, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(x, y, z) instanceof TileEntityGeckoCage) {
			TileEntityGeckoCage tile = (TileEntityGeckoCage) world.getTileEntity(x, y, z);

			if(player.isSneaking())
				return false;

			if(player.getHeldItem() != null) {
				ItemStack stack = player.getHeldItem();
				if(stack.getItem() == BLItemRegistry.gecko) {
					if(!tile.hasGecko()) {
						tile.addGecko(12);
						if(!player.capabilities.isCreativeMode) --stack.stackSize;
						return true;
					}
					return false;
				}
				if(tile.getAspectType() == null) {
					if(tile.hasGecko()) {
						if(AspectManager.hasDiscoveryProvider(player)) {
							AspectManager manager = AspectManager.get(world);
							List<Aspect> aspects = manager.getDiscoveredAspects(stack, null);
							if(aspects.size() > 0) {
								DiscoveryContainer mergedKnowledge = AspectManager.getMergedDiscoveryContainer(player);
								AspectItem aspectItem = new AspectItem(stack);
								AspectDiscovery discovery = mergedKnowledge.discover(manager, aspectItem);
								switch(discovery.result) {
								case NEW:
								case LAST:
									AspectManager.addDiscoveryToContainers(player, aspectItem, discovery.discovered.type);
									if(!world.isRemote) {
										tile.setAspectType(discovery.discovered.type, 600);
										if(ConfigHandler.DEBUG) {
											player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery." + discovery.discovered.type.getName()));
											player.addChatMessage(new ChatComponentText("Aspect: " + discovery.discovered.type.getName()));
										} else {
											player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery." + discovery.discovered.type.getName()));
										}
										if(discovery.result == EnumDiscoveryResult.LAST) {
											player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.last"));
										} else {
											player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.more"));
										}
										if(!player.capabilities.isCreativeMode) --stack.stackSize;
									}
									return true;
								case END:
									//already all discovered
									if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.end"));
									return false;
								default:
									//no aspects
									if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.none"));
									return false;
								}
							} else {
								//no aspects
								if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.none"));
								return false;
							}
						} else {
							//no herblore book
							if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.book.none"));
							return false;
						}
					} else {
						//no gecko
						if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.gecko.none"));
						return false;
					}
				} else {
					//recovering
					if(!world.isRemote) player.addChatMessage(new ChatComponentTranslation("chat.aspect.discovery.gecko.recovering"));
					return false;
				}
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(10) == 0 && world.getTileEntity(x, y, z) instanceof TileEntityGeckoCage) {
			TileEntityGeckoCage cage = (TileEntityGeckoCage) world.getTileEntity(x, y, z);
			if (cage.hasGecko()) {
				float xx = (float) x + 0.5F;
				float yy = (float) (y + 0.1F + rand.nextFloat() * 0.5F);
				float zz = (float) z + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
				if(cage.getAspectType() != null) {
					BLParticle.BUBBLE_INFUSION.spawn(world, (double) (xx - fixedOffset), (double) yy, (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.BUBBLE_INFUSION.spawn(world, (double) (xx + fixedOffset), (double) yy, (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.BUBBLE_INFUSION.spawn(world, (double) (xx + randomOffset), (double) yy, (double) (zz - fixedOffset), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.BUBBLE_INFUSION.spawn(world, (double) (xx + randomOffset), (double) yy, (double) (zz + fixedOffset), 0.0D, 0.0D, 0.0D, 0);
				} else {
					BLParticle.RUSTLE_LEAF.spawn(world, (double) (xx - fixedOffset / 2.0F), (double) yy, (double) (zz + randomOffset / 2.0F), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.RUSTLE_LEAF.spawn(world, (double) (xx + fixedOffset / 2.0F), (double) yy, (double) (zz + randomOffset / 2.0F), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.RUSTLE_LEAF.spawn(world, (double) (xx + randomOffset / 2.0F), (double) yy, (double) (zz - fixedOffset / 2.0F), 0.0D, 0.0D, 0.0D, 0);
					BLParticle.RUSTLE_LEAF.spawn(world, (double) (xx + randomOffset / 2.0F), (double) yy, (double) (zz + fixedOffset / 2.0F), 0.0D, 0.0D, 0.0D, 0);
				}
			}
		}
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityGeckoCage();
	}
}