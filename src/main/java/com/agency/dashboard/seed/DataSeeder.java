package com.agency.dashboard.seed;

import com.agency.dashboard.domain.*;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.CreativeRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ClientRepository clients;
    private final CreativeRequestRepository requests;

    public DataSeeder(ClientRepository clients, CreativeRequestRepository requests) {
        this.clients = clients;
        this.requests = requests;
    }

    @Override
    public void run(String... args) {
        if (clients.count() > 0) return;

        var c1 = clients.save(new Client("Óticas Tavares"));
        var c2 = clients.save(new Client("Clínica Lucas & Cruz"));
        var c3 = clients.save(new Client("Win Soluções"));

        CreativeRequest r1 = new CreativeRequest();
        r1.setClient(c1);
        r1.setType(RequestType.CARROSSEL);
        r1.setStatus(RequestStatus.ENTREGUE);
        r1.setTitle("Dia da Mulher - carrossel");
        r1.setCreatedAt(LocalDateTime.now().minusDays(10));
        r1.setDeliveredAt(LocalDateTime.now().minusDays(8));

        CreativeRequest r2 = new CreativeRequest();
        r2.setClient(c1);
        r2.setType(RequestType.CRIATIVO_TRAFEGO);
        r2.setStatus(RequestStatus.EM_REVISAO);
        r2.setTitle("Criativo tráfego - oferta");
        r2.setCreatedAt(LocalDateTime.now().minusDays(3));

        CreativeRequest r3 = new CreativeRequest();
        r3.setClient(c2);
        r3.setType(RequestType.SOCIAL_MEDIA);
        r3.setStatus(RequestStatus.EM_PRODUCAO);
        r3.setTitle("Posts institucionais");
        r3.setCreatedAt(LocalDateTime.now().minusDays(5));

        CreativeRequest r4 = new CreativeRequest();
        r4.setClient(c3);
        r4.setType(RequestType.STORY);
        r4.setStatus(RequestStatus.ENTREGUE);
        r4.setTitle("Stories - CNH");
        r4.setCreatedAt(LocalDateTime.now().minusDays(14));
        r4.setDeliveredAt(LocalDateTime.now().minusDays(12));

        requests.saveAll(List.of(r1, r2, r3, r4));
    }
}