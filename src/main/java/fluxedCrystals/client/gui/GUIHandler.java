package fluxedCrystals.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import fluxedCrystals.FluxedCrystals;
import fluxedCrystals.client.gui.gemCutter.ContainerGemCutter;
import fluxedCrystals.client.gui.gemCutter.GUIGemCutter;
import fluxedCrystals.client.gui.gemRefiner.ContainerGemRefiner;
import fluxedCrystals.client.gui.gemRefiner.GUIGemRefiner;
import fluxedCrystals.client.gui.seedInfuser.ContainerSeedInfuser;
import fluxedCrystals.client.gui.seedInfuser.GUISeedInfuser;
import fluxedCrystals.tileEntity.TileEntityGemCutter;
import fluxedCrystals.tileEntity.TileEntityGemRefiner;
import fluxedCrystals.tileEntity.TileEntitySeedInfuser;

public class GUIHandler implements IGuiHandler {

	public GUIHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(FluxedCrystals.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {



		case 1:
			if (te != null && te instanceof TileEntitySeedInfuser) {
				return new ContainerSeedInfuser(player.inventory, (TileEntitySeedInfuser) te);
			}
			break;

		case 6:
			if (te != null && te instanceof TileEntityGemRefiner) {
				return new ContainerGemRefiner(player.inventory, (TileEntityGemRefiner) te);
			}
			break;
		case 7:
			if (te != null && te instanceof TileEntityGemCutter) {
				return new ContainerGemCutter(player.inventory, (TileEntityGemCutter) te);
			}
			break;
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
		case 1:
			if (te != null && te instanceof TileEntitySeedInfuser) {
				return new GUISeedInfuser(player.inventory, (TileEntitySeedInfuser) te);
			}
			break;
		case 6:
			if (te != null && te instanceof TileEntityGemRefiner) {
				return new GUIGemRefiner(player.inventory, (TileEntityGemRefiner) te);
			}
			break;
		case 7:
			if (te != null && te instanceof TileEntityGemCutter) {
				return new GUIGemCutter(player.inventory, (TileEntityGemCutter) te);
			}
			break;
		}

		return null;
	}

}
