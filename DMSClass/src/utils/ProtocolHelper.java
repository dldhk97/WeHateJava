package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import enums.Bool;

public final class ProtocolHelper {
    public static byte[] serialization(final Serializable obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                return baos.toByteArray();
            }
        }
    }

    public static Serializable deserialization(final byte[] bytes) throws ClassNotFoundException, IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                final Object obj = ois.readObject();
                return (Serializable) obj;
            }
        }
    }

    static byte[] shortToBytes(short input) {
        byte[] result = new byte[2];
        result[0] = (byte) ((input >> 8) & 0xFF);
        result[1] = (byte) (input & 0xFF);
        return result;
    }

    static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
        buffer.putInt(x);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.array();
    }

    static short bytesToShort(byte[] input) {
        return (short) (input[0] << 8 | (input[1] & 0xFF));
    }

    static short bytesToShort(byte a, byte b) {
        return (short) (a << 8 | (b & 0xFF));
    }

    static int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    static List<byte[]> splitBySize(final byte[] bytes, final int chunk_size) {
        final List<byte[]> result = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += chunk_size) {
            result.add(Arrays.copyOfRange(bytes, i, Math.min(bytes.length, i + chunk_size)));
        }
        return result;
    }

    static List<Protocol> split(final Protocol protocol) {
        List<Protocol> result = new ArrayList<>();
        byte[] tmp = protocol.getBody();
        List<byte[]> body_chunks = splitBySize(tmp, (int) SocketHelper.send_buffer_size - Protocol.HEADER_LENGTH);

        int body_chunks_size = body_chunks.size();
        short seq = 0;
        Bool isLast = Bool.get(false);

        for (; seq < body_chunks_size; ++seq) {
            if (seq + 1 == body_chunks_size)
                isLast = Bool.get(true);
            result.add(new Protocol.Builder(protocol.type, protocol.direction, protocol.code1, protocol.code2)
                    .body(body_chunks.get(seq)).sequence(seq, isLast).build());
        }

        return result;
    }

    static List<Protocol> bytesToProtocols(byte[] bytes) {
        List<Protocol> tmp = new ArrayList<>();

        int chunk_size = 0;
        int cursor = 0;
        for (cursor = 0; cursor < bytes.length; cursor = cursor + chunk_size) {
            chunk_size = bytesToInt(Arrays.copyOfRange(bytes, cursor, cursor + 4));
            tmp.add(new Protocol.Builder(Arrays.copyOfRange(bytes, cursor, cursor + chunk_size)).build());
        }

        return tmp;
    }

    static Protocol merge(List<Protocol> protocols) {
        if (protocols.size() == 0)
            return null;

        protocols.sort(null);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (Protocol protocol : protocols) {
            try {
                output.write(protocol.getBody());
            } catch (IOException e) {
                // ByteArrayOutputStream 는 예외 안던진다.
                e.printStackTrace();
            }
        }

        return new Protocol.Builder(protocols.get(0).type, protocols.get(0).direction, protocols.get(0).code1,
                protocols.get(0).code2).body(output.toByteArray()).build();
    }
}