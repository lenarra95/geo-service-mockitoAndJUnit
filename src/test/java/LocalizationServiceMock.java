import ru.netology.entity.Country;
import ru.netology.i18n.LocalizationService;

public class LocalizationServiceMock implements LocalizationService {

    @Override
    public String locale (Country country) {
        switch (country) {
            case RUSSIA:
                return "Добро пожаловать";
            default:
                return "Welcome";
        }
    }
}
