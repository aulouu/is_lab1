package itmo.aulouu.is_lab1.service;

import itmo.aulouu.is_lab1.Pagification;
import itmo.aulouu.is_lab1.exceptions.AdminRequestAlreadyExistException;
import itmo.aulouu.is_lab1.exceptions.AdminRequestNotFoundException;
import itmo.aulouu.is_lab1.exceptions.UserAlreadyAdminException;
import itmo.aulouu.is_lab1.security.jwt.JwtUtils;
import itmo.aulouu.is_lab1.dao.*;
import itmo.aulouu.is_lab1.dto.*;
import itmo.aulouu.is_lab1.model.*;
import itmo.aulouu.is_lab1.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
   private final UserRepository userRepository;
   private final JwtUtils jwtUtils;
   private final AdminRequestRepository adminRequestRepository;
   private final SimpMessagingTemplate simpMessagingTemplate;

   public List<AdminRequestDTO> getAdminRequests(int from, int size) {
      Pageable page = Pagification.createPageTemplate(from, size);
      List<AdminRequest> adminRequests = adminRequestRepository.findAll(page).getContent();

      return adminRequests
            .stream()
            .map(adminRequest -> new AdminRequestDTO(adminRequest.getId(), adminRequest.getUser().getUsername()))
            .sorted(new Comparator<AdminRequestDTO>() {
               @Override
               public int compare(AdminRequestDTO o1, AdminRequestDTO o2) {
                  return o1.getId().compareTo(o2.getId());
               }
            })
            .toList();
   }

   public void approveOnAdminRequest(Long adminRequestId, HttpServletRequest request) {
      AdminRequest adminJoinRequest = adminRequestRepository.findById(adminRequestId)
            .orElseThrow(() -> new AdminRequestNotFoundException(
                  String.format("Admin join request not found %d", adminRequestId)));

      User fromUser = adminJoinRequest.getUser();
      fromUser.setRole(Role.ADMIN);
      userRepository.save(fromUser);

      adminRequestRepository.delete(adminJoinRequest);
      simpMessagingTemplate.convertAndSend("/topic", "Admin Request approved");
   }

   public void createAdminRequest(HttpServletRequest request) {
      User fromUser = findUserByRequest(request);

      if (userRepository.findAllByRole(Role.ADMIN).isEmpty()) {
         fromUser.setRole(Role.ADMIN);
         userRepository.save(fromUser);
         simpMessagingTemplate.convertAndSend("/topic", "New Admin Created");
         return;
      }

      if (fromUser.getRole() == Role.ADMIN)
         throw new UserAlreadyAdminException(
               String.format("User %s is already an admin", fromUser.getUsername()));
      if (adminRequestRepository.existsByUser(fromUser))
         throw new AdminRequestAlreadyExistException(
               String.format("Admin request already exists from user %s", fromUser.getUsername()));
      AdminRequest adminRequest = new AdminRequest(null, fromUser);

      adminRequestRepository.save(adminRequest);
      simpMessagingTemplate.convertAndSend("/topic", "Admin Request created");
   }


   private User findUserByRequest(HttpServletRequest request) {
      String username = jwtUtils.getUserNameFromJwtToken(jwtUtils.parseJwt(request));
      return userRepository.findByUsername(username).get();
   }


}
