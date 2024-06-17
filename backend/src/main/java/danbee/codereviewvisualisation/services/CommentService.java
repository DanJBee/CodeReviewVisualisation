package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Comment;
import danbee.codereviewvisualisation.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Comment service class.
 *
 * @author Dan Bee
 */
@Service
public class CommentService {

  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public List<Comment> findByNumber(Long number) {
    return commentRepository.findByNumber(number);
  }

  public List<Comment> findByAuthorId(String authorId) {
    return commentRepository.findByAuthorId(authorId);
  }

  public List<Comment> findByCreatedAt(Timestamp timestamp) {
    return commentRepository.findByCreatedAt(timestamp);
  }
}
