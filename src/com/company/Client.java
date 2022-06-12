package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Client {

    static void receive(SocketChannel socketChannel, ByteBuffer inputBuffer) throws IOException {
        int bytesCount = socketChannel.read(inputBuffer);
        String answer = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
        inputBuffer.clear();
        System.out.println("SERVER: " + answer);
    }

    static void send(SocketChannel socketChannel, String toSend) throws IOException {
        socketChannel.write(ByteBuffer.wrap(String.format(toSend).getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws IOException {
        // Определяем сокет сервера
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 23334);
        final SocketChannel socketChannel = SocketChannel.open();
//  подключаемся к серверу
        socketChannel.connect(socketAddress);

// Получаем входящий и исходящий потоки информации
        try (Scanner scanner = new Scanner(System.in)) {
            //  Определяем буфер для получения данных
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            String msg;
            System.out.println("Client started");
            while (true) {
                receive(socketChannel, inputBuffer);
                System.out.print("YOU: ");
                String toSend = scanner.nextLine();
                if ("end".equals(toSend)) {
                    break;
                }
                send(socketChannel, toSend);
                receive(socketChannel, inputBuffer);
            }
        } finally {
            socketChannel.close();
        }
    }
}


