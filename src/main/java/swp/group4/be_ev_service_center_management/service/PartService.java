package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.PartDTO;
import java.util.List;

public interface PartService {
    List<PartDTO> getAllParts();
    PartDTO getPartById(Integer partId);
}
