package ru.enke.minecraft.protocol.packet.server.game

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*
import ru.enke.minecraft.protocol.packet.data.message.Message

object ServerPlayerListDataPacket : Packet<PlayerListData> {

    override fun write(message: PlayerListData, buffer: ByteBuf) {
        buffer.writeString(message.header.toJson())
        buffer.writeString(message.footer.toJson());
    }

    override fun read(buffer: ByteBuf): PlayerListData {
        val header = buffer.readString();
        val footer = buffer.readString();
        return PlayerListData(Message.fromJson(header), Message.fromJson(footer));
    }
}

data class PlayerListData(val header: Message, val footer: Message) : PacketMessage
