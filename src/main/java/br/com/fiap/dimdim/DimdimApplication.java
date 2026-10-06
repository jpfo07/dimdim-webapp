package br.com.fiap.dimdim;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DimdimApplication {

    public static void main(String[] args) {
        // Anexa o agente do Application Insights. A connection string vem da
        // variavel de ambiente APPLICATIONINSIGHTS_CONNECTION_STRING (App Setting).
        ApplicationInsights.attach();
        SpringApplication.run(DimdimApplication.class, args);
    }
}
