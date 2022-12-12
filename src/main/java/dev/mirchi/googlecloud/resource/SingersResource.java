package dev.mirchi.googlecloud.resource;

import dev.mirchi.googlecloud.repository.SingersRepo;
import dev.mirchi.googlecloud.repository.model.Singer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/singers")
public class SingersResource {

    private final SingersRepo singersRepo;

    @Autowired
    public SingersResource(SingersRepo singersRepo) {
        this.singersRepo = singersRepo;
    }

    @GetMapping("/{id}")
    private Mono<Singer> getSingerById(@PathVariable Long id) {
        return Mono.justOrEmpty(singersRepo.findById(id));
    }

    @GetMapping
    private Flux<Singer> getAllSingers() {
        return Flux.fromIterable(singersRepo.findAll());
    }
}
