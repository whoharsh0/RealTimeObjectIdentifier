package com.rudra.objectidentifier.presentation.detection

import com.rudra.objectidentifier.domain.repository.CameraControls

class FakeCameraControls : CameraControls {
    var torchOn: Boolean = false

    override fun toggleTorch(): Boolean {
        torchOn = !torchOn
        return torchOn
    }

    override fun disableTorch() {
        torchOn = false
    }

    override fun hasFlashUnit(): Boolean = false
}
