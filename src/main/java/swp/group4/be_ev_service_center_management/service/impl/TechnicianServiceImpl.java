package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.response.TechnicianResponse;
import swp.group4.be_ev_service_center_management.entity.Technician;
import swp.group4.be_ev_service_center_management.repository.TechnicianRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.TechnicianService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;

    @Override
    public List<TechnicianResponse> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::toTechnicianResponse)
                .collect(Collectors.toList());
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