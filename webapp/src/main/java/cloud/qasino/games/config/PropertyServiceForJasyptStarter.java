package cloud.qasino.games.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class PropertyServiceForJasyptStarter {

    @Value("${encrypted.property.sa}")
    private String propertySa;

    public String getPropertySa() {
        return propertySa;
    }

}
