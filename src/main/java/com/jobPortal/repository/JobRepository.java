package com.jobPortal.repository;

import com.jobPortal.entity.Job;
import com.jobPortal.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	@Query("SELECT j FROM Job j JOIN j.applicants a " +
			"WHERE a.applicantId = :applicantId AND a.applicationStatus = :applicationStatus")
	List<Job> findByApplicantIdAndApplicationStatus(@Param("applicantId") Long applicantId,
													@Param("applicationStatus") ApplicationStatus applicationStatus);

	List<Job> findByPostedBy(Long postedBy);
}
