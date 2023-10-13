package com.mattsmeets.macrokey.gui.list;

import com.google.common.collect.ImmutableList;
import com.mattsmeets.macrokey.MacroKey;
import com.mattsmeets.macrokey.gui.GuiLayerManagement;
import com.mattsmeets.macrokey.gui.GuiModifyLayer;
import com.mattsmeets.macrokey.model.LayerInterface;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class LayerListFragment extends ContainerObjectSelectionList<LayerListFragment.Entry> {
    private static final Logger LOGGER = LogManager.getLogger();

    private final GuiLayerManagement guiLayerManagement;

    public LayerListFragment(final GuiLayerManagement guiLayerManagement) throws IOException {
        super(guiLayerManagement.getMinecraft(), guiLayerManagement.width + 45, guiLayerManagement.height, 63, guiLayerManagement.height - 32, 20);

        this.guiLayerManagement = guiLayerManagement;

        for (LayerInterface layer : MacroKey.modState.getLayers(true)) {
            addEntry(new LayerEntry(layer));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<LayerListFragment.Entry> {
    }

    public class LayerEntry extends Entry {
        private final LayerInterface layer;

        private final Button btnRemove;
        private final Button btnEdit;

        private LayerEntry(final LayerInterface layer) {
            this.layer = layer;

            this.btnEdit = Button.builder(Component.translatable("edit"),button->{
                minecraft.setScreen(new GuiModifyLayer(guiLayerManagement, layer));
            }).pos(0, 0).size( 60, 20).build();

            this.btnRemove = Button.builder(Component.translatable("fragment.list.text.remove"), button->{
                try {
                    if (layer.equals(MacroKey.modState.getActiveLayer())) {
                        MacroKey.modState.setActiveLayer(null);
                    }

                    MacroKey.bindingsRepository.deleteLayer(layer, true);
                } catch (IOException e) {
                    LOGGER.error(e);
                } finally {
                    minecraft.setScreen(guiLayerManagement);
                }
            }).pos(0, 0).size( 15, 20).build();
        }

        @Override
        public void render(GuiGraphics ps, int entryWidth, int entryHeight, int mouseX, int mouseY, int c, int b, int a, boolean isSelected, float partialTicks) {
            // Render layer name
            ps.drawString(minecraft.font, this.layer.getDisplayName(),
                    mouseX + 90f - minecraft.font.width(this.layer.getDisplayName()),
                    (float)(entryHeight + c / 2 - 9 / 2),
                    0xFFFFFF, true);
            // Render buttons
            this.btnEdit.setX(mouseX + 140);
            this.btnEdit.setY(entryHeight);
            this.btnEdit.render(ps, b, a, 0.0f);

            this.btnRemove.setX(mouseX + 200);
            this.btnRemove.setY(entryHeight);
            this.btnRemove.render(ps, b, a, 0.0f);
        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            final boolean btnEditResult = this.btnEdit.mouseClicked(mouseX, mouseY, button);
            final boolean btnRemoveResult = this.btnRemove.mouseClicked(mouseX, mouseY, button);

            return btnEditResult || btnRemoveResult;
        }
    }
}
