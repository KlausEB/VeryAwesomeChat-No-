package com.epam.Multithreading.impl;

import com.epam.Multithreading.IHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectedThreadTest {

    @InjectMocks
    @Spy
    ConnectedThread connectedThread;

    @Mock
    Socket mockSocket;
    @Mock
    CopyOnWriteArraySet<PrintWriter> mockCOWAS;
    @Mock
    IHistory mockHistory;
    @Mock
    BufferedReader mockSocketReader;
    @Mock
    PrintWriter mockSocketWriter;

    @BeforeEach
    void init() throws IOException {
        connectedThread =spy(
                new ConnectedThread(mockSocket, mockCOWAS, mockHistory, mockSocketReader, mockSocketWriter));
    }

    @Test
    void takeNicknameFromClient_True_CheckingCorrectNickname() throws IOException {
        //GIVEN
        doNothing().when(mockSocketWriter).println(anyString());
        doNothing().when(mockSocketWriter).flush();
        String nickname = "Николай";
        doReturn(nickname).when(mockSocketReader).readLine();

        //WHEN
        String actual = connectedThread.takeNicknameFromClient();

        //THEN
        assertEquals("<" + nickname + "> ", actual);
    }

    @Test
    void run_Verified_VerificationOFTheLogicalSequence() throws IOException {
        //GIVEN
        String nickname = "<Николай>";
        String firstMessage = "Hello";
        String secondMessage = "!EXIT";
        String disconnectMessage = "User " + nickname + "log out of chat";
        doReturn(true).when(mockCOWAS).add(mockSocketWriter);
        doReturn(nickname).when(connectedThread).takeNicknameFromClient();
        doNothing().when(connectedThread).sendHistoryToClient();
        doReturn(false, false, true).when(mockSocket).isInputShutdown();
        doReturn(true, true).when(mockSocketReader).ready();
        doReturn(firstMessage,secondMessage).when(mockSocketReader).readLine();
        doReturn(true).when(mockCOWAS).remove(mockSocketWriter);
        doNothing().when(connectedThread).sendMessageToAllClients(anyString());
        doNothing().when(mockHistory).updateHistory(anyString());
        doNothing().when(mockSocketReader).close();
        doNothing().when(mockSocket).close();

        //WHEN
        connectedThread.run();

        //THEN
        verify(connectedThread).sendMessageToAllClients(nickname + firstMessage);
        verify(connectedThread).sendMessageToAllClients(disconnectMessage);
        verify(mockCOWAS, times(1)).remove(mockSocketWriter);
        verify(mockSocket, times(2)).isInputShutdown();
    }
}