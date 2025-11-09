package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.PartDTO;
import swp.group4.be_ev_service_center_management.entity.Part;
import swp.group4.be_ev_service_center_management.repository.PartRepository;
import swp.group4.be_ev_service_center_management.service.PartService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {
    
    private final PartRepository partRepository;

    @Override
    public List<PartDTO> getAllParts() {
        List<Part> parts = partRepository.findAll();
        return parts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PartDTO getPartById(Integer partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found with id: " + partId));
        return convertToDTO(part);
    }

    private PartDTO convertToDTO(Part part) {
        PartDTO dto = new PartDTO();
        dto.setPartId(part.getPartId());
        dto.setCenterId(part.getServiceCenter() != null ? part.getServiceCenter().getCenterId() : null);
        dto.setName(part.getName());
        dto.setPartCode(part.getPartCode());
        dto.setQuantityInStock(part.getQuantityInStock());
        dto.setMinStock(part.getMinStock());
        dto.setUnitPrice(part.getPrice()); // price -> unitPrice
        return dto;
    }
}
