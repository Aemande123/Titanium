package com.hrznstudio.titanium.client.gui;

import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.container.ContainerTileBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class GuiContainerTile extends GuiContainer {

    private final ContainerTileBase containerTileBase;
    private AssetProvider assetProvider;
    private int x;
    private int y;
    private List<IGuiAddon> addonList;

    public GuiContainerTile(ContainerTileBase containerTileBase) {
        super(containerTileBase);
        this.containerTileBase = containerTileBase;
        this.assetProvider = AssetProvider.get(containerTileBase.getTile().getClass());
        this.xSize = assetProvider.getBackground().getArea().width;
        this.ySize = assetProvider.getBackground().getArea().height;
        this.addonList = new ArrayList<>();
        this.addonList.addAll(containerTileBase.getTile().getGuiAddons());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        //BG RENDERING
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(assetProvider.getBackground().getResourceLocation());
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        addonList.forEach(iGuiAddon -> iGuiAddon.drawGuiContainerBackgroundLayer(this, partialTicks, mouseX, mouseY));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        addonList.forEach(iGuiAddon -> iGuiAddon.drawGuiContainerForegroundLayer(this, mouseX, mouseY));
    }

    public AssetProvider getAssetProvider() {
        return assetProvider;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}