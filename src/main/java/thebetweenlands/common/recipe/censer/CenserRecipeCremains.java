package thebetweenlands.common.recipe.censer;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityCenser;

public class CenserRecipeCremains extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "cremains");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return EnumItemMisc.CREMAINS.isItemOf(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Void context, int amountLeft, TileEntity censer, boolean running, float effectStrength, double x, double y, double z, float partialTicks) {
		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.13F;
			float inScattering = 0.004F * effectStrength;
			float extinction = 0.15F;

			AxisAlignedBB fogArea = new AxisAlignedBB(censer.getPos()).grow(6, 0.1D, 6).expand(0, 32, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.2F, fogBrightness * 0.1F));
		}
	}

	private List<EntityLivingBase> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(6, 0.1D, 6).expand(0, 12, 0));
	}

	@Override
	public int update(Void context, int amountLeft, TileEntity censer) {
		World world = censer.getWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 10 == 0) {
			BlockPos pos = censer.getPos();

			List<EntityLivingBase> affected = this.getAffectedEntities(world, pos);
			for(EntityLivingBase living : affected) {
				if(!living.isInWater() && !living.isEntityInvulnerable(DamageSource.IN_FIRE) && !living.isImmuneToFire() && (living instanceof EntityPlayer == false || !((EntityPlayer) living).isCreative())) {
					living.setFire(1);
				}
			}
		}

		return 0;
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		boolean isDeathFromCenser = false;

		if(entity != null && entity.isBurning() && event.getSource().isFireDamage()) {
			int sx = MathHelper.floor(entity.posX - 6) >> 4;
			int sz = MathHelper.floor(entity.posZ - 6) >> 4;
			int ex = MathHelper.floor(entity.posX + 6) >> 4;
			int ez = MathHelper.floor(entity.posZ + 6) >> 4;

			check : for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					Chunk chunk = entity.world.getChunk(cx, cz);

					for(Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
						TileEntity tile = entry.getValue();

						if(tile instanceof TileEntityCenser) {
							TileEntityCenser censer = (TileEntityCenser) tile;

							if(censer.isRecipeRunning() && ((ICenserRecipe<?>) censer.getCurrentRecipe()) instanceof CenserRecipeCremains) {
								isDeathFromCenser = true;
								break check;
							}
						}
					}
				}
			}
		}

		if(isDeathFromCenser) {
			Iterator<EntityItem> dropsIT = event.getDrops().iterator();

			while(dropsIT.hasNext()) {
				dropsIT.next();

				if(entity.world.rand.nextBoolean()) {
					dropsIT.remove();
				}
			}

			int cremains = entity.world.rand.nextInt(3);

			for(int i = 0; i < cremains; i++) {
				event.getDrops().add(new EntityItem(entity.world, entity.posX, entity.posY + entity.height / 2, entity.posZ, EnumItemMisc.CREMAINS.create(1)));
			}
		}
	}

	@Override
	public int getConsumptionDuration(Void context, int amountLeft, TileEntity censer) {
		//1.6 min. / item
		return 3;
	}

	@Override
	public int getEffectColor(Void context, int amountLeft, TileEntity censer, EffectColorType type) {
		return 0xFF500000;
	}
}
