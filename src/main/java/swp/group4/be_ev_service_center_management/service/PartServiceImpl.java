package swp.group4.be_ev_service_center_management.service;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.PartRequest;
import swp.group4.be_ev_service_center_management.dto.response.PartResponse;
import swp.group4.be_ev_service_center_management.entity.Part;
import swp.group4.be_ev_service_center_management.entity.ServiceCenter;
import swp.group4.be_ev_service_center_management.exception.ResourceNotFoundException;
import swp.group4.be_ev_service_center_management.repository.PartRepository;
import swp.group4.be_ev_service_center_management.service.PartService;
import swp.group4.be_ev_service_center_management.repository.ServiceCenterRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {
    private final PartRepository partRepo;
    private final ServiceCenterRepository centerRepo;

    @Override
    public PartResponse addPart(PartRequest req) {
        ServiceCenter center = centerRepo.findById(req.getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceCenter not found"));
        Part part = new Part();
        part.setServiceCenter(center);
        part.setName(req.getName());
        part.setPartCode(req.getPartCode());
        part.setQuantityInStock(req.getQuantityInStock());
        part.setMinStock(req.getMinStock());
        part.setPrice(BigDecimal.valueOf(req.getPrice()));
        return entityToResponse(partRepo.save(part));
    }

    @Override
    public void restock(Integer partId, Integer quantity) {
        Part part = partRepo.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
        part.setQuantityInStock(part.getQuantityInStock() + quantity);
        partRepo.save(part);
    }

    @Override
    public List<PartResponse> getPartsByCenter(Integer centerId) {
        return partRepo.findByServiceCenterCenterId(centerId)
                .stream().map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    private PartResponse entityToResponse(Part part) {
        PartResponse r = new PartResponse();
        r.setPartId(part.getPartId());
        r.setCenterId(part.getServiceCenter().getCenterId());
        r.setName(part.getName());
        r.setPartCode(part.getPartCode());
        r.setQuantityInStock(part.getQuantityInStock());
        r.setMinStock(part.getMinStock());
        r.setPrice(part.getPrice().doubleValue());
        return r;
    }
}