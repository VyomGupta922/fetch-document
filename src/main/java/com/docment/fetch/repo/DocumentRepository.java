package com.docment.fetch.repo;


import com.docment.fetch.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByContentContainingIgnoreCase(String keyword);
}
