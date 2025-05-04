package com.docment.fetch.serviceTest;

import com.docment.fetch.entity.Document;
import com.docment.fetch.repo.DocumentRepository;
import com.docment.fetch.service.DocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    public void testUploadDocument() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "sample content".getBytes());
        Document mockDoc = new Document();
        mockDoc.setId(1L);

        when(documentRepository.save(any(Document.class))).thenReturn(mockDoc);

        Document saved = documentService.uploadDocument("Title", "Author", "TXT", file);

        assertNotNull(saved);
        verify(documentRepository, times(1)).save(any(Document.class));
    }


    @Test
    public void testSearchDocuments() {
        String keyword = "test";
        List<Document> mockList = List.of(new Document());

        when(documentRepository.findByContentContainingIgnoreCase(keyword)).thenReturn(mockList);

        List<Document> result = documentService.searchByKeyword(keyword);

        assertEquals(1, result.size());
        verify(documentRepository, times(1)).findByContentContainingIgnoreCase(keyword);
    }
}
