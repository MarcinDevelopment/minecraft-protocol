package ru.enke.minecraft.protocol.packet

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.data.game.*
import ru.enke.minecraft.protocol.packet.data.game.EntityMetadataType.*
import ru.enke.minecraft.protocol.packet.data.message.Message
import java.util.*
import kotlin.collections.ArrayList


interface PacketMessage

interface Packet<T : PacketMessage> {
    fun write(message: T, buffer: ByteBuf)
    fun read(buffer: ByteBuf): T
}

fun ByteBuf.writeVarInt(value: Int) {
    var result = value

    while((result and 0xFFFFFF80.toInt()) != 0x0) {
        writeByte(result or 0x80)
        result = result ushr 7
    }

    writeByte(result)
}

fun ByteBuf.writeVarLong(value: Long) {
    var result = value

    while((result and 0xFFFFFF80).toInt() != 0x0) {
        writeByte((result or 0x80).toInt())
        result = result ushr 7
    }

    writeByte(result.toInt())
}

fun ByteBuf.readVarInt(): Int {
    var result = 0
    var shift = 0
    var b: Int

    do {
        if(shift >= 32) {
            throw IndexOutOfBoundsException("VarInt too long")
        }

        b = readByte().toInt()
        result = result or (b and 0x7F shl shift)
        shift += 7
    } while(b and 0x80 != 0)

    return result
}

fun ByteBuf.readVarLong(): Long {
    var result: Long = 0
    var shift = 0
    var b: Int

    do {
        if(shift >= 64) {
            throw IndexOutOfBoundsException("VarLong too long")
        }

        b = readByte().toInt()
        result = result or (b and 0x7F shl shift).toLong()
        shift += 7
    } while(b and 0x80 != 0)

    return result
}

fun ByteBuf.writeString(value: String) {
    val bytes = value.toByteArray()
    writeVarInt(bytes.size)
    writeBytes(bytes)
}

fun ByteBuf.readString(): String {
    return String(readByteArray())
}

fun ByteBuf.writeByteArray(bytes: ByteArray) {
    writeVarInt(bytes.size)
    writeBytes(bytes)
}

fun ByteBuf.readByteArray(): ByteArray {
    val bytes = ByteArray(readVarInt())
    readBytes(bytes)
    return bytes
}

fun ByteBuf.writeEnum(enum: Enum<*>) {
    writeByte(enum.ordinal)
}

inline fun <reified T : Enum<T>> ByteBuf.readEnum(): T {
    val enums = enumValues<T>()
    val index = readUnsignedByte().toInt()

    if(index > enums.size) {
        throw IllegalArgumentException()
    }

    return enums[index]
}

fun ByteBuf.writeVarEnum(enum: Enum<*>) {
    writeVarInt(enum.ordinal)
}

inline fun <reified T : Enum<T>> ByteBuf.readVarEnum(): T {
    val enums = enumValues<T>()
    val index = readVarInt()

    if(index < 0) {
        throw IllegalArgumentException()
    }

    if(index > enums.size) {
        throw IllegalArgumentException()
    }

    return enums[index]
}

fun ByteBuf.writeEnumAsString(enum: Enum<*>) {
    writeString(enum.name.toLowerCase())
}

inline fun <reified T : Enum<T>> ByteBuf.readEnumAsString(): T {
    return enumValueOf(readString().toUpperCase())
}

fun ByteBuf.writePosition(position: Position) {
    val x = (position.x and 0x3FFFFFF).toLong()
    val y = (position.y and 0xFFF).toLong()
    val z = (position.z and 0x3FFFFFF).toLong()

    writeLong(x shl 38 or (y shl 26) or z)
}

fun ByteBuf.readPosition() : Position {
    val value = readLong()

    val x = (value shr 38).toInt()
    val y = (value shr 26 and 4095).toInt()
    val z = (value shl 38 shr 38).toInt()

    return Position(x, y, z)
}

fun ByteBuf.writeSlot(itemStack: ItemStack?) {
    if(itemStack == null) {
        writeShort(-1)
        return
    }

    writeShort(itemStack.id)
    writeByte(itemStack.quantity)
    writeShort(itemStack.metadata)
    writeBytes(itemStack.state ?: return)
}

fun ByteBuf.readSlot() : ItemStack? {
    val id = readShort().toInt()

    if(id == -1) {
        return null
    }

    val quantity = readByte().toInt()
    val metadata = readShort().toInt()

    val length = readableBytes()
    val state = if(length > 0) ByteArray(length) else null

    if(state != null) {
        readBytes(state)
    }

    return ItemStack(id, quantity, metadata, state)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    writeLong(uuid.mostSignificantBits)
    writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUUID(): UUID {
    return UUID(readLong(), readLong())
}

fun ByteBuf.writeBlockState(state: BlockState) {
    writeVarInt(state.id shl 4 or state.data)
}

fun ByteBuf.readBlockState(): BlockState {
    val id = readVarInt()

    return BlockState(id shr 4, id and 0xF)
}

fun ByteBuf.readBytes(): ByteArray {
    val bytes = ByteArray(readableBytes())
    readBytes(bytes)

    return bytes
}

fun ByteBuf.writeEntityMetadata(metadata: List<EntityMetadata>) {
    for((id, type, value) in metadata) {
        writeByte(id)
        writeVarEnum(type)

        when(type) {
            BYTE -> writeByte(value as Int)
            INT -> writeInt(value as Int)
            FLOAT -> writeFloat(value as Float)
            STRING -> writeString(value as String)
            CHAT -> writeString((value as Message).toJson())
            SLOT -> writeSlot(value as ItemStack)
            BOOLEAN -> writeBoolean(value as Boolean)
            ROTATION -> {}
            POSITION -> readPosition()
            OPTIONAL_POSITION -> {}
            BLOCK_FACE -> writeVarEnum(value as BlockFace)
            OPTIONAL_UUID -> {}
            BLOCK_STATE -> writeBlockState(value as BlockState)
            NBT_TAG -> {}
        }
    }

    writeByte(255)
}

fun ByteBuf.readEntityMetadata() : List<EntityMetadata> {
    val metadata = ArrayList<EntityMetadata>()

    while(true) {
        val id = readUnsignedByte().toInt()

        if(255 == id) {
            break
        }

        val types = EntityMetadataType.values()
        val typeId = readVarInt()

        if(typeId > types.size || typeId < 0) {
            throw IllegalStateException()
        }

        val type = types[typeId]
        val value: Any? = when(type) {
            BYTE -> readByte()
            INT -> readVarInt()
            FLOAT -> readFloat()
            STRING -> readString()
            CHAT -> Message.fromJson(readString())
            SLOT -> readSlot()
            BOOLEAN -> readBoolean()
            ROTATION -> null
            POSITION -> readPosition()
            OPTIONAL_POSITION -> {
                val positionPresent = readBoolean()

                if(positionPresent) {
                    readPosition()
                } else {
                    null
                }
            }
            BLOCK_FACE -> readVarEnum<BlockFace>()
            OPTIONAL_UUID -> {
                val uuidPresent = readBoolean()

                if(uuidPresent) {
                    readUUID()
                } else {
                    null
                }
            }
            BLOCK_STATE -> readBlockState()
            NBT_TAG -> null
        }

        metadata.add(EntityMetadata(id, type, value))
    }

    return metadata
}
