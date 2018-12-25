import java.io.*;
import java.net.*;

class ServerConnect extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток завписи в сокет

    ServerConnect(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }

    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            out.write(word + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
        try {
            while (true) {
                word = in.readLine();
                if (word.equals("stop")) {
                    break; // если пришла пустая строка - выходим из цикла прослушки
                }
                System.out.println("Message: " + word);
                Server.serverConnect.send(word);
            }
        } catch (IOException ignored) {

        }
    }

    private void send(String message) {
        try {
            out.write("Длина отправленного сообщения " + message.length() +  " символ(ов) \n");
            out.flush();
        } catch (IOException ignored) {}
    }
}

class Server {

    private static final int PORT = 8080;
    static ServerConnect serverConnect;

    public static void main(String[] args) throws IOException {
        System.out.println("Сервер запущен");
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    serverConnect = new ServerConnect(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}