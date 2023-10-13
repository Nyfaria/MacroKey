package com.mattsmeets.macrokey.gui;

import com.google.common.collect.ImmutableList;
import com.mattsmeets.macrokey.gui.list.LayerListFragment;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class GuiLayerManagement extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Screen parentScreen;

    private final String screenTitle = I18n.get("gui.manage.layer.text.title");
    private final String addLayerButtonText = I18n.get("gui.manage.text.layer.add");
    private final String doneText = I18n.get("gui.done");

    private LayerListFragment layerListFragment;

    private Button btnDone, btnAdd;

    GuiLayerManagement(Screen screen) {
        super(Component.literal("test"));
        this.parentScreen = screen;
    }

    @Override
    public void render(GuiGraphics ps, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ps);

        // Render list
        this.layerListFragment.render(ps, mouseX, mouseY, partialTicks);

        // Render title
        ps.drawCenteredString(this.font, this.screenTitle, this.width / 2, 8, 0xFFFFFF);

        // Render buttons & labels
        super.render(ps, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        final GuiLayerManagement that = this;

        // Cancel button
        btnDone = this.addRenderableWidget(Button.builder( Component.literal(this.doneText), button->{
            Minecraft.getInstance().setScreen(parentScreen);
        }).pos(this.width / 2 - 155, this.height - 29).size( 150, 20).build());

        // Add layer button
        btnAdd = this.addRenderableWidget(Button.builder( Component.literal(this.addLayerButtonText), button->{
            Minecraft.getInstance().setScreen(new GuiModifyLayer(that));
        }).pos(this.width / 2 - 155 + 160, this.height - 29).size( 150, 20).build());

        try {
            this.layerListFragment = new LayerListFragment(this);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public List<? extends GuiEventListener> children() {
        return ImmutableList.of(this.btnAdd, this.btnDone);
    }

    public List<? extends NarratableEntry> narratables() {
        return ImmutableList.of(this.btnAdd, this.btnDone);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton != 0 || !this.layerListFragment.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
