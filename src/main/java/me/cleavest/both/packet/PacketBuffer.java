package me.cleavest.both.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * @author Cleavest on 19/5/2024
 */
public class PacketBuffer extends ByteBuf {

    private final ByteBuf buf;

    public PacketBuffer(ByteBuf wrapped) {
        this.buf = wrapped;
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public byte[] readByteArray(int maxLength) {
        int i = this.readVarInt();

        if (i > maxLength) {
            throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxLength);
        } else {
            byte[] abyte = new byte[i];
            this.readBytes(abyte);
            return abyte;
        }
    }

    public PacketBuffer writeVarIntArray(int[] array) {
        this.writeVarInt(array.length);

        for (int i : array) {
            this.writeVarInt(i);
        }

        return this;
    }

    public int[] readVarIntArray() {
        return this.readVarIntArray(this.readableBytes());
    }

    public int[] readVarIntArray(int maxLength) {
        int i = this.readVarInt();

        if (i > maxLength) {
            throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxLength);
        } else {
            int[] aint = new int[i];

            for (int j = 0; j < aint.length; ++j) {
                aint[j] = this.readVarInt();
            }

            return aint;
        }
    }

    public PacketBuffer writeLongArray(long[] array) {
        this.writeVarInt(array.length);

        for (long i : array) {
            this.writeLong(i);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
        return (T) ((Enum[]) enumClass.getEnumConstants())[this.readVarInt()];
    }

    public PacketBuffer writeEnumValue(Enum<?> value) {
        return this.writeVarInt(value.ordinal());
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public long readVarLong() {
        long i = 0L;
        int j = 0;

        while (true) {
            byte b0 = this.readByte();
            i |= (long) (b0 & 127) << j++ * 7;

            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public PacketBuffer writeUniqueId(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUniqueId() {
        return new UUID(this.readLong(), this.readLong());
    }

    public PacketBuffer writeVarInt(int input) {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
        return this;
    }

    public PacketBuffer writeVarLong(long value) {
        while ((value & -128L) != 0L) {
            this.writeByte((int) (value & 127L) | 128);
            value >>>= 7;
        }

        this.writeByte((int) value);
        return this;
    }

    public String readString(int maxLength) {
        int i = this.readVarInt();

        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i
                    + " > " + maxLength * 4 + ")");
        } else if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
            this.readerIndex(this.readerIndex() + i);

            if (s.length() > maxLength) {
                throw new DecoderException(
                        "The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

    public PacketBuffer writeString(String string) {
        byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

        if (abyte.length > 32767) {
            throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(abyte.length);
            this.writeBytes(abyte);
            return this;
        }
    }

    public Date readTime() {
        return new Date(this.readLong());
    }

    public PacketBuffer writeTime(Date time) {
        this.writeLong(time.getTime());
        return this;
    }
    

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int p_capacity_1_) {
        return this.buf.capacity(p_capacity_1_);
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ByteBuf order(ByteOrder p_order_1_) {
        return this.buf.order(p_order_1_);
    }

    @Override
    public ByteBuf unwrap() {
        return this.buf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.buf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int p_readerIndex_1_) {
        return this.buf.readerIndex(p_readerIndex_1_);
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int p_writerIndex_1_) {
        return this.buf.writerIndex(p_writerIndex_1_);
    }

    @Override
    public ByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
        return this.buf.setIndex(p_setIndex_1_, p_setIndex_2_);
    }

    @Override
    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isReadable(int p_isReadable_1_) {
        return this.buf.isReadable(p_isReadable_1_);
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public boolean isWritable(int p_isWritable_1_) {
        return this.buf.isWritable(p_isWritable_1_);
    }

    @Override
    public ByteBuf clear() {
        return this.buf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.buf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.buf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.buf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.buf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.buf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.buf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int p_ensureWritable_1_) {
        return this.buf.ensureWritable(p_ensureWritable_1_);
    }

    @Override
    public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
        return this.buf.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
    }

    @Override
    public boolean getBoolean(int p_getBoolean_1_) {
        return this.buf.getBoolean(p_getBoolean_1_);
    }

    @Override
    public byte getByte(int p_getByte_1_) {
        return this.buf.getByte(p_getByte_1_);
    }

    @Override
    public short getUnsignedByte(int p_getUnsignedByte_1_) {
        return this.buf.getUnsignedByte(p_getUnsignedByte_1_);
    }

    @Override
    public short getShort(int p_getShort_1_) {
        return this.buf.getShort(p_getShort_1_);
    }

    @Override
    public short getShortLE(int p_getShortLE_1_) {
        return this.buf.getShortLE(p_getShortLE_1_);
    }

    @Override
    public int getUnsignedShort(int p_getUnsignedShort_1_) {
        return this.buf.getUnsignedShort(p_getUnsignedShort_1_);
    }

    @Override
    public int getUnsignedShortLE(int p_getUnsignedShortLE_1_) {
        return this.buf.getUnsignedShortLE(p_getUnsignedShortLE_1_);
    }

    @Override
    public int getMedium(int p_getMedium_1_) {
        return this.buf.getMedium(p_getMedium_1_);
    }

    @Override
    public int getMediumLE(int p_getMediumLE_1_) {
        return this.buf.getMediumLE(p_getMediumLE_1_);
    }

    @Override
    public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
        return this.buf.getUnsignedMedium(p_getUnsignedMedium_1_);
    }

    @Override
    public int getUnsignedMediumLE(int p_getUnsignedMediumLE_1_) {
        return this.buf.getUnsignedMediumLE(p_getUnsignedMediumLE_1_);
    }

    @Override
    public int getInt(int p_getInt_1_) {
        return this.buf.getInt(p_getInt_1_);
    }

    @Override
    public int getIntLE(int p_getIntLE_1_) {
        return this.buf.getIntLE(p_getIntLE_1_);
    }

    @Override
    public long getUnsignedInt(int p_getUnsignedInt_1_) {
        return this.buf.getUnsignedInt(p_getUnsignedInt_1_);
    }

    @Override
    public long getUnsignedIntLE(int p_getUnsignedIntLE_1_) {
        return this.buf.getUnsignedIntLE(p_getUnsignedIntLE_1_);
    }

    @Override
    public long getLong(int p_getLong_1_) {
        return this.buf.getLong(p_getLong_1_);
    }

    @Override
    public long getLongLE(int p_getLongLE_1_) {
        return this.buf.getLongLE(p_getLongLE_1_);
    }

    @Override
    public char getChar(int p_getChar_1_) {
        return this.buf.getChar(p_getChar_1_);
    }

    @Override
    public float getFloat(int p_getFloat_1_) {
        return this.buf.getFloat(p_getFloat_1_);
    }

    @Override
    public double getDouble(int p_getDouble_1_) {
        return this.buf.getDouble(p_getDouble_1_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_) {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
    }

    @Override
    public ByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public int getBytes(int p_getBytes_1_, FileChannel p_getBytes_2_, long p_getBytes_3_, int p_getBytes_5_)
            throws IOException {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_5_);
    }

    @Override
    public CharSequence getCharSequence(int p_getCharSequence_1_, int p_getCharSequence_2_,
                                        Charset p_getCharSequence_3_) {
        return this.buf.getCharSequence(p_getCharSequence_1_, p_getCharSequence_2_, p_getCharSequence_3_);
    }

    @Override
    public ByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
        return this.buf.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
    }

    @Override
    public ByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
        return this.buf.setByte(p_setByte_1_, p_setByte_2_);
    }

    @Override
    public ByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
        return this.buf.setShort(p_setShort_1_, p_setShort_2_);
    }

    @Override
    public ByteBuf setShortLE(int p_setShortLE_1_, int p_setShortLE_2_) {
        return this.buf.setShortLE(p_setShortLE_1_, p_setShortLE_2_);
    }

    @Override
    public ByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        return this.buf.setMedium(p_setMedium_1_, p_setMedium_2_);
    }

    @Override
    public ByteBuf setMediumLE(int p_setMediumLE_1_, int p_setMediumLE_2_) {
        return this.buf.setMediumLE(p_setMediumLE_1_, p_setMediumLE_2_);
    }

    @Override
    public ByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        return this.buf.setInt(p_setInt_1_, p_setInt_2_);
    }

    @Override
    public ByteBuf setIntLE(int p_setIntLE_1_, int p_setIntLE_2_) {
        return this.buf.setIntLE(p_setIntLE_1_, p_setIntLE_2_);
    }

    @Override
    public ByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        return this.buf.setLong(p_setLong_1_, p_setLong_2_);
    }

    @Override
    public ByteBuf setLongLE(int p_setLongLE_1_, long p_setLongLE_2_) {
        return this.buf.setLongLE(p_setLongLE_1_, p_setLongLE_2_);
    }

    @Override
    public ByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
        return this.buf.setChar(p_setChar_1_, p_setChar_2_);
    }

    @Override
    public ByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
        return this.buf.setFloat(p_setFloat_1_, p_setFloat_2_);
    }

    @Override
    public ByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
        return this.buf.setDouble(p_setDouble_1_, p_setDouble_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }

    @Override
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_) {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, FileChannel p_setBytes_2_, long p_setBytes_3_, int p_setBytes_5_)
            throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_5_);
    }

    @Override
    public ByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        return this.buf.setZero(p_setZero_1_, p_setZero_2_);
    }

    @Override
    public int setCharSequence(int p_setCharSequence_1_, CharSequence p_setCharSequence_2_,
                               Charset p_setCharSequence_3_) {
        return this.buf.setCharSequence(p_setCharSequence_1_, p_setCharSequence_2_, p_setCharSequence_3_);
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buf.readLongLE();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int p_readBytes_1_) {
        return this.buf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readSlice(int p_readSlice_1_) {
        return this.buf.readSlice(p_readSlice_1_);
    }

    @Override
    public ByteBuf readRetainedSlice(int p_readRetainedSlice_1_) {
        return this.buf.readRetainedSlice(p_readRetainedSlice_1_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_) {
        return this.buf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }

    @Override
    public ByteBuf readBytes(byte[] p_readBytes_1_) {
        return this.buf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer p_readBytes_1_) {
        return this.buf.readBytes(p_readBytes_1_);
    }

    @Override
    public ByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public CharSequence readCharSequence(int p_readCharSequence_1_, Charset p_readCharSequence_2_) {
        return this.buf.readCharSequence(p_readCharSequence_1_, p_readCharSequence_2_);
    }

    @Override
    public int readBytes(FileChannel p_readBytes_1_, long p_readBytes_2_, int p_readBytes_4_) throws IOException {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_4_);
    }

    @Override
    public ByteBuf skipBytes(int p_skipBytes_1_) {
        return this.buf.skipBytes(p_skipBytes_1_);
    }

    @Override
    public ByteBuf writeBoolean(boolean p_writeBoolean_1_) {
        return this.buf.writeBoolean(p_writeBoolean_1_);
    }

    @Override
    public ByteBuf writeByte(int p_writeByte_1_) {
        return this.buf.writeByte(p_writeByte_1_);
    }

    @Override
    public ByteBuf writeShort(int p_writeShort_1_) {
        return this.buf.writeShort(p_writeShort_1_);
    }

    @Override
    public ByteBuf writeShortLE(int p_writeShortLE_1_) {
        return this.buf.writeShortLE(p_writeShortLE_1_);
    }

    @Override
    public ByteBuf writeMedium(int p_writeMedium_1_) {
        return this.buf.writeMedium(p_writeMedium_1_);
    }

    @Override
    public ByteBuf writeMediumLE(int p_writeMediumLE_1_) {
        return this.buf.writeMediumLE(p_writeMediumLE_1_);
    }

    @Override
    public ByteBuf writeInt(int p_writeInt_1_) {
        return this.buf.writeInt(p_writeInt_1_);
    }

    @Override
    public ByteBuf writeIntLE(int p_writeIntLE_1_) {
        return this.buf.writeIntLE(p_writeIntLE_1_);
    }

    @Override
    public ByteBuf writeLong(long p_writeLong_1_) {
        return this.buf.writeLong(p_writeLong_1_);
    }

    @Override
    public ByteBuf writeLongLE(long p_writeLongLE_1_) {
        return this.buf.writeLongLE(p_writeLongLE_1_);
    }

    @Override
    public ByteBuf writeChar(int p_writeChar_1_) {
        return this.buf.writeChar(p_writeChar_1_);
    }

    @Override
    public ByteBuf writeFloat(float p_writeFloat_1_) {
        return this.buf.writeFloat(p_writeFloat_1_);
    }

    @Override
    public ByteBuf writeDouble(double p_writeDouble_1_) {
        return this.buf.writeDouble(p_writeDouble_1_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
        return this.buf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }

    @Override
    public ByteBuf writeBytes(byte[] p_writeBytes_1_) {
        return this.buf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public ByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer p_writeBytes_1_) {
        return this.buf.writeBytes(p_writeBytes_1_);
    }

    @Override
    public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(FileChannel p_writeBytes_1_, long p_writeBytes_2_, int p_writeBytes_4_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_4_);
    }

    @Override
    public ByteBuf writeZero(int p_writeZero_1_) {
        return this.buf.writeZero(p_writeZero_1_);
    }

    @Override
    public int writeCharSequence(CharSequence p_writeCharSequence_1_, Charset p_writeCharSequence_2_) {
        return this.buf.writeCharSequence(p_writeCharSequence_1_, p_writeCharSequence_2_);
    }

    @Override
    public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
        return this.buf.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
    }

    @Override
    public int bytesBefore(byte p_bytesBefore_1_) {
        return this.buf.bytesBefore(p_bytesBefore_1_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
    }

    @Override
    public int forEachByte(ByteProcessor p_forEachByte_1_) {
        return this.buf.forEachByte(p_forEachByte_1_);
    }

    @Override
    public int forEachByte(int p_forEachByte_1_, int p_forEachByte_2_, ByteProcessor p_forEachByte_3_) {
        return this.buf.forEachByte(p_forEachByte_1_, p_forEachByte_2_, p_forEachByte_3_);
    }

    @Override
    public int forEachByteDesc(ByteProcessor p_forEachByteDesc_1_) {
        return this.buf.forEachByteDesc(p_forEachByteDesc_1_);
    }

    @Override
    public int forEachByteDesc(int p_forEachByteDesc_1_, int p_forEachByteDesc_2_, ByteProcessor p_forEachByteDesc_3_) {
        return this.buf.forEachByteDesc(p_forEachByteDesc_1_, p_forEachByteDesc_2_, p_forEachByteDesc_3_);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int p_copy_1_, int p_copy_2_) {
        return this.buf.copy(p_copy_1_, p_copy_2_);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int p_slice_1_, int p_slice_2_) {
        return this.buf.slice(p_slice_1_, p_slice_2_);
    }

    @Override
    public ByteBuf retainedSlice(int p_retainedSlice_1_, int p_retainedSlice_2_) {
        return this.buf.retainedSlice(p_retainedSlice_1_, p_retainedSlice_2_);
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
        return this.buf.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
    }

    @Override
    public ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
        return this.buf.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
        return this.buf.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public String toString(Charset p_toString_1_) {
        return this.buf.toString(p_toString_1_);
    }

    @Override
    public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
        return this.buf.toString(p_toString_1_, p_toString_2_, p_toString_3_);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return this.buf.equals(p_equals_1_);
    }

    @Override
    public int compareTo(ByteBuf p_compareTo_1_) {
        return this.buf.compareTo(p_compareTo_1_);
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    @Override
    public ByteBuf retain(int p_retain_1_) {
        return this.buf.retain(p_retain_1_);
    }

    @Override
    public ByteBuf retain() {
        return this.buf.retain();
    }

    @Override
    public ByteBuf touch() {
        return this.buf.touch();
    }

    @Override
    public ByteBuf touch(Object p_touch_1_) {
        return this.buf.touch(p_touch_1_);
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int p_release_1_) {
        return this.buf.release(p_release_1_);
    }

    

    
}
