package io.github.alexf6.mitosyfauna.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Estado de renderizado compartido para los mobs del mod con propiedades semánticas explícitas.
 */
public class FolkRenderState extends LivingEntityRenderState {
    public int variant = 0;
    public boolean sitting = false;
    public boolean angry = false;
    public boolean holdingStolen = false;
    public boolean isVendor = false;
}
