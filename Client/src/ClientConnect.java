import java.net.*;
import java.io.*;
import java.util.Scanner;

class ClientConnect {

    private Socket socket;
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток чтения в сокет
    private BufferedReader inputUser; // поток чтения с консоли
    private String address; // ip адрес клиента
    private int port; // порт соединения

    ClientConnect(String address, int port) {
        this.address = address;
        this.port = port;
        try {
            this.socket = new Socket(address, port);
            System.out.println("Соединение установлено");
        } catch (IOException e) {
            System.err.println("Соединение не установлено");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadMsg().start();
            new WriteMsg().start();
        } catch (Exception e) {
            ClientConnect.this.downService();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String message;
            try {
                while (true) {
                    message = in.readLine(); // ждем сообщения с сервера
                    System.out.println(message); // пишем сообщение с сервера на консоль
                }
            } catch (IOException e) {
                ClientConnect.this.downService();
            }
        }
    }

    public class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                String message;
                try {
                    message = inputUser.readLine(); // сообщения с консоли
                    if (message.equals("close_connect")) {
                        out.write("close_connect" + "\n");
                        ClientConnect.this.downService();
                        break;
                    } else {
                        out.write(message + "\n"); // отправляем на сервер
                    }
                    out.flush(); // чистим
                } catch (IOException e) {
                    ClientConnect.this.downService();
                }
            }
        }
    }
}

class Client {

    public static void main(String[] args) {
        System.out.println("Введите IP-адрес сервера");
        Scanner scanner = new Scanner(System.in);
        String IPAddress = scanner.nextLine();
        int port = 8080;
        try {
            new ClientConnect(IPAddress, port);
        } catch (Exception e) {
            System.err.println("Сервер не запущен");
        }

    }
}