package tech.nomad4;

import org.junit.jupiter.api.Test;
import tech.nomad4.dockersocketmanager.service.SocatSetupResult;

import static org.junit.jupiter.api.Assertions.*;

class SocatSetupResultTest {

    @Test
    void external_setsPortCorrectly() {
        SocatSetupResult result = SocatSetupResult.external(2375);
        assertEquals(2375, result.getPort());
    }

    @Test
    void external_managedByUsIsFalse() {
        SocatSetupResult result = SocatSetupResult.external(2375);
        assertFalse(result.isManagedByUs());
    }

    @Test
    void external_pidIsNull() {
        SocatSetupResult result = SocatSetupResult.external(2375);
        assertNull(result.getPid());
    }

    @Test
    void external_descriptionContainsPort() {
        SocatSetupResult result = SocatSetupResult.external(2375);
        assertTrue(result.getDescription().contains("2375"));
    }

    @Test
    void managedByUs_setsPortCorrectly() {
        SocatSetupResult result = SocatSetupResult.managedByUs(2375, 12345);
        assertEquals(2375, result.getPort());
    }

    @Test
    void managedByUs_managedByUsIsTrue() {
        SocatSetupResult result = SocatSetupResult.managedByUs(2375, 12345);
        assertTrue(result.isManagedByUs());
    }

    @Test
    void managedByUs_pidIsSet() {
        SocatSetupResult result = SocatSetupResult.managedByUs(2375, 12345);
        assertEquals(12345, result.getPid());
    }

    @Test
    void managedByUs_descriptionContainsPort() {
        SocatSetupResult result = SocatSetupResult.managedByUs(2375, 12345);
        assertTrue(result.getDescription().contains("2375"));
    }
}
