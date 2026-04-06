package com.leadpro1.repository;

import com.leadpro1.entity.Lead;
import com.leadpro1.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    // Get all leads of a LeadList (simple, no filtering)
    List<Lead> findByLeadListId(UUID leadListId);

    /**
     * Search & filter leads.
     *
     * Root cause of the original error:
     *   PostgreSQL's lower() does NOT accept bytea. If phone/email columns are
     *   stored as bytea (or Hibernate mapped them that way), calling lower(phone)
     *   throws "function lower(bytea) does not exist".
     *
     * Fix: native SQL + explicit CAST(...AS TEXT) on every searched column.
     * This is safe whether the underlying type is varchar, text, or bytea.
     *
     * Status is received as a String here (instead of LeadStatus enum) because
     * native queries don't auto-convert enums to their string representation.
     */
    @Query(value = """
        SELECT *
        FROM lead
        WHERE lead_list_id = :leadListId
          AND (
              CAST(:search AS TEXT) IS NULL
              OR lower(CAST(name  AS TEXT)) LIKE lower(CONCAT('%', CAST(:search AS TEXT), '%'))
              OR lower(CAST(phone AS TEXT)) LIKE lower(CONCAT('%', CAST(:search AS TEXT), '%'))
              OR lower(CAST(email AS TEXT)) LIKE lower(CONCAT('%', CAST(:search AS TEXT), '%'))
          )
          AND (
              CAST(:status AS TEXT) IS NULL
              OR status = CAST(:status AS TEXT)
          )
          AND (
              :inNeed IS NULL
              OR in_need = :inNeed
          )
        """, nativeQuery = true)
    List<Lead> searchAndFilter(
            @Param("leadListId") UUID    leadListId,
            @Param("search")     String  search,
            @Param("status")     String  status,
            @Param("inNeed")     Boolean inNeed
    );
}