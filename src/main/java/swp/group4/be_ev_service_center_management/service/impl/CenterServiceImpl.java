package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.CenterMembersResponse;
import swp.group4.be_ev_service_center_management.dto.response.StaffResponse;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.entity.Staff;
import swp.group4.be_ev_service_center_management.entity.Technician;
import swp.group4.be_ev_service_center_management.repository.StaffRepository;
import swp.group4.be_ev_service_center_management.repository.TechnicianRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.CenterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterServiceImpl implements CenterService {
    private final StaffRepository staffRepository;
    private final TechnicianRepository technicianRepository;

    @Override
    public CenterMembersResponse getMembersByCenterId(Integer centerId) {
        List<StaffResponse> staffs = staffRepository.findByServiceCenter_CenterId(centerId)
                .stream().map(this::toStaffResponse).collect(Collectors.toList());
        List<TechnicianResponse> technicians = technicianRepository.findByServiceCenter_CenterId(centerId)
                .stream().map(this::toTechnicianResponse).collect(Collectors.toList());
        return CenterMembersResponse.builder()
                .staffs(staffs)
                .technicians(technicians)
                .build();
    }

    private StaffResponse toStaffResponse(Staff staff) {
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

    private TechnicianResponse toTechnicianResponse(Technician technician) {
        return TechnicianResponse.builder()
                .technicianId(technician.getTechnicianId())
                .serviceCenterId(technician.getServiceCenter() != null ? technician.getServiceCenter().getCenterId() : null)
                .accountId(technician.getAccount() != null ? technician.getAccount().getAccountId() : null)
                .fullName(technician.getFullName())
                .phone(technician.getPhone())
                .email(technician.getEmail())
                .createdAt(technician.getCreatedAt())
                .build();
    }
}
