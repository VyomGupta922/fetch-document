package com.docment.fetch.repo;


import com.docment.fetch.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> ,
        JpaSpecificationExecutor<Document> {
    List<Document> findByContentContainingIgnoreCase(String keyword);
    Page<Document> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
}
