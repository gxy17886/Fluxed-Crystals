package fluxedCrystals.tileEntity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectSourceHelper;
import vazkii.botania.api.mana.IManaReceiver;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import fluxedCrystals.FluxedCrystals;
import fluxedCrystals.api.RecipeRegistry;
import fluxedCrystals.api.recipe.RecipeGemCutter;
import fluxedCrystals.api.recipe.RecipeGemRefiner;
import fluxedCrystals.items.FCItems;
import fluxedCrystals.network.MessageGemRefiner;
import fluxedCrystals.network.PacketHandler;

/**
 * Created by Jared on 11/2/2014.
 */
public class TileEntityGemRefiner extends TileEnergyBase implements IInventory, IManaReceiver, ISidedInventory {

	public ItemStack[] items;

	private boolean refining = false;
	private int refined = 0;

	public int getRecipeIndex() {
		return recipeIndex;
	}

	public void setRecipeIndex(int recipeIndex) {
		this.recipeIndex = recipeIndex;
	}

	public boolean isRefining() {
		return refining;
	}

	public int getRefined() {
		return refined;
	}

	private int recipeIndex;

	private int mana;
	private int MAX_MANA;
	private boolean RF = true;
	private int energy = 0;

	public TileEntityGemRefiner() {
		super(10000);
		MAX_MANA = getMaxStorage();
		mana = 0;
		items = new ItemStack[7];

	}

	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote && getStackInSlot(0) != null && !refining){
            PacketHandler.INSTANCE.sendToDimension(new MessageGemRefiner(xCoord, yCoord, zCoord), worldObj.provider.dimensionId);
		}
		if (worldObj.isRemote && getStackInSlot(0) != null && !refining) {
			PacketHandler.INSTANCE.sendToServer(new MessageGemRefiner(xCoord, yCoord, zCoord));
		}
		if (getStackInSlot(0) != null)
			if (worldObj != null) {
				if (storage.getEnergyStored() > 0) {
					if (!isUpgradeActive(new ItemStack(FCItems.upgradeMana)) && !isUpgradeActive(new ItemStack(FCItems.upgradeLP)) && !isUpgradeActive(new ItemStack(FCItems.upgradeEssentia))) {
						if (getStackInSlot(1) != null) {
							if (refining && worldObj.getWorldTime() % getSpeed() == 0 && storage.getEnergyStored() >= getEffeciency() && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize()) {
								storage.extractEnergy(refineShard(), false);
								return;
							}
						} else {
							if (refining && worldObj.getWorldTime() % getSpeed() == 0 && storage.getEnergyStored() >= getEffeciency()) {
								storage.extractEnergy(refineShard(), false);
								return;
							}
						}
					}
				}
			}
	}

	public boolean isUpgradeActive(ItemStack stack) {
		return (getUpgradeSlotOne() != null && getUpgradeSlotOne().isItemEqual(stack)) || (getUpgradeSlotTwo() != null && getUpgradeSlotTwo().isItemEqual(stack)) || (getUpgradeSlotThree() != null && getUpgradeSlotThree().isItemEqual(stack));
	}

	public ArrayList<ItemStack> getUpgrades() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(getUpgradeSlotOne());
		list.add(getUpgradeSlotTwo());
		list.add(getUpgradeSlotThree());
		return list;
	}

	public int getSpeed() {
		int speed = 100;
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeSpeed))) {
					speed -= 20;
				}
			}
		}
		return speed;
	}

	public int getEffeciency() {
		int eff = 250;
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeSpeed))) {
					eff += 15;
				}

			}
		}
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeSpeed))) {
					eff += 15;
				}

			}
		}
		for (ItemStack item : getUpgrades()) {
			if (item != null) {
				if (item.isItemEqual(new ItemStack(FCItems.upgradeEffeciency))) {
					eff -= 25;
				}
			}
		}

		if (eff == 0) {
			eff = 1;
		}
		return eff;
	}

	public ItemStack getUpgradeSlotOne() {
		return getStackInSlot(2);
	}

	public ItemStack getUpgradeSlotTwo() {
		return getStackInSlot(3);
	}

	public ItemStack getUpgradeSlotThree() {
		return getStackInSlot(4);
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
		return "Gem Refiner";
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
		if (stack == null) {
			return false;
		}
		switch (slot) {
		default:
			return false;

		case 0:
			for (RecipeGemRefiner r : RecipeRegistry.getGemRefinerRecipes()) {
				if (r.getInput().isItemEqual(stack)) {
					return true;
				}
			}
		case 1:
			return false;

		}
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

	public boolean addInventorySlotContents(int i, ItemStack itemstack) {
		if (items[i] != null) {

			if (items[i].isItemEqual(itemstack)) {
				items[i].stackSize += itemstack.stackSize;
			}
			if (items[i].stackSize > getInventoryStackLimit()) {
				items[i].stackSize = getInventoryStackLimit();
			}
		} else {
			setInventorySlotContents(i, itemstack);
		}
		return false;
	}

	/* NBT */
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		readInventoryFromNBT(tags);
		refining = tags.getBoolean("refining");
		refined = tags.getInteger("refined");
		setRecipeIndex(tags.getInteger("recipeIndex"));
		mana = tags.getInteger("mana");
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
		tags.setBoolean("refining", refining);
		tags.setInteger("refined", refined);
		tags.setInteger("recipeIndex", getRecipeIndex());
		tags.setInteger("mana", mana);
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

	public boolean addItemToSlot(int slotNumber, ItemStack stack) {
		boolean returnBool = false;
		if (stack != null) {
			if (getStackInSlot(slotNumber) == null || (getStackInSlot(slotNumber).isItemEqual(stack)) && (getStackInSlot(slotNumber).getMaxStackSize() - getStackInSlot(slotNumber).stackSize - stack.stackSize) > 0) {
				ItemStack out = stack.copy();
				out.stackSize = stack.stackSize;
				if (getStackInSlot(slotNumber) != null) {
					out.stackSize += getStackInSlot(slotNumber).stackSize;
				}
				setInventorySlotContents(slotNumber, out);
				returnBool = true;
			}
		}

		return returnBool;
	}

	public int refineShard() {
		int energyUsed = 0;
		if (getRecipeIndex() >= 0) {
			RecipeGemRefiner recipe = RecipeRegistry.getGemRefinerRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0)) && recipe.getInputamount() <= getStackInSlot(0).stackSize) {
				refined++;
				ItemStack out = recipe.getOutput().copy();
				out.stackSize = recipe.getOutputAmount();
                
				PacketHandler.INSTANCE.sendToDimension(new MessageGemRefiner(xCoord, yCoord, zCoord, recipeIndex), worldObj.provider.dimensionId);
                
				if (addItemToSlot(1, out)) {
					decrStackSize(0, recipe.getInputamount());
					refining = false;
					refined = 0;
					setRecipeIndex(-1);
					energyUsed = (250 * recipe.getInputamount());
				}
			}
		}

		return energyUsed;
	}

	public boolean refine() {
		if (getRecipeIndex() >= 0) {
			RecipeGemRefiner recipe = RecipeRegistry.getGemRefinerRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					refined++;
					storage.extractEnergy(250, false);
					if (refined == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						refining = false;
						refined = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		refined = 0;
		setRecipeIndex(-1);
		refining = false;
		return false;
	}

	public boolean refineMana() {
		if (getRecipeIndex() >= 0) {
			RecipeGemRefiner recipe = RecipeRegistry.getGemRefinerRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					refined++;
					mana -= 250;
					if (refined == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						refining = false;
						refined = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		refined = 0;
		setRecipeIndex(-1);
		refining = false;
		return false;
	}

	public boolean refineLP() {
		if (getRecipeIndex() >= 0) {
			RecipeGemRefiner recipe = RecipeRegistry.getGemRefinerRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					refined++;
					SoulNetworkHandler.syphonFromNetwork(getStackInSlot(6), 250 / 4);
					if (refined == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						refining = false;
						refined = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		refined = 0;
		setRecipeIndex(-1);
		refining = false;
		return false;
	}

	public boolean refineEssentia() {
		if (getRecipeIndex() >= 0) {
			RecipeGemRefiner recipe = RecipeRegistry.getGemRefinerRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					refined++;
					if (refined == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						refining = false;
						refined = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		refined = 0;
		setRecipeIndex(-1);
		refining = false;
		return false;
	}

	public void setRefining(boolean infusing) {
		this.refining = infusing;
		int number = -1;
		setRecipeIndex(number);
		if (getStackInSlot(0) != null)
			for (RecipeGemRefiner recipe : RecipeRegistry.getGemRefinerRecipes()) {
				number++;
				if (recipe.matchesExact(getStackInSlot(0))) {
					setRecipeIndex(number);
					break;
				}
			}
	}

	@Override
	public EnumSet<ForgeDirection> getValidOutputs() {
		return EnumSet.noneOf(ForgeDirection.class);
	}

	@Override
	public EnumSet<ForgeDirection> getValidInputs() {
		return EnumSet.allOf(ForgeDirection.class);
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean isFull() {
		return mana == MAX_MANA;
	}

	@Override
	public void recieveMana(int mana) {
		if (!isFull()) {
			this.mana += mana;
		}
		if (getCurrentMana() > MAX_MANA) {
			this.mana = MAX_MANA;
		}
	}

	private static int[] slotsAll = { 0, 1, 2, 3, 4, 5, 6 };

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slotsAll;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if (isItemValidForSlot(slot, stack))
			for (RecipeGemRefiner recipe : RecipeRegistry.getGemRefinerRecipes()) {
				if (recipe.getInput().isItemEqual(stack)) {
					return true;
				}
			}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return slot != 0 && slot != 2 && slot != 3 && slot != 4;
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return true;
	}
}