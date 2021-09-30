package com.epam.Multithreading;

import com.epam.Multithreading.impl.ClientImpl;
import com.epam.Multithreading.impl.ServerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        String APP_PROPERTIES = "/app.properties";
        InputStream inputStream = App.class.getResourceAsStream(APP_PROPERTIES);
        properties.load(inputStream);

        String serverIP = properties.getProperty("serverIP");
        int port = Integer.parseInt(properties.getProperty("port"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input !startServer or !startClient: ");
        String command = reader.readLine();
        if (command.equals("!startServer")) {
            IServer server = new ServerImpl(port);
            server.serverStart();
        }
        if (command.equals("!startClient")) {
            IClient client = new ClientImpl(serverIP, port);
            client.logInToChat();
        }
    }
}
