package client.scenes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import client.utils.ServerUtils;
import org.junit.jupiter.api.Test;

class ServerConnectionCtrlTest {


    /**
     * testing whether the setIsConnected sets the value correctly
     */
    @Test
    public void testSetIsConnected() {
        ServerConnectionCtrl ctrl =
                new ServerConnectionCtrl(new ServerUtils(), new MainCtrlTalio(new ServerUtils()));
        assertFalse(ctrl.isIsConnected());
        ctrl.setIsConnected(true);
        assertTrue(ctrl.isIsConnected());
    }

    /**
     * testing whether the correct method is being called my the mainCtrl
     */
    @Test
    public void testClickBackHome() {
        MainCtrlTalio mainCtrlTalio = mock(MainCtrlTalio.class);
        ServerConnectionCtrl ctrl = new ServerConnectionCtrl(new ServerUtils(), mainCtrlTalio);
        ctrl.clickBackHome();
        verify(mainCtrlTalio, times(1)).showHome();

    }

}

