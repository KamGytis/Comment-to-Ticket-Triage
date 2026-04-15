package com.pulsedesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final HuggingFaceService huggingFaceService;

    @Transactional
    public Mono<Comment> submitComment(String text, String source) {

        // 1. Validation
        if (text == null || text.isBlank()) {
            return Mono.error(new IllegalArgumentException("Comment text must not be empty"));
        }

        // 2. Initial Comment Setup
        Comment comment = new Comment();
        comment.setText(text);
        comment.setSource(source);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setConvertedToTicket(false);

        // 3. Save to DB (Blocking call before the reactive chain)
        Comment savedComment = commentRepository.save(comment);

        // 4. Start the Reactive Chain
        return huggingFaceService.analyzeComment(text)
            .flatMap(analysis -> {
                // If AI decides this should be a ticket
                if (analysis != null && analysis.path("isTicket").asBoolean(false)) {

                    Ticket ticket = new Ticket();
                    ticket.setTitle(analysis.path("title").asText("No title"));
                    ticket.setCategory(analysis.path("category").asText("other").toUpperCase());
                    ticket.setPriority(analysis.path("priority").asText("medium").toUpperCase());
                    ticket.setSummary(analysis.path("summary").asText("No summary"));
                    ticket.setCommentId(savedComment.getId());
                    ticket.setCreatedAt(LocalDateTime.now());

                    ticketRepository.save(ticket);

                    savedComment.setConvertedToTicket(true);
                    return Mono.just(commentRepository.save(savedComment));
                }
                // If not a ticket, just return the saved comment
                return Mono.just(savedComment);
            })
            .doOnError(e -> System.err.println("Critical error in triage flow: " + e.getMessage()))
            .onErrorReturn(savedComment); // Fallback: always return the comment
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}