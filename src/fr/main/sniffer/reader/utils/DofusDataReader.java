package fr.main.sniffer.reader.utils;

import fr.main.sniffer.reader.utils.types.BitConverter;
import fr.main.sniffer.reader.utils.types.Int64;
import fr.main.sniffer.reader.utils.types.UInt64;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;



@SuppressWarnings("unused")
public class DofusDataReader implements IDofusDataInput {
    public DataInputStream dis;
    public ByteArrayInputStream bis;
    private int lengthStream;
    private final int INT_SIZE = 32;
    private final int SHORT_SIZE = 16;
    private final int SHORT_MIN_VALUE = -32768;
    private final int SHORT_MAX_VALUE = 32767;
    private final int UNSIGNED_SHORT_MAX_VALUE = 65536;
    private final int CHUNCK_BIT_SIZE = 7;
    private final int MAX_ENCODING_LENGTH = (int) Math.ceil(INT_SIZE / CHUNCK_BIT_SIZE);
    private final int MASK_10000000 = 128;
    private final int MASK_01111111 = 127;

    public DofusDataReader(ByteArrayInputStream bis) {
        this.bis = bis;
        this.lengthStream = bis.available();
        this.dis = new DataInputStream(this.bis);
        this.dis.mark(0);
    }

    public void Dispose(){
        this.dis = new DataInputStream(bis);
    }

    public int getPosition() throws IOException{
        return this.lengthStream - this.available();
    }

    public void setPosition(int n){
        try
        {
            this.dis.reset();
            this.dis.mark(0);
            this.dis.readFully(new byte[n],0,n);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int readVarInt() throws IOException {
        System.out.println("readVarInt");
        int b = 0;
        int value = 0;
        int offset = 0;
        boolean hasNext = false;

        while (offset < INT_SIZE) {
            b = dis.readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value += ((b & MASK_01111111) << offset);
            else
                value += (b & MASK_01111111);
            offset += CHUNCK_BIT_SIZE;
            if (!hasNext)
                return value;
        }
        throw new Error("Too much data");
    }

    public int readVarUhInt() throws IOException {
        System.out.println("readVarUhInt");
        int b = 0;
        int value = 0;
        int offset = 0;
        boolean hasNext = false;
        while (offset < INT_SIZE)
        {
            b = readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value = (int) (value + ((b & MASK_01111111) << offset));
            else
                value = (int) (value + (b & MASK_01111111));
            offset = offset + CHUNCK_BIT_SIZE;
            if (!hasNext)
                return value;
        }
        try {
            throw new Exception("Too much data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int readVarShort() throws IOException {
        System.out.println("readVarShort");
        int b = 0;
        short value = 0;
        int offset = 0;
        boolean hasNext = false;
        while (offset < SHORT_SIZE) {
            b = dis.readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value += ((b & MASK_01111111) << offset);
            else
                value += (b & MASK_01111111);
            offset += CHUNCK_BIT_SIZE;
            if (!hasNext) {
                if (value > SHORT_MAX_VALUE)
                    value -= UNSIGNED_SHORT_MAX_VALUE;
                return value;
            }
        }
        throw new Error("Too much data");
    }

    public int readVarUhShort() throws IOException {
        System.out.println("readVarUhShort");
        int b = 0;
        short value = 0;
        int offset = 0;
        boolean hasNext = false;
        while (offset < SHORT_SIZE)
        {
            b = readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value = (short) (value + ((b & MASK_01111111) << offset));
            else
                value = (short) (value + (b & MASK_01111111));
            offset = offset + CHUNCK_BIT_SIZE;
            if (!hasNext)
            {
                if (value > SHORT_MAX_VALUE)
                    value = (short) (value - UNSIGNED_SHORT_MAX_VALUE);
                return value;
            }
        }
        try {
            throw new Exception("Too much data");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public long readVarLong() throws IOException {
        System.out.println("readVarLong");
        return readInt64().toNumber();
    }

    public long readVarUhLong() throws IOException {
        System.out.println("readVarUhLong");
        return readUInt64().toNumber();
    }


    private Int64 readInt64()
    {
        System.out.println("readInt64");
        int b = 0;
        Int64 result = new Int64();
        int i = 0;
        while (true)
        {
            try {
                b = dis.readUnsignedByte();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 28)
                break;
            if (b >= 128)
            {
                result.low = result.low | ((b & 127) << i);
                i = i + 7;
                continue;
            }
            result.low = result.low | (b << i);
            return result;
        }

        if (b >= 128)
        {
            b = b & 127;
            result.low = result.low | (b << i);
            result.high = b >> 4;
            i = 3;
            while (true)
            {
                try {
                    b = dis.readUnsignedByte();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (i < 32)
                    if (b >= 128)
                        result.high = result.high | ((b & 127) << i);
                    else
                        break;
                i = i + 7;
            }

            result.high = result.high | (b << i);
            return result;
        }
        result.low = result.low | (b << i);
        result.high = b >> 4;
        return result;
    }

    private UInt64 readUInt64() throws IOException
    {
        System.out.println("readUInt64");
        int b = 0;
        UInt64 result = new UInt64();
        int i = 0;
        while (true) {
            b = dis.readUnsignedByte();
            if( i == 28)
                break;
            if (b >= 128) {
                result.low = result.low | (b & 127) << i;
                i += 7;
                continue;
            }
            result.low = result.low | b << i;
            return result;
        }
        if ( b >= 128) {
            b = b & 127;
            result.low = (long) (result.low | ((b << i) & 0xFFFFFFFFL));
            result.high = b >>> 4;
            i = 3;
            while (true) {
                b = dis.readUnsignedByte();
                if (i < 32) {
                    if (b >= 128)
                        result.high = result.high | (b & 127) << i;
                    else
                        break;
                }
                i += 7;
            }
            result.high = result.high | b << i;
            return result;
        }
        result.low = result.low | b << i;
        result.high = b >>> 4;
        return result;
    }

    public boolean readBoolean() throws IOException {
        System.out.println("readBoolean");
        return dis.readBoolean();
    }

    public byte readByte() throws IOException {
        System.out.println("readByte");
        return dis.readByte();
    }

    public byte[] readBytes(int n) throws IOException{
        System.out.println("readBytes");
        byte[] b = new byte[n];
        dis.readFully(b, 0, n);
        return b;
    }

    public char readChar() throws IOException{
        System.out.println("readChar");
        return dis.readChar();
    }

    public double readDouble() throws IOException {
        System.out.println("readDouble");
        return dis.readDouble();
    }

    public float readFloat() throws IOException {
        System.out.println("readFloat");
        return dis.readFloat();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        System.out.println("readFully");
        dis.readFully(b, off, len);
    }

    public int readInt() throws IOException {
        System.out.println("readInt");
        return dis.readInt();
    }

    public int readUInt() throws IOException{
        System.out.println("readUInt");
        return (int) BitConverter.ToUInt32(ReadBigEndianBytes(4), 0);
    }

    @SuppressWarnings("deprecation")
    public String readLine() throws IOException {
        System.out.println("readLine");
        return dis.readLine();
    }

    public long readLong() throws IOException {
        System.out.println("readLong");
        return dis.readLong();
    }

    public int readULong() throws IOException{
        System.out.println("readULong");
        return (int) BitConverter.ToUInt64(ReadBigEndianBytes(8), 0);
    }

    public short readShort() throws IOException {
        System.out.println("readShort");
        return dis.readShort();
    }

//    public short readUShort() throws IOException{
//        return (short) BitConverter.ToUInt16(ReadBigEndianBytes(2), 0);
//    }

    public int readUnsignedByte() throws IOException {
        System.out.println("readUnsignedByte");
        return dis.readUnsignedByte();
    }

    public int readUShort() throws IOException {
        System.out.println("readUShort");
        return dis.readUnsignedShort();
    }

    public String ReadAscii(int bytesAmount) throws IOException
    {
        System.out.println("ReadAscii");
        byte[] buffer = this.readBytes(bytesAmount);
        return new String(buffer, StandardCharsets.US_ASCII);
    }

    public String readUTF() throws IOException {
        System.out.println("readUTF");
        int len  = readUShort();
        byte[] bytes = readBytes(len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String ReadUTFBytes(short len) throws IOException
    {
        System.out.println("ReadUTFBytes");
        byte[] bytes = readBytes(len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int skipBytes(int n) throws IOException {
        System.out.println("skipBytes");
        return dis.skipBytes(n);
    }

    public int available() throws IOException {
        return this.dis.available();
    }

    private byte[] ReadBigEndianBytes(int count) throws IOException
    {
        System.out.println("ReadBigEndianBytes");
        byte[] array = new byte[count];
        for (int i = count - 1; i >= 0; i--)
            array[i] = (byte) dis.readByte();
        return array;
    }


    public float ReadSingle() throws IOException
    {
        System.out.println("ReadSingle");
        return BitConverter.ToSingle(ReadBigEndianBytes(4), 0);
    }

    public String ReadUTF7BitLength() throws IOException
    {
        System.out.println("ReadUTF7BitLength");
        int n = readInt();
        byte[] bytes = readBytes(n);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        System.out.println("readFully");
        this.dis.readFully(b);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        System.out.println("readUnsignedShort");
        return dis.readUnsignedShort();
    }



}
