import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageSenderTests {

    public Stream<Arguments> correctArgumentsForSenderTest () {
        return Stream.of(
                Arguments.of("172.123.12.19", "Добро пожаловать"),
                Arguments.of("96.123.12.19", "Welcome")
                        );
    }

    public Stream<Arguments> argumentsForGeoServiceTest () {
        return Stream.of(
                Arguments.of(null, "127.0.0.1"),
                Arguments.of(Country.RUSSIA, "172.0.32.11"),
                Arguments.of(Country.USA, "96.44.183.149"),
                Arguments.of(Country.RUSSIA, "172.12.22.1"),
                Arguments.of(Country.USA, "96.44.442.11")
                        );
    }

    @ParameterizedTest
    @MethodSource("correctArgumentsForSenderTest")
    public void positiveSenderTest (String ip, String msg) {
        GeoService geoService = new GeoServiceMock();
        LocalizationService localizationService = new LocalizationServiceMock();
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);
        Assertions.assertEquals(msg, messageSender.send(headers));
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "34.3.4.5"})
    public void negativeSenderTest (String ip) {
        GeoService geoService = new GeoServiceMock();
        LocalizationService localizationService = new LocalizationServiceMock();
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);
        Assertions.assertThrows(NullPointerException.class, () -> messageSender.send(headers));
    }

    @ParameterizedTest
    @MethodSource("argumentsForGeoServiceTest")
    public void geoServiceTest (Country country, String id) {
        GeoService geoService = new GeoServiceImpl();
        Assertions.assertEquals(country, geoService.byIp(id).getCountry());
    }

    @ParameterizedTest
    @EnumSource(Country.class)
    public void localServiceTestForRU (Country country) {
        LocalizationService localService = new LocalizationServiceImpl();
        String msg = country.equals(Country.RUSSIA) ? "Добро пожаловать" : "Welcome";
        Assertions.assertEquals(msg, localService.locale(country));
    }
}
