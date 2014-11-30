/**
 * This file is part of Backpack, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.backpack.client;

import com.almuradev.backpack.BackpackContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BackpackGui extends GuiContainer {

    private static final ResourceLocation LOCATION_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;

    public BackpackGui(IInventory upperInventory, IInventory lowerInventory) {
        super(new BackpackContainer(upperInventory, lowerInventory));
        this.upperChestInventory = upperInventory;
        this.lowerChestInventory = lowerInventory;
        this.allowUserInput = false;
        short var3 = 222;
        int var4 = var3 - 108;
        this.inventoryRows = lowerInventory.getSizeInventory() / 9;
        this.ySize = var4 + this.inventoryRows * 18;
    }

    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_) {
        this.fontRendererObj.drawString(this.lowerChestInventory.hasCustomInventoryName() ? this.lowerChestInventory.getInventoryName()
                                                                                          : I18n.format(this.lowerChestInventory.getInventoryName()),
                                        8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName()
                                                                                          : I18n.format(this.upperChestInventory.getInventoryName()),
                                        8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_,
                                                   int p_drawGuiContainerBackgroundLayer_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LOCATION_BACKGROUND);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(var4, var5 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);

        if (p_73869_2_ == ClientProxy.BINDING_OPEN_BACKPACK.getKeyCode()) {
            mc.thePlayer.closeScreen();
        }
    }
}
