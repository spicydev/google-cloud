package dev.mirchi.googlecloud.repository;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import dev.mirchi.googlecloud.repository.model.Singer;
import org.springframework.stereotype.Repository;

@Repository
public interface SingersRepo extends SpannerRepository<Singer, Long> {
}
