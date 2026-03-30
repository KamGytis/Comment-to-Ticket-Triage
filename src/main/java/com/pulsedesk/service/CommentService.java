package com.pulsedesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
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
    public Comment submitComment(String text, String source) {

        Comment comment = new Comment();
        comment.setText(text);
        comment.setSource(source);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setConvertedToTicket(false);

        comment = commentRepository.save(comment);

        try {
            JsonNode analysis = huggingFaceService.analyzeComment(text);

            if (analysis != null && analysis.path("isTicket").asBoolean(false)) {

                Ticket ticket = new Ticket();
                ticket.setTitle(analysis.path("title").asText("No title"));
                ticket.setCategory(analysis.path("category").asText("other"));
                ticket.setPriority(analysis.path("priority").asText("medium"));
                ticket.setSummary(analysis.path("summary").asText("No summary"));
                ticket.setCommentId(comment.getId());
                ticket.setCreatedAt(LocalDateTime.now());

                ticketRepository.save(ticket);

                // FIX: must save comment again so convertedToTicket=true persists in DB
                comment.setConvertedToTicket(true);
                comment = commentRepository.save(comment);
            }

        } catch (Exception e) {
            System.err.println("Error analyzing comment: " + e.getMessage());
        }

        return comment;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}