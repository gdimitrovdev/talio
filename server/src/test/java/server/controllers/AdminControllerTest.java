package server.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {
    @InjectMocks
    private AdminController adminControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get() {

    }

    @Test
    void getBoards() {

    }

    @Test
    void clear() {

    }

    @Test
    void refill() {

    }

    @Test
    void fill() {

    }

}
