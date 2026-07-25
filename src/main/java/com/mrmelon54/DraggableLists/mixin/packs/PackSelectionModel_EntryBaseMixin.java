package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import com.mrmelon54.DraggableLists.duck.ResourcePackOrganizerDuckProvider;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Stream;

@Mixin(PackSelectionModel.EntryBase.class)
public abstract class PackSelectionModel_EntryBaseMixin implements AbstractPackDuckProvider {
    @Shadow
    protected abstract List<Pack> getSelfList();

    @Shadow
    @Final
    private Pack pack;

    @Shadow
    @Final
    private PackSelectionModel this$0;

    @Override
    public void draggable_lists$moveTo(int j) {
        if (pack.isFixedPosition()) return;

        List<Pack> list = getSelfList();
        list.remove(pack);
        // j can be == original list size (dragging below last item); cap after removal
        list.add(Math.min(j, list.size()), pack);

        // get all fixed position resource packs, remove them and add them all at the end
        Stream<Pack> packStream = list.stream().filter(Pack::isFixedPosition);
        List<Pack> list1 = packStream.toList();
        list.removeAll(list1);
        list.addAll(list1);

        if (this$0 instanceof ResourcePackOrganizerDuckProvider duckProvider)
            duckProvider.draggable_lists$updateSelectedList((PackSelectionModel.EntryBase) (Object) this);
    }

}
