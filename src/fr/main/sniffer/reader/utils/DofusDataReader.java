package fr.main.sniffer.reader.utils;

import fr.main.sniffer.reader.utils.types.Int64;
import fr.main.sniffer.reader.utils.types.UInt64;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DofusDataReader implements IDofusDataInput {
    private DataInputStream dis;
    private ByteArrayInputStream bis;
    private int lengthStream;
    private final int INT_SIZE = 32;
    private final int SHORT_SIZE = 16;
    private final int CHUNCK_BIT_SIZE = 7;
    private final int MASK_10000000 = 128;
    private final int MASK_01111111 = 127;

    public DofusDataReader(ByteArrayInputStream bis) {
    	this.bis = bis;
    	this.lengthStream = bis.available();
        this.dis = new DataInputStream(this.bis);
        this.dis.mark(0);
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
        int b;
        int value = 0;
        int offset = 0;
        boolean hasNext;

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
        int b;
        int value = 0;
        int offset = 0;
        boolean hasNext;
        while (offset < INT_SIZE)
        {
            b = readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value = value + ((b & MASK_01111111) << offset);
            else
                value = value + (b & MASK_01111111);
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
        int b;
        short value = 0;
        int offset = 0;
        boolean hasNext;
        while (offset < SHORT_SIZE) {
            b = dis.readByte();
            hasNext = (b & MASK_10000000) == MASK_10000000;
            if (offset > 0)
                value += ((b & MASK_01111111) << offset);
            else
                value += (b & MASK_01111111);
            offset += CHUNCK_BIT_SIZE;
            if (!hasNext) {
                return value;
            }
        }
        throw new Error("Too much data");
    }

    public int readVarUhShort() throws IOException {
        int b;
        short value = 0;
        int offset = 0;
        boolean hasNext;
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
                return value;
            }
        }
        try {
			throw new Exception("Too much data");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
    }

    public long readVarLong() {
        return readInt64().toNumber();
    }

    public long readVarUhLong() throws IOException {
        return readUInt64().toNumber();
    }


    private Int64 readInt64()
    {
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
    	int b;
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
            result.low = result.low | ((b << i) & 0xFFFFFFFFL);
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
        return dis.readBoolean();
    }

    public byte readByte() throws IOException {
        return dis.readByte();
    }

    public byte[] readBytes(int n) throws IOException{
    	byte[] b = new byte[n];
    	dis.readFully(b, 0, n);
    	return b;
    }

    public char readChar() throws IOException{
        return dis.readChar();
    }

    public double readDouble() throws IOException {
        return dis.readDouble();
    }

    public float readFloat() throws IOException {
        return dis.readFloat();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        dis.readFully(b, off, len);
    }

    public int readInt() throws IOException {
        return dis.readInt();
    }

	@SuppressWarnings("deprecation")
	public String readLine() throws IOException {
        return dis.readLine();
    }

    public long readLong() throws IOException {
        return dis.readLong();
    }


    public short readShort() throws IOException {
        return dis.readShort();
    }

    public int readUnsignedByte() throws IOException {
        return dis.readUnsignedByte();
    }

    private int readUShort() throws IOException {
        return dis.readUnsignedShort();
    }

    public String readUTF() throws IOException {
    	int len  = readUShort();
    	byte[] bytes = readBytes(len);
        return new String(bytes, StandardCharsets.UTF_8);
        }

    public int skipBytes(int n) throws IOException {
        return dis.skipBytes(n);
    }

	public int available() throws IOException {
		return this.dis.available();
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		this.dis.readFully(b);
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return dis.readUnsignedShort();
	}



}
