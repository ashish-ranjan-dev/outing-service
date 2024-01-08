package com.outing.outingDashboard.services;

import com.outing.auth.api.dto.UserDto;
import com.outing.expense.api.dto.ExpenseDto;
import com.outing.friendship.api.dto.FriendshipDto;
import com.outing.outingDashboard.dto.OutingDto;
//import com.outing.outingDashboard.dto.OutingExpensesDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface OutingService extends Serializable {

    public OutingDto createOuting(Map<String, Object> data);

    public OutingDto updateOuting(String outingId, Map<String, Object> requestData);

    public void deleteOuting(String outingId);

    public List<OutingDto> getAllOutings();


    public OutingDto getOutingByOutingId(String outingId);

    void addFriendInOuting(String outingId, Map<String, Object> outingDto);

    void updateOutingStatus(String outingId, String userId, String status);

    List<UserDto> getFriends(String outingId, String userId);

//    public ExpenseDto addOutingExpense(ExpenseDto expenseDto, String outingId);
}
