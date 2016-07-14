package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.IEntityBL;

public class BlockBasicVine extends BlockVine /*implements ISickleHarvestable, ISyrmoriteShearable*/ {
    public boolean isPoisonous;
    public BlockBasicVine(boolean isPoisonous){
        this.isPoisonous = isPoisonous;
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(BLCreativeTabs.PLANTS);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        //TODO add && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entity) when elxirs are added
        if (!worldIn.isRemote && !(entityIn instanceof IEntityBL) && entityIn instanceof EntityLivingBase && worldIn.rand.nextInt(200) == 0 ) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 50, 25));
        }
    }


    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return false;
    }

    /*@Override
    public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
        dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.POISON_IVY));
        return dropList;
    }*/

}
