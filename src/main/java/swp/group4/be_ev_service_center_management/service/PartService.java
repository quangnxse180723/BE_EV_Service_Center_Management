package swp.group4.be_ev_service_center_management.service;

import swp.group4.be_ev_service_center_management.dto.request.PartRequest;
import swp.group4.be_ev_service_center_management.dto.response.PartResponse;

import java.util.List;

public interface PartService {
    PartResponse addPart(PartRequest req);
    void restock(Integer partId, Integer quantity);
    List<PartResponse> getPartsByCenter(Integer centerId);
}