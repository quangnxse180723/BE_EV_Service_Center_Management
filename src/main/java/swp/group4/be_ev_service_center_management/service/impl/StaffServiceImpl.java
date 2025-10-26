package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp.group4.be_ev_service_center_management.dto.request.StaffRequest;
import swp.group4.be_ev_service_center_management.dto.response.StaffResponse;
import swp.group4.be_ev_service_center_management.entity.Account;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;
import swp.group4.be_ev_service_center_management.entity.Staff;
import swp.group4.be_ev_service_center_management.repository.AccountRepository;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;
import swp.group4.be_ev_service_center_management.repository.StaffRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.StaffService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<StaffResponse> getAllStaffs() {
        return staffRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StaffResponse addStaff(StaffRequest request) {
        ServiceCenter center = serviceCenterRepository.findById(request.getServiceCenterId())
                .orElseThrow(() -> new RuntimeException("Service center not found"));
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Staff staff = new Staff();
        staff.setServiceCenter(center);
        staff.setAccount(account);
        staff.setFullName(request.getFullName());
        staff.setPhone(request.getPhone());
        staff.setEmail(request.getEmail());
        staff = staffRepository.save(staff);
        return toResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Integer staffId, StaffRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        if (request.getServiceCenterId() != null) {
            ServiceCenter center = serviceCenterRepository.findById(request.getServiceCenterId())
                    .orElseThrow(() -> new RuntimeException("Service center not found"));
            staff.setServiceCenter(center);
        }
        if (request.getAccountId() != null) {
            Account account = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            staff.setAccount(account);
        }
        if (request.getFullName() != null) staff.setFullName(request.getFullName());
        if (request.getPhone() != null) staff.setPhone(request.getPhone());
        if (request.getEmail() != null) staff.setEmail(request.getEmail());
        staff = staffRepository.save(staff);
        return toResponse(staff);
    }

    private StaffResponse toResponse(Staff staff) {
        return StaffResponse.builder()
                .staffId(staff.getStaffId())
                .serviceCenterId(staff.getServiceCenter() != null ? staff.getServiceCenter().getCenterId() : null)
                .accountId(staff.getAccount() != null ? staff.getAccount().getAccountId() : null)
                .fullName(staff.getFullName())
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .createdAt(staff.getCreatedAt())
                .build();
    }
}