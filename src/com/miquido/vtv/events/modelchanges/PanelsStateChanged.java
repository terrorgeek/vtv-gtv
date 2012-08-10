package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class PanelsStateChanged {
    @Getter
    private final PanelsStateViewModel panelsStateViewModel;

    public PanelsStateChanged(PanelsStateViewModel panelsStateViewModel) {
        this.panelsStateViewModel = panelsStateViewModel;
    }
}
