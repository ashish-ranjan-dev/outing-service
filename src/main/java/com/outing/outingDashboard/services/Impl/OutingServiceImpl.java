package com.outing.outingDashboard.services.Impl;

import com.outing.auth.api.controller.UserController;
import com.outing.auth.api.dto.UserDto;
import com.outing.auth.security.util.PrincipalDetails;
import com.outing.commons.api.exception.OutingException;
import com.outing.expense.api.controller.ExpenseCheckController;
import com.outing.expense.api.dto.ExpenseDto;
import com.outing.friendship.api.controller.FriendshipCheckController;
import com.outing.friendship.api.dto.DummyUserDto;
import com.outing.friendship.api.dto.FriendshipDto;
import com.outing.outingDashboard.dto.OutingDetailsDto;
import com.outing.outingDashboard.dto.OutingDto;
import com.outing.outingDashboard.model.OutingDetailsModel;
import com.outing.outingDashboard.model.OutingModel;
import com.outing.outingDashboard.repository.OutingDetailsRepository;
import com.outing.outingDashboard.repository.OutingRepository;
import com.outing.outingDashboard.services.OutingService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OutingServiceImpl implements OutingService {


    private final OutingRepository outingRepository;
    private final OutingDetailsRepository outingDetailsRepository;
    private final PrincipalDetails principalDetails;
    private final FriendshipCheckController friendshipCheckController;
    private final ExpenseCheckController expenseCheckController;
    @Autowired
    UserController userController;



    @Autowired
    public OutingServiceImpl(ExpenseCheckController expenseCheckController,OutingRepository outingRepository, OutingDetailsRepository outingDetailsRepository, PrincipalDetails principalDetails, FriendshipCheckController friendshipCheckController){
        this.outingRepository = outingRepository;
        this.outingDetailsRepository = outingDetailsRepository;
        this.principalDetails = principalDetails;
        this.friendshipCheckController = friendshipCheckController;
        this.expenseCheckController = expenseCheckController;

    }

    public List<OutingDetailsDto> convertToDetailsDtos(List<OutingDetailsModel> outingDetailsModels){
        List<OutingDetailsDto> outingDetailsDtos = new ArrayList<>();
        for(OutingDetailsModel outingDetailsModel : outingDetailsModels){
           UserDto user = this.userController.getUserByUserId(outingDetailsModel.getUserId()).getBody();
           if(user==null) {
               DummyUserDto dummyUser = this.friendshipCheckController.getDummyUserById(outingDetailsModel.getUserId()).getBody();
               user = new UserDto(dummyUser.getId(),dummyUser.getName(),null);
           }
           outingDetailsDtos.add(new OutingDetailsDto(outingDetailsModel.getId(),outingDetailsModel.getOutingId(),user, outingDetailsModel.getStatus()));
        }
        return outingDetailsDtos;
    }

    public List<OutingDto> convertToDtos(List<OutingModel> outings){
       List<OutingDto> outingDtos = new ArrayList<>();
        for(OutingModel outingModel : outings){
            UserDto creator = this.userController.getUserByUserId(outingModel.getCreatorId()).getBody();
            List<OutingDetailsModel> outingDetailsModels = this.outingDetailsRepository.findByOutingId(outingModel.getId());
            List<OutingDetailsDto> outingDetailsDtos = convertToDetailsDtos(outingDetailsModels);
            outingDtos.add(new OutingDto(outingModel.getId(), outingModel.getDescription(), outingModel.getOutingName(), outingModel.getDate(), creator, outingDetailsDtos));
            Collections.sort(outingDetailsDtos, (dto1, dto2) -> {
                // Custom comparator to prioritize "invited" > "rejected" > "accepted"
                String status1 = dto1.getStatus();
                String status2 = dto2.getStatus();

                if (status1.equals(status2)) {
                    return 0; // Both have the same status
                } else if (status1.equals("invited")) {
                    return -1; // "invited" comes before "rejected" and "accepted"
                } else if (status1.equals("rejected") && status2.equals("accepted")) {
                    return -1; // "rejected" comes before "accepted"
                } else {
                    return 1; // "accepted" comes after "rejected" and "invited"
                }
            });
        }
        outingDtos.sort(Comparator.comparing(OutingDto::getDate).reversed());
        return outingDtos;
    }

    public List<OutingDto> convertToDtosById(List<OutingModel> outings){
        String loggedInUser = this.principalDetails.getPrincipalDetails().getId();
        List<OutingDto> outingDtos = new ArrayList<>();
        for(OutingModel outingModel : outings){
            UserDto creator = this.userController.getUserByUserId(outingModel.getCreatorId()).getBody();
            List<OutingDetailsModel> outingDetailsModels = this.outingDetailsRepository.findByOutingId(outingModel.getId());
            boolean check = false;
            for(OutingDetailsModel outingDetailsModel:outingDetailsModels){
                if (Objects.equals(outingDetailsModel.getUserId(), loggedInUser)) {
                    check = true;
                    break;
                }
            }
            if(!check){
                throw new OutingException("Outing not found",HttpStatus.BAD_REQUEST);
            }
            List<OutingDetailsDto> outingDetailsDtos = convertToDetailsDtos(outingDetailsModels);
            outingDtos.add(new OutingDto(outingModel.getId(), outingModel.getDescription(), outingModel.getOutingName(), outingModel.getDate(), creator, outingDetailsDtos));
        }
        return outingDtos;
    }

    public List<OutingDto> getAllOutings(){
        String loggedInUser = this.principalDetails.getPrincipalDetails().getId();
        System.out.println(loggedInUser);
        List<OutingDetailsModel> outingDetails = this.outingDetailsRepository.findByUserId(loggedInUser);
        List<OutingModel> outingModels = new ArrayList<>();
        for(OutingDetailsModel outingDetail : outingDetails){
            String outingId = outingDetail.getOutingId();
            Optional<OutingModel> optionalOutingModel = this.outingRepository.findById(outingId);
            optionalOutingModel.ifPresent(outingModels::add);
        }
        return convertToDtos(outingModels);
    }

    public OutingDto getOutingByOutingId(String outingId){
        List<OutingModel> outingModels = new ArrayList<>();
        Optional<OutingModel> optionalOutingModel = this.outingRepository.findById(outingId);
        optionalOutingModel.ifPresent(outingModels::add);
        if(outingModels.isEmpty()){
            List<String> errors = new ArrayList<>();
            errors.add("Outing not present!");
            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
        }
        return convertToDtosById(outingModels).get(0);

    }

    @Override
    public void addFriendInOuting(String outingId, Map<String, Object> outingDto) {
        if(outingId!=null){
            List<String> userIds = (List<String>) outingDto.get("userIds");
            if(!checkFriendship(outingDto).isEmpty()){
                List<String> errors = checkFriendship(outingDto);
                throw new OutingException(errors , HttpStatus.BAD_REQUEST);
            }
            List<OutingDetailsModel> outingDetailsModels = this.outingDetailsRepository.findByOutingId(outingId);
            for(OutingDetailsModel outingDetailsModel : outingDetailsModels){
                if(userIds.contains(outingDetailsModel.getUserId())){
                    userIds.remove(outingDetailsModel.getUserId());
                }
            }

            // i will only get those friends that are neither invited nor accpeted in outing
            for(String user : userIds){
                String outingDetailsId = UUID.randomUUID().toString();
                OutingDetailsModel outingDetailsModel = new OutingDetailsModel(outingDetailsId, outingId, user,"invited" );
                this.outingDetailsRepository.save(outingDetailsModel);
            }
        }
        else{
            throw new OutingException("Outing id cannot be null",HttpStatus.BAD_REQUEST);
        }
    }

    public List<UserDto> getFriends(String outingId, String userId){
        if(outingId == null){
            throw  new OutingException("Outing is not present" , HttpStatus.BAD_REQUEST);
        }else if(userId == null){
            throw  new OutingException("User not found", HttpStatus.BAD_REQUEST);
        }else{
            OutingDetailsModel outingDetailsModel = this.outingDetailsRepository.findByOutingIdAndUserId(outingId, userId);

            if(outingDetailsModel == null){
                throw new OutingException("You are not the part of outing", HttpStatus.BAD_REQUEST);
            }else {
              List<FriendshipDto> friendshipDtos = this.friendshipCheckController.getAllFriendsByUserId(userId).getBody();

              String loggedInUser = this.principalDetails.getPrincipalDetails().getId();
                System.out.println(loggedInUser+"logged");
              List<UserDto> listOfFriends = new ArrayList<>();
              for(FriendshipDto friendshipDto: friendshipDtos){
                  if(friendshipDto.getInviteeUser() != null){
                      if(Objects.equals(friendshipDto.getInviteeUser().getId(), loggedInUser)){
                          System.out.println(friendshipDto.getInviterUser().getId()+"inviter");
                          listOfFriends.add(friendshipDto.getInviterUser());
                      }else{
                          System.out.println(friendshipDto.getInviteeUser().getId()+"invitee");
                          listOfFriends.add(friendshipDto.getInviteeUser());
                      }
                  }else{
                      UserDto userDto = new UserDto(friendshipDto.getDummyUser().getId(), friendshipDto.getDummyUser().getName(), null);
                      System.out.println(friendshipDto.getDummyUser().getId()+"dummy");
                      listOfFriends.add(userDto);
                  }
              }
                List<UserDto> friendUtils = new ArrayList<>();
              for(UserDto userDto:listOfFriends){
                  friendUtils.add(userDto);
              }
              for(UserDto f:friendUtils){
                  System.out.println(f.getId());
              }
                System.out.println(friendUtils.size());
              for(UserDto user : friendUtils){
                  OutingDetailsModel outingDetailsModel1 = this.outingDetailsRepository.findByOutingIdAndUserId(outingId, user.getId());
                  System.out.println(outingId+" "+user.getId());
                  if(outingDetailsModel1!=null){
                      listOfFriends.remove(user);
                  }
              }
                System.out.println(listOfFriends);
              return listOfFriends;
            }
        }
    }
    @Override
    public void updateOutingStatus(String outingId, String userId, String status){
        if(outingId == null){
            throw  new OutingException("Outing is not present" , HttpStatus.BAD_REQUEST);
        }else if(userId == null){
            throw  new OutingException("User not found", HttpStatus.BAD_REQUEST);
        }else if(Objects.equals(status, "accepted") || Objects.equals(status, "rejected")){
            OutingModel outingModel1 ;
            Optional<OutingModel> outingModel = this.outingRepository.findById(outingId);
            if(outingModel.isPresent()){
                outingModel1 = outingModel.get();
            }
            System.out.println(outingId+" "+userId);
            OutingDetailsModel outingDetailsModel = this.outingDetailsRepository.findByOutingIdAndUserId(outingId, userId);
            System.out.println(outingDetailsModel+"204");
            if(outingDetailsModel == null){
                throw new OutingException("You are not allowed to update the status", HttpStatus.BAD_REQUEST);
            }else{
                if(outingDetailsModel.getStatus() == status){
                    throw  new OutingException("You have already " + status + " the outing", HttpStatus.BAD_REQUEST);
                }else{
                    outingDetailsModel.setStatus(status);
                    System.out.println(outingDetailsModel+"212");
                    this.outingDetailsRepository.save(outingDetailsModel);
                }
            }
        }
    }

    List<String> checkData(Map<String, Object> requestData){
        List<String> errors = new ArrayList<>();

        // Check for "outingName"
        if (requestData.containsKey("outingName")) {
            Object outingNameObj = requestData.get("outingName");

            if (outingNameObj instanceof String outingName) {

                if (outingName.trim().isEmpty()) {
                    errors.add("Outing Name field is empty");
                }
            } else {
                errors.add("Outing name is not a string");
            }
        }

        if (requestData.containsKey("date")) {
            Object dateObj = requestData.get("date");

            if (dateObj instanceof String date) {

                if (date.trim().isEmpty()) {
                    errors.add("Date field is empty");
                }
            } else {
                errors.add("Date is not a string");
            }
        }

        // Check for "description"
        if (requestData.containsKey("description")) {
            Object descriptionObj = requestData.get("description");

            if (descriptionObj instanceof String description) {

                if (description.trim().isEmpty()) {
                    errors.add("Description field is empty");
                }
            } else {
                errors.add("Description is not a string");
            }
        }

        // Check for "userIds"
        if (requestData.containsKey("userIds")) {
            Object userIdsObj = requestData.get("userIds");

            if (userIdsObj instanceof List) {
                List<String> userIds = (List<String>) userIdsObj;

                if (userIds.isEmpty()) {
                    errors.add("No friends selected");
                }
            } else {
                errors.add("userIds is not a vector");
            }
        }

        return errors;
    }

    List<String> checkFriendship(Map<String,Object> requestData){
        List<String> errors = new ArrayList<>();
        String loggedInUserId = this.principalDetails.getPrincipalDetails().getId();
        List<String> userIds1 = (List<String>) requestData.get("userIds");
        List<String> userIds = new ArrayList<>(userIds1);
        List<FriendshipDto> friends = this.friendshipCheckController.getAllFriendsByUserId(loggedInUserId).getBody();

        if(friends != null) {
            for (FriendshipDto friend : friends) {

                if(friend.getInviteeUser() == null){
                   userIds.remove(friend.getDummyUser().getId());
                }else {
                    userIds.remove(friend.getInviteeUser().getId());
                    userIds.remove(friend.getInviterUser().getId());
                }
            }
        }

        if(!userIds.isEmpty()){
            errors.add("You can only add your friends!");
        }

        return errors;
    }
    public OutingDto createOuting(Map<String, Object>  requestData){
        String outingId = UUID.randomUUID().toString();
        if(!checkData(requestData).isEmpty()){
            List<String> errors = checkData(requestData);
            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
        }
        System.out.println(requestData.get("userIds"));
        if(!checkFriendship(requestData).isEmpty()){
            List<String> errors = checkFriendship(requestData);
            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
        }
        System.out.println(requestData.get("userIds"));

        String loggedInUser = this.principalDetails.getPrincipalDetails().getId();
        OutingModel outingModel = new OutingModel(outingId, (String) requestData.get("date"), (String) requestData.get("outingName"), (String) requestData.get("description"),false, loggedInUser);
        List<OutingModel> outingModels = new ArrayList<>();
        outingModels.add(outingModel);

        List<String> userIds = (List<String>) requestData.get("userIds");
        userIds.add(loggedInUser);
        System.out.println(userIds);
        for (String userId : userIds) {
            String outingDetailId = UUID.randomUUID().toString();
            OutingDetailsModel outingDetailsModel;
            if(loggedInUser == userId) {
                 outingDetailsModel = new OutingDetailsModel(outingDetailId, outingId, userId, "accepted");
            }else{
                outingDetailsModel = new OutingDetailsModel(outingDetailId, outingId, userId, "invited");
            }
            this.outingDetailsRepository.save(outingDetailsModel);
        }

        List<OutingDto> outingDto1 = convertToDtos(outingModels);
        this.outingRepository.save(outingModels.get(0));
        return outingDto1.get(0);
    }

    public OutingDto updateOuting(String outingId, Map<String, Object> outingDto){
        String loggedInUser = this.principalDetails.getPrincipalDetails().getId();
        List<OutingModel> outingModels = new ArrayList<>();
        if(!checkData(outingDto).isEmpty()){
            List<String> errors = checkData(outingDto);
            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
        }
    //TODO check if the user exists in outing
//        if(!checkFriendship(outingDto).isEmpty()){
//            List<String> errors = checkFriendship(outingDto);
//            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
//        }

        Optional<OutingModel> optionalOutingModel = this.outingRepository.findById(outingId);
        optionalOutingModel.ifPresent(outingModels::add);
        if(outingModels.isEmpty()){
            List<String> errors = new ArrayList<>();
            errors.add("Outing not found");
            throw new OutingException(errors , HttpStatus.BAD_REQUEST);
        }
        OutingModel outingModel = outingModels.get(0);
        outingModel.setOutingName((String) outingDto.get("outingName"));
        outingModel.setDate((String) outingDto.get("date"));
        outingModel.setDescription((String) outingDto.get("description"));
        List<String> userIds = (List<String>) outingDto.get("userIds");
        List<OutingDetailsModel> outingDetailsModels = this.outingDetailsRepository.findByOutingId(outingId);
        List<OutingDetailsModel> toBeRemoved = new ArrayList<>();
        for (OutingDetailsModel outingDetailsModel : outingDetailsModels) {
            if (userIds.contains(outingDetailsModel.getUserId())) {
                userIds.remove(outingDetailsModel.getUserId());
            } else {
                toBeRemoved.add(outingDetailsModel);
            }
        }
        this.outingDetailsRepository.deleteAllInBatch(toBeRemoved);
        this.outingDetailsRepository.saveAll(outingDetailsModels);
        userIds.add(this.principalDetails.getPrincipalDetails().getId());
        for (String userId : userIds) {
            if(userId==loggedInUser)continue;
            String outingDetailId = UUID.randomUUID().toString();
            OutingDetailsModel outingDetailsModel = new OutingDetailsModel(outingDetailId, outingModel.getId(), userId);
            this.outingDetailsRepository.save(outingDetailsModel);
        }

        OutingDto outingDto1 = convertToDtos(outingModels).get(0);
        this.outingRepository.save(outingModel);
        return outingDto1;
    }

    public void deleteOuting(String outingId){
    }



}
