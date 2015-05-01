package fluxedCrystals.client.gui.gemCutter;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import fluxedCrystals.ModProps;
import fluxedCrystals.items.FCItems;
import fluxedCrystals.tileEntity.TileEntityGemCutter;

public class GUIGemCutter extends GuiContainer {

	private TileEntityGemCutter tile;
	private int energyOffset = 0;
	private int cut = 0;
	private int sawX;
	private int sawY;
	private boolean sawRange = false;

	public GUIGemCutter(InventoryPlayer invPlayer, TileEntityGemCutter tile2) {
		super(new ContainerGemCutter(invPlayer, tile2));

		xSize = 176;
		ySize = 166;
		this.tile = tile2;

	}

	private static final ResourceLocation texture = new ResourceLocation(ModProps.modid, "textures/gui/cutrefine.png");

	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
		sawX = guiLeft + 90;
		sawY = guiTop + 37;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		energyOffset++;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(guiLeft + 14, guiTop + 15, 177, 4, 14, 42);
		if (tile.getEnergyStored() > 0 && !tile.isUpgradeActive(new ItemStack(FCItems.upgradeMana)) && !tile.isUpgradeActive(new ItemStack(FCItems.upgradeEssentia)) && !tile.isUpgradeActive(new ItemStack(FCItems.upgradeLP))) {
			GL11.glColor4d(tile.getEnergyColor() / 10000, tile.getEnergyColor() / 10000, tile.getEnergyColor() / 10000, 1f);
			drawTexturedModalRect(guiLeft + 14, guiTop + 15, 193, 4, 14, 42);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
		if (tile.isUpgradeActive(new ItemStack(FCItems.upgradeMana))) {
			drawTexturedModalRect(guiLeft + 14, guiTop + 15, 193, 47, 14, 42);
		}
		if (tile.isUpgradeActive(new ItemStack(FCItems.upgradeLP))) {
			drawTexturedModalRect(guiLeft + 14, guiTop + 15, 193, 90, 14, 42);
		}
		if (tile.isUpgradeActive(new ItemStack(FCItems.upgradeEssentia))) {
			drawTexturedModalRect(guiLeft + 14, guiTop + 15, 193, 133, 14, 42);
		}
		GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
		if (tile.getRecipeIndex() >= 0 && tile.getStackInSlot(0) != null) {
			if (energyOffset % 20 == 0) {

				if (sawRange) {
					sawX = sawX + 5;
					sawY = sawY - 2;
					sawRange = false;
				} else {
					sawX = sawX + 5;
					sawY = sawY + 2;
					sawRange = true;
				}
			}
			if (sawX >= guiLeft + 105) {
				sawX = guiLeft + 62;
			}
			RenderItem.getInstance().renderItemIntoGUI(fontRendererObj, Minecraft.getMinecraft().getTextureManager(), tile.getStackInSlot(0), sawX, sawY);

		}
	}
}
