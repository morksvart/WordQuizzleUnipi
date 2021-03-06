package org.baratta.logic;

import shared.Registration;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * WordQuizzleClient
 */
public class WordQuizzleClient {
    private static Integer UDPport = 33333;
    public static final int TCP_PORT = 2919;
    public SocketChannel socket;
    public Registration registration;
    final ByteBuffer length;
    final ByteBuffer reply;
    ByteBuffer msg;
    public DatagramSocket challengeSocket = null;

    public WordQuizzleClient() {
        length = ByteBuffer.allocate(Integer.BYTES);
        msg = ByteBuffer.allocate(256);
        reply = ByteBuffer.allocate(Character.BYTES * 50);

    }

    private static class SingletonHelper {
        private static final WordQuizzleClient client = new WordQuizzleClient();
    }

    public static WordQuizzleClient getInstance() {
        return SingletonHelper.client;
    }

    public String login(String username, String password) {
        do {
            try {
                challengeSocket = new DatagramSocket(UDPport);
            } catch (SocketException e) {
                UDPport++;
            }
        } while (challengeSocket == null);

        InetSocketAddress address = new InetSocketAddress("localhost", TCP_PORT);
        try {
            socket = SocketChannel.open(address);
            socket.configureBlocking(true);
            String sb = "LOGIN " + username + " " + password + " " +
                    UDPport;
            return write(sb);
        } catch (IOException e) {
            System.err.println("Error opening socket " + e);
            return "Error opening socket";
        }
    }

    public void logout(String username) throws IOException {
        if (socket != null && socket.isOpen()) {
            write("LOGOUT " + username);
            socket.close();

        }
        if (challengeSocket != null) {
            challengeSocket=null;
        }
    }

    private String write(String command) {
        length.putInt(command.length());
        length.flip();
        msg = ByteBuffer.wrap(command.getBytes());
        ByteBuffer[] bfs = {length, msg};
        int l = 0;
        try {
            socket.write(bfs);
            length.clear();
            msg.clear();
            reply.clear();
            l = socket.read(reply);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Login non effettuato");
        }
        reply.flip();
        if (!command.startsWith("LOGOUT"))
            return new String(reply.array(), 0, l).trim();
        return "";
    }

    public String register(String username, String password) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(9999);
            registration = (Registration) registry.lookup(Registration.SERVICE_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            return "Error connecting to registering service";
        }
        return registration.register(username, password);
    }

    public String aggiungi_amico(String username, String friendname) {
        return write("ADD_FRIEND " + username + " " + friendname);
    }

    public String sfida(String username, String friendname) {
        return write("CHALLENGE " + username + " " + friendname);
    }

    public String mostra_classifica(String username) {
        return write("RANK " + username);
    }

    public String word(String prevAnswer) {
        StringBuilder sb = new StringBuilder();
        sb.append("WORD ");
        if (prevAnswer != null) {
            sb.append(prevAnswer);
        }
        return write(sb.toString());
    }
}