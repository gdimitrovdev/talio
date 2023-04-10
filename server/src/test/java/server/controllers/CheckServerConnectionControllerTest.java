package server.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckServerConnectionControllerTest {
    @InjectMocks
    private CheckServerConnectionController checkServerConnectionControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConnection() {
        assertTrue(checkServerConnectionControllerMock.testConnection().getStatusCode()
                .is2xxSuccessful());
    }
}
