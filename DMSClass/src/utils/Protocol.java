package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import enums.*;
import interfaces.*;

// 사용법
// 1. Builder 를 통해 프로토콜을 만든다.
// 2. SocketHelper 를 통해 통신한다
// 3. profit!

// Length 가 내부적으론 int 지만 통신할때는 short 로 짤려서 통신됨!!!!
// why? 일단 코드상에서는 프로토콜 body 가 대빵 큰거 넣어도 실제로 SocketHelper 가 작게 잘라서 보낸다
// 잘라서 보낼땐 최대 길이가 send_buffer_size 만큼 보내서 상관 없는데
// 처음에 대빵 큰거 들어올때는 short 범위를 넘어설수 있기 때문!!

public final class Protocol implements Comparable<Protocol> {
    // 프로토콜 생성시 Builder 이용할 것
    public static class Builder {
        // 필수
        private final ProtocolType type; // 1바이트, 프로토콜 타입
        private final Direction direction; // 1바이트, 프로토콜 응답 방향
        private final ICode1 code1; // 1바이트, 프로토콜 코드
        private final ICode2 code2; // 1바이트, 프로토콜 코드
        // 옵션
        private int length = HEADER_LENGTH; // 2바이트, 전체 프로토콜 길이.
        // 실제 프로토콜에선 필수 정보지만 헤더길이는 10으로 고정되어 있으니
        // body 받고나면 길이 계산 가능해서 Builder 에선 옵션임.
        private Bool is_splitted = Bool.FALSE; // 1바이트, 프로토콜 분리 여부
        private Bool is_last = Bool.TRUE; // 1바이트, 마지막 프로토콜인지 여부
        private int sequence = 0; // 4바이트, 시퀀스 넘버
        private byte[] body_bytes = {(byte) 0x00}; // body가 직렬화 된것

        public Builder(ProtocolType type, Direction direction, ICode1 code1, ICode2 code2) {
            this.type = type;
            this.direction = direction;
            this.code1 = code1;
            this.code2 = code2;
        }

        Builder sequence(short seq, Bool islast) {
            is_splitted = Bool.TRUE;
            sequence = seq;
            is_last = islast;
            return this;
        }

        public Builder body(byte[] serialized) {
            body_bytes = serialized;
            this.length = HEADER_LENGTH + serialized.length;
            return this;
        }

        public Builder(byte[] packet) {
            // 빅 엔디안으로 읽음
            this.length = ProtocolHelper.bytesToInt(new byte[]{packet[0], packet[1], packet[2], packet[3]});
            this.type = ProtocolType.get(packet[4]);
            this.direction = Direction.get(packet[5]);
            this.code1 = Code1.get(this.type, packet[6]);
            this.code2 = Code2.get(this.type, packet[7]);
            this.is_splitted = Bool.get(packet[8]);
            this.is_last = Bool.get(packet[9]);
            this.sequence = ProtocolHelper.bytesToInt(new byte[]{packet[10], packet[11], packet[12], packet[13]});

            this.body_bytes = Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
        }

        public Protocol build() {
            return new Protocol(this);
        }
    }

    // Header
    public final int length; // 실제 전송될땐 send_buffer_size 이하라 2바이트, 전체 프로토콜 길이
    public final ProtocolType type; // 1바이트, 프로토콜 타입
    public final Direction direction; // 1바이트, 프로토콜 응답 방향
    public final ICode1 code1; // 1바이트, 프로토콜 코드 종류
    public final ICode2 code2; // 1바이트, 프로토콜 코드
    // 아래 3개는 body 가 커져서 프로토콜 분리시 쓰임
    // utils 내에서 쓰이고 외부 프로젝트에는 숨김
    final Bool is_splitted;// 1바이트, 프로토콜 분리 여부
    final Bool is_last; // 1바이트, 마지막 프로토콜인지 여부
    final int sequence; // 4바이트, 시퀀스 넘버
    // body 시작 인덱스를 알기 위해 자신의 길이를 가지고 있다.
    public static final int HEADER_LENGTH = 14;

    // Body
    private final byte[] body_bytes;

    public byte[] getBody() {
        return body_bytes;
    }

    // Builder 로부터 프로토콜 생성
    protected Protocol(Builder builder) {
        this.length = builder.length;
        this.type = builder.type;
        this.direction = builder.direction;
        this.code1 = builder.code1;
        this.code2 = builder.code2;
        this.is_splitted = builder.is_splitted;
        this.is_last = builder.is_last;
        this.sequence = builder.sequence;
        this.body_bytes = builder.body_bytes;
    }

    // head 를 바이트 배열로 바꿔서 이를 body 랑 합쳐 반환함.
    // 순서는 변수 선언 순서와 같음
    byte[] getPacket() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(ProtocolHelper.intToBytes(length));
            baos.write(type.getCode());
            baos.write(direction.getCode());
            baos.write(code1.getCode());
            baos.write(code2.getCode());
            baos.write(is_splitted.bit);
            baos.write(is_last.bit);
            baos.write(ProtocolHelper.intToBytes(sequence));
            baos.write(body_bytes);
        } catch (IOException e) {
            // ByteArrayOutputStream 는 OutputStream 상속해서 이때문에 예외가 던져짐
            // 즉 실제로는 예외 뜰일 없음.
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    @Override
    public int compareTo(Protocol o) {
        if (this.sequence < o.sequence)
            return -1;
        else if (this.sequence > o.sequence)
            return 1;
        else if (this.sequence == 0 && o.sequence == 0)
            return 0;
        else
            throw new RuntimeException("두 프로토콜의 시퀀스가 같으면 안됩니다!");
    }
}