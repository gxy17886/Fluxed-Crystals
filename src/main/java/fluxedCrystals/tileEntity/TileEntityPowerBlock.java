package fluxedCrystals.tileEntity;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import fluxedCrystals.api.CrystalBase;
import fluxedCrystals.api.RecipeRegistry;
import fluxedCrystals.api.recipe.RecipeGemRefiner;
import fluxedCrystals.blocks.crystal.BlockCrystal;
import fluxedCrystals.items.FCItems;

/**
 * Created by Jared on 11/2/2014.
 */
public class TileEntityPowerBlock extends TileEnergyBase implements ISidedInventory {

	public ItemStack[] items;;

	public TileEntityPowerBlock() {
		super(100000);
		items = new ItemStack[3];
	}

	public boolean canUpdate() {
		return false;
	}

	public boolean growPlant(World world, boolean night) {
		if (world != null)
			if (world.getBlock(xCoord, yCoord + 1, zCoord) instanceof CrystalBase) {
				TileEntityCrystal crystal = (TileEntityCrystal) world.getTileEntity(xCoord, yCoord + 1, zCoord);
				return ((CrystalBase) world.getBlock(xCoord, yCoord + 1, zCoord)).growCrop(world, xCoord, yCoord + 1, zCoord, world.rand, night);
			}
		return false;
	}

	public BlockCrystal getCrop(World world) {
		return world.getBlock(xCoord, yCoord + 1, zCoord) != null && world.getBlock(xCoord, yCoord + 1, zCoord) instanceof BlockCrystal ? (BlockCrystal) world.getBlock(xCoord, yCoord + 1, zCoord) : null;
	}

	public TileEntityCrystal getCropTile(World world) {
		return world.getTileEntity(xCoord, yCoord + 1, zCoord) != null && world.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TileEntityCrystal ? (TileEntityCrystal) world.getTileEntity(xCoord, yCoord + 1, zCoord) : null;
	}

	/* NBT */
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		readInventoryFromNBT(tags);
	}

	public void readInventoryFromNBT(NBTTagCompound tags) {
		NBTTagList nbttaglist = tags.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int iter = 0; iter < nbttaglist.tagCount(); iter++) {
			NBTTagCompound tagList = (NBTTagCompound) nbttaglist.getCompoundTagAt(iter);
			byte slotID = tagList.getByte("Slot");
			if (slotID >= 0 && slotID < items.length) {
				items[slotID] = ItemStack.loadItemStackFromNBT(tagList);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
		writeInventoryToNBT(tags);
	}

	public void writeInventoryToNBT(NBTTagCompound tags) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int iter = 0; iter < items.length; iter++) {
			if (items[iter] != null) {
				NBTTagCompound tagList = new NBTTagCompound();
				tagList.setByte("Slot", (byte) iter);
				items[iter].writeToNBT(tagList);
				nbttaglist.appendTag(tagList);
			}
		}

		tags.setTag("Items", nbttaglist);
	}

	@Override
	public EnumSet<ForgeDirection> getValidOutputs() {
		return EnumSet.noneOf(ForgeDirection.class);
	}

	@Override
	public EnumSet<ForgeDirection> getValidInputs() {
		EnumSet<ForgeDirection> set = EnumSet.allOf(ForgeDirection.class);
		set.remove(ForgeDirection.UP);
		return set;
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack decrStackSize(int i, int count) {
		ItemStack itemstack = getStackInSlot(i);

		if (itemstack != null) {
			if (itemstack.stackSize <= count) {
				setInventorySlotContents(i, null);
			} else {
				itemstack = itemstack.splitStack(count);

			}
		}

		return itemstack;
	}

	@Override
	public String getInventoryName() {
		return "Power Soil";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1) {

		return items[par1];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack item = getStackInSlot(i);
		setInventorySlotContents(i, item);
		return item;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		items[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}

	public ItemStack getUpgradeOne() {
		return items[0];
	}

	public ItemStack getUpgradeTwo() {
		return items[1];
	}

	public ItemStack getUpgradeThree() {
		return items[2];
	}

	public boolean addUpgrade(ItemStack stack) {
		ItemStack upgrade = stack.copy();
		if (getUpgradeOne() == null) {
			upgrade.stackSize = 1;
			setInventorySlotContents(0, upgrade);
			return true;
		}
		if (getUpgradeTwo() == null) {
			upgrade.stackSize = 1;
			setInventorySlotContents(1, upgrade);
			return true;
		}
		if (getUpgradeThree() == null) {
			upgrade.stackSize = 1;
			setInventorySlotContents(2, upgrade);
			return true;
		}
		return false;
	}

	public ArrayList<ItemStack> getUpgrades() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(getUpgradeOne());
		list.add(getUpgradeTwo());
		list.add(getUpgradeThree());
		return list;
	}

	public ItemStack removeUpgrade() {
		ItemStack stack = null;
		if (getStackInSlot(2) != null && stack == null) {
			stack = getStackInSlot(2).copy();
			setInventorySlotContents(2, null);
		}
		if (getStackInSlot(1) != null && stack == null) {
			stack = getStackInSlot(1).copy();
			setInventorySlotContents(1, null);
		}
		if (getStackInSlot(0) != null && stack == null) {
			stack = getStackInSlot(0).copy();
			setInventorySlotContents(0, null);
		}
		return stack;
	}

	public int getSpeed() {
		int speed = 8;
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeSpeed))) {
					speed += 2;
				}
			}
		}
		return speed;
	}

	public int getEffeciency() {
		int eff = 0;
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeEffeciency))) {
					eff += 15;
				}
			}
		}
		if (eff == 0) {
			eff = 1;
		}
		return eff;
	}

	public boolean isUpgradeActive(ItemStack stack) {
		return (getUpgradeOne() != null && getUpgradeOne().isItemEqual(stack)) || (getUpgradeTwo() != null && getUpgradeTwo().isItemEqual(stack)) || (getUpgradeThree() != null && getUpgradeThree().isItemEqual(stack));
	}

	public int getUpgradeDrain(int idx) {
		int energy = RecipeRegistry.getPowerPerStage(idx);

		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeNight))) {
					energy += energy / 15;
				}
				if (item.isItemEqual(new ItemStack(FCItems.upgradeSpeed))) {
					energy += energy / 12;
				}
			}
		}

		if (isUpgradeActive(new ItemStack(FCItems.upgradeEffeciency))) {
			energy /= getEffeciency();
		}

		return energy;
	}
}
