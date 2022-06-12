package com.company;

// Использован NonBlocking способ взаимодействия по причине значительного количества данных
// для обмена

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class Server {

    static String receive(SocketChannel socketChannel, ByteBuffer inputBuffer) throws IOException {
        int bytesCount = socketChannel.read(inputBuffer);
//  если из потока читать нельзя, перестаем работать с этим клиентом
        if (bytesCount == -1) return null;
//  получаем переданную от клиента строку в нужной кодировке и очищаем буфер
        String answer = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
        inputBuffer.clear();
        System.out.println("CLIENT: " + answer);
        return answer;
    }

    static void send(SocketChannel socketChannel, String toSend) throws IOException {
        socketChannel.write(ByteBuffer.wrap((String.format(toSend)).getBytes(StandardCharsets.UTF_8)));
        System.out.println("SERVER: " + toSend);
    }

    public static void main(String[] args) throws IOException {

        //  Занимаем порт, определяя серверный сокет
        int port = 23334;
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", port));
        System.out.println("Server started.");
        while (true) {
//  Ждем подключения клиента и получаем потоки для дальнейшей работы
            try (SocketChannel socketChannel = serverChannel.accept()) {
                System.out.println("Client connected " + socketChannel.getRemoteAddress());
//  Определяем буфер для получения данных
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                while (socketChannel.isConnected()) { //  читаем данные из канала в буфер
                    String data = null;
                    do {
                        send(socketChannel, "Input string");
                        data = receive(socketChannel, inputBuffer);
                        if (data == null) {
                            System.out.println("Incorrect answer from Client or it's session terminated.");
                            break;
                        }
                        send(socketChannel, data.replaceAll("\\s", ""));
                    } while (data == null);
                }
            } catch (
                    IOException err) {
                System.out.println(err.getMessage());
            }
        }
    }
}


