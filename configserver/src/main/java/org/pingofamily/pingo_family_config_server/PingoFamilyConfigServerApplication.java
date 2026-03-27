package org.pingofamily.pingo_family_config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class PingoFamilyConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingoFamilyConfigServerApplication.class, args);
    }

}
