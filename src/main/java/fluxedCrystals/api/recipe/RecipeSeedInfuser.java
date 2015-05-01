package fluxedCrystals.api.recipe;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeSeedInfuser {

	private ItemStack input;
	private ItemStack output;
	private ItemStack ingredient;
	private int inputAmount;
	
	public RecipeSeedInfuser(ItemStack ingredient, ItemStack input, ItemStack output, int inputAmount) {
		this.input = input;
		this.output = output;
		this.ingredient = ingredient;
		this.inputAmount = inputAmount;
	}

	public boolean matches(ItemStack ingredient, ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			String name = OreDictionary.getOreName(id);
			if (matches(name) && ingredient.isItemEqual(this.ingredient)) {
				return true;
			}
		}
		return stack != null && OreDictionary.itemMatches(stack, input, false);
	}

	public boolean matches(String oreDict) {
		ArrayList<ItemStack> stacks = OreDictionary.getOres(oreDict);
		for (ItemStack stack : stacks) {
			if (OreDictionary.itemMatches(stack, input, false) && ingredient.isItemEqual(this.ingredient)) {
				return true;
			}
		}
		return false;
	}

	public boolean matchesExact(ItemStack input, ItemStack ingredient) {
		return this.input.isItemEqual(ingredient) && this.ingredient.isItemEqual(input);
	}


	public ItemStack getInput() {
		ItemStack stack = input.copy();
		stack.stackSize = getInputamount();
		return stack;
	}

	public ItemStack getOutput() {
		return output.copy();
	}

	public ItemStack getIngredient() {
		return ingredient.copy();
	}

	public int getInputamount() {
		return inputAmount;
	}
}
