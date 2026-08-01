package com.owned.collection_service.repository;

import com.owned.collection_service.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCollectionItemId(Long collectionItemId);

    Optional<Certificate> findBySerialNo(String serialNo);
}
