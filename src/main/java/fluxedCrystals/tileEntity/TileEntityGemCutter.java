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
import fluxedCrystals.items.FCItems;
import fluxedCrystals.network.MessageGemRefiner;
import fluxedCrystals.network.PacketHandler;

/**
 * Created by Jared on 11/2/2014.
 */
public class TileEntityGemCutter extends TileEnergyBase implements IManaReceiver, ISidedInventory {

	public ItemStack[] items;

	private int cut = 0;
	private int timePerCut = 0;

	// -1 if not currently working on any valid recipe
	private int recipeIndex;

	public int mana;
	public int MAX_MANA;
	public boolean RF = true;

	public int getTimePerCut() {
		return timePerCut;
	}

	public void setTimePerCut(int timePerCut) {
		this.timePerCut = timePerCut;
	}

	public int getRecipeIndex() {
		return recipeIndex;
	}

	public void setRecipeIndex(int recipeIndex) {
		this.recipeIndex = recipeIndex;
	}

	public int getCut() {
		return cut;
	}

	public TileEntityGemCutter() {
		super(10000);

		MAX_MANA = getMaxStorage();
		mana = 0;
		items = new ItemStack[7];
	}

	public void updateEntity() {
		super.updateEntity();
		if (worldObj != null && !worldObj.isRemote) {
			if (storage.getEnergyStored() > 0) {
				if (!isUpgradeActive(new ItemStack(FCItems.upgradeMana)) && !isUpgradeActive(new ItemStack(FCItems.upgradeLP)) && !isUpgradeActive(new ItemStack(FCItems.upgradeEssentia))) {
					if (getStackInSlot(1) != null) {
						if (worldObj.getWorldTime() % getSpeed() == 0 && storage.getEnergyStored() >= getEffeciency() && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize()) {
							refine();
							return;
						}
					} else {
						if (worldObj.getWorldTime() % getSpeed() == 0 && storage.getEnergyStored() >= getEffeciency()) {
							refine();
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
		return "Gem Cutter";
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
			for (RecipeGemCutter r : RecipeRegistry.getGemCutterRecipes()) {
				if (r.getInput().isItemEqual(stack)) {
					return true;
				}
			}
		case 1:
			return false;

		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f) <= 64;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		
		boolean changedItem;
		if(items[i] == null || itemstack == null)
			changedItem = (items[i] == null) != (itemstack == null); // non-null to null, or vice versa
		else
			changedItem = !items[i].isItemEqual(itemstack);
		
		items[i] = itemstack;
		
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		
		if(i == 0 && changedItem)
			updateCurrentRecipe();
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
		cut = tags.getInteger("cut");
		setRecipeIndex(tags.getInteger("recipeIndex"));
		mana = tags.getInteger("mana");
		
		updateCurrentRecipe();
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
		tags.setInteger("cut", cut);
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

	public boolean refine() {
		if (getRecipeIndex() >= 0) {
			RecipeGemCutter recipe = RecipeRegistry.getGemCutterRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					cut++;
					storage.extractEnergy(250, false);

					if (cut >= recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						cut = 0;
					}
				}
				return true;
			}
		}
		cut = 0;
		return false;
	}

	public boolean refineMana() {
		if (getRecipeIndex() >= 0) {
			RecipeGemCutter recipe = RecipeRegistry.getGemCutterRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					cut++;
					mana -= 250;
					if (cut == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						addInventorySlotContents(1, out);
						out.stackSize = recipe.getOutputAmount();
						mana -= 500;
						cut = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		cut = 0;
		return false;
	}

	public boolean refineLP() {
		if (getRecipeIndex() >= 0) {
			RecipeGemCutter recipe = RecipeRegistry.getGemCutterRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					cut++;
					SoulNetworkHandler.syphonFromNetwork(getStackInSlot(6), 250 / 4);
					if (cut == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						out.stackSize = recipe.getOutputAmount();
						addInventorySlotContents(1, out);
						cut = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		cut = 0;
		return false;
	}

	public boolean refineEssentia() {
		if (getRecipeIndex() >= 0) {
			RecipeGemCutter recipe = RecipeRegistry.getGemCutterRecipes().get(recipeIndex);
			if (recipe.matchesExact(getStackInSlot(0))) {
				if (getStackInSlot(1) == null || getStackInSlot(1).isItemEqual(recipe.getOutput())) {
					decrStackSize(0, 1);
					cut++;
					if (cut == recipe.getInputamount()) {
						ItemStack out = recipe.getOutput().copy();
						addInventorySlotContents(1, out);
						out.stackSize = recipe.getOutputAmount();
						cut = 0;
						setRecipeIndex(-1);

					}
				}
				return true;
			}
		}
		cut = 0;
		return false;
	}

	public void updateCurrentRecipe() {
		int number = -1;
		setRecipeIndex(number);
		
		ItemStack inputStack = getStackInSlot(0);
		
		if (inputStack != null && inputStack.stackSize > 0)
			for (RecipeGemCutter recipe : RecipeRegistry.getGemCutterRecipes()) {
				number++;
				if (recipe.matchesExact(inputStack)) {
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

	@Override
	public boolean canRecieveManaFromBursts() {
		return true;
	}

	public int getEnergyRemainingScaled(int amount) {
		if (storage.getEnergyStored() == storage.getMaxEnergyStored()) {
			return storage.getMaxEnergyStored() - 1;
		}
		if (storage.getEnergyStored() == 0) {
			return 1;
		}
		return storage.getEnergyStored() * amount / storage.getMaxEnergyStored();

	}

	public double getEnergyColor() {
		double energy = storage.getEnergyStored();
		double maxEnergy = storage.getMaxEnergyStored();
		if (energy == energy) {
			return energy - 1;
		}
		if (energy == 0) {
			return 1;
		}
		return (energy / 255);
	}

	public double getManaColor() {
		if (getCurrentMana() == MAX_MANA) {
			return getCurrentMana() - 1;
		}
		if (getCurrentMana() == 0) {
			return 1;
		}
		return (mana / 255);
	}

	private static int[] slotsAll = { 0, 1, 2, 3, 4, 5, 6 };

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slotsAll;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if (isItemValidForSlot(slot, stack))
			for (RecipeGemCutter recipe : RecipeRegistry.getGemCutterRecipes()) {
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

}