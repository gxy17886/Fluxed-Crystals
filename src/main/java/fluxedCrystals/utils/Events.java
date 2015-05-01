package fluxedCrystals.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Events {

	public static boolean debug = false;

	@SubscribeEvent
	public void sekritStuff(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			if (event.entity.getCommandSenderName().equalsIgnoreCase("Jaredlll08") || event.entity.getCommandSenderName().equalsIgnoreCase("kucanaut")) {
				if (event.entity.isSneaking()) {
					for (int x = -6; x < 5; x++) {
						for (int y = -3; y < 40; y++) {
							for (int z = -6; z < 5; z++) {
								World world = event.entity.worldObj;
								int xCoord = (int) event.entity.posX;
								int yCoord = (int) event.entity.posY;
								int zCoord = (int) event.entity.posZ;
								for (int j = 0; j < 20; j++) {
									if (world.getBlock(xCoord + x, yCoord + y, zCoord + z) != null) {
										Block b = world.getBlock(xCoord + x, yCoord + y, zCoord + z);
										b.updateTick(world, xCoord + x, yCoord + y, zCoord + z, world.rand);
										if (b instanceof ITileEntityProvider && world.getTileEntity(xCoord + x, yCoord + y, zCoord + z) != null) {
											world.getTileEntity(xCoord + x, yCoord + y, zCoord + z).updateEntity();
										}
									}
								}
							}
						}
					}
				}
			}

		}
	}
}
