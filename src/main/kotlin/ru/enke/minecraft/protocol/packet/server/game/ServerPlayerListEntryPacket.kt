package ru.enke.minecraft.protocol.packet.server.game

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*

object ServerPlayerListEntryPacket : Packet<PlayerListEntry> {
    //It's not finished yet. https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_List_Item
    override fun write(message: PlayerListEntry, buffer: ByteBuf) {
        buffer.writeString(message.name)
        buffer.writeBoolean(message.online)
        buffer.writeShort(message.ping.toInt());
    }

    override fun read(buffer: ByteBuf): PlayerListEntry {
        val name = buffer.readString()
        val online = buffer.readBoolean()
        val ping = buffer.readShort()
        return PlayerListEntry(name, online, ping)
    }
}

data class PlayerListEntry(val name : String, val online : Boolean, val ping : Short) : PacketMessage
