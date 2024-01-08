package com.outing.outingDashboard.repository;

import com.outing.outingDashboard.model.OutingDetailsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutingDetailsRepository extends JpaRepository<OutingDetailsModel,String> {

     List<OutingDetailsModel> findByUserId(String userId);

     List<OutingDetailsModel> findByOutingId(String outingId);

     OutingDetailsModel findByOutingIdAndUserId(String outingId, String userId);

}
