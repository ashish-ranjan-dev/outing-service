package com.outing.outingDashboard.controller;

import com.outing.auth.api.dto.UserDto;
import com.outing.commons.api.response.OutingResponse;
import com.outing.expense.api.dto.ExpenseDto;
import com.outing.friendship.api.dto.FriendshipDto;
import com.outing.outingDashboard.dto.OutingDto;
//import com.outing.outingDashboard.dto.OutingExpensesDto;
import com.outing.outingDashboard.services.OutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/outing")
@CrossOrigin("http://localhost:4200")
public class OutingRestController {

    @Autowired
    OutingService outingService;

    @SuppressWarnings("unchecked")
    @GetMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
    OutingResponse<List<OutingDto>> getAllOutings(){
        List<OutingDto> outings= outingService.getAllOutings();
        return new OutingResponse<>(outings, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @GetMapping(value = "/{outingId}",produces = MediaType.APPLICATION_JSON_VALUE)
    OutingResponse<OutingDto> getOutingById(@PathVariable String outingId ){
        OutingDto outing= outingService.getOutingByOutingId(outingId);
        return new OutingResponse<>(outing, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/create")
    OutingResponse<OutingDto> createOuting(@RequestBody Map<String, Object> requestData){
        OutingDto newOuting = outingService.createOuting(requestData);
        List<String> messages = new ArrayList<>();
        messages.add("Outing added successfully!");
        return new OutingResponse<>(newOuting,HttpStatus.OK,messages);
    }

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/{outingId}/update")
    public OutingResponse<OutingDto> updateOuting(@PathVariable(value = "outingId") String outingId,@RequestBody Map<String, Object> outingDto)  {
        OutingDto newOuting = outingService.updateOuting(outingId, outingDto);
        List<String> messages = new ArrayList<>();
        messages.add("Outing updated successfully!");
        return new OutingResponse<>(newOuting,HttpStatus.OK,messages);
    }

    @PostMapping(value = "/{outingId}/invite-friend-in-outing")
    public OutingResponse<Void> addFriendInOuting(@PathVariable(value = "outingId") String outingId,@RequestBody Map<String, Object> outingDto)  {
        outingService.addFriendInOuting(outingId, outingDto);
        List<String> messages = new ArrayList<>();
        messages.add("Friend invited for outing successfully!");
        return new OutingResponse<>(null,HttpStatus.OK,messages);
    }

    @PostMapping(value = "/{outingId}/user/{userId}/status")
    public OutingResponse<Void> updateOutingStatus(@PathVariable(value = "outingId") String outingId,@PathVariable String userId , @RequestBody String status)  {
        outingService.updateOutingStatus(outingId, userId, status);
        List<String> messages = new ArrayList<>();
        messages.add("Outing invitation "+ status.toLowerCase() + " successfully!");
        return new OutingResponse<>(null,HttpStatus.OK,messages);
    }

    @GetMapping(value = "/{outingId}/user/{userId}/get-friends")
    public OutingResponse<List<UserDto>> getFriends(@PathVariable(value = "outingId") String outingId, @PathVariable String userId )  {
        List<UserDto> friendshipDtoLis = outingService.getFriends(outingId, userId);
        return new OutingResponse<>(friendshipDtoLis,HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping(value = "/{outingId}/delete")
    public OutingResponse<Void> deleteOuting(@PathVariable String outingId){
        outingService.deleteOuting(outingId);
        List<String> messages = new ArrayList<>();
        messages.add("Outing deleted successfully!");
        return new OutingResponse<>(null,HttpStatus.OK,messages);
    }


}
