package com.recipe.jamanchu.notify;

import com.recipe.jamanchu.model.dto.response.notify.Notify;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

@Component
public class SubmissionPublisherBean {

  @Bean
  public Map<Long,FluxSink<Notify>> submissionPublisher(){
    return new ConcurrentHashMap<>();
  }

}
