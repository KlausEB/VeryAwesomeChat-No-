package com.epam.Multithreading.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryImplTest {

    @Test
    void callUpHistory_True_ReturnedNullAtBufferedReaderEntailsAnEmptyString() throws IOException {
        //GIVEN
        String emptyString = "";
        BufferedReader bufferedReader = mock(BufferedReader.class);
        Queue<String> queue = new ArrayDeque<>();
        doReturn(null).when(bufferedReader).readLine();
        HistoryImpl history = new HistoryImpl(bufferedReader, queue);

        //WHEN
        String emptyMessage = history.callUpHistory();

        //THEN
        assertEquals(emptyMessage, emptyString);
        assertEquals(0, queue.size());
    }

    @Test
    void findLatestHistoryAndPutInQueue_Verified_VerifyCorrectFillingQueue() throws IOException {
        //GIVEN
        String firstString = "Hello";
        String secondString = "I'm the test";
        String thirdString = "Very";
        String fourthString = "Awesome";
        String fifthString = "Cool";
        String sixthString = "Test";
        BufferedReader bufferedReader = mock(BufferedReader.class);
        Queue<String> queue = mock(Queue.class);
        doReturn(firstString, secondString, thirdString, fourthString, fifthString, sixthString, null).when(bufferedReader).readLine();
        doReturn(fifthString).when(queue).remove();
        doReturn(0, 1, 2, 3, 4, 5).when(queue).size();
        HistoryImpl history = new HistoryImpl(bufferedReader, queue);

        //WHEN
        history.findLatestHistoryAndPutInQueue();

        //THEN
        verify(queue).add(firstString);
        verify(queue).add(secondString);
        verify(queue).remove();
        verify(queue).add(sixthString);
    }
}