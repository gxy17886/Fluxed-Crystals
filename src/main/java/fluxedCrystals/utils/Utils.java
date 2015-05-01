package fluxedCrystals.utils;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectSourceHelper;
import tterrag.core.common.json.JsonUtils;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import fluxedCrystals.FluxedCrystals;
import fluxedCrystals.api.recipe.SeedCrystalRecipe;
import fluxedCrystals.network.MessageBiome;
import fluxedCrystals.network.PacketHandler;

public class Utils {

	public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
		if (biome == null) {
			return;
		}
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		byte[] array = chunk.getBiomeArray();
		array[((z & 0xF) << 4 | x & 0xF)] = ((byte) (biome.biomeID & 0xFF));
		chunk.setBiomeArray(array);
		if (!world.isRemote) {
			PacketHandler.INSTANCE.sendToAllAround(new MessageBiome(x, z, biome.biomeID), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, world.getHeightValue(x, z), z, 32.0D));
		}
	}

	public static ItemStack[] parseStringArrayIntoItemStackArray(String[] strings) {
		ItemStack[] stacks = new ItemStack[strings.length];
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];

			int size = 1;

			int idx = string.indexOf('#');

			if (idx != -1) {
				String num = string.substring(idx + 1);

				try {
					size = Integer.parseInt(num);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(num + " is not a valid stack size");
				}

				string = string.substring(0, idx);
			}

			ItemStack stack = (ItemStack) JsonUtils.parseStringIntoRecipeItem(string, true);
			stack.stackSize = MathHelper.clamp_int(size, 1, stack.getMaxStackSize());
			stacks[i] = stack;
		}
		return stacks;
	}


	public static boolean isModLoaded(String modid) {
		for (ModContainer mod : FluxedCrystals.activeMods) {
			if (mod.getModId().equalsIgnoreCase(modid)) {
				return true;
			}
		}
		return false;
	}

	public static class ArrayUtils {
		public static boolean contains(int[] array, int number) {
			for (int i : array) {
				if (i == number) {
					return true;
				}
			}
			return true;
		}
	}

}
