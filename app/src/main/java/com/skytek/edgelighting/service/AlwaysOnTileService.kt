package com.skytek.edgelighting.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.skytek.edgelighting.utils.Global

@RequiresApi(Build.VERSION_CODES.N)
class AlwaysOnTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // Check if qsTile is null before attempting to use it
        qsTile?.let {
            updateTile(Global.currentAlwaysOnState(this))
        } ?: run {
            Log.e("AlwaysOnTileService", "qsTile is null in onStartListening")
        }
    }

    override fun onClick() {
        Global.changeAlwaysOnState(this)
        // Check if qsTile is null before attempting to update it
        qsTile?.let {
            updateTile(Global.currentAlwaysOnState(this))
        } ?: run {
            Log.e("AlwaysOnTileService", "qsTile is null in onClick")
        }
    }

    private fun updateTile(isActive: Boolean) {
        val tile = qsTile
        if (tile != null) {
            val newState: Int = if (isActive) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            tile.state = newState
            tile.updateTile()
        } else {
            Log.e("AlwaysOnTileService", "qsTile is null in updateTile")
        }
    }
}

