package ru.enke.minecraft.protocol.packet.server.game

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*
import ru.enke.minecraft.protocol.packet.data.game.PlayerListAction

object ServerPlayerListEntryPacket : Packet<PlayerListEntry> {
    //It's not finished yet. https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_List_Item
    override fun write(message: PlayerListEntry, buffer: ByteBuf) {
        buffer.writeVarEnum(message.action)
        buffer.writeVarInt(message.numofplayers)
    }

    override fun read(buffer: ByteBuf): PlayerListEntry {
        val action = buffer.readVarEnum<PlayerListAction>()
        val numofplayers = buffer.readVarInt()

        for(i in 0 until numofplayers) {
            //gameprofile array
            when(action) {
                PlayerListAction.ADD_PLAYER -> TODO()
                PlayerListAction.UPDATE_GAME_MODE -> TODO()
                PlayerListAction.UPDATE_LATENCY -> TODO()
                PlayerListAction.UPDATE_DISPLAY_NAME -> TODO()
                PlayerListAction.REMOVE_PLAYER -> TODO()
            }
        }
        return PlayerListEntry(action, numofplayers)
    }
}

data class PlayerListEntry(val action : PlayerListAction, val numofplayers : Int) : PacketMessage
