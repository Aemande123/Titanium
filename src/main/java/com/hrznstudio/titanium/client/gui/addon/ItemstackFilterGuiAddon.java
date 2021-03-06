/*
 * This file is part of Titanium
 * Copyright (C) 2019, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.client.gui.addon;

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.client.gui.addon.interfaces.IClickable;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import com.hrznstudio.titanium.filter.ItemstackFilter;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.awt.*;
import java.util.Objects;

public class ItemstackFilterGuiAddon extends BasicGuiAddon implements IClickable {

    private final ItemstackFilter filter;

    public ItemstackFilterGuiAddon(ItemstackFilter filter) {
        super(filter.getFilterSlots()[0].getX(), filter.getFilterSlots()[0].getY());
        this.filter = filter;
    }

    @Override
    public int getXSize() {
        return filter.getFilterSlots()[filter.getFilterSlots().length - 1].getX() + 17;
    }

    @Override
    public int getYSize() {
        return filter.getFilterSlots()[filter.getFilterSlots().length - 1].getY() + 17;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        for (FilterSlot<ItemStack> filterSlot : filter.getFilterSlots()) {
            if (filterSlot != null) {
                Color color = new Color(filterSlot.getColor());
                AssetUtil.drawAsset(screen, Objects.requireNonNull(provider.getAsset(AssetTypes.SLOT)), guiX + filterSlot.getX(), guiY + filterSlot.getY());
                AbstractGui.fill(guiX + filterSlot.getX() + 1, guiY + filterSlot.getY() + 1,
                        guiX + filterSlot.getX() + 17, guiY + filterSlot.getY() + 17, new Color(color.getRed(), color.getGreen(), color.getBlue(), 256 / 2).getRGB());
                GlStateManager.color4f(1, 1, 1, 1);
                if (!filterSlot.getFilter().isEmpty()) {
                    RenderHelper.enableGUIStandardItemLighting();
                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(filterSlot.getFilter(), filterSlot.getX() + guiX + 1, filterSlot.getY() + guiY + 1);
                }
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        for (FilterSlot<ItemStack> filterSlot : filter.getFilterSlots()) {
            if (filterSlot != null && mouseX > (guiX + filterSlot.getX() + 1) && mouseX < (guiX + filterSlot.getX() + 16) && mouseY > (guiY + filterSlot.getY() + 1) && mouseY < (guiY + filterSlot.getY() + 16)) {
                GlStateManager.translated(0, 0, 200);
                AbstractGui.fill(filterSlot.getX() + 1, filterSlot.getY() + 1, filterSlot.getX() + 17, filterSlot.getY() + 17, -2130706433);
                GlStateManager.translated(0, 0, -200);
                if (!filterSlot.getFilter().isEmpty() && Minecraft.getInstance().player.inventory.getItemStack().isEmpty()) {
                    screen.renderTooltip(screen.getTooltipFromItem(filterSlot.getFilter()), mouseX - guiX, mouseY - guiY);
                    GlStateManager.color4f(1, 1, 1, 1);
                }
            }
        }
    }

    @Override
    public void handleClick(Screen screen, int guiX, int guiY, double mouseX, double mouseY, int button) {
        if (screen instanceof ContainerScreen && ((ContainerScreen) screen).getContainer() instanceof ILocatable) {
            ILocatable locatable = (ILocatable) ((ContainerScreen) screen).getContainer();
            for (FilterSlot<ItemStack> filterSlot : filter.getFilterSlots()) {
                if (filterSlot != null && mouseX > (guiX + filterSlot.getX() + 1) && mouseX < (guiX + filterSlot.getX() + 16) && mouseY > (guiY + filterSlot.getY() + 1) && mouseY < (guiY + filterSlot.getY() + 16)) {
                    CompoundNBT compoundNBT = new CompoundNBT();
                    compoundNBT.putString("Name", filter.getName());
                    compoundNBT.putInt("Slot", filterSlot.getFilterID());
                    compoundNBT.put("Filter", Minecraft.getInstance().player.inventory.getItemStack().serializeNBT());
                    Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), -2, compoundNBT));
                }
            }

        }
    }

}
