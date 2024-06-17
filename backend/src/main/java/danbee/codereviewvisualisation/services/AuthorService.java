package danbee.codereviewvisualisation.services;

import danbee.codereviewvisualisation.models.Author;
import danbee.codereviewvisualisation.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

/**
 * Author service class.
 *
 * @author Dan Bee
 */
@Service
public class AuthorService {

  private final AuthorRepository authorRepository;

  public AuthorService(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  public Author findByAuthorId(String authorId) {
    return authorRepository.findById(authorId).orElseThrow();
  }
}
