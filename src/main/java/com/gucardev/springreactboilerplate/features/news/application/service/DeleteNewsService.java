package com.gucardev.springreactboilerplate.features.news.application.service;

import com.gucardev.springreactboilerplate.features.news.application.port.in.DeleteNewsUseCase;
import com.gucardev.springreactboilerplate.features.news.application.port.out.DeleteNewsPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteNewsService implements DeleteNewsUseCase {

    private final NewsFinder finder;
    private final DeleteNewsPort deleteNewsPort;

    @Override
    @Transactional
    public void delete(UUID id) {
        deleteNewsPort.delete(finder.findById(id));
    }
}
