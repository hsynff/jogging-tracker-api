package com.jogging.tracker.repository;

import com.jogging.tracker.model.entity.Record;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    @Query(value = "select min(date) dateFrom, max(date) dateTo, week(date) weekOfYear, avg(distance/time) avgSpeed, avg(distance) avgDistance " +
            "from record where year(date)=:year and month(date)=:month and id_user=:idUser group by weekOfYear order by weekOfYear asc", nativeQuery = true)
    List<ReportTuple> generateWeeklyReport(@Param("year") Integer year, @Param("month") Integer month, @Param("idUser") Long idUser);


    interface ReportTuple {
        LocalDate getDateFrom();

        LocalDate getDateTo();

        Integer getWeekOfYear();

        Double getAvgSpeed();

        Double getAvgDistance();
    }

    static Specification<Record> recordByUserSpec(Long idUser) {
        return new Specification<Record>() {
            @Override
            public Predicate toPredicate(Root<Record> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("user").get("id"), idUser);
            }
        };
    }
}
