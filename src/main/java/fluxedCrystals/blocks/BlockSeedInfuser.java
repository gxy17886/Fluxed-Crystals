package fluxedCrystals.blocks;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import fluxedCrystals.FluxedCrystals;
import fluxedCrystals.ModProps;
import fluxedCrystals.tileEntity.TileEntityGemCutter;
import fluxedCrystals.tileEntity.TileEntityGemRefiner;
import fluxedCrystals.tileEntity.TileEntitySeedInfuser;

/**
 * Created by Jared on 11/2/2014.
 */
public class BlockSeedInfuser extends Block implements ITileEntityProvider {

	public IIcon[] textures;

	public BlockSeedInfuser() {
		super(Material.anvil);
		setHarvestLevel("pickaxe", 2);
		setHardness(2.0f);
	}

	public int getRenderType() {
		return FluxedCrystals.seedInfuserRenderID;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float par7, float par8, float par9) {
		if (!world.isRemote)
			player.openGui(FluxedCrystals.instance, 1, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntitySeedInfuser();
	}

	public void registerBlockIcons(IIconRegister icon) {
		textures = new IIcon[2];
		textures[0] = icon.registerIcon(ModProps.modid + ":InfuserOff");
		textures[1] = icon.registerIcon(ModProps.modid + ":InfuserOnOverlay");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return this.textures[0];
	}

	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		TileEntitySeedInfuser tile = (TileEntitySeedInfuser) world.getTileEntity(x, y, z);
		if (tile != null) {
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				if (tile.getStackInSlot(i) != null)
					dropBlockAsItem(world, x, y, z, tile.getStackInSlot(i));
			}
		}
		
	}
}
